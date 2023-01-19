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
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperIntruksiPo {
    
    @SelectProvider(type = ProviderIntruksiPo.class, method = "SelectAll")
    public List<IntruksiPo> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("jenis") Character jenis
            ) throws RuntimeException;


    @Insert("INSERT INTO intruksi_po "
            + "(no_intruksi_po, tanggal, id_gudang, keterangan,  status, id_forecast,create_by,create_date) "
            + "VALUES (#{no_intruksi_po}, #{tanggal}, #{id_gudang}, #{keterangan},   #{status}, #{id_forecast},#{create_by},LOCALTIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(IntruksiPo intruksiPo) throws RuntimeException;

    
    @Delete("DELETE FROM intruksi_po WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*, b.gudang,c.no_forecast, d.pesan, d.tanggal as tgl_cancel,f.nama as create_name,g.nama as modified_name   "
            + " FROM intruksi_po a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " LEFT JOIN forecast c ON a.id_forecast=c.id "
            + " LEFT JOIN pesan_cancel d ON a.no_intruksi_po = d.nomor "
            + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
            + " WHERE a.id=#{id}")
    public IntruksiPo selectOne(@Param("id") Integer id) throws RuntimeException;

    
    @Update("UPDATE intruksi_po  SET "
            + " tanggal = #{tanggal}, "
            + " id_gudang = #{id_gudang}, "
            + " keterangan = #{keterangan}, "
            + " status = #{status},"
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE id = #{id} ")
    public int update(IntruksiPo intruksiPo) throws RuntimeException;

    //////////////////// Intruksi PO Detail //////////////////
    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil "
            + " FROM intruksi_po_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil=c.id_satuan_kecil "
            + " WHERE id=#{id} "
            + " ORDER BY urut")
    public List<IntruksiPoDetail> selectAllDetail(@Param("id") Integer id) throws RuntimeException;

    
    @Insert("INSERT INTO intruksi_po_detail "
            + "(id, urut, id_barang, qty, diambil, sisa, ket) "
            + "VALUES (#{id}, #{urut}, #{id_barang}, #{qty}, #{diambil}, #{sisa}, #{ket})")
    public int insertDetail(IntruksiPoDetail intruksiPoDetail) throws RuntimeException;
    
    
    @Delete("DELETE FROM intruksi_po_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_barang "
            + " FROM intruksi_po_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.id=#{id}")
    public IntruksiPo selectOneDetail(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(no_intruksi_po,1,3)) "
            + " FROM intruksi_po "
            + " WHERE EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    
    @Update("UPDATE intruksi_po_detail  SET "
            + " diambil = #{diambil}, "
            + " sisa = qty - #{diambil} "
            + " WHERE id = #{id} AND id_barang=#{id_barang}")
    public int updateSisa(IntruksiPoDetail intruksiPoDetail) throws RuntimeException;
    
    @Update("UPDATE intruksi_po set status=#{status} WHERE id=#{id}")
    public int updateStatus(@Param("status") Character status,@Param("id") Integer id) throws RuntimeException;
    
    @SelectProvider(type = ProviderIntruksiPo.class, method = "SelectAllReport")
    public List<IntruksiPoDetail> selectAllReport(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;

}
