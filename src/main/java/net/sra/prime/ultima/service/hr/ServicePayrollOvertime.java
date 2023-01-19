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
import net.sra.prime.ultima.db.mapper.hr.MapperPayrollOvertime;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.Loan;
import net.sra.prime.ultima.entity.finance.LoanDetail;
import net.sra.prime.ultima.entity.hr.PayrollOvertime;
import net.sra.prime.ultima.entity.hr.PayrollOvertimeDetail;
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
public class ServicePayrollOvertime {

    @Autowired
    MapperPayrollOvertime referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    @Autowired
    MapperLoan mapperLoan;

    @Transactional(readOnly = true)
    public List<PayrollOvertime> onLoadList(String jenis) {
        return referensi.selectAll(jenis);
    }

    @Transactional(readOnly = false)
    public void delete(String year, Integer month,String jenis) {
        referensi.deleteDetail(year, month,jenis);
        referensi.delete(year, month,jenis);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(String year, Integer month,String jenis) {
        referensi.deleteDetail(year, month,jenis);
    }

    @Transactional(readOnly = false)
    public void tambah(PayrollOvertime item, List<PayrollOvertimeDetail> lPayrollOvertimeDetail) {
        referensi.insert(item);
        tambahDetail(item, lPayrollOvertimeDetail);

    }
    
    
    @Transactional(readOnly = false)
    public void tambahDetail(PayrollOvertime item, List<PayrollOvertimeDetail> lPayrollOvertimeDetail) {
        PayrollOvertimeDetail detail = new PayrollOvertimeDetail();

        for (int i = 0; i < lPayrollOvertimeDetail.size(); i++) {
            detail = lPayrollOvertimeDetail.get(i);
            detail.setYear(item.getYear());
            detail.setMonth(item.getMonth());
            detail.setSequence(i);
            detail.setJenis(item.getJenis());
            referensi.insertDetail(detail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(PayrollOvertime item, List<PayrollOvertimeDetail> lPayrollOvertimeDetail) {
        referensi.deleteDetail(item.getYear(),item.getMonth(),item.getJenis());
        referensi.update(item);
        tambahDetail(item, lPayrollOvertimeDetail);
    }
    
    
    @Transactional(readOnly = true)
    public PayrollOvertime onLoad(String year, Integer month,String jenis) {
        return referensi.selectOne(year, month,jenis);
    }

    @Transactional(readOnly = true)
    public List<PayrollOvertimeDetail> onLoadListDetail(String year, Integer month,String jenis) {
        return referensi.selectAllDetail(year, month,jenis);
    }
    
    
    
    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai (String id_pegawai){
        return mapperPegawai.selectOne(id_pegawai);
    }
    
    
    
    
}
