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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.Dotbl;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperDoDetail {

    @Select("SELECT * FROM do_detail a ")
    public List<DoDetail> selectAll() throws RuntimeException;
    
    @SelectProvider(type = ProviderDo.class, method = "SelectAllOsDo")
    public List<DoDetail> selectAllOsDo(
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor
            ) throws RuntimeException;
   
    
    @Insert("INSERT INTO do_detail "
            + "(no_do, urut, id_gudang, id_barang, qty, keterangan, packing_type, packing_number,qty_diterima,segel,kemasan) "
            + "VALUES "
            + "(#{no_do}, #{urut}, #{id_gudang}, #{id_barang}, #{qty}, #{keterangan}, #{packing_type}, #{packing_number},#{qty_diterima},#{segel},#{kemasan}) ")
    public int insert(DoDetail doDetail) throws RuntimeException;

    
    @Delete("DELETE FROM do_detail WHERE no_do = #{no_do}")
    public int delete(@Param("no_do") String no_do) throws RuntimeException;

    @Select("SELECT DISTINCT ON (a.urut) a.no_do,a.*, b.nama_barang, c.satuan_besar,d.harga, d.diskonpersen, d.diskonrp, b.isi_satuan"
            + " FROM do_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " INNER JOIN so_detail d ON d.id_barang = a.id_barang "
            + " WHERE a.no_do=#{id_lama} "
            + " ORDER BY a.urut")
    public List<DoDetail> selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    
    @Select("SELECT id_barang, qty_diterima as jml"
            + " FROM do_detail a "
            + " WHERE a.no_do = #{id_lama} AND a.id_barang = #{id_barang} ")
    public DoDetail selectSum(@Param("id_lama") String id_lama, @Param("id_barang") String id_barang) throws RuntimeException;

    @Update("UPDATE do_detail  SET "
            + " qty_diterima = #{qty_diterima} "
            + " WHERE no_do = #{no_do} AND urut=#{urut}")
    public int update(DoDetail doDetail) throws RuntimeException;

}
