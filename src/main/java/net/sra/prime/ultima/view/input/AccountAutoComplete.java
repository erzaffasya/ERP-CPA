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
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.service.ServiceAccount;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named("accountAutoComplete")
@ConversationScoped
@Getter
@Setter
public class AccountAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceAccount referensi;

    List<Account> allAccount;
    List<Account> selectedAccount;
    Account account;
    String completeAccountContains;
    
    private Integer batas_atas;
    private Integer batas_bawah;

    @PostConstruct
    public void init() {
        account = new Account();
    }

    @Produces
    public List<Account> completeAccount(String query) {
        List<Account> filteredAccounts = new ArrayList<>();

        for (int i = 0; i < allAccount.size(); i++) {
            Account pilih = allAccount.get(i);
            if (pilih.getAccount().toLowerCase().contains(query.toLowerCase())) {
                filteredAccounts.add(pilih);
            }
        }

        return filteredAccounts;
    }

    @Produces
    public List<Account> completeAccountContains(String query) {
        
        allAccount = referensi.selectByCategory(batas_bawah, batas_atas);
        List<Account> filteredAccounts = new ArrayList<>();
        for (int i = 0; i < allAccount.size(); i++) {
            Account pilih = allAccount.get(i);
            if (pilih.getAccount().toLowerCase().contains(query.toLowerCase())
                    || Integer.toString(pilih.getId_account()).toLowerCase().contains(query.toLowerCase())) {
                filteredAccounts.add(pilih);
            }
        }

        return filteredAccounts;
    }

    @Produces
    public List<Account> completeAllAccountContains(String query) {
        allAccount = referensi.selectAllAccount();
        List<Account> filteredAccounts = new ArrayList<>();
        for (int i = 0; i < allAccount.size(); i++) {
            Account pilih = allAccount.get(i);
            if (pilih.getAccount().toLowerCase().contains(query.toLowerCase())
                    || Integer.toString(pilih.getId_account()).toLowerCase().contains(query.toLowerCase())) {
                filteredAccounts.add(pilih);
            }
        }
        return filteredAccounts;
    }
    
    @Produces
    public List<Account> completeAccountL4Contains(String query) {
        allAccount = referensi.selectLevel(4);
        List<Account> filteredAccounts = new ArrayList<>();
        for (int i = 0; i < allAccount.size(); i++) {
            Account pilih = allAccount.get(i);
            if (pilih.getAccount().toLowerCase().contains(query.toLowerCase())
                    || Integer.toString(pilih.getId_account()).toLowerCase().contains(query.toLowerCase())) {
                filteredAccounts.add(pilih);
            }
        }
        
        return filteredAccounts;
    }
    
    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public char getAccountGroup(Account account) {
        return account.getAccount().charAt(0);
    }

}
