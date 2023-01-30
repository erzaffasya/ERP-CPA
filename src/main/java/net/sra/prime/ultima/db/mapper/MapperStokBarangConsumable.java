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
import net.sra.prime.ultima.db.provider.ProviderStokBarang;
import net.sra.prime.ultima.entity.KartuStok;
import net.sra.prime.ultima.entity.Mutasi;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.StokBarangConsumable;
import net.sra.prime.ultima.entity.StokOpname;
import net.sra.prime.ultima.entity.StokOpnameDetil;
import net.sra.prime.ultima.entity.StokValue;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperStokBarangConsumable {

    
    @Select("SELECT a.*, b.gudang as nama_gudang, c.nama_barang as nama_barang, c.material_code as material_code, c.id_satuan_kecil FROM stok_barang_consumable a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang " 
            + " INNER JOIN barang_consumable c ON a.id_barang_consumable = c.id ")
    public List<StokBarangConsumable> selectAll() throws RuntimeException;

    @Insert("INSERT INTO stok_barang_consumable "
            + "(id_gudang, stok, tanggal, hpp, batch, reff, persediaan, penampungan, id_barang_consumable) "
            + "VALUES "
            + "(#{id_gudang}, #{stok}, #{tanggal}, #{hpp}, #{batch}, #{reff}, #{persediaan}, #{penampungan}, #{id_barang_consumable}) ")
    public int insert(StokBarangConsumable kategoriBarangConsumable) throws RuntimeException;

    @Update("UPDATE stok_barang_consumable SET "
            + " id_gudang=#{id_gudang},"
            + " stok=#{stok}, "
            + " tanggal=#{tanggal}, "
            + " hpp=#{hpp}, "
            + " batch=#{batch},"
            + " reff=#{reff}, "
            + " persediaan=#{persediaan}, "
            + " penampungan=#{penampungan},"
            + " id_barang_consumable=#{id_barang_consumable}"
            + " WHERE id_stok=#{id_stok}")
    public int update(StokBarangConsumable kategoriBarangConsumable) throws RuntimeException;

    @Delete("DELETE FROM stok_barang_consumable WHERE id_stok = #{id_stok}")
    public int delete(@Param("kode") String kode) throws RuntimeException;

    
    @Select("SELECT * FROM stok_barang_consumable WHERE id_stok=#{id_stok}")
    public StokBarangConsumable selectOne(@Param("id_stok") Integer id_stok )throws RuntimeException;

 
}
