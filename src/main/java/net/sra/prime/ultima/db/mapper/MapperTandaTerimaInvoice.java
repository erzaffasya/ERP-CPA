/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import net.sra.prime.ultima.db.provider.ProviderTandaTerimaInvoice;
import net.sra.prime.ultima.entity.TandaTerimaInvoice;
import net.sra.prime.ultima.entity.TandaTerimaInvoiceDetail;
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
public interface MapperTandaTerimaInvoice {

    @SelectProvider(type = ProviderTandaTerimaInvoice.class, method = "SelectAll")
    public List<TandaTerimaInvoice> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
    ) throws RuntimeException;
    
    
    
    @Insert("INSERT INTO tanda_terima_invoice "
            + " (nomor,id_customer,create_by,create_date,status,tanggal,total,tgl_kirim,courier,resi,tgl_terima,penerima) "
            + " VALUES "
            + " (#{nomor},#{id_customer},#{create_by},LOCALTIMESTAMP,#{status},#{tanggal},#{total},#{tgl_kirim},#{courier},#{resi},#{tgl_terima},#{penerima}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(TandaTerimaInvoice tandaTerimaInvoice)  throws RuntimeException;

 
    @Delete("DELETE FROM tanda_terima_invoice WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, d.nama as create_name "
            + " FROM tanda_terima_invoice a "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN pegawai d ON a.create_by = d.id_pegawai "
            + " WHERE a.id=#{id}")
    public TandaTerimaInvoice selectOne(@Param("id") Integer id)  throws RuntimeException;
    
    @Update("UPDATE tanda_terima_invoice  SET "
            + " nomor = #{nomor}, "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " status = #{status}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP, "
            + " total = #{total}, "
            + " tgl_kirim = #{tgl_kirim}, "
            + " courier = #{courier}, "
            + " resi = #{resi}, "
            + " tgl_terima = #{tgl_terima}, "
            + " penerima = #{penerima} "
            + " WHERE "
            + " id = #{id}")
    public int update(TandaTerimaInvoice tandaTerimaInvoice)  throws RuntimeException;
    
    
    /**
     * table kpi_pegawai_detail
     */

    @Select("SELECT a.*,b.faktur as nomor_faktur,b.referensi as no_po, b.grandtotal as nilai, "
            + " array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_do "
            + " FROM tanda_terima_invoice_detail a "
            + " INNER JOIN penjualan b ON a.no_penjualan = b.no_penjualan "
            + " WHERE a.id=#{id} "
            + " ORDER BY a.urut ")
    public List<TandaTerimaInvoiceDetail> selectAllDetail(@Param("id") Integer id)  throws RuntimeException;
    
    
    @Insert("INSERT INTO tanda_terima_invoice_detail "
            + " (id,no_penjualan,urut) "
            + " VALUES "
            + " (#{id},#{no_penjualan},#{urut})")
    public int insertDetail(TandaTerimaInvoiceDetail tandaTerimaInvoiceDetail)  throws RuntimeException;

 
    @Delete("DELETE FROM tanda_terima_invoice_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*  "
            + " FROM tanda_terima_invoice_detail a "
            + " INNER JOIN penjualan b ON a.no_penjualan = b.penjualan "
            + " ORDER BY a.urut ")
    public TandaTerimaInvoiceDetail selectOneDetail(@Param("id") Integer id, 
            @Param("no_penjualan") String no_penjualan)throws RuntimeException;
    
    @Select("SELECT MAX(SUBSTR(a.nomor,1,4)) "
            + " FROM tanda_terima_invoice a "
            + " WHERE  EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
    
    @SelectProvider(type = ProviderTandaTerimaInvoice.class, method = "SelectAllDetail")
    public List<TandaTerimaInvoiceDetail> selectAllDetailReport(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
    ) throws RuntimeException;

}
