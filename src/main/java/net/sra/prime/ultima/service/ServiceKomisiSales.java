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
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPenjualan;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.KomisiSales;
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
public class ServiceKomisiSales {

    @Autowired
    MapperPenjualan referensi;

    @Autowired
    MapperReferensi mapperReferensi;
    
    @Autowired
    MapperPegawai mapperPegawai;

    @Transactional(readOnly = true)
    public List<Gudang> onLoadGudang(String idKantor) {
        return mapperReferensi.selectGudangKantor(idKantor);
    }
    
    @Transactional(readOnly = true)
    public List<KomisiSales> OnLoadList(Date awal, Date akhir, String idSales) {
        return referensi.komisiSales(awal, akhir, idSales);
    }
    
    @Transactional(readOnly = true)
    public List<KomisiSales> OnLoadListHpp(Date awal, Date akhir, String idSales) {
        return referensi.komisiSalesHpp(awal, akhir, idSales);
    }
    
    @Transactional(readOnly = true)
    public List<KomisiSales> OnLoadListDsr(Date awal, Date akhir, String idSales) {
        return referensi.komisiDsr(awal, akhir, idSales);
    }
    

    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketing(){
        return mapperPegawai.selectComboMarketingAja();
    }
    
    
}
