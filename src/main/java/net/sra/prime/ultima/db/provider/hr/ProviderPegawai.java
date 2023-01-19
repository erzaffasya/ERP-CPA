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
public class ProviderPegawai {
    
    public String selectAllOnlyPegawai(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*,b.jabatan FROM pegawai a "
                + " INNER JOIN master_jabatan b ON a.id_jabatan_new = b.id_jabatan "
                + " WHERE a.status=true");
        Integer id_departemen = (Integer) parameters.get("id_departemen");
        String id_kantor = (String ) parameters.get("id_kantor");
        
        
        if (id_kantor != null && !id_kantor.equals("")) {
            Query.append(" AND a.id_kantor_new = #{id_kantor} ");
        }
        
        if(id_departemen != null){
            Query.append(" AND a.id_departemen_new = #{id_departemen} ");
        }
        
        Query.append(" ORDER by a.nip ");
        return Query.toString();
    }

    
}
