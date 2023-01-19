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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import net.sra.prime.ultima.db.mapper.MapperAccKas;
import net.sra.prime.ultima.db.mapper.MapperKantor;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.entity.AccKas;
import net.sra.prime.ultima.entity.AccKasDetil;
import net.sra.prime.ultima.entity.AccValue;
import net.sra.prime.ultima.entity.InternalKantorCabang;
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
public class ServiceAccKas {

    @Autowired
    MapperAccKas referensi;

    @Autowired
    MapperKantor mapperKantor;

    @Autowired
    MapperAccGl mapperAccGl;

    @Transactional(readOnly = true)
    public String noMax(String idKantor, Character jenis, Integer bulan, Integer tahun) {
        return referensi.SelectMax(idKantor, jenis, bulan, tahun);
    }

    @Transactional(readOnly = true)
    public InternalKantorCabang selectOneKantor(String idKantor) {
        return mapperKantor.selectOne(idKantor);
    }

    @Transactional(readOnly = true)
    public List<AccKas> onLoadList(Date awal, Date akhir, String idKantor, Character jenis) {
        return referensi.selectAll(awal, akhir, idKantor, jenis);
    }

    @Transactional(readOnly = true)
    public AccKas onLoad(String nomor) {
        return referensi.selectOne(nomor);
    }

    @Transactional(readOnly = true)
    public List<AccKasDetil> onLoadDetail(String nomor) {
        return referensi.selectAllDetail(nomor);
    }

    @Transactional(readOnly = false)
    public void delete(String nomor) {
        AccGlTrans itemTrans = mapperAccGl.selectOneRef(nomor);
        mapperAccGl.deleteDetail(itemTrans.getGl_number());
        mapperAccGl.delete(itemTrans.getGl_number());
        referensi.deleteDetail(nomor);
        referensi.delete(nomor);

    }

    @Transactional(readOnly = false)
    public void ubah(AccKas item, List<AccKasDetil> lAccKasDetil) {
        referensi.deleteDetail(item.getNomor());
        referensi.update(item);
        tambahdetail(item, lAccKasDetil);
        if (item.getStatus().equals('S')) {
            AccGlTrans itemTrans = mapperAccGl.selectOneRef(item.getNomor());
            itemTrans.setPosting(Boolean.FALSE);
            itemTrans.setNote(item.getUraian());
            itemTrans.setGl_date(item.getTanggal());
            mapperAccGl.updatePosting(itemTrans);
            mapperAccGl.deleteDetail(itemTrans.getGl_number());
            tambahjurnal(item, lAccKasDetil, itemTrans);
        } else if (item.getStatus().equals('P')) {
            AccGlTrans itemTrans = mapperAccGl.selectOneRef(item.getNomor());
            itemTrans.setPosting(Boolean.TRUE);
            itemTrans.setNote(item.getUraian());
            itemTrans.setGl_date(item.getTanggal());
            mapperAccGl.updatePosting(itemTrans);
            mapperAccGl.deleteDetail(itemTrans.getGl_number());
            tambahjurnal(item, lAccKasDetil, itemTrans);
            updateAccValue(itemTrans);
        }
    }

