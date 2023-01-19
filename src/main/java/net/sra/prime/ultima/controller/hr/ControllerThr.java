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

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
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
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.hr.Thr;
import net.sra.prime.ultima.entity.hr.ThrDetail;
import net.sra.prime.ultima.service.ServiceKantor;
import net.sra.prime.ultima.service.hr.ServicePayroll;
import net.sra.prime.ultima.service.hr.ServiceThr;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
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
import static org.castor.core.util.Messages.message;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
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
public class ControllerThr implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceThr serviceThr;

    @Autowired
    ServiceKantor serviceKantor;

    @Autowired
    private ServicePayroll servicePayroll;

    private Thr item;
    private ThrDetail itemdetail;
    private List<Thr> lThr = new ArrayList<>();
    private List<ThrDetail> lThrDetail = new ArrayList<>();
    private List<ThrDetail> selectedThrDetail = new ArrayList<>();
    private String year;
    private Integer month;
    private Character status;
    private Double total;
    private String pinnya;
    private boolean loggedIn;
    private Boolean tes = false;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        item = new Thr();

    }

    public void initItem() {
        item = new Thr();
        item.setStatus('D');
        selectedThrDetail = new ArrayList<>();
        total = 0.00;
        try {
            lThrDetail = serviceThr.onLoadListAllPegawai();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onLoadList() {
        lThr = serviceThr.onLoadList();
    }
    
    
    public List<Thr> getDataThr() {
        return lThr;
    }

    public List<ThrDetail> getDataThrDetail() {
        return lThrDetail;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceThr.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(st);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            serviceThr.tambah(item, selectedThrDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_thr());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        // item = serviceThr.onLoad(year, month);
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_thr());

    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        // item = serviceThr.onLoad(year, month);
        context.getExternalContext().redirect("./view.jsf?id=" + item.getId_thr());

    }

    public void onLoad() throws IOException {
        item = serviceThr.onLoad(item.getId_thr());
        selectedThrDetail = serviceThr.onLoadListPegawaiSelected(item.getId_thr());
        if (item.getStatus().equals('D')) {
            lThrDetail = serviceThr.onLoadListAllPegawai();
            thrDateChange();
        } else if (item.getStatus().equals('A')) {
            lThrDetail = selectedThrDetail;
        }
    }

    public void onLoadView() throws IOException {
        FacesMessage message = null;
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
            context.getExternalContext().redirect("./listview.jsf");
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
                item = serviceThr.onLoad(item.getId_thr());
                selectedThrDetail = serviceThr.onLoadListPegawaiSelected(item.getId_thr());
                if (item.getStatus().equals('D')) {
                    lThrDetail = serviceThr.onLoadListAllPegawai();
                    thrDateChange();
                } else if (item.getStatus().equals('A')) {
                    lThrDetail = selectedThrDetail;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void ubah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(st);
            item.setModified_by(page.getMyPegawai().getId_pegawai());
            serviceThr.ubah(item, selectedThrDetail);
            selectedThrDetail = serviceThr.onLoadListPegawaiSelected(item.getId_thr());

            if (item.getStatus().equals('A')) {
                selectedThrDetail = serviceThr.onLoadListPegawaiSelected(item.getId_thr());
                lThrDetail = selectedThrDetail;
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onRowEdit(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onDeleteClicked(ThrDetail payrollOvertimeDetail) {
        lThrDetail.remove(payrollOvertimeDetail);
    }

    public void extend() {
        lThrDetail.add(new ThrDetail());
    }

    public void onPegawaiSelect(ThrDetail pm, Integer i) {
        pm.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        pm.setNama(pegawaiAutoComplete.getPegawai().getNama());
        lThrDetail.set(i, pm);
    }

    public void thrDateChange() {
        ThrDetail detail = new ThrDetail();
        LocalDate mulaibekerja;
        LocalDate thrdate;
        Period lamanya;
        for (int i = 0; i < lThrDetail.size(); i++) {
            detail = lThrDetail.get(i);
            mulaibekerja = detail.getMulai_bekerja().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            thrdate = item.getThr_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            lamanya = Period.between(mulaibekerja, thrdate);
            detail.setYear(lamanya.getYears());
            detail.setMonth(lamanya.getMonths());
            detail.setDay(lamanya.getDays());

            detail.setDate_desc(lamanya.getYears() + " tahun " + lamanya.getMonths() + " bulan " + lamanya.getDays() + " hari");
            lThrDetail.set(i, detail);

        }
    }

    public void onTglSelect(SelectEvent event) {
        thrDateChange();
    }

    public void onTglChange(AjaxBehaviorEvent event) {
        thrDateChange();
    }

    public void getReportData() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Daftar Gaji");
            PropertyTemplate pt = new PropertyTemplate();
            pt.drawBorders(new CellRangeAddress(3, 3, 0, 3), BorderStyle.DOUBLE, BorderExtent.ALL);
            pt.drawBorders(new CellRangeAddress(4, lThrDetail.size() + 3, 0, 3), BorderStyle.DOUBLE, BorderExtent.VERTICAL);
            pt.drawBorders(new CellRangeAddress(4, lThrDetail.size() + 3, 0, 3), BorderStyle.DOTTED, BorderExtent.HORIZONTAL);
            pt.drawBorders(new CellRangeAddress(lThrDetail.size() + 4, lThrDetail.size() + 4, 0, 3), BorderStyle.DOUBLE, BorderExtent.ALL);

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
            cell.setCellValue("DAFTAR THR");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            //cell.setCellValue("PERIODE : " + options.getNamaBulan(month) + " " + year);
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
            //lPayrollDetail = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lThrDetail.size(); i++) {
                row = sheet.createRow(baris + i);
                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell = row.createCell(1);
                cell.setCellValue(lThrDetail.get(i).getPemilik_rekening());
                cell = row.createCell(2);
                cell.setCellValue(lThrDetail.get(i).getNomorrekening());
                cell = row.createCell(3);
                if(lThrDetail.get(i).getValue() == null){
                    lThrDetail.get(i).setValue(0.00);
                }
                cell.setCellValue(lThrDetail.get(i).getValue());
                cell.setCellStyle(style);
                //CellStyle cellStyle = workbook.createCellStyle();
                //cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                total = total + lThrDetail.get(i).getValue();
            }
            row = sheet.createRow(baris + lThrDetail.size());
            cell = row.createCell(0);
            cell.setCellValue("TOTAL");
            cell = row.createCell(3);
            cell.setCellValue(total);
            cell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(baris + lThrDetail.size(), baris + lThrDetail.size(), 0, 2));

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            pt.applyBorders(sheet);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Daftar THR.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReportPerhitunganThr() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Perhitungan THR");
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
            cell.setCellValue("PERHITUNGAN THR ");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
            row = sheet.createRow(2);
            cell = row.createCell(0);
            //cell.setCellValue("PERIODE : " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));

            String str;
            Integer baris = 3;
            List<InternalKantorCabang> lKantor = serviceKantor.onLoadList();
            Double total = 0.00;
            for (int j = 0; j < lKantor.size(); j++) {

                itemdetail = serviceThr.onLoadListRekapThr(item.getId_thr(), lKantor.get(j).getId_kantor_cabang());
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
                    cell.setCellValue("THR");
                    //cell.setCellStyle(cellStyle);
                    cell = row.createCell(1);
                    cell.setCellValue(itemdetail.getValue());
                    cell.setCellStyle(style);
                    baris++;
                    str = new DecimalFormat("#").format(itemdetail.getValue());
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
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Perhitungan THR.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
