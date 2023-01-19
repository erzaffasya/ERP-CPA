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
package net.sra.prime.ultima.controller.cetak;

import net.sra.prime.ultima.controller.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.hr.Payroll;
import net.sra.prime.ultima.report.beans.CetakLaporan;
import net.sra.prime.ultima.report.manager.ReportManager;
import net.sra.prime.ultima.service.hr.ServicePayroll;
import org.omnifaces.util.Ajax;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Syamsu
 */
@Named
@ConversationScoped
@Setter
@Getter
public class ControllerMySalary extends CetakLaporan {

    // START STANDAR 
    @Inject
    private Page page;
    
    @Inject
    private Options options;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ReportManager reportLaporan;

    @Autowired
    private ServicePayroll servicePayroll;

    private String jRXMLFileName = "slipgaji.jrxml";
    private Integer id;
    private Date date;
    private String pinnya;
    private Boolean loggedIn;

    @Override
    public ReportManager getReportManager() {
        return reportLaporan;
    }

    @Override
    public void setJRXMLFileName(String jRXMLFileName) {
        this.jRXMLFileName = jRXMLFileName;
    }

    @Override
    public String getJRXMLFileName() {
        return jRXMLFileName;
    }

    @Override
    public String getFolderClasspath() {
        return "classpath:/static/xml/";
    }

    public void cetakLaporan() {
        Ajax.oncomplete("executePDF()");
    }

    // END STANDAR 
    @PostConstruct
    public void awal() {
        //pinnya=null;
        if (date == null) {
            date = new Date();
        }
    }

    public void onLoadList() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat tgl = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat bulan = new SimpleDateFormat("MM");
        DateFormat tahun = new SimpleDateFormat("yyyy");
        if (pinnya != null && servicePayroll.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            Payroll payroll = servicePayroll.onLoadMySalary(tahun.format(date), Integer.parseInt(bulan.format(date)));
            if (payroll != null) {
                id = payroll.getId();
            } else {
                id = null;
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Slip gaji tidak ada !! ", ""));
            }
            context.getExternalContext().redirect("./mysalary.jsf");
        } else {
            loggedIn = false;
            id = null;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Loggin Error ", "PIN Salah !!!"));
        }

        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);

    }
    
    public void onClear() throws IOException {
        //id=null;
        date=new Date();
    }

    @Override
    public Map getParameterAndValue() {

        HashMap param = new HashMap();
        if (id != null) {
            Payroll item = servicePayroll.onLoadById(id);
            param.put("perusahaan", page.getMyInternalPerusahaan().getNama());
            param.put("idpegawai", page.getMyPegawai().getId_pegawai());
            param.put("id", id);
            param.put("periode", "Periode : " +  options.getNamaBulan(item.getMonth()).toUpperCase() + " " + item.getYear());
        }
        try {
            BufferedImage logo = ImageIO.read(resourceLoader.getResource("classpath:/static/img/cpalogo.jpg").getInputStream());
            param.put("logo", logo);
            BufferedImage logoshell = ImageIO.read(resourceLoader.getResource("classpath:/static/img/shellogo.jpg").getInputStream());
            param.put("shelllogo", logoshell);
        } catch (Exception ex) {

        }
        return param;
    }

}
