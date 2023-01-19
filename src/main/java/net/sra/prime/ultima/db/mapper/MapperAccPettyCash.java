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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.AccPettyCash;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;


/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAccPettyCash {

    @SelectProvider(type = ProviderAccPettyCash.class, method = "SelectAll")
    public List<AccPettyCash> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_kantor") String id_kantor,
            @Param("st") Character st
    ) throws Exception;

    @Insert("INSERT INTO acc_petty_cash "
            + "(nomor, tanggal,   id_kantor, saldo_awal, awal, akhir, saldo_akhir, tgl_awal, tgl_akhir) "
            + "VALUES (#{nomor}, #{tanggal},  #{id_kantor}, #{saldo_awal}, #{awal}, #{akhir}, #{saldo_akhir}, #{tgl_awal}, #{tgl_akhir})")
    public int insert(AccPettyCash accPettyCash) throws Exception;

    @Delete("DELETE FROM acc_petty_cash WHERE id = #{id}")
    public int delete(@Param("id") Integer id) throws Exception;

    @Select("SELECT a.*, b.nama as kantor FROM acc_petty_cash a  "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang "
            + " WHERE a.id=#{id} ")
    public AccPettyCash selectOne(@Param("id") Integer id);

    
    
    
}
