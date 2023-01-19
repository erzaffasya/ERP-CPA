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
package net.sra.prime.ultima.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccApFaktur;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.AccApFaktur;
import net.sra.prime.ultima.entity.AccOsAp;
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
public class ServiceReportAp {

    @Autowired
    MapperAccApFaktur referensi;
    
    @Autowired
    MapperSupplier mapperSupplier;
    
    @Transactional(readOnly = true)
    public List<AccOsAp> onLoadListUmurInvoice() {
        List<AccOsAp> lAccOsAp = new ArrayList<>();
        List<AccApFaktur> lAccApFaktur= referensi.selectAllSum();
        for(int i =0; i < lAccApFaktur.size(); i++){
            AccOsAp accOsAp= new AccOsAp();
            accOsAp.setVendor_code(lAccApFaktur.get(i).getVendor_code());
            accOsAp.setSupplier(lAccApFaktur.get(i).getSupplier());
            accOsAp.setTotal(lAccApFaktur.get(i).getSumtotal());
            accOsAp.setDay1(referensi.selectOneSum(1, 30, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay2(referensi.selectOneSum(31, 60, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay3(referensi.selectOneSum(61, 90, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay4(referensi.selectOneSum(91, 120, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay5(referensi.selectOneSum(121, 1000000000, lAccApFaktur.get(i).getVendor_code()));
            lAccOsAp.add(accOsAp);
        }
        return lAccOsAp;
    }
    
    @Transactional(readOnly = true)
    public List<AccOsAp> onLoadListUmurOverdue() {
        List<AccOsAp> lAccOsAp = new ArrayList<>();
        List<AccApFaktur> lAccApFaktur= referensi.selectAllSum();
        for(int i =0; i < lAccApFaktur.size(); i++){
            AccOsAp accOsAp= new AccOsAp();
            accOsAp.setVendor_code(lAccApFaktur.get(i).getVendor_code());
            accOsAp.setSupplier(lAccApFaktur.get(i).getSupplier());
            accOsAp.setTotal(lAccApFaktur.get(i).getSumtotal());
            accOsAp.setDay1(referensi.selectOneSumDue0(0, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay2(referensi.selectOneSumDueDate(1, 30, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay3(referensi.selectOneSumDueDate(31, 60, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay4(referensi.selectOneSumDueDate(61, 90, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay5(referensi.selectOneSumDueDate(91, 120, lAccApFaktur.get(i).getVendor_code()));
            accOsAp.setDay6(referensi.selectOneSumDueDate(121, 1000000000, lAccApFaktur.get(i).getVendor_code()));
            lAccOsAp.add(accOsAp);
        }
        return lAccOsAp;
    }
    
    @Transactional(readOnly = true)
    public List<AccOsAp> onLoadListAllInvoice(String customer_code) {
        List<AccOsAp> lAccOsAp = new ArrayList<>();
        List<AccApFaktur> lAccApFaktur= referensi.selectAllInvoice(customer_code);
        for(int i =0; i < lAccApFaktur.size(); i++){
            AccOsAp accOsAp= new AccOsAp();
            accOsAp.setAp_number(lAccApFaktur.get(i).getReff());
            accOsAp.setAp_date(lAccApFaktur.get(i).getReceive_date());
            accOsAp.setUmur(referensi.selectUmurInvoice(lAccApFaktur.get(i).getAp_number()));
            accOsAp.setDay1(referensi.selectOneInvoiceSupplier(1, 30, lAccApFaktur.get(i).getVendor_code(),lAccApFaktur.get(i).getAp_number()));
            accOsAp.setDay2(referensi.selectOneInvoiceSupplier(31, 60, lAccApFaktur.get(i).getVendor_code(),lAccApFaktur.get(i).getAp_number()));
            accOsAp.setDay3(referensi.selectOneInvoiceSupplier(61, 90, lAccApFaktur.get(i).getVendor_code(),lAccApFaktur.get(i).getAp_number()));
            accOsAp.setDay4(referensi.selectOneInvoiceSupplier(91, 120, lAccApFaktur.get(i).getVendor_code(),lAccApFaktur.get(i).getAp_number()));
            accOsAp.setDay5(referensi.selectOneInvoiceSupplier(121, 1000000000, lAccApFaktur.get(i).getVendor_code(),lAccApFaktur.get(i).getAp_number()));
            lAccOsAp.add(accOsAp);
        }
        return lAccOsAp;
    }
    
    public String namaSupplier(String idSupplier){
        return mapperSupplier.selectOne(idSupplier).getSupplier();
    }
    
    @Transactional(readOnly = true)
    public List<AccOsAp> onLoadListCutOff(Integer bulan, Integer tahun, Date awal, Date akhir) {
        Double Payment = 0.0;
        Double dpp = 0.00;
        Double ppn = 0.00;
        Double totalbayar = 0.00;
        List<AccOsAp> lAccOsAp = new ArrayList<>();
        List<AccApFaktur> lAccApFaktur = referensi.selectCutOff(awal, akhir);
        for (int i = 0; i < lAccApFaktur.size(); i++) {
            
            totalbayar = referensi.selectSumBayar(lAccApFaktur.get(i).getAp_number(), awal);
            AccOsAp accOsAp = new AccOsAp();
            accOsAp.setVendor_code(lAccApFaktur.get(i).getVendor_code());
            accOsAp.setSupplier(lAccApFaktur.get(i).getSupplier());
            
            accOsAp.setTop(lAccApFaktur.get(i).getTop());
            accOsAp.setDuedate(lAccApFaktur.get(i).getDue_date());
            // Total sebelum bulan dipilih
            accOsAp.setTotal(lAccApFaktur.get(i).getTotal() - totalbayar);
            accOsAp.setPo_number(lAccApFaktur.get(i).getPo_number());
            accOsAp.setReceive_date(lAccApFaktur.get(i).getReceive_date());
            accOsAp.setTgl_invoice(lAccApFaktur.get(i).getTgl_invoice());
            accOsAp.setNomor_invoice(lAccApFaktur.get(i).getReff());
            accOsAp.setUmur(lAccApFaktur.get(i).getUmur_invoice());
            accOsAp.setUmuroverdue(lAccApFaktur.get(i).getUmur_overdue());
            if(!accOsAp.getVendor_code().equals("010001") && lAccApFaktur.get(i).getPph() > 0){
                accOsAp.setPph(lAccApFaktur.get(i).getPph());
            }
            // Payment bulan dipilih
            Payment = referensi.selectSumPayment(lAccApFaktur.get(i).getAp_number(), bulan, tahun);
            if (Payment > 0) {
                accOsAp.setPayment(Payment);
            }

            accOsAp.setOs(accOsAp.getTotal() - Payment);
            if (lAccApFaktur.get(i).getUmur_overdue() <= 0) {
                accOsAp.setDay1(accOsAp.getOs());
            } else if (lAccApFaktur.get(i).getUmur_overdue() >= 1 && lAccApFaktur.get(i).getUmur_overdue() <= 30) {
                accOsAp.setDay2(accOsAp.getOs());
            } else if (lAccApFaktur.get(i).getUmur_overdue() >= 31 && lAccApFaktur.get(i).getUmur_overdue() <= 60) {
                accOsAp.setDay3(accOsAp.getOs());
            } else if (lAccApFaktur.get(i).getUmur_overdue() >= 61 && lAccApFaktur.get(i).getUmur_overdue() <= 90) {
                accOsAp.setDay4(accOsAp.getOs());
            } else if (lAccApFaktur.get(i).getUmur_overdue() >= 91 && lAccApFaktur.get(i).getUmur_overdue() <= 120) {
                accOsAp.setDay5(accOsAp.getOs());
            } else if (lAccApFaktur.get(i).getUmur_overdue() > 120) {
                accOsAp.setDay6(accOsAp.getOs());
            }

            lAccOsAp.add(accOsAp);
        }
        return lAccOsAp;
    }


}
