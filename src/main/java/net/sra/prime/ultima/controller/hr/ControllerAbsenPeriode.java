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
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.service.hr.ServiceAbsenPeriode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAbsenPeriode implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceAbsenPeriode serviceAbsenPeriode;
    private AbsenPeriode item;
    private List<AbsenPeriode> lAbsenPeriode = new ArrayList<>();
    private String tahun;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        if (tahun == null) {
            tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        }
        item = new AbsenPeriode();

    }

    public void initItem() {
        tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

    }

    public void onLoadList() {
        try {
            lAbsenPeriode = serviceAbsenPeriode.onLoadList(tahun);
            if (lAbsenPeriode.size() == 0) {
                lAbsenPeriode = new ArrayList<>();

                for (int i = 1; i <= 12; i++) {
                    item = new AbsenPeriode();
                    item.setYear(tahun);
                    item.setMonth(i);
                    item.setMonth_name(namaBulan(i));
                    lAbsenPeriode.add(item);
                }
            }else{
                for (int i = 0; i <= lAbsenPeriode.size(); i++) {
                    item = lAbsenPeriode.get(i);
                    item.setMonth_name(namaBulan(item.getMonth()));
                    lAbsenPeriode.set(i, item);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String namaBulan(Integer i) {
        String bulan = "";
        switch (i) {
            case 1:
                bulan = "Januari";
                break;
            case 2:
                bulan = "Februari";
                break;
            case 3:
                bulan = "Maret";
                break;
            case 4:
                bulan = "April";
                break;
            case 5:
                bulan = "Mei";
                break;
            case 6:
                bulan = "Juni";
                break;
            case 7:
                bulan = "Juli";
                break;
            case 8:
                bulan = "Agustus";
                break;
            case 9:
                bulan = "September";
                break;
            case 10:
                bulan = "Oktober";
                break;
            case 11:
                bulan = "November";
                break;
            case 12:
                bulan = "Desember";
                break;
        }
        return bulan;
    }

    public List<AbsenPeriode> getDataAbsenPeriode() {
        return lAbsenPeriode;
    }

    public void delete(String tahun) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceAbsenPeriode.delete(tahun);
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
            Boolean status = true;
            for (int i = 1; i < lAbsenPeriode.size(); i++) {
                if (lAbsenPeriode.get(i).getStart() == null || lAbsenPeriode.get(i).getEnd_date() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input !!! Semua data harus diisi !!!", ""));
                    status = false;
                    break;
                }
            }
            if (status) {
                serviceAbsenPeriode.tambah(lAbsenPeriode, tahun);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
