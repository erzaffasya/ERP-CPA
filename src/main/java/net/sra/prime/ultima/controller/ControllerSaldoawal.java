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
import net.sra.prime.ultima.entity.Saldoawal;
import net.sra.prime.ultima.entity.SaldoawalDetail;
import net.sra.prime.ultima.service.ServiceSaldoAwal;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerSaldoawal implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Saldoawal> lSaldoawal = new ArrayList<>();
    private Saldoawal item;
    private SaldoawalDetail itemdetail;
    private SaldoawalDetail sP;
    private List<SaldoawalDetail> lSaldoawalDetail = new ArrayList<>();
    private String id_kantor;

    
    @Inject
    private BarangAutoComplete barangAutoComplete;

    
    @Inject
    private Page page;

    @Autowired
    private ServiceSaldoAwal serviceSaldoAwal;
    
    @PostConstruct
    public void init() {
        item = new Saldoawal();
        itemdetail = new SaldoawalDetail();
        
    }

    public void initItem() {
        Date date = new Date();
        item.setTanggal(date);
        sP = new SaldoawalDetail();
        lSaldoawalDetail = new ArrayList<>();
        lSaldoawalDetail.add(sP);
        item.setJumlah(0.00);
        
    }

    
    public void onLoadList() {
        try {
            lSaldoawal = serviceSaldoAwal.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Saldoawal> getDataSaldoawal() {
        return lSaldoawal;
    }
    
    public List<SaldoawalDetail> getDataSaldoawalDetail() {
        return lSaldoawalDetail;
    }
    
    
    public void delete(String id_gudang) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSaldoAwal.delete(id_gudang);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    
    public void extend() {
        sP = new SaldoawalDetail();
        lSaldoawalDetail.add(sP);
    }
    
    

    public void onDeleteClicked(SaldoawalDetail item) {
        lSaldoawalDetail.remove(item);
        this.hitungJumlah();
    }
    
    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_gudang());
    }

    public void onLoad() {
        try {
            item = serviceSaldoAwal.onLoad(item.getId_gudang());
            lSaldoawalDetail = serviceSaldoAwal.onLoadDetail(item.getId_gudang());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    

    public void onNama() {
        sP.setNama_barang(sP.getId_barang());
        //sP.setTotal(sP.getHarga() * sP.getQty());
    }

    

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSaldoAwal.tambah(item, lSaldoawalDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    

    public void onBarangSelect(SaldoawalDetail s, Integer i,Character asal) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        s.setIsi_satuan(barangAutoComplete.getBarang().getIsi_satuan());
        lSaldoawalDetail.set(i, s);
        
    }
    
    

    @Transactional(readOnly = false)
    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceSaldoAwal.ubah(item, lSaldoawalDetail, status);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void onQtyBesarChange(SaldoawalDetail s, Integer i){
        s.setQty(s.getQtybesar() * s.getIsi_satuan());
        this.onHppChange(s, i);
        lSaldoawalDetail.set(i, s);
    }
    
    public void onQtyKecilChange(SaldoawalDetail s, Integer i){
        s.setQtybesar(s.getQty() / s.getIsi_satuan());
        this.onHppChange(s, i);
        lSaldoawalDetail.set(i, s);
        this.hitungJumlah();
    }
    
    public void onHppChange(SaldoawalDetail s, Integer i){
        if(s.getQty() == null)
            s.setQty(0.00);
        if(s.getHpp() == null)
            s.setHpp(0.00);
        s.setTotal(s.getQty() * s.getHpp());
        lSaldoawalDetail.set(i, s);
        this.hitungJumlah();
    }

    public void hitungJumlah() {
        Double jml;
        jml = 0.00;
        for (int j = 0; j < lSaldoawalDetail.size(); j++) {
            if (lSaldoawalDetail.get(j).getTotal() != null) {
                jml = jml + lSaldoawalDetail.get(j).getTotal();
            }
        }
        item.setJumlah(jml);

    }

}
