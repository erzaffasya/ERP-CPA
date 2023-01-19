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
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.StokOpname;
import net.sra.prime.ultima.entity.StokOpnameDetil;
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
public class ServiceStokOpname {
    

    @Autowired
    MapperStokBarang referensi;
    
    @Autowired
    MapperReferensi mapperReferensi;
    
    @Transactional(readOnly = true)
    public List<StokBarang> selectStok(String idGudang, String id_satuan, String id_kategori) {
        return referensi.selectStok(idGudang, id_satuan, id_kategori);
    }
    
    @Transactional(readOnly = true)
    public List<StokBarang> selectStokHpp(String idGudang) {
        return referensi.selectStokHpp(idGudang);
    }
    
    @Transactional(readOnly = true)
    public String noMax(String idGudang, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idGudang, bulan, tahun);
    }
    
    @Transactional(readOnly = false)
    public void tambah(StokOpname item) {
        referensi.insertStokOpname(item);
        referensi.insertStokbesar0(item.getId_gudang(), item.getId());
        referensi.insertStok0(item.getId_gudang(), item.getId());
        referensi.insertStokOpnameDetil(item.getId_gudang(), item.getId());
        referensi.updateSelish(item.getId());
        referensi.updateStatusStokOpname(item.getId(), 'D');
        mapperReferensi.updateStokopname(item.getId_gudang(), Boolean.TRUE);
    }
    
    @Transactional(readOnly = false)
    public void onIsUpload(Integer id , Boolean isupload) {
        referensi.updateIsUpload(id, isupload);
    }
    
    @Transactional(readOnly = false)
    public void updateStokDetil (List<StokOpnameDetil> lDetil, Integer id,String upload_by){
        //nolkan semua stok fisik
        referensi.updateStokDetilToZero(id);
        for (int i = 0; i < lDetil.size(); i++) {
            referensi.updateStokDetil(lDetil.get(i).getQty_stok(), lDetil.get(i).getId_barang(), lDetil.get(i).getId(), lDetil.get(i).getNotes());
        }
        referensi.updateIsReason(id);
        StokOpname item = new StokOpname();
        item.setId(id);
        item.setUpload_by(upload_by);
        item.setStatus('U');
        referensi.updateUploadStokOpname(item);
    }
    
    @Transactional(readOnly = false)
    public void approve (Integer id,String approved_by)throws IOException {
        StokOpname item = referensi.selectOneStokOpname(id);
        List<StokOpnameDetil> lsLebih = referensi.selectSelishlebih(id);
        List<StokOpnameDetil> lsKurang = referensi.selectSelishkurang(id);
        
        if(lsLebih.size() > 0){
            tambahstok(item, lsLebih);
        }
        if(lsKurang.size() > 0){
            /// mengurangi stok di table stok_barang
            updatestok(item, lsKurang);
            /// mengurangi stok di table stok_barang_opname
            updatestokTemp(item, lsKurang);
        }
        item.setStatus('A');
        item.setApproved_by(approved_by);
        item.setTotal(referensi.totalPersediaan(id));
        referensi.updateApproveStokOpname(item);
        mapperReferensi.updateBlind(item.getId_gudang(), Boolean.TRUE);
        mapperReferensi.updateStokopname(item.getId_gudang(), Boolean.FALSE);
    }
    
    @Transactional(readOnly = false)
    public void delete (Integer id,String id_gudang)throws IOException {
        referensi.deleteStokBarangOpname(id);
        referensi.deleteStokOpnameDetil(id);
        referensi.deleteStokOpname(id);
        mapperReferensi.updateBlind(id_gudang, Boolean.TRUE);
        mapperReferensi.updateStokopname(id_gudang, Boolean.FALSE);
    }
    
    @Transactional(readOnly = false)
    public void tambahstok(StokOpname item, List<StokOpnameDetil> lStokOpnameDetil) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lStokOpnameDetil.size(); i++) {
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lStokOpnameDetil.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang());
            stokBarang.setTanggal(item.getTanggal());
            stokBarang.setReff(item.getNomor());
            stokBarang.setStok(lStokOpnameDetil.get(i).getSelisih());
            stokBarang.setPersediaan(lStokOpnameDetil.get(i).getSelisih());
            stokBarang.setHpp(referensi.getHpp(item.getId(), stokBarang.getId_barang()));
            stokBarang.setId_stokopname(item.getId());
            /// tambahkan stok ke table stok_barang
            referensi.insert(stokBarang);
            /// tambahkan stok ke table stok_barang_opname
            referensi.insertTemp(stokBarang);
            
            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lStokOpnameDetil.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

                if (stoktahunsebelumnya != null) {
                    // mengisi saldo awal dengan stok akhir tahun sebelumnya
                    stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                } else {
                    stokValue.setDb0(0.00);
                }
                stokValue.setCr0(0.00);
                // insert ke table stok_value
                referensi.insertMutasi(stokValue);
            }
            //// penambahan nilai ke DB (d) pada table stok_value
            referensi.updateStokValue(nomor, lStokOpnameDetil.get(i).getSelisih(), item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun, 'd');
            referensi.updateSaldoAkhir(tahun, item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun);
                referensi.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }
    
    @Transactional(readOnly = false)
    public void updatestok(StokOpname item, List<StokOpnameDetil> lStokOpnameDetil) throws IOException {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);
        Double hpp = 0.00;
        Integer j = 1;
        for (int i = 0; i < lStokOpnameDetil.size(); i++) {
            StokOpnameDetil itemdetil = lStokOpnameDetil.get(i);
            Double qty = itemdetil.getSelisih() * -1 ;
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            // satuan besar
            Double stoknya = referensi.SumStok(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }

            // membandingkan dengan satuan besar
            if (stoknya < itemdetil.getSelisih() * -1 / itemdetil.getIsi_satuan()) {
                throw new java.lang.RuntimeException("Stok " + itemdetil.getBarang() + " tersisa " + stoknya + " " + itemdetil.getSatuan_besar() + " !!! ");
            } else {
                while (qty > 0) {
                    StokBarang stokBarang = referensi.selectForUpdate(item.getId_gudang(), itemdetil.getId_barang());
                    if (qty >= stokBarang.getStok()) {
                        qty = qty - stokBarang.getStok();
                        stokBarang.setStok(-1 * stokBarang.getStok());
                        referensi.update(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getStok();
                        stokBarang.setStok(qty * -1);
                        referensi.update(stokBarang);
                        qty = qty - tmpqty;
                    }
                }
            }
            ////// berfungsi untuk menambaha stok_value
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lStokOpnameDetil.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

                if (stoktahunsebelumnya != null) {
                    // mengisi saldo awal dengan stok akhir tahun sebelumnya
                    stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                } else {
                    stokValue.setDb0(0.00);
                }
                stokValue.setCr0(0.00);
                // insert ke table stok_value
                referensi.insertMutasi(stokValue);
            }
            //// penambahan nilai ke CR (c) pada table stok_value
            referensi.updateStokValue(nomor, lStokOpnameDetil.get(i).getSelisih() * -1, item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun, 'c');
            referensi.updateSaldoAkhir(tahun, item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = referensi.selectOneStokValue(item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), tahun);
                referensi.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lStokOpnameDetil.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
            
            //update PERSEDIAAAN ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            qty = itemdetil.getSelisih() * -1 ;

            while (qty > 0) {
                StokBarang stokBarang = referensi.selectForUpdatePersediaan(item.getId_gudang(), itemdetil.getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan  tidak cukup !!! (" + itemdetil.getId_barang() + " : " + itemdetil.getBarang() + ")");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException(itemdetil.getBarang() + " HPP Belum diinput !!! Nomor Dokumen :" + stokBarang.getReff());
                } else {
                    hpp = stokBarang.getHpp();
                    if (qty >= stokBarang.getPersediaan()) {
                        qty = qty - stokBarang.getPersediaan();
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        referensi.updatePersediaan(stokBarang);

                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        stokBarang.setPersediaan(qty * -1);
                        referensi.updatePersediaan(stokBarang);
                        qty = qty - tmpqty;
                    }

                }
            }
        }
    }
    
    @Transactional(readOnly = false)
    public void updatestokTemp(StokOpname item, List<StokOpnameDetil> lStokOpnameDetil) throws IOException {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);
        
        for (int i = 0; i < lStokOpnameDetil.size(); i++) {
            StokOpnameDetil itemdetil = lStokOpnameDetil.get(i);
            Double qty = itemdetil.getSelisih() * -1;
            while (qty > 0) {
                StokBarang stokBarang = referensi.selectForUpdateTemp(item.getId(), itemdetil.getId_barang());
                if (qty >= stokBarang.getStok()) {
                    qty = qty - stokBarang.getStok();
                    stokBarang.setStok(-1 * stokBarang.getStok());
                    referensi.updateTemp(stokBarang);
                } else {
                    Double tmpqty = stokBarang.getStok();
                    stokBarang.setStok(qty * -1);
                    referensi.updateTemp(stokBarang);
                    qty = qty - tmpqty;
                }
            }
        }
    }
    
    
    
    @Transactional(readOnly = true)
    public StokOpname selectOneStokOpname(Integer id){
        return  referensi.selectOneStokOpname(id);
    }
    
    @Transactional(readOnly = true)
    public StokOpnameDetil selectOneStokOpnameDetil(Integer id,String id_barang){
        return  referensi.selectOneStokOpnameDetil(id,id_barang);
    }
    
    @Transactional(readOnly = true)
    public List<StokOpnameDetil> selectDetilStokOpname(Integer id){
        return  referensi.selectDetilStokOpname(id);
    }
    
    @Transactional(readOnly = true)
    public List<StokOpname> selectAllStokOpname(Date tglmulai, Date tglselesai, Character status, String id_pegawai){
        return  referensi.selectAllStokOpname(tglmulai, tglselesai, status,id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public int selectCountStokOPname(String id_gudang){
        return referensi.selectCountStokOPname(id_gudang);
    }
    
    public List<StokBarang> selectAllStokOpnameBarang (Integer id){
        return referensi.selectAllStokOpnameBarang(id);
    }
    
    public void updateHppFromZero(Integer id_stokopname){
        referensi.updateHPPFromZero(id_stokopname);
        referensi.updateTotalStokOpname(id_stokopname, referensi.totalPersediaan(id_stokopname));
    }
}
