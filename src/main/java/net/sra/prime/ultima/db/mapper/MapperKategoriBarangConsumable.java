/*
 * Copyright 2023 JoinFaces.
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
import net.sra.prime.ultima.entity.KategoriBarang;
import net.sra.prime.ultima.entity.KategoriBarangConsumable;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author erza
 */
@Mapper
public interface MapperKategoriBarangConsumable {

    @Select("SELECT * FROM kategori_barang_consumable")
    public List<KategoriBarangConsumable> selectAll() throws RuntimeException;

    @Insert("INSERT INTO kategori_barang_consumable "
            + "(kode, kategori_barang) "
            + "VALUES "
            + "(#{kode},#{kategori_barang}) ")
    public int insert(KategoriBarangConsumable kategoriBarangConsumable) throws RuntimeException;

    @Update("UPDATE kategori_barang_consumable SET "
            + " kode=#{kode}, "
            + " kategori_barang=#{kategori_barang}"
            + " WHERE kode=#{kode}")
    public int update(KategoriBarangConsumable kategoriBarangConsumable) throws RuntimeException;

    @Delete("DELETE FROM kategori_barang_consumable WHERE kode = #{kode}")
    public int delete(@Param("kode") String kode) throws RuntimeException;

    
    @Select("SELECT * FROM kategori_barang_consumable WHERE kode=#{kode}")
    public KategoriBarangConsumable selectOne(@Param("kode") String kode )throws RuntimeException;

}
