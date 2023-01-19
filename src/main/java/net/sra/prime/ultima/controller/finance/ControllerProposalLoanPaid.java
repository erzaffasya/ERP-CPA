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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaid;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetail;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetailManual;
import net.sra.prime.ultima.service.finance.ServiceProposalLoanPaid;
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
public class ControllerProposalLoanPaid implements java.io.Serializable {
    
    private static final long serialVersionUID = 8811960521862002964L;
    
    @Autowired
    private ServiceProposalLoanPaid serviceProposalLoanPaid;
    private ProposalLoanPaid item;
    private ProposalLoanPaidDetail itemdetail;
    private ProposalLoanPaidDetailManual itemdetailManual;
    private List<ProposalLoanPaid> lProposalLoanPaid = new ArrayList<>();
    private List<ProposalLoanPaidDetail> lProposalLoanPaidDetail = new ArrayList<>();
    private List<ProposalLoanPaidDetailManual> lProposalLoanPaidDetailManual = new ArrayList<>();
    private Date datestart;
    private Character status;
    
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
        item = new ProposalLoanPaid();
        
    }
    
    public void initItem() {
        item = new ProposalLoanPaid();
        item.setDate(new Date());
        item.setStatus('D');
        item.setDate(new Date());
        item.setJenis(page.getValueBegawi("loanpaid"));
        if (item.getJenis().equals('M')) {
            pegawaiAutoComplete.setPegawai(null);
            loanTypeAutoComplete.setLoanType(null);
            lProposalLoanPaidDetailManual = new ArrayList<>();
        } else {
            lProposalLoanPaidDetail = new ArrayList<>();
        }
        
    }
    
    public void onLoadList() {
        lProposalLoanPaid = serviceProposalLoanPaid.onLoadList();
    }
    
    public List<ProposalLoanPaid> getDataProposalLoanPaid() {
        return lProposalLoanPaid;
    }
    
    public List<ProposalLoanPaidDetail> getDataProposalLoanPaidDetail() {
        return lProposalLoanPaidDetail;
    }
    
    public List<ProposalLoanPaidDetailManual> getDataProposalLoanPaidDetailMAnual() {
        return lProposalLoanPaidDetailManual;
    }
    
    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceProposalLoanPaid.delete(id);
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
            item.setStatus(st);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            DateFormat thn = new SimpleDateFormat("yyyy");
            DateFormat bln = new SimpleDateFormat("MM");
            String tahun = thn.format(datestart);
            String bulan = bln.format(datestart);
            item.setMonth(Integer.parseInt(bulan));
            item.setYear(tahun);
            if (item.getJenis().equals('O')) {
                serviceProposalLoanPaid.tambah(item, lProposalLoanPaidDetail);
            } else {
                serviceProposalLoanPaid.tambahManual(item, lProposalLoanPaidDetailManual);
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            if (item.getJenis().equals('O')) {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_proposal_loan_paid());
            } else if (item.getJenis().equals('M')) {
                context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getId_proposal_loan_paid());
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        item = serviceProposalLoanPaid.onLoad(item.getId_proposal_loan_paid());
        if (item.getJenis().equals('O')) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_proposal_loan_paid());
        } else if (item.getJenis().equals('M')) {
            context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getId_proposal_loan_paid());
        }
    }
    
    public void onLoad() {
        item = serviceProposalLoanPaid.onLoad(item.getId_proposal_loan_paid());
        if (item.getJenis().equals('O')) {
            lProposalLoanPaidDetail = serviceProposalLoanPaid.onLoadListDetail(item.getId_proposal_loan_paid());
        } else if (item.getJenis().equals('M')) {
            lProposalLoanPaidDetailManual = serviceProposalLoanPaid.onLoadListDetailManual(item.getId_proposal_loan_paid());
        }
        
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
            item.setStatus(st);
            if (item.getJenis().equals('O')) {
                serviceProposalLoanPaid.ubah(item, lProposalLoanPaidDetail);
            } else if (item.getJenis().equals('M')) {
                serviceProposalLoanPaid.ubahManual(item, lProposalLoanPaidDetailManual);
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

//    public void selectPegawai() {
//        item.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
//        item.setNip(pegawaiAutoComplete.getPegawai().getNip());
//    }
    public void onRowEdit(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
//        item = ((Absen) event.getObject());
//        if (!item.getId_status_absen().equals('c') && !item.getId_status_absen().equals('p')) {
//            try {
//                Pegawai pegawai = servicePegawai.onLoadDataDiri(item.getId_pegawai());
//                item.setId_absen(pegawai.getAbsen());
//                Absen absen = serviceAbsen.selectOne(item.getId_absen(), item.getTanggal());
//                if (absen != null) {
//                    serviceAbsen.update(item);
//                } else {
//                    item.setUrut(0);
//                    serviceAbsen.insertOne(item);
//
//                }
//                onAttendaceClocks();
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data berhasil diedit", ""));
//            } catch (Exception e) {
//                e.printStackTrace();
//                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
//            }
//        } else {
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Untuk cuti tidak bisa diubah melalui menu ini !!! ", ""));
//        }
    }
    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    /**
     * pembayaran pinjaman karyawan diambil dari tabel loan yang remain > 0
     * kemudian dicari di loan detail jumlah cicilan pada periode tsb
     */
    public void selectPeriode() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat thn = new SimpleDateFormat("yyyy");
        DateFormat bln = new SimpleDateFormat("MM");
        String tahun = thn.format(datestart);
        String bulan = bln.format(datestart);
        if (serviceProposalLoanPaid.selectPeriodeProposal(Integer.parseInt(bulan), tahun) == null) {
            lProposalLoanPaidDetail = new ArrayList<>();
            List<Loan> lLoan = serviceProposalLoanPaid.selectRemaingLoan(addOneMonth(datestart));
            LoanDetail loanDetail = new LoanDetail();
            ProposalLoanPaidDetail detail = new ProposalLoanPaidDetail();
            for (int i = 0; i < lLoan.size(); i++) {
                detail = new ProposalLoanPaidDetail();
                detail.setId_pegawai(lLoan.get(i).getId_pegawai());
                detail.setNama(lLoan.get(i).getNama());
                detail.setLoan_number(lLoan.get(i).getLoan_number());
                detail.setLoan_type(lLoan.get(i).getLoan_type());
                detail.setId_loan(lLoan.get(i).getId_loan());
                detail.setId_loan_type(lLoan.get(i).getId_loan_type());
                loanDetail = serviceProposalLoanPaid.selectRemaingLoanDetail(Integer.parseInt(bulan), Integer.parseInt(tahun), lLoan.get(i).getId_loan());
                if (loanDetail != null) {
                    if (loanDetail.getInstallment_amount() <= lLoan.get(i).getRemain()) {
                        detail.setAmount(loanDetail.getInstallment_amount());
                    } else {
                        detail.setAmount(lLoan.get(i).getRemain());
                    }
                    detail.setDescription("Cicilan ke " + loanDetail.getSequence());
                } else {
                    detail.setAmount(lLoan.get(i).getRemain());
                    detail.setDescription("Sisa cicilan");
                }
                lProposalLoanPaidDetail.add(detail);
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pembayaran pinjaman karyawan periode " + bulan + "-" + tahun + " sudah ada !!!", ""));
        }
    }
    
    public static Date addOneMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }
    
    public void onDeleteClicked(ProposalLoanPaidDetail proposalLoanPaidDetail) {
        lProposalLoanPaidDetail.remove(proposalLoanPaidDetail);
    }
    
    public void extend() {
        lProposalLoanPaidDetail.add(new ProposalLoanPaidDetail());
    }
    
    public void onDeleteClickedManual(ProposalLoanPaidDetailManual proposalLoanPaidDetailManual) {
        lProposalLoanPaidDetailManual.remove(proposalLoanPaidDetailManual);
    }
    
    public void extendManual() {
        lProposalLoanPaidDetailManual.add(new ProposalLoanPaidDetailManual());
    }
    
    public void onPegawaiSelect(ProposalLoanPaidDetailManual pm, Integer i) {
        pm.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        pm.setNama(pegawaiAutoComplete.getPegawai().getNama());
        lProposalLoanPaidDetailManual.set(i, pm);
    }
    
    public void onTypeSelect(ProposalLoanPaidDetailManual pm, Integer i) {
        pm.setId_loan_type(loanTypeAutoComplete.getLoanType().getId_loan_type());
        pm.setLoan_type(loanTypeAutoComplete.getLoanType().getLoan_type());
        lProposalLoanPaidDetailManual.set(i, pm);
    }
}
