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
import net.sra.prime.ultima.entity.Pricelist;
import net.sra.prime.ultima.entity.PricelistDetail;
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
public interface MapperPricelist {

    @Select("SELECT a.*, b.gudang FROM pricelist a  "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang ")
    public List<Pricelist> selectAll() throws RuntimeException;

    @Insert("INSERT INTO pricelist "
            + "(id_gudang, aktif, usernamenya, id_perusahaan) "
            + "VALUES "
            + "(#{id_gudang}, #{aktif}, #{usernamenya}, #{id_perusahaan})")
    public int insert(Pricelist pricelist) throws RuntimeException;

    @Delete("DELETE FROM pricelist "
            + " WHERE "
            + " id_gudang = #{id_gudang} ")
    public int delete(@Param("id_gudang") String no_pricelist) throws RuntimeException;

    @Select("SELECT a.*, b.gudang  "
            + " FROM pricelist a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " WHERE a.id_gudang=#{id_gudang} ")
    public Pricelist selectOne(@Param("id_gudang") String id_gudang) throws RuntimeException;

    @Update("UPDATE pricelist  SET "
            + " id_gudang = #{id_gudang}, "
            + " aktif = #{aktif}, "
            + " usernamenya = #{usernamenya}, "
            + " id_perusahaan = #{id_perusahaan} "
            + " WHERE id_gudang = #{id_lama} ")
    public int update(Pricelist pricelist) throws RuntimeException;

    ////////////////////////// pricelist detail///////////////
    

    @Select("SELECT a.*  "
            + " FROM pricelist a "
            + " WHERE a.id_gudang=#{id_gudang} "
            + " AND a.id_barang = #{id_barang} ")
    public PricelistDetail selectPrices(@Param("id_gudang") String id_gudang, @Param("id_barang") String id_barang) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, d.satuan_kecil, b.isi_satuan "
            + " FROM pricelist_detail a  "
            + " INNER JOIN barang b ON a.id_barang = b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.id_gudang = #{id_gudang} "
            + " ORDER BY b.nama_barang")
    public List<PricelistDetail> selectAllDetail(@Param("id_gudang") String id_gudang) throws RuntimeException;

    @Insert("INSERT INTO pricelist_detail "
            + "(id_gudang, urut, id_barang, harga) "
            + "VALUES (#{id_gudang}, #{urut}, #{id_barang}, #{harga})")
    public int insertDetail(PricelistDetail pricelistDetail) throws RuntimeException;

    @Delete("DELETE FROM pricelist_detail "
            + " WHERE "
            + " id_gudang = #{id_gudang} ")
    public int deleteDetail(@Param("id_gudang") String id_gudang) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, d.satuan_kecil, b.isi_satuan  "
            + " FROM pricelist_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
            + " WHERE a.id_gudang=#{id_gudang} "
            + " AND a.id_barang = #{id_barang} ")
    public PricelistDetail selectOneDetail(@Param("id_gudang") String id_gudang, 
            @Param("id_barang") String id_barang) throws RuntimeException;
}
