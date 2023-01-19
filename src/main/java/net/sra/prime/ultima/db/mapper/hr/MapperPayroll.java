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
import net.sra.prime.ultima.entity.hr.Payroll;
import net.sra.prime.ultima.entity.hr.PayrollDetail;
import net.sra.prime.ultima.entity.hr.PayrollOvertime;
import net.sra.prime.ultima.entity.hr.Thr;
import net.sra.prime.ultima.entity.hr.ThrDetail;
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
public interface MapperPayroll {

    @Select("SELECT a.*,b.nama as nama_pegawai,b.nip "
            + " FROM hr.payroll_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.payroll c ON a.id=c.id "
            + " WHERE c.year=#{year} AND c.month=#{month}"
            + " ORDER BY b.id_pegawai ")
    public List<PayrollDetail> selectAll(@Param("year") String year,
            @Param("month") Integer month)  throws RuntimeException;

    @Insert("INSERT INTO hr.payroll "
            + "(year,month,create_by,create_date,status)"
            + " VALUES "
            + "(#{year},#{month},#{create_by},LOCALTIMESTAMP,#{status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Payroll payroll)  throws RuntimeException;
    
    @Insert("INSERT INTO hr.payroll_detail "
            + "(id,id_pegawai,gaji_pokok,tunjangan_jabatan,overtime,insentif_penjualan,insentif_kehadiran, "
            + " insentif_produktifitas,umt,tunjangan_komunikasi,thr,komisi_penjualan,tunjangan_bbm, "
            + " asuransi_komersil,prudential,alianz,pph21_upah,pph21_komisi,pph21_thr, "
            + " potongan_absensi,hutang_kta,hutang_dll, "
            + " bpjs_kes_perusahaan,bpjs_kes_pekerja,jht_perusahaan,jht_pekerja,jp_perusahaan,jp_pekerja,jkm,jkk,allianz_perusahaan,prudential_perusahaan,tunjangan_kesehatan) "
            + " VALUES "
            + "(#{id},#{id_pegawai},#{gaji_pokok},#{tunjangan_jabatan},#{overtime},#{insentif_penjualan},#{insentif_kehadiran}, "
            + " #{insentif_produktifitas},#{umt},#{tunjangan_komunikasi},#{thr},#{komisi_penjualan},#{tunjangan_bbm}, "
            + " #{asuransi_komersil},#{prudential},#{alianz},#{pph21_upah},#{pph21_komisi},#{pph21_thr}, "
            + " #{potongan_absensi},#{hutang_kta},#{hutang_dll}, "
            + " #{bpjs_kes_perusahaan},#{bpjs_kes_pekerja},#{jht_perusahaan},#{jht_pekerja},#{jp_perusahaan},#{jp_pekerja},#{jkm},#{jkk},#{allianz_perusahaan},#{prudential_perusahaan},#{tunjangan_kesehatan}) ")
    public int insertDetail(PayrollDetail payrollDetail)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.payroll_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.payroll WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    @Select("SELECT * FROM hr.payroll WHERE year=#{year} AND month=#{month}")
    public Payroll selectOne(@Param("year") String year, 
            @Param("month") Integer month)throws RuntimeException;
    
    @Select("SELECT * FROM hr.payroll WHERE year=#{year} AND month=#{month} AND status='P'")
    public Payroll selectOneMySalary(@Param("year") String year, 
            @Param("month") Integer month)throws RuntimeException;
    
    @Select("SELECT * FROM hr.payroll WHERE id=#{id}")
    public Payroll selectOneById(@Param("id") Integer id)throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as nama_pegawai FROM hr.payroll_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE "
            + " a.id=#{id} AND a.id_pegawai=#{id_pegawai} ")
    public PayrollDetail selectOneDetail(@Param("id") Integer id,
            @Param("id_pegawai") String id_pegawai)throws RuntimeException;
    
    @Update("UPDATE hr.payroll  SET "
            + " year = #{year}, "
            + " month = #{month}, "
            + " status = #{status}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE id = #{id} ")
    public int update(Payroll payroll)  throws RuntimeException;
    
    @Update("UPDATE hr.payroll_detail  SET "
            + " gaji_pokok = #{gaji_pokok}, "
            + " tunjangan_jabatan = #{tunjangan_jabatan}, "
            + " overtime = #{overtime}, "
            + " insentif_penjualan = #{insentif_penjualan}, "
            + " insentif_kehadiran=#{insentif_kehadiran},"
            + " insentif_produktifitas=#{insentif_produktifitas},"
            + " umt=#{umt},"
            + " tunjangan_komunikasi=#{tunjangan_komunikasi},"
            + " tunjangan_bbm=#{tunjangan_bbm},"
            + " thr=#{thr},"
            + " komisi_penjualan=#{komisi_penjualan},"
            + " asuransi_komersil=#{asuransi_komersil},"
            + " prudential=#{prudential},"
            + " alianz=#{alianz},"
            + " pph21_upah=#{pph21_upah},"
            + " pph21_komisi=#{pph21_komisi},"
            + " pph21_thr=#{pph21_thr},"
            + " potongan_absensi=#{potongan_absensi},"
            + " hutang_kta=#{hutang_kta},"
            + " hutang_dll=#{hutang_dll}, "
            + " bpjs_kes_perusahaan=#{bpjs_kes_perusahaan}, "
            + " bpjs_kes_pekerja=#{bpjs_kes_pekerja}, "
            + " jht_perusahaan=#{jht_perusahaan}, "
            + " jht_pekerja=#{jht_pekerja}, "
            + " jp_perusahaan=#{jp_perusahaan}, "
            + " jp_pekerja=#{jp_pekerja}, "
            + " jkk=#{jkk}, "
            + " jkm=#{jkm}, "
            + " tunjangan_kesehatan=#{tunjangan_kesehatan}"
            + " WHERE id = #{id} AND id_pegawai=#{id_pegawai}")
    public int updateDetail(PayrollDetail payrollDetail)  throws RuntimeException;

