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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
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
public interface MapperAbsenPeriode {

    
    @Insert("INSERT INTO hr.absen_periode "
            + "(year,month,start,end_date) "
            + " VALUES "
            + "(#{year},#{month},#{start},#{end_date}) ")
    public int insert(AbsenPeriode absenperiode)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.absen_periode WHERE year = #{year} ")
    public int delete(@Param("year") String year)  throws RuntimeException;

    @Select("SELECT * FROM hr.absen_periode WHERE year=#{year} ORDER BY month")
    public List<AbsenPeriode> selectAll(@Param("year") String year)throws RuntimeException;
    
    @Select("SELECT * FROM hr.absen_periode WHERE year=#{year} AND month=#{month}")
    public AbsenPeriode selectOne(@Param("year") String year, 
            @Param("month") Integer month)throws RuntimeException;
    
    @Select("SELECT * FROM hr.absen_periode WHERE #{date} >= start AND #{date} <= end_date")
    public AbsenPeriode selectOneByDate(@Param("date") Date date)throws RuntimeException;
    
    @Update("UPDATE hr.absen_periode  SET "
            + " start = #{start}, "
            + " end_date = #{end_date} "
            + " WHERE id = #{id} ")
    public int update(AbsenPeriode absen_periode)  throws RuntimeException;
    
    

}
