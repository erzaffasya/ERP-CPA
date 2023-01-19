package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
import net.sra.prime.ultima.entity.PembayaranPiutang;
import net.sra.prime.ultima.entity.PembayaranPiutangDetail;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.service.ServicePembayaranPiutang;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.ArAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPembayaranPiutang implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<PembayaranPiutang> lPembayaranPiutang = new ArrayList<>();
    private PembayaranPiutang item;
    private List<PembayaranPiutangDetail> lPembayaranPiutangDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private List<AccGlTrans> lAccGlTrans;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Date awal;
    private Date akhir;
    private Character statusap;
    private String id_salesman;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private ArAutoComplete arAutoComplete;

    @Inject
    private Page page;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Options options;

    @Autowired
    ServicePembayaranPiutang servicePembayaranPiutang;

    @PostConstruct
    public void init() {
        item = new PembayaranPiutang();
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
        if (statusap == null) {
            statusap = 'D';
        }
        options.setId_departemen(page.getMyPegawai().getId_departemen());
    }

    public void initItem() {
        item = new PembayaranPiutang();
        lPembayaranPiutangDetail = new ArrayList<>();
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
        lPembayaranPiutangDetail.add(new PembayaranPiutangDetail());
        customerAutoComplete.setCustomer(null);

    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());

        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());

        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = servicePembayaranPiutang.noMax(item.getId_kantor(), Integer.parseInt(bulan), Integer.parseInt(year));
        InternalKantorCabang internalKantorCabang = servicePembayaranPiutang.selectOneKantor(item.getId_kantor());
        if (noMax == null) {
            item.setNo_pembayaran_piutang("001/" + bulan + tahun + "/OR/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_pembayaran_piutang(noMax + "/" + bulan + tahun + "/OR/" + internalKantorCabang.getNumbercode());
        }

    }

    public void jurnalawal() {
        lAccGlDetail = servicePembayaranPiutang.jurnalawal(item);
        this.hitungSelisih();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPembayaranPiutangDetail.size(); j++) {
            jml = jml + lPembayaranPiutangDetail.get(j).getJumlah_bayar();
        }
        item.setTotal_bayar(jml);
        lAccGlDetail = servicePembayaranPiutang.jurnalawal(item);
        this.hitungSelisih();
    }

    public void hitungTotal(PembayaranPiutangDetail s, Integer i) {
        if (s.getDpp() == null) {
            s.setDpp(0.00);
        }
        if (s.getPpn() == null) {
            s.setPpn(0.00);
        }
        s.setJumlah_bayar(s.getDpp() + s.getPpn());
        lPembayaranPiutangDetail.set(i, s);
        hitungJumlahTotal();
    }

    public void onLoadList() {
        if (statusap.equals('X')) {
            statusap = null;
        }
        lPembayaranPiutang = servicePembayaranPiutang.onLoadList(awal, akhir, statusap);

    }

    private Double total_dpp, total_ppn, total;

    public void InsentifCustomer(Date awalnya, Date akhirnya, String id_salesmannya, String id_customernya, Integer id_departemennya) {
        total_dpp = 0.00;
        total_ppn = 0.00;
        total = 0.00;
        String id_customer = null;
        if (customerAutoComplete.getCustomer() != null) {
            id_customer = customerAutoComplete.getCustomer().getId_kontak();
        }
        lPembayaranPiutangDetail = servicePembayaranPiutang.PendapatanMarketing(awalnya, akhirnya, id_salesmannya, id_customernya, id_departemennya);
        for (int i = 0; i < lPembayaranPiutangDetail.size(); i++) {
            if (lPembayaranPiutangDetail.get(i).getDpp() != null) {
                total_dpp = total_dpp + lPembayaranPiutangDetail.get(i).getDpp();
            }
            if (lPembayaranPiutangDetail.get(i).getPpn() != null) {
                total_ppn = total_ppn + lPembayaranPiutangDetail.get(i).getPpn();
            }
            if (lPembayaranPiutangDetail.get(i).getJumlah_bayar() != null) {
                total = total + lPembayaranPiutangDetail.get(i).getJumlah_bayar();
            }
        }

    }

    public void InsentifCustomerDsm() {
        String id_customer = null;
        if (customerAutoComplete.getCustomer() != null) {
            id_customer = customerAutoComplete.getCustomer().getId_kontak();
        }
        InsentifCustomer(awal, akhir, id_salesman, id_customer, page.getMyPegawai().getId_departemen());

    }

    public void insentifCustomerAll() {

        String id_customer = null;
        if (customerAutoComplete.getCustomer() != null) {
            id_customer = customerAutoComplete.getCustomer().getId_kontak();
        }
        InsentifCustomer(awal, akhir, id_salesman, id_customer, 0);
    }

    public void insentifCustomerDsr() {
        id_salesman = page.getMyPegawai().getId_pegawai();
        String id_customer = null;
        if (customerAutoComplete.getCustomer() != null) {
            id_customer = customerAutoComplete.getCustomer().getId_kontak();
        }
        InsentifCustomer(awal, akhir, id_salesman, id_customer, 0);
    }

    public List<PembayaranPiutang> getDataPembayaranPiutang() {
        return lPembayaranPiutang;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePembayaranPiutang.delete(nomor);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            this.list('L');
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public List<PembayaranPiutangDetail> getDataPembayaranPiutangDetail() {
        return lPembayaranPiutangDetail;

    }

    public void extend() {
        lPembayaranPiutangDetail.add(new PembayaranPiutangDetail());
    }

    public void onDeleteClicked(PembayaranPiutangDetail pembayaranPiutangDetail) {
        lPembayaranPiutangDetail.remove(pembayaranPiutangDetail);
        this.hitungJumlahTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (awal == null && akhir == null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_piutang() + "&status=" + statusap);
        } else if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_piutang() + "&awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (awal == null && akhir == null) {
            context.getExternalContext().redirect("./pembayaranar-edit.jsf?id=" + item.getNo_pembayaran_piutang() + "&status=" + statusap);
        } else if (item != null) {
            context.getExternalContext().redirect("./pembayaranar-edit.jsf?id=" + item.getNo_pembayaran_piutang() + "&awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void onLoad() {
        try {
            item = servicePembayaranPiutang.onLoad(item.getNo_pembayaran_piutang());
            customerAutoComplete.setCustomer(servicePembayaranPiutang.selectOneCustomer(item.getId_customer()));
            arAutoComplete.setId_customer(item.getId_customer());
            lPembayaranPiutangDetail = servicePembayaranPiutang.onLoadDetail(item.getNo_pembayaran_piutang());
            lAccGlDetail = servicePembayaranPiutang.jurnal(item.getNo_pembayaran_piutang());
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
                servicePembayaranPiutang.tambah(item, lPembayaranPiutangDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_piutang() + "&status=D");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void onKasSelect() {
        lAccGlDetail = servicePembayaranPiutang.jurnalawal(item);
    }

    public void onCustomerSelect(SelectEvent event) {
        item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
        item.setCustomer(customerAutoComplete.getCustomer().getCustomer());
        item.setKeterangan("Terima dari " + item.getCustomer());
        arAutoComplete.setId_customer(item.getId_customer());
        lAccGlDetail = servicePembayaranPiutang.jurnalawal(item);
    }

    public void onArSelect(PembayaranPiutangDetail s, Integer i) {

        s.setNomor(item.getNo_pembayaran_piutang());
        s.setJumlah_tagihan(arAutoComplete.getAccArFaktur().getTotal() - arAutoComplete.getAccArFaktur().getBayar());
        s.setJumlah_bayar(arAutoComplete.getAccArFaktur().getTotal() - arAutoComplete.getAccArFaktur().getBayar());
        s.setNo_penjualan(arAutoComplete.getAccArFaktur().getAr_number());
        s.setNo_invoice(arAutoComplete.getAccArFaktur().getNo_invoice());
        s.setDpp(arAutoComplete.getAccArFaktur().getAmount() - arAutoComplete.getAccArFaktur().getBayar_dpp());
        if (arAutoComplete.getAccArFaktur().getPpn() != null) {
            s.setPpn(arAutoComplete.getAccArFaktur().getPpn() - arAutoComplete.getAccArFaktur().getBayar_ppn());
        } else {
            s.setPpn(0.00);
        }
        item.setId_kantor(arAutoComplete.getAccArFaktur().getId_kantor_cabang());
        lPembayaranPiutangDetail.set(i, s);
        this.hitungJumlahTotal();
    }

    public void ubah(Character status) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            Boolean benar = true;
            if (status.equals('A')) {
                PembayaranPiutang pp = servicePembayaranPiutang.onLoad(item.getNo_pembayaran_piutang());
                if (status.equals(pp.getStatus())) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pembayaran Piutang sudah pernah di Approve !!", ""));
                    context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pembayaran_piutang());
                    benar = false;
                }
            }
            if (benar) {
                for (int i = 0; i < lAccGlDetail.size(); i++) {
                    if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                        benar = false;
                    }
                }
                if (benar && jumlahDebit.equals(jumlahKredit)) {
                    Character st = status;
                    try {
                        item.setStatus(status);
                        servicePembayaranPiutang.ubah(item, lPembayaranPiutangDetail, lAccGlDetail);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePembayaranPiutang.maintenance(item);
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
        DecimalFormat df = new DecimalFormat("0.00");
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

    public void maintenanceList(Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            if ((awal == null && akhir != null) || (awal != null && akhir == null)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode salah !!!", ""));
            } else if (awal == null && akhir == null) {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./pembayaranar.jsf" + "?status=" + statusap);
                }

            } else {
                if (jenis.equals('L')) {
                    context.getExternalContext().redirect("./pembayaranar.jsf" + "?awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + statusap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakpembayaranpiutang.jsf?id=" + item.getNo_pembayaran_piutang());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            PembayaranPiutang pembayaranPiutang = servicePembayaranPiutang.onLoad(item.getNo_pembayaran_piutang());
            if (!bln.format(item.getTanggal()).equals(bln.format(pembayaranPiutang.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(pembayaranPiutang.getTanggal());
            }
        }
        if (item.getTanggal().after(new Date())) {
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
