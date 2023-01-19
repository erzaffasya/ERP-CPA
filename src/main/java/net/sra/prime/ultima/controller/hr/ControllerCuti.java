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
import net.sra.prime.ultima.entity.hr.Cuti;
import net.sra.prime.ultima.entity.hr.CutiMaster;
import net.sra.prime.ultima.service.hr.ServiceCuti;
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
public class ControllerCuti implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceCuti serviceCuti;
    private Cuti item;
    private List<Cuti> lCuti = new ArrayList<>();
    private DualListModel<MasterJabatan> masterJabatans;
    private Integer id;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Cuti();

    }

    public void initItem() {
        item = new Cuti();
        item.setJenis('h');
        item.setStatus(Boolean.TRUE);
        item.setBatasan(Boolean.FALSE);
        item.setPersetujuan2('3');
        item.setPersetujuan3('3');
        item.setPersetujuan4('3');
        List<MasterJabatan> lJabatanSource = serviceCuti.onLoadListJabatan();
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
        item.setAll_employee(Boolean.TRUE);
    }

    public void onLoadList() {
        try {
            Cuti ct;
            lCuti = serviceCuti.onLoadList();
            for (int i = 0; i < lCuti.size(); i++) {
                ct = lCuti.get(i);
                if (lCuti.get(i).getBatasan()) {
                    ct.setKeterangan("Saldo cuti tidak berbatas");
                } else {
                    ct.setKeterangan("Saldo cuti " + ct.getSaldo() + " hari kerja");
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<Cuti> getDataCuti() {
        return lCuti;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCuti.delete(id);
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
            serviceCuti.tambah(item, masterJabatans.getTarget());
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
        item = serviceCuti.onLoad(id);
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        if (!item.getAll_employee()) {
             lJabatanTarget = serviceCuti.onLoadJabatan(item.getId());
        }
        List<MasterJabatan> lJabatanSource = serviceCuti.onLoadAllJabatan(item.getId());
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCuti.ubah(item, masterJabatans.getTarget());
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

    public Map<String, String> getComboCutiMaster() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<CutiMaster> list = serviceCuti.onLoadListCutiMaster();
            map.put("Pilih salah satu", "");
            list.stream().forEach((data) -> {
                map.put(data.getNama_master(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboPersetujuan1() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Atasan langsung", "4");
            map.put("Head departemen", "1");
            map.put("Jabatan tertentu", "2");
            map.put("Tidak perlu persetujuan", "3");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboPersetujuan22() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Cuti dapat diambil", "3");
            map.put("Atasan langsung 2", "4");
            map.put("Head departemen", "1");
            map.put("Jabatan tertentu", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboPersetujuan3() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Head departemen", "1");
            map.put("Jabatan tertentu", "2");
            map.put("Cuti dapat diambil", "3");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboPersetujuan4() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Jabatan tertentu", "2");
            map.put("Cuti dapat diambil", "3");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboPersetujuan2() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Cuti dapat diambil", "1");
            map.put("Ajukan persetujuan kedua", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void onMasterSelect() {
        List<MasterJabatan> lJabatanSource = serviceCuti.onLoadAllJabatan(item.getMaster());
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void onStatusSelect() {
        if (item.getStatus()) {
            List<MasterJabatan> lJabatanSource = serviceCuti.onLoadListJabatan();
            List<MasterJabatan> lJabatanTarget = new ArrayList<>();
            masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
        }
    }

    public Map<String, String> getCombojabatan1() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MasterJabatan> list = serviceCuti.onLoadListJabatan();

            list.stream().forEach((data) -> {
                map.put(data.getJabatan(), Integer.toString(data.getId_jabatan()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getCombojabatan2() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MasterJabatan> list = serviceCuti.onLoadListJabatan();

            list.stream().forEach((data) -> {
                map.put(data.getJabatan(), Integer.toString(data.getId_jabatan()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
