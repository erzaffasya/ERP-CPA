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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperLeadtime;
import net.sra.prime.ultima.entity.Leadtime;
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
public class ServiceLeadtime {

    @Autowired
    MapperLeadtime referensi;

    @Transactional(readOnly = true)
    public List<Leadtime> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(String origin, String destination) {
        referensi.delete(origin, destination);
    }

    @Transactional(readOnly = false)
    public void tambah(Leadtime item) {
        referensi.insert(item);
    }

    @Transactional(readOnly = false)
    public void ubah(Leadtime item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Leadtime onLoad(String origin, String destination) {
        return referensi.selectOne(origin, destination);
    }

    @Transactional(readOnly = false)
    public void uploadLeadtime (List<Leadtime> lDetil){
        //nolkan semua stok fisik
        Leadtime l = new Leadtime();
        Leadtime leadtime = new Leadtime();
        for (int i = 0; i < lDetil.size(); i++) {
            leadtime = lDetil.get(i);
            l = referensi.selectOne(lDetil.get(i).getOrigin(), lDetil.get(i).getOrigin());
            if(l != null){
                referensi.update(leadtime);
            }else{
                referensi.insert(leadtime);
            }
            
        }
        
    }
    
    
   
}
