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
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.entity.CustomerAccount;
import net.sra.prime.ultima.entity.CustomerPengiriman;
import net.sra.prime.ultima.entity.CustomerKontak;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperCustomer {

    @Select("SELECT * FROM customer WHERE isaktif=true")
    public List<Customer> selectAll() throws RuntimeException;
    
    @Select("SELECT a.* FROM customer a WHERE isaktif=true AND id_sales IN (SELECT id_sales FROM hr.sales_admin WHERE id_admin=#{id_admin})")
    public List<Customer> selectAllBySalesAdmin(@Param("id_admin") String id_admin) throws RuntimeException;
    
    @Select("SELECT * FROM customer")
    public List<Customer> selectAll2() throws RuntimeException;
    
  
//    @Select("SELECT a.*, "
//            + "array_to_string(array(SELECT pegawai.nama FROM penjualan b join pegawai on b.id_salesman=pegawai.id_pegawai WHERE (b.status='A' OR b.status='P') and b.id_customer=a.id_kontak GROUP BY pegawai.nama),', ') as nama_marketing, "
//            + "array_to_string(array(SELECT nama_barang FROM penjualan b join penjualan_detail c on b.no_penjualan=c.no_penjualan join barang on c.id_barang=barang.id_barang WHERE (b.status='A' OR b.status='P') and b.id_customer=a.id_kontak  GROUP BY nama_barang),', ') as nama_barang "
//            + "FROM customer a "
//            + "WHERE a.isaktif=#{isaktif}")
//    public List<Customer> selectAllCustomer(@Param("isaktif") Boolean isaktif) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as nama_marketing, "
            + "array_to_string(array(SELECT nama_barang FROM penjualan b join penjualan_detail c on b.no_penjualan=c.no_penjualan join barang on c.id_barang=barang.id_barang WHERE (b.status='A' OR b.status='P') and b.id_customer=a.id_kontak  GROUP BY nama_barang),', ') as nama_barang "
            + "FROM customer a "
            + "LEFT JOIN pegawai b ON a.id_sales=b.id_pegawai "
            + "WHERE a.isaktif=#{isaktif}")
    public List<Customer> selectAllCustomer(@Param("isaktif") Boolean isaktif) throws RuntimeException;


   
    @Insert("INSERT INTO customer "
            + "(id_kontak, customer, no_npwp, telepon, fax, email, alamat_penagihan, id_kategori_customer, nama_bank, kena_pajak, website, isaktif, batas_kredit, top, alamat_npwp, id_sektor,id_sales,alamat_kirim) "
            + "VALUES "
            + "(#{id_kontak}, #{customer}, #{no_npwp}, #{telepon}, #{fax}, #{email}, #{alamat_penagihan}, #{id_kategori_customer}, #{nama_bank}, #{kena_pajak}, #{website}, #{isaktif}, #{batas_kredit}, #{top}, #{alamat_npwp}, #{id_sektor},#{id_sales},#{alamat_kirim}) ")
    public int insert(Customer customer) throws RuntimeException;

    
    @Delete("DELETE FROM customer WHERE id_kontak = #{id_kontak}")
    public int delete(@Param("id_kontak") String id_kontak) throws RuntimeException;

    
    @Select("SELECT * FROM customer WHERE id_kontak=#{id_lama}")
    public Customer selectOneperCustomer(@Param("id_lama") String id_lama) throws RuntimeException;

    
    @Update("UPDATE customer  SET "
            + " id_kontak = #{id_kontak}, "
            + " customer = #{customer}, "
            + " no_npwp = #{no_npwp}, "
            + " telepon = #{telepon}, "
            + " fax = #{fax}, "
            + " email = #{email}, "
            + " alamat_penagihan = #{alamat_penagihan}, "
            + " id_kategori_customer = #{id_kategori_customer}, "
            + " nama_bank = #{nama_bank}, "
            + " kena_pajak = #{kena_pajak}, "
            + " website = #{website}, "
            + " isaktif = #{isaktif}, "
            + " batas_kredit = #{batas_kredit}, "
            + " alamat_npwp = #{alamat_npwp}, "
            + " id_sektor = #{id_sektor}, "
            + " top = #{top} , "
            + " id_sales = #{id_sales}, "
            + " alamat_kirim = #{alamat_kirim} "
            + "WHERE id_kontak = #{id_lama} ")
    public int updateCustomer(Customer customer) throws RuntimeException;

    @Select("SELECT * FROM customer WHERE id_kontak = #{id}")
    public Customer selectOne(@Param("id") String id) throws RuntimeException;

    
    @Select("SELECT * FROM customer_pengiriman where id_customer=#{id_customer}")
    public List<CustomerPengiriman> selectAllpengiriman(@Param("id_customer") String id_customer) throws RuntimeException;

    
    @Select("SELECT * FROM customer_pengiriman "
            + " WHERE id_customer=#{id_customer} "
            + " AND id_pengiriman = #{id_pengiriman} ")
    public CustomerPengiriman selectOnepengiriman(@Param("id_customer") String id_customer, @Param("id_pengiriman") Integer id_pengiriman) throws RuntimeException;

    
    @Select("SELECT * FROM customer_pengiriman "
            + " WHERE id_customer=#{id_customer} "
            + " AND utama = true")
    public CustomerPengiriman selectDefaultpengiriman(@Param("id_customer") String id_customer) throws RuntimeException;

   
    @Insert("INSERT INTO customer_pengiriman "
            + "(id_pengiriman, id_customer, alamat, utama) "
            + "VALUES "
            + "(#{id_pengiriman}, #{id_customer}, #{alamat}, #{utama}) ")
    public int insertpengiriman(CustomerPengiriman customerPengiriman) throws RuntimeException;

    
    @Delete("DELETE FROM customer_pengiriman WHERE id_customer  = #{id_customer}")
    public int deletePengiriman(@Param("id_customer") String id_customer) throws RuntimeException;

    
    @Select("SELECT * FROM customer_kontak where id_customer=#{id_customer}")
    public List<CustomerKontak> selectAllKontak(@Param("id_customer") String id_customer) throws RuntimeException;

    @Select("SELECT * FROM customer_account where id_customer=#{id_customer}")
    public List<CustomerAccount> selectAllAccount(@Param("id_customer") String id_customer) throws RuntimeException;

    
    @Insert("INSERT INTO customer_kontak "
            + "(id_kontak, id_customer, kontak,  hp, email, jabatan) "
            + "VALUES "
            + "(#{id_kontak}, #{id_customer}, #{kontak},  #{hp}, #{email},#{jabatan}) ")
    public int insertKontak(CustomerKontak customerKontak) throws RuntimeException;

    
    @Delete("DELETE FROM customer_kontak WHERE id_customer = #{id_customer}")
    public int deleteKontak(@Param("id_customer") String id_customer) throws RuntimeException;

    @Select("SELECT * FROM customer_kontak "
            + " WHERE id_customer = #{id_customer} "
            + " AND id_kontak=#{id_kontak}"
    )
    public CustomerKontak selectOneKontak(@Param("id_kontak") Integer id_kontak, @Param("id_customer") String id_customer) throws RuntimeException;

    
    @Insert("INSERT INTO customer_account "
            + "(id_customer, id_account, id_kantor) "
            + "VALUES "
            + "(#{id_customer}, #{id_account}, #{id_kantor}) ")
    public int insertAccount(CustomerAccount customerAccount) throws RuntimeException;
    
    
    @Delete("DELETE FROM customer_account WHERE id_customer = #{id_customer}")
    public int deleteAccount(@Param("id_customer") String id_customer) throws RuntimeException;

    
    @Select("SELECT id_account FROM customer_account "
            + "WHERE id_customer = #{id_customer} AND id_kantor = #{id_kantor}")
    public Integer selectAccount(@Param("id_customer") String id_customer, @Param("id_kantor") String id_kantor) throws RuntimeException;
    
    @Select("SELECT a.*, b.account as nm_account, c.nama as nm_kantor "
            + "FROM customer_account a "
            + "INNER JOIN account b ON a.id_account=b.id_account "
            + "INNER JOIN internal_kantor_cabang c ON a.id_kantor=c.id_kantor_cabang "
            + "WHERE a.id_customer=#{id_customer} ")
    public List<CustomerAccount> selectCustomerAccount(@Param("id_customer") String id_customer) throws RuntimeException;
    
    @Select("SELECT MAX(SUBSTR(id_kontak,3,6)) "
            + " FROM customer ")
    public String SelectMax() throws RuntimeException;
}
