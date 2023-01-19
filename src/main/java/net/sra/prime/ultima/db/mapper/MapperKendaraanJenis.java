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
import net.sra.prime.ultima.entity.KendaraanJenis;
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
public interface MapperKendaraanJenis {

    @Select("SELECT * FROM kendaraan_jenis ORDER BY jenis ")
    public List<KendaraanJenis> selectAll( )throws RuntimeException;


    @Insert("INSERT INTO kendaraan_jenis "
            + "(id_jenis, jenis) "
            + "VALUES "
            + "(#{id_jenis}, #{jenis}) ")
    public int insert(KendaraanJenis kendaraanjenis)  throws RuntimeException;

    
    @Delete("DELETE FROM kendaraan_jenis WHERE id_jenis = #{id}")
    public int delete(@Param("id") String id)  throws RuntimeException;

    @Select("SELECT * FROM kendaraan_jenis WHERE id_jenis=#{id}")
    public KendaraanJenis selectOne(@Param("id") String id )throws RuntimeException;

    @Update("UPDATE kendaraan_jenis  SET "
            + " jenis = #{jenis} "
            + " WHERE id_jenis = #{id_jenis} ")
    public int update(KendaraanJenis kendaraanjenis)  throws RuntimeException;

    
}
