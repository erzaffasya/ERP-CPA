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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenPeriode;
import net.sra.prime.ultima.db.mapper.hr.MapperJadwalKerja;
import net.sra.prime.ultima.db.mapper.hr.MapperLembur;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.hr.AbsenPeriode;
import net.sra.prime.ultima.entity.hr.JadwalKerja;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.sra.prime.ultima.db.mapper.hr.MapperAbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenLembur;
import net.sra.prime.ultima.entity.hr.AbsenLemburHead;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceAbsenLembur {

    @Autowired
    MapperAbsenLembur referensi;

    @Autowired
    MapperJadwalKerja mapperJadwalKerja;

    @Autowired
    MapperLembur mapperLembur;

    @Autowired
    MapperPegawai mapperPegawai;

    
    @Autowired
    MapperAbsenPeriode mapperAbsenPeriode;


    @Transactional(readOnly = true)
    public List<AbsenLembur> onLoadListAbsen(String id_pegawai, Date dateStart, Date dateEnd) {
        return referensi.selectAllHrAbsen(id_pegawai, dateStart, dateEnd);
    }


    @Transactional(readOnly = true)
    public AbsenLemburHead selectOneAbsenByPeriode(String month, Integer year, String id_pegawai) {
        return referensi.selectOneByPeriode(year, month, id_pegawai);
    }

    @Transactional(readOnly = true)
    public JadwalKerja selectOneJadwalKerja(String id_pegawai) {
        return mapperJadwalKerja.selectOneJadwalKerjaFromKaryawan(id_pegawai);
    }


    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String id_pegawai) {
        return mapperPegawai.selectOne(id_pegawai);
    }


    @Transactional(readOnly = true)
    public List<Pegawai> selectAllPegawaiAbsen(Integer month, String year) {
        return referensi.selectAllAbsen(month, year);
    }

    @Transactional(readOnly = true)
    public AbsenPeriode selectOneAbsenPeriode(String year, Integer month) {
        return mapperAbsenPeriode.selectOne(year, month);
    }

    

    
}
