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
import net.sra.prime.ultima.admin.OptionKendaraan;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalOrder;
import net.sra.prime.ultima.entity.InternalOrderDetail;
import net.sra.prime.ultima.entity.InternalTransfer;
import net.sra.prime.ultima.entity.InternalTransferDetail;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServiceInternalTransfer;
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
public class ControllerInternalTransfer implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<InternalTransfer> lInternalTransfer = new ArrayList<>();
    private InternalTransfer item;
    private List<InternalTransferDetail> lInternalTransferDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private String id_gudang;
    private String id_barang;
    private String barang;
    private Pegawai pegawai;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Inject
    private OptionKendaraan optionKendaraan;

    @Autowired
    ServiceInternalTransfer serviceInternalTransfer;

    @PostConstruct
    public void init() {
        item = new InternalTransfer();
        pegawai = page.getMyPegawai();
        status = 'D';
    }

    public void initItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (serviceInternalTransfer.selectOneIO(item.getNomor_io()).getStatus().equals('A')) {

                item.setPengirim(pegawai.getId_pegawai());
                Date date = new Date();
                item.setIstransporter(Boolean.TRUE);
                item.setKantor(pegawai.getKantor());
                item.setNama_pengirim(pegawai.getNama());
                item.setTelpon(pegawai.getHp());
                lInternalTransferDetail = new ArrayList<>();
                this.onIoSelect();
                Gudang gudang = serviceInternalTransfer.selectOneGudang(item.getId_gudang_tujuan());
                Pegawai pg = serviceInternalTransfer.selectOnePegawai(gudang.getId_kontak());
                item.setPenerima(pg.getId_pegawai());
                item.setNama_penerima(pg.getNama());
                item.setTelpon(pg.getHp());

            } else {
                context.getExternalContext().redirect("./listout.jsf");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onIoSelect() {
        try {
            InternalOrder internalOrder = serviceInternalTransfer.selectOneIO(item.getNomor_io());
            item.setId_gudang_asal(internalOrder.getId_gudang_asal());
            item.setGudang_asal(internalOrder.getGudang_asal());
            item.setId_gudang_tujuan(internalOrder.getId_gudang_tujuan());
            item.setGudang_tujuan(internalOrder.getGudang_tujuan());
            List<InternalOrderDetail> lInternalOrderDetail = serviceInternalTransfer.selectIoDetail(item.getNomor_io());
            for (int i = 0; i < lInternalOrderDetail.size(); i++) {
                InternalTransferDetail itd = new InternalTransferDetail();
                itd.setNomor(item.getNomor());
                itd.setId_barang(lInternalOrderDetail.get(i).getId_barang());
                itd.setNama_barang(lInternalOrderDetail.get(i).getNama_barang());
                itd.setQty(lInternalOrderDetail.get(i).getQty());
                itd.setKeterangan(lInternalOrderDetail.get(i).getKeterangan());
                itd.setSatuan_besar(lInternalOrderDetail.get(i).getSatuan_besar());
                lInternalTransferDetail.add(itd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceInternalTransfer.noMax(serviceInternalTransfer.selectOneGudang(item.getId_gudang_asal()).getId_kantor(), Integer.parseInt(bulannya), Integer.parseInt(tahun));
        String inisial = serviceInternalTransfer.selectOneGudang(item.getId_gudang_asal()).getInisial();
        if (noMax == null) {
            item.setNomor("001/IT/" + page.getMyInternalPerusahaan().getInisial() + "-" + inisial + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/IT/" + page.getMyInternalPerusahaan().getInisial() + "-" + inisial + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        lInternalTransfer = serviceInternalTransfer.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), null, status, 'O');
    }

    public void onLoadListOut() {
        FacesContext context = FacesContext.getCurrentInstance();
        if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
            lInternalTransfer = serviceInternalTransfer.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), null, status, 'O');
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
        }
    }

    public void onLoadListIn() {
        if (status != null) {
            if (status.equals('D')) {
                status = 'S';
            }
        }
        FacesContext context = FacesContext.getCurrentInstance();
        if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
            lInternalTransfer = serviceInternalTransfer.onLoadList(awal, akhir, null, page.getMyPegawai().getId_pegawai(), status, 'I');
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
        }
    }

    public List<InternalTransfer> getDataInternalTransfer() {
        return lInternalTransfer;
    }

    public List<InternalTransferDetail> getDataInternalTransferDetail() {
        return lInternalTransferDetail;
    }

    public void delete(String nomor_it) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceInternalTransfer.delete(nomor_it);
            context.getExternalContext().redirect("./listout.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void extend() {
        lInternalTransferDetail.add(new InternalTransferDetail());
    }

    public void onDeleteClicked(InternalTransferDetail internalTransferDetail) {
        lInternalTransferDetail.remove(internalTransferDetail);
        item.setTotal(serviceInternalTransfer.totalLiter(lInternalTransferDetail));
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./internaltransfer-edit.jsf?id=" + item.getNomor());
    }

    public void updateIn() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./terima.jsf?id=" + item.getNomor());

    }

    public void onLoad() {
        item = serviceInternalTransfer.onLoad(item.getNomor());
        lInternalTransferDetail = serviceInternalTransfer.onLoadDetail(item.getNomor());
        item.setTotal(serviceInternalTransfer.totalLiter(lInternalTransferDetail));
        
        if (!item.getIstransporter()) {
            optionKendaraan.setId_jenis(item.getId_jenis());
        }
    }
    
    

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            this.nomorurut();
            item.setStatus('D');
            if (item.getIstransporter() == true) {
                item.setId_kendaraan(null);
            }
            serviceInternalTransfer.tambah(item, lInternalTransferDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onBarangSelect(InternalTransferDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lInternalTransferDetail.set(i, s);

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Character st = item.getStatus();

        if (status.equals('S')) {
            if (item.getJam_kirim() == null || (item.getDriver() == null && item.getIdtransporter() == null) || item.getTanggal_kirim() == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delivery Date, Delivery Time, Driver Harus diisi", ""));
                return;
            }
        } else if (status.equals('R')) {
            if (item.getJam_terima() == null || item.getTanggal_terima() == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Receive Date, Receive Time Harus diisi", ""));
                return;
            } else {
                for (int i = 0; i < lInternalTransferDetail.size(); i++) {
                    if (lInternalTransferDetail.get(i).getTerima() == null) {

                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barang yang diterima harus diisi", ""));
                        return;
                    }
                }
            }
        }
        try {
            item.setStatus(status);
            if (!item.getIstransporter()) {
                item.setId_kendaraan(null);
            }
            serviceInternalTransfer.ubah(item, lInternalTransferDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            item.setStatus(st);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceInternalTransfer.maitenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getNomor());
    }

    public void cancelIT() {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean benar = true;
        // diubah tanggal 31 Oktober 2021
        // internal transfer dan internal transfer detail langsung diambil dari database 
        // alasannya pada saat cancel bisa jadi form sudah terbuka dan ada user lain melakukan penerimaan barang 
        InternalTransfer st = serviceInternalTransfer.onLoad(item.getNomor());
        if (st.getStatus().equals('C') || st.getStatus().equals('X')) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Internal Transfer ini sudah di Cancel !!!", ""));
        }
        if (benar) {
            try {
                nomorurutCancel();
                st.setNo_cancel(item.getNo_cancel());
                st.setPesan(item.getPesan());
                serviceInternalTransfer.cancelIT(st, serviceInternalTransfer.onLoadDetail(st.getNomor()), page.getMyPegawai().getId_pegawai());
                onLoad();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                //item.setStatus('S');
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        }

    }

    public void nomorurutCancel() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        Date date = new Date();
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(date);
        String bulan = romanMonths[Integer.parseInt(bln.format(date)) - 1];
        String noMax = serviceInternalTransfer.noMaxCancel(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_cancel("001/IT-CANCEL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_cancel(noMax + "/IT-CANCEL/" + bulan + "/" + tahun);
        }
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        } else if (item.getStatus() != null) {
            InternalTransfer internalTransfer = serviceInternalTransfer.onLoad(item.getNomor());
            if (!bln.format(item.getTanggal()).equals(bln.format(internalTransfer.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(internalTransfer.getTanggal());
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

    public void onTglTerima() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getTanggal_terima().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Receipt tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal_terima(null);
        } else if (item.getTanggal_terima().before(item.getTanggal())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Receipt tidak boleh lebih kecil daripada tanggal IT !!!", ""));
            item.setTanggal_terima(null);
        }

    }

    public void onTglTerimaSelect(SelectEvent event) {
        onTglTerima();
    }

    public void onTglTerimaChange(AjaxBehaviorEvent event) {
        onTglTerima();
    }

    public void onBarangReportSelect() {
        id_barang = barangAutoComplete.getBarang().getId_barang();
        barang = barangAutoComplete.getBarang().getNama_barang();
    }

    public void onLoadListReportSend() {
        onLoadListReport('S');
    }

    public void onLoadListReportReceipt() {
        onLoadListReport('R');
    }

    public void onLoadListReport(Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lInternalTransferDetail = serviceInternalTransfer.onLoadReport(id_barang, awal, akhir, id_gudang, status, jenis);
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

    public void getReportSend() {
        getReport('S');
    }

    public void getReportReceipt() {
        getReport('R');
    }

    public void getReport(Character jenis) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (id_barang != null) {
                if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                    lInternalTransferDetail = serviceInternalTransfer.onLoadReport(id_barang, awal, akhir, id_gudang, status, jenis);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    String judul = "Laporan Internal Transfer ";
                    if (jenis.equals('R')) {
                        judul = "Laporan Receipt Internal Transfer";
                    }
                    XSSFSheet sheet = workbook.createSheet(judul);
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
                            8 //last column  (0-based)
                    ));

                    row = sheet.createRow(1);
                    cell = row.createCell(0);
                    if (jenis.equals('S')) {
                        cell.setCellValue("Laporan Internal Transfer  " + barang + " (" + id_barang + ") Gudang Asal " + serviceInternalTransfer.namaGudang(id_gudang));
                    } else {
                        cell.setCellValue("Laporan Internal Transfer  " + barang + " (" + id_barang + ") Gudang Tujuan " + serviceInternalTransfer.namaGudang(id_gudang));
                    }
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
                    cell.setCellValue("Nomor IO");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    if (jenis.equals('S')) {
                        cell.setCellValue("Gudang Tujuan");
                    } else {
                        cell.setCellValue("Gudang Asal");
                    }

                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(6);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(7);
                    cell.setCellValue("Keterangan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(8);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lInternalTransferDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lInternalTransferDetail.get(i).getNomor());
                        cell = row.createCell(2);
                        cell.setCellValue(lInternalTransferDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lInternalTransferDetail.get(i).getNomor_io());
                        cell = row.createCell(4);
                        if (jenis.equals('S')) {
                            cell.setCellValue(lInternalTransferDetail.get(i).getGudang_tujuan());
                        } else {
                            cell.setCellValue(lInternalTransferDetail.get(i).getGudang_asal());
                        }

                        cell = row.createCell(5);
                        if (jenis.equals('S')) {
                            cell.setCellValue(lInternalTransferDetail.get(i).getQty());
                        } else {
                            cell.setCellValue(lInternalTransferDetail.get(i).getTerima());
                        }
                        cell.setCellStyle(style);
                        cell = row.createCell(6);
                        cell.setCellValue(lInternalTransferDetail.get(i).getSatuan_besar());
                        cell = row.createCell(7);
                        cell.setCellValue(lInternalTransferDetail.get(i).getKeterangan());
                        cell = row.createCell(8);
                        if (lInternalTransferDetail.get(i).getStatus().equals('S')) {
                            cell.setCellValue("Send");
                        } else if (lInternalTransferDetail.get(i).getStatus().equals('R')) {
                            cell.setCellValue("Receipt");
                        } else if (lInternalTransferDetail.get(i).getStatus().equals('C') || lInternalTransferDetail.get(i).getStatus().equals('X')) {
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
                    sheet.autoSizeColumn(8);
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    if (jenis.equals('R')) {
                        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Receipt Internal Transfer.xlsx\"");
                    } else {
                        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Internal Transfer.xlsx\"");
                    }
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

    public void changeJenis() {
        optionKendaraan.setId_jenis(item.getId_jenis());
    }
}
