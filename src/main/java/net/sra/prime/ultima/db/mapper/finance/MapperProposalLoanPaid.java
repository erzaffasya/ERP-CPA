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
import net.sra.prime.ultima.entity.finance.ProposalLoanPaid;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetail;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetailManual;
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
public interface MapperProposalLoanPaid {

    @Select("SELECT a.*,b.nama as create_by_name ,b.nip "
            + " FROM finance.proposal_loan_paid a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " ORDER BY a.year,a.month ")
    public List<ProposalLoanPaid> selectAll()  throws RuntimeException;

    @Insert("INSERT INTO finance.proposal_loan_paid "
            + "(date,description1,status,month,year,create_by,create_date,jenis) "
            + " VALUES "
            + "(#{date},#{description1},#{status},#{month},#{year},#{create_by},LOCALTIMESTAMP,#{jenis}) "
    )
    @Options(useGeneratedKeys = true, keyProperty = "id_proposal_loan_paid", keyColumn = "id_proposal_loan_paid")
    public int insert(ProposalLoanPaid loan)  throws RuntimeException;
    
    @Insert("INSERT INTO finance.proposal_loan_paid_detail "
            + "(id_proposal_loan_paid,id_loan,amount,id_pegawai,description,id_loan_type,sequence) "
            + " VALUES "
            + "(#{id_proposal_loan_paid},#{id_loan},#{amount},#{id_pegawai},#{description},#{id_loan_type},#{sequence}) "
    )
    public int insertDetail(ProposalLoanPaidDetail proposalLoanPaidDetail)  throws RuntimeException;
    
    @Insert("INSERT INTO finance.proposal_loan_paid_detail_manual "
            + "(id_proposal_loan_paid,amount,id_pegawai,description,id_loan_type,sequence) "
            + " VALUES "
            + "(#{id_proposal_loan_paid},#{amount},#{id_pegawai},#{description},#{id_loan_type},#{sequence}) "
    )
    public int insertDetailManual(ProposalLoanPaidDetailManual proposalLoanPaidDetailManual)  throws RuntimeException;


 
    @Delete("DELETE FROM finance.proposal_loan_paid WHERE id_proposal_loan_paid = #{id_proposal_loan_paid}")
    public int delete(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid)  throws RuntimeException;
    
    @Delete("DELETE FROM finance.proposal_loan_paid_detail WHERE id_proposal_loan_paid = #{id_proposal_loan_paid}")
    public int deleteDetail(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid)  throws RuntimeException;
    
    @Delete("DELETE FROM finance.proposal_loan_paid_detail_manual WHERE id_proposal_loan_paid = #{id_proposal_loan_paid}")
    public int deleteDetailManual(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid)  throws RuntimeException;

    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM finance.proposal_loan_paid a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE a.id_proposal_loan_paid=#{id_proposal_loan_paid}")
    public ProposalLoanPaid selectOne(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid) throws RuntimeException;
    
    @Select("SELECT a.*,b.nama as create_by_name,b.nip "
            + " FROM finance.proposal_loan_paid a "
            + " INNER JOIN pegawai b ON a.create_by=b.id_pegawai "
            + " WHERE a.year=#{year} AND a.month=#{month} AND a.status='A'")
    public ProposalLoanPaid selectOneByPeriode(@Param("year") String year,
            @Param("month") Integer month) throws RuntimeException;
    
    @Select("SELECT a.*, b.nama, c.loan_type, d.loan_number "
            + " FROM finance.proposal_loan_paid_detail a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN finance.loan_type c ON a.id_loan_type=c.id_loan_type "
            + " INNER JOIN finance.loan d ON a.id_loan=d.id_loan"
            + " WHERE "
            + " a.id_proposal_loan_paid=#{id_proposal_loan_paid}")
    public List<ProposalLoanPaidDetail> selectAllDetail(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid) throws RuntimeException;
    
    @Select("SELECT a.*, b.nama, c.loan_type "
            + " FROM finance.proposal_loan_paid_detail_manual a "
            + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
            + " INNER JOIN finance.loan_type c ON a.id_loan_type=c.id_loan_type "
            + " WHERE "
            + " a.id_proposal_loan_paid=#{id_proposal_loan_paid}")
    public List<ProposalLoanPaidDetailManual> selectAllDetailManual(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid) throws RuntimeException;
    
    @Select("SELECT a.* FROM finance.proposal_loan_paid_detail a "
            + " WHERE "
            + " a.id_proposal_loan_paid=#{id_proposal_loan_paid} AND a.id_loan=#{id_loan} ")
    public ProposalLoanPaidDetail selectOneDetail(@Param("id_proposal_loan_paid") Integer id_proposal_loan_paid,
            @Param("id_loan") Integer id_loan)throws RuntimeException;
    
    
    @Update("UPDATE finance.proposal_loan_paid  SET "
            + " date = #{date}, "
            + " description1 = #{description1}, "
            + " status = #{status}, "
            + " month = #{month}, "
            + " year = #{year} "
            + " WHERE id_proposal_loan_paid = #{id_proposal_loan_paid} ")
    public int update(ProposalLoanPaid loan)  throws RuntimeException;
    
    @Select("SELECT * FROM finance.proposal_loan_paid WHERE month=#{month} AND year=#{year}")
    public ProposalLoanPaid selectPeriodeProposal(@Param("month") Integer month,
            @Param("year") String year) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.amount),0)  "
            + " FROM finance.proposal_loan_paid_detail a "
            + " INNER JOIN finance.proposal_loan_paid b ON a.id_proposal_loan_paid=b.id_proposal_loan_paid  AND b.status='A'"
            + " WHERE a.id_pegawai=#{id_pegawai} AND b.year=#{year} AND b.month=#{month}")
    public Double sumLoanPaid(@Param("id_pegawai") String id_pegawai,
    @Param("year") String year,
    @Param("month") Integer month);
    
    @Select("SELECT COALESCE(SUM(a.amount),0)  "
            + " FROM finance.proposal_loan_paid_detail_manual a "
            + " INNER JOIN finance.proposal_loan_paid b ON a.id_proposal_loan_paid=b.id_proposal_loan_paid  AND b.status='A'"
            + " WHERE a.id_pegawai=#{id_pegawai} AND b.year=#{year} AND b.month=#{month}")
    public Double sumLoanPaidManual(@Param("id_pegawai") String id_pegawai,
    @Param("year") String year,
    @Param("month") Integer month);
            
}
