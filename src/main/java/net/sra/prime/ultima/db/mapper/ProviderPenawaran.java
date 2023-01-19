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
public class ProviderPenawaran {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,  c.nama as salesman, d.nama as createby"
                + " FROM penawaran a  "
                + " LEFT JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
                + " INNER JOIN pegawai d ON a.kode_user=d.id_pegawai "
                + " INNER JOIN gudang e ON a.id_gudang=e.id_gudang");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character statuspenawaran = (Character) parameters.get("statuspenawaran");
        String id_kantor = (String) parameters.get("id_kantor");

        String where = " WHERE ";

        if (id_pegawai != null && !"".equals(id_pegawai)) {
            if (id_kantor != null && !"".equals(id_kantor)) {
                Query.append(where).append("e.id_kantor=#{id_kantor} ");
            } else {
                Query.append(where).append("(a.id_salesman=#{id_pegawai} OR a.kode_user=#{id_pegawai} OR (a.dsm=#{id_pegawai} AND a.statussend!='D'))");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }

        if (statuspenawaran != null && !"".equals(statuspenawaran)) {
            Query.append(" AND a.statussend=#{statuspenawaran} ");
        }
        Query.append(" ORDER BY  a.tanggal DESC, a.nomor DESC");
        return Query.toString();
    }

    public String SelectAllMaintenance(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,  c.nama as salesman, d.nama as createby"
                + " FROM penawaran a  "
                + " LEFT JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
                + " INNER JOIN pegawai d ON a.kode_user=d.id_pegawai "
                + " INNER JOIN gudang e ON a.id_gudang=e.id_gudang");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character statuspenawaran = (Character) parameters.get("statuspenawaran");

        String where = " WHERE ";

        if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (statuspenawaran != null && !"".equals(statuspenawaran)) {
                Query.append(" AND a.statussend=#{statuspenawaran} ");
            }
        } else if (statuspenawaran != null && !"".equals(statuspenawaran)) {
            Query.append(where).append(" a.statussend=#{statuspenawaran} ");
        }

        Query.append(" ORDER BY  a.tanggal DESC, a.nomor DESC");
        return Query.toString();
    }

    public String SelectAllPenawaranInvoice(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.nomor,a.tanggal,a.revisi, g.customer, h.nama, c.id_barang, "
                + "c.nama_barang, b.qty,d.nomor as id_lama,e.qty as jml_order,h.nama as salesman, "
                + "(SELECT aa.no_penjualan "
                + "FROM penjualan aa "
                + "INNER JOIN penjualando ab ON aa.no_penjualan=ab.no_penjualan "
                + "INNER JOIN penjualan_detail ac ON aa.no_penjualan=ac.no_penjualan "
                + "INNER JOIN do_tbl ad ON  ab.no_do=ad.nomor "
                + "INNER JOIN packinglist  ae ON ad.no_pl=ae.nomor AND ae.no_so=d.nomor "
                + "WHERE b.id_barang=ac.id_barang LIMIT 1) as no_invoice,"
                + "(SELECT ac.qty "
                + "FROM penjualan aa "
                + "INNER JOIN penjualando ab ON aa.no_penjualan=ab.no_penjualan "
                + "INNER JOIN penjualan_detail ac ON aa.no_penjualan=ac.no_penjualan "
                + "INNER JOIN do_tbl ad ON  ab.no_do=ad.nomor "
                + "INNER JOIN packinglist  ae ON ad.no_pl=ae.nomor AND ae.no_so=d.nomor "
                + "WHERE b.id_barang=ac.id_barang LIMIT 1) as jml_invoice, "
                + "e.qty- COALESCE((SELECT ac.qty "
                + "FROM penjualan aa "
                + "INNER JOIN penjualando ab ON aa.no_penjualan=ab.no_penjualan "
                + "INNER JOIN penjualan_detail ac ON aa.no_penjualan=ac.no_penjualan "
                + "INNER JOIN do_tbl ad ON  ab.no_do=ad.nomor "
                + "INNER JOIN packinglist  ae ON ad.no_pl=ae.nomor AND ae.no_so=d.nomor "
                + "WHERE b.id_barang=ac.id_barang LIMIT 1),0) as selisih "
                + "FROM penawaran a "
                + "INNER JOIN penawaran_detail b ON a.nomor=b.no_penawaran AND a.revisi=b.revisi "
                + "INNER JOIN barang c ON b.id_barang=c.id_barang "
                + "LEFT JOIN so d ON a.nomor=d.no_penawaran "
                + "LEFT JOIN so_detail e ON d.nomor=e.no_so AND e.id_barang=b.id_barang "
                + "INNER JOIN customer g ON a.id_customer=g.id_kontak "
                + "INNER JOIN pegawai h ON a.id_salesman=h.id_pegawai "
        );

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        String id_kantor = (String) parameters.get("id_kantor");

        String where = "WHERE (a.statussend='A' OR a.statussend='P') "
                + "AND a.revisi=(SELECT max(penawaran.revisi)  FROM penawaran WHERE penawaran.nomor=a.nomor) ";

        if (id_pegawai != null && !"".equals(id_pegawai)) {
//            if (id_kantor != null && !"".equals(id_kantor)) {
//                Query.append(where).append(" AND i.id_kantor=#{id_kantor} ");
//            } else {
            Query.append(where).append(" AND (a.id_salesman=#{id_pegawai} OR a.dsm=#{id_pegawai} )");
//            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }

        Query.append(" ORDER BY  a.tanggal , a.nomor ,d.nomor, b.urut");
        return Query.toString();
    }

}
