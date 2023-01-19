package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServicePenawaran;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
//import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPenawaranSo implements java.io.Serializable {

    private static final long serialVersionUID = -2;
    private List<Penawaran> lPenawaran = new ArrayList<>();
    private Penawaran item;
    private Customer itemCustomer;
    private List<PenawaranDetail> lPenawaranDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private String id_salesman;
    private Character statuspenawaran;
    private Double ppn;
    private Pegawai pegawai;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServicePenawaran servicePenawaran;

    @PostConstruct
    public void init() {
        item = new Penawaran();
        pegawai = page.getMyPegawai();
    }

    
    public void onLoadListNewCustomer() {
        lPenawaran = servicePenawaran.onLoadListNewCustomerBySalesAdmin(pegawai.getId_pegawai());
    }
    
    

    public List<Penawaran> getDataPenawaran() {
        return lPenawaran;
    }

    
    public List<PenawaranDetail> getDataPenawaranDetail() {
        return lPenawaranDetail;

    }

    public void extend() {
        lPenawaranDetail.add(new PenawaranDetail());
    }

    

    public void createSo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../so/addnewcustomer.jsf?id=" + item.getNomor() + "&revisi=" + item.getRevisi());
    }

    

    
    
}
