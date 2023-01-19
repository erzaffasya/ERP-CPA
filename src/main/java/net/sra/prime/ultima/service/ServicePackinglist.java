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
import net.sra.prime.ultima.db.mapper.MapperPackinglist;
import net.sra.prime.ultima.db.mapper.MapperPackinglistCancel;
import net.sra.prime.ultima.db.mapper.MapperPackinglistDetail;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperSo;
import net.sra.prime.ultima.db.mapper.MapperSoDetail;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.entity.PackinglistDetail;
import net.sra.prime.ultima.entity.Packinglist_cancel;
import net.sra.prime.ultima.entity.So;
import net.sra.prime.ultima.entity.SoDetail;
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
public class ServicePackinglist {

    @Autowired
    MapperPackinglist referensi;

    @Autowired
    MapperPackinglistDetail mapperPackinglistDetail;

    @Autowired
    MapperSoDetail mapperSoDetail;

    @Autowired
    MapperSo mapperSo;

    @Autowired
    MapperBarang mapperBarang;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperPackinglistCancel mapperPackinglistCancel;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public String noMaxCancel(Integer bulan, Integer tahun) {
        return mapperPackinglistCancel.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<Packinglist> onLoadList(Date awal, Date akhir, String id_pegawai, Character statusip) {
        return referensi.selectAll(awal, akhir, id_pegawai, statusip);
    }

    @Transactional(readOnly = false)
    public void delete(String noPackinglist) {
        mapperPackinglistDetail.delete(noPackinglist);
        if (mapperSo.cekPackingistComplete(referensi.selectOne(noPackinglist).getNo_so()) > 0) {
            mapperSo.updateStatus(referensi.selectOne(noPackinglist).getNo_so(), 'P');
        } else {
            mapperSo.updateStatus(referensi.selectOne(noPackinglist).getNo_so(), 'S');
        }
        referensi.delete(noPackinglist);

    }

    @Transactional(readOnly = false)
    public void tambah(Packinglist item, List<PackinglistDetail> lPackinglistDetail) {
        referensi.insert(item);
        this.tambahdetail(lPackinglistDetail, item);
        mapperSo.updateStatus(item.getNo_so(), 'C');
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PackinglistDetail> lPackinglistDetail, Packinglist item) {
        for (int i = 0; i < lPackinglistDetail.size(); i++) {
            PackinglistDetail itemdetail = lPackinglistDetail.get(i);
            itemdetail.setNo_pl(item.getNomor());
            itemdetail.setUrut(i + 1);
            mapperPackinglistDetail.insert(itemdetail);
            if (item.getStatus().equals('S')) {
                mapperSoDetail.update(item.getNo_so(), itemdetail.getQty(), itemdetail.getId_barang());
            }
        }
    }

    @Transactional(readOnly = false)
    public void updatestok(Packinglist item, List<PackinglistDetail> lPackinglistDetail) throws IOException {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);

        Integer j = 1;
        for (int i = 0; i < lPackinglistDetail.size(); i++) {
            PackinglistDetail itemdetail = lPackinglistDetail.get(i);
            Double qty = itemdetail.getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            // satuan besar
            Double stoknya = mapperStokBarang.SumStok(item.getId_gudang(), lPackinglistDetail.get(i).getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }

            // membandingkan dengan satuan besar
            if (stoknya < itemdetail.getQty()) {
                throw new java.lang.RuntimeException("Stok " + itemdetail.getNama_barang() + " tersisa " + stoknya + " " + itemdetail.getSatuan_besar() + " !!! ");
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
            stokValue.setId_barang(lPackinglistDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lPackinglistDetail.get(i).getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan(), item.getId_gudang(), lPackinglistDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lPackinglistDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lPackinglistDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Packinglist item, List<PackinglistDetail> lPackinglistDetail) throws IOException {
        referensi.update(item);
        mapperPackinglistDetail.delete(item.getNomor());
        tambahdetail(lPackinglistDetail, item);
        if (item.getStatus().equals('S')) {
            this.updatestok(item, lPackinglistDetail);
            if (mapperSoDetail.selectSisaPartial(item.getNo_so()) > 0) {
                mapperSo.updateStatus(item.getNo_so(), 'W');
            } else {
                mapperSo.updateStatus(item.getNo_so(), 'P');
            }
        }
    }

    @Transactional(readOnly = false)
    public void maintenance(Packinglist item) throws IOException {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Packinglist onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PackinglistDetail> onLoadDetail(String id) {
        return mapperPackinglistDetail.selectOne(id);
    }

    @Transactional(readOnly = true)
    public So selectOneSo(String id) {
        return mapperSo.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> selectSoDetail(String Id) {
        return mapperSoDetail.selectOne(Id);
    }

    @Transactional(readOnly = true)
    public Gudang selectOneGudang(String Id) {
        return mapperReferensi.selectOneperGudang(Id);
    }

    @Transactional(readOnly = true)
    public List<Packinglist> selectPackinglistCustomer(String Id) {
        return referensi.selectPackinglistCustomer(Id);
    }

    @Transactional(readOnly = false)
    public void cancelPl(Packinglist item, List<PackinglistDetail> packinglistDetail, String kode_user) throws Exception {

        referensi.updateStatus(item.getStatus(), item.getNomor());
        Packinglist_cancel packinglist_cancel = new Packinglist_cancel();
        packinglist_cancel.setNomor(item.getNo_cancel());
        packinglist_cancel.setNo_pl(item.getNomor());
        packinglist_cancel.setPesan(item.getPesan());
        packinglist_cancel.setKode_user(kode_user);
        mapperPackinglistCancel.insert(packinglist_cancel);
        tambahstok(packinglistDetail, item);
        mapperSo.updateStatus(item.getNo_so(), 'S');

    }

    @Transactional(readOnly = false)
    public void tambahstok(List<PackinglistDetail> lPackinglistDetails, Packinglist item) throws Exception {

        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        Date date = new Date();
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(date);
        Integer nomor = Integer.parseInt(bulan);
        Double qty = 0.00;

        for (int i = 0; i < lPackinglistDetails.size(); i++) {
            if (mapperStokBarang.selectForGetHpp(item.getId_gudang(), lPackinglistDetails.get(i).getId_barang()) == null) {
                throw new java.lang.RuntimeException(lPackinglistDetails.get(i).getNama_barang() + " HPP Belum diinput !!! ");
            } else {

                // Berfungsi untuk menambah ke tabel stok_barang
                StokBarang stokBarang = new StokBarang();
                stokBarang.setId_barang(lPackinglistDetails.get(i).getId_barang());
                stokBarang.setId_gudang(item.getId_gudang());
                // Qty * Dengan isi satuan
                stokBarang.setStok(lPackinglistDetails.get(i).getQty() * mapperBarang.selectOne(lPackinglistDetails.get(i).getId_barang()).getIsi_satuan());
                stokBarang.setTanggal(date);
                stokBarang.setHpp(mapperStokBarang.selectForGetHpp(item.getId_gudang(), lPackinglistDetails.get(i).getId_barang()).getHpp());
                stokBarang.setReff(item.getNo_cancel());
                mapperStokBarang.insert(stokBarang);

                //// update tabel so detail diambil dikurang qty, sisa ditambah qty
                mapperSoDetail.update(item.getNo_so(), lPackinglistDetails.get(i).getQty() * -1, lPackinglistDetails.get(i).getId_barang());

                ////// berfungsi untuk menambah ke stok_value
                StokValue stokValue = new StokValue();
                stokValue.setId_gudang(item.getId_gudang());
                stokValue.setId_barang(lPackinglistDetails.get(i).getId_barang());
                stokValue.setYears(tahun);

                // cek apakah id_barang sudah ada di table stok_value
                if (mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetails.get(i).getId_barang(), tahun) == null) {
                    // untuk mengambil saldo akhir tahun sebelumnya
                    StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetails.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
                mapperStokBarang.updateStokValue(nomor, lPackinglistDetails.get(i).getQty() * mapperBarang.selectOne(lPackinglistDetails.get(i).getId_barang()).getIsi_satuan(), item.getId_gudang(), lPackinglistDetails.get(i).getId_barang(), tahun, 'd');
                mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang(), lPackinglistDetails.get(i).getId_barang());

                /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
                String tahun_sekarang = thn.format(new Date());
                if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                    StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang(), lPackinglistDetails.get(i).getId_barang(), tahun);
                    mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang(), lPackinglistDetails.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Double selectSisaSO(String no_so, String id_barang) {
        return mapperSoDetail.selectSisaSO(no_so, id_barang);
    }

    @Transactional(readOnly = true)
    public Packinglist cekSoTerpakai(String no_so) {
        return referensi.cekSOterpakai(no_so);
    }
    
    @Transactional(readOnly = true)
    public List<PackinglistDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllPackinglist(id_barang, awal, akhir, gudang, status);
    }

}
