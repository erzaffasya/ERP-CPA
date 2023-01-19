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
import net.sra.prime.ultima.entity.MasterJabatan;
import net.sra.prime.ultima.entity.hr.Tunjangan;
import net.sra.prime.ultima.entity.hr.TunjanganJabatan;
import net.sra.prime.ultima.entity.hr.TunjanganMaster;
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
public interface MapperTunjangan {

    @Select("SELECT a.*,b.nama as nama_master,"
            + " array_to_string(array(SELECT CONCAT('<div class=\"ui-g-3\">',c.jabatan,'</div>') as jabatan FROM master_jabatan c INNER JOIN hr.tunjangan_jabatan d ON c.id_jabatan=d.id_jabatan WHERE a.id=d.id_tunjangan),' ') as jabatan "
            + " FROM hr.tunjangan a INNER JOIN hr.tunjangan_master b On a.master=b.id"
            + " ORDER BY a.id ")
    public List<Tunjangan> selectAll()  throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM hr.tunjangan_master "
            + " ORDER BY nama ")
    public List<TunjanganMaster> selectAllTunjanganMaster()  throws RuntimeException;

    
    @Insert("INSERT INTO hr.tunjangan "
            + "(nama,jenis,jumlah,pajak,master) "
            + "VALUES "
            + "(#{nama}, #{jenis},#{jumlah},#{pajak},#{master}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Tunjangan tunjangan)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.tunjangan WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*,b.nama as nama_master "
            + " FROM hr.tunjangan a  "
            + " INNER JOIN hr.tunjangan_master b ON a.master=b.id "
            + " WHERE a.id=#{id}")
    public Tunjangan selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.tunjangan  SET "
            + " nama = #{nama}, "
            + " jenis = #{jenis}, "
            + " jumlah = #{jumlah}, "
            + " pajak = #{pajak} "
            + " WHERE id = #{id} ")
    public int update(Tunjangan tunjangan)  throws RuntimeException;
    

    @Select("SELECT a.*,b.jabatan,c.nama as nama_jabatan "
            + " FROM hr.tunjangan_jabatan a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " INNER JOIN hr.tunjangan c ON a.id_tunjangan=c.id "
            + " WHERE a.id_tunjangan=#{id_tunjangan}"
            + " ORDER BY a.id_tunjangan ")
    public List<TunjanganJabatan> selectAllTunjanganJabatan(@Param("id_tunjangan") Integer id_tunjangan) throws RuntimeException;
    
    @Select("SELECT b.* "
            + " FROM hr.tunjangan_jabatan a "
            + " INNER JOIN master_jabatan b ON a.id_jabatan=b.id_jabatan "
            + " WHERE a.id_tunjangan=#{id_tunjangan}"
            + " ORDER BY b.id_jabatan ")
    public List<MasterJabatan> selectAllJabatan(@Param("id_tunjangan") Integer id_tunjangan) throws RuntimeException;
    
    @Select("SELECT * "
            + " FROM master_jabatan "
            + " WHERE id_jabatan NOT IN (SELECT id_jabatan FROM hr.tunjangan_jabatan a "
            + " INNER JOIN hr.tunjangan b ON a.id_tunjangan=b.id WHERE b.master= (SELECT c.master FROM  hr.tunjangan c WHERE id=#{id}))"
            + " ORDER BY id_jabatan ")
    public List<MasterJabatan> selectAllTblJabatan(@Param("id") Integer id) throws RuntimeException;
    
    @Insert("INSERT INTO hr.tunjangan_jabatan "
            + "(id_tunjangan,id_jabatan) "
            + "VALUES "
            + "(#{id_tunjangan}, #{id_jabatan}) ")
    public int insertTunjanganJabatan(TunjanganJabatan tunjanganJabatan)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.tunjangan_jabatan WHERE id_tunjangan = #{id_tunjangan}")
    public int deleteTunjanganJabatan(@Param("id_tunjangan") Integer id_tunjangan)  throws RuntimeException;
    
    @Insert("INSERT INTO hr.tunjangan_master "
            + "(nama) "
            + "VALUES "
            + "(#{nama}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertTunjanganMaster(TunjanganMaster tunjanganMaster)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.tunjangan_master WHERE id = #{id}")
    public int deleteTunjanganMaster(@Param("id") Integer id)  throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM hr.tunjangan WHERE master=(SELECT c.master FROM  hr.tunjangan c WHERE id=#{id})")
    public int jumlahTunjangan(@Param("id") Integer id)  throws RuntimeException;

}
