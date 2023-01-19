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
import net.sra.prime.ultima.db.mapper.MapperForecast;
import net.sra.prime.ultima.db.mapper.MapperPesanCancel;
import net.sra.prime.ultima.db.mapper.MapperRegion;
import net.sra.prime.ultima.entity.Forecast;
import net.sra.prime.ultima.entity.ForecastDetail;
import net.sra.prime.ultima.entity.PesanCancel;
import net.sra.prime.ultima.entity.Region;
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
public class ServiceForecast {

    @Autowired
    MapperForecast referensi;

    @Autowired
    MapperRegion mapper;
    
    @Autowired
    MapperPesanCancel mapperPesanCancel;

    @Transactional(readOnly = true)
    public String noMax(String idregion) {
        return referensi.SelectMax(idregion);
    }

    @Transactional(readOnly = true)
    public Region GetRegion(String idregion) {
        return mapper.selectOne(idregion);
    }

    @Transactional(readOnly = true)
    public List<Forecast> onLoadList(Date awal, Date akhir, String idregion) {
        return referensi.selectAll(awal, akhir, idregion);
    }
    
    @Transactional(readOnly = true)
    public List<Forecast> onLoadListForIP(Date awal, Date akhir, Character statusip) {
        return referensi.selectAllForIP(awal, akhir,statusip);
    }


     @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Forecast item, List<ForecastDetail> lForecastDetail) {
        referensi.insert(item);
        this.tambahdetail(lForecastDetail, item.getId());

    }
 
    
    public void tambahdetail(List<ForecastDetail> lForecastDetail,Integer id){
        for (int i = 0; i < lForecastDetail.size(); i++) {
            ForecastDetail itemdetail = lForecastDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);
        }
    }
    
    @Transactional(readOnly = false)
    public void ubah(Forecast item, List<ForecastDetail> lForecastDetail) {
        referensi.deleteDetail(item.getId());
        referensi.update(item);
        this.tambahdetail(lForecastDetail, item.getId());
    }

    @Transactional(readOnly = true)
    public Forecast onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<ForecastDetail> onLoadDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }
    
    @Transactional(readOnly = true)
    public List<ForecastDetail> onLoadDetailByOpenPo(Integer id) {
        return referensi.selectAllByOpenPo(id);
    }
    
    
    @Transactional(readOnly = true)
    public List<ForecastDetail> onLoadForecastToPo(Date awal, Date akhir, String idregion, String id_gudang) {
        return referensi.selectForcastToPo(awal, akhir, idregion, id_gudang);
    }
    
    @Transactional(readOnly = false)
    public void ubahStatus(Forecast item, String kode_user) {
        referensi.updateStatus(item.getStatus(),item.getId());
        PesanCancel pesanCancel= new PesanCancel();
        pesanCancel.setNomor(item.getNo_forecast());
        pesanCancel.setPesan(item.getPesan());
        pesanCancel.setKode_user(kode_user);
        mapperPesanCancel.insert(pesanCancel);
//        if(selectCountPl(item.getNo_pl()) == 0){
//            mapperPackinglist.updateStatus('S', item.getNo_pl());
//        }
    }
    
    @Transactional(readOnly = true)
    public Integer countIP(Integer id){
        return referensi.countIP(id);
    }
    
    @Transactional(readOnly = true)
    public List<ForecastDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return referensi.selectAllReport(id_barang, awal, akhir, gudang, status);
    }
    
    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }
}
