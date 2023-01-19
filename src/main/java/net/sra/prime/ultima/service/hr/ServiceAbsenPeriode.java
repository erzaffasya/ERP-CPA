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

import java.text.ParseException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenPeriode;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
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
public class ServiceAbsenPeriode {

    @Autowired
    MapperAbsenPeriode referensi;

    
    @Transactional(readOnly = true)
    public List<AbsenPeriode> onLoadList(String year) {
        return referensi.selectAll(year);
    }

    @Transactional(readOnly = false)
    public void delete(String year) {
        referensi.delete(year);
    }

    

    @Transactional(readOnly = false)
    public void tambah(List<AbsenPeriode> lAbsenPeriode, String year) throws ParseException {
        referensi.delete(year);
        for(int i=0;i<lAbsenPeriode.size();i++){
            referensi.insert(lAbsenPeriode.get(i));
        }
    }

    @Transactional(readOnly = false)
    public void ubah(AbsenPeriode item) {
        referensi.update(item);
    }

    

    @Transactional(readOnly = true)
    public AbsenPeriode onLoad(String year, Integer month) {
        return referensi.selectOne(year, month);
    }

    

}
