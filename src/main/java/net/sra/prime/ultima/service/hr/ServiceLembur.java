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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsen;
import net.sra.prime.ultima.db.mapper.hr.MapperLembur;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.Lembur;
import net.sra.prime.ultima.entity.hr.LemburJabatan;
import net.sra.prime.ultima.entity.hr.LemburKaryawan;
import net.sra.prime.ultima.entity.hr.LemburMultiplier;
import net.sra.prime.ultima.entity.hr.LemburPersetujuan;
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
public class ServiceLembur {

    @Autowired
    MapperLembur referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Autowired
    MapperAbsen mapperAbsen;

    @Transactional(readOnly = true)
    public List<Lembur> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteLemburJabatan(id);
        referensi.deleteLemburMultiplier(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Lembur item, List<MasterJabatan> lMasterJabatans, List<LemburMultiplier> lLemburMultiplierWeekday, List<LemburMultiplier> lLemburMultiplierWeekend) {
        referensi.insert(item);
        tambahLemburMultiplier(lLemburMultiplierWeekday, item.getId(), 'd');
        tambahLemburMultiplier(lLemburMultiplierWeekend, item.getId(), 'e');
        tambahLemburJabatan(lMasterJabatans, item.getId());
    }

    public void tambahLemburJabatan(List<MasterJabatan> lMasterJabatans, Integer id) {
        LemburJabatan lemburJabatan = new LemburJabatan();
        for (int i = 0; i < lMasterJabatans.size(); i++) {
            lemburJabatan.setId_lembur(id);
            lemburJabatan.setId_jabatan(lMasterJabatans.get(i).getId_jabatan());
            referensi.insertLemburJabatan(lemburJabatan);
        }
    }

