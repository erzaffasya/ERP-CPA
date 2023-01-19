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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperHargaJual;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPenawaran;
import net.sra.prime.ultima.db.mapper.MapperPenawaranDetail;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.db.mapper.MapperSo;
import net.sra.prime.ultima.db.mapper.MapperSoDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.PesanCancel;
import net.sra.prime.ultima.entity.So;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.entity.SoPersetujuan;
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
public class ServiceSo {

    @Autowired
    MapperSo referensi;

    @Autowired
    MapperSoDetail mapperSoDetail;

    @Autowired
    MapperCustomer mapperCustomer;

    @Autowired
    MapperPenawaran mapperPenawaran;

    @Autowired
    MapperPenawaranDetail mapperPenawaranDetail;

    @Autowired
    MapperHargaJual mapperHargaJual;

    @Autowired
    MapperPesanCancel mapperPesanCancel;

    @Autowired
    MapperPegawai mapperPegawai;

    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
        return referensi.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<So> onLoadList(Date awal, Date akhir, String id_pegawai, Character status) {
        return referensi.selectAll(awal, akhir, id_pegawai, status);
    }
    
    @Transactional(readOnly = true)
    public List<So> onLoadListGudang(Date awal, Date akhir, String id_pegawai, Character status) {
        return referensi.selectAllGudang(awal, akhir, id_pegawai, status);
    }
    
    @Transactional(readOnly = true)
    public List<SoDetail> onLoadListDetail(Date awal, Date akhir, String id_pegawai, Character status) {
        return referensi.selectAllDetail(awal, akhir, id_pegawai, status);
    }
    
