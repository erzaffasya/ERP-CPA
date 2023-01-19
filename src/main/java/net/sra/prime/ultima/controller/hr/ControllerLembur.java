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
import net.sra.prime.ultima.entity.hr.Lembur;
import net.sra.prime.ultima.entity.hr.LemburMultiplier;
import net.sra.prime.ultima.service.hr.ServiceLembur;
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
public class ControllerLembur implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceLembur serviceLembur;
    private Lembur item;
    private List<Lembur> lLembur = new ArrayList<>();
    private List<LemburMultiplier> lLemburMultiplierWeekday = new ArrayList<>();
    private List<LemburMultiplier> lLemburMultiplierWeekend = new ArrayList<>();
    private DualListModel<MasterJabatan> masterJabatans;
    private Integer id;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Lembur();

    }

    public void initItem() {
        item = new Lembur();
        item.setKebijakan(Boolean.TRUE);
        item.setPersetujuan2('3');
        item.setPersetujuan3('3');
        item.setPersetujuan4('3');
        List<MasterJabatan> lJabatanSource = serviceLembur.onLoadJabatanSource();
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
        lLemburMultiplierWeekday.add(new LemburMultiplier());
        lLemburMultiplierWeekend.add(new LemburMultiplier());
    }

    public void onLoadList() {
        try {
            lLembur = serviceLembur.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<Lembur> getDataLembur() {
        return lLembur;
    }
    
    public List<LemburMultiplier> getDataLemburMultiplierWeekday() {
        return lLemburMultiplierWeekday;
    }
    
    public List<LemburMultiplier> getDataLemburMultiplierWeekend() {
        return lLemburMultiplierWeekend;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLembur.delete(id);
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
            item.setBiaya(2.00);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            serviceLembur.tambah(item, masterJabatans.getTarget(),lLemburMultiplierWeekday,lLemburMultiplierWeekend);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
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
        item = serviceLembur.onLoad(id);
        List<MasterJabatan> lJabatanSource = serviceLembur.onLoadJabatanSource();
        List<MasterJabatan> lJabatanTarget = serviceLembur.onLoadJabatanTarget(item.getId());
        lLemburMultiplierWeekday = serviceLembur.selectJenisMultiplier(item.getId(), 'd');
        lLemburMultiplierWeekend = serviceLembur.selectJenisMultiplier(item.getId(), 'e');
        masterJabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLembur.ubah(item, masterJabatans.getTarget(),lLemburMultiplierWeekday,lLemburMultiplierWeekend);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
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
            map.put("Lembur dapat diambil", "1");
            map.put("Ajukan persetujuan kedua", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    
    
    public Map<String, String> getCombojabatan1() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MasterJabatan> list = serviceLembur.onLoadListJabatan();
            
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
            List<MasterJabatan> list = serviceLembur.onLoadListJabatan();
            
            list.stream().forEach((data) -> {
                map.put(data.getJabatan(), Integer.toString(data.getId_jabatan()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    public void extendWeekday() {
        lLemburMultiplierWeekday.add(new LemburMultiplier());
    }
    
    public void onDeleteClickedWeekday(LemburMultiplier lm) {
        lLemburMultiplierWeekday.remove(lm);
    }
    
    public void extendWeekend() {
        lLemburMultiplierWeekend.add(new LemburMultiplier());
    }
    
    public void onDeleteClickedWeekend(LemburMultiplier lm) {
        lLemburMultiplierWeekend.remove(lm);
    }
    
    
    
}
