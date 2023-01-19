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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class ServiceBukuBesar {

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
    public List<AccGlDetail> onLoadAllDetail(Date awal, Date akhir, Integer accountfrom, Integer accountto, String keterangan) throws ParseException {
        List<Account> lAccount = mapperAccount.selectAPeriode(accountfrom, accountto, keterangan);
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(awal);
        DateFormat thn = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(awal);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date awalbulan = format.parse(year + "-" + bulan + "-01");
        Double saldoawal = 0.00;
        List<AccGlDetail> lAccGlDetails=new ArrayList<>();
        for (int i = 0; i < lAccount.size(); i++) {
            saldoawal = onLoadSaldo(year, Integer.parseInt(bulan)-1, lAccount.get(i).getId_account()) + onLoadSaldoAcc(awalbulan, awal, lAccount.get(i).getId_account());
            AccGlDetail accGlDetail= new AccGlDetail();
            accGlDetail.setId_account(lAccount.get(i).getId_account());
            accGlDetail.setAccount(lAccount.get(i).getAccount());
            accGlDetail.setNote("Saldo Awal");
            //accGlDetail.setReference("Saldo Awal");
            //accGlDetail.setGl_date(awal);
            
            if(saldoawal >= 0){
                accGlDetail.setIs_debit(Boolean.TRUE);
            }else{
                accGlDetail.setIs_debit(Boolean.FALSE);
                saldoawal=saldoawal * -1;
            }
            accGlDetail.setValue(saldoawal);
            lAccGlDetails.add(accGlDetail);
            List<AccGlDetail> lAccGlDetailtmp=referensi.selectOneJurnalByAccount(awal, akhir, lAccount.get(i).getId_account());
            for(int j=0;j<lAccGlDetailtmp.size();j++){
                lAccGlDetails.add(lAccGlDetailtmp.get(j));
            }
        }
        return lAccGlDetails;
        //return referensi.selectJurnalByAccount(awal, akhir, accountfrom, accountto);
    }

    @Transactional(readOnly = true)
    public Double onLoadSaldoAcc(Date awal, Date akhir, Integer idAccount) {
        Double db = referensi.selectSumValue(idAccount, awal, akhir, Boolean.TRUE);
        Double cr = referensi.selectSumValue(idAccount, awal, akhir, Boolean.FALSE);
        if (db == null) {
            db = 0.00;
        }
        if (cr == null) {
            cr = 0.00;
        }
        return db - cr;
    }

    @Transactional(readOnly = true)
    public Double onLoadSaldo(String tahun, Integer bulan, Integer idAccount) {
        AccValue accValues = referensi.selectOneYearAccount(tahun, idAccount);
        Double stokawal = 0.00;
        if (accValues != null) {
            Double[] nilaiplus = {
                accValues.getDB0(),
                accValues.getDB1(),
                accValues.getDB2(),
                accValues.getDB3(),
                accValues.getDB4(),
                accValues.getDB5(),
                accValues.getDB6(),
                accValues.getDB7(),
                accValues.getDB8(),
                accValues.getDB9(),
                accValues.getDB10(),
                accValues.getDB11(),
                accValues.getDB12(),};
            Double[] nilaimin = {
                accValues.getCR0(),
                accValues.getCR1(),
                accValues.getCR2(),
                accValues.getCR3(),
                accValues.getCR4(),
                accValues.getCR5(),
                accValues.getCR6(),
                accValues.getCR7(),
                accValues.getCR8(),
                accValues.getCR9(),
                accValues.getCR10(),
                accValues.getCR11(),
                accValues.getCR12(),};
            for (int j = 0; j <= bulan; j++) {
                stokawal = stokawal + nilaiplus[j] - nilaimin[j];
            }
        }
        return stokawal;
    }

    public List<Account> selectAllAccount() {
        return mapperAccount.selectLevel(4);
    }

}
