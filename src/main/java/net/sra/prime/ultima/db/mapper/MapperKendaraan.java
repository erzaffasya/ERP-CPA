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
import net.sra.prime.ultima.entity.Kendaraan;
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
public interface MapperKendaraan {

    @Select("SELECT a.*,b.jenis FROM kendaraan a INNER JOIN kendaraan_jenis b ON a.id_jenis=b.id_jenis ORDER BY a.id ")
    public List<Kendaraan> selectAll() throws RuntimeException;

    @Select("SELECT a.*,b.jenis FROM kendaraan a INNER JOIN kendaraan_jenis b ON a.id_jenis=b.id_jenis WHERE a.id_jenis = #{id_jenis} ORDER BY a.id ")
    public List<Kendaraan> selectAllByJenis(@Param("id_jenis") String id_jenis) throws RuntimeException;

    @Insert("INSERT INTO kendaraan "
            + "(id_jenis, nopol, no_bpkb, no_stnk, no_uji, tgl_jatuh_tempo, no_rangka, no_mesin, merk, thn_perakitan, thn_pembelian, warna, no_armada, odometer, no_gps, nama_driver, foto_unit) "
            + "VALUES "
            + "(#{id_jenis}, #{nopol}, #{no_bpkb}, #{no_stnk}, #{no_uji}, #{tgl_jatuh_tempo}, #{no_rangka}, #{no_mesin}, #{merk}, #{thn_perakitan}, #{thn_pembelian}, #{warna}, #{no_armada}, #{odometer}, #{no_gps}, #{nama_driver}, #{foto_unit}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Kendaraan kendaraan) throws RuntimeException;

    @Delete("DELETE FROM kendaraan WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * FROM kendaraan WHERE id=#{id}")
    public Kendaraan selectOne(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE kendaraan  SET "
            + " id_jenis = #{id_jenis}, "
            + " nopol=#{nopol}, "
            + " no_bpkb=#{no_bpkb},  "
            + " no_stnk=#{no_stnk}, "
            + " no_uji=#{no_uji}, "
            + " tgl_jatuh_tempo=#{tgl_jatuh_tempo}, "
            + " no_rangka=#{no_rangka}, "
            + " no_mesin=#{no_mesin}, "
            + " merk=#{merk}, "
            + " thn_perakitan=#{thn_perakitan},"
            + " thn_pembelian=#{thn_pembelian}, "
            + " warna=#{warna}, "
            + " no_armada=#{no_armada}, "
            + " odometer=#{odometer}, "
            + " no_gps=#{no_gps}, "
            + " nama_driver=#{nama_driver}, "
            + " foto_unit=#{foto_unit}"
            + " WHERE id = #{id} ")
    public int update(Kendaraan kendaraan) throws RuntimeException;

}
