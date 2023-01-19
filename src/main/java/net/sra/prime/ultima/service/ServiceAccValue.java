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

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.Account;
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
public class ServiceAccValue {

    @Autowired
    MapperAccGl referensi;

    @Autowired
    MapperAccount mapperAccount;

    @Transactional(readOnly = true)
    public List<AccValue> onLoadList(String years) {
        return referensi.selectOneYearTutuptahun(years);
    }

    @Transactional(readOnly = true)
    public AccValue onLoad(String years, Integer id_account) {
        return referensi.selectOneAccValue(years, id_account);
    }

    @Transactional(readOnly = false)
    public void ubah(Integer bulan, Double nilai, Integer account, String years, Character status) {
        referensi.updateSaldoAwal(bulan, nilai, account, years, status);
        referensi.updateSaldoAkhir(years, account);
    }

    @Transactional(readOnly = false)
    public void tambah(AccValue item, Integer bulan) {
        if (item.getDebit() == null || item.getDebit().equals("")) {
            item.setDebit(0.00);
        }
        if (item.getCredit() == null || item.getCredit().equals("")) {
            item.setCredit(0.00);
        }
        referensi.insertAccValue(item);
        referensi.updateSaldoAwal(bulan, item.getDebit(), item.getAccount(), item.getYears(), 'd');
        referensi.updateSaldoAwal(bulan, item.getCredit(), item.getAccount(), item.getYears(), 'c');
        referensi.updateSaldoAkhir(item.getYears(), item.getAccount());
    }

