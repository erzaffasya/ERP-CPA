package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.KomisiSales;
import net.sra.prime.ultima.service.ServiceKomisiSales;
import org.primefaces.component.export.ExcelOptions;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKomisiSales implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<KomisiSales> lKomisiSales = new ArrayList<>();
    private KomisiSales item;
    private Date awal;
    private Date akhir;
    private String idSales;
    private Double total;
    
    
    
    private ExcelOptions excelOpt;
    
    @Autowired
    ServiceKomisiSales serviceKomisiSales;

    @Inject
    private Page page;
    
    
    @PostConstruct
    public void init() {
        total=0.00;
    }

    

    public void onLoadList() {
        lKomisiSales=serviceKomisiSales.OnLoadList(awal, akhir, idSales);
        total=0.00;
        for(int i=0;i < lKomisiSales.size();i++){
            KomisiSales komisiSales = lKomisiSales.get(i);
            if(komisiSales.getNo_pembayaran_piutang() != null){
                komisiSales.setTotal_bayar(komisiSales.getTotal());
                if(komisiSales.getUmur_pembayaran() != null){
                    if(komisiSales.getUmur_pembayaran() > 90){
                        komisiSales.setKomisi(0.00);
                    } else if(komisiSales.getUmur_pembayaran() > 60 && komisiSales.getUmur_pembayaran() <= 90){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.25/100);
                    } else if(komisiSales.getUmur_pembayaran() > 30 && komisiSales.getUmur_pembayaran() <= 60){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.50/100);
                    } else if(komisiSales.getUmur_pembayaran() >= 1 && komisiSales.getUmur_pembayaran() <= 30){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.75/100);
                    } else if(komisiSales.getUmur_pembayaran() <= 0 ){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 1/100);
                    }
                }
            }else{
                komisiSales.setTotal_bayar(0.00);
            }
            total=total+lKomisiSales.get(i).getTotal();
            lKomisiSales.set(i, komisiSales);
        }
    }
    
    public void onLoadListHpp() {
        lKomisiSales=serviceKomisiSales.OnLoadListHpp(awal, akhir, idSales);
        String no_invoice="";
        for(int i=0;i < lKomisiSales.size();i++){
            KomisiSales komisiSales = lKomisiSales.get(i);
            komisiSales.setMargin((komisiSales.getHarga() - komisiSales.getBottomprice()) / komisiSales.getHarga() * 100);
            if(komisiSales.getTotal_bayar() != null){
                // jika invoice sudah dibayar full maka total bayar diisi sesuai dengan hutangnya
                if(komisiSales.getTotal_bayar() >= komisiSales.getAmount()){
                    komisiSales.setBayar(komisiSales.getTotal());
                }else {
                    // jika belum dibayar penuh maka cukup baris ke 1 aja yg diisi dengan total bayar
                   if(!no_invoice.equals(komisiSales.getNo_invoice())){
                       no_invoice = komisiSales.getNo_invoice();
                       komisiSales.setBayar(komisiSales.getTotal_bayar());
                   } 
                }
            }
            lKomisiSales.set(i, komisiSales);
        }
    }
    
    public void onLoadListDsr() {
        lKomisiSales=serviceKomisiSales.OnLoadListDsr(awal, akhir, idSales);
        total=0.00;
        for(int i=0;i < lKomisiSales.size();i++){
            KomisiSales komisiSales = lKomisiSales.get(i);
            if(komisiSales.getNo_pembayaran_piutang() != null){
                komisiSales.setTotal_bayar(komisiSales.getTotal());
                if(komisiSales.getUmur_pembayaran() != null){
                    if(komisiSales.getUmur_pembayaran() > 90){
                        komisiSales.setKomisi(0.00);
                    } else if(komisiSales.getUmur_pembayaran() > 60 && komisiSales.getUmur_pembayaran() <= 90){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.25/100);
                    } else if(komisiSales.getUmur_pembayaran() > 30 && komisiSales.getUmur_pembayaran() <= 60){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.50/100);
                    } else if(komisiSales.getUmur_pembayaran() >= 1 && komisiSales.getUmur_pembayaran() <= 30){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 0.75/100);
                    } else if(komisiSales.getUmur_pembayaran() <= 0 ){
                        komisiSales.setKomisi(komisiSales.getTotal_bayar() * 1/100);
                    }
                }
            }else{
                komisiSales.setTotal_bayar(0.00);
            }
            total=total+lKomisiSales.get(i).getTotal();
            lKomisiSales.set(i, komisiSales);
        }
    }
  
  
  
   public List<KomisiSales> getDataKomisiSales() {
        return lKomisiSales;
    }
   
   
}

