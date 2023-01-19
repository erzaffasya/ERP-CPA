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
import net.sra.prime.ultima.entity.Penawaran;
import net.sra.prime.ultima.entity.PenawaranDetail;
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
public interface MapperPenawaran {

    @SelectProvider(type = ProviderPenawaran.class, method = "SelectAll")
    public List<Penawaran> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("statuspenawaran") Character statuspenawaran,
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderPenawaran.class, method = "SelectAllMaintenance")
    public List<Penawaran> selectAllMaintenance(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("statuspenawaran") Character statuspenawaran
    ) throws RuntimeException;
    
    
    @Select("SELECT a.*, c.nama as salesman, d.nama as createby"
            + " FROM penawaran a  "
            + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
            + " INNER JOIN pegawai d ON a.kode_user=d.id_pegawai "
            + " WHERE a.id_customer is null AND a.statussend='A' "
            + " AND revisi = (select max(revisi) FROM penawaran e WHERE a.nomor=e.nomor)"
            + " ORDER BY  a.tanggal DESC, a.nomor DESC")
    public List<Penawaran> selectPenawaranNewCustomer() throws RuntimeException;
    
    @Select("SELECT a.*, c.nama as salesman, d.nama as createby"
            + " FROM penawaran a  "
            + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
            + " INNER JOIN pegawai d ON a.kode_user=d.id_pegawai "
            + " WHERE a.id_customer is null AND a.statussend='A' "
            + " AND revisi = (select max(revisi) FROM penawaran e WHERE a.nomor=e.nomor) "
            + " AND a.id_salesman IN (SELECT id_sales FROM hr.sales_admin WHERE id_admin=#{id_admin}) "
            + " ORDER BY  a.tanggal DESC, a.nomor DESC")
    public List<Penawaran> selectPenawaranNewCustomerBySalesAdmin(@Param("id_admin") String id_admin) throws RuntimeException;

   
    @Insert("INSERT INTO penawaran "
            + "(nomor, tanggal, id_customer, kepada, keterangan, kode_user, total, total_ppn, is_ppn,  total_discount, "
            + "id_term, id_salesman, kode_mata_uang, id_gudang, alamat, tipe_diskon, nilai_harga, status, dpp, grandtotal, telpon, hp, "
            + "email, certificate, bank, deliverypoint, pov, persendiskon, syarat, top, revisi, jenis_bank, id_kantor, dsm,statussend,carabayar, newcustomer) "
            + "VALUES "
            + "(#{nomor}, #{tanggal}, #{id_customer}, #{kepada}, #{keterangan}, #{kode_user}, #{total}, #{total_ppn}, #{is_ppn}, "
            + "#{total_discount}, #{id_term}, #{id_salesman}, #{kode_mata_uang}, #{id_gudang}, #{alamat}, #{tipe_diskon}, "
            + "#{nilai_harga}, #{status}, #{dpp}, #{grandtotal}, #{telpon}, #{hp}, #{email}, #{certificate}, #{bank}, #{deliverypoint}, #{pov}, "
            + "#{persendiskon}, #{syarat}, #{top}, #{revisi}, #{jenis_bank}, #{id_kantor}, #{dsm},#{statussend},#{carabayar}, #{newcustomer})")
    public int insert(Penawaran penawaran) throws RuntimeException;

    @Delete("DELETE FROM penawaran WHERE nomor = #{nomor} AND revisi=#{revisi}")
    public int delete(@Param("nomor") String nomor, @Param("revisi") Integer revisi) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer  "
            + " FROM penawaran a "
            + " LEFT JOIN customer b ON a.id_customer=b.id_kontak"
            + " WHERE a.nomor=#{id_lama} AND a.revisi = #{revisi}")
    public Penawaran selectOne(@Param("id_lama") String id_lama, @Param("revisi") Integer revisi) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer  "
            + " FROM penawaran a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak"
            + " WHERE a.nomor=#{id_lama} "
            + " AND revisi = (select max(revisi) FROM penawaran c WHERE a.nomor=c.nomor)")
    public Penawaran selectOnePenawaran(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE penawaran  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " kode_user = #{kode_user}, "
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
            + " syarat = #{syarat},"
            + " revisi = #{revisi}, "
            + " jenis_bank = #{jenis_bank}, "
            + " id_kantor = #{id_kantor}, "
            + " status = #{status}, "
            + " statussend = #{statussend}, "
            + " carabayar=#{carabayar}, "
            + " newcustomer=#{newcustomer} "
            + " WHERE nomor = #{nomor} AND revisi = #{revisi} ")
    public int update(Penawaran penawaran) throws RuntimeException;
    
    @Update("UPDATE penawaran  SET "
            + " id_customer = #{id_customer} "
            + " WHERE nomor = #{nomor} AND revisi = #{revisi} ")
    public int updateCustomer(@Param("id_customer") String id_customer, @Param("nomor") String nomor, @Param("revisi") Integer revisi) throws RuntimeException;

    @Update("UPDATE penawaran  SET "
            + " status = #{status} "
            + " WHERE nomor = #{nomor} AND revisi = #{revisi} ")
    public int updateStatus(@Param("status") Boolean status, @Param("nomor") String nomor, @Param("revisi") Integer revisi) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer "
            + " FROM penawaran a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " WHERE revisi = (select max(revisi) FROM penawaran c WHERE a.nomor=c.nomor) "
            + " AND a.statussend='A'")
    public List<Penawaran> selectPenawaranCustomer() throws RuntimeException;
    
    @Select("SELECT a.*, b.customer,c.nama as salesman "
            + " FROM penawaran a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak AND a.id_salesman=b.id_sales "
            + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
            + " WHERE revisi = (select max(revisi) FROM penawaran c WHERE a.nomor=c.nomor) "
            + " AND a.statussend='A' AND b.id_sales IN (SELECT id_sales FROM hr.sales_admin WHERE id_admin=#{id_admin})")
    public List<Penawaran> selectPenawaranBySalesAdmin(@Param("id_admin") String id_admin) throws RuntimeException;

    @Select("SELECT DISTINCT ON (a.nomor) a.*, b.customer "
            + " FROM penawaran a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN penawaran_detail c ON a.nomor = c.no_penawaran"
            + " WHERE a.id_customer=#{id_customer} AND c.sisa > 0 AND statussend = false ")
    public List<Penawaran> selectPenawaranSisaCustomer(@Param("id_customer") String id_customer) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(nomor,4,3)) "
            + " FROM penawaran "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    @Select("SELECT harga FROM pricelist_detail WHERE id_gudang=#{id_gudang} AND id_barang=#{id_barang}")
    public Double hargaBarang(@Param("id_gudang") String id_gudang, @Param("id_barang") String id_barang) throws RuntimeException;
    
    ////////////////// Laporan ///////////////////////
    
    @SelectProvider(type = ProviderPenawaran.class, method = "SelectAllPenawaranInvoice")
    public List<PenawaranDetail> selectAllPenawaranInvoice(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;

}
