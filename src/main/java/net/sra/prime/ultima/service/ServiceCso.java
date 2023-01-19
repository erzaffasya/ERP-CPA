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
import net.sra.prime.ultima.db.mapper.MapperAccApFaktur;
import net.sra.prime.ultima.db.mapper.MapperAccCso;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.AccCso;
import net.sra.prime.ultima.entity.AccCsoDetil;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.InternalKantorCabang;
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
public class ServiceCso {

    @Autowired
    MapperAccCso referensi;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperAccApFaktur mapperAccApFaktur;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperSupplier mapperSupplier;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang selectOneKantor(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public List<AccCso> onLoadList(Date awal, Date akhir, String idKantor) {
        return referensi.selectAll(awal, akhir, idKantor);
    }

    @Transactional(readOnly = false)
    public void ubah(AccCso item, List<AccCsoDetil> lAccCsoDetil) {
        referensi.deleteDetail(item.getAcc_cso_number());
        referensi.update(item);
        tambahdetail(item, lAccCsoDetil);

        if (item.getStatus().equals('S')) {
            tambahjurnal(item, lAccCsoDetil);
            updateAccValue(item, mapperAccGl.selectJurnalGl(item.getAcc_cso_number()));
        }
    }

    @Transactional(readOnly = false)
    public void updateAccValue(AccCso item, List<AccGlDetail> lAccGlDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccValue accValue = new AccValue();
            accValue.setYears(tahun);
            accValue.setId_perusahaan("01");
            accValue.setAccount(lAccGlDetail.get(i).getId_account());

            // cek apakah kode account untuk tahun tsb sudah ada
            if (mapperAccGl.selectOneAccValue(tahun, lAccGlDetail.get(i).getId_account()) == null) {
                mapperAccGl.insertAccValue(accValue);
            }
            if (lAccGlDetail.get(i).getIs_debit()) {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            } else {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            }

        }
    }

    @Transactional(readOnly = false)
    public void delete(String nomor) {
        referensi.deleteDetail(nomor);
        referensi.delete(nomor);
    }

    @Transactional(readOnly = false)
    public void tambah(AccCso item, List<AccCsoDetil> lAccCsoDetil, List<AccGlDetail> lAccGlDetail) {
        referensi.insert(item);
        tambahdetail(item, lAccCsoDetil);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(AccCso item, List<AccCsoDetil> lAccCsoDetil) {
        for (int i = 0; i < lAccCsoDetil.size(); i++) {
            AccCsoDetil itemdetail = lAccCsoDetil.get(i);
            itemdetail.setAcc_cso_number(item.getAcc_cso_number());
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);
        }

    }

    public void tambahjurnal(AccCso item, List<AccCsoDetil> lAccCsoDetil) {
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan("01");
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        String noMax = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax + tahun);
        }
        itemTrans.setJournal_code("JBV");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getAcc_cso_number());
        itemTrans.setNote(item.getUraian());
        itemTrans.setPosting(Boolean.TRUE);
        mapperAccGl.insert(itemTrans);

        AccGlDetail accGlDetail = new AccGlDetail();
        Integer line = 1;
        for (int i = 0; i < lAccCsoDetil.size(); i++) {
            accGlDetail = new AccGlDetail();
            accGlDetail.setJournal_code(itemTrans.getJournal_code());
            accGlDetail.setGl_number(itemTrans.getGl_number());
            accGlDetail.setGl_date(itemTrans.getGl_date());
            accGlDetail.setId_account(lAccCsoDetil.get(i).getId_account_detail());
            accGlDetail.setLine(line);
            accGlDetail.setKeterangan(lAccCsoDetil.get(i).getUraian());
            if (lAccCsoDetil.get(i).getJumlah() < 0) {
                accGlDetail.setValue(-1 * lAccCsoDetil.get(i).getJumlah());
                accGlDetail.setIs_debit(Boolean.FALSE);
            } else {
                accGlDetail.setValue(lAccCsoDetil.get(i).getJumlah());
                accGlDetail.setIs_debit(Boolean.TRUE);
            }

            accGlDetail.setPosted(Boolean.FALSE);
            mapperAccGl.insertDetail(accGlDetail);
            line++;
        }
        accGlDetail = new AccGlDetail();
        accGlDetail.setGl_number(itemTrans.getGl_number());
        accGlDetail.setId_account(item.getId_account());
        accGlDetail.setLine(line);
        accGlDetail.setGl_date(item.getTanggal());
        accGlDetail.setKeterangan(item.getUraian());
        accGlDetail.setValue(item.getTotal());
        accGlDetail.setIs_debit(Boolean.FALSE);
        accGlDetail.setPosted(Boolean.FALSE);
        mapperAccGl.insertDetail(accGlDetail);

    }

    @Transactional(readOnly = true)
    public AccCso onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<AccCsoDetil> onLoadDetail(String id) {
        return referensi.selectAllDetail(id);
    }

}
