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
package net.sra.prime.ultima.controller.hr;

import net.sra.prime.ultima.controller.*;
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
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.hr.ServiceSalesAdmin;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerSalesAdmin implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceSalesAdmin serviceSalesAdmin;
    private String id_admin;
    private List<Pegawai> lSource = new ArrayList<>();
    private List<Pegawai> lTarget = new ArrayList<>();
    private DualListModel<Pegawai> pegawai;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
    pegawai = new DualListModel<Pegawai>(lSource, lTarget);

    }

    
    

    public List<Pegawai> getDataPegawaiSource() {
        return lSource;
    }

    public List<Pegawai> getDataPegawaiTarget() {
        return lTarget;
    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai p = (Pegawai) event.getObject();
        id_admin = p.getId_pegawai();
        lSource = serviceSalesAdmin.selectAllPegawai(id_admin);
        lTarget = serviceSalesAdmin.selectAllSales(id_admin);
        pegawai = new DualListModel<Pegawai>(lSource, lTarget);
    }

    

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSalesAdmin.tambah(id_admin, pegawai.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    

}
