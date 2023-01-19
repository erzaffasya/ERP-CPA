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
import net.sra.prime.ultima.db.provider.ProviderAccGl;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccValue;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAccGl {

    
    @Select("SELECT * FROM acc_gl_trans WHERE posting=true ORDER BY gl_date DESC,gl_number DESC")
    public List<AccGlTrans> selectAll() throws RuntimeException;
    
//    @Select("SELECT * FROM acc_gl_trans WHERE journal_code=#{jurnal} ORDER BY gl_date DESC,gl_number DESC")
//    public List<AccGlTrans> selectAllJU(@Param("jurnal") String jurnal) throws RuntimeException;
//    
    
    @SelectProvider(type = ProviderAccGl.class, method = "SelectAll")
    public List<AccGlTrans> selectAllJU(
            @Param("jurnal") String jurnal,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Boolean status,
            @Param("note") String note
    ) throws RuntimeException;
    
    @Select("SELECT * FROM acc_gl_trans WHERE gl_number = #{gl_number}")
    public AccGlTrans selectOne(@Param("gl_number") String gl_number) throws RuntimeException;
    
    @Select("SELECT * FROM acc_gl_trans WHERE reference = #{reff}")
    public AccGlTrans selectOneRef(@Param("reff") String reff) throws RuntimeException;

    
    @Insert("INSERT INTO acc_gl_trans "
            + "(journal_code, gl_number, gl_date, reference, note, tag, posting) "
            + "VALUES "
            + "(#{journal_code},#{gl_number}, #{gl_date}, #{reference}, #{note}, #{tag}, #{posting}) ")
    public int insert(AccGlTrans accGlTrans) throws RuntimeException;

    
    @Delete("DELETE FROM acc_gl_trans WHERE gl_number = #{gl_number}")
    public int delete(@Param("gl_number") String gl_number) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(gl_number,3,6)) "
            + " FROM acc_gl_trans "
            + " WHERE EXTRACT(year FROM gl_date)=#{tahun} ")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
    
    
    @Update("UPDATE acc_gl_trans SET "
            + " posting = #{posting}, "
            + " gl_date = #{gl_date}, "
            + " note = #{note} "
            + " WHERE gl_number = #{gl_number}")
    public int updatePosting(AccGlTrans accGlTrans) throws RuntimeException;
    
    @Update("UPDATE acc_gl_trans SET gl_date=#{gl_date} WHERE reference=#{reference}")
    public int updateDate (@Param("gl_date") Date gl_date,
            @Param("reference") String reference) throws RuntimeException;


    @Update("﻿UPDATE acc_gl_detail SET gl_date=#{gl_date} WHERE gl_number = (SELECT gl_number FROM acc_gl_trans WHERE reference=#{reference})")
    public int updateDateDetail (@Param("gl_date") Date gl_date,
            @Param("reference") String reference) throws RuntimeException;
    /////////////////////////// GL Detail /////////////////////////////////
    
    @Select("SELECT * FROM acc_gl_detail")
    public List<AccGlDetail> selectAllDetail() throws RuntimeException;
    
    @Select("SELECT a.id_account,SUM(a.value) AS value "
            + "FROM acc_gl_detail a "
            + "INNER JOIN acc_gl_trans b ON a.gl_number=b.gl_number "
            + "WHERE EXTRACT(month FROM a.gl_date)=#{bulan} and EXTRACT(year FROM a.gl_date)=#{tahun} AND b.posting=true AND a.is_debit=#{is_debit} "
            + "GROUP BY a.id_account")
    public List<AccGlDetail> selectSumAccount(@Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun,
            @Param("is_debit") Boolean is_debit) throws RuntimeException;

    
    @Insert("INSERT INTO acc_gl_detail "
            + "(journal_code, gl_number, gl_date, id_account, line, keterangan, value, is_debit, posted) "
            + "VALUES "
            + "(#{journal_code},#{gl_number}, #{gl_date}, #{id_account}, #{line}, #{keterangan}, #{value}, #{is_debit}, #{posted}) ")
    public int insertDetail(AccGlDetail accGlDetail) throws RuntimeException;

    
    @Delete("DELETE FROM acc_gl_detail WHERE gl_number = #{gl_number}")
    public int deleteDetail(@Param("gl_number") String gl_number) throws RuntimeException;

    @Select("SELECT a.*, b.note, c.account, b.reference FROM acc_gl_detail a "
            + " INNER JOIN acc_gl_trans b ON a.gl_number = b.gl_number "
            + " INNER JOIN account c ON a.id_account = c.id_account "
            + " WHERE b.id_perusahaan = #{id_perusahaan} "
            + " ORDER BY b.gl_number, a.line")
    public List<AccGlDetail> selectJurnal(@Param("id_perusahaan") String id_perusahaan) throws RuntimeException;
    
    @Select("SELECT a.*, b.note, c.account, b.reference FROM acc_gl_detail a "
            + " INNER JOIN acc_gl_trans b ON a.gl_number = b.gl_number "
            + " INNER JOIN account c ON a.id_account = c.id_account "
            + " WHERE b.reference = #{reference} "
            + " ORDER BY b.gl_number, a.line")
    public List<AccGlDetail> selectJurnalGl(@Param("reference") String reference) throws RuntimeException;
    
    @Select("SELECT a.*, b.note, c.account, b.reference FROM acc_gl_detail a "
            + " INNER JOIN acc_gl_trans b ON a.gl_number = b.gl_number "
            + " INNER JOIN account c ON a.id_account = c.id_account "
            + " WHERE "
            + " a.id_account >= #{accountfrom} AND a.id_account <= #{accountto} AND c.level=4 "
            + " AND a.gl_date >= #{awal} AND a.gl_date <= #{akhir}"
            + " ORDER BY a.id_account,b.gl_date, a.gl_number")
    public List<AccGlDetail> selectJurnalByAccount( 
            @Param("awal") Date awal, 
            @Param("akhir") Date akhir,
            @Param("accountfrom") Integer accountfrom,
            @Param("accountto") Integer accountto) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.note, c.account, b.reference FROM acc_gl_detail a "
            + " INNER JOIN acc_gl_trans b ON a.gl_number = b.gl_number "
            + " INNER JOIN account c ON a.id_account = c.id_account "
            + " WHERE "
            + " a.id_account = #{id_account} AND b.posting=true "
            + " AND a.gl_date >= #{awal} AND a.gl_date <= #{akhir}"
            + " ORDER BY b.gl_date, a.gl_number")
    public List<AccGlDetail> selectOneJurnalByAccount( 
            @Param("awal") Date awal, 
            @Param("akhir") Date akhir,
            @Param("id_account") Integer id_account
            ) throws RuntimeException;
    
    @Select("SELECT a.*, b.note, c.account, b.reference FROM acc_gl_detail a "
            + " INNER JOIN acc_gl_trans b ON a.gl_number = b.gl_number "
            + " INNER JOIN account c ON a.id_account = c.id_account "
            + " WHERE b.gl_number = #{gl_number} "
            + " ORDER BY  a.line")
    public List<AccGlDetail> selectOneDetail(@Param("gl_number") String gl_number) throws RuntimeException;
    
    @Select("SELECT a.*,b.reference "
            + " FROM acc_gl_detail a"
            + " INNER JOIN acc_gl_trans b ON a.gl_number=b.gl_number "
            + " WHERE id_account = #{id_account} "
            + " AND a.gl_number > #{awal} AND a.gl_date >='2018-01-01'")
    public List<AccGlDetail> selectKas(@Param("id_account") Integer id_account, @Param("awal") String awal) throws RuntimeException;
    
    
    @Select("SELECT a.*,b.reference "
            + " FROM acc_gl_detail a"
            + " INNER JOIN acc_gl_trans b ON a.gl_number=b.gl_number "
            + " WHERE a.id_account = #{id_account} "
            + " AND a.gl_number >= #{awal} AND a.gl_number <= #{akhir} AND  length(a.gl_number)=10 ")
    public List<AccGlDetail> selectKasEdit(@Param("id_account") Integer id_account, @Param("awal") String awal, @Param("akhir") String akhir) throws RuntimeException;

    @Select("SELECT SUM(value) FROM acc_gl_detail a "
            + "INNER JOIN acc_gl_trans b ON a.gl_number=b.gl_number "
            + "WHERE "
            + "a.id_account=#{id_account} "
            + "AND a.gl_date >= #{awal} AND a.gl_date < #{akhir} "
            + "AND a.is_debit=#{is_debit} "
            + "AND b.posting=true")
    public Double selectSumValue(
            @Param("id_account") Integer id_account, 
            @Param("awal") Date awal, 
            @Param("akhir") Date akhir,
            @Param("is_debit") Boolean is_debit) throws RuntimeException;
    /////////////// Acc_Value ///////////////////////
    
    @Select("SELECT a.*, b.account as nama_account "
            + " FROM acc_value a "
            + " INNER JOIN account b ON a.account=b.id_account "
            + " WHERE a.years=#{years} AND a.account=#{account}")
    public AccValue selectOneAccValue(@Param("years") String years, @Param("account") Integer account) throws RuntimeException;
    
    @Select("SELECT a.*, b.account as nama_account FROM acc_value a  "
            + " INNER JOIN account b ON a.account=b.id_account "
            + " WHERE a.years=#{years} "
            + " ORDER BY b.id_account")
    public List<AccValue> selectOneYear(@Param("years") String years) throws RuntimeException;
    
    @Select("SELECT a.*, b.account as nama_account FROM acc_value a  "
            + " INNER JOIN account b ON a.account=b.id_account "
            + " WHERE a.years=#{years} AND (SUBSTR(cast(a.account as text), 1,1)='1' OR SUBSTR(cast(a.account as text), 1,1)='2' OR SUBSTR(cast(a.account as text), 1,1)='3') "
            + " ORDER BY b.id_account")
    public List<AccValue> selectOneYearTutuptahun(@Param("years") String years) throws RuntimeException;
    
    @Select("SELECT a.*, b.account as nama_account FROM acc_value a  "
            + " INNER JOIN account b ON a.account=b.id_account "
            + " WHERE a.years=#{years} AND a.account=#{account}")
    public AccValue selectOneYearAccount(@Param("years") String years, @Param("account") Integer account) throws RuntimeException;
    
    @Insert("INSERT INTO acc_value "
            + "(years, account, id_perusahaan) "
            + " VALUES "
            + " (#{years}, #{account}, #{id_perusahaan})")
    public int insertAccValue(AccValue accValue) throws RuntimeException;
    
    @Insert("INSERT INTO acc_value "
            + "(years, account, id_perusahaan) "
            + " VALUES "
            + " (#{years}, #{account}, #{id_perusahaan})")
    public int insertSaldoAwal(AccValue accValue) throws RuntimeException;
    
    @SelectProvider(type = ProviderAccGl.class, method = "UpdateAccValue")
    public void updateAccValue(
            @Param("nomor") Integer nomor,
            @Param("nilai") Double nilai,
            @Param("account") Integer account,
            @Param("years") String years,
            @Param("status") Character status
            ) throws RuntimeException;

    @SelectProvider(type = ProviderAccGl.class, method = "UpdateSaldoAwal")
    public void updateSaldoAwal(
            @Param("nomor") Integer nomor,
            @Param("nilai") Double nilai,
            @Param("account") Integer account,
            @Param("years") String years,
            @Param("status") Character status
            ) throws RuntimeException;
    
    @Update("UPDATE acc_value set "
            + "db13=db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12, "
            + "cr13=cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12 "
            + "WHERE years=#{years} AND account=#{account}")
    public int updateSaldoAkhir(@Param("years") String years, @Param("account") Integer account) throws RuntimeException;
    
    @SelectProvider(type = ProviderAccGl.class, method = "UpdateAccValuetoZero")
    public void updateAccValuetoZero(
            @Param("bulan") Integer bulan,
            @Param("tahun") String tahun
            ) throws RuntimeException;
    
    
    @Update("UPDATE acc_value set "
            + "db13=db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12, "
            + "cr13=cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12 "
            + "WHERE years=#{years}")
    public int updateSaldoAkhirAll(@Param("years") String years) throws RuntimeException;
    
    @Update("UPDATE acc_value set "
            + "db0=0,db13=0, "
            + "cr0=0,cr13=0 "
            + "WHERE years=#{years}")
    public int updateSaldoAwaltoZero(@Param("years") String years) throws RuntimeException;
    
    
    
    ////////////////////////  Neraca //////////////
    
    @Select("SELECT a.id_account,a.account as nama_account,a.level,b.* "
            + "FROM account a "
            + "LEFT JOIN acc_value b ON a.id_account=b.account AND b.years=#{years}  "
            + "WHERE "
            + "a.id_account >= #{bawah} AND a.id_account < #{atas} "
            + "ORDER BY a.id_account")
    public List<AccValue> selectOneYearNeraca(
            @Param("years") String years,
            @Param("bawah") Integer bawah,
            @Param("atas") Integer atas) throws RuntimeException;
    
