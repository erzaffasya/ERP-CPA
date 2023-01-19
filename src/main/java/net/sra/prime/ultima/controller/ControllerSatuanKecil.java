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
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.SatuanKecil;
import net.sra.prime.ultima.service.ServiceSatuanKecil;
//import org.primefaces.context.RequestContext;

/**
 *
 * @author Hairian
 */
@Named
@SessionScoped
@Setter
@Getter
public class ControllerSatuanKecil implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceSatuanKecil serviceSatuanKecil;
    private List<SatuanKecil> lSatuan = new ArrayList<>();
    private SatuanKecil item;

    @PostConstruct
    public void init() {
        initItem();
    }

    private void initItem() {
        item = new SatuanKecil();
    }

    public void onLoadList() {
        try {
            lSatuan = serviceSatuanKecil.onLoadList();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<SatuanKecil> getDataSatuan() {
        return lSatuan;
    }

    public void delete(String id_satuan_kecil) {
        FacesContext context = FacesContext.getCurrentInstance();
        //int id_satuan_kecil = Integer.parseInt(id);
        try {
            serviceSatuanKecil.delete(id_satuan_kecil);
            initItem();
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            // e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + id_satuan_kecil + " " + e, ""));
        }

    }

    public void update(String id_satuan_kecil) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?idsatuan=" + item.getId_satuan_kecil());
    }

//    public void selectSatuanFromDialog(SatuanKecil satuankecil) {
//        RequestContext.getCurrentInstance().closeDialog(satuankecil);
//    }

    public void onLoad() {
        String id = item.getId_satuan_lama();
        item = serviceSatuanKecil.onLoad(item.getId_satuan_lama());
        item.setId_satuan_lama(id);
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceSatuanKecil.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            //initItem();
             context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceSatuanKecil.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            initItem();
            // context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
