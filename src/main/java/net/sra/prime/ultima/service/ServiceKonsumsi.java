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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperBarang;
import net.sra.prime.ultima.db.mapper.MapperKonsumsi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Konsumsi;
import net.sra.prime.ultima.entity.KonsumsiDetail;
import net.sra.prime.ultima.entity.StokBarang;
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
public class ServiceKonsumsi {

    @Autowired
    MapperKonsumsi referensi;

    @Autowired
    MapperBarang mapperBarang;
    
    @Autowired
    MapperStokBarang mapperStokBarang;

    

    @Transactional(readOnly = true)
    public List<Konsumsi> onLoadList(Date awal, Date akhir, String idGudang) {
        return referensi.selectAll(awal, akhir, idGudang);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Konsumsi item, List<KonsumsiDetail> lKonsumsiDetail) {
        referensi.insert(item);
        this.tambahdetail(lKonsumsiDetail, item);

    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<KonsumsiDetail> lKonsumsiDetail, Konsumsi item) {
        for (int i = 0; i < lKonsumsiDetail.size(); i++) {
            KonsumsiDetail itemdetail = lKonsumsiDetail.get(i);
            itemdetail.setUrut(i + 1);
            referensi.insertDetail(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void updatestok(Konsumsi item, List<KonsumsiDetail> lKonsumsiDetail) {
        Integer j = 1;
        for (int i = 0; i < lKonsumsiDetail.size(); i++) {
            KonsumsiDetail itemdetail = lKonsumsiDetail.get(i);
            Double qty = itemdetail.getPemakaian()* mapperBarang.selectOne(itemdetail.getId_barang()).getIsi_satuan();
            //Update STOK ke tabel stok_barang mengunakan metode fifo menggunakan satuan kecil
            while (qty > 0) {
                StokBarang stokBarang = mapperStokBarang.selectForUpdate(item.getId_gudang(), itemdetail.getId_barang());
                if (stokBarang == null) {
                    qty = 0.00;
                    throw new java.lang.RuntimeException("Stok  tidak cukup !!!");
                } else {
                    if (qty >= stokBarang.getStok()) {
                        qty = qty - stokBarang.getStok();
                        stokBarang.setStok(-1 * stokBarang.getStok());
                        mapperStokBarang.update(stokBarang);
                    } else {
                        Double tmpqty = stokBarang.getStok();
                        stokBarang.setStok(qty * -1);
                        mapperStokBarang.update(stokBarang);
                        qty = qty - tmpqty;
                    }

                }
            }
        }
    }
    
    @Transactional(readOnly = false)
    public void ubah(Konsumsi item, List<KonsumsiDetail> lKonsumsiDetail) {
        referensi.update(item);
        referensi.deleteDetail(item.getId());
        tambahdetail(lKonsumsiDetail, item);
    }

    @Transactional(readOnly = true)
    public Konsumsi onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<KonsumsiDetail> onLoadDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }

    

}
