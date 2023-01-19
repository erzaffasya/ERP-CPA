package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import net.sra.prime.ultima.entity.KartuStok;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.Mutasi;
import net.sra.prime.ultima.service.ServiceReportMutasi;
import net.sra.prime.ultima.view.input.BarangAutoComplete;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerReportMutasi implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<Mutasi> lMutasi = new ArrayList<>();
    private List<KartuStok> lKartuStok = new ArrayList<>();
    private Mutasi item;
    private String gudang;
    private Integer bulan;
    private String tahun;
    private String id_barang;
    private String barang;
    private Date awal;
    private Date akhir;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private Page page;

    @Autowired
    ServiceReportMutasi serviceReportMutasi;

    @PostConstruct
    public void init() {
        item = new Mutasi();
        awal = new Date();
        akhir = new Date();
    }

    public void search() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", item.getId_gudang()));
        context.getExternalContext().redirect("./mutasi.jsf?gudang=" + gudang + "&bulan=" + bulan + "&tahun=" + tahun);
    }

    public void searchKartuStok() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", item.getId_gudang()));
        context.getExternalContext().redirect("./kartustok.jsf?gudang=" + gudang + "&bulan=" + bulan + "&tahun=" + tahun + "&id_barang=" + id_barang);
    }

    public void onLoadList() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (gudang != null && tahun != null && bulan != null) {
            if (!serviceReportMutasi.cekBlind(gudang)) {
                lMutasi = new ArrayList<>();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Untuk sementara gudang ini tidak bisa dilihat Mutasinya (Mode Blind)!!!! ", ""));
                return;
            }

            lMutasi = serviceReportMutasi.onLoadListMutasi(gudang, tahun, bulan);
        }
    }

    public void onLoadListDaily() throws ParseException {
        if (akhir == null) {
            akhir = new Date();
        }
        LocalDate localDate = akhir.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        tahun = Integer.toString(localDate.getYear());
        bulan = localDate.getMonthValue();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date tglmulai = formatter.parse(tahun + "-" + String.format("%02d", bulan) + "-01");
        lMutasi = serviceReportMutasi.stockMovementDaily(gudang, tahun, bulan, tglmulai, akhir);
        Mutasi mutasi = new Mutasi();
        for (int i = 0; i < lMutasi.size(); i++) {
            mutasi = lMutasi.get(i);
            mutasi.setSaldo_akhir((mutasi.getSaldo_awal() + mutasi.getMasuk() - mutasi.getKeluar()));
            lMutasi.set(i, mutasi);
        }
    }

    public void onLoadListKartuStok() throws ParseException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (tahun != null && bulan != null && id_barang != null) {
            if (!serviceReportMutasi.cekBlind(gudang)) {
                lKartuStok = new ArrayList<>();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Untuk sementara gudang ini tidak bisa dilihat stoknya (Mode Blind)!!!! ", ""));
                return;
            }
            barang = serviceReportMutasi.selectBarang(id_barang).getNama_barang();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date tglmulai = formatter.parse(tahun + "-" + String.format("%02d", bulan) + "-01");
            lKartuStok = serviceReportMutasi.onLoadKartuStok(gudang, id_barang, tahun, bulan, Integer.parseInt(tahun), tglmulai);
            Double total = 0.00;
            for (int i = 0; i < lKartuStok.size(); i++) {
                KartuStok kartuStok = lKartuStok.get(i);
                total = total + kartuStok.getDebit() - kartuStok.getKredit();
                kartuStok.setSaldo_akhir(total);
                lKartuStok.set(i, kartuStok);
            }
        }
    }

    public List<Mutasi> getDataMutasi() {
        return lMutasi;
    }

    public List<KartuStok> getDataKartuStok() {
        return lKartuStok;
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

    public void onBarangSelect() {
        try {

            id_barang = barangAutoComplete.getBarang().getId_barang();
            barang = barangAutoComplete.getBarang().getNama_barang();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
