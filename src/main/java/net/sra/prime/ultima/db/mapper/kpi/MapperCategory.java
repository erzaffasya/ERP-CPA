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
import net.sra.prime.ultima.entity.kpi.Category;
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
public interface MapperCategory {

    @Select("SELECT * FROM kpi.category ORDER BY id_category ")
    public List<Category> selectAll()  throws RuntimeException;
    
    
    @Insert("INSERT INTO kpi.category "
            + "(category) "
            + "VALUES "
            + "(#{category}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id_category", keyColumn = "id_category")
    public int insert(Category category)  throws RuntimeException;

 
    @Delete("DELETE FROM kpi.category WHERE id_category = #{id_category}")
    public int delete(@Param("id_category") Integer id_category)  throws RuntimeException;

    
    @Select("SELECT * FROM kpi.category WHERE id_category = #{id_category} ")
    public Category selectOne(@Param("id_category") Integer id_category)throws RuntimeException;
    
    @Update("UPDATE kpi.category  SET "
            + " category = #{category} "
            + " WHERE id_category = #{id_category} ")
    public int update(Category category)  throws RuntimeException;

}
