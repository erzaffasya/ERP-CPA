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
package net.sra.prime.ultima.service.kpi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.kpi.MapperPegawaiKpi;
import net.sra.prime.ultima.entity.kpi.PegawaiKpi;
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
public class ServicePegawaiKpi {

    @Autowired
    MapperPegawaiKpi referensi;
    
    
    @Transactional(readOnly = true)
    public List<PegawaiKpi> onLoadList() {
        return referensi.selectAllPegawai();
    }
    
    @Transactional(readOnly = true)
    public List<PegawaiKpi> onLoadListPegawaiKpi(String id_pegawai) {
        return referensi.selectAllKpiPegawai(id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public List<PegawaiKpi> onLoadListIndicator(String id_pegawai) {
        return referensi.selectAllIndicator(id_pegawai);
    }
    
    
    @Transactional(readOnly = false)
    public void delete(String id_pegawai) {
        referensi.delete(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void tambah(List<PegawaiKpi> lPegawaiKpi, String id_pegawai) {
            PegawaiKpi item = new PegawaiKpi();
            referensi.delete(id_pegawai);
            for (int i=0; i < lPegawaiKpi.size(); i++){
                item = lPegawaiKpi.get(i);
                item.setId_pegawai(id_pegawai);
                referensi.insert(item);
            }    
    }
    
    
    @Transactional(readOnly = false)
    public void ubah(PegawaiKpi item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public PegawaiKpi onLoad(String id_pegawai, String year) {
        return referensi.selectOne(id_pegawai, year);
    }
    
    
    
}
