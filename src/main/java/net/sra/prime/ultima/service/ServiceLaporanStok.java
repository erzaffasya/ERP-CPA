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
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.StokBarang;
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
public class ServiceLaporanStok {

    @Autowired
    MapperStokBarang referensi;
    
    @Transactional(readOnly = true)
    public List<StokBarang> selectStok(String idGudang, String id_satuan, String id_kategori) {
        return referensi.selectStok(idGudang, id_satuan, id_kategori);
    }
    
    @Transactional(readOnly = true)
    public List<StokBarang> selectStokHpp(String idGudang) {
        return referensi.selectStokHpp(idGudang);
    }
    
    @Transactional(readOnly = true)
    public Boolean cekBlind(String id_gudang){
        return referensi.cekBlind(id_gudang);
    }
    
}
