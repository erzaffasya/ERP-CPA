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
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalPerusahaan;
import net.sra.prime.ultima.service.ServicePage;

/**
 *
 * @author Syamsu Rizal Ali <syamsu.smansa.polban@gmail.com>
 */
@Named
@SessionScoped
@Getter
@Setter
public class IndexPage implements java.io.Serializable {

    InternalPerusahaan iPerusahaan;
    String srcGambar;

    String cabangOrGudangId = "0000";
    List<SelectItem> cabangOrGudangs;
    List<InternalPerusahaan> perusahaan;

    @Autowired
    ServicePage servicePage;

    public boolean isLangsung() {
        return (iPerusahaan == null);
    }

    public Integer getTotalInternalPerusahaan() {
        int oo = getInternalPerusahaan().size();
        return getInternalPerusahaan().size();
    }

    public List<InternalPerusahaan> getInternalPerusahaan() {
        if (perusahaan != null && perusahaan.size() > 0) {
            return perusahaan;
        }
        try {
            perusahaan = servicePage.getAllPerusahaan();
        } catch (Exception e) {
            perusahaan = new ArrayList<>();
        }
        return perusahaan;
    }

    public void pilihIPerusahaan(String idKantor) {
        try {
         iPerusahaan = servicePage.getPerusahaan(idKantor);
            this.srcGambar = "/img/" + iPerusahaan.getLogo();
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + SecurityConfig.URL_LOGIN);
            initComboBox();
        } catch (Exception e) {
        }
    }

    void initComboBox() {
        String idPerusahaan = iPerusahaan.getId_perusahaan();
        cabangOrGudangs = new ArrayList<>();
        cabangOrGudangId = "0000";
       
        try {
            SelectItemGroup cabangG = new SelectItemGroup("Cabang");
            List<InternalKantorCabang> cabangs = servicePage.getKantorCabang(idPerusahaan);

            SelectItem[] selectItem = new SelectItem[cabangs.size()];

            int i = 0;
            for (InternalKantorCabang cabang : cabangs) {
                selectItem[i++] = new SelectItem(cabang.getId_kantor_cabang(), cabang.getNama());
            }
            cabangG.setSelectItems(selectItem);
            cabangOrGudangs.add(cabangG);
        } catch (Exception e) {

        }
        try {
            SelectItemGroup gudangG = new SelectItemGroup("Gudang");
            List<Gudang> gudangs = servicePage.getGudang(idPerusahaan);

            SelectItem[] selectItem = new SelectItem[gudangs.size()];

            int i = 0;
            for (Gudang gudang : gudangs) {
                selectItem[i++] = new SelectItem(gudang.getId_gudang(), gudang.getGudang());
            }
            gudangG.setSelectItems(selectItem);
            cabangOrGudangs.add(gudangG);
        } catch (Exception e) {

        }
    }

}
