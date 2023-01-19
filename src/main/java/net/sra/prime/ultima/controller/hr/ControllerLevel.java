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
import net.sra.prime.ultima.entity.hr.Golongan;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.LevelJabatan;
import net.sra.prime.ultima.service.hr.ServiceLevel;
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
public class ControllerLevel implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceLevel serviceLevel;
    private Level item;
    private List<Level> lLevel = new ArrayList<>();
    private DualListModel<MasterJabatan> masterjabatans;
    private Integer id;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Level();

    }

    public void initItem() {
        item = new Level();
        List<MasterJabatan> lJabatanSource = serviceLevel.onLoadListJabatan();
        List<MasterJabatan> lJabatanTarget = new ArrayList<>();
        masterjabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void onLoadList() {
        try {
            lLevel = serviceLevel.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<Level> getDataLevel() {
        return lLevel;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLevel.delete(id);
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
            serviceLevel.tambah(item, masterjabatans.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void onLoad() {
        item = serviceLevel.onLoad(item.getId());
        List<MasterJabatan> lJabatanSource = serviceLevel.onLoadListJabatan();
        List<MasterJabatan> lJabatanTarget = serviceLevel.onLoadJabatanLevel(item.getId());
        masterjabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLevel.ubah(item, masterjabatans.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboLevel() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("Pilih Level", "");
            map.put("1", "1");
            map.put("2", "2");
            map.put("3", "3");
            map.put("4", "4");
            map.put("5", "5");
            map.put("6", "6");
            map.put("7", "7");
            map.put("8", "8");
            map.put("9", "9");
            map.put("10", "10");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    
    public Map<String, String> getComboGolongan() {
        List<Golongan> list = serviceLevel.onLoadListGolongan();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {

            map.put("Pilih Golongan", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getGolongan(), Integer.toString(data.getId()));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    
//    public void onStatusSelect() {
//        if (item.getStatus()) {
//            List<MasterJabatan> lJabatanSource = serviceLevel.onLoadListJabatan();
//            List<MasterJabatan> lJabatanTarget = new ArrayList<>();
//            masterjabatans = new DualListModel<MasterJabatan>(lJabatanSource, lJabatanTarget);
//        }
//    }

}
