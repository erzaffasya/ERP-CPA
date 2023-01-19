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
public class ProviderPackinglist {

    public String SelectAll(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang,d.modified_date as tanggal_so,e.nomor as nomor_do,e.modified_date as tanggal_do "
                + " FROM packinglist a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN so d ON d.nomor = a.no_so "
                + " LEFT JOIN do_tbl e ON a.nomor=e.no_pl "
                + " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai})");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character statusIp = (Character) parameters.get("statusIp");

        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (statusIp != null && !"".equals(statusIp)) {
                Query.append(" AND a.status=#{statusIp} ");
            }
        } else if (statusIp != null && !"".equals(statusIp)) {
            Query.append(" AND a.status=#{statusIp} ");
        }
        Query.append(" ORDER BY a.nomor, a.tanggal DESC");

        return Query.toString();
    }

    public String SelectAllPackinglist(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.nomor,p.tanggal,p.status,e.customer,p.no_so "
                + " FROM packinglist_detail a "
                + " INNER JOIN packinglist p ON a.no_pl=p.nomor "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " INNER JOIN customer e ON p.id_customer=e.id_kontak"
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND p.status=#{status} ");
            } else {
                Query.append(" AND p.status !='D' ");
            }
            if (gudang != null) {
                Query.append(" AND p.id_gudang=#{gudang} ");
            }
        } else if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");
            if (status != null) {
                Query.append(" AND p.status=#{status} ");
            } else {
                Query.append(" AND p.status !='D' ");
            }
        } else if (status != null) {
            Query.append(" AND p.status=#{status} ");
        } else {
            Query.append(" AND p.status !='D' ");
        }

        Query.append(" ORDER BY p.tanggal, p.nomor");

        return Query.toString();
    }

}