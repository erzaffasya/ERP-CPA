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
import net.sra.prime.ultima.entity.AccApFaktur;
import net.sra.prime.ultima.entity.Account;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAccApFaktur {

    @Select("SELECT a.*, b.supplier "
            + " FROM acc_ap_faktur a "
            + " LEFT JOIN supplier b ON a.vendor_code = b.id "
            + " WHERE a.due_date < CURRENT_DATE + #{batas} "
            + " AND a.total - a.bayar > 0"
            + " ORDER BY a.due_date DESC")
    public List<AccApFaktur> selectAll(@Param("batas") Integer batas) throws RuntimeException;

    @Insert("INSERT INTO acc_ap_faktur "
            + "(ap_number, ap_date, vendor_code, amount, due_date, notes, reff, id_perusahaan, po_number, status, bayar, "
            + "ppn, pph, total, tgl_invoice, receive_date, top,materai) "
            + "VALUES "
            + "(#{ap_number}, #{ap_date}, #{vendor_code}, #{amount}, #{due_date}, #{notes},  #{reff}, #{id_perusahaan}, #{po_number}, #{status}, #{bayar}, "
            + "#{ppn}, #{pph}, #{total}, #{tgl_invoice}, #{receive_date}, #{top},#{materai}) ")
    public int insert(AccApFaktur accApFaktur) throws RuntimeException;

    
    @Update("UPDATE acc_ap_faktur SET "
            + " ap_date = #{ap_date}, "
            + " due_date = #{due_date}, "
            + " notes = #{notes}, "
            + " reff = #{reff}, "
            + " tgl_invoice = #{tgl_invoice}, "
            + " receive_date = #{receive_date}, "
            + " top = #{top} "
            + " WHERE ap_number = #{ap_number}")
    public int update(AccApFaktur accApFaktur) throws RuntimeException;
    
    
    @Update("UPDATE acc_ap_faktur SET "
            + " bayar = bayar + #{bayar} "
            + " WHERE ap_number = #{ap_number}")
    public int updateBayar(@Param("bayar") Double bayar, @Param("ap_number") String ap_number) throws RuntimeException;

    @Delete("DELETE FROM account WHERE id_account = #{id_account}")
    public int delete(@Param("id_account") Integer id_account) throws RuntimeException;

    
    @Select("SELECT * FROM account WHERE level = #{level} ORDER BY id_account")
    public List<Account> selectLevel(@Param("level") Integer level) throws RuntimeException;

    
    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " WHERE a.id_account = #{id_account}")
    public Account selectOne(@Param("id_account") Integer id_account) throws RuntimeException;

    
    @Select("SELECT max(id_account) FROM account WHERE level = #{level} AND parent = #{parent}")
    public Integer selectMaxId(@Param("level") Integer level, @Param("parent") Integer parent) throws RuntimeException;

    
    @Select("SELECT max(id_account) FROM account WHERE level = #{level}")
    public Integer selectMaxIdLevel1(@Param("level") Integer level) throws RuntimeException;

    
    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " WHERE "
            + " a.tipe_account = true "
            + " AND a.id_account > #{batas_bawah} "
            + " AND a.id_account < #{batas_atas} "
            + " ORDER BY a.id_account")
    public List<Account> selectByCategory(@Param("batas_bawah") Integer batas_bawah, @Param("batas_atas") Integer batas_atas) throws RuntimeException;

    
    @Select("SELECT account FROM account WHERE id_account = #{id_account}")
    public String selectAccount(@Param("id_account") Integer id_account) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total-a.bayar) as sumtotal,a.vendor_code, b.supplier "
            + " FROM acc_ap_faktur a "
            + " LEFT JOIN supplier b ON a.vendor_code = b.id "
            + " WHERE  "
            + " a.total - a.bayar > 0 "
            + " GROUP BY a.vendor_code, b.supplier "
            + " ORDER BY b.supplier")
    public List<AccApFaktur> selectAllSum() throws RuntimeException;
    
    @Select("SELECT SUM(a.total-a.bayar) as sumtotal "
            + " FROM acc_ap_faktur a "
            + " WHERE  "
            + " a.total - a.bayar > 0 "
            + " AND a.vendor_code=#{vendor_code} "
            + " AND CURRENT_DATE - a.receive_date >= #{awal} "
            + " AND CURRENT_DATE - a.receive_date <= #{akhir} "
            + " GROUP BY a.vendor_code ")
    public Double selectOneSum(@Param("awal") Integer awal, @Param("akhir") Integer akhir, @Param("vendor_code") String vendor_code) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total-a.bayar) as sumtotal "
            + " FROM acc_ap_faktur a "
            + " WHERE  "
            + " a.total - a.bayar > 0 "
            + " AND a.vendor_code=#{vendor_code} "
            + " AND CURRENT_DATE - a.due_date >= #{awal} "
            + " AND CURRENT_DATE - a.due_date <= #{akhir} "
            + " GROUP BY a.vendor_code ")
    public Double selectOneSumDueDate(@Param("awal") Integer awal, @Param("akhir") Integer akhir, @Param("vendor_code") String vendor_code) throws RuntimeException;
    
    @Select("SELECT SUM(a.total-a.bayar) as sumtotal "
            + " FROM acc_ap_faktur a "
            + " WHERE  "
            + " a.total - a.bayar > 0 "
            + " AND a.vendor_code=#{vendor_code} "
            + " AND CURRENT_DATE - a.due_date <= #{awal} "
            + " GROUP BY a.vendor_code ")
    public Double selectOneSumDue0(@Param("awal") Integer awal, @Param("vendor_code") String vendor_code) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer "
            + " FROM acc_ap_faktur a "
            + " LEFT JOIN customer b ON a.vendor_code = b.id_kontak "
            + " WHERE  a.vendor_code=#{vendor_code} AND "
            + " a.total - a.bayar > 0 "
            + " ORDER BY a.ap_date")
    public List<AccApFaktur> selectAllInvoice(@Param("vendor_code") String vendor_code) throws RuntimeException;
    
    @Select("SELECT a.total-a.bayar as sumtotal "
            + " FROM acc_ap_faktur a "
            + " WHERE  "
            + " a.total - a.bayar > 0 "
            + " AND a.vendor_code=#{vendor_code} "
            + " AND CURRENT_DATE - a.receive_date >= #{awal} "
            + " AND CURRENT_DATE - a.receive_date <= #{akhir} "
            + " AND a.ap_number=#{ap_number}")
    public Double selectOneInvoiceSupplier(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("vendor_code") String vendor_code,
            @Param("ap_number") String ap_number) throws RuntimeException;
    
    @Select("SELECT CURRENT_DATE - ap_date FROM acc_ap_faktur WHERE ap_number=#{ap_number}")
    public Integer selectUmurInvoice(
            @Param("ap_number") String ap_number) throws RuntimeException;
    
           
    @Select("SELECT a.*, ab.supplier, "
            + " #{akhir} - a.tgl_invoice as umur_invoice, #{akhir} - a.due_date as umur_overdue "
            + " FROM acc_ap_faktur a "
            + " INNER JOIN supplier ab ON a.vendor_code=ab.id "
            + " WHERE a.receive_date <= #{akhir} "
            + " AND  a.total > a.bayar - (SELECT COALESCE(SUM(jumlah_bayar),0) FROM pembayaran_hutang_detail b "
            + " INNER JOIN pembayaran_hutang c ON c.no_pembayaran_hutang=b.nomor_pembayaran_hutang "
            + " WHERE "
            + " a.ap_number=b.no_penerimaan "
            + " AND c.tanggal >= #{awal} AND c.tanggal <= #{akhir} AND c.status='A') AND a.due_date is not null")
    public List<AccApFaktur> selectCutOff(@Param("awal") Date awal,
            @Param("akhir") Date akhir) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.jumlah_bayar),0) "
            + " FROM pembayaran_hutang_detail a "
            + " INNER JOIN pembayaran_hutang b ON a.nomor_pembayaran_hutang=b.no_pembayaran_hutang "
            + " WHERE "
            + " b.tanggal < #{tanggal} "
            + " AND a.nomor_pembayaran_hutang=#{nomor}")
    public Double selectSumBayar(@Param("nomor") String nomor,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(jumlah_bayar),0) from pembayaran_hutang_detail b "
            + " INNER JOIN pembayaran_hutang c On c.no_pembayaran_hutang=b.nomor_pembayaran_hutang "
            + " WHERE "
            + " b.no_penerimaan = #{no_penerimaan}"
            + " AND EXTRACT(month FROM c.tanggal) = #{bulan}  "
            + " AND EXTRACT(year FROM c.tanggal) = #{tahun} "
            + " AND c.status='A' ")
    public Double selectSumPayment(@Param("no_penerimaan") String no_penerimaan,
            @Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun) throws RuntimeException;
}
