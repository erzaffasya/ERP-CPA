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
package net.sra.prime.ultima.controller.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.AccValue;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.service.ServiceLabaRugi;
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

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerLabaRugi implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Inject
    private Options options;

    @Inject
    private Page page;

    @Autowired
    ServiceLabaRugi serviceLabaRugi;

    private Account item;
    private List<Account> lAccount;
    private String tipe, tahun;
    private Integer awal, akhir, mulai, panjang;
    private String id_kantor;

    @PostConstruct
    public void init() {
        item = new Account();
        awal = 1;
        akhir = 12;
        mulai = 1;
        panjang = 1;
        DateFormat thn = new SimpleDateFormat("yyyy");
        tahun = thn.format(new Date());
    }

    public void onLoadList() {
        Double total_pendapatan = 0.00;
        Double total_hpp = 0.00;
        Double total_biaya = 0.00;
        Double total_pendapatan_lain = 0.00;
        Double biaya_lain = 0.00;
        String kode;
        lAccount = new ArrayList<>();
        try {
            // Pendapatan
            if (id_kantor.equals("")) {
                kode = "4";
                mulai = 1;
                panjang = 1;
            } else {
                item = new Account();
                item.setId_account(40100000);
                item.setAccount("PENJUALAN");
                lAccount.add(item);
                kode = "401" + id_kantor;
                mulai = 1;
                panjang = 5;
            }
            List<Account> lAccount1 = serviceLabaRugi.onLoadList(kode, tahun, awal, akhir, mulai, panjang);
            for (int i = 0; i < lAccount1.size(); i++) {
                item = lAccount1.get(i);
                if (item.getDebit() != null) {
                    item.setDebit(item.getDebit() * -1);
                    total_pendapatan = total_pendapatan + item.getDebit();
                }
                if ((item.getDebit() != null && item.getLevel() == 4 && Math.round(item.getDebit() * 100.0) / 100.0 != 0.00) || item.getLevel() != 4) {
                    lAccount.add(item);
                }
            }
            item = new Account();
            item.setAccount("TOTAL PENDAPATAN");
            item.setDebit(total_pendapatan);
            lAccount.add(item);

            lAccount.add(new Account());

            //HPP
            if (id_kantor.equals("")) {
                kode = "5";
                mulai = 1;
                panjang = 1;
            } else {
                item = new Account();
                item.setId_account(50100000);
                item.setAccount("HARGA POKOK PENJUALAN");
                lAccount.add(item);
                kode = "501" + id_kantor;
                mulai = 1;
                panjang = 5;
            }
            lAccount1 = serviceLabaRugi.onLoadList(kode, tahun, awal, akhir, mulai, panjang);
            for (int i = 0; i < lAccount1.size(); i++) {
                item = lAccount1.get(i);
                if (item.getDebit() != null) {
                    total_hpp = total_hpp + item.getDebit();
                }
                if ((item.getDebit() != null && item.getLevel() == 4 && Math.round(item.getDebit() * 100.0) / 100.0 != 0.00) || item.getLevel() != 4) {
                    lAccount.add(item);
                }
            }

            item = new Account();
            item.setAccount("TOTAL HARGA POKOK PENJUALAN");
            item.setDebit(total_hpp);
            lAccount.add(item);

            lAccount.add(new Account());

            item = new Account();
            item.setAccount("GROSS PROFIT");
            item.setDebit(total_pendapatan - total_hpp);
            lAccount.add(item);

            lAccount.add(new Account());

            //Biaya Operasional
            if (id_kantor.equals("")) {
                kode = "6";
                mulai = 1;
                panjang = 1;
            } else {
                kode = "6" + id_kantor;
                mulai = 1;
                panjang = 3;
            }
            lAccount1 = serviceLabaRugi.onLoadList(kode, tahun, awal, akhir, mulai, panjang);
            for (int i = 0; i < lAccount1.size(); i++) {
                item = lAccount1.get(i);
                if (item.getDebit() != null) {
                    total_biaya = total_biaya + item.getDebit();
                }
                if ((item.getDebit() != null && item.getLevel() == 4 && Math.round(item.getDebit() * 100.0) / 100.0 != 0.00) || item.getLevel() != 4) {
                    lAccount.add(item);
                }
            }

            item = new Account();
            item.setAccount("TOTAL BIAYA OPERSIONAL");
            item.setDebit(total_biaya);
            lAccount.add(item);

            lAccount.add(new Account());
            if (id_kantor.equals("") || id_kantor.equals("01")) {
                item = new Account();
                item.setAccount("OPERATING PROFIT");
                item.setDebit(total_pendapatan - total_hpp - total_biaya);
                lAccount.add(item);

                lAccount.add(new Account());

                //Pendapatan Lain-lain
                lAccount1 = serviceLabaRugi.onLoadList("8", tahun, awal, akhir, 1, 1);
                for (int i = 0; i < lAccount1.size(); i++) {
                    item = lAccount1.get(i);
                    if (item.getDebit() != null) {
                        item.setDebit(item.getDebit() * -1);
                        total_pendapatan_lain = total_pendapatan_lain + item.getDebit();
                    }
                    if ((item.getDebit() != null && item.getLevel() == 4 && Math.round(item.getDebit() * 100.0) / 100.0 != 0.00) || item.getLevel() != 4) {
                        lAccount.add(item);
                    }
                }

                item = new Account();
                item.setAccount("TOTAL PENDAPATANA LAIN-LAIN");
                item.setDebit(total_pendapatan_lain);
                lAccount.add(item);

                lAccount.add(new Account());

                //Biaya Lain-lain
                lAccount1 = serviceLabaRugi.onLoadList("9", tahun, awal, akhir, 1, 1);
                for (int i = 0; i < lAccount1.size(); i++) {
                    item = lAccount1.get(i);
                    if (item.getDebit() != null) {
                        biaya_lain = biaya_lain + item.getDebit();
                    }
                    if ((item.getDebit() != null && item.getLevel() == 4 && Math.round(item.getDebit() * 100.0) / 100.0 != 0.00) || item.getLevel() != 4) {
                        lAccount.add(item);
                    }
                }

                item = new Account();
                item.setAccount("TOTAL BIAYA LAIN-LAIN");
                item.setDebit(biaya_lain);
                lAccount.add(item);
            }
            lAccount.add(new Account());

            item = new Account();
            item.setAccount("NETT PROFIT/LOSS");
            item.setDebit(total_pendapatan - total_hpp - total_biaya + total_pendapatan_lain - biaya_lain);
            lAccount.add(item);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Account> getDataAccount() {
        return lAccount;
    }

    public Map<String, String> getComboBulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Bulan", "");
            map.put("Januari", "1");
            map.put("Februari", "2");
            map.put("Maret", "3");
            map.put("April", "4");
            map.put("Mei", "5");
            map.put("Juni", "6");
            map.put("Juli", "7");
            map.put("Agustus", "8");
            map.put("September", "9");
            map.put("Oktober", "10");
            map.put("November", "11");
            map.put("Desember", "12");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public void getReport() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Laba Rugi");
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

            CellStyle styleBoldRightNumber = workbook.createCellStyle();
            styleBoldRightNumber.setFont(font);
            styleBoldRightNumber.setDataFormat(format.getFormat("#,##0"));
            styleBoldRightNumber.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle styleBoldRight = workbook.createCellStyle();
            styleBoldRight.setFont(font);
            styleBoldRight.setAlignment(HorizontalAlignment.RIGHT);

            XSSFCellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat formatDate = workbook.createDataFormat();
            dateCellStyle.setDataFormat(formatDate.getFormat("dd-MM-yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(page.getMyInternalPerusahaan().getNama());
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row  (0-based)
                    0, //first column (0-based)
                    7 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("LAPORAN LABA RUGI");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            DateFormat tgl = new SimpleDateFormat("dd-MM-yyyy"); // Just the year, with 2 digits
            //String tahun = thn.format(date);
            row = sheet.createRow(2);
            cell = row.createCell(0);
            cell.setCellValue("Untuk bulan yang berakhir per  " + options.getNamaBulan(akhir) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            Double labakotor = 0.00;
            Double labakotorsd = 0.00;
            Double labausaha = 0.00;
            Double labausahasd = 0.00;
            Double totalbiayalain = 0.00;
            Double totalbiayalainsd = 0.00;
            Double laba = 0.00;
            Double labasd = 0.00;
            Double penjualan = 0.00;
            Double penjualansd = 0.00;
            Double hpp = 0.00;
            Double hppsd = 0.00;
            Double biayaopersiaonal = 0.00;
            Double biayaoperasionalsd = 0.00;
            Double pendapatanlain = 0.00;
            Double pendapatanlainsd = 0.00;
            Double biayalain = 0.00;
            Double biayalainsd = 0.00;
            AccValue aV = new AccValue();

            Integer rw = 4;

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(options.getNamaBulan(akhir) + "-" + tahun);
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("sd " + options.getNamaBulan(akhir) + "-" + tahun);
            cell.setCellStyle(style);
            rw = rw + 1;

            // ambil dari table account ; Penjualan
            List<Account> lAcc = serviceLabaRugi.selectAccountLevel(40100000, 40200000, 3);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                aV = serviceLabaRugi.onLoadListSum(tahun, akhir, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1000, 'K');
                if (aV != null) {

                    penjualan = penjualan + aV.getDB13();
                    penjualansd = penjualansd + aV.getDB0();
                    row = sheet.createRow(rw);
                    cell = row.createCell(0);
                    cell = row.createCell(1);
                    cell.setCellValue(lAcc.get(i).getAccount());
                    sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                    cell = row.createCell(4);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(aV.getDB13());
                    cell.setCellStyle(style);
                    cell = row.createCell(6);
                    cell.setCellValue(aV.getDB0());
                    cell.setCellStyle(style);
                    rw = rw + 1;

                }
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Total Penjualan");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(penjualan);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(penjualansd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            // ambil dari table account ; Penjualan
            lAcc = serviceLabaRugi.selectAccountLevel(50100000, 50200000, 3);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                aV = serviceLabaRugi.onLoadListSum(tahun, akhir, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 1000, 'D');
                if (aV != null) {

                    hpp = hpp + aV.getDB13();
                    hppsd = hppsd + aV.getDB0();
                    row = sheet.createRow(rw);
                    cell = row.createCell(0);
                    cell = row.createCell(1);
                    cell.setCellValue(lAcc.get(i).getAccount());
                    sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                    cell = row.createCell(4);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(aV.getDB13());
                    cell.setCellStyle(style);
                    cell = row.createCell(6);
                    cell.setCellValue(aV.getDB0());
                    cell.setCellStyle(style);
                    rw = rw + 1;

                }
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Total HPP");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(hpp);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(hppsd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            labakotor = penjualan - hpp;
            labakotorsd = penjualansd - hppsd;
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba Kotor");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(labakotor);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(labakotorsd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            // ambil dari table account ; Biaya
            lAcc = serviceLabaRugi.selectAccountLevel(60000000, 70000000, 2);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                aV = serviceLabaRugi.onLoadListSum(tahun, akhir, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 100000, 'D');
                if (aV != null) {

                    biayaopersiaonal = biayaopersiaonal + aV.getDB13();
                    biayaoperasionalsd = biayaoperasionalsd + aV.getDB0();
                    row = sheet.createRow(rw);
                    cell = row.createCell(0);
                    cell = row.createCell(1);
                    cell.setCellValue(lAcc.get(i).getAccount());
                    sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                    cell = row.createCell(4);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(aV.getDB13());
                    cell.setCellStyle(style);
                    cell = row.createCell(6);
                    cell.setCellValue(aV.getDB0());
                    cell.setCellStyle(style);
                    rw = rw + 1;

                }
            }

            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Total Biaya Operasional");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(biayaopersiaonal);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(biayaoperasionalsd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            labausaha = labakotor - biayaopersiaonal;
            labausahasd = labakotorsd - biayaoperasionalsd;
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba Usaha");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(labausaha);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(labausahasd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            // ambil dari table account ; Pendapatan Lain
            lAcc = serviceLabaRugi.selectAccountLevel(80000000, 90000000, 1);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                aV = serviceLabaRugi.onLoadListSum(tahun, akhir, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 10000000, 'K');
                if (aV != null) {

                    pendapatanlain = pendapatanlain + aV.getDB13();
                    pendapatanlainsd = pendapatanlainsd + aV.getDB0();
                    row = sheet.createRow(rw);
                    cell = row.createCell(0);
                    cell = row.createCell(1);
                    cell.setCellValue(lAcc.get(i).getAccount());
                    sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                    cell = row.createCell(4);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(aV.getDB13());
                    cell.setCellStyle(style);
                    cell = row.createCell(6);
                    cell.setCellValue(aV.getDB0());
                    cell.setCellStyle(style);
                    rw = rw + 1;

                }
            }

            // ambil dari table account ; Pendapatan Lain
            lAcc = serviceLabaRugi.selectAccountLevel(90000000, 91000000, 1);
            for (int i = 0; i < lAcc.size(); i++) {
                //System.out.println(lAcc.get(i).getId_account());
                aV = serviceLabaRugi.onLoadListSum(tahun, akhir, lAcc.get(i).getId_account(), lAcc.get(i).getId_account() + 10000000, 'D');
                if (aV != null) {

                    biayalain = biayalain + aV.getDB13();
                    biayalainsd = biayalainsd + aV.getDB0();
                    row = sheet.createRow(rw);
                    cell = row.createCell(0);
                    cell = row.createCell(1);
                    cell.setCellValue(lAcc.get(i).getAccount());
                    sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
                    cell = row.createCell(4);
                    cell.setCellValue("");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(aV.getDB13());
                    cell.setCellStyle(style);
                    cell = row.createCell(6);
                    cell.setCellValue(aV.getDB0());
                    cell.setCellStyle(style);
                    rw = rw + 1;

                }
            }

            totalbiayalain = biayalain - pendapatanlain;
            totalbiayalainsd = biayalainsd - pendapatanlainsd;
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Total Biaya Lain-Lain");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(totalbiayalain);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(totalbiayalainsd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

            laba = labausaha - totalbiayalain;
            labasd = labausahasd - totalbiayalainsd;
            row = sheet.createRow(rw);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue("Laba Usaha Sebelum Pajak");
            sheet.addMergedRegion(new CellRangeAddress(rw, rw, 1, 3));
            cell.setCellStyle(styleBoldRight);
            cell = row.createCell(4);
            cell.setCellValue("");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue(laba);
            cell.setCellStyle(styleBoldRightNumber);
            cell = row.createCell(6);
            cell.setCellValue(labasd);
            cell.setCellStyle(styleBoldRightNumber);
            rw = rw + 2;

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
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"LabaRugi_" + options.getNamaBulan(akhir) + tahun + ".xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
