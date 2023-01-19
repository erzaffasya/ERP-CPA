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
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperDoDetail;
import net.sra.prime.ultima.db.mapper.MapperDotbl;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPenjualan;
import net.sra.prime.ultima.db.mapper.MapperPenjualanDetail;
import net.sra.prime.ultima.db.mapper.MapperPenjualanDo;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperSoDetail;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.AccArFaktur;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.Dotbl;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.entity.Penjualan;
import net.sra.prime.ultima.entity.PenjualanDetailReguler;
import net.sra.prime.ultima.entity.PenjualanDo;
import net.sra.prime.ultima.entity.PesanCancel;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.entity.StokBarang;
import org.joda.time.DateTime;
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
public class ServicePenjualan {

    @Autowired
    MapperPenjualan referensi;

    @Autowired
    MapperPenjualanDetail mapperPenjualanDetail;

    @Autowired
    MapperPenjualanDo mapperPenjualanDo;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperCustomer mapperCustomer;

    @Autowired
    MapperAccArFaktur mapperAccArFaktur;

    @Autowired
    MapperSoDetail mapperSoDetail;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperDotbl mapperDotbl;

    @Autowired
    MapperDoDetail mapperDoDetail;

    @Autowired
    MapperPesanCancel mapperPesanCancel;
    
    
    @Transactional(readOnly = true)
    public String noMax(Integer tahun) {
        return referensi.SelectMax(tahun);
    }

