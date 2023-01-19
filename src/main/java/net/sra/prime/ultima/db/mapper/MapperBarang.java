/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" )throws RuntimeException;
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
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.BarangMinMax;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperBarang {

    
    @Select("SELECT * FROM barang a "
            + " INNER JOIN satuan_kecil b ON a.id_satuan_kecil=b.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON a.id_satuan_besar=d.id_satuan_besar "
            + " INNER JOIN kategori_barang c ON a.id_kategori_barang=c.id_kategori_barang")
    public List<Barang> selectAll( )throws RuntimeException;

   
    @Insert("INSERT INTO barang "
            + "(id_barang,nama_barang,id_kategori_barang,id_satuan_kecil,barcode,status,keterangan,nama_alias,id_satuan_besar, isi_satuan,id_family) "
            + "VALUES "
            + "(#{id_barang}, #{nama_barang}, #{id_kategori_barang}, #{id_satuan_kecil}, #{barcode}, #{status}, #{keterangan}, #{nama_alias}, #{id_satuan_besar}, #{isi_satuan}, #{id_family}) ")
    public int insert(Barang barang)  throws RuntimeException;

    
    
    @Delete("DELETE FROM barang WHERE id_barang = #{id_barang}")
    public int delete(@Param("id_barang") String id_barang)  throws RuntimeException;

    
    @Select("SELECT a.*, b.kategori_barang"
            + " FROM barang a "
            + " INNER JOIN kategori_barang b ON a.id_kategori_barang=b.id_kategori_barang"
            + " WHERE a.id_barang=#{id_lama}")
    public Barang selectOneperBarang(@Param("id_lama") String id_lama )throws RuntimeException;

    

    @Update("UPDATE barang  SET "
            + " id_barang = #{id_barang}, "
            + " nama_barang = #{nama_barang}, "
            + " id_kategori_barang = #{id_kategori_barang}, "
            + " id_satuan_besar = #{id_satuan_besar}, "
            + " id_satuan_kecil = #{id_satuan_kecil}, "
            + " barcode = #{barcode}, "
            + " id_jenis_barang = #{id_jenis_barang}, "
            + " nama_alias = #{nama_alias}, "
            + " status = #{status}, "
            + " keterangan = #{keterangan}, "
            + " isi_satuan = #{isi_satuan}, "
            + " id_family = #{id_family} "
            + " WHERE id_barang = #{id_lama}")
    public int updateBarang(Barang barang)  throws RuntimeException;

    @Select("SELECT * FROM barang a "
            + " INNER JOIN satuan_kecil b ON a.id_satuan_kecil=b.id_satuan_kecil "
            + " INNER JOIN satuan_besar c ON a.id_satuan_besar=c.id_satuan_besar "
            + " WHERE a.id_barang = #{id_barang}")
    public Barang selectOne(@Param("id_barang") String id_barang )throws RuntimeException;
    
    
    @Insert("INSERT INTO decanting_mapping "
            + " (id_barang,id_barang_to) "
            + " VALUES "
            + " (#{id_barang},#{id_barang_to})")
    public int insertMapping(@Param("id_barang") String id_barang,
            @Param("id_barang_to") String id_barang_to)  throws RuntimeException;
    
    @Delete("DELETE FROM decanting_mapping WHERE id_barang = #{id_barang} AND id_barang_to=#{id_barang_to}")
    public int deleteMapping(@Param("id_barang") String id_barang,
            @Param("id_barang_to") String id_barang_to)  throws RuntimeException;
    
    @Select("SELECT a.*,b.*,c.*,d.* FROM barang a "
            + " INNER JOIN decanting_mapping dm ON a.id_barang=dm.id_barang_to "
            + " INNER JOIN satuan_kecil b ON a.id_satuan_kecil=b.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON a.id_satuan_besar=d.id_satuan_besar "
            + " INNER JOIN kategori_barang c ON a.id_kategori_barang=c.id_kategori_barang "
            + " WHERE dm.id_barang=#{id_barang}")
    public List<Barang> selectAllMapping(@Param("id_barang") String id_barang)throws RuntimeException;
    
    @SelectProvider(type = ProviderBarang.class, method = "selectAll")
    public List<Barang> selectAllMappingArray(
            @Param("id_barang") List<String> id_barang
            ) throws RuntimeException;
    
    @Insert("INSERT INTO barang_minmax (id_gudang,id_barang,min,max) VALUES (#{id_gudang},#{id_barang},#{min},#{max})")
    public int insertMinMax(BarangMinMax barangMinMax)  throws RuntimeException;
    
    @Delete("DELETE FROM barang_minmax WHERE id_gudang = #{id_gudang}")
    public int deleteMinMax(@Param("id_gudang") String id_gudang)  throws RuntimeException;
    
    @Delete("DELETE FROM barang_minmax WHERE id_gudang = #{id_gudang} AND id_barang=#{id_barang}")
    public int deleteMinMaxPerBarang(@Param("id_gudang") String id_gudang,@Param("id_barang") String id_barang)  throws RuntimeException;
    
    
    @Select("SELECT a.*,b.satuan_kecil,d.satuan_besar,e.min,e.max "
            + " FROM barang a "
            + " INNER JOIN satuan_kecil b ON a.id_satuan_kecil=b.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON a.id_satuan_besar=d.id_satuan_besar "
            + " INNER JOIN (SELECT id_barang FROM stok_barang WHERE id_gudang = #{id_gudang} GROUP BY id_barang ) f ON a.id_barang = f.id_barang "
            + " LEFT JOIN barang_minmax e ON a.id_barang=e.id_barang AND e.id_gudang=#{id_gudang} "
            + " ORDER BY a.nama_barang "
            )
    public List<BarangMinMax> selectAllMinMAx(String id_gudang)throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String getGudang(@Param("id_gudang") String id_gudang) throws RuntimeException;
}
