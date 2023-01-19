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
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.KategoriSupplier;
import net.sra.prime.ultima.service.ServiceSupplier;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKategoriSupplier implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceSupplier serviceSupplier;
    private KategoriSupplier item;
    private List<KategoriSupplier> lKategoriSupplier = new ArrayList<>();

    @PostConstruct
    public void init() {
        initItem();
    }

    private void initItem() {
        item = new KategoriSupplier();
    }

    public void onLoadList() {
        try {
            lKategoriSupplier = serviceSupplier.onLoadListKategori();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<KategoriSupplier> getDataKategoriSupplier() {
        return lKategoriSupplier;
    }

    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        int id_kategori_supplier = Integer.parseInt(id);
        try {
            serviceSupplier.deleteKategori(id_kategori_supplier);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceSupplier.tambahKategori(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            initItem();
            // context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_kategori_supplier());
    }

    public void onLoad() {
        Integer id = item.getId_lama();
        item = serviceSupplier.onLoadKategori(item.getId_lama());
        item.setId_lama(id);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceSupplier.ubahKategori(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            initItem();
            // context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