    @Transactional(readOnly = false)
    public void tambah(AccKas item, List<AccKasDetil> lAccKasDetil) {
        referensi.insert(item);
        tambahdetail(item, lAccKasDetil);
        AccGlTrans itemTrans = nomorGl(item);
        mapperAccGl.insert(itemTrans);
        tambahjurnal(item, lAccKasDetil, itemTrans);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(AccKas item, List<AccKasDetil> lAccKasDetil) {
        for (int i = 0; i < lAccKasDetil.size(); i++) {
            AccKasDetil itemdetail = lAccKasDetil.get(i);
            itemdetail.setNomor(item.getNomor());
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void tambahjurnal(AccKas item, List<AccKasDetil> lAccKasDetil, AccGlTrans itemTrans) {
        AccGlDetail accGlDetail = new AccGlDetail();
        Integer line = 1;
        /////////// untuk kas : keluar biaya pada debet kas di kredit////////////////
        if (item.getId_jenis_transaksi().equals('O')) {
            for (int i = 0; i < lAccKasDetil.size(); i++) {
                accGlDetail = new AccGlDetail();
                accGlDetail.setJournal_code(itemTrans.getJournal_code());
                accGlDetail.setGl_number(itemTrans.getGl_number());
                accGlDetail.setGl_date(itemTrans.getGl_date());
                accGlDetail.setId_account(lAccKasDetil.get(i).getId_account_detail());
                accGlDetail.setLine(line);
                accGlDetail.setKeterangan(lAccKasDetil.get(i).getUraian());

                if (lAccKasDetil.get(i).getJumlah() < 0) {
                    accGlDetail.setIs_debit(Boolean.FALSE);
                    accGlDetail.setValue(lAccKasDetil.get(i).getJumlah() * -1);
                } else {
                    accGlDetail.setIs_debit(Boolean.TRUE);
                    accGlDetail.setValue(lAccKasDetil.get(i).getJumlah());
                }
                accGlDetail.setPosted(Boolean.FALSE);
                mapperAccGl.insertDetail(accGlDetail);
                line++;
            }
            accGlDetail.setGl_number(itemTrans.getGl_number());
            accGlDetail.setJournal_code(itemTrans.getJournal_code());
            accGlDetail.setId_account(item.getId_account());
            accGlDetail.setLine(line);
            accGlDetail.setGl_date(item.getTanggal());
            accGlDetail.setKeterangan(item.getUraian());
            accGlDetail.setValue(item.getTotal());
            accGlDetail.setIs_debit(Boolean.FALSE);
            accGlDetail.setPosted(Boolean.FALSE);
            mapperAccGl.insertDetail(accGlDetail);

        } else {
            ////////////// Kas Maauk : kas di debet biaya kredit
            accGlDetail.setGl_number(itemTrans.getGl_number());
            accGlDetail.setJournal_code(itemTrans.getJournal_code());
            accGlDetail.setId_account(item.getId_account());
            accGlDetail.setLine(line);
            accGlDetail.setGl_date(item.getTanggal());
            accGlDetail.setKeterangan(item.getUraian());
            accGlDetail.setValue(item.getTotal());
            accGlDetail.setIs_debit(Boolean.TRUE);
            accGlDetail.setPosted(Boolean.FALSE);
            mapperAccGl.insertDetail(accGlDetail);
            for (int i = 0; i < lAccKasDetil.size(); i++) {
                line++;
                accGlDetail = new AccGlDetail();
                accGlDetail.setJournal_code(itemTrans.getJournal_code());
                accGlDetail.setGl_number(itemTrans.getGl_number());
                accGlDetail.setGl_date(itemTrans.getGl_date());
                accGlDetail.setId_account(lAccKasDetil.get(i).getId_account_detail());
                accGlDetail.setLine(line);
                accGlDetail.setKeterangan(lAccKasDetil.get(i).getUraian());
                if (lAccKasDetil.get(i).getJumlah() < 0) {
                    accGlDetail.setValue(lAccKasDetil.get(i).getJumlah() * -1);
                    accGlDetail.setIs_debit(Boolean.TRUE);
                } else {
                    accGlDetail.setValue(lAccKasDetil.get(i).getJumlah());
                    accGlDetail.setIs_debit(Boolean.FALSE);
                }
                accGlDetail.setPosted(Boolean.FALSE);
                mapperAccGl.insertDetail(accGlDetail);

            }
        }

    }

    public AccGlTrans nomorGl(AccKas item) {
        AccGlTrans itemTrans = new AccGlTrans();
        String tahun = new SimpleDateFormat("yy").format(item.getTanggal());
        String thn = new SimpleDateFormat("yyyy").format(item.getTanggal());
        String noMax = mapperAccGl.SelectMax(Integer.parseInt(thn));
        if (noMax == null) {
            itemTrans.setGl_number("GL000001" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%06d", nomor);
            itemTrans.setGl_number("GL" + noMax + tahun);
        }
        itemTrans.setGl_date(item.getTanggal());
        itemTrans.setJournal_code("JPC");
        itemTrans.setNote(item.getUraian());
        itemTrans.setPosting(Boolean.FALSE);
        itemTrans.setReference(item.getNomor());
        itemTrans.setId_perusahaan("01");
        return itemTrans;
    }

    @Transactional(readOnly = false)
    public void updateAccValue(AccGlTrans item) {
        List<AccGlDetail> lAccGlDetail = mapperAccGl.selectJurnalGl(item.getReference());
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getGl_date());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getGl_date());
        Integer nomor = Integer.parseInt(bulan);

        for (int i = 0; i < lAccGlDetail.size(); i++) {
            AccValue accValue = new AccValue();
            accValue.setYears(tahun);
            accValue.setId_perusahaan("01");
            accValue.setAccount(lAccGlDetail.get(i).getId_account());

            // cek apakah kode account untuk tahun tsb sudah ada
            if (mapperAccGl.selectOneAccValue(tahun, lAccGlDetail.get(i).getId_account()) == null) {
                mapperAccGl.insertAccValue(accValue);
            }
            if (lAccGlDetail.get(i).getIs_debit()) {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'd');
            } else {
                mapperAccGl.updateAccValue(nomor, lAccGlDetail.get(i).getValue(), lAccGlDetail.get(i).getId_account(), tahun, 'c');
            }
            mapperAccGl.updateSaldoAkhir(tahun, lAccGlDetail.get(i).getId_account());
        }
    }

}
