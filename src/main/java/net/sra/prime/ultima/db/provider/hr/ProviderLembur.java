/*
 * Copyright 2016 JoinFaces.
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
package net.sra.prime.ultima.db.provider.hr;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderLembur {
    
    public String selectLemburKaryawan(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, c.nama as nama_pegawai,c.nip as nik "
            + " FROM hr.lembur_karyawan a "
            + " INNER JOIN hr.lembur b ON a.id_lembur=b.id "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " WHERE date_part('month', date (a.tanggal))=date_part('month',date (#{bulan})) "
            + " AND date_part('year',date (a.tanggal))=date_part('year',date (#{bulan})) "
            );

        Integer id_departemen = (Integer) parameters.get("id_departemen");
        Integer id_jabatan = (Integer) parameters.get("id_jabatan");
        Integer id_lembur = (Integer) parameters.get("id_lembur");
        Character status = (Character) parameters.get("status");
        Date bulan = (Date) parameters.get("bulan");
        String id_pegawai = (String) parameters.get("id_pegawai");
        
        if (id_departemen != null ) {
            Query.append("  ");
        }
        
        if (id_pegawai != null ) {
            Query.append(" AND a.id_pegawai=#{id_pegawai} ");
        }
        
        Query.append(" ORDER by a.tanggal ");
        return Query.toString();
    }

    
}
