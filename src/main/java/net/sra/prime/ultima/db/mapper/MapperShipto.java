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
package net.sra.prime.ultima.db.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.Shipto;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Syamsu
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperShipto {

    //// Master Gudang ////
    @Select("SELECT * from shipto "
            + " ORDER BY id")
    public List<Shipto> select() throws RuntimeException;

    
    @Insert("INSERT INTO shipto "
            + "(shipto, alamat) "
            + "VALUES "
            + "(#{shipto}, #{alamat}) ")
    public int insert(Shipto shipto) throws RuntimeException;
    
    @Update("UPDATE shipto set shipto=#{shipto}, alamat=#{alamat} WHERE id=#{id}")
    public int update(Shipto shipto) throws RuntimeException;

    @Delete("DELETE FROM shipto where id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * FROM shipto WHERE shipto=#{shipto}")
    public Shipto selectOne(@Param("shipto") String shipto) throws RuntimeException;

    
}
