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
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccount;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
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
public class ServiceLaporanNeracaSaldo {

    @Autowired
    MapperAccGl referensi;

    @Autowired
    MapperAccount mapperAccount;

    @Transactional(readOnly = true)
    public List<AccGlTrans> onLoad() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadDetail(String id) {
        return referensi.selectOneDetail(id);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadAllDetail() {
        return referensi.selectAllDetail();
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> onLoadListNeracaSaldo(String tahun, Integer bulan) {
        List<AccGlDetail> lAccGlDetails = new ArrayList<>();
        List<AccValue> lAccValues = referensi.selectOneYear(tahun);
        for (int i = 0; i < lAccValues.size(); i++) {
            AccGlDetail accGlDetail = new AccGlDetail();
            Double stokawal = 0.00;
            Double[] nilaiplus = {
                lAccValues.get(i).getDB0(),
                lAccValues.get(i).getDB1(),
                lAccValues.get(i).getDB2(),
                lAccValues.get(i).getDB3(),
                lAccValues.get(i).getDB4(),
                lAccValues.get(i).getDB5(),
                lAccValues.get(i).getDB6(),
                lAccValues.get(i).getDB7(),
                lAccValues.get(i).getDB8(),
                lAccValues.get(i).getDB9(),
                lAccValues.get(i).getDB10(),
                lAccValues.get(i).getDB11(),
                lAccValues.get(i).getDB12(),};
            Double[] nilaimin = {
                lAccValues.get(i).getCR0(),
                lAccValues.get(i).getCR1(),
                lAccValues.get(i).getCR2(),
                lAccValues.get(i).getCR3(),
                lAccValues.get(i).getCR4(),
                lAccValues.get(i).getCR5(),
                lAccValues.get(i).getCR6(),
                lAccValues.get(i).getCR7(),
                lAccValues.get(i).getCR8(),
                lAccValues.get(i).getCR9(),
                lAccValues.get(i).getCR10(),
                lAccValues.get(i).getCR11(),
                lAccValues.get(i).getCR12(),};
            for (int j = 0; j <= bulan; j++) {
                if (nilaiplus[j] == null) {
                    nilaiplus[j] = 0.00;
                }
                if (nilaimin[j] == null) {
                    nilaimin[j] = 0.00;
                }
                stokawal = stokawal + nilaiplus[j] - nilaimin[j];
            }
            if (stokawal != 0) {
                accGlDetail.setId_account(lAccValues.get(i).getAccount());
                accGlDetail.setAccount(lAccValues.get(i).getNama_account());
                if (stokawal > 0) {
                    accGlDetail.setDebit(stokawal);
                } else if (stokawal < 0) {
                    accGlDetail.setCredit(stokawal * -1);
                }
                lAccGlDetails.add(accGlDetail);
            }
        }
        return lAccGlDetails;
    }

    ///////////////////////////   Neraca  /////////////////////////////////////
    @Transactional(readOnly = false)
    public List<AccGlDetail> onLoadListNeraca(String tahun, Integer bulan) {
        List<AccGlDetail> lAccGlDetails = new ArrayList<>();
        List<AccValue> lAccValues = referensi.selectOneYearNeraca(tahun, 10000000, 20000000);
        Double total_aktiva = 0.00;
        for (int i = 0; i < lAccValues.size(); i++) {
            AccGlDetail accGlDetail = new AccGlDetail();
            Double stokawal = 0.00;
            Double[] nilaiplus = {
                lAccValues.get(i).getDB0(),
                lAccValues.get(i).getDB1(),
                lAccValues.get(i).getDB2(),
                lAccValues.get(i).getDB3(),
                lAccValues.get(i).getDB4(),
                lAccValues.get(i).getDB5(),
                lAccValues.get(i).getDB6(),
                lAccValues.get(i).getDB7(),
                lAccValues.get(i).getDB8(),
                lAccValues.get(i).getDB9(),
                lAccValues.get(i).getDB10(),
                lAccValues.get(i).getDB11(),
                lAccValues.get(i).getDB12(),};
            Double[] nilaimin = {
                lAccValues.get(i).getCR0(),
                lAccValues.get(i).getCR1(),
                lAccValues.get(i).getCR2(),
                lAccValues.get(i).getCR3(),
                lAccValues.get(i).getCR4(),
                lAccValues.get(i).getCR5(),
                lAccValues.get(i).getCR6(),
                lAccValues.get(i).getCR7(),
                lAccValues.get(i).getCR8(),
                lAccValues.get(i).getCR9(),
                lAccValues.get(i).getCR10(),
                lAccValues.get(i).getCR11(),
                lAccValues.get(i).getCR12(),};
            for (int j = 0; j <= bulan; j++) {
                if (nilaiplus[j] == null) {
                    nilaiplus[j] = 0.00;
                }
                if (nilaimin[j] == null) {
                    nilaimin[j] = 0.00;
                }
                stokawal = stokawal + nilaiplus[j] - nilaimin[j];
            }
            if ((stokawal != 0 && lAccValues.get(i).getLevel() == 4) || lAccValues.get(i).getLevel() != 4) {
                accGlDetail.setId_account(lAccValues.get(i).getId_account());
                accGlDetail.setAccount(lAccValues.get(i).getNama_account());
                if (stokawal != 0) {
                    accGlDetail.setDebit(stokawal);
                }
                lAccGlDetails.add(accGlDetail);
            }
            total_aktiva = total_aktiva + stokawal;
        }
        lAccGlDetails.add(new AccGlDetail());
        AccGlDetail accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Total Aktiva");
        accGlDetail.setDebit(total_aktiva);
        lAccGlDetails.add(accGlDetail);
        lAccGlDetails.add(new AccGlDetail());

        ///////////////////  Hutang ///////////////////
        List<AccValue> lAccValuesHutang = referensi.selectOneYearNeraca(tahun, 20000000, 30000000);
        Double total_hutang = 0.00;
        for (int i = 0; i < lAccValuesHutang.size(); i++) {
            accGlDetail = new AccGlDetail();
            Double stokawal = 0.00;
            Double[] nilaiplus = {
                lAccValuesHutang.get(i).getDB0(),
                lAccValuesHutang.get(i).getDB1(),
                lAccValuesHutang.get(i).getDB2(),
                lAccValuesHutang.get(i).getDB3(),
                lAccValuesHutang.get(i).getDB4(),
                lAccValuesHutang.get(i).getDB5(),
                lAccValuesHutang.get(i).getDB6(),
                lAccValuesHutang.get(i).getDB7(),
                lAccValuesHutang.get(i).getDB8(),
                lAccValuesHutang.get(i).getDB9(),
                lAccValuesHutang.get(i).getDB10(),
                lAccValuesHutang.get(i).getDB11(),
                lAccValuesHutang.get(i).getDB12(),};
            Double[] nilaimin = {
                lAccValuesHutang.get(i).getCR0(),
                lAccValuesHutang.get(i).getCR1(),
                lAccValuesHutang.get(i).getCR2(),
                lAccValuesHutang.get(i).getCR3(),
                lAccValuesHutang.get(i).getCR4(),
                lAccValuesHutang.get(i).getCR5(),
                lAccValuesHutang.get(i).getCR6(),
                lAccValuesHutang.get(i).getCR7(),
                lAccValuesHutang.get(i).getCR8(),
                lAccValuesHutang.get(i).getCR9(),
                lAccValuesHutang.get(i).getCR10(),
                lAccValuesHutang.get(i).getCR11(),
                lAccValuesHutang.get(i).getCR12(),};
            for (int j = 0; j <= bulan; j++) {
                if (nilaiplus[j] == null) {
                    nilaiplus[j] = 0.00;
                }
                if (nilaimin[j] == null) {
                    nilaimin[j] = 0.00;
                }
                stokawal = stokawal + nilaimin[j] - nilaiplus[j];
            }
            if ((stokawal != 0 && lAccValuesHutang.get(i).getLevel() == 4) || lAccValuesHutang.get(i).getLevel() != 4) {
                accGlDetail.setId_account(lAccValuesHutang.get(i).getId_account());
                accGlDetail.setAccount(lAccValuesHutang.get(i).getNama_account());
                if (stokawal != 0) {
                    accGlDetail.setDebit(stokawal);
                }
                lAccGlDetails.add(accGlDetail);
            }
            total_hutang = total_hutang + stokawal;
        }
        lAccGlDetails.add(new AccGlDetail());
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Total Hutang");
        accGlDetail.setDebit(total_hutang);
        lAccGlDetails.add(accGlDetail);

        /////////////// Net Asset //////////
        Double net_asset = 0.00;
        net_asset = total_aktiva - total_hutang;
        lAccGlDetails.add(new AccGlDetail());
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Net Assets");
        accGlDetail.setDebit(net_asset);
        lAccGlDetails.add(accGlDetail);
        lAccGlDetails.add(new AccGlDetail());

        ///////////// Modal ///////////////////
        if (referensi.selectOneAccValue(tahun, 30201002) == null) {
            AccValue av = new AccValue();
            av.setAccount(30201002);
            av.setYears(tahun);
            av.setId_perusahaan("01");
            referensi.insertAccValue(av);
        }
        Double laba_tahun_berjalan = onLoadLabarugi(tahun, bulan);

        List<AccValue> lAccValuesModal = referensi.selectOneYearNeraca(tahun, 30000000, 40000000);
        Double total_modal = 0.00;
        for (int i = 0; i < lAccValuesModal.size(); i++) {
            accGlDetail = new AccGlDetail();
            Double stokawal = 0.00;
            Double[] nilaiplus = {
                lAccValuesModal.get(i).getDB0(),
                lAccValuesModal.get(i).getDB1(),
                lAccValuesModal.get(i).getDB2(),
                lAccValuesModal.get(i).getDB3(),
                lAccValuesModal.get(i).getDB4(),
                lAccValuesModal.get(i).getDB5(),
                lAccValuesModal.get(i).getDB6(),
                lAccValuesModal.get(i).getDB7(),
                lAccValuesModal.get(i).getDB8(),
                lAccValuesModal.get(i).getDB9(),
                lAccValuesModal.get(i).getDB10(),
                lAccValuesModal.get(i).getDB11(),
                lAccValuesModal.get(i).getDB12(),};
            Double[] nilaimin = {
                lAccValuesModal.get(i).getCR0(),
                lAccValuesModal.get(i).getCR1(),
                lAccValuesModal.get(i).getCR2(),
                lAccValuesModal.get(i).getCR3(),
                lAccValuesModal.get(i).getCR4(),
                lAccValuesModal.get(i).getCR5(),
                lAccValuesModal.get(i).getCR6(),
                lAccValuesModal.get(i).getCR7(),
                lAccValuesModal.get(i).getCR8(),
                lAccValuesModal.get(i).getCR9(),
                lAccValuesModal.get(i).getCR10(),
                lAccValuesModal.get(i).getCR11(),
                lAccValuesModal.get(i).getCR12(),};
            for (int j = 0; j <= bulan; j++) {
                if (nilaiplus[j] == null) {
                    nilaiplus[j] = 0.00;
                }
                if (nilaimin[j] == null) {
                    nilaimin[j] = 0.00;
                }
                stokawal = stokawal + nilaimin[j] - nilaiplus[j];
                // if (lAccValuesModal.get(i).getId_account() == 30201002) {
                //   stokawal=laba_tahun_berjalan;
                // }
            }
            if ((stokawal != 0 && lAccValuesModal.get(i).getLevel() == 4) || lAccValuesModal.get(i).getLevel() != 4) {
                accGlDetail.setId_account(lAccValuesModal.get(i).getId_account());
                accGlDetail.setAccount(lAccValuesModal.get(i).getNama_account());
                if (stokawal != 0) {
                    accGlDetail.setDebit(stokawal);
                }
                lAccGlDetails.add(accGlDetail);
            }
            total_modal = total_modal + stokawal;

        }

        accGlDetail.setId_account(30201002);
        accGlDetail.setAccount("Laba (Rugi) Tahun Berjalan");
        if (laba_tahun_berjalan != 0) {
            accGlDetail.setDebit(laba_tahun_berjalan);
        }
        lAccGlDetails.add(accGlDetail);
        total_modal = total_modal + laba_tahun_berjalan;

        //total_modal = total_modal + laba_tahun_berjalan;
        lAccGlDetails.add(new AccGlDetail());
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Total Modal");
        accGlDetail.setDebit(total_modal);
        lAccGlDetails.add(accGlDetail);

        return lAccGlDetails;
    }

    //////////// labaruginya neh ////////
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

    @Transactional(readOnly = true)
    public Double onLoadListSumNeraca(String year, Integer month, Integer accbottom, Integer acctop, Character type) {

        return referensi.numSaldoAwal(month, year, accbottom, acctop, type);

    }

    @Transactional(readOnly = true)
    public List<Account> selectAccountLevel(Integer accbottom, Integer acctop, Integer level) {
        return mapperAccount.selectAccountLevel(accbottom, acctop, level);
    }

    @Transactional(readOnly = true)
    public List<AccGlDetail> selectBiayaOperasionall(Integer bulan, Integer tahun, Integer id_account) {
        return referensi.selectBiayaOperasioanal(bulan, tahun, id_account);
    }

    @Transactional(readOnly = true)
    public Integer selectAccountKantor(String id_kantor) {
        return referensi.selectAccountKantor(id_kantor);
    }

    @Transactional(readOnly = false)
    public List<AccGlDetail> onLoadArusKas(String tahun) {
        List<AccGlDetail> lAccGlDetails = new ArrayList<>();
        Double kas=0.00;
        AccGlDetail accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Arus kas dari aktivitas operasi");
        lAccGlDetails.add(accGlDetail);

        Double tmp = referensi.numArusKas(12, tahun, 30201002, 30201003, 'K');
        Double labarugi = onLoadLabarugi(tahun, 12);
        if (tmp != null) {
            labarugi = labarugi + tmp;
        }

        kas=kas+labarugi;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Laba tahun berjalan");
        accGlDetail.setDebit(labarugi);
        lAccGlDetails.add(accGlDetail);
        

        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Penyesuaian rugi bersih terhadap kas bersih yang diperoleh dari aktivitas operasi;");
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.selectPenyusutan(tahun);
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Penyusutan");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);
        lAccGlDetails.add(new AccGlDetail());

        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Perubahan aset lancar dan hutang lancar:");
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 10200000, 10300000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("    Piutang Usaha");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        Double pll = 0.00;
        tmp = referensi.numArusKas(12, tahun, 10300000, 10400000, 'K');
        if (tmp != null) {
            pll = pll + tmp;
        }
        tmp = referensi.numArusKas(12, tahun, 10700000, 10800000, 'K');
        if (tmp != null) {
            pll = pll + tmp;
        }
        kas = kas + pll;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Piutan Lain-lain");
        accGlDetail.setDebit(pll);
        lAccGlDetails.add(accGlDetail);

        tmp=referensi.numArusKas(12, tahun, 10401000, 10405000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Piutang Karyawan dan Direksi");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp=referensi.numArusKas(12, tahun, 10502000, 10505000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Uang Muka");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 10501000, 10502000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Uang Muka Pajak");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 10600000, 10603000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Persediaan");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 20100000, 20200000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Utang Usaha");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        
        tmp = referensi.numArusKas(12, tahun, 20200000, 20300000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Biaya yang masih harus dibayar");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 20403000, 20700000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Utang lain-lain");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 20300000, 20400000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Utang Pajak");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 20701000, 20702000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Utang Bank");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);

        tmp = referensi.numArusKas(12, tahun, 20402000, 20403000, 'K');
        kas = kas + tmp;
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Utang Dividen");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);
        
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Jumlah kas bersih yang diperoleh dari aktivitas operasi");
        accGlDetail.setDebit(kas);
        lAccGlDetails.add(accGlDetail);
        lAccGlDetails.add(new AccGlDetail());
        
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Arus kas dari aktivitas Investasi");
        lAccGlDetails.add(accGlDetail);
        
        tmp = referensi.numArusKas(12, tahun, 10801001, 10801007, 'D');
        accGlDetail = new AccGlDetail();
        accGlDetail.setAccount("Pembelian/Pengeluaran aset tetap");
        accGlDetail.setDebit(tmp);
        lAccGlDetails.add(accGlDetail);
        
        return lAccGlDetails;
    }

}
