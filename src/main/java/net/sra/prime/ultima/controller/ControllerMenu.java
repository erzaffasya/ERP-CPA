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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Menu;
import net.sra.prime.ultima.service.ServiceMenu;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerMenu implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceMenu serviceMenu;
    private Menu item;
    private List<Menu> lMenu = new ArrayList<>();

    @PostConstruct
    public void init() {
        item = new Menu();

    }

    public void initItem() {
        item = new Menu();
    }

    public void onLoadList() {
        try {
            lMenu = serviceMenu.onLoadListMenu();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Menu> getDataMenu() {
        return lMenu;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceMenu.deleteMenu(id);
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
            serviceMenu.tambahMenu(item);
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
        Integer id = item.getIdlama();
        item = serviceMenu.onLoad(item.getIdlama());
        item.setIdlama(id);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceMenu.updatehMenu(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboParent() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Menu> list = serviceMenu.onLoadListParent();
            map.put("Plilih Parent", "");
            list.stream().forEach((data) -> {
                map.put(data.getName(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboJenis() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Plilih Jenis", "");
            map.put("Detail", "D");
            map.put("Group", "G");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void jenisChange() {
        if (item.getJenis().equals('G')) {
            item.setId(serviceMenu.selectMaxGroup());
        }
    }

    public void parentChange() {
        item.setId(serviceMenu.selectMaxDetail(item.getParent()));
    }

}
