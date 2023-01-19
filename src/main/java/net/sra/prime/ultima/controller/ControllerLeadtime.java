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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Leadtime;
import net.sra.prime.ultima.entity.StokOpname;
import net.sra.prime.ultima.entity.Leadtime;
import net.sra.prime.ultima.service.ServiceLeadtime;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
public class ControllerLeadtime implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceLeadtime serviceLeadtime;
    private Leadtime item;
    private List<Leadtime> lLeadtime = new ArrayList<>();
    private String uploadedFile;
    private byte[] uploadedData;

    @PostConstruct
    public void init() {
        item = new Leadtime();

    }

    public void initItem() {
        item = new Leadtime();
    }

    public void onLoadList() {
        try {
            lLeadtime = serviceLeadtime.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Leadtime> getDataLeadtime() {
        return lLeadtime;
    }

    public void delete() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLeadtime.delete(item.getOrigin(), item.getDestination());
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
            serviceLeadtime.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onLoad() {
        item = serviceLeadtime.onLoad(item.getOrigin(), item.getDestination());
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?origin=" + item.getOrigin() +"&destination="+item.getDestination());
    }
        
    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLeadtime.ubah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void handleFileUploadXls(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        UploadedFile argFile = event.getFile();
        uploadedFile = argFile.getFileName();
        uploadedData = argFile.getContents();
        doStuffXls();

    }

    public void doStuffXls() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat tgl = new SimpleDateFormat("MM/dd/yyyy"); // Just the year, with 2 digits
        DateFormat jam = new SimpleDateFormat("HH:mm");

        try {

            InputStream fis = new ByteArrayInputStream(uploadedData);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
            Row row = sheet.getRow(2);

            Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
            int i = 0;
            String origin, destination, region;
            Double normaly, spare, total;

            List<Leadtime> lDetil = new ArrayList<>();
            Leadtime detil = new Leadtime();
            // pengulangan untuk memasukan stok fisik kedalam list stok_opname_detil
            while (itr.hasNext()) {
                row = itr.next();
                if (i > 2) {

                    origin = row.getCell(0).getStringCellValue().toLowerCase();
                    destination = row.getCell(1).getStringCellValue().toLowerCase();
                    region = row.getCell(2).getStringCellValue().toLowerCase();
                    normaly = row.getCell(3).getNumericCellValue();
                    spare = row.getCell(4).getNumericCellValue();
                    total = row.getCell(5).getNumericCellValue();

                    detil = new Leadtime();
                    detil.setOrigin(origin);
                    detil.setDestination(destination);
                    detil.setRegion(region);
                    detil.setNormaly(normaly);
                    detil.setSpare(spare);
                    detil.setTotal(total);
                    lDetil.add(detil);

                }
                i++;
            }
            serviceLeadtime.uploadLeadtime(lDetil);
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
