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
import net.sra.prime.ultima.db.provider.ProviderPengguna;
import net.sra.prime.ultima.entity.MapPenggunaCabang;
import net.sra.prime.ultima.entity.MapPenggunaGudang;
import net.sra.prime.ultima.entity.Pengguna;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Syamsu
 */
public interface MapperPengguna {

    @Select("SELECT * "
            + "FROM pengguna WHERE usernamenya = #{usernamenya} ")
    public Pengguna selectOnePengguna(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Select("SELECT * FROM pengguna WHERE usernamenya=#{usernamenya} AND passwordnya=md5(#{passwordnya})")
    public Pengguna CheckPassword(@Param("usernamenya") String usernamenya, @Param("passwordnya") String passwordnya) throws RuntimeException;
    
    
    @Select("SELECT * FROM pengguna WHERE usernamenya=#{usernamenya} AND pinnya=md5(#{pinnya})")
    public Pengguna CheckPin(@Param("usernamenya") String usernamenya, @Param("pinnya") String pinnya) throws RuntimeException;
    
    @Update("UPDATE pengguna SET loginterkahir = loginterbaru WHERE usernamenya = #{usernamenya}")
    public int updateLastLogin(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Update("UPDATE pengguna SET loginterbaru = now(), jmlogin = jmlogin + 1 WHERE usernamenya = #{usernamenya}")
    public int updateNewLogin(@Param("usernamenya") String usernamenya) throws RuntimeException;

    
    @Insert("INSERT INTO PENGGUNA "
            + "(id_pegawai, usernamenya, passwordnya, nama, "
            + "dibuat, roles,enabled,pinnya) "
            + "VALUES "
            + "(#{id_pegawai}, #{usernamenya}, md5(#{passwordnya}), "
            + "#{nama},  NOW(), #{roles}, #{enabled},md5(#{pinnya}))")
    public int insertPengguna(Pengguna pengguna) throws RuntimeException;

    
    @SelectProvider(type = ProviderPengguna.class, method = "SelectAll")
    public List<Pengguna> selectAllPengguna(
            @Param("status") Character status
    ) throws RuntimeException;

    @Select("SELECT * FROM pengguna "
            + "WHERE id_pegawai = #{id_pegawai}")
    public List<Pengguna> selectAllperPengguna(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT usernamenya FROM pengguna WHERE usernamenya <> 'ADMSU' ")
    public List<String> selectAllPenggunaUserName() throws RuntimeException;

    @Update("UPDATE pengguna SET passwordnya = md5(#{passwordnya}) "
            + "WHERE usernamenya = #{usernamenya}")
    public int updatePasswordPengguna(@Param("usernamenya") String usernamenya, @Param("passwordnya") String passwordnya) throws RuntimeException;
    
    @Update("UPDATE pengguna SET pinnya = md5(#{pinnya}) "
            + "WHERE usernamenya = #{usernamenya}")
    public int updatePinPengguna(@Param("usernamenya") String usernamenya, @Param("pinnya") String pinnya) throws RuntimeException;

    @Update("UPDATE pengguna SET "
            + " passwordnya=md5(#{passwordnya}), "
            + " pinnya=md5(#{pinnya}), "
            + " enabled=#{enabled} "
            + " WHERE usernamenya = #{usernamenya}")
    public void update(Pengguna item) throws RuntimeException;

    @Delete("DELETE FROM pengguna WHERE usernamenya = #{usernamenya} ")
    public int delete(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Select("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen "
            + " FROM pengguna a "
            + " INNER JOIN pegawai p ON a.id_pegawai=p.id_pegawai "
            + " LEFT JOIN master_jabatan b ON p.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON p.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON p.id_departemen_new=d.id_departemen "
            + " WHERE usernamenya = #{usernamenya} ")
            
    public Pengguna selectOne(@Param("usernamenya") String usernamenya) throws RuntimeException;

    ////////////////////////// Map Pengguna Cabang
    @Select("SELECT a.*, b.nama as cabang FROM map_pengguna_cabang a "
            + " INNER JOIN internal_kantor_cabang  ON a.id_kantor_cabang = b.id_kantor_cabang")
    public List<MapPenggunaCabang> selectAllPenggunaCabang() throws RuntimeException;

    @Select("SELECT * "
            + "FROM map_pengguna_cabang WHERE usernamenya = #{usernamenya} ")
    public MapPenggunaCabang selectOnePenggunaCabang(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Insert("INSERT INTO map_pengguna_cabang "
            + "(id_kantor_cabang, id_perusahaan, usernamenya) "
            + " VALUES "
            + " (#{id_kantor_cabang}, #{id_perusahaan}, #{usernamenya}) ")
    public int insertPenggunaCabang(MapPenggunaCabang mapPenggunaCabang) throws RuntimeException;

    @Delete("DELETE FROM map_pengguna_cabang WHERE usernamenya = #{usernamenya} ")
    public int deletePenggunaCabang(@Param("usernamenya") String usernamenya) throws RuntimeException;

    ////////////////////////// Map Pengguna Gudang
    @Select("SELECT a.*, b.nama as cabang FROM map_pengguna_gudang a "
            + " INNER JOIN gudang  ON a.id_gudang = b.id_gudang")
    public List<MapPenggunaGudang> selectAllPenggunaGudang() throws RuntimeException;

    @Select("SELECT * "
            + "FROM map_pengguna_gudang WHERE usernamenya = #{usernamenya} ")
    public MapPenggunaGudang selectOnePenggunaGudang(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Insert("INSERT INTO map_pengguna_gudang "
            + "(id_gudang, id_perusahaan, usernamenya) "
            + " VALUES "
            + " (#{id_gudang}, #{id_perusahaan}, #{usernamenya}) ")
    public int insertPenggunaGudang(MapPenggunaGudang mapPenggunaGudang) throws RuntimeException;

    @Delete("DELETE FROM map_pengguna_gudang WHERE usernamenya = #{usernamenya} ")
    public int deletePenggunaGudang(@Param("usernamenya") String usernamenya) throws RuntimeException;

}
