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
package net.sra.prime.ultima.db.mapper.kpi;

import java.util.List;
import net.sra.prime.ultima.entity.kpi.KpiPegawai;
import net.sra.prime.ultima.entity.kpi.KpiPegawaiDetail;
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
public interface MapperKpiPegawai {

    @Select("SELECT a.*, b.nama, c.penilai_name, d.create_name "
            + " FROM kpi.kpi_pegawai a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " INNER JOIN pegawai c ON a.penilai = c.id_pegawai "
            + " INNER JOIN pegawai d ON a.create_by ON d.id_pegawai "
            + " ORDER BY a.year DESC ,id_pegawai ")
    public List<KpiPegawai> selectAll()  throws RuntimeException;
    
    
    @Select("SELECT a.id_indicator,a.indicator,b.id_category,b.category,c.evidence,c.bobot,c.target,c.id_pegawai, d.unit, d.id_unit "
            + " FROM kpi.indicator a"
            + " INNER JOIN kpi.category b ON a.id_category = b.id_category "
            + " INNER JOIN kpi.pegawai_kpi c ON a.id_indicator = c.id_indicator "
            + " INNER JOIN kpi.unit d ON a.id_unit = d.id_unit "
            + " WHERE "
            + " c.id_pegawai = #{id_pegawai}"
            + " ORDER BY b.id_category,a.id_indicator ")
    public List<KpiPegawaiDetail> selectAllKpiPegawai(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    
    @Select("SELECT a.id_indicator,a.indicator,b.id_category,b.category,c.evidence,c.bobot,c.target,c.id_pegawai, d.unit, d.id_unit,e.actual,e.persen,e.point "
            + " FROM kpi.indicator a"
            + " INNER JOIN kpi.category b ON a.id_category = b.id_category "
            + " INNER JOIN kpi.pegawai_kpi c ON a.id_indicator = c.id_indicator "
            + " INNER JOIN kpi.unit d ON a.id_unit = d.id_unit "
            + " LEFT JOIN kpi.kpi_pegawai_detail e ON c.id_indicator=e.id_indicator AND e.id_pegawai=c.id_pegawai AND e.year=#{year} AND e.month=#{month}"
            + " WHERE "
            + " c.id_pegawai = #{id_pegawai}"
            + " ORDER BY b.id_category,a.id_indicator ")
    public List<KpiPegawaiDetail> selectAllKpiPegawaiDraft(@Param("id_pegawai") String id_pegawai,
            @Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Select("SELECT a.id_indicator,a.indicator,b.id_category,b.category,c.evidence,c.bobot,c.target,c.id_pegawai, d.unit, d.id_unit,c.actual,c.persen,c.point "
            + " FROM kpi.indicator a"
            + " INNER JOIN kpi.category b ON a.id_category = b.id_category "
            + " INNER JOIN kpi.kpi_pegawai_detail c ON a.id_indicator = c.id_indicator "
            + " INNER JOIN kpi.unit d ON a.id_unit = d.id_unit "
            + " WHERE "
            + " c.id_pegawai = #{id_pegawai} AND c.year=#{year} AND c.month=#{month}"
            + " ORDER BY b.id_category,a.id_indicator ")
    public List<KpiPegawaiDetail> selectAllKpiPegawaiPosting(@Param("id_pegawai") String id_pegawai,
            @Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;

    
    
    @Insert("INSERT INTO kpi.kpi_pegawai "
            + " (id_pegawai,year,month,date,penilai,create_by,create_date,totalpoint) "
            + " VALUES "
            + " (#{id_pegawai},#{year},#{month},#{date},#{penilai},#{create_by},LOCALTIMESTAMP,#{totalpoint}) ")
    public int insert(KpiPegawai kpiPegawai)  throws RuntimeException;

 
    @Delete("DELETE FROM kpi.kpi_pegawai WHERE id_pegawai = #{id_pegawai} AND year = #{year}")
    public int delete(@Param("id_pegawai") String id_pegawai, 
            @Param("year") String year)  throws RuntimeException;

    
    @Select("SELECT a.*, b.nama, c.nama as penilai_name, d.nama as create_name "
            + " FROM kpi.kpi_pegawai a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " LEFT JOIN pegawai c ON a.penilai = c.id_pegawai "
            + " LEFT JOIN pegawai d ON a.create_by = d.id_pegawai "
            + " WHERE "
            + " a.id_pegawai = #{id_pegawai} AND a.year = #{year} AND a.month=#{month}")
    public KpiPegawai selectOne(@Param("id_pegawai") String id_pegawai, 
            @Param("year") String year, 
            @Param("month") Integer month)throws RuntimeException;
    
    @Update("UPDATE kpi.kpi_pegawai  SET "
            + " date = #{date}, "
            + " penilai = #{penilai}, "
            + " totalpoint = #{totalpoint}, "
            + " status = #{status} "
            + " WHERE "
            + " id_pegawai = #{id_pegawai}  AND year = #{year} AND month = #{month}")
    public int update(KpiPegawai kpiPegawai)  throws RuntimeException;
    
    
    /**
     * table kpi_pegawai_detail
     */

    @Select("SELECT a.*, b.nama, d.indicator, e.unit "
            + " FROM kpi.kpi_pegawai_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " INNER JOIN kpi.kpi_pegawai c ON a.id_pegawai = c.id_pegawai AND a.year = # c.year "
            + " INNER JOIN kpi.indicator d ON a.id_indicatoe ON d.id_indicator "
            + " INNER JOIN kpi.unit e ON a.id_unit = e.id_unit "
            + " ORDER BY a.year DESC ,id_pegawai ")
    public List<KpiPegawaiDetail> selectAllDetail()  throws RuntimeException;
    
    
    @Insert("INSERT INTO kpi.kpi_pegawai_detail "
            + " (id_pegawai,year,month,id_indicator,evidence,id_unit,bobot,target,point,persen,actual) "
            + " VALUES "
            + " (#{id_pegawai},#{year},#{month},#{id_indicator},#{evidence},#{id_unit},#{bobot},#{target},#{point},#{persen},#{actual})")
    public int insertDetail(KpiPegawaiDetail kpiPegawaiDetail)  throws RuntimeException;

 
    @Delete("DELETE FROM kpi.kpi_pegawai_detail WHERE id_pegawai = #{id_pegawai} AND year = #{year} AND month = #{month}")
    public int deleteDetail(@Param("id_pegawai") String id_pegawai, 
            @Param("year") String year, 
            @Param("month") Integer month)  throws RuntimeException;

    
    @Select("SELECT a.*, b.nama, d.indicator, e.unit "
            + " FROM kpi.kpi_pegawai_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " INNER JOIN kpi.kpi_pegawai c ON a.id_pegawai = c.id_pegawai AND a.year = # c.year "
            + " INNER JOIN kpi.indicator d ON a.id_indicatoe ON d.id_indicator "
            + " INNER JOIN kpi.unit e ON a.id_unit = e.id_unit "
            + " id_pegawai = #{id_pegawai} AND year = #{year} AND id_indicator = #{id_indicator}")
    public KpiPegawaiDetail selectOneDetail(@Param("id_pegawai") String id_pegawai, 
            @Param("year") String year, 
            @Param("id_indicator") Integer id_indicator)throws RuntimeException;
    
    

}
