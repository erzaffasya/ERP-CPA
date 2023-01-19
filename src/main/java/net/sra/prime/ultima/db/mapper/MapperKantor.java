/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")throws RuntimeException;
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
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.Region;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperKantor {

   
    @Select("SELECT * FROM internal_kantor_cabang a LEFT JOIN region b ON a.id_region=b.id_region")
    public List<InternalKantorCabang> selectAll()throws RuntimeException;

   
    @Insert("INSERT INTO internal_kantor_cabang "
            + "(id_kantor_cabang, id_perusahaan, nama, ho, alamat, account_penjualan, account_hpp, telpon, kontak, email, fax, aktif) "
            + "VALUES "
            + "(#{id_kantor_cabang}, #{id_perusahaan}, #{nama}, #{ho}, #{alamat}, #{account_penjualan}, #{account_hpp}, #{telpon}, #{kontak}, #{email}, #{fax}, #{aktif}) ")
    public int insert(InternalKantorCabang internalKantorCabang)  throws RuntimeException;
  
    @Delete("DELETE FROM internal_kantor_cabang WHERE id_kantor_cabang = #{id}")
    public int delete(@Param("id") String id)  throws RuntimeException;

    @Select("SELECT * FROM internal_kantor_cabang WHERE id_kantor_cabang=#{id_lama}")
    public InternalKantorCabang selectOne(@Param("id_lama") String id_lama)throws RuntimeException;

   
    @Update("UPDATE internal_kantor_cabang  SET "
            + " id_kantor_cabang = #{id_kantor_cabang}, "
            + " id_perusahaan = #{id_perusahaan}, "
            + " nama = #{nama}, "
            + " ho = #{ho}, "
            + " fax = #{fax}, "
            + " email = #{email}, "
            + " alamat = #{alamat}, "
            + " account_penjualan = #{account_penjualan}, "
            + " account_hpp = #{account_hpp}, "
            + " kontak = #{kontak}, "
            + " aktif = #{aktif} "
            + " WHERE id_kantor_cabang = #{id_lama} ")
    public int update(InternalKantorCabang internalKantorCabang)  throws RuntimeException;

    @Select("SELECT * FROM region")
    public List<Region> selectRegion()throws RuntimeException;
    
}
