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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccApFaktur;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccProposalAp;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPembayaranHutang;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccProposalApDetail;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.PembayaranHutang;
import net.sra.prime.ultima.entity.PembayaranHutangDetail;
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
public class ServicePembayaranHutang {

    @Autowired
    MapperPembayaranHutang referensi;

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
    
    @Autowired
    MapperAccProposalAp mapperAccProposalAp;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor,bulan, tahun);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang selectOneKantor(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public List<PembayaranHutang> onLoadList(Date awal, Date akhir, Character statusap) {
        return referensi.selectAll(awal, akhir, statusap);
    }

    @Transactional(readOnly = false)
    public void ubah(PembayaranHutang item, List<PembayaranHutangDetail> lPembayaranHutangDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.deleteDetail(item.getNo_pembayaran_hutang());
        referensi.update(item);
        tambahdetail(item, lPembayaranHutangDetail);
        AccGlTrans accGlTrans=mapperAccGl.selectOneRef(item.getNo_pembayaran_hutang());
        mapperAccGl.deleteDetail(accGlTrans.getGl_number());
        if (item.getStatus().equals('A')) {
            accGlTrans.setPosting(Boolean.TRUE);
            updateAccValue(item, lAccGlDetail);
        }
        accGlTrans.setNote(item.getKeterangan());
        accGlTrans.setGl_date(item.getTanggal());

        mapperAccGl.updatePosting(accGlTrans);
        tambahjurnal(accGlTrans, lAccGlDetail);
    }
    
    @Transactional(readOnly = false)
    public void maintenance(PembayaranHutang item) {
        referensi.update(item);
    }
    
    @Transactional(readOnly = false)
    public void updateAccValue(PembayaranHutang item, List<AccGlDetail> lAccGlDetail) {
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
            if (lAccGlDetail.get(i).getDebit() == null || lAccGlDetail.get(i).getDebit() == 0) {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getCredit(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            } else {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getDebit(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            }
            mapperAccGl.updateSaldoAkhir(tahun, lAccGlDetail.get(i).getId_account());
        }
    }

    
    @Transactional(readOnly = false)
    public void delete(String nomor) {
        referensi.deleteDetail(nomor);
        referensi.delete(nomor);
        String glNumber=mapperAccGl.selectOneRef(nomor).getGl_number();
        mapperAccGl.deleteDetail(glNumber);
        mapperAccGl.delete(glNumber);
    }

    @Transactional(readOnly = false)
    public void tambah(PembayaranHutang item, List<PembayaranHutangDetail> lPembayaranHutangDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.insert(item);
        tambahdetail(item, lPembayaranHutangDetail);
        
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan(item.getId_perusahaan());
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
        itemTrans.setJournal_code("JPH");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNo_pembayaran_hutang());
        itemTrans.setNote(item.getKeterangan());
        itemTrans.setPosting(Boolean.FALSE);
        mapperAccGl.insert(itemTrans);
        tambahjurnal(itemTrans, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(PembayaranHutang item, List<PembayaranHutangDetail> lPembayaranHutangDetail) {
        for (int i = 0; i < lPembayaranHutangDetail.size(); i++) {
            PembayaranHutangDetail itemdetail = lPembayaranHutangDetail.get(i);
            itemdetail.setNomor_pembayaran_hutang(item.getNo_pembayaran_hutang());
            itemdetail.setUrut(i+1);
            referensi.insertDetail(itemdetail);
            if(item.getStatus().equals('A')){
                mapperAccApFaktur.updateBayar(itemdetail.getJumlah_bayar(), itemdetail.getNo_penerimaan());
            }
        }

    }
    
    public void tambahjurnal(AccGlTrans itemTrans, List<AccGlDetail> lAccGlDetail) {
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccGlDetail accGlDetail = lAccGlDetail.get(i);
            accGlDetail.setLine(i + 1);
            accGlDetail.setJournal_code(itemTrans.getJournal_code());
            accGlDetail.setGl_number(itemTrans.getGl_number());
            accGlDetail.setGl_date(itemTrans.getGl_date());
            accGlDetail.setKeterangan(itemTrans.getNote());
            if (accGlDetail.getDebit() == null || accGlDetail.getDebit() == 0) {
                accGlDetail.setIs_debit(Boolean.FALSE);
                accGlDetail.setValue(accGlDetail.getCredit());
            } else {
                accGlDetail.setIs_debit(Boolean.TRUE);
                accGlDetail.setValue(accGlDetail.getDebit());
            }
            mapperAccGl.insertDetail(accGlDetail);
        }
    }

   
    @Transactional(readOnly = true)
    public PembayaranHutang onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PembayaranHutangDetail> onLoadDetail(String id) {
        return referensi.selectAllDetail(id);
    }

   
    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnal(String nomor) {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        lAccGlDetail = mapperAccGl.selectJurnalGl(nomor);
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccGlDetail itemdetail = lAccGlDetail.get(i);
            if (itemdetail.getIs_debit()) {
                itemdetail.setDebit(itemdetail.getValue());
            } else {
                itemdetail.setCredit(itemdetail.getValue());
            }
            lAccGlDetail.set(i, itemdetail);
        }
        return lAccGlDetail;
    }
    
    
    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalawal(PembayaranHutang item) {
        
        /////////////////// GL Detail //////////////////
        List<AccGlDetail>lAccGlDetail = new ArrayList<>();
        lAccGlDetail = new ArrayList<>();
        AccGlDetail itemGl = new AccGlDetail();
        itemGl.setId_account(mapperSupplier.selectAccount(item.getId_supplier()));
        itemGl.setDebit(item.getTotal_bayar());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        itemGl = new AccGlDetail();
        itemGl.setId_account(item.getAccount_bayar());
        itemGl.setCredit(item.getTotal_bayar());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);
        
        return lAccGlDetail;
    }
    
    
    ///////////////////////////////
    
    @Transactional(readOnly = true)
    public List<AccProposalApDetail> selectAllDetailPayment(String vendor_code){
      return mapperAccProposalAp.selectAllDetailPayment(vendor_code);
    }
    
    @Transactional(readOnly = true)
    public AccProposalApDetail selectOneDetail(String id){
      return mapperAccProposalAp.selectOneDetail(id);
    }

}