    public InternalKantorCabang selecOne(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public List<Penjualan> onLoadList(Date awal, Date akhir, Character status, Character jenis) {
        return referensi.selectAll(awal, akhir, status, jenis);
    }

    @Transactional(readOnly = true)
    public List<Penjualan> onLoadListAdmin(Date awal, Date akhir, String id_kantor) {
        return referensi.selectAllAdmin(awal, akhir, id_kantor);
    }

    @Transactional(readOnly = false)
    public void delete(String noPenjualan) {
        mapperPenjualanDetail.delete(noPenjualan);
        List<PenjualanDo> lPenjualanDos = mapperPenjualanDo.selectAll(noPenjualan);
        for (int i = 0; i < lPenjualanDos.size(); i++) {
            mapperDotbl.updateStatus('R', lPenjualanDos.get(i).getNo_do());
        }
        referensi.deletePenjualanDo(noPenjualan);
        referensi.delete(noPenjualan);

    }

    @Transactional(readOnly = false)
    public void tambah(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<Dotbl> lDoaTmp) throws IOException {
        referensi.insert(item);
        this.tambahpenjualando(item, lDoaTmp);
        this.tambahdetail(lPenjualanDetail, item);
        if (item.getStatus().equals('P')) {
            this.updatePersediaan(lPenjualanDetail, item);
        }
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PenjualanDetail> lPenjualanDetail, Penjualan item) {

        for (int i = 0; i < lPenjualanDetail.size(); i++) {
            PenjualanDetail itemdetail = lPenjualanDetail.get(i);
            itemdetail.setNo_penjualan(item.getNo_penjualan());
            itemdetail.setUrut(i + 1);
            mapperPenjualanDetail.insert(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void tambahpenjualando(Penjualan item, List<Dotbl> lDoaTmp) {
        for (int i = 0; i < lDoaTmp.size(); i++) {
            PenjualanDo penjualanDo = new PenjualanDo();
            penjualanDo.setNo_penjualan(item.getNo_penjualan());
            penjualanDo.setNo_do(lDoaTmp.get(i).getNomor());
            mapperPenjualanDo.insert(penjualanDo);

        }
    }

    @Transactional(readOnly = false)
    public void updatePersediaan(List<PenjualanDetail> lPenjualanDetail, Penjualan item) throws IOException {
        Double qty;
        for (int i = 0; i < lPenjualanDetail.size(); i++) {
            
            qty = lPenjualanDetail.get(i).getQty() * lPenjualanDetail.get(i).getIsi_satuan();
            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang(), lPenjualanDetail.get(i).getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Stok Persediaan   " + lPenjualanDetail.get(i).getNama_barang() + " tidak cukup !!!");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException(lPenjualanDetail.get(i).getNama_barang() + " HPP Belum diinput !!! Nomor Dokumen : " + stokBarang.getReff());
                } else {
                    if (qty >= stokBarang.getPersediaan()) {
                        qty = qty - stokBarang.getPersediaan();
                        mapperPenjualanDetail.insertPenjualanHPP(item.getNo_penjualan(), lPenjualanDetail.get(i).getId_barang(), stokBarang.getHpp(), stokBarang.getId_stok(),stokBarang.getPersediaan());
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        mapperStokBarang.updatePersediaan(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        stokBarang.setPersediaan(qty * -1);
                        mapperStokBarang.updatePersediaan(stokBarang);
                        mapperPenjualanDetail.insertPenjualanHPP(item.getNo_penjualan(), lPenjualanDetail.get(i).getId_barang(), stokBarang.getHpp(), stokBarang.getId_stok(),qty);
                        qty = qty - tmpqty;
                    }

                }
            }

        }

    }

    @Transactional(readOnly = false)
    public void ubah(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<Dotbl> lDoaTmp, Character jns) {
        mapperPenjualanDetail.delete(item.getNo_penjualan());
        referensi.update(item);
        this.tambahdetail(lPenjualanDetail, item);
        //Berfungsi untuk mengembalikan status do yang lama menjadi  Receipt(R)
        List<PenjualanDo> lPenjualanDo = selectOnePenjualanDo(item.getNo_penjualan());
        for (int i = 0; i < lPenjualanDo.size(); i++) {
            referensi.updateStatusDo(lPenjualanDo.get(i).getNo_do(), 'R');
        }

        referensi.deletePenjualanDo(item.getNo_penjualan());
        this.tambahpenjualando(item, lDoaTmp);
        // Berfungsi untuk mengubah status DO yang dipilih menjadi Invoice (I)
        lPenjualanDo = referensi.selectPenjualanDo(item.getNo_penjualan());
        for (int i = 0; i < lPenjualanDo.size(); i++) {
            referensi.updateStatusDo(lPenjualanDo.get(i).getNo_do(), 'I');
        }

        //jika ini adalah maintenance
        if (jns == null) {
            Penjualan penjualan = referensi.selectOne(item.getNo_penjualan());
            // untuk mengubah tanggal di acc_gl_trans dan acc_gl_detail 
            if (penjualan.getStatus().equals('P') && penjualan.getTanggal() == item.getTanggal()) {
                mapperAccGl.updateDate(item.getTanggal(), item.getNo_penjualan());
                mapperAccGl.updateDateDetail(item.getTanggal(), item.getNo_penjualan());
                
            }
        }
    }

    @Transactional(readOnly = false)
    public void posting(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<AccGlDetail> lAccGlDetail) throws IOException {
        referensi.updateStatus(item.getNo_penjualan(), item.getStatus());
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan(item.getId_perusahaan());
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        String noMax1 = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax1 == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax1);
            nomor = nomor + 1;
            noMax1 = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax1 + tahun);
        }
        itemTrans.setJournal_code("JAR");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNo_penjualan());
        itemTrans.setNote("Invoice " + item.getCustomer() + " Nomor Do : " + mapperPenjualanDo.selectDo(item.getNo_penjualan()));
        itemTrans.setPosting(Boolean.TRUE);
        mapperAccGl.insert(itemTrans);
        this.updatePersediaan(lPenjualanDetail, item);
        this.tambahjurnal(itemTrans, lAccGlDetail);
        //tanggal terima invoice diisi tanggal invoice untuk mengisi tabel acc_ar_faktur
        item.setTgl_terimainvoice(item.getTanggal());
        this.tambahAr(item);
        //}
        updateAccValue(item, lAccGlDetail);
        
             
    }
    
    @Transactional(readOnly = false)
    public void maintenancePosting(Penjualan item) throws IOException {
        AccArFaktur accArFaktur = new AccArFaktur();
        
        accArFaktur.setNo_invoice(item.getNo_penjualan());
        accArFaktur.setInvoice_date(item.getTanggal());
        //accArFaktur.setAr_date(item.getTanggal());
        accArFaktur.setNotes(item.getKeterangan());
        accArFaktur.setTop(item.getTop());
        accArFaktur.setDue_date(item.getTgl_jatuh_tempo());
        accArFaktur.setReff(item.getReferensi());
        mapperAccArFaktur.updateMaintenance(accArFaktur);
    }

    @Transactional(readOnly = false)
    public void postingReguler(Penjualan item, List<AccGlDetail> lAccGlDetail) throws IOException {
        referensi.updateStatus(item.getNo_penjualan(), item.getStatus());
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan(item.getId_perusahaan());
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        String noMax1 = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax1 == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax1);
            nomor = nomor + 1;
            noMax1 = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax1 + tahun);
        }
        itemTrans.setJournal_code("JAR");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNo_penjualan());
        itemTrans.setNote("Invoice " + item.getCustomer() + " No.Po : " + item.getReferensi());
        itemTrans.setPosting(Boolean.TRUE);
        mapperAccGl.insert(itemTrans);

        this.tambahjurnal(itemTrans, lAccGlDetail);
        //uncomment this if payment cash
        //if (!item.getIs_invoice()) {
        item.setTgl_terimainvoice(item.getTanggal());
        this.tambahAr(item);
        //}
        updateAccValue(item, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void unPosting(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<AccGlDetail> lAccGlDetail) throws IOException {
        referensi.updateStatus(item.getNo_penjualan(), item.getStatus());
        String glNumber = mapperAccGl.selectOneRef(item.getNo_penjualan()).getGl_number();
        mapperAccGl.deleteDetail(glNumber);
        mapperAccGl.delete(glNumber);
        //  mapperAccArFaktur.selectOne(glNumber)
        this.updatePersediaan(lPenjualanDetail, item);
        this.tambahAr(item);

        //}
        updateAccValue(item, lAccGlDetail);
    }

    @Transactional(readOnly = false)
    public void terimaInvoice(Penjualan item) throws IOException {
        referensi.updateTanggalTerimaInvoice(item.getNo_penjualan(), item.getTgl_terimainvoice(), item.getTgl_jatuh_tempo());
        referensi.updateArTanggalTerimaInvoice(item.getNo_penjualan(), item.getTgl_terimainvoice(), item.getTgl_jatuh_tempo());
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
    public void tambahAr(Penjualan item) {
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
        accArFaktur.setNo_invoice(item.getNo_penjualan());
        accArFaktur.setInvoice_date(item.getTanggal());
        //accArFaktur.setAr_date(item.getTanggal());
        accArFaktur.setCustomer_code(item.getId_customer());
        accArFaktur.setAmount(item.getDpp() + item.getBiayalain());
        accArFaktur.setNotes(item.getKeterangan());
        accArFaktur.setId_perusahaan(item.getId_perusahaan());
        accArFaktur.setBayar(0.00);
        accArFaktur.setPpn(item.getTotal_ppn());
        accArFaktur.setTotal(item.getGrandtotal());
        accArFaktur.setTop(item.getTop());
        accArFaktur.setDue_date(item.getTgl_jatuh_tempo());
        accArFaktur.setTgl_terimainvoice(item.getTgl_terimainvoice());
        accArFaktur.setId_salesman(item.getId_salesman());
        accArFaktur.setReff(item.getReferensi());
        mapperAccArFaktur.insert(accArFaktur);
    }

    @Transactional(readOnly = true)
    public Penjualan onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public Penjualan selectOnePenjualan(String id) {
        return referensi.selectOnePenjualan(id);
    }

    @Transactional(readOnly = true)
    public Penjualan onLoadReguler(String id) {
        return referensi.selectOneReguler(id);
    }

    @Transactional(readOnly = true)
    public List<PenjualanDetail> onLoadDetail(String id) {
        return mapperPenjualanDetail.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PenjualanDo> selectOnePenjualanDo(String id) {
        return referensi.selectPenjualanDo(id);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwal(Penjualan item, List<PenjualanDetail> lPenjualanDetail) throws IOException {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        if (item.getStatus().equals('P')) {
            lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNo_penjualan());
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                AccGlDetail itemdetail = lAccGlDetail.get(i);
                if (itemdetail.getIs_debit()) {
                    itemdetail.setDebit(itemdetail.getValue());
                } else {
                    itemdetail.setCredit(itemdetail.getValue());
                }
                lAccGlDetail.set(i, itemdetail);
            }
            //} else if(!item.getIs_invoice()){
        } else {

            Gudang gudang = mapperReferensi.selectOneperGudang(item.getId_gudang());

            InternalKantorCabang ikc = mapperKantor.selectOne(gudang.getId_kantor());

            AccGlDetail itemGl = new AccGlDetail();
            itemGl.setId_account(mapperCustomer.selectAccount(item.getId_customer(), gudang.getId_kantor()));
            itemGl.setDebit(item.getGrandtotal());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);

            itemGl = new AccGlDetail();
            itemGl.setId_account(ikc.getAccount_penjualan());
            itemGl.setCredit(item.getDpp());
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);

            if (item.getIs_ppn()) {
                itemGl = new AccGlDetail();
                itemGl.setId_account(20301001);
                itemGl.setCredit(item.getTotal_ppn());
                itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
                lAccGlDetail.add(itemGl);
            }

            if (item.getBiayalain() != null && item.getBiayalain() != 0) {
                itemGl = new AccGlDetail();
                itemGl.setId_account(80101005);
                itemGl.setCredit(item.getBiayalain());
                itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
                lAccGlDetail.add(itemGl);
            }

            Double hpp = hitungHpp(item, lPenjualanDetail);
            itemGl = new AccGlDetail();
            itemGl.setId_account(ikc.getAccount_hpp());
            itemGl.setDebit(hpp);
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);

            itemGl = new AccGlDetail();
            itemGl.setId_account(gudang.getAccount_persediaan());
            itemGl.setCredit(hpp);
            itemGl.setAccount(mapperAccount.selectAccount(itemGl.getId_account()));
            lAccGlDetail.add(itemGl);
        }
        return lAccGlDetail;
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwalReguler(Penjualan item) throws IOException {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        if (item.getStatus().equals('P')) {
            lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNo_penjualan());
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                AccGlDetail itemdetail = lAccGlDetail.get(i);
                if (itemdetail.getIs_debit()) {
                    itemdetail.setDebit(itemdetail.getValue());
                } else {
                    itemdetail.setCredit(itemdetail.getValue());
                }
                lAccGlDetail.set(i, itemdetail);
            }
            //} else if(!item.getIs_invoice()){
        }
        return lAccGlDetail;
    }

    @Transactional(readOnly = true)
    public Double hitungHpp(Penjualan item, List<PenjualanDetail> lPenjualanDetail) throws IOException {
        Double hpp = 0.00;
        for (int i = 0; i < lPenjualanDetail.size(); i++) {
            Double qty = lPenjualanDetail.get(i).getQty() * lPenjualanDetail.get(i).getIsi_satuan();

            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang(), lPenjualanDetail.get(i).getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan " + lPenjualanDetail.get(i).getNama_barang()+ "tidak cukup !!!");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Gagal Membuat Jurnal Karena HPP " + lPenjualanDetail.get(i).getNama_barang() + " Belum diinput !!! Nomor Dokumen : " + stokBarang.getReff());
                } else {
                    if (qty > stokBarang.getPersediaan()) {
                        qty = qty - stokBarang.getPersediaan();
                        hpp = hpp + (stokBarang.getPersediaan() * stokBarang.getHpp());
                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        hpp = hpp + (qty * stokBarang.getHpp());
                        qty = qty - tmpqty;
                    }

                }
            }
        }
        return hpp;
    }

    public void CekHpp(Penjualan item, List<PenjualanDetail> lPenjualanDetail) throws IOException {
        Double hpp = 0.00;
        for (int i = 0; i < lPenjualanDetail.size(); i++) {
            Double qty = lPenjualanDetail.get(i).getQty() * lPenjualanDetail.get(i).getIsi_satuan();

            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang(), lPenjualanDetail.get(i).getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan tidak cukup !!!");
                    //return false;
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    //return false;
                    throw new java.lang.RuntimeException("Gagal Membuat Jurnal Karena HPP " + lPenjualanDetail.get(i).getNama_barang() + " Belum diinput !!! Nomor Dokumen : " + stokBarang.getReff());
                } else {
                    if (qty > stokBarang.getPersediaan()) {
                        qty = qty - stokBarang.getPersediaan();
                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        qty = qty - tmpqty;
                    }

                }
            }
        }
        //return true;
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnal(String noPenjualan) {
        return mapperAccGl.selectJurnalGl(noPenjualan);
    }

    @Transactional(readOnly = true)
    public Dotbl selectOneDo(String nomor) {
        return mapperDotbl.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> selectOneSo(String nomor) {
        return mapperSoDetail.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public DoDetail selectSumDoDetail(String nomor, String idBarang) {
        return mapperDoDetail.selectSum(nomor, idBarang);
    }

    @Transactional(readOnly = false)
    public void updateAccValue(Penjualan item, List<AccGlDetail> lAccGlDetail) {
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
    public void ubahStatus(Penjualan item, String kode_user) {
        referensi.updateStatusAja(item.getStatus(), item.getNo_penjualan());
        PesanCancel pesanCancel = new PesanCancel();
        pesanCancel.setNomor(item.getNo_penjualan());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
        List<PenjualanDo> lPenjualanDos = mapperPenjualanDo.selectAll(item.getNo_penjualan());
        for (int i = 0; i < lPenjualanDos.size(); i++) {
            mapperDotbl.updateStatus('R', lPenjualanDos.get(i).getNo_do());
        }
    }

    ///////////// Reguler //////////////////
    @Transactional(readOnly = false)
    public void tambahReguler(Penjualan item, List<PenjualanDetailReguler> lPenjualanDetailReguler) throws IOException {
        referensi.insert(item);
        this.tambahdetailReguler(lPenjualanDetailReguler, item);
//        if (item.getStatus().equals('P')) {
//            this.updatePersediaan(lPenjualanDetail, item);
//        }
    }

    @Transactional(readOnly = false)
    public void tambahdetailReguler(List<PenjualanDetailReguler> lPenjualanDetailReguler, Penjualan item) {

        for (int i = 0; i < lPenjualanDetailReguler.size(); i++) {
            PenjualanDetailReguler itemdetail = lPenjualanDetailReguler.get(i);
            itemdetail.setNo_penjualan(item.getNo_penjualan());
            itemdetail.setUrut(i + 1);
            mapperPenjualanDetail.insertReguler(itemdetail);
        }
    }

    @Transactional(readOnly = true)
    public List<PenjualanDetailReguler> onLoadDetailReguler(String id) {
        return mapperPenjualanDetail.selectOneReguler(id);
    }

    @Transactional(readOnly = true)
    public Customer selectOneCustomer(String id) {
        return mapperCustomer.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void ubahReguler(Penjualan item, List<PenjualanDetailReguler> lPenjualanDetailRegulers) {
        mapperPenjualanDetail.deleteReguler(item.getNo_penjualan());
        referensi.update(item);
        this.tambahdetailReguler(lPenjualanDetailRegulers, item);
    }

    @Transactional(readOnly = false)
    public void deleteReguler(String noPenjualan) {
        mapperPenjualanDetail.deleteReguler(noPenjualan);
        referensi.delete(noPenjualan);

    }

    @Transactional(readOnly = false)
    public List<Penjualan> selectSisaHutang(String id_customer) {
        return referensi.selectSisaHutang(id_customer);

    }

    @Transactional(readOnly = false)
    public List<Penjualan> selectPenjualanPosting() {
        return referensi.selectPenjualanPosting();

    }

    @Transactional(readOnly = false)
    public List<Penjualan> selectPenjualanByCustomer(String id_customer) {
        return referensi.selectAllPenjualanCustomer(id_customer);

    }
    
    public List<Penjualan> selectPenjualanperBulan(Integer tahun, Integer bulan) {
        return referensi.selectPenjualanperBulan(tahun, bulan);
    }

    @Transactional(readOnly = false)
    public void PostingAll(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<AccGlDetail> lAccGlDetail) throws IOException {
        referensi.updateStatus(item.getNo_penjualan(), item.getStatus());
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan(item.getId_perusahaan());
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        String noMax1 = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax1 == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax1);
            nomor = nomor + 1;
            noMax1 = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax1 + tahun);
        }
        itemTrans.setJournal_code("JAR");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNo_penjualan());
        itemTrans.setNote("Invoice " + item.getCustomer() + " Nomor Do : " + mapperPenjualanDo.selectDo(item.getNo_penjualan()));
        itemTrans.setPosting(Boolean.TRUE);
        mapperAccGl.insert(itemTrans);
        updatePersediaan(lPenjualanDetail, item);
        tambahjurnal(itemTrans, lAccGlDetail);
        //uncomment this if payment cash
        //if (!item.getIs_invoice()) {
        item.setTgl_terimainvoice(item.getTanggal());
        DateTime duedate = new DateTime(item.getTanggal());
        DateTime jt = duedate.plusDays(item.getTop());
        item.setTgl_jatuh_tempo(jt.toDate());

        this.tambahAr(item);
        updateAccValue(item, lAccGlDetail);

    }

}
