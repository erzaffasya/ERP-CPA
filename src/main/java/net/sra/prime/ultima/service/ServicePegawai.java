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
package net.sra.prime.ultima.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPengguna;
import net.sra.prime.ultima.db.mapper.hr.MapperAsset;
import net.sra.prime.ultima.db.mapper.hr.MapperKarir;
import net.sra.prime.ultima.db.mapper.hr.MapperPendidikan;
import net.sra.prime.ultima.db.mapper.hr.MapperResign;
import net.sra.prime.ultima.db.mapper.hr.MapperTrainning;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.entity.hr.Asset;
import net.sra.prime.ultima.entity.hr.Kabupaten;
import net.sra.prime.ultima.entity.hr.Karir;
import net.sra.prime.ultima.entity.hr.KontakDarurat;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.PegawaiGudang;
import net.sra.prime.ultima.entity.hr.PegawaiPtkp;
import net.sra.prime.ultima.entity.hr.Pendidikan;
import net.sra.prime.ultima.entity.hr.Pkwtt;
import net.sra.prime.ultima.entity.hr.Provinsi;
import net.sra.prime.ultima.entity.hr.Ptkp;
import net.sra.prime.ultima.entity.hr.Resign;
import net.sra.prime.ultima.entity.hr.SettingBpjs;
import net.sra.prime.ultima.entity.hr.Tanggungan;
import net.sra.prime.ultima.entity.hr.Trainning;
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
public class ServicePegawai {

    @Autowired
    MapperPegawai referensi;

    @Autowired
    MapperKarir mapperKarir;

    @Autowired
    MapperAsset mapperAsset;

    @Autowired
    MapperTrainning mapperTrainning;

    @Autowired
    MapperPendidikan mapperPendidikan;
    
    @Autowired
    MapperPengguna mapperPengguna;
    
    @Autowired
    MapperResign mapperResign;

