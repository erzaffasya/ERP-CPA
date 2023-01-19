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
import net.sra.prime.ultima.entity.Packinglist;
import net.sra.prime.ultima.entity.PackinglistDetail;
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
public interface MapperPackinglist {

    @SelectProvider(type = ProviderPackinglist.class, method = "SelectAll")
    public List<Packinglist> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("statusIp") Character statusIp
    ) throws RuntimeException;

    
    @Insert("INSERT INTO packinglist "
            + "(nomor, tanggal, id_customer, kepada, keterangan, kode_user, id_perusahaan, id_salesman,  id_gudang, alamat, status,  no_so, deliverypoint, telpon,create_date) "
            + "VALUES (#{nomor}, #{tanggal}, #{id_customer}, #{kepada}, #{keterangan}, #{kode_user}, #{id_perusahaan}, #{id_salesman}, #{id_gudang}, #{alamat}, #{status},#{no_so},#{deliverypoint},#{telpon},LOCALTIMESTAMP)")
    public int insert(Packinglist packinglist) throws RuntimeException;

    
    @Delete("DELETE FROM packinglist WHERE nomor = #{nomor}")
    public int delete(@Param("nomor") String nomor) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, c.referensi, c.telpon as tlp, c.kepada, c.modified_date as tanggal_so, "
            + " c.alamat as alamatso, d.gudang, e.nomor as no_cancel, e.tanggal as tgl_cancel, e.pesan,f.nama as create_by,g.nama as modified_name  "
            + " FROM packinglist a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN so c ON c.nomor = a.no_so "
            + " INNER JOIN gudang d ON a.id_gudang=d.id_gudang "
            + " LEFT JOIN packinglist_cancel e ON a.nomor=e.no_pl "
            + " LEFT JOIN pegawai f ON a.kode_user=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
            + " WHERE a.nomor=#{id_lama}")
    public Packinglist selectOne(@Param("id_lama") String id_lama) throws RuntimeException;
    
    
    @Update("UPDATE packinglist  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " id_salesman = #{id_salesman}, "
            + " id_gudang = #{id_gudang}, "
            + " no_so = #{no_so}, "
            + " status = #{status},"
            + " deliverypoint = #{deliverypoint}, "
            + " telpon = #{telpon}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP"
            + " WHERE nomor = #{nomor} ")
    public int update(Packinglist packinglist) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, c.referensi, d.gudang "
            + " FROM packinglist a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN so c ON c.nomor = a.no_so "
            + " INNER JOIN gudang d ON a.id_gudang=d.id_gudang "
            + " WHERE "
            + " a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai}) "
            + " AND a.status='S' "
            + " ORDER BY a.nomor, a.tanggal DESC")
    public List<Packinglist> selectPackinglistCustomer(
            @Param("id_pegawai") String id_gudang ) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.nomor,4,3)) "
            + " FROM packinglist a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang"
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND b.id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    
    @Select("SELECT * FROM packinglist WHERE no_so=#{no_so} AND status='D'")
    public Packinglist cekSOterpakai(@Param("no_so") String no_so)  throws RuntimeException;
    
    
    @Update("UPDATE packinglist  SET "
            + " status = #{status} "
            + " WHERE nomor = #{nomor} ")
    public int updateStatus(@Param("status") Character status,@Param("nomor") String nomor) throws RuntimeException;

    @SelectProvider(type = ProviderPackinglist.class, method = "SelectAllPackinglist")
    public List<PackinglistDetail> selectAllPackinglist(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
}
