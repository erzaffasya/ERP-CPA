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

import au.com.bytecode.opencsv.CSVReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.service.ServicePegawai;
import net.sra.prime.ultima.service.hr.ServiceAbsen;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAbsensi implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    private String uploadedFile;
    private byte[] uploadedData;
    private Logger logger;
    private List<Absen> lAbsen = new ArrayList<>();
    private List<Pegawai> lPegawai = new ArrayList<>();
    private Absen item;
    private Date datestart;
    private Date dateend;
    private Pegawai itemPegawai;
    private AbsenPegawaiStatus itemStatus;
    private String year;
    private String month_name;
    private String judul;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServiceAbsen serviceAbsen;

    @Autowired
    ServicePegawai servicePegawai;

    @PostConstruct
    public void init() {
        item = new Absen();
        itemPegawai = new Pegawai();
    }

    public List<Absen> getDataAbsen() {
        return lAbsen;
    }

    public void onAttendaceClocks() {
        itemPegawai = serviceAbsen.selectOnePegawai(item.getId_pegawai());
        LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        year = Integer.toString(localDate.getYear());
        int month = localDate.getMonthValue();
        //cek apakah sudah ada status absen
        itemStatus = serviceAbsen.selectOnePegawaiStatus(item.getId_pegawai(), month, year);
        if (itemStatus == null) {

            attendaceFunction(datestart, item.getId_pegawai());
        } else {
            // load periode absen untuk bulan dan tahun dipilih
            AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriode(year, month);

            lAbsen = serviceAbsen.onLoadListAbsen(item.getId_pegawai(), absenPeriode.getStart(), absenPeriode.getEnd_date());
        }
    }

    public void onGenerateAbsen(Date dtstart, String id_pegawai) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceAbsen.autoGenerateAbsen(dtstart, id_pegawai, page.getValueBegawi("autogenerateabsen"));
            lAbsen = new ArrayList<>();
            attendaceFunction(dtstart, id_pegawai);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            //e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), ""));
        }
    }

    public void myAttendaceClocks() {
        JadwalKerja jadwalKerja = serviceAbsen.selectOneJadwalKerja(page.getMyPegawai().getId_pegawai());
        if (jadwalKerja != null) {
            if (datestart == null) {
                datestart = new Date();
            }
            attendaceFunction(datestart, page.getMyPegawai().getId_pegawai());
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jadwal kerja pegawai ini belum ada !!!", ""));
        }

    }

    public void attendaceFunction(Date tanggalmulai, String id_pegawai) {
        LocalDate localDate = tanggalmulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        long terlambat = 0;
        long pulangcepat = 0;
        CutiKaryawan cutiKaryawan = new CutiKaryawan();
        AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriode(Integer.toString(year), month);

        // Ambil jadwal kerja karyawan
        JadwalKerja jadwalKerja = serviceAbsen.selectOneJadwalKerja(id_pegawai);
        int jmlhari=0;
        /**
         * menghitung penambah hari jadwal kerja*
         */
        if (jadwalKerja.getJenis().equals('h')) {
            long dateBeforeInMs = jadwalKerja.getStart_date().getTime();
            long dateAfterInMs = absenPeriode.getStart().getTime();
            /**
             * hitung hari pertama periode absen merupakan hari ke berapa jadwal
             * kerja *
             */
            long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
            long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
             jmlhari = (int) (long) daysDiff;

            if (jmlhari >= jadwalKerja.getCycle()) {
                jmlhari = jmlhari % jadwalKerja.getCycle();
            }
        }

        Pegawai pegawai = servicePegawai.onLoadDataDiri(id_pegawai);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long duration;

        if (jadwalKerja != null) {
            Calendar cal1 = Calendar.getInstance();
            Integer hari;
            lAbsen = serviceAbsen.onLoadList(id_pegawai, absenPeriode.getStart(), absenPeriode.getEnd_date());
            JadwalKerjaWaktu jadwalKerjaWaktu = new JadwalKerjaWaktu();
            Absen absen = new Absen();
            for (int i = 0; i < lAbsen.size(); i++) {
                absen = lAbsen.get(i);
                absen.setId_absen(pegawai.getAbsen());
                absen.setId_pegawai(item.getId_pegawai());
                cal1.setTime(absen.getTanggal());

                // jadwal kerja bulanan
                if (jadwalKerja.getJenis().equals('m')) {
                    /**
                     * hari 1--> senin hari 2 --> selasa hari 7 --> ahad
                     *
                     */
                    if (cal1.get(Calendar.DAY_OF_WEEK) == 1) {
                        hari = 7;
                    } else {
                        hari = cal1.get(Calendar.DAY_OF_WEEK) - 1;
                    }
                    if (jadwalKerja.getCycle() == 1) {
                        jadwalKerjaWaktu = serviceAbsen.selectOneJadwalKerjaWaktu(jadwalKerja.getId(), hari);
                        if (!jadwalKerjaWaktu.getSelected()) {

                            absen.setSchedule('o'); // off
                            absen.setSchedule_des("Libur");
                            if (absen.getJam_keluar() != null && absen.getJam_masuk() != null) {
                                duration = absen.getJam_keluar().getTime() - absen.getJam_masuk().getTime();
                                if (duration > 0) {
                                    duration = duration / (60 * 60 * 1000) % 24;
                                    absen.setLemburafter((int) (long) duration);
                                }
                            }

                        } else {
                            absen.setSchedule('p'); // present
                            absen.setSchedule_des(dateFormat.format(jadwalKerjaWaktu.getTime_in()) + " - " + dateFormat.format(jadwalKerjaWaktu.getTime_out()));
                            absen.setTime_in(jadwalKerjaWaktu.getTime_in());
                            absen.setTime_out(jadwalKerjaWaktu.getTime_out());
                            absen.setTime_break(jadwalKerjaWaktu.getTime_break());

                        }

                    }
                    // jadwal kerja harian/shift
                } else if (jadwalKerja.getJenis().equals('h')) {
                    long dateBeforeInMs = absenPeriode.getStart().getTime();
                    long dateAfterInMs = absen.getTanggal().getTime();
                    /**
                     * hitung hari pertama periode absen merupakan hari ke
                     * berapa jadwal kerja *
                     */
                    long timeDiff = Math.abs(dateAfterInMs - dateBeforeInMs);
                    long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                    
                    hari = (int) (long) daysDiff;
                    hari = hari + 1 + jmlhari;
                    if(hari > jadwalKerja.getCycle()){
                        hari = hari%jadwalKerja.getCycle();
                        // jika sisa pembagi = 0 berarti dia sudah lebih dari satu kali Cycle
                        if (hari == 0){
                            hari = jadwalKerja.getCycle();
                        }
                    }
                    jadwalKerjaWaktu = serviceAbsen.selectOneJadwalKerjaWaktu(jadwalKerja.getId(), hari);
                    if (!jadwalKerjaWaktu.getSelected()) {

                        absen.setSchedule('o'); // off
                        absen.setSchedule_des("Libur");
                        if (absen.getJam_keluar() != null && absen.getJam_masuk() != null) {
                            duration = absen.getJam_keluar().getTime() - absen.getJam_masuk().getTime();
                            if (duration > 0) {
                                duration = duration / (60 * 60 * 1000) % 24;
                                absen.setLemburafter((int) (long) duration);
                            }
                        }

                    } else {
                        absen.setSchedule('p'); // present
                        absen.setSchedule_des(dateFormat.format(jadwalKerjaWaktu.getTime_in()) + " - " + dateFormat.format(jadwalKerjaWaktu.getTime_out()));
                        absen.setTime_in(jadwalKerjaWaktu.getTime_in());
                        absen.setTime_out(jadwalKerjaWaktu.getTime_out());
                        absen.setTime_break(jadwalKerjaWaktu.getTime_break());

                    }

                }

                /// Hari Libur
                HariLibur hariLibur = serviceAbsen.selectHariLibur(absen.getTanggal());
                if (hariLibur != null) {
                    absen.setSchedule('o'); // off
                    absen.setSchedule_des("Libur " + hariLibur.getNama());
                    if (absen.getJam_keluar() != null && absen.getJam_masuk() != null) {
                        duration = absen.getJam_keluar().getTime() - absen.getJam_masuk().getTime();
                        if (duration > 0) {
                            duration = duration / (60 * 60 * 1000) % 24;
                            absen.setLemburafter((int) (long) duration);
                        }
                    }

                }
                if (absen.getId_status_absen() == null || absen.getId_status_absen().equals("")) {

                    //bukan hari libur
                    if (!absen.getSchedule().equals('o')) {
                        // Cek Izin karyawan
                        cutiKaryawan = serviceAbsen.selectOneCutiKaryawan(absen.getId_absen(), absen.getId_cuti());
                        if (cutiKaryawan != null) {
                            if (absen.getJam_masuk() == null && absen.getJam_keluar() == null) {
                                absen.setId_status_absen("UL");
                                absen.setStatus_absen("Unpaid Leave");
                            } else {
                                if (!absen.getJam_masuk().equals(absen.getJam_keluar())) {

                                    terlambat = absen.getJam_masuk().getTime() - absen.getTime_in().getTime();
                                    terlambat = terlambat / (60 * 1000);
                                    if (terlambat < 0) {
                                        terlambat = 0;
                                    }
                                    pulangcepat = absen.getTime_out().getTime() - absen.getJam_keluar().getTime();
                                    pulangcepat = pulangcepat / (60 * 1000);
                                    if (pulangcepat < 0) {
                                        pulangcepat = 0;
                                    }

                                    if (terlambat + pulangcepat < 180) {
                                        absen.setId_status_absen("M");
                                        absen.setStatus_absen("To be present");
                                    } else if (terlambat + pulangcepat >= 180 && terlambat + pulangcepat < 240) {
                                        absen.setId_status_absen("MW");
                                        absen.setStatus_absen("To be present without UMT");
                                    } else if (terlambat + pulangcepat >= 240) {
                                        absen.setId_status_absen("UL");
                                        absen.setStatus_absen("Unpaid Leave");
                                    }
                                } else {
                                    absen.setId_status_absen("UL");
                                    absen.setStatus_absen("Unpaid Leave");
                                }

                            }
                        } else if (absen.getJam_masuk() != null) {

                            if (absen.getJam_masuk() != null) {
                                if (absen.getJam_masuk().equals(absen.getJam_keluar())) {
                                    absen.setId_status_absen("LA");
                                    absen.setStatus_absen("Lupa Absen");
                                } else {
                                    terlambat = absen.getJam_masuk().getTime() - absen.getTime_in().getTime();
                                    terlambat = terlambat / (60 * 1000);

                                    pulangcepat = absen.getTime_out().getTime() - absen.getJam_keluar().getTime();
                                    pulangcepat = pulangcepat / (60 * 1000);
                                    if ((int) (long) terlambat > jadwalKerja.getBatas_terlambat() || (int) (long) pulangcepat > jadwalKerja.getBatas_pulang()) {
                                        absen.setId_status_absen("MW");
                                        absen.setStatus_absen("To be present without UMT");
                                    } else {
                                        absen.setId_status_absen("M");
                                        absen.setStatus_absen("To be present");
                                    }
                                }

                            }
                        }
                    } else if (absen.getJam_masuk() != null) {
                        absen.setId_status_absen("M");
                        absen.setStatus_absen("To be present");

                    }

                }
                //serviceAbsen.update(absen);
                lAbsen.set(i, absen);
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        UploadedFile argFile = event.getFile();
        uploadedFile = argFile.getFileName();
        uploadedData = argFile.getContents();
        doStuff();

    }

    public void doStuff() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        DateFormat tgl = new SimpleDateFormat("MM/dd/yyyy"); // Just the year, with 2 digits
        DateFormat jam = new SimpleDateFormat("HH:mm");
        Integer urut = serviceAbsen.urutMax();

        try {
            lAbsen = new ArrayList<>();
            CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(uploadedData)), '\t', '\"');
            Integer i = 0;
            for (String[] str : reader.readAll()) {
                if (i != 0) {
                    item = new Absen();
                    item.setId_absen(Integer.parseInt(str[0]));
                    item.setTanggal(tgl.parse(str[3]));
                    if (str[4] != null && !str[4].equals("")) {
                        item.setJam_masuk(new Time(jam.parse(str[4].substring(0, 5)).getTime()));
                        item.setJam_keluar(new Time(jam.parse(str[4].substring(str[4].length() - 6)).getTime()));
                    }
                    item.setUrut(urut);
                    lAbsen.add(item);
                }
                i++;
            }
            serviceAbsen.tambah(lAbsen, urut);

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onLoadListPegawai() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        if (datestart == null) {
            datestart = new Date();
            LocalDateTime localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            AbsenPeriode absenPeriode = serviceAbsen.selectOneAbsenPeriodeByDate(datestart);
            if (absenPeriode != null) {
                lPegawai = serviceAbsen.selectAllPegawaiAbsen(absenPeriode.getMonth(), absenPeriode.getYear());
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Periode Absen belum dibuat !!!", ""));
            }
        } else {
            LocalDateTime localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            lPegawai = serviceAbsen.selectAllPegawaiAbsen(localDate.getMonthValue(), Integer.toString(localDate.getYear()));
        }

    }

    public List<Pegawai> getDataPegawai() {
        return lPegawai;
    }

    public void gotoAttendaceClocks(String idPegawai, Date tanggal) throws IOException {
        JadwalKerja jadwalKerja = serviceAbsen.selectOneJadwalKerja(idPegawai);
        if (jadwalKerja != null) {

            DateFormat tgl = new SimpleDateFormat("yyyy-MM-dd"); // Just the year, with 2 digits
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect("./attendance_clocks.jsf?id=" + idPegawai + "&tanggal=" + tgl.format(tanggal));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Jadwal kerja pegawai ini belum ada !!!", ""));
        }
    }

    public java.sql.Time sqlTime(java.util.Date calendarDate) {
        return new java.sql.Time(calendarDate.getTime());
    }

    public void onRowEdit(RowEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        item = ((Absen) event.getObject());
        if (!item.getId_status_absen().equals('c') && !item.getId_status_absen().equals('p')) {
            try {
                Pegawai pegawai = servicePegawai.onLoadDataDiri(item.getId_pegawai());
                item.setId_absen(pegawai.getAbsen());
                Absen absen = serviceAbsen.selectOne(item.getId_absen(), item.getTanggal());
                if (absen != null) {
                    serviceAbsen.update(item);
                } else {
                    item.setUrut(0);
                    serviceAbsen.insertOne(item);

                }
                onAttendaceClocks();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data berhasil diedit", ""));
            } catch (Exception e) {
                e.printStackTrace();
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
            }
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Untuk cuti tidak bisa diubah melalui menu ini !!! ", ""));
        }
    }

    public void onRowAuto(Absen item, Integer i) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            Pegawai pegawai = servicePegawai.onLoadDataDiri(item.getId_pegawai());
            item.setId_absen(pegawai.getAbsen());
            item.setJam_masuk(item.getTime_in());
            item.setJam_keluar(item.getTime_out());
            item.setId_status_absen("M");
            item.setStatus_absen("To be present");
            Absen absen = serviceAbsen.selectOne(item.getId_absen(), item.getTanggal());
            if (absen != null) {
                serviceAbsen.update(item);
            } else {
                item.setUrut(0);
                serviceAbsen.insertOne(item);

            }
            //onAttendaceClocks();
            lAbsen.set(i, item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data berhasil diedit", ""));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Absen Cancelled", ((Absen) event.getObject()).getKeterangan());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void submitAbsen(Character statusnya) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean tesAbsen = true;
        try {
            for (int i = 0; i < lAbsen.size(); i++) {
                item = lAbsen.get(i);
                //System.out.println("tanggal :" + item.getTanggal());
                //selain hari libur
                if (!item.getSchedule().equals('o')) {
                    if (item.getId_status_absen() == null) {
                        tesAbsen = false;
                        break;
                    } else if (item.getId_status_absen().equals("M") || item.getId_status_absen().equals("LA") || item.getId_status_absen().equals("MW")) {
                        if (item.getJam_masuk() == null || item.getJam_keluar() == null) {
                            tesAbsen = false;
                            break;
                        } else if (item.getJam_masuk().equals(item.getJam_keluar())) {
                            tesAbsen = false;
                            break;
                        }
                    }
                } else if (item.getId_status_absen() != null) {
                    if (item.getId_status_absen().equals("M")) {
                        if (item.getJam_masuk() == null || item.getJam_keluar() == null) {
                            tesAbsen = false;
                            break;
                        } else if (item.getJam_masuk().equals(item.getJam_keluar())) {
                            tesAbsen = false;
                            break;
                        }
                    }
                }

            }
            if (tesAbsen) {
                LocalDate localDate = datestart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                serviceAbsen.insertSubmit(lAbsen, page.getMyPegawai().getId_pegawai(), item.getId_pegawai(), localDate.getMonthValue(), String.valueOf(localDate.getYear()), statusnya, page.getValueBegawi("umt"));
                itemStatus = serviceAbsen.selectOnePegawaiStatus(item.getId_pegawai(), localDate.getMonthValue(), String.valueOf(localDate.getYear()));
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Data berhasil di Input", ""));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Absen tidak bisa diposting masih ada beberapa kesalahan !!!", ""));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

}
