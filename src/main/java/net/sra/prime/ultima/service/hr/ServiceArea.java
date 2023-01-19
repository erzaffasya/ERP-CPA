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
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.hr.MapperArea;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.hr.Area;
import net.sra.prime.ultima.entity.hr.AreaKantor;
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
public class ServiceArea {

    @Autowired
    MapperArea referensi;
    
    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Transactional(readOnly = true)
    public List<Area> onLoadList() {
        return referensi.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<Area> onLoadListLevel1() {
        return referensi.selectAllLevel1();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        Integer jumlahArea = referensi.jumlahArea(id);
        Integer master =referensi.selectOne(id).getParent();
        referensi.deleteAreaKantor(id);
        referensi.delete(id);
        if(jumlahArea <= 1){
            referensi.deleteAreaKantor(master);
        }
    }

    @Transactional(readOnly = false)
    public void tambah(Area item,List<InternalKantorCabang> lInternalKantorCabangs) {
        if(item.getStatus()){
            item.setLevel(1);
            referensi.insert(item);
            Area areaParent = new Area();
            areaParent.setNama(item.getNama());
            areaParent.setKode(item.getKode()+".01");
            areaParent.setLevel(2);
            areaParent.setParent(item.getId());
            referensi.insert(areaParent);
            tambahAreaKantor(lInternalKantorCabangs, areaParent.getId());
        }else {
            item.setLevel(2);
            referensi.insert(item);
            tambahAreaKantor(lInternalKantorCabangs, item.getId());
        }
        
        
    }
    
    public void tambahAreaKantor(List<InternalKantorCabang> lInternalKantorCabangs, Integer id) {
        AreaKantor areaKantor = new AreaKantor();
        for (int i = 0; i < lInternalKantorCabangs.size(); i++) {
            areaKantor.setId_area(id);
            areaKantor.setId_kantor(lInternalKantorCabangs.get(i).getId_kantor_cabang());
            referensi.insertAreaKantor(areaKantor);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Area item,List<InternalKantorCabang> lInternalKantorCabangs) {
        referensi.deleteAreaKantor(item.getId());
        referensi.update(item);
        tambahAreaKantor(lInternalKantorCabangs, item.getId());
    }

    @Transactional(readOnly = true)
    public Area onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
//    @Transactional(readOnly = true)
//    public List<AreaKantor> onLoadAreaKantor(Integer id) {
//        return referensi.selectAllKantor(id);
//    }
    
    
    
    @Transactional(readOnly = true)
    public List<InternalKantorCabang> onLoadListKantor() {
        return referensi.selectAllKantor();
    }
    
    @Transactional(readOnly = true)
    public List<InternalKantorCabang> onLoadKantorArea(Integer id) {
        return referensi.selectKantorArea(id);
    }
    

   
}
