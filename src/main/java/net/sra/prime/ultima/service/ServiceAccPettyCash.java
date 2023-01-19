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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccKas;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccPettyCash;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.InternalKantorCabang;
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
public class ServiceAccPettyCash {
    
    @Autowired
    MapperAccKas referensi;
    
    @Autowired
    MapperAccGl mapperAccGl;

    @Autowired
    MapperKantor mapperKantor;
    
    @Transactional(readOnly = true)
    public String  noMax(String idKantor) {
        return referensi.SelectMaxPettyCash(idKantor);
    }

    @Transactional(readOnly = true)
    public AccPettyCash selectPC(String idKantor) {
        return referensi.selectPc(idKantor);
    }
    
    @Transactional(readOnly = true)
    public AccPettyCash selectOnePC(String nomor) {
        return referensi.selectOnePettyCash(nomor);
    }
    
    @Transactional(readOnly = true)
    public InternalKantorCabang selectOneKantor(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }
    
    @Transactional(readOnly = true)
    public AccValue selectOneAccValue(String tahun, Integer idAccount) {
        return mapperAccGl.selectOneAccValue(tahun, idAccount);
    }
    
    @Transactional(readOnly = true)
    public List<AccGlDetail> selectKas(Integer account, String Awal) {
        return  mapperAccGl.selectKas(account, Awal);
    }
    
    @Transactional(readOnly = true)
    public List<AccGlDetail> selectKasEdit(Integer account, String awal, String akhir) {
        return  mapperAccGl.selectKasEdit(account, awal, akhir);
    }
    
    @Transactional(readOnly = true)
    public List<AccPettyCash> onLoadList(Date awal, Date akhir, String idKantor, Character jenis) {
        return referensi.selectAllPettyCash(awal, akhir, idKantor, jenis);
    }


    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deletePettyCash(id);
    }

    @Transactional(readOnly = false)
    public void tambah(AccPettyCash item) {
        referensi.insertPettyCash(item);
    }

    
}
