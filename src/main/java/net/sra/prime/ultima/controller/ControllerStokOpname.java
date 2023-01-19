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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import net.sra.prime.ultima.admin.Options;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.StokOpname;
import net.sra.prime.ultima.entity.StokOpnameDetil;
import net.sra.prime.ultima.view.input.AccAutoComplete;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import net.sra.prime.ultima.service.ServiceGudang;
import net.sra.prime.ultima.service.ServiceStokOpname;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerStokOpname implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;

    @Inject
    private Page page;

    @Inject
    Options options;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private AccAutoComplete accAutoComplete;

    @Autowired
    ServiceGudang serviceGudang;

    @Autowired
    ServiceStokOpname serviceStokOpname;

    @Autowired
    private ResourceLoader resourceLoader;

    private Gudang item;
    private List<Gudang> lGudang = new ArrayList<>();
    private StokOpname itemso;
    private StokOpnameDetil itemsodetil;
    private List<StokOpname> lSo = new ArrayList<>();
    private List<StokOpnameDetil> lSoDetil = new ArrayList<>();
    private List<StokBarang> lStok = new ArrayList<>();
    private StokBarang itemstok;
    private String uploadedFile;
    private byte[] uploadedData;
    private Date tglmulai, tglselesai;
    private Character status;
    private Boolean blind;

    @PostConstruct
    public void init() {
        //serviceGudang.init();
        item = new Gudang(); //serviceGudang.getItem();
        itemso = new StokOpname();
        //itemsodetil = new StokOpnameDetil();
    }

    public void onLoadList() {
        lGudang = serviceGudang.onLoadList();
    }

    public void onLoadListStokOpname() {
        lSo = serviceStokOpname.selectAllStokOpname(tglmulai, tglselesai, status, page.getMyPegawai().getId_pegawai());
    }

    public void onLoadListStokOpnameAkuntansi() {
        lSo = serviceStokOpname.selectAllStokOpname(tglmulai, tglselesai, status,"akuntansi");
    }

    public void onLoad() {
        try {
            itemso = serviceStokOpname.selectOneStokOpname(itemso.getId());
            blind = serviceGudang.onLoad(itemso.getId_gudang()).getBlind();
            lSoDetil = serviceStokOpname.selectDetilStokOpname(itemso.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoadAkuntansi() {
        try {
            itemso = serviceStokOpname.selectOneStokOpname(itemso.getId());
            lStok = serviceStokOpname.selectAllStokOpnameBarang(itemso.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initItem() {
        item = new Gudang();
        itemso = new StokOpname();
    }

    public List<Gudang> getDataGudang() {
        return lGudang;
    }

    public List<StokOpname> getDataStokOpname() {
        return lSo;
    }

    public List<StokOpnameDetil> getDataStokOpnameDetil() {
        return lSoDetil;
    }

    public List<StokBarang> getDataStok() {
        return lStok;
    }

    public void blind(String id_gudang, Boolean blind) throws IOException {
        item = serviceGudang.onLoad(id_gudang);
        if (item.getBlind() != blind) {
            serviceGudang.blind(id_gudang, blind);
        }
        onLoadList();
    }

    public void onIsUpload(Integer id, Boolean status) {
        serviceStokOpname.onIsUpload(id, status);
        onLoad();
    }

    public void onReloadHpp(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceStokOpname.updateHppFromZero(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Reload HPP Berhasil"));
            context.getExternalContext().redirect("./view.jsf?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Reload Gagal", ""));
        }
    }

    public void onStokOpname(String id_gudang, Boolean x) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (serviceStokOpname.selectCountStokOPname(id_gudang) > 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Stok Opname Gagal dibuat..Gudang ini masih ada proses stok opname yang belum selesai !!!", ""));
            return;
        }
        StokOpname s = new StokOpname();
        s.setId_gudang(id_gudang);
        s.setCreate_by(page.getMyPegawai().getId_pegawai());
        s.setTanggal(new Date());
        s.setStatus('D');
        s.setNomor(nomorurut(id_gudang));
        serviceStokOpname.tambah(s);
        onLoadList();
    }

    public void onApprove(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            StokOpname so = serviceStokOpname.selectOneStokOpname(id);
            if (so.getStatus().equals('A') || so.getStatus().equals('P')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Stok Opname ini sudah diApprove !!", ""));
                return;
            }
            serviceStokOpname.approve(id, page.getMyPegawai().getId_pegawai());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./view.jsf?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal diApprove", ""));
        }
    }

    public String nomorurut(String id_gudang) {
        String nomor = "";
        String kode_gudang = serviceGudang.onLoad(id_gudang).getInisial();
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(new Date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(new Date());
        String bulan = romanMonths[Integer.parseInt(bln.format(new Date())) - 1];
        String noMax = serviceStokOpname.noMax(id_gudang, Integer.parseInt(bulannya), Integer.parseInt(tahun));

        if (noMax == null) {
            nomor = "001/" + page.getMyInternalPerusahaan().getInisial() + "-" + kode_gudang + "/ST/" + bulan + "/" + tahun;
        } else {
            Integer no = Integer.parseInt(noMax);
            no = no + 1;
            noMax = String.format("%03d", no);
            nomor = noMax + "/" + page.getMyInternalPerusahaan().getInisial() + "-" + kode_gudang + "/ST/" + bulan + "/" + tahun;
        }
        return nomor;
    }

    /**
     * percobaan *
     */
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
            Row row = sheet.getRow(2);
            Cell cell = row.getCell(2);
            String nomor = cell.getStringCellValue();
            row = sheet.getRow(4);
            cell = row.getCell(2);
            String idGudang = cell.getStringCellValue();
            StokOpname so = serviceStokOpname.selectOneStokOpname(id);
            if (!nomor.equals(so.getNomor()) || !idGudang.equals(so.getId_gudang())) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nomor StokOpname atau id gudang tidak sama !!", ""));
                return;
            }

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
            int i = 0;
            String id_barang = "";
            String notes = "";
            Double qty_stok = 0.00;
            List<StokOpnameDetil> lDetil = new ArrayList<>();
            StokOpnameDetil detil = new StokOpnameDetil();
            // pengulangan untuk memasukan stok fisik kedalam list stok_opname_detil
            while (itr.hasNext()) {
                row = itr.next();
                if (i > 7) {

                    id_barang = row.getCell(1).getStringCellValue();
                    qty_stok = row.getCell(3).getNumericCellValue();
                    notes = row.getCell(5).getStringCellValue();
                    //cek apakah ini upload ulang
                    if (so.getQtyupload() > 0) {
                        // cek apakah ini yang stok selisih
                        StokOpnameDetil sod = serviceStokOpname.selectOneStokOpnameDetil(id, id_barang);
                        if (sod.getIsreason() && notes == "") {
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Keterangan  Stok yang selisih harus diisi !!", id_barang));
                            return;
                        }
                    }

                    // masukan kedalam list hannya yang ada stok fisiknya
                    //if (qty_stok != null && qty_stok != 0 && id_barang != null) {
                        detil = new StokOpnameDetil();
                        detil.setId(id);
                        detil.setId_barang(id_barang);
                        detil.setQty_stok(qty_stok);
                        detil.setNotes(notes);
                        lDetil.add(detil);

                    //}

                }
                i++;
            }

            serviceStokOpname.updateStokDetil(lDetil, id, page.getMyPegawai().getId_pegawai());
            context.getExternalContext().redirect("./view.jsf?id=" + id);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * END Percobaan *
     */
    public void downloadXls() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream fis = classLoader.getResourceAsStream("static/excel/FormatStokOpname.xlsx");

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

            StokOpname so = serviceStokOpname.selectOneStokOpname(itemso.getId());

            XSSFRow row = sheet.getRow(2);
            XSSFCell cell = row.getCell(2);
            cell.setCellValue(so.getNomor());
            //cell.setCellStyle(cellStyle);

            row = sheet.getRow(3);
            cell = row.getCell(2);
            cell.setCellValue(so.getTanggal());
            cell.setCellStyle(dateCellStyle);

            row = sheet.getRow(4);
            cell = row.getCell(2);
            cell.setCellValue(so.getId_gudang());
            row = sheet.getRow(5);
            cell = row.getCell(2);
            cell.setCellValue(so.getGudang());

            int r = 8;
            List<StokOpnameDetil> sod = serviceStokOpname.selectDetilStokOpname(itemso.getId());
            for (int i = 0; i < sod.size(); i++) {
                row = sheet.createRow(r);
                if (so.getQtyupload() > 0) {
                    if (sod.get(i).getSelisih() > 0 || sod.get(i).getSelisih() < 0) {
                    //if (!sod.get(i).getQty().equals(sod.get(i).getQty_stok())) {
                       cell = row.createCell(0);
                    cell.setCellValue(i + 1);
                    cell.setCellStyle(styleborder1);
                    cell = row.createCell(1);
                    cell.setCellValue(sod.get(i).getId_barang());
                    cell.setCellStyle(styleborder1);
                    cell = row.createCell(2);
                    cell.setCellValue(sod.get(i).getBarang());
                    cell.setCellStyle(styleborder1);
                    cell = row.createCell(3);
                    cell.setCellValue(sod.get(i).getQty_stok()/ sod.get(i).getIsi_satuan());
                    cell.setCellStyle(style1);
                    cell = row.createCell(4);
                    cell.setCellStyle(styleborder1);
                    cell.setCellValue(sod.get(i).getSatuan_besar());
                    cell = row.createCell(5);
                    cell.setCellValue(sod.get(i).getNotes());
                    cell.setCellStyle(styleborder1);
                    } else {
                        cell = row.createCell(0);
                    cell.setCellValue(i + 1);
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(1);
                    cell.setCellValue(sod.get(i).getId_barang());
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(2);
                    cell.setCellValue(sod.get(i).getBarang());
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(3);
                    cell.setCellValue(sod.get(i).getQty_stok()/ sod.get(i).getIsi_satuan());
                    cell.setCellStyle(style);
                    cell = row.createCell(4);
                    cell.setCellStyle(styleborder);
                    cell.setCellValue(sod.get(i).getSatuan_besar());
                    cell = row.createCell(5);
                    cell.setCellValue(sod.get(i).getNotes());
                    cell.setCellStyle(styleborder);
                    }

                } else {
                    cell = row.createCell(0);
                    cell.setCellValue(i + 1);
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(1);
                    cell.setCellValue(sod.get(i).getId_barang());
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(2);
                    cell.setCellValue(sod.get(i).getBarang());
                    cell.setCellStyle(styleborder);
                    cell = row.createCell(3);
                    cell.setCellValue(0);
                    cell.setCellStyle(style);
                    cell = row.createCell(4);
                    cell.setCellStyle(styleborder);
                    cell.setCellValue(sod.get(i).getSatuan_besar());
                    cell = row.createCell(5);
                    cell.setCellValue(sod.get(i).getNotes());
                    cell.setCellStyle(styleborder);
                }

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
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"StokOpname.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadHasilXls() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream fis = classLoader.getResourceAsStream("static/excel/HasilStokOpname.xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            PropertyTemplate pt = new PropertyTemplate();

            CellStyle styleborder = workbook.createCellStyle();
            styleborder.setBorderBottom(BorderStyle.THIN);
            styleborder.setBorderTop(BorderStyle.THIN);
            styleborder.setBorderRight(BorderStyle.THIN);
            styleborder.setBorderLeft(BorderStyle.THIN);

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
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

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
            StokOpname so = serviceStokOpname.selectOneStokOpname(itemso.getId());

            Row row = sheet.getRow(2);
            Cell cell = row.getCell(2);
            cell.setCellValue(so.getNomor());
            //cell.setCellStyle(cellStyle);

            row = sheet.getRow(3);
            cell = row.getCell(2);
            cell.setCellValue(so.getTanggal());
            cell.setCellStyle(dateCellStyle);

            row = sheet.getRow(4);
            cell = row.getCell(2);
            cell.setCellValue(so.getId_gudang());
            row = sheet.getRow(5);
            cell = row.getCell(2);
            cell.setCellValue(so.getGudang());
            int r = 8;
            List<StokOpnameDetil> sod = serviceStokOpname.selectDetilStokOpname(itemso.getId());
            for (int i = 0; i < sod.size(); i++) {
                row = sheet.createRow(r);

                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(styleborder);
                cell = row.createCell(1);
                cell.setCellValue(sod.get(i).getId_barang());
                cell.setCellStyle(styleborder);
                cell = row.createCell(2);
                cell.setCellValue(sod.get(i).getBarang());
                cell.setCellStyle(styleborder);
                cell = row.createCell(3);
                cell.setCellValue(sod.get(i).getQty() / sod.get(i).getIsi_satuan());
                cell.setCellStyle(style);
                cell = row.createCell(4);
                cell.setCellValue(sod.get(i).getQty_stok() / sod.get(i).getIsi_satuan());
                cell.setCellStyle(style);
                cell = row.createCell(5);
                cell.setCellValue(sod.get(i).getSelisih() / sod.get(i).getIsi_satuan());
                cell.setCellStyle(style);
                cell = row.createCell(6);
                cell.setCellStyle(styleborder);
                cell.setCellValue(sod.get(i).getSatuan_besar());
                cell = row.createCell(7);
                cell.setCellStyle(styleborder);
                cell.setCellValue(sod.get(i).getNotes());
                r++;
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
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"HasilStokOpname.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void view() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./view.jsf?id=" + itemso.getId());
    }
    
    public void viewapprove() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./viewapprove.jsf?id=" + itemso.getId());
    }

    public void delete(Integer id, String id_gudang) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            StokOpname so = serviceStokOpname.selectOneStokOpname(id);
            if (so.getStatus().equals('A') || so.getStatus().equals('P')) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Stok Opname ini sudah diApprove !!", ""));
                return;
            }
            serviceStokOpname.delete(id, id_gudang);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus ", ""));
        }

    }
}
