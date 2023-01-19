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
import net.sra.prime.ultima.db.mapper.hr.MapperHariLibur;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.HariLiburTipe;
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
public class ServiceHariLibur {

    @Autowired
    MapperHariLibur referensi;
    
    
    @Transactional(readOnly = true)
    public List<HariLibur> onLoadList(Integer tahun) {
        return referensi.selectAll(tahun);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(HariLibur item) {
        referensi.insert(item);
    }
    
    
    @Transactional(readOnly = false)
    public void ubah(HariLibur item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public HariLibur onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<HariLiburTipe> onLoadListTipe() {
        return referensi.selectAllTipe();
    }
}
