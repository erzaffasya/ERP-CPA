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
package net.sra.prime.ultima.db.provider;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderPengguna {

    public String SelectAll(Map<String, Object> parameters) {

        Character status = (Character) parameters.get("status");
        StringBuilder Query = new StringBuilder("SELECT a.*, b.jabatan, c.nama as kantor, d.departemen "
            + " FROM pengguna a"
            + " INNER JOIN pegawai p ON a.id_pegawai=p.id_pegawai "
            + " LEFT JOIN master_jabatan b ON p.id_jabatan_new = b.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang c ON p.id_kantor_new=c.id_kantor_cabang "
            + " LEFT JOIN hr_departemen d ON p.id_departemen_new=d.id_departemen ");
          
        if (status.equals('1')) {
            Query.append(" WHERE a.enabled=true ");
        }else if (status.equals('0')) {
            Query.append(" WHERE a.enabled=false ");
        }
        Query.append("ORDER BY a.id_pegawai");

        return Query.toString();
    }

    
}
