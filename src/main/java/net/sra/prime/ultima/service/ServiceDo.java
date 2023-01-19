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
import net.sra.prime.ultima.db.mapper.MapperDotbl;
import net.sra.prime.ultima.db.mapper.MapperDoDetail;
import net.sra.prime.ultima.db.mapper.MapperPackinglist;
import net.sra.prime.ultima.db.mapper.MapperPackinglistDetail;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.Dotbl;
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.entity.PackinglistDetail;
import net.sra.prime.ultima.entity.PesanCancel;
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
public class ServiceDo {

    @Autowired
    MapperDotbl referensi;
    
    @Autowired
    MapperDoDetail mapperDoDetail;
    
    
    @Autowired
    MapperPackinglist mapperPackinglist;
    
    @Autowired
    MapperPackinglistDetail mapperPackinglistDetail;
   

    @Autowired
    MapperCustomer mapperCustomer;
    
    @Autowired
    MapperPesanCancel mapperPesanCancel;
    
    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
        return referensi.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<Dotbl> onLoadList(String id_pegawai, Character st,Date awal, Date akhir,Character statusdo) {
        return referensi.selectAllDo(id_pegawai, st, awal, akhir, statusdo);
    }
    
    @Transactional(readOnly = true)
    public List<DoDetail> onLoadListXls(String id_pegawai, Date awal, Date akhir, Character status, String id_gudang) {
        return referensi.selectAllDoXls(id_pegawai,  awal, akhir, status,id_gudang);
    }
    
    @Transactional(readOnly = true)
    public List<DoDetail> onLoadListReport(String id_pegawai, Date awal, Date akhir, Character status, String id_gudang,String id_salesman, String id_customer,String id_barang) {
        return referensi.selectAllReport(id_pegawai, awal, akhir, status, id_gudang, id_salesman, id_customer,id_barang);
    }
    
    @Transactional(readOnly = true)
    public List<Dotbl> onLoadListMaintenace(Date awal, Date akhir,Character statusdo) {
        return referensi.selectAllMaintenance(awal, akhir, statusdo);
    }
    
    @Transactional(readOnly = true)
    public List<DoDetail> onLoadListOsDo(Date akhir, String id_kantor) {
        return mapperDoDetail.selectAllOsDo(akhir,id_kantor);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor, String no_pl) {
        mapperDoDetail.delete(nomor);
        referensi.delete(nomor);
        mapperPackinglist.updateStatus('S', no_pl);
    }

    @Transactional(readOnly = false)
    public void tambah(Dotbl item, List<DoDetail> lDoDetail) {
        if(item.getIstransporter()){
            item.setIdtransporter(null);
        }else{
            item.setDriver(referensi.selectPegawaiName(item.getIdtransporter()));
        }
        referensi.insert(item);
        this.tambahdetail(lDoDetail, item);
        mapperPackinglist.updateStatus('P', item.getNo_pl());
    }
    
    

    @Transactional(readOnly = false)
    public void tambahdetail(List<DoDetail> lDoDetail, Dotbl item) {
        for (int i = 0; i < lDoDetail.size(); i++) {
            DoDetail itemdetail = lDoDetail.get(i);
            itemdetail.setNo_do(item.getNomor());
            itemdetail.setUrut(i+1);
            if(itemdetail.getQty_diterima() == null){
                itemdetail.setQty_diterima(0.00);
            }
            mapperDoDetail.insert(itemdetail);
            
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Dotbl item, List<DoDetail> lDoDetail) {
        if(item.getIstransporter()){
            item.setIdtransporter(null);
        }else{
            item.setDriver(referensi.selectPegawaiName(item.getIdtransporter()));
        }
        referensi.update(item);
        mapperDoDetail.delete(item.getNomor());
        tambahdetail(lDoDetail, item);
        if(item.getStatus().equals('S')){
            mapperPackinglist.updateStatus('A', item.getNo_pl());
        }
    }
    
    @Transactional(readOnly = false)
    public void ubahMaintenance(Dotbl item, List<DoDetail> lDoDetail) {
        referensi.update(item);
        mapperDoDetail.delete(item.getNomor());
        tambahdetail(lDoDetail, item);
        
    }
    
    @Transactional(readOnly = false)
    public void ubahStatus(Dotbl item, String kode_user) {
        referensi.updateStatus(item.getStatus(),item.getNomor());
        PesanCancel pesanCancel= new PesanCancel();
        pesanCancel.setNomor(item.getNomor());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
        if(selectCountPl(item.getNo_pl()) == 0){
            mapperPackinglist.updateStatus('S', item.getNo_pl());
        }
    }
    
    @Transactional(readOnly = true)
    public Integer selectCountPl(String id) {
        return referensi.selectCountPl(id);
    }

    @Transactional(readOnly = true)
    public Dotbl onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<DoDetail> onLoadDetail(String id) {
        return mapperDoDetail.selectOne(id);
    }

    @Transactional(readOnly = true)
    public Packinglist selectOnePackinglist(String id) {
        return mapperPackinglist.selectOne(id);
    }

    @Transactional(readOnly = true)
    public CustomerPengiriman selectDefaultpengiriman(String idCustomer) {
        return mapperCustomer.selectDefaultpengiriman(idCustomer);
    }
    
    @Transactional(readOnly = true)
    public List<PackinglistDetail> selectOnePackinglistDetail(String nomor){
        return mapperPackinglistDetail.selectOne(nomor);
    }

    
    @Transactional(readOnly = false)
    public void simpanterima(Dotbl item, List<DoDetail> lDoDetail) {
        referensi.updatereceived(item);
        this.updatedetail(item, lDoDetail);
    }
    
    public void updatedetail(Dotbl item, List<DoDetail> lDoDetail) {
        for (int i = 0; i < lDoDetail.size(); i++) {
            DoDetail itemdetail = lDoDetail.get(i);
            mapperDoDetail.update(itemdetail);
        }
    }
    
    @Transactional(readOnly = true)
    public CustomerPengiriman selectOnePengiriman(String nomor, Integer id_pengiriman){
        return mapperCustomer.selectOnepengiriman(nomor, id_pengiriman);
    }
    
    @Transactional(readOnly = true)
    public List<Dotbl> selectDoCustomer(){
        return referensi.selectDoCustomer();
    }
    
    @Transactional(readOnly = true)
    public String selectPegawaiName(String id_pegawai){
        return referensi.selectPegawaiName(id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public List<String> selectAllOrigin(){
        return referensi.selectAllOrigin();
    }
    
    @Transactional(readOnly = true)
    public List<String> selectAllDestination(String origin){
        return referensi.selectAllDestination(origin);
    }
    
    @Transactional(readOnly = true)
    public Double getTotalLeadtime(String origin,String destination){
        return referensi.getTotalLeadtime(origin, destination);
    }
}
