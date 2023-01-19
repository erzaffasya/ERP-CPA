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
package net.sra.prime.ultima.service.hr;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPengguna;
import net.sra.prime.ultima.db.mapper.finance.MapperProposalLoanPaid;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsen;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenLembur;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenPeriode;
import net.sra.prime.ultima.db.mapper.hr.MapperPayroll;
import net.sra.prime.ultima.db.mapper.hr.MapperPayrollOvertime;
import net.sra.prime.ultima.db.mapper.hr.MapperPayrollProduktivitas;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaid;
import net.sra.prime.ultima.entity.hr.AbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.Payroll;
import net.sra.prime.ultima.entity.hr.PayrollDetail;
import net.sra.prime.ultima.entity.hr.Thr;
import net.sra.prime.ultima.entity.hr.ThrDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import begawi.BegawiDate;
import java.text.DecimalFormat;
import net.sra.prime.ultima.db.mapper.finance.MapperTunjanganKesehatan;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServicePayroll {

    @Autowired
    MapperPayroll referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperAbsen mapperAbsen;

    @Autowired
    MapperProposalLoanPaid mapperProposalLoanPaid;

    @Autowired
    MapperPengguna mapperPengguna;

    @Autowired
    MapperPayrollOvertime mapperPayrollOvertime;

    @Autowired
    MapperPayrollProduktivitas mapperPayrollProduktivitas;

    @Autowired
    MapperAbsenLembur mapperAbsenLembur;

    @Autowired
    MapperAbsenPeriode mapperAbsenPeriode;

    @Autowired
    MapperTunjanganKesehatan mapperTunjanganKesehatan;

    @Transactional(readOnly = true)
    public List<PayrollDetail> onLoadList(String year, Integer month) {
        return referensi.selectAll(year, month);
    }

    @Transactional(readOnly = true)
    public PayrollDetail onLoadListRekapGaji(String year, Integer month, String id_kantor) {
        return referensi.rekapGaji(year, month, id_kantor);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Payroll item, Character s, Character jenis, Character pphsetting, Character umt, Character overtime, Character produktivitas) throws ParseException {

        Thr thr = referensi.checkThr(item.getMonth(), Integer.parseInt(item.getYear()));
        ThrDetail thrDetail = new ThrDetail();
        if (s.equals('u')) {
            Payroll payroll = referensi.selectOne(item.getYear(), item.getMonth());
            referensi.deleteDetail(payroll.getId());
            referensi.delete(payroll.getId());
        }
        referensi.insert(item);
        AbsenPegawaiStatus aps = new AbsenPegawaiStatus();
        PayrollDetail payrollDetail = new PayrollDetail();
        List<Pegawai> lPegawai = mapperPegawai.selectAll(true);

        LocalDate mulaibekerja;
        LocalDate tglgajian;
        Period lamanya;
        Integer totalkehadiran;
        AbsenPeriode absenPeriode = mapperAbsenPeriode.selectOne(item.getYear(), item.getMonth());
        tglgajian = absenPeriode.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //PayrollOvertimeDetail payrollOvertimeDetail = new PayrollOvertimeDetail();
        /*
        jika diambil dari inputan keunagan
        PayrollProduktivitasDetail payrollProduktivitasDetail = new PayrollProduktivitasDetail();
         */
        Double tmp;
        Double gajitetap;
        for (int i = 0; i < lPegawai.size(); i++) {
             if(lPegawai.get(i).getId_pegawai().equals("CPA-158")){
            //cek apakah karyawan ada mengajukan resign 
            if (lPegawai.get(i).getTgl_resign() != null) {
                // jika resign sebelum periode gajian makan skip
                if (BegawiDate.dayBeforeAfter(lPegawai.get(i).getTgl_resign(), -1).before(absenPeriode.getStart())) {
                    continue;
                }
            }
            // Resume Absensi
            aps = mapperAbsen.selectOnePegawaiStatus(lPegawai.get(i).getId_pegawai(), item.getMonth(), item.getYear());
            if (aps != null) {
                totalkehadiran = aps.getTobepresent() + aps.getVisitcustomer() + aps.getLupaabsen() + aps.getTobepresentwithoutumt() + aps.getVisitcustomerharilibur();
            } else {
                totalkehadiran = 0;
            }

            mulaibekerja = lPegawai.get(i).getMulai_bekerja().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
            //mulaibekerja = lPegawai.get(i).getMulai_bekerja().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            lamanya = Period.between(mulaibekerja, tglgajian);

            payrollDetail = new PayrollDetail();
            payrollDetail.setId(item.getId());
            payrollDetail.setId_pegawai(lPegawai.get(i).getId_pegawai());

            // cek apakah pegawai tsb sudah lebih dari 1 bulan kerja
            if (lamanya.getMonths() > 0 || lamanya.getYears() > 0) {
                //cek apakah pegawai tsb resign
                if (lPegawai.get(i).getTgl_resign() != null) {
                    // cek apakah resign dalam periode absen
                    if (lPegawai.get(i).getTgl_resign().after(absenPeriode.getStart()) && BegawiDate.dayBeforeAfter(lPegawai.get(i).getTgl_resign(), -1).before(absenPeriode.getEnd_date())) {
                        payrollDetail.setGaji_pokok(lPegawai.get(i).getGaji_pokok() / 21 * (totalkehadiran + aps.getSakit() + aps.getIjin() + aps.getCuti()));
                        payrollDetail.setTunjangan_jabatan(lPegawai.get(i).getTunjangan_jabatan() / 21 * totalkehadiran);
                        payrollDetail.setInsentif_produktifitas(lPegawai.get(i).getInsentif_kpi() / 21 * totalkehadiran);
                        payrollDetail.setTunjangan_komunikasi(lPegawai.get(i).getTunjangan_komunikasi() / 21 * totalkehadiran);
                        // dipakai untuk insentif mutasi 
                        payrollDetail.setTunjangan_bbm(lPegawai.get(i).getTunjangan_bbm() / 21 * totalkehadiran);
                        /// DIPAKAI UNTUK INSENTIF SITE
                        payrollDetail.setInsentif_kehadiran(lPegawai.get(i).getInsentif_kehadiran() / 21 * totalkehadiran);
                    } else {
                        // jika resign setelah periode gajian
                        payrollDetail.setGaji_pokok(lPegawai.get(i).getGaji_pokok());
                        payrollDetail.setTunjangan_jabatan(lPegawai.get(i).getTunjangan_jabatan());
                        payrollDetail.setInsentif_produktifitas(lPegawai.get(i).getInsentif_kpi());
                        payrollDetail.setTunjangan_komunikasi(lPegawai.get(i).getTunjangan_komunikasi());
                        // dipakai untuk insentif mutasi 
                        payrollDetail.setTunjangan_bbm(lPegawai.get(i).getTunjangan_bbm());
                        /// DIPAKAI UNTUK INSENTIF SITE
                        payrollDetail.setInsentif_kehadiran(lPegawai.get(i).getInsentif_kehadiran());
                    }
                } else {
                    payrollDetail.setGaji_pokok(lPegawai.get(i).getGaji_pokok());
                    payrollDetail.setTunjangan_jabatan(lPegawai.get(i).getTunjangan_jabatan());
                    payrollDetail.setInsentif_produktifitas(lPegawai.get(i).getInsentif_kpi());
                    payrollDetail.setTunjangan_komunikasi(lPegawai.get(i).getTunjangan_komunikasi());
                    // dipakai untuk insentif mutasi 
                    payrollDetail.setTunjangan_bbm(lPegawai.get(i).getTunjangan_bbm());
                    /// DIPAKAI UNTUK INSENTIF SITE
                    payrollDetail.setInsentif_kehadiran(lPegawai.get(i).getInsentif_kehadiran());

                }
            } else {
                payrollDetail.setGaji_pokok(lPegawai.get(i).getGaji_pokok() / 21 * totalkehadiran);
                payrollDetail.setTunjangan_jabatan(lPegawai.get(i).getTunjangan_jabatan() / 21 * totalkehadiran);
                payrollDetail.setInsentif_produktifitas(lPegawai.get(i).getInsentif_kpi() / 21 * totalkehadiran);
                payrollDetail.setTunjangan_komunikasi(lPegawai.get(i).getTunjangan_komunikasi() / 21 * totalkehadiran);
                // dipakai untuk insentif mutasi 
                payrollDetail.setTunjangan_bbm(lPegawai.get(i).getTunjangan_bbm() / 21 * totalkehadiran);
                /// DIPAKAI UNTUK INSENTIF SITE
                payrollDetail.setInsentif_kehadiran(lPegawai.get(i).getInsentif_kehadiran() / 21 * totalkehadiran);
            }

            gajitetap = payrollDetail.getGaji_pokok() + payrollDetail.getTunjangan_jabatan();

            payrollDetail.setUmt(0.00);

            payrollDetail.setInsentif_penjualan(0.00);
            payrollDetail.setKomisi_penjualan(0.00);
            payrollDetail.setThr(0.00);
            payrollDetail.setAsuransi_komersil(0.00);
            payrollDetail.setPotongan_absensi(0.00);
            payrollDetail.setPph21_komisi(0.00);
            payrollDetail.setPph21_thr(0.00);
            payrollDetail.setHutang_dll(0.00);

            if (thr != null) {
                if (thr.getInclude_thp()) {
                    thrDetail = referensi.selectOneThrDetail(thr.getId_thr(), lPegawai.get(i).getId_pegawai());
                    if (thrDetail != null) {
                        if (thrDetail.getYear() >= 1) {
                            payrollDetail.setThr(gajitetap);
                        } else if (thrDetail.getMonth() >= 1) {
                            payrollDetail.setThr(gajitetap * thrDetail.getMonth() / 12);
                        }
                    }
                }
            }

            // Insentif liter
            tmp = referensi.getInsentif(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth(), "liter");

            if (tmp != null) {
                payrollDetail.setInsentif_penjualan(tmp);
            }

            tmp = referensi.getInsentif(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth(), "komisi");
            if (tmp != null) {
                payrollDetail.setKomisi_penjualan(tmp);
            }

            payrollDetail.setOvertime(calculateOverTime(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth(), payrollDetail.getGaji_pokok() + payrollDetail.getTunjangan_jabatan()));

            /*
        jika diambil dari inputan keunagan
            if (produktivitas.equals('M')) {
                payrollProduktivitasDetail = mapperPayrollProduktivitas.selectOneDetailPosting(item.getYear(), item.getMonth(), lPegawai.get(i).getId_pegawai());
                if (payrollProduktivitasDetail != null) {
                    payrollDetail.setInsentif_produktifitas(payrollProduktivitasDetail.getAmount());
                }
            }
             */
            if (aps != null) {

                // Potongan Absensi
                if (aps.getAbsen() + aps.getUnpaidleave() > 0) {
                    payrollDetail.setPotongan_absensi((gajitetap / 21) * (aps.getAbsen() + aps.getUnpaidleave()));
                }

                /*
                jika terlambat lebih besar daripada 50% maka tidak mendapat UMT
                 */
//                if (aps.getTobepresentwithoutumt() > 11) {
//                    payrollDetail.setUmt(0.00);
//                } else {
                /// pakai rumus 
                if (umt.equals('M')) {
                    payrollDetail.setUmt((aps.getTobepresent() + aps.getPerjalanandinas() + aps.getVisitcustomer() + aps.getVisitcustomerharilibur()) * lPegawai.get(i).getUmt());
                } else if (umt.equals('A')) {
                    payrollDetail.setUmt(aps.getUmt() * lPegawai.get(i).getUmt());
                }

            }

            /// Potongan
            payrollDetail.setBpjs_ketenagakerjaan(lPegawai.get(i).getJkk() + lPegawai.get(i).getJkm() + lPegawai.get(i).getJht_pekerja());

            payrollDetail.setBpjs_kes_pekerja(lPegawai.get(i).getKesehatan_pekerja());
            payrollDetail.setBpjs_kes_perusahaan(lPegawai.get(i).getKesehatan_perusahaan());
            payrollDetail.setJht_perusahaan(lPegawai.get(i).getJht_perusahaan());
            payrollDetail.setJht_pekerja(lPegawai.get(i).getJht_pekerja());
            payrollDetail.setJp_perusahaan(lPegawai.get(i).getJp_perusahaan());
            payrollDetail.setJp_pekerja(lPegawai.get(i).getJp_pekerja());
            payrollDetail.setJkk(lPegawai.get(i).getJkk());
            payrollDetail.setJkm(lPegawai.get(i).getJkm());

            payrollDetail.setPrudential(lPegawai.get(i).getIuran_prudential());
            payrollDetail.setAlianz(lPegawai.get(i).getIuran_allianz());
            payrollDetail.setAllianz_perusahaan(lPegawai.get(i).getIuran_allianz_perusahaan());
            payrollDetail.setPrudential_perusahaan(lPegawai.get(i).getIuran_prudential_perusahaan());
            payrollDetail.setTunjangan_kesehatan(mapperTunjanganKesehatan.sumTunjanganKesehatan(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth()));

            //Perhitungan PPH 21 manual
            if (pphsetting.equals('M')) {

                if (lPegawai.get(i).getPph() == null) {
                    lPegawai.get(i).setPph(0.00);
                }
                payrollDetail.setPph21_upah(lPegawai.get(i).getPph());
                // Regulasi
            } else if (pphsetting.equals('R')) {
                payrollDetail.setPph21_upah(0.00);
                if (lPegawai.get(i).getId_jenis_pph() != null) {
                    if (lPegawai.get(i).getId_jenis_pph().equals('a')) {
//                        thrDetail = referensi.selectOneThrDetail(thr.getId_thr(), lPegawai.get(i).getId_pegawai());
                        Double thrnya = 0.00;
//                        if(thrDetail != null){
//                            thrnya=thrDetail.getValue();
//                        }
                        payrollDetail.setPph21_upah(calculatePph(item, payrollDetail, lPegawai.get(i), thrnya));
                    }
                }
            }

            if (jenis.equals('O')) {
                payrollDetail.setHutang_kta(mapperProposalLoanPaid.sumLoanPaid(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth()));
            } else if (jenis.equals('M')) {
                payrollDetail.setHutang_kta(mapperProposalLoanPaid.sumLoanPaidManual(lPegawai.get(i).getId_pegawai(), item.getYear(), item.getMonth()));
            }

            referensi.insertDetail(payrollDetail);
             } // end if tes
        }// end for

    }

    @Transactional(readOnly = false)
    public void ubah(Payroll item) {
        referensi.update(item);
    }

    @Transactional(readOnly = false)
    public void ubahDetail(PayrollDetail item
    ) {
        referensi.updateDetail(item);
    }

    @Transactional(readOnly = true)
    public Payroll onLoad(String year, Integer month) {
        return referensi.selectOne(year, month);
    }

    @Transactional(readOnly = true)
    public Payroll onLoadMySalary(String year, Integer month) {
        return referensi.selectOneMySalary(year, month);
    }

    @Transactional(readOnly = true)
    public Payroll onLoadById(Integer id) {
        return referensi.selectOneById(id);
    }

    @Transactional(readOnly = true)
    public PayrollDetail onLoadDetail(Integer id, String id_pegawai
    ) {
        return referensi.selectOneDetail(id, id_pegawai);
    }

    @Transactional(readOnly = true)
    public Pengguna CheckPin(String usernamenya, String pinnya
    ) {
        return mapperPengguna.CheckPin(usernamenya, pinnya);
    }

    @Transactional(readOnly = true)
    public Integer countAbsenNotPosting(Integer month, String year, Date periode) {
        return referensi.countAbsenNotPosting(month, year, periode);
    }

    @Transactional(readOnly = true)
    public List<PayrollDetail> onloadListDaftarGaji(Integer month, String year
    ) {
        return referensi.onloadListDaftarGaji(month, year);
    }

    /**
     * Perhiungan lembur : jumlah jam lembur X (gaji pokok + tunjangan tetap ) *
     * 1/173
     */
    public Double calculateOverTime(String id_pegawai, String year, Integer month, Double tunjanganTetap) {
        Double grandtotal = 0.00;
        Double totalovertime = 0.00;
        Pegawai itemPegawai = mapperPegawai.selectOne(id_pegawai);
        AbsenPeriode absenPeriode = mapperAbsenPeriode.selectOne(year, month);
        List<AbsenLembur> lAbsen = mapperAbsenLembur.selectAllHrAbsen(id_pegawai, absenPeriode.getStart(), absenPeriode.getEnd_date());

        long masuk;
        long keluar;
        long istirahat;
        long totalmenit;
        long totaljam;

        double totallembur;
        double tmp;
        //System.out.println(id_pegawai);
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

                        if (totalmenit >= 30 && totalmenit < 55) {
                            tmp = 0.5;
                        } else if (totalmenit > 55) {
                            tmp = 1.00;
                        }
                    }
                    // Jika ada pengajuan lembur sebelum jam kerja
                    if (lAbsen.get(i).getHour_before() != null && lAbsen.get(i).getHour_before() != 0) {
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
                    if (lAbsen.get(i).getHour_after() != null && lAbsen.get(i).getHour_after() != 0) {
                        if (((double) totaljam) + tmp < (double) lAbsen.get(i).getHour_after()) {
                            totallembur += ((double) totaljam) + tmp;
                        } else {
                            totallembur += (double) lAbsen.get(i).getHour_after();
                        }
                    }

                    //jika ada pengajuan lembur pada saat jam istirahat
                    if (lAbsen.get(i).getBreak_time() != null && lAbsen.get(i).getBreak_time() != 0 && lAbsen.get(i).getJam_break() != null) {
                        DateFormat jam = new SimpleDateFormat("HH:mm");
                        String jamnya = jam.format(lAbsen.get(i).getJam_break());
                        // System.out.println(jamnya.substring(3, 4));
                        totalmenit = (long) Integer.parseInt(jamnya.substring(3, 4));
                        totaljam = (long) Integer.parseInt(jamnya.substring(0, 2));
                        tmp = 0.00;
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
                    if (lAbsen.get(i).getHour_before() != null || lAbsen.get(i).getHour_after() != null) {
                        if (lAbsen.get(i).getHour_before() == null) {
                            lAbsen.get(i).setHour_before(0);
                        }

                        if (lAbsen.get(i).getHour_after() == null) {
                            lAbsen.get(i).setHour_after(0);
                        }

                        Double totalnya = (double) lAbsen.get(i).getHour_before() + lAbsen.get(i).getHour_after();
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
        return grandtotal * tunjanganTetap * 1 / 173;
    }

    /**
     * Perhitungan pph Penghasilan sampai dengan Rp50.000.000 per tahun
     * dikenakan tarif pajak sebesar 5%. Penghasilan Rp50.000.000,-sampai dengan
     * Rp250.000.000,- per tahun dikenakan tarif pajak sebesar 15%. Penghasilan
     * Rp250.000.000,- sampai Rp500.000.000,- per tahun dikenakan tarif sebesar
     * 25%. Penghasilan di atas Rp500.000.000,- per tahun dikenakan tarif pajak
     * sebesar 30%.
     */
    @Transactional(readOnly = true)
    public Double calculatePph(Payroll item, PayrollDetail payrollDetail, Pegawai pegawai, Double thrnya) throws ParseException {
        Double tarifptkp = 0.00;
        Double pph21 = 0.00;
        Integer pengali;
        // uncheck for check employee
        //if (payrollDetail.getId_pegawai().equals("CPA-226")) {
        tarifptkp = mapperPegawai.tarifPtkpPegawai(item.getYear(), payrollDetail.getId_pegawai());
        if (tarifptkp != null) {
            pengali = 1;
            if (pegawai.getMulai_bekerja() != null) {
                Date mulaibekerja = pegawai.getMulai_bekerja();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date awaltahun = sdf.parse(item.getYear() + "-01-20");
                if (mulaibekerja.compareTo(awaltahun) < 0) {
                    pengali = 12;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mulaibekerja);
                    Integer month = calendar.get(Calendar.MONTH);
                    pengali = 11 - month;
                }
            } else {
                pengali = 12;
            }

            Double tunjangan = payrollDetail.getGaji_pokok()
                    + payrollDetail.getTunjangan_jabatan()
                    + payrollDetail.getUmt()
                    + payrollDetail.getTunjangan_komunikasi()
                    + payrollDetail.getInsentif_kehadiran()
                    + payrollDetail.getInsentif_penjualan()
                    + payrollDetail.getInsentif_produktifitas()
                    + payrollDetail.getTunjangan_bbm()
                    + payrollDetail.getTunjangan_kesehatan()
                    + payrollDetail.getOvertime()
                    + thrnya;
            Double penambah = payrollDetail.getJkk()
                    + payrollDetail.getJkm()
                    + payrollDetail.getBpjs_kes_perusahaan()
                    + payrollDetail.getPrudential_perusahaan();

            //// ///
            Double penghasilanbruto = payrollDetail.getGaji_pokok()
                    + payrollDetail.getTunjangan_jabatan()
                    + payrollDetail.getUmt()
                    + payrollDetail.getTunjangan_komunikasi()
                    + payrollDetail.getInsentif_kehadiran()
                    + payrollDetail.getInsentif_penjualan()
                    + payrollDetail.getInsentif_produktifitas()
                    + payrollDetail.getTunjangan_bbm()
                    + payrollDetail.getTunjangan_kesehatan()
                    + payrollDetail.getOvertime()
                    + thrnya
                    + payrollDetail.getJkk()
                    + payrollDetail.getJkm()
                    + payrollDetail.getBpjs_kes_perusahaan()
                    + payrollDetail.getPrudential_perusahaan();

            Double biayajabatan = penghasilanbruto * 5 / 100;
            //biaya jabatan maksimum 6 jt pertahun atau 500.000 per bulan
            if (biayajabatan > 500000.00) {
                biayajabatan = 500000.00;
            }
            Double pengurangan = biayajabatan + payrollDetail.getJp_pekerja() + payrollDetail.getJht_pekerja();
            Double penghasilanneto = (penghasilanbruto - pengurangan) * pengali;
            Double penghasilankenapajaksetahun = penghasilanneto - tarifptkp;
/**
            if (payrollDetail.getId_pegawai().equals("CPA - 229")) {
                System.out.println("Rincian Gaji an : " + pegawai.getNama());
                System.out.println("Pengali (bulan) : " + pengali);
                System.out.println("");
                DecimalFormat df = new DecimalFormat("###,###,###");

//            System.out.println("Tunjangan Jabatan : " + df.format(payrollDetail.getTunjangan_jabatan()) );
//            System.out.println("Overtime : " + df.format(payrollDetail.getOvertime()));
//            System.out.println("UMT : " + df.format(payrollDetail.getUmt()));
//            System.out.println("Insetntif Liter : " + df.format(payrollDetail.getInsentif_penjualan()));
//            System.out.println("Insentif Komunikasi : " + df.format(payrollDetail.getTunjangan_komunikasi()));
//            System.out.println("UMT : " + df.format(payrollDetail.getUmt()));
//            
//            System.out.println("Gaji setahun : " + df.format((penambah + tunjangan) * 12));
                System.out.println("Gaji Pokok : " + df.format(payrollDetail.getGaji_pokok()));
                System.out.println("Tunjangan Sebulan : " + df.format(penghasilanbruto - payrollDetail.getGaji_pokok() - penambah));
                System.out.println("Jamsostek : " + df.format(penambah));
                System.out.println("Gaji setahun : " + df.format((penambah + tunjangan) * pengali));
                System.out.println("Penghasilan Bruto : " + df.format(penghasilanbruto));
                System.out.println("Potongan Jabatan : " + df.format(biayajabatan * pengali));
                System.out.println("Jht dan iuran pensiun : " + df.format(payrollDetail.getJp_pekerja() + payrollDetail.getJht_pekerja()));
                System.out.println("Jht dan iuran pensiun Setahun: " + df.format((payrollDetail.getJp_pekerja() + payrollDetail.getJht_pekerja()) * pengali));
                System.out.println("Penghasilan Netto Sebulan : " + df.format(penghasilanneto / pengali));
                System.out.println("PTKP : " + df.format(tarifptkp));
                System.out.println("PKP Atas Gaji : " + df.format(penghasilankenapajaksetahun));
                if (penghasilankenapajaksetahun > 0) {

                    if (penghasilankenapajaksetahun > 500000000.00) {
                        System.out.println("");
                        System.out.println("PKP Atas Gaji > 50.0000.000");
                        System.out.println("");
                        pph21 = (penghasilankenapajaksetahun - 500000000.00) * 30 / 100;
                        System.out.println(df.format(penghasilankenapajaksetahun - 500000000.00) + " X 30% = " + df.format(pph21));
                        pph21 = pph21 + (250000000 * 25 / 100);
                        System.out.println("250.000.000 X 25% = " + df.format(250000000 * 25 / 100));
                        pph21 = pph21 + (190000000 * 15 / 100);
                        System.out.println("190.000.000 X 15% = " + df.format(190000000 * 15 / 100));
                        pph21 = pph21 + (60000000 * 5 / 100);
                        System.out.println("60.000.000 X 5% = " + df.format(60000000 * 5 / 100));
                    } else if (penghasilankenapajaksetahun > 250000000 && penghasilankenapajaksetahun <= 500000000.00) {
                        System.out.println("");
                        System.out.println("PKP Atas Gaji > 250.000.000 && PKP Atas Gaji <= 500.000.000");
                        System.out.println("");
                        pph21 = (penghasilankenapajaksetahun - 250000000) * 25 / 100;
                        System.out.println(df.format(penghasilankenapajaksetahun - 250000000) + " X 25% = " + df.format(pph21));
                        System.out.println("190.000.000 X 15% = " + df.format(190000000 * 15 / 100));
                        System.out.println("60.000.000 X 5% = " + df.format(60000000 * 5 / 100));
                        pph21 = pph21 + 28500000 + 3000000;
                    } else if (penghasilankenapajaksetahun > 60000000 && penghasilankenapajaksetahun <= 250000000.00) {
                        System.out.println("");
                        System.out.println("PKP Atas Gaji > 60.000.000 && PKP Atas Gaji <= 250.000.000");
                        System.out.println("");
                        pph21 = (penghasilankenapajaksetahun - 60000000) * 15 / 100;
                        System.out.println("" + df.format(penghasilankenapajaksetahun - 60000000) + " x 15%");
                        System.out.println("= " + df.format(pph21));
                        pph21 = pph21 + (60000000 * 5 / 100);
                        System.out.println("60.000.000 X 5% = " + df.format(60000000 * 5 / 100));
                    } else {
                        pph21 = penghasilankenapajaksetahun * 5 / 100;
                    }
                    // pajak perbulan
                    System.out.println("PPH Setahun:" + df.format(pph21));
                    System.out.println("PPH (" + df.format(pph21) + "/" + pengali + ") = " + df.format(pph21 / pengali));
                    pph21 = pph21 / pengali;
                    pph21 =0.00;
                }
            }
**/
            if (penghasilankenapajaksetahun > 0) {

                if (penghasilankenapajaksetahun > 500000000.00) {
                    pph21 = (penghasilankenapajaksetahun - 500000000.00) * 30 / 100;
                    pph21 = pph21 + (250000000 * 25 / 100);
                    pph21 = pph21 + (190000000 * 15 / 100);
                    pph21 = pph21 + (60000000 * 5 / 100);
                } else if (penghasilankenapajaksetahun > 250000000 && penghasilankenapajaksetahun <= 500000000.00) {
                    pph21 = (penghasilankenapajaksetahun - 250000000) * 25 / 100;
                    pph21 = pph21 + 28500000 + 3000000;
                    //pph21 = pph21 + (60000000 * 5 / 100);
                } else if (penghasilankenapajaksetahun > 60000000 && penghasilankenapajaksetahun <= 250000000.00) {
                    pph21 = (penghasilankenapajaksetahun - 60000000) * 15 / 100;
                    pph21 = pph21 + (60000000 * 5 / 100);
                } else {
                    pph21 = penghasilankenapajaksetahun * 5 / 100;
                }
                // pajak perbulan
                pph21 = pph21 / pengali;
            }
        }
        //}
        return pph21;
    }

    public ProposalLoanPaid selectOneByPeriode(String year, Integer month) {
        return mapperProposalLoanPaid.selectOneByPeriode(year, month);
    }

    public Boolean checkThr(String year, Integer month) {
        if (referensi.checkThr(month, Integer.parseInt(year)) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkTunjangan(String year, Integer month, String jenis) {
        if (referensi.checkTunjangan(year, month, jenis) == null) {
            return false;
        } else {
            return true;
        }
    }
}
