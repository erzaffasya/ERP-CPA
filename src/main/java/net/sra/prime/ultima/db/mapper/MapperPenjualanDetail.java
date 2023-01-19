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
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.entity.PenjualanDetailReguler;
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
public interface MapperPenjualanDetail {

    @Select("SELECT * FROM penjualan_detail a ")
    public List<PenjualanDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO penjualan_detail "
            + "(no_penjualan, urut, id_gudang, id_barang, qty, harga, total, harga_asli,  id_pajak, diskonrp, diskonpersen, additional_charge) "
            + "VALUES "
            + "(#{no_penjualan}, #{urut}, #{id_gudang}, #{id_barang}, #{qty}, #{harga}, #{total}, #{harga_asli},  #{id_pajak}, #{diskonrp}, #{diskonpersen}, #{additional_charge}) ")
    public int insert(PenjualanDetail penjualandetail) throws RuntimeException;

   
    @Delete("DELETE FROM penjualan_detail WHERE no_penjualan = #{no_penjualan}")
    public int delete(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, b.isi_satuan "
            + " FROM penjualan_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.no_penjualan=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PenjualanDetail> selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE penjualan_detail  SET "
            + " diambil = diambil + #{diambil}, "
            + " sisa = sisa - #{diambil} "
            + " WHERE no_penjualan = #{no_penjualan} AND urut = #{urutso}")
    public int update(@Param("no_penjualan") String no_penjualan, @Param("diambil") Double diambil, @Param("urutso") Integer urutpo) throws RuntimeException;

    ///// Reguler /////
    
    
    @Insert("INSERT INTO penjualan_detail_reguler "
            + "(no_penjualan, urut,  nm_barang, qty, harga,satuan, total,  diskonrp, diskonpersen) "
            + "VALUES "
            + "(#{no_penjualan}, #{urut},  #{nm_barang}, #{qty}, #{harga},#{satuan}, #{total},  #{diskonrp}, #{diskonpersen}) ")
    public int insertReguler(PenjualanDetailReguler penjualanDetailReguler) throws RuntimeException;
    
    
    @Select("SELECT a.*"
            + " FROM penjualan_detail_reguler a "
            + " WHERE a.no_penjualan=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PenjualanDetailReguler> selectOneReguler(@Param("id_lama") String id_lama) throws RuntimeException;

    @Delete("DELETE FROM penjualan_detail_reguler WHERE no_penjualan = #{no_penjualan}")
    public int deleteReguler(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    ///// Penjualan HPP ////////////
    
    @Insert("INSERT INTO penjualan_hpp "
            + " (no_penjualan,id_barang,hpp,stok_id,qty) "
            + " VALUES "
            + " (#{no_penjualan},#{id_barang},#{hpp},#{stok_id},#{qty})")
    public int insertPenjualanHPP(@Param("no_penjualan") String no_penjualan,
            @Param("id_barang") String id_barang,
            @Param("hpp") Double hpp,
            @Param("stok_id") Double stok_id,
            @Param("qty") Double qty) throws RuntimeException;
}
