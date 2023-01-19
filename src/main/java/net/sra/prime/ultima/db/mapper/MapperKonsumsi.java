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
import net.sra.prime.ultima.db.provider.ProviderKonsumsi;
import net.sra.prime.ultima.entity.Konsumsi;
import net.sra.prime.ultima.entity.KonsumsiDetail;
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
public interface MapperKonsumsi {

    
    @SelectProvider(type = ProviderKonsumsi.class, method = "SelectAll")
    public List<Konsumsi> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang
    ) throws RuntimeException;

    @Insert("INSERT INTO konsumsi "
            + "(id_gudang, periode_awal, periode_akhir, tanggal, no_reff, diinput, status) "
            + "VALUES (#{id_gudang}, #{periode_awal}, #{periode_akhir},  #{tanggal}, #{no_reff}, #{diinput}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Konsumsi konsumsi) throws RuntimeException;

    @Delete("DELETE FROM konsumsi WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*, b.gudang FROM konsumsi a  "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " WHERE a.id=#{id} ")
    public Konsumsi selectOne(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE konsumsi  SET "
            + " tanggal = #{tanggal}, "
            + " periode_awal = #{periode_awal}, "
            + " periode_akhir = #{periode_akhir}, "
            + " no_reff = #{no_reff}, "
            + " status = #{status} "
            + " WHERE id = #{id} ")
    public int update(Konsumsi konsumsi) throws RuntimeException;

    //////// Konsumsi  Detail //////////////////////////////////
    @Select("SELECT a.*, b.nama_barang"
            + " FROM konsumsi_detail a "
            + " LEFT JOIN barang b ON a.id_barang = b.barang "
            + " WHERE a.id=#{id}  "
            + " ORDER BY a.urut")
    public List<KonsumsiDetail> selectAllDetail(@Param("id") Integer id) throws RuntimeException;

    @Insert("INSERT INTO konsumsi_detail "
            + "(id_barang, saldo_awal, pemakaian, deviasi, saldo_akhir, ditagih, keterangan, urut) "
            + "VALUES "
            + " (#{id_barang}, #{saldo_awal}, #{pemakaian}, #{deviasi}, #{saldo_akhir}, #{ditagih}, #{keterangan}, #{urut})")
    public int insertDetail(KonsumsiDetail konsumsiDetail) throws RuntimeException;

    @Delete("DELETE FROM konsumsi_detail WHERE id = #{id} ")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;

    
    
}
