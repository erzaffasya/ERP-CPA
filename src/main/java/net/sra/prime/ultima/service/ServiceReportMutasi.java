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
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Barang;
import net.sra.prime.ultima.entity.KartuStok;
import net.sra.prime.ultima.entity.Mutasi;
import net.sra.prime.ultima.entity.StokValue;
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
public class ServiceReportMutasi {

    @Autowired
    MapperStokBarang referensi;
    
    @Autowired
    MapperBarang mapperBarang;

    @Transactional(readOnly = true)
    public List<Mutasi> onLoadListMutasi(String id_gudang, String tahun, Integer bulan) {
        List<Mutasi> lMutasi = new ArrayList<>();
        List<StokValue> lStokValues = referensi.selectAllStokValue(id_gudang, tahun);
        for(int i=0; i<lStokValues.size();i++){
            Mutasi mutasi = new Mutasi();
            Double stokawal=0.00;
            Double[] nilaiplus ={
                lStokValues.get(i).getDb0(),
                lStokValues.get(i).getDb1(),
                lStokValues.get(i).getDb2(),
                lStokValues.get(i).getDb3(),
                lStokValues.get(i).getDb4(),
                lStokValues.get(i).getDb5(),
                lStokValues.get(i).getDb6(),
                lStokValues.get(i).getDb7(),
                lStokValues.get(i).getDb8(),
                lStokValues.get(i).getDb9(),
                lStokValues.get(i).getDb10(),
                lStokValues.get(i).getDb11(),
                lStokValues.get(i).getDb12(),
            };
            Double[] nilaimin ={
                lStokValues.get(i).getCr0(),
                lStokValues.get(i).getCr1(),
                lStokValues.get(i).getCr2(),
                lStokValues.get(i).getCr3(),
                lStokValues.get(i).getCr4(),
                lStokValues.get(i).getCr5(),
                lStokValues.get(i).getCr6(),
                lStokValues.get(i).getCr7(),
                lStokValues.get(i).getCr8(),
                lStokValues.get(i).getCr9(),
                lStokValues.get(i).getCr10(),
                lStokValues.get(i).getCr11(),
                lStokValues.get(i).getCr12(),
            };
            for(int j=0; j < bulan; j++){
                stokawal=stokawal + nilaiplus[j] - nilaimin[j];
            }
            mutasi.setNama_barang(lStokValues.get(i).getNama_barang());
            mutasi.setSatuan_besar(lStokValues.get(i).getSatuan_besar());
            mutasi.setId_barang(lStokValues.get(i).getId_barang());
            mutasi.setSaldo_awal(stokawal / lStokValues.get(i).getIsi_satuan());
            mutasi.setMasuk(nilaiplus[bulan] / lStokValues.get(i).getIsi_satuan());
            mutasi.setKeluar(nilaimin[bulan] /  lStokValues.get(i).getIsi_satuan());
            mutasi.setSaldo_akhir((mutasi.getSaldo_awal() + mutasi.getMasuk() - mutasi.getKeluar()));
            lMutasi.add(mutasi);
       }
        return lMutasi;
    }
    
    @Transactional(readOnly = true)
    public List<Mutasi> stockMovementDaily(String id_gudang, String tahun, Integer bulan, Date tgl_awal,Date tgl_akhir) {
        return referensi.stockMovementDaily(bulan, id_gudang, tahun, tgl_awal, tgl_akhir);
    }
    
    
    
    @Transactional(readOnly = true)
    public List<KartuStok> onLoadKartuStok(String id_gudang,String id_barang, String year, Integer bulan, Integer tahun,Date tglmulai) {
        return referensi.kartuStok(bulan, id_gudang, id_barang, year, tahun,tglmulai);
       
    }
    
    @Transactional(readOnly = true)
    public Barang selectBarang(String id_barang) {
        return mapperBarang.selectOne(id_barang);
       
    }
    
    @Transactional(readOnly = true)
    public Boolean cekBlind(String id_gudang){
        return referensi.cekBlind(id_gudang);
    }
}
