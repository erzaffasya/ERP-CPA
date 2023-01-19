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
package net.sra.prime.ultima.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.KategoriCustomer;
import net.sra.prime.ultima.service.ServiceCustomer;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKategoriCustomer implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceCustomer serviceCustomer;
    private KategoriCustomer sKategoriCustomer;
    private List<KategoriCustomer> lKategoriCustomer = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            lKategoriCustomer = serviceCustomer.onLoadListKategori();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<KategoriCustomer> getDataKategoriCustomer() {
        return lKategoriCustomer;
    }

    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        int id_kategori_customer = Integer.parseInt(id);
        try {

            serviceCustomer.deleteKategori(id_kategori_customer);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            // e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

}
