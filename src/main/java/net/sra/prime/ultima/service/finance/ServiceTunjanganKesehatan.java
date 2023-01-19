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
package net.sra.prime.ultima.service.finance;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.finance.MapperTunjanganKesehatan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatan;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatanDetail;
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
public class ServiceTunjanganKesehatan {

    @Autowired
    MapperTunjanganKesehatan referensi;

    @Autowired
    MapperPegawai mapperPegawai;
    
    
    @Transactional(readOnly = true)
    public List<TunjanganKesehatan> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(Integer id) {
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void tambah(TunjanganKesehatan item, List<TunjanganKesehatanDetail> lTunjanganKesehatanDetail) {
        referensi.insert(item);
        tambahDetail(item.getId(), lTunjanganKesehatanDetail);

    }
    
    
    @Transactional(readOnly = false)
    public void tambahDetail(Integer id, List<TunjanganKesehatanDetail> lTunjanganKesehatanDetail) {
        TunjanganKesehatanDetail detail = new TunjanganKesehatanDetail();

        for (int i = 0; i < lTunjanganKesehatanDetail.size(); i++) {
            detail = lTunjanganKesehatanDetail.get(i);
            detail.setId(id);
            detail.setSequence(i + 1);
            referensi.insertDetail(detail);
        }

    }
    
    @Transactional(readOnly = false)
    public void ubah(TunjanganKesehatan item, List<TunjanganKesehatanDetail> lTunjanganKesehatanDetail) {
        referensi.deleteDetail(item.getId());
        referensi.update(item);
        tambahDetail(item.getId(), lTunjanganKesehatanDetail);
    }
    
    
    @Transactional(readOnly = true)
    public List<TunjanganKesehatanDetail> onLoadListDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }
    
    
    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai (String id_pegawai){
        return mapperPegawai.selectOne(id_pegawai);
    }
    
    
    @Transactional(readOnly = true)
    public TunjanganKesehatan selectPeriodeProposal(Integer month, String year){
        return referensi.selectPeriodeProposal(month, year);
    }
    
    @Transactional(readOnly = true)
    public TunjanganKesehatan onLoad(Integer id) {
        return referensi.selectOne(id);
    }
}
