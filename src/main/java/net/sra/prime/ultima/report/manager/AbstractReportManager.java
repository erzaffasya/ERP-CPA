/*
 * Copyright 2016 JoinFaces.
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
package net.sra.prime.ultima.report.manager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 *
 * @author Syamsu
 */
public abstract class AbstractReportManager<E extends Exception> implements ReportManager {

    private int max = 0;

    protected JasperPrint jasperPrint;
    protected boolean init = false;
    protected Integer index = 0;
    protected StringBuffer outBuffer;
    protected SimpleHtmlExporterOutput exporterOutput;
    protected HtmlExporter jrHtmlExporter;
    protected SimpleHtmlReportConfiguration htmlReportConfig;
    protected FacesContext facesContext;
    protected JasperReport jasperReport;

    protected SimpleExporterInput exporterInput;
    protected SimpleHtmlExporterConfiguration htmlConfiguration;

    protected void setMaxPages(int max) {
        this.max = max;
    }

    @Override
    public boolean isInit() {
        return init;
    }

    @Override
    public void reset() {
        init = false;
    }

    @Override
    public void firstPage() {
        index = 0;
    }

    @Override
    public void lastPage() {
        index = max;
    }

    @Override
    public void nextPage() {
        if (index < max) {
            index++;
        }
    }

    @Override
    public void prevPage() {
        if (index > 0) {
            index--;
        }
    }

    protected abstract Map fillParam(Map<Integer, Object> param);

    @Override
    public void compileReport(InputStream xmljasper) throws JRException {
        DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
        JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory",
                "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");
        //facesContext = FacesContext.getCurrentInstance();
        //String is = facesContext.getExternalContext().getRealPath(xmljasper);

        jasperReport = JasperCompileManager.compileReport(xmljasper);

    }

    @Override
    public void generateReport(Map param) throws JRException {
        DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
        JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.xpath.executer.factory",
                "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

        Map parameterMap = fillParam(param);

        jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, getConnection());

        System.out.println(jasperReport.getQuery().getText());

        jrHtmlExporter = new HtmlExporter();
        exporterInput = new SimpleExporterInput(jasperPrint);
        htmlConfiguration = new SimpleHtmlExporterConfiguration();
        htmlReportConfig = new SimpleHtmlReportConfiguration();

        //JRHtmlExporter htmlE = new JRHtmlExporter ();
        htmlConfiguration.setHtmlHeader("");
        htmlConfiguration.setHtmlFooter("");
        htmlConfiguration.setBetweenPagesHtml("");

        htmlConfiguration.setFlushOutput(Boolean.TRUE);
        jrHtmlExporter.setConfiguration(htmlConfiguration);
        jrHtmlExporter.setExporterInput(exporterInput);
        jrHtmlExporter.setConfiguration(htmlReportConfig);

        setMaxPages(jasperPrint.getPages().size() - 1);
    }

    @Override
    public String showReport() throws JRException {
        outBuffer = new StringBuffer();
        exporterOutput = new SimpleHtmlExporterOutput(outBuffer);
        jrHtmlExporter.setExporterOutput(exporterOutput);
        htmlReportConfig.setPageIndex(index);
        jrHtmlExporter.exportReport();
        init = true;
        return outBuffer.toString();
    }

    @Override
    public void showXLS(OutputStream out) throws JRException {
        showXLS(out, new boolean[]{true, true, false});
    }

    @Override
    public void showXLS(OutputStream out, boolean[] options) throws JRException {
        JRXlsxExporter exporterXLS = new JRXlsxExporter();
        SimpleXlsxReportConfiguration xlsxReportConf = new SimpleXlsxReportConfiguration();
        xlsxReportConf.setOnePagePerSheet(options[0]);
        xlsxReportConf.setShowGridLines(options[1]);
        if (options[2]) {
            xlsxReportConf.setPageIndex(index);
        }
        exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporterXLS.setConfiguration(xlsxReportConf);
        exporterXLS.setConfiguration(new SimpleXlsxExporterConfiguration());
        exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        exporterXLS.exportReport();
    }

    @Override
    public void showPDF(OutputStream out) throws JRException {
        showPDF(out, new boolean[]{true, true, false});
    }

    @Override
    public void showPDF(OutputStream out, boolean[] options) throws JRException {
        JRPdfExporter exporterPDF = new JRPdfExporter();
        SimplePdfReportConfiguration pdfReportConf = new SimplePdfReportConfiguration();
        pdfReportConf.setSizePageToContent(options[0]);
        pdfReportConf.setForceSvgShapes(options[1]);
        if (options[2]) {
            pdfReportConf.setPageIndex(index);
        }

        SimplePdfExporterConfiguration pdfExporterconf = new SimplePdfExporterConfiguration();
        pdfExporterconf.setCompressed(true);

        exporterPDF.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporterPDF.setConfiguration(pdfReportConf);
        exporterPDF.setConfiguration(pdfExporterconf);
        exporterPDF.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        exporterPDF.exportReport();
    }

}
