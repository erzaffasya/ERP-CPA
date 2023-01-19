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
import net.sra.prime.ultima.db.mapper.MapperHargaJual;
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.entity.HargaJualDetil;
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
public class ServiceHargaJual {
    
    @Autowired
    MapperHargaJual referensi;

    
    @Transactional(readOnly = true)
    public List<HargaJual> onLoadList() {
        return referensi.selectAll();
    }


    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteDetil(id);
        referensi.delete(id);
        
    }

    @Transactional(readOnly = false)
    public void tambah(HargaJual item, List<HargaJualDetil> lHargaJualDetil) throws IOException {
        referensi.insert(item);
        this.tambahdetail(item.getNo_kontrak(), lHargaJualDetil);
    }

    
    @Transactional(readOnly = false)
    public void tambahdetail(String id, List<HargaJualDetil> lHargaJualDetil) {
            for (int i = 0; i < lHargaJualDetil.size(); i++) {
                HargaJualDetil itemdetail= lHargaJualDetil.get(i);
                Integer k = i + 1;
                itemdetail.setNo_kontrak(id);
                itemdetail.setUrut(k);
                itemdetail.setId_barang(lHargaJualDetil.get(i).getId_barang());
                itemdetail.setHarga(lHargaJualDetil.get(i).getHarga());
                referensi.insertDetil(itemdetail);

            }
    }
    
    @Transactional(readOnly = false)
    public void ubah(HargaJual item, List<HargaJualDetil> lHargaJualDetil) {
        referensi.deleteDetil(item.getNo_kontrak());
        referensi.update(item);
        this.tambahdetail(item.getNo_kontrak(), lHargaJualDetil);
    }

    @Transactional(readOnly = true)
    public HargaJual onLoad(String id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<HargaJualDetil> selectAllDetil(String id) {
        return referensi.selectAllDetil(id);
    }
    
    @Transactional(readOnly = true)
    public HargaJualDetil selectOneDetil(Integer id) {
        return referensi.selectOneDetil(id);
    }

    



}
