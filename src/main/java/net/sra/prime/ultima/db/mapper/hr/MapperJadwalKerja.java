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

import java.util.List;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import net.sra.prime.ultima.entity.hr.JadwalKerjaKaryawan;
import net.sra.prime.ultima.entity.hr.JadwalKerjaWaktu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperJadwalKerja {

    @Select("SELECT a.* "
            + " FROM hr.jadwal_kerja a "
            + " ORDER BY a.id ")
    public List<JadwalKerja> selectAll()  throws RuntimeException;

    
    @Insert("INSERT INTO hr.jadwal_kerja "
            + "(nama_kebijakan,batas_terlambat,batas_pulang,cycle,jenis,start_date,jmlkerja) "
            + "VALUES "
            + "(#{nama_kebijakan},#{batas_terlambat},#{batas_pulang},#{cycle},#{jenis},#{start_date},#{jmlkerja}) "
            )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(JadwalKerja jadwalKerja)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.jadwal_kerja WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM hr.jadwal_kerja WHERE id=#{id}")
    public JadwalKerja selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.Jadwal_kerja  SET "
            + " nama_kebijakan = #{nama_kebijakan}, "
            + " batas_terlambat = #{batas_terlambat}, "
            + " batas_pulang = #{batas_pulang}, "
            + " start_date = #{start_date}, "
            + " cycle = #{cycle}, "
            + " jenis = #{jenis}, "
            + " jmlkerja = #{jmlkerja} "
            + " WHERE id = #{id} ")
    public int update(JadwalKerja jadwalKerja)  throws RuntimeException;

    @Select("SELECT a.*,b.nama "
            + " FROM hr.jadwal_kerja_karyawan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.jadwal_kerja c ON a.id_jadwal_kerja=c.id "
            + " WHERE a.id_jadwal_kerja=#{id_jadwal_kerja}"
            + " ORDER BY a.id ")
    public List<JadwalKerjaKaryawan> selectAllJadwalKerjaKaryawan(@Param("id_jadwal_kerja") Integer id_jadwal_kerja) throws RuntimeException;
    
    @Select("SELECT c.* "
            + " FROM hr.jadwal_kerja_karyawan a "
            + " INNER JOIN hr.jadwal_kerja c ON a.id_jadwal_kerja=c.id "
            + " WHERE a.id_pegawai=#{id_pegawai}"
            )
    public JadwalKerja selectOneJadwalKerjaFromKaryawan(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Insert("INSERT INTO hr.jadwal_kerja_karyawan "
            + "(id_jadwal_kerja,id_pegawai) "
            + "VALUES "
            + "(#{id_jadwal_kerja}, #{id_pegawai}) ")
    public int insertJadwalKerjaKaryawan(JadwalKerjaKaryawan jadwalKerjaKaryawan)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.jadwal_kerja_karyawan WHERE id_jadwal_kerja = #{id_jadwal_kerja}")
    public int deleteJadwalKerjaKaryawan(@Param("id_jadwal_kerja") Integer id_jadwal_kerja)  throws RuntimeException;

    @Insert("INSERT INTO hr.jadwal_kerja_waktu "
            + "(id_jadwal_kerja,day,time_in,time_out,time_break,selected,nama_waktu) "
            + "VALUES "
            + "(#{id_jadwal_kerja},#{day}, #{time_in}, #{time_out},#{time_break},#{selected},#{nama_waktu}) ")
    public int insertJadwalKerjaWaktu(JadwalKerjaWaktu jadwalKerjaWaktu)  throws RuntimeException;
    
    @Select("SELECT b.* "
            + " FROM hr.jadwal_kerja_karyawan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai"
            + " WHERE id_jadwal_kerja=#{id_jadwal_kerja} "
            + " ORDER BY a.id_pegawai ")
    public List<Pegawai> selectAllKaryawan(@Param("id_jadwal_kerja") Integer id_jadwal_kerja)  throws RuntimeException;
    
    @Select("SELECT a.* FROM pegawai a WHERE id_pegawai NOT IN (SELECT id_pegawai FROM  hr.jadwal_kerja_karyawan)")
    public List<Pegawai> selectAllPegawai()  throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.jadwal_kerja_waktu a "
            + " WHERE a.id_jadwal_kerja=#{id_jadwal_kerja} "
            + " ORDER BY a.day ")
    public List<JadwalKerjaWaktu> selectAllJadwalKerjaWaktu(@Param("id_jadwal_kerja") Integer id_jadwal_kerja)  throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.jadwal_kerja_waktu a "
            + " WHERE a.id_jadwal_kerja=#{id_jadwal_kerja} "
            + " AND a.day=#{day}"
            )
    public JadwalKerjaWaktu selectOnelJadwalKerjaWaktu(@Param("id_jadwal_kerja") Integer id_jadwal_kerja,
            @Param("day") Integer day)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.jadwal_kerja_waktu WHERE id_jadwal_kerja = #{id_jadwal_kerja}")
    public int deleteJadwalKerjaWaktu(@Param("id_jadwal_kerja") Integer id_jadwal_kerja)  throws RuntimeException;
    
    @Select("SELECT b.* FROM hr.jadwal_kerja_karyawan a "
            + " INNER JOIN hr.jadwal_kerja_waktu b ON a.id_jadwal_kerja=b.id_jadwal_kerja "
            + " WHERE a.id_pegawai=#{id_pegawai} AND day=#{hari}")
    public JadwalKerjaWaktu selectOneDay(@Param("id_pegawai") String id_pegawai,
            @Param("hari") Integer hari) throws RuntimeException;
}