///////////////////////Saldo awal neraca/////////////////////////


@SelectProvider(type = ProviderAccGl.class, method = "numSaldoAwal")
    public Double numSaldoAwal(
            @Param("bulan") Integer bulan,
            @Param("tahun") String tahun,
            @Param("awal") Integer awal,
            @Param("akhir") Integer akhir,
            @Param("type") Character type
    ) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderAccGl.class, method = "numArusKas")
    public Double numArusKas(
            @Param("bulan") Integer bulan,
            @Param("tahun") String tahun,
            @Param("awal") Integer awal,
            @Param("akhir") Integer akhir,
            @Param("type") Character type
    ) throws RuntimeException;

    
    
    
    @SelectProvider(type = ProviderAccGl.class, method = "numSaldo")
    public AccValue numSaldo(
            @Param("bulan") Integer bulan,
            @Param("tahun") String tahun,
            @Param("awal") Integer awal,
            @Param("akhir") Integer akhir,
            @Param("type") Character type
    ) throws RuntimeException;
    
    @Select("SELECT a.id_account,a.account," 
            + " (" 
            + "     (SELECT COALESCE(SUM(b.value),0) FROM acc_gl_detail b " 
            + "     INNER JOIN account c ON b.id_account=c.id_account" 
            + "     INNER JOIN acc_gl_trans d ON b.gl_number=d.gl_number" 
            + "     WHERE " 
            + "     c.parent=a.id_account" 
            + "     AND  EXTRACT(year FROM d.gl_date)=#{tahun} " 
            + "     AND  EXTRACT(month FROM d.gl_date)=#{bulan} " 
            + "     AND b.is_debit=true" 
            + "     ) -" 
            + "     (SELECT COALESCE(SUM(b.value),0) FROM acc_gl_detail b " 
            + "     INNER JOIN account c ON b.id_account=c.id_account" 
            + "     INNER JOIN acc_gl_trans d ON b.gl_number=d.gl_number" 
            + "     WHERE " 
            + "     c.parent=a.id_account" 
            + "     AND  EXTRACT(year FROM d.gl_date)=#{tahun}" 
            + "     AND  EXTRACT(month FROM d.gl_date)=#{bulan}" 
            + "     AND b.is_debit=false" 
            + "     )" 
            + " ) as saldo	" 
            + " FROM account a " 
            + " WHERE a.parent =#{id_account}")
    public List<AccGlDetail> selectBiayaOperasioanal(
            @Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun,
            @Param("id_account") Integer id_account) throws RuntimeException;
    
    @Select("SELECT account_operasional FROM internal_kantor_cabang WHERE id_kantor_cabang=#{id_kantor}")
    public Integer selectAccountKantor(
            @Param("id_kantor") String id_kantor
    ) throws RuntimeException;

    //// Arus Kas
    
    @Select("SELECT SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11-cr12) " 
            + " FROM acc_value" 
            + " WHERE" 
            + " years=#{tahun}" 
            + " AND SUBSTRING(account::text,1,1)::int=6 " 
            + " AND SUBSTRING(account::text,4,1)::int=0 " 
            + " AND SUBSTRING(account::text,5,4)::int8 >=9001")
    public Double selectPenyusutan(@Param("tahun") String tahun) throws RuntimeException;
    
}
