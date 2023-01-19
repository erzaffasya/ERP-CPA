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
import net.sra.prime.ultima.entity.hr.AbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenLemburHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAbsenLembur {
    /**
     * Absen lembur Head
     */
    
    

    @Select("SELECT a.jam_masuk,a.jam_keluar,a.keterangan,a.tipe_absen,a.id_jadwal_kerja,a.id_absen,a.urut,i::date as tanggal, "
            + " b.nama_kebijakan as jadwal_kerja,overtimebefore,overtimeafter,a.id_pegawai,a.id_status_absen,sb.status_absen " 
            + " FROM " 
            + " generate_series(#{dateStart},#{dateEnd},'1 day'::interval) i " 
            + " LEFT JOIN (SELECT a.*,b.id_pegawai FROM hr.absen a INNER JOIN pegawai b ON a.id_absen=b.absen AND b.id_pegawai=#{id_pegawai}) a ON i.date = a.tanggal " 
            + " LEFT JOIN hr.jadwal_kerja b ON a.id_jadwal_kerja=b.id  "
            + " LEFT JOIN hr.status_absen sb ON a.id_status_absen=sb.id_status_absen " 
            + " ORDER BY i.date ")
    public List<AbsenLembur> selectAll(@Param("id_pegawai") String id_pegawai,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd)  throws RuntimeException;
    
    @Select("SELECT a.*,b.id_pegawai,c.status_absen,d.hour_before,d.hour_after,d,break_time,d.keterangan as keterangan_lembur, d.id as id_lembur_karyawan, "
            + " CASE "
            + "    WHEN schedule='p' THEN concat(TO_CHAR(time_in,'HH24:MI'),' - ',TO_CHAR(time_out,'HH24:MI')) " 
            + "    WHEN schedule='o' THEN "
            + "    ("
            + "        SELECT CASE WHEN EXISTS(select * FROM hr.hari_libur WHERE tanggal=a.tanggal) = true "
            + "        THEN (SELECT nama FROM hr.hari_libur where tanggal=a.tanggal) "
            + "        ELSE 'libur' END "
            + "    )" 
            + "  END  schedule_des , "
            + " CASE "
            + "    WHEN a.jam_masuk is not null AND jam_keluar is not null THEN concat(TO_CHAR(jam_masuk,'HH24:MI'),' - ',TO_CHAR(jam_keluar,'HH24:MI')) " 
            + "  END  absen_desc "
            + " FROM hr.absen a "
            + " INNER JOIN pegawai b ON a.id_absen=b.absen "
            + " LEFT JOIN hr.status_absen c ON a.id_status_absen=c.id_status_absen "
            + " INNER JOIN hr.lembur_karyawan d ON a.tanggal=d.tanggal AND b.id_pegawai=d.id_pegawai AND d.status='A'"
            + " WHERE "
            + " a.tanggal >= #{dateStart} AND a.tanggal <= #{dateEnd}  AND b.id_pegawai=#{id_pegawai} "
            + " ORDER by a.tanggal")
    public List<AbsenLembur> selectAllHrAbsen(@Param("id_pegawai") String id_pegawai,
            @Param("dateStart") Date dateStart,
            @Param("dateEnd") Date dateEnd)  throws RuntimeException;
    
    

    @Select("SELECT * FROM hr.absen_lembur_head a "
            + " WHERE a.month=#{month} AND a.year=#{year} AND a.id_pegawai=#{id_pegawai}")
    public AbsenLemburHead selectOneByPeriode(@Param("month") Integer month,
                    @Param("year") String year,
                    @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
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
    
    
}
