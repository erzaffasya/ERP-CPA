package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerKontak;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServicePenawaran;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
//import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
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
public class ControllerPenawaran implements java.io.Serializable {

    private static final long serialVersionUID = -2;
    private List<Penawaran> lPenawaran = new ArrayList<>();
    private Penawaran item;
    private Customer itemCustomer;
    private List<PenawaranDetail> lPenawaranDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private String id_salesman;
    private Character statuspenawaran;
    private Double ppn;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServicePenawaran servicePenawaran;

    @PostConstruct
    public void init() {
        item = new Penawaran();
        item.setTanggal(new Date());
        itemCustomer = new Customer();
        item.setId_kantor(page.getMyPegawai().getId_kantor());
        item.setKode_user(page.getName());
        Pegawai pegawai = servicePenawaran.selectHeadDepartemen(page.getMyPegawai().getId_departemen(), item.getId_kantor());
        if (pegawai != null) {
            if (pegawai.getId_pegawai().equals(page.getMyPegawai().getId_pegawai())) {
                statuspenawaran = 'S';
            } else {
                statuspenawaran = 'D';
                id_salesman = page.getMyPegawai().getId_pegawai();
            }
        }
    }

    public void initItem() {
        String id = item.getId_customer();
        lPenawaranDetail = new ArrayList();
        item = new Penawaran();
        item.setId_kantor(page.getMyPegawai().getId_kantor_new());
        item.setKode_user(page.getMyPegawai().getId_pegawai());
        item.setTanggal(new Date());
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setRevisi(0);
        item.setJenis_bank(Boolean.TRUE);
        item.setStatus(Boolean.FALSE);
        item.setId_salesman(page.getMyPegawai().getId_pegawai());
        item.setCarabayar('C');
        lPenawaranDetail.add(new PenawaranDetail());
        options.setId_customer(null);
        options.setId_departemen(page.getMyPegawai().getId_departemen_new());
        options.setId_kantor(page.getMyPegawai().getId_kantor_new());
        customerAutoComplete.setCustomer(null);
        if (id != null) {
            item.setId_customer(id);
            customerAutoComplete.setCustomer(servicePenawaran.selectOneCustomer(id));
            this.onCustomerSelect();

        }

        Pegawai pegawai = servicePenawaran.selectHeadDepartemen(page.getMyPegawai().getId_departemen_new(), item.getId_kantor());
        item.setDsm(pegawai.getId_pegawai());
        if (item.getId_salesman().equals(item.getDsm())) {
            item.setStatussend('S');
        } else {
            item.setStatussend('D');
        }

    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePenawaran.noMax(item.getId_kantor(), Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNomor(item.getId_kantor() + "/001/SPH/CPA/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(item.getId_kantor() + "/" + noMax + "/SPH/CPA/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenawaran = servicePenawaran.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), statuspenawaran, null);
                for (int i = 0; i < lPenawaran.size(); i++) {
                    Penawaran penawaran = lPenawaran.get(i);
                    if (penawaran.getId_customer() == null) {
                        penawaran.setCustomer(penawaran.getNewcustomer());
                        lPenawaran.set(i, penawaran);
                    }
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah ?!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onLoadListMaintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenawaran = servicePenawaran.onLoadListMaintenance(awal, akhir, statuspenawaran);
                for (int i = 0; i < lPenawaran.size(); i++) {
                    Penawaran penawaran = lPenawaran.get(i);
                    if (penawaran.getId_customer() == null) {
                        penawaran.setCustomer(penawaran.getNewcustomer());
                        lPenawaran.set(i, penawaran);
                    }
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah ?!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onLoadListSalesAdmin() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenawaran = servicePenawaran.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), statuspenawaran, page.getMyKantor().getId_kantor_cabang());
                for (int i = 0; i < lPenawaran.size(); i++) {
                    Penawaran penawaran = lPenawaran.get(i);
                    if (penawaran.getId_customer() == null) {
                        penawaran.setCustomer(penawaran.getNewcustomer());
                        lPenawaran.set(i, penawaran);
                    }
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah ?!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onLoadListNewCustomer() {
        lPenawaran = servicePenawaran.onLoadListNewCustomer();
    }
    
    

    public List<Penawaran> getDataPenawaran() {
        return lPenawaran;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenawaran.delete(nomor, item.getRevisi());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void deleteMaintenance(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenawaran.delete(nomor, item.getRevisi());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./penawaran.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PenawaranDetail> getDataPenawaranDetail() {
        return lPenawaranDetail;

    }

    public void extend() {
        lPenawaranDetail.add(new PenawaranDetail());
    }

    public void onDeleteClicked(PenawaranDetail itemPenawaranDetail) {
        lPenawaranDetail.remove(itemPenawaranDetail);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getId_customer() != null) {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            } else {
                context.getExternalContext().redirect("./editnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Pilih Penawaran yang akan diedit 3 !!!", ""));
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getId_customer() != null) {
                context.getExternalContext().redirect("./penawaran-edit.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            } else {
                context.getExternalContext().redirect("./penawaran-editnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Pilih Penawaran yang akan diedit 3 !!!", ""));
        }
    }

    public void createSo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../so/addnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./laporan.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
    }

    public void onLoad() {
        try {
            item = servicePenawaran.onLoad(item.getNomor(), item.getRevisi());
            customerAutoComplete.setCustomer(servicePenawaran.selectOneCustomer(item.getId_customer()));
            lPenawaranDetail = servicePenawaran.onLoadDetail(item.getNomor(), item.getRevisi());
            options.setId_customer(item.getId_customer());
            options.setId_departemen(page.getMyPegawai().getId_departemen());
            options.setId_kantor(page.getMyKantor().getId_kantor_cabang());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void revisiPenawaran() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getId_customer() != null) {
                context.getExternalContext().redirect("./revisi.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            } else {
                context.getExternalContext().redirect("./revisinewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Pilih Penawaran yang akan direvisi ya  !!!", ""));
        }

    }

    public void onrevisiPenawaran() {
        this.onLoad();
        if (item.getId_salesman().equals(item.getDsm())) {
            item.setStatussend('S');
        } else {
            item.setStatussend('D');
        }
        item.setRevisi(item.getRevisi() + 1);
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        PenawaranDetail entity = context.getApplication().evaluateExpressionGet(context, "#{item}", PenawaranDetail.class);
        entity.setNama_barang(entity.getId_barang());
        // ...
    }

    public void hitungDiskonPersen(PenawaranDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getDiskonpersen() == null) {
            s.setDiskonpersen(0.00);
        }

        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lPenawaranDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungDiskonRp(PenawaranDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }

        if (s.getDiskonrp() == null) {
            s.setDiskonrp(0.00);
        }

        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lPenawaranDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungTotal(PenawaranDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }

        s.setTotal((s.getHarga() - s.getDiskonrp() + s.getAdditional_charge()) * s.getQty());
        lPenawaranDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPenawaranDetail.size(); j++) {
            jml = jml + lPenawaranDetail.get(j).getTotal();
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

    public void changeDate() {
        hitungPpn();
    }

    public void onTglSelect(SelectEvent event) {
        changeDate();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        changeDate();
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

    public void hitungGrandTotal() {
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn());
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            if (item.getId_customer() != null) {
                item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
                item.setStatussend('D');
                servicePenawaran.tambah(item, lPenawaranDetail);
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } else {
                servicePenawaran.tambah(item, lPenawaranDetail);
                context.getExternalContext().redirect("./editnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void tambahRevisi() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getId_customer() != null) {
                item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
            }
            servicePenawaran.tambah(item, lPenawaranDetail);
            if (item.getId_customer() != null) {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            } else {
                context.getExternalContext().redirect("./editnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onCustomerSelect() {
        Customer selectedCustomer = servicePenawaran.selectOneCustomer(customerAutoComplete.getCustomer().getId_kontak());
        item.setTelpon(selectedCustomer.getTelepon());
        //item.setEmail(selectedCustomer.getEmail());
        item.setKepada(selectedCustomer.getKontak());
        item.setId_customer(selectedCustomer.getId_kontak());
        item.setTop(selectedCustomer.getTop());
        options.setId_customer(item.getId_customer());
    }

    public void onGudangSelect() {
        try {
            for (int i = 0; i < lPenawaranDetail.size(); i++) {
                if (lPenawaranDetail.get(i).getId_barang() != null) {
                    Double harga = servicePenawaran.hargaBarang(item.getId_gudang(), lPenawaranDetail.get(i).getId_barang());
                    PenawaranDetail itemdetail = lPenawaranDetail.get(i);
                    if (harga != null) {
                        itemdetail.setHarga(harga);

                    } else {
                        itemdetail.setHarga(0.00);
                    }
                    this.hitungTotal(itemdetail, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBarangSelect(PenawaranDetail s, Integer i) {
        try {
            Double harga = servicePenawaran.hargaBarang(item.getId_gudang(), barangAutoComplete.getBarang().getId_barang());
            s.setId_barang(barangAutoComplete.getBarang().getId_barang());
            s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
            s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
            s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
            s.setHarga(harga);
            s.setAdditional_charge(0.00);
            s.setDiskonpersen(0.00);
            s.setDiskonrp(0.00);
            lPenawaranDetail.set(i, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onKontakSelect() {
        CustomerKontak customerKontak = servicePenawaran.selectOneKontak(item.getId_kontak(), item.getId_customer());
        item.setKepada(customerKontak.getKontak());
        item.setHp(customerKontak.getHp());
        item.setEmail(customerKontak.getEmail());
    }

    public void ubah(Character statussend) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            item.setStatussend(statussend);
            servicePenawaran.ubah(item, lPenawaranDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubahMaintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePenawaran.ubah(item, lPenawaranDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void kirimpesan() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePenawaran.kirimPesan(item, page.getName());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void tambahCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurutCustomer();
            servicePenawaran.tambahCustomer(itemCustomer);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf?id=" + itemCustomer.getId_kontak());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void choose() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        //RequestContext.getCurrentInstance().openDialog("form_umum", options, null);
    }

    public void selectCustomerFromDialog(Customer customer) {
        //RequestContext.getCurrentInstance().closeDialog(customer);
        //RequestContext.getCurrentInstance().execute("dl3.hide();");
    }

    public void nomorurutCustomer() {
        String noMax = servicePenawaran.noMaxCustomer();
        if (noMax == null) {
            itemCustomer.setId_kontak(page.getMyKantor().getId_kantor_cabang() + "/000001");
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            itemCustomer.setId_kontak(page.getMyKantor().getId_kantor_cabang() + noMax);
        }
    }

    public void onTop() {
        if (!item.getCarabayar().equals('C')) {
            item.setTop(0);
        }
    }

    ////////////////////// Laporan //////////////////////////
    public void onLoadListPenawaranInvoice() {
        FacesContext context = FacesContext.getCurrentInstance();
        options.setId_departemen(page.getMyPegawai().getId_departemen());
        options.setId_kantor(page.getMyKantor().getId_kantor_cabang());
        try {
            if (id_salesman != null && !id_salesman.equals("") && awal != null && akhir != null) {
                if (page.getMyPegawai().getId_jabatan() == 37 || page.getMyPegawai().getId_jabatan() == 30 || page.getMyPegawai().getId_jabatan() == 56 || page.getMyPegawai().getId_jabatan() == 35 || page.getMyPegawai().getId_jabatan() == 38) {
                    lPenawaranDetail = servicePenawaran.onLoadListPenawaranInvoice(awal, akhir, id_salesman, page.getMyKantor().getId_kantor_cabang());
                } else {
                    lPenawaranDetail = servicePenawaran.onLoadListPenawaranInvoice(awal, akhir, id_salesman, null);
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Pilih Periode dan Salesman !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Map<String, String> getComboStatus() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Draft", "D");
            map.put("Send", "S");
            map.put("Approve", "A");
            map.put("Posting", "P");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

}
