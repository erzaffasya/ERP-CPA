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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.AccountTipe;
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
public class ServiceAccount {
    
    
    @Autowired
    MapperAccount referensi;

    @Transactional(readOnly = true)
    public List<Account> onLoadList(String tipe) {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        
        if(tipe != null && !tipe.equals("") ){
            return referensi.selectAllTipeAccount(tipe,thn.format(new Date()));
        }else{
            return referensi.selectAll(thn.format(new Date()));
        }
    }


    @Transactional(readOnly = false)
    public void delete(Integer id_account) {
        referensi.delete(id_account);
    }

    @Transactional(readOnly = false)
    public void tambah(Account item) {
        referensi.insert(item);
    }

    @Transactional(readOnly = false)
    public void ubah(Account item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public Account onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public Integer selectMaxIdLevel1(Integer level) {
        return referensi.selectMaxIdLevel1(level);
    }
    
    @Transactional(readOnly = true)
    public Integer selectMaxId(Integer level, Integer parent) {
        return referensi.selectMaxId(level, parent);
    }
    
    @Transactional(readOnly = true)
    public List<AccountTipe> selectAllTipe() {
        return referensi.selectAllTipe();
    }

    @Transactional(readOnly = true)
    public List<Account> selectByCategory(Integer batas_bawah, Integer batas_atas) {
        return referensi.selectByCategory(batas_bawah,batas_atas);
    }
    
    @Transactional(readOnly = true)
    public List<Account> selectLevel(Integer level) {
        return referensi.selectLevel(level);
    }
    
    @Transactional(readOnly = true)
    public List<Account> selectAllAccount() {
        return referensi.selectAllAccount();
    }
    
    @Transactional(readOnly = true)
    public Account selectOne(Integer id) {
        return referensi.selectOne(id);
    }
    
}
