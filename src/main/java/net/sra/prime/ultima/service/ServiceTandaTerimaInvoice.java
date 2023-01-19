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
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperTandaTerimaInvoice;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.TandaTerimaInvoice;
import net.sra.prime.ultima.entity.TandaTerimaInvoiceDetail;
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
public class ServiceTandaTerimaInvoice {

    @Autowired
    MapperTandaTerimaInvoice referensi;

    @Autowired
    MapperCustomer mapperCustomer;
    
//    @Transactional(readOnly = true)
//    public String noMax(String idGudang, Integer bulan, Integer tahun) {
//        return referensi.SelectMax(idGudang, bulan, tahun);
//    }

    @Transactional(readOnly = true)
    public List<TandaTerimaInvoice> onLoadList(Date awal, Date akhir,  Character status) {
        return referensi.selectAll(awal, akhir, status);
    }

    

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(TandaTerimaInvoice item, List<TandaTerimaInvoiceDetail> lTandaTerimaInvoiceDetail) {
        referensi.insert(item);
        this.tambahdetail(lTandaTerimaInvoiceDetail, item.getId());
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<TandaTerimaInvoiceDetail> lTandaTerimaInvoiceDetail, Integer id) {
        for (int i = 0; i < lTandaTerimaInvoiceDetail.size(); i++) {
            TandaTerimaInvoiceDetail itemdetail = lTandaTerimaInvoiceDetail.get(i);
            itemdetail.setUrut(i + 1);
            itemdetail.setId(id);
            referensi.insertDetail(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(TandaTerimaInvoice item, List<TandaTerimaInvoiceDetail> lTandaTerimaInvoiceDetail) {
        referensi.update(item);
        referensi.deleteDetail(item.getId());
        tambahdetail(lTandaTerimaInvoiceDetail, item.getId());
        
    }

    @Transactional(readOnly = true)
    public TandaTerimaInvoice onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<TandaTerimaInvoiceDetail> onLoadDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }

    @Transactional(readOnly = true)
    public Customer selectOneCustomer(String id) {
        return mapperCustomer.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public String noMax(Integer tahun) {
        return referensi.SelectMax(tahun);
    }
    
    @Transactional(readOnly = true)
    public List<TandaTerimaInvoiceDetail> onLoadListDetail(Date awal, Date akhir,  Character status) {
        return referensi.selectAllDetailReport(awal, akhir, status);
    }
}
