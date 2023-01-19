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
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.Gudang;
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
public class ServiceGudang {
    
    @Autowired
    MapperReferensi referensi;

    @Autowired
    MapperAccount mapper;

    @Transactional(readOnly = true)
    public List<Gudang> onLoadList() {
        return referensi.selectAllGudang();
    }


    @Transactional(readOnly = false)
    public void delete(String id_gudang) {
        referensi.deleteGudang(id_gudang);
    }

    @Transactional(readOnly = false)
    public void tambah(Gudang item) {
        referensi.insertGudang(item);
    }

    @Transactional(readOnly = false)
    public void ubah(Gudang item) {
        referensi.updateGudang(item);
    }
    
    @Transactional(readOnly = false)
    public void blind(String id_gudang, Boolean blind) {
        referensi.updateBlind(id_gudang, blind);
    }
    
    @Transactional(readOnly = false)
    public void stokopname(String id_gudang, Boolean stokopname) {
        referensi.updateStokopname(id_gudang, stokopname);
    }

    @Transactional(readOnly = true)
    public Gudang onLoad(String id) {
        return referensi.selectOneperGudang(id);
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



}
