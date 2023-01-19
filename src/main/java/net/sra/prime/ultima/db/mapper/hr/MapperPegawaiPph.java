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
import net.sra.prime.ultima.entity.hr.PegawaiPph;
import net.sra.prime.ultima.entity.hr.PegawaiPphDetail;
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
public interface MapperPegawaiPph {

    /**
     * Pegawai pph
     */
    @Select("SELECT a.* FROM hr.pegawai_pph a ORDER BY a.year,a.month")
    public List<PegawaiPph> selectAll() throws RuntimeException;
    
    
    @Select("SELECT a.* FROM hr.pegawai_pph a "
            + " WHERE "
            + " a.year=#{year} AND a.month=#{month}")
    public PegawaiPph selectOne(@Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Insert("INSERT INTO hr.pegawai_pph "
            + "(year,month,create_date,create_by)"
            + " VALUES "
            + "(#{year},#{month},LOACALTIMESTAMP,#{create_by}) ")
    public int insert(PegawaiPph pegawaiPph)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_pph "
            + " WHERE "
            + " a.year=#{year} AND a.month=#{month}")
    public int delete(@Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Update("UPDATE hr.pegawai_pph  SET "
            + " status = #{status}, "
            + " posting_date = LOACALTIMESTAMP, "
            + " posting_by = #{posting_by}"
            + " WHERE year = #{year} AND month=#{month} ")
    public int update(PegawaiPph pegawaiPph)  throws RuntimeException;
    
    
    /**
     * Pegawai pph detail
     */
    
    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen,"  
            + " g.nama as nama_atasan,i.ptkp,e.pph "
            + " FROM pegawai a "
            + " LEFT JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON a.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON a.id_departemen_new=d.id_departemen "
            + " LEFT JOIN pegawai g ON a.atasan_langsung=g.id_pegawai "
            + " LEFT JOIN hr.pegawai_ptkp h ON a.id_pegawai=h.id_pegawai AND h.years=TO_CHAR(CURRENT_DATE, 'YYYY') "
            + " LEFT JOIN hr.ptkp i ON h.id_ptkp=i.id_ptkp "
            + " WHERE a.status=true"
            + " ORDER BY a.nip")
    public List<PegawaiPphDetail> selectAllFromPegawai() throws RuntimeException;
    
    
    @Select("SELECT a.*,b.nama FROM hr.pegawai_pph_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE "
            + " a.year=#{year} AND a.month=#{month}"
            + " ORDER BY a.id_pegawai")
    public List<PegawaiPphDetail> selectAllDetail(@Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama FROM hr.pegawai_pph_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE "
            + " a.year=#{year} AND a.month=#{month} AND a.id_pegawai=#{id_pegawai} "
            )
    public PegawaiPphDetail selectOneDetail(@Param("year") String year,
            @Param("month") Integer month,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Insert("INSERT INTO hr.pegawai_pph_detail "
            + "(year,month,id_pegawai,value)"
            + " VALUES "
            + "(#{year},#{month},#{id_pegawai},#{value}) ")
    public int insertDetail(PegawaiPphDetail pegawaiPphDetail)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_pph_detail "
            + " WHERE "
            + " year=#{year} AND month=#{month} ")
    public int deleteDetail(@Param("year") String year,
            @Param("month") Integer month
            ) throws RuntimeException;
    
    @Delete("DELETE FROM hr.pegawai_pph_detail "
            + " WHERE "
            + " year=#{year} AND month=#{month} AND id_pegawai=#{id_pegawai} ")
    public int deleteDetailByPegawai(@Param("year") String year,
            @Param("month") Integer month,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    @Update("UPDATE hr.pegawai_pph_detail  SET "
            + " id_pegawai = #{id_pegawai}, "
            + " value = #{value} "
            + " WHERE year = #{year} AND month=#{month} ")
    public int updateDetail(PegawaiPphDetail pegawaiPphDetail)  throws RuntimeException;
    
    
}
