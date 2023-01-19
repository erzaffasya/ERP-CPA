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
import net.sra.prime.ultima.entity.BarangConsumable;
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
public interface MapperBarangConsumable {

    @Select("SELECT a.*, b.kategori_barang as nama_kategori, c.satuan_besar as nama_kemasan, d.satuan_kecil as nama_satuan_kecil  FROM barang_consumable a "
            + " INNER JOIN  kategori_barang_consumable b ON a.id_kategori_barang_consumable=b.kode "
            + " INNER JOIN satuan_besar c ON a.kemasan=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON a.id_satuan_kecil=d.id_satuan_kecil ")
    public List<BarangConsumable> selectAll() throws RuntimeException;

    @Insert("INSERT INTO barang_consumable "
            + "(nama_barang, material_code, part_number, id_kategori_barang_consumable, id_satuan_kecil, kemasan, isi_satuan) "
            + "VALUES "
            + "(#{nama_barang}, #{material_code}, #{part_number}, #{id_kategori_barang_consumable}, #{id_satuan_kecil}, #{kemasan}, #{isi_satuan}) ")
    public int insert(BarangConsumable barangConsumable) throws RuntimeException;

    @Update("UPDATE barang_consumable SET "
            + " nama_barang=#{nama_barang}, "
            + " material_code=#{material_code}, "
            + " part_number=#{part_number}, "
            + " id_kategori_barang_consumable=#{id_kategori_barang_consumable}, "
            + " id_satuan_kecil=#{id_satuan_kecil}, "
            + " kemasan=#{kemasan}, "
            + " isi_satuan=#{isi_satuan} "
            + " WHERE id=#{id}")
    public int update(BarangConsumable barangConsumable) throws RuntimeException;

    @Delete("DELETE FROM barang_consumable WHERE kode = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT a.*, b.kategori_barang as nama_kategori, c.satuan_besar as nama_kemasan, d.satuan_kecil as nama_satuan_kecil  FROM barang_consumable a "
            + " INNER JOIN  kategori_barang_consumable b ON a.id_kategori_barang_consumable=b.kode "
            + " INNER JOIN satuan_besar c ON a.kemasan=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON a.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE id=#{id}")
    public BarangConsumable selectOne(@Param("id") Integer id) throws RuntimeException;

}
