package net.sra.prime.ultima.controller;

import au.com.bytecode.opencsv.CSVReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.Pricelist;
import net.sra.prime.ultima.entity.PricelistDetail;
import net.sra.prime.ultima.service.ServicePricelist;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
//import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPricelist implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Pricelist> lPricelist = new ArrayList<>();
    private Pricelist item;
    private PricelistDetail itemdetail;
    private PricelistDetail sP;
    private List<PricelistDetail> lPricelistDetail = new ArrayList<>();
    private String uploadedFile;
    private byte[] uploadedData;
    private String id_gudang;
    private Boolean aktif;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    private ServicePricelist servicePricelist;

    @PostConstruct
    public void init() {
        item = new Pricelist();
        itemdetail = new PricelistDetail();
    }

    public List<Pricelist> getDataPricelist() {
        return lPricelist;
    }

    public List<PricelistDetail> getDataPricelistDetail() {
        return lPricelistDetail;

    }

    public void onLoad() {
        try {
            item = servicePricelist.onLoad(id_gudang);
            if (item != null) {
                lPricelistDetail = servicePricelist.onLoadDetail(item.getId_gudang());
            } else {
                lPricelistDetail = new ArrayList<>();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        UploadedFile argFile = event.getFile();
        uploadedFile = argFile.getFileName();
        uploadedData = argFile.getContents();
        doStuff();
        lPricelistDetail = servicePricelist.onLoadDetail(item.getId_gudang());
    }

    public void doStuff() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            servicePricelist.delete(id_gudang);
            item = new Pricelist();
            item.setId_gudang(id_gudang);
            item.setId_perusahaan(page.getMyKantor().getId_perusahaan());
            item.setUsernamenya(page.getName());
            item.setAktif(Boolean.TRUE);
            servicePricelist.tambah(item);
            CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(uploadedData)), ',', '\"');
            Integer i = 0;
            for (String[] str : reader.readAll()) {
                if (i != 0) {
                    PricelistDetail itemdetail = new PricelistDetail();
                    itemdetail.setId_gudang(item.getId_gudang());
                    itemdetail.setUrut(i);
                    itemdetail.setId_barang(str[1]);
                    itemdetail.setHarga(Double.parseDouble(str[2]));
                    servicePricelist.tambahOneDetail(itemdetail);
                }
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
}
