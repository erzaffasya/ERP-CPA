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
package net.sra.prime.ultima.db.mapper.hr;

import java.util.List;
import net.sra.prime.ultima.entity.Pegawai;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperSalesAdmin {

    
    @Insert("INSERT INTO hr.sales_admin "
            + "(id_admin,id_sales) "
            + "VALUES "
            + "(#{id_admin},#{id_sales}) "
            )
    public int insert(@Param("id_admin") String id_admin,@Param("id_sales") String id_sales)  throws RuntimeException;

 
    @Delete("DELETE FROM hr.sales_admin WHERE id_admin = #{id_admin}")
    public int delete(@Param("id_admin") String id_admin)  throws RuntimeException;

    
    
    @Select("SELECT a.* FROM pegawai a WHERE id_pegawai NOT IN (SELECT id_sales FROM  hr.sales_admin WHERE id_admin=#{id_admin})")
    public List<Pegawai> selectAllPegawai(@Param("id_admin") String id_admin)  throws RuntimeException;
    
    @Select("SELECT a.* FROM pegawai a INNER JOIN hr.sales_admin b ON a.id_pegawai=b.id_sales WHERE b.id_admin=#{id_admin}")
    public List<Pegawai> selectAllSales(@Param("id_admin") String id_admin)  throws RuntimeException;
    
    
}
