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
import net.sra.prime.ultima.entity.PenerimaanDetail;
import net.sra.prime.ultima.entity.PenerimaanDetailReguler;
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
public interface MapperPenerimaanDetail {

    @Select("SELECT * FROM po_detail a ")
    public List<PenerimaanDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO penerimaan_detail "
            + "(no_penerimaan, urut,  id_barang, qty, harga, total, diorder) "
            + "VALUES "
            + "(#{no_penerimaan}, #{urut}, #{id_barang}, #{qty}, #{harga}, #{total}, #{diorder}) ")
    public int insert(PenerimaanDetail podetail) throws RuntimeException;

    @Delete("DELETE FROM penerimaan_detail WHERE no_penerimaan = #{no_penerimaan}")
    public int delete(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_kecil "
            + " FROM penerimaan_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil"
            + " WHERE a.no_penerimaan=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PenerimaanDetail> selectOneperPenerimaanDetail(@Param("id_lama") String id_lama) throws RuntimeException;

    ////////////////////////////////////////////// REGULER /////////////////////////////////////////
    @Insert("INSERT INTO penerimaan_detail_reguler "
            + "(no_penerimaan, urut,  nm_barang, qty, harga, total, diorder, satuan) "
            + "VALUES "
            + "(#{no_penerimaan}, #{urut}, #{nm_barang}, #{qty}, #{harga}, #{total}, #{diorder}, #{satuan}) ")
    public int insertReguler(PenerimaanDetailReguler penerimaanDetailReguler) throws RuntimeException;

    @Select("SELECT a.* "
            + " FROM penerimaan_detail_reguler a "
            + " WHERE a.no_penerimaan=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PenerimaanDetailReguler> selectOneReguler(@Param("id_lama") String id_lama) throws RuntimeException;
    
    @Delete("DELETE FROM penerimaan_detail_reguler WHERE no_penerimaan = #{no_penerimaan}")
    public int deleteReguler(@Param("no_penerimaan") String no_penerimaan) throws RuntimeException;

}
