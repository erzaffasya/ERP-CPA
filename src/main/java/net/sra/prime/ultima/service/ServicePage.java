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
import net.sra.prime.ultima.db.mapper.MapperInternalPerusahaan;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPengguna;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalPerusahaan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.entity.hr.SettingApproval;
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
public class ServicePage {
    
    
    @Autowired
    MapperPengguna mapperPengguna;
    
    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperReferensi mapperReferensi;
    
    @Autowired
    MapperInternalPerusahaan mapperInternalPerusahaan;
    
    
    @Transactional(readOnly = true)
    public Pengguna selectOnePengguna(String name) {
        return mapperPengguna.selectOnePengguna(name);
    }

    @Transactional(readOnly = true)
    public Pegawai MyPegawai(String idPegawai) {
        return mapperPegawai.selectOne(idPegawai);
    }
    
    @Transactional(readOnly = true)
    public Gudang MyGudang(String name) {
        return mapperReferensi.selectUserGudang(name);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang MyKantor(String name) {
        return mapperInternalPerusahaan.getUserKantorCabang(name);
    }
    
    @Transactional(readOnly = true)
    public InternalPerusahaan MyPerusahaan(String name) {
        return mapperInternalPerusahaan.getOnePerusahaan(name);
    }
    
    @Transactional(readOnly = true)
    public InternalPerusahaan getPerusahaan(String idKantor) {
        return mapperInternalPerusahaan.getPerusahaan(idKantor);
    }
    
    @Transactional(readOnly = true)
    public List<InternalPerusahaan> getAllPerusahaan() {
        return mapperInternalPerusahaan.getAllPerusahaan();
    }
    
    @Transactional(readOnly = true)
    public List<InternalKantorCabang> getKantorCabang(String idPerusahaan) {
        return mapperInternalPerusahaan.getKantorCabang(idPerusahaan);
    }
    
    @Transactional(readOnly = true)
    public List<Gudang> getGudang(String idPerusahaan) {
        return mapperInternalPerusahaan.getGudang(idPerusahaan);
    }
    
    @Transactional(readOnly = false)
    public void updateLastLogin(String name) {
        mapperPengguna.updateLastLogin(name);
    }
    
    @Transactional(readOnly = false)
    public void updateNewLogin(String name) {
        mapperPengguna.updateNewLogin(name);
    }
    
    @Transactional(readOnly = true)
    public Character onLoadValueBegawai(String id) {
        return mapperReferensi.selectValueBegawi(id);
    }
    
    @Transactional(readOnly = true)
    public SettingApproval settingApproval(Integer id) {
        return mapperReferensi.settinganApproval(id);
    }
    
    
}
