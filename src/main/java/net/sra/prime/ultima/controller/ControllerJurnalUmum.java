package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.service.ServiceJurnalUmum;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerJurnalUmum implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccGlTrans> lAccGlTrans = new ArrayList<>();
    private AccGlTrans item;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Date awal;
    private Date akhir;
    private Boolean status;
    private String note;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceJurnalUmum serviceJurnalUmum;

    @PostConstruct
    public void init() {
        item = new AccGlTrans();
        //awal = new Date();
        //akhir = new Date();
        status = null;
        note="";
    }

    public void initItem() {
        item = new AccGlTrans();
        lAccGlDetail = new ArrayList<>();
        lAccGlDetail.add(new AccGlDetail());
        Date date = new Date();
        item.setGl_date(date);
    }

    public void onLoadList() {
        lAccGlTrans = serviceJurnalUmum.onLoadList("JU",awal, akhir, status,note);
    }

    public List<AccGlTrans> getDataAccGlTrans() {
        return lAccGlTrans;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceJurnalUmum.delete(nomor);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lAccGlDetail.add(new AccGlDetail());
    }

    public void onDeleteClicked(AccGlDetail hapus) {
        lAccGlDetail.remove(hapus);
        this.hitungSelisih();
    }
//

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getGl_number());
    }
//

    public void onLoad() {
        try {
            item = serviceJurnalUmum.onLoad(item.getGl_number());
            lAccGlDetail = serviceJurnalUmum.onLoadDetail(item.getGl_number());
            jumlahDebit=0.00;
            jumlahKredit=0.00;
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                AccGlDetail itemdetail = lAccGlDetail.get(i);
                if (itemdetail.getIs_debit()) {
                    itemdetail.setDebit(itemdetail.getValue());
                    jumlahDebit = jumlahDebit + itemdetail.getValue();
                } else {
                    itemdetail.setCredit(itemdetail.getValue());
                    jumlahKredit = jumlahKredit + itemdetail.getValue();
                }
                lAccGlDetail.set(i, itemdetail);
            }
            selisih = jumlahDebit - jumlahKredit;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hitungSelisih() {
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih=0.00;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getDebit() != null) {
                jumlahDebit = jumlahDebit + lAccGlDetail.get(i).getDebit();
            }

            if (lAccGlDetail.get(i).getCredit() != null) {
                jumlahKredit = jumlahKredit + lAccGlDetail.get(i).getCredit();
            }
            jumlahDebit=Math.round(jumlahDebit*100.0)/100.0;
            jumlahKredit=Math.round(jumlahKredit*100.0)/100.0;
            selisih = jumlahDebit - jumlahKredit;
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        if (!jumlahDebit.equals(jumlahKredit)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal tidak balance", ""));
        } else {
            try {
                this.nomorGl();
                item.setPosting(Boolean.FALSE);
                item.setJournal_code("JU");
                serviceJurnalUmum.tambah(item, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getGl_number());
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void onAccountSelect(AccGlDetail s, Integer i) {
        s.setId_account(accountAutoComplete.getAccount().getId_account());
        s.setAccount(accountAutoComplete.getAccount().getAccount());
        lAccGlDetail.set(i, s);

    }

    public void ubah(Boolean posting) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (!jumlahDebit.equals(jumlahKredit)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal tidak balance", ""));
        } else {
            try {
                item.setPosting(posting);
                serviceJurnalUmum.ubah(item, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }

    }

    public void nomorGl() {
        String tahun = new SimpleDateFormat("yy").format(item.getGl_date());
        String thn = new SimpleDateFormat("yyyy").format(item.getGl_date());
        
        String noMax = serviceJurnalUmum.noMax(Integer.parseInt(thn));
        if (noMax == null) {
            item.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            item.setGl_number("GL" + noMax + tahun);
            item.setReference(item.getGl_number());
        }
    }
    
    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakjurnalumum.jsf?id=" + item.getGl_number());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getGl_number()!= null) {
            DateFormat bln = new SimpleDateFormat("MM");
            DateFormat tahun = new SimpleDateFormat("yyyy");
            AccGlTrans accGlTrans = serviceJurnalUmum.onLoad(item.getGl_number());
            if (!bln.format(item.getGl_date()).equals(bln.format(accGlTrans.getGl_date())) || !tahun.format(item.getGl_date()).equals(tahun.format(accGlTrans.getGl_date()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulan dan tahunnya !!!", ""));
                item.setGl_date(accGlTrans.getGl_date());
            }
            
            
        }
        if (item.getGl_date().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setGl_date(null);
        }

    }
    
    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
    }
}
