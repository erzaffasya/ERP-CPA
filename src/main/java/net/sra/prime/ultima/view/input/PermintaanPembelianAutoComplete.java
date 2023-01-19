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
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.service.ServicePermintaanPembelian;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("permintaanPembelianAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PermintaanPembelianAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePermintaanPembelian servicePermintaanPembelian;

    List<PermintaanPembelian> allPermintaanPembelian;
    List<PermintaanPembelian> selectedPermintaanPembelian;
    PermintaanPembelian permintaanPembelian;
    String id_customer;
    String completePermintaanPembelianContains;

    @PostConstruct
    public void init() {
        permintaanPembelian = new PermintaanPembelian();
    }

    @Produces
    public List<PermintaanPembelian> completePermintaanPembelian(String query) {
        List<PermintaanPembelian> filteredPermintaanPembelians = new ArrayList<>();

        for (int i = 0; i < allPermintaanPembelian.size(); i++) {
            PermintaanPembelian pilih = allPermintaanPembelian.get(i);
            if (pilih.getNo_pp().toLowerCase().contains(query.toLowerCase())) {
                filteredPermintaanPembelians.add(pilih);
            }
        }

        return filteredPermintaanPembelians;
    }

    @Produces
    public List<PermintaanPembelian> completePermintaanPembelianContains(String query) throws Exception {
        allPermintaanPembelian = servicePermintaanPembelian.onLoadList(null,null,null,true,null,null);
        List<PermintaanPembelian> filteredPermintaanPembelians = new ArrayList<>();

        for (int i = 0; i < allPermintaanPembelian.size(); i++) {
            PermintaanPembelian pilih = allPermintaanPembelian.get(i);
            if (pilih.getNo_pp().toLowerCase().contains(query.toLowerCase())) {
                filteredPermintaanPembelians.add(pilih);
            }
        }

        return filteredPermintaanPembelians;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