    @Transactional(readOnly = true)
    public List<Pegawai> onLoadList(Boolean status) {
        return referensi.selectAll(status);
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> onLoadListPegawaiPengguna() {
        return referensi.selectPegawaiPengguna();
    }
    
    @Transactional(readOnly = true)
    public List<Pegawai> onLoadListForHr(Boolean status) {
        return referensi.selectAllForHr(status);
    }

    @Transactional(readOnly = true)
    public List<Pegawai> onLoadListOnlyPegawai(String id_kantor, Integer id_departemen) {
        return referensi.selectAllOnlyPegawai(id_kantor, id_departemen);
    }

    
    @Transactional(readOnly = true)
    public Pegawai onLoad(String nomor) {
        return referensi.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public Pegawai onLoadDataDiri(String nomor) {
        return referensi.selectOneDataDiri(nomor);
    }

    @Transactional(readOnly = true)
    public Pegawai onLoadHubunganKerja(String nomor) {
        return referensi.selectOneHubunganKerja(nomor);
    }

    @Transactional(readOnly = true)
    public Pegawai onLoadBpjs(String nomor) {
        return referensi.selectOneBpjs(nomor);
    }

    @Transactional(readOnly = true)
    public Pegawai onLoadStatusKetenagakerjaan(String nomor) {
        return referensi.selectOneStatusKetenagakerjaan(nomor);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor) {
        referensi.deleteKontakPegawai(nomor);
        referensi.deleteTanggunganPegawai(nomor);
        referensi.delete(nomor);

    }

    @Transactional(readOnly = false)
    public void ubahPkwt(String id_pegawai, Date pkwt) {
        referensi.updatePkwt(pkwt, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void ubahDataDiri(Pegawai item) {
        referensi.updateDataDiri(item);
        referensi.updateStatusPenggunas(item.getId_pegawai(), item.getStatus());
    }

    @Transactional(readOnly = false)
    public void ubahHubunganKerja(Pegawai item) {
        
        if(!item.getStatusumt()){
            item.setAktifasiumt(null);
        }
        
        if(!item.getStatuskehadiran()){
            item.setAktifasikehadiran(null);
        }
        
        referensi.updateHubunganKerja(item);
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        PegawaiPtkp pegawaiPtkp = referensi.selectOnePegawaiPtkp(year, item.getId_pegawai());
        if (pegawaiPtkp!=null){
            pegawaiPtkp.setId_ptkp(item.getId_ptkp());
            referensi.updatePegawaiPtkp(pegawaiPtkp);
        }else{
            pegawaiPtkp = new PegawaiPtkp();
            pegawaiPtkp.setYears(year);
            pegawaiPtkp.setId_pegawai(item.getId_pegawai());
            pegawaiPtkp.setId_ptkp(item.getId_ptkp());
            referensi.insertPegawaiPtkp(pegawaiPtkp);
        }
    }
    
    @Transactional(readOnly = false)
    public void ubahHakAksesGudangs(String id_pegawai, List<Gudang> lGudangs) {
        referensi.deletePegawaiGudang(id_pegawai);
        tambahPegawaiGudang(lGudangs, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void ubahRincianGaji(Pegawai item) {
        if (referensi.selectOnePegawaiGaji(item.getId_pegawai()) != null) {
            referensi.updateRincianGaji(item);
        } else {
            referensi.insertPegawaiGaji(item);
        }
    }
    
    @Transactional(readOnly = false)
    public void ubahPph(Pegawai item) {
        if (referensi.selectOnePegawaiGaji(item.getId_pegawai()) != null) {
            referensi.updateRincianGajiPph(item);
        } else {
            referensi.insertPegawaiGajiPph(item);
        }
    }
    
    @Transactional(readOnly = false)
    public void ubahPphRule(Pegawai item) {
        if (referensi.selectOnePegawaiGaji(item.getId_pegawai()) != null) {
            referensi.updateRincianGajiPphRule(item);
        } else {
            referensi.insertPegawaiGajiPphRule(item);
        }
    }

    @Transactional(readOnly = false)
    public void ubahBpjs(Pegawai item) {
        if (item.getJht_perusahaan() == null) {
            item.setJht_perusahaan(0.00);
        }
        if (item.getJht_pekerja() == null) {
            item.setJht_pekerja(0.00);
        }

        if (item.getJkk() == null) {
            item.setJkk(0.00);
        }
        if (item.getJkm() == null) {
            item.setJkm(0.00);
        }

        if (item.getJp_perusahaan() == null) {
            item.setJp_perusahaan(0.00);
        }

        if (item.getKesehatan_perusahaan() == null) {
            item.setKesehatan_perusahaan(0.00);
        }
        if (item.getKesehatan_pekerja() == null) {
            item.setKesehatan_pekerja(0.00);
        }

        if (item.getIuran_prudential() == null) {
            item.setIuran_prudential(0.00);
        }
        if (item.getIuran_allianz() == null) {
            item.setIuran_allianz(0.00);
        }
        
        if (item.getIuran_prudential_perusahaan() == null) {
            item.setIuran_prudential_perusahaan(0.00);
        }
        if (item.getIuran_allianz_perusahaan() == null) {
            item.setIuran_allianz_perusahaan(0.00);
        }

        if (referensi.selectBpjs(item.getId_pegawai()) != null) {
            referensi.updateBpjs(item);
        } else {
            referensi.insertBpjs(item);
        }
    }

    @Transactional(readOnly = false)
    public void ubahStatusKetenagakerjaan(Pegawai item) {
        referensi.updateStatusKetenagakerjaan(item);
    }

    @Transactional(readOnly = false)
    public void tambah(Pegawai item) {
        referensi.insert(item);
    }

    @Transactional(readOnly = true)
    public MasterJabatan onLoadJabatan(Integer id) {
        return referensi.selectJabatan(id);
    }

    @Transactional(readOnly = true)
    public Level selectLevel(Integer id_jabatan) {
        return referensi.selectLevel(id_jabatan);
    }

    @Transactional(readOnly = true)
    public List<Provinsi> selectProvinsi() {
        return referensi.selecProvinsi();
    }

    @Transactional(readOnly = true)
    public List<Kabupaten> selectKabupaten(String id_provinsi) {
        return referensi.selecKabupaten(id_provinsi);
    }

    /////////////// Kontak Darurat /////////////
    @Transactional(readOnly = false)
    public void tambahKontakDarurat(KontakDarurat item) {
        referensi.insertKontakDarurat(item);
    }

    @Transactional(readOnly = false)
    public void ubahKontakDarurat(KontakDarurat item) {
        referensi.updateKontakDarurat(item);
    }

    @Transactional(readOnly = false)
    public void deleteKontakDarurat(Integer id) {
        referensi.deleteKontakDarurat(id);

    }

    @Transactional(readOnly = true)
    public KontakDarurat onLoadKontakDarurat(Integer id) {
        return referensi.selectOneKontakDarurat(id);
    }

    @Transactional(readOnly = true)
    public List<KontakDarurat> selectAllKontakDarurat(String id) {
        return referensi.selectAllKontakDarurat(id);
    }

    @Transactional(readOnly = false)
    public void tambahTanggungan(Tanggungan item) {
        referensi.insertTanggungan(item);
    }

    @Transactional(readOnly = true)
    public List<Tanggungan> selectAllTanggungan(String id_pegawai) {
        return referensi.selectAllTanggungan(id_pegawai);
    }

    @Transactional(readOnly = true)
    public Tanggungan selectOneTanggungan(Integer id) {
        return referensi.selectOneTanggungan(id);
    }

    @Transactional(readOnly = false)
    public void ubahTanggungan(Tanggungan item) {
        referensi.updateTanggungan(item);
    }

    @Transactional(readOnly = false)
    public void deleteTanggungan(Integer id) {
        referensi.deleteTanggunngan(id);

    }
    
    @Transactional(readOnly = false)
    public void ubahFoto(String id_pegawai,String fotonya) {
        referensi.updateFoto(id_pegawai, fotonya);
    }

    @Transactional(readOnly = true)
    public Integer jumlahTanggungan(String id_pegawai) {
        return referensi.jumlahTanggungan(id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<Karir> selectAllKarir(String id_pegawai) {
        return mapperKarir.selectAll(id_pegawai);
    }

    @Transactional(readOnly = false)
    public void tambahKarir(Karir karir) {
        mapperKarir.insert(karir);
        Pegawai pegawai = new Pegawai();
        pegawai.setId_pegawai(karir.getId_pegawai());
        pegawai.setId_kantor_new(karir.getId_kantor());
        pegawai.setId_departemen_new(karir.getId_departemen());
        pegawai.setId_jabatan_new(karir.getId_jabatan());
        pegawai.setStatuskaryawan(karir.getStatuskaryawan());
        referensi.updateHubunganKerjabytransfer(pegawai);
    }
    
    @Transactional(readOnly = false)
    public void tambahResign(Resign resign) {
        mapperResign.insert(resign);
    }

    @Transactional(readOnly = true)
    public List<Asset> selectAsset(String id_pegawai) {
        return mapperAsset.selectAllbyPegawai(id_pegawai);
    }

    ////////// Trainning ////////
    @Transactional(readOnly = true)
    public List<Trainning> selectTrainning(String id_pegawai) {
        return mapperTrainning.selectAll(id_pegawai);
    }

    @Transactional(readOnly = true)
    public Trainning selectOneTrainning(Integer id) {
        return mapperTrainning.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void tambahTrainning(Trainning trainning) {
        mapperTrainning.insert(trainning);
    }

    @Transactional(readOnly = false)
    public void deleteTrainning(Integer id) {
        mapperTrainning.delete(id);
    }

    @Transactional(readOnly = false)
    public void updateTrainning(Trainning trainning) {
        mapperTrainning.update(trainning);
    }

    ////////// Trainning ////////
    @Transactional(readOnly = true)
    public List<Pendidikan> selectPendidikan(String id_pegawai) {

        return mapperPendidikan.selectAll(id_pegawai);
    }

    @Transactional(readOnly = true)
    public Pendidikan selectOnePendidikan(Integer id) {
        return mapperPendidikan.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void tambahPendidikan(Pendidikan pendidikan) {
        mapperPendidikan.insert(pendidikan);
    }

    @Transactional(readOnly = false)
    public void deletePendidikan(Integer id) {
        mapperPendidikan.delete(id);
    }

    @Transactional(readOnly = false)
    public void updatePendidikan(Pendidikan pendidikan) {
        mapperPendidikan.update(pendidikan);
    }

    @Transactional(readOnly = true)
    public SettingBpjs selectOneSettingBpjs() {
        return referensi.selectOneSettingBpjs();
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawaiGaji(String id_pegawai) {
        return referensi.selectOnePegawaiGaji(id_pegawai);
    }

    //// PKWTT ////
    @Transactional(readOnly = true)
    public List<Pkwtt> selectAllPkwtt(String id_pegawai, Character jenis) {
        return referensi.selectAllPkwtt(id_pegawai, jenis);
    }

    @Transactional(readOnly = true)
    public Pkwtt selectOnePkwtt(Integer id_pkwtt) {
        return referensi.selectOnePkwtt(id_pkwtt);
    }

    @Transactional(readOnly = false)
    public void insertPkwtt(Pkwtt pkwtt) {
        referensi.insertPkwtt(pkwtt);
    }

    @Transactional(readOnly = false)
    public void updatePkwtt(Pkwtt pkwtt) {
        referensi.updatePkwtt(pkwtt);
    }

    @Transactional(readOnly = false)
    public void deletePkwtt(Integer id_pkwtt) {
        referensi.deletePkwtt(id_pkwtt);
    }

    ///// Pegawai gudang /////
    @Transactional(readOnly = true)
    public List<Gudang> onLoadListGudang(String id_pegawai) {
        return referensi.selectAllGudang(id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<Gudang> onLoadPegawaiGudang(String id) {
        return referensi.selectPegawaiGudang(id);
    }

    @Transactional(readOnly = false)
    public void tambahPegawaiGudang(List<Gudang> lInternalGudangs, String id_pegawai) {
        PegawaiGudang pegawaiGudang = new PegawaiGudang();
        for (int i = 0; i < lInternalGudangs.size(); i++) {
            pegawaiGudang.setId_pegawai(id_pegawai);
            pegawaiGudang.setId_gudang(lInternalGudangs.get(i).getId_gudang());
            referensi.insertPegawaiGudang(pegawaiGudang);
        }
    }

    @Transactional(readOnly = true)
    public List<Ptkp> selectAllPtkp() {
        return referensi.selectAllPtkp();
    }

    @Transactional(readOnly = true)
    public Ptkp selectOnePtkp(Integer id_ptkp) {
        return referensi.selectOnePtkp(id_ptkp);
    }

    @Transactional(readOnly = true)
    public List<PegawaiPtkp> selectAllPegawaiPtkp(String years) {
        return referensi.selectAllPegawaiPtkp(years);
    }

    @Transactional(readOnly = true)
    public PegawaiPtkp selectOnePegwaiPtkp(String years, String id_pegawai) {
        return referensi.selectOnePegawaiPtkp(years, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<PegawaiPtkp> selectOnePegwaiPtkp() {
        return referensi.countPegawaiPtkpByYears();
    }

    @Transactional(readOnly = false)
    public void deletePegawaiPtkpByYear(String years) {
        referensi.deletePegawaiPtkpByYears(years);
    }

    @Transactional(readOnly = false)
    public void deletePegawaiPtkpByPegawai(String years, String id_pegawai) {
        referensi.deletePegawaiPtkpByPegawai(years, id_pegawai);
    }

    @Transactional(readOnly = true)
    public List<PegawaiPtkp> countPegawaiPtkpByYears() {
        return referensi.countPegawaiPtkpByYears();
    }

    @Transactional(readOnly = true)
    public List<PegawaiPtkp> selectListPegawaiPtkp(String years, String yearbefore) {
        return referensi.selectListPegawaiPtkp(years, yearbefore);
    }

    @Transactional(readOnly = false)
    public void tambahPegawaiPtkp(List<PegawaiPtkp> lPegawaiPtkp, String years) {
        PegawaiPtkp pegawaiPtkp = new PegawaiPtkp();
        for (int i = 0; i < lPegawaiPtkp.size(); i++) {
            pegawaiPtkp = lPegawaiPtkp.get(i);
            pegawaiPtkp.setYears(years);
            if (pegawaiPtkp.getId_ptkp() != null) {
                referensi.insertPegawaiPtkp(pegawaiPtkp);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public Pengguna CheckPin(String usernamenya, String pinnya) {
        return mapperPengguna.CheckPin(usernamenya, pinnya);
    }
}
