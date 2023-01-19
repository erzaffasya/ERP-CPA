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
import net.sra.prime.ultima.db.provider.hr.ProviderPerjalananDinas;
import net.sra.prime.ultima.entity.hr.PerjalananDinas;
import net.sra.prime.ultima.entity.hr.PerjalananDinasPersetujuan;
import net.sra.prime.ultima.entity.hr.SettingApproval;
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
public interface MapperPerjalananDinas {

    
    @Select("SELECT a.*,e.nama as nama_pegawai,f.nama as create_by_name "
            + " FROM hr.perjalanan_dinas a "
            + " INNER JOIN hr.perjalanandinas_persetujuan d ON a.id=d.id_perjalanan_dinas "
            + " INNER JOIN pegawai e ON a.id_pegawai=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " WHERE "
            + " d.id_pegawai=#{id_pegawai} "
            + " AND d.status is null "
            + " AND ((a.status='D' AND d.urut=1) OR (a.status='W' AND d.urut=2))"
            )
    public List<PerjalananDinas> selectNotifikasiPerjalananDinas(
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @SelectProvider(type = ProviderPerjalananDinas.class, method = "selectPerjalananDinas")
    public List<PerjalananDinas> selectAllPerjalananDinas(
            @Param("id_departemen") Integer id_departemen,
            @Param("id_jabatan") Integer id_jabatan,
            @Param("id_perjalanan_dinas") Integer id_perjalanan_dinas,
            @Param("status") Character status,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir
    ) throws RuntimeException;

    @Select("SELECT a.*, c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.perjalanan_dinas a "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal_awal DESC"
            )
    public List<PerjalananDinas> selectMyPerjalananDinas(
            @Param("id_pegawai") String id_pegawai,
            @Param("tahun") String tahun) throws RuntimeException;
    
    
    @Select("SELECT a.*, c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.perjalanan_dinas a "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN hr.perjalanandinas_persetujuan d ON a.id=d.id_perjalanan_dinas AND d.id_pegawai=#{id_pegawai} AND d.status is not null "
            + " WHERE a.id_pegawai != #{id_pegawai} "
            + " AND EXTRACT(year FROM a.tanggal_awal)::varchar=#{tahun} "
            + " ORDER BY a.tanggal_awal DESC"
            )
    public List<PerjalananDinas> selectOtherPerjalananDinas(
            @Param("id_pegawai") String id_pegawai,
            @Param("tahun") String tahun) throws RuntimeException;
    
    @Insert("INSERT INTO hr.perjalanan_dinas "
            + "(waktu,tanggal_awal,tanggal_akhir,status,create_by,create_date,id_pegawai,keterangan_status,keterangan,tujuan) "
            + "VALUES "
            + "(#{waktu},#{tanggal_awal},#{tanggal_akhir},#{status},#{create_by},LOCALTIMESTAMP,#{id_pegawai},#{keterangan_status},#{keterangan},#{tujuan}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertPerjalananDinas(PerjalananDinas perjalananDinas) throws RuntimeException;

    @Delete("DELETE FROM hr.perjalanan_dinas WHERE id = #{id}")
    public int deletePerjalananDinas(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*, c.nama as nama_pegawai,f.jabatan, g.nama as create_by_name  "
            + " FROM hr.perjalanan_dinas a"
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN master_jabatan f ON c.id_jabatan_new = f.id_jabatan "
            + " INNER JOIN pegawai g ON a.create_by=g.id_pegawai "
            + " WHERE a.id=#{id}")
    public PerjalananDinas selectOnePerjalananDinas(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE hr.perjalanan_dinas  SET "
            + " id_pegawai = #{id_pegawai}, "
            + " waktu = #{waktu}, "
            + " tanggal_awal = #{tanggal_awal}, "
            + " tanggal_akhir = #{tanggal_akhir}, "
            + " tujuan = #{tujuan}, "
            + " keterangan = #{keterangan}, "
            + " keterangan_status = #{keterangan_status}, "
            + " status = #{status}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE id = #{id} ")
    public int updatePerjalananDinas(PerjalananDinas perjalananDinas) throws RuntimeException;

    
    ///////////// Cuti Persetujuan /////////////
    @Insert("INSERT INTO hr.perjalanandinas_persetujuan "
            + "(id_perjalanan_dinas,id_pegawai,urut) "
            + "VALUES "
            + "(#{id_perjalanan_dinas},#{id_pegawai},#{urut}) ")
    public int insertPerjalananDinasPersetujuan(PerjalananDinasPersetujuan perjalananDinasPersetujuan) throws RuntimeException;
    
    @Update("UPDATE hr.perjalanandinas_persetujuan  SET "
            + " status = #{status}, "
            + " tanggal_persetujuan = LOCALTIMESTAMP "
            + " WHERE id_perjalanan_dinas = #{id_perjalanan_dinas} AND id_pegawai=#{id_pegawai}")
    public int updatePersetujuaan(@Param("status") Boolean status,
            @Param("id_perjalanan_dinas") Integer id_perjalanan_dinas,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Update("UPDATE hr.perjalanan_dinas SET "
            + " status=#{status}, "
            + " keterangan_status=#{keterangan_status} "
            + " WHERE "
            + " id=#{id}")
    public int updateStatusPerjalananDinas(@Param("status") Character status,
            @Param("id") Integer id,
            @Param("keterangan_status") String keterangan_status) throws RuntimeException;
    
    @Select("SELECT count(*) FROM hr.perjalanandinas_persetujuan WHERE id_perjalanan_dinas=#{id_perjalanan_dinas}")
    public int countPersetujuan(@Param("id_perjalanan_dinas") Integer id_perjalanan_dinas) throws RuntimeException;
    
    @Select("SELECT a.*,c.nama as nama_pegawai"
            + " FROM hr.perjalanan_dinas a"
            + " INNER JOIN hr.perjalanandinas_persetujuan b ON a.id=b.id_perjalanan_dinas "
            + " INNER JOIN pegawai c ON b.id_pegawai=c.id_pegawai "
            + " WHERE a.id=#{id} AND b.urut=#{urut}")
    public PerjalananDinas selectPersetujuankedua(@Param("id") Integer id,
            @Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT * FROM hr.setting_approval WHERE id=1")
    public SettingApproval settingan() throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.perjalanandinas_persetujuan "
            + " WHERE "
            + " id_perjalanan_dinas=#{id_perjalanan_dinas} AND id_pegawai=#{id_pegawai} AND urut=#{urut} AND status is null")
    public Integer searchPersetujuan (@Param("id_perjalanan_dinas") Integer id_perjalanan_dinas,
            @Param("id_pegawai") String id_pegawai, 
            @Param("urut") Integer urut) throws RuntimeException;
    
    @Select("SELECT id_jadwal_kerja FROM hr.jadwal_kerja_karyawan WHERE id_pegawai=#{id_pegawai}")
    public Integer selectIdJadwalKerja(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as nama_pegawai,c.jabatan  "
            + " FROM hr.perjalanandinas_persetujuan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
            + " WHERE a.id_perjalanan_dinas=#{id_perjalanan_dinas} "
            + " ORDER BY a.urut")
    public List<PerjalananDinasPersetujuan> selectAllPerjalananDinasPersetujuan(@Param("id_perjalanan_dinas") Integer id_perjalanan) throws RuntimeException;
    
}
