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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccApFaktur;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPenerimaan;
import net.sra.prime.ultima.db.mapper.MapperPenerimaanDetail;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelian;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelianDetail;
import net.sra.prime.ultima.db.mapper.MapperPo;
import net.sra.prime.ultima.db.mapper.MapperPoDetail;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.AccApFaktur;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Penerimaan;
import net.sra.prime.ultima.entity.PenerimaanDetail;
import net.sra.prime.ultima.entity.PenerimaanDetailReguler;
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.entity.PoDetailReguler;
import net.sra.prime.ultima.entity.Supplier;
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
public class ServicePenerimaan {

    @Autowired
    MapperPenerimaan referensi;

    @Autowired
    MapperPenerimaanDetail mapperPenerimaanDetail;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperSupplier mapperSupplier;

    @Autowired
    MapperPo mapperPo;

    @Autowired
    MapperPoDetail mapperPoDetail;

    @Autowired
    MapperPermintaanPembelianDetail mapperPermintaanPembelianDetail;

    @Autowired
    MapperPermintaanPembelian mapperPermintaanPembelian;

    @Autowired
    MapperAccApFaktur mapperAccApFaktur;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor, bulan, tahun);
    }

    public InternalKantorCabang selecOne(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public List<Penerimaan> onLoadList(Date awal, Date akhir) {
        return referensi.selectAll(awal, akhir);
    }
    
    @Transactional(readOnly = true)
    public List<Penerimaan> onLoadListPajak(Date awal, Date akhir) {
        return referensi.selectAllPajak(awal, akhir);
    }

    @Transactional(readOnly = false)
    public void delete(String noPenerimaan, Character jenis) {
        AccGlTrans accGlTrans = mapperAccGl.selectOneRef(noPenerimaan);
        mapperAccGl.deleteDetail(accGlTrans.getGl_number());
        mapperAccGl.delete(accGlTrans.getGl_number());
        if (jenis.equals('S')) {
            mapperPenerimaanDetail.delete(noPenerimaan);
        } else {

            mapperPenerimaanDetail.deleteReguler(noPenerimaan);
        }
        referensi.delete(noPenerimaan);
    }

    @Transactional(readOnly = false)
    public void tambah(Penerimaan item, List<PenerimaanDetail> lPenerimaanDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.insert(item);
        this.tambahdetail(lPenerimaanDetail, item);
        this.tambahAccReguler(item, Boolean.FALSE, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PenerimaanDetail> lPenerimaanDetail, Penerimaan item) {

        for (int i = 0; i < lPenerimaanDetail.size(); i++) {
            PenerimaanDetail itemdetail = lPenerimaanDetail.get(i);
            itemdetail.setNo_penerimaan(item.getNo_penerimaan());
            itemdetail.setUrut(i + 1);
            mapperPenerimaanDetail.insert(itemdetail);

        }
        if (item.getStatus().equals('A')) {
            Double totalQty = 0.00;
            for (int j = 0; j < lPenerimaanDetail.size(); j++) {
                totalQty = totalQty + lPenerimaanDetail.get(j).getQty();
            }
            Double totalBiaya = item.getBiaya_transportasi() + item.getBiaya_asuransi() + item.getBiaya_bongkar_muat() + item.getBiaya_bongkar_muat_eksternal() - item.getTotal_discount();
            Double penambahHPP = totalBiaya / totalQty;
            NumberFormat formatter = new DecimalFormat("#0");
            Double tambahHpp = Double.parseDouble(formatter.format(penambahHPP));
            Po po = mapperPo.selectOne(item.getNomor_po());
            for (int i = 0; i < lPenerimaanDetail.size(); i++) {
                Double hppnya = lPenerimaanDetail.get(i).getHarga() + tambahHpp;
                mapperStokBarang.updateHPP(hppnya, item.getNomor_po(), lPenerimaanDetail.get(i).getId_barang());
                mapperPoDetail.updateInvoice(po.getId(), lPenerimaanDetail.get(i).getQty(), lPenerimaanDetail.get(i).getId_barang());
            }
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Penerimaan item, List<PenerimaanDetail> lPenerimaanDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.updatePenerimaan(item);
        mapperPenerimaanDetail.delete(item.getNo_penerimaan());
        tambahdetail(lPenerimaanDetail, item);
        AccGlTrans accGlTrans = mapperAccGl.selectOneRef(item.getNo_penerimaan());
        mapperAccGl.deleteDetail(accGlTrans.getGl_number());
        if (item.getStatus().equals('A')) {
            this.tambahApTrans(item);
            accGlTrans.setPosting(Boolean.TRUE);
            updateAccValue(item, lAccGlDetail);
        }
        accGlTrans.setNote(item.getKeterangan());
        accGlTrans.setGl_date(item.getTgl_penerimaan());

        mapperAccGl.updatePosting(accGlTrans);
        tambahjurnal(accGlTrans, lAccGlDetail);
    }
    
    @Transactional(readOnly = false)
    public void maintenance(Penerimaan item) {
        referensi.updatePenerimaan(item);
        AccApFaktur accApFaktur = new AccApFaktur();
        accApFaktur.setAp_number(item.getNo_penerimaan());
        accApFaktur.setAp_date(item.getTgl_penerimaan());
        accApFaktur.setDue_date(item.getTgl_jatuh_tempo());
        accApFaktur.setNotes(item.getKeterangan());
        accApFaktur.setReff(item.getReferensi());
        accApFaktur.setReff_date(item.getTanggal_referensi());
        accApFaktur.setTgl_invoice(item.getTanggal_referensi());
        accApFaktur.setReceive_date(item.getTgl_pengiriman());
        accApFaktur.setTop(item.getTop());
        mapperAccApFaktur.update(accApFaktur);
    }

    @Transactional(readOnly = true)
    public Penerimaan onLoad(String id) {
        return referensi.selectOneperPenerimaan(id);
    }

    @Transactional(readOnly = true)
    public List<PenerimaanDetail> onLoadDetail(String id) {
        return mapperPenerimaanDetail.selectOneperPenerimaanDetail(id);
    }

    @Transactional(readOnly = true)
    public Po selectOnePo(String id) {
        return mapperPo.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PoDetail> selectPoDetail(Integer Id) {
        return mapperPoDetail.selectOneperPo(Id);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwal(Penerimaan item) {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        Gudang gudang = mapperReferensi.selectOneperGudang(item.getId_gudang());
        InternalKantorCabang ikc = mapperKantor.selectOne(gudang.getId_kantor());
        AccGlDetail itemGl = new AccGlDetail();
        itemGl.setId_account(10603001);
        itemGl.setDebit(item.getDpp());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);
        if (item.getIs_ppn()) {
            itemGl = new AccGlDetail();
            itemGl.setId_account(10501004);
            itemGl.setDebit(item.getTotal_ppn());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }
        if (item.getIs_pph()) {
            itemGl = new AccGlDetail();
            itemGl.setId_account(10501001);
            itemGl.setDebit(item.getTotal_pph());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }

        itemGl = new AccGlDetail();
        itemGl.setId_account(mapperSupplier.selectAccount(item.getId_supplier()));
        itemGl.setCredit(item.getGrandtotal());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);
        Double persediaan = item.getDpp() + item.getBiaya_transportasi()
                + item.getBiaya_bongkar_muat_eksternal() + item.getBiaya_bongkar_muat() + item.getBiaya_asuransi();
        itemGl = new AccGlDetail();
        itemGl.setId_account(gudang.getAccount_persediaan());
        itemGl.setDebit(persediaan);
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        itemGl = new AccGlDetail();
        itemGl.setId_account(10603001);
        itemGl.setCredit(persediaan);
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        return lAccGlDetail;
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnal(String noPenerimaan) {
        return mapperAccGl.selectJurnalGl(noPenerimaan);
    }

    @Transactional(readOnly = false)
    public void tambahApTrans(Penerimaan item) {
        AccApFaktur accApFaktur = new AccApFaktur();
        accApFaktur.setAp_number(item.getNo_penerimaan());
        accApFaktur.setAp_date(item.getTgl_penerimaan());
        accApFaktur.setVendor_code(item.getId_supplier());
        accApFaktur.setDue_date(item.getTgl_jatuh_tempo());
        accApFaktur.setNotes(item.getKeterangan());
        accApFaktur.setReff(item.getReferensi());
        accApFaktur.setReff_date(item.getTanggal_referensi());
        accApFaktur.setId_perusahaan(item.getId_perusahaan());
        accApFaktur.setPo_number(item.getNomor_po());
        accApFaktur.setStatus('H');
        accApFaktur.setBayar(0.00);
        accApFaktur.setPpn(item.getTotal_ppn());
        accApFaktur.setPph(item.getTotal_pph());
        if (item.getJenis().equals('S')) {
            accApFaktur.setAmount(item.getDpp());
            accApFaktur.setTotal(item.getGrandtotal());
        } else {
            if(item.getTotal_pph() == null){
                item.setTotal_pph(0.00);
            }
            accApFaktur.setAmount(item.getDpp() - item.getTotal_pph());
            accApFaktur.setTotal(item.getGrandtotal() - item.getTotal_pph());
        }
        accApFaktur.setTgl_invoice(item.getTanggal_referensi());
        accApFaktur.setReceive_date(item.getTgl_pengiriman());
        accApFaktur.setTop(item.getTop());
        accApFaktur.setMaterai(item.getBiayalain());
        mapperAccApFaktur.insert(accApFaktur);
    }

    public void tambahjurnal(AccGlTrans itemTrans, List<AccGlDetail> lAccGlDetail) {
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccGlDetail accGlDetail = lAccGlDetail.get(i);
            accGlDetail.setLine(i + 1);
            accGlDetail.setJournal_code(itemTrans.getJournal_code());
            accGlDetail.setGl_number(itemTrans.getGl_number());
            accGlDetail.setGl_date(itemTrans.getGl_date());
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
    public void updateAccValue(Penerimaan item, List<AccGlDetail> lAccGlDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTgl_penerimaan());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTgl_penerimaan());
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

    public List<PoDetailReguler> selectPoDetailReguler(Integer id) {
        return mapperPoDetail.selectOneReguler(id);
    }

    public PermintaanPembelian selectPp(String id) {
        return mapperPermintaanPembelian.selectOne(id);
    }

    public List<PermintaanPembelianDetail> selectPpDetail(String id) {
        return mapperPermintaanPembelianDetail.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void tambahReguler(Penerimaan item, List<PenerimaanDetailReguler> lPenerimaanDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.insert(item);
        this.tambahdetailReguler(item, lPenerimaanDetail);
        this.tambahAccReguler(item, Boolean.FALSE, lAccGlDetail);
    }

    public void tambahdetailReguler(Penerimaan item, List<PenerimaanDetailReguler> lPenerimaanDetailReguler) {
        for (int i = 0; i < lPenerimaanDetailReguler.size(); i++) {
            PenerimaanDetailReguler itemdetailReguler = new PenerimaanDetailReguler();
            Integer k = i + 1;
            itemdetailReguler.setNo_penerimaan(item.getNo_penerimaan());
            itemdetailReguler.setUrut(k);
            itemdetailReguler.setNm_barang(lPenerimaanDetailReguler.get(i).getNm_barang());
            itemdetailReguler.setHarga(lPenerimaanDetailReguler.get(i).getHarga());
            itemdetailReguler.setQty(lPenerimaanDetailReguler.get(i).getQty());
            itemdetailReguler.setTotal(lPenerimaanDetailReguler.get(i).getTotal());
            itemdetailReguler.setDiorder(lPenerimaanDetailReguler.get(i).getDiorder());
            itemdetailReguler.setSatuan(lPenerimaanDetailReguler.get(i).getSatuan());
            mapperPenerimaanDetail.insertReguler(itemdetailReguler);

        }
    }

    public List<PenerimaanDetailReguler> selectPenerimaanDetailReguler(String noPenerimaan) {
        return mapperPenerimaanDetail.selectOneReguler(noPenerimaan);
    }

    public Supplier selectOneSupplier(String idSupplier) {
        return mapperSupplier.selectOne(idSupplier);
    }

    @Transactional(readOnly = false)
    public void ubahReguler(Penerimaan item, List<PenerimaanDetailReguler> lPenerimaanDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.updatePenerimaan(item);
        mapperPenerimaanDetail.deleteReguler(item.getNo_penerimaan());
        tambahdetailReguler(item, lPenerimaanDetail);
        AccGlTrans accGlTrans = mapperAccGl.selectOneRef(item.getNo_penerimaan());
        mapperAccGl.deleteDetail(accGlTrans.getGl_number());
        if (item.getStatus().equals('A')) {
            this.tambahApTrans(item);
            updateAccValue(item, lAccGlDetail);
            accGlTrans.setPosting(Boolean.TRUE);

        }
        accGlTrans.setNote(item.getKeterangan());
        accGlTrans.setGl_date(item.getTgl_penerimaan());
        mapperAccGl.updatePosting(accGlTrans);
        tambahjurnal(accGlTrans, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void tambahAccReguler(Penerimaan item, Boolean posting, List<AccGlDetail> lAccGlDetail) {
        AccGlTrans itemTrans = new AccGlTrans();
        String tahun = new SimpleDateFormat("yy").format(item.getTgl_penerimaan());
        String thn = new SimpleDateFormat("yyyy").format(item.getTgl_penerimaan());
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
        itemTrans.setGl_date(item.getTgl_penerimaan());
        itemTrans.setReference(item.getNo_penerimaan());
        itemTrans.setNote(item.getKeterangan());
        itemTrans.setPosting(posting);
        mapperAccGl.insert(itemTrans);
        tambahjurnal(itemTrans, lAccGlDetail);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwalReguler(Penerimaan item) {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        AccGlDetail itemGl = new AccGlDetail();
        //itemGl.setId_account(mapperSupplier.selectAccountBiaya(item.getId_supplier()));
        itemGl.setDebit(item.getDpp());
        itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
        lAccGlDetail.add(itemGl);

        if (item.getIs_ppn()) {
            itemGl = new AccGlDetail();
            itemGl.setId_account(10501004);
            itemGl.setGl_date(item.getTgl_penerimaan());
            itemGl.setDebit(item.getTotal_ppn());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }
        if (item.getTotal_pph() > 0) {
            itemGl = new AccGlDetail();
            //itemGl.setId_account();
            itemGl.setCredit(item.getTotal_pph());
            itemGl.setGl_date(item.getTgl_penerimaan());
            //itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
            
            itemGl = new AccGlDetail();
            itemGl.setId_account(mapperSupplier.selectAccount(item.getId_supplier()));
            itemGl.setCredit(item.getGrandtotal()- item.getTotal_pph());
            itemGl.setGl_date(item.getTgl_penerimaan());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        } else {
            itemGl = new AccGlDetail();
            itemGl.setId_account(mapperSupplier.selectAccount(item.getId_supplier()));
            itemGl.setCredit(item.getGrandtotal());
            itemGl.setGl_date(item.getTgl_penerimaan());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }

        return lAccGlDetail;
    }
    
    @Transactional(readOnly = false)
    public List<Penerimaan> selectSisaHutang(String idSupplier) {
        return referensi.selectSisaHutang(idSupplier);
    }
}
