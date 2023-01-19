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
import net.sra.prime.ultima.db.provider.ProviderCreditNote;
import net.sra.prime.ultima.entity.CreditNote;
import net.sra.prime.ultima.entity.CreditNoteCancel;
import net.sra.prime.ultima.entity.CreditNoteDetail;
import net.sra.prime.ultima.entity.PenjualanDetail;
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
public interface MapperCreditNote {
    
@SelectProvider(type = ProviderCreditNote.class, method = "SelectAll")
    public List<CreditNote> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
    ) throws RuntimeException;

    @Insert("INSERT INTO credit_note "
            + "(nomor, tanggal, id_customer, keterangan, kode_user, total, total_ppn, "
            + "is_ppn, total_discount, "
            + "alamat,  status, dpp, grandtotal,  persendiskon, top,no_po, due_date, no_do, biayalain, no_invoice,diskonrp,id_gudang, id_salesman,jenis) "
            + "VALUES (#{nomor}, #{tanggal}, #{id_customer},  #{keterangan}, #{kode_user}, "
            + "#{total}, #{total_ppn}, #{is_ppn}, #{total_discount}, "
            + " #{alamat},  #{status}, #{dpp}, #{grandtotal},  "
            + "#{persendiskon}, #{top}, #{no_po},#{due_date}, #{no_do}, #{biayalain},#{no_invoice},#{diskonrp},#{id_gudang}, #{id_salesman},#{jenis})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(CreditNote creditNote) throws RuntimeException;

    @Delete("DELETE FROM credit_note WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.customer, e.nomor as no_cancel, e.tanggal as tgl_cancel, e.pesan  "
            + " FROM credit_note a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " LEFT JOIN credit_note_cancel e ON a.nomor=e.no_cn "
            + " WHERE a.id=#{id}")
    public CreditNote selectOne(@Param("id") Integer id) throws RuntimeException;

    
    @Update("UPDATE credit_note  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " keterangan = #{keterangan}, "
            + " total = #{total}, "
            + " total_ppn = #{total_ppn}, "
            + " is_ppn = #{is_ppn}, "
            + " total_discount = #{total_discount}, "
            + " persendiskon = #{persendiskon}, "
            + " dpp = #{dpp}, "
            + " grandtotal = #{grandtotal}, "
            + " top = #{top}, "
            + " no_po = #{no_po}, "
            + " due_date = #{due_date},"
            + " status = #{status}, "
            + " no_do = #{no_do}, "
            + " biayalain = #{biayalain}, "
            + " no_invoice = #{no_invoice},"
            + " diskonrp = #{diskonrp}, "
            + " id_gudang = #{id_gudang},"
            + " id_salesman = #{id_salesman}, "
            + " jenis = #{jenis} "
            + " WHERE id = #{id} ")
    public int update(CreditNote creditNote) throws RuntimeException;

    
    @Select("SELECT a.id_barang, c.nama_barang "
            + " FROM credit_note_detail a "
            + " INNER JOIN credit_note b ON a.id=b.id "
            + " INNER JOIN barang c ON a.id_barang = c.id_barang "
            + " WHERE "
            + " ORDER BY a.urut")
    public List<CreditNoteDetail> selectCreditNoteBarang(@Param("id") Integer id) throws RuntimeException;


       
    @Select("SELECT MAX(SUBSTR(a.nomor,1,4)) "
            + " FROM credit_note a "
            + " WHERE   EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
    
    
    @Update("UPDATE credit_note SET status=#{status} WHERE id=#{id}")
    public int updateStatusAja(@Param("status") Character status, @Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT * FROM credit_note_detail a ")
    public List<PenjualanDetail> selectAllDetail() throws RuntimeException;

    @Insert("INSERT INTO credit_note_detail "
            + "(id, urut, id_barang, qty, harga, total,  diskonrp, diskonpersen, additional_charge) "
            + "VALUES "
            + "(#{id}, #{urut},  #{id_barang}, #{qty}, #{harga}, #{total},  #{diskonrp}, #{diskonpersen}, #{additional_charge}) ")
    public int insertDetail(CreditNoteDetail creditNoteDetail) throws RuntimeException;
    
    @Insert("INSERT INTO credit_note_detail_reguler "
            + "(id, urut, nama_barang, qty, harga, total,  diskonrp, diskonpersen,satuan_besar) "
            + "VALUES "
            + "(#{id}, #{urut},  #{nama_barang}, #{qty}, #{harga}, #{total},  #{diskonrp}, #{diskonpersen},#{satuan_besar}) ")
    public int insertDetailReguler(CreditNoteDetail creditNoteDetail) throws RuntimeException;

   
    @Delete("DELETE FROM credit_note_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;
    
    @Delete("DELETE FROM credit_note_detail_reguler WHERE id = #{id}")
    public int deleteDetailReguler(@Param("id") Integer id) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.nama_barang,  d.satuan_besar, b.isi_satuan "
            + " FROM credit_note_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut")
    public List<CreditNoteDetail> selectOneDetail(@Param("id") Integer  id) throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM credit_note_detail_reguler a "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut")
    public List<CreditNoteDetail> selectOneDetailReguler(@Param("id") Integer  id) throws RuntimeException;

    @Select("SELECT string_agg(no_do, ',') no_do FROM penjualando WHERE no_penjualan=#{no_penjualan}")
    public String selectDo(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    @Update("UPDATE credit_note SET status=#{status} WHERE nomor=#{nomor}")
    public int updateStatus(@Param("status") Character status, @Param("nomor") String nomor) throws RuntimeException;
    
    @Select("SELECT MAX(SUBSTR(a.nomor,1,3)) "
            + " FROM credit_note_cancel a "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMaxCancel(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Insert("INSERT INTO credit_note_cancel (nomor,no_cn,tanggal,pesan,kode_user) "
            + "VALUES (#{nomor},#{no_cn},LOCALTIMESTAMP,#{pesan},#{kode_user})")
    public int insertCancel(CreditNoteCancel creditNoteCancel) throws RuntimeException;
    
    @Delete("DELETE FROM acc_ar_faktur WHERE no_invoice=#{no_invoice}")
    public int deleteAccArFaktur(@Param("no_invoice") String no_invoice) throws RuntimeException;
}
