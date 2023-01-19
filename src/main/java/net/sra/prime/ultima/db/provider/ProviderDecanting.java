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
public class ProviderDecanting {

    public String SelectAll(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang  FROM decanting a "
                + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
                + "  WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai})");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character status = (Character) parameters.get("status");

        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null) {
            Query.append(" AND status=#{status} ");
        }
        Query.append(" ORDER BY  a.tanggal DESC, a.no_decanting DESC");
        return Query.toString();
    }

    public String SelectAllDecanting(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.no_decanting,p.tanggal,p.status "
                + " FROM decanting_detail a "
                + " INNER JOIN decanting p ON a.no_decanting=p.no_decanting "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " WHERE a.id_barang=#{id_barang} AND a.asal='F'"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND p.status=#{status} ");
            } else {
                Query.append(" AND p.status != 'D' ");
            }
            if (gudang != null) {
                Query.append(" AND p.id_gudang=#{gudang} ");
            }
        } else if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");
            if (status != null) {
                Query.append(" AND p.status=#{status} ");
            } else {
               Query.append(" AND p.status != 'D' ");
            }
        } else if (status != null) {
            Query.append(" AND p.status=#{status} ");
        } else {
            Query.append(" AND p.status != 'D' ");
        }
        Query.append(" ORDER BY p.tanggal, p.no_decanting");
        return Query.toString();
    }

}
