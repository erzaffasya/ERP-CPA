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
package net.sra.prime.ultima.controller.hr;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.controller.finance.ControllerProposalLoanPaid;
import net.sra.prime.ultima.entity.hr.PayrollOvertime;
import net.sra.prime.ultima.entity.hr.PayrollOvertimeDetail;
import net.sra.prime.ultima.service.hr.ServicePayrollOvertime;
import net.sra.prime.ultima.view.input.LoanTypeAutoComplete;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPayrollOvertime implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePayrollOvertime servicePayrollOvertime;
    private PayrollOvertime item;
    private PayrollOvertimeDetail itemdetail;
    private List<PayrollOvertime> lPayrollOvertime = new ArrayList<>();
    private List<PayrollOvertimeDetail> lPayrollOvertimeDetail = new ArrayList<>();
    private String year;
    private Integer month;
    private Character status;
    private Date datestart;
    private Double total;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @Inject
    private LoanTypeAutoComplete loanTypeAutoComplete;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        item = new PayrollOvertime();

    }

    public void initItem() {
        item = new PayrollOvertime();
        item.setStatus('D');
        pegawaiAutoComplete.setPegawai(null);
        loanTypeAutoComplete.setLoanType(null);
        lPayrollOvertimeDetail = new ArrayList<>();
        datestart = new Date();
        total=0.00;
    }

    public void onLoadList(String jenis) {
        lPayrollOvertime = servicePayrollOvertime.onLoadList(jenis);
    }

    public List<PayrollOvertime> getDataPayrollOvertime() {
        return lPayrollOvertime;
    }

    public List<PayrollOvertimeDetail> getDataPayrollOvertimeDetail() {
        return lPayrollOvertimeDetail;
    }

    public void delete(String tahun, Integer bulan,String jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePayrollOvertime.delete(tahun, bulan,jenis);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah(Character st,String jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setJenis(jenis);
            item.setTotal(total);
            item.setStatus(st);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            DateFormat thn = new SimpleDateFormat("yyyy");
            DateFormat bln = new SimpleDateFormat("MM");
            String tahun = thn.format(datestart);
            String bulan = bln.format(datestart);
            item.setMonth(Integer.parseInt(bulan));
            item.setYear(tahun);
            servicePayrollOvertime.tambah(item, lPayrollOvertimeDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?year=" + item.getYear() + "&month=" + item.getMonth());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        // item = servicePayrollOvertime.onLoad(year, month);
        context.getExternalContext().redirect("./edit.jsf?year=" + item.getYear() + "&month=" + item.getMonth());

    }

    
    public void onLoad(String jenis) {
        item = servicePayrollOvertime.onLoad(item.getYear(), item.getMonth(),jenis);
        if (item.getTotal() == null) {
            total = 0.00;
        }else{
            total = item.getTotal();
        }
        lPayrollOvertimeDetail = servicePayrollOvertime.onLoadListDetail(item.getYear(), item.getMonth(),item.getJenis());
        try {
            String tgl = "01/" + Integer.toString(item.getMonth()) + "/" + item.getYear();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            datestart = format.parse(tgl);
        } catch (ParseException ex) {
            Logger.getLogger(ControllerProposalLoanPaid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ubah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setTotal(total);
            item.setStatus(st);
            item.setModified_by(page.getMyPegawai().getId_pegawai());
            servicePayrollOvertime.ubah(item, lPayrollOvertimeDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onDeleteClicked(PayrollOvertimeDetail payrollOvertimeDetail) {
        lPayrollOvertimeDetail.remove(payrollOvertimeDetail);
        calculateTotal();
    }

    public void extend() {
        lPayrollOvertimeDetail.add(new PayrollOvertimeDetail());
    }

    public void onPegawaiSelect(PayrollOvertimeDetail pm, Integer i) {
        pm.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        pm.setNama(pegawaiAutoComplete.getPegawai().getNama());
        lPayrollOvertimeDetail.set(i, pm);
    }

    public void calculateTotal() {
        total = 0.00;
        for (int i = 0; i < lPayrollOvertimeDetail.size(); i++) {
            total += lPayrollOvertimeDetail.get(i).getAmount();
        }

    }

    public String getFooter() {
        return new DecimalFormat("###,###.##").format(total);
    }

}
