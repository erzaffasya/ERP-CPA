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
import net.sra.prime.ultima.entity.Dotbl;
import net.sra.prime.ultima.service.ServiceDo;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("doAutoComplete")
@ConversationScoped
@Getter
@Setter
public class DoAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceDo serviceDo;

    List<Dotbl> allDotbl;
    List<Dotbl> selectedDotbl;
    Dotbl dotbl;
    //List<Dotbl> selectedDotbl;
    String id_customer;
    String completeSoContains;

    @PostConstruct
    public void init() {
        dotbl = new Dotbl();
    }

    @Produces
    public List<Dotbl> completeDotbl(String query) {
        List<Dotbl> filteredSos = new ArrayList<>();

        for (int i = 0; i < allDotbl.size(); i++) {
            Dotbl pilih = allDotbl.get(i);
            if (pilih.getNomor().toLowerCase().contains(query.toLowerCase())) {
                filteredSos.add(pilih);
            }
        }

        return filteredSos;
    }

    @Produces
    public List<Dotbl> completeDotblContains(String query) {
        allDotbl = serviceDo.selectDoCustomer();
        List<Dotbl> filteredSos = new ArrayList<>();

        for (int i = 0; i < allDotbl.size(); i++) {
            Dotbl pilih = allDotbl.get(i);
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
