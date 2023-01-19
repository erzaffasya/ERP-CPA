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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HargaBeliDetil;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.PermintaanPembelianPesan;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.entity.PoDetailReguler;
import net.sra.prime.ultima.entity.Shipto;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.entity.SupplierKontak;
import net.sra.prime.ultima.service.ServicePo;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SupplierAutoComplete;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPo implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Po> lPo;
    private Po item;
    private PoDetailReguler itemdetailreguler;
    private PoDetailReguler sPReguler;
    private List<PoDetail> lPoDetail = new ArrayList<>();
    private List<PoDetailReguler> lPoDetailReguler = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character statuspo;
    private String id_gudang;
    private String suppliernya;
    private Double ppn;
    private String id_barang;
    private String barang;
    private String id_pegawai;

    @Inject
    private SupplierAutoComplete sac;

    @Inject
    private Options options;

    @Inject
    private Page page;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServicePo servicePo;

    @PostConstruct
    public void init() {
        item = new Po();
        lPo = new ArrayList<>();
        suppliernya = "";
        
        Pegawai pegawai = page.getMyPegawai();
        id_pegawai = pegawai.getId_pegawai();
        statuspo = 'D';
        if(pegawai.getId_jabatan_new() == 101){
            statuspo = 'C';
        }else if(pegawai.getId_jabatan_new() == 150){
            statuspo = 'S';
        }
    }

    private void initItem() {
        Integer ip = item.getId_intruksi_po();
        item = new Po();
        item.setId_intruksi_po(ip);
        IntruksiPo intruksiPo = servicePo.intruksiPo(ip);
        item.setNo_intruksi_po(intruksiPo.getNo_intruksi_po());
        item.setId_gudang(intruksiPo.getId_gudang());
        item.setGudang(intruksiPo.getGudang());
        item.setId_supplier("010001");
        sac.setSupplier(servicePo.selectOneSupplier(item.getId_supplier()));
        item.setNo_forecast(intruksiPo.getNo_forecast());
        Customer customer = new Customer();
        customerAutoComplete.setCustomer(customer);
        this.onSupplierSelect();
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setTotal_pph(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setIs_pph(Boolean.TRUE);
        item.setPurchasing(id_pegawai);
        item.setChecked1(servicePo.getPurchasing());
        item.setDirector("CPA-002");
        item.setJenis_po("IP");
        lPoDetail = servicePo.onIntruksiPoSelect(item.getId_intruksi_po());
        options.setId_supplier(item.getId_supplier());
        options.setId_customer(null);

        this.initPegawai();
    }

    public void initManual() {
        item = new Po();
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setTotal_pph(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setIs_pph(Boolean.FALSE);
        item.setJenis_po("MA");
        lPoDetail = new ArrayList<>();
        lPoDetail.add(new PoDetail());
        options.setId_supplier(item.getId_supplier());
        item.setPurchasing(id_pegawai);
        item.setChecked1(servicePo.getPurchasing());
        item.setDirector("CPA-002");
        this.initPegawai();
    }

    public void onLoadList() {
        try{
        Pegawai pegawai = page.getMyPegawai();
        lPo = servicePo.onLoadList(awal, akhir, statuspo, 'A',pegawai.getId_pegawai(),pegawai.getId_jabatan_new());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListVew() {
        try{
        Pegawai pegawai = page.getMyPegawai();
        lPo = servicePo.onLoadList(awal, akhir, statuspo, 'M',pegawai.getId_pegawai(),pegawai.getId_jabatan_new());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListAdminDashboard() {
        init();
        Pegawai pegawai = page.getMyPegawai();
        lPo = servicePo.onLoadList(awal, akhir, 'C', 'M',pegawai.getId_pegawai(),pegawai.getId_jabatan_new());
    }

    public void onLoadListPoGudang() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPo = servicePo.onLoadListPoGudang(awal, akhir, id_pegawai);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onInit() {
        initItem();
    }

    public List<Po> getDataPo() {
        return lPo;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePo.delete(id);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PoDetail> getDataPoDetail() {
        return lPoDetail;
    }

    public List<PoDetailReguler> getDataPoDetailReguler() {
        return lPoDetailReguler;
    }

    public void extend() {
        lPoDetail.add(new PoDetail());
    }

    public void onDeleteClicked(PoDetail poDetail) {
        lPoDetail.remove(poDetail);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis_po().equals("IP")) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("PP")) {
            context.getExternalContext().redirect("./editreguler.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("MA")) {
            context.getExternalContext().redirect("./edit_manual.jsf?id=" + item.getId());
        }

    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis_po().equals("IP")) {
            context.getExternalContext().redirect("./po-edit.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("PP")) {
            context.getExternalContext().redirect("./po-editreguler.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("MA")) {
            context.getExternalContext().redirect("./po-editmanual.jsf?id=" + item.getId());
        }

    }

    public void updatebyNumber(String nomor) throws IOException {
        item = servicePo.selectOne(nomor);
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis_po().equals("IP")) {
            context.getExternalContext().redirect("../transaksi/pembelian/po/edit.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("PP")) {
            context.getExternalContext().redirect("../transaksi/pembelian/po/editreguler.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("MA")) {
            context.getExternalContext().redirect("../transaksi/pembelian/po/edit_manual.jsf?id=" + item.getId());
        }

    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item.getJenis_po().equals("IP")) {
            context.getExternalContext().redirect("./view.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("PP")) {
            context.getExternalContext().redirect("./viewreguler.jsf?id=" + item.getId());
        } else if (item.getJenis_po().equals("MA")) {
            context.getExternalContext().redirect("./view_manual.jsf?id=" + item.getId());
        }
    }

    public void viewDashboard(Po po) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (po.getJenis_po().equals("IP")) {
            context.getExternalContext().redirect("./transaksi/pembelian/po/view.jsf?id=" + po.getId());
        } else if (po.getJenis_po().equals("PP")) {
            context.getExternalContext().redirect("./transaksi/pembelian/po/viewreguler.jsf?id=" + po.getId());
        } else if (po.getJenis_po().equals("MA")) {
            context.getExternalContext().redirect("./transaksi/pembelian/po/view_manual.jsf?id=" + po.getId());
        }
    }

    public void viewPo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./viewpo.jsf?id=" + item.getId());

    }

    public void onLoad() {
        try {

            item = servicePo.onLoad(item.getId());
            Supplier supplier = new Supplier();
            supplier.setId(item.getId_supplier());
            supplier.setSupplier(item.getNama_supplier());
            sac.setSupplier(supplier);
            Customer customer = new Customer();
            customer.setId_kontak(item.getId_customer());
            customer.setCustomer(item.getCustomer());
            customerAutoComplete.setCustomer(customer);
            lPoDetail = servicePo.onLoadDetail(item.getId());
            options.setId_supplier(item.getId_supplier());
            options.setId_customer(item.getId_customer());

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

//    public void onNama() {
//        sP.setNama_barang(sP.getId_barang());
//        //sP.setTotal(sP.getHarga() * sP.getQty());
//    }
    public void hitungDiskonPersen(PoDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lPoDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungDiskonRp(PoDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lPoDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungTotal(PoDetail s, Integer i) {
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
        lPoDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungPph();
        this.hitungGrandTotal();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPoDetail.size(); j++) {
            if (lPoDetail.get(j).getTotal() != null) {
                jml = jml + lPoDetail.get(j).getTotal();
            }
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
        this.hitungPph();
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
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn() + item.getTotal_pph());
    }

//    public void choose(String list) {
//        Map<String, Object> options = new HashMap<String, Object>();
//        options.put("resizable", false);
//        options.put("draggable", false);
//        options.put("modal", true);
//        RequestContext.getCurrentInstance().openDialog(list, options, null);
//    }
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            item.setKode_mata_uang("IDR");
            item.setStatus(Boolean.FALSE);
            servicePo.tambah(item, lPoDetail);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            if (item.getJenis_po().equals("IP")) {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
            } else if (item.getJenis_po().equals("MA")) {
                context.getExternalContext().redirect("./edit_manual.jsf?id=" + item.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onSupplierSelect() {
        Supplier selectedSupplier = servicePo.onSupplierSelect(item.getId_supplier());
        item.setTelpon(selectedSupplier.getTelepon());
        item.setEmail(selectedSupplier.getEmail());
        item.setTop(selectedSupplier.getTop());
        item.setNama_supplier(selectedSupplier.getSupplier());
        //item.setId_supplier(sac.getSupplier().getId());
        item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
        options.setId_supplier(item.getId_supplier());
        if (item.getId_customer() != null) {
            options.setId_customer(item.getId_customer());
        } else {
            options.setId_customer("");
        }
    }

    public void onCustomerSelect(SelectEvent event) {
        item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
        options.setId_supplier(item.getId_supplier());
        options.setId_customer(item.getId_customer());
    }

    public void onKontrakSelect() {
        HargaBeli hargaBeli = servicePo.onHargaBeliSelect(item.getNo_kontrak());
        //item.setShiptonumber(hargaBeli.getShiptonumber());
        //item.setShipto(hargaBeli.getShipto());
        for (int i = 0; i < lPoDetail.size(); i++) {
            PoDetail itemdetail = new PoDetail();
            HargaBeliDetil hargaBeliDetil = servicePo.onSelectPrice(item.getNo_kontrak(), lPoDetail.get(i).getId_barang());
            itemdetail = lPoDetail.get(i);
            if (hargaBeliDetil != null) {
                itemdetail.setHarga(hargaBeliDetil.getHarga());
            } else {
                itemdetail.setHarga(null);
            }
            this.hitungTotal(itemdetail, i);
        }
    }

    public void onShiptoSelect() {
        Shipto shipto = servicePo.selectOneShipto(item.getShiptonumber());
        item.setShipto(shipto.getAlamat());
    }

    public void onKontakSelect() {
        if (item.getKontak() != null && !"".equals(item.getKontak())) {
            SupplierKontak supplierKontak = servicePo.onKontakSelect(item.getKontak(), item.getId_supplier());
            item.setKepada(supplierKontak.getKontak());
            item.setHp(supplierKontak.getHp());
            item.setEmail(supplierKontak.getEmail());
        } else {
            item.setKepada(null);
            item.setHp(null);
            item.setEmail(null);
        }
    }

    public void onBarangSelect(PoDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setId_satuan_kecil(barangAutoComplete.getBarang().getId_satuan_kecil());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setDiskonpersen(0.00);
        s.setDiskonrp(0.00);
        lPoDetail.set(i, s);

    }

    public void ubah(Boolean nilai, Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Po po = servicePo.onLoad(item.getId());
            if (po.getIs_approve() != null) {
                if (po.getIs_approve() && servicePo.isPenerimaan(item.getNomor_po()) > 0) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Po tidak bisa diedit Karena sudah ada proses Penerimaan Barang !!", ""));
                    return;
                }
            }
            
            if (jenis.equals('P')) {
                if(po.getIs_checked1() != null){
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Po tidak bisa diedit Karena sudah dicheck oleh Supervisor !!", ""));
                    return;
                }
                item.setIs_purchasing(nilai);
            } else if (jenis.equals('C')) {
                if(po.getIs_approve() != null){
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Po tidak bisa diedit Karena sudah dapprove oleh direktur !!", ""));
                    return;
                }
                item.setIs_checked1(nilai);
            }

            servicePo.ubah(item, lPoDetail, jenis);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubahMaintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;
        try {
            servicePo.ubahMaintenance(item, lPoDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void nomorurut() {

        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePo.noMax(Integer.parseInt(year));
        if (noMax == null) {
            item.setNomor_po("001/" + page.getMyInternalPerusahaan().getInisial() + "-PO/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor_po(noMax + "/" + page.getMyInternalPerusahaan().getInisial() + "-PO/" + bulan + "/" + tahun);
        }
    }

    private void initPegawai() {
        
        Pegawai pegawai = servicePo.onPegawaiSelect(item.getPurchasing());
        item.setNama_purchasing(pegawai.getNama());
        pegawai = servicePo.onPegawaiSelect(item.getDirector());
        item.setNama_director(pegawai.getNama());
        pegawai = servicePo.onPegawaiSelect(item.getChecked1());
        item.setNama_checked1(pegawai.getNama());
        
    }

    /////////////////////////////////////////////////////REGULER/////////////////////////////////////
    public void initItemreguler() {
        // String id = item.getNo_intruksi_po();
        String pp = item.getNo_pp();
        item = new Po();
        item.setNo_pp(pp);
        item.setNomor_po(null);
        item.setTanggal(null);
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setKepada(null);
        item.setTelpon(null);
        item.setHp(null);
        item.setEmail(null);
        item.setDeliverytime(null);
        item.setTop(null);
        item.setReferensi(null);
        item.setTanggal_referensi(null);
        options.setId_supplier(null);
        item.setPurchasing(id_pegawai);
        item.setChecked1(servicePo.getPurchasing());
        item.setDirector("CPA-002");
        sPReguler = new PoDetailReguler();
        lPoDetailReguler.add(sPReguler);
        Supplier supplier = new Supplier();
        sac.setSupplier(supplier);
        //this.nomorurut();
        this.onPPSelect();
        
        this.initPegawai();
    }

    public void onSupplierRegulerSelect(SelectEvent event) {
        Supplier selectedSupplier = new Supplier();
        Supplier tes = (Supplier) event.getObject();
        item.setId_supplier(tes.getId());
        selectedSupplier = servicePo.selectOneSupplier(item.getId_supplier());
        item.setTelpon(selectedSupplier.getTelepon());
        item.setEmail(selectedSupplier.getEmail());
        item.setTop(selectedSupplier.getTop());
        item.setNama_supplier(selectedSupplier.getSupplier());
        item.setKepada("");
        options.setId_supplier(item.getId_supplier());
        // this.nomorurut();
    }

    public void onPPSelect() {
        lPoDetailReguler = new ArrayList<>();
        item.setKeterangan(servicePo.onPPSelectOne(item.getNo_pp()).getKeterangan());
        List<PermintaanPembelianDetail> lPp = servicePo.onPPSelect(item.getNo_pp());
        for (int k = 0; k < lPp.size(); k++) {
            PoDetailReguler poDetailReguler = new PoDetailReguler();
            poDetailReguler.setNm_barang(lPp.get(k).getId_barang());
            poDetailReguler.setQty(lPp.get(k).getJumlah_order());
            poDetailReguler.setSatuan(lPp.get(k).getSatuan());
            poDetailReguler.setDiskonpersen(0.00);
            poDetailReguler.setDiskonrp(0.00);
            poDetailReguler.setTotal(0.00);
            lPoDetailReguler.add(poDetailReguler);

        }
    }

    public void hitungDiskonPersenReguler(PoDetailReguler s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lPoDetailReguler.set(i, s);
        this.hitungTotalReguler(s, i);
    }

    public void hitungDiskonRpReguler(PoDetailReguler s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lPoDetailReguler.set(i, s);
        this.hitungTotalReguler(s, i);
    }

    public void hitungTotalReguler(PoDetailReguler s, Integer i) {
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
        lPoDetailReguler.set(i, s);
        this.hitungJumlahTotalReguler();
        this.hitungDiskonReguler();
        this.hitungDppReguler();
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();
    }

    public void hitungJumlahTotalReguler() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPoDetailReguler.size(); j++) {
            if (lPoDetailReguler.get(j).getTotal() != null) {
                jml = jml + lPoDetailReguler.get(j).getTotal();
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
                if (item.getDpp() == null) {
                    item.setDpp(0.00);
                }
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

    public void extendreguler() {
        lPoDetailReguler.add(new PoDetailReguler());
    }

    public void tambahReguler() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            item.setKode_mata_uang("IDR");
            item.setJenis_po("PP");
            item.setStatus(Boolean.FALSE);
            item.setTotal_pph(0.00);
            servicePo.tambahReguler(item, lPoDetailReguler);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./editreguler.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onLoadReguler() {
        try {
            item = servicePo.onLoad(item.getId());
            Supplier supplier = new Supplier();
            supplier.setId(item.getId_supplier());
            supplier.setSupplier(item.getNama_supplier());
            sac.setSupplier(supplier);
            lPoDetailReguler = servicePo.onLoadDetailReguler(item.getId());
            options.setId_supplier(item.getId_supplier());
            this.initPegawai();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ubahReguler(Boolean status, Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Po po = servicePo.onLoad(item.getId());
            if (jenis.equals('P')) {
                if(po.getIs_checked1() != null){
                    return;
                }
                item.setIs_purchasing(status);
            } else if (jenis.equals('C')) {
                if(po.getIs_approve() != null){
                    return;
                }
                item.setIs_checked1(status);
            }
            servicePo.ubahReguler(item, lPoDetailReguler,jenis);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void approveAtasan(Boolean nilai) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        if (servicePo.selectOne(item.getNomor_po()).getIs_approve() != null) {
            if (servicePo.selectOne(item.getNomor_po()).getIs_approve() && servicePo.isPenerimaan(item.getNomor_po()) > 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Po tidak bisa di Reject Karena sudah ada proses Penerimaan Barang !!", ""));
                return;
            }
        }
        
            try {
                servicePo.updateStatusApprove(nilai, item.getId(), item.getNomor_po(), item.getPesan(), id_pegawai, item.getId_intruksi_po());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "PO berhasil di"));
                if (item.getJenis_po().equals("IP")) {
                    context.getExternalContext().redirect("./view.jsf?id=" + item.getId());
                } else if (item.getJenis_po().equals("PP")) {
                    context.getExternalContext().redirect("./viewreguler.jsf?id=" + item.getId());
                } else if (item.getJenis_po().equals("MA")) {
                    context.getExternalContext().redirect("./view_manual.jsf?id=" + item.getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        

    }
    
    
    
    

    public void cetakreguler() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakreguler.jsf?id=" + item.getId());
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getId());
    }

    public void cetakPenerimaan() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../../gudang/penerimaan/cetak.jsf?id=" + item.getNomor_po());
    }

    public List<PermintaanPembelianPesan> getDataPesan() {
        return servicePo.getDataPesan(item.getNomor_po());
    }

    public void selectOutstandingPo() {
        lPoDetail = servicePo.selectOutstandiPo(id_gudang, suppliernya);
    }

    public void onDeleteClickedReguler(PoDetailReguler poDetailReguler) {
        lPoDetailReguler.remove(poDetailReguler);
        this.hitungJumlahTotalReguler();
        this.hitungDiskonReguler();
        this.hitungDppReguler();
        this.hitungPpnReguler();
        this.hitungGrandTotalReguler();

    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getStatus() != null) {
                DateFormat bln = new SimpleDateFormat("MM");
                Po po = servicePo.onLoad(item.getId());
                if (!bln.format(item.getTanggal()).equals(bln.format(po.getTanggal()))) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                    item.setTanggal(po.getTanggal());
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

    public void onBarangReportSelect() {
        id_barang = barangAutoComplete.getBarang().getId_barang();
        barang = barangAutoComplete.getBarang().getNama_barang();
    }

    public void onLoadListReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lPoDetail = servicePo.onLoadReport(id_barang, awal, akhir, id_gudang, statuspo);
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda belum memilih barang !! !!!!", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lPoDetail = servicePo.onLoadReport(id_barang, awal, akhir, id_gudang, statuspo);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Laporan Purchase Order");
                    PropertyTemplate pt = new PropertyTemplate();
                    CellStyle cellStyle = workbook.createCellStyle();
                    DataFormat format = workbook.createDataFormat();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    font.setFontName("Calibri");
                    cellStyle.setFont(font);
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);

                    CellStyle style = workbook.createCellStyle();
                    style.setDataFormat(format.getFormat("#,##0"));
                    style.setAlignment(HorizontalAlignment.RIGHT);

                    XSSFCellStyle dateCellStyle = workbook.createCellStyle();
                    XSSFDataFormat formatDate = workbook.createDataFormat();
                    dateCellStyle.setDataFormat(formatDate.getFormat("dd-MM-yyyy"));
                    dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

                    Row row = sheet.createRow(0);
                    Cell cell = row.createCell(0);
                    cell.setCellValue(page.getMyInternalPerusahaan().getNama().toUpperCase());
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(
                            0, //first row (0-based)
                            0, //last row  (0-based)
                            0, //first column (0-based)
                            8 //last column  (0-based)
                    ));

                    row = sheet.createRow(1);
                    cell = row.createCell(0);
                    cell.setCellValue("Laporan Purchase Order " + barang + " (" + id_barang + ") Gudang " + servicePo.namaGudang(id_gudang));
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

                    DateFormat tgl = new SimpleDateFormat("dd-MM-yyyy"); // Just the year, with 2 digits
                    //String tahun = thn.format(date);
                    row = sheet.createRow(2);
                    cell = row.createCell(0);
                    if (awal != null && akhir != null) {
                        cell.setCellValue("PERIODE : " + tgl.format(awal) + " s/d " + tgl.format(akhir));
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

                    row = sheet.createRow(4);
                    cell = row.createCell(0);
                    cell.setCellValue("No");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue("Nomor");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(2);
                    cell.setCellValue("Tanggal");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(3);
                    cell.setCellValue("Supplier");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Ltr");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(6);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(7);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(8);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lPoDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lPoDetail.get(i).getNomor_po());
                        cell = row.createCell(2);
                        cell.setCellValue(lPoDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lPoDetail.get(i).getNama_supplier());
                        cell = row.createCell(4);
                        cell.setCellValue(lPoDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(5);
                        cell.setCellValue(lPoDetail.get(i).getSatuan_kecil());
                        cell = row.createCell(6);
                        cell.setCellValue(lPoDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(7);
                        cell.setCellValue(lPoDetail.get(i).getSatuan_besar());

                        cell = row.createCell(8);
                        if (!lPoDetail.get(i).getIs_purchasing()) {
                            cell.setCellValue("Draft");
                        } else if (lPoDetail.get(i).getIs_purchasing() && lPoDetail.get(i).getIs_approve() == null) {
                            cell.setCellValue("Send");
                        } else if (lPoDetail.get(i).getIs_approve() == true) {
                            cell.setCellValue("Approve");
                        } else if (lPoDetail.get(i).getIs_approve() == false) {
                            cell.setCellValue("Reject");
                        }

                        baris++;
                    }
                    sheet.autoSizeColumn(0);
                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    sheet.autoSizeColumn(8);
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan PO.xlsx\"");
                    workbook.write(externalContext.getResponseOutputStream());
                    facesContext.responseComplete();
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda belum memilih barang !! !!!!", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
