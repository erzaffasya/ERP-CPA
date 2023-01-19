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
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.service.ServicePackinglist;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named("packinglistAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PackinglistAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePackinglist servicePackinglist;

    @Inject
    private Page page;

    List<Packinglist> allPackinglist;
    List<Packinglist> selectedPackinglist;
    Packinglist packinglist;
    String completeSoContains;

    @PostConstruct
    public void init() {
        packinglist = new Packinglist();
    }

    @Produces
    public List<Packinglist> completePackinglist(String query) {
        List<Packinglist> filteredSos = new ArrayList<>();

        for (int i = 0; i < allPackinglist.size(); i++) {
            Packinglist pilih = allPackinglist.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())) {
                filteredSos.add(pilih);
            }
        }

        return filteredSos;
    }

    @Produces
    public List<Packinglist> completePackinglistContains(String query) {
        allPackinglist = servicePackinglist.selectPackinglistCustomer(page.getMyPegawai().getId_pegawai());
        List<Packinglist> filteredSos = new ArrayList<>();
        for (int i = 0; i < allPackinglist.size(); i++) {
            Packinglist pilih = allPackinglist.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())
                    || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())
                    || pilih.getReferensi().toLowerCase().contains(query.toLowerCase())) {
                filteredSos.add(pilih);
            }
        }
        return filteredSos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
