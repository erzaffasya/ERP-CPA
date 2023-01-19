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
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPenawaran;
import net.sra.prime.ultima.db.mapper.MapperPenawaranDetail;
import net.sra.prime.ultima.db.mapper.MapperPesan;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.Pesan;
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
public class ServicePenawaran {

    @Autowired
    MapperCustomer mapperCustomer;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperPenawaranDetail mapperPenawaranDetail;

    @Autowired
    MapperPenawaran referensi;

    @Autowired
    MapperAccount mapperAccount;

    @Autowired
    MapperPesan mapperPesan;
    
    @Transactional(readOnly = true)
    public Customer selectOneCustomer(String idcustomer) {
        return mapperCustomer.selectOne(idcustomer);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectHeadDepartemen(Integer id_departemen, String id_kantor) {
        return mapperPegawai.selectHeadDepartemenByKantor(id_departemen,id_kantor);
    }
    
    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor,bulan, tahun);
    }
    
    @Transactional(readOnly = true)
    public String noMaxCustomer() {
        return mapperCustomer.SelectMax();
    }

    

    @Transactional(readOnly = true)
    public List<Penawaran> onLoadList(Date awal, Date akhir, String idPegawai, Character statuspenawaran, String id_kantor) {
        return referensi.selectAll(awal,akhir, idPegawai,statuspenawaran, id_kantor);
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> onLoadListMaintenance(Date awal, Date akhir, Character statuspenawaran) {
        return referensi.selectAllMaintenance(awal, akhir, statuspenawaran);
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> onLoadListNewCustomer() {
        return referensi.selectPenawaranNewCustomer();
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> onLoadListNewCustomerBySalesAdmin(String id_admin) {
        return referensi.selectPenawaranNewCustomerBySalesAdmin(id_admin);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor, Integer revisi) {
        mapperPenawaranDetail.delete(nomor,revisi);
        referensi.delete(nomor, revisi);
    }
    
    @Transactional(readOnly = false)
    public void tambah(Penawaran item, List<PenawaranDetail> lPenawaranDetail) {
        referensi.insert(item);
        this.tambahdetail(lPenawaranDetail, item);

    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PenawaranDetail> lPenawaranDetail, Penawaran item) {
        
        for (int i = 0; i < lPenawaranDetail.size(); i++) {
            PenawaranDetail itemdetail = lPenawaranDetail.get(i);
            itemdetail.setNo_penawaran(item.getNomor());
            itemdetail.setRevisi(item.getRevisi());
            itemdetail.setUrut(i + 1);
            itemdetail.setDiambil(0.00);
            itemdetail.setSisa(lPenawaranDetail.get(i).getQty());
            mapperPenawaranDetail.insert(itemdetail);
            
        }
    }

    public Double hargaBarang(String idGudang,String idBarang){
        return referensi.hargaBarang(idGudang, idBarang);
    }
    
    public CustomerKontak selectOneKontak(Integer idKontak,String idCustomer){
        return mapperCustomer.selectOneKontak(idKontak, idCustomer);
    }
    
    
    @Transactional(readOnly = false)
    public void ubah(Penawaran item, List<PenawaranDetail> lPenawaranDetail) {
            referensi.update(item);
            mapperPenawaranDetail.delete(item.getNomor(), item.getRevisi());
            tambahdetail(lPenawaranDetail, item);
    }

    @Transactional(readOnly = false)
    public void kirimPesan(Penawaran item,String username) {
            Pesan pesan = new Pesan();
        //pesan.setId_pesan(item.getNomor());
        pesan.setPesan(item.getPesan());
        pesan.setTgl_kirim(new Date());
        pesan.setDari(username);
        referensi.updateStatus(true, item.getNomor(), item.getRevisi());
        mapperPesan.insert(pesan);
    }

    
    @Transactional(readOnly = true)
    public Penawaran onLoad(String id, Integer revisi) {
        return referensi.selectOne(id,revisi);
    }

    @Transactional(readOnly = true)
    public List<PenawaranDetail> onLoadDetail(String id, Integer revisi) {
        return mapperPenawaranDetail.selectOne(id, revisi);
    }

    @Transactional(readOnly = false)
    public void tambahCustomer(Customer item) {
        mapperCustomer.insert(item);
    }
    
    
    ///////////////// Laporan ///////////
    
    @Transactional(readOnly = true)
    public List<PenawaranDetail> onLoadListPenawaranInvoice(Date awal, Date akhir, String idPegawai,  String id_kantor) {
        return referensi.selectAllPenawaranInvoice(awal,akhir, idPegawai, id_kantor);
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> selectPenawaranCustomer() {
        return referensi.selectPenawaranCustomer();
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> selectPenawaranBySalesAdmin(String id_admin) {
        return referensi.selectPenawaranBySalesAdmin(id_admin);
    }
    
    @Transactional(readOnly = true)
    public List<Penawaran> selectPenawaranSisaCustomer(String id_customer) {
        return referensi.selectPenawaranSisaCustomer(id_customer);
    }
    
    @Transactional(readOnly = true)
    public Penawaran selectOnePenawaran(String id) {
        return referensi.selectOnePenawaran(id);
    }
}
