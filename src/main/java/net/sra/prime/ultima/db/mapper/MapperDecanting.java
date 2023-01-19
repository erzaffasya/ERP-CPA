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
import net.sra.prime.ultima.db.provider.ProviderDecanting;
import net.sra.prime.ultima.entity.Decanting;
import net.sra.prime.ultima.entity.DecantingDetail;
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
public interface MapperDecanting {

    @SelectProvider(type = ProviderDecanting.class, method = "SelectAll")
    public List<Decanting> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status
            ) throws RuntimeException;


    @Insert("INSERT INTO decanting "
            + "(no_decanting, tanggal, id_gudang, id_perusahaan, keterangan, diajukan, referensi, flushing, totalatas, totalbawah, status) "
            + "VALUES (#{no_decanting}, #{tanggal}, #{id_gudang}, #{id_perusahaan}, #{keterangan},  #{diajukan}, #{referensi}, #{flushing}, #{totalatas}, #{totalbawah},#{status})")
    public int insert(Decanting decanting) throws RuntimeException;

    
    @Delete("DELETE FROM decanting WHERE no_decanting = #{no_decanting}")
    public int delete(@Param("no_decanting") String no_decanting) throws RuntimeException;

    
    @Select("SELECT a.*, b.gudang  "
            + " FROM decanting a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang"
            + " WHERE a.no_decanting=#{id_lama}")
    public Decanting selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE decanting  SET "
            + " tanggal = #{tanggal}, "
            + " referensi = #{referensi},"
            + " status = #{status} "
            + " WHERE no_decanting = #{no_decanting} ")
    public int update(Decanting decanting) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.no_decanting,1,3)) "
            + " FROM decanting a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang"
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND b.id_gudang = #{id_gudang} OR b.parent=#{id_gudang}")
    public String SelectMax(@Param("id_gudang") String id_gudang, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    
    /////////////////////////////////// Decanting Detail ////////////////////////////////
    @Insert("INSERT INTO decanting_detail "
            + "(no_decanting, urut,  id_barang, qty, packaging,batch, asal) "
            + "VALUES "
            + "(#{no_decanting}, #{urut}, #{id_barang}, #{qty}, #{packaging}, #{batch}, #{asal}) ")
    public int insertDetail(DecantingDetail decantingDetail) throws RuntimeException;

    
    @Delete("DELETE FROM decanting_detail WHERE no_decanting = #{no_decanting}")
    public int deleteDetail(@Param("no_decanting") String no_decanting) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, a.qty / b.isi_satuan as qtybesar "
            + " FROM decanting_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar=d.id_satuan_besar "
            + " WHERE a.no_decanting=#{id_lama} AND a.asal=#{asal}"
            + " ORDER BY a.urut")
    public List<DecantingDetail> selectOneDetail(@Param("id_lama") String id_lama, @Param("asal") Character asal) throws RuntimeException;

    
    @SelectProvider(type = ProviderDecanting.class, method = "SelectAllDecanting")
    public List<DecantingDetail> selectAllDecanting(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;
}
