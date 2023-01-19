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
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.service.ServiceSupplier;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("supplierAutoComplete")
@ViewScoped
@Getter
@Setter
public class SupplierAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceSupplier serviceSupplier;

    List<Supplier> allSupplier;
    List<Supplier> selectedSupplier;
    Supplier supplier;
    String completeSupplierContains;

    @PostConstruct
    public void init() {
        allSupplier = serviceSupplier.onLoadList();
        
    }

    @Produces
    public List<Supplier> completeSupplier(String query) {
        List<Supplier> filteredSuppliers = new ArrayList<>();

        for (int i = 0; i < allSupplier.size(); i++) {
            Supplier pilih = allSupplier.get(i);
            if (pilih.getSupplier().toLowerCase().contains(query.toLowerCase())) {
                filteredSuppliers.add(pilih);
            }
        }

        return filteredSuppliers;
    }

    @Produces
    public List<Supplier> completeSupplierContains(String query) {
        List<Supplier> filteredSuppliers = new ArrayList<>();

        for (int i = 0; i < allSupplier.size(); i++) {
            Supplier pilih = allSupplier.get(i);
            if (pilih.getSupplier().toLowerCase().contains(query.toLowerCase()) || pilih.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredSuppliers.add(pilih);
            }
        }

        return filteredSuppliers;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public char getSupplierGroup(Supplier supplier) {
        return supplier.getSupplier().charAt(0);
    }

}
