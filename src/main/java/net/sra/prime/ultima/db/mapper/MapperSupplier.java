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
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.entity.SupplierKontak;
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
public interface MapperSupplier {

    
    @Select("SELECT * FROM supplier ORDER BY id ")
    public List<Supplier> selectAll() throws RuntimeException;

    @Insert("INSERT INTO supplier "
            + "(id, supplier, npwp, telepon, fax, email, alamat, id_kategori_supplier, "
            + "bank, kena_pajak, website, isaktif, batas_kredit, top, alamat_npwp, account_hutang,user_input, input_date) "
            + "VALUES "
            + "(#{id}, #{supplier}, #{npwp}, #{telepon}, #{fax}, #{email}, #{alamat}, #{id_kategori_supplier}, "
            + "#{bank}, #{kena_pajak}, #{website},  #{isaktif}, #{batas_kredit}, #{top}, #{alamat_npwp}, #{account_hutang}, #{user_input}, LOCALTIMESTAMP) ")
    public int insert(Supplier supplier) throws RuntimeException;

    @Delete("DELETE FROM supplier WHERE id = #{id}")
    public int delete(@Param("id") String id) throws RuntimeException;

    
    @Update("UPDATE supplier  SET "
            + " supplier = #{supplier}, "
            + " npwp = #{npwp}, "
            + " telepon = #{telepon}, "
            + " fax = #{fax}, "
            + " email = #{email}, "
            + " alamat = #{alamat}, "
            + " id_kategori_supplier = #{id_kategori_supplier}, "
            + " bank = #{bank}, "
            + " kena_pajak = #{kena_pajak}, "
            + " website = #{website}, "
            + " isaktif = #{isaktif}, "
            + " batas_kredit = #{batas_kredit}, "
            + " top = #{top}, "
            + " alamat_npwp = #{alamat_npwp}, "
            + " account_hutang = #{account_hutang}, "
            + " user_update = #{user_update}, "
            + " last_update = LOCALTIMESTAMP "
            + " WHERE id = #{id} ")
    public int updateSupplier(Supplier supplier) throws RuntimeException;

    @Select("SELECT * FROM supplier WHERE id = #{id}")
    public Supplier selectOne(@Param("id") String id) throws RuntimeException;

    
    @Select("SELECT * FROM supplier_kontak WHERE id_supplier = #{id_supplier} ")
    public List<SupplierKontak> selectAllKontak(@Param("id_supplier") String id_supplier) throws RuntimeException;

    @Select("SELECT * FROM supplier_kontak "
            + " WHERE id_supplier = #{id_supplier} "
            + " AND urut=#{urut}"
    )
    public SupplierKontak selectOneKontak(@Param("urut") Integer urut, @Param("id_supplier") String id_supplier) throws RuntimeException;

    @Insert("INSERT INTO supplier_kontak "
            + "(id_supplier, urut, kontak, hp, jabatan, email) "
            + "VALUES "
            + "(#{id_supplier}, #{urut}, #{kontak}, #{hp}, #{jabatan}, #{email}) ")
    public int insertKontak(SupplierKontak supplierKontak) throws RuntimeException;

    @Delete("DELETE FROM supplier_kontak WHERE id_supplier = #{id_supplier}")
    public int deleteKontak(@Param("id_supplier") String id_supplier) throws RuntimeException;

    

    @Select("SELECT account_hutang FROM supplier WHERE id = #{id_supplier}")
    public Integer selectAccount(@Param("id_supplier") String id_supplier) throws RuntimeException;
    
    @Select("SELECT account_biaya FROM supplier WHERE id = #{id_supplier}")
    public Integer selectAccountBiaya(@Param("id_supplier") String id_supplier) throws RuntimeException;
    
    
    @Select("SELECT MAX(SUBSTR(id,3,4)) "
            + " FROM supplier ")
    public String SelectMax() throws RuntimeException;

}
