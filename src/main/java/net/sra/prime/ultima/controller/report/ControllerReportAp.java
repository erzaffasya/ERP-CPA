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
import net.sra.prime.ultima.entity.AccOsAp;
import net.sra.prime.ultima.service.ServiceReportAp;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerReportAp implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccOsAp> lAccOsAp = new ArrayList<>();
    private AccOsAp item;
    private Integer bulan;
    private Integer tahun;

    @Inject
    private Page page;

    @Autowired
    ServiceReportAp serviceReportAp;

    @PostConstruct
    public void init() {
        item = new AccOsAp();
        bulan = Calendar.getInstance().get(Calendar.MONTH);
        tahun = Calendar.getInstance().get(Calendar.YEAR);
    }

    public void onLoadList() {
        try {
            lAccOsAp = serviceReportAp.onLoadListUmurInvoice();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListOverdue() {
        lAccOsAp = serviceReportAp.onLoadListUmurOverdue();

    }

    public void onLoadListInvoiceSupplier() {
        item.setSupplier(serviceReportAp.namaSupplier(item.getVendor_code()));
        lAccOsAp = serviceReportAp.onLoadListAllInvoice(item.getVendor_code());

    }

    public List<AccOsAp> getDataAccOsAp() {
        return lAccOsAp;
    }

    public void linktoInvoiceSupplier() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", item.getSupplier()));
        context.getExternalContext().redirect("./invoicesupplier.jsf?id=" + item.getVendor_code());
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
    
    private Double total_hutang=0.00;
    private Double total_payment=0.00;
    private Double total_os=0.00;
    private Double total_pph=0.00;
    private Double total1=0.00;
    private Double total2=0.00;
    private Double total3=0.00;
    private Double total4=0.00;
    private Double total5=0.00;
    private Double total6=0.00;
    
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
            lAccOsAp = serviceReportAp.onLoadListCutOff(bulan,tahun,tgl_awal,tgl_akhir);
            for(int i=0; i < lAccOsAp.size();i++){
                total_hutang=total_hutang + lAccOsAp.get(i).getTotal();
                if (lAccOsAp.get(i).getPayment() != null){
                    total_payment=total_payment + lAccOsAp.get(i).getPayment();
                }
                total_os=total_os + lAccOsAp.get(i).getOs();
                if(lAccOsAp.get(i).getPph() != null){
                    total_pph=total_pph + lAccOsAp.get(i).getPph();
                }
               
                if(lAccOsAp.get(i).getDay1() != null){
                    total1=total1 + lAccOsAp.get(i).getDay1();
                }else if(lAccOsAp.get(i).getDay2() != null){
                    total2=total2 + lAccOsAp.get(i).getDay2();
                }else if(lAccOsAp.get(i).getDay3() != null){
                    total3=total3 + lAccOsAp.get(i).getDay3();
                }else if(lAccOsAp.get(i).getDay4() != null){
                    total4=total4 + lAccOsAp.get(i).getDay4();
                }else if(lAccOsAp.get(i).getDay5() != null){
                    total5=total5 + lAccOsAp.get(i).getDay5();
                }else if(lAccOsAp.get(i).getDay6() != null){
                    total6=total6 + lAccOsAp.get(i).getDay6();
                }
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
