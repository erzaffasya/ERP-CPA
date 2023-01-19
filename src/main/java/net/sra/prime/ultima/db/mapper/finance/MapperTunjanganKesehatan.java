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
package net.sra.prime.ultima.db.mapper.finance;

import java.util.List;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatan;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatanDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperTunjanganKesehatan {

    @Select("SELECT a.*,b.nama as create_by_name ,b.nip "
            + " FROM finance.tunjangan_kesehatan a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " ORDER BY a.year DESC,a.month  DESC")
    public List<TunjanganKesehatan> selectAll()  throws RuntimeException;

    @Insert("INSERT INTO finance.tunjangan_kesehatan "
            + "(date,description1,status,month,year,create_by,create_date) "
            + " VALUES "
            + "(#{date},#{description1},#{status},#{month},#{year},#{create_by},LOCALTIMESTAMP) "
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insert(TunjanganKesehatan tunjanganKesehatan)  throws RuntimeException;
    
    @Insert("INSERT INTO finance.tunjangan_kesehatan_detail "
            + "(id,amount,id_pegawai,description,sequence) "
            + " VALUES "
            + "(#{id},#{amount},#{id_pegawai},#{description},#{sequence}) "
    )
    public int insertDetail(TunjanganKesehatanDetail tunajKesehatanDetail)  throws RuntimeException;
    
    
 
    @Delete("DELETE FROM finance.tunjangan_kesehatan WHERE id = #{id}")
    public int delete(@Param("id") Integer id)  throws RuntimeException;
    
    @Delete("DELETE FROM finance.tunjangan_kesehatan_detail WHERE id = #{id}")
    public int deleteDetail(@Param("id") Integer id)  throws RuntimeException;
    
    
    @Select("SELECT a.* FROM finance.tunjangan_kesehatan_detail a "
            + " WHERE "
            + " a.id=#{id}")
    public TunjanganKesehatanDetail selectOneDetail(@Param("id") Integer id
            )throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM finance.tunjangan_kesehatan a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE a.id=#{id}")
    public TunjanganKesehatan selectOne(@Param("id") Integer id) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM finance.tunjangan_kesehatan a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE a.year=#{year} AND a.month=#{month} AND a.status='A'")
    public TunjanganKesehatan selectOneByPeriode(@Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Select("SELECT a.*, b.nama "
            + " FROM finance.tunjangan_kesehatan_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE "
            + " a.id=#{id}")
    public List<TunjanganKesehatanDetail> selectAllDetail(@Param("id") Integer id) throws RuntimeException;
    
    
    
    @Update("UPDATE finance.tunjangan_kesehatan  SET "
            + " date = #{date}, "
            + " description1 = #{description1}, "
            + " status = #{status}, "
            + " month = #{month}, "
            + " year = #{year} "
            + " WHERE id = #{id} ")
    public int update(TunjanganKesehatan tunjanganKesehatan)  throws RuntimeException;
    
    @Select("SELECT * FROM finance.tunjangan_kesehatan WHERE month=#{month} AND year=#{year}")
    public TunjanganKesehatan selectPeriodeProposal(@Param("month") Integer month,
            @Param("year") String year) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.amount),0)  "
            + " FROM finance.tunjangan_kesehatan_detail a "
            + " INNER JOIN finance.tunjangan_kesehatan b ON a.id=b.id  AND b.status='A'"
            + " WHERE a.id_pegawai=#{id_pegawai} AND b.year=#{year} AND b.month=#{month}")
    public Double sumTunjanganKesehatan(@Param("id_pegawai") String id_pegawai,
    @Param("year") String year,
    @Param("month") Integer month);
            
}
