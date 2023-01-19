package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.service.ServiceSalesReport;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.component.export.ExcelOptions;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerSalesReport implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<PenjualanDetail> lPenjualanDetail = new ArrayList<>();
    private PenjualanDetail item;
    private Date awal;
    private Date akhir;
    private String idGudang;
    private String idKantor;
    private String idSales;
    private Double total;
    private Double id_customer;
    
    private ExcelOptions excelOpt;
    
    @Autowired
    ServiceSalesReport serviceSalesReport;

    @Inject
    private Page page;
    
    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @PostConstruct
    public void init() {
        awal = new Date();
        akhir = new Date();
        total=0.00;
        idKantor=page.getMyKantor().getId_kantor_cabang();
        if(page.getMyGudang()!=null){
            idGudang=page.getMyGudang().getId_gudang();
        }else{
            idGudang=null;
        }
        getComboGudang();
    }

    

    public void onLoadList() {
        lPenjualanDetail=serviceSalesReport.OnLoadList(awal, akhir, idKantor, idGudang);
        total=0.00;
        for(int i=0;i < lPenjualanDetail.size();i++){
            total=total+lPenjualanDetail.get(i).getTotal();
        }
    }
    
    
    // Rekap penjuaan All
    public void onLoadListRekap() {
        if (awal == null){
            awal = new Date();
        }
        
        if(akhir == null){
            akhir = new Date();
        }
        lPenjualanDetail=serviceSalesReport.OnLoadListRekapPenjualan(awal, akhir, idSales,0);
        total=0.00;
        for(int i=0;i < lPenjualanDetail.size();i++){
            total=total+lPenjualanDetail.get(i).getTotal();
        }
    }
    
    
    // Rekap penjuaan Marketing
    public void onLoadListRekapDsr() {
        if (awal == null){
            awal = new Date();
        }
        
        if(akhir == null){
            akhir = new Date();
        }
        idSales=page.getMyPegawai().getId_pegawai();
        lPenjualanDetail=serviceSalesReport.OnLoadListRekapPenjualan(awal, akhir, idSales,0);
        total=0.00;
        for(int i=0;i < lPenjualanDetail.size();i++){
            total=total+lPenjualanDetail.get(i).getTotal();
        }
    }
    
    
    public void onLoadListRekapSalesAdmin() {
        
        if (awal == null){
            awal = new Date();
        }
        
        if(akhir == null){
            akhir = new Date();
        }
        lPenjualanDetail=serviceSalesReport.OnLoadListRekapPenjualan(awal, akhir, idSales,page.getMyPegawai().getId_departemen_new());
        total=0.00;
        for(int i=0;i < lPenjualanDetail.size();i++){
            total=total+lPenjualanDetail.get(i).getTotal();
        }
    }
    
    
    

    public void onKantorSelect(){
        getComboGudang();
    }
    
    public void search() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./bukubesar.jsf?awal=" + awal + "&akhir=" + akhir);
    }

   public Map<String, String> getComboGudang() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Gudang> list = serviceSalesReport.onLoadGudang(idKantor);
            map.put("Pilih Gudang", "");
            list.stream().forEach((data) -> {
                map.put(data.getGudang(), data.getId_gudang());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    } 
   
   public List<PenjualanDetail> getDataPenjualanDetail() {
        return lPenjualanDetail;
    }
    
   public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String tgl_awal="";
        String tgl_akhir="";
        if (awal != null){
           tgl_awal=df.format(awal);
        }
        if (akhir != null){
           tgl_akhir=df.format(akhir);
        }
        context.getExternalContext().redirect("./cetaksalesreport.jsf?id_gudang=" +idGudang + "&id_kantor=" + idKantor + "&tgl_awal=" + tgl_awal + "&tgl_akhir=" + tgl_akhir);
    }
   
   public Map<String, String> getComboMarketing() {
        List<Pegawai> list = serviceSalesReport.selectComboMarketing();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Salesman / Marketing", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
   
   public Map<String, String> getComboMarketingDepartemen() {
        List<Pegawai> list = serviceSalesReport.selectComboMarketingDepatemen(page.getMyPegawai().getId_departemen_new(),page.getMyPegawai().getId_kantor_new());
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Salesman / Marketing", "");
            if (!list.isEmpty()) {
                list.stream().forEach((data) -> {
                    map.put(data.getNama(), data.getId_pegawai());
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
   
   public void onCustomerSelect() {
        //item.setCustomer(customerAutoComplete.getCustomer().getCustomer());
        lPenjualanDetail=serviceSalesReport.penjualanBarang(customerAutoComplete.getCustomer().getId_kontak());
    }
   
   public void customizationOptions() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F88017");
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");
        
    }
   
   public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
         
         
        HSSFCellStyle cellStyle = wb.createCellStyle();  
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
         
        for(int i=0; i < header.getPhysicalNumberOfCells();i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
    }
}

