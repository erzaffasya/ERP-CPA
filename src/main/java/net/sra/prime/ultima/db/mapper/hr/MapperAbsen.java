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
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Absen;
import net.sra.prime.ultima.entity.hr.AbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenPegawaiStatus;
import net.sra.prime.ultima.entity.hr.CutiKaryawan;
import net.sra.prime.ultima.entity.hr.StatusAbsen;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAbsen {

    @Select("SELECT a.jam_masuk,a.jam_keluar,a.keterangan,a.tipe_absen,a.id_jadwal_kerja,a.id_absen,a.urut,i::date as tanggal,a.id_cuti, "
            + " b.nama_kebijakan as jadwal_kerja,overtimebefore,overtimeafter,a.id_pegawai,a.id_status_absen,sb.status_absen,c.break_time,a.jam_break " 
            + " FROM " 
            + " generate_series(#{dateStart},#{dateEnd},'1 day'::interval) i " 
            + " LEFT JOIN (SELECT a.*,b.id_pegawai FROM hr.absen a INNER JOIN pegawai b ON a.id_absen=b.absen AND b.id_pegawai=#{id_pegawai}) a ON i.date = a.tanggal " 
            + " LEFT JOIN hr.jadwal_kerja b ON a.id_jadwal_kerja=b.id  "
            + " LEFT JOIN hr.status_absen sb ON a.id_status_absen=sb.id_status_absen "
            + " LEFT JOIN hr.lembur_karyawan c ON c.id_pegawai = c.id_pegawai AND a.tanggal = c.tanggal AND c.status='A' AND c.break_time > 0" 
            + " ORDER BY i.date ")
    public List<Absen> selectAll(@Param("id_pegawai") String id_pegawai,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd)  throws RuntimeException;
    
    @Select("SELECT a.*,b.id_pegawai,a.overtimebefore + overtimeafter as lemburafter,c.status_absen,d.break_time, "
            + " CASE "
            + "    WHEN schedule='p' THEN concat(TO_CHAR(time_in,'HH24:MI'),' - ',TO_CHAR(time_out,'HH24:MI')) " 
            + "    WHEN schedule='o' THEN "
            + "    ("
            + "        SELECT CASE WHEN EXISTS(select * FROM hr.hari_libur WHERE tanggal=a.tanggal) = true "
            + "        THEN (SELECT nama FROM hr.hari_libur where tanggal=a.tanggal) "
            + "        ELSE 'libur' END "
            + "    )" 
            + "  END  schedule_des"  
            + " FROM hr.absen a "
            + " INNER JOIN pegawai b ON a.id_absen=b.absen "
            + " LEFT JOIN hr.status_absen c ON a.id_status_absen=c.id_status_absen "
            + " LEFT JOIN hr.lembur_karyawan d ON b.id_pegawai = d.id_pegawai AND a.tanggal = d.tanggal AND d.status='A' AND d.break_time > 0" 
            + " WHERE "
            + " a.tanggal >= #{dateStart} AND a.tanggal <= #{dateEnd}  AND b.id_pegawai=#{id_pegawai} "
            + " ORDER by a.tanggal")
    public List<Absen> selectAllHrAbsen(@Param("id_pegawai") String id_pegawai,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd)  throws RuntimeException;
    
    

    @Select("SELECT * FROM hr.absen a "
            + " INNER JOIN pegawai b ON a.id_absen=b.absen"
            + " WHERE a.tanggal=#{tanggal} AND b.id_pegawai=#{id_pegawai}")
    public Absen selectOneAbsen(@Param("tanggal") Date tanggal,
                    @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Insert("INSERT INTO hr.absen "
            + "(id_absen,tanggal,jam_masuk,jam_keluar,keterangan,tipe_absen,id_jadwal_kerja,urut,id_status_absen,jam_break) "
            + "VALUES "
            + "(#{id_absen},#{tanggal}, #{jam_masuk},#{jam_keluar},#{keterangan},#{tipe_absen},#{id_jadwal_kerja},#{urut},#{id_status_absen},#{jam_break}) ")
    public int insert(Absen absen)  throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.absen "
            + "(id_absen,tanggal,jam_masuk,jam_keluar,keterangan,tipe_absen,id_jadwal_kerja,urut,id_status_absen,time_in,time_out,schedule) "
            + "VALUES "
            + "(#{id_absen},#{tanggal}, #{jam_masuk},#{jam_keluar},#{keterangan},#{tipe_absen},#{id_jadwal_kerja},#{urut},#{id_status_absen},#{time_in},#{time_out},#{schedule}) ")
    public int insertSubmit(Absen absen)  throws RuntimeException;
    
    @Insert("INSERT INTO hr.absen "
            + "(id_absen,tanggal,overtimebefore,overtimeafter,tipe_absen) "
            + "VALUES "
            + "(#{id_absen},#{tanggal}, #{overtimebefore},#{overtimeafter},#{tipe_absen}) ")
    //@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertFromOvertime(Absen absen)  throws RuntimeException;

    
    @Insert("INSERT INTO hr.absen "
            + "(id_absen,tanggal,id_cuti,tipe_absen,keterangan,id_status_absen) "
            + "VALUES "
            + "(#{id_absen},#{tanggal}, #{id_cuti},#{tipe_absen},#{keterangan},#{id_status_absen}) ")
    //@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertFromCutoff(Absen absen)  throws RuntimeException;
 
    @Delete("DELETE FROM hr.absen WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.absen WHERE id_absen = #{id_absen} AND tanggal >= #{datestart} AND tanggal <= #{dateend}")
    public int deletebyDate(@Param("id_absen") Integer id_absen,
            @Param("datestart") Date datestart,
            @Param("dateend") Date dateend)  throws RuntimeException;

    
    @Delete("DELETE FROM hr.absen WHERE id_absen = #{id_absen} AND tanggal = #{tanggal}")
    public int deleteOneDate(@Param("id_absen") Integer id_absen,
            @Param("tanggal") Date datestart)  throws RuntimeException;

    @Select("SELECT * FROM hr.absen WHERE id_absen=#{id_absen} AND tanggal=#{tanggal}")
    public Absen selectOne(@Param("id_absen") Integer id_absen,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    
    
    @Update("UPDATE hr.absen  SET "
            + " jam_masuk = #{jam_masuk}, "
            + " jam_keluar = #{jam_keluar}, "
            + " jam_break = #{jam_break}, "
            + " keterangan = #{keterangan}, "
            + " tipe_absen = #{tipe_absen}, "
            + " id_status_absen = #{id_status_absen}, "
            + " id_jadwal_kerja = #{id_jadwal_kerja}, "
            + " time_in = #{time_in}, "
            + " time_out = #{time_out},"
            + " time_break = #{time_break}, "
            + " schedule = #{schedule} "
            + " WHERE id_absen = #{id_absen} AND tanggal=#{tanggal}")
    public int update(Absen absen)  throws RuntimeException;
    
    @Update("UPDATE hr.absen  SET "
            + " jam_masuk = #{jam_masuk}, "
            + " jam_keluar = #{jam_keluar} "
            + " WHERE id_absen = #{id_absen} AND tanggal=#{tanggal}")
    public int updateUpload(Absen absen)  throws RuntimeException;
    
    @Update("UPDATE hr.absen  SET "
            + " overtimebefore = #{overtimebefore}, "
            + " overtimeafter = #{overtimeafter} "
            + " WHERE id_absen = #{id_absen} AND tanggal=#{tanggal}")
    public int updateFromOvertime(Absen absen)  throws RuntimeException;
    
    @Update("UPDATE hr.absen  SET "
            + " id_cuti = #{id_cuti}, "
            + " keterangan = #{keterangan}, "
            + " id_status_absen = #{id_status_absen}"
            + " WHERE id_absen = #{id_absen} AND tanggal=#{tanggal}")
    public int updateFromCutoff(Absen absen)  throws RuntimeException;
    
    
    
    @Select("SELECT COALESCE(max(urut),0) + 1 FROM hr.absen")
    public int urutMax () throws RuntimeException;
    
    @Select("SELECT id_absen FROM hr.absen WHERE urut=#{urut} GROUP BY id_absen ORDER BY id_absen")
    public List<Integer> listNomorAbsen(@Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT * FROM hr.absen WHERE urut=#{urut}  ORDER BY id_absen")
    public List<Absen> listAbsenByUrut(@Param("urut") Integer urut) throws RuntimeException;
    
    @Update("UPDATE hr.absen "
            + " SET "
            + " id_jadwal_kerja = (SELECT a.id_jadwal_kerja FROM hr.jadwal_kerja_karyawan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE b.absen=#{id_absen}) "
            + " WHERE id_absen=#{id_absen}")
    public int updateIdJadwalKerja (@Param("id_absen") Integer id_absen) throws RuntimeException;
    
    
    @Select("SELECT EXTRACT(isodow FROM (SELECT start_date from hr.jadwal_kerja))")
    public int startJadwalKerja(@Param("id_jadwal_kerja") Integer id_jadwal_kerja); 
    
    
    @Select("SELECT * FROM hr.status_absen WHERE id_status_absen=#{id_status_absen}")
    public StatusAbsen selectOneStatusAbsen(@Param("id_status_absen") String id_status_absen) throws RuntimeException;
    
    @Insert("INSERT INTO hr.absen_pegawai_status "
            + " (month,year,id_pegawai,create_by,create_date,status,terlambat,pulangcepat,lupaabsen,sakit,ijin,absen,cuti,cutibersama,unpaidleave,off,tobepresent,visitcustomer,libur,tobepresentwithoutumt,visitcustomerharilibur,umt) " 
            + " VALUES "
            + " (#{month},#{year},#{id_pegawai},#{create_by},LOCALTIMESTAMP,#{status},#{terlambat},#{pulangcepat},#{lupaabsen},#{sakit},#{ijin},#{absen},#{cuti},#{cutibersama},#{unpaidleave},#{off},#{tobepresent},#{visitcustomer},#{libur},#{tobepresentwithoutumt},#{visitcustomerharilibur},#{umt})")
    public int insertAbsenPegawaiStatus (AbsenPegawaiStatus absenPegawaiStatus) throws RuntimeException;
    
    @Delete("DELETE FROM hr.absen_pegawai_status WHERE id_pegawai=#{id_pegawai} AND month=#{month} AND year=#{year}")
    public int deletePegawaiStatus(@Param("id_pegawai") String id_pegawai,
            @Param("month") Integer month,
            @Param("year") String year) throws RuntimeException;
    
    @Select("SELECT * FROM hr.absen_pegawai_status WHERE id_pegawai=#{id_pegawai} AND month=#{month} AND year=#{year}")
    public AbsenPegawaiStatus selectOnePegawaiStatus(@Param("id_pegawai") String id_pegawai,
            @Param("month") Integer month,
            @Param("year") String year) throws RuntimeException;
    
    @Select("SELECT a.id_pegawai,a.nama,a.nip,b.jabatan, c.nama as kantor, d.departemen, e.status as statuskaryawan "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN hr.absen_pegawai_status e ON a.id_pegawai=e.id_pegawai AND e.month=#{month} AND e.year=#{year} "
            + " WHERE a.status=true "
            + " ORDER BY a.nip")
    public List<Pegawai> selectAllAbsen(@Param("month") Integer month,
            @Param("year") String year) throws RuntimeException;
    
    
    /**
     * Absen lembur
     */
   
    @Select("SELECT a.*,b.id_pegawai,a.overtimebefore + overtimeafter as lemburafter,c.status_absen, "
            + " CASE "
            + "    WHEN schedule='p' THEN concat(TO_CHAR(time_in,'HH24:MI'),' - ',TO_CHAR(time_out,'HH24:MI')) " 
            + "    WHEN schedule='o' THEN "
            + "    ("
            + "        SELECT CASE WHEN EXISTS(select * FROM hr.hari_libur WHERE tanggal=a.tanggal) = true "
            + "        THEN (SELECT nama FROM hr.hari_libur where tanggal=a.tanggal) "
            + "        ELSE 'libur' END "
            + "    )" 
            + "  END  schedule_des"  
            + " FROM hr.absen a "
            + " INNER JOIN pegawai b ON a.id_absen=b.absen "
            + " LEFT JOIN hr.status_absen c ON a.id_status_absen=c.id_status_absen "
            + " WHERE "
            + " a.tanggal >= #{dateStart} AND a.tanggal <= #{dateEnd}  AND b.id_pegawai=#{id_pegawai} "
            + " ORDER by a.tanggal")
    public List<AbsenLembur> selectAllHrAbsenLembur(@Param("id_pegawai") String id_pegawai,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd)  throws RuntimeException;
    
    // get ijin karyawan
    @Select("SELECT a.* FROM hr.cuti_karyawan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai AND b.absen=#{absen} "
            + " INNER JOIN hr.cuti c ON a.id_cuti=c.id AND c.master=3"
            + " WHERE "
            + " a.id=#{id_cuti} AND a.status='A'")
    public CutiKaryawan selectCutiKaryawan(@Param("absen") Integer absen,
            @Param("id_cuti") Integer id_cuti) throws RuntimeException;
    
}
