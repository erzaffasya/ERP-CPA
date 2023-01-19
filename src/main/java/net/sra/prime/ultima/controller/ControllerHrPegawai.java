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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Pegawai;

import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.context.ExternalContext;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Asset;
import net.sra.prime.ultima.entity.hr.Karir;
import net.sra.prime.ultima.entity.hr.KontakDarurat;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.PegawaiPtkp;
import net.sra.prime.ultima.entity.hr.Pendidikan;
import net.sra.prime.ultima.entity.hr.Pkwtt;
import net.sra.prime.ultima.entity.hr.Ptkp;
import net.sra.prime.ultima.entity.hr.Resign;
import net.sra.prime.ultima.entity.hr.SettingBpjs;
import net.sra.prime.ultima.entity.hr.Tanggungan;
import net.sra.prime.ultima.entity.hr.Trainning;
import net.sra.prime.ultima.service.ServicePegawai;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.shaded.commons.io.FilenameUtils;
import org.springframework.core.io.ResourceLoader;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerHrPegawai implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    ServicePegawai servicePegawai;

    private Pegawai item;
    private Karir karir;
    private Resign resign;
    private List<Karir> lKarir;
    private List<Pegawai> lPegawai = new ArrayList<>();
    private String id_perusahaan;
    private List<KontakDarurat> lKontakDarurat;
    private KontakDarurat kontakDarurat;

    private List<Tanggungan> lTanggungan;
    private Tanggungan tanggungan;

    private List<Asset> lAsset;

    private Trainning trainning;
    private List<Trainning> lTrainning;
    private Pendidikan pendidikan;
    private List<Pendidikan> lPendidikan;

    private ExcelOptions excelOpt;

    private PDFOptions pdfOpt;

    private List<Pkwtt> lPkwtt;
    private List<Pkwtt> lProbation;
    private Pkwtt pkwtt;
    private Pkwtt probation;

    private List<PegawaiPtkp> lPegawaiPtkp;
    private PegawaiPtkp pegawaiPtkp;
    private String years;
    private Boolean status;
    private String pinnya;

    private DualListModel<Gudang> gudang;

    @Autowired
    private ResourceLoader resourceLoader;

    @Inject
    private Page page;

    @Inject
    Options options;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @PostConstruct
    public void init() {

        item = new Pegawai();
        karir = new Karir();
        resign = new Resign();
        kontakDarurat = new KontakDarurat();
        tanggungan = new Tanggungan();
        pendidikan = new Pendidikan();
        pkwtt = new Pkwtt();
        probation = new Pkwtt();
        pegawaiPtkp = new PegawaiPtkp();
        status = true;
    }

    public void initItem() {
        item = new Pegawai();
        item.setStatus(Boolean.TRUE);
        status = true;
    }

    public void initItemTanggungan() {
        tanggungan = new Tanggungan();
        tanggungan.setId_pegawai(item.getId_pegawai());
    }

    public void onLoadList() {
        karir = new Karir();
        resign = new Resign();
        lPegawai = servicePegawai.onLoadList(status);

    }

    public void onLoadListSalary() throws IOException {
        lPegawai = new ArrayList<>();
        FacesMessage message = null;
        boolean loggedIn = true;
        if (pinnya != null && servicePegawai.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            if (page.getMyPegawai().getId_departemen_new() == 106) {
                lPegawai = servicePegawai.onLoadListForHr(status);
            } else {
                lPegawai = servicePegawai.onLoadList(status);
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.getExternalContext().redirect("./salary.jsf");
        }
    }

    public void onLoadListPph() throws IOException {
        lPegawai = new ArrayList<>();
        FacesMessage message = null;
        boolean loggedIn = true;
        if (pinnya != null && servicePegawai.CheckPin(page.getName(), pinnya) != null) {
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", page.getMyName());
        } else {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "PIN Salah !!!");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().addCallbackParam("loggedIn", loggedIn);
        if (loggedIn) {
            lPegawai = servicePegawai.onLoadList(status);
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.getExternalContext().redirect("./pph.jsf");
        }
    }

    public List<Pegawai> getDataPegawai() {
        return lPegawai;
    }

    public void delete(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.delete(id);
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
            servicePegawai.tambah(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void tambahTanggungan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            tanggungan.setId_pegawai(item.getId_pegawai());
            servicePegawai.tambahTanggungan(tanggungan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./tanggungan.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./datapribadi.jsf?id=" + item.getId_pegawai());
    }

    public void onLoad() {
        item = servicePegawai.onLoad(item.getId_pegawai());
    }

    public void onLoadDataDiri() {
        if (item.getId_pegawai() == null) {
            item = servicePegawai.onLoadDataDiri(page.getMyPegawai().getId_pegawai());
        } else {
            item = servicePegawai.onLoadDataDiri(item.getId_pegawai());
        }
        String pattern = "dd MMMMM yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        if (item.getTempat_lahir() == null) {
            item.setTempat_lahir("");
        }
        if (item.getTanggal_lahir() != null) {
            item.setTtl(item.getTempat_lahir() + ", " + df.format(item.getTanggal_lahir()));
        } else {
            item.setTtl(item.getTempat_lahir());
        }

        options.setIdProvinsi(item.getProvinsi_ktp());
        options.setIdProvinsiDomisili(item.getProvinsi_domisili());
    }

    public void onLoadMyProfile() {
        item = servicePegawai.onLoadDataDiri(page.getMyPegawai().getId_pegawai());
        String pattern = "dd MMMMM yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        if (item.getTempat_lahir() == null) {
            item.setTempat_lahir("");
        }
        if (item.getTanggal_lahir() != null) {
            item.setTtl(item.getTempat_lahir() + ", " + df.format(item.getTanggal_lahir()));
        } else {
            item.setTtl(item.getTempat_lahir());
        }

        options.setIdProvinsi(item.getProvinsi_ktp());
        options.setIdProvinsiDomisili(item.getProvinsi_domisili());
    }

    public void onLoadHubunganKerja() {
        if (item.getId_pegawai() == null) {
            item = servicePegawai.onLoadHubunganKerja(page.getMyPegawai().getId_pegawai());
        } else {
            item = servicePegawai.onLoadHubunganKerja(item.getId_pegawai());
        }
        Pegawai pegawai = new Pegawai();
        pegawai.setId_pegawai(item.getAtasan_langsung());
        pegawai.setNama(item.getNama_atasan());
        pegawaiAutoComplete.setPegawai(pegawai);
        if (item.getAktifasiumt() != null) {
            item.setStatusumt(true);
        } else {
            item.setStatusumt(false);
        }

        if (item.getAktifasikehadiran() != null) {
            item.setStatuskehadiran(true);
        } else {
            item.setStatuskehadiran(false);
        }

    }

    public void onLoadBpjs() {
        item = servicePegawai.onLoadBpjs(item.getId_pegawai());
    }

    public void onLoadStatusKetenagakerjaan() {
        item = servicePegawai.onLoadHubunganKerja(item.getId_pegawai());
    }

    public void profile() {
        item = servicePegawai.onLoad(page.getMyPegawai().getId_pegawai());
    }

    public void ubahDataPribadi() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getAlamatsama()) {
                item.setAlamat_domisili(item.getAlamat_ktp());
                item.setProvinsi_domisili(item.getProvinsi_ktp());
                item.setKabupaten_domisili(item.getKabupaten_ktp());
            }
            servicePegawai.ubahDataDiri(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./datapribadi.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahHubunganKerja() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahHubunganKerja(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./hubungankerja.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahRincianGaji(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item = ((Pegawai) event.getObject());
        try {
            
            servicePegawai.ubahRincianGaji(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            if (page.getMyPegawai().getId_departemen_new() == 106) {
                lPegawai = servicePegawai.onLoadListForHr(status);
            } else {
                lPegawai = servicePegawai.onLoadList(status);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahPph(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item = ((Pegawai) event.getObject());
        try {
            servicePegawai.ubahPph(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            lPegawai = servicePegawai.onLoadList(status);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void ubahPphRule(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item = ((Pegawai) event.getObject());
        try {
            servicePegawai.ubahPphRule(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            lPegawai = servicePegawai.onLoadList(status);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Pegawai Cancelled", ((Pegawai) event.getObject()).getNama());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

//    public void ubahRincianGaji() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        try {
//            servicePegawai.ubahRincianGaji(item);
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
//            //context.getExternalContext().redirect("./rinciangaji.jsf?id=" + item.getId_pegawai());
//            context.getExternalContext().redirect("./salary.jsf");
//        } catch (Exception e) {
//            e.printStackTrace();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
//        }
//    }
    public void ubahBpjs() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahBpjs(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahStatusKetenagakerjaan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahStatusKetenagakerjaan(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./statusketenagakerjaan.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboPegawai() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Pegawai> list = servicePegawai.onLoadList(true);
            map.put("Pilih Pegawai", "");
            list.stream().forEach((data) -> {
                map.put(data.getNama(), data.getId_pegawai());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

    public void customizationOptions() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F88017");
        excelOpt.setFacetFontSize("10");
        excelOpt.setFacetFontColor("#0000ff");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontColor("#00ff00");
        excelOpt.setCellFontSize("8");

        pdfOpt = new PDFOptions();

        pdfOpt.setFacetBgColor("#F88017");
        pdfOpt.setFacetFontColor("#0000ff");
        pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("12");
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String logo = externalContext.getRealPath("") + File.separator + "resources" + File.separator + "ultima-layout" + File.separator + "images" + File.separator + "avatar1.png";

        pdf.add(Image.getInstance(logo));

    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            String path = new File(".").getCanonicalPath();
            //File file = new File(path + "/img/pegawai/ttd/" + event.getFile().getFileName());
            // File  disimpan di folder /img/pegawai/ttd/ dan namanua diganti sesuai dengan 
            String namafile = item.getId_pegawai() + "." + FilenameUtils.getExtension(event.getFile().getFileName());
            servicePegawai.ubahFoto(item.getId_pegawai(), namafile);
            File file = new File(path + "/target/classes/static/img/pegawai/" + namafile);

            InputStream is = event.getFile().getInputstream();
            OutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            is.close();
            out.close();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Foto berhasil diedit"));
            context.getExternalContext().redirect("./editfoto.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onLoadJabatan() {
        MasterJabatan jbt = servicePegawai.onLoadJabatan(item.getId_jabatan());
    }

    public void onSelectJabatan() {
        Level level = servicePegawai.selectLevel(item.getId_jabatan());
        if (level != null) {
            item.setLevel(level.getLevel());
            item.setGolongan(level.getGolongan());
        }
    }

    public void onLoadProvinsiKtp() {
        options.setIdProvinsi(item.getProvinsi_ktp());
    }

    public void onLoadProvinsiDomisili() {
        options.setIdProvinsiDomisili(item.getProvinsi_domisili());
    }

    public void onLoadAlamatsama() {
        if (item.getAlamatsama()) {
            item.setAlamat_domisili(item.getAlamat_ktp());
            item.setProvinsi_domisili(item.getProvinsi_ktp());
            item.setKabupaten_domisili(item.getKabupaten_ktp());
        }
    }

    //////////// Kontak Darurat ////////////////
    public void initItemKontakDarurat() {
        kontakDarurat = new KontakDarurat();

    }

    public void onLoadListKontakDarurat() {
        onLoadDataDiri();
        if (item.getId_pegawai() == null) {
            lKontakDarurat = servicePegawai.selectAllKontakDarurat(page.getMyPegawai().getId_pegawai());
        } else {
            lKontakDarurat = servicePegawai.selectAllKontakDarurat(item.getId_pegawai());
        }

    }

    public void onLoadListKarir() {
        onLoadDataDiri();
        if (item.getId_pegawai() == null) {
            lKarir = servicePegawai.selectAllKarir(page.getMyPegawai().getId_pegawai());
        } else {
            lKarir = servicePegawai.selectAllKarir(item.getId_pegawai());
        }
    }

    public void onLoadKontakDarurat() {
        item = servicePegawai.onLoad(item.getId_pegawai());
        kontakDarurat = servicePegawai.onLoadKontakDarurat(kontakDarurat.getId());

    }

    public void tambahKontakDarurat() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            kontakDarurat.setId_pegawai(item.getId_pegawai());
            servicePegawai.tambahKontakDarurat(kontakDarurat);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./kontakdarurat.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahKontakDarurat() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            kontakDarurat.setId_pegawai(item.getId_pegawai());
            servicePegawai.ubahKontakDarurat(kontakDarurat);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./kontakdarurat.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void deleteKontakDarurat(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.deleteKontakDarurat(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./kontakdarurat.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public List<KontakDarurat> getDataKontakDarurat() {
        return lKontakDarurat;
    }

    public void updateKontakDarurat(String id_pegawai, Integer id) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./ubahkontakdarurat.jsf?id=" + item.getId_pegawai());
    }

    public List<Tanggungan> getDataTanggungan() {
        return lTanggungan;
    }

    public void onLoadListTanggungan() {
        onLoadDataDiri();
        lTanggungan = servicePegawai.selectAllTanggungan(item.getId_pegawai());
    }

    public void updateTanggungan() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edittanggungan.jsf?id=" + item.getId_pegawai() + "&tg=" + tanggungan.getId());
    }

    public void onloadTanggungan() {
        tanggungan = servicePegawai.selectOneTanggungan(tanggungan.getId());
    }

    public void ubahTanggungan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahTanggungan(tanggungan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./tanggungan.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void deleteTanggungan(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.deleteTanggungan(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            onLoadListTanggungan();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void onloadHitungBpjsKesehatan() {
        SettingBpjs sb = servicePegawai.selectOneSettingBpjs();
        Pegawai pegawaigaji = servicePegawai.selectOnePegawaiGaji(item.getId_pegawai());

        Double gaji = pegawaigaji.getGaji_pokok() + pegawaigaji.getTunjangan_jabatan();
        if (gaji < sb.getUmk()) {
            gaji = sb.getUmk();
        }
        item.setJkk(gaji * sb.getJkk() / 100);
        item.setJkm(gaji * sb.getJkm() / 100);
        item.setJht_perusahaan(gaji * sb.getJht_perusahaan() / 100);
        item.setJht_pekerja(gaji * sb.getJht_pekerja() / 100);
        if (gaji >= sb.getBatas_jp()) {
            item.setJp_perusahaan(sb.getBatas_jp() * sb.getJp_perusahaan() / 100);
            item.setJp_pekerja(sb.getBatas_jp() * sb.getJp_pekerja() / 100);
        } else {
            item.setJp_perusahaan(gaji * sb.getJp_perusahaan() / 100);
            item.setJp_pekerja(gaji * sb.getJp_pekerja() / 100);
        }

        if (gaji >= sb.getKesehatan_max()) {
            item.setKesehatan_perusahaan(sb.getKesehatan_max() * sb.getKesehatan_perusahaan() / 100);
            item.setKesehatan_pekerja(sb.getKesehatan_max() * sb.getKesehatan_pekerja() / 100);
        } else {
            item.setKesehatan_perusahaan(gaji * sb.getKesehatan_perusahaan() / 100);
            item.setKesehatan_pekerja(gaji * sb.getKesehatan_pekerja() / 100);
        }

        // Integer jumlahtanggungan = servicePegawai.jumlahTanggungan(item.getId_pegawai());
    }

    public List<Karir> getDataKarir() {
        return lKarir;
    }

    public void onKarir() {
        karir = new Karir();
        karir.setId_pegawai(item.getId_pegawai());
        karir.setNama(item.getNama());
    }

    public void onResign() {
        resign = new Resign();
        resign.setId_pegawai(item.getId_pegawai());
        resign.setNama(item.getNama());
        resign.setJabatan(item.getJabatan());
        resign.setId_jabatan(item.getId_jabatan_new());
    }

    public void tambahKarir() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.tambahKarir(karir);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            lPegawai = servicePegawai.onLoadList(true);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void tambahResign() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.tambahResign(resign);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            lPegawai = servicePegawai.onLoadList(true);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public List<Asset> getDataAsset() {
        return lAsset;
    }

    public void onLoadListAsset() {
        onLoadDataDiri();
        if (item.getId_pegawai() == null) {
            lAsset = servicePegawai.selectAsset(page.getMyPegawai().getId_pegawai());
        } else {
            lAsset = servicePegawai.selectAsset(item.getId_pegawai());
        }
    }

    ////////////// Trainning /////////////////
    public void initItemTrainning() {
        trainning = new Trainning();

    }

    public void onLoadTrainning() {
        if (item.getId_pegawai() == null) {
            item = servicePegawai.onLoad(page.getMyPegawai().getId_pegawai());
        } else {
            item = servicePegawai.onLoad(item.getId_pegawai());
        }
        trainning = servicePegawai.selectOneTrainning(trainning.getId());

    }

    public void onLoadListTrainning() {
        onLoadDataDiri();
        lTrainning = servicePegawai.selectTrainning(item.getId_pegawai());
    }

    public List<Trainning> getDataTrainning() {
        return lTrainning;
    }

    public void tambahTrainning() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            trainning.setId_pegawai(item.getId_pegawai());
            servicePegawai.tambahTrainning(trainning);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./trainning.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahTrainning() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            trainning.setId_pegawai(item.getId_pegawai());
            servicePegawai.updateTrainning(trainning);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./trainning.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void deleteTrainning(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.deleteTrainning(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./kontakdarurat.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void onDeleteTrainningClicked(Integer id) {
        servicePegawai.deleteTrainning(id);
        onLoadListTrainning();
    }

    public void updateTrainning(Integer id) throws IOException {
        trainning = new Trainning();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edittrainning.jsf?id=" + item.getId_pegawai() + "&idtrainning=" + id);
    }

    ////////////// Pendidikan /////////////////
    public void initItemPendidikan() {
        pendidikan = new Pendidikan();

    }

    public void onLoadListPendidikan() {
        onLoadDataDiri();
        if (item.getId_pegawai() == null) {
            lPendidikan = servicePegawai.selectPendidikan(page.getMyPegawai().getId_pegawai());
        } else {
            lPendidikan = servicePegawai.selectPendidikan(item.getId_pegawai());
        }
    }

    public List<Pendidikan> getDataPendidikan() {
        return lPendidikan;
    }

    public void tambahPendidikan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            pendidikan.setId_pegawai(item.getId_pegawai());
            servicePegawai.tambahPendidikan(pendidikan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./pendidikan.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubahPendidikan() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            pendidikan.setId_pegawai(item.getId_pegawai());
            servicePegawai.updatePendidikan(pendidikan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./pendidikan.jsf?id=" + item.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onDeletePendidikanClicked(Integer id) {
        servicePegawai.deletePendidikan(id);
        onLoadListPendidikan();
    }

    public void updatePendidikan(Integer id) throws IOException {
        pendidikan = new Pendidikan();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./editpendidikan.jsf?id=" + item.getId_pegawai() + "&idpendidikan=" + id);
    }

    public void onLoadPendidikan() {
        item = servicePegawai.onLoad(item.getId_pegawai());
        pendidikan = servicePegawai.selectOnePendidikan(pendidikan.getId());

    }

    //// Pkwtt////
    public void initPkwtt() {
        //pkwtt = new Pkwtt();
        pkwtt.setId_pegawai(item.getId_pegawai());
    }

    public void tambahPkwtt() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            pkwtt.setId_pegawai(item.getId_pegawai());
            servicePegawai.insertPkwtt(pkwtt);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./statusketenagakerjaan.jsf?id=" + pkwtt.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public List<Pkwtt> getDataPkwtt() {
        return lPkwtt;
    }

    public List<Pkwtt> getDataProbation() {
        return lProbation;
    }

    public void onLoadListPkwtt() {
        onLoadDataDiri();
        pkwtt = new Pkwtt();
        lPkwtt = servicePegawai.selectAllPkwtt(item.getId_pegawai(), '1');
        lProbation = servicePegawai.selectAllPkwtt(item.getId_pegawai(), '2');
    }

    public void updatePkwtt() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./editstatusketenagakerjaan.jsf?id_pkwtt=" + pkwtt.getId_pkwtt());
    }

    public void updateProbation() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./editstatusketenagakerjaan.jsf?id_pkwtt=" + probation.getId_pkwtt());
    }

    public void onloadPkwtt() {
        pkwtt = servicePegawai.selectOnePkwtt(pkwtt.getId_pkwtt());
    }

    public void ubahPkwtt() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.updatePkwtt(pkwtt);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./statusketenagakerjaan.jsf?id=" + pkwtt.getId_pegawai());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void deletePkwtt(Integer id, String idPegawai) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.deletePkwtt(id);
            item.setId_pegawai(idPegawai);
            onLoadListPkwtt();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            onLoadListTanggungan();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void ubahPkwt(String idPegawai, Date pkwt) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahPkwt(idPegawai, pkwt);

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "PKWT berhasil diupdate"));

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error PKWT gagal diupdate", ""));
        }
    }

    /*
    Pegawai PTKP
     */
    public List<PegawaiPtkp> getDataPegawaiPtkp() {
        return lPegawaiPtkp;
    }

    public void onloadListCountPegawaiPtkp() {
        lPegawaiPtkp = servicePegawai.countPegawaiPtkpByYears();
    }

    public void onloadListPegawaiPtkp() {
        pegawaiPtkp = new PegawaiPtkp();
        lPegawaiPtkp = servicePegawai.selectAllPegawaiPtkp(years);
    }

    public void comboYearsSelect() {
        if (years != null) {
            lPegawaiPtkp = servicePegawai.selectListPegawaiPtkp(years, Integer.toString(Integer.parseInt(years) - 1));
        }
    }

    public Map<String, String> getComboPtkp() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Ptkp> list = servicePegawai.selectAllPtkp();
            list.stream().forEach((data) -> {
                map.put(data.getPtkp(), Integer.toString(data.getId_ptkp()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void onPtkpSelect(PegawaiPtkp s, Integer i) {
        s.setPtkp(servicePegawai.selectOnePtkp(s.getId_ptkp()).getPtkp());
        lPegawaiPtkp.set(i, s);
    }

    public void tambahPegawaiPtkp() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.tambahPegawaiPtkp(lPegawaiPtkp, years);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./ptkp.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void detailPtkp() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./ptkpdetail.jsf?years=" + pegawaiPtkp.getYears());
    }

    public void deletePegawaiPtkp(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.deletePegawaiPtkpByPegawai("2019", id);
            onloadListPegawaiPtkp();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void onAtasanSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setAtasan_langsung(pegawai.getId_pegawai());
        item.setNama_atasan(pegawai.getNama());

    }

    public void onLoadListHakAksesGudang() {

        List<Gudang> lGudangSource = servicePegawai.onLoadListGudang(item.getId_pegawai());
        List<Gudang> lGudangTarget = servicePegawai.onLoadPegawaiGudang(item.getId_pegawai());
        gudang = new DualListModel<Gudang>(lGudangSource, lGudangTarget);

    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setId_pegawai(pegawai.getId_pegawai());
        item.setNama(pegawai.getNama());
        List<Gudang> lGudangSource = servicePegawai.onLoadListGudang(item.getId_pegawai());
        List<Gudang> lGudangTarget = servicePegawai.onLoadPegawaiGudang(item.getId_pegawai());
        gudang = new DualListModel<Gudang>(lGudangSource, lGudangTarget);
    }

    public void ubahhakAksesGudang() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePegawai.ubahHakAksesGudangs(item.getId_pegawai(), gudang.getTarget());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
}
