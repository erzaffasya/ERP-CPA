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

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.service.ServicePegawai;
import net.sra.prime.ultima.service.hr.ServiceAbsen;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAbsensiReport implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    private List<String> rowNames = new ArrayList<String>();
    private List<String> rowNames2 = new ArrayList<String>();
    private List<String> rowNames3 = new ArrayList<String>();
    private List<String> colNames = new ArrayList<String>();
    private ArrayList<ArrayList<ArrayList<String>>> data3D = new ArrayList<ArrayList<ArrayList<String>>>();
    private List<Date> listDate = new ArrayList<Date>();

    private Logger logger;
    private List<Absen> lAbsen = new ArrayList<>();
    private List<Pegawai> lPegawai = new ArrayList<>();
    private Absen item;
    private Date datestart;
    private String stringdatestart;
    private Date dateend;
    private Pegawai itemPegawai;
    private String year;
    private String month_name;
    private String judul;

    private Integer id_departemen;
    private String id_kantor;

    private Double progress2;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServiceAbsen serviceAbsen;

    @Autowired
    ServicePegawai servicePegawai;

    @PostConstruct
    public void init() {
        item = new Absen();
        itemPegawai = new Pegawai();
        datestart = new Date();
    }
    
    public void initItem(){
        progress2=0.0;
    }

    public List<Absen> getDataAbsen() {
        return lAbsen;
    }

    public void createReport() throws InterruptedException, IOException {
        progress2 = 0.00;
        rowNames = new ArrayList<String>();
        rowNames2 = new ArrayList<String>();
        rowNames3 = new ArrayList<String>();
        colNames = new ArrayList<String>();
        data3D = new ArrayList<ArrayList<ArrayList<String>>>();

        if (datestart == null) {
            datestart = new Date();
        }

        DateFormat tglString = new SimpleDateFormat("yyyy-MM-dd"); // Just the year, with 2 digits
        stringdatestart = tglString.format(datestart);

        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate.plusDays(1);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        DateFormat tgl = new SimpleDateFormat("dd"); // Just the year, with 2 digits
        AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriode(Integer.toString(year), month);
        LocalDateTime localDateTime = absenPeriode.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeEnd = localDateTime.plusDays(1);
        dateend = Date.from(localDateTimeEnd.atZone(ZoneId.systemDefault()).toInstant());

        listDate = getDatesBetweenUsingJava7(absenPeriode.getStart(), dateend);
        for (int i = 0; i < listDate.size(); i++) {
            colNames.add(tgl.format(listDate.get(i)));
        }

        lPegawai = servicePegawai.onLoadListOnlyPegawai(id_kantor, id_departemen);
        for (int i = 0; i < lPegawai.size(); i++) {
            rowNames.add(lPegawai.get(i).getNama());
            rowNames2.add(lPegawai.get(i).getJabatan());
            rowNames3.add(lPegawai.get(i).getId_pegawai());
        }

        Double pengali = 0.00;

        if (lPegawai.size() > 0) {
            pengali = 100.00 / lPegawai.size();
        }
        progress2 = pengali;
        // Setup 3 dimensional structure
        for (int i = 0; i < lPegawai.size(); i++) {
            lAbsen = attendaceFunction(datestart, lPegawai.get(i).getId_pegawai());
            if (lAbsen == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                progress2 = 0.00;
                rowNames = new ArrayList<String>();
                rowNames2 = new ArrayList<String>();
                rowNames3 = new ArrayList<String>();
                colNames = new ArrayList<String>();
                data3D = new ArrayList<ArrayList<ArrayList<String>>>();
                context.getExternalContext().redirect("./monthlyreport.jsf");
                progress2 = 0.00;
                break;
            }
            data3D.add(new ArrayList<ArrayList<String>>());
            for (int j = 0; j < listDate.size(); j++) {
                data3D.get(i).add(new ArrayList<String>());
                Absen abs = findAbsen(listDate.get(j), lAbsen);
                //data3D.get(i).get(j).add(lPegawai.get(j).getNama());
                if (abs != null) {
                    if (abs.getSchedule().equals('o')) {
                        if (abs.getStatus_absen() != null) {
                            //if (abs.getStatus_absen().equals("LA")) {
                            data3D.get(i).get(j).add(abs.getId_status_absen());
                            //}
                        } else {
                            data3D.get(i).get(j).add("LBR");
                        }
                    } else {
                        if (abs.getStatus_absen() != null) {
                            data3D.get(i).get(j).add(abs.getId_status_absen());
                        }
                    }
                } else {
                    data3D.get(i).get(j).add("X");
                }

            }
            progress2 = progress2 + pengali;
        }
        //progress2=progress2 + pengali;
        progress2 = 100.00;
    }

    public void createSummary() throws InterruptedException, IOException {
        progress2 = 0.00;
        rowNames = new ArrayList<String>();
        rowNames2 = new ArrayList<String>();
        rowNames3 = new ArrayList<String>();
        colNames = new ArrayList<String>();
        data3D = new ArrayList<ArrayList<ArrayList<String>>>();

        if (datestart == null) {
            datestart = new Date();
        }

        DateFormat tglString = new SimpleDateFormat("yyyy-MM-dd"); // Just the year, with 2 digits
        stringdatestart = tglString.format(datestart);

        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate.plusDays(1);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        DateFormat tgl = new SimpleDateFormat("dd"); // Just the year, with 2 digits

        colNames.add("M");
        colNames.add("M-UMT");
        colNames.add("LA");
        colNames.add("VC");
        colNames.add("A");
        colNames.add("S");
        colNames.add("CK");
        colNames.add("CT");
        colNames.add("OFF");
        colNames.add("UL");
        colNames.add("LBR");
        colNames.add("CB");
        colNames.add("M-DL");
        colNames.add("UMT");
        colNames.add("Tot.Kehadiran");
        colNames.add("Status");

        lPegawai = servicePegawai.onLoadListOnlyPegawai(id_kantor, id_departemen);
        for (int i = 0; i < lPegawai.size(); i++) {
            rowNames.add(lPegawai.get(i).getNama());
            rowNames2.add(lPegawai.get(i).getJabatan());
            rowNames3.add(lPegawai.get(i).getId_pegawai());
        }

        Double pengali = 0.00;

        if (lPegawai.size() > 0) {
            pengali = 100.00 / lPegawai.size();
        }
        progress2 = pengali;
        // Setup 3 dimensional structure
        for (int i = 0; i < lPegawai.size(); i++) {
            data3D.add(new ArrayList<ArrayList<String>>());
            AbsenPegawaiStatus aps = serviceAbsen.selectOnePegawaiStatus(lPegawai.get(i).getId_pegawai(), month, Integer.toString(year));
            if (aps != null) {
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(0).add(Integer.toString(aps.getTobepresent()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(1).add(Integer.toString(aps.getTobepresentwithoutumt()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(2).add(Integer.toString(aps.getLupaabsen()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(3).add(Integer.toString(aps.getVisitcustomer()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(4).add(Integer.toString(aps.getAbsen()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(5).add(Integer.toString(aps.getSakit()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(6).add(Integer.toString(aps.getIjin()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(7).add(Integer.toString(aps.getCuti()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(8).add(Integer.toString(aps.getOff()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(9).add(Integer.toString(aps.getUnpaidleave()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(10).add(Integer.toString(aps.getLibur()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(11).add(Integer.toString(aps.getCutibersama()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(12).add(Integer.toString(aps.getVisitcustomerharilibur()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(13).add(Integer.toString(aps.getUmt()));
                data3D.get(i).add(new ArrayList<String>());
                data3D.get(i).get(14).add(Integer.toString(aps.getTobepresent() + aps.getVisitcustomer() + aps.getLupaabsen() + aps.getTobepresentwithoutumt() + aps.getVisitcustomerharilibur()));
                data3D.get(i).add(new ArrayList<String>());
                if (aps.getStatus().equals('D')) {
                    data3D.get(i).get(15).add("Draft");
                } else if (aps.getStatus().equals('P')) {
                    data3D.get(i).get(15).add("Posting");
                }
            }

            progress2 = progress2 + pengali;
        }
        //progress2=progress2 + pengali;
        progress2 = 100.00;
    }

    public Absen findAbsen(
            Date tgl, List<Absen> absens) {

        for (Absen absen : absens) {
            if (absen.getTanggal().equals(tgl)) {
                return absen;
            }
        }
        return null;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getDatadata3D() {
        return data3D;
    }

    public static List<Date> getDatesBetweenUsingJava7(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public List<Absen> attendaceFunction(Date tanggalmulai, String id_pegawai) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        LocalDate localDate = tanggalmulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        long terlambat = 0;
        long pulangcepat = 0;
        AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriode(Integer.toString(year), month);
        Pegawai pegawai = servicePegawai.onLoadDataDiri(id_pegawai);
        JadwalKerja jadwalKerja = serviceAbsen.selectOneJadwalKerja(id_pegawai);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long duration;

        if (jadwalKerja != null) {
            Calendar cal1 = Calendar.getInstance();
            Integer hari;
            //lAbsen = serviceAbsen.onLoadList(id_pegawai, tanggalmulai, dateend);
            lAbsen = serviceAbsen.onLoadList(id_pegawai, absenPeriode.getStart(), absenPeriode.getEnd_date());
            JadwalKerjaWaktu jadwalKerjaWaktu = new JadwalKerjaWaktu();
            Absen absen = new Absen();
            for (int i = 0; i < lAbsen.size(); i++) {
                absen = lAbsen.get(i);
                absen.setId_absen(pegawai.getAbsen());
                absen.setId_pegawai(item.getId_pegawai());
                cal1.setTime(absen.getTanggal());
                if (cal1.get(Calendar.DAY_OF_WEEK) == 1) {
                    hari = 7;
                } else {
                    hari = cal1.get(Calendar.DAY_OF_WEEK) - 1;
                }

                if (jadwalKerja.getJenis().equals('m')) {
                    if (jadwalKerja.getCycle() == 1) {
                        jadwalKerjaWaktu = serviceAbsen.selectOneJadwalKerjaWaktu(jadwalKerja.getId(), hari);
                        if (!jadwalKerjaWaktu.getSelected()) {

                            absen.setSchedule('o'); // off
                            absen.setSchedule_des("Libur");

                        } else {
                            absen.setSchedule('p'); // present
                            absen.setSchedule_des(dateFormat.format(jadwalKerjaWaktu.getTime_in()) + " - " + dateFormat.format(jadwalKerjaWaktu.getTime_out()));
                        }
                        absen.setTime_in(jadwalKerjaWaktu.getTime_in());
                        absen.setTime_out(jadwalKerjaWaktu.getTime_out());
                        absen.setTime_break(jadwalKerjaWaktu.getTime_break());
                        if (absen.getJam_keluar() != null && absen.getTime_out() != null) {
                            duration = absen.getJam_keluar().getTime() - absen.getTime_out().getTime();
                            if (duration > 0) {
                                duration = duration / (60 * 60 * 1000) % 24;

                                absen.setLemburafter((int) (long) duration);
                            }
                        }
                    }
                }

                /// Hari Libur
                HariLibur hariLibur = serviceAbsen.selectHariLibur(absen.getTanggal());
                if (hariLibur != null) {
                    absen.setSchedule('o'); // off
                    absen.setSchedule_des("Libur " + hariLibur.getNama());

                }
                if (absen.getId_status_absen() == null) {

                    //bukan hari libur
                    if (!absen.getSchedule().equals('o')) {
                        if (absen.getJam_masuk() != null) {
                            terlambat = absen.getJam_masuk().getTime() - absen.getTime_in().getTime();
                            terlambat = terlambat / (60 * 1000) % 60;

                            pulangcepat = absen.getTime_out().getTime() - absen.getJam_keluar().getTime();
                            pulangcepat = pulangcepat / (60 * 1000) % 60;
                            if (absen.getJam_masuk() != null) {
                                if (absen.getJam_masuk().equals(absen.getJam_keluar())) {
                                    absen.setId_status_absen("LA");
                                    absen.setStatus_absen("Lupa Absen");
                                } else if ((int) (long) terlambat > jadwalKerja.getBatas_terlambat() || (int) (long) pulangcepat > jadwalKerja.getBatas_pulang()) {
                                    absen.setId_status_absen("MW");
                                    absen.setStatus_absen("To be present without UMT");
                                } else {
                                    absen.setId_status_absen("M");
                                    absen.setStatus_absen("To be present");
                                }

                            }
                        }
                    } else if (absen.getJam_masuk() != null) {
                        absen.setId_status_absen("M");
                        absen.setStatus_absen("To be present");

                    }
                    serviceAbsen.update(absen);
                }

                lAbsen.set(i, absen);
            }
            return lAbsen;
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jadwal Kerja  " + pegawai.getNama() + " belum dibuat !!!", ""));
            return null;
        }
    }

    public void createXls() throws InterruptedException, IOException {

        progress2 = 0.00;
        rowNames = new ArrayList<String>();
        colNames = new ArrayList<String>();
        data3D = new ArrayList<ArrayList<ArrayList<String>>>();

        if (datestart == null) {
            datestart = new Date();
        }

        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate.plusDays(1);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        DateFormat tgl = new SimpleDateFormat("dd"); // Just the year, with 2 digits
        AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriode(Integer.toString(year), month);
        LocalDateTime localDateTime = absenPeriode.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeEnd = localDateTime.plusDays(1);
        dateend = Date.from(localDateTimeEnd.atZone(ZoneId.systemDefault()).toInstant());

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Absen Pegawai");
        PropertyTemplate pt = new PropertyTemplate();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // style gray
        XSSFCellStyle styleGray = workbook.createCellStyle();
        XSSFColor myColor = new XSSFColor(Color.GRAY);
        styleGray.setAlignment(HorizontalAlignment.CENTER);
        styleGray.setFillBackgroundColor(myColor);
        styleGray.setFillForegroundColor(myColor);
        styleGray.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // Style Red
        XSSFCellStyle styleRed = workbook.createCellStyle();
        XSSFColor redColor = new XSSFColor(Color.RED);
        styleRed.setAlignment(HorizontalAlignment.CENTER);
        styleRed.setFillBackgroundColor(redColor);
        styleRed.setFillForegroundColor(redColor);
        styleRed.setFillPattern(CellStyle.SOLID_FOREGROUND);

        Row row = sheet.createRow(0);
        Cell cell;
//        cell.setCellValue("PT. CAHAYA PENGAJARAN ABADI");
//        cell.setCellStyle(cellStyle);
//        sheet.addMergedRegion(new CellRangeAddress(
//                0, //first row (0-based)
//                0, //last row  (0-based)
//                0, //first column (0-based)
//                3 //last column  (0-based)
//        ));

        listDate = getDatesBetweenUsingJava7(absenPeriode.getStart(), dateend);
        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("Name");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Position");
        cell.setCellStyle(cellStyle);
        for (int i = 0; i < listDate.size(); i++) {
            //colNames.add(tgl.format(listDate.get(i)));
            cell = row.createCell(i + 3);
            cell.setCellValue(tgl.format(listDate.get(i)));
            cell.setCellStyle(cellStyle);
        }

        Double pengali = 0.00;
        if (lPegawai.size() > 0) {
            pengali = 100.00 / lPegawai.size();
        }
        progress2 = pengali;
        // Setup 3 dimensional structure

        lPegawai = servicePegawai.onLoadListOnlyPegawai(id_kantor, id_departemen);
        for (int i = 0; i < lPegawai.size(); i++) {
            //System.out.println(lPegawai.get(i).getId_pegawai() + " " + lPegawai.get(i).getNama());
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell = row.createCell(1);
            cell.setCellValue(lPegawai.get(i).getNama());
            cell = row.createCell(2);
            cell.setCellValue(lPegawai.get(i).getJabatan());
            lAbsen = attendaceFunction(datestart, lPegawai.get(i).getId_pegawai());
            if (lAbsen == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getFlash().setKeepMessages(true);
                progress2 = 0.00;
                rowNames = new ArrayList<String>();
                rowNames2 = new ArrayList<String>();
                rowNames3 = new ArrayList<String>();
                colNames = new ArrayList<String>();
                data3D = new ArrayList<ArrayList<ArrayList<String>>>();
                context.getExternalContext().redirect("./monthlyreport.jsf");
                progress2 = 0.00;
                break;
            }
            for (int j = 0; j < listDate.size(); j++) {
                Absen abs = findAbsen(listDate.get(j), lAbsen);
                if (abs != null) {
                    // hari libur
                    if (abs.getSchedule().equals('o')) {
                        cell = row.createCell(j + 3);
                        if (abs.getStatus_absen() != null) {
                            if (abs.getStatus_absen().equals("M") || abs.getStatus_absen().equals("MW")) {
                                if (abs.getJam_masuk() != null && abs.getJam_keluar() != null) {
                                    if (abs.getJam_masuk().equals(abs.getJam_keluar())) {
                                        cell.setCellStyle(styleRed);
                                    } else {
                                        cell.setCellValue(abs.getId_status_absen());
                                    }
                                } else {
                                    cell.setCellStyle(styleRed);
                                }
                            } else {
                                cell.setCellValue(abs.getId_status_absen());
                            }
                        } else {
                            cell.setCellValue("LBR");
                            cell.setCellStyle(styleGray);
                        }
                        // hari kerja    
                    } else {
                        cell = row.createCell(j + 3);
                        if (abs.getId_status_absen() != null) {

                            if (abs.getStatus_absen().equals("M") || abs.getStatus_absen().equals("MW")) {
                                if (abs.getJam_masuk() != null && abs.getJam_keluar() != null) {
                                    if (abs.getJam_masuk().equals(abs.getJam_keluar())) {
                                        cell.setCellStyle(styleRed);
                                    } else {
                                        cell.setCellValue(abs.getId_status_absen());
                                    }
                                } else {
                                    cell.setCellStyle(styleRed);
                                }
                            } else {
                                cell.setCellValue(abs.getId_status_absen());
                            }
                        } else {
                            cell.setCellValue("");

                        }
                    }
                } else {
                    cell = row.createCell(j + 3);
                    cell.setCellValue("");
                }

            }
            progress2 = progress2 + pengali;
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        progress2 = 100.00;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"MyFirstExcel.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();
    }

    public void createSummaryXls() throws InterruptedException, IOException {

        progress2 = 0.00;
        //rowNames = new ArrayList<String>();
        //colNames = new ArrayList<String>();
        //data3D = new ArrayList<ArrayList<ArrayList<String>>>();

        if (datestart == null) {
            datestart = new Date();
        }

        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate.plusDays(1);
        int year = localDate.getYear();
        int month = localDate.getMonthValue();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Resume Absensi");
        PropertyTemplate pt = new PropertyTemplate();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // style gray
        XSSFCellStyle styleGray = workbook.createCellStyle();
        XSSFColor myColor = new XSSFColor(Color.GRAY);
        styleGray.setAlignment(HorizontalAlignment.CENTER);
        styleGray.setFillBackgroundColor(myColor);
        styleGray.setFillForegroundColor(myColor);
        styleGray.setFillPattern(CellStyle.SOLID_FOREGROUND);

        // Style Red
        XSSFCellStyle styleRed = workbook.createCellStyle();
        XSSFColor redColor = new XSSFColor(Color.RED);
        styleRed.setAlignment(HorizontalAlignment.CENTER);
        styleRed.setFillBackgroundColor(redColor);
        styleRed.setFillForegroundColor(redColor);
        styleRed.setFillPattern(CellStyle.SOLID_FOREGROUND);

        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("Name");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Position");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("M");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("M-UMT");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("LA");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("VC");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("A");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("S");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("CK");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("CT");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(11);
        cell.setCellValue("OFF");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(12);
        cell.setCellValue("UL");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(13);
        cell.setCellValue("LBR");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(14);
        cell.setCellValue("CB");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(15);
        cell.setCellValue("M-DL");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(16);
        cell.setCellValue("UMT");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(17);
        cell.setCellValue("Tot.Kehadiran");
        cell.setCellStyle(cellStyle);

        Double pengali = 0.00;
        if (lPegawai.size() > 0) {
            pengali = 100.00 / lPegawai.size();
        }
        progress2 = pengali;
        // Setup 3 dimensional structure

        lPegawai = servicePegawai.onLoadListOnlyPegawai(id_kantor, id_departemen);
        for (int i = 0; i < lPegawai.size(); i++) {
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell = row.createCell(1);
            cell.setCellValue(lPegawai.get(i).getNama());
            cell = row.createCell(2);
            cell.setCellValue(lPegawai.get(i).getJabatan());

            AbsenPegawaiStatus aps = serviceAbsen.selectOnePegawaiStatus(lPegawai.get(i).getId_pegawai(), month, Integer.toString(year));
            if (aps != null) {
                cell = row.createCell(3);
                cell.setCellValue(aps.getTobepresent());

                cell = row.createCell(4);
                cell.setCellValue(aps.getTobepresentwithoutumt());

                cell = row.createCell(5);
                cell.setCellValue(aps.getLupaabsen());

                cell = row.createCell(6);
                cell.setCellValue(aps.getVisitcustomer());

                cell = row.createCell(7);
                cell.setCellValue(aps.getAbsen());

                cell = row.createCell(8);
                cell.setCellValue(aps.getSakit());

                cell = row.createCell(9);
                cell.setCellValue(aps.getIjin());

                cell = row.createCell(10);
                cell.setCellValue(aps.getCuti());

                cell = row.createCell(11);
                cell.setCellValue(aps.getOff());

                cell = row.createCell(12);
                cell.setCellValue(aps.getUnpaidleave());

                cell = row.createCell(13);
                cell.setCellValue(aps.getLibur());

                cell = row.createCell(14);
                cell.setCellValue(aps.getCutibersama());

                cell = row.createCell(15);
                cell.setCellValue(aps.getVisitcustomerharilibur());

                cell = row.createCell(16);
                //cell.setCellValue(aps.getTobepresent() + aps.getVisitcustomer() + aps.getVisitcustomerharilibur() + aps.getPerjalanandinas());
                cell.setCellValue(aps.getUmt());
//                if (!lPegawai.get(i).getStatuskaryawan().equals('1')) {
//                    LocalDate mulaikerja = lPegawai.get(i).getMulai_bekerja().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    Date tgl = new Date();
//                    LocalDate sekarang = tgl.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    Integer umur = calculateAge(birtday, sekarang);
//                    System.out.println("Umur :" + umur + " tahun");
//                    cell.setCellStyle(styleRed);
//                }

                cell = row.createCell(17);
                cell.setCellValue(aps.getTobepresent() + aps.getVisitcustomer() + aps.getLupaabsen() + aps.getTobepresentwithoutumt() + aps.getVisitcustomerharilibur() + aps.getPerjalanandinas());

            }

            progress2 = progress2 + pengali;
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        progress2 = 100.00;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"ResumeAbsensi.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();
    }
    
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
    
    public List<Pegawai> getDataPegawai() {
        return lPegawai;
    }
    
    public void postingAll() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        serviceAbsen.postingAll1(lPegawai, localDate.getMonthValue(), Integer.toString(localDate.getYear()), page.getValueBegawi("umt"));
        createSummary();
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

}
