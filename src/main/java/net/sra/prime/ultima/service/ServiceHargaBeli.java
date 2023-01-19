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
import net.sra.prime.ultima.db.mapper.MapperHargaBeli;
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HargaBeliDetil;
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
public class ServiceHargaBeli {
    
    @Autowired
    MapperHargaBeli referensi;

    
    @Transactional(readOnly = true)
    public List<HargaBeli> onLoadList() {
        return referensi.selectAll();
    }


    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteDetil(id);
        referensi.delete(id);
        
    }

    @Transactional(readOnly = false)
    public void tambah(HargaBeli item, List<HargaBeliDetil> lHargaBeliDetil) throws IOException {
        referensi.insert(item);
        this.tambahdetail(item.getNo_kontrak(), lHargaBeliDetil);
    }

    
    @Transactional(readOnly = false)
    public void tambahdetail(String id, List<HargaBeliDetil> lHargaBeliDetil) {
            for (int i = 0; i < lHargaBeliDetil.size(); i++) {
                HargaBeliDetil itemdetail= lHargaBeliDetil.get(i);
                Integer k = i + 1;
                itemdetail.setNo_kontrak(id);
                itemdetail.setUrut(k);
                itemdetail.setId_barang(lHargaBeliDetil.get(i).getId_barang());
                itemdetail.setHarga(lHargaBeliDetil.get(i).getHarga());
                referensi.insertDetil(itemdetail);

            }
    }
    
    @Transactional(readOnly = false)
    public void ubah(HargaBeli item, List<HargaBeliDetil> lHargaBeliDetil) {
        referensi.deleteDetil(item.getNo_kontrak());
        referensi.update(item);
        this.tambahdetail(item.getNo_kontrak(), lHargaBeliDetil);
    }

    @Transactional(readOnly = true)
    public HargaBeli onLoad(String id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<HargaBeliDetil> selectAllDetil(String id) {
        return referensi.selectAllDetil(id);
    }
    
    @Transactional(readOnly = true)
    public HargaBeliDetil selectOneDetil(Integer id) {
        return referensi.selectOneDetil(id);
    }

    



}
