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
import net.sra.prime.ultima.db.mapper.MapperPenerimaanGudang;
import net.sra.prime.ultima.db.mapper.MapperPenerimaanGudangCancel;
import net.sra.prime.ultima.db.mapper.MapperPo;
import net.sra.prime.ultima.db.mapper.MapperPoDetail;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.PenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudangCancel;
import net.sra.prime.ultima.entity.PenerimaanGudangDetail;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
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
public class ServicePenerimaanGudang {

    @Autowired
    MapperPenerimaanGudang referensi;

    @Autowired
    MapperPo mreferensi;

    @Autowired
    MapperPoDetail mapper;

    @Autowired
    MapperPoDetail mapperPoDetail;

    @Autowired
    MapperStokBarang mapperStokBarang;
    
    @Autowired
    MapperReferensi mapperReferensi;
    
    @Autowired
    MapperPenerimaanGudangCancel mapperPenerimaanGudangCancel;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor,bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<PenerimaanGudang> onLoadList(Date awal, Date akhir, String idPegawai, Boolean status, Boolean st) {
        return referensi.SelectAll(awal, akhir, idPegawai, status, st);
    }

    @Transactional(readOnly = false)
    public void delete(String noPenerimaan) {
        referensi.deleteDetail(noPenerimaan);
        referensi.delete(noPenerimaan);
    }

