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
import net.sra.prime.ultima.entity.AccKas;
import net.sra.prime.ultima.entity.AccKasDetil;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.service.ServiceAccKas;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAccKas implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccKas> lAccKas = new ArrayList<>();
    private AccKas item;
    private List<AccKasDetil> lAccKasDetil = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private String id_kantor;

    @Inject
    private Page page;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Autowired
    ServiceAccKas serviceAccKas;

    @PostConstruct
    public void init() {
        item = new AccKas();
    }

    public void initKasmasuk() {
        this.initItem();
        item.setId_jenis_transaksi('I');
    }

    public void initKasKeluar() {
        this.initItem();
        item.setId_jenis_transaksi('O');
    }

    public void initItem() {
        Date date = new Date();
        item = new AccKas();
        lAccKasDetil = new ArrayList<>();
        item.setTanggal(date);
        item.setId_kantor(page.getMyPegawai().getId_kantor_new());
        lAccKasDetil.add(new AccKasDetil());
        InternalKantorCabang internalKantorCabang = serviceAccKas.selectOneKantor(page.getMyPegawai().getId_kantor_new());
        item.setKantor(internalKantorCabang.getNama());
        item.setId_account(internalKantorCabang.getAccount_kas());
    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = serviceAccKas.noMax(page.getMyPegawai().getId_kantor_new(), item.getId_jenis_transaksi(), Integer.parseInt(bulan), Integer.parseInt(year));
        InternalKantorCabang internalKantorCabang = serviceAccKas.selectOneKantor(page.getMyPegawai().getId_kantor_new());
        String tanda;
        if (item.getId_jenis_transaksi().equals('O')) {
            tanda = "OUT";
        } else {
            tanda = "IN";
        }
        if (noMax == null) {
            item.setNomor("001/" + bulan + tahun + "/PC-" + tanda + "/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/" + bulan + tahun + "/PC-" + tanda + "/" + internalKantorCabang.getNumbercode());
        }
    }

    public void hitungJumlahTotal() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lAccKasDetil.size(); j++) {
            if (lAccKasDetil.get(j).getJumlah() != null) {
                jml = jml + lAccKasDetil.get(j).getJumlah();
            }
        }
        item.setTotal(jml);

    }

    public void onLoadListIn() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, page.getMyPegawai().getId_kantor_new(), 'I');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, page.getMyPegawai().getId_kantor_new(), 'I');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListInAcc() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, id_kantor, 'I');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, id_kantor, 'I');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListOut() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, page.getMyPegawai().getId_kantor_new(), 'O');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, page.getMyPegawai().getId_kantor_new(), 'O');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListOutAcc() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, id_kantor, 'O');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, id_kantor, 'O');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AccKas> getDataAccKas() {
        return lAccKas;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (nomor != null && !"".equals(nomor)) {
            try {
                serviceAccKas.delete(nomor);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
                context.getExternalContext().redirect("./list.jsf");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data belum dipilih !!!!", ""));
        }
    }

    public List<AccKasDetil> getDataAccKasDetil() {
        return lAccKasDetil;

    }

    public void extend() {
        lAccKasDetil.add(new AccKasDetil());
    }

    public void onDeleteClicked(AccKasDetil accKasDetil) {
        lAccKasDetil.remove(accKasDetil);
        this.hitungJumlahTotal();
    }

    public void updatedsm() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./editdsm.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./view.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void onLoad() {
        item = serviceAccKas.onLoad(item.getNomor());
        lAccKasDetil = serviceAccKas.onLoadDetail(item.getNomor());
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setStatus('D');
        try {
            this.nomorurut();
            serviceAccKas.tambah(item, lAccKasDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean cek = true;
        if (status.equals('P')) {
            for (int i = 0; i < lAccKasDetil.size(); i++) {
                if (lAccKasDetil.get(i).getId_account_detail() == null) {
                    cek = false;
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Account Code Harus diisi", ""));
                    i = i + lAccKasDetil.size();
                }
            }
        }
        if (cek) {
            item.setStatus(status);
            try {
                item.setStatus(status);
                serviceAccKas.ubah(item, lAccKasDetil);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void onPosting(AccKas accKas) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            item = serviceAccKas.onLoad(accKas.getNomor());
            lAccKasDetil = serviceAccKas.onLoadDetail(accKas.getNomor());
            item.setStatus('P');
            serviceAccKas.ubah(item, lAccKasDetil);
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, id_kantor, 'I');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, id_kantor, 'I');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onPostingOut(AccKas accKas) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            item = serviceAccKas.onLoad(accKas.getNomor());
            lAccKasDetil = serviceAccKas.onLoadDetail(accKas.getNomor());
            item.setStatus('P');
            serviceAccKas.ubah(item, lAccKasDetil);
            if (awal != null && akhir != null) {
                lAccKas = serviceAccKas.onLoadList(awal, akhir, id_kantor, 'O');
            } else if (awal == null && akhir == null) {
                lAccKas = serviceAccKas.onLoadList(null, null, id_kantor, 'O');
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onAccountSelect(AccKasDetil s, Integer i) {
        s.setId_account_detail(accountAutoComplete.getAccount().getId_account());
        s.setAccount(accountAutoComplete.getAccount().getAccount());

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakkaskeluar.jsf?id=" + item.getNomor());
    }

    public void cetakkasmasuk() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakkasmasuk.jsf?id=" + item.getNomor());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            AccKas accKas = serviceAccKas.onLoad(item.getNomor());
            if (!bln.format(item.getTanggal()).equals(bln.format(accKas.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(accKas.getTanggal());
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
