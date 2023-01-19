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
import net.sra.prime.ultima.entity.AccKas;
import net.sra.prime.ultima.entity.AccKasDetil;
import net.sra.prime.ultima.entity.AccPettyCash;
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
public interface MapperAccKas {

    @SelectProvider(type = ProviderAccKas.class, method = "SelectAll")
    public List<AccKas> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor,
            @Param("st") Character st
    ) throws RuntimeException;

    
    @Select("SELECT b.*, a.id_jenis_transaksi, a.tanggal "
            + " FROM acc_kas a "
            + " INNER JOIN acc_kas_detil b ON a.nomor=b.nomor "
            + " WHERE a.id > #{id} AND a.id_kantor=#{id_kantor} "
            + " ORDER BY a.tanggal,a.id")
    public List<AccKasDetil> selectAllKas(@Param("id") Integer id, @Param("id_kantor") String id_kantor) throws RuntimeException;
    
    
    public static final String INSERT = "INSERT INTO acc_kas "
            + "(nomor, tanggal, id_jenis_transaksi, uraian, total, id_kantor, id_account, id_pegawai, status, kepada) "
            + "VALUES (#{nomor}, #{tanggal}, #{id_jenis_transaksi},  #{uraian}, #{total}, #{id_kantor}, #{id_account}, #{id_pegawai}, #{status}, #{kepada})";

    @Insert(INSERT)
    public int insert(AccKas accKas) throws RuntimeException;

    @Delete("DELETE FROM acc_kas WHERE nomor = #{nomor}")
    public int delete(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT a.*, b.nama as kantor FROM acc_kas a  "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang "
            + " WHERE a.nomor=#{nomor} ")
    public AccKas selectOne(@Param("nomor") String nomor) throws RuntimeException;

    
    @Update("UPDATE acc_kas  SET "
            + " tanggal = #{tanggal}, "
            + " id_jenis_transaksi = #{id_jenis_transaksi}, "
            + " uraian = #{uraian}, "
            + " total = #{total}, "
            + " id_kantor = #{id_kantor}, "
            + " id_account = #{id_account}, "
            + " status = #{status}, "
            + " kepada = #{kepada} "
            + " WHERE nomor = #{nomor} ")
    public int update(AccKas accKas) throws RuntimeException;

    //////// Pembayaran Hutang Detail //////////////////////////////////
    
    @Select("SELECT a.*, b.account"
            + " FROM acc_kas_detil a "
            + " LEFT JOIN account b ON a.id_account_detail = b.id_account "
            + " WHERE a.nomor=#{nomor}  "
            + " ORDER BY a.urut")
    public List<AccKasDetil> selectAllDetail(@Param("nomor") String nomor) throws RuntimeException;

    @Insert("INSERT INTO acc_kas_detil "
            + "(nomor, "
            + " urut, "
            + " uraian, "
            + " jumlah, "
            + " id_account_detail ) "
            + "VALUES "
            + " (#{nomor}, "
            + " #{urut}, "
            + " #{uraian}, "
            + " #{jumlah}, "
            + " #{id_account_detail} )")
    public int insertDetail(AccKasDetil accKasDetil) throws RuntimeException;

    @Delete("DELETE FROM acc_kas_detil WHERE nomor = #{nomor} ")
    public int deleteDetail(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(nomor,1,3)) "
            + " FROM acc_kas "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND id_kantor = #{id_kantor} AND id_jenis_transaksi=#{id}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("id") Character id, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    /////////////////////////////////// Petty Cash ///////////////////////////////////////
    
    @SelectProvider(type = ProviderAccKas.class, method = "SelectAllPettyCash")
    public List<AccPettyCash> selectAllPettyCash    (
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor,
            @Param("st") Character st
    ) throws RuntimeException;

    
    @Insert("INSERT INTO acc_petty_cash "
            + "(nomor, tanggal,   id_kantor, saldo_awal, awal, akhir, saldo_akhir, tgl_awal, tgl_akhir) "
            + "VALUES (#{nomor}, #{tanggal},  #{id_kantor}, #{saldo_awal}, #{awal}, #{akhir}, #{saldo_akhir}, #{tgl_awal}, #{tgl_akhir})")
    public int insertPettyCash(AccPettyCash accPettyCash) throws RuntimeException;

    @Delete("DELETE FROM acc_petty_cash WHERE id = #{id}")
    public int deletePettyCash(@Param("id") Integer id) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama as kantor FROM acc_petty_cash a  "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang "
            + " WHERE a.nomor=#{nomor} ")
    public AccPettyCash selectOnePettyCash(@Param("nomor") String nomor) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(nomor,1,3)) "
            + " FROM acc_petty_cash "
            + " WHERE date_part('year', tanggal)=date_part('year', CURRENT_DATE) "
            + " AND id_kantor = #{id_kantor} ")
    public String SelectMaxPettyCash(@Param("id_kantor") String id_kantor) throws RuntimeException;
    
    
    @Select("SELECT a.* FROM acc_petty_cash a  "
            + " WHERE a.id_kantor=#{id} ORDER BY id DESC LIMIT 1")
    public AccPettyCash selectPc(@Param("id") String id) throws RuntimeException;


}
