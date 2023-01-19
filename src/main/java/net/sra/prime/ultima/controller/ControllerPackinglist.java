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
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.entity.PackinglistDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.So;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.service.ServicePackinglist;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SoAutoComplete;
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

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPackinglist implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Packinglist> lPackinglist = new ArrayList<>();
    private Packinglist item;
    private List<PackinglistDetail> lPackinglistDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character statusIp;
    private String gudang;
    private String id_barang;
    private String barang;

    @Inject
    private SoAutoComplete soAutoComplete;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Autowired
    ServicePackinglist servicePackinglist;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new Packinglist();
        statusIp = 'D';
    }

    public void initItem() {
        Date date = new Date();
        // item.setTanggal(date);
        this.onSoSelect();
        Customer customer = new Customer();
        soAutoComplete.setSo(new So());
        awal = new Date();
        akhir = new Date();
    }

    public void nomorurut() {
        String id_kantor = servicePackinglist.selectOneGudang(item.getId_gudang()).getId_kantor();
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePackinglist.noMax(id_kantor, Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNomor(id_kantor + "/001/PL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(id_kantor + "/" + noMax + "/PL/" + bulan + "/" + tahun);
        }
    }

    public void nomorurutCancel() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePackinglist.noMaxCancel(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNo_cancel("001/PL-CANCEL/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_cancel(noMax + "/PL-CANCEL/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if ((awal != null && akhir != null) || (awal == null && akhir == null)) {
                lPackinglist = servicePackinglist.onLoadList(awal, akhir, page.getMyPegawai().getId_pegawai(), statusIp);
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Salah !!!!", ""));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Packinglist> getDataPackinglist() {
        return lPackinglist;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            Character test = servicePackinglist.onLoad(item.getNomor()).getStatus();
            if (!test.equals('D')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal dihapuas Packinglist ini sudah diApprove ", ""));
                return;
            }
            servicePackinglist.delete(nomor);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<PackinglistDetail> getDataPackinglistDetail() {
        return lPackinglistDetail;

    }

    public void extend() {
        lPackinglistDetail.add(new PackinglistDetail());
    }

    public void onDeleteClicked(PackinglistDetail packinglistDetail) {
        lPackinglistDetail.remove(packinglistDetail);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./pickinglist-edit.jsf?id=" + item.getNomor());
    }

    public void onLoad() {
        try {
            item = servicePackinglist.onLoad(item.getNomor());
            So so = new So();
            so.setNomor(item.getNo_so());
            soAutoComplete.setSo(so);
            lPackinglistDetail = servicePackinglist.onLoadDetail(item.getNomor());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean tes = true;
        Packinglist pl = servicePackinglist.cekSoTerpakai(item.getNo_so());
        if (pl != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Packinglist No : " + pl.getNomor() + " dengan nomor SO yang sama masih dalam status Draft !!!! Silahkan selesaikan terlebih dahulu Packinglist tersebut !!", ""));
            tes = false;
        } else {
            if (lPackinglistDetail.size() > 0) {
                for (int i = 0; i < lPackinglistDetail.size(); i++) {
                    if (lPackinglistDetail.get(i).getSisa() < lPackinglistDetail.get(i).getQty() || lPackinglistDetail.get(i).getQty() <= 0) {
                        tes = false;
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery tidak boleh lebih besar daripada Back Order dan tidak boleh 0", ""));
                        break;
                    } else if (lPackinglistDetail.get(i).getQty() > servicePackinglist.selectSisaSO(item.getNo_so(), lPackinglistDetail.get(i).getId_barang())) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery Pickinglist tidak  boleh lebih besar daripada  QTY SO !!!!", ""));
                        tes = false;
                    }
                }
                if (tes) {
                    try {
                        item.setStatus('D');
                        this.nomorurut();
                        item.setKode_user(page.getMyPegawai().getId_pegawai());
                        servicePackinglist.tambah(item, lPackinglistDetail);
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
                    } catch (Exception e) {
                        e.printStackTrace();
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
                    }
                }
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barang tidak boleh kosong !!!", ""));
            }
        }
    }

    public void onSoSelect() {
        lPackinglistDetail = new ArrayList<>();

        So selectedSo = servicePackinglist.selectOneSo(item.getNo_so());
        item.setId_customer(selectedSo.getId_customer());
        item.setCustomer(selectedSo.getCustomer());
        item.setKepada(selectedSo.getKepada());
        item.setTelpon(selectedSo.getTelpon());
        item.setId_gudang(selectedSo.getId_gudang());
        item.setEmail(selectedSo.getEmail());
        item.setNo_so(selectedSo.getNomor());
        item.setReferensi(selectedSo.getReferensi());
        item.setId_salesman(selectedSo.getId_salesman());
        item.setGudang(selectedSo.getGudang());
        item.setDeliverypoint(selectedSo.getDeliverypoint());
        item.setKeterangan(selectedSo.getKeterangan());
        item.setTanggal_so(selectedSo.getTanggal());
        List<SoDetail> listSoDetail = servicePackinglist.selectSoDetail(item.getNo_so());
        for (int j = 0; j < listSoDetail.size(); j++) {
            if (listSoDetail.get(j).getSisa() != 0) {
                PackinglistDetail pd = new PackinglistDetail();
                pd.setNo_pl(item.getNomor());
                pd.setUrut(listSoDetail.get(j).getUrut());
                pd.setId_barang(listSoDetail.get(j).getId_barang());
                pd.setNama_barang(listSoDetail.get(j).getNama_barang());
                pd.setQty(0.00);
                pd.setDiorder(listSoDetail.get(j).getQty());
                pd.setSisa(listSoDetail.get(j).getSisa());
                pd.setSatuan_kecil(listSoDetail.get(j).getSatuan_kecil());
                pd.setSatuan_besar(listSoDetail.get(j).getSatuan_besar());
                pd.setIsi_satuan(listSoDetail.get(j).getIsi_satuan());
                pd.setTotalsatuan(listSoDetail.get(j).getQty() * listSoDetail.get(j).getIsi_satuan());
                lPackinglistDetail.add(pd);
            }
        }
    }

    public void onBarangSelect(PackinglistDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lPackinglistDetail.set(i, s);

    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (status.equals('S')) {
            Character test = servicePackinglist.onLoad(item.getNomor()).getStatus();
            if (!test.equals('D')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal dihapuas Packinglist ini sudah diApprove ", ""));
                return;
            }
        }
        Character st = item.getStatus();
        if (lPackinglistDetail.size() > 0) {
            if (status.equals('S')) {
                for (int i = 0; i < lPackinglistDetail.size(); i++) {
                    if (lPackinglistDetail.get(i).getQty() <= 0 || lPackinglistDetail.get(i).getQty() == null) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery tidak boleh kosong !!!!", ""));
                        return;
                    }
                    if (lPackinglistDetail.get(i).getQty() > servicePackinglist.selectSisaSO(item.getNo_so(), lPackinglistDetail.get(i).getId_barang())) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery Pickinglist tidak  boleh lebih besar daripada  QTY SO !!!!", ""));
                        return;
                    }
                }
            }

            try {
                item.setStatus(status);
                item.setModified_by(page.getMyPegawai().getId_pegawai());
                servicePackinglist.ubah(item, lPackinglistDetail);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
            } catch (Exception e) {
                item.setStatus(st);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Barang tidak boleh kosong !!!", ""));
        }
    }

    public void maintenance() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            servicePackinglist.maintenance(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getNomor());
    }

    public void cancelPl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        nomorurutCancel();
        try {
            Packinglist pl = servicePackinglist.onLoad(item.getNomor());
            if (pl.getStatus().equals('S')) {
                item.setStatus('C');
                servicePackinglist.cancelPl(item, lPackinglistDetail, page.getMyPegawai().getId_pegawai());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Data ini tidak bisa di cancel"));
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
            }
        } catch (Exception e) {
            e.printStackTrace();
            item.setStatus('S');
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }

    }

    public void periksaDelivery(PackinglistDetail s, Integer i) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (s.getSisa() < s.getQty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Qty Delivery tidak boleh lebih besar daripada Back Order", ""));
            s.setQty(0.00);
        }
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if(item.getTanggal().before(item.getTanggal_so())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl Packinglist tidak boleh sebelum tanggal SO !!!", ""));
            item.setTanggal(null);
            return;
        }

        DateFormat bln = new SimpleDateFormat("MM");
        if (item.getTanggal().after(new Date())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh lebih besar daripada tanggal hari ini !!!", ""));
            item.setTanggal(null);
        } else if (item.getStatus() != null) {

            Packinglist packinglist = servicePackinglist.onLoad(item.getNomor());
            if (!bln.format(item.getTanggal()).equals(bln.format(packinglist.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(packinglist.getTanggal());
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
                    lPackinglistDetail = servicePackinglist.onLoadReport(id_barang, awal, akhir, gudang, statusIp);
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
                    lPackinglistDetail = servicePackinglist.onLoadReport(id_barang, awal, akhir, gudang, statusIp);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet("Laporan Picking List ");
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
                    cell.setCellValue("Laporan Picking List Barang " + barang + " (" + id_barang + ")");
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
                    cell.setCellValue("Nomor SO");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(4);
                    cell.setCellValue("Customer");
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
                    for (int i = 0; i < lPackinglistDetail.size(); i++) {
                        row = sheet.createRow(baris);
                        cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                        cell = row.createCell(1);
                        cell.setCellValue(lPackinglistDetail.get(i).getNomor());
                        cell = row.createCell(2);
                        cell.setCellValue(lPackinglistDetail.get(i).getTanggal());
                        cell.setCellStyle(dateCellStyle);
                        cell = row.createCell(3);
                        cell.setCellValue(lPackinglistDetail.get(i).getNo_so());
                        cell = row.createCell(4);
                        cell.setCellValue(lPackinglistDetail.get(i).getCustomer());
                        cell = row.createCell(5);
                        cell.setCellValue(lPackinglistDetail.get(i).getQty());
                        cell.setCellStyle(style);
                        cell = row.createCell(6);
                        cell.setCellValue(lPackinglistDetail.get(i).getSatuan_besar());
                        cell = row.createCell(7);
                        cell.setCellValue(Jsoup.parse(lPackinglistDetail.get(i).getKeterangan()).text());
                        cell = row.createCell(8);
                        if (lPackinglistDetail.get(i).getStatus().equals('S')) {
                            cell.setCellValue("Send");
                        } else if (lPackinglistDetail.get(i).getStatus().equals('A')) {
                            cell.setCellValue("DO Completed");
                        } else if (lPackinglistDetail.get(i).getStatus().equals('P')) {
                            cell.setCellValue("DO Process");
                        } else if (lPackinglistDetail.get(i).getStatus().equals('C')) {
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
                    externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Pickinglist.xlsx\"");
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
