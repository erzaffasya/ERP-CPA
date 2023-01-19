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
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Forecast;
import net.sra.prime.ultima.entity.ForecastDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import net.sra.prime.ultima.service.ServiceForecast;
import net.sra.prime.ultima.service.ServiceIntruksiPo;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
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
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerIntruksiPo implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<IntruksiPo> lIntruksiPo = new ArrayList<>();
    private IntruksiPo item;
    private List<IntruksiPoDetail> lIntruksiPoDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character statusip;
    private Character statusfo;
    private List<Forecast> lForecast = new ArrayList<>();
    private Forecast itemForecast;
    private List<ForecastDetail> lForecastDetail = new ArrayList<>();
    private Integer id_jabatan;
    private String id_pegawai;

    @Autowired
    ServiceForecast serviceForecast;

    @Autowired
    ServiceIntruksiPo serviceIntruksiPo;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        item = new IntruksiPo();
        itemForecast = new Forecast();
        
        id_jabatan=page.getMyPegawai().getId_jabatan_new();
        id_pegawai=page.getMyPegawai().getId_pegawai();
        if (id_jabatan == 110) {
            statusip = 'D';
        } else {
            statusip = 'S';
        }
    }

    public void initItem() throws IOException {
        if (id_jabatan != 110) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("/error/401.html");
        }
        item = new IntruksiPo();
        item.setJenis('M');
        Date date = new Date();
        item.setTanggal(date);
        lIntruksiPoDetail = new ArrayList<>();
        lIntruksiPoDetail.add(new IntruksiPoDetail());

    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = serviceIntruksiPo.noMax(Integer.parseInt(bulan), Integer.parseInt(year));
        if (noMax == null) {
            item.setNo_intruksi_po("001/" + bulan + tahun + "/INT-PO");
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_intruksi_po(noMax + "/" + bulan + tahun + "/INT-PO");
        }
    }

    public void onLoadList() {
        lIntruksiPo = serviceIntruksiPo.onLoadList(awal, akhir, statusip, 'W');
    }

    public void onLoadListAdmin() {
        lIntruksiPo = serviceIntruksiPo.onLoadList(awal, akhir, statusip, 'A');
    }

    public List<IntruksiPo> getDataIntruksiPo() {
        return lIntruksiPo;
    }

    public List<IntruksiPoDetail> getDataIntruksiPoDetail() {
        return lIntruksiPoDetail;
    }

    @Transactional(readOnly = false)
    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Character st=serviceIntruksiPo.onLoad(item.getId()).getStatus();
            if(id_jabatan != 110 && !st.equals('D')){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda tidak boleh menghapus IP ini " , ""));
                return;
            }
            if(id_jabatan == 110 && !st.equals('D') && !st.equals('S')){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda tidak boleh menghapus IP ini " , ""));
                return;
            }
            serviceIntruksiPo.delete(item.getId());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lIntruksiPoDetail.add(new IntruksiPoDetail());
    }

    public void onDeleteClicked(IntruksiPoDetail itemdetail) {
        lIntruksiPoDetail.remove(itemdetail);
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./intruksipo-edit.jsf?id=" + item.getId());
    }

    public void createPo() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../pembelian/po/add.jsf?id=" + item.getId());
    }

    public void onLoad() {
        try {
            item = serviceIntruksiPo.onLoad(item.getId());
            lIntruksiPoDetail = serviceIntruksiPo.onLoadDetail(item.getId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            if (item.getTanggal() != null) {
                item.setId_gudang(itemForecast.getId_gudang());
                item.setNo_forecast(itemForecast.getNo_forecast());
                lIntruksiPoDetail = new ArrayList<>();
                for (int i = 0; i < lForecastDetail.size(); i++) {
                    IntruksiPoDetail ipd = new IntruksiPoDetail();
                    ipd.setId_barang(lForecastDetail.get(i).getId_barang());
                    ipd.setNama_barang(lForecastDetail.get(i).getNama_barang());
                    if (lForecastDetail.get(i).getOpenpo() == null) {
                        ipd.setQty(0.00);
                    } else {
                        ipd.setQty(lForecastDetail.get(i).getOpenpo());
                    }
                    ipd.setSatuan_kecil(lForecastDetail.get(i).getSatuan_kecil());
                    ipd.setUrut(i + 1);
                    lIntruksiPoDetail.add(ipd);
                }
                this.nomorurut();
                item.setCreate_by(id_pegawai);
                item.setStatus('D');
                serviceIntruksiPo.tambah(item, lIntruksiPoDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal harus diisi !!", ""));
            }
        } catch (Exception e) {
             e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void tambahmanual() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            item.setCreate_by(id_pegawai);
            item.setStatus('D');
            serviceIntruksiPo.tambah(item, lIntruksiPoDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubah(Character statusnya) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (statusnya != null) {
                item.setStatus(statusnya);
                item.setModified_by(id_pegawai);
                serviceIntruksiPo.ubah(item, lIntruksiPoDetail);
            } else {
                serviceIntruksiPo.ubahMaintenance(item, lIntruksiPoDetail);
            }

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onBarangSelect(IntruksiPoDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        lIntruksiPoDetail.set(i, s);

    }

    /////////////// Forecast /////////////
    public void onLoadListForecast() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (statusfo == null) {
            statusfo = 'B';
        }
        itemForecast = new Forecast();
        if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
            lForecast = serviceForecast.onLoadListForIP(awal, akhir, statusfo);
            for (int i = 0; i < lForecast.size(); i++) {
                Forecast forecast = lForecast.get(i);
                if (forecast.getStatus().equals('R')) {
                    forecast.setStatusInIp("Forecast Reject");
                } else if (forecast.getId_forecast() != null) {
                    forecast.setStatusInIp("Sudah IP");
                } else {
                    forecast.setStatusInIp("Belum IP");
                }
                lForecast.set(i, forecast);
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
        }

    }

    public void onLoadForecast() {
        item = new IntruksiPo();
        item.setId_forecast(itemForecast.getId());
        itemForecast = serviceForecast.onLoad(itemForecast.getId());
        this.onTriwulanSelect();
        lForecastDetail = serviceForecast.onLoadDetail(itemForecast.getId());
    }

    public List<Forecast> getDataForecast() {
        return lForecast;
    }

    public List<ForecastDetail> getDataForecastDetail() {
        return lForecastDetail;
    }

    public void openPO() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./openpo.jsf?id=" + itemForecast.getId());
    }

    public void onTriwulanSelect() {
        switch (itemForecast.getTriwulan()) {
            case 1:
                itemForecast.setLb1("Januari");
                itemForecast.setLb2("Februari");
                itemForecast.setLb3("Maret");
                break;
            case 2:
                itemForecast.setLb1("April");
                itemForecast.setLb2("Mei");
                itemForecast.setLb3("Juni");
                break;
            case 3:
                itemForecast.setLb1("Juli");
                itemForecast.setLb2("Agustus");
                itemForecast.setLb3("September");
                break;
            case 4:
                itemForecast.setLb1("Oktober");
                itemForecast.setLb2("November");
                itemForecast.setLb3("Desember");
                break;
            default:
                itemForecast.setLb1("");
                itemForecast.setLb2("");
                itemForecast.setLb3("");
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakintruksipo.jsf?id=" + item.getId());
    }

    public void onDeleteClickedForecast(ForecastDetail forecastDetail) {
        lForecastDetail.remove(forecastDetail);
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            IntruksiPo intruksiPo = serviceIntruksiPo.onLoad(item.getId());
            if (!bln.format(item.getTanggal()).equals(bln.format(intruksiPo.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(intruksiPo.getTanggal());
            }
        }
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        }

    }

    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
    }

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            serviceIntruksiPo.ubahStatus(item, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahStatusForecast(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (serviceForecast.countIP(itemForecast.getId()) > 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Reject Forecast Gagal !!! Forecast ini sudah dibuat Intruksi PO nya !!", ""));
        } else {
            try {
                itemForecast.setStatus(status);
                serviceForecast.ubahStatus(itemForecast, page.getMyPegawai().getId_pegawai());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void createXls() throws InterruptedException, IOException {
        itemForecast = serviceForecast.onLoad(item.getId_forecast());
        this.onTriwulanSelect();
        lForecastDetail = serviceForecast.onLoadDetailByOpenPo(item.getId_forecast());
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
        cell.setCellValue(itemForecast.getNo_forecast());

        cell = row.createCell(4);
        cell = row.createCell(5);
        cell.setCellValue("Tanggal");
        cell = row.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));
        cell = row.createCell(7);
        cell.setCellValue(itemForecast.getTanggal());
        cell.setCellStyle(dateCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 9));

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell = row.createCell(1);
        cell.setCellValue("Gudang");
        cell = row.createCell(2);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 2));
        cell = row.createCell(3);
        cell.setCellValue(itemForecast.getGudang());

        cell = row.createCell(4);
        cell = row.createCell(5);
        cell.setCellValue("Triwulan");
        cell = row.createCell(6);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 6));
        cell = row.createCell(7);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, 9));
        cell.setCellValue(itemForecast.getTriwulan());
        cell.setCellStyle(leftStyle);

        //cell.setCellStyle(dateCellStyle);
        row = sheet.createRow(4);
        //cell = row.createCell(0);
        cell = row.createCell(1);
        cell.setCellValue("Keterangan");
        cell = row.createCell(2);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 2));
        cell = row.createCell(3);
        if (itemForecast.getKeterangan() != null) {
            cell.setCellValue(itemForecast.getKeterangan());
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
        cell.setCellValue(itemForecast.getLb1());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue(itemForecast.getLb2());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue(itemForecast.getLb3());
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
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Forecast_" + itemForecast.getNo_forecast() + ".xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    public void createIpXls() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Intruksi Open Po");
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
        cell.setCellValue("INTRUKSI OPEN PO");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        cell.setCellStyle(headerStyle);

        row = sheet.createRow(2);
        sheet.setColumnWidth(1, 256 * 20);
        sheet.setColumnWidth(2, 256 * 20);

        cell = row.createCell(0);
        cell.setCellValue("Nomor Intruksi PO");
        cell = row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
        cell = row.createCell(2);
        cell.setCellValue(item.getNo_intruksi_po());

        cell = row.createCell(3);
        cell = row.createCell(4);
        cell.setCellValue("Tanggal");
        cell = row.createCell(5);
        cell.setCellValue(item.getTanggal());
        cell.setCellStyle(dateCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Nomor Forecast");
        cell = row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
        cell = row.createCell(2);
        cell.setCellValue(item.getNo_forecast());

        cell = row.createCell(3);
        cell = row.createCell(4);
        cell.setCellValue("Gudang");
        cell = row.createCell(5);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 6));
        cell.setCellValue(item.getGudang());
        cell.setCellStyle(leftStyle);

        //cell.setCellStyle(dateCellStyle);
        row = sheet.createRow(4);

        cell = row.createCell(0);
        cell.setCellValue("Keterangan");
        cell = row.createCell(1);
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1));
        cell = row.createCell(2);
        if (item.getKeterangan() != null) {
            cell.setCellValue(item.getKeterangan());
        }
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 2, 6));

        Integer numRow = 7;
        row = sheet.createRow(numRow);
        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);

        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("Nama Barang");
        cell.setCellStyle(cellStyle);
        sheet.addMergedRegion(new CellRangeAddress(numRow, numRow, 1, 2));
        cell = row.createCell(2);
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Material Code");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Proposal Open Po");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Satuan");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Keterangan");
        cell.setCellStyle(endBordercellStyle);

        numRow++;

        for (int i = 0; i < lIntruksiPoDetail.size(); i++) {
            row = sheet.createRow(numRow);

            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(lIntruksiPoDetail.get(i).getNama_barang());
            cell.setCellStyle(leftCellStyle);
            sheet.addMergedRegion(new CellRangeAddress(numRow, numRow, 1, 2));
            cell = row.createCell(2);
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(lIntruksiPoDetail.get(i).getId_barang());
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(4);
            if (lIntruksiPoDetail.get(i).getQty() != null) {
                cell.setCellValue(lIntruksiPoDetail.get(i).getQty());
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(5);
            cell.setCellValue(lIntruksiPoDetail.get(i).getSatuan_kecil());
            cell.setCellStyle(leftCellStyle);

            cell = row.createCell(6);
            cell.setCellValue(lIntruksiPoDetail.get(i).getKet());
            cell.setCellStyle(rightCellStyle);
            numRow++;
        }
        sheet.autoSizeColumn(0);
        //sheet.autoSizeColumn(1);
        //sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"IntruksiPo_" + item.getNo_intruksi_po() + ".xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

}
