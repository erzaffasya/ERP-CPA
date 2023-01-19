package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.entity.HargaJualDetil;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.service.ServiceCustomer;
import net.sra.prime.ultima.service.ServiceHargaJual;
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
public class ControllerHargaJual implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<HargaJual> lHargaJual = new ArrayList<>();
    private HargaJual item;
    private HargaJualDetil itemdetail;
    private HargaJualDetil sP;
    private List<HargaJualDetil> lHargaJualDetil = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServiceHargaJual serviceHargaJual;
    
    @Autowired
    ServiceCustomer serviceCustomer;
    

    @PostConstruct
    public void init() {
        initItem();
    }

    private void initItem() {
        item = new HargaJual();
        itemdetail = new HargaJualDetil();
        sP = new HargaJualDetil();
        lHargaJualDetil.add(sP);
        Customer customer = new Customer();
        customerAutoComplete.setCustomer(customer);

    }

    public void onLoadList() {
        try {
            lHargaJual = serviceHargaJual.onLoadList();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<HargaJual> getDataHargaJual() {
        return lHargaJual;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceHargaJual.delete(nomor);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<HargaJualDetil> getDataHargaJualDetil() {
        return lHargaJualDetil;

    }

    public void extend() {
        sP = new HargaJualDetil();
        lHargaJualDetil.add(sP);
    }

    public void onDeleteClicked(HargaJualDetil hapus) {
        lHargaJualDetil.remove(hapus);
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_kontrak());
    }

    public void onLoad() {
        try {
            String id = item.getId_lama();
            item = serviceHargaJual.onLoad(item.getId_lama());
            item.setId_lama(id);
            Customer customer = new Customer();
            customer.setId_kontak(item.getId_customer());
            customer.setCustomer(item.getCustomer());
            customerAutoComplete.setCustomer(customer);
            lHargaJualDetil = serviceHargaJual.selectAllDetil(id);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        HargaJualDetil entity = context.getApplication().evaluateExpressionGet(context, "#{item}", HargaJualDetil.class);
        entity.setNama_barang(entity.getId_barang());
        // ...
    }

    public void onNama() {
        sP.setNama_barang(sP.getId_barang());
        //sP.setTotal(sP.getHarga() * sP.getQty());
    }

    public void choose(String list) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("modal", true);
        //RequestContext.getCurrentInstance().openDialog(list, options, null);
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
            serviceHargaJual.tambah(item, lHargaJualDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_kontrak());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    

    public void onCustomerSelect(SelectEvent event) {
        Customer selectedCustomer = new Customer();
        Customer tes = (Customer) event.getObject();
        //selectedCustomer = mreferensi.selectOne(sac.getCustomer().getId());
        selectedCustomer = serviceCustomer.selectOneCustomer(tes.getId_kontak());
        

    }

    public void onBarangSelect(HargaJualDetil s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        //s.setId_satuan_kecil(barangAutoComplete.getBarang().getId_satuan_kecil());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lHargaJualDetil.set(i, s);

    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceHargaJual.ubah(item, lHargaJualDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
  
    }

}
