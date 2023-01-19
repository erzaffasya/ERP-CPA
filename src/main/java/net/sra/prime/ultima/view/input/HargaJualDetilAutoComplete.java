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
import net.sra.prime.ultima.entity.HargaJualDetil;
import net.sra.prime.ultima.service.ServiceHargaJual;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("hargaJualDetilAutoComplete")
@ConversationScoped
@Getter
@Setter
public class HargaJualDetilAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceHargaJual serviceHargaJual;

    List<HargaJualDetil> allHargaJualDetil;
    List<HargaJualDetil> selectedHargaJualDetil;
    HargaJualDetil hargaJualDetil;
    String no_kontrak;
    String completeHargaJualDetilContains;

    @PostConstruct
    public void init() {
        hargaJualDetil = new HargaJualDetil();
    }

    @Produces
    public List<HargaJualDetil> completeHargaJualDetil(String query) {
        List<HargaJualDetil> filteredHargaJualDetils = new ArrayList<>();

        for (int i = 0; i < allHargaJualDetil.size(); i++) {
            HargaJualDetil pilih = allHargaJualDetil.get(i);
            if (pilih.getNo_kontrak().toLowerCase().contains(query.toLowerCase())) {
                filteredHargaJualDetils.add(pilih);
            }
        }

        return filteredHargaJualDetils;
    }

    @Produces
    public List<HargaJualDetil> completeHargaJualDetilContains(String query) {
        allHargaJualDetil = serviceHargaJual.selectAllDetil(no_kontrak);
        List<HargaJualDetil> filteredHargaJualDetils = new ArrayList<>();

        for (int i = 0; i < allHargaJualDetil.size(); i++) {
            HargaJualDetil pilih = allHargaJualDetil.get(i);
            if (pilih.getNo_kontrak().toLowerCase().contains(query.toLowerCase())
                    || pilih.getNama_barang().toLowerCase().contains(query.toLowerCase())) {
                filteredHargaJualDetils.add(pilih);
            }
        }
        return filteredHargaJualDetils;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
