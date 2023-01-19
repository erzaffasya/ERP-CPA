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
import net.sra.prime.ultima.db.provider.ProviderPembayaranPiutang;
import net.sra.prime.ultima.entity.InsentifCustomer;
import net.sra.prime.ultima.entity.PembayaranPiutang;
import net.sra.prime.ultima.entity.PembayaranPiutangDetail;
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
public interface MapperPembayaranPiutang {

    @SelectProvider(type = ProviderPembayaranPiutang.class, method = "SelectAll")
    public List<PembayaranPiutang> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
            ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPembayaranPiutang.class, method = "PendapatanMarketing")
    public List<PembayaranPiutangDetail> pendapatanMarketing(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_salesman") String id_salesman,
            @Param("id_customer") String id_customer,
            @Param("idDepartemen") Integer idDepartemen
            ) throws RuntimeException;

   
    @Insert("INSERT INTO pembayaran_piutang "
            + "(no_pembayaran_piutang, tanggal, id_customer, keterangan, kode_user, total_tagihan, total_discount, total_bayar, total_denda, is_app, account_bayar, id_perusahaan, id_kantor, status) "
            + "VALUES "
            + "(#{no_pembayaran_piutang}, #{tanggal}, #{id_customer},  #{keterangan}, #{kode_user}, #{total_tagihan}, #{total_discount}, #{total_bayar}, #{total_denda}, #{is_app}, #{account_bayar}, "
            + "#{id_perusahaan}, #{id_kantor}, #{status})")
    public int insert(PembayaranPiutang pembayaranPiutang) throws RuntimeException;

    
    @Delete("DELETE FROM pembayaran_piutang WHERE no_pembayaran_piutang = #{no_pembayaran_piutang}")
    public int delete(@Param("no_pembayaran_piutang") String no_pembayaran_piutang) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer  "
            + " FROM pembayaran_piutang a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak"
            + " WHERE a.no_pembayaran_piutang=#{id_lama} ")
    public PembayaranPiutang selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE pembayaran_piutang  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
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
            + " WHERE no_pembayaran_piutang = #{no_pembayaran_piutang} ")
    public int update(PembayaranPiutang pembayaranPiutang) throws RuntimeException;

    //////// Pembayaran Piutang Detail //////////////////////////////////
    @Select("SELECT *"
            + " FROM pembayaran_piutang_detail a "
            + " WHERE a.nomor=#{nomor}  "
            + " ORDER BY a.urut")
    public List<PembayaranPiutangDetail> selectAllDetail(@Param("nomor") String nomor) throws RuntimeException;

    

    @Insert("INSERT INTO pembayaran_piutang_detail "
            + "(nomor, "
            + " urut, "
            + " no_penjualan, "
            + " jumlah_tagihan, "
            + " jumlah_discount, "
            + " jumlah_bayar, "
            + " no_invoice,"
            + " dpp,"
            + " ppn,"
            + " keterangan) "
            + "VALUES "
            + " (#{nomor}, "
            + " #{urut}, "
            + " #{no_penjualan}, "
            + " #{jumlah_tagihan}, "
            + " #{jumlah_discount}, "
            + " #{jumlah_bayar}, "
            + " #{no_invoice},"
            + " #{dpp},"
            + " #{ppn},"
            + " #{keterangan})")
    public int insertDetail(PembayaranPiutangDetail pembayaranPiutangDetail) throws RuntimeException;

    @Delete("DELETE FROM pembayaran_piutang_detail WHERE nomor = #{nomor} ")
    public int deleteDetail(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(no_pembayaran_piutang,1,3)) "
            + " FROM pembayaran_piutang "
            + " WHERE EXTRACT(month FROM tanggal)=#{bulan} AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    @Select("SELECT SUM(jumlah_bayar) FROM pembayaran_piutang_detail "
            + " WHERE no_penjualan = #{no_penjualan}")
    public Double selectSum(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    // INsentif Customer
    @Insert("INSERT INTO insentif_customer "
            + "(nomor, tanggal, dsr, createby, createdate, note, status) "
            + "VALUES "
            + "(#{nomor}, #{tanggal}, #{dsr},  #{createby}, LOCALTIMESTAMP, #{note}, #{status} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertInsentifCustomer(InsentifCustomer insentifCustomer) throws RuntimeException;
    
    @Insert("INSERT INTO insentif_customer_detail "
            + "(id, "
            + " urut, "
            + " insentif, "
            + " no_invoice, "
            + " keterangan)"
            + "VALUES "
            + " (#{id}, "
            + " #{urut}, "
            + " #{insentif}, "
            + " #{no_invoice}, "
            + " #{ket})")
    public int insertDetailInsentifCustomer(PembayaranPiutangDetail pembayaranPiutangDetail) throws RuntimeException;
    
    @Select("SELECT MAX(SUBSTR(no_proposal,1,3)) "
            + " FROM acc_proposal_ap "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMaxInsentif( @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;


}
