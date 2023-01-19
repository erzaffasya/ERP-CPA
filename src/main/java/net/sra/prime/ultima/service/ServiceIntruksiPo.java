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
import net.sra.prime.ultima.db.mapper.MapperIntruksiPo;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
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
public class ServiceIntruksiPo {

    @Autowired
    MapperIntruksiPo referensi;
    
    @Autowired
    MapperPesanCancel mapperPesanCancel;
    
    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
       return referensi.SelectMax(bulan, tahun);
    }

    

    @Transactional(readOnly = true)
    public List<IntruksiPo> onLoadList(Date awal, Date akhir,Character status, Character jenis) {
        return referensi.selectAll(awal, akhir, status, jenis);
    }


     @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(IntruksiPo item, List<IntruksiPoDetail> lIntruksiPoDetail) {
        referensi.insert(item);
        this.tambahdetail(lIntruksiPoDetail, item.getId());

    }
 
    
    public void tambahdetail(List<IntruksiPoDetail> lIntruksiPoDetail,Integer id){
        for (int i = 0; i < lIntruksiPoDetail.size(); i++) {
            IntruksiPoDetail itemdetail = lIntruksiPoDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            itemdetail.setDiambil(0.00);
            itemdetail.setSisa(itemdetail.getQty());
            referensi.insertDetail(itemdetail);
        }
    }
    
    public void tambahdetailMaintenance(List<IntruksiPoDetail> lIntruksiPoDetail,Integer id){
        for (int i = 0; i < lIntruksiPoDetail.size(); i++) {
            IntruksiPoDetail itemdetail = lIntruksiPoDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(itemdetail.getQty()-itemdetail.getDiambil());
            referensi.insertDetail(itemdetail);
        }
    }
    
    
    
    @Transactional(readOnly = false)
    public void ubah(IntruksiPo item, List<IntruksiPoDetail> lIntruksiPoDetail) {
        referensi.deleteDetail(item.getId());
        referensi.update(item);
        this.tambahdetail(lIntruksiPoDetail, item.getId());
    }

    @Transactional(readOnly = false)
    public void ubahMaintenance(IntruksiPo item, List<IntruksiPoDetail> lIntruksiPoDetail) {
        referensi.deleteDetail(item.getId());
        referensi.update(item);
        this.tambahdetailMaintenance(lIntruksiPoDetail, item.getId());
    }

    
    @Transactional(readOnly = true)
    public IntruksiPo onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<IntruksiPoDetail> onLoadDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }

    @Transactional(readOnly = false)
    public void ubahStatus(IntruksiPo item, String kode_user) {
        referensi.updateStatus(item.getStatus(),item.getId());
        PesanCancel pesanCancel= new PesanCancel();
        pesanCancel.setNomor(item.getNo_intruksi_po());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
//        if(selectCountPl(item.getNo_pl()) == 0){
//            mapperPackinglist.updateStatus('S', item.getNo_pl());
//        }
    }
    
    @Transactional(readOnly = true)
    public List<IntruksiPoDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllReport(id_barang, awal, akhir, gudang, status);
    }
    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }
}
