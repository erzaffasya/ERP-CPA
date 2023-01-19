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
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.service.ServiceHargaJual;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("hargaJualAutoComplete")
@ConversationScoped
@Getter
@Setter
public class HargaJualAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceHargaJual serviceHargaJual;

    List<HargaJual> allHargaJual;
    List<HargaJual> selectedHargaJual;
    HargaJual hargaJual;
    String id_customer;
    String completeHargaJualContains;

    @PostConstruct
    public void init() {
        hargaJual = new HargaJual();
    }

    @Produces
    public List<HargaJual> completeHargaJual(String query) {
        List<HargaJual> filteredHargaJuals = new ArrayList<>();

        for (int i = 0; i < allHargaJual.size(); i++) {
            HargaJual pilih = allHargaJual.get(i);
            if (pilih.getNo_kontrak().toLowerCase().contains(query.toLowerCase())) {
                filteredHargaJuals.add(pilih);
            }
        }

        return filteredHargaJuals;
    }

    @Produces
    public List<HargaJual> completeHargaJualContains(String query) {
        allHargaJual = serviceHargaJual.onLoadList();
        List<HargaJual> filteredHargaJuals = new ArrayList<>();

        for (int i = 0; i < allHargaJual.size(); i++) {
            HargaJual pilih = allHargaJual.get(i);
            if (pilih.getNo_kontrak().toLowerCase().contains(query.toLowerCase())
                    || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredHargaJuals.add(pilih);
            }
        }

        return filteredHargaJuals;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
