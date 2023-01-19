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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import net.sra.prime.ultima.entity.KategoriSupplier;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperKategoriSupplier {

    @Select("SELECT * FROM kategori_supplier")
    public List<KategoriSupplier> selectAll() throws RuntimeException;;

    @Insert("INSERT INTO kategori_supplier "
            + "(nm_kategori_supplier) "
            + "VALUES "
            + "(#{nm_kategori_supplier})")
    public int insert(KategoriSupplier kategoriSupplier) throws RuntimeException;

    @Delete("DELETE FROM kategori_supplier WHERE id_kategori_supplier = #{id_kategori_supplier}")
    public int delete(@Param("id_kategori_supplier") Integer id_kategori_supplier) throws RuntimeException;

    @Select("SELECT * FROM kategori_supplier WHERE id_kategori_supplier=#{id_lama}")
    public KategoriSupplier selectOneperkategorisupplier(@Param("id_lama") Integer id_lama);

    @Update("UPDATE kategori_supplier  SET "
            + " id_kategori_supplier = #{id_kategori_supplier}, "
            + " nm_kategori_supplier = #{nm_kategori_supplier} "
            + " WHERE id_kategori_supplier = #{id_lama}")
    public int updateKategoriSupplier(KategoriSupplier kategori_supplier) throws RuntimeException;

}
