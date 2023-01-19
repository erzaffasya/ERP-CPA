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
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.service.ServicePo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;


@Named("poAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PoAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePo servicePo;        
    
    
    List<Po> allPo;
    List<Po> selectedPo;
    Po po;
    String jenis_po;
    String id_gudang;
    String id_pegawai;
    String completePoContains;

    @PostConstruct
    public void init() {
        po = new Po();
    }

    @Produces
    public List<Po> completePo(String query) {
        List<Po> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPo.size(); i++) {
            Po pilih = allPo.get(i);
            if (pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    @Produces
    public List<Po> completePoContains(String query) {
        allPo = servicePo.selectPoSupplier(jenis_po);
        List<Po> filteredPos = new ArrayList<>();
        for (int i = 0; i < allPo.size(); i++) {
            Po pilih = allPo.get(i);
            if (pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }
    
    
    @Produces
    public List<Po> completePoRegulerContains(String query) {
        allPo = servicePo.selectPoRegulerSupplier();
        List<Po> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPo.size(); i++) {
            Po pilih = allPo.get(i);
            if (pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }
    
    @Produces
    public List<Po> completePoForPenerimaan(String query) {
        allPo = servicePo.selectPoForPenerimaan();
        List<Po> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPo.size(); i++) {
            Po pilih = allPo.get(i);
            if (pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }
    
 
   @Produces
    public List<Po> completePoSisaContains(String query) {
        allPo = servicePo.selectPoSisaSupplier(id_pegawai);
        List<Po> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPo.size(); i++) {
            Po pilih = allPo.get(i);
            if (pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())
                    || pilih.getNama_supplier().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
