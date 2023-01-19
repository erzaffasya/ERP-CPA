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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.service.ServicePegawai;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.inject.Inject;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.controller.Page;
import net.sra.prime.ultima.entity.hr.AbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenLemburHead;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.service.hr.ServiceAbsenLembur;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerAbsensiLembur implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    private List<AbsenLembur> lAbsen = new ArrayList<>();
    private AbsenLemburHead item;
    private Pegawai itemPegawai;
    private AbsenPegawaiStatus itemStatus;
    private String year;
    private Integer month;
    private Double grandtotal;
    private Double totalovertime;
    private String id_pegawai;

    @Inject
    private Page page;

    @Inject
    private Options options;

    @Autowired
    ServiceAbsenLembur serviceAbsenLembur;

    @Autowired
    ServicePegawai servicePegawai;

    @PostConstruct
    public void init() {
        item = new AbsenLemburHead();
        itemPegawai = new Pegawai();
        grandtotal = 0.00;
        totalovertime = 0.00;
        year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        month = Integer.parseInt(new SimpleDateFormat("MM").format(Calendar.getInstance().getTime()));
    }

    public List<AbsenLembur> getDataAbsen() {
        return lAbsen;
    }

    public void onAttendaceClocks() {
        grandtotal = 0.00;
        totalovertime = 0.00;
        itemPegawai = serviceAbsenLembur.selectOnePegawai(item.getId_pegawai());
        AbsenPeriode absenPeriode = serviceAbsenLembur.selectOneAbsenPeriode(year, month);
        lAbsen = serviceAbsenLembur.onLoadListAbsen(item.getId_pegawai(), absenPeriode.getStart(), absenPeriode.getEnd_date());

        long masuk;
        long keluar;
        long istirahat;
        long totalmenit;
        long totaljam;
        
        double totallembur;
        double tmp;
        for (int i = 0; i < lAbsen.size(); i++) {
            lAbsen.get(i).setX1(0.00);
            lAbsen.get(i).setJx1(0.00);
            lAbsen.get(i).setX2(0.00);
            lAbsen.get(i).setJx2(0.00);
            lAbsen.get(i).setX3(0.00);
            lAbsen.get(i).setJx3(0.00);
            lAbsen.get(i).setX4(0.00);
            lAbsen.get(i).setJx4(0.00);
            lAbsen.get(i).setTx(0.00);
            lAbsen.get(i).setQtylembur(0.00);
            totallembur = 0.00;
            tmp = 0.00;
            // Perhitungan lembur hari kerja
            if (lAbsen.get(i).getSchedule().equals('p')) {
                if (lAbsen.get(i).getJam_masuk() != null && lAbsen.get(i).getJam_keluar() != null) {
                    masuk = lAbsen.get(i).getTime_in().getTime() - lAbsen.get(i).getJam_masuk().getTime();
                    if (masuk <= 0) {
                        masuk = 0;
                        totaljam = 0;
                        totalmenit = 0;
                    } else {
                        totalmenit = masuk / (60 * 1000) % 60;
                        totaljam = masuk / (60 * 60 * 1000) % 24;
                        lAbsen.get(i).setLmbbefore(totaljam + ":" + totalmenit);
                        if (totalmenit >= 30 && totalmenit < 55) {
                            tmp = 0.5;
                        } else if (totalmenit > 55) {
                            tmp = 1.00;
                        }
                    }
                    // Jika ada pengajuan lembur sebelum jam kerja
                    if (lAbsen.get(i).getHour_before()!= null && lAbsen.get(i).getHour_before()!= 0) {
                        if (((double) totaljam) + tmp < (double) lAbsen.get(i).getHour_before()) {
                            totallembur = ((double) totaljam) + tmp;
                        } else {
                            totallembur = (double) lAbsen.get(i).getHour_before();
                        }
                    }

                    keluar = lAbsen.get(i).getJam_keluar().getTime() - lAbsen.get(i).getTime_out().getTime();
                    tmp = 0.00;
                    if (keluar <= 0) {
                        keluar = 0;
                        totaljam = 0;
                        totalmenit = 0;
                    } else {
                        totalmenit = keluar / (60 * 1000) % 60;
                        totaljam = keluar / (60 * 60 * 1000) % 24;
                        lAbsen.get(i).setLmbafter(totaljam + ":" + totalmenit);
                        if (totalmenit >= 30 && totalmenit < 55) {
                            tmp = 0.5;
                        } else if (totalmenit > 55) {
                            tmp = 1.00;
                        }
                    }

                    // jika ada pengajuan lembur setelah jam kerja
                    if (lAbsen.get(i).getHour_after()!= null && lAbsen.get(i).getHour_after()!= 0) {
                        if (((double) totaljam) + tmp < (double) lAbsen.get(i).getHour_after()) {
                            totallembur += ((double) totaljam) + tmp;
                        } else {
                            totallembur += (double) lAbsen.get(i).getHour_after();
                        }
                    }
                    
                    //jika ada pengajuan lembur pada saat jam istirahat
                    if (lAbsen.get(i).getBreak_time()!= null && lAbsen.get(i).getBreak_time() != 0 && lAbsen.get(i).getJam_break() != null) {
                        DateFormat jam = new SimpleDateFormat("HH:mm");
                        String jamnya = jam.format(lAbsen.get(i).getJam_break());
                       // System.out.println(jamnya.substring(3, 4));
                        totalmenit = (long) Integer.parseInt(jamnya.substring(3,4));
                        totaljam = (long) Integer.parseInt(jamnya.substring(0,2));
                        tmp=0.00;
                        if (totalmenit >= 30 && totalmenit < 55) {
                            tmp = 0.5;
                        } else if (totalmenit > 55) {
                            tmp = 1.00;
                        }
                        
                        if (((double) totaljam) + tmp < (double) lAbsen.get(i).getBreak_time()) {
                            totallembur += ((double) totaljam) + tmp;
                        } else {
                            totallembur += (double) lAbsen.get(i).getBreak_time();
                        }
                    }
                    
                    // jika ada pengajuan lembur
                    lAbsen.get(i).setQtylembur((double) totallembur);
                    /// sementara perhitungan di inject langsung bukan dari database
                    if (totallembur >= 1) {
                        lAbsen.get(i).setX1(1.00);
                        lAbsen.get(i).setJx1(1.5);
                        //Double pengurang=1.5;
                        lAbsen.get(i).setX2(totallembur - 1);
                        lAbsen.get(i).setJx2((totallembur - 1) * 2);
                    }
                    lAbsen.get(i).setTx(lAbsen.get(i).getJx1() + lAbsen.get(i).getJx2() + lAbsen.get(i).getJx3() + lAbsen.get(i).getJx4());
                    totalovertime += totallembur;

                }
                //Hari libur
            } else {
                if (lAbsen.get(i).getJam_masuk() != null && lAbsen.get(i).getJam_keluar() != null) {
                    masuk = lAbsen.get(i).getJam_keluar().getTime() - lAbsen.get(i).getJam_masuk().getTime();
                    totalmenit = masuk / (60 * 1000) % 60;
                    totaljam = masuk / (60 * 60 * 1000) % 24;
                    lAbsen.get(i).setLmbbefore(totaljam + ":" + totalmenit);
                    if (totalmenit >= 30 && totalmenit < 55) {
                        tmp = 0.5;
                    } else if (totalmenit > 55) {
                        tmp = 1.00;
                    }
                    // Jika ada pengajuan lembur sebelum/sesudah jam kerja
                    if (lAbsen.get(i).getHour_before() != null || lAbsen.get(i).getHourafter()!= null) {
                        if (lAbsen.get(i).getHour_before()== null) {
                            lAbsen.get(i).setHour_before(0);
                        }

                        if (lAbsen.get(i).getHour_after()== null) {
                            lAbsen.get(i).setHour_after(0);
                        }

                        Double totalnya = (double) lAbsen.get(i).getHour_before()+ lAbsen.get(i).getHour_after();
                        if (((double) totaljam) + tmp < totalnya) {
                            totallembur = ((double) totaljam) + tmp;
                        } else {
                            totallembur = totalnya;
                        }

                        lAbsen.get(i).setQtylembur((double) totallembur);
                        /// sementara perhitungan di inject langsung bukan dari database
                        totalovertime += totallembur;
                        if (totallembur <= 8) {
                            lAbsen.get(i).setX2(totallembur);
                            lAbsen.get(i).setJx2(totallembur * 2.00);
                        } else {
                            lAbsen.get(i).setX2(8.00);
                            lAbsen.get(i).setJx2(16.00);

                            totallembur -= 8;
                            lAbsen.get(i).setX3(totallembur);
                            lAbsen.get(i).setJx3(3.00);
                            //Double pengurang=1.5;
                            totallembur -= 1;

                            if (totallembur >= 0) {
                                lAbsen.get(i).setX4(totallembur);
                                lAbsen.get(i).setJx4(totallembur * 4.00);
                            }

                        }

                        lAbsen.get(i).setTx(lAbsen.get(i).getJx1() + lAbsen.get(i).getJx2() + lAbsen.get(i).getJx3() + lAbsen.get(i).getJx4());

                    }
                }

            }
            grandtotal += lAbsen.get(i).getTx();

        }
    }

    public String getFooterGrandTotal() {
        return new DecimalFormat("###,###.##").format(grandtotal);
    }

    public String getFooterTotalOvertime() {
        return new DecimalFormat("###,###.##").format(totalovertime);
    }

    

    public void onPegawaiSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Pegawai pegawai = (Pegawai) event.getObject();
        id_pegawai = pegawai.getId_pegawai();
        item.setId_pegawai(id_pegawai);
        onAttendaceClocks();
    }

}
