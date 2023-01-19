/*
 * Copyright 2017 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sra.prime.ultima.controller.hr;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
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
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.Payroll;
import net.sra.prime.ultima.entity.hr.PayrollDetail;
import net.sra.prime.ultima.service.ServiceKantor;
import net.sra.prime.ultima.service.hr.ServiceAbsenPeriode;
import net.sra.prime.ultima.service.hr.ServicePayroll;
import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPayroll implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePayroll servicePayroll;
    @Autowired
    private ServiceAbsenPeriode serviceAbsenPeriode;
    @Autowired
    ServiceKantor serviceKantor;
    private Payroll item;
    private PayrollDetail itemdetail;
    private List<Payroll> lPayroll = new ArrayList<>();
    private List<PayrollDetail> lPayrollDetail = new ArrayList<>();

    private Integer bulan;
    private String tahun;
    private String pinnya;
    private boolean loggedIn;
    private Boolean tes = false;
    private String keterangan;
    private Integer hitung;
    private Double total;
    private String statusnya;
    private Boolean statusAbsen, statusPph, statusPinjaman, statusThr, statusInsentifPenjualan, statusKomisiPenjualan;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (tahun == null) {
            tahun = Integer.toString(localDate.getYear());
        }
        if (bulan == null) {
            bulan = localDate.getMonthValue();
        }
        item = new Payroll();
        lPayrollDetail = new ArrayList<>();

    }

    public void initItem() throws IOException {
        tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        bulan = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        //bulan =  Calendar.getInstance().get(Calendar.MONTH);
        try {
            item = servicePayroll.onLoad(tahun, bulan);
            if (item != null) {
                keterangan = "Payroll Periode " + options.getNamaBulan(bulan) + " " + tahun + " sudah pernah diposting !!!";
            }
            //hitung = servicePayroll.countAbsenNotPosting(bulan, tahun);

            // hitung=0;
            FacesMessage message = null;

            if (pinnya != null && servicePayroll.CheckPin(page.getName(), pinnya) != null) {
                loggedIn = true;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
            } else {
                loggedIn = false;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
            }

            checkStatus();
            FacesContext.getCurrentInstance().addMessage(null, message);
            PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void checkStatus() {
        AbsenPeriode periode_awal = serviceAbsenPeriode.onLoad(tahun, bulan);
        hitung = servicePayroll.countAbsenNotPosting(bulan, tahun, periode_awal.getStart());
        if (hitung > 0) {
            statusAbsen = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Absen belum diUpload"));
        } else {
            statusAbsen = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Absen sudah diinput"));
        }

        if (servicePayroll.selectOneByPeriode(tahun, bulan) == null) {
            statusPinjaman = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Potongan pinjaman karyawan belum diinput"));
        } else {
            statusPinjaman = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Potongan pinjaman karyawan sudah diinput"));
        }

        if (servicePayroll.checkTunjangan(tahun, bulan, "komisi").equals(false)) {
            statusKomisiPenjualan = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Komisi penjualan belum diinput"));
        } else {
            statusKomisiPenjualan = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Komisi penjualan sudah diinput"));
        }

        if (servicePayroll.checkTunjangan(tahun, bulan, "liter").equals(false)) {
            statusInsentifPenjualan = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Insentif Over Job - Non OT belum diinput"));
        } else {
            statusInsentifPenjualan = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Insentif Over Job - Non OT sudah diinput"));
        }

        if (servicePayroll.checkThr(tahun, bulan).equals(false)) {
            statusThr = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Pengajuan Thr belum diinput"));
        } else {
            statusThr = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Pengajuan THR sudah diinput"));
        }

    }

    public void onLoadListDaftarGaji() throws IOException {
        FacesMessage message = null;

        if (pinnya != null && servicePayroll.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            tes = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else if (pinnya == null && !tes) {
            loggedIn = false;
            tes = true;
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else if (pinnya == null && tes) {
            loggedIn = false;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("./daftargaji.jsf");
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else {
            loggedIn = false;
            tes = true;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            total = 0.00;
            lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lPayrollDetail.size(); i++) {
                total = total + lPayrollDetail.get(i).getThp();
            }
        }
    }

    public void onLoadListRekapGaji() throws IOException {
        FacesMessage message = null;

        if (pinnya != null && servicePayroll.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            tes = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else if (pinnya == null && !tes) {
            loggedIn = false;
            tes = true;
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else if (pinnya == null && tes) {
            loggedIn = false;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("./daftargaji.jsf");
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else {
            loggedIn = false;
            tes = true;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            total = 0.00;
            lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lPayrollDetail.size(); i++) {
                total = total + lPayrollDetail.get(i).getThp();
            }
        }
    }

    public void onLoadList() throws IOException {
        itemdetail = new PayrollDetail();
        lPayrollDetail = new ArrayList<>();
        FacesMessage message = null;
        //checkStatus();
        if (pinnya != null && servicePayroll.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            tes = true;
//            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
//            FacesContext.getCurrentInstance().addMessage(null, message);
        } else if (pinnya == null && !tes) {
            loggedIn = false;
            tes = true;
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else if (pinnya == null && tes) {
            loggedIn = false;
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("./viewpayroll.jsf");
            //message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Silahkan masukan PIN Anda", "");
        } else {
            loggedIn = false;
            tes = true;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            try {

                item = servicePayroll.onLoad(tahun, bulan);
                if (item == null) {
                    statusnya = "Belum ada daftar gaji bulan ini";
                } else {
                    if (item.getStatus().equals('D')) {
                        statusnya = "Gaji ";
                    }
                }
                itemdetail = new PayrollDetail();
                if (tahun == null) {
                    tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
                }
                if (bulan == null) {
                    bulan = Calendar.getInstance().get(Calendar.MONTH) + 1;
                }
                if (item == null) {
                    statusnya = "Belum ada daftar gaji bulan ini";
                } else {
                    if (item.getStatus().equals('D')) {
                        statusnya = "Belum diposting";
                    }
                    if (item.getStatus().equals('P')) {
                        statusnya = "Posting";
                    }
                }
                lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
//                lPayrollDetail = servicePayroll.onLoadList(tahun, bulan);
//                PayrollDetail pd = new PayrollDetail();
//                for (int i = 0; i < lPayrollDetail.size(); i++) {
//                    pd = lPayrollDetail.get(i);
//                    pd.setTotal_gaji(pd.getGaji_pokok() + pd.getTunjangan_jabatan() + pd.getOvertime() + pd.getInsentif_penjualan()
//                            + pd.getInsentif_kehadiran() + pd.getInsentif_produktifitas() + pd.getUmt() + pd.getTunjangan_komunikasi() + pd.getTunjangan_bbm()
//                            + pd.getThr() + pd.getKomisi_penjualan());
//
//                    pd.setTotal_potongan(pd.getJht_pekerja() + pd.getJp_pekerja() + pd.getBpjs_kes_pekerja() + pd.getAsuransi_komersil()
//                            + pd.getPrudential() + pd.getAlianz() + pd.getPph21_upah() + pd.getPph21_komisi() + pd.getPph21_thr()
//                            + pd.getPotongan_absensi() + pd.getHutang_kta() + pd.getHutang_dll());
//
//                    pd.setThp(pd.getTotal_gaji() - pd.getTotal_potongan());
//                    lPayrollDetail.set(i, pd);
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public List<Payroll> getDataPayroll() {
        return lPayroll;
    }

    public List<PayrollDetail> getDataPayrollDetail() {
        return lPayrollDetail;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePayroll.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah(Character s) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item = new Payroll();
            item.setYear(tahun);
            item.setMonth(bulan);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            item.setStatus('D');
            servicePayroll.tambah(item, s, page.getValueBegawi("loanpaid"), page.getValueBegawi("pphsetting"), page.getValueBegawi("umt"), page.getValueBegawi("bayarovertime"), page.getValueBegawi("bayarproduktivitas"));
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
//            context.getExternalContext().redirect("./viewpayroll.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update(String idtunjangan) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + idtunjangan);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setYear(tahun);
            item.setMonth(bulan);
            item.setModified_by(page.getMyPegawai().getId_pegawai());
            item.setStatus('P');
            servicePayroll.ubah(item);
            statusnya = "Posting";
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            item.setStatus('D');
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onDetilSelect(Integer id, String id_pegawai) {
        itemdetail = servicePayroll.onLoadDetail(id, id_pegawai);
        itemdetail.setTotal_gaji(itemdetail.getGaji_pokok()
                + itemdetail.getTunjangan_jabatan()
                + itemdetail.getOvertime()
                + itemdetail.getInsentif_penjualan()
                + itemdetail.getInsentif_kehadiran()
                + itemdetail.getInsentif_produktifitas()
                + itemdetail.getUmt()
                + itemdetail.getTunjangan_komunikasi()
                + itemdetail.getTunjangan_bbm()
                + itemdetail.getThr()
                + itemdetail.getKomisi_penjualan());

        itemdetail.setTotal_potongan(itemdetail.getJht_pekerja()
                + itemdetail.getJp_pekerja()
                + itemdetail.getBpjs_kes_pekerja()
                + itemdetail.getAsuransi_komersil()
                + itemdetail.getPrudential()
                + itemdetail.getAlianz()
                + itemdetail.getPph21_upah()
                + itemdetail.getPph21_komisi()
                + itemdetail.getPph21_thr()
                + itemdetail.getPotongan_absensi()
                + itemdetail.getHutang_kta()
                + itemdetail.getHutang_dll());

        itemdetail.setThp(itemdetail.getTotal_gaji() - itemdetail.getTotal_potongan());
    }

    public void ubahDetail() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePayroll.ubahDetail(itemdetail);
            onLoadList();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void hitungTHP() {
        itemdetail.setTotal_gaji(itemdetail.getGaji_pokok()
                + itemdetail.getTunjangan_jabatan()
                + itemdetail.getOvertime()
                + itemdetail.getInsentif_penjualan()
                + itemdetail.getInsentif_kehadiran()
                + itemdetail.getInsentif_produktifitas()
                + itemdetail.getUmt()
                + itemdetail.getTunjangan_komunikasi()
                + itemdetail.getTunjangan_bbm()
                + itemdetail.getThr()
                + itemdetail.getKomisi_penjualan());

        itemdetail.setTotal_potongan(itemdetail.getJht_pekerja()
                + itemdetail.getJp_pekerja()
                + itemdetail.getBpjs_kes_pekerja()
                + itemdetail.getAsuransi_komersil()
                + itemdetail.getPrudential()
                + itemdetail.getAlianz()
                + itemdetail.getPph21_upah()
                + itemdetail.getPph21_komisi()
                + itemdetail.getPph21_thr()
                + itemdetail.getPotongan_absensi()
                + itemdetail.getHutang_kta()
                + itemdetail.getHutang_dll());

        itemdetail.setThp(itemdetail.getTotal_gaji() - itemdetail.getTotal_potongan());
    }

    public void cetak(Integer id, String idPegawai) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakslip.jsf?id=" + id + "&idpegawai=" + idPegawai);

    }

    public void onPeriodeChange() {
        item = servicePayroll.onLoad(tahun, bulan);
        if (item != null) {
            keterangan = "Payroll Periode " + options.getNamaBulan(bulan) + " " + tahun + " sudah pernah diposting !!!";
        } else {
            keterangan = null;
        }

        checkStatus();
    }

    public void getReportData() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Daftar Gaji");
            PropertyTemplate pt = new PropertyTemplate();
            pt.drawBorders(new CellRangeAddress(3, 3, 0, 3), BorderStyle.DOUBLE, BorderExtent.ALL);
            pt.drawBorders(new CellRangeAddress(4, lPayrollDetail.size() + 3, 0, 3), BorderStyle.DOUBLE, BorderExtent.VERTICAL);
            pt.drawBorders(new CellRangeAddress(4, lPayrollDetail.size() + 3, 0, 3), BorderStyle.DOTTED, BorderExtent.HORIZONTAL);
            pt.drawBorders(new CellRangeAddress(lPayrollDetail.size() + 4, lPayrollDetail.size() + 4, 0, 3), BorderStyle.DOUBLE, BorderExtent.ALL);

            CellStyle cellStyle = workbook.createCellStyle();
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("#,##0"));
            Font font = workbook.createFont();

            font.setBold(true);
            font.setFontName("Calibri");

            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    3 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("DAFTAR GAJI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("PERIODE : " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue("NAMA KARYAWAN");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("NO. REKENING");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue("GAJI ( Rp. )");
            cell.setCellStyle(cellStyle);

            total = 0.00;
            Integer baris = 4;
            lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lPayrollDetail.size(); i++) {
                row = sheet.createRow(baris + i);
                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell = row.createCell(1);
                cell.setCellValue(lPayrollDetail.get(i).getPemilik_rekening());
                cell = row.createCell(2);
                cell.setCellValue(lPayrollDetail.get(i).getNomorrekening());
                cell = row.createCell(3);
                cell.setCellValue(lPayrollDetail.get(i).getThp());
                cell.setCellStyle(style);
                //CellStyle cellStyle = workbook.createCellStyle();
                //cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                total = total + lPayrollDetail.get(i).getThp();
            }
            row = sheet.createRow(baris + lPayrollDetail.size());
            cell = row.createCell(0);
            cell.setCellValue("TOTAL");
            cell = row.createCell(3);
            cell.setCellValue(total);
            cell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(baris + lPayrollDetail.size(), baris + lPayrollDetail.size(), 0, 2));

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            pt.applyBorders(sheet);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Daftar Gaji.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
        }
    }

    public void getReportDataRekap() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Rekap Gaji");
            PropertyTemplate pt = new PropertyTemplate();
            pt.drawBorders(new CellRangeAddress(3, 3, 0, 25), BorderStyle.DOUBLE, BorderExtent.ALL);
            pt.drawBorders(new CellRangeAddress(4, lPayrollDetail.size() + 3, 0, 25), BorderStyle.DOUBLE, BorderExtent.VERTICAL);
            pt.drawBorders(new CellRangeAddress(4, lPayrollDetail.size() + 3, 0, 25), BorderStyle.DOTTED, BorderExtent.HORIZONTAL);
            pt.drawBorders(new CellRangeAddress(lPayrollDetail.size() + 4, lPayrollDetail.size() + 4, 0, 25), BorderStyle.DOUBLE, BorderExtent.ALL);

            CellStyle cellStyle = workbook.createCellStyle();
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat("#,##0"));
            Font font = workbook.createFont();

            font.setBold(true);
            font.setFontName("Calibri");

            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    25 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("REKAPITULASI GAJI ");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 25));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("PERIODE : " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 25));

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue("Nama Karyawan");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("Gaji Pokok");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue("Insentif Jabatan");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue("Over Time");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue("Insentif Over Job-Non OT");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue("UMT");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(7);
            cell.setCellValue("Insentif Site");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(8);
            cell.setCellValue("Insentif Produktivitas");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(9);
            cell.setCellValue("Insentif Komunikasi");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(10);
            cell.setCellValue("THR");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(11);
            cell.setCellValue("Insentif Mutasi");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(12);
            cell.setCellValue("Komisi Penjualan");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(13);
            cell.setCellValue("Total Gaji");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(14);
            cell.setCellValue("Jaminan Hari Tua");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(15);
            cell.setCellValue("Jaminan Pensiun");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(16);
            cell.setCellValue("Jaminan Kesehatan");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(17);
            cell.setCellValue("Prudential");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(18);
            cell.setCellValue("Allianz");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(19);
            cell.setCellValue("Hutang KTA");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(20);
            cell.setCellValue("Hutang Lain2");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(21);
            cell.setCellValue("Unpaid Leave(UL)");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(22);
            cell.setCellValue("PPH");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(23);
            cell.setCellValue("Lain-lain");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(24);
            cell.setCellValue("Total Potongan");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(25);
            cell.setCellValue("Total Gaji");
            cell.setCellStyle(cellStyle);
            total = 0.00;
            Integer baris = 4;
            lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lPayrollDetail.size(); i++) {
                row = sheet.createRow(baris + i);
                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell = row.createCell(1);
                cell.setCellValue(lPayrollDetail.get(i).getNama_pegawai());
                cell = row.createCell(2);
                cell.setCellValue(lPayrollDetail.get(i).getGaji_pokok());
                cell.setCellStyle(style);
                cell = row.createCell(3);
                cell.setCellValue(lPayrollDetail.get(i).getTunjangan_jabatan());
                cell.setCellStyle(style);
                cell = row.createCell(4);
                cell.setCellValue(lPayrollDetail.get(i).getOvertime());
                cell.setCellStyle(style);
                cell = row.createCell(5);
                cell.setCellValue(lPayrollDetail.get(i).getInsentif_penjualan());
                cell.setCellStyle(style);
                cell = row.createCell(6);
                cell.setCellValue(lPayrollDetail.get(i).getUmt());
                cell.setCellStyle(style);
                cell = row.createCell(7);
                cell.setCellValue(lPayrollDetail.get(i).getInsentif_kehadiran());
                cell.setCellStyle(style);
                cell = row.createCell(8);
                cell.setCellValue(lPayrollDetail.get(i).getInsentif_produktifitas());
                cell.setCellStyle(style);
                cell = row.createCell(9);
                cell.setCellValue(lPayrollDetail.get(i).getTunjangan_komunikasi());
                cell.setCellStyle(style);
                cell = row.createCell(10);
                cell.setCellValue(lPayrollDetail.get(i).getThr());
                cell.setCellStyle(style);
                cell = row.createCell(11);
                cell.setCellValue(lPayrollDetail.get(i).getTunjangan_bbm());
                cell.setCellStyle(style);
                cell = row.createCell(12);
                cell.setCellValue(lPayrollDetail.get(i).getKomisi_penjualan());
                cell.setCellStyle(style);
                cell = row.createCell(13);
                cell.setCellValue(lPayrollDetail.get(i).getTotal_gaji());
                cell.setCellStyle(style);
                cell = row.createCell(14);
                cell.setCellValue(lPayrollDetail.get(i).getJht_pekerja());
                cell.setCellStyle(style);
                cell = row.createCell(15);
                cell.setCellValue(lPayrollDetail.get(i).getJp_pekerja());
                cell.setCellStyle(style);
                cell = row.createCell(16);
                cell.setCellValue(lPayrollDetail.get(i).getBpjs_kes_pekerja());
                cell.setCellStyle(style);
                cell = row.createCell(17);
                cell.setCellValue(lPayrollDetail.get(i).getPrudential());
                cell.setCellStyle(style);
                cell = row.createCell(18);
                cell.setCellValue(lPayrollDetail.get(i).getAlianz());
                cell.setCellStyle(style);
                cell = row.createCell(19);
                cell.setCellValue(lPayrollDetail.get(i).getHutang_kta());
                cell.setCellStyle(style);
                cell = row.createCell(20);
                cell.setCellValue(lPayrollDetail.get(i).getHutang_dll());
                cell.setCellStyle(style);
                cell = row.createCell(21);
                cell.setCellValue(lPayrollDetail.get(i).getPotongan_absensi());
                cell.setCellStyle(style);
                cell = row.createCell(22);
                cell.setCellValue(lPayrollDetail.get(i).getPph21_upah());
                cell.setCellStyle(style);
                cell = row.createCell(23);
                cell.setCellValue(0);
                cell.setCellStyle(style);
                cell = row.createCell(24);
                cell.setCellValue(lPayrollDetail.get(i).getTotal_potongan());
                cell.setCellStyle(style);
                cell = row.createCell(25);
                cell.setCellValue(lPayrollDetail.get(i).getThp());
                cell.setCellStyle(style);
                //CellStyle cellStyle = workbook.createCellStyle();
                //cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                total = total + lPayrollDetail.get(i).getThp();
            }
            row = sheet.createRow(baris + lPayrollDetail.size());
            cell = row.createCell(0);
            cell.setCellValue("TOTAL");
            cell = row.createCell(25);
            cell.setCellValue(total);
            cell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(baris + lPayrollDetail.size(), baris + lPayrollDetail.size(), 0, 24));

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

            pt.applyBorders(sheet);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Rekap Gaji.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
        }
    }

    public void getReportPerhitunganGaji() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Perhitungan Gaji");
            PropertyTemplate pt = new PropertyTemplate();

            CellStyle cellStyle = workbook.createCellStyle();
            CellStyle style = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();

            style.setDataFormat(format.getFormat("#,##0"));
            Font font = workbook.createFont();

            font.setBold(true);
            font.setFontName("Calibri");

            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    1 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("PERHITUNGAN GAJI ");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("PERIODE : " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));

            String str;
            Integer baris = 3;
            List<InternalKantorCabang> lKantor = serviceKantor.onLoadList();
            Double total = 0.00;
            for (int j = 0; j < lKantor.size(); j++) {

                itemdetail = servicePayroll.onLoadListRekapGaji(tahun, bulan, lKantor.get(j).getId_kantor_cabang());
                if (itemdetail != null) {
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("CABANG " + lKantor.get(j).getNama().toUpperCase());
                    //cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(baris, baris, 0, 1));
                    baris++;
                    pt.drawBorders(new CellRangeAddress(baris, baris + 8, 0, 1), BorderStyle.DOUBLE, BorderExtent.ALL);
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("KETERANGAN");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue("JUMLAH (IDR)");
                    cell.setCellStyle(cellStyle);
                    baris++;

                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("GAJI");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(itemdetail.getTotal_gaji());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("PINJAMAN KARYAWAN/ JTRUST");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(-1 * itemdetail.getHutang_kta());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("BPJS KESEHATAN & KETENAGAKERJAAN");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(-1 * itemdetail.getJht_pekerja());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("UL/ ABSEN");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(-1 * itemdetail.getPotongan_absensi());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("ALLIANZ/ PRUDENTIAL");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(-1 * itemdetail.getAsuransi_komersil());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("PPH KARYAWAN");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(-1 * itemdetail.getPph21_upah());
                    cell.setCellStyle(style);
                    baris++;
                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("SISA GAJI");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(itemdetail.getThp());
                    cell.setCellStyle(style);
                    baris++;
                    str = new DecimalFormat("#").format(itemdetail.getThp());
                    int length = str.length();
                    str = str.substring(0, length - 2) + "00";

                    row = sheet.createRow(baris);
                    cell = row.createCell(0);
                    cell.setCellValue("PEMBULATAN");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(Double.parseDouble(str));
                    cell.setCellStyle(style);
                    baris++;
                    total = total + Double.parseDouble(str);
                }
            }
            baris++;

            row = sheet.createRow(baris);
            cell = row.createCell(0);
            cell.setCellValue("TOTAL");
            //cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue(total);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            pt.applyBorders(sheet);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Perhitungan Gaji.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
