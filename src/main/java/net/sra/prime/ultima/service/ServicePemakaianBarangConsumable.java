/*
 * Copyright 2023 JoinFaces.
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
import net.sra.prime.ultima.db.mapper.MapperPemakaianBarangConsumable;
import net.sra.prime.ultima.entity.PemakaianBarangConsumable;
import net.sra.prime.ultima.entity.PemakaianBarangConsumableDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author erza
 */
@Service
@Setter
@Getter
public class ServicePemakaianBarangConsumable {
      @Autowired
    MapperPemakaianBarangConsumable referensi;

    @Transactional(readOnly = true)
    public List<PemakaianBarangConsumable> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = true)
    public List<PemakaianBarangConsumable> onLoadAprroveList() {
        return referensi.selectApprove();
    }
    
    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
        return referensi.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = false)
    public void tambah(PemakaianBarangConsumable item, List<PemakaianBarangConsumableDetail> lPemakaianBarangConsumableDetail) {
        referensi.insert(item);
        this.tambahdetail(lPemakaianBarangConsumableDetail, item);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<PemakaianBarangConsumableDetail> lPemakaianBarangConsumableDetail, PemakaianBarangConsumable item) {

        for (int i = 0; i < lPemakaianBarangConsumableDetail.size(); i++) {
            PemakaianBarangConsumableDetail itemdetail = lPemakaianBarangConsumableDetail.get(i);
            itemdetail.setId(item.getId());
            itemdetail.setId_barang_consumable(lPemakaianBarangConsumableDetail.get(i).getId_barang_consumable());
            itemdetail.setUrut(i + 1);
            itemdetail.setQty(lPemakaianBarangConsumableDetail.get(i).getQty());
            itemdetail.setNote(lPemakaianBarangConsumableDetail.get(i).getNote());
            itemdetail.setId_kendaraan(lPemakaianBarangConsumableDetail.get(i).getId_kendaraan());
            referensi.insertDetail(itemdetail);

        }
    }

    @Transactional(readOnly = false)
    public void deletedetail(Integer id) {
        referensi.deleteDetail(id);
    }

    @Transactional(readOnly = false)
    public void ubah(PemakaianBarangConsumable item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public PemakaianBarangConsumable onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PemakaianBarangConsumableDetail> onLoadDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }
}
