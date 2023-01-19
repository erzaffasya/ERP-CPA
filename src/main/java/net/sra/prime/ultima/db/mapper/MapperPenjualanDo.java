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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.PenjualanDo;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperPenjualanDo {

    @Select("SELECT * FROM penjualando WHERE no_penjualan=#{no_penjualan}")
    public List<PenjualanDo> selectAll(@Param("no_penjualan") String no_penjualan) throws RuntimeException;

    
    @Insert("INSERT INTO penjualando "
            + "(no_penjualan,no_do) "
            + "VALUES "
            + "(#{no_penjualan},#{no_do}) ")
    public int insert(PenjualanDo penjualanDo) throws RuntimeException;

    
    @Delete("DELETE FROM penjualando WHERE no_penjualan = #{no_penjualan}")
    public int delete(@Param("no_penjualan") String no_penjualan) throws RuntimeException;
    
    @Select("SELECT string_agg(no_do, ',') no_do FROM penjualando WHERE no_penjualan=#{no_penjualan}")
    public String selectDo(@Param("no_penjualan") String no_penjualan) throws RuntimeException;

}
