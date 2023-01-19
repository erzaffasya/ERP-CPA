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
import net.sra.prime.ultima.db.mapper.MapperPenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudang;
import net.sra.prime.ultima.service.ServicePenerimaanGudang;
import org.apache.ibatis.session.SqlSession;

import org.apache.ibatis.session.SqlSessionFactory;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("penerimaanGudangAutoComplete")
@ConversationScoped
@Getter
@Setter
public class PenerimaanGudangAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePenerimaanGudang servicePenerimaanGudang;

    List<PenerimaanGudang> allPenerimaan;
    List<PenerimaanGudang> selectedPenerimaan;
    PenerimaanGudang penerimaan;
    String id_supplier;
    String id_perusahaan;
    String completePenerimaanContains;

    @PostConstruct
    public void init() {
        penerimaan = new PenerimaanGudang();
    }

    @Produces
    public List<PenerimaanGudang> completePenerimaan(String query) {
        List<PenerimaanGudang> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenerimaan.size(); i++) {
            PenerimaanGudang pilih = allPenerimaan.get(i);
            if (pilih.getNo_penerimaan().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    @Produces
    public List<PenerimaanGudang> completePenerimaanContains(String query) {
        
       // allPenerimaan = mapper.selectAll(id_perusahaan);
        List<PenerimaanGudang> filteredPos = new ArrayList<>();

        for (int i = 0; i < allPenerimaan.size(); i++) {
            PenerimaanGudang pilih = allPenerimaan.get(i);
            if (pilih.getNo_penerimaan().toLowerCase().contains(query.toLowerCase())
                    || pilih.getReferensi().toLowerCase().contains(query.toLowerCase())
                    || pilih.getNomor_po().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
