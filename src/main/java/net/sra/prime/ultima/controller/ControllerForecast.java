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
import net.sra.prime.ultima.entity.Forecast;
import net.sra.prime.ultima.entity.ForecastDetail;
import net.sra.prime.ultima.entity.Region;
import net.sra.prime.ultima.service.ServiceForecast;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerForecast implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Forecast> lForecast = new ArrayList<>();
    private Forecast item;
    private List<ForecastDetail> lForecastDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private String id_gudang;
    private String id_barang;
    private String barang;
    private Character status;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceForecast serviceForecast;

    @PostConstruct
    public void init() {
        item = new Forecast();
    }

    public void initItem() {
        item = new Forecast();
        Date date = new Date();
        // item.setTanggal(date);
        lForecastDetail = new ArrayList<>();
        lForecastDetail.add(new ForecastDetail());
        item.setLb1("");
        item.setLb2("");
        item.setLb3("");
        item.setId_region(page.getMyKantor().getId_region());
    }

    public void nomorurut() throws Exception {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = serviceForecast.noMax(page.getMyKantor().getId_region());
        Region region = serviceForecast.GetRegion(page.getMyKantor().getId_region());
        if (noMax == null) {
            item.setNo_forecast("001/" + bulan + tahun + "/FRC/" + region.getId_region());
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_forecast(noMax + "/" + bulan + tahun + "/FRC/" + region.getId_region());
        }

    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
            if (page.getMyPegawai().getId_jabatan() == 13) {
                lForecast = serviceForecast.onLoadList(awal, akhir, null);
            } else {
                lForecast = serviceForecast.onLoadList(awal, akhir, page.getMyKantor().getId_region());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
        }

    }

    public List<Forecast> getDataForecast() {
        return lForecast;
    }

    public List<ForecastDetail> getDataForecastDetail() {
        return lForecastDetail;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceForecast.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lForecastDetail.add(new ForecastDetail());
    }

    public void onDeleteClicked(ForecastDetail item) {
        lForecastDetail.remove(item);
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./forecast-edit.jsf?id=" + item.getId());
    }

    public void onLoad() {
        try {
            item = serviceForecast.onLoad(item.getId());
            this.onTriwulanSelect();
            lForecastDetail = serviceForecast.onLoadDetail(item.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void report() {
        try {

            this.onTriwulanSelect();
            lForecastDetail = serviceForecast.onLoadDetail(item.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            item.setStatus('D');
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            serviceForecast.tambah(item, lForecastDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (status != null) {
                item.setStatus(status);
            }
            serviceForecast.ubah(item, lForecastDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onBarangSelect(ForecastDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        lForecastDetail.set(i, s);

    }

    public void onQtyChange(ForecastDetail s, Integer i) {
        s.setTotal(0.00);
        if (s.getBulan1() != null) {
            s.setTotal(s.getTotal() + s.getBulan1());
        }
        if (s.getBulan2() != null) {
            s.setTotal(s.getTotal() + s.getBulan2());
        }
        if (s.getBulan3() != null) {
            s.setTotal(s.getTotal() + s.getBulan3());
        }
        if (s.getTotal() == 0) {
            s.setTotal(null);
        }
        lForecastDetail.set(i, s);

    }

    public void onTriwulanSelect() {
        switch (item.getTriwulan()) {
            case 1:
                item.setLb1("Januari");
                item.setLb2("Februari");
                item.setLb3("Maret");
                break;
            case 2:
                item.setLb1("April");
                item.setLb2("Mei");
                item.setLb3("Juni");
                break;
            case 3:
                item.setLb1("Juli");
                item.setLb2("Agustus");
                item.setLb3("September");
                break;
            case 4:
                item.setLb1("Oktober");
                item.setLb2("November");
                item.setLb3("Desember");
                break;
            default:
                item.setLb1("");
                item.setLb2("");
                item.setLb3("");
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakforecast.jsf?id=" + item.getId());
    }

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (serviceForecast.countIP(item.getId()) > 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Cancel Forecast Gagal !!! Forecast ini sudah dibuat Intruksi PO nya !!", ""));
        } else {
            try {
                item.setStatus(status);
                serviceForecast.ubahStatus(item, page.getMyPegawai().getId_pegawai());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    /////// Laporan ////////////////
    public void onLoadForecastPo() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (id_gudang != null && !id_gudang.equals("") && awal != null && akhir != null) {
            lForecastDetail = serviceForecast.onLoadForecastToPo(awal, akhir, page.getMyKantor().getId_region(), id_gudang);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan Pilih Periode dan Gudang !!!!", ""));
        }
    }

    public void createXls() throws InterruptedException, IOException {
        onLoad();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Forecast");
        PropertyTemplate pt = new PropertyTemplate();

        sheet.setDisplayGridlines(false);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle endBordercellStyle = workbook.createCellStyle();

        endBordercellStyle.setFont(font);
        endBordercellStyle.setAlignment(HorizontalAlignment.CENTER);
        endBordercellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endBordercellStyle.setBorderTop(BorderStyle.THIN);
        endBordercellStyle.setBorderBottom(BorderStyle.THIN);
        endBordercellStyle.setBorderRight(BorderStyle.THIN);
        endBordercellStyle.setBorderLeft(BorderStyle.THIN);

        XSSFCellStyle leftCellStyle = workbook.createCellStyle();
        leftCellStyle.setBorderBottom(BorderStyle.THIN);
        leftCellStyle.setBorderLeft(BorderStyle.THIN);
        leftCellStyle.setWrapText(true);

        XSSFCellStyle rightCellStyle = workbook.createCellStyle();
        rightCellStyle.setBorderBottom(BorderStyle.THIN);
        rightCellStyle.setBorderRight(BorderStyle.THIN);
        rightCellStyle.setBorderLeft(BorderStyle.THIN);
        rightCellStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        dateCellStyle.setDataFormat(format.getFormat("dd-MM-yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(format.getFormat("#,##0"));
        numberCellStyle.setBorderBottom(BorderStyle.THIN);
        numberCellStyle.setBorderLeft(BorderStyle.THIN);
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFCellStyle leftStyle = workbook.createCellStyle();
        leftStyle.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(font);

        Row row = sheet.createRow(0);
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("FORECAST");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(2);
        sheet.setColumnWidth(1, 256 * 20);

        //cell = row.createCell(0);
        cell = row.createCell(1);
        cell.setCellValue("Nomor Forecast");
        cell = row.createCell(2);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
        cell = row.createCell(3);
        cell.setCellValue(item.getNo_forecast());

        cell = row.createCell(4);
        cell = row.createCell(5);
        cell.setCellValue("Tanggal");
        cell = row.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));
        cell = row.createCell(7);
        cell.setCellValue(item.getTanggal());
        cell.setCellStyle(dateCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 9));

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell = row.createCell(1);
        cell.setCellValue("Gudang");
        cell = row.createCell(2);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
        cell = row.createCell(3);
        cell.setCellValue(item.getGudang());

        cell = row.createCell(4);
        cell = row.createCell(5);
        cell.setCellValue("Triwulan");
        cell = row.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 6));
        cell = row.createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, 9));
        cell.setCellValue(item.getTriwulan());
        cell.setCellStyle(leftStyle);

        //cell.setCellStyle(dateCellStyle);
        row = sheet.createRow(4);
        //cell = row.createCell(0);
        cell = row.createCell(1);
        cell.setCellValue("Keterangan");
        cell = row.createCell(2);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 2));
        cell = row.createCell(3);
        if (item.getKeterangan() != null) {
            cell.setCellValue(item.getKeterangan());
        }
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 3, 9));

        Integer numRow = 7;
        row = sheet.createRow(numRow);
        cell = row.createCell(0);
        cell.setCellValue("No");
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 0, 0));
        cell.setCellStyle(cellStyle);

        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("Nama Barang");
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 1, 2));
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Material Code");
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 3, 3));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Bulan");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow, 4, 6));

        cell = row.createCell(7);
        cell.setCellValue("Total");
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 7, 7));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Open Po");
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 8, 8));
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Satuan");
        cell.setCellStyle(endBordercellStyle);

        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow + 1, 9, 9));

        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        numRow++;

        row = sheet.createRow(numRow);

        cell = row.createCell(0);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue(item.getLb1());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue(item.getLb2());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue(item.getLb3());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellStyle(endBordercellStyle);

        numRow++;

        for (int i = 0; i < lForecastDetail.size(); i++) {
            row = sheet.createRow(numRow);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(lForecastDetail.get(i).getNama_barang());
            cell.setCellStyle(leftCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(numRow, numRow, 1, 2));
            cell = row.createCell(2);
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(lForecastDetail.get(i).getId_barang());
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(4);
            if (lForecastDetail.get(i).getBulan1() != null) {
                cell.setCellValue(lForecastDetail.get(i).getBulan1());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(5);
            if (lForecastDetail.get(i).getBulan2() != null) {
                cell.setCellValue(lForecastDetail.get(i).getBulan2());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(6);
            if (lForecastDetail.get(i).getBulan3() != null) {
                cell.setCellValue(lForecastDetail.get(i).getBulan3());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(7);
            if (lForecastDetail.get(i).getTotal() != null) {
                cell.setCellValue(lForecastDetail.get(i).getTotal());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(8);
            if (lForecastDetail.get(i).getOpenpo() != null) {
                cell.setCellValue(lForecastDetail.get(i).getOpenpo());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(9);
            cell.setCellValue(lForecastDetail.get(i).getSatuan_kecil());
            cell.setCellStyle(rightCellStyle);
            numRow++;
        }
        sheet.autoSizeColumn(0);
        //sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Forecast.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }
    
    public void onBarangReportSelect() {
        id_barang = barangAutoComplete.getBarang().getId_barang();
        barang = barangAutoComplete.getBarang().getNama_barang();
    }
    
    public void onLoadListReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lForecastDetail = serviceForecast.onLoadReport(id_barang, awal, akhir, id_gudang, status);
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda belum memilih barang !! !!!!", ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void getReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lForecastDetail = serviceForecast.onLoadReport(id_barang, awal, akhir, id_gudang, status);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Laporan Forecast ");
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
                    cell.setCellValue(page.getMyInternalPerusahaan().getNama().toUpperCase());
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(
                            0, //first row (0-based)
                            0, //last row  (0-based)
                            0, //first column (0-based)
                            8 //last column  (0-based)
                    ));

                    row = sheet.createRow(1);
                    cell = row.createCell(0);
                    cell.setCellValue("Laporan Forecast Barang " + barang + " (" + id_barang + ") Gudang " + serviceForecast.namaGudang(id_gudang));
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

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
                    sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

                    row = sheet.createRow(4);
                    cell = row.createCell(0);
                    cell.setCellValue("No");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue("Nomor");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(2);
                    cell.setCellValue("Tanggal");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(3);
                    cell.setCellValue("Ltr");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(6);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(7);
                    cell.setCellValue("Dibuat");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(8);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lForecastDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lForecastDetail.get(i).getNo_forecast());
                        cell = row.createCell(2);
                        cell.setCellValue(lForecastDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lForecastDetail.get(i).getTotal());
                        cell.setCellStyle(style);
                        cell = row.createCell(4);
                        cell.setCellValue(lForecastDetail.get(i).getSatuan_kecil());
                        cell = row.createCell(5);
                        cell.setCellValue(lForecastDetail.get(i).getTotalbesar());
                        cell.setCellStyle(style);
                        cell = row.createCell(6);
                        cell.setCellValue(lForecastDetail.get(i).getSatuan_besar());
                        cell = row.createCell(7);
                        cell.setCellValue(lForecastDetail.get(i).getCreate_name());
                        cell = row.createCell(8);
                        if (lForecastDetail.get(i).getStatus().equals('D')) {
                            cell.setCellValue("Draft");
                        } else if (lForecastDetail.get(i).getStatus().equals('S')) {
                            cell.setCellValue("Send");
                        } else if (lForecastDetail.get(i).getStatus().equals('A')) {
                            cell.setCellValue("Approve");    
                        }else{
                            cell.setCellValue("Reject");    
                        }

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
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Forecast.xlsx\"");
                    workbook.write(externalContext.getResponseOutputStream());
                    facesContext.responseComplete();
                } else {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
                }

            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda belum memilih barang !! !!!!", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
