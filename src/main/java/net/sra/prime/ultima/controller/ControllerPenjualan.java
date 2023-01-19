package net.sra.prime.ultima.controller;

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
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.Penjualan;
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.entity.Dotbl;
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.PenjualanDetailReguler;
import net.sra.prime.ultima.entity.PenjualanDo;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.service.ServicePenjualan;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.DoAutoComplete;
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
public class ControllerPenjualan implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Penjualan> lPenjualan = new ArrayList<>();
    private Penjualan item;
    private List<PenjualanDetail> lPenjualanDetail = new ArrayList<>();
    private List<PenjualanDetailReguler> lPenjualanDetailReguler = new ArrayList<>();
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private Character status;
    private String tahun;
    private Integer bulan;
    private List<String> lMessage = new ArrayList<>();
    private Double ppn;

//    @Inject
//    private CustomerAutoComplete customerAutoComplete;
    @Inject
    private DoAutoComplete doAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private BatchModel batchModel;

    @Inject
    private Page page;

    @Autowired
    ServicePenjualan servicePenjualan;

    @PostConstruct
    public void init() {
        item = new Penjualan();
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
        bulan = 0;
        if (tahun == null) {
            tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        }
    }

    public void initItem() {
        item = new Penjualan();
        Date date = new Date();
        // item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setTotal_bayar(0.00);
        item.setPersendiskon(0.00);
        item.setBiayalain(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setIsbank(Boolean.FALSE);
        doAutoComplete.setSelectedDotbl(null);

    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePenjualan.noMax(Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_penjualan("0001/INV/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%04d", nomor);
            item.setNo_penjualan(noMax + "/INV/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        if (status == null) {
            status = 'D';
        } else if (status.equals('9')) {
            status = null;
        }
        lPenjualan = servicePenjualan.onLoadList(awal, akhir, status, null);
    }

    public void onLoadListAdmin() {
        if (status == null) {
            status = 'D';
        } else if (status.equals('9')) {
            status = null;
        }
        lPenjualan = servicePenjualan.onLoadListAdmin(awal, akhir, page.getMyKantor().getId_kantor_cabang());
    }

    public void onLoadListAr() {
        if (status == null) {
            status = 'A';
        } else if (status.equals('9')) {
            status = null;
        }
        lPenjualan = servicePenjualan.onLoadList(awal, akhir, status, 'D');
    }

    public List<Penjualan> getDataPenjualan() {
        return lPenjualan;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (item.getJenis() == null) {
                servicePenjualan.delete(nomor);
            } else {
                servicePenjualan.deleteReguler(nomor);
            }
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public List<PenjualanDetail> getDataPenjualanDetail() {
        return lPenjualanDetail;
    }

    public List<PenjualanDetailReguler> getDataPenjualanDetailReguler() {
        return lPenjualanDetailReguler;
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public void extend() {
        lPenjualanDetail.add(new PenjualanDetail());
    }

    public void onDeleteClicked(PenjualanDetail item) {
        lPenjualanDetail.remove(item);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis() == null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penjualan());
        } else {
            context.getExternalContext().redirect("./editReguler.jsf?id=" + item.getNo_penjualan());
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis() == null) {
            context.getExternalContext().redirect("./invoice-edit.jsf?id=" + item.getNo_penjualan());
        } else {
            context.getExternalContext().redirect("./invoice-editReguler.jsf?id=" + item.getNo_penjualan());
        }
    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view.jsf?id=" + item.getNo_penjualan());
    }

    public void viewAr() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis() == null) {
            context.getExternalContext().redirect("./ar.jsf?id=" + item.getNo_penjualan());
        } else {
            context.getExternalContext().redirect("./ar_reguler.jsf?id=" + item.getNo_penjualan());
        }
    }

    public void editTerimaInvoice() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getStatus().equals('P')) {
            if (item.getJenis() == null) {
                context.getExternalContext().redirect("./terimainvoice.jsf?id=" + item.getNo_penjualan());
            } else {
                context.getExternalContext().redirect("./terimainvoice_reguler.jsf?id=" + item.getNo_penjualan());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tidak bisa memilih AR karena AR belum diposting !!!", ""));
        }
    }

    public void revisi() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./revisi.jsf?id=" + item.getNo_penjualan());
    }

    public void onRevisi() {
        this.onLoad();
        item.setNo_penjualan_lama(item.getNo_penjualan());
        this.nomorurut();
        item.setTop(item.getDurasi());
    }

    public void onLoad() {
        try {
            item = servicePenjualan.onLoad(item.getNo_penjualan());
            List<PenjualanDo> lPenjualanDo = servicePenjualan.selectOnePenjualanDo(item.getNo_penjualan());
            List<Dotbl> lTmpDotbl = new ArrayList<>();
            if (lPenjualanDo != null) {
                for (int i = 0; i < lPenjualanDo.size(); i++) {
                    Dotbl dotbl = new Dotbl();
                    dotbl.setNomor(lPenjualanDo.get(i).getNo_do());
                    lTmpDotbl.add(dotbl);
                }
                doAutoComplete.setSelectedDotbl(lTmpDotbl);
            } else {
                doAutoComplete.setSelectedDotbl(null);
            }
            lPenjualanDetail = servicePenjualan.onLoadDetail(item.getNo_penjualan());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadArReguler() {
        try {
            item = servicePenjualan.onLoadReguler(item.getNo_penjualan());
            lPenjualanDetailReguler = servicePenjualan.onLoadDetailReguler(item.getNo_penjualan());
            customerAutoComplete.setCustomer(servicePenjualan.selectOneCustomer(item.getId_customer()));
            lAccGlDetail = servicePenjualan.jurnalAwalReguler(item);
            this.hitungSelisih();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadAr() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item = servicePenjualan.onLoad(item.getNo_penjualan());
            if (item.getTgl_jatuh_tempo() == null) {
                changeDueDatebyTglInvoice();
            }
            List<PenjualanDo> lPenjualanDo = servicePenjualan.selectOnePenjualanDo(item.getNo_penjualan());
            List<Dotbl> lTmpDotbl = new ArrayList<>();
            if (lPenjualanDo != null) {
                for (int i = 0; i < lPenjualanDo.size(); i++) {
                    Dotbl dotbl = new Dotbl();
                    dotbl.setNomor(lPenjualanDo.get(i).getNo_do());
                    lTmpDotbl.add(dotbl);
                }
                doAutoComplete.setSelectedDotbl(lTmpDotbl);
            } else {
                doAutoComplete.setSelectedDotbl(null);
            }
            lPenjualanDetail = servicePenjualan.onLoadDetail(item.getNo_penjualan());
            lAccGlDetail = servicePenjualan.jurnalAwal(item, lPenjualanDetail);
            this.hitungSelisih();
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void onLoadArTerimaInvoice() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item = servicePenjualan.onLoad(item.getNo_penjualan());
            List<PenjualanDo> lPenjualanDo = servicePenjualan.selectOnePenjualanDo(item.getNo_penjualan());
            List<Dotbl> lTmpDotbl = new ArrayList<>();
            if (lPenjualanDo != null) {
                for (int i = 0; i < lPenjualanDo.size(); i++) {
                    Dotbl dotbl = new Dotbl();
                    dotbl.setNomor(lPenjualanDo.get(i).getNo_do());
                    lTmpDotbl.add(dotbl);
                }
                doAutoComplete.setSelectedDotbl(lTmpDotbl);
            } else {
                doAutoComplete.setSelectedDotbl(null);
            }
            lPenjualanDetail = servicePenjualan.onLoadDetail(item.getNo_penjualan());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void hitungDiskonPersen(PenjualanDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getDiskonpersen() == null) {
            s.setDiskonpersen(0.00);
        }
        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lPenjualanDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungDiskonRp(PenjualanDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lPenjualanDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungTotal(PenjualanDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }

        s.setTotal((s.getHarga() - s.getDiskonrp() + s.getAdditional_charge()) * s.getQty());
        lPenjualanDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPenjualanDetail.size(); j++) {
            jml = jml + lPenjualanDetail.get(j).getTotal();
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
        try {
            String tgl = "2022-04-01";
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
            ppn = 0.11;
            if (item.getTanggal() != null) {
                if (item.getTanggal().before(date1)) {
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

    public void changeCash() {
        item.setTop(0);
    }

    public void changeIsbank() {
        if (!item.getIsbank()) {
            item.setIdbank(null);
        }
    }

    public void changeDueDate() {
        DateTime duedate = new DateTime(item.getTgl_terimainvoice());
        DateTime jt = duedate.plusDays(item.getTop());
        item.setTgl_jatuh_tempo(jt.toDate());
    }

    public void changeDueDatebyTglInvoice() {
        if (item.getTop() == null) {
            item.setTop(0);
        }
        DateTime duedate = new DateTime(item.getTanggal());
        DateTime jt = duedate.plusDays(item.getTop());
        item.setTgl_jatuh_tempo(jt.toDate());
    }

    public void hitungGrandTotal() {
        if (item.getBiayalain() == null) {
            item.setBiayalain(0.00);
        }
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getBiayalain());
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.nomorurut();
            item.setStatus('D');
            item.setKode_user(page.getMyPegawai().getId_pegawai());
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            List<Dotbl> lDoaTmp = doAutoComplete.getSelectedDotbl();

            servicePenjualan.tambah(item, lPenjualanDetail, lDoaTmp);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penjualan());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onDoSelect() throws Exception {
        lPenjualanDetail = new ArrayList<>();

        List<Dotbl> lDo = doAutoComplete.getSelectedDotbl();
        List<Dotbl> lDotmp = new ArrayList<>();
        Dotbl selectTbl = new Dotbl();
        FacesContext context = FacesContext.getCurrentInstance();

        for (int k = 0; k < lDo.size(); k++) {
            if (k > 0) {
                if (!lDo.get(k).getNo_so().equals(lDo.get(k - 1).getNo_so())) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informasi", "Bukan dari PO yang Sama"));
                } else {
                    lDotmp.add(lDo.get(k));

                }

            } else {
                selectTbl = servicePenjualan.selectOneDo(lDo.get(k).getNomor());
                item.setId_customer(selectTbl.getId_customer());
                item.setCustomer(selectTbl.getCustomer());
                item.setTop(selectTbl.getTop());
                item.setKode_mata_uang(selectTbl.getKode_mata_uang());
                item.setKepada(selectTbl.getKepada());
                item.setId_gudang(selectTbl.getId_gudang());
                item.setPersendiskon(selectTbl.getPersendiskon());
                //item.setNo_penjualan(selectTbl.getNomor());
                item.setReferensi(selectTbl.getReferensi());
                item.setTop(selectTbl.getTop());

                item.setTotal(0.00);
                item.setPersendiskon(selectTbl.getPersendiskon());
                item.setIs_ppn(selectTbl.getIs_ppn());
                item.setId_salesman(selectTbl.getId_salesman());
                lDotmp.add(lDo.get(k));
            }

        }
        //diambilnya dari tabel SO bukan tabel DOtbl
        List<SoDetail> lSo = servicePenjualan.selectOneSo(selectTbl.getNo_so());
        doAutoComplete.setSelectedDotbl(lDotmp);

        for (int j = 0; j < lSo.size(); j++) {

            Double jml = 0.00;
            DoDetail doDetail = new DoDetail();
            for (int l = 0; l < lDotmp.size(); l++) {
                doDetail = servicePenjualan.selectSumDoDetail(lDotmp.get(l).getNomor(), lSo.get(j).getId_barang());
                if (doDetail == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", lSo.get(j).getId_barang()));
                } else {
                    jml = jml + doDetail.getJml();
                }
            }

            if (jml != 0.00) {
                //DoDetail doDetail = mreferensiDetail.selectSum(id_lama, lSo.get(j).getId_barang());
                PenjualanDetail pd = new PenjualanDetail();
                pd.setNo_penjualan(item.getNo_penjualan());
                pd.setUrut(lSo.get(j).getUrut());
                pd.setId_barang(lSo.get(j).getId_barang());
                pd.setNama_barang(lSo.get(j).getNama_barang());
                pd.setIsi_satuan(lSo.get(j).getIsi_satuan());
                pd.setQty(jml);
                pd.setSatuan_kecil(lSo.get(j).getSatuan_kecil());
                pd.setSatuan_besar(lSo.get(j).getSatuan_besar());
                pd.setHarga(lSo.get(j).getHarga());
                pd.setDiskonpersen(lSo.get(j).getDiskonpersen());
                pd.setDiskonrp(lSo.get(j).getDiskonrp());
                pd.setAdditional_charge(lSo.get(j).getAdditional_charge());
                if (lSo.get(j).getAdditional_charge() == null) {
                    lSo.get(j).setAdditional_charge(0.00);
                }
                pd.setTotal((lSo.get(j).getHarga() - lSo.get(j).getDiskonrp() + lSo.get(j).getAdditional_charge()) * jml);
                item.setTotal(item.getTotal() + pd.getTotal());
                lPenjualanDetail.add(pd);
            }
        }
        item.setTotal_discount(item.getTotal() * item.getPersendiskon() / 100);
        item.setDpp(item.getTotal() - item.getTotal_discount());
        if (item.getIs_ppn()) {
            item.setTotal_ppn(item.getDpp() * 10 / 100);
        } else {
            item.setTotal_ppn(0.00);
        }
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getBiayalain());

    }

    public void onBarangSelect(PenjualanDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setDiskonpersen(0.00);
        s.setDiskonrp(0.00);
        lPenjualanDetail.set(i, s);

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Character st = item.getStatus();
        try {
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }

            List<Dotbl> lDoaTmp = doAutoComplete.getSelectedDotbl();
            if (status != null) {
                item.setStatus(status);

            }
            servicePenjualan.ubah(item, lPenjualanDetail, lDoaTmp, status);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            item.setStatus(st);
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
                if (item.getJenis() == null) {
                    servicePenjualan.posting(item, lPenjualanDetail, lAccGlDetail);
                } else {
                    servicePenjualan.postingReguler(item, lAccGlDetail);
                }
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diposting"));
            } catch (Exception e) {
                item.setStatus(st);
                //e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah", ""));
        }
    }

    public void maintenancePosting() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePenjualan.maintenancePosting(item);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diposting"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void unPosting(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                benar = false;
            }
        }
        Double jmlDebit = Math.round(jumlahDebit * 100.0) / 100.0;
        Double jmlKredit = Math.round(jumlahKredit * 100.0) / 100.0;
        if (benar && jmlDebit.equals(jmlKredit)) {
            Character st = item.getStatus();
            try {
                item.setStatus(status);
                servicePenjualan.posting(item, lPenjualanDetail, lAccGlDetail);
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

    public void terimainvoice() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePenjualan.terimaInvoice(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diposting"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
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
        context.getExternalContext().redirect("./cetakinvoice.jsf?id=" + item.getNo_penjualan());
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
            item.setStatus(status);
            servicePenjualan.ubahStatus(item, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    ////////////////// Reguler////////////
    public void initItemReguler() {
        item = new Penjualan();
        ppn = 0.11;
        Date date = new Date();
        // item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setTotal_bayar(0.00);
        item.setPersendiskon(0.00);
        item.setBiayalain(0.00);
        item.setIs_ppn(Boolean.TRUE);
        lPenjualanDetailReguler = new ArrayList<>();
        lPenjualanDetailReguler.add(new PenjualanDetailReguler());

    }

    public void extendreguler() {
        lPenjualanDetailReguler.add(new PenjualanDetailReguler());
    }

    public void hitungTotalReguler(PenjualanDetailReguler s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }
        if (s.getDiskonrp() == null) {
            s.setDiskonrp(0.00);
        }
        s.setTotal((s.getHarga() - s.getDiskonrp()) * s.getQty());
        lPenjualanDetailReguler.set(i, s);
        this.hitungJumlahTotalReguler();
        this.hitungDiskonReguler();
        this.hitungDppReguler();
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();
    }

    public void hitungJumlahTotalReguler() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPenjualanDetailReguler.size(); j++) {
            if (lPenjualanDetailReguler.get(j).getTotal() != null) {
                jml = jml + lPenjualanDetailReguler.get(j).getTotal();
            }
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
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();
    }

    public void hitungPpnReguler() {
        try {
            String tgl = "2022-04-01";
            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(tgl);
            ppn = 0.11;
            if (item.getTanggal() != null) {
                if (item.getTanggal().before(date1)) {
                    ppn = 0.1;
                }
            }

            if (item.getIs_ppn()) {
                item.setTotal_ppn(item.getDpp() * ppn);
            } else {
                item.setTotal_ppn(0.00);
            }
            this.hitungGrandTotalReguler();
        } catch (Exception e) {

        }
    }

    public void hitungGrandTotalReguler() {
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn());
    }

    public void tambahReguler() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.nomorurut();
            item.setStatus('D');
            item.setJenis('R');
            item.setKode_user(page.getMyPegawai().getId_pegawai());
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            servicePenjualan.tambahReguler(item, lPenjualanDetailReguler);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./editReguler.jsf?id=" + item.getNo_penjualan());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onLoadReguler() {
        try {
            item = servicePenjualan.onLoadReguler(item.getNo_penjualan());
            if (item.getTgl_jatuh_tempo() == null) {
                changeDueDatebyTglInvoice();
            }
            lPenjualanDetailReguler = servicePenjualan.onLoadDetailReguler(item.getNo_penjualan());
            Customer cst = servicePenjualan.selectOneCustomer(item.getId_customer());
            customerAutoComplete.setCustomer(cst);
            lAccGlDetail = servicePenjualan.jurnalAwalReguler(item);
            this.hitungSelisih();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadRegulerTerimaInvoice() {
        try {
            item = servicePenjualan.onLoadReguler(item.getNo_penjualan());
            lPenjualanDetailReguler = servicePenjualan.onLoadDetailReguler(item.getNo_penjualan());
            customerAutoComplete.setCustomer(servicePenjualan.selectOneCustomer(item.getId_customer()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ubahReguler(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Character st = item.getStatus();
        try {
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            if (status != null) {
                item.setStatus(status);
            }
            item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
            servicePenjualan.ubahReguler(item, lPenjualanDetailReguler);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            if (status != null) {
                item.setStatus(st);
            }
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void cetakReguler() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakinvoicereguler.jsf?id=" + item.getNo_penjualan());
    }

    public void onCustomerSelect(SelectEvent event) {
        Customer tes = (Customer) event.getObject();
        item.setTop(tes.getTop());
        item.setId_customer(tes.getId_kontak());
        item.setCustomer(tes.getCustomer());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getStatus() != null) {
                DateFormat bln = new SimpleDateFormat("MM");
                Penjualan penjualan = servicePenjualan.onLoad(item.getNo_penjualan());
                if (!bln.format(item.getTanggal()).equals(bln.format(penjualan.getTanggal()))) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                    item.setTanggal(penjualan.getTanggal());
                } else {
                    hitungPpn();
                }
            }
            if (item.getTanggal().after(new Date())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
                item.setTanggal(null);
            } else {
                hitungPpn();
            }
        } catch (Exception e) {

        }

    }

    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
    }

    public void creditnote() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.nomorurut();
            item.setStatus('D');
            if (item.getBiayalain() == null) {
                item.setBiayalain(0.00);
            }
            List<Dotbl> lDoaTmp = doAutoComplete.getSelectedDotbl();
            servicePenjualan.tambah(item, lPenjualanDetail, lDoaTmp);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penjualan());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
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

    public void postingAll() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            batchModel.setProgress(0);

            lMessage = new ArrayList<>();
            Boolean benar;
            String tes = "tes";
            List<PenjualanDetail> lPenjualanDetails = new ArrayList<>();
            Double jumlahDebit;
            Double jumlahKredit;
            Double selisih;

            List<Penjualan> lPenjualans = servicePenjualan.selectPenjualanperBulan(Integer.parseInt(tahun), bulan);
            if (lPenjualans.size() == 0) {
                batchModel.setProgress(Math.round(100 * (float) 1 / (float) 1));
                lMessage.add("Data tidak ditemukan !!");
            }
            for (int j = 0; j < lPenjualans.size(); j++) {
                batchModel.setProgress(Math.round(100 * (float) (j + 1) / (float) lPenjualans.size()));
                if (lPenjualans.get(j).getJenis() == null) {
                    lPenjualanDetails = servicePenjualan.onLoadDetail(lPenjualans.get(j).getNo_penjualan());

                    tes = CekHpp(lPenjualans.get(j), lPenjualanDetails);
                    if (tes == null) {
                        //if (servicePenjualan.CekHpp(lPenjualans.get(j), lPenjualanDetails)) {
                        lAccGlDetail = servicePenjualan.jurnalAwal(lPenjualans.get(j), lPenjualanDetails);
                        jumlahDebit = 0.00;
                        jumlahKredit = 0.00;

                        for (int i = 0; i < lAccGlDetail.size(); i++) {
                            if (lAccGlDetail.get(i).getDebit() != null) {
                                jumlahDebit = jumlahDebit + lAccGlDetail.get(i).getDebit();
                            }

                            if (lAccGlDetail.get(i).getCredit() != null) {
                                jumlahKredit = jumlahKredit + lAccGlDetail.get(i).getCredit();
                            }
                            jumlahDebit = Math.round(jumlahDebit * 100.0) / 100.0;
                            jumlahKredit = Math.round(jumlahKredit * 100.0) / 100.0;
                        }

                        benar = true;
                        if (lAccGlDetail.size() <= 0) {
                            benar = false;
                        } else {
                            for (int i = 0; i < lAccGlDetail.size(); i++) {
                                if (lAccGlDetail.get(i).getId_account() == null) {
                                    benar = false;
                                    lMessage.add("Invoice Nomor : " + lPenjualans.get(j).getNo_penjualan() + " Gagal diposting karena Jurnal salah !!");
                                    break;
                                } else if ((lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                                    benar = false;
                                    lMessage.add("Invoice Nomor : " + lPenjualans.get(j).getNo_penjualan() + " Gagal diposting karena Jurnal Jurnal tidak balance !!");
                                    break;
                                }
                            }
                        }

                        if (benar && jumlahDebit.equals(jumlahKredit)) {
                            lPenjualans.get(j).setStatus('P');
                            tes = submitPostingAll(lPenjualans.get(j), lPenjualanDetails, lAccGlDetail, 'A');
                            lMessage.add(tes);
                        }
                    } else {
                        lMessage.add(tes);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public String submitPostingAll(Penjualan item, List<PenjualanDetail> lPenjualanDetail, List<AccGlDetail> lAccGlDetail, Character tanda) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            servicePenjualan.PostingAll(item, lPenjualanDetail, lAccGlDetail);
        } catch (Exception e) {
            return "Invoice nomor :" + item.getNo_penjualan() + " Gagal diposting karena  !! " + e.getMessage();
        }
        return "Invoice nomor :" + item.getNo_penjualan() + " Berhasil diposting !!";
    }

    public String CekHpp(Penjualan item, List<PenjualanDetail> lPenjualanDetail) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            servicePenjualan.CekHpp(item, lPenjualanDetail);
        } catch (Exception e) {
            return "Invoice nomor :" + item.getNo_penjualan() + " Gagal diposting karena  !! " + e.getMessage();
        }
        return null;
    }

}
