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
import net.sra.prime.ultima.entity.Bank;
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
public interface MapperBank {

    @Select("SELECT * FROM bank ORDER BY bank ")
    public List<Bank> selectAll( )throws RuntimeException;


    @Insert("INSERT INTO bank (bank,rekening,atasnama) VALUES (#{bank}, #{rekening}, #{atasnama})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Bank bank)  throws RuntimeException;

    
    @Delete("DELETE FROM bank WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    @Select("SELECT * FROM bank WHERE id=#{id}")
    public Bank selectOne(@Param("id") Integer id )throws RuntimeException;

    @Update("UPDATE bank  SET bank = #{bank},rekening=#{rekening},atasnama=#{atasnama} WHERE id = #{id}")
    public int update(Bank bank)  throws RuntimeException;

    
}
