package net.sra.prime.ultima.controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.KategoriCustomer;
import net.sra.prime.ultima.service.ServiceCustomer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class EntriKategoriCustomer implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;

    private KategoriCustomer item;

    @Autowired
    private ServiceCustomer referensi;

    @PostConstruct
    public void init() {
        mode = "tambah";
        initItem();
    }

    private void initItem() {
        item = new KategoriCustomer();
    }

    private String mode;

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            referensi.tambahKategori(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            initItem();
            // context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
