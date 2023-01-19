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
import net.sra.prime.ultima.entity.AccApFaktur;
import net.sra.prime.ultima.entity.AccProposalAp;
import net.sra.prime.ultima.entity.AccProposalApDetail;
import net.sra.prime.ultima.service.ServiceProposalAp;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerProposalAp implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccApFaktur> lAccApFaktur = new ArrayList<>();
    private List<AccApFaktur> selectedAccApFaktur = new ArrayList<>();
    private List<AccProposalAp> lAccProposalAp = new ArrayList<>();
    private List<AccProposalAp> selectedAccProposalAp = new ArrayList<>();
    private List<AccProposalApDetail> lAccProposalApDetail = new ArrayList<>();
    private List<AccProposalApDetail> selectedAccProposalApDetail = new ArrayList<>();
    private AccProposalAp item;
    private AccProposalApDetail itemDetail;
    private AccApFaktur itemAp;
    private Integer batas;

    private Date awal;
    private Date akhir;
    private Character status;
    
    @Inject
    private Page page;

    @Autowired
    ServiceProposalAp serviceProposalAp;
    
    @PostConstruct
    public void init() {
        initItem();
        batas = 7;
        item = new AccProposalAp();
    }

    public void initItem() {
        Date date = new Date();
        itemAp = new AccApFaktur();
        if(page.getMyPegawai().getId_departemen_new()== 105){
            status='D';
        }else{
            status='S';
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
            String noMax = serviceProposalAp.noMax(Integer.parseInt(bulannya), Integer.parseInt(tahun));
            if (noMax == null) {
                item.setNo_proposal("001/"+ page.getMyInternalPerusahaan().getInisial()+"/PAP/" + bulan + "/" + tahun);
            } else {
                Integer nomor = Integer.parseInt(noMax);
                nomor = nomor + 1;
                noMax = String.format("%03d", nomor);
                item.setNo_proposal(noMax + "/"+ page.getMyInternalPerusahaan().getInisial()+"/PAP/" + bulan + "/" + tahun);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createProposal() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./add.jsf");
    }

    public void onLoad() {
        Date date = new Date();
        item.setTanggal(date);
        item.setStatus('D');
        lAccProposalApDetail = new ArrayList<>();
        this.onLoadListAp();
    }

    public void onLoadList() {
        try {
            lAccProposalAp = serviceProposalAp.onLoadList(awal, akhir, status, page.getMyPegawai().getId_departemen_new());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void searchAp() {
        lAccProposalApDetail = new ArrayList<>();
        this.onLoadListAp();
    }

    public void onLoadEdit() {
        
        try {
            item = serviceProposalAp.onLoad(item.getNo_proposal());
            lAccProposalApDetail = serviceProposalAp.onLoadDetail(item.getNo_proposal());
            selectedAccProposalApDetail = lAccProposalApDetail;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListAp() {
        try {
            lAccApFaktur = serviceProposalAp.selectAllAccApFaktur(batas);
            for (int i = 0; i < lAccApFaktur.size(); i++) {
                Integer k = i + 1;
                AccProposalApDetail itemDetil = new AccProposalApDetail();
                itemDetil.setNo_proposal(item.getNo_proposal());
                itemDetil.setAp_number(lAccApFaktur.get(i).getAp_number());
                itemDetil.setUrut(k);
                itemDetil.setVendor_code(lAccApFaktur.get(i).getVendor_code());
                itemDetil.setDuedate(lAccApFaktur.get(i).getDue_date());
                itemDetil.setPpn(lAccApFaktur.get(i).getPpn());
                itemDetil.setPph(lAccApFaktur.get(i).getPph());
                if(lAccApFaktur.get(i).getVendor_code().equals("010001")){
                    itemDetil.setDpp(lAccApFaktur.get(i).getAmount());
                    itemDetil.setTotal(itemDetil.getDpp() + itemDetil.getPpn() + itemDetil.getPph());
                }else {
                    itemDetil.setDpp(lAccApFaktur.get(i).getAmount() + lAccApFaktur.get(i).getPph());
                    itemDetil.setTotal(itemDetil.getDpp() + itemDetil.getPpn() - itemDetil.getPph() + lAccApFaktur.get(i).getMaterai());
                }
                itemDetil.setSupplier(lAccApFaktur.get(i).getSupplier());
                itemDetil.setNo_invoice(lAccApFaktur.get(i).getReff());
                lAccProposalApDetail.add(itemDetil);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AccProposalAp> getDataAccProposalAp() {
        return lAccProposalAp;
    }

    public List<AccApFaktur> getDataAccApFaktur() {
        return lAccApFaktur;
    }

    public List<AccProposalApDetail> getDataProposalApDetail() {
        return lAccProposalApDetail;
    }

    public void onDeleteClicked(AccProposalApDetail it) {
        lAccProposalApDetail.remove(it);

    }

    public void onStatusClicked(AccProposalApDetail s, Character st, Integer i) {
        s.setSt(st);
        lAccProposalApDetail.set(i, s);
    }

    public void onStatusManyClicked(Character st) {
        for (int i = 0; i < selectedAccProposalApDetail.size(); i++) {
            itemDetail=selectedAccProposalApDetail.get(i);
            itemDetail.setSt(st);
            selectedAccProposalApDetail.set(i, itemDetail);
            
        }
    }
    
    public void onDeleteManyClicked() {
        for (int i = 0; i < selectedAccProposalApDetail.size(); i++) {
            lAccProposalApDetail.remove(selectedAccProposalApDetail.get(i));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            nomorurut();
            item.setStatus('D');
            item.setPrepared(page.getMyPegawai().getId_pegawai());
            serviceProposalAp.tambah(item,selectedAccProposalApDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_proposal());
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_proposal());
    }
    
    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./paymentproposal-edit.jsf?id=" + item.getNo_proposal());
    }

    public void updateDashboard(String id) throws IOException {
        item = new AccProposalAp();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./akuntansi/ap//edit.jsf?id=" + id);
    }
    
    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            item.setStatus(status);
            serviceProposalAp.ubah(item, selectedAccProposalApDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceProposalAp.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void ubahStatus() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            item.setStatus('C');
            serviceProposalAp.updateStatus(item, lAccProposalApDetail);
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
        
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakpaymentproposal.jsf?id=" + item.getNo_proposal());
    }
    
    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceProposalAp.delete(id);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }


}
