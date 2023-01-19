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
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
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
public class ServiceLaporanJurnal {
    
    @Autowired
    MapperAccGl referensi;

    @Transactional(readOnly = true)
    public List<AccGlTrans> onLoad() {
        return referensi.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadDetail(String id) {
        return referensi.selectOneDetail(id);
    }
    
    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadAllDetail() {
        return referensi.selectAllDetail();
    }

}
