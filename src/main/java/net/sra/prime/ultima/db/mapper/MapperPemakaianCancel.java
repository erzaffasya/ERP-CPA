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

import net.sra.prime.ultima.entity.Pemakaian_cancel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperPemakaianCancel {

    @Insert("INSERT INTO pemakaian_cancel (nomor,no_pemakaian,tanggal,pesan,kode_user) "
            + "VALUES (#{nomor},#{no_pemakaian},LOCALTIMESTAMP,#{pesan},#{kode_user})")
    public int insert(Pemakaian_cancel pemakaian_cancel) throws RuntimeException;
    
            
    @Select("SELECT MAX(SUBSTR(a.nomor,1,3)) "
            + " FROM pemakaian_cancel a "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax(@Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;
    
}
