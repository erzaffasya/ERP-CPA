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
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.entity.SupplierKontak;
import net.sra.prime.ultima.service.ServiceSupplier;
import net.sra.prime.ultima.view.input.AccountAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerSupplier implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    private Supplier item;
    private List<Supplier> lSupplier = new ArrayList<>();
    private List<SupplierKontak> lSupplierKontak;

    @Autowired
    ServiceSupplier serviceSupplier;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Supplier();
    }

    public void initItem() {
        item = new Supplier();
        this.nomorurut();
        accountAutoComplete.setAccount(new Account());
        accountAutoComplete.setBatas_bawah(20101000);
        accountAutoComplete.setBatas_atas(20102000);
        lSupplierKontak = serviceSupplier.selectAllKontak(item.getId());
        item.setIsaktif(Boolean.TRUE);
        //item.setSupplier("tes");
    }

    public void onLoadList() {
        try {
            lSupplier = serviceSupplier.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void nomorurut() {
        String noMax = serviceSupplier.noMax();
        if (noMax == null) {
            item.setId("010001");
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%04d", nomor);
            item.setId("01" + noMax);
        }
    }

    public List<Supplier> getDataSupplier() {
        return lSupplier;
    }

    public List<SupplierKontak> getDataSupplierKontak() {
        return lSupplierKontak;
    }

    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSupplier.delete(id);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            // e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setUser_input(page.getMyPegawai().getId_pegawai());
            serviceSupplier.tambah(item, lSupplierKontak);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        }else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pilih dulu Supplier yang akan diedit !!!", ""));
        }
    }

    public void onAccountSelect() {
        item.setAccount_hutang(accountAutoComplete.getAccount().getId_account());
    }

    public void onLoad() {
        item = serviceSupplier.onLoad(item.getId());
        accountAutoComplete.setBatas_bawah(20101000);
        accountAutoComplete.setBatas_atas(20102000);
        accountAutoComplete.setAccount(serviceSupplier.accoutHpp(item.getAccount_hutang()));
        lSupplierKontak = serviceSupplier.selectAllKontak(item.getId());
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            item.setUser_update(page.getMyPegawai().getId_pegawai());
            serviceSupplier.ubah(item, lSupplierKontak);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void extendKontak() {
        lSupplierKontak.add(new SupplierKontak());
    }

    public void onDeleteKontakClicked(SupplierKontak hapus) {
        lSupplierKontak.remove(hapus);
    }

}
