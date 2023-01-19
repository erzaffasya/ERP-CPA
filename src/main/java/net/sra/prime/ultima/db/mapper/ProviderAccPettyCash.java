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
package net.sra.prime.ultima.db.mapper;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderAccPettyCash {

    public static final String SELECT = "SELECT a.*, b.nama as kantor FROM acc_petty_cash a  "
            + " INNER JOIN internal_kantor_cabang b ON a.id_kantor = b.id_kantor_cabang ";
    
            
    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder(SELECT);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_kantor = (String) parameters.get("id_kantor");
        Character st = (Character) parameters.get("st");
            
        String where = " WHERE ";
        
        if (id_kantor != null && !"".equals(id_kantor)) {
            Query.append(where).append(" a.id_kantor='").append(id_kantor).append("'");
            if (awal != null)
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }else if (awal != null){
                Query.append("  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            
        }
        Query.append(" ORDER BY a.nomor, a.tanggal, a.id DESC");
        return Query.toString();
    }

}
