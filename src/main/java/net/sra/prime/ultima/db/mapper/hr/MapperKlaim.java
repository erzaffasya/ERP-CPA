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
package net.sra.prime.ultima.db.mapper.hr;

import java.util.List;
import net.sra.prime.ultima.entity.hr.Klaim;
import net.sra.prime.ultima.entity.hr.KlaimJabatan;
import net.sra.prime.ultima.entity.hr.KlaimKaryawan;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperKlaim {

    @Select("SELECT a.* "
            + " FROM hr.klaim a "
            + " ORDER BY a.id ")
    public List<Klaim> selectAll()  throws RuntimeException;

    
    @Insert("INSERT INTO hr.klaim "
            + "(nama,jenis,saldo,bukti,persetujuan1,persetujuan1_,persetujuan2,persetujuan2_by) "
            + "VALUES "
            + "(#{nama},#{jenis},#{saldo},#{bukti},#{persetujuan1},#{jpersetujuan1_by},#{persetujuan2},#{persetujuan2_by}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Klaim klaim)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.klaim WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM hr.klaim WHERE id=#{id}")
    public Klaim selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.klaim  SET "
            + " nama = #{nama}, "
            + " jenis = #{jenis}, "
            + " saldo = #{saldo}, "
            + " bukti = #{bukti}, "
            + " persetujuan1 = #{persetujuan1}, "
            + " persetujuan1_by = #{persetujuan1_by}, "
            + " persetujuan2 = #{persetujuan2}, "
            + " persetujuan2_by = #{persetujuan2_by} "
            + " WHERE id = #{id} ")
    public int update(Klaim klaim)  throws RuntimeException;

    @Insert("INSERT INTO hr.klaim_jabatan "
            + "(id_klaim,id_jabatan) "
            + "VALUES "
            + "(#{id_klaim},#{id_jabatan}) ")
    public int insertKlaimJabatan(KlaimJabatan klaimJabatan)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.klaim_jabatan WHERE id_klaim = #{id_klaim}")
    public int deleteKlaimJabatan(@Param("id_klaim") Integer id_klaim)  throws RuntimeException;
    
    @Select("SELECT a.* "
            + " FROM hr.klaim_jabatan a WHERE a.id_klaim=#{id_klaim} "
            + " ORDER BY a.id_klaim ")
    public List<KlaimJabatan> selectAllKlaimJabatan(@Param("id_klaim") Integer id_klaim)  throws RuntimeException;
    
    
    @Select("SELECT a.*, b.nama_klaim,c.nama as nama_pegawai "
            + " FROM hr.klaim_karyawan a "
            + " INNER JOIN hr.klaim b ON a.id_klaim=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " ORDER BY a.id ")
    public List<KlaimKaryawan> selectAllKlaimKaryawan()  throws RuntimeException;

    
    @Insert("INSERT INTO hr.klaim_karyawan "
            + "(id_klaim,id_pegawai,tanggal,jumlah,keterangan,create_by,create_date,approve1_by,approve2_by,status) "
            + "VALUES "
            + "(#{id_klaim},#{id_pegawai},#{tanggal},#{jumlah},#{keterangan},#{create_by},LOCALTIMESTAMP,#{approve1_by},#{approve2_by},#{status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertKlaimKaryawan(KlaimKaryawan klaimKaryawan)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.klaim_karyawan WHERE id = #{id}")
    public int deleteKlaimKaryawan(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_klaim,c.nama as nama_pegawai "
            + " INNER JOIN hr.klaim b ON a.id_klaim=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE id=#{id}")
    public KlaimKaryawan selectOneKlaimKaryawan(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.klaim  SET "
            + " id_klaim = #{id_klaim}, "
            + " id_pegawai = #{id_pegawai}, "
            + " tanggal = #{tanggal}, "
            + " jumlah = #{jumlah}, "
            + " keterangan = #{keterangan}, "
            + " aprrove1_by = #{approve1_by}, "
            + " aprrove2_by = #{approve2_by}, "
            + " status = #{status} "
            + " WHERE id = #{id} ")
    public int updateKlaimKaryawan(KlaimKaryawan klaimKaryawan)  throws RuntimeException;

    
}

