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
import net.sra.prime.ultima.entity.PembayaranPiutangDetail;
import net.sra.prime.ultima.entity.InsentifCustomer;
import net.sra.prime.ultima.service.ServicePembayaranPiutang;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerInsentifCustomer implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private InsentifCustomer item;
    private List<InsentifCustomer> lInsentifCustomers = new ArrayList<>();
    private List<PembayaranPiutangDetail> lPembayaranPiutangDetail = new ArrayList<>();
    private List<PembayaranPiutangDetail> selectedPembayaranPiutangDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private String id_salesman;

    
    @Inject
    private Page page;

    
    @Inject
    private Options options;

    @Autowired
    ServicePembayaranPiutang servicePembayaranPiutang;

    @PostConstruct
    public void init() {
        item = new InsentifCustomer();
        if (status == null) {
            status = 'D';
        }
        lPembayaranPiutangDetail = new ArrayList<>();
        selectedPembayaranPiutangDetail = new ArrayList<>();
        Date date = new Date();
    }

    

    
    
    public void onLoadList() {
        if (status.equals('X')) {
            status = null;
        }
        //lPembayaranPiutang = servicePembayaranPiutang.onLoadList(awal, akhir, statusap);

    }

    private Double total_dpp, total_ppn, total;

    public void InsentifCustomer(Date awalnya, Date akhirnya, String id_salesmannya, String id_customernya, Integer id_departemennya) {
        total_dpp = 0.00;
        total_ppn = 0.00;
        total = 0.00;
        String id_customer = null;
        
        lPembayaranPiutangDetail = servicePembayaranPiutang.PendapatanMarketing(awalnya, akhirnya, id_salesmannya, id_customernya, id_departemennya);
        for (int i = 0; i < lPembayaranPiutangDetail.size(); i++) {
            if (lPembayaranPiutangDetail.get(i).getDpp() != null) {
                total_dpp = total_dpp + lPembayaranPiutangDetail.get(i).getDpp();
            }
            if (lPembayaranPiutangDetail.get(i).getPpn() != null) {
                total_ppn = total_ppn + lPembayaranPiutangDetail.get(i).getPpn();
            }
            if (lPembayaranPiutangDetail.get(i).getJumlah_bayar() != null) {
                total = total + lPembayaranPiutangDetail.get(i).getJumlah_bayar();
            }
        }

    }

    

    public void onLoadInvoice() {
        id_salesman = page.getMyPegawai().getId_pegawai();
        String id_customer = null;
        InsentifCustomer(awal, akhir, id_salesman, id_customer, 0);
    }

    public List<InsentifCustomer> getDataInsentifCustomer() {
        return lInsentifCustomers;
    }

    
    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePembayaranPiutang.delete(nomor);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public List<PembayaranPiutangDetail> getDataPembayaranPiutangDetail() {
        return lPembayaranPiutangDetail;

    }
    
    public List<PembayaranPiutangDetail> getDataPembayaranPiutangDetailSelected() {
        return selectedPembayaranPiutangDetail;

    }

    public void extend() {
        lPembayaranPiutangDetail.add(new PembayaranPiutangDetail());
    }

    public void onDeleteClicked(PembayaranPiutangDetail pembayaranPiutangDetail) {
        lPembayaranPiutangDetail.remove(pembayaranPiutangDetail);
        

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (awal == null && akhir == null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor()+ "&status=" + status);
        } else if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor()+ "&awal=" + new SimpleDateFormat("yyyy-MM-dd").format(awal) + "&akhir=" + new SimpleDateFormat("yyyy-MM-dd").format(akhir) + "&status=" + status);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    

    public void onLoad() {
        try {
            //item = servicePembayaranPiutang.onLoad(item.getNo_pembayaran_piutang());
           // lPembayaranPiutangDetail = servicePembayaranPiutang.onLoadDetail(item.getNo_pembayaran_piutang());
           
        } catch (Exception ex) {
            ex.printStackTrace();
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
            String noMax = servicePembayaranPiutang.noMaxInsentif(Integer.parseInt(bulannya), Integer.parseInt(tahun));
            if (noMax == null) {
                item.setNomor("001/"+ page.getMyInternalPerusahaan().getInisial()+"/PAP/" + bulan + "/" + tahun);
            } else {
                Integer nomor = Integer.parseInt(noMax);
                nomor = nomor + 1;
                noMax = String.format("%03d", nomor);
                item.setNomor(noMax + "/"+ page.getMyInternalPerusahaan().getInisial()+"/PAP/" + bulan + "/" + tahun);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
            try {
                this.nomorurut();
                item.setStatus('D');
                item.setDsr(page.getMyPegawai().getId_pegawai());
                item.setCreateby(page.getMyPegawai().getId_pegawai());
                servicePembayaranPiutang.tambahInsentif(item, selectedPembayaranPiutangDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                //context.getExternalContext().redirect("./edit.jsf?id=" + item.getId()+ "&status=D");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        
    }


    
    
}
