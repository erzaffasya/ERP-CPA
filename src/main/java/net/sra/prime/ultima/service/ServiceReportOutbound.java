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
import net.sra.prime.ultima.db.mapper.MapperOutbound;
import net.sra.prime.ultima.entity.Outbound;
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
public class ServiceReportOutbound {

    @Autowired
    MapperOutbound referensi;
    
   
    
    @Transactional(readOnly = true)
    public List<Outbound> onLoadList(Date awal, Date akhhir, String id_gudang,String id_barang) {
        return referensi.selectAll(awal, akhhir, id_gudang,id_barang);
       
    }
    
    @Transactional(readOnly = true)
    public List<Outbound> onLoadListXls(Date awal, Date akhhir, String id_gudang,String id_barang) {
        return referensi.selectAllXls(awal, akhhir, id_gudang,id_barang);
       
    }
    
    @Transactional(readOnly = true)
    public List<Outbound> onLoadListIn(Date awal, Date akhhir, String id_gudang,String id_barang) {
        return referensi.selectAllIn(awal, akhhir, id_gudang,id_barang);
       
    }
    
    
    

}
