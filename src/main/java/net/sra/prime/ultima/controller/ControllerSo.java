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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.OptionSalesAdmin;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.So;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.SoPersetujuan;
import net.sra.prime.ultima.service.ServiceSo;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import net.sra.prime.ultima.view.input.HargaJualAutoComplete;
import net.sra.prime.ultima.view.input.HargaJualDetilAutoComplete;
import net.sra.prime.ultima.view.input.PenawaranAutoComplete;
import net.sra.prime.ultima.view.input.SoAutoComplete;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
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
public class ControllerSo implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<So> lSo = new ArrayList<>();
    private List<So> lNotifikasi = new ArrayList<>();
    private So item;
    private List<SoDetail> lSoDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    Character statusso;
    private Double limitKredit, hutang, hutangJatuhTempo;
    private String infoCustomer, infoPersetujuan;
    private Boolean statusSend, statusPersetujuan;
    private String id_gudang;
    private String id_customer;
    private Double ppn;
    private String id_pegawai;
    private String id_kantor;
    private Integer id_departemen;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private PenawaranAutoComplete penawaranAutoComplete;

    @Inject
    private HargaJualAutoComplete hargaJualAutoComplete;

    @Inject
    private HargaJualDetilAutoComplete hargaJualDetilAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private SoAutoComplete soAutoComplete;

    @Inject
    private Page page;

    @Inject
    Options options;
    
    @Inject
    OptionSalesAdmin optionSalesAdmin;

    @Autowired
    ServiceSo serviceSo;

    @PostConstruct
    public void init() {
        item = new So();
        Pegawai pegawai = page.getMyPegawai();
        id_pegawai = pegawai.getId_pegawai();
        id_kantor = pegawai.getId_kantor_new();
        id_departemen = pegawai.getId_departemen_new();
        optionSalesAdmin.setId_admin(id_pegawai);
        penawaranAutoComplete.setId_admin(id_pegawai);
        customerAutoComplete.setId_admin(id_pegawai);
        // jika departemen adalah supply chain maka statusso : send jika bukan maka statusso : D
        if (id_departemen == 107) {
            statusso = 'S';
        } else {
            statusso = 'D';
        }
    }

    public void initItem() {
        item = new So();
        //Date date = new Date();
        //item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setIs_manajemen(Boolean.FALSE);
        penawaranAutoComplete.setPenawaran(new Penawaran());
        hargaJualAutoComplete.setHargaJual(new HargaJual());
        lSoDetail = new ArrayList<>();
        infoCustomer = "";
        infoPersetujuan = "";
        soAutoComplete.setSo(new So());
    }

    public void newCustomer() {
        lSoDetail = new ArrayList<>();
        Penawaran selectedPenawaran = serviceSo.selectOnePenawaran(item.getNo_penawaran(), item.getRevisi_penawaran());
        item.setId_customer(selectedPenawaran.getId_customer());
        item.setCustomer(selectedPenawaran.getNewcustomer());
        item.setKepada(selectedPenawaran.getKepada());
        if (item.getHp() != null) {
            item.setTelpon(selectedPenawaran.getTelpon() + " HP : " + item.getHp());
        } else {
            item.setTelpon(selectedPenawaran.getTelpon());
        }
        item.setId_gudang(selectedPenawaran.getId_gudang());
        item.setKode_mata_uang(selectedPenawaran.getKode_mata_uang());
        item.setEmail(selectedPenawaran.getEmail());
        item.setHp(selectedPenawaran.getHp());
        item.setTotal(selectedPenawaran.getTotal());
        item.setPersendiskon(selectedPenawaran.getPersendiskon());
        item.setTotal_discount(selectedPenawaran.getTotal_discount());
        item.setDpp(item.getTotal() - item.getTotal_discount());
        item.setTotal_ppn(selectedPenawaran.getTotal_ppn());
        item.setGrandtotal(item.getTotal() - item.getTotal_discount() + item.getTotal_ppn());
        item.setTop(selectedPenawaran.getTop());
        item.setCertificate(selectedPenawaran.getCertificate());
        item.setDeliverypoint(selectedPenawaran.getDeliverypoint());
        item.setPov(selectedPenawaran.getPov());
        item.setSyarat(selectedPenawaran.getSyarat());
        item.setNo_penawaran(selectedPenawaran.getNomor());
        item.setJenis("p");
        item.setRevisi_penawaran(selectedPenawaran.getRevisi());
        item.setId_salesman(selectedPenawaran.getId_salesman());
        item.setIs_ppn(selectedPenawaran.getIs_ppn());
        List<PenawaranDetail> listPenawaranDetail = new ArrayList<>();
        listPenawaranDetail = serviceSo.selectPenawaranDetail(item.getNo_penawaran(), item.getRevisi_penawaran());
        for (int j = 0; j < listPenawaranDetail.size(); j++) {
            SoDetail pd = new SoDetail();
            pd.setNo_so(item.getNomor());
            pd.setUrut(listPenawaranDetail.get(j).getUrut());
            pd.setId_barang(listPenawaranDetail.get(j).getId_barang());
            pd.setNama_barang(listPenawaranDetail.get(j).getNama_barang());
            pd.setQty(listPenawaranDetail.get(j).getQty());
            pd.setSatuan_kecil(listPenawaranDetail.get(j).getSatuan_kecil());
            pd.setId_satuan_kecil(listPenawaranDetail.get(j).getId_satuan_kecil());
            pd.setHarga(listPenawaranDetail.get(j).getHarga());
            pd.setTotal(listPenawaranDetail.get(j).getTotal());
            pd.setDiskonpersen(listPenawaranDetail.get(j).getDiskonpersen());
            pd.setDiskonrp(listPenawaranDetail.get(j).getDiskonrp());
            pd.setSatuan_besar(listPenawaranDetail.get(j).getSatuan_besar());
            pd.setAdditional_charge(listPenawaranDetail.get(j).getAdditional_charge());
            lSoDetail.add(pd);
        }
        infoCustomer = "";
        infoPersetujuan = "";
        soAutoComplete.setSo(new So());
    }

    public void initItemManual() {
        item = new So();
        Date date = new Date();
        //item.setTanggal(date);
        item.setTotal_discount(0.00);
        item.setPersendiskon(0.00);
        item.setIs_ppn(Boolean.TRUE);
        item.setJenis("m");
        item.setIs_manajemen(Boolean.FALSE);
        lSoDetail = new ArrayList();
        lSoDetail.add(new SoDetail());
        infoCustomer = "";
        infoPersetujuan = "";
        soAutoComplete.setSo(new So());
        options.setId_departemen(id_departemen);
        options.setId_kantor(id_kantor);
        
    }

    public void initItemConsignment() {
        item = new So();
        Date date = new Date();
        //item.setTanggal(date);
        item.setJenis("m");
        customerAutoComplete.setCustomer(new Customer());
        lSoDetail = new ArrayList<>();
        lSoDetail.add(new SoDetail());
        infoCustomer = "";
        infoPersetujuan = "";
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceSo.noMax(Integer.parseInt(bulannya), Integer.parseInt(tahun));
        if (noMax == null) {
            item.setNomor("001/SO/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/SO/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSo = serviceSo.onLoadList(awal, akhir, null, statusso);
                for (int i = 0; i < lSo.size(); i++) {
                    lSo.get(i).setStPersetujuan(true);
                    if (lSo.get(i).getStatus().equals('D')) {
                        limitKredit = serviceSo.selectOneCustomer(lSo.get(i).getId_customer()).getBatas_kredit();
                        hutangJatuhTempo = serviceSo.sisaHutangCustomerbyTop(lSo.get(i).getId_customer(), new Date());
                        hutang = serviceSo.jumlahHutangCustomer(lSo.get(i).getId_customer());
                        if (hutangJatuhTempo > 0 || serviceSo.checkArCount(lSo.get(i).getId_customer()).equals(0)) {
                            SoPersetujuan sp = serviceSo.selectApproveSoPersetujuan(lSo.get(i).getNomor());
                            if (sp == null) {
                                lSo.get(i).setStPersetujuan(false);
                            } else if (sp.getApprove() == false) {
                                lSo.get(i).setStPersetujuan(false);
                            }
                        } else if (limitKredit != null) {
                            if (hutang >= limitKredit) {
                                SoPersetujuan sp = serviceSo.selectApproveSoPersetujuan(lSo.get(i).getNomor());
                                if (sp == null) {
                                    lSo.get(i).setStPersetujuan(false);
                                } else if (sp.getApprove() == false) {
                                    lSo.get(i).setStPersetujuan(false);
                                }
                            }
                        }
                    }
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListDetail() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSoDetail = serviceSo.onLoadListDetail(awal, akhir, null, statusso);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListPengajuan() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            lNotifikasi = serviceSo.selectSoNotifikasi(id_pegawai);
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSo = serviceSo.onLoadListPengajuan(awal, akhir, id_pegawai);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadMonitoringSo() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSo = serviceSo.onLoadMonitoringSo(awal, akhir, null, statusso);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadSoReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSoDetail = serviceSo.onLoadSoReport(awal, akhir, null, statusso);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListGudang() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSo = serviceSo.onLoadListGudang(awal, akhir, id_pegawai, statusso);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListConsignment() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSo = serviceSo.onLoadListConsignment(awal, akhir, id_kantor);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<So> getDataSo() {
        return lSo;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Character test = serviceSo.onLoad(item.getNomor()).getStatus();
            if (!test.equals('D') && !test.equals('S')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal dihpuas SO ini sudah proses packinglist ", ""));
                return;
            }
            serviceSo.delete(nomor);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<SoDetail> getDataSoDetail() {
        return lSoDetail;
    }

    public List<So> getDataNotifikasi() {
        return lNotifikasi;
    }

    public void extend() {
        lSoDetail.add(new SoDetail());
    }

    public void onDeleteClicked(SoDetail item) {
        lSoDetail.remove(item);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();

    }

    public void onHapusClicked(SoDetail item) {
        lSoDetail.remove(item);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getJenis().equals("c")) {
                context.getExternalContext().redirect("./edit_consignment.jsf?id=" + item.getNomor());
            }
            if (item.getJenis().equals("m")) {
                context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getNomor());
            } else {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Belum dipilih", ""));
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getJenis().equals("c")) {
                context.getExternalContext().redirect("./so-consignment.jsf?id=" + item.getNomor());
            }
            if (item.getJenis().equals("m")) {
                context.getExternalContext().redirect("./so-manual.jsf?id=" + item.getNomor());
            } else {
                context.getExternalContext().redirect("./so-edit.jsf?id=" + item.getNomor());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Belum dipilih", ""));
        }
    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view_consignment.jsf?id=" + item.getNomor());
    }

    public void createIo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("../../../transaksi/internalOrder/addso.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Belum dipilih", ""));
        }
    }

    public void packinglist() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (serviceSo.cekPackinglist(item.getNomor()) > 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nomor SO " + item.getNomor() + " sedang proses Pickinglist !!!", ""));
            } else {
                context.getExternalContext().redirect("../../../gudang/packinglist/add.jsf?id=" + item.getNomor());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Belum dipilih", ""));
        }
    }

    public void customerInfo(String id_customer) {
        if (serviceSo.checkArCount(id_customer).equals(0)) {
            infoCustomer = "<ul><li>Customer Baru</li></ul>";
            hutangJatuhTempo = 0.00;
        } else {
            String lk = "";
            Customer customer = serviceSo.selectOneCustomer(id_customer);

            limitKredit = customer.getBatas_kredit();
            hutangJatuhTempo = serviceSo.sisaHutangCustomerbyTop(id_customer, new Date());
            hutang = serviceSo.jumlahHutangCustomer(item.getId_customer());
            if (customer.getBatas_kredit() == null) {
                lk = "  <li>Batas kredit belum diinput</li>";
            } else {
                lk = "  <li>Batas kredit Rp. " + new DecimalFormat("###,###.##").format(limitKredit) + "</li>";
            }

            infoCustomer = "<ul>"
                    + lk
                    + "  <li>Hutang jatuh tempo Rp. " + new DecimalFormat("###,###.##").format(hutangJatuhTempo) + "</li>"
                    + "  <li>Total hutang Rp. " + new DecimalFormat("###,###.##").format(hutang) + "</li>"
                    + "</ul>";
        }

    }

    public void statusInfo(String id, String id_customer) {
        statusSend = true;
        statusPersetujuan = true;
        // Mengambil jumlah hutang yg pernah terbit
        Integer jmlAr = serviceSo.checkArCount(id_customer);

        // jika tidak ada hutang dan customer baru(belum pernah ada AR)
       // if (!hutang.equals(0.0) || jmlAr.equals(0)) {

            if (item.getIs_manajemen() == false) {
                //apakah sudah ada ditabel so_persetujuaan
                if (serviceSo.selectOneSoPersetujuan(id) == null) {
                    statusPersetujuan = false;
                    statusSend = false;
                }

                SoPersetujuan sp = serviceSo.selectApproveSoPersetujuan(id);
                    if (sp == null) {
                        statusSend = false;
                    } else if (sp.getApprove() == false) {
                        statusSend = false;
                    }
//                
//                if (hutangJatuhTempo > 0 || jmlAr.equals(0)) {
//                    SoPersetujuan sp = serviceSo.selectApproveSoPersetujuan(id);
//                    if (sp == null) {
//                        statusSend = false;
//                    } else if (sp.getApprove() == false) {
//                        statusSend = false;
//                    }
//                } else if (limitKredit != null) {
//                    if (hutang >= limitKredit) {
//                        SoPersetujuan sp = serviceSo.selectApproveSoPersetujuan(id);
//                        if (sp == null) {
//                            statusSend = false;
//                        } else if (sp.getApprove() == false) {
//                            statusSend = false;
//                        }
//                    }
//                }
//            }
        }
    }

    public void persetujuanInfo(String nomor) {
        List<SoPersetujuan> lsoPersetujuans = serviceSo.selectListSoPersetujuan(nomor);
        String note = "";
        infoPersetujuan = "";
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        if (lsoPersetujuans.size() > 0) {
            infoPersetujuan = "<ul>";

            if (lsoPersetujuans.get(0).getApprove() == null) {
                infoPersetujuan = infoPersetujuan + "<li>Menunggu persetujuan dari " + lsoPersetujuans.get(0).getNama() + "</li>";
            } else if (lsoPersetujuans.get(0).getApprove() == true) {
                if (lsoPersetujuans.get(0).getNote() != null) {
                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(0).getNote();
                }
                infoPersetujuan = infoPersetujuan + "<li>Disetuji oleh " + lsoPersetujuans.get(0).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(0).getDate()) + note + "</li>";
            } else if (lsoPersetujuans.get(0).getApprove() == false) {
                if (lsoPersetujuans.get(0).getNote() != null) {
                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(0).getNote();
                }
                infoPersetujuan = infoPersetujuan + "<li>Ditolak oleh " + lsoPersetujuans.get(0).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(0).getDate()) + note + "</li>";
            }

            if (lsoPersetujuans.get(0).getApprove() != null) {
                if (lsoPersetujuans.get(0).getApprove() != false) {
                    if (lsoPersetujuans.size() > 1) {
                        if (lsoPersetujuans.get(1).getApprove() != null) {
                            if (lsoPersetujuans.get(1).getApprove() == true) {
                                note = "";
                                if (lsoPersetujuans.get(1).getNote() != null) {
                                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(1).getNote();
                                }
                                infoPersetujuan = infoPersetujuan + "<li>Disetuji oleh " + lsoPersetujuans.get(1).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(1).getDate()) + note + "</li>";
                            } else if (lsoPersetujuans.get(1).getApprove() == false) {
                                note = "";
                                if (lsoPersetujuans.get(1).getNote() != null) {
                                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(1).getNote();
                                }
                                infoPersetujuan = infoPersetujuan + "<li>Ditolak oleh " + lsoPersetujuans.get(1).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(1).getDate()) + note + "</li>";
                            }
                            if (lsoPersetujuans.get(2).getApprove() != null) {
                                if (lsoPersetujuans.get(2).getApprove() == true) {
                                    note = "";
                                    if (lsoPersetujuans.get(2).getNote() != null) {
                                        note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(2).getNote();
                                    }
                                    infoPersetujuan = infoPersetujuan + "<li>Disetuji oleh " + lsoPersetujuans.get(2).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(2).getDate()) + note + "</li>";
                                } else if (lsoPersetujuans.get(2).getApprove() == false) {
                                    note = "";
                                    if (lsoPersetujuans.get(2).getNote() != null) {
                                        note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(2).getNote();
                                    }
                                    infoPersetujuan = infoPersetujuan + "<li>Ditolak oleh " + lsoPersetujuans.get(2).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(2).getDate()) + note + "</li>";
                                }
                            }
                        } else if (lsoPersetujuans.get(2).getApprove() != null) {
                            if (lsoPersetujuans.get(2).getApprove() == true) {
                                note = "";
                                if (lsoPersetujuans.get(2).getNote() != null) {
                                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(2).getNote();
                                }
                                infoPersetujuan = infoPersetujuan + "<li>Disetuji oleh " + lsoPersetujuans.get(2).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(2).getDate()) + note + "</li>";
                            } else if (lsoPersetujuans.get(2).getApprove() == false) {
                                note = "";
                                if (lsoPersetujuans.get(2).getNote() != null) {
                                    note = " <br/> Alasan Persetujuan : " + lsoPersetujuans.get(2).getNote();
                                }
                                infoPersetujuan = infoPersetujuan + "<li>Ditolak oleh " + lsoPersetujuans.get(2).getNama() + " pada tanggal " + df.format(lsoPersetujuans.get(2).getDate()) + note + "</li>";
                            }
                        } else {
                            infoPersetujuan = infoPersetujuan + "<li>Menunggu persetujuan dari " + lsoPersetujuans.get(1).getNama() + " atau " + lsoPersetujuans.get(2).getNama() + "</li>";
                        }
                    }
                }
            }

            infoPersetujuan = infoPersetujuan + "</ul>";
        }
    }

    public void onLoad() {
        try {
            options.setId_departemen(id_departemen);
            options.setId_kantor(id_kantor);
            item = serviceSo.onLoad(item.getNomor());
            customerInfo(item.getId_customer());
            infoPersetujuan = "";
            if (item.getJenis().equals("p")) {
                Penawaran penawaran = new Penawaran();
                penawaran.setNomor(item.getNo_penawaran());
                penawaranAutoComplete.setPenawaran(penawaran);
                item.setIs_manajemen(Boolean.FALSE);
            } else if (item.getJenis().equals("k")) {
                if (item.getId_salesman() == null) {
                    item.setIs_manajemen(Boolean.TRUE);
                } else {
                    item.setIs_manajemen(Boolean.FALSE);
                }
                HargaJual hargaJual = new HargaJual();
                hargaJual.setNo_kontrak(item.getNo_penawaran());
                hargaJualAutoComplete.setHargaJual(hargaJual);
            } else if (item.getJenis().equals("c") || item.getJenis().equals("m")) {
                if (item.getId_salesman() == null) {
                    item.setIs_manajemen(Boolean.TRUE);
                } else {
                    item.setIs_manajemen(Boolean.FALSE);
                }
                Customer customer = serviceSo.selectOneCustomer(item.getId_customer());
                customerAutoComplete.setCustomer(customer);
            }
            lSoDetail = serviceSo.onLoadDetail(item.getNomor());
            statusInfo(item.getNomor(), item.getId_customer());
            persetujuanInfo(item.getNomor());
            if (item.getSobefore() != null) {
                soAutoComplete.setSo(serviceSo.onLoad(item.getSobefore()));
            } else {
                soAutoComplete.setSo(new So());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadPersetujuan() {
        onLoad();
        statusPersetujuan = serviceSo.selectSoPersetujuanByPegawai(item.getNomor(), id_pegawai).getApprove();
    }

    public void onLoadView() {
        try {
            item = serviceSo.onLoad(item.getNomor());
            lSoDetail = serviceSo.onLoadDetailView(item.getNomor());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        SoDetail entity = context.getApplication().evaluateExpressionGet(context, "#{item}", SoDetail.class
        );
        entity.setNama_barang(entity.getId_barang());
        // ...
    }

    public void hitungDiskonPersen(SoDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonrp(s.getHarga() * s.getDiskonpersen() / 100);
        lSoDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void hitungDiskonRp(SoDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        s.setDiskonpersen(s.getDiskonrp() * 100 / s.getHarga());
        lSoDetail.set(i, s);
        this.hitungTotal(s, i);
    }

    public void onQty(SoDetail s, Integer i) {
        s.setQty_kecil(s.getQty() * s.getIsi_satuan());
        hitungTotal(s, i);
    }

    public void onQtyKecil(SoDetail s, Integer i) {
        s.setQty(s.getQty_kecil() / s.getIsi_satuan());
        hitungTotal(s, i);
    }

    public void hitungTotal(SoDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getQty() == null) {
            s.setQty(0.00);
        }
        if (s.getAdditional_charge() == null) {
            s.setAdditional_charge(0.00);
        }

        s.setTotal((s.getHarga() - s.getDiskonrp() + s.getAdditional_charge()) * s.getQty());
        lSoDetail.set(i, s);
        this.hitungJumlahTotal();
        this.hitungDiskon();
        this.hitungDpp();
        this.hitungPpn();
        this.hitungGrandTotal();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lSoDetail.size(); j++) {
            jml = jml + lSoDetail.get(j).getTotal();
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
            e.printStackTrace();
        }
    }

    public void hitungGrandTotal() {
        item.setGrandtotal(item.getDpp() + item.getTotal_ppn());
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getId_customer() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Penawaran  dalam proses pembuatan SO !!", ""));
        } else {
            try {
                this.nomorurut();
                item.setStatus('D');
                item.setKode_user(id_pegawai);
                serviceSo.tambah(item, lSoDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                if (item.getJenis().equals("c")) {
                    context.getExternalContext().redirect("./edit_consignment.jsf?id=" + item.getNomor());
                }
                if (item.getJenis().equals("m")) {
                    context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getNomor());
                } else {
                    context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void tambahNwCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            this.nomorurut();
            item.setStatus('D');
            item.setKode_user(id_pegawai);
            serviceSo.tambahNewCustomer(item, lSoDetail);
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onCustomerSelect(SelectEvent event) {
        Customer tes = (Customer) event.getObject();
        Customer selectedCustomer = serviceSo.selectOneCustomer(tes.getId_kontak());
        item.setId_customer(selectedCustomer.getId_kontak());
        item.setTelpon(selectedCustomer.getTelepon());
        item.setEmail(selectedCustomer.getEmail());
        item.setKepada(selectedCustomer.getKontak());
        item.setTop(selectedCustomer.getTop());
        item.setCustomer(selectedCustomer.getCustomer());
        item.setId_salesman(selectedCustomer.getId_sales());
        item.setDeliverypoint(selectedCustomer.getAlamat_kirim());
        customerInfo(item.getId_customer());

    }

    public void onSoSelect(SelectEvent event) {
        item.setSobefore(soAutoComplete.getSo().getNomor());
    }

    public void onPenawaranSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Penawaran tes = (Penawaran) event.getObject();
        Boolean cek = true;
        if (serviceSo.cekPenawaran(tes.getNomor()) > 0) {
            cek = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Penawaran No " + tes.getNomor() + " dalam proses pembuatan SO !!", ""));
        }
        if (cek) {
            lSoDetail = new ArrayList<>();

            Penawaran selectedPenawaran = serviceSo.selectOnePenawaran(tes.getNomor(), tes.getRevisi());
            item.setId_customer(selectedPenawaran.getId_customer());
            item.setCustomer(selectedPenawaran.getCustomer());
            item.setKepada(selectedPenawaran.getKepada());
            if (item.getHp() != null) {
                item.setTelpon(selectedPenawaran.getTelpon() + "   Hp : " + selectedPenawaran.getHp());
            } else {
                item.setTelpon(selectedPenawaran.getTelpon());
            }
            item.setId_gudang(selectedPenawaran.getId_gudang());
            item.setKode_mata_uang(selectedPenawaran.getKode_mata_uang());
            item.setEmail(selectedPenawaran.getEmail());
            item.setHp(selectedPenawaran.getHp());
            item.setTotal(selectedPenawaran.getTotal());
            item.setPersendiskon(selectedPenawaran.getPersendiskon());
            item.setTotal_discount(selectedPenawaran.getTotal_discount());
            item.setDpp(item.getTotal() - item.getTotal_discount());
            item.setTotal_ppn(selectedPenawaran.getTotal_ppn());
            item.setGrandtotal(item.getTotal() - item.getTotal_discount() + item.getTotal_ppn());
            item.setTop(selectedPenawaran.getTop());
            item.setCertificate(selectedPenawaran.getCertificate());
            item.setDeliverypoint(selectedPenawaran.getDeliverypoint());
            item.setPov(selectedPenawaran.getPov());
            item.setSyarat(selectedPenawaran.getSyarat());
            item.setNo_penawaran(selectedPenawaran.getNomor());
            item.setJenis("p");
            item.setRevisi_penawaran(selectedPenawaran.getRevisi());
            item.setId_salesman(selectedPenawaran.getId_salesman());

            customerInfo(item.getId_customer());

            List<PenawaranDetail> listPenawaranDetail = new ArrayList<>();
            listPenawaranDetail = serviceSo.selectPenawaranDetail(tes.getNomor(), tes.getRevisi());
            for (int j = 0; j < listPenawaranDetail.size(); j++) {
                SoDetail pd = new SoDetail();
                pd.setNo_so(item.getNomor());
                pd.setUrut(listPenawaranDetail.get(j).getUrut());
                pd.setId_barang(listPenawaranDetail.get(j).getId_barang());
                pd.setNama_barang(listPenawaranDetail.get(j).getNama_barang());
                pd.setQty(listPenawaranDetail.get(j).getQty());
                pd.setQty_kecil(listPenawaranDetail.get(j).getQty_kecil());
                pd.setSatuan_kecil(listPenawaranDetail.get(j).getSatuan_kecil());
                pd.setId_satuan_kecil(listPenawaranDetail.get(j).getId_satuan_kecil());
                pd.setHarga(listPenawaranDetail.get(j).getHarga());
                pd.setTotal(listPenawaranDetail.get(j).getTotal());
                pd.setDiskonpersen(listPenawaranDetail.get(j).getDiskonpersen());
                pd.setDiskonrp(listPenawaranDetail.get(j).getDiskonrp());
                pd.setSatuan_besar(listPenawaranDetail.get(j).getSatuan_besar());
                pd.setAdditional_charge(listPenawaranDetail.get(j).getAdditional_charge());
                lSoDetail.add(pd);
            }
        }
    }

    public void onHargaJualSelect(SelectEvent event) {
        lSoDetail = new ArrayList<>();
        HargaJual selectedHargaJual = new HargaJual();
        HargaJual tes = (HargaJual) event.getObject();
        selectedHargaJual = serviceSo.selectOneHargaJual(tes.getNo_kontrak());
        item.setId_customer(selectedHargaJual.getId_customer());
        item.setCustomer(selectedHargaJual.getCustomer());
        item.setTop(selectedHargaJual.getTop());
        item.setNo_penawaran(selectedHargaJual.getNo_kontrak());
        item.setTelpon(selectedHargaJual.getTelepon());
        item.setJenis("k");
        hargaJualDetilAutoComplete.setNo_kontrak(selectedHargaJual.getNo_kontrak());
        lSoDetail.add(new SoDetail());

    }

    public void onBarangSelect(SoDetail s, Integer i) {
        s.setId_barang(hargaJualDetilAutoComplete.getHargaJualDetil().getId_barang());
        s.setNama_barang(hargaJualDetilAutoComplete.getHargaJualDetil().getNama_barang());
        s.setSatuan_besar(hargaJualDetilAutoComplete.getHargaJualDetil().getSatuan_besar());
        s.setHarga(hargaJualDetilAutoComplete.getHargaJualDetil().getHarga());
        s.setDiskonpersen(0.00);
        s.setDiskonrp(0.00);
        lSoDetail.set(i, s);

    }

    public void onBrgSelect(SoDetail s, Integer i) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Integer k = 0;
        for (int j = 0; j < lSoDetail.size(); j++) {
            if (lSoDetail.get(j).getId_barang() != null && (lSoDetail.get(j).getId_barang().equals(barangAutoComplete.getBarang().getId_barang()))) {
                k++;
            }

        }
        if (k > 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, barangAutoComplete.getBarang().getNama_barang() + " Sudah dipilih !!!", ""));
        } else {
            s.setId_barang(barangAutoComplete.getBarang().getId_barang());
            s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
            s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
            s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
            s.setIsi_satuan(barangAutoComplete.getBarang().getIsi_satuan());
            s.setDiskonpersen(0.00);
            s.setDiskonrp(0.00);
            s.setAdditional_charge(0.00);
            lSoDetail.set(i, s);
        }

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            if (status != null) {
                // jika disend maka cek status so didatabase apakah sudah packinglist
                if (status.equals('S')) {
                    Character test = serviceSo.onLoad(item.getNomor()).getStatus();
                    if (test.equals('C') || test.equals('W') || test.equals('P') || test.equals('R')) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input SO ini sudah proses packinglist ", ""));
                        if (item.getJenis().equals("c")) {
                            context.getExternalContext().redirect("./edit_consignment.jsf?id=" + item.getNomor());
                        }
                        if (item.getJenis().equals("m")) {
                            context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getNomor());
                        } else {
                            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
                        }
                        return;
                    }
                }
                if (status.equals('R')) {
                    status = 'D';
                    requestApproval();
                }
                item.setStatus(status);
                serviceSo.ubah(item, lSoDetail);
                onLoad();
            } else {
                serviceSo.ubahMaintenance(item, lSoDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./so-manual.jsf?id=" + item.getNomor());
                
            }

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            if (item.getJenis().equals("c")) {
                context.getExternalContext().redirect("./edit_consignment.jsf?id=" + item.getNomor());
            }
            if (item.getJenis().equals("m")) {
                context.getExternalContext().redirect("./editmanual.jsf?id=" + item.getNomor());
            } else {
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void requestApproval() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            // hutang jatuhntempo > 0 atau belum pernah ada hutang
            if (hutangJatuhTempo > 0 || serviceSo.checkArCount(item.getId_customer()).equals(0)) {
                serviceSo.insertPersetujuan(item, 'A');
                statusPersetujuan = true;
            } else {
                // hutang lebih besar dari limitkredit
                //dicooment karena semua butuh persetujuan dari DSM
                //if (hutang > limitKredit) {
                    serviceSo.insertPersetujuan(item, 'L');
                //}
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Permintaan persetujuan berhasil dikirim"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Permintaan persetujuan gagal dikirim", ""));
        }
    }

    public void selectBookingRecord() {
        lSoDetail = serviceSo.selectBookingRecord(id_kantor);
    }

    public void manajemenClick() {
        if (item.getIs_manajemen()) {
            item.setId_salesman(null);
        }
        if (item.getStatus() != null) {
            statusInfo(item.getNomor(), item.getId_customer());
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakso.jsf?id=" + item.getNomor());
    }

    public void cetakDaftarSo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String tgl_awal = "";
        String tgl_akhir = "";
        String statusnya = "";
        if (awal != null) {
            tgl_awal = df.format(awal);
        }
        if (akhir != null) {
            tgl_akhir = df.format(akhir);
        }
        if (statusso != null) {
            statusnya = statusso.toString();
        }
        context.getExternalContext().redirect("./cetakDaftarSo.jsf?tgl_awal=" + tgl_awal + "&tgl_akhir=" + tgl_akhir + "&status=" + statusnya);
    }

    public void viewSo(String no) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("/transaksi/penjualan/so/view.jsf?id=" + no);
    }

    public void viewSo2() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view.jsf?id=" + item.getNomor());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            if (item.getStatus() != null) {
                DateFormat bln = new SimpleDateFormat("MM");
                So so = serviceSo.onLoad(item.getNomor());
                if (!bln.format(item.getTanggal()).equals(bln.format(so.getTanggal()))) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                    item.setTanggal(so.getTanggal());
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

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;
        if (serviceSo.onLoad(item.getNomor()).getStatus().equals('R')) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SO ini sudah pernah dicancel", ""));
        }
        if (benar) {
            try {
                item.setStatus(status);
                serviceSo.ubahStatus(item, id_pegawai);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "SO berhasil dicancel"));
            } catch (Exception e) {
                //e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SO gagal dicancel", ""));
            }
        }
    }

    public void updatePersetujuan(Boolean approve) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSo.updatePersetujuan(approve, item.getNomor(), id_pegawai, item.getPesanPersetujuan());
            onLoadPersetujuan();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            //e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void createXls() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Daftar Sales Order");
        PropertyTemplate pt = new PropertyTemplate();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        dateCellStyle.setDataFormat(format.getFormat("dd-MM-yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(format.getFormat("#,##0"));
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFCellStyle centerCellStyle = workbook.createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("No. Sales Order");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("ID Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("Gudang");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("No. Penawaran");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("No. PO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("Tgl PO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("Material Code");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("Nama Produk");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("Qty");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("Satuan");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("Unit Price");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(15);
        cell.setCellValue("Salesman");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(16);
        cell.setCellValue("Dibuat Oleh");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(17);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);

        onLoadListDetail();
        String tes = "";
        String st = "";
        Integer no = 1;
        for (int i = 0; i < lSoDetail.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(no);
                cell.setCellStyle(centerCellStyle);
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(1);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getNomor());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(2);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getTanggal());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(3);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getId_customer());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(4);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getCustomer());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(5);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getGudang());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(6);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getNo_penawaran());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(7);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getReferensi());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(8);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getTgl_ref());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(9);
            cell.setCellValue(lSoDetail.get(i).getId_barang());

            cell = row.createCell(10);
            cell.setCellValue(lSoDetail.get(i).getNama_barang());

            cell = row.createCell(11);
            cell.setCellValue(lSoDetail.get(i).getQty());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(12);
            cell.setCellValue(lSoDetail.get(i).getSatuan_besar());
            cell.setCellStyle(centerCellStyle);

            cell = row.createCell(13);
            cell.setCellValue(lSoDetail.get(i).getHarga());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(14);
            cell.setCellValue(lSoDetail.get(i).getTotal());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(15);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getSalesman());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(16);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lSoDetail.get(i).getUser());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(17);
            if (!lSoDetail.get(i).getNomor().equals(tes)) {
                switch (lSoDetail.get(i).getStatus()) {
                    case 'D':
                        st = "Draft";
                        break;
                    case 'S':
                        st = "Send";
                        break;
                    case 'C':
                        st = "Packinglist Draft";
                        break;
                    case 'P':
                        st = "Packinglist Completed";
                        break;
                    case 'R':
                        st = "Cancel";
                        break;

                }
                cell.setCellValue(st);
                tes = lSoDetail.get(i).getNomor();
                no++;
            } else {
                cell.setCellValue("");
            }

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
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        sheet.autoSizeColumn(15);
        sheet.autoSizeColumn(16);
        sheet.autoSizeColumn(17);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Daftar Sales Order.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    public void outStandingSo() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lSoDetail = serviceSo.outstandingSo(awal, akhir, id_gudang, item.getId_customer());
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