    public void tambahLemburMultiplier(List<LemburMultiplier> lLemburMultipliers, Integer id, Character jenis) {
        LemburMultiplier lemburMultiplier = new LemburMultiplier();
        for (int i = 0; i < lLemburMultipliers.size(); i++) {
            lemburMultiplier = lLemburMultipliers.get(i);
            lemburMultiplier.setId_lembur(id);
            lemburMultiplier.setJenis(jenis);
            lemburMultiplier.setUrut(i);
            referensi.insertLemburMultiplier(lemburMultiplier);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Lembur item, List<MasterJabatan> lMasterJabatans, List<LemburMultiplier> lLemburMultiplierWeekday, List<LemburMultiplier> lLemburMultiplierWeekend) {
        referensi.deleteLemburJabatan(item.getId());
        referensi.deleteLemburMultiplier(item.getId());
        referensi.update(item);
        tambahLemburJabatan(lMasterJabatans, item.getId());
        tambahLemburMultiplier(lLemburMultiplierWeekday, item.getId(), 'd');
        tambahLemburMultiplier(lLemburMultiplierWeekend, item.getId(), 'e');
    }

    @Transactional(readOnly = true)
    public Lembur onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<LemburJabatan> onLoadLemburJabatan(Integer id) {
        return referensi.selectAllLemburJabatan(id);
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadJabatanTarget(Integer id) {
        return referensi.selectAllJabatanTarget(id);
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadJabatanSource() {
        return referensi.selectAllJabatanSource();
    }

    @Transactional(readOnly = true)
    public List<MasterJabatan> onLoadListJabatan() {
        return referensi.selectAllJabatan();
    }

    ///////////////////// Lembur Karywan //////////////
    @Transactional(readOnly = true)
    public List<LemburKaryawan> onLoadListLemburKaryawan(Integer id_departemen, Integer id_jabatan, Integer id_lembur, Character status, Date bulan) {
        return referensi.selectAllLemburKaryawan(id_departemen, id_jabatan, id_lembur, status, bulan, null);
    }

    @Transactional(readOnly = true)
    public List<LemburKaryawan> onLoadMyListOvertime(String tahun, String id_pegawai) {
        return referensi.myOvertime(tahun, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<LemburKaryawan> onLoadOtherOvertime(String tahun, String id_pegawai) {
        return referensi.otherOvertime(tahun, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<LemburKaryawan> onLoadListNotifikasiLemburKaryawan(String id_pegawai) {
        return referensi.selectNotifikasiLemburKaryawan(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void deleteLemburKaryawan(Integer id) {
        referensi.deleteLemburKaryawan(id);

    }

    @Transactional(readOnly = false)
    public void tambahLemburKaryawan(LemburKaryawan item, Lembur ct, Integer id_departemen) {

        referensi.insertLemburKaryawan(item);
        // Jika persetujuan pertama tidak sama dengan tidak perlu persetujuan
        if (!ct.getPersetujuan1().equals('3')) {
            LemburPersetujuan cp = new LemburPersetujuan();
            cp.setId_lembur_karyawan(item.getId());
            // Jika persetujuan pertama adalah head departemen;
            if (ct.getPersetujuan1().equals('1')) {
                // script ini masih salah
                cp.setId_pegawai(mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai());
            } else if (ct.getPersetujuan1().equals('2')) {
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatan_persetujuan1()).getId_pegawai());
            }
            if (item.getStatus().equals('T') || item.getStatus().equals('W')) {
                cp.setStatus(Boolean.TRUE);
            }
            cp.setUrut(1);
            referensi.insertLemburPersetujuan(cp);

            // jika perlu persetujuan kedua
            if (ct.getPersetujuan2().equals('2')) {
                if (item.getStatus().equals('T')) {
                    cp.setStatus(Boolean.TRUE);
                }
                cp = new LemburPersetujuan();
                cp.setId_lembur_karyawan(item.getId());
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(ct.getJabatan_persetujuan2()).getId_pegawai());
                cp.setUrut(2);
                referensi.insertLemburPersetujuan(cp);
            }

        }

        // Untuk menginput ke tabel absen
//        if (item.getStatus().equals('A') || item.getStatus().equals('T')) {
//            updateAbsen(item.getId(), item.getId_pegawai(), item.getStatus());
//        }
    }

    @Transactional(readOnly = false)
    public void addMyOvertime(LemburKaryawan item, Lembur ct, Integer id_departemen) {

        referensi.insertLemburKaryawan(item);

        Pegawai pegawai = mapperPegawai.selectOne(item.getId_pegawai());
        Pegawai pegawaiatasan = mapperPegawai.selectOne(pegawai.getAtasan_langsung());

        LemburPersetujuan cp = new LemburPersetujuan();
        // hr Manager dan supervisor hr
        if (pegawai.getId_jabatan_new() == 109 || pegawaiatasan.getId_jabatan_new() == 109) {
            cp.setId_lembur_karyawan(item.getId());
            cp.setRevisi(item.getRevisi());
            cp.setId_pegawai(pegawai.getAtasan_langsung());
            cp.setUrut(1);
            referensi.insertLemburPersetujuan(cp);
            // head departemen sales
        } else if (id_departemen == 102 && mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new()).getId_pegawai().equals(item.getId_pegawai())) {
            //persetujuan pertama oleh HR manager
            cp.setId_lembur_karyawan(item.getId());
            cp.setRevisi(item.getRevisi());
            cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
            cp.setUrut(1);
            referensi.insertLemburPersetujuan(cp);
            // persetujuan kedua oleh direktur
            cp.setId_pegawai(pegawai.getAtasan_langsung());
            cp.setUrut(2);
            referensi.insertLemburPersetujuan(cp);
            // head departemen  selain sales dan GM
        } else if ((id_departemen != 106 && mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(item.getId_pegawai())) || pegawai.getId_jabatan_new() == 102 || pegawai.getId_jabatan_new() == 103) {
                //persetujuan pertama oleh HR manager
                cp.setId_lembur_karyawan(item.getId());
                cp.setRevisi(item.getRevisi());
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                cp.setUrut(1);
                referensi.insertLemburPersetujuan(cp);
                // persetujuan kedua oleh direktur
                cp.setId_pegawai(pegawai.getAtasan_langsung());
                cp.setUrut(2);
                referensi.insertLemburPersetujuan(cp);
            
        } else {
            cp.setId_lembur_karyawan(item.getId());
            cp.setRevisi(item.getRevisi());
            cp.setId_pegawai(pegawai.getAtasan_langsung());
            cp.setUrut(1);
            referensi.insertLemburPersetujuan(cp);
            //jika atasan pertama adalah head departemen dan deprtemen sales
            if (id_departemen == 102 && mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new()).getId_pegawai().equals(pegawai.getAtasan_langsung())) {
                // persejuan kedua (terakhir) oleh hr manager
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                cp.setUrut(2);
                referensi.insertLemburPersetujuan(cp);
                // jika atasan pertama adalah head departemen dan departemen selain sales
            } else if (mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(pegawai.getAtasan_langsung())) {
                // persejuan kedua (terakhir) oleh hr manager
                cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                cp.setUrut(2);
                referensi.insertLemburPersetujuan(cp);
            } else {
                // persetujuan kedua oleh supervisor
                cp.setId_pegawai(pegawaiatasan.getAtasan_langsung());
                cp.setUrut(2);
                referensi.insertLemburPersetujuan(cp);

                if (id_departemen == 102 && mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new()).getId_pegawai().equals(pegawaiatasan.getAtasan_langsung())) {
                    // pesetujuan ke 3 (terkahir) oleh hr manager
                    cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                    cp.setUrut(3);
                    referensi.insertLemburPersetujuan(cp);
                    // jika atasan ke 2 ada head departemen
                } else if (mapperPegawai.selectHeadDepartemenFromChild(id_departemen).getId_pegawai().equals(pegawaiatasan.getAtasan_langsung())) {
                    // jika departemennya selain hr departemen
                    if (id_departemen != 106) {
                        // pesetujuan ke 3 (terkahir) oleh hr manager
                        cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                        cp.setUrut(3);
                        referensi.insertLemburPersetujuan(cp);
                    }
                } else {
                    // persetjuan ke 3 oleh head departemen/manager
                    if (id_departemen == 102) {
                        cp.setId_pegawai(mapperPegawai.selectHeadDepartemenByKantor(id_departemen, pegawai.getId_kantor_new()).getId_pegawai());
                    } else {
                        cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(id_departemen).getId_pegawai());

                    }
                    cp.setUrut(3);
                    referensi.insertLemburPersetujuan(cp);
                    //persetujuan keempat oleh hr manager
                    cp.setId_pegawai(mapperPegawai.selectPegawaiByJabatan(109).getId_pegawai());
                    cp.setUrut(4);
                    referensi.insertLemburPersetujuan(cp);
                }
            }

        }

    }

    @Transactional(readOnly = false)
    public void addMyRevisi(LemburKaryawan item) {
        referensi.updateLemburKaryawanRevisi(item.getId(), item.getRevisi() - 1);
        item.setStatus('D');
        item.setKeterangan_status("Menunggu Persetujuan");
        referensi.insertLemburKaryawanRevisi(item);
        List<LemburPersetujuan> lLemburPersetujuan = referensi.selectAllLemburPersetujuan(item.getId(), item.getRevisi() - 1);
        addLemburPersetujuanRevisi(lLemburPersetujuan);
    }

    @Transactional(readOnly = false)
    public void addLemburPersetujuanRevisi(List<LemburPersetujuan> lLemburPersetujuan) {
        for (int i = 0; i < lLemburPersetujuan.size(); i++) {
            lLemburPersetujuan.get(i).setRevisi(lLemburPersetujuan.get(i).getRevisi() + 1);
            referensi.insertLemburPersetujuan(lLemburPersetujuan.get(i));
        }
    }

    @Transactional(readOnly = true)
    public LemburPersetujuan selectMaxLemburPersetujuan(Integer id_lembur_karyawan) {
        return referensi.selectMaxLemburPersetujuan(id_lembur_karyawan);
    }

    @Transactional(readOnly = true)
    public LemburKaryawan onLoadLemburKaryawan(Integer id, Integer revisi) {
        return referensi.selectOneLemburKaryawan(id, revisi);
    }

    @Transactional(readOnly = true)
    public Lembur selectOneLemburFromMaster(Integer id_jabatan_new) {
        return referensi.selectOneLemburFromMaster(id_jabatan_new);
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String id_pegawai) {
        return referensi.selectOnePegawai(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updateStatusLemburKaryawan(Integer id_lembur, Boolean status, String id_pegawai, Character statusnya, String keterangan_status, String idPegawai, Integer revisi) {
        referensi.updateStatusLemburKaryawan(statusnya, id_lembur, keterangan_status, revisi);
        referensi.updatePersetujuaan(status, id_lembur, id_pegawai, revisi);
//        if (statusnya.equals('A') || statusnya.equals('T')) {
//            updateAbsen(id_lembur, idPegawai, statusnya);
//        }
    }

    @Transactional(readOnly = false)
    public void declineOvertime(Integer id_lembur, Character statusnya, String keterangan_status, Integer revisi) {
        referensi.updateStatusLemburKaryawan(statusnya, id_lembur, keterangan_status, revisi);
    }

    @Transactional(readOnly = false)
    public void updateAbsen(Integer id_lembur, String id_pegawai, Character status, Integer revisi) {
        LemburKaryawan item = referensi.selectOneLemburKaryawan(id_lembur, revisi);

        Pegawai pegawai = mapperPegawai.selectOneDataDiri(id_pegawai);
        Absen abs = new Absen();
        abs.setId_absen(pegawai.getAbsen());
        abs.setTanggal(item.getTanggal());

        if (item.getHour_before() != null) {
            abs.setOvertimebefore(item.getHour_before());
        } else {
            abs.setOvertimebefore(0);
        }

        if (item.getHour_after() != null) {
            abs.setOvertimeafter(item.getHour_after());
        } else {
            abs.setOvertimeafter(0);
        }
        Absen absenexist = mapperAbsen.selectOne(pegawai.getAbsen(), item.getTanggal());
        if (absenexist == null) {
            //abs.setId_jadwal_kerja(referensi.selectIdJadwalKerja(item.getId_pegawai()));
            //abs.setTipe_absen('m');
            //abs.setId_status_absen("o");
            mapperAbsen.insertFromOvertime(abs);
        } else {
            mapperAbsen.updateFromOvertime(abs);
        }

    }

    @Transactional(readOnly = true)
    public int countPersetujuan(Integer id_lembur_karyawaInteger) {
        return referensi.countPersetujuan(id_lembur_karyawaInteger);
    }

    @Transactional(readOnly = true)
    public LemburKaryawan onLoadPersetujuankedua(Integer id, Integer urut) {
        return referensi.selectPersetujuankedua(id, urut);
    }

    @Transactional(readOnly = true)
    public List<LemburMultiplier> selectJenisMultiplier(Integer id_lembur, Character jenis) {
        return referensi.selectJenisMultiplier(id_lembur, jenis);
    }

    @Transactional(readOnly = true)
    public Integer searchPersetujuan(Integer id_lembur_karyawan, String id_pegawai, Integer revisi) {
        return referensi.searchPersetujuan(id_lembur_karyawan, id_pegawai, revisi);
    }

    @Transactional(readOnly = true)
    public List<LemburPersetujuan> selectAllLemburPersetujuan(Integer id_lembur_karyawan, Integer revisi) {
        return referensi.selectAllLemburPersetujuan(id_lembur_karyawan, revisi);
    }

    @Transactional(readOnly = true)
    public String selectMax(Integer year) {
        return referensi.SelectMax(year);
    }

    @Transactional(readOnly = true)
    public LemburKaryawan checkOvertime(Date tanggal, String id_pegawai) {
        return referensi.checkOvertime(tanggal, id_pegawai);
    }

    @Transactional(readOnly = true)
    public LemburKaryawan selectLemburKaryawanById(Integer id, Integer revisi) {
        return referensi.selectLemburKaryawanById(id, revisi);
    }
}
