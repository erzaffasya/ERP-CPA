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

import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPricelist;
import net.sra.prime.ultima.entity.PricelistDetail;
import net.sra.prime.ultima.entity.Pricelist;
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
public class ServicePricelist {

    @Autowired
    MapperPricelist referensi;
    
    @Transactional(readOnly = true)
    public List<Pricelist> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteDetail(id);
        referensi.delete(id);

    }
    
    @Transactional(readOnly = false)
    public void deleteDetail(String id) {
        referensi.deleteDetail(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Pricelist item) throws IOException {
        referensi.insert(item);
    }
    
    public void tambahOneDetail(PricelistDetail itemdetail) throws IOException {
        referensi.insertDetail(itemdetail);
        
    }
    

    

    @Transactional(readOnly = false)
    public void ubah(Pricelist item, List<PricelistDetail> lPricelistDetail) {
        referensi.deleteDetail(item.getId_gudang());
        referensi.update(item);
        
    }
    
    @Transactional(readOnly = true)
    public Pricelist onLoad(String id) {
        return referensi.selectOne(id);
    }
 
    @Transactional(readOnly = true)
    public List<PricelistDetail> onLoadDetail(String id) {
        return referensi.selectAllDetail(id);
    }

 
}
