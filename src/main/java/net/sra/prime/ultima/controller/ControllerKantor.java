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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Region;
import net.sra.prime.ultima.service.ServiceKantor;
import net.sra.prime.ultima.view.input.AccAutoComplete;
import net.sra.prime.ultima.view.input.AccountAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKantor implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceKantor referensi;
    private InternalKantorCabang item;
    private List<InternalKantorCabang> lInternalKantorCabang = new ArrayList<>();

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private AccAutoComplete accAutoComplete;

    @PostConstruct
    public void init() {
        item = new InternalKantorCabang();

    }

    public void initItem() {
        Account account = new Account();
        accountAutoComplete.setAccount(account);
        accountAutoComplete.setBatas_bawah(40100000);
        accountAutoComplete.setBatas_atas(40200000);
        accAutoComplete.setAccount(account);
        accAutoComplete.setBatas_bawah(50100000);
        accAutoComplete.setBatas_atas(50200000);
        item.setAktif(Boolean.TRUE);
    }

    public void onLoadList() {
        try {
            lInternalKantorCabang = referensi.onLoadList();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<InternalKantorCabang> getDataInternalKantorCabang() {
        return lInternalKantorCabang;
    }

    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            referensi.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            referensi.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_kantor_cabang());
    }

    public void onAccountPenjualanSelect() {
        item.setAccount_penjualan(accountAutoComplete.getAccount().getId_account());
    }

    public void onAccountHppSelect() {
        item.setAccount_hpp(accAutoComplete.getAccount().getId_account());
    }

    public void onLoad() {
        accAutoComplete.setAccount(null);
        accountAutoComplete.setAccount(null);
        String id = item.getId_lama();
        item = referensi.selectOne(item.getId_lama());
        item.setId_lama(id);
        Account account_penjualan = new Account();
        account_penjualan = referensi.selectOneAccount(item.getAccount_penjualan());
        accountAutoComplete.setBatas_bawah(40100000);
        accountAutoComplete.setBatas_atas(40200000);
        if (account_penjualan != null) {
            accountAutoComplete.setAccount(account_penjualan);
        }
        Account account_hpp = new Account();
        account_hpp = referensi.selectOneAccount(item.getAccount_hpp());
        if (account_hpp != null) {
            accAutoComplete.setAccount(account_hpp);
        }
        accAutoComplete.setBatas_bawah(50100000);
        accAutoComplete.setBatas_atas(50200000);

    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            referensi.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            //initItem();
            // context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    
    }
    
    public Map<String, String> getComboRegion() {
        List<Region> list = referensi.selectRegion();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Region", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getRegion_name(), data.getId_region());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
