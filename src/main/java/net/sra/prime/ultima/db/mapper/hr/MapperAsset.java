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
import net.sra.prime.ultima.entity.hr.Asset;
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
public interface MapperAsset {

    @Select("SELECT a.*,b.nama "
            + " FROM hr.asset a  "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " ORDER BY a.tanggal_pinjam ")
    public List<Asset> selectAll()  throws RuntimeException;
    
    @Select("SELECT a.*,b.nama "
            + " FROM hr.asset a  "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE a.id_pegawai=#{id_pegawai} "
            + " ORDER BY a.tanggal_pinjam ")
    public List<Asset> selectAllbyPegawai(@Param("id_pegawai") String id_pegawai)  throws RuntimeException;
    
    
    @Insert("INSERT INTO hr.asset "
            + "(id_pegawai,id_asset,tanggal_pinjam,tanggal_kembali,status,nama_barang) "
            + "VALUES "
            + "(#{id_pegawai}, #{id_asset},#{tanggal_pinjam},#{tanggal_kembali},#{status},#{nama_barang}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(Asset asset)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.asset WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT a.*,b.nama "
            + " FROM hr.asset a  "
            + " INNER JOIN pegawai  b ON a.id_pegawai=b.id_pegawai "
            + " WHERE a.id=#{id}")
    public Asset selectOne(@Param("id") Integer id)throws RuntimeException;
    
    @Update("UPDATE hr.asset  SET "
            + " id_pegawai = #{id_pegawai}, "
            + " nama_barang = #{nama_barang}, "
            + " id_asset = #{id_asset}, "
            + " tanggal_pinjam = #{tanggal_pinjam}, "
            + " tanggal_kembali = #{tanggal_kembali}, "
            + " status = #{status} "
            + " WHERE id = #{id} ")
    public int update(Asset asset)  throws RuntimeException;
    

    
}
