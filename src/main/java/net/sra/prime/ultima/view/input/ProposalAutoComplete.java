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
import net.sra.prime.ultima.entity.AccProposalApDetail;
import net.sra.prime.ultima.service.ServicePembayaranHutang;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jasamedika
 */
@Named("proposalAutoComplete")
@ConversationScoped
@Getter
@Setter
public class ProposalAutoComplete implements java.io.Serializable {

    @Autowired
    ServicePembayaranHutang servicePembayaranHutang;

    List<AccProposalApDetail> allAccProposalApDetail;
    List<AccProposalApDetail> selectedAccProposalApDetail;
    AccProposalApDetail accProposalApDetail;
    String vendor_code;
    String completeAccProposalApDetailContains;

    @PostConstruct
    public void init() {
        accProposalApDetail = new AccProposalApDetail();
    }

    @Produces
    public List<AccProposalApDetail> completeAccProposalApDetail(String query) {
        List<AccProposalApDetail> filteredPos = new ArrayList<>();

        for (int i = 0; i < allAccProposalApDetail.size(); i++) {
            AccProposalApDetail pilih = allAccProposalApDetail.get(i);
            if (pilih.getAp_number().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }

        return filteredPos;
    }

    @Produces
    public List<AccProposalApDetail> completeAccProposalApDetailContains(String query) {
        allAccProposalApDetail = servicePembayaranHutang.selectAllDetailPayment(vendor_code);
        List<AccProposalApDetail> filteredPos = new ArrayList<>();

        for (int i = 0; i < allAccProposalApDetail.size(); i++) {
            AccProposalApDetail pilih = allAccProposalApDetail.get(i);
            if (pilih.getAp_number().toLowerCase().contains(query.toLowerCase())
                    || pilih.getSupplier().toLowerCase().contains(query.toLowerCase())
                    || pilih.getNo_invoice().toLowerCase().contains(query.toLowerCase())) {
                filteredPos.add(pilih);
            }
        }
        return filteredPos;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
