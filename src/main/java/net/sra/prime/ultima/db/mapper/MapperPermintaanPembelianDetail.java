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
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperPermintaanPembelianDetail {

    @Select("SELECT * FROM permintaan_pembeliaan_detail a ")
    public List<PermintaanPembelianDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO permintaan_pembeliaan_detail "
            + "(no_pp, urut,  id_barang, keperluan, spesifikasi, jumlah_order, jumlah_stok, tanggal_dibutuhkan, keterangan,satuan,harga,amount) "
            + "VALUES "
            + "(#{no_pp}, #{urut}, #{id_barang}, #{keperluan}, #{spesifikasi}, #{jumlah_order}, #{jumlah_stok}, #{tanggal_dibutuhkan}, #{keterangan},#{satuan},#{harga},#{amount}) ")
    public int insert(PermintaanPembelianDetail permintaanPembeliaanDetail) throws RuntimeException;

    @Delete("DELETE FROM permintaan_pembeliaan_detail WHERE no_pp = #{no_pp}")
    public int delete(@Param("no_pp") String no_pp) throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM permintaan_pembeliaan_detail a "
            + " WHERE a.no_pp =#{id_lama} "
            + " ORDER BY a.urut")
    public List<PermintaanPembelianDetail> selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

}
