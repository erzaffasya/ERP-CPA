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
import net.sra.prime.ultima.entity.hr.Trainning;
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
public interface MapperTrainning {

    @Select("SELECT a.* "
            + " FROM hr.trainning a  "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " ORDER BY a.tahun ")
    public List<Trainning> selectAll(@Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    
    
    @Insert("INSERT INTO hr.trainning "
            + "(tahun,deskripsi,no_sertifikat,tgl_expire,id_pegawai) "
            + "VALUES "
            + "(#{tahun}, #{deskripsi},#{no_sertifikat},#{tgl_expire},#{id_pegawai}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Trainning trainning)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.trainning WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.* "
            + " FROM hr.trainning a  "
            + " WHERE a.id=#{id}")
    public Trainning selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.trainning  SET "
            + " tahun = #{tahun}, "
            + " deskripsi = #{deskripsi}, "
            + " no_sertifikat = #{no_sertifikat}, "
            + " tgl_expire = #{tgl_expire} "
            + " WHERE id = #{id} ")
    public int update(Trainning trainning)  throws RuntimeException;
    

    
}
