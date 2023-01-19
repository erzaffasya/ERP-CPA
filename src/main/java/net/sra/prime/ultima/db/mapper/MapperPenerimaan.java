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
import net.sra.prime.ultima.db.provider.ProviderPenerimaan;
import net.sra.prime.ultima.entity.Penerimaan;
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
public interface MapperPenerimaan {

   @SelectProvider(type = ProviderPenerimaan.class, method = "SelectAll")
    public List<Penerimaan> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir
    ) throws RuntimeException;

    
    @Insert("INSERT INTO penerimaan "
            + "(no_penerimaan, tgl_penerimaan, id_supplier, id_gudang, referensi, tgl_jatuh_tempo, keterangan, kode_user, "
            + "total, is_app, total_bayar, is_invoice, tgl_bayar, total_discount, id_term, kode_mata_uang, alamat, "
            + "kode_diskon, id_nilai_harga, total_ppn, potongan_pajak, status, persendiskon, is_ppn, dpp, grandtotal, "
            + "top, nomor_po, tanggal_referensi, tgl_pengiriman, is_lunas, is_pph, total_pph, "
            + "biaya_transportasi, biaya_asuransi, biaya_bongkar_muat, biaya_bongkar_muat_eksternal, id_kantor, jenis, id_perusahaan, no_faktur_pajak,ppnpersen,biayalain,no_pp) "
            + "VALUES (#{no_penerimaan}, #{tgl_penerimaan}, #{id_supplier}, #{id_gudang}, #{referensi}, #{tgl_jatuh_tempo}, #{keterangan}, #{kode_user}, "
            + " #{total}, #{is_app}, #{total_bayar}, #{is_invoice}, #{tgl_bayar}, #{total_discount}, #{id_term}, #{kode_mata_uang}, #{alamat}, #{kode_diskon}, "
            + " #{id_nilai_harga}, #{total_ppn}, #{potongan_pajak}, #{status}, #{persendiskon}, #{is_ppn}, #{dpp}, #{grandtotal}, #{top}, #{nomor_po}, "
            + " #{tanggal_referensi}, #{tgl_pengiriman}, #{is_lunas}, #{is_pph}, #{total_pph},"
            + " #{biaya_transportasi}, #{biaya_asuransi}, #{biaya_bongkar_muat}, #{biaya_bongkar_muat_eksternal}, #{id_kantor}, #{jenis}, #{id_perusahaan}, "
            + " #{no_faktur_pajak},#{ppnpersen},#{biayalain}, #{no_pp})")
    public int insert(Penerimaan penerimaan) throws RuntimeException;

    @Delete("DELETE FROM penerimaan WHERE no_penerimaan = #{no_penerimaan}")
    public int delete(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

    @Select("SELECT a.*, b.supplier as nama_supplier"
            + " FROM Penerimaan a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id"
            + " WHERE a.no_penerimaan=#{id_lama}")
    public Penerimaan selectOneperPenerimaan(@Param("id_lama") String id_lama) throws RuntimeException;

    
    @Update("UPDATE penerimaan  SET "
            + " tgl_penerimaan = #{tgl_penerimaan}, "
            + " id_supplier = #{id_supplier}, "
            + " id_gudang = #{id_gudang}, "
            + " tgl_jatuh_tempo = #{tgl_jatuh_tempo}, "
            + " keterangan = #{keterangan}, "
            + " kode_user = #{kode_user}, "
            + " total = #{total}, "
            + " is_app = #{is_app}, "
            + " total_bayar = #{total_bayar}, "
            + " is_invoice = #{is_invoice}, "
            + " tgl_bayar = #{tgl_bayar}, "
            + " total_ppn = #{total_ppn}, "
            + " id_term = #{id_term}, "
            + " is_ppn = #{is_ppn}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " total_discount = #{total_discount}, "
            + " kode_mata_uang = #{kode_mata_uang}, "
            + " referensi = #{referensi}, "
            + " tanggal_referensi = #{tanggal_referensi}, "
            + " top = #{top}, "
            + " dpp = #{dpp}, "
            + " grandtotal = #{grandtotal}, "
            + " nomor_po = #{nomor_po}, "
            + " tgl_pengiriman = #{tgl_pengiriman}, "
            + " is_lunas = #{is_lunas}, "
            + " persendiskon = #{persendiskon}, "
            + " is_pph = #{is_pph}, "
            + " total_pph = #{total_pph}, "
            + " biaya_transportasi = #{biaya_transportasi}, "
            + " biaya_asuransi = #{biaya_asuransi}, "
            + " biaya_bongkar_muat = #{biaya_bongkar_muat}, "
            + " biaya_bongkar_muat_eksternal = #{biaya_bongkar_muat_eksternal}, "
            + " status = #{status}, "
            + " no_faktur_pajak = #{no_faktur_pajak}, "
            + " ppnpersen = #{ppnpersen}, "
            + " biayalain = #{biayalain}"
            + " WHERE no_penerimaan = #{no_penerimaan} ")
    public int updatePenerimaan(Penerimaan penerimaan) throws RuntimeException;

    @Update("UPDATE penerimaan  SET "
            + " total_bayar = #{total_bayar} "
            + " WHERE no_penerimaan = #{no_penerimaan}")
    public int updateBayar(@Param("no_penerimaan") String no_penerimaan, @Param("total_bayar") Double total_bayar) throws RuntimeException;

    @Select("SELECT a.*, b.supplier from penerimaan a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " WHERE "
            + " a.id_supplier = #{id_supplier}")
    public List<Penerimaan> selectBySupplier(@Param("id_supplier") String id_supplier) throws RuntimeException;

    @Select("SELECT a.*, b.supplier from penerimaan a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " WHERE "
            + " a.id_supplier = #{id_supplier} "
            + " AND a.grandtotal - a.total_bayar > 0")
    public List<Penerimaan> selectSisaHutang(@Param("id_supplier") String id_supplier) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(no_penerimaan,1,3)) "
            + " FROM penerimaan "
            + " WHERE  EXTRACT(month FROM tgl_penerimaan)=#{bulan}  AND EXTRACT(year FROM tgl_penerimaan)=#{tahun}"
            + " AND id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Select("SELECT a.no_faktur_pajak, b.supplier as nama_supplier,b.npwp as keterangan,a.referensi,a.tanggal_referensi,a.dpp,a.total_ppn "
            + " FROM penerimaan a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " WHERE "
            + " a.tgl_penerimaan >= #{awal} AND a.tgl_penerimaan <= #{akhir} "
            + " AND a.status='A' "
            )
    public List<Penerimaan> selectAllPajak(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir) throws RuntimeException;

}
