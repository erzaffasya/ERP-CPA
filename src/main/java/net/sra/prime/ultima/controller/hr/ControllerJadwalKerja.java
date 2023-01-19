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
package net.sra.prime.ultima.controller.hr;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import net.sra.prime.ultima.service.hr.ServiceJadwalKerja;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerJadwalKerja implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceJadwalKerja serviceJadwalKerja;
    private JadwalKerja item;
    private List<JadwalKerja> lJadwalKerja = new ArrayList<>();
    private List<JadwalKerjaWaktu> lJadwalKerjaWaktu = new ArrayList<>();
    private DualListModel<Pegawai> pegawai;
    private Integer id;

    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        item = new JadwalKerja();

    }

    public void initItem() {
        item = new JadwalKerja();
        item.setJenis('m');
        item.setCycle(1);
        initJadwalKerjaWaktu();
        List<Pegawai> lPegawaiSource = serviceJadwalKerja.onLoadListPegawai();
        List<Pegawai> lPegawaiTarget = new ArrayList<>();
        pegawai = new DualListModel<Pegawai>(lPegawaiSource, lPegawaiTarget);
    }

    public void initJadwalKerjaWaktu() {
        
        lJadwalKerjaWaktu = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);

        Calendar calOut = Calendar.getInstance();
        calOut.set(Calendar.HOUR_OF_DAY, 15);
        calOut.set(Calendar.MINUTE, 0);

        Calendar calBreak = Calendar.getInstance();
        calBreak.set(Calendar.HOUR_OF_DAY, 12);
        calBreak.set(Calendar.MINUTE, 0);

        Calendar calHoliday = Calendar.getInstance();
        calHoliday.set(Calendar.HOUR_OF_DAY, 0);
        calHoliday.set(Calendar.MINUTE, 0);

        JadwalKerjaWaktu jkw = new JadwalKerjaWaktu();
        if (item.getJenis().equals('m')) {

            jkw.setDay(1);
            jkw.setNama_waktu("Senin");
            jkw.setTime_in(cal.getTime());
            jkw.setTime_out(calOut.getTime());
            jkw.setTime_break(calBreak.getTime());
            jkw.setSelected(true);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(2);
            jkw.setNama_waktu("Selasa");
            jkw.setTime_in(cal.getTime());
            jkw.setTime_out(calOut.getTime());
            jkw.setTime_break(calBreak.getTime());
            jkw.setSelected(true);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(3);
            jkw.setNama_waktu("Rabu");
            jkw.setTime_in(cal.getTime());
            jkw.setTime_out(calOut.getTime());
            jkw.setTime_break(calBreak.getTime());
            jkw.setSelected(true);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(4);
            jkw.setNama_waktu("Kamis");
            jkw.setTime_in(cal.getTime());
            jkw.setTime_out(calOut.getTime());
            jkw.setTime_break(calBreak.getTime());
            jkw.setSelected(true);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(5);
            jkw.setNama_waktu("Jum'at");
            jkw.setTime_in(cal.getTime());
            jkw.setTime_out(calOut.getTime());
            jkw.setTime_break(calBreak.getTime());
            jkw.setSelected(true);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(6);
            jkw.setNama_waktu("Sabtu");
            jkw.setTime_in(calHoliday.getTime());
            jkw.setTime_out(calHoliday.getTime());
            jkw.setTime_break(calHoliday.getTime());
            jkw.setSelected(false);
            lJadwalKerjaWaktu.add(jkw);

            jkw = new JadwalKerjaWaktu();
            jkw.setDay(7);
            jkw.setNama_waktu("MInggu");
            jkw.setTime_in(calHoliday.getTime());
            jkw.setTime_out(calHoliday.getTime());
            jkw.setTime_break(calHoliday.getTime());
            jkw.setSelected(false);
            lJadwalKerjaWaktu.add(jkw);

        } else if (item.getJenis().equals('h')) {
            for (int i = 1; i <= item.getCycle(); i++) {
                jkw = new JadwalKerjaWaktu();
                jkw.setDay(i);
                jkw.setNama_waktu("Hari ke " + i);
                jkw.setTime_in(calHoliday.getTime());
                jkw.setTime_out(calHoliday.getTime());
                jkw.setTime_break(calHoliday.getTime());
                jkw.setSelected(true);
                lJadwalKerjaWaktu.add(jkw);
            }
        }
    }

    public void onLoadList() {
        try {
            lJadwalKerja = serviceJadwalKerja.onLoadList();
            for(int i=0;i<lJadwalKerja.size();i++){
                JadwalKerja jk = lJadwalKerja.get(i);
                DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
                jk.setLabeltanggal("Berlaku mulai tanggal " + tgl.format(jk.getStart_date()));
                //jk.getListJadwalKerjaWaktu().add(serviceJadwalKerja.onLoadListJadwalKerjaWaktu(jk.getId()));
                jk.setListJadwalKerjaWaktu(serviceJadwalKerja.onLoadListJadwalKerjaWaktu(jk.getId()));
                lJadwalKerja.set(i, jk);
            }
                
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<JadwalKerja> getDataJadwalKerja() {
        return lJadwalKerja;
    }

    public List<JadwalKerjaWaktu> getDataJadwalKerjaWaktu() {
        return lJadwalKerjaWaktu;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceJadwalKerja.delete(id);
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
            serviceJadwalKerja.tambah(item, pegawai.getTarget(),lJadwalKerjaWaktu);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void update(String idtunjangan) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + idtunjangan);
    }

    public void onLoad() {
        item = serviceJadwalKerja.onLoad(id);
        List<Pegawai> lPegawaiSource = serviceJadwalKerja.onLoadListPegawai();
        List<Pegawai> lPegawaiTarget = serviceJadwalKerja.onLoadPegawaiFromJadwalKerjaKarywan(item.getId());
        pegawai = new DualListModel<Pegawai>(lPegawaiSource, lPegawaiTarget);
        lJadwalKerjaWaktu = serviceJadwalKerja.onLoadListJadwalKerjaWaktu(id);
    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceJadwalKerja.ubah(item, pegawai.getTarget(),lJadwalKerjaWaktu);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboJenis() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        try {
            map.put("harian", "h");
            map.put("Mingguan", "m");
            //map.put("Bulanan", "b");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
