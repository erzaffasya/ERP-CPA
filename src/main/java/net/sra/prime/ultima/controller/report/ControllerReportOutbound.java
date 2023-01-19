package net.sra.prime.ultima.controller.report;

import java.io.IOException;
import net.sra.prime.ultima.controller.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Outbound;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.service.ServiceReportOutbound;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerReportOutbound implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Outbound> lOutbound = new ArrayList<>();
    private Outbound item;
    private String id_gudang;
    private Date awal;
    private Date akhir;
    private String id_barang;

    @Inject
    private Page page;
    
    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServiceReportOutbound serviceReportOutbound;

    @Autowired
    private Options options;

    @PostConstruct
    public void init() {
        item = new Outbound();
        if (page.getMyGudang() != null) {
            options.setIdgudang(page.getMyGudang().getId_gudang());
            id_gudang=page.getMyGudang().getId_gudang();
        }
    }

    public void onLoadList() {
        if(id_gudang != null && !id_gudang.equals("") && awal !=null && akhir != null){
            onBarangSelect();
            lOutbound = serviceReportOutbound.onLoadList(awal, akhir, id_gudang,id_barang);
        }
    }
    
    public void onLoadListIn() {
        if(id_gudang != null && !id_gudang.equals("") && awal !=null && akhir != null){
            onBarangSelect();
            lOutbound = serviceReportOutbound.onLoadListIn(awal, akhir, id_gudang,id_barang);
        }
    }

    public List<Outbound> getDataOutbound() {
        return lOutbound;
    }

    public void createXls() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Outbound Data");
        PropertyTemplate pt = new PropertyTemplate();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        dateCellStyle.setDataFormat(format.getFormat("dd-MM-yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(format.getFormat("#,##0"));
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFCellStyle centerCellStyle = workbook.createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        
        XSSFCellStyle addressCellStyle = workbook.createCellStyle();
        addressCellStyle.setWrapText(true);
        
        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("DO No.");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("Material Code");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("Product");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("PO No.");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("Gudang");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("Delivery Address");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("Qty");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("Ltr/Kg");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("Batch Number");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("Transporter");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("Lic Plate");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("Driver Name");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(15);
        cell.setCellValue("Received Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(16);
        cell.setCellValue("Delivery Time (Days)");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(17);
        cell.setCellValue("Lead Time (Days)");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(18);
        cell.setCellValue("Recipient Name");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(19);
        cell.setCellValue("Return Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(20);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(21);
        cell.setCellValue("Day Of Lateness");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(22);
        cell.setCellValue("Remark");
        cell.setCellStyle(cellStyle);
        
        String st="";
        onBarangSelect();
        lOutbound = serviceReportOutbound.onLoadListXls(awal, akhir,id_gudang,id_barang);
        for (int i = 0; i < lOutbound.size(); i++) {
            if(lOutbound.get(i).getStatusDo().equals('R')){
                st="Received";
            }else if(lOutbound.get(i).getStatusDo().equals('S')){
                st="Send";
            }else if(lOutbound.get(i).getStatusDo().equals('I')){
                st="Invoice";
            }else if(lOutbound.get(i).getStatusDo().equals('C')){
                st="Cancel";
            }
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(centerCellStyle);
            
            cell = row.createCell(1);
            cell.setCellValue(lOutbound.get(i).getTanggal());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(lOutbound.get(i).getNo_do());

            cell = row.createCell(3);
            cell.setCellValue(lOutbound.get(i).getId_barang());
            
            cell = row.createCell(4);
            cell.setCellValue(lOutbound.get(i).getNama_barang());
            
            cell = row.createCell(5);
            cell.setCellValue(lOutbound.get(i).getPo());
            
            cell = row.createCell(6);
            cell.setCellValue(lOutbound.get(i).getGudang());
            
            cell = row.createCell(7);
            cell.setCellValue(lOutbound.get(i).getTujuan());
            cell.setCellStyle(addressCellStyle);
            
            cell = row.createCell(8);
            cell.setCellValue(Jsoup.parse(lOutbound.get(i).getAlamat()).text());
            cell.setCellStyle(addressCellStyle);
            
            cell = row.createCell(9);
            cell.setCellValue(lOutbound.get(i).getQty());
            cell.setCellStyle(numberCellStyle);
            
            cell = row.createCell(10);
            cell.setCellValue(lOutbound.get(i).getQty_kecil());
            cell.setCellStyle(numberCellStyle);
            
            cell = row.createCell(11);
            cell.setCellValue(lOutbound.get(i).getPacking_number());
            
            cell = row.createCell(12);
            cell.setCellValue(lOutbound.get(i).getTransporter());
            
            
            cell = row.createCell(13);
            cell.setCellValue(lOutbound.get(i).getLic_plate());
            
            
            cell = row.createCell(14);
            cell.setCellValue(lOutbound.get(i).getDriver());
            
            
            cell = row.createCell(15);
            cell.setCellValue(lOutbound.get(i).getReceived_date());
            cell.setCellStyle(dateCellStyle);
            
            cell = row.createCell(16);
            if(lOutbound.get(i).getDays() != null){
            cell.setCellValue(lOutbound.get(i).getDays());
            }else{
              cell.setCellValue("");  
            }
            cell.setCellStyle(numberCellStyle);
            
            cell = row.createCell(17);
            if(lOutbound.get(i).getLeadtime()!= null){
            cell.setCellValue(lOutbound.get(i).getLeadtime());
            }else{
              cell.setCellValue("");  
            }
            cell.setCellStyle(numberCellStyle);
            
            cell = row.createCell(18);
            cell.setCellValue(lOutbound.get(i).getReceived_name());
            
            
            cell = row.createCell(19);
            cell.setCellValue(lOutbound.get(i).getReceived_date());
            cell.setCellStyle(dateCellStyle);
            
            cell = row.createCell(20);
            cell.setCellValue(st);
            cell = row.createCell(21);
            if(lOutbound.get(i).getLeadtime()!= null && lOutbound.get(i).getDays()!= null){
            cell.setCellValue(lOutbound.get(i).getLeadtime() - lOutbound.get(i).getDays());
            }else{
              cell.setCellValue("");  
            }
            cell.setCellStyle(numberCellStyle);
            cell = row.createCell(22);
            cell.setCellValue("");
            
            
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.setColumnWidth(7, 10000);
        sheet.setColumnWidth(8, 10000);
        //sheet.autoSizeColumn(7);
        //sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.setColumnWidth(11, 10000);
        //sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        sheet.autoSizeColumn(15);
        sheet.autoSizeColumn(16);
        sheet.autoSizeColumn(17);
        sheet.autoSizeColumn(18);
        sheet.autoSizeColumn(19);
        sheet.autoSizeColumn(20);
        sheet.autoSizeColumn(21);
        sheet.autoSizeColumn(22);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Outbound Data.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }
    
    public void onBarangSelect() {
        if(barangAutoComplete.getBarang() != null){
            id_barang = barangAutoComplete.getBarang().getId_barang();
            
        }else{
            id_barang= null;
        }
    }
    
}
