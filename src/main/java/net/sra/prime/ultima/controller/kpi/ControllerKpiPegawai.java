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
package net.sra.prime.ultima.controller.kpi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.Pegawai;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.kpi.KpiPegawai;
import net.sra.prime.ultima.entity.kpi.KpiPegawaiDetail;
import net.sra.prime.ultima.service.kpi.ServiceKpiPegawai;
import org.primefaces.event.SelectEvent;
import java.util.stream.Collectors;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerKpiPegawai implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceKpiPegawai serviceKpiPegawai;
    private KpiPegawai item;
    private List<KpiPegawai> lKpiPegawai = new ArrayList<>();
    private List<KpiPegawaiDetail> lKpiPegawaiDetail = new ArrayList<>();
    private String id_pegawai;
    private String year;
    private Integer month;
    private Double total;
    private Double actual;
    private Double point;
    private Double bobotpoint;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new KpiPegawai();

    }

    public void initItem() {
        item = new KpiPegawai();
        year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
        lKpiPegawaiDetail = new ArrayList<>();
        point = 0.00;
    }

    public void onLoadList() {

        Double jmlBobot = 0.00;
        Double tmpBobot = 0.00;
        Double jmlpoint = 0.00;
        try {
            if (year == null) {
                year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
            }
            if (month == null) {
                month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
            }
            item = serviceKpiPegawai.onLoad(id_pegawai, year, month);
            if (item == null) {
                item = new KpiPegawai();
            }
            lKpiPegawaiDetail = serviceKpiPegawai.onLoadListKpiPegawaiDetail(id_pegawai, year, month);
            calculateTotal();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<KpiPegawai> getDataKpiPegawai() {
        return lKpiPegawai;
    }

    public List<KpiPegawaiDetail> getDataKpiPegawaiDetail() {
        return lKpiPegawaiDetail;
    }

    public void delete(String id_pegawai, String yr) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceKpiPegawai.delete(id_pegawai, yr);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah(Character status) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean tes = true;
        if (status.equals('A')) {
            for (int i = 0; i < lKpiPegawaiDetail.size(); i++) {
                if (month.equals(12)) {
                    if (lKpiPegawaiDetail.get(i).getActual() == null) {
                        tes = false;
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, lKpiPegawaiDetail.get(i).getIndicator() + "nilai Aktual harus diisi !!", ""));
                        break;
                    }
                } else if (month.equals(3) || month.equals(6) || month.equals(9)) {
                    if (!lKpiPegawaiDetail.get(i).getId_unit().equals(5)) {
                        if (lKpiPegawaiDetail.get(i).getActual() == null) {
                            tes = false;
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, lKpiPegawaiDetail.get(i).getIndicator() + " nili Aktual harus diisi !!", ""));
                            break;
                        }
                    }
                } else {
                    if (!lKpiPegawaiDetail.get(i).getId_unit().equals(4) && !lKpiPegawaiDetail.get(i).getId_unit().equals(5)) {
                        if (lKpiPegawaiDetail.get(i).getActual() == null) {
                            tes = false;
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, lKpiPegawaiDetail.get(i).getIndicator() + " nilai Aktual belum diisi !!", ""));
                            break;
                        }
                    }
                }
            }
        }
        if (tes) {
            try {
                item.setPenilai(page.getMyPegawai().getId_pegawai());
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setMonth(month);
                item.setYear(year);
                //item.setTotalpoint(point);
                item.setStatus(status);
                item.setId_pegawai(id_pegawai);

                serviceKpiPegawai.tambah(item, lKpiPegawaiDetail);
                //onLoadList();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public Double getAcummulatedValue(String category) {
        Double jumlah = 0.00;
        total = 0.00;
        Double bobotTmp = 0.00;
        for (int i = 0; i < lKpiPegawaiDetail.size(); i++) {
            if (lKpiPegawaiDetail.get(i).getCategory().equals(category)) {
                jumlah += lKpiPegawaiDetail.get(i).getBobot();
            }
            total += lKpiPegawaiDetail.get(i).getBobot();
        }
        return jumlah;
    }

    public Double getAcummulatedBobotForPoint(String category) {
        Double jumlah = 0.00;
        Double bobotTmp = 0.00;
        for (int i = 0; i < lKpiPegawaiDetail.size(); i++) {
            if (lKpiPegawaiDetail.get(i).getCategory().equals(category)) {
                jumlah += lKpiPegawaiDetail.get(i).getBobot();

                if (month.equals(12)) {
                    bobotTmp += lKpiPegawaiDetail.get(i).getBobot();

                } else if (month.equals(3) || month.equals(6) || month.equals(9)) {
                    if (!lKpiPegawaiDetail.get(i).getId_unit().equals(5)) {
                        bobotTmp += lKpiPegawaiDetail.get(i).getBobot();
                    }
                } else {
                    if (!lKpiPegawaiDetail.get(i).getId_unit().equals(4) && !lKpiPegawaiDetail.get(i).getId_unit().equals(5)) {
                        bobotTmp += lKpiPegawaiDetail.get(i).getBobot();
                    }
                }

            }

        }
        if (bobotTmp.equals(0.00)) {
            return 0.00;
        } else {
            return jumlah / bobotTmp;
        }
    }

    public Double getAcummulatedPoint(String category) {
        Double jumlah = 0.00;

        for (int i = 0; i < lKpiPegawaiDetail.size(); i++) {
            if (lKpiPegawaiDetail.get(i).getCategory().equals(category)) {
                if (lKpiPegawaiDetail.get(i).getPoint() != null) {
                    jumlah += lKpiPegawaiDetail.get(i).getPoint();
                }
            }
        }
        Double bobotTmp = getAcummulatedBobotForPoint(category);

        if (bobotTmp.equals(0.00)) {
            return getAcummulatedValue(category);
        } else {
            return bobotTmp * jumlah;
        }
    }

    public void calculateTotal() {
        point = 0.00;
        Map<String, Long> pointMap1 = lKpiPegawaiDetail.stream().collect(
                Collectors.groupingBy(KpiPegawaiDetail::getCategory, Collectors.counting()));

        for (Map.Entry<String, Long> entry : pointMap1.entrySet()) {
            point += getAcummulatedPoint(entry.getKey());

        }
        item.setTotalpoint(point);
    }

    public void calculateResult(KpiPegawaiDetail detail, Integer i) {
        detail.setPersen(detail.getActual() / detail.getTarget() * 100);
        detail.setPoint((detail.getPersen() / 100) * detail.getBobot());
        lKpiPegawaiDetail.set(i, detail);
        calculateTotal();
    }

    public String getFooterBobot() {
        Double jumlah = 0.00;
        for (int i = 0; i < lKpiPegawaiDetail.size(); i++) {
            if (lKpiPegawaiDetail.get(i).getBobot() != null) {
                jumlah += lKpiPegawaiDetail.get(i).getBobot();
            }
        }
        total = jumlah;
        return new DecimalFormat("###,###.##").format(jumlah);
    }

    public String getFooterPoint() {
        return new DecimalFormat("###,###.##").format(point);
    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        id_pegawai = pegawai.getId_pegawai();
        onLoadList();
    }

}
