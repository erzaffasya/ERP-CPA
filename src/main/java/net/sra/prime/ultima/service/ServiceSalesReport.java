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
import net.sra.prime.ultima.entity.PenjualanDetail;
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
public class ServiceSalesReport {

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
    public List<PenjualanDetail> OnLoadList(Date awal, Date akhir, String idKantor, String idGudang) {
        return referensi.selectSalesReport(awal, akhir, idKantor, idGudang);
    }
    
    @Transactional(readOnly = true)
    public List<PenjualanDetail> OnLoadListRekapPenjualan(Date awal, Date akhir, String idSales,Integer idDepartemen) {
        return referensi.selectRekapPenjualan(awal, akhir, idSales,idDepartemen);
    }

    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketing(){
        return mapperPegawai.selectComboMarketingAja();
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> selectComboMarketingDepatemen(Integer id_departemen, String id_kantor){
        return mapperPegawai.selectComboMarketing(id_departemen,id_kantor);
    }
    
    @Transactional(readOnly = true)
    public List<PenjualanDetail> penjualanBarang(String id_customer) {
        return referensi.selectPenjualanBarang(id_customer);
    }
    
}
