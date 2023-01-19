package net.sra.prime.ultima.controller;

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
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Dotbl;
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.PackinglistAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.primefaces.event.SelectEvent;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.PackinglistDetail;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServiceDo;
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
public class ControllerDo implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Dotbl> lDo = new ArrayList<>();
    private Dotbl item;
    private List<DoDetail> lDoDetail = new ArrayList<>();
    private List<Customer> lCustomer = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character statusdo;
    private String id_gudang, id_pegawai, id_kantor;
    private Integer id_jabatan;

    @Inject
    private PackinglistAutoComplete packinglistAutoComplete;

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
        item = new Dotbl();
        Pegawai pegawai = page.getMyPegawai();
        id_pegawai = pegawai.getId_pegawai();
        id_jabatan = pegawai.getId_jabatan_new();
        id_kantor = pegawai.getId_kantor_new();
        if (id_jabatan == 22) {
            statusdo = 'S';
        } else {
            statusdo = 'D';
        }
    }

    public void initItem() {
        Date date = new Date();
        // item.setTanggal(date);
        Customer customer = new Customer();
        packinglistAutoComplete.setPackinglist(new Packinglist());
        item.setIstransporter(Boolean.TRUE);
        //item.setIstransporter(Boolean.FALSE);
    }

    public void initRekapPengiriman() {
        statusdo = null;
    }

    public void nomorurut() {

        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceDo.noMax(Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNomor("001/DO/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(noMax + "/DO/" + page.getMyInternalPerusahaan().getInisial() + "/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        try {
            lDo = serviceDo.onLoadList(id_pegawai, null, awal, akhir, statusdo);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListMaintenance() {
        try {
            lDo = serviceDo.onLoadListMaintenace(awal, akhir, statusdo);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListReceiveDO() {
        try {
            lDo = serviceDo.onLoadList(id_pegawai, 'T', awal, akhir, statusdo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListOsDo() {
        try {
            if (akhir == null) {
                akhir = new Date();
            }
            lDoDetail = serviceDo.onLoadListOsDo(akhir, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadListOsDoCabang() {
        try {
            if (akhir == null) {
                akhir = new Date();
            }
            lDoDetail = serviceDo.onLoadListOsDo(akhir, id_kantor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Dotbl> getDataDo() {
        return lDo;
    }

    public void delete(String nomor) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Character test = serviceDo.onLoad(item.getNomor()).getStatus();
            if (!test.equals('D')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal dihapuas DO ini diApprove ", ""));
                return;
            }
            serviceDo.delete(nomor, item.getNo_pl());
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<DoDetail> getDataDoDetail() {
        return lDoDetail;

    }

    public void extend() {
        lDoDetail.add(new DoDetail());
    }

    public void onDeleteClicked(DoDetail item) {
        lDoDetail.remove(item);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan pilih data yang akan diedit !!!!", ""));
        }
    }

    public void updateMaintenance() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./do-edit.jsf?id=" + item.getNomor());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan pilih data yang akan diedit !!!!", ""));
        }
    }

    public void terimado() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            if (item.getStatus().equals('C')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Do sudah di Cancel !!!!", ""));
            } else {
                context.getExternalContext().redirect("./terimado.jsf?id=" + item.getNomor());
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan pilih data yang akan diedit !!!!", ""));
        }

    }

    public void onLoad() {
        try {
            item = serviceDo.onLoad(item.getNomor());
            Packinglist packinglist = new Packinglist();
            packinglist.setNomor(item.getNo_pl());
            packinglistAutoComplete.setPackinglist(packinglist);
            lDoDetail = serviceDo.onLoadDetail(item.getNomor());
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
            item.setId_pegawai_log(id_pegawai);

            serviceDo.tambah(item, lDoDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getNomor());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onPackinglistSelect(SelectEvent event) {

        lDoDetail = new ArrayList<>();
        Packinglist selectedPackinglist = new Packinglist();
        Packinglist tes = (Packinglist) event.getObject();
        selectedPackinglist = serviceDo.selectOnePackinglist(tes.getNomor());
        item.setId_customer(selectedPackinglist.getId_customer());
        item.setCustomer(selectedPackinglist.getCustomer());
        item.setKepada(selectedPackinglist.getKepada());
        item.setTelpon(selectedPackinglist.getTlp());
        item.setId_gudang(selectedPackinglist.getId_gudang());
        item.setGudang(selectedPackinglist.getGudang());
        item.setEmail(selectedPackinglist.getEmail());
        item.setNo_pl(selectedPackinglist.getNomor());
        item.setReferensi(selectedPackinglist.getReferensi());
        item.setId_salesman(selectedPackinglist.getId_salesman());
        item.setAlamat(selectedPackinglist.getDeliverypoint());
        item.setKeterangan(selectedPackinglist.getKeterangan());
        item.setTgl_pl(selectedPackinglist.getTanggal());
        options.setId_customer(selectedPackinglist.getId_customer());
        if (selectedPackinglist.getAlamatso() != null) {
            item.setAlamat(selectedPackinglist.getAlamatso());
        } else {
            CustomerPengiriman customerPengiriman = serviceDo.selectDefaultpengiriman(selectedPackinglist.getId_customer());
            if (customerPengiriman != null) {
                item.setAlamat(customerPengiriman.getAlamat());
            }
        }

        List<PackinglistDetail> listDetail = serviceDo.selectOnePackinglistDetail(item.getNo_pl());
        for (int j = 0; j < listDetail.size(); j++) {
            DoDetail pd = new DoDetail();
            pd.setNo_do(item.getNomor());
            pd.setUrut(listDetail.get(j).getUrut());
            pd.setId_barang(listDetail.get(j).getId_barang());
            pd.setNama_barang(listDetail.get(j).getNama_barang());
            pd.setQty(listDetail.get(j).getQty());
            pd.setSatuan_kecil(listDetail.get(j).getSatuan_kecil());
            pd.setSatuan_besar(listDetail.get(j).getSatuan_besar());
            pd.setIsi_satuan(listDetail.get(j).getIsi_satuan());
            pd.setSegel(listDetail.get(j).getSegel());
            pd.setKemasan(listDetail.get(j).getKemasan());
            lDoDetail.add(pd);
        }
        onTgl();
    }

    public void onBarangSelect(DoDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
        lDoDetail.set(i, s);

    }

    public void onPengirimanSelect() {
        CustomerPengiriman customerPengiriman = serviceDo.selectOnePengiriman(item.getId_customer(), item.getId_pengiriman());
        item.setAlamat(customerPengiriman.getAlamat());
    }

    public void ubah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (status != null) {
                item.setStatus(status);
                serviceDo.ubah(item, lDoDetail);
            } else {
                serviceDo.ubahMaintenance(item, lDoDetail);
            }

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahStatus(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            serviceDo.ubahStatus(item, id_pegawai);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void simpanterima(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            item.setReceived_posting(id_pegawai);
            serviceDo.simpanterima(item, lDoDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakdo.jsf?id=" + item.getNomor());
    }

    public void onTgl() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if(item.getTanggal() == null){
            return;
        }
        if (item.getTanggal().before(item.getTgl_pl())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tgl DO tidak boleh sebelum tanggal Packing List !!!", ""));
            item.setTanggal(null);
            return;
        }
        if (item.getStatus() != null) {
            DateFormat bln = new SimpleDateFormat("MM");
            Dotbl dotbl = serviceDo.onLoad(item.getNomor());
            if (!bln.format(item.getTanggal()).equals(bln.format(dotbl.getTanggal()))) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak boleh beda bulannya !!!", ""));
                item.setTanggal(dotbl.getTanggal());
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

    public void createXls() throws InterruptedException, IOException {
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
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("No. DO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("Return Date DO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("PO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("Pickinglist");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("Delivery Address");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("Gudang");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("Material Code");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("Nama Produk");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("Qty");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("Satuan");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("Volume");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("Salesman");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(15);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(16);
        cell.setCellValue("Tgl Terima Barang");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(17);
        cell.setCellValue("Penerima");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(18);
        cell.setCellValue("Diposting Oleh");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(19);
        cell.setCellValue("Tanggal Posting");
        cell.setCellStyle(cellStyle);

        String st = "";
        lDoDetail = serviceDo.onLoadListXls(id_pegawai, awal, akhir, statusdo, id_gudang);
        for (int i = 0; i < lDoDetail.size(); i++) {
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(centerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(lDoDetail.get(i).getNomor());

            cell = row.createCell(2);
            cell.setCellValue(lDoDetail.get(i).getTanggal());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(lDoDetail.get(i).getReturn_date());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(lDoDetail.get(i).getPo());

            cell = row.createCell(5);
            cell.setCellValue(lDoDetail.get(i).getNo_pl());

            cell = row.createCell(6);
            cell.setCellValue(lDoDetail.get(i).getCustomer());
            cell.setCellStyle(addressCellStyle);

            cell = row.createCell(7);
            cell.setCellValue(Jsoup.parse(lDoDetail.get(i).getAlamat()).text());
            cell.setCellStyle(addressCellStyle);

            cell = row.createCell(8);
            cell.setCellValue(lDoDetail.get(i).getGudang());

            cell = row.createCell(9);
            cell.setCellValue(lDoDetail.get(i).getId_barang());

            cell = row.createCell(10);
            cell.setCellValue(lDoDetail.get(i).getNama_barang());

            cell = row.createCell(11);
            cell.setCellValue(lDoDetail.get(i).getQty());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(12);
            cell.setCellValue(lDoDetail.get(i).getSatuan_besar());

            cell = row.createCell(13);
            cell.setCellValue(lDoDetail.get(i).getIsi_satuan());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(14);
            cell.setCellValue(lDoDetail.get(i).getSalesman());

            cell = row.createCell(15);
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

            cell = row.createCell(16);
            cell.setCellValue(lDoDetail.get(i).getReceived_date());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(17);
            cell.setCellValue(lDoDetail.get(i).getReceived_name());

            cell = row.createCell(18);
            cell.setCellValue(lDoDetail.get(i).getReceived_posting_name());

            cell = row.createCell(19);
            cell.setCellValue(lDoDetail.get(i).getReceived_date_posting());
            cell.setCellStyle(dateCellStyle);

        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        //sheet.autoSizeColumn(6);
        sheet.setColumnWidth(6, 10000);
        sheet.setColumnWidth(7, 10000);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        sheet.autoSizeColumn(15);
        sheet.autoSizeColumn(16);
        sheet.autoSizeColumn(17);
        sheet.autoSizeColumn(18);
        sheet.autoSizeColumn(19);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Receipt Do.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    public void createXlsRekapPengiriman() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Laporan Rekap Pengiriman");
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
        cell.setCellValue("Po. No");
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
        cell.setCellValue("Recipient Name");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(18);
        cell.setCellValue("Return Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(19);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(20);
        cell.setCellValue("Day Of Lateness");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(21);
        cell.setCellValue("Actual Cost");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(22);
        cell.setCellValue("UPD");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(23);
        cell.setCellValue("Rec. INV Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(24);
        cell.setCellValue("Invoice No.");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(25);
        cell.setCellValue("Invoice Date");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(26);
        cell.setCellValue("Remark");
        cell.setCellStyle(cellStyle);
        String st = "";
        lDoDetail = serviceDo.onLoadListXls(id_pegawai, awal, akhir, statusdo, id_gudang);
        for (int i = 0; i < lDoDetail.size(); i++) {
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(centerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(lDoDetail.get(i).getTanggal());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(lDoDetail.get(i).getNomor());

            cell = row.createCell(3);
            cell.setCellValue(lDoDetail.get(i).getId_barang());

            cell = row.createCell(4);
            cell.setCellValue(lDoDetail.get(i).getNama_barang());

            cell = row.createCell(5);
            cell.setCellValue(lDoDetail.get(i).getPo());

            cell = row.createCell(6);
            cell.setCellValue(lDoDetail.get(i).getGudang());

            cell = row.createCell(7);
            cell.setCellValue(lDoDetail.get(i).getCustomer());
            cell.setCellStyle(addressCellStyle);

            cell = row.createCell(8);
            cell.setCellValue(Jsoup.parse(lDoDetail.get(i).getAlamat()).text());
            cell.setCellStyle(addressCellStyle);

            cell = row.createCell(9);
            cell.setCellValue(lDoDetail.get(i).getQty());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(10);
            cell.setCellValue(lDoDetail.get(i).getIsi_satuan());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(11);
            cell.setCellValue(lDoDetail.get(i).getPacking_number());

            cell = row.createCell(12);
            cell.setCellValue(lDoDetail.get(i).getTransporter());

            cell = row.createCell(13);
            cell.setCellValue(lDoDetail.get(i).getLic_plate());

            cell = row.createCell(14);
            cell.setCellValue(lDoDetail.get(i).getDriver());

            cell = row.createCell(15);
            cell.setCellValue(lDoDetail.get(i).getReceived_date());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(16);
            if (lDoDetail.get(i).getDays() != null) {
                cell.setCellValue(lDoDetail.get(i).getDays());
            } else {
                cell.setCellValue("");
            }

            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(17);
            cell.setCellValue(lDoDetail.get(i).getReceived_name());

            cell = row.createCell(18);
            cell.setCellValue(lDoDetail.get(i).getReturn_date());
            cell.setCellStyle(dateCellStyle);

            cell = row.createCell(19);
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

            cell = row.createCell(20);
            cell.setCellValue("");

            cell = row.createCell(21);
            if (lDoDetail.get(i).getShippingcosts() != null) {
                cell.setCellValue(lDoDetail.get(i).getShippingcosts());
            } else {
                cell.setCellValue("");
            }
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(22);
            cell.setCellValue("");

            cell = row.createCell(23);
            cell.setCellValue("");

            cell = row.createCell(24);
            cell.setCellValue("");

            cell = row.createCell(25);
            cell.setCellValue("");

            cell = row.createCell(26);
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
        sheet.autoSizeColumn(23);
        sheet.autoSizeColumn(24);
        sheet.autoSizeColumn(25);
        sheet.autoSizeColumn(26);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Laporan Rekap Pengiriman.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    public Map<String, String> getOptionOrigin() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<String> list = serviceDo.selectAllOrigin();

            list.stream().forEach((data) -> {
                map.put(data, data);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void changeOrigin() {
        getOptionDestination();
    }
    
    public void changeDestination(){
        if(item.getOrigin() != null && item.getDestination() != null){
            item.setLeadtime(serviceDo.getTotalLeadtime(item.getOrigin(), item.getDestination()));
        }
    }

    public Map<String, String> getOptionDestination() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            if (item.getOrigin() != null) {

                List<String> list = serviceDo.selectAllDestination(item.getOrigin());

                list.stream().forEach((data) -> {
                    map.put(data, data);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
