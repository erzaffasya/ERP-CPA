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
import net.sra.prime.ultima.db.mapper.hr.MapperTunjangan;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Tunjangan;
import net.sra.prime.ultima.entity.hr.TunjanganJabatan;
import net.sra.prime.ultima.entity.hr.TunjanganMaster;
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
public class ServiceTunjangan {

    @Autowired
    MapperTunjangan referensi;
    
    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Transactional(readOnly = true)
    public List<Tunjangan> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        Integer jumlahTunjangan = referensi.jumlahTunjangan(id);
        Integer master =referensi.selectOne(id).getMaster();
        referensi.deleteTunjanganJabatan(id);
        referensi.delete(id);
        if(jumlahTunjangan <= 1){
            referensi.deleteTunjanganMaster(master);
        }
    }

    @Transactional(readOnly = false)
    public void tambah(Tunjangan item,List<MasterJabatan> lMasterJabatans) {
        if(item.getStatus()){
            TunjanganMaster tunjanganMaster = new TunjanganMaster();
            tunjanganMaster.setNama(item.getNama());
            referensi.insertTunjanganMaster(tunjanganMaster);
            item.setNama("Standar Perusahaan");
            item.setMaster(tunjanganMaster.getId());
        }
        referensi.insert(item);
        tambahTunjanganJabatan(lMasterJabatans, item.getId());
    }
    
    public void tambahTunjanganJabatan(List<MasterJabatan> lMasterJabatans, Integer id) {
        TunjanganJabatan tunjanganJabatan = new TunjanganJabatan();
        for (int i = 0; i < lMasterJabatans.size(); i++) {
            tunjanganJabatan.setId_tunjangan(id);
            tunjanganJabatan.setId_jabatan(lMasterJabatans.get(i).getId_jabatan());
            referensi.insertTunjanganJabatan(tunjanganJabatan);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Tunjangan item,List<MasterJabatan> lMasterJabatans) {
        referensi.deleteTunjanganJabatan(item.getId());
        referensi.update(item);
        tambahTunjanganJabatan(lMasterJabatans, item.getId());
    }

    @Transactional(readOnly = true)
    public Tunjangan onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<TunjanganJabatan> onLoadTunjanganJabatan(Integer id) {
        return referensi.selectAllTunjanganJabatan(id);
    }
    
    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadJabatan(Integer id) {
        return referensi.selectAllJabatan(id);
    }
    
    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadAllJabatan(Integer id) {
        return referensi.selectAllTblJabatan(id);
    }
    
    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadListJabatan() {
        return mapperHrJabatan.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<TunjanganMaster> onLoadListTunjanganMaster() {
        return referensi.selectAllTunjanganMaster();
    }

   
}