    @Transactional(readOnly = true)
    public List<So> onLoadListPengajuan(Date awal, Date akhir, String id_pegawai) {
        return referensi.selectAllPengajuan(awal, akhir, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<So> onLoadMonitoringSo(Date awal, Date akhir, String idGudang, Character status) {
        return referensi.MonitoringSo(awal, akhir, idGudang, status);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> onLoadSoReport(Date awal, Date akhir, String idGudang, Character status) {
        return referensi.SoReport(awal, akhir, idGudang, status);
    }

    @Transactional(readOnly = true)
    public List<So> onLoadListConsignment(Date awal, Date akhir, String idKantor) {
        return referensi.selectAllConsignment(awal, akhir, idKantor);
    }
 
    @Transactional(readOnly = false)
    public void delete(String nomor) {
        mapperSoDetail.delete(nomor);
        referensi.delete(nomor);
        referensi.deletePersetujuan(nomor);
    }

    @Transactional(readOnly = false)
    public void tambah(So item, List<SoDetail> lSoDetail) {
        referensi.insert(item);
        this.tambahdetail(item.getNomor(), lSoDetail);
    }

    @Transactional(readOnly = false)
    public void tambahNewCustomer(So item, List<SoDetail> lSoDetail) {
        Customer customer = new Customer();
        customer.setId_kontak(item.getId_customer());
        customer.setCustomer(item.getCustomer());
        customer.setTop(item.getTop());
        mapperCustomer.insert(customer);
        mapperPenawaran.updateCustomer(item.getId_customer(), item.getNo_penawaran(), item.getRevisi_penawaran());
        referensi.insert(item);
        this.tambahdetail(item.getNomor(), lSoDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(String nomor, List<SoDetail> lSoDetail) {
        for (int i = 0; i < lSoDetail.size(); i++) {
            SoDetail itemdetail = lSoDetail.get(i);
            itemdetail.setNo_so(nomor);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(lSoDetail.get(i).getQty());
            itemdetail.setDiambil(0.00);
            if (itemdetail.getAdditional_charge() == null) {
                itemdetail.setAdditional_charge(0.00);
            }
            if (itemdetail.getDiskonrp() == null) {
                itemdetail.setDiskonrp(0.00);
            }
            mapperSoDetail.insert(itemdetail);
        }

    }

    @Transactional(readOnly = false)
    public void tambahdetailMaintenance(String nomor, List<SoDetail> lSoDetail) {
        for (int i = 0; i < lSoDetail.size(); i++) {
            SoDetail itemdetail = lSoDetail.get(i);
            itemdetail.setNo_so(nomor);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(itemdetail.getQty() - itemdetail.getDiambil());

            if (itemdetail.getAdditional_charge() == null) {
                itemdetail.setAdditional_charge(0.00);
            }
            if (itemdetail.getDiskonrp() == null) {
                itemdetail.setDiskonrp(0.00);
            }
            mapperSoDetail.insert(itemdetail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(So item, List<SoDetail> lSoDetail) {
        mapperSoDetail.delete(item.getNomor());
        referensi.update(item);
        if(item.getStatus().equals('S')){
            referensi.updateSend(item);
        }
        this.tambahdetail(item.getNomor(), lSoDetail);
    }

    @Transactional(readOnly = false)
    public void ubahMaintenance(So item, List<SoDetail> lSoDetail) {
        mapperSoDetail.delete(item.getNomor());
        referensi.update(item);
        referensi.updateSalesPackinglist(item.getId_salesman(), item.getNomor());
        referensi.updateSalesDo(item.getId_salesman(), item.getNomor());
        referensi.updateSalesPenjualan(item.getId_salesman(), item.getNomor());
        referensi.updateSalesAr(item.getId_salesman(), item.getNomor());
        this.tambahdetailMaintenance(item.getNomor(), lSoDetail);
    }

    @Transactional(readOnly = true)
    public So onLoad(String nomor) {
        return referensi.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> onLoadDetail(String nomor) {
        return mapperSoDetail.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> onLoadDetailView(String nomor) {
        return mapperSoDetail.selectOneView(nomor);
    }

    @Transactional(readOnly = true)
    public Customer selectOneCustomer(String idCustomer) {
        return mapperCustomer.selectOne(idCustomer);
    }

    @Transactional(readOnly = true)
    public Penawaran selectOnePenawaran(String nomor, Integer revisi) {
        return mapperPenawaran.selectOne(nomor, revisi);
    }

    @Transactional(readOnly = true)
    public List<PenawaranDetail> selectPenawaranDetail(String nomor, Integer revisi) {
        return mapperPenawaranDetail.selectOne(nomor, revisi);
    }

    @Transactional(readOnly = true)
    public HargaJual selectOneHargaJual(String noKOntrak) {
        return mapperHargaJual.selectOne(noKOntrak);
    }

    @Transactional(readOnly = true)
    public List<SoDetail> selectBookingRecord(String id_kantor) {
        return mapperSoDetail.selectBookingRecord(id_kantor);
    }

    @Transactional(readOnly = true)
    public Integer cekPackinglist(String no_so) {
        return referensi.cekPackingist(no_so);
    }

    @Transactional(readOnly = true)
    public Integer cekPenawaran(String no_penawaran) {
        return referensi.cekPenawaran(no_penawaran);
    }

    @Transactional(readOnly = false)
    public void ubahStatus(So item, String kode_user) {
        referensi.updateStatus(item.getNomor(), item.getStatus());
        PesanCancel pesanCancel = new PesanCancel();
        pesanCancel.setNomor(item.getNomor());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
    }

    @Transactional(readOnly = true)
    public List<So> selectSoCustomer() {
        return referensi.selectSoCustomer();
    }
    
    @Transactional(readOnly = true)
    public List<So> selectSoCancel() {
        return referensi.selectSoCancel();
    }

    @Transactional(readOnly = true)
    public List<So> selectSoSisaCustomer() {
        return referensi.selectSoSisaCustomer();
    }

    @Transactional(readOnly = true)
    public Double sisaHutangCustomerbyTop(String customer_code, Date date) {
        return referensi.sisaHutangCustomerbyTop(customer_code, date);
    }
    
    @Transactional(readOnly = true)
    public Double jumlahHutangCustomer(String customer) {
        return referensi.sisaHutangCustomer(customer);
    }

    @Transactional(readOnly = false)
    public void insertPersetujuan(So so, Character tipe) {

        SoPersetujuan soPersetujuan = new SoPersetujuan();
        if (!tipe.equals('L')) {
            soPersetujuan.setJenis('W');
        }else{
            soPersetujuan.setJenis('A');
        }
        soPersetujuan.setTipe(tipe);
        soPersetujuan.setNomor(so.getNomor());
        
        Pegawai marketing = mapperPegawai.selectOneStatusKetenagakerjaan(so.getId_salesman());
        Pegawai pegawai = mapperPegawai.selectHeadDepartemenByKantor(marketing.getId_departemen_new(), marketing.getId_kantor_new());
        soPersetujuan.setId_pegawai(pegawai.getId_pegawai());
        soPersetujuan.setUrut(1);
        referensi.insertPersetujuan(soPersetujuan);

        
        if (!tipe.equals('L')) {
            //Mery
            soPersetujuan.setUrut(2);
            soPersetujuan.setJenis('A');
            soPersetujuan.setId_pegawai("CPA-079");
            referensi.insertPersetujuan(soPersetujuan);
            //Margono
            soPersetujuan.setUrut(3);
            soPersetujuan.setId_pegawai("CPA-073");
            referensi.insertPersetujuan(soPersetujuan);
        }
    }
    
    @Transactional(readOnly = true)
    public SoPersetujuan selectOneSoPersetujuan(String nomor){
        return referensi.selectOneSoPersetujuan(nomor);
    }
    
    @Transactional(readOnly = true)
    public List<SoPersetujuan> selectListSoPersetujuan(String nomor){
        return referensi.selectListPersetujuan(nomor);
    }
    
    @Transactional(readOnly = true)
    public SoPersetujuan selectApproveSoPersetujuan(String nomor){
        return referensi.selectApproveSoPersetujuan(nomor);
    }

    @Transactional(readOnly = true)
    public SoPersetujuan selectSoPersetujuanByPegawai(String nomor,String id_pegawai){
        return referensi.selectOneSoPersetujuanByPegawai(nomor, id_pegawai);
    }

    
    @Transactional(readOnly = true)
    public List<So> selectSoNotifikasi(String id_pegawai) {
        return referensi.selectSoNotifikasi(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updatePersetujuan(Boolean approve, String nomor, String id_pegawi,String note) {
        referensi.updatePersetujuan(approve, nomor, id_pegawi,note);
    }
    
    @Transactional(readOnly = true)
    public Integer checkArCount(String customer_code) {
        return referensi.cekArCount(customer_code);
    }
    
    @Transactional(readOnly = true)
    public List<SoDetail> outstandingSo(Date awal, Date akhir, String idGudang, String id_customer) {
        return referensi.outstandingSo(awal, akhir, idGudang, id_customer);
    }
}
