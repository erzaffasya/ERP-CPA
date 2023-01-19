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
package net.sra.prime.ultima.controller;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.BarangMinMax;
import net.sra.prime.ultima.service.ServiceBarang;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerBarang implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    ServiceBarang serviceBarang;
    private Barang item;
    private BarangMinMax itemMinMax;
    private List<Barang> lBarang = new ArrayList<>();
    private List<BarangMinMax> lBarangMinMax = new ArrayList<>();
    private String uploadedFile;
    private byte[] uploadedData;
   
    
    @Inject
    private BarangAutoComplete barangAutoComplete;

    private String id_gudang;

    @PostConstruct
    public void init() {
        item = new Barang();
        itemMinMax = new BarangMinMax();
    }

    public void initItem() {
        item.setStatus(Boolean.TRUE);
    }

    public void initMapping() {
        item = new Barang();
        barangAutoComplete.setBarang(new Barang());
    }

    public void onLoadList() {
        try {
            lBarang = serviceBarang.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Barang> getDataBarang() {
        return lBarang;
    }

    public List<BarangMinMax> getDataMinMAx() {
        return lBarangMinMax;
    }

    public void delete(String id_barang) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {

            serviceBarang.delete(id_barang);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah() throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceBarang.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));

        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_barang());
    }

    public void onLoad() {
        String id = item.getId_lama();
        item = serviceBarang.onLoad(item.getId_lama());
        item.setId_lama(id);
    }

    public void onLoadMapping() {
        item = serviceBarang.onLoad(item.getId_barang());
        barangAutoComplete.setBarang(item);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceBarang.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void cetakdaftarbarang() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetakdaftarbarang.jsf");
    }

    public void onBarangFromSelect() {
        try {
            item.setId_barang(barangAutoComplete.getBarang().getId_barang());
            item.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
            item.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
            item.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
            item.setNama_alias(barangAutoComplete.getBarang().getNama_alias());
            item.setIsi_satuan(barangAutoComplete.getBarang().getIsi_satuan());
            lBarang = new ArrayList<>();
            lBarang = serviceBarang.onLoadListMapping(item.getId_barang());
//            List<String> abarang = new ArrayList<>();
//            abarang.add("500000064");
//            abarang.add("500008280");
//            lBarang = serviceBarang.onLoadListMappingArray(abarang);
            lBarang.add(new Barang());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBarangSelect(Barang s, Integer i) {
        try {
            s.setId_barang(barangAutoComplete.getBarang().getId_barang());
            s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
            s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
            s.setSatuan_besar(barangAutoComplete.getBarang().getSatuan_besar());
            s.setIsi_satuan(barangAutoComplete.getBarang().getIsi_satuan());
            s.setNama_alias(barangAutoComplete.getBarang().getNama_alias());
            serviceBarang.tambahMapping(item.getId_barang(), s.getId_barang());
            lBarang.add(new Barang());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extend() {
        lBarang.add(new Barang());
    }

    public void onDeleteClicked(Barang barang) {
        if (barang.getId_barang() != null) {
            serviceBarang.deleteMapping(item.getId_barang(), barang.getId_barang());
            lBarang.remove(barang);
        }
    }

    public void onLoadListMinMax() {
        try {
            lBarangMinMax = new ArrayList<>();
            lBarangMinMax = serviceBarang.onLoadListMinMax(id_gudang);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onRowEdit(RowEditEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            itemMinMax = ((BarangMinMax) event.getObject());
            if (itemMinMax.getMin() != null && itemMinMax.getMax() != null) {
                serviceBarang.tambahMinMaxPerBarang(itemMinMax, id_gudang);
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));

        }
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", ((BarangMinMax) event.getObject()).getKeterangan());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void downloadXls() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream fis = classLoader.getResourceAsStream("static/excel/MinmaxStok.xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            //PropertyTemplate pt = new PropertyTemplate();

            XSSFColor myColor = new XSSFColor(Color.black);
            XSSFColor warningcolor = new XSSFColor(Color.red);

            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontName("Calibri");
            font.setColor(warningcolor);

            XSSFFont myfont = workbook.createFont();
            myfont.setBold(true);
            myfont.setFontName("Calibri");
            myfont.setColor(myColor);

            XSSFCellStyle styleborder = workbook.createCellStyle();
            styleborder.setBorderBottom(BorderStyle.THIN);
            styleborder.setBorderTop(BorderStyle.THIN);
            styleborder.setBorderRight(BorderStyle.THIN);
            styleborder.setBorderLeft(BorderStyle.THIN);
            styleborder.setFont(myfont);

            XSSFCellStyle styleborder1 = workbook.createCellStyle();
            styleborder1.setBorderBottom(BorderStyle.THIN);
            styleborder1.setBorderTop(BorderStyle.THIN);
            styleborder1.setBorderRight(BorderStyle.THIN);
            styleborder1.setBorderLeft(BorderStyle.THIN);
            styleborder1.setFont(font);

            DataFormat format = workbook.createDataFormat();

            XSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(format.getFormat("#,##0"));
            style.setAlignment(HorizontalAlignment.RIGHT);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFont(myfont);

            XSSFCellStyle style1 = workbook.createCellStyle();
            style1.setDataFormat(format.getFormat("#,##0"));
            style1.setAlignment(HorizontalAlignment.RIGHT);
            style1.setBorderBottom(BorderStyle.THIN);
            style1.setBorderTop(BorderStyle.THIN);
            style1.setBorderRight(BorderStyle.THIN);
            style1.setBorderLeft(BorderStyle.THIN);
            style1.setFont(font);

            XSSFCellStyle dateCellStyle = workbook.createCellStyle();
            XSSFDataFormat formatDate = workbook.createDataFormat();
            dateCellStyle.setDataFormat(formatDate.getFormat("dd-MM-yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.LEFT);

            String gudang = serviceBarang.getGudang(id_gudang);
            XSSFRow row = sheet.getRow(1);
            XSSFCell cell = row.getCell(0);
            cell.setCellValue("MIN MAX STOK BARANG GUDANG " + gudang.toUpperCase());
            //cell.setCellStyle(cellStyle);

            int r = 4;
            List<BarangMinMax> sod = lBarangMinMax;
            for (int i = 0; i < sod.size(); i++) {
                row = sheet.createRow(r);

                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleborder);
                cell = row.createCell(1);
                cell.setCellValue(sod.get(i).getId_barang());
                cell.setCellStyle(styleborder);
                cell = row.createCell(2);
                cell.setCellValue(sod.get(i).getNama_barang());
                cell.setCellStyle(styleborder);
                cell = row.createCell(3);
                cell.setCellValue(sod.get(i).getNama_alias());
                cell.setCellStyle(styleborder);
                cell = row.createCell(4);
                if (sod.get(i).getMin() != null) {
                    cell.setCellValue(sod.get(i).getMin());
                } else {
                    cell.setCellValue(0);
                }
                cell.setCellStyle(style);
                cell = row.createCell(5);
                if (sod.get(i).getMax() != null) {
                    cell.setCellValue(sod.get(i).getMax());
                } else {
                    cell.setCellValue(0);
                }

                cell.setCellStyle(style);

                cell = row.createCell(6);
                cell.setCellStyle(styleborder);
                cell.setCellValue(sod.get(i).getSatuan_besar());
                r++;
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"MinmaxStok_" + gudang + ".xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void handleFileUploadXls(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        UploadedFile argFile = event.getFile();
        uploadedFile = argFile.getFileName();
        uploadedData = argFile.getContents();
        Integer id = (Integer) event.getComponent().getAttributes().get("idnya");
        doStuffXls(id);

    }
    
    public void doStuffXls(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat tgl = new SimpleDateFormat("MM/dd/yyyy"); // Just the year, with 2 digits
        DateFormat jam = new SimpleDateFormat("HH:mm");

        try {

            InputStream fis = new ByteArrayInputStream(uploadedData);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
            Row row = sheet.getRow(1);
            

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
            int i = 0;
            String id_barang = "";
            String notes = "";
            Double min = 0.00;
            Double max = 0.00;
            List<BarangMinMax> lDetil = new ArrayList<>();
            BarangMinMax detil = new BarangMinMax();
            while (itr.hasNext()) {
                row = itr.next();
                if (i > 3) {

                    id_barang = row.getCell(1).getStringCellValue();
                    min = row.getCell(4).getNumericCellValue();
                    max = row.getCell(5).getNumericCellValue();

                    // masukan kedalam list hannya yang ada stok min maxnya
                    if (id_barang != null && ((min != 0 && min != 0) || (max != null || max != 0))) {
                        detil = new BarangMinMax();
                        detil.setId_gudang(id_gudang);
                        detil.setId_barang(id_barang);
                        detil.setMin(min);
                        detil.setMax(max);
                        lDetil.add(detil);

                    }

                }
                i++;
            }
            serviceBarang.tambahMinMax(lDetil, id_gudang);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
