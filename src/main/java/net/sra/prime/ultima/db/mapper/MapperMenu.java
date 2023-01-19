/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") throws RuntimeException;
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
import net.sra.prime.ultima.entity.HakAkses;
import net.sra.prime.ultima.entity.Menu;
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
public interface MapperMenu {

    @Select("SELECT * FROM menu a  "
            + " ORDER BY id")
    public List<Menu> selectAllMenu() throws RuntimeException;
    
    @Select("SELECT * FROM menu a  WHERE jenis='G'"
            + " ORDER BY id")
    public List<Menu> selectAllParent() throws RuntimeException;
    
    @Insert("INSERT INTO menu "
            + "(id,name,icon,outcome,urut,parent,keterangan,jenis,roles) "
            + "VALUES "
            + "(#{id},#{name},#{icon},#{outcome},#{urut},#{parent},#{keterangan},#{jenis},#{roles})")
    public int insert(Menu menu)  throws RuntimeException;
    
    @Select("SELECT COALESCE(max(id)+1,#{parent} + 1) FROM menu WHERE parent=#{parent}")
    public Integer selectMaxidDetail(@Param("parent") Integer parent) throws RuntimeException;
    
    @Select("SELECT max(id)+100 FROM menu WHERE jenis='G'")
    public Integer selectMaxidGroup() throws RuntimeException;
    
    @Select("SELECT * FROM menu a  WHERE id=#{id}")
    public Menu selectOneMenu(@Param("id") Integer id) throws RuntimeException;
    
    @Update("UPDATE menu SET "
            + " id = #{id}, "
            + " name = #{name}, "
            + " icon = #{icon}, "
            + " outcome = #{outcome}, "
            + " urut = #{urut}, "
            + " parent = #{parent}, "
            + " keterangan = #{keterangan}, "
            + " jenis = #{jenis}, "
            + " roles = #{roles} "
            + " WHERE id=#{idlama}")
    public int updateMenu(Menu menu) throws RuntimeException;
   
    @Delete("DELETE FROM menu WHERE id=#{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;
   
    @Select("SELECT * FROM menu a INNER JOIN hakakses b ON a.id=b.id_menu "
            + " WHERE parent is null AND usernamenya=#{usernamenya} "
            + " ORDER BY urut")
    public List<Menu> selectAll(@Param("usernamenya") String usernamenya) throws RuntimeException;
    
//    @Select("SELECT id FROM menu WHERE parent=#{id} LIMIT 1")
//    public Integer checkChild(@Param("id") Integer id) throws RuntimeException;
//    
    @Select("SELECT * FROM menu WHERE parent=#{id}")
    public List<Menu> subMenu(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT id FROM menu a INNER JOIN hakakses b ON a.id=b.id_menu "
            + " WHERE parent=#{id} AND usernamenya=#{usernamenya} LIMIT 1")
    public Integer checkChild(@Param("id") Integer id,
            @Param("usernamenya") String usernamenya) throws RuntimeException;
    
    @Select("SELECT * FROM menu a INNER JOIN hakakses b ON a.id=b.id_menu "
            + " WHERE parent=#{id} AND b.usernamenya=#{usernamenya} "
            + " ORDER BY a.urut")
    public List<Menu> subMenuNya(@Param("id") Integer id,
    @Param("usernamenya") String usernamenya) throws RuntimeException;
    
    @Select("SELECT * FROM menu WHERE jenis='D' AND parent is null ORDER BY urut")
    public List<Menu> selectAllMenuUtama() throws RuntimeException;
    
    @Select("SELECT a.id as id_menu2,b.* "
            + "FROM menu a "
            + "LEFT JOIN hakakses b ON a.id=b.id_menu AND b.usernamenya=#{usernamenya} "
            + "WHERE "
            + "a.jenis='D' AND a.parent is null  ORDER BY a.urut")
    public List<HakAkses> selectAllMenuUtamaHakAkses(@Param("usernamenya") String usernamenya) throws RuntimeException;
    
    
    @Select("SELECT a.id as id_menu2,b.* "
            + "FROM menu a "
            + "LEFT JOIN hakakses b ON a.id=b.id_menu AND b.usernamenya=#{usernamenya} "
            + "WHERE "
            + "a.jenis='D' AND a.parent=#{parent}  ORDER BY a.urut")
    public List<HakAkses> selectAllMenuHakAkses(@Param("usernamenya") String usernamenya, 
            @Param("parent") Integer parent) throws RuntimeException;
    
    
    
    @Insert("INSERT INTO hakakses "
            + "(usernamenya,id_menu) "
            + "VALUES "
            + "(#{usernamenya},#{id_menu})")
    public int insertHakAkses(HakAkses hakAkses)  throws RuntimeException;

    
    @Delete("DELETE FROM hakakses WHERE usernamenya = #{usernamenya}")
    public int deleteHakAkses(@Param("usernamenya") String usernamenya)  throws RuntimeException;
    
    @Delete("DELETE FROM hakakses WHERE usernamenya = #{usernamenya} AND id_menu=#{id_menu}")
    public int deleteOneHakAkses(@Param("usernamenya") String usernamenya, 
            @Param("id_menu") Integer id_menu)  throws RuntimeException;

    
    @Select("SELECT * FROM hakakses WHERE usernamenya=#{usernamenya}")
    public Integer[] selectAllHakAkses(@Param("usernamenya") String usernamenya) throws RuntimeException;
    
    @Delete("DELETE FROM hakakses WHERE id_menu=#{id_menu}")
    public int deleteMenuFromHakAkses(@Param("id_menu") Integer id_menu) throws RuntimeException;
    
    @Update("UPDATE hakakses SET id_menu=#{id_menu} WHERE id_menu=#{id_menu_lama}")
    public int updateHakAkses(@Param("id_menu") Integer id_menu, 
            @Param("id_menu_lama") Integer id_menu_lama) throws RuntimeException;
}
