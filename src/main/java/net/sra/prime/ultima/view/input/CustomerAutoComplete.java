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
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.service.ServiceCustomer;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author hairian
 */
@Named("customerAutoComplete")
@ViewScoped
@Getter
@Setter
public class CustomerAutoComplete implements java.io.Serializable {

    @Autowired
    ServiceCustomer serviceCustomer;

    List<Customer> allCustomer;
    List<Customer> selectedCustomer;
    Customer customer;
    String completeCustomerContains;
    private String id_admin;

    @PostConstruct
    public void init() {
        customer = new Customer();
    }

    @Produces
    public List<Customer> completeCustomer(String query) {
        allCustomer = serviceCustomer.selectAll();
        List<Customer> filteredCustomers = new ArrayList<>();
    for (int i = 0; i < allCustomer.size(); i++) {
            Customer pilih = allCustomer.get(i);
            if (pilih.getId_kontak().toLowerCase().contains(query.toLowerCase()) || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredCustomers.add(pilih);
            }
        }

        return filteredCustomers;
    }

    @Produces
    public List<Customer> completeCustomerContains(String query) {
        allCustomer = serviceCustomer.selectAll();
        List<Customer> filteredCustomers = new ArrayList<>();

        for (int i = 0; i < allCustomer.size(); i++) {
            Customer pilih = allCustomer.get(i);
            if (pilih.getId_kontak().toLowerCase().contains(query.toLowerCase()) || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredCustomers.add(pilih);
            }
        }

        return filteredCustomers;
    }
    
    @Produces
    public List<Customer> completeCustomerBySalesAdmin(String query) {
        allCustomer = serviceCustomer.selectAllBySalesAdmin(id_admin);
        List<Customer> filteredCustomers = new ArrayList<>();

        for (int i = 0; i < allCustomer.size(); i++) {
            Customer pilih = allCustomer.get(i);
            if (pilih.getId_kontak().toLowerCase().contains(query.toLowerCase()) || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredCustomers.add(pilih);
            }
        }

        return filteredCustomers;
    }
    
    @Produces
    public List<Customer> completeCustomerContainsAll(String query) {
        allCustomer = serviceCustomer.selectAll2();
        List<Customer> filteredCustomers = new ArrayList<>();

        for (int i = 0; i < allCustomer.size(); i++) {
            Customer pilih = allCustomer.get(i);
            if (pilih.getId_kontak().toLowerCase().contains(query.toLowerCase()) || pilih.getCustomer().toLowerCase().contains(query.toLowerCase())) {
                filteredCustomers.add(pilih);
            }
        }

        return filteredCustomers;
    }

    public void onItemSelect(SelectEvent event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));
    }

    public char getCustomerGroup(Customer customer) {
        return customer.getCustomer().charAt(0);
    }

}
