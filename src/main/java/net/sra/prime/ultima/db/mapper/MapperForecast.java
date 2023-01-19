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
import net.sra.prime.ultima.entity.Forecast;
import net.sra.prime.ultima.entity.ForecastDetail;
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
public interface MapperForecast {

    @SelectProvider(type = ProviderForecast.class, method = "SelectAll")
    public List<Forecast> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_region") String id_region
    ) throws RuntimeException;

    @SelectProvider(type = ProviderForecast.class, method = "SelectAllFporIp")
    public List<Forecast> selectAllForIP(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("statusip") Character statusip
    ) throws RuntimeException;

    @Insert("INSERT INTO forecast "
            + "(no_forecast, id_gudang, tanggal, triwulan, keterangan,id_region,status,create_by) "
            + "VALUES (#{no_forecast}, #{id_gudang}, #{tanggal},  #{triwulan}, #{keterangan}, #{id_region},#{status},#{create_by})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Forecast forecast) throws RuntimeException;

   
    @Delete("DELETE FROM forecast WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

  
    @Select("SELECT a.*, b.gudang, c.pesan, c.tanggal as tgl_cancel, d.nama as user_cancel  "
            + " FROM forecast a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " LEFT JOIN pesan_cancel c ON a.no_forecast = c.nomor "
            + " LEFT JOIN pegawai d ON c.kode_user=d.id_pegawai"
            + " WHERE a.id=#{id} ")
    public Forecast selectOne(@Param("id") Integer id) throws RuntimeException;

    
    @Update("UPDATE forecast  SET "
            + " id_gudang = #{id_gudang}, "
            + " tanggal = #{tanggal}, "
            + " triwulan = #{triwulan}, "
            + " keterangan = #{keterangan}, "
            + " id_region = #{id_region},"
            + " status = #{status} "
            + " WHERE id = #{id} ")
    public int update(Forecast forecast) throws RuntimeException;

    //////// Forecast Detail //////////////////////////////////
   

    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil,e.qty as openpo "
            + " FROM forecast_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil=c.id_satuan_kecil "
            + " LEFT JOIN intruksi_po d ON a.id=d.id_forecast AND d.status !='D' AND d.status != 'X' "
            + " LEFT JOIN intruksi_po_detail e ON d.id=e.id AND a.id_barang=e.id_barang"
            + " WHERE a.id=#{id}  "
            + " ORDER BY a.urut")
    public List<ForecastDetail> selectAllDetail(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil,e.qty as openpo "
            + " FROM forecast_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil=c.id_satuan_kecil "
            + " LEFT JOIN intruksi_po d ON a.id=d.id_forecast  "
            + " LEFT JOIN intruksi_po_detail e ON d.id=e.id AND a.id_barang=e.id_barang"
            + " WHERE a.id=#{id}  "
            + " ORDER BY a.urut")
    public List<ForecastDetail> selectAllByOpenPo(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil,e.qty as openpo "
            + " FROM forecast_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil=c.id_satuan_kecil "
            + " LEFT JOIN intruksi_po d ON a.id=d.id_forecast AND d.status !='D' AND d.status != 'X' "
            + " LEFT JOIN intruksi_po_detail e ON d.id=e.id AND a.id_barang=e.id_barang"
            + " WHERE a.id=#{id}  "
            + " ORDER BY a.urut")
    public List<ForecastDetail> selectAllDetailReport(@Param("id") Integer id) throws RuntimeException;

    
    @Insert("INSERT INTO forecast_detail "
            + "(id, "
            + " urut, "
            + " id_barang, "
            + " bulan1,"
            + " bulan2,"
            + " bulan3,"
            + " total) "
            + "VALUES "
            + " (#{id}, "
            + " #{urut}, "
            + " #{id_barang}, "
            + " #{bulan1}, "
            + " #{bulan2}, "
            + " #{bulan3},"
            + " #{total}) ")
    public int insertDetail(ForecastDetail forecastDetail) throws RuntimeException;

   
    @Delete("DELETE FROM forecast_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT MAX(SUBSTR(no_forecast,1,3)) "
            + " FROM forecast "
            + " WHERE date_part('year', tanggal)=date_part('year', CURRENT_DATE) "
            + " AND id_region = #{id_region}")
    public String SelectMax(@Param("id_region") String id_region) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM intruksi_po WHERE id_forecast=#{id_forecast} AND status != 'X' ")
    public Integer countIP(@Param("id_forecast") Integer id_forecast) throws RuntimeException;
    
    @Update("UPDATE forecast set status=#{status} WHERE id=#{id}")
    public int updateStatus(@Param("status") Character status,@Param("id") Integer id) throws RuntimeException;

    ///////////////// Laporan //////////////////////
    
    @SelectProvider(type = ProviderForecast.class, method = "SelectForecastToPo")
    public List<ForecastDetail> selectForcastToPo(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_region") String id_region,
            @Param("id_gudang") String id_gudang
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderForecast.class, method = "SelectAllReport")
    public List<ForecastDetail> selectAllReport(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;
}
