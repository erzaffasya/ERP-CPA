package net.sra.prime.ultima.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.StokValue;
import net.sra.prime.ultima.service.ServiceStokValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerStokValue implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<StokValue> lStokValue = new ArrayList<>();
    private StokValue item;
    private String tahun;
    
    @Inject
    private Page page;

    @Autowired
    ServiceStokValue serviceStokValue;

    @PostConstruct
    public void init() {
//        if (tahun == null) {
//            tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
//        }
        item = new StokValue();
    }

    public void initItem() {
        item = new StokValue();
    }

    public List<StokValue> getDataStokValue() {
        return lStokValue;
    }


    public void posting() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceStokValue.posting(tahun);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    
}
