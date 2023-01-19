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
import net.sra.prime.ultima.entity.Leadtime;
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
public interface MapperLeadtime {

    @Select("SELECT * FROM leadtime ORDER BY origin ")
    public List<Leadtime> selectAll( )throws RuntimeException;


    @Insert("INSERT INTO leadtime "
            + "(origin,destination,region,normaly,spare,total) "
            + "VALUES "
            + "(#{origin}, #{destination},#{region},#{normaly},#{spare},#{total}) ")
    public int insert(Leadtime leadtime)  throws RuntimeException;

    
    @Delete("DELETE FROM leadtime WHERE origin = #{origin} AND destination = #{destination}")
    public int delete(@Param("origin") String origin,@Param("destination") String destination)  throws RuntimeException;

    @Select("SELECT * FROM leadtime WHERE lower(origin)=lower(#{origin}) AND lower(destination)=lower(#{destination})")
    public Leadtime selectOne(@Param("origin") String origin,@Param("destination") String destination)throws RuntimeException;

    @Update("UPDATE leadtime  SET "
            + " region = #{region}, "
            + " normaly = #{normaly}, "
            + " spare = #{spare}, "
            + " total = #{total} "
            + " WHERE origin = #{origin} AND destination = #{destination} ")
    public int update(Leadtime leadtime)  throws RuntimeException;

    
}
