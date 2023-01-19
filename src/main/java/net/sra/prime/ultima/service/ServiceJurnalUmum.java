/*
 * Copyright 2017 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sra.prime.ultima.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceJurnalUmum {

    @Autowired
    MapperAccGl referensi;

    @Transactional(readOnly = true)
    public String noMax(Integer tahun) {
        return referensi.SelectMax(tahun);
    }

    @Transactional(readOnly = true)
    public List<AccGlTrans> onLoadList(String jurnal,Date awal, Date akhir, Boolean status, String note) {
        return referensi.selectAllJU(jurnal, awal, akhir, status,note);
    }

    @Transactional(readOnly = true)
    public AccGlTrans onLoad(String glNumber) {
        return referensi.selectOne(glNumber);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadDetail(String glNumber) {
        return referensi.selectOneDetail(glNumber);
    }

    @Transactional(readOnly = false)
    public void ubah(AccGlTrans item, List<AccGlDetail> lAccGlDetail) {
        referensi.updatePosting(item);
        referensi.deleteDetail(item.getGl_number());
        tambahdetail(item, lAccGlDetail);
        if (item.getPosting()) {
            updateAccValue(item, lAccGlDetail);
        }
    }

    @Transactional(readOnly = false)
    public void tambah(AccGlTrans item, List<AccGlDetail> lAccGlDetail) {
        referensi.insert(item);
        tambahdetail(item, lAccGlDetail);
    }
    
    @Transactional(readOnly = false)
    public void delete(String glNumber) {
        referensi.deleteDetail(glNumber);
        referensi.delete(glNumber);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(AccGlTrans item, List<AccGlDetail> lAccGlDetail) {

        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccGlDetail itemdetail = lAccGlDetail.get(i);
            itemdetail.setLine(i + 1);
            itemdetail.setGl_number(item.getGl_number());
            itemdetail.setGl_date(item.getGl_date());
            if (itemdetail.getDebit() == null || itemdetail.getDebit() == 0) {
                itemdetail.setIs_debit(Boolean.FALSE);
                itemdetail.setValue(itemdetail.getCredit());
            } else {
                itemdetail.setIs_debit(Boolean.TRUE);
                itemdetail.setValue(itemdetail.getDebit());
            }
            referensi.insertDetail(itemdetail);

        }

    }

    @Transactional(readOnly = false)
    public void updateAccValue(AccGlTrans item, List<AccGlDetail> lAccGlDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getGl_date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getGl_date());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccValue accValue = new AccValue();
            accValue.setYears(tahun);
            accValue.setId_perusahaan("01");
            accValue.setAccount(lAccGlDetail.get(i).getId_account());

            // cek apakah kode account untuk tahun tsb sudah ada
            if (referensi.selectOneAccValue(tahun, lAccGlDetail.get(i).getId_account()) == null) {
                referensi.insertAccValue(accValue);
            }
            if (lAccGlDetail.get(i).getDebit() == null || lAccGlDetail.get(i).getDebit() == 0) {
                referensi.updateAccValue(nomor, lAccGlDetail.get(i).getCredit(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            } else {
                referensi.updateAccValue(nomor, lAccGlDetail.get(i).getDebit(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            }
            referensi.updateSaldoAkhir(tahun, lAccGlDetail.get(i).getId_account());
        }
    }

}
