/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")throws RuntimeException;
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
import net.sra.prime.ultima.entity.KategoriBarang;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperKategoriBarang {

    @Select("SELECT * FROM kategori_barang")
    public List<KategoriBarang> selectAll()throws RuntimeException;

    @Insert("INSERT INTO kategori_barang "
            + "(id_kategori_barang,kategori_barang) "
            + "VALUES "
            + "(#{id_kategori_barang},#{kategori_barang}) ")
    public int insert(KategoriBarang kategoriBarang)  throws RuntimeException;

    @Delete("DELETE FROM kategori_barang WHERE id_kategori_barang = #{id_kategori_barang}")
    public int delete(@Param("id_kategori_barang") String id_kategori_barang)  throws RuntimeException;

}
