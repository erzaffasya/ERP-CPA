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
import net.sra.prime.ultima.entity.hr.Pendidikan;
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
public interface MapperPendidikan {

    @Select("SELECT a.* "
            + " FROM hr.pendidikan a  "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " ORDER BY a.tahun ")
    public List<Pendidikan> selectAll(@Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    
    
    @Insert("INSERT INTO hr.pendidikan "
            + "(tahun,deskripsi,id_pegawai) "
            + "VALUES "
            + "(#{tahun}, #{deskripsi},#{id_pegawai}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Pendidikan pendidikan)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.pendidikan WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.* "
            + " FROM hr.pendidikan a  "
            + " WHERE a.id=#{id}")
    public Pendidikan selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.pendidikan  SET "
            + " tahun = #{tahun}, "
            + " deskripsi = #{deskripsi} "
            + " WHERE id = #{id} ")
    public int update(Pendidikan pendidikan)  throws RuntimeException;
    

    
}
