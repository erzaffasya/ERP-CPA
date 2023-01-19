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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.HrDepartemen;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.Cuti;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;
import net.sra.prime.ultima.entity.hr.CutiMaster;
import net.sra.prime.ultima.entity.hr.CutiPersetujuan;
import net.sra.prime.ultima.entity.hr.CutiSub;
import net.sra.prime.ultima.entity.hr.CutiTahunan;
import net.sra.prime.ultima.service.ServiceHrDepartemen;
import net.sra.prime.ultima.service.ServiceMasterJabatan;
import net.sra.prime.ultima.service.hr.ServiceCuti;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerCutiKaryawan implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceCuti serviceCuti;

    @Autowired
    private ServiceHrDepartemen serviceHrDepartemen;

    @Autowired
    private ServiceMasterJabatan serviceMasterJabatan;
    
    
    private CutiKaryawan item;
    private CutiTahunan cutiTahunan;
    private List<CutiKaryawan> lCuti = new ArrayList<>();
    private List<CutiTahunan> lCutiTahunan = new ArrayList<>();
    private List<CutiKaryawan> lOtherCuti = new ArrayList<>();
    private List<CutiKaryawan> lOtherTimeOff = new ArrayList<>();
    private List<CutiKaryawan> lNotifikasi = new ArrayList<>();
    private List<CutiKaryawan> lSelectedNotifikasi = new ArrayList<>();
    private List<CutiPersetujuan> lCutiPersetujuan = new ArrayList<>();
    private Integer id_jabatan;
    private Integer id_departemen;
    private Integer id_cuti;
    private Character status;
    private Date bulan;
    private String tahun;
    private Boolean statuscancel;
    private Date awal;
    private Date akhir;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @PostConstruct
    public void init() {
        item = new CutiKaryawan();
        cutiTahunan = new CutiTahunan();
        tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
    }

    public void initItem() {
        item = new CutiKaryawan();
        item.setWaktu_cuti('1');
        pegawaiAutoComplete.setPegawai(page.getMyPegawai());
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setNama_pegawai(page.getMyPegawai().getNama());
    }
    
    public void onLoadListCutiTahunan() {
        try {
            lCutiTahunan = serviceCuti.selectAllCutiTahunan(tahun);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    

    public void onLoadList() {
        try {
            if (awal == null) {
                awal = new Date();
            }
            
            if(akhir == null){
                akhir = new Date();
            }

            DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
            CutiKaryawan ct;
            // Uncoment this  for standar
            //lNotifikasi = serviceCuti.onLoadListNotifikasiCutiKaryawan(page.getMyPegawai().getId_pegawai());
            lNotifikasi = serviceCuti.onLoadListNotifikasiCutiKaryawan1(page.getMyPegawai().getId_pegawai());

            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                if (ct.getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan " + ct.getNama_tipe() + " untuk tanggal " + ct.getTanggal());
                lNotifikasi.set(i, ct);
            }

            lCuti = serviceCuti.onLoadListCutiKaryawan(id_departemen, id_jabatan, id_cuti, status, awal, akhir);
            for (int i = 0; i < lCuti.size(); i++) {
                ct = lCuti.get(i);
                if (lCuti.get(i).getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                lCuti.set(i, ct);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void myLoadList() {
        try {

            DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
            CutiKaryawan ct;
            // Uncoment this  for standar

            lCuti = serviceCuti.myTimeOff(tahun, page.getMyPegawai().getId_pegawai());
            lNotifikasi = serviceCuti.onLoadListNotifikasiCutiKaryawan1(page.getMyPegawai().getId_pegawai());
            if (page.getMyPegawai().getId_jabatan_new().equals(109) || page.getMyPegawai().getId_jabatan_new().equals(120) || page.getMyPegawai().getId_jabatan_new().equals(161)) {
                lOtherCuti = serviceCuti.selectAllTimeOffByYear(tahun);
            } else {
                lOtherCuti = serviceCuti.myTimeOffOther(tahun, page.getMyPegawai().getId_pegawai());
                lOtherTimeOff = serviceCuti.otherTimeOff(tahun, page.getMyPegawai().getId_pegawai());
            }

            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                if (ct.getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan " + ct.getNama_tipe() + " untuk tanggal " + ct.getTanggal());
                lNotifikasi.set(i, ct);
            }

            for (int i = 0; i < lCuti.size(); i++) {
                ct = lCuti.get(i);
                if (lCuti.get(i).getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                lCuti.set(i, ct);
            }

            for (int i = 0; i < lOtherCuti.size(); i++) {
                ct = lOtherCuti.get(i);
                if (lOtherCuti.get(i).getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                lOtherCuti.set(i, ct);
            }

            for (int i = 0; i < lOtherTimeOff.size(); i++) {
                ct = lOtherTimeOff.get(i);
                if (lOtherTimeOff.get(i).getWaktu_cuti().equals('1')) {
                    ct.setTanggal(tgl.format(ct.getTanggal_awal()));
                } else {
                    DateFormat hari = new SimpleDateFormat("dd");
                    DateFormat bulan = new SimpleDateFormat("MM");
                    if (bulan.format(ct.getTanggal_awal()).equals(bulan.format(ct.getTanggal_akhir()))) {
                        ct.setTanggal(hari.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    } else {
                        ct.setTanggal(tgl.format(ct.getTanggal_awal()) + " - " + tgl.format(ct.getTanggal_akhir()));
                    }

                }
                lOtherTimeOff.set(i, ct);
            }
            cutiTahunan = serviceCuti.selectCutiTahunan(tahun,page.getMyPegawai().getId_pegawai());
            if(cutiTahunan != null){
            cutiTahunan.setSisa(cutiTahunan.getSaldo() - cutiTahunan.getJumlah());
            }else{
                cutiTahunan = new CutiTahunan();
                cutiTahunan.setSaldo(12);
                cutiTahunan.setJumlah(0);
                cutiTahunan.setSisa(12);
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

    }

    public List<CutiKaryawan> getDataCuti() {
        return lCuti;
    }
    
    public List<CutiTahunan> getDataCutiTahunan() {
        return lCutiTahunan;
    }

    public List<CutiKaryawan> getDataOtherCuti() {
        return lOtherCuti;
    }

    public List<CutiKaryawan> getDataOtherTimeOff() {
        return lOtherTimeOff;
    }

    public List<CutiKaryawan> getDataNotifikasi() {
        return lNotifikasi;
    }

    public List<CutiPersetujuan> getDataCutiPersetujuan() {
        return lCutiPersetujuan;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCuti.deleteCutiKaryawan(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pg = serviceCuti.selectOnePegawai(item.getId_pegawai());
        Cuti ct = serviceCuti.selectOneCutiFromMaster(item.getId_master(), pg.getId_jabatan());
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cuti untuk jabatan " + pg.getJabatan() + "belum dibuat !!!", ""));
        }
        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setId_cuti(ct.getId());
                // Jika cuti ini tidak memerliukan persetujuan makan status diisi T
                if (ct.getPersetujuan1().equals('3')) {
                    item.setStatus('T');
                    // Jika cuti ini memerlukan persetujuan dari jabatan tertentu dan yg menginput cuti karyawan
                    // adalah yg memiliki wewenang untuk melakukan persetujuan tsb makan status diisi T
                } else if (ct.getPersetujuan1().equals('2')) {
                    // jika tidak memerlukan persetujuan kedua dan persetujuan ke 1 adalah yg menginput cuti ini
                    if (ct.getPersetujuan2().equals('1')) {
                        if (page.getMyPegawai().getId_jabatan_new() == ct.getJabatan_persetujuan1()) {
                            item.setStatus('A');
                            item.setKeterangan_status("Tidak perlu persetujuan");
                        } else {
                            item.setStatus('D');
                            item.setKeterangan_status("Menunggu persetujuan");
                        }
                        // jika memerlukan persetujuan kedua dan yang menginput cuti ini adalah yang membuat persetujuan
                    } else if (ct.getPersetujuan2().equals('2')) {
                        // jika yang menginput adalah yg memberikan persetujuan kedua maka tidak perlu ada persetjuan lagi 
                        // status diisi T
                        if (page.getMyPegawai().getId_jabatan_new() == ct.getJabatan_persetujuan2()) {
                            item.setStatus('A');
                            item.setKeterangan_status("Tidak perlu persetujuan");
                            // jika yang menginput adalah yang memberikan persetujuan kesatu maka masih memerlukan     
                        } else if (page.getMyPegawai().getId_jabatan_new() == ct.getJabatan_persetujuan1()) {
                            item.setStatus('W');
                            item.setKeterangan_status("Menunggu persetujuan ke dua");
                        } else {
                            item.setStatus('D');
                            item.setKeterangan_status("Menunggu persetujuan");
                        }
                    }
                }
                serviceCuti.tambahCutiKaryawan(item, ct, pg.getId_departemen_new());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./activity.jsf");
            } catch (Exception e) {
                //e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void addMyTimeOff() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pg = serviceCuti.selectOnePegawai(item.getId_pegawai());
        Cuti ct = serviceCuti.selectOneCutiAllEmployee(item.getId_master());
        Boolean benar = true;
        if (ct == null) {
            ct = serviceCuti.selectOneCutiFromMaster(item.getId_master(), pg.getId_jabatan_new());
            if (ct == null) {
                benar = false;
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cuti untuk jabatan " + pg.getJabatan() + "belum dibuat !!!", ""));
            }
        }
        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setId_cuti(ct.getId());
                item.setStatus('D');
                item.setKeterangan_status("Menunggu persetujuan");
                serviceCuti.addTimeOff(item, ct, pg.getId_departemen_new());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./timeoff.jsf");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void update(String idtunjangan) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./editpengajuan.jsf?id=" + idtunjangan);
    }

    public void onLoad() {
        item = serviceCuti.onLoadCutiKaryawan(item.getId());

    }

    public void onDetilSelect(Integer id) {
        item = serviceCuti.onLoadCutiKaryawan(id);
        lCutiPersetujuan = serviceCuti.selectAllCutiPersetujuan(item.getId());

        Character stAbsen = serviceCuti.statusAbsen(item.getTanggal_awal(), item.getId_pegawai());
        statuscancel = true;
        if (stAbsen != null) {
            if (stAbsen.equals('P')) {
                statuscancel = false;
            }
        }

        DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
        if (item.getWaktu_cuti().equals('1')) {
            item.setTanggal(tgl.format(item.getTanggal_awal()));
        } else {
            DateFormat hari = new SimpleDateFormat("dd");
            DateFormat bulan = new SimpleDateFormat("MM");
            if (bulan.format(item.getTanggal_awal()).equals(bulan.format(item.getTanggal_akhir()))) {
                item.setTanggal(hari.format(item.getTanggal_awal()) + " - " + tgl.format(item.getTanggal_akhir()));
            } else {
                item.setTanggal(tgl.format(item.getTanggal_awal()) + " - " + tgl.format(item.getTanggal_akhir()));
            }

        }

    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceCuti.ubahCutiKaryawan(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public Map<String, String> getComboCutiMaster() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<CutiMaster> list = serviceCuti.onLoadListCutiMaster();
            list.stream().forEach((data) -> {
                map.put(data.getNama_master(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboJenis() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Sehari penuh", "1");
            map.put("hanya pagi", "2");
            map.put("Hanya siang", "3");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void onWaktuCutiSelect() {
        item.setTanggal_awal(item.getTanggal_awal());

    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setId_pegawai(pegawai.getId_pegawai());
        item.setId_jabatan(pegawai.getId_jabatan());
        onTipeCutiSelect();
    }

    public void onTipeCutiSelect() {
        if (item.getId_master() != null && item.getId_jabatan() != null) {
            Cuti ct = serviceCuti.selectOneCutiFromMaster(item.getId_master(), item.getId_jabatan());
            item.setId_cuti(ct.getId());
        }
    }

    public Map<String, String> getComboCutiSub() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<CutiSub> list = serviceCuti.selectAllCutiSub();
            map.put("Pilih salah satu", "");
            list.stream().forEach((data) -> {
                map.put(data.getNama_sub_cuti(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboDepartement() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<HrDepartemen> list1 = serviceHrDepartemen.onLoadList();
            map.put("Semua Departemen", "");
            list1.stream().forEach((data) -> {
                map.put(data.getDepartemen(), Integer.toString(data.getId_departemen()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboJabatan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<MasterJabatan> list = serviceMasterJabatan.onLoadList();
            map.put("Semua jabatan", "");
            list.stream().forEach((data) -> {
                map.put(data.getJabatan(), Integer.toString(data.getId_jabatan()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, String> getComboStatus() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            map.put("Semua cuti", "");
            map.put("Telah disetujui", "1");
            map.put("Telah ditolak", "2");
            map.put("Telah dibatalkan", "3");
            map.put("Menunggu persetujuan", "3");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void persetujuanSelect(Boolean st) {
        String keterangan_status;
        Character statusnya;
        if (st) {
            // ct : adalah pegawai yang malakukan persetujuan terakhir
            CutiPersetujuan ct = serviceCuti.selectMaxCutiPersetujuan(item.getId());
            if (page.getMyPegawai().getId_pegawai().equals(ct.getId_pegawai())) {
                keterangan_status = "Telah disetujui oleh " + page.getMyPegawai().getNama();
                statusnya = 'A';
                //jika cuti tahunan maka update saldo cuti tahunan
                if (item.getId_master().equals(1)) {
                    updateCutiTahunan();
                }
            } else {
                if (ct.getStatus() == null) {
                    keterangan_status = "Menunggu persetujuan";
                    statusnya = 'W';
                } else {
                    keterangan_status = item.getKeterangan_status();
                    statusnya = item.getStatus();
                }
            }
        } else {
            keterangan_status = "Ditolak oleh " + page.getMyPegawai().getNama();
            statusnya = 'R';
        }
        serviceCuti.updateStatusCutiKaryawan(item.getId(), st, page.getMyPegawai().getId_pegawai(), statusnya, keterangan_status, item.getId_pegawai(), item.getId_master());
        myLoadList();
    }

    public void updateCutiTahunan() {

        // cuti 1 hari
        if (item.getWaktu_cuti().equals('1')) {

            Integer jumlahwekend = saturdaysundaycount(item.getTanggal_awal(), item.getTanggal_awal());
            if(serviceCuti.selectHariLibur(item.getTanggal_awal()) == null && jumlahwekend.equals(0)){
                DateFormat tahun = new SimpleDateFormat("YYYY");
                serviceCuti.updateCutiTahunan(tahun.format(item.getTanggal_awal()), item.getId_pegawai(), 1,12);
            }
            
            //cuti beberapa hari    
        } else if (item.getWaktu_cuti().equals('2')) {
            long diff = item.getTanggal_akhir().getTime() - item.getTanggal_awal().getTime();
            int jumlahharicuti = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            jumlahharicuti++;
            int jumlahwekend = saturdaysundaycount(item.getTanggal_awal(), item.getTanggal_akhir());
            
            Date current = item.getTanggal_awal();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.DATE, -1);
            current = calendar.getTime();
            int jmllibur= hariLibur(current, item.getTanggal_akhir());
            int jml= jumlahharicuti - jumlahwekend - jmllibur;
            if(jml > 0){
                DateFormat tahun = new SimpleDateFormat("YYYY");
                serviceCuti.updateCutiTahunan(tahun.format(item.getTanggal_awal()), item.getId_pegawai(), jml,12);
            }
            
        }
    }

    public  int hariLibur(Date start, Date end) {
        Calendar c1 = Calendar.getInstance();
        
        Date current = start;
        int jml = 0;
        while (current.before(end)) {
            if(serviceCuti.selectHariLibur(current) != null){
                c1.setTime(current);
                if(c1.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && c1.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                    jml++;
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }
        return jml;
    }

    public static int saturdaysundaycount(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);

        int sundays = 0;
        int saturday = 0;

        while (!c1.after(c2)) {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                saturday++;
            }
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sundays++;
            }

            c1.add(Calendar.DATE, 1);
        }

        return saturday + sundays;
    }

//    public void persetujuanSelect(Boolean st) {
//        String keterangan_status;
//        Character statusnya;
//        if (st) {
//            if (item.getStatus().equals('D') && serviceCuti.countPersetujuan(item.getId()) == 1) {
//                keterangan_status = "Telah disetujui oleh " + page.getMyPegawai().getNama();
//                statusnya = 'A';
//            } else if (item.getStatus().equals('W') && serviceCuti.countPersetujuan(item.getId()) == 2) {
//                keterangan_status = "Telah disetujui oleh " + serviceCuti.onLoadPersetujuankedua(item.getId(), 1).getNama_pegawai() + " dan " + page.getMyPegawai().getNama();
//                statusnya = 'A';
//            } else {
//                keterangan_status = "Telah disetujui <br> oleh " + page.getMyPegawai().getNama() + " dan menunggu persetujuan dari " + serviceCuti.onLoadPersetujuankedua(item.getId(), 2).getNama_pegawai();
//                statusnya = 'W';
//            }
//        } else {
//            keterangan_status = "Ditolak oleh " + page.getMyPegawai().getNama();
//            statusnya = 'R';
//        }
//        serviceCuti.updateStatusCutiKaryawan(item.getId(), st, page.getMyPegawai().getId_pegawai(), statusnya, keterangan_status,item.getId_pegawai());
//        onLoadList();
//    }
    public void declineSelect() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        serviceCuti.declineCutiKaryawan(item.getId(), 'C', "Telah dibatalkan oleh " + page.getMyPegawai().getNama() + " tanggal " + sdf.format(timestamp));
        myLoadList();
    }

    public Integer searchPersetujuan(Integer id_lembur_karyawan, String id_pegawai) {
        return serviceCuti.searchPersetujuan(id_lembur_karyawan, id_pegawai);
    }

    public Integer searchPersetujuanBerurut(Integer id_lembur_karyawan, String id_pegawai, Integer urut) {
        return serviceCuti.searchPersetujuanBerurut(id_lembur_karyawan, id_pegawai, urut);
    }

    public Character cekCutiPersetujuan(Integer id_cuti_karyawan, String id_pegawai) {
        CutiPersetujuan ck = serviceCuti.cekCutiPersetujuan(id_cuti_karyawan, id_pegawai);
        if (ck != null) {
            if (ck.getStatus() == null) {
                return 'N';
            } else {
                return 'O';
            }
        } else {
            return 'K';
        }
    }

    public void addNote() {
        serviceCuti.addNote(item.getNote(), item.getId());
        myLoadList();
    }
    
    public void ubahCutiTahunan(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        cutiTahunan = ((CutiTahunan) event.getObject());
        try {
            serviceCuti.updateCutiTahunanHr(tahun, cutiTahunan.getId_pegawai(), cutiTahunan.getJumlah(), cutiTahunan.getSaldo());
            onLoadListCutiTahunan();
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", ((Absen) event.getObject()).getKeterangan());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