    @Transactional(readOnly = false)
    public void tambah(PenerimaanGudang item, List<PenerimaanGudangDetail> lPenerimaanGudangDetail) {
        referensi.insert(item);
        this.tambahdetail(lPenerimaanGudangDetail, item.getNo_penerimaan());

    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PenerimaanGudangDetail> lPenerimaanGudangDetail, String id) {
        Double qty;
        for (int i = 0; i < lPenerimaanGudangDetail.size(); i++) {

            PenerimaanGudangDetail itemdetail = lPenerimaanGudangDetail.get(i);
            itemdetail.setNo_penerimaan(id);
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);

        }
    }

    @Transactional(readOnly = false)
    public void tambahstok(List<PenerimaanGudangDetail> lPenerimaanDetail, PenerimaanGudang item) {

        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTgl_penerimaan());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTgl_penerimaan());
        Integer nomor = Integer.parseInt(bulan);
        Double qty = 0.00;

        for (int i = 0; i < lPenerimaanDetail.size(); i++) {
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lPenerimaanDetail.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang());
            stokBarang.setStok(lPenerimaanDetail.get(i).getQty());
            stokBarang.setBatch(lPenerimaanDetail.get(i).getBatch());
            stokBarang.setTanggal(item.getTgl_penerimaan());
            stokBarang.setHpp(0.00);
            stokBarang.setReff(item.getNo_penerimaan());
            stokBarang.setPersediaan(lPenerimaanDetail.get(i).getQty());
            mapperStokBarang.insert(stokBarang);

            qty = 0.00;
            qty = referensi.jumlahQty(item.getNomor_po(), lPenerimaanDetail.get(i).getId_barang());
            mapperPoDetail.update(mreferensi.selectOne(item.getNomor_po()).getId(), qty, lPenerimaanDetail.get(i).getId_barang());

            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lPenerimaanDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPenerimaanDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPenerimaanDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun)-1));
                if(stoktahunsebelumnya != null){
                    stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                }else{
                    stokValue.setDb0(0.00);
                }
                // insert ke table stok_value
                stokValue.setCr0(0.00);
                mapperStokBarang.insertMutasi(stokValue);
            }
            mapperStokBarang.updateStokValue(nomor, lPenerimaanDetail.get(i).getQty(), item.getId_gudang(), lPenerimaanDetail.get(i).getId_barang(), tahun, 'd');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(),lPenerimaanDetail.get(i).getId_barang());
            
            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang=thn.format(new Date());
            if(Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)  ){
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPenerimaanDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(),lPenerimaanDetail.get(i).getId_barang(), stokakhir.getDb13()-stokakhir.getCr13(),0.00);
            }
            
        }
    }

    @Transactional(readOnly = false)
    public void ubah(PenerimaanGudang item, List<PenerimaanGudangDetail> lPenerimaanGudangDetail) {
        referensi.deleteDetail(item.getNo_penerimaan());
        referensi.update(item);
        this.tambahdetail(lPenerimaanGudangDetail, item.getNo_penerimaan());
        if (item.getStatus() == true) {
            this.tambahstok(lPenerimaanGudangDetail, item);
        }
    }
    
    @Transactional(readOnly = false)
    public void maintenance(PenerimaanGudang item) {
        referensi.maintenance(item);
    }

    @Transactional(readOnly = true)
    public PenerimaanGudang onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PenerimaanGudangDetail> onLoadDetail(String id) {
        return referensi.selectOneperPenerimaanDetail(id);
    }

    @Transactional(readOnly = true)
    public Po selectOnePo(String id) {
        return mreferensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PoDetail> selectPoDetail(Integer Id) {
        return mapper.selectOneperPo(Id);
    }
    
    @Transactional(readOnly = true)
    public Gudang selectOneGudang(String id_gudang) {
        return mapperReferensi.selectOneperGudang(id_gudang);
    }
    
    @Transactional(readOnly = true)
    public Integer cekPo(String id) {
        return referensi.cekPo(id);
    }
    
    @Transactional(readOnly = true)
    public String noMaxCancel(Integer bulan, Integer tahun) {
        return mapperPenerimaanGudangCancel.SelectMax(bulan, tahun);
    }
    
    @Transactional(readOnly = false)
    public void cancelPenerimaan(PenerimaanGudang item, List<PenerimaanGudangDetail> lPenerimaanGudangDetail, String kode_user) throws Exception {
        PenerimaanGudangCancel penerimaanGudangCancel = new PenerimaanGudangCancel();
        penerimaanGudangCancel.setNomor(item.getNo_cancel());
        penerimaanGudangCancel.setNo_penerimaan(item.getNo_penerimaan());
        penerimaanGudangCancel.setPesan(item.getPesan());
        penerimaanGudangCancel.setKode_user(kode_user);
        mapperPenerimaanGudangCancel.insert(penerimaanGudangCancel);
        updatestokCancel(item, lPenerimaanGudangDetail);
        referensi.updateStatus(item.getStatus(), item.getNo_penerimaan());
    }
    
    @Transactional(readOnly = false)
    public void updatestokCancel(PenerimaanGudang item, List<PenerimaanGudangDetail> lpenerimaanGudangDetail) throws IOException {

        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        Date date = new Date();
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(date);
        Integer nomor = Integer.parseInt(bulan);
        Double totalhpp = 0.00;
        Integer j = 1;
        for (int i = 0; i < lpenerimaanGudangDetail.size(); i++) {
            PenerimaanGudangDetail itemdetail = lpenerimaanGudangDetail.get(i);
            Double qty = itemdetail.getQty() ;
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            // satuan besar
            Double stoknya = mapperStokBarang.SumStokSatuanKecil(item.getId_gudang(), itemdetail.getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }

            // membandingkan dengan satuan besar
            if (stoknya < itemdetail.getQty()) {
                throw new java.lang.RuntimeException("Stok " + itemdetail.getNama_barang() + " tersisa " + stoknya + " " + itemdetail.getSatuan_kecil()+ " !!! ");
            } else {
                while (qty > 0) {
                    // Ambil barang dari no IT asal dulu jika kurang baru dari sumber lain
                    StokBarang stokBarang = mapperStokBarang.selectBackIT(item.getId_gudang(), itemdetail.getId_barang(), item.getNo_penerimaan());
                    if (stokBarang == null) {
                        stokBarang = mapperStokBarang.selectForUpdate(item.getId_gudang(), itemdetail.getId_barang());
                    }
                    if (qty >= stokBarang.getStok()) {
                        qty = qty - stokBarang.getStok();
                        stokBarang.setStok(-1 * stokBarang.getStok());
                        mapperStokBarang.update(stokBarang);
                        // berfungsi untuk mengupdate out standing po di tabel po detail
                        mapperPoDetail.updateCancel(mreferensi.selectOne(item.getNomor_po()).getId(), (-1 * stokBarang.getStok()), itemdetail.getId_barang());
                    } else {
                        Double tmpqty = stokBarang.getStok();
                        stokBarang.setStok(qty * -1);
                        mapperStokBarang.update(stokBarang);
                        // berfungsi untuk mengupdate out standing po di tabel po detail
                        mapperPoDetail.updateCancel(mreferensi.selectOne(item.getNomor_po()).getId(), qty, itemdetail.getId_barang());
                        qty = qty - tmpqty;
                    }
                }
            }

            ////// berfungsi untuk menambahah stok_value
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lpenerimaanGudangDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lpenerimaanGudangDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lpenerimaanGudangDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun)-1));
                if(stoktahunsebelumnya != null){
                    stokValue.setDb0(stoktahunsebelumnya.getDb13() - stoktahunsebelumnya.getCr13());
                }else{
                    stokValue.setDb0(0.00);
                }
                // insert ke table stok_value
                stokValue.setCr0(0.00);
                mapperStokBarang.insertMutasi(stokValue);
            }
            //// penambahan nilai ke CR (c) pada table stok_value
            mapperStokBarang.updateStokValue(nomor, lpenerimaanGudangDetail.get(i).getQty(), item.getId_gudang(), lpenerimaanGudangDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lpenerimaanGudangDetail.get(i).getId_barang());
            
            /// berfungsi untuk mengupdate saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang=thn.format(new Date());
            if(Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)  ){
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lpenerimaanGudangDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(),lpenerimaanGudangDetail.get(i).getId_barang(), stokakhir.getDb13()-stokakhir.getCr13(),0.00);
            }

            //update PERSEDIAAAN ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            qty = itemdetail.getQty();
            while (qty > 0) {
                // Ambil barang dari no IT asal dulu jika kurang baru dari sumber lain
                StokBarang stokBarang = mapperStokBarang.selectBackITPersediaan(item.getId_gudang(), itemdetail.getId_barang(), item.getNo_penerimaan());
                if (stokBarang == null) {
                    stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang(), itemdetail.getId_barang());
                }
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan tidak cukup !!!");
                } else {
                    if (qty >= stokBarang.getPersediaan()) {

                        qty = qty - stokBarang.getPersediaan();
                        j++;
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        mapperStokBarang.updatePersediaan(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        j++;
                        stokBarang.setPersediaan(qty * -1);
                        mapperStokBarang.updatePersediaan(stokBarang);
                        qty = qty - tmpqty;
                    }

                }
            }
            
        }
        
    }
    
    @Transactional(readOnly = true)
    public List<PenerimaanGudangDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllPenerimaanGudang(id_barang, awal, akhir, gudang, status);
    }

    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }
}
