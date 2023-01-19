/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" )throws RuntimeException;
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
package net.sra.prime.ultima.db.mapper;

import java.util.List;
import net.sra.prime.ultima.entity.Family;
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
public interface MapperFamily {

    @Select("SELECT * FROM family ORDER BY family ")
    public List<Family> selectAll( )throws RuntimeException;


    @Insert("INSERT INTO family "
            + "(id_family, family) "
            + "VALUES "
            + "(#{id_family}, #{family}) ")
    public int insert(Family family)  throws RuntimeException;

    
    @Delete("DELETE FROM family WHERE id_family = #{id}")
    public int delete(@Param("id") String id)  throws RuntimeException;

    @Select("SELECT * FROM family WHERE id_family=#{id}")
    public Family selectOne(@Param("id") String id )throws RuntimeException;

    @Update("UPDATE family  SET "
            + " family = #{family} "
            + " WHERE id_family = #{id_family} ")
    public int update(Family family)  throws RuntimeException;

    
}
