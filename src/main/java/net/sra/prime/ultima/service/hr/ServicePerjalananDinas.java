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
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsen;
import net.sra.prime.ultima.db.mapper.hr.MapperPerjalananDinas;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.PerjalananDinas;
import net.sra.prime.ultima.entity.hr.PerjalananDinasPersetujuan;
import net.sra.prime.ultima.entity.hr.SettingApproval;
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
public class ServicePerjalananDinas {

    @Autowired
    MapperPerjalananDinas referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Autowired
    MapperAbsen mapperAbsen;

    ///////////////////// Perjalanan Dinas //////////////
    @Transactional(readOnly = true)
    public List<PerjalananDinas> onLoadList(Integer id_departemen, Integer id_jabatan, Integer id_cuti, Character status, Date awal, Date akhir) {
        return referensi.selectAllPerjalananDinas(id_departemen, id_jabatan, id_cuti, status, awal, akhir);
    }

    @Transactional(readOnly = true)
    public List<PerjalananDinas> onLoadMyList(String id_pegawai,String tahun) {
        return referensi.selectMyPerjalananDinas(id_pegawai,tahun);
    }
    
    @Transactional(readOnly = true)
    public List<PerjalananDinas> onLoadOtherList(String id_pegawai,String tahun) {
        return referensi.selectOtherPerjalananDinas(id_pegawai,tahun);
    }

    @Transactional(readOnly = true)
    public List<PerjalananDinas> onLoadListNotifikasiPerjalananDinas(String id_pegawai) {
        return referensi.selectNotifikasiPerjalananDinas(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void deletePerjalananDinas(Integer id) {
        referensi.deletePerjalananDinas(id);

    }

    @Transactional(readOnly = false)
    public void tambahPerjalananDinas(PerjalananDinas item, SettingApproval ct, Integer id_departemen) {

        referensi.insertPerjalananDinas(item);
        // Jika persetujuan pertama tidak sama dengan tidak perlu persetujuan
        if (!ct.getPersetujuan1().equals('3')) {
            PerjalananDinasPersetujuan cp = new PerjalananDinasPersetujuan();
            cp.setId_perjalanan_dinas(item.getId());
            // Jika persetujuan pertama adalah head departemen;
            if (ct.getPersetujuan1().equals('1')) {
                cp.setId_pegawai(mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai());
            } else if (ct.getPersetujuan1().equals('2')) {
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan1()).getId_pegawai());
            }
            if (item.getStatus().equals('T') || item.getStatus().equals('W')) {
                cp.setStatus(Boolean.TRUE);
            }
            cp.setUrut(1);
            referensi.insertPerjalananDinasPersetujuan(cp);

            // jika perlu persetujuan kedua
            if (ct.getPersetujuan2().equals('2')) {
                if (item.getStatus().equals('T')) {
                    cp.setStatus(Boolean.TRUE);
                }
                cp = new PerjalananDinasPersetujuan();
                cp.setId_perjalanan_dinas(item.getId());
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan2()).getId_pegawai());
                cp.setUrut(2);
                referensi.insertPerjalananDinasPersetujuan(cp);
            }

        }
        // Untuk menginput ke tabel absen
