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
import net.sra.prime.ultima.db.provider.ProviderPenjualan;
import net.sra.prime.ultima.entity.KomisiSales;
import net.sra.prime.ultima.entity.Penjualan;
import net.sra.prime.ultima.entity.PenjualanDetail;
import net.sra.prime.ultima.entity.PenjualanDo;
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
public interface MapperPenjualan {

    @SelectProvider(type = ProviderPenjualan.class, method = "SelectSalesReport")
    public List<PenjualanDetail> selectSalesReport(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("idKantor") String idKantor,
            @Param("idGudang") String idGudang
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectKomisiSales")
    public List<KomisiSales> komisiSales(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("idSales") String idKantor
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectKomisiSalesHpp")
    public List<KomisiSales> komisiSalesHpp(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("idSales") String idKantor
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectKomisiDsr")
    public List<KomisiSales> komisiDsr(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("idSales") String idKantor
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectAll")
    public List<Penjualan> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("jenis") Character jenis
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectAllAdmin")
    public List<Penjualan> selectAllAdmin(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderPenjualan.class, method = "SelectRekapPenjualan")
    public List<PenjualanDetail> selectRekapPenjualan(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("idSales") String idSales,
            @Param("idDepartemen") Integer idDepartemen
    ) throws RuntimeException;
    
    

    @Insert("INSERT INTO penjualan "
            + "(no_penjualan, tanggal, id_customer, kepada, keterangan, kode_user, total, total_ppn, "
            + "is_ppn, id_perusahaan, total_discount, id_term, id_salesman, kode_mata_uang, id_gudang, "
            + "alamat,  status, dpp, grandtotal, bank,  persendiskon, top,referensi, tgl_jatuh_tempo, "
            + "no_penjualan_lama, is_invoice, faktur,biayalain,jenis,cn,create_date,isbank,idbank) "
            + "VALUES "
            + "(#{no_penjualan}, #{tanggal}, #{id_customer}, #{kepada}, #{keterangan}, #{kode_user}, "
            + "#{total}, #{total_ppn}, #{is_ppn}, #{id_perusahaan}, #{total_discount}, #{id_term}, #{id_salesman}, "
            + "#{kode_mata_uang}, #{id_gudang}, #{alamat},  #{status}, #{dpp}, #{grandtotal},  #{bank}, "
            + "#{persendiskon}, #{top}, #{referensi},#{tgl_jatuh_tempo}, #{no_penjualan_lama}, "
            + "#{is_invoice}, #{faktur},#{biayalain},#{jenis},#{cn},LOCALTIMESTAMP,#{isbank},#{idbank})")
    public int insert(Penjualan penjualan) throws RuntimeException;

    @Delete("DELETE FROM penjualan WHERE no_penjualan = #{no_penjualan}")
    public int delete(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    @Delete("DELETE FROM penjualando WHERE no_penjualan = #{no_penjualan}")
    public int deletePenjualanDo(@Param("no_penjualan") String no_penjualan) throws RuntimeException;

    @Select("SELECT a.*, b.customer, tgl_jatuh_tempo - CURRENT_DATE as durasi, c.gudang, d.pesan, d.tanggal as tgl_cancel, "
            + " array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_dos "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pesan_cancel d ON a.no_penjualan=d.nomor "
            + " WHERE a.no_penjualan=#{id_lama}")
    public Penjualan selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, c.due_date "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " LEFT JOIN acc_ar_faktur c ON a.no_penjualan=c.no_invoice"
            + " WHERE a.no_penjualan=#{id}")
    public Penjualan selectOnePenjualan(@Param("id") String id) throws RuntimeException;
    
    @Update("UPDATE penjualan  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " kode_user = #{kode_user}, "
            + " total = #{total}, "
            + " total_ppn = #{total_ppn}, "
            + " is_ppn = #{is_ppn}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " total_discount = #{total_discount}, "
            + " id_salesman = #{id_salesman}, "
            + " kode_mata_uang = #{kode_mata_uang}, "
            + " id_gudang = #{id_gudang}, "
            + " persendiskon = #{persendiskon}, "
            + " dpp = #{dpp}, "
            + " grandtotal = #{grandtotal}, "
            + " bank = #{bank} ,"
            + " top = #{top}, "
            + " referensi = #{referensi}, "
            + " tgl_jatuh_tempo = #{tgl_jatuh_tempo},"
            + " status = #{status}, "
            + " is_invoice = #{is_invoice}, "
            + " biayalain = #{biayalain}, "
            + " faktur = #{faktur}, "
            + " cn = #{cn},"
            + " isbank = #{isbank}, "
            + " idbank = #{idbank} "
            + " WHERE no_penjualan = #{no_penjualan} ")
    public int update(Penjualan penjualan) throws RuntimeException;

    @Select("SELECT a.*, b.customer "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak")
    public List<Penjualan> selectPenjualanCustomer() throws RuntimeException;
    
    @Select("SELECT a.id_barang, c.nama_barang "
            + " FROM penjualan_detail a "
            + " INNER JOIN penjualan b ON b.no_penjualan=a.no_penjualan "
            + " INNER JOIN barang c ON a.id_barang = c.id_barang "
            + " WHERE "
            + " b.id_customer=#{id_customer} "
            + " GROUP BY a.id_barang,c.nama_barang "
            + " ORDER BY c.nama_barang")
    public List<PenjualanDetail> selectPenjualanBarang(@Param("id_customer") String id_customer) throws RuntimeException;

//    public static final String SELECTSOCUST = "SELECT DISTINCT ON (a.no_penjualan) a.*, b.customer "
//            + " FROM penjualan a "
//            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
//            + " INNER JOIN penjualan_detail c ON a.no_penjualan = c.no_penjualan"
//            + " WHERE   a.status = false ";
//
//    @Select(SELECTSOCUST)
//    public List<Penjualan> selectPenjualanSisaCustomer() throws RuntimeException;

    @Select("SELECT * "
            + " FROM penjualando  "
            + " WHERE  no_penjualan = #{no_penjualan} ")
    public List<PenjualanDo> selectPenjualanDo(@Param("no_penjualan") String no_penjualan) throws RuntimeException;

    @Select("SELECT a.*, b.customer from penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " WHERE "
            + " a.id_customer = #{id_customer} "
            + " AND a.grandtotal - a.total_bayar > 0")
    public List<Penjualan> selectSisaHutang(@Param("id_customer") String id_customer) throws RuntimeException;

    @Update("UPDATE penjualan  SET "
            + " total_bayar = #{total_bayar} "
            + " WHERE no_penjualan = #{no_penjualan}")
    public int updateBayar(@Param("no_penjualan") String no_penjualan, @Param("total_bayar") Double total_bayar) throws RuntimeException;
    
    @Update("UPDATE penjualan  SET "
            + " status = #{status} "
            + " WHERE no_penjualan = #{no_penjualan}")
    public int updateStatus(@Param("no_penjualan") String no_penjualan, 
            @Param("status") Character status
            ) throws RuntimeException;
    
    @Update("UPDATE penjualan  SET "
            + " tgl_terimainvoice = #{tgl_terimainvoice},tgl_jatuh_tempo=#{tgl_jatuh_tempo} "
            + " WHERE no_penjualan = #{no_penjualan}")
    public int updateTanggalTerimaInvoice(@Param("no_penjualan") String no_penjualan, 
            @Param("tgl_terimainvoice") Date tgl_terimainvoice,
            @Param("tgl_jatuh_tempo") Date tgl_jatuh_tempo
            ) throws RuntimeException;
    
    @Update("UPDATE acc_ar_faktur  SET "
            + " tgl_terimainvoice = #{tgl_terimainvoice}, due_date=#{due_date} "
            + " WHERE no_invoice = #{no_invoice}")
    public int updateArTanggalTerimaInvoice(@Param("no_invoice") String no_invoice, 
            @Param("tgl_terimainvoice") Date tgl_terimainvoice,
            @Param("due_date") Date due_date
            ) throws RuntimeException;

       
    @Select("SELECT MAX(SUBSTR(a.no_penjualan,1,4)) "
            + " FROM penjualan a "
            + " WHERE   EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
    
    @Update("UPDATE do_tbl  SET "
            + " status = #{status} "
            + " WHERE nomor= #{nomor}")
    public int updateStatusDo(@Param("nomor") String nomor,@Param ("status") Character status) throws RuntimeException;
    
    @Update("UPDATE penjualan SET status=#{status} WHERE no_penjualan=#{no_penjualan}")
    public int updateStatusAja(@Param("status") Character status, @Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer, tgl_jatuh_tempo - CURRENT_DATE as durasi,  d.pesan, d.tanggal as tgl_cancel  "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " LEFT JOIN pesan_cancel d ON a.no_penjualan=d.nomor "
            + " WHERE a.no_penjualan=#{no_penjualan}")
    public Penjualan selectOneReguler(@Param("no_penjualan") String no_pejualan) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, c.due_date as tgl_jatuh_tempo, d.nama as salesman "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN acc_ar_faktur c ON a.no_penjualan=c.no_invoice "
            + " LEFT JOIN pegawai d ON a.id_salesman = d.id_pegawai "
            + " WHERE a.status='P'")
    public List<Penjualan> selectPenjualanPosting() throws RuntimeException;
    
    
    @Select("SELECT a.*, b.customer, "
            + " array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_dos"
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " WHERE "
            + " a.id_customer=#{id_customer} "
            + " ORDER BY tanggal DESC"
            )
    public List<Penjualan> selectAllPenjualanCustomer(@Param("id_customer") String id_customer) throws RuntimeException;
    
    
    @Select("SELECT a.*,b.customer "
            + " FROM penjualan a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " WHERE "
            + " EXTRACT(year FROM a.tanggal)=#{tahun} AND EXTRACT(month FROM a.tanggal)=#{bulan} "
            + " AND a.status='A' ORDER BY a.tanggal" )
    public List<Penjualan> selectPenjualanperBulan(@Param("tahun") Integer tahun,
            @Param("bulan") Integer bulan) throws RuntimeException;
}
