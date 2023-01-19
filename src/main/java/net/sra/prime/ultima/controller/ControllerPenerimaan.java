package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Penerimaan;
import net.sra.prime.ultima.entity.PenerimaanDetail;
import net.sra.prime.ultima.entity.PenerimaanDetailReguler;
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.entity.PoDetailReguler;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.service.ServicePenerimaan;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.PermintaanPembelianAutoComplete;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SupplierAutoComplete;
import net.sra.prime.ultima.view.input.PoAutoComplete;
import org.joda.time.DateTime;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPenerimaan implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Penerimaan> lPenerimaan = new ArrayList<>();
    private Penerimaan item;
    private PenerimaanDetailReguler itemdetailReguler;
    private List<PenerimaanDetail> lPenerimaanDetail = new ArrayList<>();
    private List<PenerimaanDetailReguler> lPenerimaanDetailReguler = new ArrayList<>();
    private List<Supplier> lSupplier = new ArrayList<>();
    private AccGlTrans itemTrans;
    private List<AccGlTrans> lAccGlTrans;
    private AccGlDetail itemGl;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Double totalQty;
    private Date awal;
    private Date akhir;
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Double ppn;

    @Autowired
    ServicePenerimaan servicePenerimaan;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    //private MapperPenerimaan referensi;
    @Inject
    private SupplierAutoComplete supplierAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private PoAutoComplete poAutoComplete;

    @Inject
    private PermintaanPembelianAutoComplete permintaanPembelianAutoComplete;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Penerimaan();
        itemGl = new AccGlDetail();
        //awal = new Date();
        //akhir = new Date();
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
    }

    public void initShell() {
        Po po = new Po();
        poAutoComplete.setJenis_po("IP");
        poAutoComplete.setPo(po);
        this.initItem();
        item.setJenis('S');

    }

    public void initAp() {
        lPenerimaanDetailReguler = new ArrayList<>();
        lPenerimaanDetailReguler.add(new PenerimaanDetailReguler());
        this.initItem();
        item.setJenis('A');

        lAccGlDetail.add(new AccGlDetail());

    }

    public void initReguler() {
//        Po po = new Po();
//        poAutoComplete.setJenis_po("PP");
//        poAutoComplete.setPo(po);
        permintaanPembelianAutoComplete.setPermintaanPembelian(new PermintaanPembelian());
        this.initItem();
        item.setJenis('R');

    }

    public void initRegulerbyPo() {
        Po po = new Po();
        poAutoComplete.setJenis_po("PP");
        poAutoComplete.setPo(po);
        permintaanPembelianAutoComplete.setPermintaanPembelian(new PermintaanPembelian());
        this.initItem();
        item.setJenis('P');

    }

    public void initItem() {
        item = new Penerimaan();
        Date date = new Date();
        //item.setTgl_penerimaan(date);
        item.setDpp(0.00);
        item.setTotal(0.00);
        item.setTotal_ppn(0.00);
        item.setGrandtotal(0.00);
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setTotal_bayar(0.00);
        item.setTotal_pph(0.00);
        item.setPpnpersen(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setBiaya_transportasi(0.00);
        item.setBiaya_asuransi(0.00);
        item.setBiaya_bongkar_muat(0.00);
        item.setBiaya_bongkar_muat_eksternal(0.00);
        item.setBiayalain(0.00);
        item.setId_kantor(page.getMyKantor().getId_kantor_cabang());
        item.setIs_pph(Boolean.TRUE);
        supplierAutoComplete.setSupplier(new Supplier());
        itemGl = new AccGlDetail();

    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTgl_penerimaan());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTgl_penerimaan());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTgl_penerimaan());
        String noMax = servicePenerimaan.noMax(item.getId_kantor(), Integer.parseInt(bulan), Integer.parseInt(year));
        InternalKantorCabang internalKantorCabang = servicePenerimaan.selecOne(item.getId_kantor());
        if (noMax == null) {
            item.setNo_penerimaan("001/" + bulan + tahun + "/AP/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_penerimaan(noMax + "/" + bulan + tahun + "/AP/" + internalKantorCabang.getNumbercode());
        }
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenerimaan = servicePenerimaan.onLoadList(awal, akhir);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListPajak() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenerimaan = servicePenerimaan.onLoadListPajak(awal, akhir);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Penerimaan> getDataPenerimaan() {
        return lPenerimaan;
    }

    public void delete(String nomor_po) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenerimaan.delete(item.getNo_penerimaan(), item.getJenis());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PenerimaanDetail> getDataPenerimaanDetail() {
        return lPenerimaanDetail;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public void extend() {
        lPenerimaanDetail.add(new PenerimaanDetail());
    }

    public void onDeleteClicked(PenerimaanDetail itemPenerimaanDetail) {
        lPenerimaanDetail.remove(itemPenerimaanDetail);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungPph();
        this.hitungGrandTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis().equals('S')) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('R')) {
            context.getExternalContext().redirect("./editreguler.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('A')) {
            context.getExternalContext().redirect("./editap.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('P')) {
            context.getExternalContext().redirect("./editregulerpo.jsf?id=" + item.getNo_penerimaan());
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis().equals('S')) {
            context.getExternalContext().redirect("./ap-edit.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('R')) {
            context.getExternalContext().redirect("./ap-editreguler.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('A')) {
            context.getExternalContext().redirect("./ap-editap.jsf?id=" + item.getNo_penerimaan());
        } else if (item.getJenis().equals('P')) {
            context.getExternalContext().redirect("./ap-editregulerpo.jsf?id=" + item.getNo_penerimaan());
        }
    }

    public void onLoad() {
        try {

            item = servicePenerimaan.onLoad(item.getNo_penerimaan());
            Po po = new Po();
            po.setNomor_po(item.getNomor_po());
            poAutoComplete.setPo(po);
            if (item.getJenis().equals('S')) {
                lPenerimaanDetail = servicePenerimaan.onLoadDetail(item.getNo_penerimaan());

            } else if (item.getJenis().equals('R') || item.getJenis().equals('A') || item.getJenis().equals('P')) {
                lPenerimaanDetailReguler = servicePenerimaan.selectPenerimaanDetailReguler(item.getNo_penerimaan());
                supplierAutoComplete.setSupplier(servicePenerimaan.selectOneSupplier(item.getId_supplier()));
                permintaanPembelianAutoComplete.setPermintaanPembelian(servicePenerimaan.selectPp(item.getNo_pp()));
            }
            lAccGlDetail = servicePenerimaan.jurnal(item.getNo_penerimaan());
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                AccGlDetail itemdetail = lAccGlDetail.get(i);
                if (itemdetail.getIs_debit()) {
                    itemdetail.setDebit(itemdetail.getValue());
                } else {
                    itemdetail.setCredit(itemdetail.getValue());
                }
                lAccGlDetail.set(i, itemdetail);
            }
            this.hitungSelisih();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        PenawaranDetail entity = context.getApplication().evaluateExpressionGet(context, "#{item}", PenawaranDetail.class);
        entity.setNama_barang(entity.getId_barang());
        // ...
    }

    public void hitungTotal(PenerimaanDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }

        s.setTotal(s.getHarga() * s.getQty());
        lPenerimaanDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungPph();
        this.hitungGrandTotal();
        lAccGlDetail = servicePenerimaan.jurnalAwal(item);
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        totalQty = 0.00;
        for (int j = 0; j < lPenerimaanDetail.size(); j++) {
            jml = jml + lPenerimaanDetail.get(j).getTotal();
            totalQty = totalQty + lPenerimaanDetail.get(j).getQty();
        }
        item.setTotal(jml);
        this.hitungSelisih();
    }

    public void hitungDiskon() {
        item.setTotal_discount(item.getPersendiskon() / 100 * item.getTotal());
        this.hitungDpp();
        this.hitungPpn();
        this.hitungPph();
        this.hitungGrandTotal();
    }

    public void hitungDpp() {
        item.setDpp(item.getTotal() - item.getTotal_discount());
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungPpn() {
        try {
            String tgl = "2022-04-01";
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
            ppn = 0.11;
            if (item.getTgl_penerimaan() != null) {
                if (item.getTgl_penerimaan().before(date1)) {
                    ppn = 0.1;
                }
            }

            if (item.getIs_ppn()) {
                item.setTotal_ppn(item.getDpp() * ppn);
            } else {
                item.setTotal_ppn(0.00);
            }
            this.hitungGrandTotal();
        } catch (Exception e) {

        }
    }

    public void hitungPpnManual() {
        item.setTotal_ppn(item.getDpp() * item.getPpnpersen() / 100);
        this.hitungGrandTotal();
    }

    public void hitungPph() {
        if (item.getIs_pph()) {
            item.setTotal_pph(item.getDpp() * 0.3 / 100);
        } else {
            item.setTotal_pph(0.00);
        }
        this.hitungGrandTotal();
    }

    public void hitungGrandTotal() {
        if (item.getTotal_pph() == null) {
            item.setTotal_pph(0.00);
        }

        if (item.getBiayalain() == null) {
            item.setBiayalain(0.00);
        }

        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getTotal_pph() + item.getBiayalain());

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
                item.setKode_user(page.getMyPegawai().getId_pegawai());
                servicePenerimaan.tambah(item, lPenerimaanDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penerimaan());
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void onPoSelect(SelectEvent event) {
        lPenerimaanDetail = new ArrayList<>();

        Po tes = (Po) event.getObject();
        Po selectedPo = servicePenerimaan.selectOnePo(tes.getNomor_po());
        item.setNomor_po(selectedPo.getNomor_po());
        item.setId_gudang(selectedPo.getId_gudang());
        item.setNama_supplier(selectedPo.getNama_supplier());
        item.setId_supplier(selectedPo.getId_supplier());
        item.setPersendiskon(selectedPo.getPersendiskon());
        item.setTotal_discount(selectedPo.getTotal_discount());
        item.setTop(selectedPo.getTop());
        item.setIs_pph(selectedPo.getIs_pph());
        item.setId_gudang(selectedPo.getId_gudang());
        item.setKeterangan("Tagihan Pembelian " + item.getNama_supplier());
        //////////// set account pembeliaan di debet
        List<PoDetail> listPoDetail = servicePenerimaan.selectPoDetail(tes.getId());
        totalQty = 0.00;
        Double totalharga = 0.00;
        for (int j = 0; j < listPoDetail.size(); j++) {
            if (listPoDetail.get(j).getDiambil() - listPoDetail.get(j).getInvoice() > 0) {
                PenerimaanDetail pd = new PenerimaanDetail();
                pd.setNo_penerimaan(item.getNo_penerimaan());
                pd.setUrut(listPoDetail.get(j).getUrut());
                pd.setId_barang(listPoDetail.get(j).getId_barang());
                pd.setNama_barang(listPoDetail.get(j).getNama_barang());
                pd.setQty(listPoDetail.get(j).getDiambil() - listPoDetail.get(j).getInvoice());
                pd.setSatuan_kecil(listPoDetail.get(j).getSatuan_kecil());
                pd.setId_satuan_kecil(listPoDetail.get(j).getId_satuan_kecil());
                pd.setHarga(listPoDetail.get(j).getHarga());
                pd.setTotal(listPoDetail.get(j).getHarga() * pd.getQty());
                totalQty = totalQty + pd.getQty();
                totalharga = totalharga + pd.getTotal();
                lPenerimaanDetail.add(pd);
            }
        }
        this.hitungJumlahTotal();
        this.hitungDiskon();
        //this.jurnalAwal(sess, item.getStatus());
        lAccGlDetail = servicePenerimaan.jurnalAwal(item);

    }

    public void onBarangSelect(PenerimaanDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setId_satuan_kecil(barangAutoComplete.getBarang().getId_satuan_kecil());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        lPenerimaanDetail.set(i, s);

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            if (item.getJenis().equals('S')) {
                servicePenerimaan.ubah(item, lPenerimaanDetail, lAccGlDetail);
            } else {
                servicePenerimaan.ubahReguler(item, lPenerimaanDetailReguler, lAccGlDetail);
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenerimaan.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onTglPengiriman() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            if (item.getTgl_pengiriman().after(new Date())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Receipt Date tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
                item.setTgl_pengiriman(null);
            } else if (item.getTgl_pengiriman() != null && item.getTop() != null) {
                if (item.getId_supplier().equals("010001")) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String date = df.format(item.getTgl_pengiriman());
                    LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedString = convertedDate.format(formatter);
                    DateTime duedate = new DateTime(df.parse(formattedString));
                    DateTime jt = duedate.plusDays(item.getTop());
                    item.setTgl_jatuh_tempo(jt.toDate());

                } else {
                    DateTime duedate = new DateTime(item.getTgl_pengiriman());
                    DateTime jt = duedate.plusDays(item.getTop());
                    item.setTgl_jatuh_tempo(jt.toDate());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onTglPengirimanSelect(SelectEvent event) {
        onTglPengiriman();
    }

    public void onTglPengirimanChange(AjaxBehaviorEvent event) {
        onTglPengiriman();
    }

    public void onTopChange() {
        onTglPengiriman();
    }

    public void onTglAp() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            if (item.getStatus() != null) {
                DateFormat bln = new SimpleDateFormat("MM");
                Penerimaan penerimaan = servicePenerimaan.onLoad(item.getNo_penerimaan());
                if (!bln.format(item.getTgl_penerimaan()).equals(bln.format(penerimaan.getTgl_penerimaan()))) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ap Date tidak boleh beda bulannya !!!", ""));
                    item.setTgl_penerimaan(penerimaan.getTgl_penerimaan());
                } else {
                    if (item.getJenis().equals('S')) {
                        hitungPpn();
                    }
                }
            }
            if (item.getTgl_penerimaan().after(new Date())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ap Date tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
                item.setTgl_penerimaan(null);
            } else {
                if (item.getJenis().equals('S')) {
                    hitungPpn();
                }
            }
        } catch (Exception e) {

        }

    }

    public void onTglApSelect(SelectEvent event) {
        onTglAp();
    }

    public void onTglApChange(AjaxBehaviorEvent event) {
        onTglAp();
    }

    public void onTopSelect() {
        if (item.getTgl_pengiriman() != null && item.getTop() != null) {
            DateTime duedate = new DateTime(item.getTgl_pengiriman());
            DateTime jt = duedate.plusDays(item.getTop());
            item.setTgl_jatuh_tempo(jt.toDate());
        }
    }

    ////////////////////////////////////////////////// REGULER ///////////////////////////////////////////
    public void onPoSelectReguler(SelectEvent event) {
        lPenerimaanDetailReguler = new ArrayList<>();
        Po selectedPo = new Po();
        Po tes = (Po) event.getObject();
        selectedPo = servicePenerimaan.selectOnePo(tes.getNomor_po());
        item.setNomor_po(selectedPo.getNomor_po());
        item.setNama_supplier(selectedPo.getNama_supplier());
        item.setId_supplier(selectedPo.getId_supplier());
        item.setTotal(selectedPo.getTotal());
        item.setPersendiskon(selectedPo.getPersendiskon());
        item.setTotal_discount(selectedPo.getTotal_discount());
        item.setDpp(item.getTotal() - item.getTotal_discount());
        item.setTotal_ppn(selectedPo.getTotal_ppn());
        item.setGrandtotal(item.getTotal() - item.getTotal_discount() + item.getTotal_ppn());
        item.setTop(selectedPo.getTop());
        item.setIs_pph(selectedPo.getIs_pph());
        item.setTotal_pph(selectedPo.getTotal_pph());
        item.setId_gudang(selectedPo.getId_gudang());
        item.setKeterangan("Hutang " + item.getNama_supplier());
        //////////// set account pembeliaan di debet
        // this.jurnalAwal(sess);
        List<PoDetailReguler> listPoDetailReguler = servicePenerimaan.selectPoDetailReguler(selectedPo.getId());
        totalQty = 0.00;
        for (int j = 0; j < listPoDetailReguler.size(); j++) {
            PenerimaanDetailReguler pd = new PenerimaanDetailReguler();
            pd.setNo_penerimaan(item.getNo_penerimaan());
            pd.setUrut(listPoDetailReguler.get(j).getUrut());
            pd.setNm_barang(listPoDetailReguler.get(j).getNm_barang());
            pd.setQty(listPoDetailReguler.get(j).getQty());
            pd.setSatuan(listPoDetailReguler.get(j).getSatuan());
            pd.setHarga(listPoDetailReguler.get(j).getHarga());
            pd.setTotal(listPoDetailReguler.get(j).getTotal());
            pd.setDiorder(listPoDetailReguler.get(j).getQty());
            totalQty = totalQty + pd.getQty();
            lPenerimaanDetailReguler.add(pd);

        }
        lAccGlDetail = servicePenerimaan.jurnalAwalReguler(item);
    }

    public void onPPSelectReguler(SelectEvent event) {
        lPenerimaanDetailReguler = new ArrayList<>();
        PermintaanPembelian pp = (PermintaanPembelian) event.getObject();
        item.setNo_pp(pp.getNo_pp());

//        PermintaanPembelian selectedPp=servicePenerimaan.selectPp(item.getNo_pp());
//        item.setKeterangan(selectedPp.getKeterangan());
//        z//////////// set account pembeliaan di debet
        // this.jurnalAwal(sess);
        List<PermintaanPembelianDetail> listPPDetailReguler = servicePenerimaan.selectPpDetail(item.getNo_pp());
        totalQty = 0.00;
        for (int j = 0; j < listPPDetailReguler.size(); j++) {
            PenerimaanDetailReguler pd = new PenerimaanDetailReguler();
            pd.setNo_penerimaan(item.getNo_penerimaan());
            pd.setUrut(listPPDetailReguler.get(j).getUrut());
            pd.setNm_barang(listPPDetailReguler.get(j).getId_barang());
            pd.setQty(listPPDetailReguler.get(j).getJumlah_order());
            pd.setSatuan(listPPDetailReguler.get(j).getSatuan());
            pd.setHarga(0.00);
            pd.setTotal(0.00);
            lPenerimaanDetailReguler.add(pd);

        }
        //lAccGlDetail = servicePenerimaan.jurnalAwalReguler(item);
    }

    public List<PenerimaanDetailReguler> getDataPenerimaanDetailReguler() {
        return lPenerimaanDetailReguler;

    }

    public void extendReguler() {
        PenerimaanDetailReguler sPReguler = new PenerimaanDetailReguler();
        lPenerimaanDetailReguler.add(sPReguler);
    }

    public void onDeleteClickedReguler(PenerimaanDetailReguler itemreguler) {
        lPenerimaanDetailReguler.remove(itemreguler);
        this.hitungJumlahTotalReguler();
        this.hitungDiskonReguler();
        this.hitungDppReguler();
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();

    }

    public void hitungTotalReguler(PenerimaanDetailReguler s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }

        s.setTotal(s.getHarga() * s.getQty());
        lPenerimaanDetailReguler.set(i, s);
        this.hitungJumlahTotalReguler();
        this.hitungDiskonReguler();
        this.hitungDppReguler();
        this.hitungPpnManual();
        this.hitungGrandTotalReguler();
    }

    public void hitungJumlahTotalReguler() {
        Double jml;
        jml = 0.00;
        totalQty = 0.00;
        for (int j = 0; j < lPenerimaanDetailReguler.size(); j++) {
            jml = jml + lPenerimaanDetailReguler.get(j).getTotal();
            totalQty = totalQty + lPenerimaanDetailReguler.get(j).getQty();
        }
        item.setTotal(jml);

    }

    public void hitungDiskonReguler() {
        item.setTotal_discount(item.getPersendiskon() / 100 * item.getTotal());
        this.hitungDppReguler();
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();
    }

    public void hitungDppReguler() {
        item.setDpp(item.getTotal() - item.getTotal_discount());
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungPpnReguler() {
        if (item.getIs_ppn()) {
            item.setTotal_ppn(item.getDpp() * 10 / 100);
        } else {
            item.setTotal_ppn(0.00);
        }
        this.hitungGrandTotalReguler();
    }

    public void hitungPpnManualReguler() {
        item.setTotal_ppn(item.getDpp() * item.getPpnpersen() / 100);
        this.hitungGrandTotalReguler();
    }

    public void hitungPphManual() {
        item.setTotal_pph(item.getDpp() * item.getPphpersen() / 100);
    }

    public void hitungPphManualReguler() {
        item.setTotal_pph(item.getDpp() * item.getPphpersen() / 100);
        lAccGlDetail = servicePenerimaan.jurnalAwalReguler(item);
    }

    public void hitungGrandTotalReguler() {
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getBiayalain());
        lAccGlDetail = servicePenerimaan.jurnalAwalReguler(item);
    }

    public void tambahReguler() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;

        for (int i = 0; i < lPenerimaanDetailReguler.size(); i++) {
            if (lPenerimaanDetailReguler.get(i).getHarga() == null || lPenerimaanDetailReguler.get(i).getHarga() == 0
                    || lPenerimaanDetailReguler.get(i).getQty() == 0 || lPenerimaanDetailReguler.get(i).getQty() == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Dan harga " + lPenerimaanDetailReguler.get(i).getNm_barang() + " Harus diisi !!!", ""));
                benar = false;
            }
        }

        /////// untuk tes jurnal
        if (benar) {
            if (lAccGlDetail.size() == 0 || !jumlahDebit.equals(jumlahKredit)) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal Salah", ""));
                benar = false;
            }
        }
        if (benar) {
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal Salah", ""));
                    benar = false;
                }
            }
        }

        if (benar) {

            try {
                this.nomorurut();
                item.setStatus('D');
                item.setKode_user(page.getMyPegawai().getId_pegawai());
                servicePenerimaan.tambahReguler(item, lPenerimaanDetailReguler, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                if (item.getJenis().equals('R')) {
                    context.getExternalContext().redirect("./editreguler.jsf?id=" + item.getNo_penerimaan());
                } else if (item.getJenis().equals('A')) {
                    context.getExternalContext().redirect("./editap.jsf?id=" + item.getNo_penerimaan());
                } else if (item.getJenis().equals('P')) {
                    context.getExternalContext().redirect("./editregulerpo.jsf?id=" + item.getNo_penerimaan());
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }

    }

    public void onSupplierRegulerSelect(SelectEvent event) {
        try {
            Supplier selectedSupplier = (Supplier) event.getObject();
            item.setId_supplier(selectedSupplier.getId());
            item.setTop(selectedSupplier.getTop());
            item.setNama_supplier(selectedSupplier.getSupplier());
            item.setKeterangan("Hutang " + item.getNama_supplier());
            if (item.getTgl_pengiriman() != null && item.getTop() != null) {
                DateTime duedate = new DateTime(item.getTgl_pengiriman());
                DateTime jt = duedate.plusDays(item.getTop());
                item.setTgl_jatuh_tempo(jt.toDate());
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getNo_penerimaan());
    }
}
