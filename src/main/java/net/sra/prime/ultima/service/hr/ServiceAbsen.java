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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsen;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenPeriode;
import net.sra.prime.ultima.db.mapper.hr.MapperHariLibur;
import net.sra.prime.ultima.db.mapper.hr.MapperJadwalKerja;
import net.sra.prime.ultima.db.mapper.hr.MapperLembur;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import net.sra.prime.ultima.entity.hr.LemburKaryawan;
import net.sra.prime.ultima.entity.hr.LemburMultiplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import begawi.BegawiDate;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceAbsen {

    @Autowired
    MapperAbsen referensi;

    @Autowired
    MapperJadwalKerja mapperJadwalKerja;

    @Autowired
    MapperLembur mapperLembur;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperHariLibur mapperHariLibur;

    @Autowired
    MapperAbsenPeriode mapperAbsenPeriode;

    @Transactional(readOnly = true)
    public List<Absen> onLoadList(String id_pegawai, Date dateStart, Date dateEnd) {
        return referensi.selectAll(id_pegawai, dateStart, dateEnd);
    }

    @Transactional(readOnly = true)
    public List<Absen> onLoadListAbsen(String id_pegawai, Date dateStart, Date dateEnd) {
        return referensi.selectAllHrAbsen(id_pegawai, dateStart, dateEnd);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(List<Absen> lAbsen, Integer urut) {
        Absen absen = new Absen();
        for (int i = 0; i < lAbsen.size(); i++) {
            absen=referensi.selectOne(lAbsen.get(i).getId_absen(), lAbsen.get(i).getTanggal());
            if (absen == null) {
                referensi.insert(lAbsen.get(i));
            } else {
                referensi.updateUpload(lAbsen.get(i));
            }
        }

        // mengambil no absen yang baru diupload
        List<Integer> lUrut = referensi.listNomorAbsen(urut);
        // update id_jadwal_kerja semua karywan yg baru diupload
        for (int i = 0; i < lUrut.size(); i++) {
            referensi.updateIdJadwalKerja(lUrut.get(i));
        }
    }

    @Transactional(readOnly = false)
    public void update(Absen absen) {
        referensi.update(absen);
    }

    @Transactional(readOnly = false)
    public void insertOne(Absen absen) {
        referensi.insert(absen);
        referensi.updateIdJadwalKerja(absen.getId_absen());
    }

    @Transactional(readOnly = false)
    public void insertSubmit(List<Absen> lAbsen, String create_by, String id_pegawai, Integer month, String year, Character statusnya, Character begawiUmt) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Pegawai pegawai = mapperPegawai.selectOne(id_pegawai);
        if (begawiUmt.equals('A') && pegawai.getAktifasiumt() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, pegawai.getNama() + " tidak mendapatkan UMT karena UMT belum diaktifkan !!!", ""));

        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(pegawai.getAktifasiumt());
//        calendar.add(Calendar.DATE, -1);
//        Date yesterday = calendar.getTime();

        AbsenPegawaiStatus absenPegawaiStatus = new AbsenPegawaiStatus();
        absenPegawaiStatus.setTerlambat(0);
        absenPegawaiStatus.setPulangcepat(0);
        absenPegawaiStatus.setLupaabsen(0);
        absenPegawaiStatus.setSakit(0);
        absenPegawaiStatus.setIjin(0);
        absenPegawaiStatus.setAbsen(0);
        absenPegawaiStatus.setVisitcustomer(0);
        absenPegawaiStatus.setOff(0);
        absenPegawaiStatus.setCuti(0);
        absenPegawaiStatus.setPerjalanandinas(0);
        absenPegawaiStatus.setCutibersama(0);
        absenPegawaiStatus.setUnpaidleave(0);
        absenPegawaiStatus.setTobepresent(0);
        absenPegawaiStatus.setVisitcustomer(0);
        absenPegawaiStatus.setLibur(0);
        absenPegawaiStatus.setTobepresentwithoutumt(0);
        absenPegawaiStatus.setVisitcustomerharilibur(0);
        absenPegawaiStatus.setUmt(0);
        Absen item = new Absen();
        JadwalKerja jk = new JadwalKerja();
        long terlambat = 0;
        long pulangcepat = 0;
        for (int i = 0; i < lAbsen.size(); i++) {

            item = lAbsen.get(i);

            if (i == 0) {
                if (item.getId_jadwal_kerja() == null) {
                    item.setId_jadwal_kerja(mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(item.getId_pegawai()).getId());
                }
                jk = mapperJadwalKerja.selectOne(item.getId_jadwal_kerja());
            }

            if (referensi.selectOne(item.getId_absen(), item.getTanggal()) == null) {
                item.setUrut(0);
                referensi.insertSubmit(item);
            } else {
                referensi.update(item);
            }
            if (item.getId_status_absen() != null && !item.getId_status_absen().isEmpty()) {
                switch (item.getId_status_absen()) {
                    case "A":
                        absenPegawaiStatus.setAbsen(absenPegawaiStatus.getAbsen() + 1);
                        break;
                    case "I":
                        absenPegawaiStatus.setIjin(absenPegawaiStatus.getIjin() + 1);
                        break;
                    case "S":
                        absenPegawaiStatus.setSakit(absenPegawaiStatus.getSakit() + 1);
                        break;
                    case "M":
                        //if (!item.getSchedule().equals('o')) {
                        absenPegawaiStatus.setTobepresent(absenPegawaiStatus.getTobepresent() + 1);
                        //}
                        break;
                    case "MW":
                        absenPegawaiStatus.setTobepresentwithoutumt(absenPegawaiStatus.getTobepresentwithoutumt() + 1);
                        break;
                    case "LA":
                        absenPegawaiStatus.setLupaabsen(absenPegawaiStatus.getLupaabsen() + 1);
                        break;
                    case "CT":
                        absenPegawaiStatus.setCuti(absenPegawaiStatus.getCuti() + 1);
                        break;
                    case "CB":
                        absenPegawaiStatus.setCutibersama(absenPegawaiStatus.getCutibersama() + 1);
                        break;
                    case "UL":
                        absenPegawaiStatus.setUnpaidleave(absenPegawaiStatus.getUnpaidleave() + 1);
                        break;
                    case "OFF":
                        absenPegawaiStatus.setOff(absenPegawaiStatus.getOff() + 1);
                        break;
                    case "VC":
                        absenPegawaiStatus.setVisitcustomer(absenPegawaiStatus.getVisitcustomer() + 1);
                        break;
                    case "M-DL":
                        absenPegawaiStatus.setVisitcustomerharilibur(absenPegawaiStatus.getVisitcustomerharilibur() + 1);
                        break;
                }

                /**
                 * Menghitung jumlah terlambat dan datang cepat jika sudah
                 * terlambat maka pulang cepat tidak dihitung
                 */
                if (item.getId_status_absen().equals("M")) {
                    // jika hari lubr maka tidak usah dihitung terlambatanya
                    if (!item.getSchedule().equals('o')) {
                        terlambat = item.getJam_masuk().getTime() - item.getTime_in().getTime();
                        terlambat = terlambat / (60 * 1000) % 60;

                        pulangcepat = item.getTime_out().getTime() - item.getJam_keluar().getTime();
                        pulangcepat = pulangcepat / (60 * 1000) % 60;
                        if ((int) (long) terlambat > jk.getBatas_terlambat() || (int) (long) pulangcepat > jk.getBatas_pulang()) {
                            absenPegawaiStatus.setTerlambat(absenPegawaiStatus.getTerlambat() + 1);
                        }

//                    if ((int) (long) pulangcepat > jk.getBatas_pulang()) {
//                        absenPegawaiStatus.setPulangcepat(absenPegawaiStatus.getPulangcepat() + 1);
//                    }
                    }

                }
                /**
                 * Hitung UMT
                 */

                if (begawiUmt.equals('A')) {
                    if (pegawai.getAktifasiumt() != null) {
                        if (item.getId_status_absen().equals("M") || item.getId_status_absen().equals("VC") || item.getId_status_absen().equals("M-DL")) {
                            if (BegawiDate.dayBeforeAfter(pegawai.getAktifasiumt(), -1).before(lAbsen.get(i).getTanggal())) {
                                absenPegawaiStatus.setUmt(absenPegawaiStatus.getUmt() + 1);
                            }
                        }
                    }
                }

            } else {
                /**
                 * *Menghitung hari libur
                 */
                if (item.getSchedule().equals('o')) {
                    absenPegawaiStatus.setLibur(absenPegawaiStatus.getLibur() + 1);
                }
            }

        }

        absenPegawaiStatus.setCreate_by(create_by);
        absenPegawaiStatus.setId_pegawai(id_pegawai);
        absenPegawaiStatus.setMonth(month);
        absenPegawaiStatus.setYear(year);
        absenPegawaiStatus.setStatus(statusnya);
        referensi.deletePegawaiStatus(id_pegawai, month, year);
        referensi.insertAbsenPegawaiStatus(absenPegawaiStatus);
    }

    @Transactional(readOnly = false)
    public void postingAll1(List<Pegawai> lPegawai, Integer month, String year, Character begawiUmt) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        List<Absen> lAbsen = new ArrayList<>();
        JadwalKerja jk = new JadwalKerja();

        for (int i = 0; i < lPegawai.size(); i++) {
            AbsenPegawaiStatus aps = referensi.selectOnePegawaiStatus(lPegawai.get(i).getId_pegawai(), month, year);
            if (aps != null) {
                if (aps.getStatus().equals('D')) {

                    Pegawai pegawai = mapperPegawai.selectOne(lPegawai.get(i).getId_pegawai());
                    if (begawiUmt.equals('A')) {
                        if (pegawai.getAktifasiumt() == null) {
                            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, pegawai.getNama() + " tidak mendapatkan UMT karena UMT belum diaktifkan !!!", " "));
                        }
                        AbsenPegawaiStatus absenPegawaiStatus = new AbsenPegawaiStatus();
                        absenPegawaiStatus.setTerlambat(0);
                        absenPegawaiStatus.setPulangcepat(0);
                        absenPegawaiStatus.setLupaabsen(0);
                        absenPegawaiStatus.setSakit(0);
                        absenPegawaiStatus.setIjin(0);
                        absenPegawaiStatus.setAbsen(0);
                        absenPegawaiStatus.setVisitcustomer(0);
                        absenPegawaiStatus.setOff(0);
                        absenPegawaiStatus.setCuti(0);
                        absenPegawaiStatus.setPerjalanandinas(0);
                        absenPegawaiStatus.setCutibersama(0);
                        absenPegawaiStatus.setUnpaidleave(0);
                        absenPegawaiStatus.setTobepresent(0);
                        absenPegawaiStatus.setVisitcustomer(0);
                        absenPegawaiStatus.setLibur(0);
                        absenPegawaiStatus.setTobepresentwithoutumt(0);
                        absenPegawaiStatus.setVisitcustomerharilibur(0);
                        absenPegawaiStatus.setUmt(0);
                        Absen item = new Absen();
                        AbsenPeriode absenPeriode = mapperAbsenPeriode.selectOne(year, month);
                        lAbsen = referensi.selectAllHrAbsen(lPegawai.get(i).getId_pegawai(), absenPeriode.getStart(), absenPeriode.getEnd_date());
                        long terlambat = 0;
                        long pulangcepat = 0;
                        jk = mapperJadwalKerja.selectOne(mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(lPegawai.get(i).getId_pegawai()).getId());

                        for (int j = 0; j < lAbsen.size(); j++) {

                            item = lAbsen.get(j);
                            if (item.getId_status_absen() != null && !item.getId_status_absen().isEmpty()) {
                                switch (item.getId_status_absen()) {
                                    case "A":
                                        absenPegawaiStatus.setAbsen(absenPegawaiStatus.getAbsen() + 1);
                                        break;
                                    case "I":
                                        absenPegawaiStatus.setIjin(absenPegawaiStatus.getIjin() + 1);
                                        break;
                                    case "S":
                                        absenPegawaiStatus.setSakit(absenPegawaiStatus.getSakit() + 1);
                                        break;
                                    case "M":
                                        //if (!item.getSchedule().equals('o')) {
                                        absenPegawaiStatus.setTobepresent(absenPegawaiStatus.getTobepresent() + 1);
                                        //}
                                        break;
                                    case "MW":
                                        absenPegawaiStatus.setTobepresentwithoutumt(absenPegawaiStatus.getTobepresentwithoutumt() + 1);
                                        break;
                                    case "LA":
                                        absenPegawaiStatus.setLupaabsen(absenPegawaiStatus.getLupaabsen() + 1);
                                        break;
                                    case "CT":
                                        absenPegawaiStatus.setCuti(absenPegawaiStatus.getCuti() + 1);
                                        break;
                                    case "CB":
                                        absenPegawaiStatus.setCutibersama(absenPegawaiStatus.getCutibersama() + 1);
                                        break;
                                    case "UL":
                                        absenPegawaiStatus.setUnpaidleave(absenPegawaiStatus.getUnpaidleave() + 1);
                                        break;
                                    case "OFF":
                                        absenPegawaiStatus.setOff(absenPegawaiStatus.getOff() + 1);
                                        break;
                                    case "VC":
                                        absenPegawaiStatus.setVisitcustomer(absenPegawaiStatus.getVisitcustomer() + 1);
                                        break;
                                    case "M-DL":
                                        absenPegawaiStatus.setVisitcustomerharilibur(absenPegawaiStatus.getVisitcustomerharilibur() + 1);
                                        break;
                                }

                                /**
                                 * Menghitung jumlah terlambat dan datang cepat
                                 * jika sudah terlambat maka pulang cepat tidak
                                 * dihitung
                                 */
                                if (item.getId_status_absen().equals("M")) {
                                    // jika hari lubr maka tidak usah dihitung terlambatanya
                                    if (!item.getSchedule().equals('o')) {
                                       // System.out.println(lPegawai.get(i).getId_pegawai());
                                        terlambat = item.getJam_masuk().getTime() - item.getTime_in().getTime();
                                        terlambat = terlambat / (60 * 1000) % 60;

                                        pulangcepat = item.getTime_out().getTime() - item.getJam_keluar().getTime();
                                        pulangcepat = pulangcepat / (60 * 1000) % 60;
                                        if ((int) (long) terlambat > jk.getBatas_terlambat() || (int) (long) pulangcepat > jk.getBatas_pulang()) {
                                            absenPegawaiStatus.setTerlambat(absenPegawaiStatus.getTerlambat() + 1);
                                        }

//                    if ((int) (long) pulangcepat > jk.getBatas_pulang()) {
//                        absenPegawaiStatus.setPulangcepat(absenPegawaiStatus.getPulangcepat() + 1);
//                    }
                                    }

                                }
                                /**
                                 * Hitung UMT
                                 */

                                if (begawiUmt.equals('A')) {
                                    if (pegawai.getAktifasiumt() != null) {
                                        if (item.getId_status_absen().equals("M") || item.getId_status_absen().equals("VC") || item.getId_status_absen().equals("M-DL")) {
                                            if (BegawiDate.dayBeforeAfter(pegawai.getAktifasiumt(), -1).before(lAbsen.get(j).getTanggal())) {
                                                absenPegawaiStatus.setUmt(absenPegawaiStatus.getUmt() + 1);
                                            }
                                        }
                                    }
                                }

                            } else {
                                /**
                                 * *Menghitung hari libur
                                 */
                                if (item.getSchedule().equals('o')) {
                                    absenPegawaiStatus.setLibur(absenPegawaiStatus.getLibur() + 1);
                                }
                            }

                        }
                        absenPegawaiStatus.setStatus('P');
                        absenPegawaiStatus.setCreate_by(aps.getCreate_by());
                        absenPegawaiStatus.setCreate_date(aps.getCreate_date());
                        absenPegawaiStatus.setId_pegawai(lPegawai.get(i).getId_pegawai());
                        absenPegawaiStatus.setMonth(month);
                        absenPegawaiStatus.setYear(year);
                        referensi.deletePegawaiStatus(lPegawai.get(i).getId_pegawai(), month, year);
                        referensi.insertAbsenPegawaiStatus(absenPegawaiStatus);

                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public Integer urutMax() {
        return referensi.urutMax();
    }

    @Transactional(readOnly = true)
    public Absen selectOne(Integer id_absen, Date tanggal) {
        return referensi.selectOne(id_absen, tanggal);
    }

    @Transactional(readOnly = true)
    public Absen selectOneAbsen(Date tanggal, String id_pegawai) {
        return referensi.selectOneAbsen(tanggal, id_pegawai);
    }

    @Transactional(readOnly = true)
    public JadwalKerja selectOneJadwalKerja(String id_pegawai) {
        return mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(id_pegawai);
    }

    @Transactional(readOnly = true)
    public JadwalKerja selectOneJadwalKerjanya(Integer id) {
        return mapperJadwalKerja.selectOne(id);
    }

    @Transactional(readOnly = true)
    public JadwalKerjaWaktu selectOneJadwalKerjaWaktu(Integer id, Integer day) {
        return mapperJadwalKerja.selectOnelJadwalKerjaWaktu(id, day);
    }

    @Transactional(readOnly = true)
    public List<LemburKaryawan> onLoadListLembuKaryawan(String id_pegawai, Date dateStart, Date dateEnd) {
        return mapperLembur.selectLemburKaryawan(id_pegawai, dateStart, dateEnd);
    }

    @Transactional(readOnly = true)
    public JadwalKerjaWaktu selectOneDay(String id_pegawai, Integer hari) {
        return mapperJadwalKerja.selectOneDay(id_pegawai, hari);
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String id_pegawai) {
        return mapperPegawai.selectOne(id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<LemburMultiplier> selectMultiplierByJabatan(Integer id_jabatan, Character jenis) {
        return mapperLembur.selectMultiplierbyJabatan(id_jabatan, jenis);
    }

    @Transactional(readOnly = true)
    public AbsenPegawaiStatus selectOnePegawaiStatus(String id_pegawai, Integer month, String year) {
        return referensi.selectOnePegawaiStatus(id_pegawai, month, year);
    }

    @Transactional(readOnly = false)
    public void deletePegawaiStatus(String id_pegawai, Integer month, String year) {
        referensi.deletePegawaiStatus(id_pegawai, month, year);
    }

    @Transactional(readOnly = true)
    public HariLibur selectHariLibur(Date tanggal) {
        return mapperHariLibur.selectOneByDate(tanggal);
    }

    @Transactional(readOnly = true)
    public List<Pegawai> selectAllPegawaiAbsen(Integer month, String year) {
        return referensi.selectAllAbsen(month, year);
    }

    @Transactional(readOnly = true)
    public AbsenPeriode selectOneAbsenPeriode(String year, Integer month) {
        return mapperAbsenPeriode.selectOne(year, month);
    }

    @Transactional(readOnly = true)
    public AbsenPeriode selectOneAbsenPeriodeByDate(Date date) {
        return mapperAbsenPeriode.selectOneByDate(date);
    }

    @Transactional(readOnly = false)
    public void autoGenerateAbsen(Date tanggalmulai, String id_pegawai, Character begawi) throws IOException {
        LocalDate localDate = tanggalmulai.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        long terlambat = 0;
        long pulangcepat = 0;
        AbsenPeriode absenPeriode = mapperAbsenPeriode.selectOne(Integer.toString(year), month);

        JadwalKerja jadwalKerja = mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(id_pegawai);
        Pegawai pegawai = mapperPegawai.selectOneDataDiri(id_pegawai);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long duration;

        if (jadwalKerja != null) {
            Calendar cal1 = Calendar.getInstance();
            Integer hari;

            // jika autogenerate absen =1 : hapus semua absen dalam rentang waktu sesuai dengan periode absen
            if (begawi.equals('1')) {
                referensi.deletebyDate(pegawai.getAbsen(), absenPeriode.getStart(), absenPeriode.getEnd_date());
            }

            List<Absen> lAbsen = referensi.selectAll(id_pegawai, absenPeriode.getStart(), absenPeriode.getEnd_date());
            JadwalKerjaWaktu jadwalKerjaWaktu = new JadwalKerjaWaktu();
            Absen absen = new Absen();
            Absen absi = new Absen();
            for (int i = 0; i < lAbsen.size(); i++) {
                // jika autogenerateabsen=2
                if (begawi.equals('2')) {
                    absi = referensi.selectOne(pegawai.getAbsen(), lAbsen.get(i).getTanggal());
                    if (absi != null) {
                        // jika ada salah terisi naka skip loop
                        if (absi.getId_status_absen() != null || absi.getJam_masuk() != null || absi.getJam_keluar() != null) {
                            continue;
                        } else {
                            referensi.deleteOneDate(pegawai.getAbsen(), lAbsen.get(i).getTanggal());
                        }
                    }
                }
                absen = lAbsen.get(i);
                absen.setId_absen(pegawai.getAbsen());
                absen.setId_pegawai(id_pegawai);
                cal1.setTime(absen.getTanggal());
                if (cal1.get(Calendar.DAY_OF_WEEK) == 1) {
                    hari = 7;
                } else {
                    hari = cal1.get(Calendar.DAY_OF_WEEK) - 1;
                }

                if (jadwalKerja.getJenis().equals('m')) {
                    if (jadwalKerja.getCycle() == 1) {
                        jadwalKerjaWaktu = mapperJadwalKerja.selectOnelJadwalKerjaWaktu(jadwalKerja.getId(), hari);
                        if (jadwalKerjaWaktu.getSelected()) {
                            HariLibur hariLibur = mapperHariLibur.selectOneByDate(absen.getTanggal());
                            if (hariLibur == null) {
                                absen.setSchedule('p'); // present
                                absen.setSchedule_des(dateFormat.format(jadwalKerjaWaktu.getTime_in()) + " - " + dateFormat.format(jadwalKerjaWaktu.getTime_out()));
                                absen.setTime_in(jadwalKerjaWaktu.getTime_in());
                                absen.setTime_out(jadwalKerjaWaktu.getTime_out());
                                absen.setTime_break(jadwalKerjaWaktu.getTime_break());
                                absen.setUrut(0);
                                /// buat otomatis jam masuk dan jam keluarnya mengikut jadwal

                                // set tipe absen A (Auto)
                                absen.setTipe_absen('A');
                                absen.setJam_masuk(jadwalKerjaWaktu.getTime_in());
                                absen.setJam_keluar(jadwalKerjaWaktu.getTime_out());
                                absen.setId_status_absen("M");
                                referensi.insert(absen);
                            }
                        }

                    }
                }
            }
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    @Transactional(readOnly = true)
    public CutiKaryawan selectOneCutiKaryawan(Integer absen, Integer id_cuti){
        return referensi.selectCutiKaryawan(absen, id_cuti);
    }

}
