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
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.Penambahan;
import net.sra.prime.ultima.entity.PenambahanDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.service.ServicePenambahan;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
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
import org.jsoup.Jsoup;
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
public class ControllerPenambahan implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Penambahan> lPenambahan = new ArrayList<>();
    private Penambahan item;
    private List<PenambahanDetail> lPenambahanDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private List<AccGlDetail> lAccGlDetail = new ArrayList<>();
    private Double jumlahDebit;
    private Double jumlahKredit;
    private Double selisih;
    private String gudang;
    private String id_barang;
    private String barang;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Autowired
    ServicePenambahan servicePenambahan;

    @Inject
    private Page page;

    @Inject
    Options options;

    @PostConstruct
    public void init() {
        item = new Penambahan();
    }

    public void initItem() {
        item = new Penambahan();
        lPenambahanDetail = new ArrayList<>();
        lPenambahanDetail.add(new PenambahanDetail());
        status = 'D';
        options.setIdpegawai(page.getMyPegawai().getId_pegawai());

    }

    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());

        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());

        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String noMax = servicePenambahan.noMax(item.getId_gudang(), Integer.parseInt(bulan), Integer.parseInt(year));
        String inisial = servicePenambahan.selectOneGudang(item.getId_gudang()).getInisial();
        if (noMax == null) {
            item.setNomor("001/IN/" + inisial + "/" + bulan + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/IN/" + inisial + "/" + bulan + tahun);
        }
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenambahan = servicePenambahan.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), status);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListJurnal() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPenambahan = servicePenambahan.onLoadListJurnal(awal, akhir, status);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Penambahan> getDataPenambahan() {
        return lPenambahan;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenambahan.delete(id);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PenambahanDetail> getDataPenambahanDetail() {
        return lPenambahanDetail;

    }

    public void extend() {
        lPenambahanDetail.add(new PenambahanDetail());
    }

    public void onDeleteClicked(PenambahanDetail pemakaianDetail) {
        lPenambahanDetail.remove(pemakaianDetail);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./penambahan-edit.jsf?id=" + item.getId());
    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit_jurnal.jsf?id=" + item.getId());
    }

    public void onLoad() {
        options.setIdpegawai(page.getMyPegawai().getId_pegawai());
        try {
            item = servicePenambahan.onLoad(item.getId());
            lPenambahanDetail = servicePenambahan.onLoadDetail(item.getId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadJurnal() {

        try {
            item = servicePenambahan.onLoad(item.getId());
            lPenambahanDetail = servicePenambahan.onLoadDetail(item.getId());
            lAccGlDetail = servicePenambahan.jurnalAwal(item);
            this.hitungSelisih();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus('D');
            this.nomorurut();
            item.setKode_user(page.getMyPegawai().getId_pegawai());
            servicePenambahan.tambah(item, lPenambahanDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onBarangSelect(PenambahanDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lPenambahanDetail.set(i, s);

    }

    public void onBarangReportSelect() {
        id_barang = barangAutoComplete.getBarang().getId_barang();
        barang = barangAutoComplete.getBarang().getNama_barang();
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        Character st = item.getStatus();
        if (status.equals('A')) {
            for (int i = 0; i < lPenambahanDetail.size(); i++) {
                if (lPenambahanDetail.get(i).getQty() == 0 || lPenambahanDetail.get(i).getQty() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery tidak boleh kosong !!!!", ""));
                    benar = false;
                }
            }
        }
        if (benar) {
            try {
                item.setStatus(status);
                servicePenambahan.ubah(item, lPenambahanDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                item.setStatus(st);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePenambahan.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getId());
    }

    public List<AccGlDetail> getDataAccGlDetail() {
        return lAccGlDetail;
    }

    public void onDeleteClickedGl(AccGlDetail hapus) {
        lAccGlDetail.remove(hapus);
        this.hitungSelisih();
    }

    public void extendGl() {
        lAccGlDetail.add(new AccGlDetail());
    }

    public void hitungSelisih() {
        jumlahDebit = 0.00;
        jumlahKredit = 0.00;
        selisih = 0.00;
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (lAccGlDetail.get(i).getDebit() != null) {
                jumlahDebit = jumlahDebit + lAccGlDetail.get(i).getDebit();
            }

            if (lAccGlDetail.get(i).getCredit() != null) {
                jumlahKredit = jumlahKredit + lAccGlDetail.get(i).getCredit();
            }
            selisih = jumlahDebit - jumlahKredit;
        }
    }

    public void posting(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        for (int i = 0; i < lPenambahanDetail.size(); i++) {
            if (lPenambahanDetail.get(i).getHpp() <= 0) {
                benar = false;
            }
        }
        if (lAccGlDetail.size() <= 0) {
            benar = false;
        } else {
            for (int i = 0; i < lAccGlDetail.size(); i++) {
                if (lAccGlDetail.get(i).getId_account() == null || (lAccGlDetail.get(i).getDebit() == null && lAccGlDetail.get(i).getCredit() == null)) {
                    benar = false;
                }
            }
        }

        Double jmlDebit = Math.round(jumlahDebit * 100.0) / 100.0;
        Double jmlKredit = Math.round(jumlahKredit * 100.0) / 100.0;
        if (benar && jmlDebit.equals(jmlKredit)) {
            Character st = item.getStatus();
            try {
                item.setStatus(status);
                servicePenambahan.posting(item, lPenambahanDetail, lAccGlDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diposting"));
            } catch (Exception e) {
                item.setStatus(st);
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jurnal salah atau hpp belum diinput", ""));
        }
    }

    public void onAccountSelect(AccGlDetail s, Integer i) {
        s.setId_account(accountAutoComplete.getAccount().getId_account());
        s.setAccount(accountAutoComplete.getAccount().getAccount());
        lAccGlDetail.set(i, s);

    }

    public void hitungTotal(PenambahanDetail s, Integer i) {
        s.setTotal(s.getQty() * s.getHpp());
        lPenambahanDetail.set(i, s);
    }

    public void nomorurutCancel() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        Date date = new Date();
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(date);
        String bulan = romanMonths[Integer.parseInt(bln.format(date)) - 1];
        String noMax = servicePenambahan.noMaxCancel(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_cancel("001/IN-CANCEL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_cancel(noMax + "/IN-CANCEL/" + bulan + "/" + tahun);
        }
    }

    public void cancelPenambahan() {
        FacesContext context = FacesContext.getCurrentInstance();
        nomorurutCancel();
        try {
            item.setStatus('C');
            servicePenambahan.cancelPenambahan(item, lPenambahanDetail, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            item.setStatus('A');
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void onTglPenambahan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        } else if (item.getStatus() != null) {
            Penambahan penambahan = servicePenambahan.onLoad(item.getId());
            if (!bln.format(item.getTanggal()).equals(bln.format(penambahan.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(penambahan.getTanggal());
            }
        } else {
            if (!bln.format(item.getTanggal()).equals(bln.format(new Date()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal  tidak boleh beda dengan bulan sekarang !!!", ""));
                item.setTanggal(null);
            }
        }

    }

    public void onTglPenambahanSelect(SelectEvent event) {
        onTglPenambahan();
    }

    public void onTglPenambahanChange(AjaxBehaviorEvent event) {
        onTglPenambahan();
    }

    public void onLoadListReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lPenambahanDetail = servicePenambahan.onLoadReport(id_barang, awal, akhir, gudang, status);
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
                    lPenambahanDetail = servicePenambahan.onLoadReport(id_barang, awal, akhir, gudang, status);

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
                    cell.setCellValue("Laporan Penambahan Barang");
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

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
                    sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

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
                    cell.setCellValue("Keterangan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(6);
                    cell.setCellValue("Note");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(7);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lPenambahanDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lPenambahanDetail.get(i).getNomor());
                        cell = row.createCell(2);
                        cell.setCellValue(lPenambahanDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        //cell.setCellValue(lPenambahanDetail.get(i).getKeterangan());
                        cell.setCellValue(Jsoup.parse(lPenambahanDetail.get(i).getKeterangan()).text());
                        cell = row.createCell(4);
                        cell.setCellValue(lPenambahanDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(5);
                        cell.setCellValue(lPenambahanDetail.get(i).getSatuan_kecil());
                        cell = row.createCell(6);
                        cell.setCellValue(lPenambahanDetail.get(i).getNote());
                        cell = row.createCell(7);
                        if (lPenambahanDetail.get(i).getStatus().equals('A')) {
                            cell.setCellValue("Approve");
                        } else if (lPenambahanDetail.get(i).getStatus().equals('P')) {
                            cell.setCellValue("Posting");
                        } else if (lPenambahanDetail.get(i).getStatus().equals('C')) {
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
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Penambahan Barang.xlsx\"");
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
