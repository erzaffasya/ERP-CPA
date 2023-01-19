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
import net.sra.prime.ultima.entity.hr.PayrollOvertime;
import net.sra.prime.ultima.entity.hr.PayrollOvertimeDetail;
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
public interface MapperPayrollOvertime {

    @Select("SELECT a.*,b.nama as create_by_name ,b.nip "
            + " FROM hr.payroll_overtime a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE a.jenis=#{jenis} "
            + " ORDER BY a.year DESC,a.month DESC")
    public List<PayrollOvertime> selectAll(@Param("jenis") String jenis)  throws RuntimeException;
    
    
    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM hr.payroll_overtime a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE year=#{year} AND month=#{month} AND jenis=#{jenis}")
    public PayrollOvertime selectOne(@Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis)  throws RuntimeException;

    @Insert("INSERT INTO hr.payroll_overtime "
            + "(status,month,year,create_by,create_date,total,jenis) "
            + " VALUES "
            + "(#{status},#{month},#{year},#{create_by},LOCALTIMESTAMP,#{total},#{jenis}) ")
    public int insert(PayrollOvertime payrollOvertime)  throws RuntimeException;
    
    @Delete("DELETE FROM hr.payroll_overtime WHERE year=#{year} AND month=#{month} AND jenis=#{jenis}")
    public int delete(@Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis)  throws RuntimeException;
    
    @Update("UPDATE hr.payroll_overtime  SET "
            + " status = #{status}, "
            + " month = #{month}, "
            + " year = #{year}, "
            + " modified_by = #{modified_by}, "
            + " modified_date = LOCALTIMESTAMP "
            + " WHERE year=#{year} AND month=#{month} AND jenis=#{jenis} ")
    public int update(PayrollOvertime payrollOvertime)  throws RuntimeException;
    /**
     * Detail
     * @param payrollOvertimeDetail
     * @return
     * @throws RuntimeException 
     */
    @Insert("INSERT INTO hr.payroll_overtime_detail "
            + "(year,month,amount,id_pegawai,sequence,jenis) "
            + " VALUES "
            + "(#{year},#{month},#{amount},#{id_pegawai},#{sequence},#{jenis}) "
    )
    public int insertDetail(PayrollOvertimeDetail payrollOvertimeDetail)  throws RuntimeException;

    
    @Delete("DELETE FROM hr.payroll_overtime_detail WHERE year=#{year} AND month=#{month} AND jenis=#{jenis}")
    public int deleteDetail(@Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis)  throws RuntimeException;

    @Select("SELECT a.*, b.nama,b.nip"
            + " FROM hr.payroll_overtime_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE year=#{year} AND month=#{month} AND jenis=#{jenis}")
    public List<PayrollOvertimeDetail> selectAllDetail(@Param("year") String year,
            @Param("month") Integer month,
            @Param("jenis") String jenis)  throws RuntimeException;
    
    @Select("SELECT a.*,b.nama,b.nip FROM hr.payroll_overtime_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " WHERE "
            + " year=#{year} AND month=#{month} AND a.id_pegawai=#{id_pegawai} AND jenis=#{jenis}")
    public PayrollOvertimeDetail selectOneDetail(@Param("year") String year,
            @Param("month") Integer month,
            @Param("id_pegawai") String id_pegawai,
            @Param("jenis") String jenis)  throws RuntimeException;
    
    @Select("SELECT a.*,b.nama,b.nip "
            + " FROM hr.payroll_overtime_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai = b.id_pegawai "
            + " INNER JOIN hr.payroll_overtime c ON a.year=c.year AND a.month=c.month"
            + " WHERE "
            + " a.year=#{year} AND a.month=#{month} AND a.id_pegawai=#{id_pegawai} AND c.status='A' AND jenis=#{jenis}")
    public PayrollOvertimeDetail selectOneDetailPosting(@Param("year") String year,
            @Param("month") Integer month,
            @Param("id_pegawai") String id_pegawai,
            @Param("jenis") String jenis)  throws RuntimeException;
    
            
}
