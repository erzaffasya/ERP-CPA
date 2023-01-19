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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.db.mapper.hr.MapperJadwalKerja;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaKaryawan;
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
public class ServiceJadwalKerja {

    @Autowired
    MapperJadwalKerja referensi;
    
    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Transactional(readOnly = true)
    public List<JadwalKerja> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteJadwalKerjaKaryawan(id);
        referensi.deleteJadwalKerjaWaktu(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(JadwalKerja item,List<Pegawai> lPegawai, List<JadwalKerjaWaktu> lJadwalKerjaWaktu) {
        referensi.insert(item);
        tambahJadwalKerjaKaryawan(lPegawai, item.getId());
        tambahJadwalKerjaWaktu(lJadwalKerjaWaktu, item.getId());
    }
    
    @Transactional(readOnly = false)
    public void tambahJadwalKerjaKaryawan(List<Pegawai> lPegawai, Integer id) {
        
        for (int i = 0; i < lPegawai.size(); i++) {
            JadwalKerjaKaryawan jadwalKerjaKaryawan = new JadwalKerjaKaryawan();
            jadwalKerjaKaryawan.setId_jadwal_kerja(id);
            jadwalKerjaKaryawan.setId_pegawai(lPegawai.get(i).getId_pegawai());
            referensi.insertJadwalKerjaKaryawan(jadwalKerjaKaryawan);
        }
    }
    
    @Transactional(readOnly = false)
    public void tambahJadwalKerjaWaktu(List<JadwalKerjaWaktu> lJadwalKerjaWaktu, Integer id) {
        JadwalKerjaWaktu jadwalKerjaWaktu = new JadwalKerjaWaktu();
        for (int i = 0; i < lJadwalKerjaWaktu.size(); i++) {
            jadwalKerjaWaktu = lJadwalKerjaWaktu.get(i);
            jadwalKerjaWaktu.setId_jadwal_kerja(id);
            referensi.insertJadwalKerjaWaktu(jadwalKerjaWaktu);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(JadwalKerja item,List<Pegawai> lPegawai,List<JadwalKerjaWaktu> lJadwalKerjaWaktu) {
        referensi.deleteJadwalKerjaKaryawan(item.getId());
        referensi.deleteJadwalKerjaWaktu(item.getId());
        referensi.update(item);
        tambahJadwalKerjaKaryawan(lPegawai, item.getId());
        tambahJadwalKerjaWaktu(lJadwalKerjaWaktu, item.getId());
    }

    @Transactional(readOnly = true)
    public JadwalKerja onLoad(Integer id) {
        return referensi.selectOne(id);
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> onLoadPegawaiFromJadwalKerjaKarywan(Integer id) {
        return referensi.selectAllKaryawan(id);
    }
    
//    @Transactional(readOnly = true)
//    public List<MasterJabatan> onLoadJabatan(Integer id) {
//        return referensi.selectAllJabatan(id);
//    }
//    
//    @Transactional(readOnly = true)
//    public List<MasterJabatan> onLoadAllJabatan(Integer id) {
//        return referensi.selectAllTblJabatan(id);
//    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> onLoadListPegawai() {
        return referensi.selectAllPegawai();
    }
    
    @Transactional(readOnly = true)
    public List<JadwalKerjaWaktu> onLoadListJadwalKerjaWaktu(Integer id_jadwal_kerja) {
        return referensi.selectAllJadwalKerjaWaktu(id_jadwal_kerja);
    }
    
    

   
}
