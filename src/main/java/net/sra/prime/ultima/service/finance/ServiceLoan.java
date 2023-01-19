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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.finance.MapperLoan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.finance.LoanType;
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
public class ServiceLoan {

    @Autowired
    MapperLoan referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Transactional(readOnly = true)
    public List<Loan> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(Integer id) {
        referensi.deleteDetail(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Loan item, List<LoanDetail> lLoanDetail) {
        referensi.insert(item);
        tambahDetail(item.getId_loan(), lLoanDetail);

    }

    @Transactional(readOnly = false)
    public void tambahDetail(Integer id_loan, List<LoanDetail> lLoanDetail) {
        LoanDetail detail = new LoanDetail();

        for (int i = 0; i < lLoanDetail.size(); i++) {
            detail = lLoanDetail.get(i);
            detail.setId_loan(id_loan);
            detail.setSequence(i + 1);
            referensi.insertDetail(detail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(Loan item, List<LoanDetail> lLoanDetail) {
        referensi.update(item);
        deleteDetail(item.getId_loan());
        tambahDetail(item.getId_loan(), lLoanDetail);
    }

    @Transactional(readOnly = true)
    public Loan onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<LoanDetail> onLoadListDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }

    @Transactional(readOnly = true)
    public LoanDetail onLoadDetail(Integer id, Integer sequence) {
        return referensi.selectOneDetail(id, sequence);
    }

    
    
    @Transactional(readOnly = true)
    public String selectMax (Integer year) {
        return referensi.SelectMax(year);
    }
    
    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai (String id_pegawai){
        return mapperPegawai.selectOne(id_pegawai);
    }
    
    /**
     * Loan Type
     */
    
    @Transactional(readOnly = true)
    public List<LoanType> onLoadListLoanType() {
        return referensi.selectAllLoanType();
    }
    
    @Transactional(readOnly = true)
    public LoanType onLoadLoanType(Integer id_loan_type) {
        return referensi.selectOneLoanType(id_loan_type);
    }
}
