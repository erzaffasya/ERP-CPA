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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccArFaktur;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperCreditNote;
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.AccArFaktur;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.CreditNoteDetail;
import net.sra.prime.ultima.entity.CreditNote;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.CreditNoteCancel;
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
public class ServiceCreditNote {

    @Autowired
    MapperCreditNote referensi;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperAccArFaktur mapperAccArFaktur;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperCustomer mapperCustomer;

    @Autowired
    MapperPesanCancel mapperPesanCancel;

    @Transactional(readOnly = true)
    public String noMax(Integer tahun) {
        return referensi.SelectMax(tahun);
    }

    @Transactional(readOnly = true)
    public List<CreditNote> onLoadList(Date awal, Date akhir, Character status) {
        return referensi.selectAll(awal, akhir, status);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id, Character jenis) {
        AccGlTrans accGlTrans = mapperAccGl.selectOneRef(referensi.selectOne(id).getNomor());
        if (accGlTrans != null) {
            mapperAccGl.deleteDetail(accGlTrans.getGl_number());
            mapperAccGl.delete(accGlTrans.getGl_number());
        }
        if (jenis == null) {
            referensi.deleteDetail(id);
        } else {
            referensi.deleteDetailReguler(id);
        }
        referensi.delete(id);

    }

    @Transactional(readOnly = false)
    public void tambah(CreditNote item, List<CreditNoteDetail> lCreditNoteDetail, List<AccGlDetail> lAccGlDetail) throws IOException {
        referensi.insert(item);
        this.tambahdetail(lCreditNoteDetail, item);
        tambahAccReguler(item, Boolean.FALSE, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<CreditNoteDetail> lCreditNoteDetail, CreditNote item) {

        for (int i = 0; i < lCreditNoteDetail.size(); i++) {
            CreditNoteDetail itemdetail = lCreditNoteDetail.get(i);
            itemdetail.setId(item.getId());
            itemdetail.setUrut(i + 1);
            if (item.getJenis() == null) {
                referensi.insertDetail(itemdetail);
            } else {
                referensi.insertDetailReguler(itemdetail);
            }
        }
    }

    @Transactional(readOnly = false)
    public void ubah(CreditNote item, List<CreditNoteDetail> lCreditNoteDetail, List<AccGlDetail> lAccGlDetail) {
        if (item.getJenis()== null) {
            referensi.deleteDetail(item.getId());
        } else {
            referensi.deleteDetailReguler(item.getId());
        }
        referensi.update(item);
        this.tambahdetail(lCreditNoteDetail, item);
        AccGlTrans accGlTrans = mapperAccGl.selectOneRef(item.getNomor());
        mapperAccGl.deleteDetail(accGlTrans.getGl_number());
        if (item.getStatus().equals('P')) {
            this.tambahAr(item);
            accGlTrans.setPosting(Boolean.TRUE);
            updateAccValue(item, lAccGlDetail);
        }
        accGlTrans.setNote("Credit Note " + item.getCustomer() + " No. Invoice : " + item.getNo_invoice());
        accGlTrans.setGl_date(item.getTanggal());

        mapperAccGl.updatePosting(accGlTrans);
        tambahjurnal(accGlTrans, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void maintenace(CreditNote item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwal(CreditNote item, List<CreditNoteDetail> lCreditNoteDetail) throws IOException {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();

        Gudang gudang = mapperReferensi.selectOneperGudang(item.getId_gudang());

        InternalKantorCabang ikc = mapperKantor.selectOne(gudang.getId_kantor());

        AccGlDetail itemGl = new AccGlDetail();
        itemGl.setId_account(ikc.getAccount_penjualan());
        itemGl.setDebit(item.getDpp());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        if (item.getIs_ppn()) {
            itemGl = new AccGlDetail();
            itemGl.setId_account(20301001);
            itemGl.setDebit(item.getTotal_ppn());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }

        if (item.getBiayalain() != null && item.getBiayalain() != 0) {
            itemGl = new AccGlDetail();
            itemGl.setId_account(80101005);
            itemGl.setDebit(item.getBiayalain());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }

        itemGl = new AccGlDetail();
        itemGl.setId_account(mapperCustomer.selectAccount(item.getId_customer(), gudang.getId_kantor()));
        itemGl.setCredit(item.getGrandtotal());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        return lAccGlDetail;
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwalReguler(CreditNote item, List<CreditNoteDetail> lCreditNoteDetail) throws IOException {
        List<AccGlDetail> lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNo_invoice());
        return lAccGlDetail;
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

    @Transactional(readOnly = false)
    public void tambahAr(CreditNote item) {
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        AccArFaktur accArFaktur = new AccArFaktur();
        String noMax = mapperAccArFaktur.SelectMax(Integer.parseInt(thn));
        if (noMax == null) {
            accArFaktur.setAr_number("AR000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            accArFaktur.setAr_number("AR" + noMax + tahun);
        }
        accArFaktur.setNo_invoice(item.getNomor());
        accArFaktur.setInvoice_date(item.getTanggal());
        //accArFaktur.setAr_date(item.getTanggal());
        accArFaktur.setCustomer_code(item.getId_customer());
        accArFaktur.setAmount((item.getDpp() + item.getBiayalain()) * -1);
        accArFaktur.setNotes(item.getKeterangan());
        accArFaktur.setId_perusahaan("01");
        accArFaktur.setBayar(0.00);
        accArFaktur.setPpn(-1 * item.getTotal_ppn());
        accArFaktur.setTotal(-1 * item.getGrandtotal());
        accArFaktur.setTop(item.getTop());
        accArFaktur.setDue_date(item.getDue_date());
        accArFaktur.setTgl_terimainvoice(item.getTanggal());
        accArFaktur.setId_salesman(item.getId_salesman());
        accArFaktur.setReff(item.getNo_po());
        mapperAccArFaktur.insert(accArFaktur);
    }

    @Transactional(readOnly = true)
    public CreditNote onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<CreditNoteDetail> onLoadDetail(Integer id) {
        return referensi.selectOneDetail(id);
    }

    @Transactional(readOnly = true)
    public List<CreditNoteDetail> onLoadDetailReguler(Integer id) {
        return referensi.selectOneDetailReguler(id);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnal(String noCreditNote) {
        List<AccGlDetail> lAccGlDetail = mapperAccGl.selectJurnalGl(noCreditNote);
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

    @Transactional(readOnly = false)
    public void updateAccValue(CreditNote item, List<AccGlDetail> lAccGlDetail) {
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

//    @Transactional(readOnly = false)
//    public void ubahStatus(CreditNote item, String kode_user) {
//        referensi.updateStatusAja(item.getStatus(), item.getNo_penjualan());
//        PesanCancel pesanCancel = new PesanCancel();
//        pesanCancel.setNomor(item.getNo_penjualan());
//        pesanCancel.setPesan(item.getPesan());
//        pesanCancel.setKode_user(kode_user);
//        mapperPesanCancel.insert(pesanCancel);
//        List<CreditNoteDo> lCreditNoteDos= mapperCreditNoteDo.selectAll(item.getNo_penjualan());
//        for (int i=0; i < lCreditNoteDos.size();i++){
//            mapperDotbl.updateStatus('R', lCreditNoteDos.get(i).getNo_do());
//        }
//    }
    @Transactional(readOnly = true)
    public String selectDo(String id) {
        return referensi.selectDo(id);
    }

    @Transactional(readOnly = false)
    public void tambahAccReguler(CreditNote item, Boolean posting, List<AccGlDetail> lAccGlDetail) {
        AccGlTrans itemTrans = new AccGlTrans();
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
        itemTrans.setJournal_code("JAP");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNomor());
        itemTrans.setNote("Credit Note " + item.getCustomer() + " No. Invoice : " + item.getNo_invoice());
        itemTrans.setPosting(posting);
        mapperAccGl.insert(itemTrans);
        tambahjurnal(itemTrans, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void ubahStatus(CreditNote item, String kode_user) {

        referensi.updateStatus(item.getStatus(), item.getNomor());

        CreditNoteCancel cnc = new CreditNoteCancel();
        cnc.setNomor(item.getNo_cancel());
        cnc.setNo_cn(item.getNomor());
        cnc.setPesan(item.getPesan());
        cnc.setKode_user(kode_user);
        referensi.insertCancel(cnc);
        referensi.deleteAccArFaktur(item.getNomor());
        tambahAccCancel(item);
    }

    @Transactional(readOnly = true)
    public String noMaxCancel(Integer bulan, Integer tahun) {
        return referensi.SelectMaxCancel(bulan, tahun);
    }

    @Transactional(readOnly = false)
    public void tambahAccCancel(CreditNote item) {
        AccGlTrans itemTrans = new AccGlTrans();
        String tahun = new SimpleDateFormat("yy").format(new Date());
        String thn = new SimpleDateFormat("yyyy").format(new Date());
        String noMax = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax + tahun);
        }
        itemTrans.setJournal_code("JAP");
        itemTrans.setGl_date(new Date());
        itemTrans.setReference(item.getNo_cancel());
        itemTrans.setNote(item.getPesan());
        itemTrans.setPosting(true);
        mapperAccGl.insert(itemTrans);

        List<AccGlDetail> lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNomor());
        List<AccGlDetail> lAccGlDetailCancel = new ArrayList<>();

        Integer line = 1;
        AccGlDetail accGlDetail = new AccGlDetail();
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            accGlDetail = lAccGlDetail.get(i);
            if (!accGlDetail.getIs_debit()) {
                accGlDetail.setIs_debit(Boolean.TRUE);
                accGlDetail.setLine(line);
                line++;
                accGlDetail.setJournal_code(itemTrans.getJournal_code());
                accGlDetail.setGl_number(itemTrans.getGl_number());
                accGlDetail.setGl_date(itemTrans.getGl_date());
                accGlDetail.setKeterangan(itemTrans.getNote());
                lAccGlDetailCancel.add(accGlDetail);
                mapperAccGl.insertDetail(accGlDetail);
            }
        }

        lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNomor());
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            accGlDetail = lAccGlDetail.get(i);
            if (accGlDetail.getIs_debit()) {
                accGlDetail.setIs_debit(Boolean.FALSE);
                accGlDetail.setLine(line);
                line++;
                accGlDetail.setJournal_code(itemTrans.getJournal_code());
                accGlDetail.setGl_number(itemTrans.getGl_number());
                accGlDetail.setGl_date(itemTrans.getGl_date());
                accGlDetail.setKeterangan(itemTrans.getNote());
                lAccGlDetailCancel.add(accGlDetail);
                mapperAccGl.insertDetail(accGlDetail);
            }
        }
        updateAccValueCancel(item, lAccGlDetailCancel);
    }

    @Transactional(readOnly = false)
    public void updateAccValueCancel(CreditNote item, List<AccGlDetail> lAccGlDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(new Date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(new Date());
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
            if (!lAccGlDetail.get(i).getIs_debit()) {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            } else {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            }
            mapperAccGl.updateSaldoAkhir(tahun, lAccGlDetail.get(i).getId_account());

        }
    }

}
