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
import net.sra.prime.ultima.db.provider.ProviderStokBarang;
import net.sra.prime.ultima.entity.KartuStok;
import net.sra.prime.ultima.entity.Mutasi;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.StokOpname;
import net.sra.prime.ultima.entity.StokOpnameDetil;
import net.sra.prime.ultima.entity.StokValue;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperStokBarang {

    
    @Select("SELECT * FROM stok_barang")
    public List<StokBarang> selectAll() throws RuntimeException;

    
    @Select("SELECT * FROM stok_barang "
            + " WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND HPP > 0  "
            + " ORDER BY tanggal DESC,id_stok DESC limit 1")
    public StokBarang selectForGetHpp(@Param("id_gudang") String id_gudang, @Param("id_barang") String id_barang) throws RuntimeException;
    
    
    @Select("SELECT * FROM stok_barang "
            + " WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND stok > 0  "
            + " ORDER BY tanggal,id_stok limit 1")
    public StokBarang selectForUpdate(@Param("id_gudang") String id_gudang, @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT * FROM stok_barang_opname "
            + " WHERE id_stokopname = #{id} AND id_barang = #{id_barang} AND stok > 0  "
            + " ORDER BY tanggal,id_stok limit 1")
    public StokBarang selectForUpdateTemp(@Param("id") Integer id, @Param("id_barang") String id_barang) throws RuntimeException;
    
    
    @Select("SELECT * FROM stok_barang "
            + " WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND stok > 0 AND reff=#{reff}  "
            + " ORDER BY tanggal,id_stok limit 1")
    public StokBarang selectBackIT(@Param("id_gudang") String id_gudang, 
            @Param("id_barang") String id_barang,
            @Param("reff") String reff) throws RuntimeException;
    
    
    @Select("SELECT sum(a.stok/b.isi_satuan)  "
            + "FROM stok_barang a "
            + "INNER JOIN barang b ON a.id_barang=b.id_barang "
            + "WHERE a.id_gudang=#{id_gudang} "
            + "AND a.id_barang=#{id_barang}")
    public Double SumStok(@Param("id_gudang") String id_gudang,@Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT sum(a.stok)  "
            + "FROM stok_barang a "
            + "INNER JOIN barang b ON a.id_barang=b.id_barang "
            + "WHERE a.id_gudang=#{id_gudang} "
            + "AND a.id_barang=#{id_barang}")
    public Double SumStokSatuanKecil(@Param("id_gudang") String id_gudang,@Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT a.*, b.penampung FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.id_gudang = #{id_gudang} AND a.id_barang = #{id_barang} AND a.persediaan > 0  "
            + " ORDER BY tanggal,id_stok limit 1")
    public StokBarang selectForUpdatePersediaan(@Param("id_gudang") String id_gudang, @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT a.*, b.penampung FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.id_gudang = #{id_gudang} AND a.id_barang = #{id_barang} AND a.persediaan > 0  AND reff=#{reff} "
            + " ORDER BY tanggal,id_stok limit 1")
    public StokBarang selectBackITPersediaan(@Param("id_gudang") String id_gudang, 
            @Param("id_barang") String id_barang,
            @Param("reff") String Reff) throws RuntimeException;

//    @Select("SELECT a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil , c.kategori_barang, sum(a.stok) as stokkecil, b.isi_satuan "
//            + " FROM stok_barang a "
//            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
//            + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
//            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
//            + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
//            + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
//            + " WHERE a.id_gudang = #{id_gudang} "
//            + " GROUP BY a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil,  c.kategori_barang, b.isi_satuan "
//            + " ORDER BY b.nama_barang ")
//    public List<StokBarang> selectStok(@Param("id_gudang") String id_gudang) throws RuntimeException;
//    
    
    @SelectProvider(type = ProviderStokBarang.class, method = "selectStok")
    public List<StokBarang> selectStok(
            @Param("id_gudang") String id_gudang,
            @Param("id_satuan") String id_satuan,
            @Param("id_kategori") String id_kategori
            ) throws RuntimeException;
    
    
    @Select("SELECT a.id_barang,a.hpp,a.stok, b.nama_barang, d.satuan_besar, e.satuan_kecil , c.kategori_barang, b.isi_satuan "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
            + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
            + " WHERE a.id_gudang = #{id_gudang} AND a.stok > 0 "
            + " ORDER BY b.nama_barang ")
    public List<StokBarang> selectStokHpp(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    
    @Select("SELECT a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil ,  sum(a.stok) as stokkecil, b.isi_satuan "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
            + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
            + " WHERE f.parent = #{id_gudang} OR f.id_gudang = #{id_gudang}"
            + " GROUP BY a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil,  c.kategori_barang, b.isi_satuan "
            + " ORDER BY b.nama_barang ")
    public List<StokBarang> selectStokParent(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    ///////////////////////////////// Persediaan ////////////////////////////////////////////////
    @Select("SELECT a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil , c.kategori_barang, sum(a.persediaan) as stokkecil, b.isi_satuan, a.hpp "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
            + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
            + " WHERE a.id_gudang = #{id_gudang} "
            + " GROUP BY a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil,  c.kategori_barang, b.isi_satuan, hpp "
            + " ORDER BY b.nama_barang ")
    public List<StokBarang> selectStokPersediaan(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    
    @Select("SELECT a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil ,  sum(a.persediaan) as stokkecil, b.isi_satuan, a.hpp "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
            + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
            + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
            + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
            + " WHERE f.parent = #{id_gudang} "
            + " GROUP BY a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil,  c.kategori_barang, b.isi_satuan, a.hpp "
            + " ORDER BY b.nama_barang ")
    public List<StokBarang> selectStokParentPersediaan(@Param("id_gudang") String id_gudang) throws RuntimeException;

    
    ///////////////////////////////END Persediaan///////////////////////////////////////////////
    @Insert("INSERT INTO stok_barang "
            + "(id_gudang, id_barang, stok, tanggal, id_perusahaan, hpp, batch, reff, persediaan) "
            + "VALUES "
            + "(#{id_gudang}, #{id_barang}, #{stok}, #{tanggal}, #{id_perusahaan}, #{hpp}, #{batch}, #{reff}, #{persediaan}) ")
    public int insert(StokBarang stokBarang) throws RuntimeException;
    
    @Insert("INSERT INTO stok_barang_opname "
            + "(id_gudang, id_barang, stok, tanggal, hpp, batch, reff, persediaan,id_stokopname,isi_satuan) "
            + "VALUES "
            + "(#{id_gudang}, #{id_barang}, #{stok}, #{tanggal},  #{hpp}, #{batch}, #{reff}, #{persediaan},#{id_stokopname},#{isi_satuan}) ")
    public int insertTemp(StokBarang stokBarang) throws RuntimeException;

    
    @Delete("DELETE FROM stok_barang WHERE reff = #{reff}")
    public int delete(@Param("reff") String reff) throws RuntimeException;

    
    @Update("UPDATE stok_barang  SET "
            + " stok = stok + #{stok} "
            + " WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND id_stok = #{id_stok}")
    public int update(StokBarang stokBarang) throws RuntimeException;
    
    @Update("UPDATE stok_barang_opname  SET "
            + " stok = stok + #{stok} "
            + " WHERE id_stok = #{id_stok} AND id_stokopname=#{id_stokopname}")
    public int updateTemp(StokBarang stokBarang) throws RuntimeException;
    
    
    @Update("UPDATE stok_barang  SET "
            + " persediaan = persediaan + #{persediaan} "
            + " WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND id_stok = #{id_stok}")
    public int updatePersediaan(StokBarang stokBarang) throws RuntimeException;

    // update hpp untuk semua no po yang sama
    @Update("UPDATE stok_barang a SET hpp=#{hpp} "
            + "FROM (select no_penerimaan FROM penerimaan_gudang WHERE nomor_po=#{nomor_po}) b "
            + "WHERE a.id_barang=#{id_barang} AND a.reff=b.no_penerimaan")
    public int updateHPP(@Param("hpp") Double hpp, 
            @Param("nomor_po") String nomor_po, 
            @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Update("UPDATE stok_barang a SET hpp=#{hpp} "
            + "WHERE a.id_barang=#{id_barang} AND a.reff=#{reff}")
    public int updateHPPGeneral(@Param("hpp") Double hpp, 
            @Param("reff") String reff, 
            @Param("id_barang") String id_barang) throws RuntimeException;
    
    @Update("UPDATE stok_barang  SET "
            + " stok = stok + #{stok} "
            + " WHERE  id_barang = #{id_barang} AND reff = #{reff}")
    public int updateByPenerimaan(@Param("stok") Double stok, 
            @Param("id_barang") String id_barang,
            @Param("reff") String reff)
            throws RuntimeException;
    
    
    ////////////////////////// Stok Value /////////////////////
    
    @Insert("INSERT INTO stok_value "
            + "(years, id_barang, id_gudang,db0,cr0) "
            + "VALUES "
            + "(#{years}, #{id_barang}, #{id_gudang},#{db0},#{cr0}) ")
    public int insertMutasi(StokValue stokValue) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderStokBarang.class, method = "UpdateStokValue")
    public void updateStokValue(
            @Param("nomor") Integer nomor,
            @Param("nilai") Double nilai,
            @Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang,
            @Param("years") String years,
            @Param("status") Character status
            ) throws RuntimeException;

    @Select("SELECT * FROM stok_value WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND years = #{years}" )
    public StokValue selectOneStokValue(@Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang,
            @Param("years") String years) throws RuntimeException;

    
    @Select("SELECT a.*, b.nama_barang, c.satuan_besar, b.isi_satuan "
            + "FROM stok_value a "
            + "INNER JOIN barang b ON a.id_barang=b.id_barang "
            + "INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + "WHERE "
            + "a.id_gudang=#{id_gudang} AND a.years=#{tahun}")
    public List<StokValue> selectAllStokValue(@Param("id_gudang") String id_gudang, @Param("tahun") String tahun) throws RuntimeException;
    
    @Update("UPDATE stok_value set "
            + "db13=db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12, "
            + "cr13=cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12 "
            + "WHERE years=#{years} AND id_gudang=#{id_gudang} AND id_barang=#{id_barang}")
    public int updateSaldoAkhir(@Param("years") String years, 
            @Param("id_gudang") String id_gudang, 
            @Param("id_barang") String id_barang) throws RuntimeException;
    
    
    @Update("UPDATE stok_value set "
            + "db0=#{db0}, "
            + "cr0=#{cr0} "
            + "WHERE years=#{years} AND id_gudang=#{id_gudang} AND id_barang=#{id_barang}")
    public int updateSaldoAwal(@Param("years") String years, 
            @Param("id_gudang") String id_gudang, 
            @Param("id_barang") String id_barang,
            @Param("db0") Double db0,
            @Param("cr0") Double cr0) throws RuntimeException;
    
    
    @SelectProvider(type = ProviderStokBarang.class, method = "selectKartuStok")
    public List<KartuStok> kartuStok(
            @Param("nomor") Integer nomor,
            @Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang,
            @Param("years") String years,
            @Param("tahun") Integer tahun,
            @Param("tglmulai") Date tglmulai
            ) throws RuntimeException;
    
    @SelectProvider(type = ProviderStokBarang.class, method = "stockMovementDaily")
    public List<Mutasi> stockMovementDaily(
            @Param("nomor") Integer nomor,
            @Param("id_gudang") String id_gudang,
            @Param("years") String years,
            @Param("tglmulai") Date tglmulai,
            @Param("tglselesai") Date tglselesai
            ) throws RuntimeException;
    
/** Stok Opname **/
    
    @Insert("INSERT INTO stok_opname "
            + " (id_gudang,tanggal,nomor,keterangan,status,create_date,create_by) "
            + " VALUES (#{id_gudang},#{tanggal}, #{nomor}, #{keterangan}, #{status}, LOCALTIMESTAMP, #{create_by})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertStokOpname(StokOpname stokOpname) throws RuntimeException;
    
    @Insert("INSERT INTO stok_opname_detil "
            + " (id,id_barang,qty,id_gudang,isi_satuan) "
            + " SELECT #{id_stokopname},id_barang,sum(stok),id_gudang,isi_satuan "
            + " FROM stok_barang_opname a "
            + " WHERE "
            + " a.id_gudang= #{id_gudang} AND id_stokopname=#{id_stokopname} "
            + " GROUP BY id_barang,id_gudang,isi_satuan")
    public int insertStokOpnameDetil(@Param("id_gudang") String id_gudang,
            @Param("id_stokopname") Integer id_stokopname) throws RuntimeException;
    
    @Update("UPDATE stok_opname_detil SET selisih= qty_stok - qty WHERE id=#{id_stokopname}")
    public int updateSelish(@Param("id_stokopname") Integer id_stokopname) throws RuntimeException;
    
    @Insert("INSERT INTO stok_barang_opname "
            + " (id_stokopname,id_gudang,id_barang,stok,tanggal,hpp,batch,id_stok,reff,persediaan,isi_satuan) "
            + " SELECT #{id_stokopname},a.id_gudang,a.id_barang,a.stok,a.tanggal,a.hpp,a.batch,a.id_stok,a.reff,a.persediaan,b.isi_satuan "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang"
            + " WHERE "
            + " id_gudang = #{id_gudang} AND stok > 0")
    public int insertStokbesar0(@Param("id_gudang") String id_gudang,
            @Param("id_stokopname") Integer id_stokopname) throws RuntimeException;
    
    
    @Insert("INSERT INTO stok_barang_opname "
            + " (id_stokopname,id_gudang,id_barang,stok,tanggal,hpp,batch,id_stok,reff,persediaan,isi_satuan) "
            + " SELECT #{id_stokopname},a.id_gudang,a.id_barang,a.stok,a.tanggal,a.hpp,a.batch,a.id_stok,a.reff,a.persediaan,b.isi_satuan "
            + " FROM stok_barang a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE "
            + " a.id_gudang = #{id_gudang} AND a.stok=0 "
            + " AND a.tanggal = (SELECT MAX(c.tanggal) FROM stok_barang c WHERE a.id_gudang=c.id_gudang AND a.id_barang=c.id_barang) ORDER BY id_barang")
    public int insertStok0(@Param("id_gudang") String id_gudang,
            @Param("id_stokopname") Integer id_stokopname) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(a.nomor,0,3)) "
            + " FROM stok_opname a "
            + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang"
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}"
            + " AND b.id_gudang = #{id_gudang}")
    public String SelectMax(@Param("id_gudang") String id_gudang, @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Select("SELECT SUM(stok*hpp)  FROM stok_barang_opname WHERE id_stokopname=#{id_stokopname} AND stok > 0")
    public Double totalPersediaan(@Param("id_stokopname") Integer id_stokopname) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama_barang,c.satuan_besar,d.satuan_kecil "
            + " FROM stok_barang_opname a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.id_stokopname=#{id} AND stok > 0 "
            + " ORDER BY a.id_barang")
    public List<StokBarang> selectAllStokOpnameBarang(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.gudang FROM stok_opname a "
            + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
            + " WHERE a.id=#{id} "
    )
    public StokOpname selectOneStokOpname(@Param("id")Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama_barang as barang FROM stok_opname_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.id=#{id} AND a.id_barang=#{id_barang} "
    )
    public StokOpnameDetil selectOneStokOpnameDetil(@Param("id")Integer id,@Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama_barang as barang,c.satuan_besar,d.satuan_kecil FROM stok_opname_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.id=#{id} "
            + " ORDER BY b.nama_barang"
    )
    public List<StokOpnameDetil> selectDetilStokOpname(@Param("id")Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama_barang as barang,c.satuan_besar,d.satuan_kecil,b.isi_satuan "
            + " FROM stok_opname_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.id=#{id} AND selisih < 0"
            + " ORDER BY b.nama_barang"
    )
    public List<StokOpnameDetil> selectSelishkurang(@Param("id")Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama_barang as barang,c.satuan_besar,d.satuan_kecil,b.isi_satuan "
            + " FROM stok_opname_detil a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
            + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil=d.id_satuan_kecil "
            + " WHERE a.id=#{id} AND selisih > 0"
            + " ORDER BY b.nama_barang"
    )
    public List<StokOpnameDetil> selectSelishlebih(@Param("id")Integer id) throws RuntimeException;
    
    @Select("SELECT COUNT(*) FROM stok_opname WHERE id_gudang=#{id_gudang} AND (status='U' OR status='D')")
    public int selectCountStokOPname(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    @SelectProvider(type = ProviderStokBarang.class, method = "selectAllSo")
    public List<StokOpname> selectAllStokOpname(
            @Param("tglmulai") Date tglmulai,
            @Param("tglselesai") Date tglselesai,
            @Param("status") Character status,
            @Param("id_pegawai") String id_pegawai
            ) throws RuntimeException;
    
    @Update("UPDATE stok_opname_detil  SET qty_stok = #{qty_stok} * isi_satuan, selisih = (#{qty_stok} * isi_satuan) - qty, notes = #{notes} WHERE  id_barang = #{id_barang} AND id = #{id}")
    public int updateStokDetil(@Param("qty_stok") Double qty_stok, 
            @Param("id_barang") String id_barang,
            @Param("id") Integer id,
            @Param("notes") String notes)
            throws RuntimeException;
    
    @Update("UPDATE stok_opname SET total=#{total} WHERE id=#{id}")
    public int updateTotalStokOpname(@Param("id") Integer id,@Param("total") Double total) throws RuntimeException;
    
    @Update("UPDATE stok_opname_detil  SET  qty_stok = 0  WHERE   id = #{id}")
    public int updateStokDetilToZero(
            @Param("id") Integer id)
            throws RuntimeException;
    
    @Update("UPDATE stok_opname  SET  isupload = #{isupload}  WHERE   id = #{id}")
    public int updateIsUpload(
            @Param("id") Integer id,@Param("isupload") Boolean isupload)
            throws RuntimeException;
    
    @Update("UPDATE stok_opname  SET  status = #{status}  WHERE   id = #{id}")
    public int updateStatusStokOpname(
            @Param("id") Integer id,@Param("status") Character status)
            throws RuntimeException;
    
    @Update("UPDATE stok_opname  SET  "
            + " status = #{status},approved_by=#{approved_by},approved_date=LOCALTIMESTAMP,total=#{total}  "
            + " WHERE   id = #{id}")
    public int updateApproveStokOpname(StokOpname stokOpname)
            throws RuntimeException;
    
    @Update("UPDATE stok_opname  SET  "
            + " status = #{status},upload_by=#{upload_by},upload_date=LOCALTIMESTAMP,isupload=false,qtyupload=qtyupload + 1  "
            + " WHERE   id = #{id}")
    public int updateUploadStokOpname(StokOpname stokOpname) throws RuntimeException;
    
    @Update("UPDATE stok_opname_detil  SET  isreason = true  WHERE   id = #{id} AND qty != qty_stok")
    public int updateIsReason(
            @Param("id") Integer id)
            throws RuntimeException;
    @Update("UPDATE stok_barang_opname a SET hpp= b.hpp FROM stok_barang b WHERE a.id_stok=b.id_stok AND a.HPP=0 AND a.id_stokopname=#{id_stokopname}")
    public int updateHPPFromZero(@Param("id_stokopname") Integer id_stokopname) throws RuntimeException;
    
    
    @Select("SELECT hpp FROM stok_barang_opname WHERE id_stokopname=#{id} AND id_barang=#{id_barang} ORDER BY tanggal DESC LIMIT 1")
    public Double getHpp(@Param("id") Integer id,@Param("id_barang") String id_barang) throws RuntimeException;
    
    @Select("SELECT blind FROM gudang WHERE id_gudang=#{id_gudang}")
    public Boolean cekBlind(@Param("id_gudang") String id_gudang) throws RuntimeException;
    
    @Delete("DELETE FROM stok_barang_opname WHERE id_stokopname = #{id}")
    public int deleteStokBarangOpname(@Param("id") Integer id) throws RuntimeException;
    
    @Delete("DELETE FROM stok_opname_detil WHERE id = #{id}")
    public int deleteStokOpnameDetil(@Param("id") Integer id) throws RuntimeException;
    
    @Delete("DELETE FROM stok_opname WHERE id = #{id}")
    public int deleteStokOpname(@Param("id") Integer id) throws RuntimeException;
    
}
