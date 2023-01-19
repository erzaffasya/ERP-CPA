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
import net.sra.prime.ultima.db.mapper.MapperInternalOrder;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalOrder;
import net.sra.prime.ultima.entity.InternalOrderDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import net.sra.prime.ultima.entity.Pegawai;
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
public class ServiceInternalOrder {

    @Autowired
    MapperInternalOrder referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperPesanCancel mapperPesanCancel;
    
    @Autowired
    MapperReferensi mapperReferensi;
    
    
   
    @Transactional(readOnly = true)
    public Pegawai jabatanPegawai(Integer idJabatan) {
        return mapperPegawai.selectJabatanPegawai(idJabatan);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String idPegawai) {
        return mapperPegawai.selectOne(idPegawai);
    }

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor,bulan, tahun);
    }

    @Transactional(readOnly = true)
    public List<InternalOrder> onLoadList(String idKantor, String id_pegawai, Date awal, Date akhir, Character status) {
        return referensi.selectAllIO(idKantor, id_pegawai, awal, akhir, status);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor_io) {
        referensi.deleteDetail(nomor_io);
        referensi.delete(nomor_io);
    }

    @Transactional(readOnly = false)
    public void tambah(InternalOrder item, List<InternalOrderDetail> lInternalOrderDetail) {
        referensi.insert(item);
        this.tambahdetail(item, lInternalOrderDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail (InternalOrder item, List<InternalOrderDetail> lInternalOrderDetail) {
        for (int i = 0; i < lInternalOrderDetail.size(); i++) {
            InternalOrderDetail itemdetail=lInternalOrderDetail.get(i);
            itemdetail.setNomor_io(item.getNomor_io());
            itemdetail.setUrut(i+1);
            referensi.insertDetail(itemdetail);
        }

    }

    @Transactional(readOnly = false)
    public void approveStatus(InternalOrder item) {
        List<InternalOrderDetail> lDetil = referensi.selectOneDetail(item.getNomor_io());
        referensi.updateApprove(item.getNomor_io(), item.getStatus());
        if(item.getId_ip() != null){
           for (int i = 0; i < lDetil.size(); i++) {
              referensi.updateIP(lDetil.get(i).getQty() * lDetil.get(i).getIsi_satuan(), lDetil.get(i).getId_barang(), item.getId_ip());
           } 
        }
    }
    
    @Transactional(readOnly = false)
    public void sendStatus(String nomor_io, Character status) {
        referensi.updateSendBy(nomor_io, status);
    }
    
    @Transactional(readOnly = false)
    public void ubah(InternalOrder item, List<InternalOrderDetail> lInternalOrderDetail) {
        referensi.deleteDetail(item.getNomor_io());
        referensi.update(item);
        if(item.getStatus().equals('S')){
            referensi.updateModifiedBy(item.getNomor_io(), 'S');
        }else if(item.getStatus().equals('F') || item.getStatus().equals('K')){
            referensi.updateSendBy(item.getNomor_io(), item.getStatus());
        }else if(item.getStatus().equals('A') || item.getStatus().equals('R')){
            referensi.updateApprove(item.getNomor_io(), item.getStatus());
        }    
        this.tambahdetail(item, lInternalOrderDetail);
    }

    @Transactional(readOnly = true)
    public InternalOrder onLoad(String id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<InternalOrderDetail> onLoadDetail(String id) {
        return referensi.selectOneDetail(id);
    }
    
    @Transactional(readOnly = false)
    public void ubahStatus(InternalOrder item, String kode_user) {
        referensi.updateStatus(item.getNomor_io(),item.getStatus());
        PesanCancel pesanCancel= new PesanCancel();
        pesanCancel.setNomor(item.getNomor_io());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
        
    }
    
    @Transactional(readOnly = true)
    public Gudang selectOneGudanga(String id) {
        return mapperReferensi.selectOneperGudang(id);
    }
    
    /// Intruksi PO
    
    @Transactional(readOnly = true)
    public List<IntruksiPo> onLoadListIp(String id_pegawai, Date awal, Date akhir) {
        return referensi.selectAllIP(id_pegawai, awal, akhir);
    }

    @Transactional(readOnly = true)
    public IntruksiPo onLoadIp(Integer id) {
        return referensi.selectOneIp(id);
    }
    
    @Transactional(readOnly = true)
    public List<IntruksiPoDetail> onLoadListIpDetil(Integer id) {
        return referensi.selectAllDetailIp(id);
    }
    
    @Transactional(readOnly = true)
    public Integer selectCountHeadDepartemen(Integer id_departemen) {
        return mapperPegawai.selectCountHeadDepartemen(id_departemen);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectDsmGudang(String id_gudang) {
        return mapperPegawai.selectDsmGudang(id_gudang);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectHeadDepartemen(Integer id_departemen) {
        return mapperPegawai.selectHeadDepartemen(id_departemen);
    }
}
