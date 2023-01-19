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
package net.sra.prime.ultima.service.kpi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.kpi.MapperCategory;
import net.sra.prime.ultima.entity.kpi.Category;
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
public class ServiceCategory {

    @Autowired
    MapperCategory referensi;
    
    
    @Transactional(readOnly = true)
    public List<Category> onLoadList() {
        return referensi.selectAll();
    }
    
    
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Category item) {
            referensi.insert(item);
    }
    
    
    @Transactional(readOnly = false)
    public void ubah(Category item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Category onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
   
}
