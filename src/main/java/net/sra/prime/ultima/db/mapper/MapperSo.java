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
import net.sra.prime.ultima.entity.So;
import net.sra.prime.ultima.entity.SoDetail;
import net.sra.prime.ultima.entity.SoPersetujuan;
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
public interface MapperSo {

    @SelectProvider(type = ProviderSo.class, method = "SelectAll")
    public List<So> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status
    ) throws RuntimeException;

    @SelectProvider(type = ProviderSo.class, method = "SelectAllGudang")
    public List<So> selectAllGudang(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status
    ) throws RuntimeException;

    @SelectProvider(type = ProviderSo.class, method = "SelectAllDetail")
    public List<SoDetail> selectAllDetail(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status
    ) throws RuntimeException;

    @SelectProvider(type = ProviderSo.class, method = "SelectAllPengajuan")
    public List<So> selectAllPengajuan(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai
    ) throws RuntimeException;

    @SelectProvider(type = ProviderSo.class, method = "SelectMonitoringSo")
    public List<So> MonitoringSo(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("status") Character status
    ) throws RuntimeException;

    @SelectProvider(type = ProviderSo.class, method = "SelectSoReport")
    public List<SoDetail> SoReport(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    

    @SelectProvider(type = ProviderSo.class, method = "SelectAllConsignment")
    public List<So> selectAllConsignment(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;

    @Insert("INSERT INTO so "
            + "(nomor, tanggal, id_customer, kepada, keterangan, kode_user, total, total_ppn, is_ppn, "
            + "total_discount, id_term, id_salesman, kode_mata_uang, id_gudang, alamat, "
            + "tipe_diskon, nilai_harga, status, dpp, grandtotal, telpon, hp, email, certificate, bank, "
            + "deliverypoint, pov, persendiskon, syarat, top, no_penawaran, referensi, tgl_ref, jenis, revisi_penawaran,sobefore,create_date) "
            + "VALUES (#{nomor}, #{tanggal}, #{id_customer}, #{kepada}, #{keterangan}, #{kode_user}, #{total}, #{total_ppn}, #{is_ppn}, "
            + "#{total_discount}, #{id_term}, #{id_salesman}, #{kode_mata_uang}, #{id_gudang}, #{alamat}, #{tipe_diskon}, "
            + "#{nilai_harga}, #{status}, #{dpp}, #{grandtotal}, #{telpon}, #{hp}, #{email}, #{certificate}, #{bank}, #{deliverypoint}, #{pov}, "
            + "#{persendiskon}, #{syarat}, #{top}, #{no_penawaran}, #{referensi}, #{tgl_ref}, #{jenis}, #{revisi_penawaran},#{sobefore},LOCALTIMESTAMP)")
    public int insert(So so) throws RuntimeException;

    @Delete("DELETE FROM so WHERE nomor = #{nomor}")
    public int delete(@Param("nomor") String nomor) throws RuntimeException;
    
    @Delete("DELETE FROM so_persetujuan WHERE nomor = #{nomor}")
    public int deletePersetujuan(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT a.*, b.customer, c.gudang, d.nama as user, f.pesan, f.tanggal as tgl_cancel, g.nama as salesman  "
            + " FROM so a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " LEFT JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
            + " LEFT JOIN pesan_cancel f ON a.nomor=f.nomor "
            + " LEFT JOIN pegawai g ON a.id_salesman = g.id_pegawai "
            + " WHERE a.nomor=#{nomor}")
    public So selectOne(@Param("nomor") String nomor) throws RuntimeException;

    @Update("UPDATE so  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " total = #{total}, "
            + " total_ppn = #{total_ppn}, "
            + " is_ppn = #{is_ppn}, "
            + " total_discount = #{total_discount}, "
            + " id_salesman = #{id_salesman}, "
            + " kode_mata_uang = #{kode_mata_uang}, "
            + " id_gudang = #{id_gudang}, "
            + " telpon = #{telpon}, "
            + " hp = #{hp}, "
            + " email = #{email}, "
            + " top = #{top}, "
            + " persendiskon = #{persendiskon}, "
            + " dpp = #{dpp}, "
            + " grandtotal = #{grandtotal}, "
            + " deliverypoint = #{deliverypoint}, "
            + " pov = #{pov}, "
            + " certificate = #{certificate}, "
            + " bank = #{bank}, "
            + " syarat = #{syarat}, "
            + " no_penawaran = #{no_penawaran}, "
            + " referensi = #{referensi}, "
            + " tgl_ref = #{tgl_ref} ,"
            + " jenis = #{jenis}, "
            + " revisi_penawaran = #{revisi_penawaran}, "
            + " status = #{status}, "
            + " sobefore = #{sobefore} "
            + " WHERE nomor = #{nomor} ")

    public int update(So so) throws RuntimeException;
    
    
    @Update("UPDATE so  SET "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE nomor = #{nomor} ")

    public int updateSend(So so) throws RuntimeException;


    @Update("UPDATE packinglist SET id_salesman=#{id_salesman} WHERE no_so=#{no_so}")
    public int updateSalesPackinglist(@Param("id_salesman") String id_salesman,
            @Param("no_so") String no_so) throws RuntimeException;

    @Update("UPDATE do_tbl SET id_salesman=#{id_salesman} WHERE no_pl IN (SELECT nomor FROM packinglist WHERE no_so=#{no_so})")
    public int updateSalesDo(@Param("id_salesman") String id_salesman,
            @Param("no_so") String no_so) throws RuntimeException;

    @Update("UPDATE penjualan SET id_salesman=#{id_salesman} "
            + " WHERE no_penjualan IN (SELECT no_penjualan FROM penjualando WHERE no_do IN (SELECT nomor FROM do_tbl WHERE  no_pl IN (SELECT nomor FROM packinglist WHERE no_so=#{no_so})))")
    public int updateSalesPenjualan(@Param("id_salesman") String id_salesman,
            @Param("no_so") String no_so) throws RuntimeException;
    
    @Update("UPDATE acc_ar_faktur SET id_salesman=#{id_salesman} "
            + " WHERE no_invoice IN (SELECT no_penjualan FROM penjualando WHERE no_do IN (SELECT nomor FROM do_tbl WHERE  no_pl IN (SELECT nomor FROM packinglist WHERE no_so=#{no_so})))")
    public int updateSalesAr(@Param("id_salesman") String id_salesman,
            @Param("no_so") String no_so) throws RuntimeException;

    @Select("SELECT a.*, b.customer "
            + " FROM so a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak")
    public List<So> selectSoCustomer() throws RuntimeException;
    
    
    @Select("SELECT a.*, b.customer "
            + " FROM so a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " WHERE a.status='R'")
    public List<So> selectSoCancel() throws RuntimeException;
    

    @Select("SELECT DISTINCT ON (a.nomor) a.*, b.customer "
            + " FROM so a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN so_detail c ON a.nomor = c.no_so"
            + " WHERE  c.sisa > 0 AND a.status = false ")
    public List<So> selectSoSisaCustomer() throws RuntimeException;

    @Select("SELECT a.*, c.customer FROM so a "
            + " INNER JOIN so_persetujuan b ON a.nomor=b.nomor AND b.approve is null and b.id_pegawai = #{id_pegawai} "
            + " INNER JOIN customer c ON a.id_customer=c.id_kontak"
            + " WHERE a.status='D' AND (b.urut=1  "
            + " OR (b.urut=2 AND true=(select approve from so_persetujuan where nomor=a.nomor and urut=1) AND  (select approve from so_persetujuan where nomor=a.nomor and urut=3) is null) "
            + " OR (b.urut=3 AND true=(select approve from so_persetujuan where nomor=a.nomor and urut=1) AND  (select approve from so_persetujuan where nomor=a.nomor and urut=2) is null) "
            + " )")
    public List<So> selectSoNotifikasi(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(nomor,1,3)) "
            + " FROM so "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    @Update("UPDATE so set status=#{status} WHERE nomor=#{nomor}")
    public int updateStatus(@Param("nomor") String nomor, @Param("status") Character status) throws RuntimeException;

    @Select("SELECT COUNT(*) FROM packinglist WHERE status='D' AND no_so=#{no_so}")
    public Integer cekPackingist(@Param("no_so") String no_so);

    @Select("SELECT COUNT(*) FROM packinglist WHERE (status='A' OR status='S' OR status='P') AND no_so=#{no_so}")
    public Integer cekPackingistComplete(@Param("no_so") String no_so);

    @Select("SELECT COUNT(*) FROM so WHERE status='D' AND no_penawaran=#{no_penawaran}")
    public Integer cekPenawaran(@Param("no_penawaran") String no_penawaran);

    @Select("SELECT COALESCE(SUM(total-bayar),0) FROM acc_ar_faktur WHERE customer_code=#{customer_code}")
    public Double sisaHutangCustomer(@Param("customer_code") String customer_code) throws RuntimeException;

    @Select("SELECT COALESCE(SUM(total-bayar),0) FROM acc_ar_faktur WHERE customer_code=#{customer_code} AND due_date <= #{date}")
    public Double sisaHutangCustomerbyTop(@Param("customer_code") String customer_code,
            @Param("date") Date date) throws RuntimeException;

    @Insert("INSERT INTO so_persetujuan (nomor,id_pegawai,urut,approve,jenis,tipe)"
            + " VALUES (#{nomor},#{id_pegawai},#{urut},#{approve},#{jenis},#{tipe})")
    public int insertPersetujuan(SoPersetujuan soPersetujuan) throws RuntimeException;

    @Select("SELECT * FROM so_persetujuan WHERE nomor=#{nomor} LIMIT 1")
    public SoPersetujuan selectOneSoPersetujuan(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT COUNT(*) FROM acc_ar_faktur WHERE customer_code=#{customer_code}")
    public Integer cekArCount(@Param("customer_code") String customer_code) throws RuntimeException;

    @Select("SELECT * FROM so_persetujuan WHERE nomor=#{nomor} AND id_pegawai=#{id_pegawai}")
    public SoPersetujuan selectOneSoPersetujuanByPegawai(@Param("nomor") String nomor,
            @Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT * FROM so_persetujuan WHERE nomor=#{nomor} AND jenis='A' AND approve is not null ORDER BY urut DESC LIMIT 1")
    public SoPersetujuan selectApproveSoPersetujuan(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT a.*, b.nama FROM so_persetujuan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE a.nomor=#{nomor} ORDER by a.urut")
    public List<SoPersetujuan> selectListPersetujuan(@Param("nomor") String nomor) throws RuntimeException;

    @Update("UPDATE so_persetujuan set approve=#{approve}, date=LOCALTIMESTAMP,note=#{note} WHERE nomor=#{nomor} AND id_pegawai=#{id_pegawai}")
    public void updatePersetujuan(@Param("approve") Boolean approve,
            @Param("nomor") String nomor,
            @Param("id_pegawai") String id_pegawai,
            @Param("note") String note) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderSo.class, method = "OutstandingSo")
    public List<SoDetail> outstandingSo(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("id_customer") String status
    ) throws RuntimeException;
}
