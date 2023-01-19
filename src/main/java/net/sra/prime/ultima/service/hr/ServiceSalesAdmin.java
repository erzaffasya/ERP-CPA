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
package net.sra.prime.ultima.service.hr;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.hr.MapperSalesAdmin;
import net.sra.prime.ultima.entity.Pegawai;
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
public class ServiceSalesAdmin {

    @Autowired
    MapperSalesAdmin referensi;
    
    
    @Transactional(readOnly = false)
    public void tambah(String id_admin,List<Pegawai> list) {
        referensi.delete(id_admin);
        for (int i = 0; i < list.size(); i++) {
            referensi.insert(id_admin, list.get(i).getId_pegawai());
        }
    }

   @Transactional(readOnly = true)
    public List<Pegawai> selectAllPegawai(String id_admin) {
        return referensi.selectAllPegawai(id_admin);
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectAllSales(String id_admin) {
        return referensi.selectAllSales(id_admin);
    }
    

   
}
