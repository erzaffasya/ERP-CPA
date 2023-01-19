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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.DoDetail;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.service.ServiceDo;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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
public class ControllerDoReport implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<DoDetail> lDoDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character statusdo;
    private String id_gudang;
    private String id_customer;
    private String customer;
    private String id_salesman;
    private String id_barang;
    private String barang;

    @Inject
    private CustomerAutoComplete customerAutoComplete;
    
    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Options options;

    @Inject
    private Page page;

    @Autowired
    ServiceDo serviceDo;

    @PostConstruct
    public void init() {
        awal = new Date();
        akhir = new Date();
    }

    public List<DoDetail> getDataDoDetail() {
        return lDoDetail;

    }

    public void getReport() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Receipt DO");
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

        cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row (0-based)
                0, //last row  (0-based)
                0, //first column (0-based)
                14 //last column  (0-based)
        ));

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Laporan Delivery Order");
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 14));

        DateFormat tgl = new SimpleDateFormat("dd-MM-yyyy"); // Just the year, with 2 digits
        //String tahun = thn.format(date);
        row = sheet.createRow(2);
        cell = row.createCell(0);
        if (awal != null && akhir != null) {
            cell.setCellValue("PERIODE : " + tgl.format(awal) + " s/d " + tgl.format(akhir));
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 14));
        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("No. DO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("PO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("Salesman");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("Gudang");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("Receipt Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("Penerima");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("Material Code");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("Nama Produk");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("Qty");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("Satuan");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("Keterangan");
        cell.setCellStyle(cellStyle);
        String st = "";
        lDoDetail = serviceDo.onLoadListReport(page.getMyPegawai().getId_pegawai(), awal, akhir, statusdo, id_gudang, id_salesman, id_customer,id_barang);
        Integer baris = 5;
        for (int i = 0; i < lDoDetail.size(); i++) {
            row = sheet.createRow(baris);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(centerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(lDoDetail.get(i).getNomor());

            cell = row.createCell(2);
            cell.setCellValue(lDoDetail.get(i).getTanggal());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(lDoDetail.get(i).getCustomer());
            cell.setCellStyle(addressCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(lDoDetail.get(i).getPo());

            cell = row.createCell(5);
            cell.setCellValue(lDoDetail.get(i).getSalesman());

            cell = row.createCell(6);
            cell.setCellValue(lDoDetail.get(i).getGudang());

            cell = row.createCell(7);
            if (lDoDetail.get(i).getStatus().equals('R')) {
                st = "Received";
            } else if (lDoDetail.get(i).getStatus().equals('I')) {
                st = "Invoice";
            } else if (lDoDetail.get(i).getStatus().equals('S')) {
                st = "Send";
            } else if (lDoDetail.get(i).getStatus().equals('C')) {
                st = "Cancel";
            }
            cell.setCellValue(st);

            cell = row.createCell(8);
            cell.setCellValue(lDoDetail.get(i).getReceived_date());
            cell.setCellStyle(dateCellStyle);
            
            cell = row.createCell(9);
            cell.setCellValue(lDoDetail.get(i).getReceived_name());

            cell = row.createCell(10);
            cell.setCellValue(lDoDetail.get(i).getId_barang());

            cell = row.createCell(11);
            cell.setCellValue(lDoDetail.get(i).getNama_barang());

            cell = row.createCell(12);
            cell.setCellValue(lDoDetail.get(i).getQty());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(13);
            cell.setCellValue(lDoDetail.get(i).getSatuan_besar());

            cell = row.createCell(14);
            cell.setCellValue(Jsoup.parse(lDoDetail.get(i).getKeterangan()).text());
            cell.setCellStyle(addressCellStyle);
            
            baris++;

        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Report Do.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    public void onLoadListReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (awal != null && akhir != null) {
                lDoDetail = serviceDo.onLoadListReport(page.getMyPegawai().getId_pegawai(), awal, akhir, statusdo, id_gudang, id_salesman, id_customer,id_barang);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCustomerSelect() {
        id_customer = customerAutoComplete.getCustomer().getId_kontak();
        customer = customerAutoComplete.getCustomer().getCustomer();
    }
    
    public void onBarangReportSelect() {
        id_barang = barangAutoComplete.getBarang().getId_barang();
        barang = barangAutoComplete.getBarang().getNama_barang();
    }

}
