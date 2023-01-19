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
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.db.mapper.MapperKategoriCustomer;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerAccount;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.KategoriCustomer;
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
public class ServiceCustomer {

    @Autowired
    MapperCustomer referensi;
    
    @Autowired
    MapperKategoriCustomer mapperKategoriCustomer;
    
    @Autowired
    MapperKantor mapperKantor;

    @Transactional(readOnly = true)
    public List<Customer> selectAll() {
        return referensi.selectAll();
    }
    
    @Transactional(readOnly = true)
    public List<Customer> selectAllBySalesAdmin(String id_admin) {
        return referensi.selectAllBySalesAdmin(id_admin);
    }
    
    @Transactional(readOnly = true)
    public List<Customer> selectAll2() {
        return referensi.selectAll2();
    }
    
    @Transactional(readOnly = true)
    public List<Customer> onLoadList(Boolean status) {
        return referensi.selectAllCustomer(status);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteAccount(id);
        referensi.deleteKontak(id);
        referensi.deletePengiriman(id);
        referensi.delete(id);
    }

    public void tambahpengiriman(List<CustomerPengiriman> lCustomerPengiriman, String id) {
        for (int i = 0; i < lCustomerPengiriman.size(); i++) {
            CustomerPengiriman itempengiriman = new CustomerPengiriman();
            itempengiriman.setId_customer(id);
            itempengiriman.setId_pengiriman(i);
            itempengiriman.setAlamat(lCustomerPengiriman.get(i).getAlamat());
            itempengiriman.setUtama(lCustomerPengiriman.get(i).getUtama());
            referensi.insertpengiriman(itempengiriman);
        }

    }

    public void tambahKontak(List<CustomerKontak> lCustomerKontak, String id) {
        for (int i = 0; i < lCustomerKontak.size(); i++) {
            CustomerKontak itemCustomerKontak = new CustomerKontak();
            itemCustomerKontak = new CustomerKontak();
            itemCustomerKontak.setId_customer(id);
            itemCustomerKontak.setId_kontak(i);
            itemCustomerKontak.setKontak(lCustomerKontak.get(i).getKontak());
            itemCustomerKontak.setUtama(lCustomerKontak.get(i).getUtama());
            itemCustomerKontak.setEmail(lCustomerKontak.get(i).getEmail());
            itemCustomerKontak.setHp(lCustomerKontak.get(i).getHp());
            referensi.insertKontak(itemCustomerKontak);
        }

    }

    public void tambahAccount(List<CustomerAccount> lCustomerAccount, String id) {
        for (int i = 0; i < lCustomerAccount.size(); i++) {
            CustomerAccount itemaccount = new CustomerAccount();
            itemaccount.setId_customer(id);
            itemaccount.setId_account(lCustomerAccount.get(i).getId_account());
            itemaccount.setId_kantor(lCustomerAccount.get(i).getId_kantor());
            referensi.insertAccount(itemaccount);
        }

    }

    @Transactional(readOnly = false)
    public void tambah(Customer item, List<CustomerKontak> lCustomerKontak, List<CustomerAccount> lCustomerAccount, List<CustomerPengiriman> lCustomerPengiriman) {
        referensi.insert(item);
        tambahKontak(lCustomerKontak, item.getId_kontak());
        tambahAccount(lCustomerAccount, item.getId_kontak());
        tambahpengiriman(lCustomerPengiriman, item.getId_kontak());
    }

    @Transactional(readOnly = false)
    public void ubah(Customer item, List<CustomerKontak> lCustomerKontak, List<CustomerAccount> lCustomerAccount, List<CustomerPengiriman> lCustomerPengiriman) {
        referensi.updateCustomer(item);
        referensi.deletePengiriman(item.getId_kontak());
        referensi.deleteKontak(item.getId_kontak());
        referensi.deleteAccount(item.getId_kontak());
        tambahKontak(lCustomerKontak, item.getId_kontak());
        tambahAccount(lCustomerAccount, item.getId_kontak());
        tambahpengiriman(lCustomerPengiriman, item.getId_kontak());
    }

    @Transactional(readOnly = true)
    public Customer onLoad(String id) {
        return referensi.selectOneperCustomer(id);
    }

    @Transactional(readOnly = true)
    public Customer selectOne(String id) {
        return referensi.selectOneperCustomer(id);
    }
    
    @Transactional(readOnly = true)
    public Customer selectOneCustomer(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerPengiriman> selectAllpengiriman(String id) {
        return referensi.selectAllpengiriman(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerKontak> selectAllKontak(String id) {
        return referensi.selectAllKontak(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerAccount> selectCustomerAccount(String id) {
        return referensi.selectCustomerAccount(id);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang selectoneKantor(String id) {
        return mapperKantor.selectOne(id);
    }
    
    
    @Transactional(readOnly = false)
    public void tambahKategori(KategoriCustomer item) {
        mapperKategoriCustomer.insert(item);
        
    }
    
    @Transactional(readOnly = true)
    public List<KategoriCustomer> onLoadListKategori() {
        return mapperKategoriCustomer.selectAll();
    }
    
    @Transactional(readOnly = false)
    public void deleteKategori(Integer id) {
        mapperKategoriCustomer.delete(id);
    }
}
