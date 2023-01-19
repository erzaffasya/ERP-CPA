package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import net.sra.prime.ultima.entity.Decanting;
import net.sra.prime.ultima.entity.DecantingDetail;
import net.sra.prime.ultima.service.ServiceDecanting;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.BrgAutoComplete;
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
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerDecanting implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Decanting> lDecanting = new ArrayList<>();
    private Decanting item;
    private List<DecantingDetail> lDecantingDetail = new ArrayList<>();
    private List<DecantingDetail> lDecantingDetailTo = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private String id_gudang;
    private String id_barang;
    private String barang;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private BrgAutoComplete brgAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServiceDecanting serviceDecanting;

    @PostConstruct
    public void init() {
        item = new Decanting();
        if (page.getMyPegawai().getId_jabatan_new() == 110) {
            status = 'S';
        } else {
            status = 'D';
        }
    }

    public void initItem() {
        item = new Decanting();
        Date date = new Date();
        // item.setTanggal(date);
        item.setReferensi("");
        if (page.getMyGudang() != null) {
            item.setId_gudang(page.getMyGudang().getId_gudang());
        }
        item.setFlushing(0.00);
        item.setDiajukan(page.getMyPegawai().getId_pegawai());
        lDecantingDetail = new ArrayList<>();
        lDecantingDetailTo = new ArrayList<>();
        lDecantingDetail.add(new DecantingDetail());
        lDecantingDetailTo.add(new DecantingDetail());
        options.setIdpegawai(page.getMyPegawai().getId_pegawai());
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceDecanting.noMax(item.getId_gudang(), Integer.parseInt(bulannya), Integer.parseInt(tahun));
        String inisial = serviceDecanting.selectOneGudang(item.getId_gudang()).getInisial();
        if (noMax == null) {
            item.setNo_decanting("001/SPK-DCNT/" + inisial + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_decanting(noMax + "/SPK-DCNT/" + inisial + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        lDecanting = serviceDecanting.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), status);
    }

    public List<Decanting> getDataDecanting() {
        return lDecanting;
    }

    public List<DecantingDetail> getDataDecantingDetail() {
        return lDecantingDetail;
    }

    public List<DecantingDetail> getDataDecantingDetailTo() {
        return lDecantingDetailTo;
    }

    public void delete(String no_decanting) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceDecanting.delete(no_decanting);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lDecantingDetail.add(new DecantingDetail());
    }

    public void extendTo() {
        lDecantingDetailTo.add(new DecantingDetail());
    }

    public void onDeleteClicked(DecantingDetail item) {
        lDecantingDetail.remove(item);
    }

    public void onDeleteClickedTo(DecantingDetail item) {
        lDecantingDetailTo.remove(item);
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_decanting());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./decanting-edit.jsf?id=" + item.getNo_decanting());
    }

    public void onLoad() {
        options.setIdpegawai(page.getMyPegawai().getId_pegawai());
        item = serviceDecanting.onLoad(item.getNo_decanting());
        lDecantingDetail = serviceDecanting.onLoadDetail(item.getNo_decanting(), 'F');
        lDecantingDetailTo = serviceDecanting.onLoadDetail(item.getNo_decanting(), 'T');

    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (Objects.equals(item.getTotalatas(), item.getTotalbawah())) {
            try {
                this.nomorurut();
                item.setStatus('D');
                serviceDecanting.tambah(item, lDecantingDetail, lDecantingDetailTo);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_decanting());
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hasil Decanting tidak sama !!!!!", ""));
        }
    }

    public void onBarangSelect(DecantingDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        s.setIsi_satuan(barangAutoComplete.getBarang().getIsi_satuan());
        lDecantingDetail.set(i, s);
        if (page.getValueBegawi("decanting").equals('M')) {
            List<String> abarang = new ArrayList<>();
            for (int j = 0; j < lDecantingDetail.size(); j++) {
                abarang.add(lDecantingDetail.get(j).getId_barang());
            }
            brgAutoComplete.setIdbarang(abarang);
        }
    }

    public void onBrgSelect(DecantingDetail s, Integer i) {
        s.setId_barang(brgAutoComplete.getBarang().getId_barang());
        s.setNama_barang(brgAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(brgAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(brgAutoComplete.getBarang().getSatuan_besar());
        s.setIsi_satuan(brgAutoComplete.getBarang().getIsi_satuan());
        lDecantingDetailTo.set(i, s);
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Decanting tmp = serviceDecanting.onLoad(item.getNo_decanting());
        if(status == 'D' && tmp.getStatus() != 'D'){
             item.setStatus(tmp.getStatus());
             return;
        }        
        if (status == 'S' && tmp.getStatus() == 'A') {
            item.setStatus(tmp.getStatus());
            return;
        }
        if (status == 'A' && tmp.getStatus() == 'A') {
            item.setStatus(tmp.getStatus());
            return;
        }
        Character st = item.getStatus();
        item.setStatus(status);
        try {
            serviceDecanting.ubah(item, lDecantingDetail, lDecantingDetailTo);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            item.setStatus(st);
            //e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceDecanting.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void onQtyBesarChange(DecantingDetail s, Integer i, Character asal) {
        if (s.getQtybesar() == null) {
            s.setQtybesar(0.00);
        }
        s.setQty(s.getQtybesar() * s.getIsi_satuan());
        if (asal.equals('F')) {
            lDecantingDetail.set(i, s);
            this.totalatas();
        } else {
            lDecantingDetailTo.set(i, s);
            this.totalbawah();
        }
    }

    public void onQtyKecilChange(DecantingDetail s, Integer i, Character asal) {
        if(s.getQty() == null){
            s.setQty(0.00);
        }
        s.setQtybesar(s.getQty() / s.getIsi_satuan());
        if (asal.equals('F')) {
            lDecantingDetail.set(i, s);
            this.totalatas();
        } else {
            lDecantingDetailTo.set(i, s);
            this.totalbawah();
        }
    }

    public void totalatas() {
        Double totalatas = 0.00;
        for (int i = 0; i < lDecantingDetail.size(); i++) {
            if (lDecantingDetail.get(i).getQty() != null) {
                totalatas = totalatas + lDecantingDetail.get(i).getQty();
            }
        }
        item.setTotalatas(totalatas);
    }

    public void totalbawah() {
        Double totalbawah = 0.00;
        for (int i = 0; i < lDecantingDetailTo.size(); i++) {
            if (lDecantingDetailTo.get(i).getQty() != null) {
                totalbawah = totalbawah + lDecantingDetailTo.get(i).getQty();
            }
        }
        item.setTotalbawah(totalbawah + item.getFlushing());

    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        } else if (item.getStatus() != null) {

            Decanting decanting = serviceDecanting.onLoad(item.getNo_decanting());
            if (!bln.format(item.getTanggal()).equals(bln.format(decanting.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(decanting.getTanggal());
            }
        } else {
            if (!bln.format(item.getTanggal()).equals(bln.format(new Date()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal  tidak boleh beda dengan bulan sekarang !!!", ""));
                item.setTanggal(null);
            }
        }

    }

    public void onTglSelect(SelectEvent event) {
        onTgl();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        onTgl();
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
                    lDecantingDetail = serviceDecanting.onLoadReport(id_barang, awal, akhir, id_gudang, status);
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
                    lDecantingDetail = serviceDecanting.onLoadReport(id_barang, awal, akhir, id_gudang, status);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Laporan Decanting ");
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
                            5 //last column  (0-based)
                    ));

                    row = sheet.createRow(1);
                    cell = row.createCell(0);
                    cell.setCellValue("Laporan Decanting Barang " + barang + " (" + id_barang + ") Gudang " + serviceDecanting.namaGudang(id_gudang));
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

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
                    sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));

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
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lDecantingDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lDecantingDetail.get(i).getNo_decanting());
                        cell = row.createCell(2);
                        cell.setCellValue(lDecantingDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lDecantingDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(4);
                        cell.setCellValue(lDecantingDetail.get(i).getSatuan_kecil());
                        cell = row.createCell(5);
                        if (lDecantingDetail.get(i).getStatus().equals('A')) {
                            cell.setCellValue("Approve");
                        } else {
                            cell.setCellValue("Cancel");
                        }

                        baris++;
                    }
                    sheet.autoSizeColumn(0);
                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Decanting Barang.xlsx\"");
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
