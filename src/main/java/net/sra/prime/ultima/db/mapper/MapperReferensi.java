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

import net.sra.prime.ultima.entity.Gudang;
import java.util.List;
import net.sra.prime.ultima.entity.Account;
import net.sra.prime.ultima.entity.Agama;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.Family;
import net.sra.prime.ultima.entity.HrPegawai;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.KategoriBarang;
import net.sra.prime.ultima.entity.KategoriCustomer;
import net.sra.prime.ultima.entity.KategoriSupplier;
import net.sra.prime.ultima.entity.MataUang;
import net.sra.prime.ultima.entity.SatuanBesar;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import net.sra.prime.ultima.entity.SatuanKecil;
import net.sra.prime.ultima.entity.Sektor;
import net.sra.prime.ultima.entity.finance.LoanType;
import net.sra.prime.ultima.entity.hr.HubunganKeluarga;
import net.sra.prime.ultima.entity.hr.SettingApproval;
import net.sra.prime.ultima.entity.hr.StatusAbsen;
import net.sra.prime.ultima.entity.kpi.Category;
import net.sra.prime.ultima.entity.kpi.Unit;

/**
 *
 * @author Syamsu
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperReferensi {

    //// Master Gudang ////
    @Select("SELECT a.*,c.nama as nama_kontak, b.nama as kantor,d.nama as dsm_name "
            + " FROM gudang a "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor=b.id_kantor_cabang "
            + " LEFT JOIN pegawai c ON a.id_kontak=c.id_pegawai "
            + " LEFT JOIN pegawai d ON a.dsm=d.id_pegawai "
            + " ORDER BY id_gudang")
    public List<Gudang> selectAllGudang() throws RuntimeException;

    
    @Select("SELECT * FROM gudang WHERE id_kantor=#{id_kantor}")
    public List<Gudang> selectGudangKantor(@Param("id_kantor") String id_kantor) throws RuntimeException;
    
    @Select("SELECT * FROM agama")
    public List<Agama> selectAgama() throws RuntimeException;
    
    @Insert("INSERT INTO gudang "
            + "(id_gudang, gudang,  id_kontak, telpon, alamat, parent, id_kantor, inisial, account_persediaan, account_hpp,dsm) "
            + "VALUES "
            + "(#{id_gudang}, #{gudang}, #{id_kontak}, #{telpon}, #{alamat}, #{parent}, #{id_kantor}, #{inisial}, #{account_persediaan}, #{account_hpp},#{dsm}) ")
    public int insertGudang(Gudang gudang) throws RuntimeException;

    @Delete("DELETE FROM gudang where id_gudang = #{id_gudang}")
    public int deleteGudang(@Param("id_gudang") String id_gudang) throws RuntimeException;

    // MAster Satuan Kecil
    @Select("SELECT id_satuan_kecil, satuan_kecil FROM satuan_kecil")
    public List<SatuanKecil> selectAllSatuan() throws RuntimeException;

    @Select("SELECT id_satuan_kecil, satuan_kecil FROM satuan_kecil WHERE id_satuan_kecil=#{id_satuan_lama}")
    public SatuanKecil selectOneperSatuan(@Param("id_satuan_lama") String id_satuan_kecil_lama) throws RuntimeException;

    @Insert("INSERT INTO satuan_kecil "
            + "(id_satuan_kecil, satuan_kecil) "
            + "VALUES "
            + "(#{id_satuan_kecil}, #{satuan_kecil}) ")
    public int insertSatuan(SatuanKecil satuanKecil) throws RuntimeException;

    @Update("UPDATE satuan_kecil  SET "
            + "id_satuan_kecil=#{id_satuan_kecil}, "
            + "satuan_kecil=#{satuan_kecil} "
            + "WHERE id_satuan_kecil=#{id_satuan_lama}")
    public int updateSatuan(SatuanKecil satuanKecil) throws RuntimeException;

    @Delete("DELETE FROM satuan_kecil where id_satuan_kecil = #{id_satuan_kecil}")
    public int deleteSatuan(@Param("id_satuan_kecil") String id_satuan_kecil) throws RuntimeException;

    @Select("SELECT id_kategori_customer, nm_kategori_customer FROM kategori_customer ORDER BY id_kategori_customer")
    public List<KategoriCustomer> selectKategoriCustomer() throws RuntimeException;

    @Select("SELECT * FROM sektor ORDER BY sektor")
    public List<Sektor> selectSektor() throws RuntimeException;

    @Select("SELECT id_kategori_supplier, nm_kategori_supplier FROM kategori_supplier ORDER BY id_kategori_supplier")
    public List<KategoriSupplier> selectKategoriSupplier() throws RuntimeException;

    @Select("SELECT id_kategori_barang, kategori_barang FROM kategori_barang ORDER BY id_kategori_barang")
    public List<KategoriBarang> selectKategoriBarang() throws RuntimeException;

    @Select("SELECT * FROM satuan_besar ORDER BY id_satuan_besar")
    public List<SatuanBesar> selectSatuanBesar() throws RuntimeException;

    @Select("SELECT id_satuan_kecil, satuan_kecil FROM satuan_kecil ORDER BY id_satuan_kecil")
    public List<SatuanKecil> selectSatuanKecil() throws RuntimeException;

    @Select("SELECT id_kontak,customer FROM customer ORDER BY customer")
    public List<Customer> selectComboCustomer() throws RuntimeException;

    @Select("SELECT id_gudang,gudang FROM gudang where id_gudang != '001' ORDER BY gudang")
    public List<Gudang> selectComboGudangAll() throws RuntimeException;
    
    @Select("SELECT id_gudang,gudang FROM gudang  ORDER BY gudang")
    public List<Gudang> selectComboGudangAllWithParent() throws RuntimeException;
    
    @Select("SELECT id_gudang,gudang FROM gudang WHERE id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai})")
    public List<Gudang> selectComboGudang(@Param("id_pegawai") String id_pegawai) throws RuntimeException;

    @Select("SELECT id_barang,nama_barang FROM barang ORDER BY nama_barang")
    public List<Barang> selectComboBarang() throws RuntimeException;

    @Select("SELECT kode_mata_uang, mata_uang FROM mata_uang ORDER BY mata_uang")
    public List<MataUang> selectMataUang() throws RuntimeException;

    @Select("SELECT * FROM customer WHERE LOWER(customer) like  LOWER ('%' || #{query} || '%') ORDER BY customer")
    public List<Customer> selectCustomer(@Param("query") String query) throws Exception;

    
    @Select("SELECT * FROM gudang WHERE id_gudang=#{id_lama}")
    public Gudang selectOneperGudang(@Param("id_lama") String id_lama) throws RuntimeException;
    
     
    @Select("SELECT d.* FROM pengguna a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN master_jabatan c ON b.id_jabatan=c.id_jabatan "
            + " INNER JOIN gudang d ON c.id_gudang=d.id_gudang"
            + " WHERE a.usernamenya=#{usernamenya}")
    
    public Gudang selectUserGudang(@Param("usernamenya") String usernamenya) throws RuntimeException;

    @Update("UPDATE gudang  SET "
            + " id_gudang = #{id_gudang}, "
            + " gudang = #{gudang}, "
            + " parent = #{parent}, "
            + " id_kontak = #{id_kontak}, "
            + " telpon = #{telpon}, "
            + " alamat = #{alamat}, "
            + " id_kantor = #{id_kantor}, "
            + " inisial = #{inisial}, "
            + " account_persediaan = #{account_persediaan}, "
            + " account_hpp = #{account_hpp}, "
            + " dsm = #{dsm} "
            + " WHERE id_gudang = #{id_lama}")
    public int updateGudang(Gudang gudang) throws RuntimeException;
    
    @Update("UPDATE gudang  SET "
            + " blind = #{blind}"
            + " WHERE id_gudang = #{id_gudang}")
    public int updateBlind(@Param("id_gudang") String id_gudang,
            @Param("blind") Boolean blind) throws RuntimeException;
    
    @Update("UPDATE gudang  SET "
            + " stokopname = #{stokopname}"
            + " WHERE id_gudang = #{id_gudang}")
    public int updateStokopname(@Param("id_gudang") String id_gudang,
            @Param("stokopname") Boolean stokopname) throws RuntimeException;

    @Select("SELECT * FROM hrpegawai ORDER BY nama_depan")
    public List<HrPegawai> selectComboPegawai() throws RuntimeException;

    @Select("SELECT * from internal_kantor_cabang")
    public List<InternalKantorCabang> selectComboKantor() throws RuntimeException;

    @Select("SELECT * from family")
    public List<Family> selectComboFamily() throws RuntimeException;

    @Select("SELECT * FROM account WHERE pembayaran = true AND level = 4")
    public List<Account> selectComboKas() throws RuntimeException;
    
    @Select("SELECT * FROM hr.hubungan_keluarga")
    public List<HubunganKeluarga> selectComboHubunganKeluarga() throws RuntimeException;
    
    @Select("SELECT * FROM hr.status_absen WHERE type='1' ORDER by urut")
    public List<StatusAbsen> selectAllStatusAbsen() throws RuntimeException;
    
    @Select("SELECT value FROM begawi WHERE id=#{id}")
    public Character selectValueBegawi(@Param("id") String id) throws RuntimeException;
    
    @Select("SELECT * FROM finance.loan_type ORDER BY loan_type") 
        public List<LoanType> selectAllLoanType() throws RuntimeException;
        
    @Select("SELECT * FROM kpi.category ORDER BY id_category") 
        public List<Category> selectAllKpiCategory() throws RuntimeException;
        
    @Select("SELECT * FROM kpi.unit ORDER BY unit") 
        public List<Unit> selectAllKpiUnit() throws RuntimeException;    
        
    @Select("SELECT * FROM hr.setting_approval WHERE id=#{id}")
    public SettingApproval settinganApproval(@Param("id") Integer id) throws RuntimeException;

}
