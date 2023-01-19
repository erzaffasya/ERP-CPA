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
import net.sra.prime.ultima.entity.Pesan;
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
public interface MapperPesan {

    @Select("SELECT * FROM pesan")
    public List<Pesan> selectAll() throws RuntimeException;

    @Select("SELECT * FROM pesan WHERE id_pesan=#{id_pesan}")
    public Pesan selectOne(@Param("id_pesan") String id_pesan) throws RuntimeException;

    public static final String INSERT = "INSERT INTO pesan "
            + "(referensi, pesan, tgl_kirim, tgl_baca, dari, kepada,status_baca) "
            + "VALUES "
            + "(#{referensi}, #{pesan}, #{tgl_kirim}, #{tgl_baca}, #{dari}, #{kepada}, false) ";

    @Insert(INSERT)
    public int insert(Pesan pesan) throws RuntimeException;

    @Delete("DELETE FROM pesan WHERE id_pesan = #{id}")
    public int delete(@Param("id") String id) throws RuntimeException;

    @Update("UPDATE pesan  SET "
            + " status_baca = #{status_baca}, "
            + " tgl_baca = #{tgl_baca} "
            + " WHERE id_kantor_cabang = #{id_lama}")
    public int update(Pesan pesan) throws RuntimeException;

}