//        if (item.getStatus().equals('A') || item.getStatus().equals('T')) {
//            updateAbsen(item.getId(), item.getId_pegawai(), item.getStatus());
//        }

    }

    @Transactional(readOnly = false)
    public void addMyBusinessTrip(PerjalananDinas item, SettingApproval ct) {
        Pegawai pegawai = mapperPegawai.selectOne(item.getId_pegawai());
        Integer i = 1;
        // Jika persetujuan pertama tidak sama dengan tidak perlu persetujuan
        if (!ct.getPersetujuan1().equals('3')) {
            PerjalananDinasPersetujuan cp = new PerjalananDinasPersetujuan();

            // Jika persetujuan pertama adalah head departemen;
            if (ct.getPersetujuan1().equals('1')) {
                item.setStatus('D');
                if (pegawai.getId_departemen_new().equals(102)) {
                    Pegawai dsm = mapperPegawai.selectHeadDepartemenByKantor(pegawai.getId_departemen_new(), pegawai.getId_kantor_new());
                    if (!dsm.getId_pegawai().equals(pegawai.getId_pegawai())) {
                        Pegawai pg = mapperPegawai.selectHeadDepartemenByKantor(pegawai.getId_departemen_new(), pegawai.getId_kantor_new());
                        cp.setId_pegawai(pg.getId_pegawai());
                        item.setKeterangan_status("Menunggu persetujuan dari " + pg.getJabatan());
                        referensi.insertPerjalananDinas(item);
                        cp.setId_perjalanan_dinas(item.getId());
                        cp.setUrut(i);
                        referensi.insertPerjalananDinasPersetujuan(cp);
                        i++;
                    }
                } else if (!mapperPegawai.selectHeadDepartemenFromChild(pegawai.getId_departemen_new()).getId_pegawai().equals(pegawai.getId_pegawai())) {
                    Pegawai pg = mapperPegawai.selectHeadDepartemenFromChild(pegawai.getId_departemen_new());
                    cp.setId_pegawai(pg.getId_pegawai());
                    item.setKeterangan_status("Menunggu persetujuan dari " + pg.getJabatan());
                    referensi.insertPerjalananDinas(item);
                    cp.setId_perjalanan_dinas(item.getId());
                    cp.setUrut(i);
                    referensi.insertPerjalananDinasPersetujuan(cp);
                    i++;
                }
                // Jabatan tertentu
            } else if (ct.getPersetujuan1().equals('2')) {
                Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan1());
                cp.setId_pegawai(pg.getId_pegawai());
                item.setStatus('D');
                item.setKeterangan_status("Menunggu persetujuan dari " + pg.getJabatan());
                referensi.insertPerjalananDinas(item);
                cp.setId_perjalanan_dinas(item.getId());
                cp.setUrut(i);
                referensi.insertPerjalananDinasPersetujuan(cp);
                i++;
            } else {
                item.setStatus('A');
                item.setKeterangan_status("Tidak perlu Persetujuan");
                referensi.insertPerjalananDinas(item);
            }

            // jika perlu persetujuan kedua jabatan tertentu
            if (ct.getPersetujuan2().equals('2')) {
                if (pegawai.getId_departemen_new().equals(102)) {
                    Pegawai dsm = mapperPegawai.selectHeadDepartemenByKantor(pegawai.getId_departemen_new(), pegawai.getId_kantor_new());
                    if (!dsm.getId_jabatan_new().equals(ct.getJabatanpersetujuan2())) {
                        item.setKeterangan_status("Menunggu persetujuan");
                        if (i.equals(1)) {
                            referensi.insertPerjalananDinas(item);
                        }
                        Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan2());
                        cp.setId_pegawai(pg.getId_pegawai());
                        cp.setId_perjalanan_dinas(item.getId());
                        cp.setUrut(i);
                        referensi.insertPerjalananDinasPersetujuan(cp);
                        i++;
                    }
                } else if (!mapperPegawai.selectHeadDepartemenFromChild(pegawai.getId_departemen_new()).getId_jabatan_new().equals(ct.getJabatanpersetujuan2())) {
                    item.setKeterangan_status("Menunggu persetujuan");
                    if (i.equals(1)) {
                        referensi.insertPerjalananDinas(item);
                    }
                    Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan2());
                    cp.setId_pegawai(pg.getId_pegawai());
                    cp.setId_perjalanan_dinas(item.getId());
                    cp.setUrut(i);
                    referensi.insertPerjalananDinasPersetujuan(cp);
                    i++;
                }

            }

            // jika perlu persetujuan ketiga jabatan tertentu
            if (ct.getPersetujuan3().equals('2')) {
                if (pegawai.getId_departemen_new().equals(102)) {
                    Pegawai dsm = mapperPegawai.selectHeadDepartemenByKantor(pegawai.getId_departemen_new(), pegawai.getId_kantor_new());
                    if (!dsm.getId_jabatan_new().equals(ct.getJabatanpersetujuan3())) {
                        item.setKeterangan_status("Menunggu persetujuan");
                        if (i.equals(1)) {
                            referensi.insertPerjalananDinas(item);
                        }
                        Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan3());
                        cp.setId_pegawai(pg.getId_pegawai());
                        cp.setId_perjalanan_dinas(item.getId());
                        cp.setUrut(i);
                        referensi.insertPerjalananDinasPersetujuan(cp);
                        i++;
                    }
                } else if (!mapperPegawai.selectHeadDepartemenFromChild(pegawai.getId_departemen_new()).getId_jabatan_new().equals(ct.getJabatanpersetujuan3())) {
                    item.setKeterangan_status("Menunggu persetujuan");
                    if (i.equals(1)) {
                        referensi.insertPerjalananDinas(item);
                    }
                    Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan3());
                    cp.setId_pegawai(pg.getId_pegawai());
                    cp.setId_perjalanan_dinas(item.getId());
                    cp.setUrut(i);
                    referensi.insertPerjalananDinasPersetujuan(cp);
                    i++;
                }

            }


            // jika perlu persetujuan ketiga jabatan tertentu
            if (ct.getPersetujuan4().equals('2')) {
                if (!pegawai.getId_jabatan_new().equals(ct.getJabatanpersetujuan4())) {
                    item.setKeterangan_status("Menunggu persetujuan");
                    if (i.equals(1)) {
                        referensi.insertPerjalananDinas(item);
                    }
                    Pegawai pg = mapperPegawai.selectPegawaiByJabatan(ct.getJabatanpersetujuan4());
                    cp.setId_pegawai(pg.getId_pegawai());
                    cp.setId_perjalanan_dinas(item.getId());
                    cp.setUrut(i);
                    referensi.insertPerjalananDinasPersetujuan(cp);
                    i++;
                }

            }

        }

    }

