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
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.entity.PoDetailReguler;
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
public interface MapperPoDetail {

    
    @Select("SELECT * FROM po_detail a ")
    public List<PoDetail> selectAll() throws RuntimeException;

    
    @Insert("INSERT INTO po_detail "
            + "(id, urut,  id_barang, qty, harga, total, diambil, sisa, diskonpersen, diskonrp) "
            + "VALUES "
            + "(#{id}, #{urut}, #{id_barang}, #{qty}, #{harga}, #{total}, 0, #{qty}, #{diskonpersen}, #{diskonrp}) ")
    public int insert(PoDetail podetail) throws RuntimeException;
    
    @Insert("INSERT INTO po_detail "
            + "(id, urut,  id_barang, qty, harga, total, diambil, sisa, diskonpersen, diskonrp,invoice) "
            + "VALUES "
            + "(#{id}, #{urut}, #{id_barang}, #{qty}, #{harga}, #{total}, #{diambil}, #{sisa}, #{diskonpersen}, #{diskonrp},#{invoice}) ")
    public int insertMaintenance(PoDetail podetail) throws RuntimeException;

    
    @Delete("DELETE FROM po_detail WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

  
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, "
            + " d.satuan_besar, a.qty / b.isi_satuan as qtybesar, "
            + " a.diambil / b.isi_satuan as diambilbesar, "
            + " a.sisa / b.isi_satuan as sisabesar "
            + " FROM po_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut")
    public List<PoDetail> selectOneperPo(@Param("id") Integer id) throws RuntimeException;

    
    @Update("UPDATE po_detail  SET "
            + " diambil = #{diambil}, "
            + " sisa = qty - #{diambil} "
            + " WHERE id = #{id} AND id_barang = #{id_barang}")
    public int update(@Param("id") Integer id, @Param("diambil") Double diambil, @Param("id_barang") String id_barang) throws RuntimeException;
    
    /// untuk cancel penerimaan barang
    @Update("UPDATE po_detail  SET "
            + " diambil = diambil - #{diambil}, "
            + " sisa = sisa + #{diambil} "
            + " WHERE id = #{id} AND id_barang = #{id_barang}")
    public int updateCancel(@Param("id") Integer id, @Param("diambil") Double diambil, @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Update("UPDATE po_detail  SET "
            + " invoice = #{invoice} + invoice "
            + " WHERE id = #{id} AND id_barang = #{id_barang}")
    public int updateInvoice(@Param("id") Integer id, @Param("invoice") Double invoice, @Param("id_barang") String id_barang) throws RuntimeException;


    //////////////////////////////////////////////////////////REGULER///////////////////////////////////////////////////////
    @Insert("INSERT INTO po_detail_reguler "
            + "(id, urut,  nm_barang, qty, harga, total, diambil, sisa, diskonpersen, diskonrp, satuan) "
            + "VALUES "
            + "(#{id}, #{urut}, #{nm_barang}, #{qty}, #{harga}, #{total}, #{diambil}, #{sisa}, #{diskonpersen}, #{diskonrp}, #{satuan}) ")
    public int insertReguler(PoDetailReguler poDetailReguler) throws RuntimeException;

    
    @Select("SELECT a.* "
            + " FROM po_detail_reguler a "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut")
    public List<PoDetailReguler> selectOneReguler(@Param("id") Integer id) throws RuntimeException;

    
    @Delete("DELETE FROM po_detail_reguler WHERE id = #{id}")
    public int deleteReguler(@Param("id") Integer id) throws RuntimeException;

    
    @SelectProvider(type = ProviderPo.class, method = "selectOutstanding")
    public List<PoDetail> selectOutstanding(
            @Param("id_gudang") String id_gudang,
            @Param("suppliernya") String suppliernya
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPo.class, method = "SelectAllPo")
    public List<PoDetail> selectAllPo(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;

    
}
