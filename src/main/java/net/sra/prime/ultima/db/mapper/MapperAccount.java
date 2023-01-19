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

import java.util.List;
import net.sra.prime.ultima.db.provider.ProviderAccount;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.AccountTipe;
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
public interface MapperAccount {

    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " ORDER BY a.id_account")
    public List<Account> selectAllAccount() throws RuntimeException;
    
    
    @Select("SELECT a.*,b.account as account_parent,c.db13 - c.cr13 as debit "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " LEFT JOIN acc_value c ON a.id_account = c.account AND c.years=#{tahun}"
            + " ORDER BY a.id_account")
    public List<Account> selectAll(@Param("tahun") String tahun) throws RuntimeException;
    
    
    
    @SelectProvider(type = ProviderAccount.class, method = "selectBukuBesar")
    public List<Account> selectAPeriode(
            @Param("accountfrom") Integer accountfrom,
            @Param("accountto") Integer accountto,
            @Param("keterangan") String keterangan
            ) throws RuntimeException;
    
    
    @Select("SELECT a.*,b.account as account_parent,c.db13 - c.cr13 as debit"
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " LEFT JOIN acc_value c ON a.id_account = c.account AND c.years=#{tahun} "
            + " WHERE SUBSTR(cast(a.id_account as text), 1,1)=#{tipe}  "
            + " ORDER BY a.id_account")
    public List<Account> selectAllTipeAccount(@Param("tipe") String tipe, @Param("tahun") String tahun) throws RuntimeException;

    @Insert("INSERT INTO account "
            + "(id_account, tipe_account, account, pembayaran, aktif, level,  parent) "
            + "VALUES "
            + "(#{id_account}, #{tipe_account}, #{account}, #{pembayaran}, #{aktif}, #{level},  #{parent}) ")
    public int insert(Account account) throws RuntimeException;
    
    @Update("UPDATE account SET "
            + " tipe_account = #{tipe_account}, "
            + " pembayaran = #{pembayaran}, "
            + " account = #{account}, "
            + " aktif = #{aktif}, "
            + " level = #{level}, "
            + " parent = #{parent} "
            + " WHERE id_account = #{id_account}")
    public int update(Account account) throws RuntimeException;

    @Delete("DELETE FROM account WHERE id_account = #{id_account}")
    public int delete(@Param("id_account") Integer id_account) throws RuntimeException;
    
    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " WHERE a.level = #{level} ORDER BY a.id_account")
    public List<Account> selectLevel(@Param("level") Integer level) throws RuntimeException;

    
    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " WHERE a.id_account = #{id_account}")
    public Account selectOne(@Param("id_account") Integer id_account) throws RuntimeException;

    @Select("SELECT max(id_account) FROM account WHERE level = #{level} AND parent = #{parent}")
    public Integer selectMaxId(@Param("level") Integer level, @Param("parent") Integer parent) throws RuntimeException;
    
    @Select("SELECT max(id_account) FROM account WHERE level = #{level}")
    public Integer selectMaxIdLevel1(@Param("level") Integer level) throws RuntimeException;

    @Select("SELECT a.*,b.account as account_parent "
            + " FROM account a "
            + " LEFT JOIN account b ON a.parent=b.id_account "
            + " WHERE "
            + " a.level = 4 "
            + " AND a.id_account > #{batas_bawah} "
            + " AND a.id_account < #{batas_atas} "
            + " ORDER BY a.id_account")
    public List<Account> selectByCategory(@Param("batas_bawah") Integer batas_bawah, @Param("batas_atas") Integer batas_atas) throws RuntimeException;
    
    
    @Select("SELECT a.*"
            + " FROM account a "
            + " WHERE "
            + " a.id_account >= #{batas_bawah} "
            + " AND a.id_account < #{batas_atas} "
            + " AND a.level=#{level}"
            + " ORDER BY a.id_account")
    public List<Account> selectAccountLevel(@Param("batas_bawah") Integer batas_bawah, 
            @Param("batas_atas") Integer batas_atas,
            @Param("level") Integer level) throws RuntimeException;
    
 
    @Select("SELECT account FROM account WHERE id_account = #{id_account}")
    public String selectAccount(@Param("id_account") Integer id_account) throws RuntimeException;

    @Select("SELECT * "
            + " FROM tipe_account  "
            + " ORDER BY kode_tipe")
    public List<AccountTipe> selectAllTipe() throws RuntimeException;
    
    
    @SelectProvider(type = ProviderAccount.class, method = "selectLabaRugi")
    public List<Account> selectLabaRugi(
            @Param("tipe") String tipe,
            @Param("tahun") String tahun,
            @Param("awal") Integer awal,
            @Param("akhir") Integer akhir,
            @Param("mulai") Integer mulai,
            @Param("panjang") Integer panjang
            ) throws RuntimeException;


}
