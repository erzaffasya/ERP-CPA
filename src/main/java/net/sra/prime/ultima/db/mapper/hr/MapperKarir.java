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
import net.sra.prime.ultima.entity.hr.Karir;
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
public interface MapperKarir {

    @Select("SELECT a.*,d.nama as kantor,c.departemen,b.jabatan "
            + " FROM hr.karir a  "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " INNER JOIN hr_departemen c ON a.id_departemen=c.id_departemen "
            + " INNER JOIN internal_kantor_cabang d ON a.id_kantor=d.id_kantor_cabang "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " ORDER BY a.tanggal_mulai ")
    public List<Karir> selectAll(@Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    
    
    @Insert("INSERT INTO hr.karir "
            + "(tanggal_mulai,tanggal_selesai,id_jabatan,id_departemen,id_kantor,statuskaryawan,id_pegawai) "
            + "VALUES "
            + "(#{tanggal_mulai}, #{tanggal_selesai},#{id_jabatan},#{id_departemen},#{id_kantor},#{statuskaryawan},#{id_pegawai}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Karir karir)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.karir WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*,d.nama as kantor,c.departemen,b.jabatan "
            + " FROM hr.karir a  "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " INNER JOIN hr_departemen c ON a.id_departemen=c.id_departemen "
            + " INNER JOIN internal_kantor_cabang d ON a.id_kantor=d.id_kantor_cabang"
            + " WHERE a.id=#{id}")
    public Karir selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.karir  SET "
            + " tanggal_mulai = #{tanggal_mulai}, "
            + " tanggal_selesai = #{tanggal_selesai}, "
            + " id_jabatan = #{id_jabatan}, "
            + " id_kantor = #{id_kantor}, "
            + " id_departemen = #{id_departemen}, "
            + " statuskaryawan=#{statuskaryawan} "
            + " WHERE id = #{id} ")
    public int update(Karir karir)  throws RuntimeException;
    

    
}
