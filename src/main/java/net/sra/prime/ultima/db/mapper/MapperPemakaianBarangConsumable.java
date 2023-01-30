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
import net.sra.prime.ultima.entity.PemakaianBarangConsumable;
import net.sra.prime.ultima.entity.PemakaianBarangConsumableDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author erza
 */
@Mapper 
public interface MapperPemakaianBarangConsumable {
      @Select("SELECT a.*, b.nama as created_name, c.gudang as nama_gudang, d.nama as modified_name "
            + " FROM pemakaian_barang_consumable a "
            + " INNER JOIN pegawai b ON a.created_by=b.id_pegawai"
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.modified_by=d.id_pegawai "
            + " ORDER BY a.tanggal DESC, a.nomor DESC")
    public List<PemakaianBarangConsumable> selectAll() throws RuntimeException;

    @Select("SELECT a.*, b.nama as created_name, c.gudang as nama_gudang, d.nama as modified_name "
            + " FROM pemakaian_barang_consumable a "
            + " INNER JOIN pegawai b ON a.created_by=b.id_pegawai"
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.modified_by=d.id_pegawai "
            + " ORDER BY a.tanggal DESC, a.nomor DESC "
            + " WHERE a.status='A'"
            + " ORDER BY a.date DESC, a.nomor DESC")
    public List<PemakaianBarangConsumable> selectApprove() throws RuntimeException;

    @Select("SELECT a.*, b.nama as created_name, c.gudang as nama_gudang, d.nama as modified_name "
            + " FROM pemakaian_barang_consumable a "
            + " INNER JOIN pegawai b ON a.created_by=b.id_pegawai"
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.modified_by=d.id_pegawai "
            + " WHERE id=${id}"
            + " ORDER BY a.tanggal DESC, a.nomor DESC ")
    public PemakaianBarangConsumable selectOne(@Param("id") Integer id) throws RuntimeException;

    @Insert("INSERT INTO pemakaian_barang_consumable "
            + "(nomor, tanggal, keterangan, id_gudang, status, created_by, created_date ) "
            + "VALUES "
            + "(#{nomor},  #{tanggal}, #{keterangan}, #{id_gudang}, #{status}, #{created_by}, LOCALTIMESTAMP) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(PemakaianBarangConsumable pemakaianBarangConsumable) throws RuntimeException;

    @Delete("DELETE FROM pemakaian_barang_consumable where id = #{id}")
    public int delete(@Param("id") Integer id) throws RuntimeException;

    @Delete("DELETE FROM pemakaian_barang_consumable_detail where id = #{id}")
    public int deleteDetail(@Param("id") Integer id) throws RuntimeException;

    @Update("UPDATE pemakaian_barang_consumable  SET "
            + " nomor = #{nomor}, "
            + " tanggal = #{tanggal}, "
            + " keterangan = #{keterangan}, "
            + " id_gudang = #{id_gudang}, "
            + " status = #{status}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE id = #{id}")
    public int update(PemakaianBarangConsumable pemakaianBarangConsumable) throws RuntimeException;

    @Insert("INSERT INTO pemakaian_barang_consumable_detail "
            + "(id, id_barang_consumable,  urut, qty,  note, id_kendaraan) "
            + "VALUES "
            + "(#{id}, #{id_barang_consumable}, #{urut}, #{qty}, #{note}, #{id_kendaraan}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertDetail(PemakaianBarangConsumableDetail pemakaianBarangConsumableDetail) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang as nama_barang_consumable, b.id_satuan_kecil as nama_satuan, b.material_code, c.nopol as nopol"
            + " FROM pemakaian_barang_consumable_detail a"
            + " INNER JOIN barang_consumable b ON a.id_barang_consumable=b.id"
            + " LEFT JOIN kendaraan c ON a.id_kendaraan=c.id"
            + " WHERE a.id=#{id}")
    public List<PemakaianBarangConsumableDetail> selectAllDetail(@Param("id") Integer id) throws RuntimeException;

    @Select("SELECT MAX(RIGHT(nomor,3)) "
            + " FROM pemakaian_barang_consumable "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

}
