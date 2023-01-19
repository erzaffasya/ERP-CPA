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
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.db.mapper.MapperInternalOrder;
import net.sra.prime.ultima.db.mapper.MapperInternalTransfer;
import net.sra.prime.ultima.db.mapper.MapperInternalTransferCancel;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalOrder;
import net.sra.prime.ultima.entity.InternalOrderDetail;
import net.sra.prime.ultima.entity.InternalTranferCancel;
import net.sra.prime.ultima.entity.InternalTransfer;
import net.sra.prime.ultima.entity.InternalTransferDetail;
import net.sra.prime.ultima.entity.InternalTransferHpp;
import net.sra.prime.ultima.entity.Pegawai;
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
public class ServiceInternalTransfer {

    @Autowired
    MapperInternalTransfer referensi;

    @Autowired
    MapperInternalOrder mapperInternalOrder;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperReferensi mapperReferensi;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperBarang mapperBarang;

    @Autowired
    MapperStokBarang mapperStokBarang;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperInternalTransferCancel mapperInternalTransferCancel;

    @Transactional(readOnly = true)
    public Pegawai jabatanPegawai(Integer idJabatan) {
        return mapperPegawai.selectJabatanPegawai(idJabatan);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang selectOneKantor(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String idPegawai) {
        return mapperPegawai.selectOne(idPegawai);
    }

    @Transactional(readOnly = true)
    public Gudang selectOneGudang(String idGudang) {
        return mapperReferensi.selectOneperGudang(idGudang);
    }

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<InternalTransfer> onLoadList(Date awal, Date akhir, String id_pegawai_asal, String id_pegawai_tujuan, Character status, Character jenis) {
        return referensi.selectAll(awal, akhir, id_pegawai_asal, id_pegawai_tujuan, status, jenis);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor_it) {
        referensi.deleteDetail(nomor_it);
        mapperInternalOrder.updateStatus(referensi.selectOne(nomor_it).getNomor_io(), 'A');
        referensi.delete(nomor_it);
    }

    @Transactional(readOnly = false)
    public void tambah(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) {
        referensi.insert(item);
        this.tambahdetail(item, lInternalTransferDetail);
        mapperInternalOrder.updateStatus(item.getNomor_io(), 'P');
    }

    @Transactional(readOnly = false)
    public void tambahstok(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal_terima());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal_terima());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lInternalTransferDetail.size(); i++) {
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lInternalTransferDetail.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang_tujuan());
            stokBarang.setTanggal(item.getTanggal_terima());
            stokBarang.setReff(item.getNomor());
            // diambil dari jumlah barang yg diterima
            Double qty = lInternalTransferDetail.get(i).getTerima() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan();
            while (qty > 0) {
                InternalTransferHpp internalTransferHpp = referensi.selectForUpdate(lInternalTransferDetail.get(i).getId_barang(), item.getNomor());
                if (internalTransferHpp == null) {
                    qty = 0.00;
                } else {
                    if (qty >= internalTransferHpp.getQtysisa()) {
                        stokBarang.setStok(internalTransferHpp.getQtysisa());
                        stokBarang.setPersediaan(internalTransferHpp.getQtysisa());
                        stokBarang.setHpp(internalTransferHpp.getHpp());
                        stokBarang.setBatch(internalTransferHpp.getBatch());
                        qty = qty - internalTransferHpp.getQtysisa();
                        internalTransferHpp.setQtysisa(0.00);
                        referensi.updateHpp(internalTransferHpp);
                    } else {
                        stokBarang.setStok(qty);
                        stokBarang.setPersediaan(qty);
                        stokBarang.setHpp(internalTransferHpp.getHpp());
                        stokBarang.setBatch(internalTransferHpp.getBatch());
                        internalTransferHpp.setQtysisa(internalTransferHpp.getQtysisa() - qty);
                        qty = 0.00;
                        referensi.updateHpp(internalTransferHpp);
                    }
                    mapperStokBarang.insert(stokBarang);

                }

            }

            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang_tujuan());
            stokValue.setId_barang(lInternalTransferDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lInternalTransferDetail.get(i).getTerima() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan(), item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun, 'd');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
    }

    @Transactional(readOnly = false)
    public void updatestok(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) throws IOException {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        Integer nomor = Integer.parseInt(bulan);
        Double totalhpp = 0.00;
        Integer j = 1;
        for (int i = 0; i < lInternalTransferDetail.size(); i++) {
            InternalTransferDetail itemdetail = lInternalTransferDetail.get(i);
            Double qty = itemdetail.getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            // satuan besar
            Double stoknya = mapperStokBarang.SumStok(item.getId_gudang_asal(), itemdetail.getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }

            // membandingkan dengan satuan besar
            if (stoknya < itemdetail.getQty()) {
                throw new java.lang.RuntimeException("Stok " + itemdetail.getNama_barang() + " tersisa " + stoknya + " " + itemdetail.getSatuan_besar() + " !!! ");
            } else {
                while (qty > 0) {
                    StokBarang stokBarang = mapperStokBarang.selectForUpdate(item.getId_gudang_asal(), itemdetail.getId_barang());
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
            stokValue.setId_gudang(item.getId_gudang_asal());
            stokValue.setId_barang(lInternalTransferDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lInternalTransferDetail.get(i).getQty() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan(), item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }

            //update PERSEDIAAAN ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            qty = itemdetail.getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            Double hpp = 0.00;
            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang_asal(), itemdetail.getId_barang());
                InternalTransferHpp internalTransferHpp = new InternalTransferHpp();
                internalTransferHpp.setId_barang(itemdetail.getId_barang());
                internalTransferHpp.setNomor(item.getNomor());
                internalTransferHpp.setUrut(j);
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan tidak cukup !!!");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException(itemdetail.getNama_barang() + " HPP Belum diinput !!! Nomor Dokumen : " + stokBarang.getReff());
                } else {
                    if (qty >= stokBarang.getPersediaan()) {

                        qty = qty - stokBarang.getPersediaan();
                        internalTransferHpp.setQtyout(stokBarang.getPersediaan());
                        internalTransferHpp.setQtysisa(stokBarang.getPersediaan());
                        internalTransferHpp.setHpp(stokBarang.getHpp());
                        internalTransferHpp.setBatch(stokBarang.getBatch());
                        referensi.insertHpp(internalTransferHpp);
                        j++;
                        totalhpp = totalhpp + (stokBarang.getHpp() * stokBarang.getPersediaan());
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        mapperStokBarang.updatePersediaan(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        internalTransferHpp.setQtyout(qty);
                        internalTransferHpp.setQtysisa(qty);
                        internalTransferHpp.setHpp(stokBarang.getHpp());
                        internalTransferHpp.setBatch(stokBarang.getBatch());
                        referensi.insertHpp(internalTransferHpp);
                        j++;
                        stokBarang.setPersediaan(qty * -1);
                        mapperStokBarang.updatePersediaan(stokBarang);
                        totalhpp = totalhpp + (stokBarang.getHpp() * qty);
                        qty = qty - tmpqty;
                    }

                }
            }

        }
        Integer gudang_asal = mapperReferensi.selectOneperGudang(item.getId_gudang_asal()).getAccount_persediaan();
        Integer gudang_tujuan = mapperReferensi.selectOneperGudang(item.getId_gudang_tujuan()).getAccount_persediaan();
        if (!gudang_asal.equals(gudang_tujuan)) {
            tambahJurnal(item, totalhpp);
        }
    }

    @Transactional(readOnly = false)
    public void tambahdetail(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) {
        Integer j = 1;
        for (int i = 0; i < lInternalTransferDetail.size(); i++) {
            InternalTransferDetail itemdetail = lInternalTransferDetail.get(i);
            itemdetail.setNomor(item.getNomor());
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);
        }

    }

    @Transactional(readOnly = false)
    public void approveStatus(String nomor, Character status, Date tanggal) {
        referensi.updateApprove(nomor, status, tanggal);
    }

    @Transactional(readOnly = false)
    public void ubah(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) throws IOException {
        if (item.getStatus().equals('R')) {
            referensi.updateReceipt(item);
            this.tambahstok(item, lInternalTransferDetail);
        } else {
            referensi.update(item);
            if (item.getStatus().equals('S')) {
                mapperInternalOrder.updateStatus(item.getNomor_io(), 'C');
                this.updatestok(item, lInternalTransferDetail);
            }
        }
        referensi.deleteDetail(item.getNomor());
        this.tambahdetail(item, lInternalTransferDetail);
    }

    @Transactional(readOnly = false)
    public void maitenance(InternalTransfer item) throws IOException {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public InternalOrder selectOneIO(String id) {
        return mapperInternalOrder.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<InternalOrderDetail> selectIoDetail(String id) {
        return mapperInternalOrder.selectOneDetail(id);
    }

    @Transactional(readOnly = true)
    public InternalTransfer onLoad(String nomor) {
        return referensi.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public List<InternalTransferDetail> onLoadDetail(String nomor) {
        return referensi.selectOneDetail(nomor);
    }

    @Transactional(readOnly = false)
    public void tambahJurnal(InternalTransfer item, Double persediaan) {
        item.setKeterangan("Internal Transfer Nomor " + item.getNomor() + " dari gudang " + item.getGudang_asal() + " ke gudang " + item.getGudang_tujuan());
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
        itemTrans.setJournal_code("JIT");
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setReference(item.getNomor());
        itemTrans.setNote(item.getKeterangan());
        itemTrans.setPosting(true);
        mapperAccGl.insert(itemTrans);

        List<AccGlDetail> lAccGlDetails = new ArrayList<>();
        AccGlDetail itemGl = new AccGlDetail();
        itemGl = new AccGlDetail();
        itemGl.setJournal_code("JIT");
        itemGl.setGl_number(itemTrans.getGl_number());
        itemGl.setLine(1);
        itemGl.setGl_date(item.getTanggal());
        itemGl.setId_account(mapperReferensi.selectOneperGudang(item.getId_gudang_tujuan()).getAccount_persediaan());
        itemGl.setIs_debit(Boolean.TRUE);
        itemGl.setValue(persediaan);
        itemGl.setKeterangan(item.getKeterangan());
        lAccGlDetails.add(itemGl);
        mapperAccGl.insertDetail(itemGl);

        itemGl = new AccGlDetail();
        itemGl.setJournal_code("JIT");
        itemGl.setGl_number(itemTrans.getGl_number());
        itemGl.setLine(2);
        itemGl.setGl_date(item.getTanggal());
        itemGl.setId_account(mapperReferensi.selectOneperGudang(item.getId_gudang_asal()).getAccount_persediaan());
        itemGl.setIs_debit(Boolean.FALSE);
        itemGl.setValue(persediaan);
        itemGl.setKeterangan(item.getKeterangan());
        lAccGlDetails.add(itemGl);
        mapperAccGl.insertDetail(itemGl);
        updateAccValue(item, lAccGlDetails);

    }

    @Transactional(readOnly = false)
    public void tambahJurnalBalik(InternalTransfer item) {
        item.setKeterangan("Cancel Internal Transfer Nomor " + item.getNomor() + " dari gudang " + item.getGudang_asal() + " ke gudang " + item.getGudang_tujuan());
        AccGlTrans itemTrans = new AccGlTrans();
        Date date = new Date();
        String tahun = new SimpleDateFormat("yy").format(date);
        String thn = new SimpleDateFormat("yyyy").format(date);
        String noMax = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax + tahun);
        }
        itemTrans.setJournal_code("JIT");
        itemTrans.setGl_date(date);
        itemTrans.setReference(item.getNo_cancel());
        itemTrans.setNote(item.getKeterangan());
        itemTrans.setPosting(true);
        mapperAccGl.insert(itemTrans);

        AccGlDetail itemGl = new AccGlDetail();
        List<AccGlDetail> lAccGlDetails = mapperAccGl.selectJurnalGl(item.getNomor());
        for (int i = 0; i < lAccGlDetails.size(); i++) {
            itemGl = lAccGlDetails.get(i);
            itemGl.setJournal_code("JIT");
            itemGl.setGl_number(itemTrans.getGl_number());
            itemGl.setGl_date(date);
            itemGl.setKeterangan(item.getKeterangan());
            if (itemGl.getIs_debit()) {
                itemGl.setIs_debit(Boolean.FALSE);
            } else {
                itemGl.setIs_debit(Boolean.TRUE);
            }
            mapperAccGl.insertDetail(itemGl);
            lAccGlDetails.set(i, itemGl);
        }
        updateAccValue(item, lAccGlDetails);

    }

    @Transactional(readOnly = false)
    public void updateAccValue(InternalTransfer item, List<AccGlDetail> lAccGlDetail) {
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
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            } else {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            }
            mapperAccGl.updateSaldoAkhir(tahun, lAccGlDetail.get(i).getId_account());
        }
    }

    @Transactional(readOnly = true)
    public String noMaxCancel(Integer bulan, Integer tahun) {
        return mapperInternalTransferCancel.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = false)
    public void cancelIT(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail, String kode_user) throws Exception {
        InternalTranferCancel internalTranferCancel = new InternalTranferCancel();
        internalTranferCancel.setNomor(item.getNo_cancel());
        internalTranferCancel.setNo_it(item.getNomor());
        internalTranferCancel.setPesan(item.getPesan());
        internalTranferCancel.setKode_user(kode_user);
        mapperInternalTransferCancel.insert(internalTranferCancel);

        // Jika IT Sudah Receipt maka status diubah ke X dan jika baru Send maka status diubah ke C
        if (item.getStatus().equals('R')) {
            updatestokCancel(item, lInternalTransferDetail);
            referensi.updateStatus('X', item.getNomor());
        } else {
            referensi.updateStatus('C', item.getNomor());
        }
        tambahstokCancel(item, lInternalTransferDetail);
        mapperInternalOrder.updateStatus(item.getNomor_io(), 'A');
        tambahJurnalBalik(item);
    }

    @Transactional(readOnly = false)
    public void tambahstokCancel(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        Date date = new Date();
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(date);
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lInternalTransferDetail.size(); i++) {
            List<InternalTransferHpp> linternalTransferHpp = referensi.selectKembalikanStok(lInternalTransferDetail.get(i).getId_barang(), item.getNomor());
            for (int j = 0; j < linternalTransferHpp.size(); j++) {
                StokBarang stokBarang = new StokBarang();
                stokBarang.setId_barang(lInternalTransferDetail.get(i).getId_barang());
                stokBarang.setId_gudang(item.getId_gudang_asal());
                stokBarang.setTanggal(date);
                stokBarang.setReff(item.getNo_cancel());
                Double qty = lInternalTransferDetail.get(i).getQty() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan();
                stokBarang.setStok(linternalTransferHpp.get(j).getQtyout());
                stokBarang.setPersediaan(linternalTransferHpp.get(j).getQtyout());
                stokBarang.setHpp(linternalTransferHpp.get(j).getHpp());
                stokBarang.setBatch(linternalTransferHpp.get(j).getBatch());
                mapperStokBarang.insert(stokBarang);

            }

            //////
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang_asal());
            stokValue.setId_barang(lInternalTransferDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lInternalTransferDetail.get(i).getQty() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan(), item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun, 'd');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang_asal(), lInternalTransferDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }
        }
        //tambahJurnal(item, totalhpp);

    }

    @Transactional(readOnly = false)
    public void updatestokCancel(InternalTransfer item, List<InternalTransferDetail> lInternalTransferDetail) throws IOException {

        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        Date date = new Date();
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(date);
        Integer nomor = Integer.parseInt(bulan);
        Double totalhpp = 0.00;
        Integer j = 1;
        for (int i = 0; i < lInternalTransferDetail.size(); i++) {
            InternalTransferDetail itemdetail = lInternalTransferDetail.get(i);
            /// kurangi barang yang sudah diterima
            Double qty = itemdetail.getTerima() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            // satuan besar
            Double stoknya = mapperStokBarang.SumStok(item.getId_gudang_tujuan(), itemdetail.getId_barang());
            if (stoknya == null) {
                stoknya = 0.00;
            }

            // membandingkan dengan satuan besar
            if (stoknya < itemdetail.getTerima()) {
                throw new java.lang.RuntimeException("Stok " + itemdetail.getNama_barang() + " tersisa " + stoknya + " " + itemdetail.getSatuan_besar() + " !!! ");
            } else {
                while (qty > 0) {
                    // Ambil barang dari no IT asal dulu jika kurang baru dari sumber lain
                    StokBarang stokBarang = mapperStokBarang.selectBackIT(item.getId_gudang_tujuan(), itemdetail.getId_barang(), item.getNomor());
                    if (stokBarang == null) {
                        stokBarang = mapperStokBarang.selectForUpdate(item.getId_gudang_tujuan(), itemdetail.getId_barang());
                    }
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

            ////// berfungsi untuk menambahah stok_value
            StokValue stokValue = new StokValue();
            stokValue.setId_gudang(item.getId_gudang_tujuan());
            stokValue.setId_barang(lInternalTransferDetail.get(i).getId_barang());
            stokValue.setYears(tahun);

            // cek apakah id_barang sudah ada di table stok_value jika tidak maka input stok_value
            if (mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun) == null) {
                // untuk mengambil saldo akhir tahun sebelumnya
                StokValue stoktahunsebelumnya = mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), Integer.toString(Integer.parseInt(tahun) - 1));

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
            mapperStokBarang.updateStokValue(nomor, lInternalTransferDetail.get(i).getTerima() * mapperBarang.selectOne(lInternalTransferDetail.get(i).getId_barang()).getIsi_satuan(), item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun, 'c');
            mapperStokBarang.updateSaldoAkhir(tahun, item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang());

            /// berfungsi untuk mengisi saldo awal jika ada edit data tahun sebelumnya
            String tahun_sekarang = thn.format(new Date());
            if (Integer.parseInt(tahun) + 1 == Integer.parseInt(tahun_sekarang)) {
                StokValue stokakhir = mapperStokBarang.selectOneStokValue(item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), tahun);
                mapperStokBarang.updateSaldoAwal(tahun_sekarang, item.getId_gudang_tujuan(), lInternalTransferDetail.get(i).getId_barang(), stokakhir.getDb13() - stokakhir.getCr13(), 0.00);
            }

            //update PERSEDIAAAN ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            qty = itemdetail.getQty() * mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            Double hpp = 0.00;
            while (qty > 0) {
                // Ambil barang dari no IT asal dulu jika kurang baru dari sumber lain
                StokBarang stokBarang = mapperStokBarang.selectBackITPersediaan(item.getId_gudang_tujuan(), itemdetail.getId_barang(), item.getNomor());
                if (stokBarang == null) {
                    stokBarang = mapperStokBarang.selectForUpdatePersediaan(item.getId_gudang_tujuan(), itemdetail.getId_barang());
                }
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Persediaan tidak cukup !!!");
                } else if (stokBarang.getHpp() == 0) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException(itemdetail.getNama_barang() + " HPP Belum diinput !!! ");
                } else {
                    if (qty >= stokBarang.getPersediaan()) {

                        qty = qty - stokBarang.getPersediaan();
                        j++;
                        totalhpp = totalhpp + (stokBarang.getHpp() * stokBarang.getPersediaan());
                        stokBarang.setPersediaan(-1 * stokBarang.getPersediaan());
                        mapperStokBarang.updatePersediaan(stokBarang);

                    } else {
                        Double tmpqty = stokBarang.getPersediaan();
                        j++;
                        stokBarang.setPersediaan(qty * -1);
                        mapperStokBarang.updatePersediaan(stokBarang);
                        totalhpp = totalhpp + (stokBarang.getHpp() * qty);
                        qty = qty - tmpqty;
                    }

                }
            }

        }

    }

    @Transactional(readOnly = true)
    public List<InternalTransferDetail> onLoadReport(String id_barang, Date awal, Date akhir, String gudang, Character status, Character jenis) {
        if (jenis.equals('S')) {
            return referensi.selectAllInternalTransfer(id_barang, awal, akhir, gudang, status);
        } else {
            return referensi.selectAllInternalTransferReceipt(id_barang, awal, akhir, gudang, status);
        }
    }

    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }
    
    @Transactional(readOnly = true)
    public Double totalLiter(List<InternalTransferDetail> list){
        Double total=0.00;
        for (int i = 0; i < list.size(); i++) {
            total = total + (list.get(i).getQty() * referensi.isiSatuan(list.get(i).getId_barang()));
        }
        return total;
    }

}
