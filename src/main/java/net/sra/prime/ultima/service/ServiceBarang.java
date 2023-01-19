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
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.BarangMinMax;
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
public class ServiceBarang {

    @Autowired
    MapperBarang referensi;

    @Transactional(readOnly = true)
    public List<Barang> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Barang item) {
        referensi.insert(item);
    }

    @Transactional(readOnly = false)
    public void ubah(Barang item) {
        referensi.updateBarang(item);
    }

    @Transactional(readOnly = true)
    public Barang onLoad(String id) {
        return referensi.selectOneperBarang(id);
    }
    
    @Transactional(readOnly = true)
    public Barang selectOne(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void tambahMapping(String id_barang, String id_barang_to) {
            referensi.insertMapping(id_barang, id_barang_to);
    }
   
    @Transactional(readOnly = false)
    public void deleteMapping(String id, String id_barang_to) {
        referensi.deleteMapping(id,id_barang_to);
    }
    
    @Transactional(readOnly = true)
    public List<Barang> onLoadListMapping(String id) {
        return referensi.selectAllMapping(id);
    }
    
    @Transactional(readOnly = true)
    public List<Barang> onLoadListMappingArray(List<String> id) {
        return referensi.selectAllMappingArray(id);
    }
    
    @Transactional(readOnly = false)
    public void tambahMinMax(List<BarangMinMax> listnya, String id_gudang) {
        BarangMinMax item = new BarangMinMax();
        referensi.deleteMinMax(id_gudang);
        for (int i = 0; i < listnya.size(); i++) {
          if(listnya.get(i).getMin() > 0 || listnya.get(i).getMax() > 0){
              item = listnya.get(i);
              item.setId_gudang(id_gudang);
              referensi.insertMinMax(item);
          }  
            
        }
    }
    
    @Transactional(readOnly = false)
    public void tambahMinMaxPerBarang(BarangMinMax item, String id_gudang) {
              referensi.deleteMinMaxPerBarang(id_gudang,item.getId_barang());
              item.setId_gudang(id_gudang);
              referensi.insertMinMax(item);
    }
    
    
    @Transactional(readOnly = true)
    public List<BarangMinMax> onLoadListMinMax(String id_gudang) {
        return referensi.selectAllMinMAx(id_gudang);
    }
    @Transactional(readOnly = true)
    public String getGudang(String id_gudang) {
        return referensi.getGudang(id_gudang);
    }
    
}
