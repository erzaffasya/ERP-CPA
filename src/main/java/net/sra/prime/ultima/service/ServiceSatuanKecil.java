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
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.SatuanKecil;
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
public class ServiceSatuanKecil {
    
    @Autowired
    MapperReferensi referensi;

    
    @Transactional(readOnly = true)
    public List<SatuanKecil> onLoadList() {
        return referensi.selectAllSatuan();
    }


    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteSatuan(id);
    }

    @Transactional(readOnly = false)
    public void tambah(SatuanKecil item) {
        referensi.insertSatuan(item);
    }

    @Transactional(readOnly = false)
    public void ubah(SatuanKecil item) {
        referensi.updateSatuan(item);
    }

    @Transactional(readOnly = true)
    public SatuanKecil onLoad(String id) {
        return referensi.selectOneperSatuan(id);
    }

    
}
