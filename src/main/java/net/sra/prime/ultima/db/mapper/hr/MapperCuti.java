/*
 * Copyright 2016 JoinFaces.
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
package net.sra.prime.ultima.db.mapper.hr;

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.db.provider.hr.ProviderCuti;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.Cuti;
import net.sra.prime.ultima.entity.hr.CutiJabatan;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;
import net.sra.prime.ultima.entity.hr.CutiMaster;
import net.sra.prime.ultima.entity.hr.CutiPersetujuan;
import net.sra.prime.ultima.entity.hr.CutiSub;
import net.sra.prime.ultima.entity.hr.CutiTahunan;
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
public interface MapperCuti {

    @Select("SELECT a.*,b.nama_master,"
            + " array_to_string(array(SELECT CONCAT('<div class=\"ui-g-3\">',c.jabatan,'</div>') as jabatan FROM master_jabatan c INNER JOIN hr.cuti_jabatan d ON c.id_jabatan=d.id_jabatan WHERE a.id=d.id_cuti),' ') as jabatan, "
            + " array_to_string(array(SELECT CONCAT('<div class=\"ui-g-3\">',c.nama_sub_cuti, '</div><div class=\"ui-g-3\">',c.jumlahhari, ' hari kerja </div><div class=\"ui-g-3\">&nbsp;</div><div class=\"ui-g-3\">&nbsp;</div>')  FROM hr.cuti_sub c  WHERE c.id_cuti=a.id),' ') as keterangan_khusus "
            + " FROM hr.cuti a INNER JOIN hr.cuti_master b On a.master=b.id"
            + " ORDER BY b.id ASC")
    public List<Cuti> selectAll() throws RuntimeException;

    @Select("SELECT CONCAT('<h:outputText value=\"',nama_sub_cuti, '\" />','<h:outputText value=\"',jumlahhari, 'hari kerja\" />') as nama_sub_cuti FROM hr.cuti_sub "
            + " ORDER BY id ")
    public String selectAllCutiKhusus() throws RuntimeException;

    @Insert("INSERT INTO hr.cuti "
            + "(nama_cuti,batasan,saldo,perbaharui,tgl_perbaharui,mulai_menggunakan,persetujuan1,jabatan_persetujuan1,persetujuan2,jabatan_persetujuan2, "
            + " persetujuan3,jabatan_persetujuan3,persetujuan4,jabatan_persetujuan4,jenis,master,status,all_employee) "
            + "VALUES "
            + "(#{nama_cuti},#{batasan},#{saldo},#{perbaharui},#{tgl_perbaharui},#{mulai_menggunakan},#{persetujuan1},#{jabatan_persetujuan1},#{persetujuan2},#{jabatan_persetujuan2}, "
            + "#{persetujuan3},#{jabatan_persetujuan3},#{persetujuan4},#{jabatan_persetujuan4},#{jenis},#{master},#{status},#{all_employee}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Cuti cuti) throws RuntimeException;

    @Delete("DELETE FROM hr.cuti WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * FROM hr.cuti WHERE id=#{id}")
    public Cuti selectOne(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT * FROM hr.cuti WHERE master=#{id_master} AND all_employee = true")
    public Cuti selectOneCutiAllEmployee(@Param("id_master") Integer id_master) throws RuntimeException;

    @Update("UPDATE hr.cuti  SET "
            + " nama_cuti = #{nama_cuti}, "
            + " batasan = #{batasan}, "
            + " saldo = #{saldo}, "
            + " perbaharui = #{perbaharui}, "
            + " tgl_perbaharui = #{tgl_perbaharui}, "
            + " mulai_menggunakan = #{mulai_menggunakan}, "
            + " persetujuan1 = #{persetujuan1}, "
            + " jabatan_persetujuan1 = #{jabatan_persetujuan1}, "
            + " persetujuan2 = #{persetujuan2}, "
            + " jabatan_persetujuan2 = #{jabatan_persetujuan2}, "
            + " persetujuan3 = #{persetujuan3}, "
            + " jabatan_persetujuan3 = #{jabatan_persetujuan3}, "
            + " persetujuan4 = #{persetujuan4}, "
            + " jabatan_persetujuan4 = #{jabatan_persetujuan4}, "
            + " jenis = #{jenis}, "
            + " status = #{status}, "
            + " master = #{master}, "
            + " all_employee = #{all_employee} "
            + " WHERE id = #{id} ")
    public int update(Cuti cuti) throws RuntimeException;

    @Insert("INSERT INTO hr.cuti_jabatan "
            + "(id_cuti,id_jabatan) "
            + "VALUES "
            + "(#{id_cuti},#{id_jabatan}) ")
    public int insertCutiJabatan(CutiJabatan cutiJabatan) throws RuntimeException;

    @Delete("DELETE FROM hr.cuti_jabatan WHERE id_cuti = #{id_cuti}")
    public int deleteCutiJabatan(@Param("id_cuti") Integer id_cuti) throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM hr.cuti_jabatan a WHERE a.id_cuti=#{id_cuti} "
            + " ORDER BY a.id_cuti ")
    public List<CutiJabatan> selectAllCutiJabatan(@Param("id_cuti") Integer id_cuti) throws RuntimeException;
    
    @Select("SELECT a.*, d.nama_master as nama_tipe,c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master d ON b.master=d.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE  a.id_pegawai = #{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            )
    public List<CutiKaryawan> myCuti(@Param("id_pegawai") String id_pegawai,
                    @Param("tahun") String tahun) throws RuntimeException;
    
    @Select("SELECT a.*, d.nama_master as nama_tipe,c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master d ON b.master=d.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN hr.cuti_persetujuan e ON a.id=e.id_cuti_karyawan  AND e.id_pegawai=#{id_pegawai} AND e.status is not null "
            + " WHERE  a.id_pegawai != #{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal_awal DESC"
            )
    public List<CutiKaryawan> myOtherCuti(@Param("id_pegawai") String id_pegawai,
                    @Param("tahun") String tahun) throws RuntimeException;
    
    @Select("SELECT a.*, d.nama_master as nama_tipe,c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master d ON b.master=d.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE   "
            + " EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal_awal DESC"
            )
    public List<CutiKaryawan> selectAllTimeOffByYear(@Param("tahun") String tahun) throws RuntimeException;
    
    @Select("SELECT a.*, d.nama_master as nama_tipe,c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master d ON b.master=d.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE  a.id_pegawai != #{id_pegawai} AND a.create_by = #{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal_awal DESC"
            )
    public List<CutiKaryawan> otherTimeOff(@Param("id_pegawai") String id_pegawai,
                    @Param("tahun") String tahun) throws RuntimeException;


    @Select("SELECT a.*,c.nama_master as nama_tipe,e.nama as nama_pegawai,f.nama as create_by_name "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master c ON b.master=c.id "
            + " INNER JOIN hr.cuti_persetujuan d ON a.id=d.id_cuti_karyawan "
            + " INNER JOIN pegawai e ON a.id_pegawai=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " WHERE "
            + " d.id_pegawai=#{id_pegawai} "
            + " AND d.status is null "
            + " AND ((a.status='D' AND d.urut=1) OR (a.status='W' AND d.urut=2))"
            )
    public List<CutiKaryawan> selectNotifikasiCutiKaryawan(
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT a.*,c.nama_master as nama_tipe,e.nama as nama_pegawai,f.nama as create_by_name "
            + " FROM hr.cuti_karyawan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN hr.cuti_master c ON b.master=c.id "
            + " INNER JOIN hr.cuti_persetujuan d ON a.id=d.id_cuti_karyawan "
            + " INNER JOIN pegawai e ON a.id_pegawai=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " WHERE "
            + " d.id_pegawai=#{id_pegawai} "
            + " AND d.status is null "
            + " AND a.status != 'R' "
            + " AND a.status != 'C'"
            )
    public List<CutiKaryawan> selectNotifikasiCutiKaryawan1(
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    
    @SelectProvider(type = ProviderCuti.class, method = "selectCutiKaryawan")
    public List<CutiKaryawan> selectAllCutiKaryawan(
            @Param("id_departemen") Integer id_departemen,
            @Param("id_jabatan") Integer id_jabatan,
            @Param("id_cuti") Integer id_cuti,
            @Param("status") Character status,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir
    ) throws RuntimeException;
    
    

    @Insert("INSERT INTO hr.cuti_karyawan "
            + "(id_cuti,waktu_cuti,tanggal_awal,jenis_awal,tanggal_akhir,jenis_akhir,telpon,alasan,status,persetujuan1,persetujuan2,create_by,create_date,id_pegawai,id_cuti_sub,keterangan_status) "
            + "VALUES "
            + "(#{id_cuti},#{waktu_cuti},#{tanggal_awal},#{jenis_awal},#{tanggal_akhir},#{jenis_akhir},#{telpon},#{alasan},#{status},#{persetujuan1},#{persetujuan2},#{create_by},LOCALTIMESTAMP,#{id_pegawai},#{id_cuti_sub},#{keterangan_status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertCutiKaryawan(CutiKaryawan cutiKaryawan) throws RuntimeException;

    @Delete("DELETE FROM hr.cuti_karyawan WHERE id = #{id}")
    public int deleteCutiKaryawan(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*, e.nama_master as nama_cuti,c.nama as nama_pegawai,f.jabatan, g.nama as create_by_name,h.nama_sub_cuti,b.master as id_master  "
            + " FROM hr.cuti_karyawan a"
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN hr.cuti_master e ON e.id=b.master "
            + " INNER JOIN master_jabatan f ON c.id_jabatan_new = f.id_jabatan "
            + " INNER JOIN pegawai g ON a.create_by=g.id_pegawai "
            + " LEFT JOIN hr.cuti_sub h ON a.id_cuti_sub=h.id "
            + " WHERE a.id=#{id}")
    public CutiKaryawan selectOneCutiKaryawan(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE hr.cuti  SET "
            + " tipe_cuti = #{tipe_cuti}, "
            + " waktu_cuti = #{waktu_cuti}, "
            + " tanggal_awal = #{tanggal_awal}, "
            + " jenis_awal = #{jenis_awal}, "
            + " tanggal_akhir = #{tanggal_akhir}, "
            + " jenis_akhir = #{jenis_akhir}, "
            + " telpon = #{telpon}, "
            + " alasan = #{alasan}, "
            + " status = #{status}, "
            + " persetujuan1 = #{persetujuan1}, "
            + " persetujuan2 = #{persetujuan2} "
            + " WHERE id = #{id} ")
    public int updateCutiKaryawan(CutiKaryawan cutiKaryawan) throws RuntimeException;

    @Insert("INSERT INTO hr.cuti_master "
            + "(nama_master) "
            + "VALUES "
            + "(#{nama_master}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertCutiMaster(CutiMaster cutiMaster) throws RuntimeException;

    @Delete("DELETE FROM hr.cuti_master WHERE id = #{id}")
    public int deleteCutiMaster(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT COUNT(*) FROM hr.cuti WHERE master=(SELECT c.master FROM  hr.cuti c WHERE id=#{id})")
    public int jumlahCuti(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * "
            + " FROM hr.cuti_master "
            + " ORDER BY nama_master ")
    public List<CutiMaster> selectAllCutiMaster() throws RuntimeException;

    @Select("SELECT b.* "
            + " FROM hr.cuti_jabatan a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " WHERE a.id_cuti=#{id_cuti}"
            + " ORDER BY b.id_jabatan ")
    public List<MasterJabatan> selectAllJabatan(@Param("id_cuti") Integer id_cuti) throws RuntimeException;

    @Select("SELECT * "
            + " FROM master_jabatan "
            + " WHERE id_jabatan NOT IN (SELECT id_jabatan FROM hr.cuti_jabatan a "
            + " INNER JOIN hr.cuti b ON a.id_cuti=b.id WHERE b.master= (SELECT c.master FROM  hr.cuti c WHERE id=#{id}))"
            + " ORDER BY id_jabatan ")
    public List<MasterJabatan> selectAllTblJabatan(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM hr.cuti a "
            + " INNER JOIN hr.cuti_master b ON a.master=b.id "
            + " INNER JOIN hr.cuti_jabatan c ON a.id=c.id_cuti "
            + " WHERE "
            + " a.master=#{id_master} AND c.id_jabatan=#{id_jabatan}")
    public Cuti selectOneCutiFromMaster(@Param("id_master") Integer id_master,
            @Param("id_jabatan") Integer id_jabatan) throws RuntimeException;

    @Select("SELECT * FROM hr.cuti_sub ORDER BY id")
    public List<CutiSub> selectAllCutiSub() throws RuntimeException;

    @Select("SELECT a.*,b.jabatan FROM pegawai a  "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE id_pegawai=#{id_pegawai}")
    public Pegawai selectOnePegawai(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    ///////////// Cuti Persetujuan /////////////
    @Insert("INSERT INTO hr.cuti_persetujuan "
            + "(id_cuti_karyawan,id_pegawai,urut) "
            + "VALUES "
            + "(#{id_cuti_karyawan},#{id_pegawai},#{urut}) ")
    public int insertCutiPersetujuan(CutiPersetujuan cutiPersetujuan) throws RuntimeException;
    
    @Update("UPDATE hr.cuti_persetujuan  SET "
            + " status = #{status}, "
            + " tanggal_persetujuan = LOCALTIMESTAMP "
            + " WHERE id_cuti_karyawan = #{id_cuti_karyawan} AND id_pegawai=#{id_pegawai}")
    public int updatePersetujuaan(@Param("status") Boolean status,
            @Param("id_cuti_karyawan") Integer id_cuti_karyawan,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Update("UPDATE hr.cuti_karyawan SET "
            + " status=#{status}, "
            + " keterangan_status=#{keterangan_status} "
            + " WHERE "
            + " id=#{id}")
    public int updateStatusCutiKaryawan(@Param("status") Character status,
            @Param("id") Integer id,
            @Param("keterangan_status") String keterangan_status) throws RuntimeException;
    
    @Select("SELECT count(*) FROM hr.cuti_persetujuan WHERE id_cuti_karyawan=#{id_cuti_karyawan}")
    public int countPersetujuan(@Param("id_cuti_karyawan") Integer id_cuti_karyawan) throws RuntimeException;
    
    @Select("SELECT a.*,c.nama as nama_pegawai"
            + " FROM hr.cuti_karyawan a"
            + " INNER JOIN hr.cuti_persetujuan b ON a.id=b.id_cuti_karyawan "
            + " INNER JOIN pegawai c ON b.id_pegawai=c.id_pegawai "
            + " WHERE a.id=#{id} AND b.urut=#{urut}")
    public CutiKaryawan selectPersetujuankedua(@Param("id") Integer id,
            @Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT id_jadwal_kerja FROM hr.jadwal_kerja_karyawan WHERE id_pegawai=#{id_pegawai}")
    public Integer selectIdJadwalKerja(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.cuti_persetujuan "
            + " WHERE "
            + " id_cuti_karyawan=#{id_cuti_karyawan} AND id_pegawai=#{id_pegawai}  AND status is null")
    public Integer searchPersetujuan (@Param("id_cuti_karyawan") Integer id_lembur_karyawan,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    
    @Select("SELECT COUNT(*) FROM hr.cuti_persetujuan "
            + " WHERE "
            + " id_cuti_karyawan=#{id_cuti_karyawan} AND id_pegawai=#{id_pegawai} AND urut=#{urut} AND status is null")
    public Integer searchPersetujuanBerurut (@Param("id_cuti_karyawan") Integer id_cuti_karyawan,
            @Param("id_pegawai") String id_pegawai, 
            @Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as nama_pegawai "
            + " FROM hr.cuti_persetujuan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.cuti_karyawan c ON a.id_cuti_karyawan=c.id "
            + " WHERE "
            + " a.id_cuti_karyawan = #{id_cuti_karyawan} "
            + " ORDER BY a.urut" )
    public List<CutiPersetujuan> selectAllCutiPersetujuan(@Param("id_cuti_karyawan") Integer id_cuti_karyawan) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.cuti_persetujuan a "
            + " WHERE "
            + " a.id_cuti_karyawan = #{id_cuti_karyawan} "
            + " ORDER BY a.urut DESC LIMIT 1" )
    public CutiPersetujuan selectMaxCutiPersetujuan(@Param("id_cuti_karyawan") Integer id_cuti_karyawan) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.cuti_persetujuan a "
            + " WHERE "
            + " a.id_cuti_karyawan = #{id_cuti_karyawan} AND a.id_pegawai=#{id_pegawai}"
            )
    public CutiPersetujuan cekCutiPersetujuan(@Param("id_cuti_karyawan") Integer id_cuti_karyawan,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
 
    @Select("SELECT * FROM hr.absen_periode WHERE #{date} >= start AND #{date} <= end_date")
    public AbsenPeriode selectAbsenPeriode(@Param("date") Date date) throws RuntimeException;
    
    @Select("SELECT status FROM hr.absen_pegawai_status WHERE month=#{month} AND year=#{year} AND id_pegawai=#{id_pegawai}")
    public Character statusAbsen (@Param("month") Integer month,
            @Param("year") String year,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Update("UPDATE hr.absen  SET "
            + " id_cuti = null, "
            + " keterangan = null, "
            + " id_status_absen = null"
            + " WHERE id_absen = #{id_absen} AND tanggal=#{tanggal}")
    public int timeOffCancel(@Param("tanggal") Date tanggal,
            @Param("id_absen") Integer id_absen)  throws RuntimeException;
    
    @Update("UPDATE hr.cuti_karyawan SET note=#{note} WHERE id=#{id}")
    public int addNote(@Param("note") String note,
            @Param("id") Integer id) throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.cuti_tahunan "
            + "(year,id_pegawai,saldo,jumlah) "
            + "VALUES "
            + "(#{year}, #{id_pegawai},#{saldo},#{jumlah}) ")
    public int insertCutiTahunan(@Param("year") String year,
            @Param("id_pegawai") String id_pegawai,
            @Param("jumlah") Integer jumlah,
            @Param("saldo") Integer saldo)  throws RuntimeException;
    
    @Update("UPDATE hr.cuti_tahunan  SET "
            + " jumlah = jumlah + #{jumlah} "
            + " WHERE year = #{year} AND id_pegawai=#{id_pegawai}")
    public int updateCutiTahunan(@Param("year") String year,
            @Param("id_pegawai") String id_pegawai,
            @Param("jumlah") Integer jumlah)  throws RuntimeException;
    
    @Update("UPDATE hr.cuti_tahunan  SET "
            + " jumlah = #{jumlah}, "
            + " saldo = #{saldo} "
            + " WHERE year = #{year} AND id_pegawai=#{id_pegawai}")
    public int updateCutiTahunanSaldo(@Param("year") String year,
            @Param("id_pegawai") String id_pegawai,
            @Param("jumlah") Integer jumlah,
            @Param("saldo") Integer saldo)  throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.cuti_tahunan WHERE year=#{year} AND id_pegawai=#{id_pegawai}")
    public int checkCutitahunan (
            @Param("year") String year,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT * FROM hr.cuti_tahunan WHERE year=#{year} AND id_pegawai=#{id_pegawai}")
    public CutiTahunan selectCutitahunan (
            @Param("year") String year,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen, coalesce(e.saldo,12) as saldo, coalesce(e.jumlah,0) as jumlah,coalesce(e.saldo - e.jumlah,12) as sisa "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.cuti_tahunan e ON a.id_pegawai=e.id_pegawai AND year=#{year}"
            + " WHERE a.status=true"
            + " ORDER BY a.nip")
    public List<CutiTahunan> selectAllCutitahunan (
            @Param("year") String year) throws RuntimeException;
}
