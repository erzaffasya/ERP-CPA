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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.HakAkses;
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
public class ControllerMenuAkses implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceMenu serviceMenu;
    private Menu item;
    private List<Menu> lMenuUtama = new ArrayList<>();
    private List<Integer> selectedMenuUtama = new ArrayList<>();
    private List<Menu> lMenuAdmin = new ArrayList<>();
    private List <Integer> selectedMenuAdmin= new ArrayList<>();
    private List<Menu> lMenuWarehouse = new ArrayList<>();
    private List <Integer> selectedMenuWarehouse= new ArrayList<>();
    private List<Menu> lMenuMarketing = new ArrayList<>();
    private List <Integer> selectedMenuMarketing= new ArrayList<>();
    private List<Menu> lMenuAccounting = new ArrayList<>();
    private List <Integer> selectedMenuAccounting;
    private List<Menu> lMenuHrd = new ArrayList<>();
    private List <Integer> selectedMenuHrd;
    private List<Menu> lMenuMaster = new ArrayList<>();
    private List <Integer> selectedMenuMaster;
    private List<Menu> lMenuLaporan = new ArrayList<>();
    private List <Integer> selectedMenuLaporan;
    private List<Menu> lMenuMaintenance = new ArrayList<>();
    private List <Integer> selectedMenuMaintenance;
    private List<Menu> lMenuSetting = new ArrayList<>();
    private List <Integer> selectedMenuSetting;
    private List<Menu> lMenuPayroll= new ArrayList<>();
    private List <Integer> selectedMenuPayroll;
    private List<Menu> lMenuKpi= new ArrayList<>();
    private List <Integer> selectedMenuKpi;
    
    private String usernya;
    
    

    @PostConstruct
    public void init() {
        item = new Menu();
        
    }

    public void initItem() {
        item = new Menu();
    }

    public void onLoadList() {
        try {
            
            lMenuUtama = serviceMenu.menuUtama();
            lMenuAdmin = serviceMenu.subMenu(100);
            lMenuMarketing = serviceMenu.subMenu(300);
            lMenuAccounting = serviceMenu.subMenu(400);
            lMenuWarehouse = serviceMenu.subMenu(200);
            lMenuHrd = serviceMenu.subMenu(500);
            lMenuMaster = serviceMenu.subMenu(600);
            lMenuLaporan = serviceMenu.subMenu(700);
            lMenuMaintenance = serviceMenu.subMenu(800);
            lMenuSetting = serviceMenu.subMenu(900);
            lMenuPayroll = serviceMenu.subMenu(1000);
            lMenuKpi = serviceMenu.subMenu(1100);
            
            selectedMenuUtama= new ArrayList<>();
            List<HakAkses> hakAkses=serviceMenu.menuUtamaHakAkses(usernya);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuUtama.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuAdmin= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 100);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuAdmin.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuMarketing= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 300);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuMarketing.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuWarehouse= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 200);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuWarehouse.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuAccounting= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 400);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuAccounting.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuHrd= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 500);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuHrd.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuMaster= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 600);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuMaster.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuLaporan= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 700);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuLaporan.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuMaintenance= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 800);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuMaintenance.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuSetting= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 900);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuSetting.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuPayroll= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 1000);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuPayroll.add(hakAkses.get(i).getId_menu());
            }
            
            selectedMenuKpi= new ArrayList<>();
            hakAkses=serviceMenu.menuHakAkses(usernya, 1100);
            for(int i=0;i < hakAkses.size();i++){
              selectedMenuKpi.add(hakAkses.get(i).getId_menu());
            }
            
            
    }
    catch (Exception ex) {
            ex.printStackTrace();
    }
}

public List<Menu> getDataMenuUtama() {
        return lMenuUtama;
}

public List<Menu> getDataMenuAdmin() {
        return lMenuAdmin;
}

public List<Menu> getDataMenuWarehouse() {
        return lMenuWarehouse;
}

public List<Menu> getDataMenuMarketing() {
        return lMenuMarketing;
}

public List<Menu> getDataMenuAccounting() {
        return lMenuAccounting;
}

public List<Menu> getDataMenuHrd() {
        return lMenuHrd;
}

public List<Menu> getDataMenuMaster() {
        return lMenuMaster;
}

public List<Menu> getDataMenuLaporan() {
        return lMenuLaporan;
}

public List<Menu> getDataMenuMaintenance() {
        return lMenuMaintenance;
}

public List<Menu> getDataMenuSetting() {
        return lMenuSetting;
}

public List<Menu> getDataMenuPayroll() {
        return lMenuPayroll;
}

public List<Menu> getDataMenuKpi() {
        return lMenuKpi;
}



//    public void delete(String id) {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        try {
//            serviceMenu.delete(id);
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
//            context.getExternalContext().redirect("./list.jsf");
//        } catch (Exception e) {
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
//        }
//    }
//
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceMenu.tambah(selectedMenuUtama, selectedMenuAdmin, selectedMenuMarketing, selectedMenuWarehouse,selectedMenuHrd,selectedMenuMaster,selectedMenuLaporan,selectedMenuAccounting,selectedMenuMaintenance,selectedMenuSetting,selectedMenuPayroll,selectedMenuKpi, usernya);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
//
//    public void update() throws IOException {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_family());
//    }
//
//    public void onLoad() {
//        item = serviceMenu.onLoad(item.getId_family());
//    }
//
//    public void ubah() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        try {
//            serviceMenu.ubah(item);
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
//            context.getExternalContext().redirect("./list.jsf");
//        } catch (Exception e) {
//            e.printStackTrace();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
//        }
//    }

    

}
