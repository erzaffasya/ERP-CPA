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
import net.sra.prime.ultima.entity.HargaJual;
import net.sra.prime.ultima.entity.HargaJualDetil;
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
public interface MapperHargaJual {

    @Select("SELECT a.*, b.* FROM harga_jual a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak ")
    public List<HargaJual> selectAll()  throws RuntimeException;

    @Insert("INSERT INTO harga_jual "
            + "(id_customer, no_kontrak, nama_kontrak, tgl_mulai, tgl_selesai, tanggal) "
            + "VALUES (#{id_customer}, #{no_kontrak}, #{nama_kontrak}, #{tgl_mulai}, #{tgl_selesai}, #{tanggal})")
    public int insert(HargaJual hargaJual)  throws RuntimeException;

    @Delete("DELETE FROM harga_jual "
            + " WHERE "
            + " no_kontrak = #{no_kontrak} ")
    public int delete(@Param("no_kontrak") String no_kontrak)  throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, b.top, b.telepon  "
            + " FROM harga_jual a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak"
            + " WHERE a.no_kontrak=#{no_kontrak} ")
    public HargaJual selectOne(@Param("no_kontrak") String no_kontrak)  throws RuntimeException;

    @Select("SELECT a.*  "
            + " FROM harga_jual_detil a "
            + " WHERE a.no_kontrak=#{no_kontrak} "
            + " AND a.id_barang = #{id_barang} ")
    public HargaJualDetil selectPrices(@Param("no_kontrak") String no_kontrak, @Param("id_barang") String id_barang)  throws RuntimeException;

    
    @Update("UPDATE harga_jual  SET "
            + " id_customer = #{id_customer}, "
            + " no_kontrak = #{no_kontrak}, "
            + " nama_kontrak = #{nama_kontrak}, "
            + " tgl_mulai = #{tgl_mulai}, "
            + " tgl_selesai = #{tgl_selesai}, "
            + " tanggal = #{tanggal} "
            + " WHERE no_kontrak = #{id_lama} ")
    public int update(HargaJual hargaJual)  throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar "
            + " FROM harga_jual_detil a  "
            + " INNER JOIN barang b ON a.id_barang = b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " WHERE a.no_kontrak = #{no_kontrak}")
    public List<HargaJualDetil> selectAllDetil(@Param("no_kontrak") String no_kontrak) throws RuntimeException;

    @Insert("INSERT INTO harga_jual_detil "
            + "(no_kontrak, urut, id_barang, harga) "
            + "VALUES (#{no_kontrak}, #{urut}, #{id_barang}, #{harga})")
    public int insertDetil(HargaJualDetil hargaJualDetil)  throws RuntimeException;

    @Delete("DELETE FROM harga_jual_detil "
            + " WHERE "
            + " no_kontrak = #{no_kontrak} ")
    public int deleteDetil(@Param("no_kontrak") String no_kontrak)  throws RuntimeException;

    

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar  "
            + " FROM harga_jual_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " WHERE a.id=#{id} ")
    public HargaJualDetil selectOneDetil(@Param("id") Integer id) throws RuntimeException;
}
