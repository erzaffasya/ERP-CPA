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
import net.sra.prime.ultima.entity.hr.Tanggungan;
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
public interface MapperTanggungan {

    @Select("SELECT a.* "
            + " FROM hr.tanggungan a "
            + " ORDER BY a.id ")
    public List<Tanggungan> selectAll()  throws RuntimeException;

    
    @Insert("INSERT INTO hr.tanggungan "
            + "(id_pegawai,nama,hubungan,tgl_lahir,no_identitas,kerja,status_perkawinan) "
            + "VALUES "
            + "(#{id_pegawai},#{nama}, #{hubungan},#{tgl_lahir},#{no_identitas},#{kerja},#{status_perkawinan}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Tanggungan tanggungan)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.tanggungan WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM hr.tanggungan WHERE id=#{id}")
    public Tanggungan selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.tanggungan  SET "
            + " id_pegawai = #{id_pegawai}, "
            + " nama = #{nama}, "
            + " hubungan = #{hubungan}, "
            + " tgl_lahir = #{tgl_lahir}, "
            + " no_identitas = #{no_identitas}, "
            + " status_perkwinan = #{status_perkawinan} "
            + " WHERE id = #{id} ")
    public int update(Tanggungan tanggungan)  throws RuntimeException;

    

}
