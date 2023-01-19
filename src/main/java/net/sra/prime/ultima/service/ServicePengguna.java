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
import net.sra.prime.ultima.db.mapper.MapperMenu;
import net.sra.prime.ultima.db.mapper.MapperPengguna;
import net.sra.prime.ultima.entity.HakAkses;
import net.sra.prime.ultima.entity.Pengguna;
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
public class ServicePengguna {

    @Autowired
    MapperPengguna referensi;
    
    @Autowired
    MapperMenu mapperMenu;
    
    @Transactional(readOnly = true)
    public List<Pengguna> onLoadList(Character status) {
        return referensi.selectAllPengguna(status);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        mapperMenu.deleteHakAkses(id);
        referensi.delete(id);
        
    }

    @Transactional(readOnly = false)
    public void tambah(Pengguna item) throws IOException {
        referensi.insertPengguna(item);
        HakAkses hakAkses = new HakAkses();
        
        hakAkses.setUsernamenya(item.getUsernamenya());
        hakAkses.setId_menu(1);
        mapperMenu.insertHakAkses(hakAkses);
        hakAkses.setId_menu(2);
        mapperMenu.insertHakAkses(hakAkses);
    }

    @Transactional(readOnly = false)
    public void ubah(Pengguna item) {
        referensi.update(item);
       
    }

    @Transactional(readOnly = true)
    public Pengguna onLoad(String id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public Pengguna CheckPassword(String id, String pass) {
        return referensi.CheckPassword(id, pass);
    }
    
    @Transactional(readOnly = true)
    public Pengguna CheckPin(String id, String pin) {
        return referensi.CheckPin(id, pin);
    }
    
    @Transactional(readOnly = false)
    public void updatePasswordPengguna(String id, String pass) {
        referensi.updatePasswordPengguna(id, pass);
       
    }
    
    @Transactional(readOnly = false)
    public void updatePinPengguna(String id, String pin) {
        referensi.updatePinPengguna(id, pin);
       
    }

   

}
