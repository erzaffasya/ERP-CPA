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
import net.sra.prime.ultima.db.provider.hr.ProviderLembur;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Lembur;
import net.sra.prime.ultima.entity.hr.LemburJabatan;
import net.sra.prime.ultima.entity.hr.LemburKaryawan;
import net.sra.prime.ultima.entity.hr.LemburMultiplier;
import net.sra.prime.ultima.entity.hr.LemburPersetujuan;
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
public interface MapperLembur {

    @Select("SELECT a.*,"
            + " array_to_string(array(SELECT CONCAT('<div class=\"ui-g-3\">',c.jabatan,'</div>') as jabatan FROM master_jabatan c INNER JOIN hr.lembur_jabatan d ON c.id_jabatan=d.id_jabatan WHERE a.id=d.id_lembur),' ') as jabatan "
            + " FROM hr.lembur a "
            + " ORDER BY a.id ASC")
    public List<Lembur> selectAll() throws RuntimeException;

    @Select("SELECT CONCAT('<h:outputText value=\"',nama_sub_lembur, '\" />','<h:outputText value=\"',jumlahhari, 'hari kerja\" />') as nama_sub_lembur FROM hr.lembur_sub "
            + " ORDER BY id ")
    public String selectAllLemburKhusus() throws RuntimeException;

    @Insert("INSERT INTO hr.lembur "
            + " (nama,kebijakan,persetujuan1,jabatan_persetujuan1,persetujuan2,jabatan_persetujuan2, "
            + " persetujuan3,jabatan_persetujuan3,persetujuan4,jabatan_persetujuan4,biaya,create_by,create_date) "
            + " VALUES "
            + " (#{nama},#{kebijakan},#{persetujuan1},#{jabatan_persetujuan1},#{persetujuan2},#{jabatan_persetujuan2}, "
            + " #{persetujuan3},#{jabatan_persetujuan3},#{persetujuan4},#{jabatan_persetujuan4},#{biaya},#{create_by},LOCALTIMESTAMP) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Lembur lembur) throws RuntimeException;

    @Delete("DELETE FROM hr.lembur WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * FROM hr.lembur WHERE id=#{id}")
    public Lembur selectOne(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE hr.lembur  SET "
            + " nama = #{nama}, "
            + " kebijakan = #{kebijakan}, "
            + " biaya = #{biaya}, "
            + " persetujuan1 = #{persetujuan1}, "
            + " jabatan_persetujuan1 = #{jabatan_persetujuan1}, "
            + " persetujuan2 = #{persetujuan2}, "
            + " jabatan_persetujuan2 = #{jabatan_persetujuan2}, "
             + " persetujuan3 = #{persetujuan3}, "
            + " jabatan_persetujuan3 = #{jabatan_persetujuan3}, "
            + " persetujuan4 = #{persetujuan4}, "
            + " jabatan_persetujuan4 = #{jabatan_persetujuan4} "
            + " WHERE id = #{id} ")
    public int update(Lembur lembur) throws RuntimeException;

    @Insert("INSERT INTO hr.lembur_jabatan "
            + "(id_lembur,id_jabatan) "
            + "VALUES "
            + "(#{id_lembur},#{id_jabatan}) ")
    public int insertLemburJabatan(LemburJabatan lemburJabatan) throws RuntimeException;

    @Delete("DELETE FROM hr.lembur_jabatan WHERE id_lembur = #{id_lembur}")
    public int deleteLemburJabatan(@Param("id_lembur") Integer id_lembur) throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM hr.lembur_jabatan a WHERE a.id_lembur=#{id_lembur} "
            + " ORDER BY a.id_lembur ")
    public List<LemburJabatan> selectAllLemburJabatan(@Param("id_lembur") Integer id_lembur) throws RuntimeException;

