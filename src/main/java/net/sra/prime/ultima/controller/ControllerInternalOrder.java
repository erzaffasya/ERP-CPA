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
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.InternalOrder;
import net.sra.prime.ultima.entity.InternalOrderDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.service.ServiceInternalOrder;
import net.sra.prime.ultima.service.ServiceSo;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerInternalOrder implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<InternalOrder> lInternalOrder = new ArrayList<>();
    private InternalOrder item;
    private List<IntruksiPo> lIp = new ArrayList<>();
    private IntruksiPo itemIp;
    private List<InternalOrderDetail> lInternalOrderDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    Character status;
    private Pegawai pegawai;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ServiceInternalOrder serviceInternalOrder;

    @Autowired
    ServiceSo serviceSo;

    @PostConstruct
    public void init() {
        item = new InternalOrder();
        pegawai = page.getMyPegawai();
        if (pegawai.getId_jabatan_new() == 110) {
            status = 'F';
        } else if (pegawai.getId_departemen_new() == 107) {
            status = 'A';
        } else if (pegawai.getHeaddepartemen()) {
            status = 'S';
        } else {
            status = 'D';
        }
    }

    private void initItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Integer jmlhead = serviceInternalOrder.selectCountHeadDepartemen(pegawai.getId_departemen_new());
        Pegawai pg = serviceInternalOrder.jabatanPegawai(110);
        item.setApprove(pg.getId_pegawai());
        item.setApprove_name(pg.getNama());
        item.setJabatan_approved(pg.getJabatan());
        item.setDibuat_nama(pegawai.getNama());
        item.setJabatan(pegawai.getJabatan());
        Date date = new Date();
        //item.setTanggal(date);
        item.setId_kantor(pegawai.getId_kantor_new());
        item.setKontak(pegawai.getId_pegawai());
        item.setStatus('D');

    }

    public void initItemNonSo() {
        item = new InternalOrder();
        lInternalOrderDetail = new ArrayList<>();
        lInternalOrderDetail.add(new InternalOrderDetail());
        this.initItem();
    }

    public void initItemSo() {
        this.initItem();
        //lInternalOrderDetail = new ArrayList<>();
        List<SoDetail> lSoDetail = serviceSo.onLoadDetail(item.getRef_no());
        for (int i = 0; i < lSoDetail.size(); i++) {
            InternalOrderDetail itemdetail = new InternalOrderDetail();
            itemdetail.setId_barang(lSoDetail.get(i).getId_barang());
            itemdetail.setNama_barang(lSoDetail.get(i).getNama_barang());
            itemdetail.setQty(lSoDetail.get(i).getQty());
            itemdetail.setSatuan_besar(lSoDetail.get(i).getSatuan_besar());
            lInternalOrderDetail.add(itemdetail);
        }
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceInternalOrder.noMax(pegawai.getId_kantor_new(), Integer.parseInt(bulannya), Integer.parseInt(tahun));
        if (noMax == null) {
            item.setNomor_io("001/IO/" + page.getMyInternalPerusahaan().getInisial() + "-" + page.getMyKantor().getNumbercode() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor_io(noMax + "/IO/" + page.getMyInternalPerusahaan().getInisial() + "-" + page.getMyKantor().getNumbercode() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        lInternalOrder = serviceInternalOrder.onLoadList(pegawai.getId_kantor_new(), pegawai.getId_pegawai(), awal, akhir, status);
    }

    public void onLoadListApprove() {
        lInternalOrder = serviceInternalOrder.onLoadList(null, null, awal, akhir, status);
    }

    public void onLoadListGudang() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (page.getMyPegawai().getId_jabatan_new() == 110) {
                lInternalOrder = serviceInternalOrder.onLoadList(null, null, awal, akhir, status);
            } else {
                lInternalOrder = serviceInternalOrder.onLoadList(null, pegawai.getId_pegawai(), awal, akhir, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoadListMaintenance() {
        lInternalOrder = serviceInternalOrder.onLoadList(null, null, awal, akhir, status);
    }

    public void onLoadListForIt() {
        lInternalOrder = serviceInternalOrder.onLoadList(null, pegawai.getId_pegawai(), awal, akhir, 'A');
    }

    public List<InternalOrder> getDataInternalOrder() {
        return lInternalOrder;
    }

    public List<IntruksiPo> getDataIp() {
        return lIp;
    }

    public List<InternalOrderDetail> getDataInternalOrderDetail() {
        return lInternalOrderDetail;
    }

    public void delete(String nomor_io) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceInternalOrder.delete(nomor_io);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lInternalOrderDetail.add(new InternalOrderDetail());
    }

    public void onDeleteClicked(InternalOrderDetail internalOrderDetail) {
        lInternalOrderDetail.remove(internalOrderDetail);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor_io());

    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./internalorder-edit.jsf?id=" + item.getNomor_io());

    }

    public void viewForIt() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view_forit.jsf?id=" + item.getNomor_io());

    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view.jsf?id=" + item.getNomor_io());

    }

    public void createIT() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (serviceInternalOrder.selectOneGudanga(item.getId_gudang_asal()).getId_kontak() == null || serviceInternalOrder.selectOneGudanga(item.getId_gudang_asal()).getId_kontak().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input Contact Person Gudang" + item.getGudang_asal() + " belum diisi !!!", ""));
        } else if (serviceInternalOrder.selectOneGudanga(item.getId_gudang_tujuan()).getId_kontak() == null || serviceInternalOrder.selectOneGudanga(item.getId_gudang_tujuan()).getId_kontak().equals("")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input Contact Person Gudang" + item.getGudang_tujuan() + " belum diisi !!!", ""));
        } else {
            context.getExternalContext().redirect("../internaltransfer/add.jsf?id=" + item.getNomor_io());
        }
    }

    public void onLoad() {
        item = serviceInternalOrder.onLoad(item.getNomor_io());
        lInternalOrderDetail = serviceInternalOrder.onLoadDetail(item.getNomor_io());
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {

            for (int i = 0; i < lInternalOrderDetail.size(); i++) {
                if (lInternalOrderDetail.get(i).getQty() == null || lInternalOrderDetail.get(i).getQty() == 0) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty harus diisi !!", ""));
                    return;
                }
            }
            this.nomorurut();
            serviceInternalOrder.tambah(item, lInternalOrderDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor_io());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onBarangSelect(InternalOrderDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lInternalOrderDetail.set(i, s);

    }

    public void approveStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            Date date = new Date();
            serviceInternalOrder.approveStatus(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (status != null) {
                item.setStatus(status);
            }
            serviceInternalOrder.ubah(item, lInternalOrderDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            serviceInternalOrder.ubahStatus(item, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    /// Intruksi PO
    public void onLoadListIp() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            lIp = serviceInternalOrder.onLoadListIp(pegawai.getId_pegawai(), awal, akhir);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal diload", ""));
        }
    }

    public void viewIp() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./addbyip.jsf?id=" + itemIp.getId());

    }

    public void onLoadIp() {
        itemIp = serviceInternalOrder.onLoadIp(item.getId_ip());
        initItem();
        //item.setId_ip(item.getId_ip());
        item.setNomor_ip(itemIp.getNo_intruksi_po());
        item.setTanggal_ip(itemIp.getTanggal());
        item.setId_gudang_tujuan(itemIp.getId_gudang());
        List<IntruksiPoDetail> lIpDetil = serviceInternalOrder.onLoadListIpDetil(item.getId_ip());
        lInternalOrderDetail = new ArrayList<>();
        InternalOrderDetail detil;
        for (int i = 0; i < lIpDetil.size(); i++) {

            detil = new InternalOrderDetail();
            if (lIpDetil.get(i).getQty() > 0) {
                detil.setUrut(i + 1);
                detil.setId_barang(lIpDetil.get(i).getId_barang());
                detil.setNama_barang(lIpDetil.get(i).getNama_barang());
                detil.setQty_ip(lIpDetil.get(i).getQty());
                detil.setSatuan_besar(lIpDetil.get(i).getSatuan_kecil());
                lInternalOrderDetail.add(detil);
            }
        }
    }

    public void onGudangAsalSelect() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai p = serviceInternalOrder.selectDsmGudang(item.getId_gudang_asal());
        if (p == null) {
            item.setId_gudang_asal("");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "DSM Gudang ini blm ada", "Silahkan hubungi administrator"));
            return;
        }
        item.setSend_by(p.getId_pegawai());
        item.setSend_by_name(p.getNama());
        item.setSend_by_jabatan(p.getJabatan());

    }
}
