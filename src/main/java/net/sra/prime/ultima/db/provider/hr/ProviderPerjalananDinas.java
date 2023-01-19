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
public class ProviderPerjalananDinas {
    
    public String selectPerjalananDinas(Map<String, Object> parameters) {
       Integer id_departemen = (Integer) parameters.get("id_departemen");
        Integer id_jabatan = (Integer) parameters.get("id_jabatan");
        Integer id_perjalanan_dinas = (Integer) parameters.get("id_perjalanan_dinas");
        Character status = (Character) parameters.get("status");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        
        StringBuilder Query = new StringBuilder("SELECT a.*, c.nama as nama_pegawai,c.nip as nik ,e.departemen,f.jabatan "
            + " FROM hr.perjalanan_dinas a "
            + " INNER JOIN pegawai c ON a.id_pegawai=c.id_pegawai "
            + " INNER JOIN hr_departemen e ON c.id_departemen_new = e.id_departemen "
            + " INNER JOIN master_jabatan f ON c.id_jabatan_new = f.id_jabatan "
            + " WHERE  a.tanggal_awal >= #{awal} AND a.tanggal_awal <= #{akhir}"
            );

        
        
        if (id_departemen != null ) {
            Query.append(" AND c.id_departemen_new = #{id_departemen}");
        }
        if (id_jabatan != null ) {
            Query.append(" AND c.id_jabatan_new = #{id_jabatan}");
        }
        
        
        Query.append(" ORDER by a.tanggal_awal ");
        return Query.toString();
    }

    
}
