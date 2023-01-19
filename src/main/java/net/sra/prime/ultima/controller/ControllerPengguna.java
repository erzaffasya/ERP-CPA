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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.MapPenggunaCabang;
import net.sra.prime.ultima.entity.MapPenggunaGudang;
import net.sra.prime.ultima.entity.Pegawai;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.service.ServiceKantor;
import net.sra.prime.ultima.service.ServicePegawai;
import net.sra.prime.ultima.service.ServicePengguna;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPengguna implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    ServicePengguna servicePengguna;

    @Autowired
    ServicePegawai servicePegawai;

    @Autowired
    ServiceKantor serviceKantor;

    private Pengguna item;
    private List<Pengguna> lPengguna = new ArrayList<>();
    private MapPenggunaCabang itemCabang;
    private List<MapPenggunaCabang> lPenggunaCabang;
    private MapPenggunaGudang itemGudang;
    private List<MapPenggunaGudang> lPenggunaGudang;
    private String id_perusahaan;
    private UploadedFile file;
    private Character status;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @PostConstruct
    public void init() throws IOException {
        item = new Pengguna();
        itemCabang = new MapPenggunaCabang();
        itemGudang = new MapPenggunaGudang();

    }

    public void initItem() {
        item = new Pengguna();
        Pegawai pegawai = new Pegawai();
        pegawaiAutoComplete.setPegawai(pegawai);
        item.setEnabled(Boolean.TRUE);
        lPenggunaCabang = new ArrayList<>();
        lPenggunaGudang = new ArrayList<>();
        options.setId_perusahaan(id_perusahaan);
    }

    public void onLoadList() {
        try {
            if(status == null){
                status='1';
            }
            lPengguna = servicePengguna.onLoadList(status);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Pengguna> getDataPengguna() {
        return lPengguna;
    }

    public List<MapPenggunaCabang> getDataPenggunaCabang() {
        return lPenggunaCabang;
    }

    public List<MapPenggunaGudang> getDataPenggunaGudang() {
        return lPenggunaGudang;
    }

    public void delete(String usernamenya) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePengguna.delete(usernamenya);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePengguna.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getUsernamenya());
    }

    public void hakAkses() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("../master/hakakses/add.jsf?id=" + item.getUsernamenya());
    }

    public void onLoad() {
        try {
            String id = item.getId_lama();
            item = servicePengguna.onLoad(item.getId_lama());
            item.setId_lama(id);
            Pegawai pegawai = servicePegawai.onLoad(item.getId_pegawai());
            pegawai.setId_pegawai(pegawai.getId_pegawai());
            pegawaiAutoComplete.setPegawai(pegawai);
            item.setJabatan(pegawai.getJabatan());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void onChangePassword() {
        try {
            item = servicePengguna.onLoad(page.getName());
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePengguna.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubahPassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (servicePengguna.CheckPassword(page.getName(), item.getPasswordold()) != null) {
                servicePengguna.updatePasswordPengguna(page.getName(), item.getPasswordnya());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password lama salahhhh !!!!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void ubahPin() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (servicePengguna.CheckPin(page.getName(), item.getPinold()) != null) {
                servicePengguna.updatePinPengguna(page.getName(), item.getPinnya());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "PIN berhasil diganti."));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "PIN lama salahhhh !!!!", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void onPegawaiSelect() {
        item.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        item.setNama(pegawaiAutoComplete.getPegawai().getNama());
        item.setJabatan(pegawaiAutoComplete.getPegawai().getJabatan());
        item.setDepartemen(pegawaiAutoComplete.getPegawai().getDepartemen());
        item.setKantor(pegawaiAutoComplete.getPegawai().getKantor());

    }
    ///////////////// Cabang /////////////////////

    public void extendCabang() {
        itemCabang = new MapPenggunaCabang();
        itemCabang.setId_perusahaan(id_perusahaan);
        itemCabang.setUsernamenya(item.getUsernamenya());
        lPenggunaCabang.add(itemCabang);
    }

    public void onDeleteCabangClicked(MapPenggunaCabang hapus) {
        lPenggunaCabang.remove(hapus);
    }

    public void onCabangSelected() {
        InternalKantorCabang internalKantorCabang = serviceKantor.onLoad(itemCabang.getId_kantor_cabang());
        itemCabang.setCabang(internalKantorCabang.getNama());
    }

    public void upload() {
        if (file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        String namapegawai = item.getId_pegawai();
        String path = "/Users/hairian/Documents/Projects/CPA/CPA/foto/";
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = item.getId_pegawai()
                + event.getFile().getFileName().substring(
                        event.getFile().getFileName().lastIndexOf('.'));
        File file = new File(path + name);

        InputStream is = event.getFile().getInputstream();
        FileOutputStream out = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        is.close();
        out.close();

        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " Berhasil diulpoad !!.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void getReportData() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Daftar Username");
            PropertyTemplate pt = new PropertyTemplate();
            pt.drawBorders(new CellRangeAddress(3, 3, 0, 5), BorderStyle.DOUBLE, BorderExtent.ALL);
            pt.drawBorders(new CellRangeAddress(4, lPengguna.size() + 3, 0, 5), BorderStyle.DOUBLE, BorderExtent.VERTICAL);
            pt.drawBorders(new CellRangeAddress(4, lPengguna.size() + 3, 0, 5), BorderStyle.DOTTED, BorderExtent.HORIZONTAL);
            pt.drawBorders(new CellRangeAddress(lPengguna.size() + 4, lPengguna.size() + 4, 0, 5), BorderStyle.DOUBLE, BorderExtent.ALL);

            CellStyle cellStyle = workbook.createCellStyle();
            
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
                    5 //last column  (0-based)
            ));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue("DAFTAR USERNAME");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));

            row = sheet.createRow(2);
            cell = row.createCell(0);
            //cell.setCellValue("PERIODE : " + options.getNamaBulan(bulan) + " " + tahun);
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue("No");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue("USERNAME");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("NAMA PEGAWAI");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue("JABATAN");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue("DEPARTEMEN");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue("CABANG");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(6);
            cell.setCellValue("STATUS");
            cell.setCellStyle(cellStyle);

            Integer baris = 4;
            //lPengguna = servicePayroll.onloadListDaftarGaji(bulan, tahun);
            for (int i = 0; i < lPengguna.size(); i++) {
                row = sheet.createRow(baris + i);
                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell = row.createCell(1);
                cell.setCellValue(lPengguna.get(i).getUsernamenya());
                cell = row.createCell(2);
                cell.setCellValue(lPengguna.get(i).getNama());
                cell = row.createCell(3);
                cell.setCellValue(lPengguna.get(i).getJabatan());
                //cell.setCellStyle(style);
                cell = row.createCell(4);
                cell.setCellValue(lPengguna.get(i).getDepartemen());
                //cell.setCellStyle(style);
                cell = row.createCell(5);
                cell.setCellValue(lPengguna.get(i).getKantor());
                cell = row.createCell(6);
                if(lPengguna.get(i).getEnabled()){
                cell.setCellValue("Aktif");
                }else{
                    cell.setCellValue("Non Aktif");
                }
                //cell.setCellStyle(style);

            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            //pt.applyBorders(sheet);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/vnd.ms-excel");
            externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Daftar Username.xlsx\"");
            workbook.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();

        } catch (Exception e) {
        }
    }

}
