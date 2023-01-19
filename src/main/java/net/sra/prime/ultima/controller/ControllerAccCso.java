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
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccCso;
import net.sra.prime.ultima.entity.AccCsoDetil;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.service.ServiceCso;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.ProposalAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SupplierAutoComplete;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAccCso implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccCso> lAccCso = new ArrayList<>();
    private AccCso item;
    private List<AccCsoDetil> lAccCsoDetil = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private List<AccGlTrans> lAccGlTrans;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;

    @Inject
    private SupplierAutoComplete supplierAutoComplete;

    @Inject
    private ProposalAutoComplete proposalAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Autowired
    ServiceCso serviceCso;

    @PostConstruct
    public void init() {
        item = new AccCso();
    }

    public void initItem() {
        Date date = new Date();
        item.setTanggal(date);
        item.setId_kantor(page.getMyPegawai().getId_kantor_new());
        lAccCsoDetil.add(new AccCsoDetil());
    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = serviceCso.noMax(page.getMyKantor().getId_kantor_cabang(), Integer.parseInt(bulan), Integer.parseInt(year));
        InternalKantorCabang internalKantorCabang = serviceCso.selectOneKantor(page.getMyPegawai().getId_kantor_new());
        item.setKantor(internalKantorCabang.getNama());
        if (noMax == null) {
            item.setAcc_cso_number("001/" + bulan + tahun + "/BV/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setAcc_cso_number(noMax + "/" + bulan + tahun + "/BV/" + internalKantorCabang.getNumbercode());
        }
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lAccCsoDetil.size(); j++) {
            if (lAccCsoDetil.get(j).getJumlah() != null) {
                jml = jml + lAccCsoDetil.get(j).getJumlah();
            }
        }
        item.setTotal(jml);

    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lAccCso = serviceCso.onLoadList(awal, akhir, page.getMyPegawai().getId_kantor_new());
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AccCso> getDataAccCso() {
        return lAccCso;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;

    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCso.delete(nomor);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public List<AccCsoDetil> getDataAccCsoDetil() {
        return lAccCsoDetil;

    }

    public void extend() {
        lAccCsoDetil.add(new AccCsoDetil());
    }

    public void onDeleteClicked(AccCsoDetil accCsoDetil) {
        lAccCsoDetil.remove(accCsoDetil);
        this.hitungJumlahTotal();
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getAcc_cso_number());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void onLoad() {
        try {
            item = serviceCso.onLoad(item.getAcc_cso_number());
            lAccCsoDetil = serviceCso.onLoadDetail(item.getAcc_cso_number());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setStatus('D');
        item.setId_jenis_transaksi("BV");
        this.nomorurut();
        try {
            serviceCso.tambah(item, lAccCsoDetil, lAccGlDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getAcc_cso_number());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item.setStatus(status);
        try {
            serviceCso.ubah(item, lAccCsoDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onAccountSelect(AccCsoDetil s, Integer i) {
        s.setId_account_detail(accountAutoComplete.getAccount().getId_account());
        s.setAccount(accountAutoComplete.getAccount().getAccount());

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakbankvoucher.jsf?id=" + item.getAcc_cso_number());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            AccCso accCso = serviceCso.onLoad(item.getAcc_cso_number());
            if (!bln.format(item.getTanggal()).equals(bln.format(accCso.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ap Date tidak boleh beda bulannya !!!", ""));
                item.setTanggal(accCso.getTanggal());
            }
        }
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ap Date tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        }

    }

    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
    }
}
