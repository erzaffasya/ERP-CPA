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
import net.sra.prime.ultima.entity.kpi.PegawaiKpi;
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
public interface MapperPegawaiKpi {

    @Select("SELECT a.*, e.nama as kantor, c.jabatan, d.departemen, f.nama as nama_atasan "
            + " FROM pegawai a "
            + " INNER JOIN master_jabatan c ON a.id_jabatan_new = c.id_jabatan "
            + " INNER JOIN hr_departemen d ON a.id_departemen_new = d.id_departemen "
            + " INNER JOIN internal_kantor_cabang e ON a.id_kantor_new = e.id_kantor_cabang "
            + " LEFT JOIN pegawai f ON a.atasan_langsung = f.id_pegawai "
            + " ORDER BY a.id_pegawai ")
    public List<PegawaiKpi> selectAllPegawai() throws RuntimeException;

    @Select("SELECT a.id_indicator,a.indicator,b.id_category,b.category,c.evidence,c.bobot,c.target,c.id_pegawai "
            + " FROM kpi.indicator a"
            + " INNER JOIN kpi.category b ON a.id_category = b.id_category "
            + " INNER JOIN kpi.pegawai_kpi c ON a.id_indicator = c.id_indicator "
            + " WHERE "
            + " c.id_pegawai = #{id_pegawai}"
            + " ORDER BY b.id_category,a.id_indicator ")
    public List<PegawaiKpi> selectAllKpiPegawai(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT a.id_indicator,a.indicator,b.id_category,b.category, "
            + " CASE WHEN c.bobot IS NULL "
            + "            THEN a.bobot "
            + "            ELSE c.bobot "
            + "    END AS bobot, "
            + " CASE WHEN c.target IS NULL "
            + "            THEN a.target "
            + "            ELSE c.target "
            + "    END AS target, "
            + " CASE WHEN c.evidence IS NULL "
            + "            THEN a.evidence "
            + "            ELSE c.evidence "
            + "    END AS evidence "
            + " FROM kpi.indicator a"
            + " INNER JOIN kpi.category b ON a.id_category = b.id_category "
            + " LEFT JOIN kpi.pegawai_kpi c ON a.id_indicator = c.id_indicator AND c.id_pegawai = #{id_pegawai} "
            + " ORDER BY b.id_category,a.id_indicator ")
    public List<PegawaiKpi> selectAllIndicator(String id_pegawai) throws RuntimeException;

    @Insert("INSERT INTO kpi.pegawai_kpi "
            + " (id_pegawai,id_indicator,target,bobot,evidence) "
            + " VALUES "
            + " (#{id_pegawai},#{id_indicator},#{target},#{bobot},#{evidence}) ")
    public int insert(PegawaiKpi pegawaiKpi) throws RuntimeException;

    @Delete("DELETE FROM kpi.pegawai_kpi WHERE id_pegawai = #{id_pegawai}")
    public int delete(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT a.*, b.nama, c.penilai_name, d.create_name "
            + " FROM kpi.pegawai_kpi a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " INNER JOIN pegawai c ON a.penilai = c.id_pegawai "
            + " INNER JOIN pegawai d ON a.create_by ON d.id_pegawai "
            + " WHERE "
            + " id_pegawai = #{id_pegawai} AND year = #{year}")
    public PegawaiKpi selectOne(@Param("id_pegawai") String id_pegawai,
            @Param("year") String year) throws RuntimeException;

    @Update("UPDATE kpi.pegawai_kpi  SET "
            + " date = #{date}, "
            + " penilai = #{penilai} "
            + " WHERE id_pegawai = #{id_pegawai}  AND year = #{year} ")
    public int update(PegawaiKpi category) throws RuntimeException;

}
