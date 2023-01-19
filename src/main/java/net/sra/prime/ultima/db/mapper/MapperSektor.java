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
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.Sektor;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperSektor {

    //// Master Gudang ////
    
    @Select("SELECT id_sektor, sektor FROM sektor")
    public List<Sektor> selectAll();

    @Insert("INSERT INTO sektor "
            + "(sektor) "
            + "VALUES "
            + "(#{sektor}) ")
    public int insert(Sektor sektor) throws Exception;

    @Delete("DELETE FROM sektor WHERE id_sektor = #{id_sektor}")
    public int delete(@Param("id_sektor") Integer id_sektor) throws Exception;

}
