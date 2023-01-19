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
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.Popp;
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
public interface MapperPo {

    @SelectProvider(type = ProviderPo.class, method = "SelectAll")
    public List<Po> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("jenis") Character jenis,
            @Param("id_pegawai") String id_pegawai,
            @Param("id_jabatan") Integer id_jabatan
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPo.class, method = "SelectPoGudang")
    public List<Po> selectPoGudang(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai
    ) throws RuntimeException;

    
    @Insert("INSERT INTO po "
            + "(nomor_po, tanggal, id_supplier, kepada, keterangan,  "
            + "total, total_ppn, is_ppn,  total_discount, kode_mata_uang, "
            + "shipto, status, telpon, hp, email, referensi, tanggal_referensi, deliverytime, "
            + "top, persendiskon,jenis,  id_gudang, shiptonumber, no_kontrak, id_customer, is_pph, "
            + "total_pph,id_intruksi_po, jenis_po, purchasing, director, no_pp, checked1,create_date) "
            + "VALUES (#{nomor_po}, #{tanggal}, #{id_supplier}, #{kepada}, #{keterangan},  #{total}, "
            + "#{total_ppn}, #{is_ppn},  #{total_discount}, #{kode_mata_uang},  "
            + "#{shipto}, #{status}, #{telpon}, #{hp}, #{email}, #{referensi}, #{tanggal_referensi}, #{deliverytime}, "
            + "#{top}, #{persendiskon}, #{jenis},  #{id_gudang}, #{shiptonumber}, #{no_kontrak}, #{id_customer}, "
            + "#{is_pph}, #{total_pph}, #{id_intruksi_po}, #{jenis_po}, #{purchasing}, #{director}, #{no_pp}, #{checked1},LOCALTIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Po po) throws RuntimeException;

    
    @Delete("DELETE FROM po WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*, b.supplier as nama_supplier, "
            + " (a.total - a.total_discount) as dpp, (a.total - a.total_discount + a.total_ppn + a.total_pph) as grandtotal, "
            + " c.customer, d.gudang, e.nama as nama_purchasing, f.nama as nama_director, g.no_intruksi_po, h.no_forecast,p.nama as nama_checked1 "
            + " FROM Po a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " LEFT JOIN customer c ON a.id_customer = c.id_kontak "
            + " LEFT JOIN gudang d ON a.id_gudang=d.id_gudang "
            + " INNER JOIN pegawai e ON a.purchasing=e.id_pegawai "
            + " INNER JOIN pegawai f ON a.director=f.id_pegawai "
            + " LEFT JOIN pegawai p ON a.checked1=p.id_pegawai "
            + " LEFT JOIN intruksi_po g ON a.id_intruksi_po=g.id "
            + " LEFT JOIN forecast h ON h.id=g.id_forecast "
            + " WHERE a.id=#{id}")
    public Po selectOneperPo(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*, b.supplier as nama_supplier, (a.total - a.total_discount) as dpp, (a.total - a.total_discount + a.total_ppn) as grandtotal "
            + " FROM po a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " WHERE "
            + " a.jenis_po=#{jenis_po} AND is_approve=true")
    public List<Po> selectPoSupplier(@Param("jenis_po") String jenis_po) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.supplier as nama_supplier, (a.total - a.total_discount) as dpp, (a.total - a.total_discount + a.total_ppn) as grandtotal "
            + " FROM po a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " LEFT JOIN penerimaan c ON a.nomor_po=c.nomor_po "
            + " WHERE "
            + " a.jenis_po='PP' AND is_approve=true")
    public List<Po> selectPoRegulerSupplier() throws RuntimeException;
    
    
    @Select("SELECT  DISTINCT a.nomor_po, a.*,c.supplier as nama_supplier, (a.total - a.total_discount) as dpp, (a.total - a.total_discount + a.total_ppn) as grandtotal "
            + " FROM po a "
            + " INNER JOIN po_detail b on a.id=b.id "
            + " INNER JOIN supplier c ON a.id_supplier=c.id"
            + " WHERE b.diambil !=0 AND b.diambil!=invoice AND a.is_approve=true "
            + " AND a.nomor_po NOT IN (SELECT nomor_po FROM penerimaan WHERE status='D' AND nomor_po IS NOT null)")
    public List<Po> selectPoForPenerimaan() throws RuntimeException;

    
    
    @Select("SELECT DISTINCT ON (a.nomor_po) a.*, b.supplier as nama_supplier, "
            + " (a.total - a.total_discount) as dpp, "
            + " (a.total - a.total_discount + a.total_ppn + a.total_pph) as grandtotal, "
            + " d.gudang "
            + " FROM po a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " INNER JOIN po_detail c ON a.id = c.id "
            + " INNER JOIN gudang d ON d.id_gudang=a.id_gudang "
            + " WHERE  c.sisa > 0 AND status = false AND is_approve=true"
            + " AND d.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai})")
    public List<Po> selectPoSisaSupplier(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    
    @Select("SELECT DISTINCT ON (a.nomor_po) a.*, b.supplier as nama_supplier, "
            + " (a.total - a.total_discount) as dpp, "
            + " (a.total - a.total_discount + a.total_ppn + a.total_pph) as grandtotal, "
            + " d.gudang "
            + " FROM po a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id "
            + " INNER JOIN po_detail c ON a.id = c.id "
            + " INNER JOIN gudang d ON d.id_gudang=a.id_gudang "
            + " WHERE  c.sisa > 0 AND status = false AND is_approve=true")
    public List<Po> selectPoSisaSupplierAll() throws RuntimeException;

    @Update("UPDATE po  SET "
            + " tanggal = #{tanggal}, "
            + " id_supplier = #{id_supplier}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " total = #{total}, "
            + " total_ppn = #{total_ppn}, "
            + " is_ppn = #{is_ppn}, "
            + " total_discount = #{total_discount}, "
            + " kode_mata_uang = #{kode_mata_uang}, "
            + " shipto = #{shipto}, "
            + " telpon = #{telpon}, "
            + " hp = #{hp}, "
            + " email = #{email}, "
            + " referensi = #{referensi}, "
            + " tanggal_referensi = #{tanggal_referensi}, "
            + " deliverytime = #{deliverytime}, "
            + " top = #{top}, "
            + " persendiskon = #{persendiskon}, "
            + " jenis = #{jenis}, "
            + " status = #{status}, "
            + " id_gudang = #{id_gudang}, "
            + " shiptonumber = #{shiptonumber}, "
            + " no_kontrak = #{no_kontrak}, "
            + " id_customer = #{id_customer}, "
            + " is_pph = #{is_pph}, "
            + " total_pph = #{total_pph} "
            + " WHERE id = #{id} ")
    public int updatePo(Po po) throws RuntimeException;

    @Select("SELECT a.*, b.supplier as nama_supplier FROM po a"
            + " INNER JOIN supplier b ON a.id_supplier = b.id "
            + " WHERE a.nomor_po = #{nomor_po} "
            + "")
    public Po selectOne(@Param("nomor_po") String nomor_po) throws RuntimeException;

    
    @Insert("INSERT INTO po_pp "
            + "(po_number, pp_number) "
            + "VALUES (#{po_number}, #{pp_number})")
    public int insertPopp(Popp popp) throws RuntimeException;

    @Delete("DELETE FROM po_pp WHERE id = #{id}")
    public int deletePopp(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT * from po_pp "
            + " WHERE id = #{id} ")
    public List<Popp> selectPopp(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(nomor_po,1,3)) "
            + " FROM po "
            + " WHERE EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;

    @Select("SELECT SUM(a.qty) FROM po_detail a "
            + "INNER JOIN po b ON a.id=b.id "
            + "WHERE b.id_intruksi_po=#{id_intruksi_po} "
            + "AND a.id_barang=#{id_barang} "
            + "AND b.is_approve=true")
    public Double jumlahQty(@Param("id_intruksi_po") Integer id_intruksi_po, @Param("id_barang") String id_barang) throws RuntimeException;

    @Update("UPDATE po SET is_purchasing=#{nilai},purchasing_date=LOCALTIMESTAMP WHERE id=#{id}")
    public int updateStatusPurchasing(@Param("nilai") Boolean nilai, @Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE po SET is_approve=#{nilai},approve_date=LOCALTIMESTAMP WHERE id=#{id}")
    public int updateStatusApprove(@Param("nilai") Boolean nilai, @Param("id") Integer id) throws RuntimeException;
    
    @Update("UPDATE po SET is_checked1=#{nilai},checked1_date=LOCALTIMESTAMP WHERE id=#{id}")
    public int updateStatusChecked(@Param("nilai") Boolean nilai, @Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT count(*)  FROM penerimaan_gudang "
            + " WHERE nomor_po=#{nomor_po} "
            + " AND status is not null ")
    public Integer isPenerimaan(@Param("nomor_po") String nomor_po) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    @Select("SELECT id_pegawai FROM pegawai WHERE id_jabatan_new=150")
    public String getPurchasing() throws RuntimeException;
}
