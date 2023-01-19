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
import net.sra.prime.ultima.db.provider.ProviderPemakaian;
import net.sra.prime.ultima.entity.Pemakaian;
import net.sra.prime.ultima.entity.PemakaianDetail;
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
public interface MapperPemakaian {

    @SelectProvider(type = ProviderPemakaian.class, method = "SelectAll")
    public List<Pemakaian> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPemakaian.class, method = "SelectAllJurnal")
    public List<Pemakaian> selectAllJurnal(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
    ) throws RuntimeException;

    @Insert("INSERT INTO pemakaian "
            + "(nomor, tanggal, kode_user, id_gudang, keterangan, status, tanggal_input) "
            + "VALUES (#{nomor}, #{tanggal}, #{kode_user}, #{id_gudang}, #{keterangan}, #{status}, LOCALTIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Pemakaian pemakaian) throws RuntimeException;

    @Delete("DELETE FROM pemakaian WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*,  b.gudang, c.nomor as no_cancel, c.tanggal as tgl_cancel, c.pesan  "
            + " FROM pemakaian a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " LEFT JOIN pemakaian_cancel c ON a.nomor=c.no_pemakaian "
            + " WHERE a.id=#{id}")
    public Pemakaian selectOne(@Param("id") Integer id) throws RuntimeException;

    
    @Update("UPDATE pemakaian  SET "
            + " nomor = #{nomor}, "
            + " tanggal = #{tanggal}, "
            + " keterangan = #{keterangan}, "
            + " kode_user = #{kode_user}, "
            + " id_gudang = #{id_gudang}, "
            + " status = #{status}, "
            + " tanggal_edit = LOCALTIMESTAMP "
            + " WHERE id = #{id} ")
    public int update(Pemakaian pemakaian) throws RuntimeException;

    @Update("UPDATE pemakaian  SET "
            + " status = #{status},  "
            + " tanggal_edit = CURRENT_DATE "
            + " WHERE nomor = #{nomor}")
    public int updateStatus(@Param("nomor") String nomor, 
            @Param("status") Character status)
            throws RuntimeException;
    
    
    @Select("SELECT MAX(SUBSTR(nomor,1,3)) "
            + " FROM pemakaian "
            + " WHERE EXTRACT(month FROM tanggal)=#{bulan} AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_gudang = #{id_gudang}")
    public String SelectMax(@Param("id_gudang") String id_gudang, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
   
    ////////////////////////// Pemakaian Detail /////////////////////////////////////
    
    @Insert("INSERT INTO pemakaian_detail "
            + "(id,urut, id_barang, qty, note) "
            + "VALUES "
            + "(#{id},#{urut}, #{id_barang}, #{qty}, #{note}) ")
    public int insertDetail(PemakaianDetail pemakaianDetail) throws RuntimeException;

    
    @Delete("DELETE FROM pemakaian_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, d.satuan_kecil, b.isi_satuan "
            + " FROM pemakaian_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut")
    public List<PemakaianDetail> selectOneDetail(@Param("id") Integer id) throws RuntimeException;
    
    @SelectProvider(type = ProviderPemakaian.class, method = "SelectAllPemakaian")
    public List<PemakaianDetail> selectAllPemakaian(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;

}
