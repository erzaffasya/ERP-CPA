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
package net.sra.prime.ultima.service.hr;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.finance.MapperLoan;
import net.sra.prime.ultima.db.mapper.hr.MapperPayrollProduktivitas;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.hr.PayrollProduktivitas;
import net.sra.prime.ultima.entity.hr.PayrollProduktivitasDetail;
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
public class ServicePayrollProduktivitas {

    @Autowired
    MapperPayrollProduktivitas referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperLoan mapperLoan;

    @Transactional(readOnly = true)
    public List<PayrollProduktivitas> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(String year, Integer month) {
        referensi.deleteDetail(year, month);
        referensi.delete(year, month);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(String year, Integer month) {
        referensi.deleteDetail(year, month);
    }

    @Transactional(readOnly = false)
    public void tambah(PayrollProduktivitas item, List<PayrollProduktivitasDetail> lPayrollProduktivitasDetail) {
        referensi.insert(item);
        tambahDetail(item, lPayrollProduktivitasDetail);

    }
    
    
    @Transactional(readOnly = false)
    public void tambahDetail(PayrollProduktivitas item, List<PayrollProduktivitasDetail> lPayrollProduktivitasDetail) {
        PayrollProduktivitasDetail detail = new PayrollProduktivitasDetail();

        for (int i = 0; i < lPayrollProduktivitasDetail.size(); i++) {
            detail = lPayrollProduktivitasDetail.get(i);
            detail.setYear(item.getYear());
            detail.setMonth(item.getMonth());
            referensi.insertDetail(detail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(PayrollProduktivitas item, List<PayrollProduktivitasDetail> lPayrollProduktivitasDetail) {
        referensi.deleteDetail(item.getYear(),item.getMonth());
        referensi.update(item);
        tambahDetail(item, lPayrollProduktivitasDetail);
    }
    
    
    @Transactional(readOnly = true)
    public PayrollProduktivitas onLoad(String year, Integer month) {
        return referensi.selectOne(year, month);
    }

    @Transactional(readOnly = true)
    public List<PayrollProduktivitasDetail> onLoadListDetail(String year, Integer month) {
        return referensi.selectAllDetail(year, month);
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
    
    
}
