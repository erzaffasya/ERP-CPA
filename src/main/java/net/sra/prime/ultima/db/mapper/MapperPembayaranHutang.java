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
import net.sra.prime.ultima.db.provider.ProviderPembayaranHutang;
import net.sra.prime.ultima.entity.PembayaranHutang;
import net.sra.prime.ultima.entity.PembayaranHutangDetail;
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
public interface MapperPembayaranHutang {

    @SelectProvider(type = ProviderPembayaranHutang.class, method = "SelectAll")
    public List<PembayaranHutang> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
            ) throws RuntimeException;

    @Insert("INSERT INTO pembayaran_hutang "
            + "(no_pembayaran_hutang, tanggal, id_supplier, keterangan, kode_user, total_tagihan, total_discount, total_bayar, total_denda, is_app, account_bayar, id_perusahaan, id_kantor, status) "
            + "VALUES (#{no_pembayaran_hutang}, #{tanggal}, #{id_supplier},  #{keterangan}, #{kode_user}, #{total_tagihan}, #{total_discount}, #{total_bayar}, #{total_denda}, #{is_app}, #{account_bayar}, #{id_perusahaan}, #{id_kantor}, #{status})")
    public int insert(PembayaranHutang pembayaranHutang) throws RuntimeException;

    @Delete("DELETE FROM pembayaran_hutang WHERE no_pembayaran_hutang = #{no_pembayaran_hutang}")
    public int delete(@Param("no_pembayaran_hutang") String no_pembayaran_hutang) throws RuntimeException;

    @Select("SELECT a.*, b.supplier  "
            + " FROM pembayaran_hutang a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id"
            + " WHERE a.no_pembayaran_hutang=#{id_lama} ")
    public PembayaranHutang selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE pembayaran_hutang  SET "
            + " tanggal = #{tanggal}, "
            + " id_supplier = #{id_supplier}, "
            + " keterangan = #{keterangan}, "
            + " kode_user = #{kode_user}, "
            + " total_tagihan = #{total_tagihan}, "
            + " total_discount = #{total_discount}, "
            + " total_bayar = #{total_bayar}, "
            + " total_denda = #{total_denda}, "
            + " is_app = #{is_app}, "
            + " account_bayar = #{account_bayar}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " id_kantor = #{id_kantor},"
            + " status = #{status} "
            + " WHERE no_pembayaran_hutang = #{no_pembayaran_hutang} ")
    public int update(PembayaranHutang pembayaranHutang) throws RuntimeException;

    //////// Pembayaran Hutang Detail //////////////////////////////////
    @Select("SELECT a.*, b.reff as referensi "
            + " FROM pembayaran_hutang_detail a "
            + " LEFT JOIN acc_ap_faktur b ON a.no_penerimaan = b.ap_number "
            + " WHERE a.nomor_pembayaran_hutang=#{nomor_pembayaran_hutang}  "
            + " ORDER BY a.urut")
    public List<PembayaranHutangDetail> selectAllDetail(@Param("nomor_pembayaran_hutang") String no_pembayaran_hutang) throws RuntimeException;

    @Insert("INSERT INTO pembayaran_hutang_detail "
            + "(nomor_pembayaran_hutang, "
            + " urut, "
            + " no_penerimaan, "
            + " jumlah_tagihan, "
            + " jumlah_discount, "
            + " jumlah_bayar ) "
            + "VALUES "
            + " (#{nomor_pembayaran_hutang}, "
            + " #{urut}, "
            + " #{no_penerimaan}, "
            + " #{jumlah_tagihan}, "
            + " #{jumlah_discount}, "
            + " #{jumlah_bayar} )")
    public int insertDetail(PembayaranHutangDetail pembayaranHutangDetail) throws RuntimeException;

    @Delete("DELETE FROM pembayaran_hutang_detail WHERE nomor_pembayaran_hutang = #{no_pembayaran_hutang} ")
    public int deleteDetail(@Param("no_pembayaran_hutang") String no_pembayaran_hutang) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(no_pembayaran_hutang,1,3)) "
            + " FROM pembayaran_hutang "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    @Select("SELECT SUM(jumlah_bayar) FROM pembayaran_hutang_detail "
            + " WHERE no_penerimaan = #{no_penerimaan}")
    public Double selectSum(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

}
