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
package net.sra.prime.ultima.controller.hr.report;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import net.sra.prime.ultima.entity.hr.LemburKaryawan;
import net.sra.prime.ultima.entity.hr.LemburMultiplier;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerRekapitulasiLembur implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    private List<Absen> lAbsen = new ArrayList<>();
    private List<Pegawai> lPegawai = new ArrayList<>();
    private Absen item;
    private Date datestart;
    private Date tglstart;
    private Date dateend;
    private Pegawai itemPegawai;
    private Double totallembur;
    private Double nominalupah;
    private Double totalupah;
    private Double rate;
    private Integer totalhour;

    @Inject
    private Page page;

    @Autowired
    ServiceAbsen serviceAbsen;

    @Autowired
    ServicePegawai servicePegawai;

    @PostConstruct
    public void init() {
        item = new Absen();
    }

    public List<Absen> getDataAbsen() {
        return lAbsen;
    }

    public void search() throws IOException {
        DateFormat tgl = new SimpleDateFormat("yyyy-MM-dd"); // Just the year, with 2 digits
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./rekapitulasilembur.jsf?id=" + item.getId_pegawai() + "&tanggal=" + tgl.format(datestart));
    }

    public void rekapitulasiLembur() {
        if (datestart != null && item != null) {
            rekapitulasiLemburFunction(datestart, item.getId_pegawai());
        }
    }

    public void myovertime() {
        rekapitulasiLemburFunction(datestart, page.getMyPegawai().getId_pegawai());
    }

    public void rekapitulasiLemburFunction(Date tanggalmulai, String id_pegawai) {
        itemPegawai = servicePegawai.onLoadHubunganKerja(id_pegawai);
        nominalupah = itemPegawai.getGaji_pokok() + itemPegawai.getTunjangan_jabatan();
        rate = nominalupah / 173;
        totallembur = 0.0;
        lAbsen = new ArrayList<>();
        LocalDateTime localDateTime = tanggalmulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeStart = localDateTime.minusMonths(1).plusDays(20);
        tanggalmulai = Date.from(localDateTimeStart.atZone(ZoneId.systemDefault()).toInstant());
        tglstart = tanggalmulai;

        LocalDateTime localDateTimeEnd = localDateTime.plusDays(19);
        dateend = Date.from(localDateTimeEnd.atZone(ZoneId.systemDefault()).toInstant());
        JadwalKerja jadwalKerja = serviceAbsen.selectOneJadwalKerja(id_pegawai);
        if (jadwalKerja != null) {
            Calendar cal1 = Calendar.getInstance();
            List<LemburKaryawan> lLemburKaryawan = serviceAbsen.onLoadListLembuKaryawan(id_pegawai, tanggalmulai, dateend);
            List<LemburMultiplier> lLemburMultiplier = new ArrayList<>();
            Absen absen = new Absen();
            Long overtime;
            Long overtimebefore;
            Long overtimeafter;
            Integer qtyovertime;
            totalhour = 0;

            for (int i = 0; i < lLemburKaryawan.size(); i++) {
                qtyovertime = 0;
                overtime = new Long(0);
                absen = serviceAbsen.selectOneAbsen(lLemburKaryawan.get(i).getTanggal(), id_pegawai);
                if (absen == null) {
                    absen = new Absen();
                }

                absen.setTx(0.0);
                absen.setId_pegawai(id_pegawai);
                absen.setOvertimeafter(lLemburKaryawan.get(i).getHour_after());
                absen.setOvertimebefore(lLemburKaryawan.get(i).getHour_before());
                absen.setKeterangan(lLemburKaryawan.get(i).getKeterangan());
                absen.setTanggal(lLemburKaryawan.get(i).getTanggal());
                
                cal1.setTime(lLemburKaryawan.get(i).getTanggal());
                if (cal1.get(Calendar.DAY_OF_WEEK) == 1) {
                    absen.setHari(7);
                } else {
                    absen.setHari(cal1.get(Calendar.DAY_OF_WEEK) - 1);
                }
                JadwalKerjaWaktu jadwalKerjaWaktu = serviceAbsen.selectOneDay(absen.getId_pegawai(), absen.getHari());
                DateFormat jam = new SimpleDateFormat("HH:mm");
                if (jadwalKerjaWaktu != null) {
                    overtimebefore = new Long(0);
                    if (absen.getJam_masuk() != null && absen.getJam_keluar() != null) {
                        if (absen.getOvertimebefore() > 0) {
                            if (jadwalKerjaWaktu.getSelected()) {
                                absen.setHourafter(jam.format(absen.getJam_masuk()) + "-" + jam.format(jadwalKerjaWaktu.getTime_in()));
                                overtimebefore = (jadwalKerjaWaktu.getTime_in().getTime() - absen.getJam_masuk().getTime()) / 3600000;
                            } else {
                                absen.setHourafter(jam.format(absen.getJam_masuk()) + "-" + jam.format(absen.getJam_keluar()));
                                overtimebefore = (absen.getJam_keluar().getTime() - absen.getJam_masuk().getTime()) / 3600000;
                            }

                            if (overtimebefore.intValue() > lLemburKaryawan.get(i).getHour_before()) {
                                qtyovertime = lLemburKaryawan.get(i).getHour_before();
                            } else {
                                qtyovertime = overtimebefore.intValue();
                            }
                        }
                        if (absen.getOvertimeafter() > 0) {
                            overtimeafter = new Long(0);
                            if (jadwalKerjaWaktu.getSelected()) {
                                absen.setHourafter(jam.format(jadwalKerjaWaktu.getTime_out()) + "-" + jam.format(absen.getJam_keluar()));
                                overtimeafter = (absen.getJam_keluar().getTime() - jadwalKerjaWaktu.getTime_out().getTime()) / 3600000;
                            } else {
                                absen.setHourafter(jam.format(absen.getJam_masuk()) + "-" + jam.format(absen.getJam_keluar()));
                                overtimeafter = (absen.getJam_keluar().getTime() - absen.getJam_masuk().getTime()) / 3600000;
                            }

                            if (overtimeafter.intValue() > lLemburKaryawan.get(i).getHour_after()) {
                                qtyovertime = qtyovertime + lLemburKaryawan.get(i).getHour_after();
                            } else {
                                qtyovertime = qtyovertime + overtimeafter.intValue();
                            }
                        }

                        if (absen.getJam_masuk().getTime() <= 12 && absen.getJam_keluar().getTime() >= 13) {
                            qtyovertime = qtyovertime - 1;
                        }

                        totalhour = totalhour + qtyovertime;

                        //absen.setOvertime(qtyovertime.doubleValue());
                        Pegawai pegawai = serviceAbsen.selectOnePegawai(absen.getId_pegawai());
                        if (jadwalKerjaWaktu.getSelected()) {
                            lLemburMultiplier = serviceAbsen.selectMultiplierByJabatan(pegawai.getId_jabatan_new(), 'd');
                        } else {
                            lLemburMultiplier = serviceAbsen.selectMultiplierByJabatan(pegawai.getId_jabatan_new(), 'e');
                        }

                        // menaruh jumlah jam lembur sesuai dengan perhitungan jam lembur
//                        for (int j = 0; j < lLemburMultiplier.size(); j++) {
//                            if (qtyovertime >= lLemburMultiplier.get(j).getMulai() && qtyovertime <= lLemburMultiplier.get(j).getSelesai()) {
//                                if (lLemburMultiplier.get(j).getMultiply() == 4) {
//                                    absen.setX4(qtyovertime - lLemburMultiplier.get(j).getMulai() + 1);
//                                    absen.setJx4(absen.getX4().doubleValue() * lLemburMultiplier.get(j).getMultiply());
//                                    absen.setTx(absen.getTx() + absen.getJx4());
//                                    totallembur = totallembur + absen.getJx4();
//                                } else if (lLemburMultiplier.get(j).getMultiply() == 3) {
//                                    absen.setX3(qtyovertime - lLemburMultiplier.get(j).getMulai() + 1);
//                                    absen.setJx3(absen.getX3().doubleValue() * lLemburMultiplier.get(j).getMultiply());
//                                    absen.setTx(absen.getTx() + absen.getJx3());
//                                    totallembur = totallembur + absen.getJx3();
//                                } else if (lLemburMultiplier.get(j).getMultiply() == 2) {
//                                    absen.setX2(qtyovertime - lLemburMultiplier.get(j).getMulai() + 1);
//                                    absen.setJx2(absen.getX2().doubleValue() * lLemburMultiplier.get(j).getMultiply());
//                                    absen.setTx(absen.getTx() + absen.getJx2());
//                                    totallembur = totallembur + absen.getJx2();
//                                } else if (lLemburMultiplier.get(j).getMultiply() == 1.5) {
//                                    absen.setX1(qtyovertime - lLemburMultiplier.get(j).getMulai() + 1);
//                                    absen.setJx1(absen.getX1().doubleValue() * lLemburMultiplier.get(j).getMultiply());
//                                    absen.setTx(absen.getTx() + absen.getJx1());
//                                    totallembur = totallembur + absen.getJx1();
//                                }
//                                qtyovertime = qtyovertime - (qtyovertime - lLemburMultiplier.get(j).getMulai() + 1);
//                            }
//                        }
                    } //  end if (absen.getJam_masuk() != null && absen.getJam_keluar() != null) {
                }

                lAbsen.add(absen);
            }
            totalupah = totallembur * nominalupah;

        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Gagal membuat laporan !!!!", ""));
        }
    }

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        item.setId_pegawai(pegawai.getId_pegawai());

    }
// 606,626,533.00
}
