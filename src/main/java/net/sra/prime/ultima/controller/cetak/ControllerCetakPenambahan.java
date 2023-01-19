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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.report.beans.CetakLaporan;
import net.sra.prime.ultima.report.manager.ReportManager;
import org.omnifaces.util.Ajax;
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
public class ControllerCetakPenambahan extends CetakLaporan {

    // START STANDAR 
    @Inject
    private Page page;

    @Autowired
    private ResourceLoader resourceLoader;

    
    @Autowired
    private ReportManager reportLaporan;

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

    public void downloadXLSLaporan(Integer nomor) {
        this.nomor = nomor;
        Ajax.oncomplete("executeXLS()");
    }

    public void cetakLaporan() {
        //nomor = "06/002/SPH/CPA-DP/02000205/IX/2017"; // INI nanti hapus saja..
        Ajax.oncomplete("executePDF()");
    }

    // END STANDAR 
    @PostConstruct
    public void awal() {
        System.err.println("coba");
    }

    // private String jRXMLFileName = "master-detail.jrxml";
    private String jRXMLFileName = "penambahan_barang.jrxml";
    // private String nomor = "06/002/SPH/CPA-DP/02000205/IX/2017";
    private Integer nomor;
    

    @Override
    public Map getParameterAndValue() {
        HashMap param = new HashMap();
        param.put("nomor", nomor);
        param.put("nama_perusahaan", page.getMyInternalPerusahaan().getNama());
        return param;
    }

}
