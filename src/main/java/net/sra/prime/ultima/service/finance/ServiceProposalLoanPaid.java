/*
 * Copyright 2017 JoinFaces.
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
package net.sra.prime.ultima.service.finance;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.finance.MapperLoan;
import net.sra.prime.ultima.db.mapper.finance.MapperProposalLoanPaid;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaid;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetail;
import net.sra.prime.ultima.entity.finance.ProposalLoanPaidDetailManual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceProposalLoanPaid {

    @Autowired
    MapperProposalLoanPaid referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperLoan mapperLoan;

    @Transactional(readOnly = true)
    public List<ProposalLoanPaid> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        ProposalLoanPaid pr = referensi.selectOne(id);
        if(pr.getJenis().equals('O')){
            referensi.deleteDetail(id);
        }else if(pr.getJenis().equals('M')){
            referensi.deleteDetailManual(id);
        }
        
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(ProposalLoanPaid item, List<ProposalLoanPaidDetail> lProposalLoanPaidDetail) {
        referensi.insert(item);
        tambahDetail(item.getId_proposal_loan_paid(), lProposalLoanPaidDetail);

    }
    
    @Transactional(readOnly = false)
    public void tambahManual(ProposalLoanPaid item, List<ProposalLoanPaidDetailManual> lProposalLoanPaidDetailManual) {
        referensi.insert(item);
        tambahDetailManual(item.getId_proposal_loan_paid(), lProposalLoanPaidDetailManual);

    }

    @Transactional(readOnly = false)
    public void tambahDetail(Integer id_proposal_loan_paid, List<ProposalLoanPaidDetail> lProposalLoanPaidDetail) {
        ProposalLoanPaidDetail detail = new ProposalLoanPaidDetail();

        for (int i = 0; i < lProposalLoanPaidDetail.size(); i++) {
            detail = lProposalLoanPaidDetail.get(i);
            detail.setId_proposal_loan_paid(id_proposal_loan_paid);
            detail.setSequence(i + 1);
            referensi.insertDetail(detail);
        }

    }
    
    @Transactional(readOnly = false)
    public void tambahDetailManual(Integer id_proposal_loan_paid, List<ProposalLoanPaidDetailManual> lProposalLoanPaidDetailManual) {
        ProposalLoanPaidDetailManual detail = new ProposalLoanPaidDetailManual();

        for (int i = 0; i < lProposalLoanPaidDetailManual.size(); i++) {
            detail = lProposalLoanPaidDetailManual.get(i);
            detail.setId_proposal_loan_paid(id_proposal_loan_paid);
            detail.setSequence(i + 1);
            referensi.insertDetailManual(detail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(ProposalLoanPaid item, List<ProposalLoanPaidDetail> lProposalLoanPaidDetail) {
        referensi.deleteDetail(item.getId_proposal_loan_paid());
        referensi.update(item);
        tambahDetail(item.getId_proposal_loan_paid(), lProposalLoanPaidDetail);
    }
    
    @Transactional(readOnly = false)
    public void ubahManual(ProposalLoanPaid item, List<ProposalLoanPaidDetailManual> lProposalLoanPaidDetailManual) {
        referensi.deleteDetailManual(item.getId_proposal_loan_paid());
        referensi.update(item);
        tambahDetailManual(item.getId_proposal_loan_paid(), lProposalLoanPaidDetailManual);
    }

    @Transactional(readOnly = true)
    public ProposalLoanPaid onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<ProposalLoanPaidDetail> onLoadListDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }
    
    @Transactional(readOnly = true)
    public List<ProposalLoanPaidDetailManual> onLoadListDetailManual(Integer id) {
        return referensi.selectAllDetailManual(id);
    }

    @Transactional(readOnly = true)
    public ProposalLoanPaidDetail onLoadDetail(Integer id, Integer sequence) {
        return referensi.selectOneDetail(id, sequence);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai (String id_pegawai){
        return mapperPegawai.selectOne(id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public List<Loan> selectRemaingLoan(Date datestart){
        return mapperLoan.selectRemainingLoan(datestart);
    }
    
    @Transactional(readOnly = true)
    public LoanDetail selectRemaingLoanDetail(Integer month, Integer year, Integer id_loan){
        return mapperLoan.selectRemainingLoanDetail(month, year, id_loan);
    }
    
    @Transactional(readOnly = true)
    public ProposalLoanPaid selectPeriodeProposal(Integer month, String year){
        return referensi.selectPeriodeProposal(month, year);
    }
}
