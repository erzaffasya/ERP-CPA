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
import net.sra.prime.ultima.entity.PenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudangDetail;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.service.ServicePenerimaanGudang;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.PoAutoComplete;
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

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPenerimaanGudang implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<PenerimaanGudang> lPenerimaan = new ArrayList<>();
    private PenerimaanGudang item;
    private List<PenerimaanGudangDetail> lPenerimaanDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;
    private String id_gudang;
    private String id_barang;
    private String barang;

    @Inject
    private PoAutoComplete poAutoComplete;

    @Inject
    private Page page;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServicePenerimaanGudang servicePenerimaanGudang;

    @PostConstruct
    public void init() {
        status = 'D';
        item = new PenerimaanGudang();

    }

    public void initItem() {
        Date date = new Date();
        if (page.getMyGudang() != null) {
            id_gudang = page.getMyGudang().getId_gudang();
        } else {
            id_gudang = null;
        }
        poAutoComplete.setPo(new Po());
        poAutoComplete.setId_pegawai(page.getMyPegawai().getId_pegawai());

    }

    public void nomorurut() {
        String id_kantor = servicePenerimaanGudang.selectOneGudang(item.getId_gudang()).getId_kantor();
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTgl_penerimaan());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTgl_penerimaan());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTgl_penerimaan())) - 1];
        String noMax = servicePenerimaanGudang.noMax(id_kantor, Integer.parseInt(bulannya), Integer.parseInt(tahun));
        if (noMax == null) {
            item.setNo_penerimaan(id_kantor + "/001/BTB-" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_penerimaan(id_kantor + "/" + noMax + "/BTB-" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        Boolean statusnya = false;
        Boolean st = false;
        if (status.equals('D')) {
            statusnya = false;
            st = true;
        } else if (status.equals('9')) {
            st = false;
            statusnya = null;
        } else if (status.equals('A')) {
            statusnya = true;
            st = true;
        } else if (status.equals('C')) {
            statusnya = null;
            st = true;
        }

        lPenerimaan = servicePenerimaanGudang.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), statusnya, st);
    }

    public List<PenerimaanGudang> getDataPenerimaan() {
        return lPenerimaan;
    }

    public void delete(String no_penerimaan) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (servicePenerimaanGudang.onLoad(no_penerimaan).getStatus()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ..Penerimaan ini sudah  di Approve", ""));
                return;
            }
            servicePenerimaanGudang.delete(no_penerimaan);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
           // e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PenerimaanGudangDetail> getDataPenerimaanDetail() {
        return lPenerimaanDetail;

    }

    public void extend() {
        lPenerimaanDetail.add(new PenerimaanGudangDetail());
    }

    public void onDeleteClicked(PenerimaanGudangDetail itemPenerimaanGudangDetail) {
        lPenerimaanDetail.remove(itemPenerimaanGudangDetail);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penerimaan());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./penerimaan-edit.jsf?id=" + item.getNo_penerimaan());
    }

    public void onLoad() {
        try {
            item = servicePenerimaanGudang.onLoad(item.getNo_penerimaan());
            Po po = new Po();
            po.setNomor_po(item.getNomor_po());
            poAutoComplete.setPo(po);
            lPenerimaanDetail = servicePenerimaanGudang.onLoadDetail(item.getNo_penerimaan());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (servicePenerimaanGudang.cekPo(item.getNomor_po()) > 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "PO Nomor " + item.getNomor_po() + " dalam proses penerimaan barang", ""));
                return;
            }
            for (int i = 0; i < lPenerimaanDetail.size(); i++) {
                if (lPenerimaanDetail.get(i).getQty() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kolom Diterima  harus diisi", ""));
                    return;
                } else if (lPenerimaanDetail.get(i).getQty() > lPenerimaanDetail.get(i).getDiorder()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jumlah Barang yang diterima tidak boleh lebih besar daripada barang yang diorder !!", ""));
                    return;
                }
            }
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            this.nomorurut();
            item.setStatus(Boolean.FALSE);
            servicePenerimaanGudang.tambah(item, lPenerimaanDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_penerimaan());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onPoSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Po tes = (Po) event.getObject();
        Boolean cek = true;
        if (servicePenerimaanGudang.cekPo(tes.getNomor_po()) > 0) {
            cek = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "PO Nomor " + tes.getNomor_po() + " dalam proses penerimaan barang", ""));

        }
        if (cek) {
            lPenerimaanDetail = new ArrayList<>();

            Po selectedPo = servicePenerimaanGudang.selectOnePo(tes.getNomor_po());
            item.setId_supplier(selectedPo.getId_supplier());
            item.setNama_supplier(selectedPo.getNama_supplier());
            item.setId_gudang(selectedPo.getId_gudang());
            item.setNomor_po(selectedPo.getNomor_po());
            item.setTanggal_po(selectedPo.getTanggal());
            List<PoDetail> listPoDetail = new ArrayList<>();
            listPoDetail = servicePenerimaanGudang.selectPoDetail(selectedPo.getId());
            for (int j = 0; j < listPoDetail.size(); j++) {
                if (listPoDetail.get(j).getSisa() > 0) {
                    PenerimaanGudangDetail pd = new PenerimaanGudangDetail();
                    pd.setNo_penerimaan(item.getNo_penerimaan());
                    pd.setUrut(listPoDetail.get(j).getUrut());
                    pd.setId_barang(listPoDetail.get(j).getId_barang());
                    pd.setNama_barang(listPoDetail.get(j).getNama_barang());
                    //pd.setQty(0.00);
                    pd.setSatuan_kecil(listPoDetail.get(j).getSatuan_kecil());
                    pd.setId_satuan_kecil(listPoDetail.get(j).getId_satuan_kecil());
                    pd.setDiorder(listPoDetail.get(j).getSisa());
                    pd.setUrutpo(listPoDetail.get(j).getUrut());
                    lPenerimaanDetail.add(pd);
                }
            }
        }
    }

    public void ubah(Boolean status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            // jika di approve cek apakah penerimaan ini sudah pernah diaaprove
            if (status) {
                if (servicePenerimaanGudang.onLoad(item.getNo_penerimaan()).getStatus()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error ..Penerimaan ini sudah pernah di Approve", ""));
                    return;
                }
            }
            for (int i = 0; i < lPenerimaanDetail.size(); i++) {
                if (lPenerimaanDetail.get(i).getQty() == null) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kolom Diterima  harus diisi", ""));
                    return;
                } else if (lPenerimaanDetail.get(i).getQty() > lPenerimaanDetail.get(i).getDiorder()) {
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jumlah Barang yang diterima tidak boleh lebih besar daripada barang yang diorder !!", ""));
                    return;
                }
            }
            item.setModified_by(page.getMyPegawai().getId_pegawai());
            item.setStatus(status);
            servicePenerimaanGudang.ubah(item, lPenerimaanDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            // e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePenerimaanGudang.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getNo_penerimaan());
    }

    public void onTglPenerimaan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTgl_penerimaan().before(item.getTanggal_po())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Penerimaan tidak boleh sebelum tanggal PO !!!", ""));
            item.setTgl_penerimaan(null);
            return;
        }
        if (item.getTanggal_referensi() != null) {
            if (item.getTgl_penerimaan().before(item.getTanggal_referensi())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Penerimaan tidak boleh sebelum tanggal DN !!!", ""));
                item.setTgl_penerimaan(null);
                return;
            }

        }

        if (item.getTgl_penerimaan().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Penerimaan tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTgl_penerimaan(null);
        } else if (item.getStatus() != null) {
            PenerimaanGudang penerimaan = servicePenerimaanGudang.onLoad(item.getNo_penerimaan());
            if (!bln.format(item.getTgl_penerimaan()).equals(bln.format(penerimaan.getTgl_penerimaan()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Penerimaan tidak boleh beda bulannya !!!", ""));
                item.setTgl_penerimaan(penerimaan.getTgl_penerimaan());
            }
        } else {
            if (!bln.format(item.getTgl_penerimaan()).equals(bln.format(new Date()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal  tidak boleh beda dengan bulan sekarang !!!", ""));
                item.setTgl_penerimaan(null);
            }
        }

    }

    public void onTglDN() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (item.getTgl_penerimaan() != null) {
            if (item.getTgl_penerimaan().before(item.getTanggal_referensi())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Penerimaan tidak boleh sebelum tanggal DN !!!", ""));
                //item.setTgl_penerimaan(null);
                item.setTanggal_referensi(null);
            }
        }
    }

    public void onTglPenerimaanSelect(SelectEvent event) {
        onTglPenerimaan();
    }

    public void onTglPenerimaanChange(AjaxBehaviorEvent event) {
        onTglPenerimaan();
    }

    public void onTglDNSelect(SelectEvent event) {
        onTglDN();
    }

    public void onTglDNChange(AjaxBehaviorEvent event) {
        onTglDN();
    }

    public void nomorurutCancel() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        Date date = new Date();
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(date);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(date);
        String bulan = romanMonths[Integer.parseInt(bln.format(date)) - 1];
        String noMax = servicePenerimaanGudang.noMaxCancel(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_cancel("001/BTB-CANCEL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_cancel(noMax + "/BTB-CANCEL/" + bulan + "/" + tahun);
        }
    }

    public void cancelPenerimaan() {
        FacesContext context = FacesContext.getCurrentInstance();
        nomorurutCancel();
        try {
            item.setStatus(null);
            servicePenerimaanGudang.cancelPenerimaan(item, lPenerimaanDetail, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            item.setStatus(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

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
                    lPenerimaanDetail = servicePenerimaanGudang.onLoadReport(id_barang, awal, akhir, id_gudang, status);
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
                    lPenerimaanDetail = servicePenerimaanGudang.onLoadReport(id_barang, awal, akhir, id_gudang, status);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Laporan Penerimaan Barang ");
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
                            9 //last column  (0-based)
                    ));

                    row = sheet.createRow(1);
                    cell = row.createCell(0);
                    cell.setCellValue("Laporan Penerimaan Barang " + barang + " (" + id_barang + ") Gudang " + servicePenerimaanGudang.namaGudang(id_gudang));
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

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
                    sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));

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
                    cell.setCellValue("Nomor PO");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Supplier");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue("Nomor DN");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(6);
                    cell.setCellValue("Tanggal DN");
                    cell.setCellStyle(cellStyle);

                    cell = row.createCell(7);
                    cell.setCellValue("Qty");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(8);
                    cell.setCellValue("Satuan");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(9);
                    cell.setCellValue("Status");
                    cell.setCellStyle(cellStyle);

                    Integer baris = 5;
                    for (int i = 0; i < lPenerimaanDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lPenerimaanDetail.get(i).getNo_penerimaan());
                        cell = row.createCell(2);
                        cell.setCellValue(lPenerimaanDetail.get(i).getTgl_penerimaan());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lPenerimaanDetail.get(i).getNomor_po());
                        cell = row.createCell(4);
                        cell.setCellValue(lPenerimaanDetail.get(i).getNama_supplier());
                        cell = row.createCell(5);
                        cell.setCellValue(lPenerimaanDetail.get(i).getReferensi());
                        cell = row.createCell(6);
                        cell.setCellValue(lPenerimaanDetail.get(i).getTanggal_referensi());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(7);
                        cell.setCellValue(lPenerimaanDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(8);
                        cell.setCellValue(lPenerimaanDetail.get(i).getSatuan_kecil());

                        cell = row.createCell(9);
                        if (lPenerimaanDetail.get(i).getStatus() == true) {
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
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    sheet.autoSizeColumn(8);
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();
                    externalContext.setResponseContentType("application/vnd.ms-excel");
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Penerimaan Barang.xlsx\"");
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
