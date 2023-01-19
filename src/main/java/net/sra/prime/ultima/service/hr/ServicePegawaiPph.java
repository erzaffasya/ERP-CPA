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
package net.sra.prime.ultima.service.hr;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPengguna;
import net.sra.prime.ultima.db.mapper.hr.MapperPegawaiPph;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.entity.hr.PegawaiPph;
import net.sra.prime.ultima.entity.hr.PegawaiPphDetail;
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
public class ServicePegawaiPph {

    @Autowired
    MapperPegawaiPph referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperPengguna mapperPengguna;

    
    ///////////////////// Pegawai PPh //////////////
    
    
    @Transactional(readOnly = true)
    public List<PegawaiPphDetail> onLoadListPph(String year, Integer month) {
        PegawaiPph item = referensi.selectOne(year, month);
        if(item == null){
            return referensi.selectAllFromPegawai();
        }else{
            return referensi.selectAllDetail(year, month);
        }
        
    }
    
    @Transactional(readOnly = true)
    public List<PegawaiPph> onLoadList() {
        return referensi.selectAll();
    }
    
    @Transactional(readOnly = true)
    public PegawaiPph onLoad(String year, Integer month) {
        return referensi.selectOne(year, month);
    }

    
    @Transactional(readOnly = false)
    public void delete(String year, Integer month) {
        referensi.delete(year, month);

    }
    
    @Transactional(readOnly = false)
    public void update(PegawaiPph item, List<PegawaiPphDetail> lPegawaiPphDetail) {
        referensi.update(item);
        referensi.deleteDetail(item.getYear(), item.getMonth());
        updateDetail(item, lPegawaiPphDetail);
    }
    
    @Transactional(readOnly = false)
    public void updateDetail(PegawaiPph item, List<PegawaiPphDetail> lPegawaiPphDetail) {
        PegawaiPphDetail detail = new PegawaiPphDetail();
        for (int i=0; i < lPegawaiPphDetail.size();i++){
            detail = lPegawaiPphDetail.get(i);
            detail.setYear(item.getYear());
            detail.setMonth(item.getMonth());
            referensi.insertDetail(detail);
        }
    }
    
    @Transactional(readOnly = true)
    public List<PegawaiPphDetail> onLoadListDetail(String year, Integer month) {
        return referensi.selectAllDetail(year, month);
    }
    
    @Transactional(readOnly = false)
    public void deleteDetailByPegawai(String year, Integer month,String id_pegawai) {
        referensi.deleteDetailByPegawai(year, month, id_pegawai);

    }
    
    @Transactional(readOnly = false)
    public void updateDetailbyPegawai(PegawaiPphDetail pegawaiPphDetail){
        referensi.updateDetail(pegawaiPphDetail);

    }
    
    @Transactional(readOnly = true)
    public Pengguna CheckPin(String usernamenya, String pinnya
    ) {
        return mapperPengguna.CheckPin(usernamenya, pinnya);
    }
    
}
