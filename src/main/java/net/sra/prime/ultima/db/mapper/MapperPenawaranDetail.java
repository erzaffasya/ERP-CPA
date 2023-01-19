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
import net.sra.prime.ultima.entity.PenawaranDetail;
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
public interface MapperPenawaranDetail {

    @Select("SELECT * FROM penawaran_detail a ")
    public List<PenawaranDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO penawaran_detail "
            + "(no_penawaran, urut, id_gudang, id_barang, qty, harga, total, harga_asli, diskon, id_pajak, diskonrp, diskonpersen, additional_charge, jenis_satuan, revisi) "
            + "VALUES "
            + "(#{no_penawaran}, #{urut}, #{id_gudang}, #{id_barang}, #{qty}, #{harga}, #{total}, #{harga_asli}, #{diskon}, #{id_pajak}, #{diskonrp}, #{diskonpersen}, #{additional_charge}, #{jenis_satuan}, #{revisi}) ")
    public int insert(PenawaranDetail penawarandetail) throws RuntimeException;

    @Delete("DELETE FROM penawaran_detail WHERE no_penawaran = #{no_penawaran} AND revisi = #{revisi}")
    public int delete(@Param("no_penawaran") String no_penawaran, @Param("revisi") Integer revisi) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil, d.satuan_besar, a.qty * b.isi_satuan as qty_kecil "
            + " FROM penawaran_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " WHERE a.no_penawaran=#{id_lama} AND a.revisi = #{revisi} "
            + " ORDER BY a.urut")
    public List<PenawaranDetail> selectOne(@Param("id_lama") String id_lama, @Param("revisi") Integer revisi) throws RuntimeException;

}
