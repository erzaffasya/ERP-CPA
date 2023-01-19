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
import net.sra.prime.ultima.entity.Penerimaan;
import net.sra.prime.ultima.service.ServicePenerimaan;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("penerimaanAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PenerimaanAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePenerimaan servicePenerimaan;

    List<Penerimaan> allPenerimaan;
    List<Penerimaan> selectedPenerimaan;
    Penerimaan penerimaan;
    String id_supplier;
    String completePenerimaanContains;

    @PostConstruct
    public void init() {
        penerimaan = new Penerimaan();
    }

    @Produces
    public List<Penerimaan> completePenerimaan(String query) {
        List<Penerimaan> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenerimaan.size(); i++) {
            Penerimaan pilih = allPenerimaan.get(i);
            if (pilih.getNo_penerimaan().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    @Produces
    public List<Penerimaan> completePenerimaanContains(String query) {
        allPenerimaan = servicePenerimaan.selectSisaHutang(id_supplier);
        List<Penerimaan> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenerimaan.size(); i++) {
            Penerimaan pilih = allPenerimaan.get(i);
            if (pilih.getNo_penerimaan().toLowerCase().contains(query.toLowerCase())
                    || pilih.getReferensi().toLowerCase().contains(query.toLowerCase())
                    || pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
