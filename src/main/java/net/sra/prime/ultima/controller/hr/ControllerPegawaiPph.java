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
import net.sra.prime.ultima.entity.hr.PegawaiPph;
import net.sra.prime.ultima.entity.hr.PegawaiPphDetail;
import net.sra.prime.ultima.service.hr.ServicePegawaiPph;
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
public class ControllerPegawaiPph implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePegawaiPph servicePegawaiPph;
    private PegawaiPph item;
    private PegawaiPphDetail itemdetail;
    private List<PegawaiPph> lPegawaiPph = new ArrayList<>();
    private List<PegawaiPphDetail> lPegawaiPphDetail = new ArrayList<>();
    private Integer month;
    private String year;
    private String pinnya;
    private boolean loggedIn;
    private Boolean tes = false;
    private String keterangan = "Apa pesannya ya ???";
    private Integer hitung;
    private Double total;
    
    @Inject
    private Page page;

    @Inject
    private Options options;

    @PostConstruct
    public void init() {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (year == null) {
            year = Integer.toString(localDate.getYear());
        }
        if (month == null) {
            month = localDate.getMonthValue();
        }
        item = new PegawaiPph();
        lPegawaiPphDetail = new ArrayList<>();

    }

    public void initItem() throws IOException {
        year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        //month =  Calendar.getInstance().get(Calendar.MONTH);
        item = servicePegawaiPph.onLoad(year, month);
        if (item != null) {
            keterangan = "PegawaiPph Periode " + month + " " + year + " sudah pernah diposting !!!";
        }
        
        FacesMessage message = null;

        if (pinnya != null && servicePegawaiPph.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
//        if(item == null){
//            item=new PegawaiPph();
//            item.setStatus('D');
//        }
    }

    
    public void onLoadListPph() throws IOException {
        lPegawaiPphDetail = new ArrayList<>();
        FacesMessage message = null;
        boolean loggedIn = true;
        if (pinnya != null && servicePegawaiPph.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            lPegawaiPphDetail = servicePegawaiPph.onLoadListDetail(year, month);
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.getExternalContext().redirect("./pph.jsf");
        }
    }

    public List<PegawaiPphDetail> getDataPegawaiPphDetail() {
        return lPegawaiPphDetail;
    }
    
    public List<PegawaiPph> getDataPegawaiPph() {
        return lPegawaiPph;
    }
        
    
}
