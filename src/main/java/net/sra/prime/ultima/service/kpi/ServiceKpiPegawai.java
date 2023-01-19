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
package net.sra.prime.ultima.service.kpi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.kpi.MapperKpiPegawai;
import net.sra.prime.ultima.entity.kpi.KpiPegawai;
import net.sra.prime.ultima.entity.kpi.KpiPegawaiDetail;
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
public class ServiceKpiPegawai {

    @Autowired
    MapperKpiPegawai referensi;

    @Transactional(readOnly = true)
    public List<KpiPegawai> onLoadList() {
        return referensi.selectAll();
    }

    @Transactional(readOnly = true)
    public List<KpiPegawaiDetail> onLoadListKpiPegawaiDetail(String id_pegawai, String year, Integer month) {
        KpiPegawai kpiPegawai = referensi.selectOne(id_pegawai, year, month);
        if (kpiPegawai == null) {
            return referensi.selectAllKpiPegawai(id_pegawai);
        } else {
            if (kpiPegawai.getStatus().equals('A')) {
                return referensi.selectAllKpiPegawaiPosting(id_pegawai,year, month);
            } else  {
                return referensi.selectAllKpiPegawaiDraft(id_pegawai, year, month);
            }
        }
    }

    @Transactional(readOnly = false)
    public void delete(String id_pegawai, String year) {
        referensi.delete(id_pegawai, year);
    }

    @Transactional(readOnly = false)
    public void tambah(KpiPegawai item, List<KpiPegawaiDetail> lKpiPegawaiDetail) {
        KpiPegawai kpiPegawai = referensi.selectOne(item.getId_pegawai(), item.getYear(), item.getMonth());
        if (kpiPegawai == null) {
            referensi.insert(item);
        } else {
            referensi.update(item);
            deleteDetail(item.getId_pegawai(), item.getYear(), item.getMonth());
        }
        tambahDetail(item, lKpiPegawaiDetail);
    }

    @Transactional(readOnly = false)
    public void ubah(KpiPegawai item) {
        referensi.update(item);
    }

    @Transactional(readOnly = true)
    public KpiPegawai onLoad(String id_pegawai, String year, Integer month) {
        return referensi.selectOne(id_pegawai, year, month);
    }

    /*
    KPI Pegawai Detail
     */
    @Transactional(readOnly = true)
    public List<KpiPegawaiDetail> onLoadListDetail() {
        return referensi.selectAllDetail();
    }

    @Transactional(readOnly = false)
    public void deleteDetail(String id_pegawai, String year, Integer id_indicator) {
        referensi.deleteDetail(id_pegawai, year, id_indicator);
    }

    @Transactional(readOnly = false)
    public void tambahDetail(KpiPegawai item, List<KpiPegawaiDetail> lKPiPegawaiDetail) {
        KpiPegawaiDetail detail = new KpiPegawaiDetail();
        for (int i = 0; i < lKPiPegawaiDetail.size(); i++) {
            detail = lKPiPegawaiDetail.get(i);
            detail.setId_pegawai(item.getId_pegawai());
            detail.setYear(item.getYear());
            detail.setMonth(item.getMonth());
            referensi.insertDetail(detail);
        }

    }

    @Transactional(readOnly = true)
    public KpiPegawai onLoadDetail(String id_pegawai, String year, Integer id_indicator) {
        return referensi.selectOneDetail(id_pegawai, year, id_indicator);
    }

}
