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
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.CreditNote;
import net.sra.prime.ultima.entity.CreditNoteDetail;
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.entity.PenjualanDetailReguler;
import net.sra.prime.ultima.service.ServiceCreditNote;
import net.sra.prime.ultima.service.ServicePenjualan;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.InvoiceAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerCreditNote implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<CreditNote> lCreditNote = new ArrayList<>();
    private CreditNote item;
    private List<CreditNoteDetail> lCreditNoteDetail = new ArrayList<>();
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private List<AccGlDetail> lAccGlDetailCancel = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Character status;

//    @Inject
//    private CustomerAutoComplete customerAutoComplete;
    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private InvoiceAutoComplete invoiceAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceCreditNote serviceCreditNote;

    @Autowired
    ServicePenjualan servicePenjualan;

    @PostConstruct
    public void init() {
        item = new CreditNote();
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
    }

    public void initItem() {
        item = new CreditNote();
        Date date = new Date();
        // item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setBiayalain(0.00);
        item.setIs_ppn(Boolean.TRUE);
        lCreditNoteDetail = new ArrayList<>();
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceCreditNote.noMax(Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNomor("0001/CN/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%04d", nomor);
            item.setNomor(noMax + "/CN/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        if (status == null) {
            status = 'D';
        } else if (status.equals('9')) {
            status = null;
        }
        lCreditNote = serviceCreditNote.onLoadList(awal, akhir, status);
    }

    public List<CreditNote> getDataCreditNote() {
        return lCreditNote;
    }

    public void delete(Integer nomor, Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceCreditNote.delete(nomor,jenis);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public List<CreditNoteDetail> getDataCreditNoteDetail() {
        return lCreditNoteDetail;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public List<AccGlDetail> getDataAccGlDetailCancel() {
        return lAccGlDetailCancel;
    }

    public void extend() {
        lCreditNoteDetail.add(new CreditNoteDetail());
    }

    public void onDeleteClicked(CreditNoteDetail item) {
        lCreditNoteDetail.remove(item);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());

    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./creditnote-edit.jsf?id=" + item.getId());

    }

    public void onLoad() {
        try {
            item = serviceCreditNote.onLoad(item.getId());
            if (item.getJenis() == null) {
                lCreditNoteDetail = serviceCreditNote.onLoadDetail(item.getId());
                invoiceAutoComplete.setPenjualan(servicePenjualan.onLoad(item.getNo_invoice()));
            } else {
                lCreditNoteDetail = serviceCreditNote.onLoadDetailReguler(item.getId());
                invoiceAutoComplete.setPenjualan(servicePenjualan.selectOnePenjualan(item.getNo_invoice()));
            }
            lAccGlDetail = serviceCreditNote.jurnal(item.getNomor());
            lAccGlDetailCancel = serviceCreditNote.jurnal(item.getNo_cancel());
            hitungSelisih();

            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hitungDiskonPersen(CreditNoteDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lCreditNoteDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungDiskonRp(CreditNoteDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lCreditNoteDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungTotal(CreditNoteDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }

        s.setTotal((s.getHarga() - s.getDiskonrp() + s.getAdditional_charge()) * s.getQty());
        lCreditNoteDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lCreditNoteDetail.size(); j++) {
            jml = jml + lCreditNoteDetail.get(j).getTotal();
        }
        item.setTotal(jml);

    }

    public void hitungDiskon() {
        item.setTotal_discount(item.getPersendiskon() / 100 * item.getTotal());
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungDpp() {
        item.setDpp(item.getTotal() - item.getTotal_discount());
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungPpn() {
        if (item.getIs_ppn()) {
            item.setTotal_ppn(item.getDpp() * 10 / 100);
        } else {
            item.setTotal_ppn(0.00);
        }
        this.hitungGrandTotal();
    }

    public void hitungGrandTotal() {
        if (item.getBiayalain() == null) {
            item.setBiayalain(0.00);
        }
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getBiayalain());
        try {
            lAccGlDetail = serviceCreditNote.jurnalAwal(item, lCreditNoteDetail);
            hitungSelisih();
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
                if (item.getBiayalain() == null) {
                    item.setBiayalain(0.00);
                }
                serviceCreditNote.tambah(item, lCreditNoteDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Character st = item.getStatus();
        try {
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            if (item.getPersendiskon() == null) {
                item.setPersendiskon(0.00);
            }
            if (item.getDiskonrp() == null) {
                item.setDiskonrp(0.00);
            }
            item.setStatus(status);
            serviceCreditNote.ubah(item, lCreditNoteDetail, lAccGlDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            item.setStatus(st);
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            if (item.getPersendiskon() == null) {
                item.setPersendiskon(0.00);
            }
            if (item.getDiskonrp() == null) {
                item.setDiskonrp(0.00);
            }

            serviceCreditNote.maintenace(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {

            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void posting(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        if (lAccGlDetail.size() <= 0) {
            benar = false;
        } else {
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                    benar = false;
                }
            }
        }
        Double jmlDebit = Math.round(jumlahDebit * 100.0) / 100.0;
        Double jmlKredit = Math.round(jumlahKredit * 100.0) / 100.0;
        if (benar && jmlDebit.equals(jmlKredit)) {
            Character st = item.getStatus();
            try {
                item.setStatus(status);
                // serviceCreditNote.posting(item, lCreditNoteDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diposting"));
            } catch (Exception e) {
                item.setStatus(st);
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void noAcces() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./list.jsf?");
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

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getId());
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

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            nomorurutCancel();
            item.setStatus(status);
            serviceCreditNote.ubahStatus(item, page.getMyPegawai().getId_pegawai());
            onLoad();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onPenjualanSelect() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            item.setNo_invoice(invoiceAutoComplete.getPenjualan().getNo_penjualan());
            item.setNo_po(invoiceAutoComplete.getPenjualan().getReferensi());
            item.setTop(invoiceAutoComplete.getPenjualan().getTop());
            item.setTotal(invoiceAutoComplete.getPenjualan().getTotal());
            item.setPersendiskon(invoiceAutoComplete.getPenjualan().getPersendiskon());
            item.setDiskonrp(invoiceAutoComplete.getPenjualan().getDiskonrp());
            item.setDpp(invoiceAutoComplete.getPenjualan().getDpp());
            item.setTotal_ppn(invoiceAutoComplete.getPenjualan().getTotal_ppn());
            item.setBiayalain(invoiceAutoComplete.getPenjualan().getBiayalain());
            item.setGrandtotal(invoiceAutoComplete.getPenjualan().getGrandtotal());
            item.setFaktur(invoiceAutoComplete.getPenjualan().getFaktur());
            item.setCustomer(invoiceAutoComplete.getPenjualan().getCustomer());
            item.setId_customer(invoiceAutoComplete.getPenjualan().getId_customer());
            item.setDue_date(invoiceAutoComplete.getPenjualan().getDue_date());
            item.setNo_do(serviceCreditNote.selectDo(item.getNo_invoice()));
            item.setKode_user(page.getMyPegawai().getId_pegawai());
            item.setId_gudang(invoiceAutoComplete.getPenjualan().getId_gudang());
            item.setId_salesman(invoiceAutoComplete.getPenjualan().getId_salesman());
            item.setJenis(invoiceAutoComplete.getPenjualan().getJenis());
            if (invoiceAutoComplete.getPenjualan().getJenis() == null) {
                List<PenjualanDetail> lPenjualanDetail = servicePenjualan.onLoadDetail(item.getNo_invoice());
                for (int i = 0; i < lPenjualanDetail.size(); i++) {
                    CreditNoteDetail cnd = new CreditNoteDetail();
                    cnd.setId_barang(lPenjualanDetail.get(i).getId_barang());
                    cnd.setNama_barang(lPenjualanDetail.get(i).getNama_barang());
                    cnd.setQty(lPenjualanDetail.get(i).getQty());
                    cnd.setSatuan_besar(lPenjualanDetail.get(i).getSatuan_besar());
                    cnd.setHarga(lPenjualanDetail.get(i).getHarga());
                    cnd.setDiskonpersen(lPenjualanDetail.get(i).getDiskonpersen());
                    cnd.setDiskonrp(lPenjualanDetail.get(i).getDiskonrp());
                    cnd.setAdditional_charge(lPenjualanDetail.get(i).getAdditional_charge());
                    cnd.setTotal(lPenjualanDetail.get(i).getTotal());
                    lCreditNoteDetail.add(cnd);
                }
            } else {
                List<PenjualanDetailReguler> lPenjualanDetail = servicePenjualan.onLoadDetailReguler(item.getNo_invoice());
                for (int i = 0; i < lPenjualanDetail.size(); i++) {
                    CreditNoteDetail cnd = new CreditNoteDetail();
                    cnd.setNama_barang(lPenjualanDetail.get(i).getNm_barang());
                    cnd.setQty(lPenjualanDetail.get(i).getQty());
                    cnd.setSatuan_besar(lPenjualanDetail.get(i).getSatuan());
                    cnd.setHarga(lPenjualanDetail.get(i).getHarga());
                    cnd.setDiskonpersen(lPenjualanDetail.get(i).getDiskonpersen());
                    cnd.setDiskonrp(lPenjualanDetail.get(i).getDiskonrp());
                    cnd.setTotal(lPenjualanDetail.get(i).getTotal());
                    lCreditNoteDetail.add(cnd);
                }
            }
            if (invoiceAutoComplete.getPenjualan().getJenis() == null) {
                lAccGlDetail = serviceCreditNote.jurnalAwal(item, lCreditNoteDetail);
            } else {
                List<AccGlDetail> lDetail = serviceCreditNote.jurnalAwalReguler(item, lCreditNoteDetail);
                AccGlDetail accGlDetail = new AccGlDetail();
                for (int i = lDetail.size() - 1; i >= 0; i--) {
                    accGlDetail = lDetail.get(i);
                    if (accGlDetail.getIs_debit()) {
                        accGlDetail.setCredit(accGlDetail.getValue());
                    } else {
                        accGlDetail.setDebit(accGlDetail.getValue());
                    }
                    lAccGlDetail.add(accGlDetail);
                }
            }
            hitungSelisih();
        } catch (Exception e) {
            //e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void nomorurutCancel() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(new Date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(new Date());
        String bulan = romanMonths[Integer.parseInt(bln.format(new Date())) - 1];
        String noMax = serviceCreditNote.noMaxCancel(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_cancel("001/CN-CANCEL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_cancel(noMax + "/CN-CANCEL/" + bulan + "/" + tahun);
        }
    }

}
