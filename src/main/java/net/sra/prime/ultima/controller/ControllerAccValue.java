package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.service.ServiceAccValue;
import net.sra.prime.ultima.view.input.AccountAutoComplete;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAccValue implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccValue> lAccValue = new ArrayList<>();
    private AccValue item;
    private Integer bulan;
    private String tahun;
    private Double debit;
    private Double credit;

    @Inject
    private AccountAutoComplete accountAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceAccValue serviceAccValue;

    @PostConstruct
    public void init() {
        bulan = 0;
        if (tahun == null) {
            tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        }
        item = new AccValue();
    }

    public void initItem() {
        item = new AccValue();
    }

    public List<AccValue> getDataAccValue() {
        return lAccValue;
    }

    public void onLoadList() {
        try {
            debit = 0.00;
            credit = 0.00;
            lAccValue = serviceAccValue.onLoadList(tahun);
            for (int i = 0; i < lAccValue.size(); i++) {
                AccValue accValue = lAccValue.get(i);
                switch (bulan) {
                    case 0:
                        accValue.setDebit(accValue.getDB0());
                        accValue.setCredit(accValue.getCR0());
                        break;
                    case 1:
                        accValue.setDebit(accValue.getDB1());
                        accValue.setCredit(accValue.getCR1());
                        break;
                    case 2:
                        accValue.setDebit(accValue.getDB2());
                        accValue.setCredit(accValue.getCR2());
                        break;
                    case 3:
                        accValue.setDebit(accValue.getDB3());
                        accValue.setCredit(accValue.getCR3());
                        break;
                    case 4:
                        accValue.setDebit(accValue.getDB4());
                        accValue.setCredit(accValue.getCR4());
                        break;
                    case 5:
                        accValue.setDebit(accValue.getDB5());
                        accValue.setCredit(accValue.getCR5());
                        break;
                    case 6:
                        accValue.setDebit(accValue.getDB6());
                        accValue.setCredit(accValue.getCR6());
                        break;
                    case 7:
                        accValue.setDebit(accValue.getDB7());
                        accValue.setCredit(accValue.getCR7());
                        break;
                    case 8:
                        accValue.setDebit(accValue.getDB8());
                        accValue.setCredit(accValue.getCR8());
                        break;
                    case 9:
                        accValue.setDebit(accValue.getDB9());
                        accValue.setCredit(accValue.getCR9());
                        break;
                    case 10:
                        accValue.setDebit(accValue.getDB10());
                        accValue.setCredit(accValue.getCR10());
                        break;
                    case 11:
                        accValue.setDebit(accValue.getDB11());
                        accValue.setCredit(accValue.getCR11());
                        break;
                    case 12:
                        accValue.setDebit(accValue.getDB12());
                        accValue.setCredit(accValue.getCR12());
                        break;
                }
                debit = debit + accValue.getDebit();
                credit = credit + accValue.getCredit();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onLoad() {
        item = serviceAccValue.onLoad(tahun, item.getAccount());
        switch (bulan) {
            case 0:
                item.setDebit(item.getDB0());
                item.setCredit(item.getCR0());
                break;
            case 1:
                item.setDebit(item.getDB1());
                item.setCredit(item.getCR1());
                break;
            case 2:
                item.setDebit(item.getDB2());
                item.setCredit(item.getCR2());
                break;
            case 3:
                item.setDebit(item.getDB3());
                item.setCredit(item.getCR3());
                break;
            case 4:
                item.setDebit(item.getDB4());
                item.setCredit(item.getCR4());
                break;
            case 5:
                item.setDebit(item.getDB5());
                item.setCredit(item.getCR5());
                break;
            case 6:
                item.setDebit(item.getDB6());
                item.setCredit(item.getCR6());
                break;
            case 7:
                item.setDebit(item.getDB7());
                item.setCredit(item.getCR7());
                break;
            case 8:
                item.setDebit(item.getDB8());
                item.setCredit(item.getCR8());
                break;
            case 9:
                item.setDebit(item.getDB9());
                item.setCredit(item.getCR9());
                break;
            case 10:
                item.setDebit(item.getDB10());
                item.setCredit(item.getCR10());
                break;
            case 11:
                item.setDebit(item.getDB11());
                item.setCredit(item.getCR11());
                break;
            case 12:
                item.setDebit(item.getDB12());
                item.setCredit(item.getCR12());
                break;
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getAccount() + "&tahun=" + tahun);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }
    
    public void add() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (tahun != null) {
            context.getExternalContext().redirect("./add.jsf?tahun=" + tahun);
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anda Belum Memilih data yang akan diedit", ""));
        }
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            if (item.getDebit() == null || item.getDebit().equals("")) {
                item.setDebit(0.00);
            }
            if (item.getCredit() == null || item.getCredit().equals("")) {
                item.setCredit(0.00);
            }

            serviceAccValue.ubah(bulan, item.getDebit(), item.getAccount(), tahun, 'd');
            serviceAccValue.ubah(bulan, item.getCredit(), item.getAccount(), tahun, 'c');

            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf?tahun=" + tahun);
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void posting() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceAccValue.posting(tahun, bulan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void postingtahun() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceAccValue.postingtahun(tahun);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setYears(tahun);
            serviceAccValue.tambah(item, bulan);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onAccountSelect() {
        item.setAccount(accountAutoComplete.getAccount().getId_account());
        item.setNama_account(accountAutoComplete.getAccount().getAccount());

    }

    public Map<String, String> getComboBulan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Pilih Bulan", "");
            map.put("Januari", "1");
            map.put("Februari", "2");
            map.put("Maret", "3");
            map.put("April", "4");
            map.put("Mei", "5");
            map.put("Juni", "6");
            map.put("Juli", "7");
            map.put("Agustus", "8");
            map.put("September", "9");
            map.put("Oktober", "10");
            map.put("November", "11");
            map.put("Desember", "12");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }

}
