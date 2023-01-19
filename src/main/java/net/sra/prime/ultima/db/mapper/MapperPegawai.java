/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") throws RuntimeException;
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
package net.sra.prime.ultima.db.mapper;

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.db.provider.hr.ProviderPegawai;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.HubunganKeluarga;
import net.sra.prime.ultima.entity.hr.Kabupaten;
import net.sra.prime.ultima.entity.hr.KontakDarurat;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.PegawaiGudang;
import net.sra.prime.ultima.entity.hr.PegawaiPtkp;
import net.sra.prime.ultima.entity.hr.Pkwtt;
import net.sra.prime.ultima.entity.hr.Provinsi;
import net.sra.prime.ultima.entity.hr.Ptkp;
import net.sra.prime.ultima.entity.hr.SettingBpjs;
import net.sra.prime.ultima.entity.hr.Tanggungan;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperPegawai {
    @SelectProvider(type = ProviderPegawai.class, method = "selectAllOnlyPegawai")
    public List<Pegawai> selectAllOnlyPegawai(
            @Param("id_kantor") String id_kantor, 
            @Param("id_departemen") Integer id_departemen) throws RuntimeException;

    /// sudah dilakukan perubahan departemen dan kantor dari tabel pegawai
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen, coalesce(e.gaji_pokok,0) as gaji_pokok, coalesce(e.tunjangan_jabatan,0) as tunjangan_jabatan,coalesce(e.tunjangan_komunikasi,0) as tunjangan_komunikasi, "
            + " coalesce(e.tunjangan_bbm,0) as tunjangan_bbm, coalesce(e.umt,0) as umt,coalesce(e.insentif_kehadiran,0) as insentif_kehadiran,coalesce(e.insentif_kpi,0) as insentif_kpi,"
            + " f.bpjs_tk,f.bpjs_kes,f.prudential,f.allianz,coalesce(f.jht_perusahaan,0) as jht_perusahaan,coalesce(f.jht_pekerja,0) as jht_pekerja,coalesce(f.jkk,0) as jkk,coalesce(f.jkm,0) as jkm,coalesce(f.jp_perusahaan,0) as jp_perusahaan,coalesce(f.jp_pekerja,0) as jp_pekerja, "
            + " coalesce(f.kesehatan_perusahaan,0) as kesehatan_perusahaan,coalesce(f.kesehatan_pekerja,0) as kesehatan_pekerja, "
            + " coalesce(f.iuran_prudential,0) as iuran_prudential, coalesce(f.iuran_prudential_perusahaan,0) as iuran_prudential_perusahaan, "
            + " coalesce(f.iuran_allianz,0) as iuran_allianz,coalesce(f.iuran_allianz_perusahaan,0) as iuran_allianz_perusahaan, "
            + " g.nama as nama_atasan,i.ptkp,e.pph, j.date as tgl_resign,e.id_jenis_pph,i.nominal "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.pegawai_gaji e ON a.id_pegawai=e.id_pegawai "
            + " LEFT JOIN hr.pegawai_bpjs f ON a.id_pegawai=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.atasan_langsung=g.id_pegawai "
            + " LEFT JOIN hr.pegawai_ptkp h ON a.id_pegawai=h.id_pegawai AND h.years=TO_CHAR(CURRENT_DATE, 'YYYY') "
            + " LEFT JOIN hr.ptkp i ON h.id_ptkp=i.id_ptkp "
            + " LEFT JOIN hr.resign j ON j.id_pegawai=a.id_pegawai "
            + " WHERE a.status=#{status}"
            + " ORDER BY a.nip")
    public List<Pegawai> selectAll(@Param("status") Boolean status) throws RuntimeException;
    
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " WHERE a.status=true AND id_pegawai NOT IN (SELECT id_pegawai FROM pengguna)"
            + " ORDER BY a.nama")
    public List<Pegawai> selectPegawaiPengguna() throws RuntimeException;
    
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen, coalesce(e.gaji_pokok,0) as gaji_pokok, coalesce(e.tunjangan_jabatan,0) as tunjangan_jabatan,coalesce(e.tunjangan_komunikasi,0) as tunjangan_komunikasi, "
            + " coalesce(e.tunjangan_bbm,0) as tunjangan_bbm, coalesce(e.umt,0) as umt,coalesce(e.insentif_kehadiran,0) as insentif_kehadiran,coalesce(e.insentif_kpi,0) as insentif_kpi,"
            + " f.bpjs_tk,f.bpjs_kes,f.prudential,f.allianz,coalesce(f.jht_perusahaan,0) as jht_perusahaan,coalesce(f.jht_pekerja,0) as jht_pekerja,coalesce(f.jkk,0) as jkk,coalesce(f.jkm,0) as jkm,coalesce(f.jp_perusahaan,0) as jp_perusahaan,coalesce(f.jp_pekerja,0) as jp_pekerja, "
            + " coalesce(f.kesehatan_perusahaan,0) as kesehatan_perusahaan,coalesce(f.kesehatan_pekerja,0) as kesehatan_pekerja, "
            + " coalesce(f.iuran_prudential,0) as iuran_prudential, coalesce(f.iuran_prudential_perusahaan,0) as iuran_prudential_perusahaan, "
            + " coalesce(f.iuran_allianz,0) as iuran_allianz,coalesce(f.iuran_allianz_perusahaan,0) as iuran_allianz_perusahaan, "
            + " g.nama as nama_atasan,i.ptkp,e.pph, j.date as tgl_resign "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.pegawai_gaji e ON a.id_pegawai=e.id_pegawai "
            + " LEFT JOIN hr.pegawai_bpjs f ON a.id_pegawai=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.atasan_langsung=g.id_pegawai "
            + " LEFT JOIN hr.pegawai_ptkp h ON a.id_pegawai=h.id_pegawai AND h.years=TO_CHAR(CURRENT_DATE, 'YYYY') "
            + " LEFT JOIN hr.ptkp i ON h.id_ptkp=i.id_ptkp "
            + " LEFT JOIN hr.resign j ON j.id_pegawai=a.id_pegawai "
            + " WHERE a.status=#{status} AND a.new=true"
            + " ORDER BY a.nip")
    public List<Pegawai> selectAllForHr(@Param("status") Boolean status) throws RuntimeException;


    @Insert("INSERT INTO pegawai "
            + "(id_pegawai, nama, id_jabatan_new, hp, email,status, nama_panggilan, nik_ktp, tempat_lahir, tanggal_lahir, "
            + " id_agama, jenis_kelamin,status_pernikahan, golongan_darah, hp2, hp3, wa, alamat_ktp, kodepos_ktp, alamat_domisili, kodepos_domisili, "
            + " tinggi_badan, berat_badan, anak_ke, nip, mulai_bekerja, id_departemen_new,id_kantor_new,absen,statuskaryawan,atasan_langsung,nomorrekening,new,pemilik_rekening)"
            + "VALUES "
            + "(#{id_pegawai}, #{nama}, #{id_jabatan_new}, #{hp}, #{email},#{status}, #{nama_panggilan}, #{nik_ktp}, #{tempat_lahir}, #{tanggal_lahir} "
            + ", #{id_agama}, #{jenis_kelamin}, #{status_pernikahan}, #{golongan_darah}, #{hp2}, #{hp3}, #{wa}, #{alamat_ktp}, #{kodepos_ktp}, #{alamat_domisili}, #{kodepos_domisili} "
            + ", #{tinggi_badan}, #{berat_badan}, #{anak_ke}, #{nip}, #{mulai_bekerja}, #{id_departemen_new},#{id_kantor_new},#{absen},#{statuskaryawan},#{atasan_langsung},#{nomorrekening},true,#{pemilik_rekening}) ")
    public int insert(Pegawai pegawai) throws RuntimeException;

    @Insert("INSERT INTO hr.kontak_darurat "
            + " (id_hubungan, telpon, nama, id_pegawai)"
            + " VALUES "
            + " (#{id_hubungan}, #{telpon}, #{nama}, #{id_pegawai})") 
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertKontakDarurat(KontakDarurat kontakDarurat) throws RuntimeException;
    
    
    @Delete("DELETE FROM pegawai WHERE id_pegawai = #{id}")
    public int delete(@Param("id") String id) throws RuntimeException;
    
    @Delete("DELETE FROM hr.kontak_darurat WHERE id_pegawai = #{id}")
    public int deleteKontakPegawai(@Param("id") String id) throws RuntimeException;
    
    @Delete("DELETE FROM hr.tanggungan WHERE id_pegawai = #{id}")
    public int deleteTanggunganPegawai(@Param("id") String id) throws RuntimeException;
    
    @Delete("DELETE FROM hr.kontak_darurat WHERE id = #{id}")
    public int deleteKontakDarurat(@Param("id") Integer id) throws RuntimeException;

    /// sudah dilakukan perubahan departemen dan kantor dari tabel pegawai
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen,f.sublevel as level,g.golongan,b.headdepartemen "
            + " FROM pegawai a " 
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.level_jabatan e ON a.id_jabatan_new=e.id_jabatan "
            + " LEFT JOIN hr.level f ON e.id_level=f.id "
            + " LEFT JOIN hr.golongan g ON f.id_golongan=g.id "
            + " WHERE a.id_pegawai=#{id} ")
    public Pegawai selectOne(@Param("id") String id) throws RuntimeException;
    
    
    
    @Select("SELECT a.*, b.agama,c.provinsi as nama_provinsi_domisili,d.kabupaten as nama_kabupaten_domisili, "
            + " e.provinsi as nama_provinsi_ktp,f.kabupaten as nama_kabupaten_domisili, "
            + " g.jabatan, h.departemen,i.nama as kantor "
            + " FROM pegawai a "
            + " LEFT JOIN agama b  ON a.id_agama=b.id "
            + " LEFT JOIN hr.provinsi c ON a.provinsi_domisili=c.id "
            + " LEFT JOIN hr.kabupaten d ON a.kabupaten_domisili=d.id "
            + " LEFT JOIN hr.provinsi e ON a.provinsi_ktp=e.id "
            + " LEFT JOIN hr.kabupaten f ON a.kabupaten_ktp=f.id "
            + " LEFT JOIN master_jabatan g ON a.id_jabatan_new=g.id_jabatan "
            + " LEFT JOIN hr_departemen h ON a.id_departemen_new=h.id_departemen "
            + " LEFT JOIN internal_kantor_cabang i ON a.id_kantor_new = i.id_kantor_cabang "
            + " WHERE a.id_pegawai=#{id} ")
    public Pegawai selectOneDataDiri(@Param("id") String id) throws RuntimeException;
    
    @Select("SELECT a.* FROM pegawai a"
            + " WHERE a.id_pegawai=#{id} ")
    public Pegawai selectOneStatusKetenagakerjaan(@Param("id") String id) throws RuntimeException;
    
    
    
    // Kalau data sudah fix semua diubah menjadi INNER JOIN
    @Select("SELECT a.*,pg.*, b.jabatan, c.nama as kantor, d.departemen,f.sublevel as level,g.golongan,i.nama as area,j.id_ptkp,k.ptkp, l.nama as nama_atasan "
            + " FROM pegawai a "
            + " LEFT JOIN hr.pegawai_gaji pg ON a.id_pegawai= pg.id_pegawai"
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.level_jabatan e ON a.id_jabatan_new=e.id_jabatan "
            + " LEFT JOIN hr.level f ON e.id_level=f.id "
            + " LEFT JOIN hr.golongan g ON f.id_golongan=g.id "
            + " LEFT JOIN hr.area_kantor h ON c.id_kantor_cabang=h.id_kantor "
            + " LEFT JOIN hr.area i ON h.id_area=i.id "
            + " LEFT JOIN hr.pegawai_ptkp j ON a.id_pegawai=j.id_pegawai AND j.years=TO_CHAR(CURRENT_DATE, 'YYYY') "
            + " LEFT JOIN hr.ptkp k ON k.id_ptkp=j.id_ptkp "
            + " LEFT JOIN pegawai l ON a.atasan_langsung=l.id_pegawai "
            + " WHERE a.id_pegawai=#{id} ")
    public Pegawai selectOneHubunganKerja(@Param("id") String id) throws RuntimeException;
    
    @Select("SELECT * FROM hr.pegawai_gaji WHERE id_pegawai=#{id_pegawai}")
    public Pegawai selectOnePegawaiGaji(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Update("UPDATE pegawai SET pkwt=#{pkwt} WHERE id_pegawai=#{id_pegawai}")
    public int updatePkwt(@Param("pkwt") Date pkwt,
                    @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Update("UPDATE pegawai  SET "
            + " nama = #{nama}, "
            + " nama_panggilan = #{nama_panggilan}, "
            + " nik_ktp= #{nik_ktp}, "
            + " tempat_lahir= #{tempat_lahir}, "
            + " tanggal_lahir= #{tanggal_lahir}, "
            + " id_agama= #{id_agama}, "
            + " jenis_kelamin= #{jenis_kelamin}, "
            + " status_pernikahan= #{status_pernikahan}, "
            + " golongan_darah= #{golongan_darah}, "
            + " alamat_ktp= #{alamat_ktp}, "
            + " kodepos_ktp= #{kodepos_ktp}, "
            + " alamat_domisili= #{alamat_domisili}, "
            + " kodepos_domisili= #{kodepos_domisili}, "
            + " tinggi_badan= #{tinggi_badan}, "
            + " berat_badan= #{berat_badan}, "
            + " anak_ke= #{anak_ke}, "
            + " provinsi_ktp = #{provinsi_ktp}, "
            + " kabupaten_ktp = #{kabupaten_ktp}, "
            + " provinsi_domisili = #{provinsi_domisili}, "
            + " kabupaten_domisili = #{kabupaten_domisili}, "
            + " alamatsama = #{alamatsama}, "
            + " hp = #{hp}, "
            + " hp2 = #{hp2}, "
            + " wa = #{wa}, "
            + " email = #{email}, "
            + " status = #{status} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateDataDiri(Pegawai pegawai) throws RuntimeException;
    
    @Update("UPDATE pegawai set fotonya=#{fotonya} WHERE id_pegawai=#{id_pegawai}")
    public int updateFoto(@Param("id_pegawai") String id_pegawai,
                    @Param("fotonya") String fotonya) throws RuntimeException;
    
    @Update("UPDATE pengguna set enabled=${status} WHERE id_pegawai=#{id_pegawai}")
    public int updateStatusPenggunas(@Param("id_pegawai") String id_pegawai,@Param("status") Boolean status) throws RuntimeException;

    @Update("UPDATE pegawai  SET "
            + " id_jabatan_new = #{id_jabatan_new}, "
            + " id_departemen_new = #{id_departemen_new}, "
            + " id_kantor_new = #{id_kantor_new},"
            + " absen = #{absen}, "
            + " mulai_bekerja = #{mulai_bekerja}, "
            + " nip = #{nip},"
            + " pkwt= #{pkwt}, "
            + " statuskaryawan = #{statuskaryawan}, "
            + " atasan_langsung = #{atasan_langsung}, "
            + " pemilik_rekening = #{pemilik_rekening}, "
            + " nomorrekening = #{nomorrekening}, "
            + " aktifasiumt = #{aktifasiumt}, "
            + " aktifasikehadiran =#{aktifasikehadiran}, "
            + " isdriver = #{isdriver} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateHubunganKerja(Pegawai pegawai) throws RuntimeException;
    
    @Update("UPDATE pegawai  SET "
            + " id_jabatan_new = #{id_jabatan_new}, "
            + " id_departemen_new = #{id_departemen_new}, "
            + " id_kantor_new = #{id_kantor_new},"
            + " statuskaryawan = #{statuskaryawan}"
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateHubunganKerjabytransfer(Pegawai pegawai) throws RuntimeException;
    
    ///// tabel pegawai_gaji
    
    @Insert("INSERT INTO hr.pegawai_gaji "
            + " (id_pegawai, gaji_pokok, tunjangan_jabatan, tunjangan_komunikasi, tunjangan_bbm, insentif_kehadiran,umt,insentif_kpi) "
            + " VALUES "
            + " (#{id_pegawai}, #{gaji_pokok}, #{tunjangan_jabatan}, #{tunjangan_komunikasi}, #{tunjangan_bbm}, #{insentif_kehadiran},#{umt},#{insentif_kpi}) ")
    public int insertPegawaiGaji(Pegawai pegawai) throws RuntimeException;        
    
    @Update("UPDATE hr.pegawai_gaji  SET "
            + " gaji_pokok = #{gaji_pokok}, "
            + " tunjangan_jabatan = #{tunjangan_jabatan}, "
            + " tunjangan_komunikasi = #{tunjangan_komunikasi}, "
            + " tunjangan_bbm = #{tunjangan_bbm}, "
            + " umt = #{umt}, "
            + " insentif_kehadiran = #{insentif_kehadiran},"
            + " insentif_kpi = #{insentif_kpi} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateRincianGaji(Pegawai pegawai) throws RuntimeException;
    
    @Insert("INSERT INTO hr.pegawai_gaji "
            + " (id_pegawai, pph) "
            + " VALUES "
            + " (#{id_pegawai}, #{pph}) ")
    public int insertPegawaiGajiPph(Pegawai pegawai) throws RuntimeException;        
    
    @Insert("INSERT INTO hr.pegawai_gaji "
            + " (id_pegawai, id_jenis_pph) "
            + " VALUES "
            + " (#{id_pegawai}, #{id_jenis_pph}) ")
    public int insertPegawaiGajiPphRule(Pegawai pegawai) throws RuntimeException;        
    
    @Update("UPDATE hr.pegawai_gaji  SET "
            + " pph = #{pph} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateRincianGajiPph(Pegawai pegawai) throws RuntimeException;
    
    @Update("UPDATE hr.pegawai_gaji  SET "
            + " id_jenis_pph = #{id_jenis_pph} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateRincianGajiPphRule(Pegawai pegawai) throws RuntimeException;
    
    
    ///  Tabel pegawai_bpjs
    @Select("SELECT * FROM hr.pegawai_bpjs WHERE id_pegawai=#{id_pegawai}")
    public Pegawai selectBpjs(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.*,pg.*, b.jabatan, c.nama as kantor, d.departemen,f.sublevel as level,g.golongan,i.nama as area,"
            + " gj.gaji_pokok,gj.tunjangan_jabatan "
            + " FROM pegawai a "
            + " LEFT JOIN hr.pegawai_bpjs pg ON a.id_pegawai= pg.id_pegawai "
            + " LEFT JOIN hr.pegawai_gaji gj ON a.id_pegawai=gj.id_pegawai "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.level_jabatan e ON a.id_jabatan_new=e.id_jabatan "
            + " LEFT JOIN hr.level f ON e.id_level=f.id "
            + " LEFT JOIN hr.golongan g ON f.id_golongan=g.id "
            + " LEFT JOIN hr.area_kantor h ON c.id_kantor_cabang=h.id_kantor "
            + " LEFT JOIN hr.area i ON h.id_area=i.id "
            + " WHERE a.id_pegawai=#{id} ")
    public Pegawai selectOneBpjs(@Param("id") String id) throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.pegawai_bpjs "
            + " (id_pegawai,bpjs_tk, bpjs_kes, prudential, allianz, jht_perusahaan, jht_pekerja, jkk, jkm, jp_perusahaan, jp_pekerja, iuran_prudential, iuran_allianz,kesehatan_perusahaan,kesehatan_pekerja, iuran_prudential_perusahaan, iuran_allianz_perusahaan)"
            + " VALUES "
            + " (#{id_pegawai},#{bpjs_tk}, #{bpjs_kes}, #{prudential}, #{allianz}, #{jht_perusahaan}, #{jht_pekerja}, #{jkk}, #{jkm}, #{jp_perusahaan}, #{jp_pekerja}, #{iuran_prudential}, #{iuran_allianz},#{kesehatan_perusahaan},#{kesehatan_pekerja}, #{iuran_prudential_perusahaan}, #{iuran_allianz_perusahaan})"
    )
    public int insertBpjs(Pegawai pegawai) throws RuntimeException;    

    @Update("UPDATE hr.pegawai_bpjs SET "
            + " bpjs_tk = #{bpjs_tk}, "
            + " bpjs_kes = #{bpjs_kes}, "
            + " prudential = #{prudential}, "
            + " allianz = #{allianz}, "
            + " jht_perusahaan = #{jht_perusahaan}, "
            + " jht_pekerja = #{jht_pekerja}, "
            + " jkk = #{jkk}, "
            + " jkm = #{jkm}, "
            + " jp_perusahaan = #{jp_perusahaan}, "
            + " jp_pekerja = #{jp_pekerja}, "
            + " kesehatan_perusahaan = #{kesehatan_perusahaan}, "
            + " kesehatan_pekerja = #{kesehatan_pekerja}, "
            + " iuran_prudential = #{iuran_prudential}, "
            + " iuran_allianz = #{iuran_allianz}, "
            + " iuran_prudential_perusahaan = #{iuran_prudential_perusahaan}, "
            + " iuran_allianz_perusahaan = #{iuran_allianz_perusahaan} "
            + " WHERE id_pegawai = #{id_pegawai}"
    )
    public int updateBpjs(Pegawai pegawai) throws RuntimeException;
    
    @Update("UPDATE pegawai  SET "
            + " pkwt_awal= #{pkwt_awal}, "
            + " pkwt_akhir= #{pkwt_akhir}, "
            + " probation_awal= #{probation_awal}, "
            + " probation_akhir= #{probation_akhir}, "
            + " pkwtt= #{pkwtt} "
            + " WHERE id_pegawai = #{id_pegawai} ")
    public int updateStatusKetenagakerjaan(Pegawai pegawai) throws RuntimeException;
    
    
    @Update("UPDATE hr.kontak_darurat  SET "
            + " id_hubungan = #{id_hubungan}, "
            + " nama = #{nama},"
            + " telpon = #{telpon}"
            + " WHERE id = #{id} ")
    public int updateKontakDarurat(KontakDarurat kontakDarurat) throws RuntimeException;
    
    @Select("SELECT a.* FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " INNER JOIN hr_departemen c ON a.id_departemen_new=c.id_departemen "
            + " INNER JOIN hr.area_kantor d ON a.id_kantor_new=d.id_kantor "
            + " WHERE c.id_departemen=#{id_departemen} AND lower(b.jabatan) NOT LIKE '%admin%' AND d.id_area IN (SELECT id_area FROM hr.area_kantor WHERE id_kantor=#{id_kantor})")
    public List<Pegawai> selectComboMarketing(@Param("id_departemen") Integer id_departemen,
            @Param("id_kantor") String id_kantor) throws RuntimeException;

    @Select("SELECT a.* FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE LOWER(b.jabatan) like '%dsr%' OR LOWER(b.jabatan) like '%dsm%' OR LOWER(b.jabatan) like '%key account excecutive%' OR LOWER(b.jabatan) like '%business%' OR a.id_jabatan_new=166 ")
    public List<Pegawai> selectComboMarketingAll() throws RuntimeException;
    
    @Select("SELECT a.* FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE a.status=true AND (a.isdriver=true OR lower(b.jabatan) LIKE '%driver%') ")
    public List<Pegawai> selectComboDriver() throws RuntimeException;

    @Select("SELECT a.* FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE LOWER(c.departemen) like '%marketing%' AND lower(b.jabatan) NOT LIKE '%sales admin%' OR a.id_jabatan_new=166")
    public List<Pegawai> selectComboMarketingAja() throws RuntimeException;

    @Select("SELECT c.*,d.jabatan FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " INNER JOIN pegawai c ON b.atasan=c.id_jabatan "
            + " INNER JOIN master_jabatan d ON c.id_jabatan=d.id_jabatan "
            + " WHERE a.id_pegawai=#{id_pegawai}")
    public Pegawai selectAtasan(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT a.*,b.jabatan FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan AND b.headdepartemen=TRUE "
            + " WHERE a.id_departemen_new = #{id_departemen} ")
    public Pegawai selectHeadDepartemen(@Param("id_departemen") Integer id_departemen) throws RuntimeException;
    
    @Select("SELECT a.*,b.jabatan FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan AND b.headdepartemen=TRUE "
            + " INNER JOIN hr.area_kantor c ON a.id_kantor_new=c.id_kantor  "
            + " WHERE a.id_departemen_new = #{id_departemen} AND a.status=true AND  c.id_area IN (SELECT id_area FROM hr.area_kantor WHERE id_kantor=#{id_kantor})")
    public Pegawai selectHeadDepartemenByKantor(@Param("id_departemen") Integer id_departemen,
            @Param("id_kantor") String id_kantor) throws RuntimeException;
    
    
    @Select("SELECT a.*,b.jabatan FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan AND b.headdepartemen=TRUE "
            + " INNER JOIN gudang c ON a.id_pegawai=c.dsm "
            + " WHERE c.id_gudang=#{id_gudang}")
    public Pegawai selectDsmGudang(@Param("id_gudang") String id_gudang) throws RuntimeException;

    @Select("SELECT a.*, b.jabatan FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE a.id_jabatan_new=#{id_jabatan}")
    public Pegawai selectJabatanPegawai(@Param("id_jabatan") Integer id_jabatan) throws RuntimeException;

    @Select("SELECT a.*, b.nama as kantor, c.departemen FROM master_jabatan a "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor=b.id_kantor_cabang "
            + " INNER JOIN hr_departemen c ON a.id_departemen=c.id_departemen "
            + " WHERE a.id_jabatan=#{id_jabatan}")
    public MasterJabatan selectJabatan(@Param("id_jabatan") Integer id_jabatan) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan AND b.headdepartemen=TRUE"
            + " WHERE a.id_departemen_new=#{id_departemen} ")
    public Integer selectCountHeadDepartemen(@Param("id_departemen") Integer id_departemen
            ) throws RuntimeException;
    
    @Select("SELECT a.sublevel as level,c.golongan FROM hr.level a "
            + "INNER JOIN hr.level_jabatan b ON a.id=b.id_level "
            + "INNER JOIN hr.golongan c ON a.id_golongan=c.id "
            + "WHERE b.id_jabatan=#{id_jabatan}")
    public Level selectLevel(@Param("id_jabatan") Integer id_jabatan )throws RuntimeException;
    
    @Select("SELECT * FROM hr.provinsi")
    public List<Provinsi> selecProvinsi() throws RuntimeException;
    
    @Select("SELECT * FROM hr.kabupaten WHERE id_provinsi=#{id_provinsi}")
    public List<Kabupaten> selecKabupaten(@Param("id_provinsi") String id_provinsi) throws RuntimeException;
    
    @Select("SELECT a.*,b.hubungan FROM hr.kontak_darurat a "
            + " INNER JOIN hr.hubungan_keluarga b ON a.id_hubungan=b.id"
            + " WHERE a.id=#{id} ")
    public KontakDarurat selectOneKontakDarurat(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.hubungan FROM hr.kontak_darurat a "
            + " INNER JOIN hr.hubungan_keluarga b ON a.id_hubungan=b.id"
            + " WHERE a.id_pegawai=#{id} ")
    public List<KontakDarurat> selectAllKontakDarurat(@Param("id") String id) throws RuntimeException;
    
    @Select("SELECT * FROM hr.hubungan_keluarga")
    public List<HubunganKeluarga> selectAllHubunganKeluarga() throws RuntimeException;
    
    @Select("SELECT * FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan AND b.headdepartemen=true "
            + " WHERE a.id_departemen_new = #{id_departemen} AND a.status=true")
    public Pegawai selectHeadDepartemenFromChild(@Param("id_departemen") Integer id_departemen) throws RuntimeException;
    
    @Select("SELECT * FROM pegawai a WHERE a.id_jabatan_new = #{id_jabatan}")
    public Pegawai selectPegawaiByJabatan(@Param("id_jabatan") Integer id_jabatan) throws RuntimeException;
    
    @Insert("INSERT INTO hr.tanggungan "
            + " (id_pegawai,nama,hubungan,tgl_lahir,no_identitas,kerja,status_perkawinan) "
            + " VALUES "
            + " (#{id_pegawai},#{nama},#{hubungan},#{tgl_lahir},#{no_identitas},#{kerja},#{status_perkawinan})")
    public int insertTanggungan(Tanggungan tanggungan) throws RuntimeException;
    
    @Select("SELECT a.*,b.hubungan as nama_hubungan "
            + " FROM hr.tanggungan a"
            + " INNER JOIN hr.hubungan_keluarga b ON a.hubungan=b.id "
            + " WHERE "
            + " a.id_pegawai=#{id_pegawai}")
    public List<Tanggungan> selectAllTanggungan(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.* FROM hr.tanggungan a"
            + " WHERE a.id=#{id} ")
    public Tanggungan selectOneTanggungan(@Param("id") Integer id) throws RuntimeException;
    
    @Update("UPDATE hr.tanggungan  SET "
            + " nama = #{nama}, "
            + " hubungan = #{hubungan},"
            + " tgl_lahir = #{tgl_lahir},"
            + " no_identitas = #{no_identitas},"
            + " kerja = #{kerja}, "
            + " status_perkawinan = #{status_perkawinan}"
            + " WHERE id = #{id} ")
    public int updateTanggungan(Tanggungan tanggungan) throws RuntimeException;
    
    @Delete("DELETE FROM hr.tanggungan WHERE id = #{id}")
    public int deleteTanggunngan(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.tanggungan WHERE id_pegawai=#{id_pegawai}")
    public int jumlahTanggungan (@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.*,b.amount as jkk "
            + " FROM hr.setting_bpjs a "
            + " INNER JOIN hr.jkk b On a.id_jkk=b.id_jkk "
            + " WHERE id=1")
    public SettingBpjs selectOneSettingBpjs() throws RuntimeException;
    
    //// PWKTT ///////////////////
    @Select("SELECT a.* FROM hr.pkwtt a"
            + " WHERE a.id_pegawai = #{id_pegawai} AND a.jenis=#{jenis}")
    public List<Pkwtt> selectAllPkwtt (@Param("id_pegawai") String id_pegawai,
            @Param("jenis") Character jenis) throws RuntimeException;
    
    @Select("SELECT a.* FROM hr.pkwtt a"
            + " WHERE a.id_pkwtt = #{id_pkwtt}")
    public Pkwtt selectOnePkwtt (@Param("id_pkwtt") Integer id_pkwtt) throws RuntimeException;
    
    @Insert("INSERT INTO hr.pkwtt "
            + " (jenis,id_pegawai,awal,akhir,keterangan) "
            + " VALUES "
            + " (#{jenis},#{id_pegawai},#{awal},#{akhir},#{keterangan}) ")
    public int insertPkwtt(Pkwtt pkwtt) throws RuntimeException;
    
    @Update("UPDATE hr.pkwtt SET "
            + " awal=#{awal}, "
            + " akhir=#{akhir}, "
            + " keterangan=#{keterangan} "
            + " WHERE "
            + " id_pkwtt=#{id_pkwtt}" )
    public int updatePkwtt(Pkwtt pkwtt) throws RuntimeException;
    
    @Delete("DELETE FROM hr.pkwtt WHERE id_pkwtt=#{id_pkwtt}")
    public int deletePkwtt(@Param("id_pkwtt") Integer id_pkwtt) throws RuntimeException;
    
    
    ////////// Pegawai gudang ///////
    
    @Select("SELECT a.* "
            + " FROM gudang a "
            + " WHERE a.id_gudang NOT IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai})"
            + " ORDER BY a.gudang ")
    public List<Gudang> selectAllGudang(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM gudang a"
            + " INNER JOIN hr.pegawai_gudang b ON a.id_gudang=b.id_gudang WHERE b.id_pegawai=#{id}"
            + " ORDER BY a.gudang ")
    public List<Gudang> selectPegawaiGudang(@Param("id") String id) throws RuntimeException;
    
    @Insert("INSERT INTO hr.pegawai_gudang "
            + "(id_pegawai,id_gudang) "
            + "VALUES "
            + "(#{id_pegawai}, #{id_gudang}) ")
    public int insertPegawaiGudang(PegawaiGudang pegawaiGudang)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_gudang WHERE id_pegawai = #{id_pegawai}")
    public int deletePegawaiGudang(@Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    @Select("SELECT * FROM hr.ptkp ORDER BY id_ptkp")
    public List<Ptkp> selectAllPtkp() throws RuntimeException;
    
    @Select("SELECT * FROM hr.ptkp WHERE id_ptkp=#{id_ptkp}")
    public Ptkp selectOnePtkp(@Param("id_ptkp") Integer id_ptkp) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama,c.ptkp FROM hr.pegawai_ptkp a"
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.ptkp c ON a.id_ptkp=c.id_ptkp "
            + " WHERE a.years=#{years}"
            + " ORDER BY b.nama")
    public List<PegawaiPtkp> selectAllPegawaiPtkp(@Param("years") String years) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama,c.ptkp FROM hr.pegawai_ptkp a"
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.ptkp c ON a.id_ptkp=c.id_ptkp "
            + " WHERE a.years=#{years} AND a.id_pegawai=#{id_pegawai}"
            )
    public PegawaiPtkp selectOnePegawaiPtkp(@Param("years") String years,@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT years,COUNT(*) as qty FROM hr.pegawai_ptkp GROUP BY years ORDER BY years")
    public List<PegawaiPtkp> countPegawaiPtkpByYears() throws RuntimeException;
    
    @Insert("INSERT INTO hr.pegawai_ptkp (years,id_ptkp,id_pegawai) VALUES (#{years},#{id_ptkp},#{id_pegawai})")
    public int insertPegawaiPtkp(PegawaiPtkp pegawaiPtkp) throws RuntimeException;
    
    @Update("UPDATE hr.pegawai_ptkp SET "
            + "id_ptkp=#{id_ptkp} "
            + " WHERE "
            + " years=#{years} AND id_pegawai=#{id_pegawai}")
    public int updatePegawaiPtkp (PegawaiPtkp pegawaiPtkp) throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_ptkp WHERE years=#{years}")
    public int deletePegawaiPtkpByYears(@Param("years") String years) throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_ptkp WHERE years=#{years} AND id_pegawai=#{id_pegawai}")
    public int deletePegawaiPtkpByPegawai(@Param("years") String years,@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.nama,a.id_pegawai,b.id_ptkp,b.years,c.ptkp as ptkp_old,c.ptkp FROM pegawai a "
            + " LEFT JOIN hr.pegawai_ptkp b ON a.id_pegawai=b.id_pegawai AND b.years=#{yearbefore} "
            + " LEFT JOIN hr.ptkp c ON b.id_ptkp=c.id_ptkp "
            + " WHERE a.id_pegawai NOT IN (SELECT id_pegawai FROM hr.pegawai_ptkp WHERE years=#{years}) AND a.status=true")
    public List<PegawaiPtkp> selectListPegawaiPtkp(@Param("years") String years,@Param("yearbefore") String yearbefore) throws RuntimeException;
    
    @Select("SELECT b.nominal FROM hr.pegawai_ptkp a "
            + " INNER JOIN hr.ptkp b ON a.id_ptkp=b.id_ptkp "
            + " WHERE a.years=#{years} AND a.id_pegawai=#{id_pegawai}")
    public Double tarifPtkpPegawai(@Param("years") String years,
            @Param("id_pegawai") String id_pegawai);
    
    @Select("SELECT headdepartemen FROM   master_jabatan a INNER JOIN pegawai b ON a.id_jabatan=b.id_jabatan_new WHERE b.id_departemen_new=102 AND b.id_pegawai=#{id_pegawai}")
    public Boolean checkHeadDepartemen(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
}
