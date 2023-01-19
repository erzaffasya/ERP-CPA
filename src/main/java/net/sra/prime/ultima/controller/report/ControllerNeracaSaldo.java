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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.service.ServiceLaporanNeracaSaldo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerNeracaSaldo implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccGlDetail> lAccGlDetails = new ArrayList<>();
    private AccGlDetail item;
    private Integer bulan;
    private String tahun;
    private Integer bawah;
    private Integer atas;
    private Double totaldebet;
    private Double totalcredit;
    private ExcelOptions excelOpt;
    private PDFOptions pdfOpt;
    private String id_kantor;

    @Autowired
    ServiceLaporanNeracaSaldo serviceLaporanNeracaSaldo;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        tahun = thn.format(new Date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        bulan = Integer.parseInt(bln.format(new Date()));
        initItem();
        customizationOptions();
    }

    private void initItem() {
        item = new AccGlDetail();

    }

    public void onLoadListNeracaSaldo() {
        try {
            totalcredit = 0.00;
            totaldebet = 0.00;
            lAccGlDetails = serviceLaporanNeracaSaldo.onLoadListNeracaSaldo(tahun, bulan);
            for (int i = 0; i < lAccGlDetails.size(); i++) {
                if (lAccGlDetails.get(i).getDebit() != null) {
                    totaldebet = totaldebet + lAccGlDetails.get(i).getDebit();
                } else if (lAccGlDetails.get(i).getCredit() != null) {
                    totalcredit = totalcredit + lAccGlDetails.get(i).getCredit();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetails;
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

    public void search() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./neracasaldo.jsf?bulan=" + bulan + "&tahun=" + tahun);
    }

    public void customizationOptions() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F88017");
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");

        pdfOpt = new PDFOptions();
        pdfOpt.setFacetBgColor("#F88017");
        pdfOpt.setFacetFontColor("#0000ff");
        //pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("10");
        float[] columnWidths = new float[]{0.1f, 0.6f, 0.2f, 0.2f};
        pdfOpt.setColumnWidths(columnWidths);

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakneracasaldo.jsf?bulan=" + Integer.toString(bulan) + "&tahun=" + tahun);
    }

    ///////////////////  Neraca ////////////////////
    public void onLoadListNeraca() {
        if (bulan != null && !tahun.equals("")) {
            lAccGlDetails = serviceLaporanNeracaSaldo.onLoadListNeraca(tahun, bulan);
        }
    }
    
    public void onLoadArusKas() {
        if (!tahun.equals("")) {
            lAccGlDetails = serviceLaporanNeracaSaldo.onLoadArusKas(tahun);
        }
    }
    
    

    public void getReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Laporan Penambahan Barang");
            PropertyTemplate pt = new PropertyTemplate();
            CellStyle cellStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontName("Calibri");
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle style = workbook.createCellStyle();
            style.setDataFormat(format.getFormat("#,##0"));
            style.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat formatDate = workbook.createDataFormat();
            dateCellStyle.setDataFormat(formatDate.getFormat("dd-MM-yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    7 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("LAPORAN NERACA");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            DateFormat tgl = new SimpleDateFormat("dd-MM-yyyy"); // Just the year, with 2 digits
            //String tahun = thn.format(date);
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Bulan " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            Double value = 0.00;
            Double activalancar = 0.00;
            Double activatetap = 0.00;
            Double totalactiva = 0.00;
            Double totalhutang = 0.00;
            Double totalmodal = 0.00;
            Double totalpasiva = 0.00;

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10100000, 10200000, 'D');
            activalancar = activalancar + value;
            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Kas & Setara Kas");
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10200000, 10300000, 'D');
            activalancar = activalancar + value;

            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Usaha");
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            Double pll = 0.00;
            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10300000, 10400000, 'D');
            pll = pll + value;
            activalancar = activalancar + value;
            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10700000, 10800000, 'D');
            activalancar = activalancar + value;
            pll = pll + value;

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Lain-Lain");
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(pll);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10401000, 10405000, 'D');
            activalancar = activalancar + value;
            row = sheet.createRow(7);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Karyawan/Direksi");
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10501000, 10502000, 'D');
            activalancar = activalancar + value;
            row = sheet.createRow(8);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Uang Muka Pajak");
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10502000, 10505000, 'D');
            activalancar = activalancar + value;
            row = sheet.createRow(9);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Uang Muka Pembeliaan");
            sheet.addMergedRegion(new CellRangeAddress(9, 9, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10600000, 10603000, 'D');
            activalancar = activalancar + value;
            row = sheet.createRow(10);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Persediaan");
            sheet.addMergedRegion(new CellRangeAddress(10, 10, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            row = sheet.createRow(11);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Aktiva Lancar");
            sheet.addMergedRegion(new CellRangeAddress(11, 11, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(activalancar);
            cell.setCellStyle(style);

            Double asset = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10801000, 10802000, 'D');
            row = sheet.createRow(13);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Aset");
            sheet.addMergedRegion(new CellRangeAddress(13, 13, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(asset);
            cell.setCellStyle(style);

            Double akumulasipenyusutan = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 10803000, 10804000, 'D');
            row = sheet.createRow(14);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Akumulasi Penyusutan");
            sheet.addMergedRegion(new CellRangeAddress(14, 14, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(akumulasipenyusutan);
            cell.setCellStyle(style);

            activatetap = asset + akumulasipenyusutan;
            row = sheet.createRow(15);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Aktiva Tetap");
            sheet.addMergedRegion(new CellRangeAddress(15, 15, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(activatetap);
            cell.setCellStyle(style);

            totalactiva = activatetap + activalancar;
            row = sheet.createRow(17);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Total Aktiva");
            sheet.addMergedRegion(new CellRangeAddress(17, 17, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(totalactiva);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20100000, 20200000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(20);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Usaha");
            sheet.addMergedRegion(new CellRangeAddress(20, 20, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20200000, 20300000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(21);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Biaya ymh dibayar");
            sheet.addMergedRegion(new CellRangeAddress(21, 21, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20300000, 20400000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(22);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Pajak");
            sheet.addMergedRegion(new CellRangeAddress(22, 22, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20403000, 20700000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(23);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Lain-Lain");
            sheet.addMergedRegion(new CellRangeAddress(23, 23, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            //
            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20401000, 20402000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(24);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Pd Direksi");
            sheet.addMergedRegion(new CellRangeAddress(24, 24, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20402000, 20403000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(25);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Dividen");
            sheet.addMergedRegion(new CellRangeAddress(25, 25, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 20701000, 20702000, 'K');
            totalhutang = totalhutang + value;
            row = sheet.createRow(26);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Bank Jangka Panjang");
            sheet.addMergedRegion(new CellRangeAddress(26, 26, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            row = sheet.createRow(27);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Total Hutang");
            sheet.addMergedRegion(new CellRangeAddress(27, 27, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(totalhutang);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 30100000, 30200000, 'K');
            totalmodal = totalmodal + value;
            row = sheet.createRow(30);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Modal Saham");
            sheet.addMergedRegion(new CellRangeAddress(30, 30, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 30201001, 30201002, 'K');
            totalmodal = totalmodal + value;
            row = sheet.createRow(31);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba (Rugi) Tahun Lalu");
            sheet.addMergedRegion(new CellRangeAddress(31, 31, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, 30201002, 30201003, 'K');
            totalmodal = totalmodal + value;
            row = sheet.createRow(32);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba (Rugi) Tahun Lalu");
            sheet.addMergedRegion(new CellRangeAddress(32, 32, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            value = serviceLaporanNeracaSaldo.onLoadLabarugi(tahun, bulan);
            totalmodal = totalmodal + value;
            row = sheet.createRow(33);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba (Rugi) Tahun Berjalan");
            sheet.addMergedRegion(new CellRangeAddress(33, 33, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(value);
            cell.setCellStyle(style);

            row = sheet.createRow(34);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Total Modal");
            sheet.addMergedRegion(new CellRangeAddress(34, 34, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(totalmodal);
            cell.setCellStyle(style);

            totalpasiva = totalhutang + totalmodal;
            row = sheet.createRow(36);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell = row.createCell(2);
            cell.setCellValue("Total Passiva");
            sheet.addMergedRegion(new CellRangeAddress(36, 36, 2, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(totalpasiva);
            cell.setCellStyle(style);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Neraca_" + options.getNamaBulan(bulan) + tahun + ".xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReportNeracaDetail() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Neraca Detail");
            PropertyTemplate pt = new PropertyTemplate();
            CellStyle cellStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontName("Calibri");
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle style = workbook.createCellStyle();
            style.setDataFormat(format.getFormat("#,##0"));
            style.setAlignment(HorizontalAlignment.RIGHT);
            
            CellStyle styleBoldRightNumber = workbook.createCellStyle();
            styleBoldRightNumber.setFont(font);
            styleBoldRightNumber.setDataFormat(format.getFormat("#,##0"));
            styleBoldRightNumber.setAlignment(HorizontalAlignment.RIGHT);
            
            CellStyle styleBoldRight = workbook.createCellStyle();
            styleBoldRight.setFont(font);
            styleBoldRight.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat formatDate = workbook.createDataFormat();
            dateCellStyle.setDataFormat(formatDate.getFormat("dd-MM-yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    7 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("LAPORAN NERACA DETAIL");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            DateFormat tgl = new SimpleDateFormat("dd-MM-yyyy"); // Just the year, with 2 digits
            //String tahun = thn.format(date);
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Bulan " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            Double value = 0.00;
            Double kasdansetarakas = 0.00;
            Double piutangusaha = 0.00;
            Double piutanglain = 0.00;
            Double piutangkaryawan = 0.00;
            Double uangmukapajak = 0.00;
            Double uangmukapembelian = 0.00;
            Double persediaan = 0.00;
            Double aktivatetap = 0.00;
            Double hutangusaha = 0.00;
            Double biayaymh = 0.00;
            Double hutangpajak = 0.00;
            Double hutanglain = 0.00;
            Double hutangdireksi = 0.00;
            Double hutangdeviden = 0.00;
            Double hutangjangkapanjang = 0.00;
            Double hutangjangkapendek = 0.00;
            Double modalsaham = 0.00;
            Integer rw = 4;

            // ambil dari table account ; kas dan setara kas
            List<Account> lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10101000, 10200000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                if (value != null) {
                    if (value > 0) {
                        kasdansetarakas = kasdansetarakas + value;
                        row = sheet.createRow(rw);
                        cell = row.createCell(0);
                        cell = row.createCell(1);
                        cell.setCellValue(lAcc.get(i).getAccount());
                        sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                        cell = row.createCell(4);
                        cell.setCellValue("");
                        cell.setCellStyle(cellStyle);
                        cell = row.createCell(5);
                        cell.setCellValue(value);
                        cell.setCellStyle(style);
                        rw = rw + 1;
                    }
                }
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Kas & Setara Kas");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(kasdansetarakas);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            // ambil dari table account ; Piutang usaha level 3
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10200000, 10300000, 3);
            for (int i = 0; i < lAcc.size(); i++) {
                
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1000, 'D');
                    if (value != null) {
                        if (value > 0) {
                            piutangusaha = piutangusaha + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Usaha");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(piutangusaha);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            
            // ambil dari table account ; Piutang lain level 3
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10300000, 10400000, 3);
            for (int i = 0; i < lAcc.size(); i++) {
                
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1000, 'D');
                    if (value != null) {
                        if (value > 0) {
                            piutanglain = piutanglain + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Lain-lain");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(piutanglain);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            
            // ambil dari table account ; Piutang Direksi level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10401000, 10402000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            piutangkaryawan = piutangkaryawan + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }
            
            
            // ambil dari table account ; Piutang karyawan level 3
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10402000, 10403000, 3);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1000, 'D');
                    if (value != null) {
                        if (value > 0) {
                            piutangkaryawan = piutangkaryawan + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
            }
            
            
            // ambil dari table account ; Piutang PB level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10403000, 10500000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            piutangkaryawan = piutangkaryawan + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }
            
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Piutang Karyawan / Direksi");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(piutangkaryawan);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            
            // ambil dari table account ; Uang muuka pajak level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10501000, 10502000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            uangmukapajak = uangmukapajak + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }
            
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Uang Muka Pajak");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(uangmukapajak);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            
            // ambil dari table account ; Uang muuka pajak level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10502000, 10506000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            uangmukapembelian =uangmukapembelian + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }
            
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Uang Muka Pembelian");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(uangmukapembelian);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            
            // ambil dari table account ; Persediaan level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10601000, 10603000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            persediaan = persediaan + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Persediaan");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(persediaan);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            
            // ambil dari table account ; Aktiva Tetap level 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(10801000, 10804000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'D');
                    if (value != null) {
                        if (value > 0) {
                            aktivatetap = aktivatetap + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Aktiva Tetap");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(aktivatetap);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            // ambil dari table account ; Hutang Usaha 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20100000, 20200000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutangusaha = hutangusaha + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Usaha");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutangusaha);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Biaya YMH dibayar 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20201000, 20300000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            biayaymh = biayaymh + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Biaya ymh Dibayar");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(biayaymh);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Biaya YMH dibayar 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20301000, 20400000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutangpajak = hutangpajak + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Pajak");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutangpajak);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Hutang lain-lain 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20403000, 20700000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutanglain = hutanglain + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Lain-lain");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutanglain);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

             // ambil dari table account ; Hutang ppd direksi 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20401000, 20402000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutangdireksi = hutangdireksi + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang PD Direksi");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutangdireksi);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Hutang Deviden 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20402000, 20403000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutangdeviden = hutangdeviden + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Deviden");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutangdeviden);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Hutang Bank Jangka Panjang 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(20701000, 20702000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            hutangjangkapanjang = hutangjangkapanjang + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Hutang Bank - Jangka Panjang");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hutangjangkapanjang);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
             // ambil dari table account ; Modal Saham 4
            lAcc = serviceLaporanNeracaSaldo.selectAccountLevel(30100000, 30200000, 4);
            for (int i = 0; i < lAcc.size(); i++) {
                    value = serviceLaporanNeracaSaldo.onLoadListSumNeraca(tahun, bulan, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1, 'K');
                    if (value != null) {
                        if (value > 0) {
                            modalsaham = modalsaham + value;
                            row = sheet.createRow(rw);
                            cell = row.createCell(0);
                            cell = row.createCell(1);
                            cell.setCellValue(lAcc.get(i).getAccount());
                            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                            cell = row.createCell(4);
                            cell.setCellValue("");
                            cell.setCellStyle(cellStyle);
                            cell = row.createCell(5);
                            cell.setCellValue(value);
                            cell.setCellStyle(style);
                            rw = rw + 1;
                        }
                    }
                
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Modal Saham");
            cell.setCellStyle(styleBoldRight);
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(modalsaham);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;
            
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Neraca_Detail_" + options.getNamaBulan(bulan) + tahun + ".xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    ///// Biaya Operasional
    public void onLoadListBiayaOperasional() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (bulan != null && !tahun.equals("")) {
            Integer acc_kantor= serviceLaporanNeracaSaldo.selectAccountKantor(id_kantor);
            if(acc_kantor != null){
            lAccGlDetails = serviceLaporanNeracaSaldo.selectBiayaOperasionall(bulan, Integer.parseInt(tahun), acc_kantor);
            }else{
                lAccGlDetails = new ArrayList<>();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kode akun operasional untuk kantor ini belum disetting !!!!", ""));
            }
        }
    }
}
