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
import net.sra.prime.ultima.entity.kpi.Unit;
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
public interface MapperUnit {

    @Select("SELECT * FROM kpi.unit ORDER BY unit ")
    public List<Unit> selectAll()  throws RuntimeException;
    
    
    @Insert("INSERT INTO kpi.unit "
            + "(unit) "
            + "VALUES "
            + "(#{unit}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id_unit", keyColumn = "id_unit")
    public int insert(Unit unit)  throws RuntimeException;

 
    @Delete("DELETE FROM kpi.unit WHERE id_unit = #{id_unit}")
    public int delete(@Param("id_unit") Integer id_unit)  throws RuntimeException;

    
    @Select("SELECT * FROM kpi.unit WHERE id_unit = #{id_unit} ")
    public Unit selectOne(@Param("id_unit") Integer id_unit)throws RuntimeException;
    
    @Update("UPDATE kpi.unit  SET "
            + " unit = #{unit} "
            + " WHERE id_unit = #{id_unit} ")
    public int update(Unit unit)  throws RuntimeException;

}
