/*
 * Copyright 2023 JoinFaces.
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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperBarangConsumable;
import net.sra.prime.ultima.entity.BarangConsumable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author erza
 */
@Service
@Setter
@Getter
public class ServiceBarangConsumable {
    @Autowired
    MapperBarangConsumable referensi;

    @Transactional(readOnly = true)
    public List<BarangConsumable> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void ubah(BarangConsumable item) {
        referensi.update(item);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(BarangConsumable item) {
        referensi.insert(item);
    }
    
    
    @Transactional(readOnly = true)
    public BarangConsumable onLoad(Integer id) {
        return referensi.selectOne(id);
    }
}
