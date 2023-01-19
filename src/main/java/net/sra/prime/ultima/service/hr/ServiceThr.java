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
package net.sra.prime.ultima.service.hr;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.finance.MapperLoan;
import net.sra.prime.ultima.db.mapper.hr.MapperThr;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.Thr;
import net.sra.prime.ultima.entity.hr.ThrDetail;
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
public class ServiceThr {

    @Autowired
    MapperThr referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperLoan mapperLoan;

    @Transactional(readOnly = true)
    public List<Thr> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        referensi.deleteDetail(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void deleteDetail(Integer id) {
        referensi.deleteDetail(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Thr item, List<ThrDetail> lThrDetail) {
        referensi.insert(item);
        tambahDetail(item, lThrDetail);

    }

    @Transactional(readOnly = false)
    public void tambahDetail(Thr item, List<ThrDetail> lThrDetail) {
        ThrDetail detail = new ThrDetail();
        Double gajitetap = 0.00;
        for (int i = 0; i < lThrDetail.size(); i++) {
            
            detail = lThrDetail.get(i);
            detail.setId_thr(item.getId_thr());
            Pegawai pegawai = mapperPegawai.selectOnePegawaiGaji(detail.getId_pegawai());
            if (pegawai != null) {
                gajitetap = pegawai.getGaji_pokok() + pegawai.getTunjangan_jabatan();
                detail.setGaji_pokok(pegawai.getGaji_pokok());
                detail.setTunjangan_jabatan(pegawai.getTunjangan_jabatan());
            }else{
                gajitetap = 0.00;
                detail.setGaji_pokok(0.00);
                detail.setTunjangan_jabatan(0.00);
            }

            if (detail.getYear() >= 1) {
                detail.setValue(gajitetap);
            } else if (detail.getMonth() >= 1) {
                detail.setValue(gajitetap * detail.getMonth() / 12);
            }
            referensi.insertDetail(detail);
        }

    }

    @Transactional(readOnly = false)
    public void ubah(Thr item, List<ThrDetail> lThrDetail) {
        referensi.deleteDetail(item.getId_thr());
        referensi.update(item);
        tambahDetail(item, lThrDetail);
    }

    @Transactional(readOnly = true)
    public Thr onLoad(Integer id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<ThrDetail> onLoadListDetail(Integer id) {
        return referensi.selectAllDetail(id);
    }

    @Transactional(readOnly = true)
    public List<ThrDetail> onLoadListPegawaiSelected(Integer id) {
        return referensi.selectAllPegawaiSelected(id);
    }

    @Transactional(readOnly = true)
    public List<ThrDetail> onLoadListAllPegawai() {
        return referensi.selectAllPegawai();
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String id_pegawai) {
        return mapperPegawai.selectOne(id_pegawai);
    }
    
    @Transactional(readOnly = true)
    public ThrDetail onLoadListRekapThr(Integer id,String id_kantor) {
        return referensi.rekapThr(id, id_kantor);
    }

}
