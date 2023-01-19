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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsen;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenPeriode;
import net.sra.prime.ultima.db.mapper.hr.MapperCuti;
import net.sra.prime.ultima.db.mapper.hr.MapperHariLibur;
import net.sra.prime.ultima.db.mapper.hr.MapperJadwalKerja;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.Cuti;
import net.sra.prime.ultima.entity.hr.CutiJabatan;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;
import net.sra.prime.ultima.entity.hr.CutiMaster;
import net.sra.prime.ultima.entity.hr.CutiPersetujuan;
import net.sra.prime.ultima.entity.hr.CutiSub;
import net.sra.prime.ultima.entity.hr.CutiTahunan;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceCuti {

    @Autowired
    MapperCuti referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Autowired
    MapperAbsen mapperAbsen;

    @Autowired
    MapperAbsenPeriode mapperAbsenPeriode;

    @Autowired
    MapperJadwalKerja mapperJadwalKerja;

    @Autowired
    MapperHariLibur mapperHariLibur;

    @Transactional(readOnly = true)
    public List<Cuti> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        Integer jumlahCuti = referensi.jumlahCuti(id);
        Integer master = referensi.selectOne(id).getMaster();
        referensi.deleteCutiJabatan(id);
        referensi.delete(id);
        if (jumlahCuti <= 1) {
            referensi.deleteCutiMaster(master);
        }
    }

    @Transactional(readOnly = false)
    public void tambah(Cuti item, List<MasterJabatan> lMasterJabatans) {
        if (item.getStatus()) {
            CutiMaster cutiMaster = new CutiMaster();
            cutiMaster.setNama_master(item.getNama_cuti());
            referensi.insertCutiMaster(cutiMaster);
            item.setNama_cuti("Standar Perusahaan");
            item.setMaster(cutiMaster.getId());
        }
        referensi.insert(item);
        if (!item.getAll_employee()) {
            tambahCutiJabatan(lMasterJabatans, item.getId());
        }
    }

    public void tambahCutiJabatan(List<MasterJabatan> lMasterJabatans, Integer id) {
        CutiJabatan cutiJabatan = new CutiJabatan();
        for (int i = 0; i < lMasterJabatans.size(); i++) {
            cutiJabatan.setId_cuti(id);
            cutiJabatan.setId_jabatan(lMasterJabatans.get(i).getId_jabatan());
            referensi.insertCutiJabatan(cutiJabatan);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Cuti item, List<MasterJabatan> lMasterJabatans) {
        referensi.deleteCutiJabatan(item.getId());
        referensi.update(item);
        if (!item.getAll_employee()) {
            tambahCutiJabatan(lMasterJabatans, item.getId());
        }
    }

    @Transactional(readOnly = true)
    public Cuti onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<CutiJabatan> onLoadCutiJabatan(Integer id) {
        return referensi.selectAllCutiJabatan(id);
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadJabatan(Integer id) {
        return referensi.selectAllJabatan(id);
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadAllJabatan(Integer id) {
        return referensi.selectAllTblJabatan(id);
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadListJabatan() {
        return mapperHrJabatan.selectAll();
    }

    @Transactional(readOnly = true)
    public List<CutiMaster> onLoadListCutiMaster() {
        return referensi.selectAllCutiMaster();
    }

    @Transactional(readOnly = true)
    public String selectAllCutiKhusus() {
        return referensi.selectAllCutiKhusus();
    }

    ///////////////////// Cuti Karywan //////////////
    @Transactional(readOnly = true)
    public List<CutiKaryawan> onLoadListCutiKaryawan(Integer id_departemen, Integer id_jabatan, Integer id_cuti, Character status, Date awal, Date akhir) {
        return referensi.selectAllCutiKaryawan(id_departemen, id_jabatan, id_cuti, status, awal, akhir);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> myTimeOff(String tahun, String id_pegawai) {
        return referensi.myCuti(id_pegawai, tahun);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> myTimeOffOther(String tahun, String id_pegawai) {
        return referensi.myOtherCuti(id_pegawai, tahun);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> selectAllTimeOffByYear(String tahun) {
        return referensi.selectAllTimeOffByYear(tahun);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> otherTimeOff(String tahun, String id_pegawai) {
        return referensi.otherTimeOff(id_pegawai, tahun);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> onLoadListNotifikasiCutiKaryawan(String id_pegawai) {
        return referensi.selectNotifikasiCutiKaryawan(id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<CutiKaryawan> onLoadListNotifikasiCutiKaryawan1(String id_pegawai) {
        return referensi.selectNotifikasiCutiKaryawan1(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void deleteCutiKaryawan(Integer id) {
        referensi.deleteCutiKaryawan(id);

    }

    /// cuti karyawan dengan persetujuan bertingakat
    @Transactional(readOnly = false)
    public void tambahCutiKaryawan(CutiKaryawan item, Cuti ct, Integer id_departemen) {

        referensi.insertCutiKaryawan(item);
        // Jika persetujuan pertama tidak sama dengan tidak perlu persetujuan
        if (!ct.getPersetujuan1().equals('3')) {
            CutiPersetujuan cp = new CutiPersetujuan();
            cp.setId_cuti_karyawan(item.getId());
            // Jika persetujuan pertama adalah head departemen;
            if (ct.getPersetujuan1().equals('1')) {
                cp.setId_pegawai(mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai());
            } else if (ct.getPersetujuan1().equals('2')) {
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatan_persetujuan1()).getId_pegawai());
            }
            if (item.getStatus().equals('T') || item.getStatus().equals('W')) {
                cp.setStatus(Boolean.TRUE);
            }
            cp.setUrut(1);
            referensi.insertCutiPersetujuan(cp);

            // jika perlu persetujuan kedua
            if (ct.getPersetujuan2().equals('2')) {
                if (item.getStatus().equals('T')) {
                    cp.setStatus(Boolean.TRUE);
                }
                cp = new CutiPersetujuan();
                cp.setId_cuti_karyawan(item.getId());
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatan_persetujuan2()).getId_pegawai());
                cp.setUrut(2);
                referensi.insertCutiPersetujuan(cp);
            }

        }
        // Untuk menginput ke tabel absen
        if (item.getStatus().equals('A') || item.getStatus().equals('T')) {
            updateAbsen(item.getId(), item.getId_pegawai(), item.getStatus(), item.getId_master());
        }

    }

    @Transactional(readOnly = false)
    public void addTimeOff(CutiKaryawan item, Cuti ct, Integer id_departemen) {
        /*
        jika yg cuti adalah head departemen hr maka yang menyetujui hanya direktur
        jika yg cuti adalah supervisor hr maka yg menyetujui hanya atasan langsung (hr manager/1 persetujuan aja)
        jika yg cuti adalah General manager maka yang menyetujui hanya direktur saja(atasan langsung)
        jika yg cuti adalah head departemen selain head departemen hr yang menyetujui adalah manager hr dan atasan langsung
         
         */

        referensi.insertCutiKaryawan(item);

        Pegawai pegawai = mapperPegawai.selectOne(item.getId_pegawai());
        Pegawai pegawaiatasan = mapperPegawai.selectOne(pegawai.getAtasan_langsung());

        CutiPersetujuan cp = new CutiPersetujuan();
        // hr Manager dan supervisor hr
        if (pegawai.getId_jabatan_new() == 109 || pegawaiatasan.getId_jabatan_new() == 109) {
            // hr manager oleh direktur , supervisor hr oleh hr manager
            cp.setId_cuti_karyawan(item.getId());
            cp.setId_pegawai(pegawai.getAtasan_langsung());
            
            cp.setUrut(1);
            referensi.insertCutiPersetujuan(cp);
            // head departemen sales
        } else if (id_departemen == 102) {
            Pegawai pg = mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new());
            if (pg.getId_pegawai().equals(item.getId_pegawai())) {
                cp.setId_cuti_karyawan(item.getId());
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
                cp.setUrut(1);
                referensi.insertCutiPersetujuan(cp);

                // persetujuan kedua oleh direktur
                // cukup hR aja
                //cp.setId_pegawai(pegawai.getAtasan_langsung());
                //cp.setUrut(2);
                //referensi.insertCutiPersetujuan(cp);
            } else {
                // persetujuan 1 oleh atasan langsung
                cp.setId_cuti_karyawan(item.getId());
                cp.setId_pegawai(pegawai.getAtasan_langsung());
                cp.setUrut(1);
                referensi.insertCutiPersetujuan(cp);
                //jika atasan pertama adalah head departemen dan deprtemen sales
                if (mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new()).getId_pegawai().equals(pegawai.getAtasan_langsung())) {
                    // persejuan kedua (terakhir) oleh HRGA & Legal Supervisor 
                    cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
                    cp.setUrut(2);
                    referensi.insertCutiPersetujuan(cp);
                } else {
                    // persetujuan kedua oleh supervisor
                    cp.setId_pegawai(pegawaiatasan.getAtasan_langsung());
                    cp.setUrut(2);
                    referensi.insertCutiPersetujuan(cp);

                    if (pg.getId_pegawai().equals(pegawaiatasan.getAtasan_langsung())) {
                        // pesetujuan ke 3 (terkahir) oleh hr manager
                        cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());

                    } else {
                        cp.setId_pegawai(pg.getId_pegawai());
                    }
                    cp.setUrut(3);
                    referensi.insertCutiPersetujuan(cp);
                }
            }

            // head departemen  selain sales dan GM
        } else if ((id_departemen != 106 && mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(item.getId_pegawai())) || pegawai.getId_jabatan_new() == 102 || pegawai.getId_jabatan_new() == 103) {
            // persetjuan pertama oleh hr manager
            cp.setId_cuti_karyawan(item.getId());
            cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
            cp.setUrut(1);
            referensi.insertCutiPersetujuan(cp);

            // persetujuan kedua oleh direktur
            //cp.setId_pegawai(pegawai.getAtasan_langsung());
            //cp.setUrut(2);
            //referensi.insertCutiPersetujuan(cp);
        } else {
            // persetujuan 1 oleh atasan langsung
            cp.setId_cuti_karyawan(item.getId());
            cp.setId_pegawai(pegawai.getAtasan_langsung());
            cp.setUrut(1);
            referensi.insertCutiPersetujuan(cp);

            // jika atasan pertama adalah head departemen dan departemen selain sales
            if (mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(pegawai.getAtasan_langsung())) {
                // persejuan kedua (terakhir) oleh hr manager
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
                cp.setUrut(2);
                referensi.insertCutiPersetujuan(cp);
            } else {
                // persetujuan kedua oleh supervisor
                cp.setId_pegawai(pegawaiatasan.getAtasan_langsung());
                cp.setUrut(2);
                referensi.insertCutiPersetujuan(cp);

                // jika atasan ke 2 ada head departemen
                if (mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(pegawaiatasan.getAtasan_langsung())) {
                    // jika departemennya selain hr departemen
                    if (id_departemen != 106) {
                        // pesetujuan ke 3 (terkahir) oleh hr manager
                        cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
                        cp.setUrut(3);
                        referensi.insertCutiPersetujuan(cp);
                    }
                } else {
                    // persetjuan ke 3 oleh head departemen/manager
                    cp.setId_pegawai(mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai());
                    cp.setUrut(3);
                    referensi.insertCutiPersetujuan(cp);
                    //persetujuan keempat oleh hr manager
                    cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(120).getId_pegawai());
                    cp.setUrut(4);
                    referensi.insertCutiPersetujuan(cp);
                }
            }

        }
    }

    @Transactional(readOnly = false)
    public Character checkSchedule(String year, Integer month, String id_pegawai, Date tanggal) {
        Character schedule = 'p';

        /// Hari Libur
        HariLibur hariLibur = mapperHariLibur.selectOneByDate(tanggal);
        if (hariLibur != null) {
            schedule = 'o';
        } else {

            AbsenPeriode absenPeriode = mapperAbsenPeriode.selectOne(year, month);
            JadwalKerja jadwalKerja = mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(id_pegawai);

            if (jadwalKerja != null) {
                Calendar cal1 = Calendar.getInstance();
                Integer hari;
                cal1.setTime(tanggal);
                if (cal1.get(Calendar.DAY_OF_WEEK) == 1) {
                    hari = 7;
                } else {
                    hari = cal1.get(Calendar.DAY_OF_WEEK) - 1;
                }
                if (jadwalKerja.getJenis().equals('m')) {
                    if (jadwalKerja.getCycle() == 1) {

                    }
                }
                if (jadwalKerja.getJenis().equals('m')) {
                    if (jadwalKerja.getCycle() == 1) {
                        JadwalKerjaWaktu jadwalKerjaWaktu = mapperJadwalKerja.selectOnelJadwalKerjaWaktu(jadwalKerja.getId(), hari);
                        if (!jadwalKerjaWaktu.getSelected()) {
                            schedule = 'o';
                        }
                    }
                }

            }
        }
        return schedule;
    }

    @Transactional(readOnly = false)
    public void updateAbsen(Integer id_cuti, String id_pegawai, Character status, Integer master) {
        String status_absen;
        CutiKaryawan item = referensi.selectOneCutiKaryawan(id_cuti);

        Pegawai pegawai = mapperPegawai.selectOneDataDiri(id_pegawai);
        Absen abs = new Absen();
        abs.setId_absen(pegawai.getAbsen());
        abs.setId_cuti(item.getId());
        if (item.getNama_sub_cuti() != null) {
            abs.setKeterangan(item.getNama_sub_cuti());
        } else {
            abs.setKeterangan(item.getNama_cuti() + " (" + item.getAlasan() + ")");
        }
        switch (master) {
            case 1:
                abs.setId_status_absen("CT");
                break;
            case 2:
                abs.setId_status_absen("S");
                break;
            case 4:
                // cuti khusus
                abs.setId_status_absen("I");
                break;

        }

        if (item.getWaktu_cuti().equals('1')) {
            abs.setTanggal(item.getTanggal_awal());
            LocalDate localDate = item.getTanggal_awal().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            /// cek apakah cuti dihari libur
            //if (checkSchedule(Integer.toHexString(localDate.getYear()), localDate.getMonthValue(), id_pegawai, item.getTanggal_awal()).equals('p')) {
            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), abs.getTanggal());
            if (absenexist == null) {
                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
                abs.setTipe_absen('m');
                mapperAbsen.insertFromCutoff(abs);
            } else {
                // master = 3 --> Ijin
                if (master.equals(3)) {
                    // status absen tergantung jam masuk kerjanya jika belum ada absen maka akan nilai defaulnya UL yg diload pada modul attenda_clocks
                    status_absen = statusAbsen(absenexist);
                    if (!status_absen.equals("null")) {
                        abs.setId_status_absen(status_absen);
                    }
                }
                mapperAbsen.updateFromCutoff(abs);
            }
            //}
        } else {
            Date current = item.getTanggal_awal();
            abs.setTanggal(current);
            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), current);
            if (absenexist == null) {
                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
                abs.setTipe_absen('m');
                mapperAbsen.insertFromCutoff(abs);
            } else {
                // master = 3 --> Ijin
                if (master.equals(3)) {
                    status_absen = statusAbsen(absenexist);
                    if (!status_absen.equals("null")) {
                        abs.setId_status_absen(status_absen);
                    }
                }
                mapperAbsen.updateFromCutoff(abs);
            }

            while (current.before(item.getTanggal_akhir())) {
                //processDate(current);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);
                calendar.add(Calendar.DATE, 1);
                current = calendar.getTime();
                abs.setTanggal(current);
                absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), current);
                if (absenexist == null) {
                    abs.setTipe_absen('m');
                    mapperAbsen.insertFromCutoff(abs);
                } else {
                    // master = 3 --> Ijin
                    if (master.equals(3)) {
                        status_absen = statusAbsen(absenexist);
                        if (!status_absen.equals("null")) {
                            abs.setId_status_absen(status_absen);
                        }
                    }
                    mapperAbsen.updateFromCutoff(abs);
                }
            }

        }

    }

    @Transactional(readOnly = false)
    public void updateCutiCancel(Integer id_cuti, String id_pegawai,
            Integer master
    ) {

        CutiKaryawan item = referensi.selectOneCutiKaryawan(id_cuti);

        Integer id_absen = mapperPegawai.selectOneDataDiri(id_pegawai).getAbsen();
        Absen absenexist = mapperAbsen.selectOne(id_absen, item.getTanggal_awal());
        if (absenexist != null) {
            if ((master.equals(1) && absenexist.getId_status_absen().equals("CT"))
                    || master.equals(2) && absenexist.getId_status_absen().equals("S")
                    || master.equals(4) && absenexist.getId_status_absen().equals("I")) {
                referensi.timeOffCancel(item.getTanggal_awal(), id_absen);
            }
        }
        if (!item.getWaktu_cuti().equals('1')) {
            Date current = item.getTanggal_awal();
            while (current.before(item.getTanggal_akhir())) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);
                calendar.add(Calendar.DATE, 1);
                current = calendar.getTime();
                absenexist = mapperAbsen.selectOne(id_absen, current);
                if (absenexist != null) {
                    if ((master.equals(1) && absenexist.getId_status_absen().equals("CT"))
                            || master.equals(2) && absenexist.getId_status_absen().equals("S")
                            || master.equals(4) && absenexist.getId_status_absen().equals("I")) {
                        referensi.timeOffCancel(item.getTanggal_awal(), id_absen);
                    }
                }
            }

        }

    }

    @Transactional(readOnly = true)
    public String statusAbsen(Absen absenexist
    ) {
        String status_absen = "null";
        long terlambat;
        long pulangcepat;
        if (absenexist.getJam_masuk() == null && absenexist.getJam_keluar() == null) {
            status_absen = "UL";
        } else {
            if (!absenexist.getJam_masuk().equals(absenexist.getJam_keluar())) {
                // absen sudah ada dari fingerprint tetapi jadwal kerja belum ada maka status absen dikosongkan 
                // jika jadwal kerja sudah ada maka jalankan script dibawah
                if (absenexist.getTime_in() != null) {
                    terlambat = absenexist.getJam_masuk().getTime() - absenexist.getTime_in().getTime();
                    terlambat = terlambat / (60 * 1000);
                    if (terlambat < 0) {
                        terlambat = 0;
                    }
                    pulangcepat = absenexist.getTime_out().getTime() - absenexist.getJam_keluar().getTime();
                    pulangcepat = pulangcepat / (60 * 1000);
                    if (pulangcepat < 0) {
                        pulangcepat = 0;
                    }

                    if (terlambat + pulangcepat < 180) {
                        status_absen = "M";
                    } else if (terlambat + pulangcepat >= 180 && terlambat + pulangcepat < 240) {
                        status_absen = "MW";
                    } else if (terlambat + pulangcepat >= 240) {
                        status_absen = "UL";
                    }
                }
            } else {
                status_absen = "UL";
            }

        }
        return status_absen;
    }

    @Transactional(readOnly = true)
    public CutiKaryawan onLoadCutiKaryawan(Integer id
    ) {
        return referensi.selectOneCutiKaryawan(id);
    }

    @Transactional(readOnly = false)
    public void ubahCutiKaryawan(CutiKaryawan item
    ) {
        referensi.updateCutiKaryawan(item);
    }

    @Transactional(readOnly = true)
    public Cuti selectOneCutiFromMaster(Integer id_master, Integer id_jabatan
    ) {
        return referensi.selectOneCutiFromMaster(id_master, id_jabatan);
    }

    @Transactional(readOnly = true)
    public List<CutiSub> selectAllCutiSub() {
        return referensi.selectAllCutiSub();
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String id_pegawai
    ) {
        return referensi.selectOnePegawai(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updateStatusCutiKaryawan(Integer id_cuti, Boolean status,
            String id_pegawai, Character statusnya,
            String keterangan_status, String idPegawaiAbsen,
            Integer master
    ) {
        referensi.updateStatusCutiKaryawan(statusnya, id_cuti, keterangan_status);
        referensi.updatePersetujuaan(status, id_cuti, id_pegawai);
        // Jika statusnya Approve atau tidak perlu persetujuan
        if (statusnya.equals('A') || statusnya.equals('T')) {
            updateAbsen(id_cuti, idPegawaiAbsen, statusnya, master);
        }
    }

    @Transactional(readOnly = false)
    public void declineCutiKaryawan(Integer id_cuti, Character statusnya,
            String keterangan_status
    ) {
        CutiKaryawan ct = referensi.selectOneCutiKaryawan(id_cuti);
        // jika status cuti karyawan sudah disetujui maka kosongkan status absen
        if (ct.getStatus().equals('A')) {
            // untuk update absen
            updateCutiCancel(id_cuti, ct.getId_pegawai(), ct.getId_master());
        }
        referensi.updateStatusCutiKaryawan(statusnya, id_cuti, keterangan_status);

    }

    @Transactional(readOnly = true)
    public int countPersetujuan(Integer id_cuti_karyawaInteger
    ) {
        return referensi.countPersetujuan(id_cuti_karyawaInteger);
    }

    @Transactional(readOnly = true)
    public CutiKaryawan onLoadPersetujuankedua(Integer id, Integer urut
    ) {
        return referensi.selectPersetujuankedua(id, urut);
    }

    @Transactional(readOnly = true)
    public Integer searchPersetujuanBerurut(Integer id_cuti_karyawan, String id_pegawai,
            Integer urut
    ) {
        return referensi.searchPersetujuanBerurut(id_cuti_karyawan, id_pegawai, urut);
    }

    @Transactional(readOnly = true)
    public Integer searchPersetujuan(Integer id_cuti_karyawan, String id_pegawai
    ) {
        return referensi.searchPersetujuan(id_cuti_karyawan, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<CutiPersetujuan> selectAllCutiPersetujuan(Integer id_cuti_karyawan
    ) {
        return referensi.selectAllCutiPersetujuan(id_cuti_karyawan);
    }

    @Transactional(readOnly = true)
    public CutiPersetujuan selectMaxCutiPersetujuan(Integer id_cuti_karyawan
    ) {
        return referensi.selectMaxCutiPersetujuan(id_cuti_karyawan);
    }

    @Transactional(readOnly = true)
    public CutiPersetujuan cekCutiPersetujuan(Integer id_cuti_karyawan, String id_pegawai
    ) {
        return referensi.cekCutiPersetujuan(id_cuti_karyawan, id_pegawai);
    }

    @Transactional(readOnly = true)
    public Cuti selectOneCutiAllEmployee(Integer id_master
    ) {
        return referensi.selectOneCutiAllEmployee(id_master);
    }

    @Transactional(readOnly = true)
    public Character statusAbsen(Date date, String id_pegawai
    ) {
        AbsenPeriode absenPeriode = referensi.selectAbsenPeriode(date);
        if (absenPeriode != null) {
            return referensi.statusAbsen(absenPeriode.getMonth(), absenPeriode.getYear(), id_pegawai);
        } else {
            return null;
        }

    }

    @Transactional(readOnly = false)
    public void addNote(String note, Integer id
    ) {
        referensi.addNote(note, id);
    }
    
    @Transactional(readOnly = true)
    public HariLibur selectHariLibur(Date tanggal) {
        return mapperHariLibur.selectOneByDate(tanggal);
    }
    
    
    @Transactional(readOnly = false)
    public void updateCutiTahunan(String year,String id_pegawai, Integer jumlah,Integer saldo){
        if(referensi.checkCutitahunan(year, id_pegawai) > 0){
            referensi.updateCutiTahunan(year, id_pegawai, jumlah);
        }else{
            referensi.insertCutiTahunan(year, id_pegawai, jumlah,saldo);
        }
    }
    
    @Transactional(readOnly = false)
    public void updateCutiTahunanHr(String year,String id_pegawai, Integer jumlah,Integer saldo){
        if(referensi.checkCutitahunan(year, id_pegawai) > 0){
            referensi.updateCutiTahunanSaldo(year, id_pegawai, jumlah,saldo);
        }else{
            referensi.insertCutiTahunan(year, id_pegawai, jumlah,saldo);
        }
    }
    
    @Transactional(readOnly = true)
    public CutiTahunan selectCutiTahunan(String year, String id_pegawai) {
        return referensi.selectCutitahunan(year, id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public List<CutiTahunan> selectAllCutiTahunan(String year) {
        return referensi.selectAllCutitahunan(year);
    }
    
}
