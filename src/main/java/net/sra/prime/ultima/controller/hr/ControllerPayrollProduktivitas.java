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
import net.sra.prime.ultima.entity.hr.PayrollProduktivitas;
import net.sra.prime.ultima.entity.hr.PayrollProduktivitasDetail;
import net.sra.prime.ultima.service.hr.ServicePayrollProduktivitas;
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
public class ControllerPayrollProduktivitas implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePayrollProduktivitas servicePayrollProduktivitas;
    private PayrollProduktivitas item;
    private PayrollProduktivitasDetail itemdetail;
    private List<PayrollProduktivitas> lPayrollProduktivitas = new ArrayList<>();
    private List<PayrollProduktivitasDetail> lPayrollProduktivitasDetail = new ArrayList<>();
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
        item = new PayrollProduktivitas();

    }

    public void initItem() {
        item = new PayrollProduktivitas();
        item.setStatus('D');
        pegawaiAutoComplete.setPegawai(null);
        loanTypeAutoComplete.setLoanType(null);
        lPayrollProduktivitasDetail = new ArrayList<>();
        datestart = new Date();
        total=0.00;
    }

    public void onLoadList() {
        lPayrollProduktivitas = servicePayrollProduktivitas.onLoadList();
    }

    public List<PayrollProduktivitas> getDataPayrollProduktivitas() {
        return lPayrollProduktivitas;
    }

    public List<PayrollProduktivitasDetail> getDataPayrollProduktivitasDetail() {
        return lPayrollProduktivitasDetail;
    }

    public void delete(String tahun, Integer bulan) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePayrollProduktivitas.delete(tahun, bulan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setTotal(total);
            item.setStatus(st);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            DateFormat thn = new SimpleDateFormat("yyyy");
            DateFormat bln = new SimpleDateFormat("MM");
            String tahun = thn.format(datestart);
            String bulan = bln.format(datestart);
            item.setMonth(Integer.parseInt(bulan));
            item.setYear(tahun);
            servicePayrollProduktivitas.tambah(item, lPayrollProduktivitasDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?year=" + item.getYear() + "&month=" + item.getMonth());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        // item = servicePayrollProduktivitas.onLoad(year, month);
        context.getExternalContext().redirect("./edit.jsf?year=" + item.getYear() + "&month=" + item.getMonth());

    }

    public void onLoad() {
        item = servicePayrollProduktivitas.onLoad(item.getYear(), item.getMonth());
        if (item.getTotal() == null) {
            total = 0.00;
        }else{
            total = item.getTotal();
        }
        lPayrollProduktivitasDetail = servicePayrollProduktivitas.onLoadListDetail(item.getYear(), item.getMonth());
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
            servicePayrollProduktivitas.ubah(item, lPayrollProduktivitasDetail);
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

    public void onDeleteClicked(PayrollProduktivitasDetail payrollProduktivitasDetail) {
        lPayrollProduktivitasDetail.remove(payrollProduktivitasDetail);
        calculateTotal();
    }

    public void extend() {
        lPayrollProduktivitasDetail.add(new PayrollProduktivitasDetail());
    }

    public void onPegawaiSelect(PayrollProduktivitasDetail pm, Integer i) {
        pm.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        pm.setNama(pegawaiAutoComplete.getPegawai().getNama());
        lPayrollProduktivitasDetail.set(i, pm);
    }

    public void calculateTotal() {
        total = 0.00;
        for (int i = 0; i < lPayrollProduktivitasDetail.size(); i++) {
            total += lPayrollProduktivitasDetail.get(i).getAmount();
        }

    }

    public String getFooter() {
        return new DecimalFormat("###,###.##").format(total);
    }

}
