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
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HargaBeliDetil;
import net.sra.prime.ultima.db.mapper.MapperHargaBeli;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.service.ServiceHargaBeli;
import net.sra.prime.ultima.service.ServiceSupplier;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
//import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SupplierAutoComplete;
import org.apache.ibatis.session.SqlSession;
import org.primefaces.event.SelectEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerHargaBeli implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<HargaBeli> lHargaBeli = new ArrayList<>();
    private HargaBeli item;
    private HargaBeliDetil itemdetail;
    private HargaBeliDetil sP;
    private List<HargaBeliDetil> lHargaBeliDetil = new ArrayList<>();
    private List<Supplier> lSupplier = new ArrayList<>();

    @Inject
    private SupplierAutoComplete supplierAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Autowired
    ServiceHargaBeli serviceHargaBeli;
    
    @Autowired
    ServiceSupplier serviceSupplier;

    @PostConstruct
    public void init() {
        item = new HargaBeli();
        itemdetail = new HargaBeliDetil();
    }

    public void initItem() {

        item.setAktif(Boolean.TRUE);
        sP = new HargaBeliDetil();
        lHargaBeliDetil.add(sP);
        customerAutoComplete.setCustomer(null);
        supplierAutoComplete.setSupplier(null);

    }

    public void onLoadList() {
        try {
            lHargaBeli = serviceHargaBeli.onLoadList();
     } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<HargaBeli> getDataHargaBeli() {
        return lHargaBeli;
    }

    @Transactional(readOnly = false)
    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            serviceHargaBeli.delete(nomor);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<HargaBeliDetil> getDataHargaBeliDetil() {
        return lHargaBeliDetil;

    }

    public void extend() {
        sP = new HargaBeliDetil();
        lHargaBeliDetil.add(sP);
    }

    public void onDeleteClicked(HargaBeliDetil hapus) {
        lHargaBeliDetil.remove(hapus);
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_kontrak());
    }

    public void onLoad() {
        try {
            String id = item.getId_lama();
            item = serviceHargaBeli.onLoad(item.getId_lama());
            item.setId_lama(id);
            Supplier supplier = new Supplier();
            supplier.setId(item.getId_supplier());
            supplier.setSupplier(item.getSupplier());
            supplierAutoComplete.setSupplier(supplier);
            Customer customer = new Customer();
            customer.setId_kontak(item.getId_customer());
            customer.setCustomer(item.getCustomer());
            customerAutoComplete.setCustomer(customer);
            lHargaBeliDetil = serviceHargaBeli.selectAllDetil(id);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        HargaBeliDetil entity = context.getApplication().evaluateExpressionGet(context, "#{item}", HargaBeliDetil.class);
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

    @Transactional(readOnly = false)
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setId_supplier(supplierAutoComplete.getSupplier().getId());
            if (customerAutoComplete.getCustomer() != null) {
                item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
            }
            serviceHargaBeli.tambah(item, lHargaBeliDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_kontrak());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void tambahdetail(SqlSession sess) throws Exception{
        MapperHargaBeli mreferensi = sess.getMapper(MapperHargaBeli.class);
            for (int i = 0; i < lHargaBeliDetil.size(); i++) {
                Integer k = i + 1;
                itemdetail.setNo_kontrak(item.getNo_kontrak());
                itemdetail.setUrut(k);
                itemdetail.setId_barang(lHargaBeliDetil.get(i).getId_barang());
                itemdetail.setHarga(lHargaBeliDetil.get(i).getHarga());
                itemdetail.setTarget(lHargaBeliDetil.get(i).getTarget());
                mreferensi.insertDetil(itemdetail);

            }
        
    }

    public void onSupplierSelect(SelectEvent event) {
        Supplier selectedSupplier = new Supplier();
        Supplier tes = (Supplier) event.getObject();
        //selectedSupplier = mreferensi.selectOne(sac.getSupplier().getId());
        selectedSupplier = serviceSupplier.onLoad(tes.getId());

    }

    public void onBarangSelect(HargaBeliDetil s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lHargaBeliDetil.set(i, s);

    }

    @Transactional(readOnly = false)
    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item.setId_supplier(supplierAutoComplete.getSupplier().getId());
        if (customerAutoComplete.getCustomer() != null) {
            item.setId_customer(customerAutoComplete.getCustomer().getId_kontak());
        }
        try {
            serviceHargaBeli.ubah(item, lHargaBeliDetil);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakhargabeli.jsf?id=" + item.getNo_kontrak());
    }

}
