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
import net.sra.prime.ultima.db.mapper.hr.MapperLevel;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Golongan;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.LevelJabatan;
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
public class ServiceLevel {

    @Autowired
    MapperLevel referensi;

    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Transactional(readOnly = true)
    public List<Level> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteLevelJabatan(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Level item, List<MasterJabatan> lMasterJabatans) {
        referensi.insert(item);
        tambahLevelJabatan(lMasterJabatans, item.getId());
    }

    public void tambahLevelJabatan(List<MasterJabatan> lMasterJabatans, Integer id) {
        LevelJabatan levelJabatan = new LevelJabatan();
        for (int i = 0; i < lMasterJabatans.size(); i++) {
            levelJabatan.setId_level(id);
            levelJabatan.setId_jabatan(lMasterJabatans.get(i).getId_jabatan());
            referensi.insertLevelJabatan(levelJabatan);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Level item, List<MasterJabatan> lMasterJabatans) {
        referensi.deleteLevelJabatan(item.getId());
        referensi.update(item);
        tambahLevelJabatan(lMasterJabatans, item.getId());
    }

    @Transactional(readOnly = true)
    public Level onLoad(Integer id) {
        return referensi.selectOne(id);
    }

//    @Transactional(readOnly = true)
//    public List<LevelJabatan> onLoadLevelJabatan(Integer id) {
//        return referensi.selectAllKantor(id);
//    }
    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadListJabatan() {
        return referensi.selectAllJabatan();
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadJabatanLevel(Integer id) {
        return referensi.selectJabatanLevel(id);
    }
    
    @Transactional(readOnly = true)
    public List<Golongan> onLoadListGolongan() {
        return referensi.selectAllGolongan();
    }

}