    @Transactional(readOnly = false)
    public void posting(String tahun, Integer bulan) {
        referensi.updateAccValuetoZero(bulan, tahun);
        ///// Untuk DB
        List<AccGlDetail> lAccGlDetail = referensi.selectSumAccount(bulan, Integer.parseInt(tahun), Boolean.TRUE);
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            System.out.println(lAccGlDetail.get(i).getId_account());
            // jika account tersebut tidak ada maka insert dulu
            if (referensi.selectOneAccValue(tahun, lAccGlDetail.get(i).getId_account()) == null) {
                AccValue item = new AccValue();
                item.setYears(tahun);
                item.setAccount(lAccGlDetail.get(i).getId_account());
                item.setId_perusahaan("01");
                
                referensi.insertAccValue(item);
            }
            // untuk input DB kebulan yang dimaksud
            referensi.updateSaldoAwal(bulan, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'd');

        }
        /// Untuk CR
        lAccGlDetail = referensi.selectSumAccount(bulan, Integer.parseInt(tahun), Boolean.FALSE);
        for (int i = 0; i < lAccGlDetail.size(); i++) {
            if (referensi.selectOneAccValue(tahun, lAccGlDetail.get(i).getId_account()) == null) {
                AccValue item = new AccValue();
                item.setYears(tahun);
                item.setAccount(lAccGlDetail.get(i).getId_account());
                item.setId_perusahaan("01");
                referensi.insertAccValue(item);
            }
            // untuk input CR kebulan yang dimaksud
            referensi.updateSaldoAwal(bulan, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'c');

        }
        referensi.updateSaldoAkhirAll(tahun);

    }

    @Transactional(readOnly = false)
    public void postingtahun(String tahun) {

        String thn = Integer.toString(Integer.parseInt(tahun) + 1);
        referensi.updateSaldoAkhirAll(tahun);
        referensi.updateSaldoAwaltoZero(thn);
        Double saldoc = 0.00;
        Double saldod = 0.00;
        ///// Ambil tahun sebelumnya
        List<AccValue> lAccValue = referensi.selectOneYearTutuptahun(tahun);
        for (int i = 0; i < lAccValue.size(); i++) {

            AccValue item = new AccValue();
            // Cek apakah account yang bersangkutan sudah ada ditahun setelahnya jika tidak maka eksekusi dibawah ini
            if (referensi.selectOneAccValue(thn, lAccValue.get(i).getAccount()) == null) {
                item.setYears(thn);
                item.setAccount(lAccValue.get(i).getAccount());
                item.setId_perusahaan("01");
                referensi.insertSaldoAwal(item);
            }
            if (lAccValue.get(i).getDB13() - lAccValue.get(i).getCR13() >= 0) {
                item.setDB0(lAccValue.get(i).getDB13() - lAccValue.get(i).getCR13());
                item.setCR0(0.00);
            } else {
                item.setCR0(lAccValue.get(i).getCR13() - lAccValue.get(i).getDB13());
                item.setDB0(0.00);
            }
            if (lAccValue.get(i).getAccount() != 30201002) {
                referensi.updateSaldoAwal(0, item.getDB0(), lAccValue.get(i).getAccount(), thn, 'd');
                referensi.updateSaldoAwal(0, item.getCR0(), lAccValue.get(i).getAccount(), thn, 'c');
            } else {
                referensi.updateSaldoAwal(0, 0.00, lAccValue.get(i).getAccount(), thn, 'd');
                referensi.updateSaldoAwal(0, 0.00, lAccValue.get(i).getAccount(), thn, 'c');
                saldoc = item.getCR0();
                saldod = item.getDB0();
            }

        }
        referensi.updateAccValue(0, onLoadLabarugi(tahun, 12) + saldoc - saldod, 30201001, thn, 'c');
        referensi.updateSaldoAkhirAll(thn);

    }

    public Double onLoadLabarugi(String tahun, Integer bulan) {
        Double total_pendapatan = 0.00;
        Double total_hpp = 0.00;
        Double total_biaya = 0.00;
        Double total_pendapatan_lain = 0.00;
        Double biaya_lain = 0.00;
        String kode;
        Integer awal, akhir, mulai, panjang;
        Account item = new Account();
        awal = 1;
        akhir = bulan;

        // Pendapatan
        kode = "4";
        mulai = 1;
        panjang = 1;
        List<Account> lAccount1 = mapperAccount.selectLabaRugi(kode, tahun, awal, akhir, mulai, panjang);
        for (int i = 0; i < lAccount1.size(); i++) {
            if (lAccount1.get(i).getDebit() != null) {
                total_pendapatan = total_pendapatan + (lAccount1.get(i).getDebit() * -1);
            }
        }

        //HPP
        kode = "5";
        mulai = 1;
        panjang = 1;
        lAccount1 = mapperAccount.selectLabaRugi(kode, tahun, awal, akhir, mulai, panjang);
        for (int i = 0; i < lAccount1.size(); i++) {
            if (lAccount1.get(i).getDebit() != null) {
                total_hpp = total_hpp + lAccount1.get(i).getDebit();
            }

        }

        //Biaya Operasional
        kode = "6";
        mulai = 1;
        panjang = 1;
        lAccount1 = mapperAccount.selectLabaRugi(kode, tahun, awal, akhir, mulai, panjang);
        for (int i = 0; i < lAccount1.size(); i++) {
            if (lAccount1.get(i).getDebit() != null) {
                total_biaya = total_biaya + lAccount1.get(i).getDebit();
            }

        }

        //Pendapatan Lain-lain
        lAccount1 = mapperAccount.selectLabaRugi("8", tahun, awal, akhir, 1, 1);
        for (int i = 0; i < lAccount1.size(); i++) {
            if (lAccount1.get(i).getDebit() != null) {
                total_pendapatan_lain = total_pendapatan_lain + (lAccount1.get(i).getDebit() * -1);
            }

        }

        //Biaya Lain-lain
        lAccount1 = mapperAccount.selectLabaRugi("9", tahun, awal, akhir, 1, 1);
        for (int i = 0; i < lAccount1.size(); i++) {
            if (lAccount1.get(i).getDebit() != null) {
                biaya_lain = biaya_lain + lAccount1.get(i).getDebit();
            }

        }

        Double netprofit = total_pendapatan - total_hpp - total_biaya + total_pendapatan_lain - biaya_lain;
        return netprofit;
    }

}
