/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") throws RuntimeException;
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

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.db.provider.ProviderProposalAp;
import net.sra.prime.ultima.entity.AccProposalAp;
import net.sra.prime.ultima.entity.AccProposalApDetail;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAccProposalAp {

    @SelectProvider(type = ProviderProposalAp.class, method = "SelectAll")
    public List<AccProposalAp> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("status") Character status,
            @Param("departemen") Integer departemen
            ) throws RuntimeException;
    
    
    @Select("SELECT a.* "
            + " FROM acc_proposal_ap a "
            + " WHERE a.no_proposal = #{no_proposal}")
    public AccProposalAp selectOne(@Param("no_proposal") String no_proposal) throws RuntimeException;
    
    
    @Insert("INSERT INTO acc_proposal_ap "
            + "(no_proposal, tanggal,  keterangan, status, prepared)"
            + "VALUES "
            + "(#{no_proposal}, #{tanggal},  #{keterangan}, #{status}, #{prepared}) ")
    public int insert(AccProposalAp accProposalAp) throws RuntimeException;

    
    @Update("UPDATE acc_proposal_ap SET "
            + " tanggal = #{tanggal}, "
            + " keterangan = #{keterangan}, "
            + " status = #{status} "
            + " WHERE no_proposal = #{no_proposal}")
    public int update(AccProposalAp accApFaktur) throws RuntimeException;
    
    
    @Update("UPDATE acc_proposal_ap SET "
            + " status = 'C' "
            + " WHERE no_proposal = #{no_proposal}")
    public int updateStatus(AccProposalAp accApFaktur) throws RuntimeException;

    
    @Update("UPDATE acc_proposal_ap SET "
            + " status = 'O' "
            + " WHERE status = 'C'")
    public int updateStatusTOOLD() throws RuntimeException;

    
    
    @Delete("DELETE FROM acc_proposal_ap WHERE no_proposal = #{no_proposal}")
    public int delete(@Param("no_proposal") String no_proposal) throws RuntimeException;

    
    @Select("SELECT MAX(SUBSTR(no_proposal,1,3)) "
            + " FROM acc_proposal_ap "
            + " WHERE  EXTRACT(month FROM tanggal)=#{bulan}  AND EXTRACT(year FROM tanggal)=#{tahun}")
    public String SelectMax( @Param("bulan") Integer bulan, @Param("tahun") Integer tahun) throws RuntimeException;

    
    ////////////////// Detail //////////
    
    @Select("SELECT a.*, b.supplier "
            + " FROM acc_proposal_ap_detail a "
            + " LEFT JOIN supplier b ON a.vendor_code=b.id "
            + " WHERE a.no_proposal = #{no_proposal}"
            + " ORDER BY a.urut ")
    public List<AccProposalApDetail> selectAllDetail(@Param("no_proposal") String no_proposal) throws RuntimeException;
    
    
           
    @Select("SELECT a.*, b.supplier "
            + " FROM acc_proposal_ap_detail a "
            + " LEFT JOIN supplier b ON a.vendor_code=b.id "
            + " INNER JOIN acc_proposal_ap c ON a.no_proposal=c.no_proposal "
            + " WHERE "
            + " a.ap_number=#{ap_number} "
            + " AND c.status='C'")
    public AccProposalApDetail selectOneDetail(@Param("ap_number") String ap_number) throws RuntimeException;
    
    
    @Insert("INSERT INTO acc_proposal_ap_detail "
            + "(no_proposal, ap_number, urut, vendor_code, no_invoice, duedate, dpp, ppn, pph, total, notes) " 
            + "VALUES "
            + "(#{no_proposal}, #{ap_number}, #{urut}, #{vendor_code}, #{no_invoice}, #{duedate}, #{dpp},  #{ppn}, #{pph}, #{total}, #{notes})")
    public int insertDetail(AccProposalApDetail accProposalApDetail) throws RuntimeException;

    
    @Delete("DELETE FROM acc_proposal_ap_detail WHERE no_proposal = #{no_proposal}")
    public int deleteDetail(@Param("no_proposal") String no_proposal) throws RuntimeException;

    
    @Update("UPDATE acc_proposal_ap_detail SET "
            + " st = #{st}, "
            + " notes = #{notes} "
            + " WHERE no_proposal = #{no_proposal} AND ap_number = #{ap_number}")
    public int updateStatusDetail(AccProposalApDetail accProposalApDetail) throws RuntimeException;
    
    
    @Select("SELECT b.*, c.supplier, e.total - e.bayar as jumlah_tagihan "
            + " FROM acc_proposal_ap a "
            + " INNER JOIN acc_proposal_ap_detail b ON a.no_proposal=b.no_proposal "
            + " INNER JOIN acc_ap_faktur e ON e.ap_number=b.ap_number"
            + " LEFT JOIN supplier c ON b.vendor_code=c.id "
            + " WHERE "
            + " a.status = 'C' "
            + " AND b.st='A' "
            + " AND b.vendor_code=#{vendor_code} "
            + " AND e.total - e.bayar > 1 "
            + " ORDER BY b.urut ")
    public List<AccProposalApDetail> selectAllDetailPayment(@Param("vendor_code") String vendor_code) throws RuntimeException;
    

}
