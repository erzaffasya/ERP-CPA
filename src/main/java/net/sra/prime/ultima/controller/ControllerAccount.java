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
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.AccountTipe;
import net.sra.prime.ultima.service.ServiceAccount;
//import org.primefaces.context.RequestContext;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAccount implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Inject
    private Options options;

    @Autowired
    ServiceAccount serviceAccount;

    private Account item;
    private List<Account> lAccount = new ArrayList<>();
    private String tipe;
    

    @PostConstruct
    public void init() {
        item = new Account();

    }

    public void initItem() {
        item.setAktif(Boolean.TRUE);
        item.setPembayaran(Boolean.FALSE);
        item.setTipe_account(Boolean.FALSE);
    }

    public void onLoadList() {
        try {
            lAccount = serviceAccount.onLoadList(tipe);
            for (int i = 0; i < lAccount.size(); i++) {
                item = lAccount.get(i);
                if (item.getDebit() != null) {
                    if (item.getDebit() < 0) {
                        item.setKredit(item.getDebit() * -1);
                        item.setDebit(null);
                    } else if (item.getDebit() > 0) {
                        item.setKredit(null);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Account> getDataAccount() {
        return lAccount;
    }

    public void delete(Integer id_account) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            serviceAccount.delete(id_account);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_account());
    }

    public void selectAccountFromDialog(Account kategoribarang) {
       // RequestContext.getCurrentInstance().closeDialog(kategoribarang);
    }

    public void onLoad() {
        item = serviceAccount.onLoad(item.getId_account());
        options.setLevel(item.getLevel() - 1);
    }

    public void onLevelSelect() {
        options.setLevel(item.getLevel() - 1);
        if (item.getLevel() == 1) {
            Integer maxNumber;
            maxNumber = serviceAccount.selectMaxIdLevel1(item.getLevel());
            if (maxNumber == null) {
                maxNumber = 0;
            }
            item.setId_account(maxNumber + 10000000);

        }
    }

    public void onParentSelect() {
        Integer maxNumber;
        if (item.getLevel() > 1) {
            maxNumber = serviceAccount.selectMaxId(item.getLevel(), item.getParent());
        } else {
            maxNumber = serviceAccount.selectMaxIdLevel1(item.getLevel());
        }

        if (maxNumber == null) {
            maxNumber = item.getParent();
        }

        if (item.getLevel() == 1) {
            item.setId_account(maxNumber + 10000000);
        } else if (item.getLevel() == 2) {
            item.setId_account(maxNumber + 100000);
        } else if (item.getLevel() == 3) {
            item.setId_account(maxNumber + 1000);
        } else if (item.getLevel() == 4) {
            item.setId_account(maxNumber + 1);
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getLevel() == 4) {
                item.setTipe_account(Boolean.TRUE);
            }
            serviceAccount.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceAccount.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboTipeAccount() {
        List<AccountTipe> list = serviceAccount.selectAllTipe();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Tipe Account", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama_tipe(), Character.toString(data.getKode_tipe()));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
