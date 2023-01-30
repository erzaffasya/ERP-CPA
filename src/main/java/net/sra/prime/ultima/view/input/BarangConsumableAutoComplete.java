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
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.BarangConsumable;
import net.sra.prime.ultima.service.ServiceBarangConsumable;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named("barangConsumableAutoComplete")
@ConversationScoped
@Getter
@Setter
public class BarangConsumableAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceBarangConsumable serviceBarangConsumable;

    List<BarangConsumable> allBarang;
    List<BarangConsumable> selectedBarang;
    BarangConsumable barangConsumable;
    String completeBarangContains;

    @PostConstruct
    public void init() {
        barangConsumable = new BarangConsumable();

    }

    @Produces
    public List<BarangConsumable> completeBarang(String query) {

        List<BarangConsumable> filteredBarangs = new ArrayList<>();

        for (int i = 0; i < allBarang.size(); i++) {
            BarangConsumable pilih = allBarang.get(i);
            if (pilih.getNama_barang().toLowerCase().contains(query.toLowerCase())) {
                filteredBarangs.add(pilih);
            }
        }

        return filteredBarangs;
    }

    @Produces
    public List<BarangConsumable> completeBarangContains(String query) {
        if (StringUtils.isBlank(query)) {
            setBarangConsumable(null);  // textfield was cleared, reset device value!
            return Collections.emptyList();
        } else {
            allBarang = serviceBarangConsumable.onLoadList();

            List<BarangConsumable> filteredBarangs = new ArrayList<>();

            for (int i = 0; i < allBarang.size(); i++) {
                BarangConsumable pilih = allBarang.get(i);
                if (pilih.getNama_barang().toLowerCase().contains(query.toLowerCase())
                        || pilih.getNama_barang().toLowerCase().contains(query.toLowerCase())
                        || pilih.getMaterial_code().toLowerCase().contains(query.toLowerCase())
                        || pilih.getPart_number().toLowerCase().contains(query.toLowerCase())) {
                    filteredBarangs.add(pilih);
                }
            }
            return filteredBarangs;
        }
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public char getBarangGroup(BarangConsumable barangConsumable) {
        return barangConsumable.getNama_barang().charAt(0);
    }

}
