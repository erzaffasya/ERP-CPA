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
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Area;
import net.sra.prime.ultima.entity.hr.AreaKantor;
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
public interface MapperArea {

    @Select("SELECT a.*,b.nama as nama_parent,b.kode as kode_parent, "
            + " array_to_string(array(SELECT CONCAT('<div class=\"ui-g-3\">',c.nama,'</div>')  FROM internal_kantor_cabang c INNER JOIN hr.area_kantor d ON c.id_kantor_cabang=d.id_kantor WHERE a.id=d.id_area),' ') as kantor "
            + " FROM hr.area a  "
            + " INNER JOIN hr.area b ON a.parent=b.id "
            + " WHERE a.level = 2 "
            + " ORDER BY a.kode ")
    public List<Area> selectAll()  throws RuntimeException;
    
    @Select("SELECT a.*"
            + " FROM hr.area a WHERE level=1"
            + " ORDER BY a.id ")
    public List<Area> selectAllLevel1()  throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.area "
            + "(kode,parent,nama,level) "
            + "VALUES "
            + "(#{kode}, #{parent},#{nama},#{level}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Area area)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.area WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*,b.nama as nama_parent "
            + " FROM hr.area a  "
            + " INNER JOIN hr.area  b ON a.parent=b.id "
            + " WHERE a.id=#{id}")
    public Area selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.area  SET "
            + " kode = #{kode}, "
            + " nama = #{nama}, "
            + " parent = #{parent}, "
            + " level = #{level} "
            + " WHERE id = #{id} ")
    public int update(Area area)  throws RuntimeException;
    

    @Select("SELECT a.*,b.nama,c.nama as kantor "
            + " FROM hr.area_kantor a "
            + " INNER JOIN hr.area b ON a.id=b.id_area "
            + " INNER JOIN internal_kantor_cabang c ON a.id_kantor=c.id_kantor_cabang "
            + " WHERE a.id_area=#{id_area}"
            + " ORDER BY a.id_area ")
    public List<AreaKantor> selectAllAreaKantor(@Param("id_area") Integer id_area) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM internal_kantor_cabang a "
            + " WHERE a.id_kantor_cabang NOT IN (SELECT id_kantor FROM hr.area_kantor)"
            + " ORDER BY a.nama ")
    public List<InternalKantorCabang> selectAllKantor() throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM internal_kantor_cabang a"
            + " INNER JOIN hr.area_kantor b ON a.id_kantor_cabang=b.id_kantor WHERE b.id_area=#{id}"
            + " ORDER BY a.id_kantor_cabang ")
    public List<InternalKantorCabang> selectKantorArea(@Param("id") Integer id) throws RuntimeException;
    
    @Insert("INSERT INTO hr.area_kantor "
            + "(id_area,id_kantor) "
            + "VALUES "
            + "(#{id_area}, #{id_kantor}) ")
    public int insertAreaKantor(AreaKantor areaKantor)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.area_kantor WHERE id_area = #{id_area}")
    public int deleteAreaKantor(@Param("id_area") Integer id_area)  throws RuntimeException;
    
    
    @Select("SELECT COUNT(*) FROM hr.area WHERE parent=(SELECT c.parent FROM  hr.area c WHERE c.id=#{id})")
    public int jumlahArea(@Param("id") Integer id)  throws RuntimeException;

}
