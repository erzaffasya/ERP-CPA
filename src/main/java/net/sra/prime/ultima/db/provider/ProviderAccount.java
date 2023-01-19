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

import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderAccount {

    public String selectLabaRugi(Map<String, Object> parameters) {

        String tipe = (String) parameters.get("tipe");
        String tahun = (String) parameters.get("tahun");
        Integer awal = (Integer) parameters.get("awal");
        Integer akhir = (Integer) parameters.get("akhir");
        Integer mulai = (Integer) parameters.get("mulai");
        Integer panjang = (Integer) parameters.get("panjang");

       String db = "(db" + awal;
        String cr = "(cr" + awal;
        for (int i = awal + 1; i <= akhir; i++) {
            db = db + " + db" + i;
            cr = cr + " + cr" + i;
        }
        db = db + ")";
        cr = cr + ")";
        String hitung = db + "-" + cr;

        String QUERYNYA = "SELECT a.*," + hitung + " as debit"
                + " FROM account a "
                + " LEFT JOIN acc_value c ON a.id_account = c.account AND c.years=#{tahun} "
                + " WHERE SUBSTR(cast(a.id_account as text), #{mulai},#{panjang})=#{tipe}  AND a.level != 1 "
                + " ORDER BY a.id_account";
        StringBuilder Query = new StringBuilder(QUERYNYA);
        return Query.toString();
    }

    public String selectBukuBesar(Map<String, Object> parameters) {
        Integer accountfrom = (Integer) parameters.get("accountfrom");
        Integer accountto = (Integer) parameters.get("accountto");
        String keterangan = (String) parameters.get("keterangan");
        StringBuilder Query = new StringBuilder("SELECT * FROM account ");

        if (accountfrom != null) {
            Query.append(" WHERE id_account >= #{accountfrom} AND id_account <= #{accountto} ");
            if (keterangan != null && !keterangan.equals("")) {
                Query.append(" AND LOWER(account) like '%' || LOWER(#{keterangan}) || '%' ");
            }
        } else if (keterangan != null && !keterangan.equals("")) {
            Query.append(" WHERE LOWER(account) like '%' || LOWER(#{keterangan}) || '%' ");
        }
        Query.append(" ORDER BY id_account ");
        return Query.toString();
    }

}
