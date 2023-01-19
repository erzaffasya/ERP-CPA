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
package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.view.input.AccAutoComplete;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.service.ServiceGudang;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerGudang implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Inject
    private Page page;

    @Inject
    Options options;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private AccAutoComplete accAutoComplete;

    @Autowired
    ServiceGudang serviceGudang;
    
    private Gudang item;
    private List<Gudang> lGudang = new ArrayList<>();

    @PostConstruct
    public void init() {
        //serviceGudang.init();
        item = new Gudang(); //serviceGudang.getItem();
    }

    public void onLoadList() {
        lGudang = serviceGudang.onLoadList();
    }
    
    public void initItem() {
        item = new Gudang();
        accAutoComplete.setAccount(serviceGudang.initAccout());
        accAutoComplete.setBatas_bawah(50101000);
        accAutoComplete.setBatas_atas(50120000);
        accountAutoComplete.setAccount(serviceGudang.initAccout());
        accountAutoComplete.setBatas_bawah(10600000);
        accountAutoComplete.setBatas_atas(10603000);
    }
    
    public void onLoad() {
        String id = item.getId_lama();
        item = serviceGudang.onLoad(item.getId_lama());
        item.setId_lama(id);
        accountAutoComplete.setBatas_bawah(10600000);
        accountAutoComplete.setBatas_atas(10603000);
        accountAutoComplete.setAccount(serviceGudang.accountPersedian(item.getAccount_persediaan()));
        accAutoComplete.setAccount(serviceGudang.accoutHpp(item.getAccount_hpp()));
        accAutoComplete.setBatas_bawah(50100000);
        accAutoComplete.setBatas_atas(50200000);
    }

    public List<Gudang> getDataGudang() {
        return lGudang;
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceGudang.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_gudang());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }
    
    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceGudang.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void delete(String id_gudang) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceGudang.delete(id_gudang);
            initItem();
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal dihapus" + e, ""));
        }
    }

    public void onAccountPersediaanSelect() {
        item.setAccount_persediaan(accountAutoComplete.getAccount().getId_account());
    }

    public void onAccountHppSelect() {
        item.setAccount_hpp(accAutoComplete.getAccount().getId_account());
    }
}
