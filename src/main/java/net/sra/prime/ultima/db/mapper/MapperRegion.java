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

import java.util.List;
import net.sra.prime.ultima.entity.Region;
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
public interface MapperRegion {

    @Select("SELECT * FROM region")
    public List<Region> selectAll();

    @Insert("INSERT INTO region "
            + "(id_region, region_name) "
            + "VALUES "
            + "(#{id_region}, #{region_name}) ")
    public int insert(Region region) throws Exception;

    @Delete("DELETE FROM region WHERE id_region = #{id}")
    public int delete(@Param("id") String id) throws Exception;

    @Select("SELECT * FROM region WHERE id_region=#{id_lama}")
    public Region selectOne(@Param("id_lama") String id_lama);

    @Update("UPDATE region  SET "
            + " id_region = #{id_region}, "
            + " region_name = #{region_name} "
            + " WHERE id_region = #{id_lama} ")
    public int update(Region region) throws Exception;

}