//    @Transactional(readOnly = false)
//    public void updateAbsen(Integer id_cuti, String id_pegawai, Character status) {
//        PerjalananDinas item = referensi.selectOnePerjalananDinas(id_cuti);
//
//        Pegawai pegawai = mapperPegawai.selectOneDataDiri(id_pegawai);
//        Absen abs = new Absen();
//        abs.setId_absen(pegawai.getAbsen());
//        abs.setId_cuti(item.getId_cuti());
//        abs.setKeterangan(item.getNama_cuti());
//        if (item.getWaktu_cuti().equals('1')) {
//            abs.setTanggal(item.getTanggal_awal());
//            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), abs.getTanggal());
//            if (absenexist == null) {
//                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
//                abs.setTipe_absen('c');
//                mapperAbsen.insertFromCutoff(abs);
//            } else {
//                mapperAbsen.updateFromCutoff(abs);
//            }
//        } else {
//            Date current = item.getTanggal_awal();
//            abs.setTanggal(current);
//            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), current);
//            if (absenexist == null) {
//                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
//                abs.setTipe_absen('c');
//                mapperAbsen.insertFromCutoff(abs);
//            } else {
//                mapperAbsen.updateFromCutoff(abs);
//            }
//            
//            while (current.before(item.getTanggal_akhir())) {
//                //processDate(current);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(current);
//                calendar.add(Calendar.DATE, 1);
//                current = calendar.getTime();
//                abs.setTanggal(current);
//                absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), current);
//                if (absenexist == null) {
//                    abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
//                    abs.setTipe_absen('c');
//                    mapperAbsen.insertFromCutoff(abs);
//                } else {
//                    mapperAbsen.updateFromCutoff(abs);
//                }
//            }
//
//        }
//
//    }
    @Transactional(readOnly = true)
    public PerjalananDinas onLoadPerjalananDinas(Integer id
    ) {
        return referensi.selectOnePerjalananDinas(id);
    }

    @Transactional(readOnly = false)
    public void ubahPerjalananDinas(PerjalananDinas item
    ) {
        referensi.updatePerjalananDinas(item);
    }

//    @Transactional(readOnly = true)
//    public Pegawai selectOnePegawai(String id_pegawai) {
//        return referensi.selectOnePegawai(id_pegawai);
//    }
    @Transactional(readOnly = false)
    public void updateStatusPerjalananDinas(Integer id_cuti, Boolean status,
            String id_pegawai, Character statusnya,
            String keterangan_status, String idPegawai
    ) {
        referensi.updateStatusPerjalananDinas(statusnya, id_cuti, keterangan_status);
        referensi.updatePersetujuaan(status, id_cuti, id_pegawai);
        if (statusnya.equals('A') || statusnya.equals('T')) {
            updateAbsen(id_cuti, idPegawai, statusnya);
        }
    }

    @Transactional(readOnly = true)
    public int countPersetujuan(Integer id_cuti_karyawaInteger
    ) {
        return referensi.countPersetujuan(id_cuti_karyawaInteger);
    }

    @Transactional(readOnly = true)
    public PerjalananDinas onLoadPersetujuankedua(Integer id, Integer urut
    ) {
        return referensi.selectPersetujuankedua(id, urut);
    }

    @Transactional(readOnly = true)
    public SettingApproval settingan() {
        return referensi.settingan();
    }

    @Transactional(readOnly = true)
    public Integer searchPersetujuan(Integer id_perjalanan_dinas, String id_pegawai,
            Integer urut
    ) {
        return referensi.searchPersetujuan(id_perjalanan_dinas, id_pegawai, urut);
    }

    @Transactional(readOnly = false)
    public void updateAbsen(Integer id, String id_pegawai,
            Character status
    ) {
        PerjalananDinas item = referensi.selectOnePerjalananDinas(id);

        Pegawai pegawai = mapperPegawai.selectOneDataDiri(id_pegawai);
        Absen abs = new Absen();
        abs.setId_absen(pegawai.getAbsen());
        //abs.setId_cuti(item.getId_cuti());
        abs.setKeterangan("Perjalanan dinas");
        if (item.getWaktu().equals('1')) {
            abs.setTanggal(item.getTanggal_awal());
            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), abs.getTanggal());
            if (absenexist == null) {
                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
                abs.setTipe_absen('m');
                abs.setId_status_absen("p");
                mapperAbsen.insertFromCutoff(abs);
            } else {
                mapperAbsen.updateFromCutoff(abs);
            }
        } else {
            Date current = item.getTanggal_awal();
            abs.setTanggal(current);
            Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), current);
            if (absenexist == null) {
                abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
                abs.setTipe_absen('m');
                abs.setId_status_absen("p");
                mapperAbsen.insertFromCutoff(abs);
            } else {
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
                    //abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
                    abs.setTipe_absen('m');
                    abs.setId_status_absen("p");
                    mapperAbsen.insertFromCutoff(abs);
                } else {
                    mapperAbsen.updateFromCutoff(abs);
                }
            }

        }
    }
    
    @Transactional(readOnly = true)
    public List<PerjalananDinasPersetujuan> selectAllPerjalananDinasPersetujuan (Integer id_perjalanan_dinas){
        return referensi.selectAllPerjalananDinasPersetujuan(id_perjalanan_dinas);
    }
}
