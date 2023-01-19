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
public class ProviderIntruksiPo {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang,c.no_forecast,f.nama as create_name,g.nama as modified_name "
                + " FROM intruksi_po a"
                + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
                + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
                + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
                + " LEFT JOIN forecast c ON a.id_forecast=c.id ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");
        Character jenis = (Character) parameters.get("jenis");

        String where = " WHERE ";
        if (jenis.equals('A')) {
            if (status != null && !"".equals(status)) {
                Query.append(where).append(" a.status=#{status}");
            } else {
                Query.append(where).append(" a.status='S' OR a.status='C' OR a.status='A'  OR a.status='R'");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (jenis.equals('W')) {
            if (status != null && !"".equals(status)) {
                Query.append(where).append(" a.status=#{status}");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY a.tanggal DESC,no_intruksi_po DESC");
        return Query.toString();
    }
    
    public String SelectAllReport(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.no_intruksi_po,p.tanggal,p.status,f.nama as create_name,a.qty / b.isi_satuan as qtybesar "
                + " FROM intruksi_po_detail a "
                + " INNER JOIN intruksi_po p ON a.id=p.id "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " LEFT JOIN pegawai f ON p.create_by=f.id_pegawai "
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
        }
        if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");
        }
        if (status != null) {
                    Query.append(" AND p.status=#{status} ");
            }

        Query.append(" ORDER BY p.tanggal, p.no_intruksi_po");

        return Query.toString();
    }

    
}
