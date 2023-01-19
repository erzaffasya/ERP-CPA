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
import javax.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperSaldoawal;
import net.sra.prime.ultima.entity.Saldoawal;
import net.sra.prime.ultima.entity.SaldoawalDetail;
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
public class ServiceSaldoAwal {

    @Autowired
    MapperSaldoawal referensi;

    @Transactional(readOnly = true)
    public List<Saldoawal> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Saldoawal item, List<SaldoawalDetail> lSaldoawalDetail) {
        referensi.insert(item);
        tambahdetail(lSaldoawalDetail, item);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(List<SaldoawalDetail> lSaldoawalDetail, Saldoawal item) {

        for (int i = 0; i < lSaldoawalDetail.size(); i++) {
            SaldoawalDetail itemdetail = new SaldoawalDetail();
            Integer k = i + 1;
            itemdetail.setId_gudang(item.getId_gudang());
            itemdetail.setUrut(k);
            itemdetail.setId_barang(lSaldoawalDetail.get(i).getId_barang());
            itemdetail.setQty(lSaldoawalDetail.get(i).getQty());
            itemdetail.setHpp(lSaldoawalDetail.get(i).getHpp());
            itemdetail.setTotal(lSaldoawalDetail.get(i).getTotal());
            referensi.insertDetail(itemdetail);

        }

    }

    @Transactional(readOnly = false)
    public void ubah(Saldoawal item, List<SaldoawalDetail> lSaldoawalDetail, Character status) {
        referensi.deleteDetail(item.getId_gudang());
        if (status.equals('S')) {
            referensi.update(item);
        } else if (status.equals('C')) {
            item.setStatus(Boolean.TRUE);
            referensi.updateStatus(item);
        }
        tambahdetail(lSaldoawalDetail, item);
    }

    @Transactional(readOnly = true)
    public Saldoawal onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<SaldoawalDetail> onLoadDetail(String id) {
        return referensi.selectAllDetail(id);
    }

}
