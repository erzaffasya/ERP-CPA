/*
 * Copyright 2023 JoinFaces.
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
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.KategoriBarangConsumable;
import net.sra.prime.ultima.service.ServiceKategoriBarangConsumable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author erza
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKategoriBarangConsumable implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    ServiceKategoriBarangConsumable serviceKategoriBarangConsumable;
    
    private KategoriBarangConsumable item;
    private List<KategoriBarangConsumable> lKategoriBarangConsumable = new ArrayList<>();

    @PostConstruct
    public void init() {
        item = new KategoriBarangConsumable();

    }

    public void initItem() {
        item = new KategoriBarangConsumable();
    }

    public void onLoadList() {
        try {
            lKategoriBarangConsumable = serviceKategoriBarangConsumable.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<KategoriBarangConsumable> getDataKategoriBarangConsumable() {
        return lKategoriBarangConsumable;
    }

    public void delete(String kode) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceKategoriBarangConsumable.delete(kode);
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
            serviceKategoriBarangConsumable.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getKode());
    }

    public void onLoad() {
        item = serviceKategoriBarangConsumable.onLoad(item.getKode());
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceKategoriBarangConsumable.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
}
