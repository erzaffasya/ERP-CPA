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
import net.sra.prime.ultima.entity.MasterJabatan;
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
public interface MapperHrJabatan {

    

    @Select("SELECT a.*, b.jabatan as jabatan_atasan "
            + " FROM master_jabatan a "
            + " LEFT JOIN master_jabatan b ON a.atasan=b.id_jabatan "
            + " WHERE a.id_jabatan >= 100 "
            + " ORDER BY a.id_jabatan ")
    public List<MasterJabatan> selectAll();

    
    @Insert("INSERT INTO master_jabatan "
            + "(jabatan, atasan,headdepartemen) "
            + "VALUES "
            + "(#{jabatan}, #{atasan},#{headdepartemen}) ")
    public int insert(MasterJabatan masterJabatan)  throws RuntimeException;

 
    @Delete("DELETE FROM master_jabatan WHERE id_jabatan = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM master_jabatan WHERE id_jabatan=#{id}")
    public MasterJabatan selectOne(@Param("id") Integer id)throws RuntimeException;

    
    @Update("UPDATE master_jabatan  SET "
            + " jabatan = #{jabatan}, "
            + " atasan = #{atasan}, "
            + " headdepartemen = #{headdepartemen}"
            + " WHERE id_jabatan = #{id_jabatan} ")
    public int update(MasterJabatan hrDepartemen)  throws RuntimeException;

    
    @Select("SELECT a.* FROM master_jabatan a "
            + " INNER JOIN pegawai b ON a.id_jabatan=b.id_jabatan "
            + " INNER JOIN pengguna c ON b.id_pegawai=c.id_pegawai "
            + "WHERE c.usernamenya=#{usernamenya}")
    public MasterJabatan SelectLogin(@Param("usernamenya") String usernamenya);
}
