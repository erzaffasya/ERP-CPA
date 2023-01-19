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
import net.sra.prime.ultima.entity.Penjualan;
import net.sra.prime.ultima.service.ServicePenjualan;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

@Named("invoiceAutoComplete")
@ConversationScoped
@Getter
@Setter
public class InvoiceAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePenjualan servicePenjualan;

    List<Penjualan> allPenjualan;
    List<Penjualan> selectedPenjualan;
    Penjualan penjualan;
    String completePenjualanContains;

    @PostConstruct
    public void init() {
        penjualan = new Penjualan();
    }

    @Produces
    public List<Penjualan> completePenjualan(String query) {
        List<Penjualan> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenjualan.size(); i++) {
            Penjualan pilih = allPenjualan.get(i);
            if (pilih.getNo_penjualan().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    @Produces
    public List<Penjualan> completePenjualanContains(String query) {
        allPenjualan = servicePenjualan.selectPenjualanPosting();
        List<Penjualan> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenjualan.size(); i++) {
            Penjualan pilih = allPenjualan.get(i);
            if (pilih.getNo_penjualan().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }
    
    

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
