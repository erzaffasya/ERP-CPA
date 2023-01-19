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
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServicePegawai;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named("pegawaiAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PegawaiAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePegawai servicePegawai;

    List<Pegawai> allPegawai;
    List<Pegawai> selectedPegawai;
    Pegawai pegawai;
    String completePegawaiContains;
    String id_perusahaan;

    @PostConstruct
    public void init() {

    }

    @Produces
    public List<Pegawai> completePegawai(String query) {
        List<Pegawai> filteredPegawais = new ArrayList<>();

        for (int i = 0; i < allPegawai.size(); i++) {
            Pegawai pilih = allPegawai.get(i);
            if (pilih.getNama().toLowerCase().contains(query.toLowerCase())) {
                filteredPegawais.add(pilih);
            }
        }

        return filteredPegawais;
    }

    @Produces
    public List<Pegawai> completePegawaiPenggunaContains(String query) {
        List<Pegawai> filteredPegawais = new ArrayList<>();
        allPegawai = servicePegawai.onLoadListPegawaiPengguna();
        for (int i = 0; i < allPegawai.size(); i++) {
            Pegawai pilih = allPegawai.get(i);
            if (pilih.getNama().toLowerCase().contains(query.toLowerCase())) {
                filteredPegawais.add(pilih);
            }
        }
        return filteredPegawais;
    }
    
    @Produces
    public List<Pegawai> completePegawaiContains(String query) {
        List<Pegawai> filteredPegawais = new ArrayList<>();
        allPegawai = servicePegawai.onLoadList(true);
        for (int i = 0; i < allPegawai.size(); i++) {
            Pegawai pilih = allPegawai.get(i);
            if (pilih.getNama().toLowerCase().contains(query.toLowerCase())) {
                filteredPegawais.add(pilih);
            }
        }
        return filteredPegawais;
    }
    
    

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public char getPegawaiGroup(Pegawai pegawai) {
        return pegawai.getNama().charAt(0);
    }

}
