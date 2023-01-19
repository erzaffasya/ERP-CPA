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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.hr.HariLibur;
import net.sra.prime.ultima.entity.hr.HariLiburTipe;
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
public interface MapperHariLibur {

    @Select("SELECT a.*,b.namatipe "
            + " FROM hr.hari_libur a "
            + " INNER JOIN hr.hari_libur_tipe b ON a.tipe = b.id "
            + " WHERE EXTRACT(year FROM a.tanggal)=#{tahun} "
            + " ORDER BY a.tanggal ")
    public List<HariLibur> selectAll(@Param("tahun") Integer tahun)  throws RuntimeException;
    
    @Select("SELECT a.*,b.namatipe "
            + " FROM hr.hari_libur a "
            + " INNER JOIN hr.hari_libur_tipe b ON a.tipe = b.id "
            + " WHERE a.tanggal = #{tanggal}")
    public HariLibur selectOneByDate(@Param("tanggal") Date tanggal) throws RuntimeException;

    
    @Insert("INSERT INTO hr.hari_libur "
            + "(tanggal,tipe,nama) "
            + "VALUES "
            + "(#{tanggal},#{tipe},#{nama}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(HariLibur hariLibur)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.hari_libur WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM hr.hari_libur WHERE id=#{id}")
    public HariLibur selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.hari_libur  SET "
            + " tanggal = #{tanggal}, "
            + " nama = #{nama}, "
            + " tipe = #{tipe} "
            + " WHERE id = #{id} ")
    public int update(HariLibur hariLibur)  throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM hr.hari_libur_tipe a "
            + " ORDER BY a.id ")
    public List<HariLiburTipe> selectAllTipe()  throws RuntimeException;

}
