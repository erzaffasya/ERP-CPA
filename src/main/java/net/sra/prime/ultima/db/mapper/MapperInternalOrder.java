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
import net.sra.prime.ultima.entity.InternalOrder;
import net.sra.prime.ultima.entity.InternalOrderDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
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
public interface MapperInternalOrder {

    @SelectProvider(type = ProviderInternalOrder.class, method = "SelectAll")
    public List<InternalOrder> selectAllIO(
            @Param("id_kantor") String id_kantor,
            @Param("id_pegawai") String id_pegawai,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status
            ) throws RuntimeException;

    
    @Insert("INSERT INTO internal_order "
            + "(nomor_io, tanggal, id_kantor, ref_no, order_status, kontak, telpon, email, id_gudang_asal,"
            + "id_gudang_tujuan, approve,status,id_ip,create_date,send_by) "
            + "VALUES "
            + "(#{nomor_io}, #{tanggal}, #{id_kantor}, #{ref_no}, #{order_status},  #{kontak}, #{telpon}, #{email} "
            + ", #{id_gudang_asal}, #{id_gudang_tujuan}, #{approve}, #{status},#{id_ip},LOCALTIMESTAMP,#{send_by})")
    public int insert(InternalOrder internal_order) throws RuntimeException;

    
    @Delete("DELETE FROM internal_order WHERE nomor_io = #{nomor_io}")
    public int delete(@Param("nomor_io") String nomor_io) throws RuntimeException;

    
    @Select("SELECT a.*, b.gudang as gudang_asal, c.gudang gudang_tujuan,d.nama as kantor,e.pesan,e.tanggal as tgl_cancel,f.no_intruksi_po as nomor_ip, f.tanggal as tanggal_ip,  "
            + " g.nama as dibuat_nama,h.jabatan,i.nama as approve_name,j.jabatan as jabatan_approved,k.nama as send_by_name,l.jabatan as send_by_jabatan "
            + " FROM internal_order a "
            + " INNER JOIN gudang b ON a.id_gudang_asal=b.id_gudang "
            + " INNER JOIN gudang c ON a.id_gudang_tujuan=c.id_gudang "
            + " INNER JOIN internal_kantor_cabang d ON a.id_kantor=d.id_kantor_cabang "
            + " INNER JOIN pegawai g ON a.kontak = g.id_pegawai "
            + " INNER JOIN master_jabatan h  ON g.id_jabatan_new=h.id_jabatan "
            + " INNER JOIN pegawai i ON a.approve = i.id_pegawai "
            + " INNER JOIN master_jabatan j  ON i.id_jabatan_new=j.id_jabatan "
            + " LEFT JOIN pegawai k ON a.send_by=k.id_pegawai "
            + " LEFT JOIN master_jabatan l  ON k.id_jabatan_new=l.id_jabatan "
            + " LEFT JOIN pesan_cancel e ON a.nomor_io=e.nomor "
            + " LEFT JOIN intruksi_po f ON a.id_ip=f.id "
            + " WHERE a.nomor_io=#{nomor}")
    public InternalOrder selectOne(@Param("nomor") String nomor) throws RuntimeException;

    
    @Update("UPDATE internal_order  SET status=#{status} WHERE nomor_io=#{nomor_io}")
    public int updateStatus(@Param("nomor_io") String nomor_io, @Param("status") Character status) throws RuntimeException;
    
    @Update("UPDATE internal_order  SET "
            + " tanggal = #{tanggal}, "
            + " ref_no = #{ref_no}, "
            + " order_status = #{order_status}, "
            + " kontak = #{kontak}, "
            + " telpon = #{telpon}, "
            + " email = #{email}, "
            + " id_gudang_asal = #{id_gudang_asal}, "
            + " id_gudang_tujuan = #{id_gudang_tujuan}, "
            + " status = #{status}, "
            + " send_by = #{send_by} "
            + " WHERE nomor_io = #{nomor_io} ")
    public int update(InternalOrder internalOrder) throws RuntimeException;
    
    @Update("UPDATE internal_order SET status=#{status}, approve_tanggal=LOCALTIMESTAMP WHERE nomor_io=#{nomor_io}")
    public int updateApprove(@Param("nomor_io") String nomor_io, @Param("status") Character status) throws RuntimeException;
    
    @Update("UPDATE internal_order SET status=#{status}, send_by_date=LOCALTIMESTAMP WHERE nomor_io=#{nomor_io}")
    public int updateSendBy(@Param("nomor_io") String nomor_io, @Param("status") Character status) throws RuntimeException;
    
    @Update("UPDATE internal_order SET status=#{status}, modified_date=LOCALTIMESTAMP WHERE nomor_io=#{nomor_io}")
    public int updateModifiedBy(@Param("nomor_io") String nomor_io, @Param("status") Character status) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.nomor_io,1,3)) "
            + " FROM internal_order a "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND a.id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    
    /////////////////////////////////// InternalOrder Detail ////////////////////////////////
    @Insert("INSERT INTO internal_order_detail "
            + "(nomor_io, urut,  id_barang, qty, keterangan) "
            + "VALUES "
            + "(#{nomor_io}, #{urut}, #{id_barang}, #{qty}, #{keterangan}) ")
    public int insertDetail(InternalOrderDetail internalOrderDetail) throws RuntimeException;

    
    @Delete("DELETE FROM internal_order_detail WHERE nomor_io = #{nomor_io}")
    public int deleteDetail(@Param("nomor_io") String nomor_io) throws RuntimeException;

    @Select("SELECT a.*, b.nama_barang,  d.satuan_besar,b.isi_satuan "
            + " FROM internal_order_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar=d.id_satuan_besar "
            + " WHERE a.nomor_io=#{id_lama} "
            + " ORDER BY a.urut")
    public List<InternalOrderDetail> selectOneDetail(@Param("id_lama") String id_lama) throws RuntimeException;

    @SelectProvider(type = ProviderInternalOrder.class, method = "SelectAllIp")
    public List<IntruksiPo> selectAllIP(
            @Param("id_pegawai") String id_pegawai,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir
            ) throws RuntimeException;
    
    @Select("SELECT a.*, b.gudang,c.no_forecast, f.nama as create_name,g.nama as modified_name   "
            + " FROM intruksi_po a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " LEFT JOIN forecast c ON a.id_forecast=c.id "
            + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
            + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
            + " WHERE a.id=#{id}")
    public IntruksiPo selectOneIp(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.id_barang,a.sisa/b.isi_satuan as qty, b.nama_barang, c.satuan_besar as satuan_kecil "
            + " FROM intruksi_po_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " WHERE id=#{id} "
            + " ORDER BY urut")
    public List<IntruksiPoDetail> selectAllDetailIp(@Param("id") Integer id) throws RuntimeException;
    
    @Update("UPDATE intruksi_po_detail set diambil=diambil + #{qty},sisa=sisa - #{qty} WHERE id_barang=#{id_barang} AND id=#{id_ip} ")
    public void updateIP(@Param("qty") Double qty,
            @Param("id_barang") String id_barang,
            @Param("id_ip") Integer id_ip) throws RuntimeException;
}
