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
package net.sra.prime.ultima.controller.kpi;

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
import net.sra.prime.ultima.entity.kpi.PegawaiKpi;
import net.sra.prime.ultima.service.kpi.ServicePegawaiKpi;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPegawaiKpi implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePegawaiKpi servicePegawaiKpi;
    private PegawaiKpi item;
    private List<PegawaiKpi> lPegawaiKpi = new ArrayList<>();
    private List<PegawaiKpi> selectedPegawaiKpi = new ArrayList<>();
    private String id_pegawai;
    private Double total;

    @PostConstruct
    public void init() {
        item = new PegawaiKpi();

    }

    public void initItem() {
        item = new PegawaiKpi();
    }

    public void onLoadList() {
        try {
            lPegawaiKpi = servicePegawaiKpi.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListPegawaiKpi() {
        try {
            total = 0.00;
            lPegawaiKpi = servicePegawaiKpi.onLoadListIndicator(id_pegawai);
            selectedPegawaiKpi = servicePegawaiKpi.onLoadListPegawaiKpi(id_pegawai);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<PegawaiKpi> getDataPegawaiKpi() {
        return lPegawaiKpi;
    }

    public void delete(String id_pegawai, String yr) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawaiKpi.delete(id_pegawai);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (total.equals(100.00)) {
            try {
                servicePegawaiKpi.tambah(selectedPegawaiKpi, id_pegawai);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "KPI tidak dapat disimpan Total Bobot harus 100", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_pegawai());
    }

//    public void onLoad() {
//        item = servicePegawaiKpi.onLoad(item.getId_pegawai(),item.getYear());
//    }
    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawaiKpi.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void calculateTotal(Double value) {
        total += value;
    }

    public Double getAcummulatedValue(String category) {
        Double jumlah = 0.00;
        total = 0.00;
        for (int i = 0; i < selectedPegawaiKpi.size(); i++) {
            if (selectedPegawaiKpi.get(i).getCategory().equals(category)) {
                jumlah += selectedPegawaiKpi.get(i).getBobot();
            }
            total += selectedPegawaiKpi.get(i).getBobot();
        }
        return jumlah;
    }

}
