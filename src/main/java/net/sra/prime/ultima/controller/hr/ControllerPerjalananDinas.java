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
import net.sra.prime.ultima.entity.hr.PerjalananDinas;
import net.sra.prime.ultima.entity.hr.PerjalananDinasPersetujuan;
import net.sra.prime.ultima.entity.hr.SettingApproval;
import net.sra.prime.ultima.service.ServiceHrDepartemen;
import net.sra.prime.ultima.service.ServiceMasterJabatan;
import net.sra.prime.ultima.service.hr.ServicePerjalananDinas;
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
public class ControllerPerjalananDinas implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServicePerjalananDinas servicePerjalananDinas;

    @Autowired
    private ServiceHrDepartemen serviceHrDepartemen;

    @Autowired
    private ServiceMasterJabatan serviceMasterJabatan;

    private PerjalananDinas item;
    private List<PerjalananDinas> lPerjalananDinas = new ArrayList<>();
    private List<PerjalananDinas> lOtherPerjalananDinas = new ArrayList<>();
    private List<PerjalananDinas> lNotifikasi = new ArrayList<>();
    private List<PerjalananDinas> lSelectedNotifikasi = new ArrayList<>();
    private List<PerjalananDinasPersetujuan> lPerjalananDinasPersetujuan = new ArrayList<>();
    private Integer id_jabatan;
    private Integer id_departemen;
    private Integer id_cuti;
    private Character status;
    private Date bulan;
    private String tahun;
    private Date awal;
    private Date akhir;

    @Inject
    private Page page;

    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;

    @PostConstruct
    public void init() {
        item = new PerjalananDinas();
        tahun = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
    }

    public void initItem() {
        item = new PerjalananDinas();
        item.setWaktu('1');
        pegawaiAutoComplete.setPegawai(page.getMyPegawai());
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setNama_pegawai(page.getMyPegawai().getNama());
    }
    
    public void initMyItem() {
        item = new PerjalananDinas();
        item.setWaktu('1');
        item.setId_pegawai(page.getMyPegawai().getId_pegawai());
        item.setNama_pegawai(page.getMyPegawai().getNama());
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
            PerjalananDinas ct;

            lPerjalananDinasPersetujuan = new ArrayList<>();
            lNotifikasi = servicePerjalananDinas.onLoadListNotifikasiPerjalananDinas(page.getMyPegawai().getId_pegawai());

            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                if (ct.getWaktu().equals('1')) {
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
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan perjalanan dinas untuk tanggal " + ct.getTanggal());
                lNotifikasi.set(i, ct);
            }

            lPerjalananDinas = servicePerjalananDinas.onLoadList(id_departemen, id_jabatan, id_cuti, status, awal,akhir);
            for (int i = 0; i < lPerjalananDinas.size(); i++) {
                ct = lPerjalananDinas.get(i);
                if (lPerjalananDinas.get(i).getWaktu().equals('1')) {
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
                lPerjalananDinas.set(i, ct);
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
            PerjalananDinas ct;
            
            lNotifikasi = servicePerjalananDinas.onLoadListNotifikasiPerjalananDinas(page.getMyPegawai().getId_pegawai());

            for (int i = 0; i < lNotifikasi.size(); i++) {
                ct = lNotifikasi.get(i);
                if (ct.getWaktu().equals('1')) {
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
                ct.setKeterangan(ct.getNama_pegawai() + " mengajukan perjalanan dinas untuk tanggal " + ct.getTanggal());
                lNotifikasi.set(i, ct);
            }


            lPerjalananDinas = servicePerjalananDinas.onLoadMyList(page.getMyPegawai().getId_pegawai(),tahun);
            
            for (int i = 0; i < lPerjalananDinas.size(); i++) {
                ct = lPerjalananDinas.get(i);
                if (lPerjalananDinas.get(i).getWaktu().equals('1')) {
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
                lPerjalananDinas.set(i, ct);
            }
            
            lOtherPerjalananDinas = servicePerjalananDinas.onLoadOtherList(page.getMyPegawai().getId_pegawai(),tahun);
            for (int i = 0; i < lOtherPerjalananDinas.size(); i++) {
                ct = lOtherPerjalananDinas.get(i);
                if (lOtherPerjalananDinas.get(i).getWaktu().equals('1')) {
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
                lOtherPerjalananDinas.set(i, ct);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<PerjalananDinas> getDataPerjalananDinas() {
        return lPerjalananDinas;
    }
    
    public List<PerjalananDinas> getDataOtherPerjalananDinas() {
        return lOtherPerjalananDinas;
    }
    
    public List<PerjalananDinasPersetujuan> getDataPerjalananDinasPersetujuan() {
        return lPerjalananDinasPersetujuan;
    }

    public List<PerjalananDinas> getDataNotifikasi() {
        return lNotifikasi;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePerjalananDinas.deletePerjalananDinas(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }

    }

    /**
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        SettingApproval ct = servicePerjalananDinas.settingan();
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modul perjalanan dinas belum disetting !!!", ""));
        }
        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                
                // Jika cuti ini tidak memerliukan persetujuan makan status diisi T
                if (ct.getPersetujuan1().equals('3')) {
                    item.setStatus('T');
                    // Jika cuti ini memerlukan persetujuan dari jabatan tertentu dan yg menginput cuti karyawan
                    // adalah yg memiliki wewenang untuk melakukan persetujuan tsb makan status diisi T
                } else if (ct.getPersetujuan1().equals('2')) {
                    // jika tidak memerlukan persetujuan kedua dan persetujuan ke 1 adalah yg menginput cuti ini
                    if (ct.getPersetujuan2().equals('1')) {
                        if (page.getMyPegawai().getId_jabatan() == ct.getJabatanpersetujuan1()) {
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
                        if (page.getMyPegawai().getId_jabatan() == ct.getJabatanpersetujuan2()) {
                            item.setStatus('A');
                            item.setKeterangan_status("Tidak perlu persetujuan");
                            // jika yang menginput adalah yang memberikan persetujuan kesatu maka masih memerlukan     
                        } else if (page.getMyPegawai().getId_jabatan() == ct.getJabatanpersetujuan1()) {
                            item.setStatus('W');
                            item.setKeterangan_status("Menunggu persetujuan ke dua");
                        } else {
                            item.setStatus('D');
                            item.setKeterangan_status("Menunggu persetujuan");
                        }
                    }
                }
                servicePerjalananDinas.tambahPerjalananDinas(item, ct,page.getMyPegawai().getId_departemen_new());
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./list.jsf");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }
**/
    
    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        SettingApproval ct = servicePerjalananDinas.settingan();
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modul perjalanan dinas belum disetting !!!", ""));
        }
        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                servicePerjalananDinas.addMyBusinessTrip(item, ct);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./list.jsf");
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        }
    }

    
    public void addMyBusinessTrip() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        
        SettingApproval ct = servicePerjalananDinas.settingan();
        Boolean benar = true;
        if (ct == null) {
            benar = false;
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Modul perjalanan dinas belum disetting !!!", ""));
        }
        if (benar) {
            try {
                item.setCreate_by(page.getMyPegawai().getId_pegawai());
                servicePerjalananDinas.addMyBusinessTrip(item, ct);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
                context.getExternalContext().redirect("./perjalanandinas.jsf");
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
        item = servicePerjalananDinas.onLoadPerjalananDinas(item.getId());

    }

    public void onDetilSelect(Integer id) {
        item = servicePerjalananDinas.onLoadPerjalananDinas(id);
        DateFormat tgl = new SimpleDateFormat("dd MMMM yyyy");
        if (item.getWaktu().equals('1')) {
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
        lPerjalananDinasPersetujuan=servicePerjalananDinas.selectAllPerjalananDinasPersetujuan(id);

    }

    public void ubah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            servicePerjalananDinas.ubahPerjalananDinas(item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    

        public void onWaktuCutiSelect() {
        item.setTanggal_awal(item.getTanggal_awal());

    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setId_pegawai(pegawai.getId_pegawai());
        
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
            map.put("Semua perjalanan dinas", "");
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
            if (item.getStatus().equals('D') && servicePerjalananDinas.countPersetujuan(item.getId()) == 1) {
                keterangan_status = "Telah disetujui oleh " + page.getMyPegawai().getNama();
                statusnya = 'A';
            }else  if (item.getStatus().equals('W') && servicePerjalananDinas.countPersetujuan(item.getId()) == 2) {
                keterangan_status = "Telah disetujui oleh "  + servicePerjalananDinas.onLoadPersetujuankedua(item.getId(),1).getNama_pegawai() + " dan " + page.getMyPegawai().getNama();
                statusnya = 'A';
            }else{
                keterangan_status = "Telah disetujui oleh " + page.getMyPegawai().getNama() + " dan menunggu persetujuan dari " + servicePerjalananDinas.onLoadPersetujuankedua(item.getId(),2).getNama_pegawai();
                statusnya = 'W';
            }
        } else {
            keterangan_status = "Ditolak oleh " + page.getMyPegawai().getNama();
            statusnya = 'R';
        }
        servicePerjalananDinas.updateStatusPerjalananDinas(item.getId(), st, page.getMyPegawai().getId_pegawai(), statusnya, keterangan_status,item.getId_pegawai());
        onLoadMyList();
    }
    
    public Integer searchPersetujuan (Integer id_perjalanan_dinas, String id_pegawai, Integer urut){
        return servicePerjalananDinas.searchPersetujuan(id_perjalanan_dinas, id_pegawai, urut);
    }
}
