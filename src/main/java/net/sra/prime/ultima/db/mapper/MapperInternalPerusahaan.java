/*
 * Copyright 2017 JoinFaces.
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
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalPerusahaan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Syamsu Rizal Ali <syamsu.rizal.ali@outlook.com>
 */
@Mapper
public interface MapperInternalPerusahaan {

    @Select("SELECT * FROM internal_perusahaan")
    public List<InternalPerusahaan> getAllPerusahaan();

    @Select("SELECT * FROM internal_perusahaan WHERE id_perusahaan = #{id_perusahaan}")
    public InternalPerusahaan getPerusahaan(@Param("id_perusahaan") String id_perusahaan);

    @Select("SELECT * FROM internal_kantor_cabang WHERE id_perusahaan = #{id_perusahaan}")
    public List<InternalKantorCabang> getKantorCabang(@Param("id_perusahaan") String id_perusahaan);

    @Select("SELECT * FROM internal_kantor_cabang WHERE id_kantor_cabang = #{id_kantor_cabang}")
    public InternalKantorCabang getOneKantorCabang(@Param("id_kantor_cabang") String id_kantor_cabang);

    @Select("SELECT d.* FROM pengguna a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
            + " INNER JOIN internal_kantor_cabang d ON b.id_kantor_new=d.id_kantor_cabang"
            + " WHERE a.usernamenya=#{usernamenya}")
    public InternalKantorCabang getUserKantorCabang(@Param("usernamenya") String usernamenya);
    
    @Select("SELECT * FROM gudang a"
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang "
            + " INNER JOIN internal_perusahaan c ON b.id_perusahaan = c.id_perusahaan "
            + " WHERE c.id_perusahaan = #{id_perusahaan} ")
    public List<Gudang> getGudang(@Param("id_perusahaan") String perusahaan);

    @Select("SELECT id_kantor_cabang FROM map_pengguna_cabang "
            + "WHERE usernamenya = #{username} "
            + "AND id_kantor_cabang = #{id_kantor_cabang} "
            + "AND id_perusahaan = #{id_perusahaan}")
    public String getKdKantorFromPegawai(@Param("username") String username,
            @Param("id_kantor_cabang") String id_kantor_cabang,
            @Param("id_perusahaan") String id_perusahaan);

    @Select("SELECT id_gudang FROM map_pengguna_gudang "
            + "WHERE id_perusahaan = #{id_perusahaan} "
            + "AND id_gudang = #{id_gudang} "
            + "AND usernamenya = #{username}")
    public String getIdGudangFromPegawai(@Param("username") String username,
            @Param("id_gudang") String id_gudang,
            @Param("id_perusahaan") String id_perusahaan);
    
    @Select("SELECT * FROM internal_perusahaan  ")
    public InternalPerusahaan getOnePerusahaan(@Param("name") String name);

}
