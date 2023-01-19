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
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.db.mapper.MapperKategoriSupplier;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.KategoriSupplier;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.entity.SupplierKontak;
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
public class ServiceSupplier {
    
    @Autowired
    MapperSupplier referensi;
    
    @Autowired
    MapperKategoriSupplier mapperKategoriSupplier;

    @Autowired
    MapperAccount mapper;

    @Transactional(readOnly = true)
    public String noMax() {
        return referensi.SelectMax();
    }
    
    @Transactional(readOnly = true)
    public List<SupplierKontak> selectAllKontak(String id) {
        return referensi.selectAllKontak(id);
    }
    
    @Transactional(readOnly = true)
    public List<Supplier> onLoadList() {
        return referensi.selectAll();
    }


    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteKontak(id);
        referensi.delete(id);
        
    }

    @Transactional(readOnly = false)
    public void tambah(Supplier item, List<SupplierKontak> lSupplierKontak) {
        referensi.insert(item);
        this.tambahKontak(item.getId(), lSupplierKontak);
    }

    @Transactional(readOnly = false)
    public void tambahKontak(String id, List<SupplierKontak> lSupplierKontak) {
            for (int i = 0; i < lSupplierKontak.size(); i++){ 
                SupplierKontak itemkontak = lSupplierKontak.get(i);
                itemkontak.setId_supplier(id);
                itemkontak.setUrut(i);
                referensi.insertKontak(itemkontak);
            }
    }
    
    @Transactional(readOnly = false)
    public void ubah(Supplier item, List<SupplierKontak> lSupplierKontak) {
        referensi.updateSupplier(item);
        referensi.deleteKontak(item.getId());
        this.tambahKontak(item.getId(), lSupplierKontak);
    }

    @Transactional(readOnly = true)
    public Supplier onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public Account accountPersedian(Integer id) {
        return mapper.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public Account accoutHpp(Integer id){
        return mapper.selectOne(id);
    }

    public Account initAccout() {
        return new Account();
    }

    @Transactional(readOnly = false)
    public void tambahKategori(KategoriSupplier item) {
        mapperKategoriSupplier.insert(item);
    }
    
    @Transactional(readOnly = true)
    public List<KategoriSupplier> onLoadListKategori() {
        return mapperKategoriSupplier.selectAll();
    }
    
    @Transactional(readOnly = false)
    public void deleteKategori(Integer id) {
        mapperKategoriSupplier.delete(id);
        
    }
    
    @Transactional(readOnly = true)
    public KategoriSupplier onLoadKategori(Integer id) {
        return mapperKategoriSupplier.selectOneperkategorisupplier(id);
    }
    
    @Transactional(readOnly = false)
    public void ubahKategori(KategoriSupplier item) {
        mapperKategoriSupplier.updateKategoriSupplier(item);
        
    }

}
