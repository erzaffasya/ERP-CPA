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
package net.sra.prime.ultima.view.input;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.service.ServicePenawaran;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("penawaranAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PenawaranAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePenawaran servicePenawaran;

    List<Penawaran> allPenawaran;
    List<Penawaran> selectedPenawaran;
    Penawaran penawaran;
    String id_customer;
    String completePenawaranContains;
    String id_admin;

    @PostConstruct
    public void init() {
        penawaran = new Penawaran();
    }

    @Produces
    public List<Penawaran> completePenawaran(String query) {
        List<Penawaran> filteredPenawarans = new ArrayList<>();

        for (int i = 0; i < allPenawaran.size(); i++) {
            Penawaran pilih = allPenawaran.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())) {
                filteredPenawarans.add(pilih);
            }
        }

        return filteredPenawarans;
    }

    @Produces
    public List<Penawaran> completePenawaranContains(String query) {
        allPenawaran = servicePenawaran.selectPenawaranCustomer();
        List<Penawaran> filteredPenawarans = new ArrayList<>();

        for (int i = 0; i < allPenawaran.size(); i++) {
            Penawaran pilih = allPenawaran.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())
                    || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredPenawarans.add(pilih);
            }
        }
        return filteredPenawarans;
    }
    
    @Produces
    public List<Penawaran> completePenawaranBySalesAdmin(String query) {
        allPenawaran = servicePenawaran.selectPenawaranBySalesAdmin(id_admin);
        List<Penawaran> filteredPenawarans = new ArrayList<>();

        for (int i = 0; i < allPenawaran.size(); i++) {
            Penawaran pilih = allPenawaran.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())
                    || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredPenawarans.add(pilih);
            }
        }
        return filteredPenawarans;
    }

    @Produces
    public List<Penawaran> completePenawaranSisaContains(String query) {
        allPenawaran = servicePenawaran.selectPenawaranSisaCustomer(id_customer);
        List<Penawaran> filteredPenawarans = new ArrayList<>();

        for (int i = 0; i < allPenawaran.size(); i++) {
            Penawaran pilih = allPenawaran.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())) {
                filteredPenawarans.add(pilih);
            }
        }
        return filteredPenawarans;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
