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
public class ProviderPembayaranPiutang {

    public String SelectAll(Map<String, Object> parameters) {
        String SELECT = "SELECT a.*, b.customer FROM pembayaran_piutang a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak";
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
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY a.tanggal DESC,no_pembayaran_piutang DESC");
        return Query.toString();

    }

    public String PendapatanMarketing(Map<String, Object> parameters) {
        String SELECT = "SELECT a.*, b.customer, c.jumlah_bayar, c.dpp, c.ppn, c.no_invoice, e.nama as salesman, d.top, d.due_date, (a.tanggal - d.due_date) as umur_pembayaran  "
                + " FROM pembayaran_piutang a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN pembayaran_piutang_detail c ON a.no_pembayaran_piutang=c.nomor "
                + " INNER JOIN acc_ar_faktur d ON c.no_penjualan=d.ar_number "
                + " LEFT JOIN pegawai e ON e.id_pegawai=d.id_salesman "
                + "LEFT JOIN master_jabatan f ON e.id_jabatan=f.id_jabatan ";

        StringBuilder Query = new StringBuilder(SELECT);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_salesman = (String) parameters.get("id_salesman");
        String id_customer = (String) parameters.get("id_customer");
        Integer idDepartemen = (Integer) parameters.get("idDepartemen");
        
        String where = " WHERE a.status='A'  ";

        if (id_salesman != null && !"".equals(id_salesman)) {
            Query.append(where).append(" AND d.id_salesman=#{id_salesman}");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}  ");
            }
            if (id_customer != null) {
                Query.append(" AND a.id_customer=#{id_customer}  ");
            }
        } else if (idDepartemen > 0) {
            Query.append(where).append(" AND f.id_departemen = #{idDepartemen} ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}  ");
            }
            if (id_customer != null) {
                Query.append(" AND a.id_customer=#{id_customer}  ");
            }
        } else if (id_customer != null) {
            Query.append(where).append(" AND a.id_customer=#{id_customer}  ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}  ");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}  ");
        } else {
            Query.append(where);
        }
        Query.append(" ORDER BY a.tanggal DESC,no_pembayaran_piutang DESC");
        return Query.toString();

    }
}
