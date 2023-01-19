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
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HargaBeliDetil;
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
public interface MapperHargaBeli {

    @Select("SELECT a.*, b.*, c.customer FROM harga_beli a  "
            + " INNER JOIN supplier b ON a.id_supplier = b.id "
            + " LEFT  JOIN customer c ON a.id_customer = c.id_kontak ")
    public List<HargaBeli> selectAll() throws RuntimeException;

    @Insert("INSERT INTO harga_beli "
            + "(id_supplier, no_kontrak, nama_kontrak, tgl_mulai, tgl_selesai, tanggal,id_customer, shiptonumber,shipto,aktif) "
            + "VALUES (#{id_supplier}, #{no_kontrak}, #{nama_kontrak}, #{tgl_mulai}, #{tgl_selesai}, #{tanggal}, #{id_customer}, #{shiptonumber}, #{shipto}, #{aktif})")
    public int insert(HargaBeli hargaBeli) throws RuntimeException;

    @Delete("DELETE FROM harga_beli "
            + " WHERE "
            + " no_kontrak = #{no_kontrak} ")
    public int delete(@Param("no_kontrak") String no_kontrak) throws RuntimeException;

    @Select("SELECT a.*, b.supplier, b.top, b.telepon, c.customer  "
            + " FROM harga_beli a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " LEFT JOIN customer c ON a.id_customer = c.id_kontak "
            + " WHERE a.no_kontrak=#{no_kontrak} ")
    public HargaBeli selectOne(@Param("no_kontrak") String no_kontrak) throws RuntimeException;

    

    @Select("SELECT a.*  "
            + " FROM harga_beli a "
            + " WHERE a.id_supplier=#{id_supplier} "
            + " AND (id_customer = #{id_customer} OR id_customer is null) "
            + " AND tgl_mulai <= now() AND tgl_selesai >= now() ")
    public List<HargaBeli> selectBuyContract(@Param("id_supplier") String id_supplier, @Param("id_customer") String id_customer) throws RuntimeException;

    @Select("SELECT a.*  "
            + " FROM harga_beli_detil a "
            + " WHERE a.no_kontrak=#{no_kontrak} "
            + " AND a.id_barang = #{id_barang} ")
    public HargaBeliDetil selectPrices(@Param("no_kontrak") String no_kontrak, @Param("id_barang") String id_barang) throws RuntimeException;

    @Update("UPDATE harga_beli  SET "
            + " id_supplier = #{id_supplier}, "
            + " no_kontrak = #{no_kontrak}, "
            + " nama_kontrak = #{nama_kontrak}, "
            + " tgl_mulai = #{tgl_mulai}, "
            + " tgl_selesai = #{tgl_selesai}, "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " shipto = #{shipto}, "
            + " shiptonumber = #{shiptonumber}, "
            + " aktif = #{aktif}"
            + " WHERE no_kontrak = #{id_lama} ")
    public int update(HargaBeli hargaBeli) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, satuan_kecil "
            + " FROM harga_beli_detil a  "
            + " INNER JOIN barang b ON a.id_barang = b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.no_kontrak = #{no_kontrak}")
    public List<HargaBeliDetil> selectAllDetil(@Param("no_kontrak") String no_kontrak) throws RuntimeException;

    @Insert("INSERT INTO harga_beli_detil "
            + "(no_kontrak, urut, id_barang, harga, target) "
            + "VALUES (#{no_kontrak}, #{urut}, #{id_barang}, #{harga}, #{target})")
    public int insertDetil(HargaBeliDetil hargaBeliDetil) throws RuntimeException;

    @Delete("DELETE FROM harga_beli_detil "
            + " WHERE "
            + " no_kontrak = #{no_kontrak} ")
    public int deleteDetil(@Param("no_kontrak") String no_kontrak) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, d.satuan_kecil  "
            + " FROM harga_beli_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
            + " WHERE a.id=#{id} ")
    public HargaBeliDetil selectOneDetil(@Param("id") Integer id) throws RuntimeException;
}
