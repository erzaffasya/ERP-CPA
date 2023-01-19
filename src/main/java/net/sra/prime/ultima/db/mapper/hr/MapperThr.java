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
import net.sra.prime.ultima.entity.hr.PayrollDetail;
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
public interface MapperThr {

    @Select("SELECT a.*,b.nama as create_by_name ,b.nip "
            + " FROM hr.thr a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " ORDER BY a.thr_date DESC")
    public List<Thr> selectAll()  throws RuntimeException;
    
    
    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM hr.thr a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE id_thr=#{id_thr}")
    public Thr selectOne(@Param("id_thr") Integer id_thr)  throws RuntimeException;

    @Insert("INSERT INTO hr.thr "
            + "(payment_date,thr_date,include_thp,tax,status,create_by,create_date,description) "
            + " VALUES "
            + "(#{payment_date},#{thr_date},#{include_thp},#{tax},#{status},#{create_by},LOCALTIMESTAMP,#{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id_thr", keyColumn = "id_thr")
    public int insert(Thr thr)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.thr WHERE id_thr=#{id_thr}")
    public int delete(@Param("id_thr") Integer id_thr)  throws RuntimeException;
    
    @Update("UPDATE hr.thr  SET "
            + " payment_date = #{payment_date}, "
            + " thr_date = #{thr_date}, "
            + " include_thp = #{include_thp}, "
            + " tax = #{tax}, "
            + " description = #{description}, "
            + " status = #{status}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE id_thr=#{id_thr}")
    public int update(Thr thr)  throws RuntimeException;
    /**
     * Detail
     * @param thrDetail
     * @return
     * @throws RuntimeException 
     */
    @Insert("INSERT INTO hr.thr_detail "
            + "(id_thr,id_pegawai,year,month,day,value,gaji_pokok,tunjangan_jabatan) "
            + " VALUES "
            + "(#{id_thr},#{id_pegawai},#{year},#{month},#{day},#{value},#{gaji_pokok},#{tunjangan_jabatan}) "
    )
    
    public int insertDetail(ThrDetail thrDetail)  throws RuntimeException;

    
    @Delete("DELETE FROM hr.thr_detail WHERE id_thr=#{id_thr}")
    public int deleteDetail(@Param("id_thr") Integer id_thr)  throws RuntimeException;

    @Select("SELECT a.*, b.nama,b.nip"
            + " FROM hr.thr_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE id_thr=#{id_detail}")
    public List<ThrDetail> selectAllDetail(@Param("id_thr") Integer id_thr)  throws RuntimeException;
    
    @Select("SELECT a.*,b.nama,b.nip FROM hr.thr_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " WHERE "
            + " id_thr=#{id_thr} AND id_pegawai=#{id_pegawai}")
    public ThrDetail selectOneDetail(@Param("id_thr") Integer id_thr,
            @Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen,e.agama "
            + " FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " INNER JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " INNER JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN agama e ON a.id_agama=e.id "
            + " WHERE a.status=true"
            + " ORDER BY a.nip")
    public List<ThrDetail> selectAllPegawai() throws RuntimeException;

    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen,f.agama, CONCAT(e.year,' tahun ',e.month,' bulan ',e.day, ' hari') as date_desc,e.gaji_pokok,e.tunjangan_jabatan,e.value"
            + " FROM pegawai a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " INNER JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " INNER JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " INNER JOIN hr.thr_detail e ON a.id_pegawai=e.id_pegawai "
            + " LEFT JOIN agama f ON a.id_agama=f.id "
            + " WHERE e.id_thr=#{id_thr}"
            + " ORDER BY a.nip")
    public List<ThrDetail> selectAllPegawaiSelected(@Param("id_thr") Integer id_thr) throws RuntimeException;
    
    @Select("SELECT SUM(a.value) as value "
            + " FROM hr.thr_detail a " 
            + " INNER JOIN hr.thr b ON a.id_thr=b.id_thr " 
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai " 
            + " WHERE "
            + " b.id_thr=#{id} "
            + " AND c.id_kantor_new=#{id_kantor}")
    public ThrDetail rekapThr(
            @Param("id") Integer id,
            @Param("id_kantor") String id_kantor) throws RuntimeException;
            
}
