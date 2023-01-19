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
public class ProviderPenerimaanGudang {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.supplier as nama_supplier, c.gudang,f.nama as create_name,g.nama as modified_name "
                + " FROM penerimaan_gudang a "
                + " INNER JOIN supplier b ON a.id_supplier=b.id "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
                + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
                + " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai})");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Boolean status = (Boolean) parameters.get("status");
        Boolean st = (Boolean) parameters.get("st");
        if (awal != null) {
            Query.append(" AND a.tgl_penerimaan >= #{awal} AND a.tgl_penerimaan <= #{akhir}");
            if (st) {
                if (status != null) {
                    Query.append(" AND a.status=#{status} ");
                } else {
                    Query.append(" AND a.status is null ");
                }
            }
        } else if (st) {
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            } else {
                Query.append(" AND a.status is null ");
            }
        }
        Query.append(" ORDER BY  a.tgl_penerimaan DESC, a.no_penerimaan DESC");
        return Query.toString();
    }

    public String SelectAllPenerimaanGudang(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.no_penerimaan,p.tgl_penerimaan,p.status,e.supplier as nama_supplier,p.nomor_po,p.referensi,p.tanggal_referensi "
                + " FROM penerimaan_gudang_detail a "
                + " INNER JOIN penerimaan_gudang p ON a.no_penerimaan=p.no_penerimaan "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " INNER JOIN supplier e ON p.id_supplier=e.id"
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tgl_penerimaan >= #{awal} AND p.tgl_penerimaan <= #{akhir}");
        }
        if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");
            if (status != null) {
                if (status.equals('A')) {
                    Query.append(" AND p.status=true ");
                } else {
                    Query.append(" AND p.status is null ");
                }
            } else {
                Query.append(" AND p.status != false ");
            }
        }
        if (status != null) {
            if (status.equals('A')) {
                Query.append(" AND p.status=true ");
            } else {
                Query.append(" AND p.status is null ");
            }
        } else {
            Query.append(" AND p.status != false ");
        }

        Query.append(" ORDER BY p.tgl_penerimaan, p.no_penerimaan");

        return Query.toString();
    }

}
