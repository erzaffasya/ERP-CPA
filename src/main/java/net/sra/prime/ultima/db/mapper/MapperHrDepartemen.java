/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License" )throws RuntimeException;
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
import net.sra.prime.ultima.entity.HrDepartemen;
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
public interface MapperHrDepartemen {

  
    @Select("SELECT a.* FROM hr_departemen a WHERE a.id_departemen > 100"
            + " ORDER BY a.id_departemen ")
    public List<HrDepartemen> selectAll( )throws RuntimeException;

    
    @Insert("INSERT INTO hr_departemen "
            + "(departemen) "
            + "VALUES "
            + "(#{departemen}) ")
    public int insert(HrDepartemen hrDepartemen)  throws RuntimeException;

    
    @Delete("DELETE FROM hr_departemen WHERE id_departemen = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;

    
    @Select("SELECT * FROM hr_departemen WHERE id_departemen=#{id}")
    public HrDepartemen selectOne(@Param("id") Integer id )throws RuntimeException;

    
    @Update("UPDATE hr_departemen  SET "
            + " departemen = #{departemen} "
            + " WHERE id_departemen = #{id_departemen} ")
    public int update(HrDepartemen hrDepartemen)  throws RuntimeException;
    
    @Update("UPDATE hr_departemen  SET "
            + "roles = concat('ROLE_DEPARTEMEN',id_departemen)")
    public int updateRole()  throws RuntimeException;

}
