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
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
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
public class ControllerCetakSalesReport extends CetakLaporan {

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

    public void downloadXLSLaporan(String id_gudang, String id_kantor, String tgl_awal, String tgl_akhir) {
        this.id_gudang=id_gudang;
        this.id_kantor=id_kantor;
        this.tgl_awal=tgl_awal;
        this.tgl_akhir=tgl_akhir;
        Ajax.oncomplete("executeXLS()");
    }

    public void cetakLaporan() {
        //nomor = "06/002/SPH/CPA-DP/02000205/IX/2017"; // INI nanti hapus saja..
        Ajax.oncomplete("executePDF()");
    }

    // END STANDAR 
    @PostConstruct
    public void awal() {
        System.err.println("Sale Report");
    }

   // private String jRXMLFileName = "master-detail.jrxml";
    private String jRXMLFileName = "sales_report.jrxml";
    private String id_gudang;
    private String id_kantor;
    private String tgl_akhir;
    private String tgl_awal;
  
    @Override
    public Map getParameterAndValue() {
        HashMap param = new HashMap();
       param.put("id_gudang", id_gudang);
       param.put("id_kantor", id_kantor);
       param.put("tgl_awal", tgl_awal);
       param.put("tgl_akhir", tgl_akhir);
       param.put("nama_perusahaan", page.getMyInternalPerusahaan().getNama());
        return param;
    }

}
