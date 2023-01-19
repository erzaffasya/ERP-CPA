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
import net.sra.prime.ultima.entity.SoDetail;
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
public interface MapperSoDetail {

    
    @Select("SELECT * FROM so_detail")
    public List<SoDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO so_detail "
            + "(no_so, urut, id_gudang, id_barang, qty, harga, total, harga_asli, diskon, "
            + "id_pajak, diskonrp, diskonpersen, diambil, sisa, additional_charge, delivery_date, remark) "
            + "VALUES "
            + "(#{no_so}, #{urut}, #{id_gudang}, #{id_barang}, #{qty}, #{harga}, #{total}, #{harga_asli}, #{diskon}, "
            + "#{id_pajak}, #{diskonrp}, #{diskonpersen}, #{diambil}, #{sisa}, #{additional_charge}, #{delivery_date}, #{remark}) ")
    public int insert(SoDetail sodetail) throws RuntimeException;

    
    @Delete("DELETE FROM so_detail WHERE no_so = #{no_so}")
    public int delete(@Param("no_so") String no_so) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, b.isi_satuan, a.qty * b.isi_satuan as qty_kecil "
            + " FROM so_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.no_so=#{id_lama} "
            + " ORDER BY a.urut")
    public List<SoDetail> selectOne(@Param("id_lama") String id_lama) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, b.isi_satuan "
            + " FROM so_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.no_so=#{id_lama} AND a.sisa > 0"
            + " ORDER BY a.urut")
    public List<SoDetail> selectOneView(@Param("id_lama") String id_lama) throws RuntimeException;

    

    @Update("UPDATE so_detail  SET "
            + " diambil = diambil + #{diambil}, "
            + " sisa = sisa - #{diambil} "
            + " WHERE no_so = #{no_so} AND id_barang = #{id_barang}")
    public int update(@Param("no_so") String no_so, @Param("diambil") Double diambil, @Param("id_barang") String id_barang) throws RuntimeException;

    
    @Select("SELECT a.*, e.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, b.isi_satuan, "
            + " f.customer, g.gudang "
            + " FROM so_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " INNER JOIN so e ON a.no_so=e.nomor "
            + " INNER JOIN customer f ON f.id_kontak=e.id_customer "
            + " INNER JOIN gudang g ON g.id_gudang=e.id_gudang "
            + " WHERE "
            + " a.sisa > 0 AND g.id_kantor=#{id_kantor} AND e.status !='R' AND e.status!='D' "
            + " ORDER BY e.tanggal DESC, e.nomor DESC")
    public List<SoDetail> selectBookingRecord(@Param("id_kantor") String id_kantor) throws RuntimeException;

    @Select("SELECT sisa FROM so_detail WHERE no_so=#{no_so} AND id_barang=#{id_barang}")
    public Double selectSisaSO(@Param("no_so") String no_so, @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT count(*) FROM so_detail WHERE no_so=#{no_so} AND sisa > 0")
    public Integer selectSisaPartial(@Param("no_so") String no_so) throws RuntimeException;
}
