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
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.db.mapper.MapperPemakaian;
import net.sra.prime.ultima.db.mapper.MapperPemakaianCancel;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.Pemakaian;
import net.sra.prime.ultima.entity.PemakaianDetail;
import net.sra.prime.ultima.entity.Pemakaian_cancel;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.StokValue;
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
public class ServicePemakaian {

    @Autowired
    MapperPemakaian referensi;

    @Autowired
    MapperBarang mapperBarang;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperPemakaianCancel mapperPemakaianCancel;

    @Transactional(readOnly = true)
    public String noMax(String idGudang, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idGudang, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<Pemakaian> onLoadList(Date awal, Date akhir, String id_pegawai, Character status) {
        return referensi.selectAll(awal, akhir, id_pegawai, status);
    }

    @Transactional(readOnly = true)
    public List<Pemakaian> onLoadListJurnal(Date awal, Date akhir, Character status) {
        return referensi.selectAllJurnal(awal, akhir, status);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Pemakaian item, List<PemakaianDetail> lPemakaianDetail) {
        referensi.insert(item);
        this.tambahdetail(lPemakaianDetail, item.getId());
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PemakaianDetail> lPemakaianDetail, Integer id) {
        for (int i = 0; i < lPemakaianDetail.size(); i++) {
            PemakaianDetail itemdetail = lPemakaianDetail.get(i);
            itemdetail.setUrut(i + 1);
            itemdetail.setId(id);
            referensi.insertDetail(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void updatestok(Pemakaian item, List<PemakaianDetail> lPemakaianDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        Integer j = 1;
        for (int i = 0; i < lPemakaianDetail.size(); i++) {
            PemakaianDetail itemdetail = lPemakaianDetail.get(i);
            //Double qty = itemdetail.getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            Double qty = itemdetail.getQty();
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            Double stoknya = mapperStokBarang.SumStokSatuanKecil(item.getId_gudang(), itemdetail.getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }
            if (stoknya < itemdetail.getQty()) {
                throw new java.lang.RuntimeException("Stok " + itemdetail.getNama_barang() + " tersisa " + stoknya + " " + itemdetail.getSatuan_kecil() + " !!! ");
            } else {
                while (qty > 0) {
                    StokBarang stokBarang = mapperStokBarang.selectForUpdate(item.getId_gudang(), itemdetail.getId_barang());
                    if (qty >= stokBarang.getStok()) {
                        qty = qty - stokBarang.getStok();
                        stokBarang.setStok(-1 * stokBarang.getStok());
                        mapperStokBarang.update(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getStok();
                        stokBarang.setStok(qty * -1);
                        mapperStokBarang.update(stokBarang);
                        qty = qty - tmpqty;
                    }
                }
            }
            ////// berfungsi untuk menambaha stok_value
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lPemakaianDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                    StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

                    if (stoktahunsebelumnya != null) {
                        // mengisi saldo awal dengan stok akhir tahun sebelumnya
                        stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                    } else {
                        stokValue.setDb0(0.00);
                    }
                    stokValue.setCr0(0.00);
                    // insert ke table stok_value
                mapperStokBarang.insertMutasi(stokValue);
            }
            //// penambahan nilai ke CR (c) pada table stok_value
            mapperStokBarang.updateStokValue(nomor, lPemakaianDetail.get(i).getQty(), item.getId_gudang(), lPemakaianDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lPemakaianDetail.get(i).getId_barang());
            
            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lPemakaianDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Pemakaian item, List<PemakaianDetail> lPemakaianDetail) {
        referensi.update(item);
        referensi.deleteDetail(item.getId());
        tambahdetail(lPemakaianDetail, item.getId());
        if (item.getStatus().equals('A')) {
            updatestok(item, lPemakaianDetail);
        }
    }
    
    @Transactional(readOnly = false)
    public void maintenance(Pemakaian item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Pemakaian onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PemakaianDetail> onLoadDetail(Integer id) {
        return referensi.selectOneDetail(id);
    }

    @Transactional(readOnly = true)
    public Gudang selectOneGudang(String idGudang) {
        return mapperReferensi.selectOneperGudang(idGudang);
    }

    @Transactional(readOnly = false)
    public void posting(Pemakaian item, List<PemakaianDetail> lPemakaianDetail, List<AccGlDetail> lAccGlDetail) {
        referensi.updateStatus(item.getNomor(), item.getStatus());
        AccGlTrans itemTrans = new AccGlTrans();
        itemTrans.setId_perusahaan("01");
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
        itemTrans.setJournal_code("JU");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNomor());
        itemTrans.setNote(item.getKeterangan());
        itemTrans.setPosting(Boolean.TRUE);
        mapperAccGl.insert(itemTrans);
        this.tambahjurnal(itemTrans, lAccGlDetail);
        updateAccValue(item, lAccGlDetail);
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
    public void updateAccValue(Pemakaian item, List<AccGlDetail> lAccGlDetail) {
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

    @Transactional(readOnly = true)
    public List<AccGlDetail> jurnalAwal(Pemakaian item) {
        List<AccGlDetail> lAccGlDetail = new ArrayList<>();
        lAccGlDetail = mapperAccGl.selectJurnalGl(item.getNomor());
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
    public String noMaxCancel(Integer bulan, Integer tahun) {
        return mapperPemakaianCancel.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = false)
    public void cancelPemakaian(Pemakaian item, List<PemakaianDetail> pemakaianDetail, String kode_user) throws Exception {

        referensi.updateStatus(item.getNomor(), item.getStatus());
        Pemakaian_cancel pemakaian_cancel = new Pemakaian_cancel();
        pemakaian_cancel.setNomor(item.getNo_cancel());
        pemakaian_cancel.setNo_pemakaian(item.getNomor());
        pemakaian_cancel.setPesan(item.getPesan());
        pemakaian_cancel.setKode_user(kode_user);
        mapperPemakaianCancel.insert(pemakaian_cancel);
        tambahstok(pemakaianDetail, item);

    }

    @Transactional(readOnly = false)
    public void tambahstok(List<PemakaianDetail> lPemakaianDetails, Pemakaian item) throws Exception {

        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        Date date = new Date();
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(date);
        Integer nomor = Integer.parseInt(bulan);
        Double qty = 0.00;

        for (int i = 0; i < lPemakaianDetails.size(); i++) {

            // Berfungsi untuk menambah ke tabel stok_barang
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lPemakaianDetails.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang());
            stokBarang.setStok(lPemakaianDetails.get(i).getQty());
            stokBarang.setTanggal(date);
            stokBarang.setReff(item.getNo_cancel());
            stokBarang.setHpp(mapperStokBarang.selectForGetHpp(item.getId_gudang(), lPemakaianDetails.get(i).getId_barang()).getHpp());
            mapperStokBarang.insert(stokBarang);

            ////// berfungsi untuk menambah ke stok_value
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lPemakaianDetails.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetails.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                    StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetails.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

                    if (stoktahunsebelumnya != null) {
                        // mengisi saldo awal dengan stok akhir tahun sebelumnya
                        stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                    } else {
                        stokValue.setDb0(0.00);
                    }
                    stokValue.setCr0(0.00);
                    // insert ke table stok_value
                mapperStokBarang.insertMutasi(stokValue);
            }
            // qty dikali dengan isi satuan
            mapperStokBarang.updateStokValue(nomor, lPemakaianDetails.get(i).getQty() , item.getId_gudang(), lPemakaianDetails.get(i).getId_barang(), tahun, 'd');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lPemakaianDetails.get(i).getId_barang());
            
            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPemakaianDetails.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lPemakaianDetails.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<PemakaianDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllPemakaian(id_barang, awal, akhir, gudang, status);
    }

}
