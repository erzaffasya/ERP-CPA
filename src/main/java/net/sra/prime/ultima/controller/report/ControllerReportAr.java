package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
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
import net.sra.prime.ultima.entity.AccOsAr;
import net.sra.prime.ultima.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.service.ServiceReportAr;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerReportAr implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccOsAr> lAccOsAr = new ArrayList<>();
    private AccOsAr item;
    private Integer bulan;
    private Integer tahun;
    private Date awal;
    
    private String id_dsr;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceReportAr serviceReportAr;

    @PostConstruct
    public void init() {
        item = new AccOsAr();
        bulan = Calendar.getInstance().get(Calendar.MONTH);
        tahun = Calendar.getInstance().get(Calendar.YEAR);
        
        
    }

    public void onLoadList() {
        try {
            lAccOsAr = serviceReportAr.onLoadListUmurInvoice();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void onLoadListbyDate() {
        try {
            lAccOsAr = serviceReportAr.onLoadListUmurInvoiceByDate(awal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListCutOff() throws ParseException {
        String tanggal = Integer.toString(tahun) + "-" + String.format("%02d", bulan) + "-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date tgl_awal = dateFormat.parse(tanggal);
        Calendar c = Calendar.getInstance();
        c.setTime(tgl_awal);
        Integer akhir=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        tanggal= Integer.toString(tahun) + "-" + String.format("%02d", bulan) + "-" + akhir;
        Date tgl_akhir = dateFormat.parse(tanggal);
        try {
            lAccOsAr = serviceReportAr.onLoadListCutOff(bulan,tahun,tgl_awal,tgl_akhir);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }
    
    public void onLoadListCutOffByDate() throws ParseException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if(awal == null){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal harus diisi !!!", ""));
                return;
            }
            lAccOsAr = new ArrayList<>();
            lAccOsAr = serviceReportAr.onLoadListCutOff(null,null,awal,awal);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    public void onLoadListOverdue() {
        lAccOsAr = serviceReportAr.onLoadListUmurOverdue(awal);

    }
    
    public void onLoadListOverdueCurrent() {
        lAccOsAr = serviceReportAr.onLoadListUmurOverdueCurrent();

    }
    
    public void onLoadListUmurOverdueCustomer() {
        lAccOsAr = serviceReportAr.onLoadListUmurOverduemarketing(awal,page.getMyPegawai().getId_pegawai());

    }

    public void onLoadListInvoiceCustomer() {
        if (item == null) {
            Customer customer = new Customer();
            customer.setId_kontak(item.getCustomer_code());
            customer.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            customerAutoComplete.setCustomer(customer);
            item.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            lAccOsAr = serviceReportAr.onLoadListAllInvoice(item.getCustomer_code());
        }
    }

    public void onCustomerSelect() {
        item.setCustomer(serviceReportAr.namaCustomer(customerAutoComplete.getCustomer().getId_kontak()));
        lAccOsAr = serviceReportAr.onLoadListAllInvoice(customerAutoComplete.getCustomer().getId_kontak());
    }

    public void onLoadListOverdueCustomer() {
        if (item == null) {
            Customer customer = new Customer();
            customer.setId_kontak(item.getCustomer_code());
            customer.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            customerAutoComplete.setCustomer(customer);
            item.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            lAccOsAr = serviceReportAr.onLoadListAllOverdue(item.getCustomer_code(), page.getMyPegawai().getId_jabatan(), page.getMyPegawai().getId_pegawai());
        }
    }
    
    public void onLoadAgingAr() {
        if (item == null) {
            Customer customer = new Customer();
            customer.setId_kontak(item.getCustomer_code());
            customer.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            customerAutoComplete.setCustomer(customer);
            item.setCustomer(serviceReportAr.namaCustomer(item.getCustomer_code()));
            lAccOsAr = serviceReportAr.onLoadListAgingAR(item.getCustomer_code(), page.getMyPegawai().getId_jabatan(), page.getMyPegawai().getId_pegawai(), awal);
        }else if(item.getCustomer_code() != null){
            lAccOsAr = serviceReportAr.onLoadListAgingAR(item.getCustomer_code(), page.getMyPegawai().getId_jabatan(), page.getMyPegawai().getId_pegawai(), awal);
        }
    }

    public void onCustomerSelectOverdue() {
        item.setCustomer(serviceReportAr.namaCustomer(customerAutoComplete.getCustomer().getId_kontak()));
        lAccOsAr = serviceReportAr.onLoadListAllOverdue(customerAutoComplete.getCustomer().getId_kontak(), page.getMyPegawai().getId_jabatan(), page.getMyPegawai().getId_pegawai());
    }
    
    public void onCustomerSelectOverdueByDsr() {
        lAccOsAr = serviceReportAr.onLoadListAllOverdueByDsr(id_dsr);
    }
    
    
    
    public void outstandingAr() {
        lAccOsAr = serviceReportAr.outstandingAr(id_dsr);
    }
    
    public void onCustomerAgingAr() {
        item.setCustomer(serviceReportAr.namaCustomer(customerAutoComplete.getCustomer().getId_kontak()));
        item.setCustomer_code(customerAutoComplete.getCustomer().getId_kontak());
    }
    
    public List<AccOsAr> getDataAccOsAr() {
        return lAccOsAr;
    }

    public void linktoInvoiceCustomer() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", item.getCustomer()));
        context.getExternalContext().redirect("./invoicecustomer.jsf?id=" + item.getCustomer_code());
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakinvoicecustomer.jsf?id=" + customerAutoComplete.getCustomer().getId_kontak());
    }

    public void cetakumurinvoice() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakumurinvoice.jsf");
    }

    public void cetakumuroverdue() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakumuroverdue.jsf");
    }

    public Map<String, String> getComboBulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Bulan", "");
            map.put("Januari", "1");
            map.put("Februari", "2");
            map.put("Maret", "3");
            map.put("April", "4");
            map.put("Mei", "5");
            map.put("Juni", "6");
            map.put("Juli", "7");
            map.put("Agustus", "8");
            map.put("September", "9");
            map.put("Oktober", "10");
            map.put("November", "11");
            map.put("Desember", "12");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

}
