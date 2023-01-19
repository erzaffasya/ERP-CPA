package net.sra.prime.ultima.controller;

import java.io.IOException;
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
import net.sra.prime.ultima.entity.Konsumsi;
import net.sra.prime.ultima.entity.KonsumsiDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.service.ServiceKonsumsi;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SoAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKonsumsi implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Konsumsi> lKonsumsi = new ArrayList<>();
    private Konsumsi item;
    private List<KonsumsiDetail> lKonsumsiDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private String gudang;

    @Inject
    private SoAutoComplete soAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServiceKonsumsi serviceKonsumsi;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Konsumsi();
    }

    public void initItem() {
        Date date = new Date();
        item.setTanggal(date);
        awal = new Date();
        akhir = new Date();
        lKonsumsiDetail = new ArrayList<>();
        lKonsumsiDetail.add(new KonsumsiDetail());
    }

    

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lKonsumsi = serviceKonsumsi.onLoadList(awal, akhir, gudang);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Konsumsi> getDataKonsumsi() {
        return lKonsumsi;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceKonsumsi.delete(id);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<KonsumsiDetail> getDataKonsumsiDetail() {
        return lKonsumsiDetail;

    }

    public void extend() {
        lKonsumsiDetail.add(new KonsumsiDetail());
    }

    public void onDeleteClicked(KonsumsiDetail packinglistDetail) {
        lKonsumsiDetail.remove(packinglistDetail);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void onLoad() {
        try {
            item = serviceKonsumsi.onLoad(item.getId());
//            Customer customer = new Customer();
//            customer.setId_kontak(item.getId_customer());
//            customer.setCustomer(item.getCustomer());
            lKonsumsiDetail = serviceKonsumsi.onLoadDetail(item.getId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus('D');
            item.setDiinput(page.getMyPegawai().getId_pegawai());
            serviceKonsumsi.tambah(item, lKonsumsiDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    
    public void onBarangSelect(KonsumsiDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lKonsumsiDetail.set(i, s);

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        Character st=item.getStatus();
        if (status.equals('S')) {
            for (int i = 0; i < lKonsumsiDetail.size(); i++) {
                if (lKonsumsiDetail.get(i).getPemakaian()== 0 || lKonsumsiDetail.get(i).getPemakaian()== null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery tidak boleh kosong !!!!", ""));
                    benar = false;
                }
            }
        }
        if (benar) {
            try {
                item.setStatus(status);
                serviceKonsumsi.ubah(item, lKonsumsiDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                item.setStatus(st);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        }
    }

}
