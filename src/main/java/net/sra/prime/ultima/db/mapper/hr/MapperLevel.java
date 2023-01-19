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
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Golongan;
import net.sra.prime.ultima.entity.hr.Level;
import net.sra.prime.ultima.entity.hr.LevelJabatan;
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
public interface MapperLevel {

    @Select("SELECT a.*,b.golongan , "
            + " array_to_string(array(SELECT c.jabatan  FROM master_jabatan c INNER JOIN hr.level_jabatan d ON c.id_jabatan=d.id_jabatan WHERE a.id=d.id_level AND c.id_jabatan >= 100),', ') as jabatan "
            + " FROM hr.level a  "
            + " INNER JOIN hr.golongan b ON a.id_golongan=b.id "
            + " ORDER BY a.level,a.sublevel ")
    public List<Level> selectAll()  throws RuntimeException;
    
//    @Select("SELECT a.*"
//            + " FROM hr.level a WHERE level=1"
//            + " ORDER BY a.id ")
//    public List<Level> selectAllLevel1()  throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.level "
            + "(level,sublevel,id_golongan) "
            + "VALUES "
            + "(#{level}, #{sublevel},#{id_golongan}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Level level)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.level WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*,b.golongan "
            + " FROM hr.level a  "
            + " INNER JOIN hr.golongan  b ON a.id_golongan=b.id "
            + " WHERE a.id=#{id}")
    public Level selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.level  SET "
            + " level = #{level}, "
            + " sublevel = #{sublevel}, "
            + " id_golongan = #{id_golongan} "
            + " WHERE id = #{id} ")
    public int update(Level level)  throws RuntimeException;
    

//    @Select("SELECT a.*,b.nama,c.nama as kantor "
//            + " FROM hr.level_jabatan a "
//            + " INNER JOIN hr.level b ON a.id=b.id_level "
//            + " INNER JOIN master_jabatan c ON a.id_jabatan=c.id_jabatan_cabang "
//            + " WHERE a.id_level=#{id_level}"
//            + " ORDER BY a.id_level ")
//    public List<LevelJabatan> selectAllLevelJabatan(@Param("id_level") Integer id_level) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM master_jabatan a "
            + " WHERE a.id_jabatan NOT IN (SELECT id_jabatan FROM hr.level_jabatan) AND a.id_jabatan >= 100"
            + " ORDER BY a.jabatan ")
    public List<MasterJabatan> selectAllJabatan() throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM master_jabatan a"
            + " INNER JOIN hr.level_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " WHERE b.id_level=#{id} "
            + " AND a.id_jabatan >= 100 "
            + " ORDER BY a.id_jabatan ")
    public List<MasterJabatan> selectJabatanLevel(@Param("id") Integer id) throws RuntimeException;
    
    @Insert("INSERT INTO hr.level_jabatan "
            + "(id_level,id_jabatan) "
            + "VALUES "
            + "(#{id_level}, #{id_jabatan}) ")
    public int insertLevelJabatan(LevelJabatan levelJabatan)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.level_jabatan WHERE id_level = #{id_level}")
    public int deleteLevelJabatan(@Param("id_level") Integer id_level)  throws RuntimeException;
    
    @Select("SELECT * FROM hr.golongan")
    public List<Golongan> selectAllGolongan()  throws RuntimeException;

}
