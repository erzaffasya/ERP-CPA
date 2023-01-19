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
import net.sra.prime.ultima.entity.kpi.Indicator;
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
public interface MapperIndicator {

    @Select("SELECT a.*, b.category,c.unit  "
            + " FROM kpi.indicator a "
            + " INNER JOIN kpi.category b ON a.id_category=b.id_category "
            + " INNER JOIN kpi.unit c ON a.id_unit = c.id_unit "
            + " ORDER BY b.id_category,id_indicator ")
    public List<Indicator> selectAll() throws RuntimeException;

    @Insert("INSERT INTO kpi.indicator "
            + "(indicator,id_category,evidence,id_unit,bobot,target) "
            + "VALUES "
            + "(#{indicator},#{id_category},#{evidence},#{id_unit},#{bobot},#{target}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id_indicator", keyColumn = "id_indicator")
    public int insert(Indicator indicator) throws RuntimeException;

    @Delete("DELETE FROM kpi.indicator WHERE id_indicator = #{id_indicator}")
    public int delete(@Param("id_indicator") Integer id_indicator) throws RuntimeException;

    @Select("SELECT a.*,b.category,c.unit "
            + " FROM kpi.indicator a "
            + " INNER JOIN kpi.category b ON a.id_category=b.id_category "
            + " INNER JOIN kpi.unit c ON a.id_unit = c.id_unit "
            + " WHERE a.id_indicator = #{id_indicator} ")
    public Indicator selectOne(@Param("id_indicator") Integer id_indicator) throws RuntimeException;

    @Update("UPDATE kpi.indicator  SET "
            + " indicator = #{indicator}, "
            + " id_category = #{id_category}, "
            + " evidence = #{evidence}, "
            + " id_unit = #{id_unit}, "
            + " bobot = #{bobot}, "
            + " target = #{target} "
            + " WHERE id_indicator = #{id_indicator} ")
    public int update(Indicator indicator) throws RuntimeException;

}
