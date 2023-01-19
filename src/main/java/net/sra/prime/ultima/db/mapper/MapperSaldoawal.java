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

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.Saldoawal;
import net.sra.prime.ultima.entity.SaldoawalDetail;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperSaldoawal {

    
    @Select("SELECT a.*,b.gudang FROM saldo_awal a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang ")
    public List<Saldoawal> selectAll() throws RuntimeException;
    
    @Select("SELECT a.*,b.gudang FROM saldo_awal a INNER JOIN gudang b ON a.id_gudang=b.id_gudang WHERE a.id_gudang=#{id_gudang}")
    public Saldoawal selectOne(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    @Insert("INSERT INTO saldo_awal "
            + "(id_gudang, status, jumlah) "
            + "VALUES "
            + "(#{id_gudang},#{status}, #{jumlah}) ")
    public int insert(Saldoawal saldoawal) throws RuntimeException;

    
    @Delete("DELETE FROM saldo_awal WHERE id_gudang = #{id_gudang}")
    public int delete(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    
    @Update("UPDATE saldo_awal  SET "
            + " jumlah = #{jumlah} "
            + " WHERE id_gudang = #{id_gudang} ")
    public int update(Saldoawal saldoawal) throws RuntimeException;
    
    
    @Update("UPDATE saldo_awal  SET "
            + " jumlah = #{jumlah}, "
            + " status = #{status} "
            + " WHERE id_gudang = #{id_gudang} ")
    public int updateStatus(Saldoawal saldoawal) throws RuntimeException;
    
    
    //////////////////// DETAIL //////////////////////
    
     
    @Select("SELECT a.*, c.nama_barang,d.satuan_kecil, e.satuan_besar, (a.qty / c.isi_satuan) as qtybesar, c.isi_satuan "
            + " FROM saldo_awal_detail a"
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " INNER JOIN barang c ON a.id_barang=c.id_barang "
            + " INNER JOIN satuan_kecil d ON c.id_satuan_kecil=d.id_satuan_kecil "
            + " INNER JOIN satuan_besar e ON c.id_satuan_besar=e.id_satuan_besar "
            + " WHERE a.id_gudang = #{id_gudang}")
    public List<SaldoawalDetail> selectAllDetail(@Param("id_gudang") String id_gudang) throws RuntimeException;;
    
    @Insert("INSERT INTO saldo_awal_detail "
            + "(id_gudang, id_barang, qty, hpp, keterangan, total) "
            + "VALUES "
            + "(#{id_gudang},#{id_barang}, #{qty}, #{hpp}, #{keterangan}, #{total}) ")
    public int insertDetail(SaldoawalDetail saldoawalDetail) throws RuntimeException;

    @Delete("DELETE FROM saldo_awal_detail WHERE id_gudang = #{id_gudang}")
    public int deleteDetail(@Param("id_gudang") String id_gudang) throws RuntimeException;
   
    
}
