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
import net.sra.prime.ultima.entity.HrDepartemen;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Lembur;
import net.sra.prime.ultima.entity.hr.LemburKaryawan;
import net.sra.prime.ultima.entity.hr.LemburPersetujuan;
import net.sra.prime.ultima.service.ServiceHrDepartemen;
import net.sra.prime.ultima.service.ServiceMasterJabatan;
import net.sra.prime.ultima.service.hr.ServiceLembur;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
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
public class ControllerLemburKaryawan implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceLembur serviceLembur;

    @Autowired
    private ServiceHrDepartemen serviceHrDepartemen;

    @Autowired
    private ServiceMasterJabatan serviceMasterJabatan;

    private LemburKaryawan item;
    private List<LemburKaryawan> lLembur = new ArrayList<>();
    private List<LemburKaryawan> lOtherLembur = new ArrayList<>();
    private List<LemburKaryawan> lNotifikasi = new ArrayList<>();
    private List<LemburKaryawan> lSelectedNotifikasi = new ArrayList<>();
    private List<LemburPersetujuan> lLemburPersetujuan = new ArrayList<>();
    private Integer id_jabatan;
    private Integer id_departemen;
    private Integer id_lembur;
    private Character status;
    private Date bulan;
    private String tahun;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @PostConstruct
    public void init() {
        item = new LemburKaryawan();
        tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
    }

    public void initItem() {
        item = new LemburKaryawan();
        item.setTanggal(new Date());
        pegawaiAutoComplete.setPegawai(page.getMyPegawai());
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setNama_pegawai(page.getMyPegawai().getNama());
        item.setRevisi(0);
    }

    public void onLoadList() {
        try {
            if (bulan == null) {
                bulan = new Date();
            }

            DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
            LemburKaryawan ct;
            lNotifikasi = serviceLembur.onLoadListNotifikasiLemburKaryawan(page.getMyPegawai().getId_pegawai());
            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan lembur untuk tanggal " + tgl.format(ct.getTanggal()));
                lNotifikasi.set(i, ct);
            }

            lLembur = serviceLembur.onLoadListLemburKaryawan(id_departemen, id_jabatan, id_lembur, status, bulan);
            for (int i = 0; i < lLembur.size(); i++) {
                ct = lLembur.get(i);
                lLembur.set(i, ct);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void onLoadMyList() {
        try {
            if (bulan == null) {
                bulan = new Date();
            }
            DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
            LemburKaryawan ct;
            lNotifikasi = serviceLembur.onLoadListNotifikasiLemburKaryawan(page.getMyPegawai().getId_pegawai());
            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan lembur untuk tanggal " + tgl.format(ct.getTanggal()));
                lNotifikasi.set(i, ct);
            }
            lLembur = serviceLembur.onLoadMyListOvertime(tahun, page.getMyPegawai().getId_pegawai());
            lOtherLembur = serviceLembur.onLoadOtherOvertime(tahun, page.getMyPegawai().getId_pegawai());
//            for (int i = 0; i < lLembur.size(); i++) {
//                ct = lLembur.get(i);
//                lLembur.set(i, ct);
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<LemburKaryawan> getDataLembur() {
        return lLembur;
    }
    
    public List<LemburKaryawan> getDataOtherLembur() {
        return lOtherLembur;
    }

    public List<LemburPersetujuan> getDataLemburPersetujuan() {
        return lLemburPersetujuan;
    }

    public List<LemburKaryawan> getDataNotifikasi() {
        return lNotifikasi;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceLembur.deleteLemburKaryawan(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pg = serviceLembur.selectOnePegawai(item.getId_pegawai());
        Lembur ct = serviceLembur.selectOneLemburFromMaster(pg.getId_jabatan_new());
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Lembur untuk jabatan " + pg.getJabatan() + "belum dibuat !!!", ""));
        }

        if (item.getHour_before() == null && item.getHour_after() == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jam lembur belum diisi !!!", ""));
        }

        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setId_lembur(ct.getId());
                if (item.getHour_before() == null) {
                    item.setHour_before(0);
                }

                if (item.getHour_after() == null) {
                    item.setHour_after(0);
                }
                // Jika cuti ini tidak memerliukan persetujuan maka status diisi T
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
                serviceLembur.tambahLemburKaryawan(item, ct, pg.getId_departemen_new());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                if (item.getId_pegawai().equals(page.getMyPegawai().getId_pegawai())) {
                    context.getExternalContext().redirect("/hr/calender/overtime.jsf");
                } else {
                    context.getExternalContext().redirect("./activity.jsf");
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    public void addMyOverTime() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pg = serviceLembur.selectOnePegawai(item.getId_pegawai());
        Lembur ct = serviceLembur.selectOneLemburFromMaster(pg.getId_jabatan_new());
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Lembur untuk jabatan " + pg.getJabatan() + "belum dibuat !!!", ""));
        }

        LemburKaryawan lemburKaryawan = serviceLembur.checkOvertime(item.getTanggal(), item.getId_pegawai());
        if(lemburKaryawan != null){
            benar = false;
            DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, item.getNama_pegawai() + " kamu sudah mengajukan lembur tanggal " + tgl.format(item.getTanggal()) + " dengan Nomor " + lemburKaryawan.getNomor(), ""));
        }
        
        if (item.getHour_before() == null && item.getHour_after() == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jam lembur belum diisi !!!", ""));
        }

        if (benar) {
            try {
                nomorurut();
                item.setStatus('D');
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                item.setId_lembur(ct.getId());
                if (item.getHour_before() == null) {
                    item.setHour_before(0);
                }

                if (item.getHour_after() == null) {
                    item.setHour_after(0);
                }
                
                if (item.getBreak_time()== null) {
                    item.setBreak_time(0);
                }
                
                item.setStatus('D');
                item.setKeterangan_status("Menunggu Persetujuan");
                serviceLembur.addMyOvertime(item, ct, pg.getId_departemen_new());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("/hr/calender/overtime.jsf");

            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }
    
    
    public void addMyRevisi() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean benar = true;
        if (item.getHour_before() == null && item.getHour_after() == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jam lembur belum diisi !!!", ""));
        }

        if (benar) {
            try {
                
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                if (item.getHour_before() == null) {
                    item.setHour_before(0);
                }

                if (item.getHour_after() == null) {
                    item.setHour_after(0);
                }
                
                if (item.getBreak_time()== null) {
                    item.setBreak_time(0);
                }
                
                item.setStatus('D');
                item.setKeterangan_status("Menunggu Persetujuan");
                serviceLembur.addMyRevisi(item);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("/hr/calender/overtime.jsf");

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
        item = serviceLembur.onLoadLemburKaryawan(item.getId(),item.getRevisi());
        item.setNomor(item.getNomor() + "-Rev" + (item.getRevisi() + 1));
        item.setRevisi(item.getRevisi() + 1);

    }

    public void onDetilSelect(Integer id,Integer revisi) {
        item = serviceLembur.onLoadLemburKaryawan(id,revisi);
        lLemburPersetujuan = serviceLembur.selectAllLemburPersetujuan(id,revisi);

    }

//    public void ubah() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        try {
//            serviceLembur.ubahLemburKaryawan(item);
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
//            context.getExternalContext().redirect("./list.jsf");
//        } catch (Exception e) {
//            e.printStackTrace();
//            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
//        }
//    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setId_pegawai(pegawai.getId_pegawai());
        //item.setId_jabatan(pegawai.getId_jabatan());
        //onTipeLemburSelect();
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
            map.put("Telah disetujui", "A");
            map.put("Telah ditolak", "R");
            map.put("Telah dibatalkan", "C");
            map.put("Menunggu persetujuan", "W");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public void persetujuanSelect(Boolean st) {
        String keterangan_status;
        Character statusnya;
        LemburPersetujuan ct = serviceLembur.selectMaxLemburPersetujuan(item.getId());
        if (st) {
            if (page.getMyPegawai().getId_pegawai().equals(ct.getId_pegawai())) {
                keterangan_status = "Telah disetujui oleh " + page.getMyPegawai().getNama();
                statusnya = 'A';

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
        serviceLembur.updateStatusLemburKaryawan(item.getId(), st, page.getMyPegawai().getId_pegawai(), statusnya, keterangan_status, item.getId_pegawai(),item.getRevisi());
        onLoadMyList();
    }

    /**
     * peretujuan dengan 2 level public void persetujuanSelect(Boolean st) {
     * String keterangan_status; Character statusnya; if (st) { if
     * (item.getStatus().equals('D') &&
     * serviceLembur.countPersetujuan(item.getId()) == 1) { keterangan_status =
     * "Telah disetujui oleh " + page.getMyPegawai().getNama(); statusnya = 'A';
     * }else if (item.getStatus().equals('W') &&
     * serviceLembur.countPersetujuan(item.getId()) == 2) { keterangan_status =
     * "Telah disetujui oleh " +
     * serviceLembur.onLoadPersetujuankedua(item.getId(), 1).getNama_pegawai() +
     * " dan " + page.getMyPegawai().getNama(); statusnya = 'A'; } else {
     * keterangan_status = "Telah disetujui oleh " +
     * page.getMyPegawai().getNama() + " dan menunggu persetujuan dari " +
     * serviceLembur.onLoadPersetujuankedua(item.getId(), 2).getNama_pegawai();
     * statusnya = 'W'; } } else { keterangan_status = "Ditolak oleh " +
     * page.getMyPegawai().getNama(); statusnya = 'R'; }
     * serviceLembur.updateStatusLemburKaryawan(item.getId(), st,
     * page.getMyPegawai().getId_pegawai(), statusnya,
     * keterangan_status,item.getId_pegawai()); onLoadList(); }
     *
     */
    public void declineSelect() {
        String keterangan_status;
        Character statusnya;
        keterangan_status = "Dibatalkan oleh " + page.getMyPegawai().getNama();
        statusnya = 'C';
        serviceLembur.declineOvertime(item.getId(), statusnya, keterangan_status,item.getRevisi());
        onLoadList();
    }

    public Integer searchPersetujuan(Integer id_lembur_karyawan, String id_pegawai, Integer revisi) {
        return serviceLembur.searchPersetujuan(id_lembur_karyawan, id_pegawai, revisi);
    }
    
    public void nomorurut() {
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(new Date());
        String noMax = serviceLembur.selectMax(Integer.parseInt(tahun));
        if (noMax == null) {
            item.setNomor("LMB" + tahun + "0000001");
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%07d", nomor);
            item.setNomor("LMB" + tahun + noMax);
        }
    }
}
