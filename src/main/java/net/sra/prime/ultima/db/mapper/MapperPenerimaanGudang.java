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
import net.sra.prime.ultima.db.provider.ProviderPenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudangDetail;
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
public interface MapperPenerimaanGudang {

    @SelectProvider(type = ProviderPenerimaanGudang.class, method = "SelectAll")
    public List<PenerimaanGudang> SelectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Boolean status,
            @Param("st") Boolean st
    ) throws RuntimeException;

    
    @Insert("INSERT INTO penerimaan_gudang "
            + "(no_penerimaan, tgl_penerimaan, id_supplier, id_gudang, referensi, keterangan,  alamat, status, nomor_po, tanggal_referensi,create_by,create_date) "
            + "VALUES "
            + "(#{no_penerimaan}, #{tgl_penerimaan}, #{id_supplier}, #{id_gudang}, #{referensi},  #{keterangan},   #{alamat},  #{status},  #{nomor_po}, #{tanggal_referensi},#{create_by},LOCALTIMESTAMP )")
    public int insert(PenerimaanGudang penerimaanGudang) throws RuntimeException;

    @Delete("DELETE FROM penerimaan_gudang WHERE no_penerimaan = #{no_penerimaan}")
    public int delete(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

    
    @Select("SELECT a.*, b.supplier as nama_supplier, c.pesan, c.nomor as no_cancel, c.tanggal as tgl_cancel,d.gudang, e.tanggal as tanggal_po,f.nama as create_name,g.nama as modified_name "
            + " FROM penerimaan_gudang a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " INNER JOIN gudang d ON a.id_gudang=d.id_gudang  "
            + " LEFT JOIN po e ON a.nomor_po=e.nomor_po"
            + " LEFT JOIN penerimaan_gudang_cancel c ON a.no_penerimaan=c.no_penerimaan "
            + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
            + " WHERE a.no_penerimaan=#{id_lama}")
    public PenerimaanGudang selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    
    @Update("UPDATE penerimaan_gudang  SET "
            + " tgl_penerimaan = #{tgl_penerimaan}, "
            + " id_supplier = #{id_supplier}, "
            + " id_gudang = #{id_gudang}, "
            + " keterangan = #{keterangan}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " referensi = #{referensi}, "
            + " tanggal_referensi = #{tanggal_referensi}, "
            + " nomor_po = #{nomor_po}, "
            + " status = #{status},"
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE no_penerimaan = #{no_penerimaan} ")
    public int update(PenerimaanGudang penerimaanGudang) throws RuntimeException;
    
    @Update("UPDATE penerimaan_gudang  SET "
            + " tgl_penerimaan = #{tgl_penerimaan}, "
            + " referensi = #{referensi}, "
            + " tanggal_referensi = #{tanggal_referensi}, "
            + " nomor_po = #{nomor_po} "
            + " WHERE no_penerimaan = #{no_penerimaan} ")
    public int maintenance(PenerimaanGudang penerimaanGudang) throws RuntimeException;
    
    @Update("UPDATE penerimaan_gudang  SET "
            + " status = #{status}"
            + " WHERE no_penerimaan = #{no_penerimaan} ")
    public int updateStatus(@Param("status") Boolean status, @Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

    
    @Select("SELECT MAX(SUBSTR(a.no_penerimaan,4,3)) "
            + " FROM penerimaan_gudang a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang"
            + " WHERE  EXTRACT(month FROM tgl_penerimaan)=#{bulan}  AND EXTRACT(year FROM tgl_penerimaan)=#{tahun}"
            + " AND b.id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    /////////////////////////////// Detail //////////////////
    
    
    @Insert("INSERT INTO penerimaan_gudang_detail "
            + "(no_penerimaan, urut,  id_barang, qty, diorder,batch) "
            + "VALUES "
            + "(#{no_penerimaan}, #{urut}, #{id_barang}, #{qty}, #{diorder}, #{batch}) ")
    public int insertDetail(PenerimaanGudangDetail penerimaanGudangDetail) throws RuntimeException;

    @Delete("DELETE FROM penerimaan_gudang_detail WHERE no_penerimaan = #{no_penerimaan}")
    public int deleteDetail(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

   
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil "
            + " FROM penerimaan_gudang_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil"
            + " WHERE a.no_penerimaan=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PenerimaanGudangDetail> selectOneperPenerimaanDetail(@Param("id_lama") String id_lama) throws RuntimeException;

    @Select("SELECT SUM(a.qty) FROM penerimaan_gudang_detail a "
            + " INNER JOIN penerimaan_gudang b ON a.no_penerimaan=b.no_penerimaan "
            + " WHERE b.nomor_po=#{nomor_po} "
            + " AND a.id_barang=#{id_barang} "
            + " AND b.status=true")
    public Double jumlahQty(@Param("nomor_po") String nomor_po, @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM penerimaan_gudang WHERE status=false AND nomor_po=#{nomor_po}")
    public Integer cekPo(@Param("nomor_po") String nomor_po);
    
    @SelectProvider(type = ProviderPenerimaanGudang.class, method = "SelectAllPenerimaanGudang")
    public List<PenerimaanGudangDetail> selectAllPenerimaanGudang(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;
}
