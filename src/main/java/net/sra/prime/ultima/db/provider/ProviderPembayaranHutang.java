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
public class ProviderPembayaranHutang {

    public static final String SELECT = "SELECT a.*, b.supplier FROM pembayaran_hutang a  "
            + " INNER JOIN supplier b ON a.id_supplier = b.id";

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder(SELECT);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");

        String where = " WHERE ";

        if (status != null && !"".equals(status)) {
            Query.append(where).append(" a.status=#{status}");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null)  {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY a.tanggal DESC,no_pembayaran_hutang DESC");
        return Query.toString();

    }
}
