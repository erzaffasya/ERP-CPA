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
import net.sra.prime.ultima.entity.DoDetail;
import net.sra.prime.ultima.entity.Dotbl;
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
public interface MapperDotbl {
      
    @SelectProvider(type = ProviderDo.class, method = "SelectAll")
    public List<Dotbl> selectAllDo(
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Character status,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("statusdo") Character statusdo
            ) throws RuntimeException;
    
    @SelectProvider(type = ProviderDo.class, method = "SelectAllXls")
    public List<DoDetail> selectAllDoXls(
            @Param("id_pegawai") String id_pegawai,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("id_gudang") String id_gudang
            ) throws RuntimeException;
    
    @SelectProvider(type = ProviderDo.class, method = "SelectAllReport")
    public List<DoDetail> selectAllReport(
            @Param("id_pegawai") String id_pegawai,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("id_gudang") String id_gudang,
            @Param("id_salesman") String id_salesman,
            @Param("id_customer") String id_customer,
            @Param("id_barang") String id_barang
            ) throws RuntimeException;
    
    @SelectProvider(type = ProviderDo.class, method = "SelectAllMaintenance")
    public List<Dotbl> selectAllMaintenance(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("statusdo") Character statusdo
            ) throws RuntimeException;
   
    @Insert("INSERT INTO do_tbl "
            + " (nomor, tanggal, id_customer, kepada, keterangan,id_salesman,  id_gudang, alamat, status,"
            + " no_pl, telpon, sipb, lic_plate, driver, hp, transporter_time, tgl_transporter,transporter,leadtime,id_pegawai_log,"
            + " istransporter,idtransporter,create_date,origin,destination) "
            + " VALUES "
            + " (#{nomor}, #{tanggal}, #{id_customer}, #{kepada}, #{keterangan},#{id_salesman}, #{id_gudang}, #{alamat}, #{status},"
            + " #{no_pl}, #{telpon}, #{sipb}, #{lic_plate}, #{driver}, #{hp}, #{transporter_time}, #{tgl_transporter},#{transporter},#{leadtime},#{id_pegawai_log},"
            + " #{istransporter},#{idtransporter},LOCALTIMESTAMP,#{origin},#{destination})")
    public int insert(Dotbl dotbl) throws RuntimeException;
    
    
    @Delete("DELETE FROM do_tbl WHERE nomor = #{nomor}")
    public int delete(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT a.*, b.customer, d.referensi, b.top,d.persendiskon, d.is_ppn, c.no_so, e.gudang, f.pesan, f.tanggal as tgl_cancel,g.nama as create_name  "
            + " FROM do_tbl a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN packinglist c ON c.nomor = a.no_pl "
            + " INNER JOIN so d ON c.no_so = d.nomor "
            + " INNER JOIN gudang e ON a.id_gudang=e.id_gudang "
            + " LEFT JOIN pesan_cancel f ON a.nomor=f.nomor "
            + " LEFT JOIN pegawai g ON a.id_pegawai_log = g.id_pegawai "
            + " WHERE a.nomor=#{id_lama}")
    public Dotbl selectOne(@Param("id_lama") String id_lama) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM do_tbl WHERE no_pl=#{no_pl} AND status !='C'")
    public Integer selectCountPl(@Param("no_pl") String no_pl) throws RuntimeException;
    

    @Update("UPDATE do_tbl  SET "
            + " tanggal = #{tanggal}, "
            + " id_customer = #{id_customer}, "
            + " kepada = #{kepada}, "
            + " keterangan = #{keterangan}, "
            + " id_salesman = #{id_salesman}, "
            + " id_gudang = #{id_gudang}, "
            + " no_pl = #{no_pl}, "
            + " alamat = #{alamat}, "
            + " telpon = #{telpon}, "
            + " status = #{status}, "
            + " sipb = #{sipb}, "
            + " driver = #{driver}, "
            + " lic_plate = #{lic_plate}, "
            + " tgl_transporter = #{tgl_transporter}, "
            + " transporter_time = #{transporter_time}, "
            + " hp = #{hp}, "
            + " received_name=#{received_name}, "
            + " received_date=#{received_date}, "
            + " transporter = #{transporter}, "
            + " leadtime = #{leadtime}, "
            + " istransporter = #{istransporter}, "
            + " idtransporter = #{idtransporter}, "
            + " modified_date = LOCALTIMESTAMP, "
            + " origin = #{origin}, "
            + " destination = #{destination} "
            + " WHERE nomor = #{nomor} ")
    public int update(Dotbl dotbl) throws RuntimeException;
    
    
    @Update("UPDATE do_tbl SET status=#{status} WHERE nomor=#{nomor}")
    public int updateStatus(@Param("status") Character status, @Param("nomor") String nomor) throws RuntimeException;
    
    @Update("UPDATE do_tbl  SET "
            + " received_date = #{received_date}, "
            + " received_name = #{received_name}, "
            + " status = #{status},"
            + " shippingcosts = #{shippingcosts}, "
            + " return_date = #{return_date}, "
            + " received_date_posting = LOCALTIMESTAMP, "
            + " received_posting = #{received_posting}, "
            + " tgl_sampai = #{tgl_sampai} "
            + " WHERE nomor = #{nomor}")
    public int updatereceived(Dotbl dotbl) throws RuntimeException;

    
    @Select("SELECT a.*, b.customer, d.referensi, d.grandtotal, c.no_so "
            + " FROM do_tbl a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN packinglist c ON a.no_pl = c.nomor "
            + " INNER JOIN so d ON c.no_so = d.nomor "
            + " WHERE a.status = 'R' "
            + " ORDER BY a.tanggal DESC, a.nomor DESC")
    public List<Dotbl> selectDoCustomer() throws RuntimeException;

    
    @Select("SELECT DISTINCT ON (a.nomor) a.*, b.customer "
            + " FROM do_tbl a "
            + " INNER JOIN customer b ON a.id_customer=b.id_kontak "
            + " INNER JOIN do_detail c ON a.nomor = c.no_pl"
            + " WHERE a.id_customer=#{id_customer} AND c.sisa > 0 AND status = false ")
    public List<Dotbl> selectDoSisaCustomer(@Param("id_customer") String id_customer) throws RuntimeException;
    
    
    @Select("SELECT MAX(SUBSTR(a.nomor,1,3)) "
            + " FROM do_tbl a "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Select("SELECT nama FROM pegawai WHERE id_pegawai=#{id_pegawai}")
    public String selectPegawaiName(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT lower(origin) FROM leadtime GROUP BY lower(origin) ORDER BY lower(origin)")
    public List<String> selectAllOrigin() throws RuntimeException;
    
    @Select("SELECT destination FROM leadtime WHERE lower(origin)=lower(#{origin}) ORDER BY destination")
    public List<String> selectAllDestination(@Param("origin") String origin) throws RuntimeException;
    
    @Select("SELECT total FROM leadtime WHERE lower(origin)=lower(#{origin}) AND lower(destination)=lower(#{destination}) ")
    public Double getTotalLeadtime(@Param("origin") String origin,@Param("destination") String destination) throws RuntimeException;
}
