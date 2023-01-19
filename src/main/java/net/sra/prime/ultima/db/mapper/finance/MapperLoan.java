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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.finance.LoanType;
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
public interface MapperLoan {

    @Select("SELECT a.*,b.nama ,b.nip "
            + " FROM finance.loan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " ORDER BY a.loan_number ")
    public List<Loan> selectAll() throws RuntimeException;

    @Insert("INSERT INTO finance.loan "
            + "(date,id_pegawai,id_loan_type,amount,installment,description,status,create_by,create_date,interest_type,interest,payment_date,loan_number,remain)"
            + " VALUES "
            + "(#{date},#{id_pegawai},#{id_loan_type},#{amount},#{installment},#{description},#{status},#{create_by},LOCALTIMESTAMP,#{interest_type},#{interest},#{payment_date},#{loan_number},#{amount})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id_loan", keyColumn = "id_loan")
    public int insert(Loan loan) throws RuntimeException;

    @Insert("INSERT INTO finance.loan_detail "
            + "(id_loan,sequence,installment_amount,remains,overdue) "
            + " VALUES "
            + "(#{id_loan},#{sequence},#{installment_amount},#{installment_amount},#{overdue}) "
    )
    public int insertDetail(LoanDetail loanDetail) throws RuntimeException;

    @Delete("DELETE FROM finance.loan WHERE id_loan = #{id_loan}")
    public int delete(@Param("id_loan") Integer id_loan) throws RuntimeException;

    @Delete("DELETE FROM finance.loan_detail WHERE id_loan = #{id_loan}")
    public int deleteDetail(@Param("id_loan") Integer id_loan) throws RuntimeException;

    @Select("SELECT a.*,b.nama,b.nip "
            + " FROM finance.loan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " WHERE id_loan=#{id_loan}")
    public Loan selectOne(@Param("id_loan") Integer id_loan) throws RuntimeException;

    @Select("SELECT a.* FROM finance.loan_detail a "
            + " WHERE "
            + " a.id_loan=#{id_loan}")
    public List<LoanDetail> selectAllDetail(@Param("id_loan") Integer id_loaInteger) throws RuntimeException;

    @Select("SELECT a.* FROM finance.loan_detail a "
            + " WHERE "
            + " a.id_loan=#{id_loan} AND a.sequence=#{sequence} ")
    public LoanDetail selectOneDetail(@Param("id_loan") Integer id_loan,
            @Param("sequence") Integer sequence) throws RuntimeException;

    @Update("UPDATE finance.loan  SET "
            + " date = #{date}, "
            + " id_pegawai = #{id_pegawai}, "
            + " id_loan_type = #{id_loan_type}, "
            + " amount = #{amount}, "
            + " installment = #{installment}, "
            + " description = #{description}, "
            + " status = #{status}, "
            + " interest_type = #{interest_type}, "
            + " interest = #{interest}, "
            + " payment_date = #{payment_date}, "
            + " loan_number = #{loan_number}, "
            + " remain = #{amount}"
            + " WHERE id_loan = #{id_loan} ")
    public int update(Loan loan) throws RuntimeException;

    @Select("SELECT MAX(SUBSTR(loan_number,5,5)) FROM finance.loan WHERE EXTRACT(year FROM date)=#{year}")
    public String SelectMax(@Param("year") Integer year) throws RuntimeException;

    @Select("SELECT a.*,b.nama,b.nip,c.loan_type "
            + " FROM finance.loan a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN finance.loan_type c ON a.id_loan_type=c.id_loan_type "
            + " WHERE "
            + " a.remain > 0 AND a.status='A' "
            + " AND a.payment_date <= #{datestart} ")
    public List<Loan> selectRemainingLoan(@Param("datestart") Date datestart) throws RuntimeException;

    @Select("SELECT * FROM finance.loan_detail WHERE EXTRACT(year FROM overdue)=#{year} AND EXTRACT(month FROM overdue)=#{month} AND id_loan=#{id_loan}")
    public LoanDetail selectRemainingLoanDetail(@Param("month") Integer month,
            @Param("year") Integer year,
            @Param("id_loan") Integer id_loan) throws RuntimeException;

    /**
     * Loan Type
     */
    
    @Select("SELECT * FROM finance.loan_type ORDER BY loan_type")
    public List<LoanType> selectAllLoanType() throws RuntimeException;
    
    @Select("SELECT * FROM finance.loan_type WHERE id_loan_type=#{id_loan_type}")
    public LoanType selectOneLoanType(@Param("id_loan_type") Integer id_loan_type) throws RuntimeException;

}
