package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.service.ServiceBukuBesar;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerBukuBesar implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccGlDetail> lAccGlDetails = new ArrayList<>();
    private AccGlDetail item;
    private Integer bulan;
    private String tahun;
    private ExcelOptions excelOpt;
    private PDFOptions pdfOpt;
    private Date awal;
    private Date akhir;
    private Integer accountFrom;
    private Integer accountTo;
    private String accountFromdesc;
    private String accountTodesc;
    private String keterangan;
    private LinkedHashMap<Integer, Double> nilaiplus;
    private LinkedHashMap<Integer, Double> nilaimin;
    private LinkedHashMap<Integer, Double> nilaitotal;

    @Autowired
    ServiceBukuBesar serviceBukuBesar;

    @Inject
    private Page page;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @PostConstruct
    public void init() {
        initItem();
        customizationOptions();
    }

    private void initItem() {
        awal = new Date();
        akhir = new Date();
        item = new AccGlDetail();
        nilaiplus = new LinkedHashMap<Integer, Double>();
        nilaimin = new LinkedHashMap<Integer, Double>();
        nilaitotal = new LinkedHashMap<Integer, Double>();
        //accountAutoComplete.setAccount(new Account());
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (accountFrom == null &&  (keterangan == null || keterangan.equals(""))) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Isi Pencarian !!!!!", ""));
        } else {
            try {
                Double totalcredit = 0.00;
                Double totaldebet = 0.00;
                Double saldo = 0.00;

                lAccGlDetails = serviceBukuBesar.onLoadAllDetail(awal, akhir, accountFrom, accountTo, keterangan);
                Integer pembanding = 0;
                for (int i = 0; i < lAccGlDetails.size(); i++) {

                    AccGlDetail accGlDetail = lAccGlDetails.get(i);
                    if (!pembanding.equals(accGlDetail.getId_account())) {
                        saldo = 0.00;
                        if (!pembanding.equals(0)) {
                            nilaiplus.put(pembanding, totaldebet);
                            nilaimin.put(pembanding, totalcredit);
                            nilaitotal.put(pembanding, totaldebet - totalcredit);
                        }
                        pembanding = accGlDetail.getId_account();

                        totalcredit = 0.00;
                        totaldebet = 0.00;
                    }

                    if (lAccGlDetails.get(i).getIs_debit()) {
                        accGlDetail.setDebit(lAccGlDetails.get(i).getValue());
                        totaldebet = totaldebet + lAccGlDetails.get(i).getValue();
                        saldo = saldo + lAccGlDetails.get(i).getValue();

                    } else {
                        accGlDetail.setCredit(lAccGlDetails.get(i).getValue());
                        totalcredit = totalcredit + lAccGlDetails.get(i).getValue();
                        saldo = saldo - lAccGlDetails.get(i).getValue();

                    }
                    accGlDetail.setSaldo(saldo);
                    lAccGlDetails.set(i, accGlDetail);
                }
                nilaiplus.put(pembanding, totaldebet);
                nilaimin.put(pembanding, totalcredit);
                nilaitotal.put(pembanding, totaldebet - totalcredit);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetails;
    }

    public Map<String, String> getComboBulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Bulan", "");
            map.put("Januari", "1");
            map.put("Februari", "2");
            map.put("Maret", "3");
            map.put("April", "4");
            map.put("Mei", "5");
            map.put("Juni", "6");
            map.put("Juli", "7");
            map.put("Agustus", "8");
            map.put("September", "9");
            map.put("Oktober", "10");
            map.put("November", "11");
            map.put("Desember", "12");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public void search() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./bukubesar.jsf?awal=" + awal + "&akhir=" + akhir);
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
        //pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("10");
        float[] columnWidths = new float[]{0.1f, 0.6f, 0.2f, 0.2f};
        pdfOpt.setColumnWidths(columnWidths);

    }

    public Map<String, String> getComboAccount() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Account> list = serviceBukuBesar.selectAllAccount();
            map.put("", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(Integer.toString(data.getId_account()), Integer.toString(data.getId_account()));
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public Double ambilNilaiPlus(Integer idAccount) {
        return nilaiplus.get(idAccount);
    }

    public Double ambilNilaiMin(Integer idAccount) {
        return nilaimin.get(idAccount);
    }

    public Double ambilNilaiTotal(Integer idAccount) {
        return nilaitotal.get(idAccount);
    }

    public void onAccountFromSelect() {
        accountFrom = accountAutoComplete.getAccount().getId_account();
        accountFromdesc = accountAutoComplete.getAccount().getAccount();
    }

    public void onAccountToSelect() {
        accountTo = accountAutoComplete.getAccount().getId_account();
        accountTodesc = accountAutoComplete.getAccount().getAccount();
    }

}
