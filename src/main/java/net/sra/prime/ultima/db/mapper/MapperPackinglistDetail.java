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
import net.sra.prime.ultima.entity.PackinglistDetail;
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
public interface MapperPackinglistDetail {

    @Select("SELECT * FROM packinglist_detail a ")
    public List<PackinglistDetail> selectAll() throws RuntimeException;

    @Insert("INSERT INTO packinglist_detail "
            + "(no_pl, urut, id_gudang, id_barang, qty, keterangan, diorder, sisa,segel,kemasan) "
            + "VALUES "
            + "(#{no_pl}, #{urut}, #{id_gudang}, #{id_barang}, #{qty}, #{keterangan}, #{diorder}, #{sisa},#{segel},#{kemasan}) ")
    public int insert(PackinglistDetail packinglistDetail) throws RuntimeException;

    
    @Delete("DELETE FROM packinglist_detail WHERE no_pl = #{no_pl}")
    public int delete(@Param("no_pl") String no_pl) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, d.satuan_kecil, b.isi_satuan "
            + " FROM packinglist_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
            + " WHERE a.no_pl=#{id_lama} "
            + " ORDER BY a.urut")
    public List<PackinglistDetail> selectOne(@Param("id_lama") String id_lama) throws RuntimeException;
    
    

}
