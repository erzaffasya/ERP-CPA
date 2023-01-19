package net.sra.prime.ultima.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.service.ServiceLaporanStok;
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
public class ControllerReportStok implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<StokBarang> lStokBarangs = new ArrayList<>();
    private StokBarang item;
    private Double totalBesar;
    private Double totalKecil;
    private String uploadedFile;
    private byte[] uploadedData;
    
    
    @Inject
    private Page page;

    @Autowired
    ServiceLaporanStok serviceLaporanStok;

    @PostConstruct
    public void init() {
        item = new StokBarang();

    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            totalBesar = 0.00;
            totalKecil = 0.00;
            if (item.getId_gudang() != "") {
                if(!serviceLaporanStok.cekBlind(item.getId_gudang())){
                    lStokBarangs = new ArrayList<>();
                    context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Untuk sementara gudang ini tidak bisa dilihat stoknya (Mode Blind)!!!! ", ""));
                    return;
                }
                lStokBarangs = serviceLaporanStok.selectStok(item.getId_gudang(), item.getId_satuan_besar(), item.getId_kategori());
                for (int i = 0; i < lStokBarangs.size(); i++) {
                    StokBarang st = lStokBarangs.get(i);
                    st.setStokbesar(lStokBarangs.get(i).getStokkecil() / lStokBarangs.get(i).getIsi_satuan());
                    totalBesar = totalBesar + st.getStokbesar();
                    totalKecil = totalKecil + lStokBarangs.get(i).getStokkecil();
                    lStokBarangs.set(i, st);
                }
            }else{
                lStokBarangs = new ArrayList<>();
            }
        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error !!!!", ""));
        }
    }

    public void onLoadListStokHpp() {
        lStokBarangs = serviceLaporanStok.selectStokHpp(item.getId_gudang());
    }

    public List<StokBarang> getDataStokBarangs() {
        return lStokBarangs;
    }
    
    /**
     * percobaan *
     */
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
            //creating Workbook instance that refers to .xlsx file  
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file  
            int i = 0;
            while (itr.hasNext()) {
                Row row = itr.next();  
                if (i > 0) {
                    //Row row = sheet.getRow(i);
                    Cell cellBarang = row.getCell(2);
                    Cell cellQty = row.getCell(3);
                    System.out.println(i + " --> " + cellBarang.getStringCellValue() + " : " + cellQty.getStringCellValue() );
                }
             i++;   
            }
            
            System.out.println("selesai : " + i);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    /**
     * END Percobaan *
     */

}