    @Select("SELECT COUNT(*) FROM pegawai a "
            + " LEFT JOIN hr.absen_pegawai_status b ON a.id_pegawai=b.id_pegawai AND month=#{month} AND year=#{year} "
            + " WHERE (b.month is  null or b.status='D')  AND a.status=true AND a.mulai_bekerja <= #{periode}")
    public Integer countAbsenNotPosting(@Param("month") Integer month,
            @Param("year") String year,
            @Param("periode") Date periode) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as nama_pegawai,b.nip,b.nomorrekening,b.pemilik_rekening,  "
            + " a.gaji_pokok + a.tunjangan_jabatan + a.overtime + a.insentif_penjualan + a.insentif_kehadiran + a.insentif_produktifitas + a.umt + a.tunjangan_komunikasi + a.thr + a.tunjangan_bbm + a.komisi_penjualan  "
            + " - a.jht_pekerja - a.jp_pekerja - a.bpjs_kes_pekerja - a.asuransi_komersil - a.prudential - a.alianz - a.pph21_upah - a.pph21_komisi - a.pph21_thr - a.hutang_kta - a.hutang_dll - a.potongan_absensi as thp, "
            + " a.gaji_pokok + a.tunjangan_jabatan + a.overtime + a.insentif_penjualan + a.insentif_kehadiran + a.insentif_produktifitas + a.umt + a.tunjangan_komunikasi + a.thr + a.tunjangan_bbm + a.komisi_penjualan  as total_gaji, "
            + " a.jht_pekerja + a.jp_pekerja + a.bpjs_kes_pekerja + a.asuransi_komersil + a.prudential + a.alianz + a.pph21_upah + a.pph21_komisi + a.pph21_thr + a.hutang_kta + a.hutang_dll + a.potongan_absensi as total_potongan "
            + " FROM hr.payroll_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN hr.payroll c ON a.id=c.id "
            + " WHERE c.month=#{month} AND c.year=#{year} "
            + " ORDER BY b.id_pegawai")        
    public List<PayrollDetail> onloadListDaftarGaji(@Param("month") Integer month, 
             @Param("year") String year) throws RuntimeException;
    
    @Select("SELECT a.amount FROM hr.payroll_overtime_detail a"
            + " INNER JOIN hr.payroll_overtime b ON a.year=b.year AND a.month=b.month AND a.jenis=b.jenis "
            + " WHERE "
            + " a.id_pegawai=#{id_pegawai} AND a.year=#{year} AND a.month=#{month} AND a.jenis=#{jenis} AND b.status='A'")
    public Double getInsentif(@Param("id_pegawai") String id_pegawai,
            @Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis) throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM hr.thr "
            + " WHERE  EXTRACT(month FROM payment_date)=#{bulan}  AND EXTRACT(year FROM payment_date)=#{tahun} AND status='A' ")
    public Thr checkThr(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Select("SELECT * FROM hr.payroll_overtime   "
            + " WHERE "
            + " year=#{year} AND month=#{month} AND jenis=#{jenis} AND status='A'")
    public PayrollOvertime checkTunjangan(@Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis) throws RuntimeException;
    
    
    @Select("SELECT * FROM hr.thr_detail WHERE id_thr=#{id_thr} AND id_pegawai=#{id_pegawai}")
    public ThrDetail selectOneThrDetail(@Param("id_thr") Integer id_thr,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Select("SELECT SUM(a.gaji_pokok + a.tunjangan_jabatan + a.overtime + a.insentif_penjualan + a.insentif_kehadiran + a.insentif_produktifitas + a.umt + a.tunjangan_komunikasi + a.thr + a.tunjangan_bbm + a.komisi_penjualan) as total_gaji, "
            + " SUM(a.hutang_kta + a.hutang_dll) as hutang_kta, "
            + " SUM(a.jht_pekerja + a.jp_pekerja + a.bpjs_kes_pekerja) as jht_pekerja, "
            + " SUM(a.prudential + a.alianz) as asuransi_komersil, "
            + " SUM(a.potongan_absensi) as potongan_absensi, "
            + " SUM(a.pph21_upah + a.pph21_komisi + a.pph21_thr) as pph21_upah, "
            + " SUM(a.gaji_pokok + a.tunjangan_jabatan + a.overtime + a.insentif_penjualan + a.insentif_kehadiran + a.insentif_produktifitas + a.umt + a.tunjangan_komunikasi + a.thr + a.tunjangan_bbm + a.komisi_penjualan "
            + " - a.jht_pekerja - a.jp_pekerja - a.bpjs_kes_pekerja - a.asuransi_komersil - a.prudential - a.alianz - a.pph21_upah - a.pph21_komisi - a.pph21_thr - a.hutang_kta - a.hutang_dll - a.potongan_absensi) as thp" 
            + " FROM hr.payroll_detail a " 
            + " INNER JOIN hr.payroll b ON a.id=b.id " 
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai " 
            + " WHERE "
            + " b.month=#{month} "
            + " AND b.year=#{year}"
            + " AND c.id_kantor_new=#{id_kantor}")
    public PayrollDetail rekapGaji(@Param("year") String year,
            @Param("month") Integer month,
            @Param("id_kantor") String id_kantor) throws RuntimeException;
}
