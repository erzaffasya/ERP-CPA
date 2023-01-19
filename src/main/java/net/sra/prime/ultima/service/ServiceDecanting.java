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
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.db.mapper.MapperDecanting;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Decanting;
import net.sra.prime.ultima.entity.DecantingDetail;
import net.sra.prime.ultima.entity.Gudang;
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
public class ServiceDecanting {

    @Autowired
    MapperDecanting referensi;

    @Autowired
    MapperBarang mapperBarang;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperReferensi mapperReferensi;

    @Transactional(readOnly = true)
    public String noMax(String idGudang, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idGudang, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<Decanting> onLoadList(Date awal, Date akhir, String id_pegawai, Character status) {
        return referensi.selectAll(awal, akhir, id_pegawai, status);
    }

    @Transactional(readOnly = false)
    public void delete(String noDecanting) {
        referensi.deleteDetail(noDecanting);
        referensi.delete(noDecanting);
    }

    @Transactional(readOnly = false)
    public void tambah(Decanting item, List<DecantingDetail> lDecantingDetail, List<DecantingDetail> lDecantingDetailTo) {
        referensi.insert(item);
        this.tambahdetail(item, lDecantingDetail, lDecantingDetailTo);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(Decanting item, List<DecantingDetail> lDecantingDetail, List<DecantingDetail> lDecantingDetailTo) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        for (int i = 0; i < lDecantingDetail.size(); i++) {
            DecantingDetail dd = lDecantingDetail.get(i);
            dd.setAsal('F');
            dd.setNo_decanting(item.getNo_decanting());
            dd.setUrut(i + 1);
            referensi.insertDetail(dd);

        }

        for (int i = 0; i < lDecantingDetailTo.size(); i++) {
            DecantingDetail ddt = lDecantingDetailTo.get(i);
            ddt.setAsal('T');
            ddt.setNo_decanting(item.getNo_decanting());
            ddt.setUrut(i + 1);
            referensi.insertDetail(ddt);

        }

    }

    @Transactional(readOnly = false)
    public void ubah(Decanting item, List<DecantingDetail> lDecantingDetail, List<DecantingDetail> lDecantingDetailTo) throws IOException {
        referensi.deleteDetail(item.getNo_decanting());
        referensi.update(item);
        this.tambahdetail(item, lDecantingDetail, lDecantingDetailTo);

        if (item.getStatus().equals('A')) {
            Double hpp = this.updatestok(item, lDecantingDetail);
            this.tambahstok(item, lDecantingDetailTo, hpp);
        }

    }
    
    @Transactional(readOnly = false)
    public void maintenance(Decanting item) throws IOException {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Decanting onLoad(String noDecanting) {
        return referensi.selectOne(noDecanting);
    }

    @Transactional(readOnly = true)
    public List<DecantingDetail> onLoadDetail(String noDecanting, Character jns) {
        return referensi.selectOneDetail(noDecanting, jns);
    }

    @Transactional(readOnly = false)
    public Double updatestok(Decanting item, List<DecantingDetail> lDecantingDetail) throws IOException {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        Double hpp = 0.00;
        for (int i = 0; i < lDecantingDetail.size(); i++) {
            DecantingDetail itemdetail = lDecantingDetail.get(i);
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
            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lDecantingDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lDecantingDetail.get(i).getQty(), item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lDecantingDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
            
            //update PERSEDIAAAN ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            qty = itemdetail.getQty();

            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang(), itemdetail.getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan tidak cukup !!!");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException(itemdetail.getNama_barang() + " HPP Belum diinput !!! Nomor Dokumen :" + stokBarang.getReff());
                } else {
                    hpp = stokBarang.getHpp();
                    if (qty >= stokBarang.getPersediaan()) {
                        qty = qty - stokBarang.getPersediaan();
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        mapperStokBarang.updatePersediaan(stokBarang);

                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        stokBarang.setPersediaan(qty * -1);
                        mapperStokBarang.updatePersediaan(stokBarang);
                        qty = qty - tmpqty;
                    }

                }
            }

        }
        return hpp;
    }

    @Transactional(readOnly = false)
    public void tambahstok(Decanting item, List<DecantingDetail> lDecantingDetail,Double hpp) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lDecantingDetail.size(); i++) {
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lDecantingDetail.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang());
            stokBarang.setTanggal(item.getTanggal());
            stokBarang.setReff(item.getNo_decanting());
            stokBarang.setStok(lDecantingDetail.get(i).getQty());
            stokBarang.setPersediaan(lDecantingDetail.get(i).getQty());
            stokBarang.setHpp(hpp);
            stokBarang.setBatch(lDecantingDetail.get(i).getBatch());
            mapperStokBarang.insert(stokBarang);

            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang());
            stokValue.setId_barang(lDecantingDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            //// penambahan nilai ke DB (d) pada table stok_value
            mapperStokBarang.updateStokValue(nomor, lDecantingDetail.get(i).getQty(), item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun, 'd');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lDecantingDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lDecantingDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }

    @Transactional(readOnly = true)
    public Gudang selectOneGudang(String id_gudang
    ) {
        return mapperReferensi.selectOneperGudang(id_gudang);
    }
    
    @Transactional(readOnly = true)
    public List<DecantingDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllDecanting(id_barang, awal, akhir, gudang, status);
    }

    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }

}
