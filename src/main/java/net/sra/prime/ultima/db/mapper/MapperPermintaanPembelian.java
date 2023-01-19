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
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianPesan;
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
public interface MapperPermintaanPembelian {

    @SelectProvider(type = ProviderPermintaanPembelian.class, method = "SelectAll")
    public List<PermintaanPembelian> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai") String id_pegawai,
            @Param("status") Boolean status,
            @Param("statusnya") Character statusnya,
            @Param("id_jabatan") Integer id_jabatan
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderPermintaanPembelian.class, method = "SelectAllMaintenance")
    public List<PermintaanPembelian> selectAllMaintenance(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir
    ) throws RuntimeException;

    @Insert("INSERT INTO permintaan_pembelian "
            + "(no_pp, tanggal, dibuat, disetujui, checked_name, keterangan,  id_perusahaan, approved2,jenis,total,spv) "
            + "VALUES (#{no_pp}, #{tanggal}, #{dibuat}, #{disetujui}, #{checked_name},  #{keterangan},  #{id_perusahaan}, #{approved2},#{jenis},#{total},#{spv}) ")
    public int insert(PermintaanPembelian permintaanPembeliaan) throws RuntimeException;

    @Delete("DELETE FROM permintaan_pembelian WHERE no_pp = #{no_pp}")
    public int delete(@Param("no_pp") String no_pp) throws RuntimeException;

    @Select("SELECT  a.*, b.nama as dibuat_nama,c.jabatan, d.nama as nama_approved,e.jabatan as jabatan_approved, f.nama as spv_name, g.jabatan as spv_jabatan,  "
            + " h.nama as nama_approved2, i.jabatan as jabatan_approved2, j.nama as nama_cheked, k.jabatan as jabatan_cheked"
            + " FROM permintaan_pembelian a  "
            + " INNER JOIN pegawai b ON a.dibuat=b.id_pegawai "
            + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
            + " INNER JOIN pegawai d ON a.disetujui=d.id_pegawai "
            + " INNER JOIN master_jabatan e ON d.id_jabatan_new=e.id_jabatan "
            + " LEFT JOIN pegawai f ON a.spv=f.id_pegawai "
            + " LEFT JOIN master_jabatan g ON f.id_jabatan_new=g.id_jabatan "
            + " LEFT JOIN pegawai h ON a.approved2=h.id_pegawai "
            + " LEFT JOIN master_jabatan i ON h.id_jabatan_new=i.id_jabatan "
            + " LEFT JOIN pegawai j ON a.checked_name=j.id_pegawai "
            + " LEFT JOIN master_jabatan k ON j.id_jabatan_new=k.id_jabatan "
            + " WHERE a.no_pp=#{no_pp}")
    public PermintaanPembelian selectOne(@Param("no_pp") String no_pp) throws RuntimeException;

    @Update("UPDATE permintaan_pembelian  SET "
            + " tanggal = #{tanggal}, "
            + " dibuat = #{dibuat}, "
            + " disetujui = #{disetujui}, "
            + " diterima = #{diterima}, "
            + " checked_name = #{checked_name},"
            + " keterangan = #{keterangan},"
            + " jenis = #{jenis},"
            + " total = #{total}"
            + " WHERE no_pp = #{no_pp} ")
    public int update(PermintaanPembelian permintaanPembeliaan) throws RuntimeException;
    
    
    @Update("UPDATE permintaan_pembelian  SET "
            + " tanggal = #{tanggal}, "
            + " dibuat = #{dibuat}, "
            + " disetujui = #{disetujui}, "
            + " diterima = #{diterima}, "
            + " checked_name = #{checked_name},"
            + " keterangan = #{keterangan},"
            + " jenis = #{jenis},"
            + " total = #{total},"
            + " status_dibuat=#{status_dibuat},"
            + " status_cheked=#{status_cheked},"
            + " status_approved=#{status_approved},"
            + " status_approved2=#{status_approved2}"
            + " WHERE no_pp = #{no_pp} ")
    public int updateMaintenance(PermintaanPembelian permintaanPembeliaan) throws RuntimeException;
    

    @Update("UPDATE permintaan_pembelian SET status_dibuat=true,create_date=LOCALTIMESTAMP WHERE no_pp=#{no_pp}")
    public int updateSend(@Param("no_pp") String no_pp) throws RuntimeException;

    @Update("UPDATE permintaan_pembelian SET spv_status=#{setuju}, spv_date=LOCALTIMESTAMP WHERE no_pp=#{no_pp}")
    public int updateSpv(@Param("no_pp") String no_pp, @Param("setuju") Boolean setuju) throws RuntimeException;
    
    @Update("UPDATE permintaan_pembelian SET status_cheked=#{setuju}, tgl_cheked=LOCALTIMESTAMP WHERE no_pp=#{no_pp}")
    public int updateAtasan(@Param("no_pp") String no_pp, @Param("setuju") Boolean setuju) throws RuntimeException;

    @Update("UPDATE permintaan_pembelian SET status_approved=#{setuju}, tgl_disetujui=LOCALTIMESTAMP WHERE no_pp=#{no_pp}")
    public int updateDirektur(@Param("no_pp") String no_pp, @Param("setuju") Boolean setuju) throws RuntimeException;
    
    @Update("UPDATE permintaan_pembelian SET status_approved2=#{setuju}, tgl_approved2=LOCALTIMESTAMP WHERE no_pp=#{no_pp}")
    public int updateKeuangan(@Param("no_pp") String no_pp, @Param("setuju") Boolean setuju) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.no_pp,1,3)) "
            + " FROM permintaan_pembelian a "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax( @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    @Insert("INSERT INTO permintaan_pembelian_pesan "
            + " (no_pp, pesan, dari) "
            + " VALUES "
            + " (#{no_pp}, #{pesan}, #{dari})")
    public int insertPesan(PermintaanPembelianPesan permintaanPembelianPesan) throws RuntimeException;

    @Select("SELECT a.*,b.nama FROM permintaan_pembelian_pesan a "
            + " INNER JOIN pegawai b ON b.id_pegawai=a.dari "
            + " WHERE a.no_pp=#{no_pp} ORDER BY id_pesan")
    public List<PermintaanPembelianPesan> selectAllPesan(@Param("no_pp") String no_pp) throws RuntimeException;

}
