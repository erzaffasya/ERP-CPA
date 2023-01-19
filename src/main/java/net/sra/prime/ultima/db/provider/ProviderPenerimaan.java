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
public class ProviderPenerimaan {

    public static final String SELECTALL = "SELECT *, b.supplier as nama_supplier, (total - total_discount + total_ppn) as grandtotal  "
            + " FROM penerimaan a "
            + " INNER JOIN supplier b ON a.id_supplier=b.id ";
            
            
    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder(SELECTALL);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
            
        String where = " WHERE ";
        if (awal != null){
                Query.append(where).append(" a.tgl_penerimaan >= #{awal} AND a.tgl_penerimaan <= #{akhir}");
        }
        Query.append(" ORDER BY  a.tgl_penerimaan DESC, a.no_penerimaan DESC");
        return Query.toString();
    }
    
    
    
    
}
