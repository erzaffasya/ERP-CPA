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
import net.sra.prime.ultima.entity.AccProposalApDetail;
import net.sra.prime.ultima.entity.PembayaranHutang;
import net.sra.prime.ultima.entity.PembayaranHutangDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.service.ServicePembayaranHutang;
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
public class ControllerPembayaranHutang implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<PembayaranHutang> lPembayaranHutang = new ArrayList<>();
    private PembayaranHutang item;
    private List<PembayaranHutangDetail> lPembayaranHutangDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private List<AccGlTrans> lAccGlTrans;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Date awal;
    private Date akhir;
    private Character statusap;

    @Inject
    private SupplierAutoComplete supplierAutoComplete;

    @Inject
    private ProposalAutoComplete proposalAutoComplete;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServicePembayaranHutang servicePembayaranHutang;

    @PostConstruct
    public void init() {
        item = new PembayaranHutang();
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
        if (statusap == null) {
            statusap = 'D';
        }
    }

    public void initItem() {
        item = new PembayaranHutang();
        lPembayaranHutang = new ArrayList<>();
        lAccGlDetail = new ArrayList<>();
        Date date = new Date();
        //item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setTotal_tagihan(0.00);
        item.setTotal_bayar(0.00);
        item.setTotal_denda(0.00);
        item.setId_perusahaan(page.getMyKantor().getId_perusahaan());
        item.setId_kantor(page.getMyKantor().getId_kantor_cabang());
        options.setId_perusahaan(item.getId_perusahaan());
        // lPembayaranHutangDetail.add(new PembayaranHutangDetail());
        supplierAutoComplete.setSupplier(null);
    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = servicePembayaranHutang.noMax(item.getId_kantor(), Integer.parseInt(bulan), Integer.parseInt(year));
        InternalKantorCabang internalKantorCabang = servicePembayaranHutang.selectOneKantor(item.getId_kantor());
        if (noMax == null) {
            item.setNo_pembayaran_hutang("001/" + bulan + tahun + "/BV-AP/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_pembayaran_hutang(noMax + "/" + bulan + tahun + "/BV-AP/" + internalKantorCabang.getNumbercode());
        }
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPembayaranHutangDetail.size(); j++) {
            jml = jml + lPembayaranHutangDetail.get(j).getJumlah_bayar();
        }
        item.setTotal_bayar(jml);
        lAccGlDetail = servicePembayaranHutang.jurnalawal(item);
        this.hitungSelisih();
    }

    public void onLoadList() {
        if (statusap.equals('X')) {
            statusap = null;
        }
        lPembayaranHutang = servicePembayaranHutang.onLoadList(awal, akhir, statusap);

    }

    public List<PembayaranHutang> getDataPembayaranHutang() {
        return lPembayaranHutang;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;

    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePembayaranHutang.delete(nomor);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            this.list('L');
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PembayaranHutangDetail> getDataPembayaranHutangDetail() {
        return lPembayaranHutangDetail;

    }

    public void extend() {
        lPembayaranHutangDetail.add(new PembayaranHutangDetail());
    }

    public void onDeleteClicked(PembayaranHutangDetail pembayaranHutangDetail) {
        lPembayaranHutangDetail.remove(pembayaranHutangDetail);
        this.hitungJumlahTotal();
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (awal == null && akhir == null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_hutang() + "&status=" + statusap);
        } else if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_hutang() + "&awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (awal == null && akhir == null) {
            context.getExternalContext().redirect("./pembayaranap-edit.jsf?id=" + item.getNo_pembayaran_hutang() + "&status=" + statusap);
        } else if (item != null) {
            context.getExternalContext().redirect("./pembayaranap-edit.jsf?id=" + item.getNo_pembayaran_hutang() + "&awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void list(Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            if ((awal == null && akhir != null) || (awal != null && akhir == null)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode salah !!!", ""));
            } else if (awal == null && akhir == null) {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./list.jsf" + "?status=" + statusap);
                }
                if (jenis.equals('A')) {
                    context.getExternalContext().redirect("./add.jsf" + "?status=" + statusap);
                }
            } else {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./list.jsf" + "?awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
                }
                if (jenis.equals('A')) {
                    context.getExternalContext().redirect("./add.jsf" + "?awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void listMaintenance(Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            if ((awal == null && akhir != null) || (awal != null && akhir == null)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode salah !!!", ""));
            } else if (awal == null && akhir == null) {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./pembayaranap.jsf" + "?status=" + statusap);
                }

            } else {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./pembayaranap.jsf" + "?awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoad() {
        try {
            item = servicePembayaranHutang.onLoad(item.getNo_pembayaran_hutang());
            Supplier supplier = new Supplier();
            supplier.setId(item.getId_supplier());
            supplier.setSupplier(item.getSupplier());
            supplierAutoComplete.setSupplier(supplier);
            lPembayaranHutangDetail = servicePembayaranHutang.onLoadDetail(item.getNo_pembayaran_hutang());
            lAccGlDetail = servicePembayaranHutang.jurnal(item.getNo_pembayaran_hutang());
            this.hitungSelisih();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                benar = false;
            }
        }
        if (benar && jumlahDebit.equals(jumlahKredit)) {
            try {
                this.nomorurut();
                item.setStatus('D');
                servicePembayaranHutang.tambah(item, lPembayaranHutangDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                this.list('L');
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void onSupplierSelect(SelectEvent event) {
        List<AccProposalApDetail> lAccProposalApDetail = servicePembayaranHutang.selectAllDetailPayment(supplierAutoComplete.getSupplier().getId());

        for (int i = 0; i < lAccProposalApDetail.size(); i++) {
            PembayaranHutangDetail itemdetail = new PembayaranHutangDetail();
            itemdetail.setNo_penerimaan(lAccProposalApDetail.get(i).getAp_number());
            itemdetail.setReferensi(lAccProposalApDetail.get(i).getNo_invoice());
            //itemdetail.setJumlah_tagihan(lAccProposalApDetail.get(i).getTotal());
            itemdetail.setJumlah_tagihan(lAccProposalApDetail.get(i).getJumlah_tagihan());
            itemdetail.setJumlah_bayar(lAccProposalApDetail.get(i).getJumlah_tagihan());
            lPembayaranHutangDetail.add(itemdetail);

        }
        if (lAccProposalApDetail.size() > 0) {
            this.hitungJumlahTotal();
        }

        item.setId_supplier(supplierAutoComplete.getSupplier().getId());
        item.setSupplier(supplierAutoComplete.getSupplier().getSupplier());
        item.setKeterangan("Pembayaran kepada " + item.getSupplier());
        proposalAutoComplete.setVendor_code(item.getId_supplier());
        lAccGlDetail = servicePembayaranHutang.jurnalawal(item);
    }

    public void onProposalSelect(PembayaranHutangDetail s, Integer i) {
        s.setNo_penerimaan(proposalAutoComplete.getAccProposalApDetail().getAp_number());
        s.setReferensi(proposalAutoComplete.getAccProposalApDetail().getNo_invoice());
        s.setJumlah_tagihan(proposalAutoComplete.getAccProposalApDetail().getTotal());
        s.setJumlah_bayar(proposalAutoComplete.getAccProposalApDetail().getTotal());;
        lPembayaranHutangDetail.set(i, s);
        this.hitungJumlahTotal();
    }

    public void onKasSelect() {
        lAccGlDetail = servicePembayaranHutang.jurnalawal(item);
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                benar = false;
            }
        }
        if (benar && jumlahDebit.equals(jumlahKredit)) {
            Character st = status;
            try {
                item.setStatus(status);
                servicePembayaranHutang.ubah(item, lPembayaranHutangDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                item.setStatus(st);
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePembayaranHutang.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onAccountSelect(AccGlDetail s, Integer i) {
        s.setId_account(accountAutoComplete.getAccount().getId_account());
        s.setAccount(accountAutoComplete.getAccount().getAccount());
        lAccGlDetail.set(i, s);

    }

    public void extendGl() {
        lAccGlDetail.add(new AccGlDetail());
    }

    public void onDeleteClickedGl(AccGlDetail hapus) {
        lAccGlDetail.remove(hapus);
        this.hitungSelisih();
    }

    public void hitungSelisih() {
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getDebit() != null) {
                jumlahDebit = jumlahDebit + lAccGlDetail.get(i).getDebit();
            }

            if (lAccGlDetail.get(i).getCredit() != null) {
                jumlahKredit = jumlahKredit + lAccGlDetail.get(i).getCredit();
            }
            jumlahDebit = Math.round(jumlahDebit * 100.0) / 100.0;
            jumlahKredit = Math.round(jumlahKredit * 100.0) / 100.0;
            selisih = jumlahDebit - jumlahKredit;
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakpembayaranhutang.jsf?id=" + item.getNo_pembayaran_hutang());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            PembayaranHutang pembayaranHutang = servicePembayaranHutang.onLoad(item.getNo_pembayaran_hutang());
            if (!bln.format(item.getTanggal()).equals(bln.format(pembayaranHutang.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(pembayaranHutang.getTanggal());
            }
        } else if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
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
