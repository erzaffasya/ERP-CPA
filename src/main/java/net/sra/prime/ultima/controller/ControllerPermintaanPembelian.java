package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.PermintaanPembelianPesan;
import net.sra.prime.ultima.service.ServicePermintaanPembelian;
import org.primefaces.event.SelectEvent;
//import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPermintaanPembelian implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<PermintaanPembelian> lPermintaanPembelian = new ArrayList<>();
    private PermintaanPembelian item;
    private PermintaanPembelianDetail itemdetail;
    private PermintaanPembelianDetail sP;
    private List<PermintaanPembelianDetail> lPermintaanPembelianDetail = new ArrayList<>();
    private String id_perusahaan;
    private Date awal;
    private Date akhir;
    private Character statusnya;
    private Double total;
    private Pegawai pegawai;
    // @Inject
    // private BarangAutoComplete barangAutoComplete;
    @Inject
    private Page page;

    @Autowired
    ServicePermintaanPembelian servicePermintaanPembelian;

    @PostConstruct
    public void init() {
        item = new PermintaanPembelian();
        itemdetail = new PermintaanPembelianDetail();
        pegawai = page.getMyPegawai();
    }

    public void initItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item = new PermintaanPembelian();
        itemdetail = new PermintaanPembelianDetail();
        lPermintaanPembelianDetail = new ArrayList<>();
        item.setJenis('P');
        item.setTotal(0.00);
        item.setDibuat(pegawai.getId_pegawai());
        item.setDibuat_nama(pegawai.getNama());
        item.setJabatan(pegawai.getJabatan());
        sP = new PermintaanPembelianDetail();
        lPermintaanPembelianDetail.add(sP);
        try {
            Integer jmlhead = servicePermintaanPembelian.selectCountHeadDepartemen(pegawai.getId_departemen_new());
            if (jmlhead == 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Head Departemen blm ada !! Silahkan hubungi Administrator", ""));

            } else {

                //jika yang membuat adalah head departemen maka hanya perlu persetuajuan dari finance dan direktur
                if (!pegawai.getHeaddepartemen()) {
                    if (jmlhead > 1) {
                        Pegawai pg = servicePermintaanPembelian.selectHeadDepartemenByKantor(pegawai.getId_departemen_new(), pegawai.getId_kantor_new());
                        item.setChecked_name(pg.getId_pegawai());
                        item.setNama_cheked(pg.getNama());

                        item.setJabatan_cheked(pg.getJabatan());
                    } else if (pegawai.getId_departemen_new() != 105) {
                        Pegawai pg = servicePermintaanPembelian.selectHeadDepartemen(pegawai.getId_departemen_new());
                        item.setChecked_name(pg.getId_pegawai());
                        item.setNama_cheked(pg.getNama());

                        item.setJabatan_cheked(pg.getJabatan());
                    }
                }

                //jika atasan langsung bukan head departemen dam atasan langusng bukan direktur dan bukan departemen finance maka perlu persetujuan spv
                if (!pegawai.getAtasan_langsung().equals(item.getChecked_name()) && !pegawai.getAtasan_langsung().equals("CPA-001")) {
                    //jika departemen finanece
                    if (pegawai.getId_departemen_new() == 105) {
                        Pegawai p = servicePermintaanPembelian.selectOnePegawai(pegawai.getAtasan_langsung());
                        Pegawai pg = servicePermintaanPembelian.selectHeadDepartemen(pegawai.getId_departemen_new());
                        //jika atasasam langsung bukan head departemen finance maka perlu persetujuan spv
                        if (!p.getId_pegawai().equals(pg.getId_pegawai())) {
                            item.setSpv(p.getId_pegawai());
                            item.setSpv_name(p.getNama());
                            item.setSpv_jabatan(p.getJabatan());
                        }

                    } else {
                        Pegawai p = servicePermintaanPembelian.selectOnePegawai(pegawai.getAtasan_langsung());
                        item.setSpv(p.getId_pegawai());
                        item.setSpv_name(p.getNama());
                        item.setSpv_jabatan(p.getJabatan());
                    }
                }

                //jika yang membuat adalah manager finance hanya butuh persetujuan direktur
                if (!pegawai.getId_pegawai().equals("CPA-073")) {
                    Pegawai peg = servicePermintaanPembelian.selectOnePegawai("CPA-073");
                    item.setApproved2(peg.getId_pegawai());
                    item.setNama_approved2(peg.getNama());
                    item.setJabatan_approved2(peg.getJabatan());
                }

                Pegawai pgw = servicePermintaanPembelian.selectOnePegawai("CPA-002");
                item.setDisetujui(pgw.getId_pegawai());
                item.setNama_approved(pgw.getNama());
                item.setJabatan_approved(pgw.getJabatan());
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void nomorurut() {
        try {
            final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
            DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
            String tahun = thn.format(item.getTanggal());
            DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
            String bulannya = bln.format(item.getTanggal());
            String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
            String noMax = servicePermintaanPembelian.noMax(Integer.parseInt(bulannya), Integer.parseInt(tahun));

            if (noMax == null) {
                item.setNo_pp("001/" + page.getMyInternalPerusahaan().getInisial() + "/PP/" + bulan + "/" + tahun);
            } else {
                Integer nomor = Integer.parseInt(noMax);
                nomor = nomor + 1;
                noMax = String.format("%03d", nomor);
                item.setNo_pp(noMax + "/" + page.getMyInternalPerusahaan().getInisial() + "/PP/" + bulan + "/" + tahun);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadList() {
        try {
            // 150 : Procurement Supervisor
            // 128 : Procurement Admin , 178 : Procurement Admin 1
            // CPA-018 : Zera Parengky --> diubah menjadi id jabatan new 194 : Treasure Staff 1
            // CPA-060 : Shergie Putri --> diubah menjadi id jabatan new 118 : Finance & Accounting Supervisor

            if (pegawai.getId_jabatan_new().equals(150) || pegawai.getId_jabatan_new().equals(128) || pegawai.getId_jabatan_new().equals(118) || pegawai.getId_jabatan_new().equals(194) || pegawai.getId_jabatan_new().equals(178)) {
                // bisa melihat semua PP yang dibuatnya dan yang sudah disetujui/reject oleh direktur oleh direktur
                lPermintaanPembelian = servicePermintaanPembelian.onLoadList(awal, akhir, pegawai.getId_pegawai(), true, statusnya, pegawai.getId_jabatan_new());
            } else {
                lPermintaanPembelian = servicePermintaanPembelian.onLoadList(awal, akhir, pegawai.getId_pegawai(), false, statusnya, pegawai.getId_jabatan_new());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListMaintenance() {
        try {
            lPermintaanPembelian = servicePermintaanPembelian.onLoadListMaintenance(awal, akhir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListDashboard() {
        init();
        try {
            lPermintaanPembelian = servicePermintaanPembelian.onLoadList(null, null, pegawai.getId_pegawai(), false, statusnya, pegawai.getId_jabatan_new());
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public List<PermintaanPembelian> getDataPermintaanPembelian() {
        return lPermintaanPembelian;
    }

    public void delete(String no_pp) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.delete(no_pp);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PermintaanPembelianDetail> getDataPermintaanPembelianDetail() {
        return lPermintaanPembelianDetail;

    }

    public List<PermintaanPembelianPesan> getDataPesan() {
        List<PermintaanPembelianPesan> lPermintaanPembelianPesan = new ArrayList<>();
        try {
            lPermintaanPembelianPesan = servicePermintaanPembelian.selectPesan(item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return lPermintaanPembelianPesan;

    }

    public void extend() {
        sP = new PermintaanPembelianDetail();
        lPermintaanPembelianDetail.add(sP);
    }

    public void onDeleteClicked(PermintaanPembelianDetail item) {
        lPermintaanPembelianDetail.remove(item);
        hitungJumlahTotal();
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./permintaanpembelian-edit.jsf?id=" + item.getNo_pp());
    }

    public void updateDashboard(String id) throws IOException {
        item = new PermintaanPembelian();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./transaksi/pembelian/permintaan/edit.jsf?id=" + id);
    }

    public void onLoad() {
        try {
            item = servicePermintaanPembelian.onLoad(item.getNo_pp());
            lPermintaanPembelianDetail = servicePermintaanPembelian.onLoadListDetail(item.getNo_pp());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            servicePermintaanPembelian.tambah(item, lPermintaanPembelianDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.ubah(item, lPermintaanPembelianDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahMaintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.ubahMaintenance(item, lPermintaanPembelianDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void sendPP() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.sendPP(item, lPermintaanPembelianDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void approveSpv(Boolean setuju) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.updateSpv(item, setuju, pegawai.getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diapprove"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void approveAtasan(Boolean setuju) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.updateAtasan(item, setuju, pegawai.getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diapprove"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void approveDirektur(Boolean setuju) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.updateDirektur(item, setuju, pegawai.getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diapprove"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void approveKeuangan(Boolean setuju) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePermintaanPembelian.updateKeuangan(item, setuju, pegawai.getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diapprove"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_pp());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void choose() {
        String list = "form_pesan";
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        //RequestContext.getCurrentInstance().openDialog(list, options, null);
    }

    public void createPO() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../po/addreguler.jsf?id=" + item.getNo_pp());
    }

    public void hitungTotal(PermintaanPembelianDetail s, Integer i) {
        if (s.getHarga() == null) {
            s.setHarga(0.00);
        }
        if (s.getJumlah_order() == null) {
            s.setJumlah_order(0.00);
        }

        s.setAmount(s.getHarga() * s.getJumlah_order());
        lPermintaanPembelianDetail.set(i, s);
        this.hitungJumlahTotal();
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lPermintaanPembelianDetail.size(); j++) {
            jml = jml + lPermintaanPembelianDetail.get(j).getAmount();
        }
        item.setTotal(jml);

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakpp.jsf?id=" + item.getNo_pp());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        } else if (item.getStatus_dibuat() != null) {

            PermintaanPembelian permintaanPembelian = servicePermintaanPembelian.onLoad(item.getNo_pp());
            if (!bln.format(item.getTanggal()).equals(bln.format(permintaanPembelian.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(permintaanPembelian.getTanggal());
            }
        } else {
            if (!bln.format(item.getTanggal()).equals(bln.format(new Date()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal  tidak boleh beda dengan bulan sekarang !!!", ""));
                item.setTanggal(null);
            }
        }

    }

    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
    }

}
