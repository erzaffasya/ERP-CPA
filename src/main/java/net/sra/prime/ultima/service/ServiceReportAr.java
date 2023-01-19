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
import net.sra.prime.ultima.db.mapper.MapperAccArFaktur;
import net.sra.prime.ultima.db.mapper.MapperCustomer;
import net.sra.prime.ultima.db.mapper.MapperHrJabatan;
import net.sra.prime.ultima.entity.AccArFaktur;
import net.sra.prime.ultima.entity.AccOsAr;
import net.sra.prime.ultima.entity.MasterJabatan;
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
public class ServiceReportAr {

    @Autowired
    MapperAccArFaktur referensi;

    @Autowired
    MapperCustomer mapperCustomer;

    @Autowired
    MapperHrJabatan mapperHrJabatan;

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListUmurInvoice() {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllSumCurrent();
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setTotal(lAccArFaktur.get(i).getSumtotal());
            accOsAr.setDay1(referensi.selectOneSum(0, 30, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay2(referensi.selectOneSum(31, 60, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay3(referensi.selectOneSum(61, 90, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay4(referensi.selectOneSum(91, 120, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay5(referensi.selectOneSum(121, 1000000000, lAccArFaktur.get(i).getCustomer_code()));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListUmurInvoiceByDate(Date awal) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllSum(awal);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setTotal(lAccArFaktur.get(i).getSumtotal());
            accOsAr.setDay1(referensi.selectOneSumByDate(0, 30, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay2(referensi.selectOneSumByDate(31, 60, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay3(referensi.selectOneSumByDate(61, 90, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay4(referensi.selectOneSumByDate(91, 120, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay5(referensi.selectOneSumByDate(121, 1000000000, lAccArFaktur.get(i).getCustomer_code(), awal));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListCutOff(Integer bulan, Integer tahun, Date awal, Date akhir) {
        Double Payment = 0.0;
        Double dpp = 0.00;
        Double ppn = 0.00;
        Double totalbayar = 0.00;
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectCutOff(awal, akhir);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            // DPP dan PPN sebelum bulan dipilih
            dpp = referensi.selectSumDPP(lAccArFaktur.get(i).getAr_number(), awal);
            ppn = referensi.selectSumPPN(lAccArFaktur.get(i).getAr_number(), awal);
            if (dpp == null) {
                dpp = 0.00;
            }
            if (ppn == null) {
                ppn = 0.00;
            }
            // Total bayar sebelum bulan dipilih  
            totalbayar = referensi.selectSumBayar(lAccArFaktur.get(i).getAr_number(), awal);
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setMarketing(lAccArFaktur.get(i).getMarketing());
            if (lAccArFaktur.get(i).getAmount() != null) {
                accOsAr.setDpp(lAccArFaktur.get(i).getAmount() - dpp);
            }
            if (lAccArFaktur.get(i).getPpn() != null) {
                accOsAr.setPpn(lAccArFaktur.get(i).getPpn() - ppn);
            }
            accOsAr.setTop(lAccArFaktur.get(i).getTop());
            accOsAr.setDuedate(lAccArFaktur.get(i).getDue_date());
            // Total sebelum bulan dipilih
            accOsAr.setTotal(lAccArFaktur.get(i).getTotal() - totalbayar);
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getReff());
            accOsAr.setUmur(lAccArFaktur.get(i).getUmur_invoice());
            accOsAr.setUmuroverdue(lAccArFaktur.get(i).getUmur_overdue());
            // Payment bulan dipilih
            if (bulan != null) {
                Payment = referensi.selectSumPayment(lAccArFaktur.get(i).getAr_number(), bulan, tahun);
                if (Payment > 0) {
                    accOsAr.setPayment(Payment);
                }

                accOsAr.setOs(accOsAr.getTotal() - Payment);
            } else {
                accOsAr.setOs(accOsAr.getTotal());
            }
            accOsAr.setArea(lAccArFaktur.get(i).getCabang());
            if (lAccArFaktur.get(i).getUmur_overdue() != null) {
                if (lAccArFaktur.get(i).getUmur_overdue() <= 0) {
                    accOsAr.setDay1(accOsAr.getOs());
                } else if (lAccArFaktur.get(i).getUmur_overdue() >= 1 && lAccArFaktur.get(i).getUmur_overdue() <= 30) {
                    accOsAr.setDay2(accOsAr.getOs());
                } else if (lAccArFaktur.get(i).getUmur_overdue() >= 31 && lAccArFaktur.get(i).getUmur_overdue() <= 60) {
                    accOsAr.setDay3(accOsAr.getOs());
                } else if (lAccArFaktur.get(i).getUmur_overdue() >= 61 && lAccArFaktur.get(i).getUmur_overdue() <= 90) {
                    accOsAr.setDay4(accOsAr.getOs());
                } else if (lAccArFaktur.get(i).getUmur_overdue() >= 91 && lAccArFaktur.get(i).getUmur_overdue() <= 120) {
                    accOsAr.setDay5(accOsAr.getOs());
                } else if (lAccArFaktur.get(i).getUmur_overdue() > 120) {
                    accOsAr.setDay6(accOsAr.getOs());
                }
            }
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListUmurOverdue(Date awal) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllSum(awal);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setTotal(lAccArFaktur.get(i).getSumtotal());
            accOsAr.setDay1(referensi.selectOneSumDue0(0, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay2(referensi.selectOneSumDueDate(1, 30, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay3(referensi.selectOneSumDueDate(31, 60, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay4(referensi.selectOneSumDueDate(61, 90, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay5(referensi.selectOneSumDueDate(91, 120, lAccArFaktur.get(i).getCustomer_code(), awal));
            accOsAr.setDay6(referensi.selectOneSumDueDate(121, 1000000000, lAccArFaktur.get(i).getCustomer_code(), awal));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListUmurOverdueCurrent() {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllSumCurrent();
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setTotal(lAccArFaktur.get(i).getSumtotal());
            accOsAr.setDay1(referensi.selectOneSumDue0Current(0, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay2(referensi.selectOneSumDueDateCurrent(1, 30, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay3(referensi.selectOneSumDueDateCurrent(31, 60, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay4(referensi.selectOneSumDueDateCurrent(61, 90, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay5(referensi.selectOneSumDueDateCurrent(91, 120, lAccArFaktur.get(i).getCustomer_code()));
            accOsAr.setDay6(referensi.selectOneSumDueDateCurrent(121, 1000000000, lAccArFaktur.get(i).getCustomer_code()));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListUmurOverduemarketing(Date awal, String id_salesman) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllSumMarketing(awal, id_salesman);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer_code(lAccArFaktur.get(i).getCustomer_code());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setMarketing(lAccArFaktur.get(i).getMarketing());
            accOsAr.setTotal(lAccArFaktur.get(i).getSumtotal());
            accOsAr.setDay1(referensi.selectOneSumDue0Customer(0, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            accOsAr.setDay2(referensi.selectOneSumDueDateCustomer(0, 30, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            accOsAr.setDay3(referensi.selectOneSumDueDateCustomer(31, 60, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            accOsAr.setDay4(referensi.selectOneSumDueDateCustomer(61, 90, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            accOsAr.setDay5(referensi.selectOneSumDueDateCustomer(91, 120, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            accOsAr.setDay6(referensi.selectOneSumDueDateCustomer(121, 1000000000, lAccArFaktur.get(i).getCustomer_code(), awal, lAccArFaktur.get(i).getId_salesman()));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListAllInvoice(String customer_code) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = referensi.selectAllInvoice(customer_code);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setCabang(lAccArFaktur.get(i).getCabang());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getNomor_po());
            accOsAr.setUmur(referensi.selectUmurInvoice(lAccArFaktur.get(i).getAr_number()));
            accOsAr.setDay1(referensi.selectOneInvoiceCustomer(0, 30, lAccArFaktur.get(i).getCustomer_code(), lAccArFaktur.get(i).getAr_number()));
            accOsAr.setDay2(referensi.selectOneInvoiceCustomer(31, 60, lAccArFaktur.get(i).getCustomer_code(), lAccArFaktur.get(i).getAr_number()));
            accOsAr.setDay3(referensi.selectOneInvoiceCustomer(61, 90, lAccArFaktur.get(i).getCustomer_code(), lAccArFaktur.get(i).getAr_number()));
            accOsAr.setDay4(referensi.selectOneInvoiceCustomer(91, 120, lAccArFaktur.get(i).getCustomer_code(), lAccArFaktur.get(i).getAr_number()));
            accOsAr.setDay5(referensi.selectOneInvoiceCustomer(121, 1000000000, lAccArFaktur.get(i).getCustomer_code(), lAccArFaktur.get(i).getAr_number()));
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListAllOverdue(String customer_code, Integer id_jabatan, String id_pegawai) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = new ArrayList<>();
        lAccArFaktur = referensi.selectAllInvoice(customer_code);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setDuedate(lAccArFaktur.get(i).getDue_date());
            accOsAr.setCabang(lAccArFaktur.get(i).getCabang());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getNomor_po());
            accOsAr.setUmur(lAccArFaktur.get(i).getUmur_invoice());
            accOsAr.setUmuroverdue(lAccArFaktur.get(i).getUmur_overdue());
            accOsAr.setOs(lAccArFaktur.get(i).getSumtotal());
            if (lAccArFaktur.get(i).getUmur_overdue() <= 0) {
                accOsAr.setDay1(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 1 && lAccArFaktur.get(i).getUmur_overdue() <= 30) {
                accOsAr.setDay2(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 31 && lAccArFaktur.get(i).getUmur_overdue() <= 60) {
                accOsAr.setDay3(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 61 && lAccArFaktur.get(i).getUmur_overdue() <= 90) {
                accOsAr.setDay4(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 91 && lAccArFaktur.get(i).getUmur_overdue() <= 120) {
                accOsAr.setDay5(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() > 120) {
                accOsAr.setDay6(accOsAr.getOs());
            }
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }
    
    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListAllOverdueByDsr(String id_pegawai) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = new ArrayList<>();
        lAccArFaktur = referensi.selectAllInvoiceByDsr(id_pegawai);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setDuedate(lAccArFaktur.get(i).getDue_date());
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getNomor_po());
            accOsAr.setUmur(lAccArFaktur.get(i).getUmur_invoice());
            accOsAr.setUmuroverdue(lAccArFaktur.get(i).getUmur_overdue());
            accOsAr.setOs(lAccArFaktur.get(i).getSumtotal());
            if (lAccArFaktur.get(i).getUmur_overdue() <= 0) {
                accOsAr.setDay1(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 1 && lAccArFaktur.get(i).getUmur_overdue() <= 30) {
                accOsAr.setDay2(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 31 && lAccArFaktur.get(i).getUmur_overdue() <= 60) {
                accOsAr.setDay3(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 61 && lAccArFaktur.get(i).getUmur_overdue() <= 90) {
                accOsAr.setDay4(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 91 && lAccArFaktur.get(i).getUmur_overdue() <= 120) {
                accOsAr.setDay5(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() > 120) {
                accOsAr.setDay6(accOsAr.getOs());
            }
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }
    
    
    
    

    @Transactional(readOnly = true)
    public List<AccOsAr> outstandingAr(String id_dsr) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = new ArrayList<>();
        lAccArFaktur = referensi.outstandingAr(id_dsr);
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setCustomer(lAccArFaktur.get(i).getCustomer());
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setDuedate(lAccArFaktur.get(i).getDue_date());
            accOsAr.setCabang(lAccArFaktur.get(i).getCabang());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getNomor_po());
            accOsAr.setUmur(lAccArFaktur.get(i).getUmur_invoice());
            accOsAr.setUmuroverdue(lAccArFaktur.get(i).getUmur_overdue());
            accOsAr.setOs(lAccArFaktur.get(i).getSumtotal());
            if (lAccArFaktur.get(i).getUmur_overdue() <= 0) {
                accOsAr.setDay1(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 1 && lAccArFaktur.get(i).getUmur_overdue() <= 30) {
                accOsAr.setDay2(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 31 && lAccArFaktur.get(i).getUmur_overdue() <= 60) {
                accOsAr.setDay3(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 61 && lAccArFaktur.get(i).getUmur_overdue() <= 90) {
                accOsAr.setDay4(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 91 && lAccArFaktur.get(i).getUmur_overdue() <= 120) {
                accOsAr.setDay5(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() > 120) {
                accOsAr.setDay6(accOsAr.getOs());
            }
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    @Transactional(readOnly = true)
    public List<AccOsAr> onLoadListAgingAR(String customer_code, Integer id_jabatan, String id_pegawai, Date awal) {
        List<AccOsAr> lAccOsAr = new ArrayList<>();
        List<AccArFaktur> lAccArFaktur = new ArrayList<>();
        MasterJabatan masterJabatan = mapperHrJabatan.selectOne(id_jabatan);
        //if (masterJabatan.getId_departemen() == 1 || masterJabatan.getId_departemen() == 4 || masterJabatan.getId_departemen() == 6) {
        lAccArFaktur = referensi.selectAgingAr(customer_code, awal);
//        } else {
//            if (!referensi.selectDsmMapping(id_jabatan)) {
//                if (masterJabatan.getJabatan().toLowerCase().contains("sales admin")) {
//                    lAccArFaktur = referensi.selectAllInvoiceSalesAdmin(customer_code, masterJabatan.getId_kantor());
//                } else {
//                    lAccArFaktur = referensi.selectAllInvoiceDSR(customer_code, id_pegawai);
//                }
//            } else {
//                lAccArFaktur = referensi.selectAllInvoiceDSM(customer_code, id_jabatan);
//            }
//        }
        for (int i = 0; i < lAccArFaktur.size(); i++) {
            AccOsAr accOsAr = new AccOsAr();
            accOsAr.setInvoice_number(lAccArFaktur.get(i).getNo_invoice());
            accOsAr.setInvoice_date(lAccArFaktur.get(i).getInvoice_date());
            accOsAr.setDuedate(lAccArFaktur.get(i).getDue_date());
            accOsAr.setCabang(lAccArFaktur.get(i).getCabang());
            accOsAr.setNomor_po(lAccArFaktur.get(i).getNomor_po());
            accOsAr.setUmur(lAccArFaktur.get(i).getUmur_invoice());
            accOsAr.setUmuroverdue(lAccArFaktur.get(i).getUmur_overdue());
            accOsAr.setOs(lAccArFaktur.get(i).getSumtotal());
            if (lAccArFaktur.get(i).getUmur_overdue() <= 0) {
                accOsAr.setDay1(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 1 && lAccArFaktur.get(i).getUmur_overdue() <= 30) {
                accOsAr.setDay2(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 31 && lAccArFaktur.get(i).getUmur_overdue() <= 60) {
                accOsAr.setDay3(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 61 && lAccArFaktur.get(i).getUmur_overdue() <= 90) {
                accOsAr.setDay4(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() >= 91 && lAccArFaktur.get(i).getUmur_overdue() <= 120) {
                accOsAr.setDay5(accOsAr.getOs());
            } else if (lAccArFaktur.get(i).getUmur_overdue() > 120) {
                accOsAr.setDay6(accOsAr.getOs());
            }
            lAccOsAr.add(accOsAr);
        }
        return lAccOsAr;
    }

    public String namaCustomer(String idCustomer) {
        return mapperCustomer.selectOne(idCustomer).getCustomer();
    }

}
