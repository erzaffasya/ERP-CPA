/*
 * Copyright 2016 JoinFaces.
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
import net.sra.prime.ultima.entity.ReturnPembelianGudang;
import net.sra.prime.ultima.entity.ReturnPembelianGudangDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperReturnPembelianGudang {

    public static final String SELECTALL = "SELECT *, b.supplier as nama_supplier  FROM return_pembelian_gudang a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id";
   @Select(SELECTALL)
    public List<ReturnPembelianGudang> selectAll();

    public static final String INSERT = "INSERT INTO return_pembelian_gudang "
            + "(no_return_pembelian_gudang, tanggal, id_supplier, id_gudang, referensi, keterangan,  status, nomor_penerimaan,  create_by, approve_by, id_perusahaan) "
            + "VALUES (#{no_return_pembelian_gudang}, #{tanggal}, #{id_supplier}, #{id_gudang}, #{referensi},  #{keterangan},  #{status},  #{nomor_penerimaan}, #{create_by}, #{approve_by}, #{id_perusahaan})";
            
    @Insert(INSERT)
    @Transactional(readOnly = false)
    public int insert(ReturnPembelianGudang retungPembelianGudang) throws Exception;

    public static final String DELETE = "DELETE FROM return_pembelian_gudang WHERE no_return_pembelian_gudang = #{no_return_pembelian_gudang}";
    @Delete(DELETE)
    @Transactional(readOnly = false)
    public int delete(@Param("no_return_pembelian_gudang") String no_return_pembelian_gudang) throws Exception;

    public static final String SELECTONE = "SELECT a.*, b.supplier as nama_supplier "
            + " FROM return_pembelian_gudang a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id"
            + " WHERE a.no_return_pembelian_gudang=#{id_lama}";

    @Select(SELECTONE)
    public ReturnPembelianGudang selectOne(@Param("id_lama") String id_lama);

    public static final String UPDATE = "UPDATE return_pembelian_gudang  SET "
            + " tanggal = #{tanggal}, "
            + " id_supplier = #{id_supplier}, "
            + " id_gudang = #{id_gudang}, "
            + " keterangan = #{keterangan}, "
            + " referensi = #{referensi}, "
            + " nomor_penerimaan = #{nomor_penerimaan} "
            + " WHERE no_return_pembelian_gudang = #{no_return_pembelian_gudang} ";

    @Update(UPDATE)
    @Transactional(readOnly = false)
    public int update(ReturnPembelianGudang retungPembelianGudang) throws Exception;

    public static final String SELECTMAX = "SELECT MAX(SUBSTR(a.no_return_pembelian_gudang,1,3)) "
            + " FROM return_pembelian_gudang a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang"
            + " WHERE date_part('year', tanggal)=date_part('year', CURRENT_DATE) "
            + " AND b.id_gudang = #{id_gudang}";

    @Select(SELECTMAX)
    public String SelectMax(@Param("id_gudang") String id_gudang);

    /////////////////////////////// Detail //////////////////
    
    public static final String INSERTDETAIL = "INSERT INTO return_pembelian_gudang_detail "
            + "(no_return_pembelian_gudang, urut,  id_barang, qty, batch, keterangan) "
            + "VALUES "
            + "(#{no_return_pembelian_gudang}, #{urut}, #{id_barang}, #{qty},  #{batch}, #{keterangan}) ";

    @Insert(INSERTDETAIL)
    @Transactional(readOnly = false)
    public int insertDetail(ReturnPembelianGudangDetail retungPembelianGudangDetail) throws Exception;

    public static final String DELETEDETAIL = "DELETE FROM return_pembelian_gudang_detail WHERE no_return_pembelian_gudang = #{no_return_pembelian_gudang}";

    @Delete(DELETEDETAIL)
    @Transactional(readOnly = false)
    public int deleteDetail(@Param("no_return_pembelian_gudang") String no_return_pembelian_gudang) throws Exception;

    public static final String SELECTONEDETAIL = "SELECT a.*, b.nama_barang, c.satuan_kecil "
            + " FROM return_pembelian_gudang_detail a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil"
            + " WHERE a.no_return_pembelian_gudang=#{id_lama} "
            + " ORDER BY a.urut";

    @Select(SELECTONEDETAIL)
    public List<ReturnPembelianGudangDetail> selectOneperPenerimaanDetail(@Param("id_lama") String id_lama);

    @Select("SELECT SUM(a.qty) FROM return_pembelian_gudang_detail a "
            + "INNER JOIN return_pembelian_gudang b ON a.no_return_pembelian_gudang=b.no_return_pembelian_gudang "
            + "WHERE b.nomor_po=#{nomor_po} "
            + "AND a.id_barang=#{id_barang}")
    public Double jumlahQty(@Param("nomor_po") String nomor_po, @Param("id_barang") String id_barang);

}
