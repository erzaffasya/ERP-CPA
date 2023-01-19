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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerAccount;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import java.io.File;
import javax.faces.context.ExternalContext;
import net.sra.prime.ultima.entity.CustomerMobil;
import net.sra.prime.ultima.service.ServiceCustomer;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerCustomer implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceCustomer serviceCustomer;
    
    private Customer item;
    private CustomerPengiriman itempengiriman;
    private CustomerAccount itemaccount;
    private CustomerPengiriman Cp;
    private List<Customer> filteredCustomers;
    public List<Customer> lCustomer = new ArrayList<>();
    public List<CustomerPengiriman> lCustomerPengiriman = new ArrayList<>();
    private CustomerKontak itemCustomerKontak;
    private CustomerKontak Ck;
    private CustomerAccount Ca;
    private List<CustomerKontak> lCustomerKontak;
    private List<CustomerAccount> lCustomerAccount;
    private List<CustomerMobil> lCustomerMobil = new ArrayList<>();
    private Integer tmp;
    private Boolean status;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Options options;

    private ExcelOptions excelOpt;

    private PDFOptions pdfOpt;

    @PostConstruct
    public void init() {
        item = new Customer();
        status=true;
        itempengiriman = new CustomerPengiriman();
        itemCustomerKontak = new CustomerKontak();
        itemaccount = new CustomerAccount();
        Ck = new CustomerKontak();
        Ca = new CustomerAccount();

        this.customizationOptions();
    }

    public void initItem() {
        lCustomerPengiriman = serviceCustomer.selectAllpengiriman(item.getId_kontak());
        lCustomerKontak = serviceCustomer.selectAllKontak(item.getId_kontak());
        lCustomerAccount = new ArrayList<>();
        item.setIsaktif(Boolean.TRUE);
        accountAutoComplete.setBatas_bawah(10200000);
        accountAutoComplete.setBatas_atas(10300000);
        accountAutoComplete.setAccount(null);
        lCustomerMobil.add(new CustomerMobil());
        
    }

    public void onLoadList() {
        try {
            item = new Customer();
            lCustomer.add(item);
            lCustomer = serviceCustomer.onLoadList(status);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Customer> getDataCustomer() {
        return lCustomer;
    }

    public List<CustomerPengiriman> getDataCustomerPengiriman() {
        return lCustomerPengiriman;
    }

    public List<CustomerKontak> getDataCustomerKontak() {
        return lCustomerKontak;
    }

    public List<CustomerAccount> getDataCustomerAccount() {
        return lCustomerAccount;
    }
    
    public List<CustomerMobil> getDataCustomerMobil(){
        return lCustomerMobil;
    }

    public void delete(String id_kontak) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceCustomer.delete(id_kontak);
            context.getExternalContext().redirect("./list.jsf");
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCustomer.tambah(item, lCustomerKontak, lCustomerAccount, lCustomerPengiriman);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_kontak());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_kontak());
    }

    public void onLoad() {
        Ck = new CustomerKontak();
        Ca = new CustomerAccount();
        String id = item.getId_lama();
        item = serviceCustomer.selectOne(item.getId_lama());
        item.setId_lama(id);
        lCustomerPengiriman = serviceCustomer.selectAllpengiriman(id);
        lCustomerKontak = serviceCustomer.selectAllKontak(id);
        lCustomerAccount = serviceCustomer.selectCustomerAccount(id);
        accountAutoComplete.setBatas_bawah(10200000);
        accountAutoComplete.setBatas_atas(10300000);
        accountAutoComplete.setAccount(null);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceCustomer.ubah(item, lCustomerKontak, lCustomerAccount, lCustomerPengiriman);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
     
    }

    public void maketoFalse() {
        if (itempengiriman.getUtama()) {
            for (int i = 0; i < lCustomerPengiriman.size(); i++) {
                CustomerPengiriman tmppengiriman = new CustomerPengiriman();
                tmppengiriman.setAlamat(lCustomerPengiriman.get(i).getAlamat());
                tmppengiriman.setUtama(Boolean.FALSE);
                lCustomerPengiriman.set(i, tmppengiriman);
            }
        }
    }

    public void extend() {
        this.maketoFalse();
        if (tmp != null) {
            lCustomerPengiriman.set(tmp, itempengiriman);
            tmp = null;
        } else {
            lCustomerPengiriman.add(itempengiriman);
        }
        itempengiriman = new CustomerPengiriman();
    }

    public void extendAccount() {
        lCustomerAccount.add(new CustomerAccount());
    }

    public void extendKontak() {
        lCustomerKontak.add(new CustomerKontak());
    }

    public void extendCustomerMobil(){
        lCustomerMobil.add(new CustomerMobil());
    }
    
    public void onDeleteClicked(CustomerPengiriman hapus) {
        lCustomerPengiriman.remove(hapus);
    }

    public void onDeleteKontakClicked(CustomerKontak hapus) {
        lCustomerKontak.remove(hapus);
    }

    public void onDeleteAccountClicked(CustomerAccount hapus) {
        lCustomerAccount.remove(hapus);
    }

    public void onEditClicked(CustomerPengiriman edit, Integer i) {
        itempengiriman.setAlamat(edit.getAlamat());
        itempengiriman.setUtama(edit.getUtama());
        tmp = i;
    }

    public void onKantorSelected(CustomerAccount s, Integer i) {
        InternalKantorCabang internalKantorCabang = serviceCustomer.selectoneKantor(s.getId_kantor());
        s.setId_kantor(internalKantorCabang.getId_kantor_cabang());
        s.setNm_kantor(internalKantorCabang.getNama());
    }

    public void onAccountSelected(CustomerAccount s, Integer i) {
        s.setId_account(accountAutoComplete.getAccount().getId_account());
        s.setNm_account(accountAutoComplete.getAccount().getAccount());
    }

    public void customizationOptions() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F88017");
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");

        pdfOpt = new PDFOptions();

        pdfOpt.setFacetBgColor("#F88017");
        pdfOpt.setFacetFontColor("#0000ff");
        pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("12");
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String logo = externalContext.getRealPath("") + File.separator + "resources" + File.separator + "ultima-layout" + File.separator + "images" + File.separator + "avatar1.png";

        pdf.add(Image.getInstance(logo));

    }

}
