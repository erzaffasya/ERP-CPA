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
package net.sra.prime.ultima.controller.finance;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.finance.LoanType;
import net.sra.prime.ultima.service.finance.ServiceLoan;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerLoan implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceLoan serviceLoan;
    private Loan item;
    private LoanDetail itemdetail;
    private List<Loan> lLoan = new ArrayList<>();
    private List<LoanDetail> lLoanDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private Double total;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @PostConstruct
    public void init() {
        item = new Loan();

    }

    public void initItem() {
        item = new Loan();
        item.setDate(new Date());
        item.setStatus('D');
        pegawaiAutoComplete.setPegawai(null);
        total = 0.00;
        lLoanDetail = new ArrayList<>();
    }

    public void onLoadList() {
        lLoan = serviceLoan.onLoadList();
    }

    public List<Loan> getDataLoan() {
        return lLoan;
    }

    public List<LoanDetail> getDataLoanDetail() {
        return lLoanDetail;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLoan.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (total.equals(item.getAmount())) {
            try {
                nomorurut();
                item.setStatus(st);
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setInterest(0.00);
                serviceLoan.tambah(item, lLoanDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_loan());
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Total cicilan dan pinjaman tidak sama !!!", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_loan());
    }

    public void onLoad() {
        item = serviceLoan.onLoad(item.getId_loan());
        lLoanDetail = serviceLoan.onLoadListDetail(item.getId_loan());
        Pegawai pegawai = serviceLoan.selectOnePegawai(item.getId_pegawai());
        pegawaiAutoComplete.setPegawai(pegawai);

    }

    public void ubah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(st);
            serviceLoan.ubah(item, lLoanDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getOptionLoanType() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<LoanType> list = serviceLoan.onLoadListLoanType();
            list.stream().forEach((data) -> {
                map.put(data.getLoan_type(), Integer.toString(data.getId_loan_type()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void selectPegawai() {
        item.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        item.setNip(pegawaiAutoComplete.getPegawai().getNip());
    }

    public void hitungCicilan() {
        lLoanDetail = new ArrayList<>();
        total = item.getAmount();
        if (item.getAmount() != null && item.getInstallment() != null && item.getPayment_date() != null && item.getInstallment() > 0) {
            Date tanggal = item.getPayment_date();
            for (int i = 1; i <= item.getInstallment(); i++) {
                itemdetail = new LoanDetail();
                itemdetail.setSequence(i);
                itemdetail.setInstallment_amount(item.getAmount() / item.getInstallment());
                itemdetail.setOverdue(tanggal);
                lLoanDetail.add(itemdetail);
                tanggal = addOneMonth(tanggal);
            }
        }
    }

    public static Date addOneMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yyyy");
        String tahun = thn.format(item.getDate());
        String noMax = serviceLoan.selectMax(Integer.parseInt(tahun));
        if (noMax == null) {
            item.setLoan_number(tahun + "00001");
        } else {
            item.setLoan_number(tahun + String.format("%05d", Integer.parseInt(noMax) + 1));
        }
    }

    public void hitungTotal() {
        total = 0.00;
        for (int i = 0; i < lLoanDetail.size(); i++) {
            total = total + lLoanDetail.get(i).getInstallment_amount();
        }

    }
}
