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
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokValue;
import net.sra.prime.ultima.entity.StokValue;
import net.sra.prime.ultima.entity.Gudang;
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
public class ServiceStokValue {

    @Autowired
    MapperStokValue referensi;

    @Autowired
    MapperReferensi mapperReferensi;

    @Transactional(readOnly = false)
    public void posting(String tahun) {

        String thn = Integer.toString(Integer.parseInt(tahun) + 1);

        referensi.updateSaldoAkhirAll(tahun);
        List<Gudang> lGudang = mapperReferensi.selectAllGudang();
        StokValue item;
        for (int k = 0; k < lGudang.size(); k++) {

            ///// Ambil tahun sebelumnya
            List<StokValue> lStokValue = referensi.selectOneYearTutuptahun(tahun, lGudang.get(k).getId_gudang());
            
            for (int i = 0; i < lStokValue.size(); i++) {
                item = new StokValue();
                item.setYears(thn);
                item.setId_barang(lStokValue.get(i).getId_barang());
                item.setId_gudang(lGudang.get(k).getId_gudang());
                // Cek apakah account yang bersangkutan sudah ada ditahun setelahnya jika tidak maka eksekusi dibawah ini
                if (referensi.selectOneStokValue(thn, lStokValue.get(i).getId_barang(), lGudang.get(k).getId_gudang()) == null) {
                    
                    referensi.insertSaldoAwal(item);
                }
                if (lStokValue.get(i).getDb13() - lStokValue.get(i).getCr13() >= 0) {
                    item.setDb0(lStokValue.get(i).getDb13() - lStokValue.get(i).getCr13());
                    item.setCr0(0.00);
                } else {
                    item.setCr0(lStokValue.get(i).getCr13() - lStokValue.get(i).getDb13());
                    item.setDb0(0.00);
                }
                referensi.updateSaldoAwal(item);

            }
        }
        referensi.updateSaldoAkhirAll(thn);

    }

}
