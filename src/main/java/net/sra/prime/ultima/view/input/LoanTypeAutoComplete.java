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
import net.sra.prime.ultima.entity.finance.LoanType;
import net.sra.prime.ultima.service.finance.ServiceLoan;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;


@Named("loanTypeAutoComplete")
@ConversationScoped
@Getter
@Setter
public class LoanTypeAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceLoan serviceLoan;        
    
    
    List<LoanType> allLoanType;
    List<LoanType> selectedLoanType;
    LoanType loanType;
    
    @PostConstruct
    public void init() {
        loanType = new LoanType();
    }

    @Produces
    public List<LoanType> completeLoanType(String query) {
        List<LoanType> filteredLoanTypes = new ArrayList<>();

        for (int i = 0; i < allLoanType.size(); i++) {
            LoanType pilih = allLoanType.get(i);
            if (pilih.getLoan_type().toLowerCase().contains(query.toLowerCase())) {
                filteredLoanTypes.add(pilih);
            }
        }

        return filteredLoanTypes;
    }

    @Produces
    public List<LoanType> completeLoanTypeContains(String query) {
        allLoanType = serviceLoan.onLoadListLoanType();
        List<LoanType> filteredLoanTypes = new ArrayList<>();
        for (int i = 0; i < allLoanType.size(); i++) {
            LoanType pilih = allLoanType.get(i);
            if (pilih.getLoan_type().toLowerCase().contains(query.toLowerCase())) {
                filteredLoanTypes.add(pilih);
            }
        }
        return filteredLoanTypes;
    }
    
    
    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

}
