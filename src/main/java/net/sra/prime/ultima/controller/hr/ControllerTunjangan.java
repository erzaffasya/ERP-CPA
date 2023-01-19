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
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Tunjangan;
import net.sra.prime.ultima.entity.hr.TunjanganMaster;
import net.sra.prime.ultima.service.hr.ServiceTunjangan;
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
public class ControllerTunjangan implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceTunjangan serviceTunjangan;
    private Tunjangan item;
    private List<Tunjangan> lTunjangan = new ArrayList<>();
    private DualListModel<MasterJabatan> masterJabatans;
    private Integer id;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Tunjangan();

    }

    public void initItem() {
        item = new Tunjangan();
        item.setJenis('h');
        item.setPajak(Boolean.TRUE);
        item.setStatus(Boolean.TRUE);
        List<MasterJabatan> lJabatanSource = serviceTunjangan.onLoadListJabatan();
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void onLoadList() {
        try {
            lTunjangan = serviceTunjangan.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<Tunjangan> getDataTunjangan() {
        return lTunjangan;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceTunjangan.delete(id);
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
            serviceTunjangan.tambah(item, masterJabatans.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update(String idtunjangan) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + idtunjangan);
    }

    public void onLoad() {
        item = serviceTunjangan.onLoad(id);
        List<MasterJabatan> lJabatanSource = serviceTunjangan.onLoadAllJabatan(item.getId());
        List<MasterJabatan> lJabatanTarget = serviceTunjangan.onLoadJabatan(item.getId());
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceTunjangan.ubah(item, masterJabatans.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboJenis() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("Harian", "h");
            map.put("Bulanan", "b");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboTunjanganMaster() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<TunjanganMaster> list = serviceTunjangan.onLoadListTunjanganMaster();
            map.put("Pilih salah satu", "");
            list.stream().forEach((data) -> {
                map.put(data.getNama(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void onMasterSelect() {
        List<MasterJabatan> lJabatanSource = serviceTunjangan.onLoadAllJabatan(item.getMaster());
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void onStatusSelect() {
        if (item.getStatus()) {
            List<MasterJabatan> lJabatanSource = serviceTunjangan.onLoadListJabatan();
            List<MasterJabatan> lJabatanTarget = new ArrayList<>();
            masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
        }
    }

}
