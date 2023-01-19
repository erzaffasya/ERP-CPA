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
import net.sra.prime.ultima.entity.AccArFaktur;
import net.sra.prime.ultima.service.ServicePembayaranPiutang;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

@Named("arAutoComplete")
@ConversationScoped
@Getter
@Setter
public class ArAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePembayaranPiutang referensi;

    List<AccArFaktur> allAccArFaktur;
    List<AccArFaktur> selectedAccArFaktur;
    AccArFaktur accArFaktur;
    String id_customer;
   
    @PostConstruct
    public void init() {
        accArFaktur = new AccArFaktur();
    }

    @Produces
    public List<AccArFaktur> completeAccArFaktur(String query) {
        List<AccArFaktur> filteredPos = new ArrayList<>();

        for (int i = 0; i < allAccArFaktur.size(); i++) {
            AccArFaktur pilih = allAccArFaktur.get(i);
            if (pilih.getAr_number().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }

    @Produces
    public List<AccArFaktur> completeAccArFakturContains(String query) {
        allAccArFaktur = referensi.selectAccArFaktur(id_customer);
        List<AccArFaktur> filteredPos = new ArrayList<>();

        for (int i = 0; i < allAccArFaktur.size(); i++) {
            AccArFaktur pilih = allAccArFaktur.get(i);
            if (pilih.getAr_number().toLowerCase().contains(query.toLowerCase()) || pilih.getNo_invoice().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
