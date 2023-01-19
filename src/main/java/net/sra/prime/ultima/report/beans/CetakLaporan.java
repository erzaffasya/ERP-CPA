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
package net.sra.prime.ultima.report.beans;

import java.io.File;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.report.manager.ReportManager;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Syamsu
 */
@Setter
@Getter
public abstract class CetakLaporan implements java.io.Serializable {

    private static final long serialVersionUID = 4108852696338012943L;

    public abstract void setJRXMLFileName(String xmlFile);

    public abstract String getJRXMLFileName();

    public abstract Map getParameterAndValue();

    public abstract ReportManager getReportManager();

    public abstract ResourceLoader getResourceLoader();

    public abstract String getFolderClasspath();

    public String getReport() {

        ReportManager reportLaporan = getReportManager();
        ResourceLoader resourceLoader = getResourceLoader();

        FacesContext context = FacesContext.getCurrentInstance();
        Map param = getParameterAndValue();
        String report = "";
        try {
            String folder = resourceLoader.getResource(getFolderClasspath()).getFile().getAbsolutePath() + File.separator;
            param.put("SUBREPORT_DIR", folder);
            //if (!reportLaporan.isInit()) {
                reportLaporan.compileReport(resourceLoader.getResource(getFolderClasspath() + getJRXMLFileName()).getInputStream());
            //}
            reportLaporan.generateReport(param);
            report = reportLaporan.showReport();
        } catch (Exception ex) {
            ex.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informasi", "Gagal menampilkan laporan, server sibuk, silahkan coba lagi.."));
        }

        return report;
    }

//    public void downloadSimplePDF() throws Exception {
//        ReportManager reportLaporan = getReportManager();
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
//        String nmdownload = "datajamaah-" + System.currentTimeMillis() + ".pdf";
//
//        ec.responseReset();
//        ec.setResponseContentType("application/pdf");
//        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + nmdownload + "\"");
//
//        reportLaporan.showPDF(ec.getResponseOutputStream());
//        ec.responseFlushBuffer();
//        fc.responseComplete();
//    }
//
//    public void downloadSimpleXLS() throws Exception {
//        ReportManager reportLaporan = getReportManager();
//        FacesContext fc = FacesContext.getCurrentInstance();
//        ExternalContext ec = fc.getExternalContext();
//        String nmdownload = "datajamaah-" + System.currentTimeMillis() + ".xlsx";
//
//        ec.responseReset();
//        ec.setResponseContentType("application/xlsx");
//        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + nmdownload + "\"");
//
//        reportLaporan.showXLS(ec.getResponseOutputStream());
//        ec.responseFlushBuffer();
//        fc.responseComplete();
//    }
}
