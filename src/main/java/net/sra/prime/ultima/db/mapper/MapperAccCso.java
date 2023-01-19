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
import net.sra.prime.ultima.entity.AccCso;
import net.sra.prime.ultima.entity.AccCsoDetil;
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
public interface MapperAccCso {

    @SelectProvider(type = ProviderAccCso.class, method = "SelectAll")
    public List<AccCso> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;

   
    @Insert("INSERT INTO acc_cso "
            + "(acc_cso_number, tanggal, id_jenis_transaksi, uraian, total, id_kantor, id_account, id_pegawai, status, kepada) "
            + "VALUES (#{acc_cso_number}, #{tanggal}, #{id_jenis_transaksi},  #{uraian}, #{total}, #{id_kantor}, #{id_account}, #{id_pegawai}, #{status}, #{kepada})")
    public int insert(AccCso accCso) throws RuntimeException;

    @Delete("DELETE FROM acc_cso WHERE acc_cso_number = #{acc_cso_number}")
    public int delete(@Param("acc_cso_number") String acc_cso_number) throws RuntimeException;

    @Select("SELECT a.*, b.nama as kantor FROM acc_cso a  "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang "
            + " WHERE a.acc_cso_number=#{acc_cso_number} ")
    public AccCso selectOne(@Param("acc_cso_number") String acc_cso_number) throws RuntimeException;

    
    @Update("UPDATE acc_cso  SET "
            + " tanggal = #{tanggal}, "
            + " id_jenis_transaksi = #{id_jenis_transaksi}, "
            + " uraian = #{uraian}, "
            + " total = #{total}, "
            + " id_kantor = #{id_kantor}, "
            + " id_account = #{id_account}, "
            + " status = #{status}, "
            + " kepada = #{kepada}"
            + " WHERE acc_cso_number = #{acc_cso_number} ")
    public int update(AccCso accCso) throws RuntimeException;

    //////// Pembayaran Hutang Detail //////////////////////////////////
    @Select("SELECT a.*, b.account"
            + " FROM acc_cso_detil a "
            + " LEFT JOIN account b ON a.id_account_detail = b.id_account "
            + " WHERE a.acc_cso_number=#{acc_cso_number}  "
            + " ORDER BY a.urut")
    public List<AccCsoDetil> selectAllDetail(@Param("acc_cso_number") String acc_cso_number) throws RuntimeException;

    
    @Insert("INSERT INTO acc_cso_detil "
            + "(acc_cso_number, "
            + " urut, "
            + " uraian, "
            + " jumlah, "
            + " jenis, "
            + " id_account_detail ) "
            + "VALUES "
            + " (#{acc_cso_number}, "
            + " #{urut}, "
            + " #{uraian}, "
            + " #{jumlah}, "
            + " #{jenis}, "
            + " #{id_account_detail} )")
    public int insertDetail(AccCsoDetil accCsoDetil) throws RuntimeException;

    @Delete("DELETE FROM acc_cso_detil WHERE acc_cso_number = #{acc_cso_number} ")
    public int deleteDetail(@Param("acc_cso_number") String acc_cso_number) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(acc_cso_number,1,3)) "
            + " FROM acc_cso "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

}