    @Select("SELECT b.* "
            + " FROM hr.lembur_jabatan a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan"
            + " WHERE a.id_lembur=#{id_lembur} "
            + " ORDER BY a.id_lembur ")
    public List<MasterJabatan> selectAllJabatanTarget(@Param("id_lembur") Integer id_lembur) throws RuntimeException;
    
    
    @Select("SELECT a.*,e.nama as nama_pegawai,f.nama as create_by_name "
            + " FROM hr.lembur_karyawan a "
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN hr.lembur_persetujuan d ON a.id=d.id_lembur_karyawan AND a.revisi=d.revisi "
            + " INNER JOIN pegawai e ON a.id_pegawai=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " WHERE "
            + " d.id_pegawai=#{id_pegawai} "
            + " AND d.status is null "
            + " AND a.status != 'R'"
            + " AND a.status != 'C'"
            )
    public List<LemburKaryawan> selectNotifikasiLemburKaryawan(
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
   /** berurutan 
    @Select("SELECT a.*,e.nama as nama_pegawai,f.nama as create_by_name "
            + " FROM hr.lembur_karyawan a "
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN hr.lembur_persetujuan d ON a.id=d.id_lembur_karyawan AND a.revisi=d.revisi "
            + " INNER JOIN pegawai e ON a.id_pegawai=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " WHERE "
            + " d.id_pegawai=#{id_pegawai} "
            + " AND d.status is null "
            + " AND ((a.status='D' AND d.urut=1) OR (a.status='W' "
            + " AND d.urut=(SELECT urut+1 FROM hr.lembur_persetujuan WHERE id_lembur_karyawan=a.id AND revisi=a.revisi AND status=true ORDER BY URUT DESC LIMIT 1)))"
            )
    public List<LemburKaryawan> selectNotifikasiLemburKaryawan(
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    **/
    @Select("SELECT a.*" 
            + " FROM hr.lembur_karyawan a "
            + " WHERE a.tanggal >=#{datestart} AND a.tanggal <=#{dateend} AND a.id_pegawai=#{id_pegawai}" 
            + " ORDER BY a.tanggal ")
    public List<LemburKaryawan> selectLemburKaryawan(@Param("id_pegawai") String id_pegawai,
            @Param("datestart") Date datestart,
            @Param("dateend") Date dateend)  throws RuntimeException;

    @SelectProvider(type = ProviderLembur.class, method = "selectLemburKaryawan")
    public List<LemburKaryawan> selectAllLemburKaryawan(
            @Param("id_departemen") Integer id_departemen,
            @Param("id_jabatan") Integer id_jabatan,
            @Param("id_lembur") Integer id_lembur,
            @Param("status") Character status,
            @Param("bulan") Date bulan,
            @Param("id_pegawai") String id_pegawai
    ) throws RuntimeException;
    @Select("SELECT a.*, c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.lembur_karyawan a "
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal DESC"
            )
    public List<LemburKaryawan> myOvertime(@Param("tahun") String tahun, 
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT a.*, c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.lembur_karyawan a "
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN hr.lembur_persetujuan e ON a.id=e.id_lembur_karyawan AND a.revisi=e.revisi AND e.id_pegawai=#{id_pegawai} AND e.status is not null "
            + " WHERE a.id_pegawai!=#{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal DESC"
            )
    public List<LemburKaryawan> otherOvertime(@Param("tahun") String tahun, 
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    
    @Insert("INSERT INTO hr.lembur_karyawan "
            + "(tanggal,id_pegawai,hour_before,hour_after,keterangan,status,create_by,create_date,keterangan_status,id_lembur,break_time,revisi,nomor) "
            + "VALUES "
            + "(#{tanggal},#{id_pegawai},#{hour_before},#{hour_after},#{keterangan},#{status},#{create_by},LOCALTIMESTAMP,#{keterangan_status},#{id_lembur},#{break_time},#{revisi},#{nomor}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertLemburKaryawan(LemburKaryawan lemburKaryawan) throws RuntimeException;
    
    @Insert("INSERT INTO hr.lembur_karyawan "
            + "(id,tanggal,id_pegawai,hour_before,hour_after,keterangan,status,create_by,create_date,keterangan_status,id_lembur,break_time,revisi,nomor) "
            + "VALUES "
            + "(#{id},#{tanggal},#{id_pegawai},#{hour_before},#{hour_after},#{keterangan},#{status},#{create_by},LOCALTIMESTAMP,#{keterangan_status},#{id_lembur},#{break_time},#{revisi},#{nomor}) ")
    public int insertLemburKaryawanRevisi(LemburKaryawan lemburKaryawan) throws RuntimeException;

    @Delete("DELETE FROM hr.lembur_karyawan WHERE id = #{id}")
    public int deleteLemburKaryawan(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*,c.nama as nama_pegawai,f.jabatan, g.nama as create_by_name  "
            + " FROM hr.lembur_karyawan a"
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN master_jabatan f ON c.id_jabatan_new = f.id_jabatan "
            + " INNER JOIN pegawai g ON a.create_by=g.id_pegawai "
            + " WHERE a.id=#{id} AND revisi=#{revisi}")
    public LemburKaryawan selectOneLemburKaryawan(@Param("id") Integer id, 
            @Param("revisi") Integer revisi) throws RuntimeException;
    
    @Select("SELECT * FROM hr.lembur_karyawan WHERE tanggal=#{tanggal} AND id_pegawai=#{id_pegawai}")
    public LemburKaryawan checkOvertime(@Param("tanggal") Date tanggal,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT * FROM hr.lembur_karyawan WHERE id=#{id} AND revisi=#{revisi}")
    public LemburKaryawan selectLemburKaryawanById(@Param("id") Integer id,
            @Param("revisi") Integer revisi) throws RuntimeException;
    
    
    @Update("UPDATE hr.lembur_karyawan  SET "
            + " status = 'R', "
            + " keterangan_status = 'direvisi' "
            + " WHERE id = #{id} AND revisi = #{revisi} ")
    public int updateLemburKaryawanRevisi(@Param("id") Integer id,
            @Param("revisi") Integer revisi) throws RuntimeException;

    
    @Select("SELECT COUNT(*) FROM hr.lembur WHERE master=(SELECT c.master FROM  hr.lembur c WHERE id=#{id})")
    public int jumlahLembur(@Param("id") Integer id) throws RuntimeException;

    
    
    @Select("SELECT * "
            + " FROM master_jabatan "
            + " WHERE id_jabatan NOT IN (SELECT id_jabatan FROM hr.lembur_jabatan) "
            + " ORDER BY id_jabatan ")
    public List<MasterJabatan> selectAllJabatanSource() throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM master_jabatan WHERE id_jabatan >= 100"
            + " ORDER BY id_jabatan ")
    public List<MasterJabatan> selectAllJabatan() throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM hr.lembur a "
            + " INNER JOIN hr.lembur_jabatan c ON a.id=c.id_lembur "
            + " WHERE "
            + " c.id_jabatan=#{id_jabatan_new}")
    public Lembur selectOneLemburFromMaster(
            @Param("id_jabatan_new") Integer id_jabatan_new) throws RuntimeException;

    
    @Select("SELECT a.*,b.jabatan FROM pegawai a  "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new=b.id_jabatan "
            + " WHERE id_pegawai=#{id_pegawai}")
    public Pegawai selectOnePegawai(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    ///////////// Lembur Persetujuan /////////////
    @Select("SELECT a.*,b.nama as nama_pegawai "
            + " FROM hr.lembur_persetujuan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.lembur_karyawan c ON a.id_lembur_karyawan=c.id AND a.revisi = c.revisi "
            + " WHERE "
            + " a.id_lembur_karyawan = #{id_lembur_karyawan} AND a.revisi = #{revisi} "
            + " ORDER BY a.urut" )
    public List<LemburPersetujuan> selectAllLemburPersetujuan(@Param("id_lembur_karyawan") Integer id_lembur_karyawan,
            @Param("revisi") Integer revisi) throws RuntimeException;
    
    @Insert("INSERT INTO hr.lembur_persetujuan "
            + "(id_lembur_karyawan,id_pegawai,urut,revisi) "
            + "VALUES "
            + "(#{id_lembur_karyawan},#{id_pegawai},#{urut},#{revisi}) ")
    public int insertLemburPersetujuan(LemburPersetujuan lemburPersetujuan) throws RuntimeException;
    
    @Update("UPDATE hr.lembur_persetujuan  SET "
            + " status = #{status}, "
            + " tanggal_persetujuan = LOCALTIMESTAMP "
            + " WHERE id_lembur_karyawan = #{id_lembur_karyawan} AND id_pegawai=#{id_pegawai} AND revisi=#{revisi}")
    public int updatePersetujuaan(@Param("status") Boolean status,
            @Param("id_lembur_karyawan") Integer id_lembur_karyawan,
            @Param("id_pegawai") String id_pegawai,
            @Param("revisi") Integer revisi) throws RuntimeException;
    
    
    @Update("UPDATE hr.lembur_karyawan SET "
            + " status=#{status}, "
            + " keterangan_status=#{keterangan_status} "
            + " WHERE "
            + " id=#{id} AND revisi=#{revisi}")
    public int updateStatusLemburKaryawan(@Param("status") Character status,
            @Param("id") Integer id,
            @Param("keterangan_status") String keterangan_status,
            @Param("revisi") Integer revisi ) throws RuntimeException;
    
    @Select("SELECT count(*) FROM hr.lembur_persetujuan WHERE id_lembur_karyawan=#{id_lembur_karyawan}")
    public int countPersetujuan(@Param("id_lembur_karyawan") Integer id_lembur_karyawan) throws RuntimeException;
    
    @Select("SELECT a.*,c.nama as nama_pegawai"
            + " FROM hr.lembur_karyawan a"
            + " INNER JOIN hr.lembur_persetujuan b ON a.id=b.id_lembur_karyawan "
            + " INNER JOIN pegawai c ON b.id_pegawai=c.id_pegawai "
            + " WHERE a.id=#{id} AND b.urut=#{urut}")
    public LemburKaryawan selectPersetujuankedua(@Param("id") Integer id,
            @Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.lembur_persetujuan a "
            + " WHERE "
            + " a.id_lembur_karyawan = #{id_lembur_karyawan} "
            + " ORDER BY a.urut DESC LIMIT 1" )
    public LemburPersetujuan selectMaxLemburPersetujuan(@Param("id_lembur_karyawan") Integer id_lembur_karyawan) throws RuntimeException;
    
    
    /////// Lembur Multiplier ///////////
    
    @Insert("INSERT INTO hr.lembur_multiplier "
            + "(id_lembur,jenis,urut,mulai,selesai,multiply) "
            + "VALUES "
            + "(#{id_lembur},#{jenis},#{urut},#{mulai},#{selesai},#{multiply}) ")
    public int insertLemburMultiplier(LemburMultiplier lemburMultiplier) throws RuntimeException;
    
    @Delete("DELETE FROM hr.lembur_multiplier WHERE id_lembur = #{id_lembur}")
    public int deleteLemburMultiplier(@Param("id_lembur") Integer id_lembur) throws RuntimeException;
    
    @Select("SELECT * FROM hr.lembur_multiplier WHERE id_lembur=#{id_lembur} AND jenis=#{jenis}")
    public List<LemburMultiplier> selectJenisMultiplier(@Param("id_lembur") Integer id_lembur,
            @Param("jenis") Character jenis) throws RuntimeException;
    
    @Select("SELECT id_jadwal_kerja FROM hr.jadwal_kerja_karyawan WHERE id_pegawai=#{id_pegawai}")
    public Integer selectIdJadwalKerja(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.lembur_multiplier a "
            + " INNER JOIN hr.lembur b ON b.id=a.id_lembur "
            + " INNER JOIN hr.lembur_jabatan c ON a.id_lembur=c.id_lembur "
            + "WHERE c.id_jabatan=#{id_jabatan} AND a.jenis=#{jenis} "
            + " ORDER BY a.urut DESC")
    public List<LemburMultiplier> selectMultiplierbyJabatan(@Param("id_jabatan") Integer id_jabatan,
            @Param("jenis") Character jenis) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.lembur_persetujuan "
            + " WHERE "
            + " id_lembur_karyawan=#{id_lembur_karyawan} AND id_pegawai=#{id_pegawai}  AND revisi = #{revisi} AND status is null")
    public Integer searchPersetujuan (@Param("id_lembur_karyawan") Integer id_lembur_karyawan,
            @Param("id_pegawai") String id_pegawai, 
            @Param("revisi") Integer revisi) throws RuntimeException;
    
    @Select("SELECT MAX(SUBSTR(a.nomor,8,7)) "
            + " FROM hr.lembur_karyawan a "
            + " WHERE  EXTRACT(year FROM create_date)=#{tahun}")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
}
