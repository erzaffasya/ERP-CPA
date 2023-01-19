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
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccPettyCash;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.service.ServiceAccPettyCash;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAccPettyCash implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;

    private List<AccPettyCash> lAccPettyCash = new ArrayList<>();
    private AccPettyCash item;
    private AccGlTrans itemTrans;
    private AccGlDetail itemGl;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Autowired
    ServiceAccPettyCash serviceAccPettyCash;

    @PostConstruct
    public void init() {
        item = new AccPettyCash();

    }

    public void initItem() {
        item = new AccPettyCash();
        AccPettyCash ite = serviceAccPettyCash.selectPC(page.getMyKantor().getId_kantor_cabang());
        Date date = new Date();
        item.setTanggal(date);
        item.setId_kantor(page.getMyKantor().getId_kantor_cabang());
        Double saldo;
        String saldoawal;
        Integer account = serviceAccPettyCash.selectOneKantor(page.getMyKantor().getId_kantor_cabang()).getAccount_kas();
        if (ite == null) {
            DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
            String tahun = thn.format(item.getTanggal());
            AccValue accValue = serviceAccPettyCash.selectOneAccValue("2018", account);
            saldo = accValue.getDB0();
            saldoawal = "0";

        } else {
            saldo = ite.getSaldo_akhir();
            saldoawal = ite.getAkhir();
        }
        item.setSaldo_awal(saldo);
        itemGl = new AccGlDetail();
        lAccGlDetail = new ArrayList<>();
        itemGl.setKeterangan("Saldo Awal");
        itemGl.setSaldo(saldo);
        lAccGlDetail.add(itemGl);
        try {
            List<AccGlDetail> lAkd = serviceAccPettyCash.selectKas(page.getMyKantor().getAccount_kas(), saldoawal);
            for (int i = 0; i < lAkd.size(); i++) {

                itemGl = lAkd.get(i);
                if (i == 0) {
                    item.setAwal(itemGl.getGl_number());
                    item.setTgl_awal(itemGl.getGl_date());
                }
                if (lAkd.get(i).getIs_debit()) {
                    saldo = saldo + lAkd.get(i).getValue();
                    itemGl.setDebit(itemGl.getValue());
                } else {
                    saldo = saldo - lAkd.get(i).getValue();
                    itemGl.setCredit(itemGl.getValue());
                }
                itemGl.setSaldo(saldo);
                lAccGlDetail.add(itemGl);
                item.setAkhir(itemGl.getGl_number());
                item.setTgl_akhir(itemGl.getGl_date());

            }
            item.setSaldo_akhir(saldo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = serviceAccPettyCash.noMax(page.getMyKantor().getId_kantor_cabang());
        InternalKantorCabang internalKantorCabang = serviceAccPettyCash.selectOneKantor(page.getMyKantor().getId_kantor_cabang());
        item.setKantor(internalKantorCabang.getNama());
        String tanda;
        if (noMax == null) {
            item.setNomor("001/" + bulan + tahun + "/PC/" + internalKantorCabang.getNumbercode());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/" + bulan + tahun + "/PC/" + internalKantorCabang.getNumbercode());
        }
    }

    public void onLoadList() {
        lAccPettyCash = serviceAccPettyCash.onLoadList(awal, akhir, page.getMyKantor().getId_kantor_cabang(), 'O');
    }

    public List<AccPettyCash> getDataAccPettyCash() {
        return lAccPettyCash;

    }

    public List<AccGlDetail> getDataAccKasDetil() {
        return lAccGlDetail;

    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (id != null) {
            try {
                serviceAccPettyCash.delete(id);
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

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void onLoad() {
        try {
            item = serviceAccPettyCash.selectOnePC(item.getNomor());
            // lAccKasDetil = referensi.selectAllDetail(item.getNomor());
            Double saldo = item.getSaldo_awal();
            //item.setSaldo_awal(saldo);
            itemGl = new AccGlDetail();
            lAccGlDetail = new ArrayList<>();
            itemGl.setKeterangan("Saldo Awal");
            itemGl.setSaldo(saldo);
            lAccGlDetail.add(itemGl);
            // lAccGlDetail = reff.selectKasEdit(page.getMyKantor().getAccount_kas(), item.getAwal(), item.getAkhir());

            List<AccGlDetail> lAkd = serviceAccPettyCash.selectKasEdit(page.getMyKantor().getAccount_kas(), item.getAwal(), item.getAkhir());
            for (int i = 0; i < lAkd.size(); i++) {

                itemGl = lAkd.get(i);
                if (i == 0) {
                    item.setAwal(itemGl.getGl_number());
                    item.setTgl_awal(itemGl.getGl_date());
                }
                if (lAkd.get(i).getIs_debit()) {
                    saldo = saldo + lAkd.get(i).getValue();
                    itemGl.setDebit(itemGl.getValue());
                } else {
                    saldo = saldo - lAkd.get(i).getValue();
                    itemGl.setCredit(itemGl.getValue());
                }
                itemGl.setSaldo(saldo);
                lAccGlDetail.add(itemGl);
                item.setAkhir(itemGl.getGl_number());
                item.setTgl_akhir(itemGl.getGl_date());

            }
            item.setSaldo_akhir(saldo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            nomorurut();
            serviceAccPettyCash.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
