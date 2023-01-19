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
import net.sra.prime.ultima.db.provider.ProviderInternalTransfer;
import net.sra.prime.ultima.entity.InternalTransfer;
import net.sra.prime.ultima.entity.InternalTransferDetail;
import net.sra.prime.ultima.entity.InternalTransferHpp;
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
public interface MapperInternalTransfer {

     @SelectProvider(type = ProviderInternalTransfer.class, method = "SelectAll")
    public List<InternalTransfer> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_pegawai_asal") String id_gudang_asal,
            @Param("id_pegawai_tujuan") String id_gudang_tujuan,
            @Param("status") Character status,
            @Param("jenis") Character jenis
    ) throws RuntimeException;

    @Insert("INSERT INTO internal_transfer "
            + "(nomor, tanggal, pengirim, penerima, approve_by, status, tanggal_kirim, "
            + "id_gudang_asal, id_gudang_tujuan,  nomor_io, keterangan, driver, sipb, jam_kirim, telpon,create_date, "
            + "istransporter,idtransporter,biaya,transporter,id_jenis,nopol,id_kendaraan,total) "
            + "VALUES "
            + "(#{nomor}, #{tanggal}, #{pengirim}, #{penerima}, #{approve_by},  #{status},  #{tanggal_kirim} "
            + ", #{id_gudang_asal}, #{id_gudang_tujuan},  #{nomor_io}, #{keterangan}, #{driver}, #{sipb}, #{jam_kirim}, #{telpon},LOCALTIMESTAMP, "
            + "#{istransporter},#{idtransporter},#{biaya},#{transporter},#{id_jenis},#{nopol},#{id_kendaraan},#{total})")
    public int insert(InternalTransfer internal_transfer) throws RuntimeException;

    
    @Delete("DELETE FROM internal_transfer WHERE nomor = #{nomor}")
    public int delete(@Param("nomor") String nomor) throws RuntimeException;

    
    @Select("SELECT a.*, b.gudang as gudang_asal, c.gudang as gudang_tujuan,f.send_by_date as modified_date_io, "
            + " d.nama as nama_penerima, e.nomor as no_cancel, e.tanggal as tgl_cancel, e.pesan,g.nama as nama_pengirim  "
            + " FROM internal_transfer a "
            + " INNER JOIN gudang b ON a.id_gudang_asal=b.id_gudang "
            + " INNER JOIN gudang c ON a.id_gudang_tujuan=c.id_gudang "
            + " INNER JOIN pegawai d ON a.penerima=d.id_pegawai "
            + " INNER JOIN internal_order f ON a.nomor_io=f.nomor_io "
            + " INNER JOIN pegawai g ON a.pengirim=g.id_pegawai "
            + " LEFT JOIN internaltransfer_cancel e ON a.nomor=e.no_it "
            + " WHERE a.nomor=#{id_lama}")
    public InternalTransfer selectOne(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE internal_transfer  SET "
            + " tanggal = #{tanggal}, "
            + " pengirim = #{pengirim}, "
            + " penerima = #{penerima}, "
            + " approve_by = #{approve_by}, "
            + " status = #{status}, "
            + " tanggal_kirim = #{tanggal_kirim}, "
            + " jam_kirim = #{jam_kirim}, "
            + " id_gudang_asal = #{id_gudang_asal}, "
            + " id_gudang_tujuan = #{id_gudang_tujuan}, "
            + " nomor_io = #{nomor_io}, "
            + " driver = #{driver}, "
            + " keterangan = #{keterangan}, "
            + " sipb = #{sipb}, "
            + " telpon = #{telpon}, "
            + " tanggal_terima = #{tanggal_terima}, "
            + " jam_terima = #{jam_terima}, "
            + " istransporter = #{istransporter}, "
            + " idtransporter = #{idtransporter}, "
            + " transporter = #{transporter}, "
            + " biaya = #{biaya}, "
            + " id_jenis = #{id_jenis}, "
            + " nopol = #{nopol}, "
            + " id_kendaraan = #{id_kendaraan}, "
            + " total = #{total} "
            + " WHERE nomor = #{nomor} ")
    public int update(InternalTransfer internal_transfer) throws RuntimeException;
    
    @Update("UPDATE internal_transfer SET "
            + " tanggal_terima=#{tanggal_terima}, "
            + " status='R', "
            + " jam_terima=#{jam_terima}, "
            + " keterangan=#{keterangan} "
            + " WHERE nomor=#{nomor} ")
    public int updateReceipt(InternalTransfer internal_transfer) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.nomor,1,3)) "
            + " FROM internal_transfer a "
            + " INNER JOIN gudang b on a.id_gudang_asal=b.id_gudang"
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND b.id_kantor = #{id_kantor}")
    public String SelectMax(@Param("id_kantor") String id_kantor, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Update("UPDATE internal_transfer  SET "
            + " status = #{status} "
            + " WHERE nomor = #{nomor} ")
    public int updateStatus(@Param("status") Character status,@Param("nomor") String nomor) throws RuntimeException;
    
    /////////////////////////////////// InternalTransfer Detail ////////////////////////////////
    
    @Insert("INSERT INTO internal_transfer_detail "
            + "(nomor, urut,  id_barang, qty, keterangan, terima,catatan) "
            + "VALUES "
            + "(#{nomor}, #{urut}, #{id_barang}, #{qty}, #{keterangan}, #{terima},#{catatan}) ")
    public int insertDetail(InternalTransferDetail internal_transferDetail) throws RuntimeException;

    
    @Delete("DELETE FROM internal_transfer_detail WHERE nomor = #{nomor}")
    public int deleteDetail(@Param("nomor") String nomor) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_barang,  d.satuan_besar "
            + " FROM internal_transfer_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar=d.id_satuan_besar "
            + " WHERE a.nomor=#{id_lama} "
            + " ORDER BY a.urut")
    public List<InternalTransferDetail> selectOneDetail(@Param("id_lama") String id_lama) throws RuntimeException;

    @Update("UPDATE internal_transfer SET status=#{status}, tanggal_terima=#{tanggal_terima} WHERE nomor=#{nomor}")
    public int updateApprove(@Param("nomor") String nomor, @Param("status") Character status,@Param("tanggal_terima") Date tanggal_terima) throws RuntimeException;

    /////////////////////////////////// InternalTransfer HPP ////////////////////////////////
    
    
    @Insert("INSERT INTO internal_transfer_hpp "
            + "(nomor, urut,  id_barang, qtyout, qtysisa, hpp, batch) "
            + "VALUES "
            + "(#{nomor}, #{urut}, #{id_barang}, #{qtyout}, #{qtysisa}, #{hpp}, #{batch}) ")
    public int insertHpp(InternalTransferHpp internalTransferHpp) throws RuntimeException;
    
    @Select("SELECT * FROM internal_transfer_hpp "
            + " WHERE id_barang = #{id_barang} AND qtysisa > 0 AND nomor=#{nomor} "
            + " ORDER BY urut limit 1")
    public InternalTransferHpp selectForUpdate(
            @Param("id_barang") String id_barang, 
            @Param("nomor") String nomor) throws RuntimeException;
    
    
    @Update("UPDATE internal_transfer_hpp  SET "
            + " qtysisa = #{qtysisa} "
            + " WHERE nomor = #{nomor} AND urut = #{urut}")
    public int updateHpp(InternalTransferHpp internalTransferHpp) throws RuntimeException;
    
    @Select("SELECT * FROM internal_transfer_hpp "
            + " WHERE id_barang = #{id_barang}   AND nomor=#{nomor} "
            + " ORDER BY urut ")
    public List<InternalTransferHpp> selectKembalikanStok(
            @Param("id_barang") String id_barang, 
            @Param("nomor") String nomor) throws RuntimeException;
    
    @SelectProvider(type = ProviderInternalTransfer.class, method = "SelectAllInternalTransfer")
    public List<InternalTransferDetail> selectAllInternalTransfer(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderInternalTransfer.class, method = "SelectAllInternalTransferReceipt")
    public List<InternalTransferDetail> selectAllInternalTransferReceipt(
            @Param("id_barang") String id_barang,
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("gudang") String gudang,
            @Param("status") Character status
    ) throws RuntimeException;
    
    @Select("SELECT gudang FROM gudang WHERE id_gudang=#{id_gudang}")
    public String namaGudang (@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    @Select("SELECT isi_satuan FROM barang WHERE id_barang=#{id_barang}")
    public Double isiSatuan(@Param("id_barang") String id_barang) throws RuntimeException;
    
}
