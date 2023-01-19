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
public class ProviderInternalTransfer {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang as gudang_asal, c.gudang as gudang_tujuan  "
            + " FROM internal_transfer a "
            + " INNER JOIN gudang b ON a.id_gudang_asal=b.id_gudang "
            + " INNER JOIN gudang c ON a.id_gudang_tujuan=c.id_gudang ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai_asal = (String) parameters.get("id_pegawai_asal");
        String id_pegawai_tujuan = (String) parameters.get("id_pegawai_tujuan");
        Character status = (Character) parameters.get("status");
        Character jenis = (Character) parameters.get("jenis");

        String where = " WHERE ";

        if (id_pegawai_asal != null && !"".equals(id_pegawai_asal)) {
            Query.append(where).append("(a.id_gudang_asal IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai_asal})) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
            if (status != null) {
                if (status.equals('C')) {
                    Query.append(" AND (a.status = 'C' OR a.status='X') ");
                } else {
                    Query.append(" AND a.status = #{status}");
                }
            } else if (jenis.equals('I')) {
                Query.append(" AND a.status != 'D'");
            }
        } else if (id_pegawai_tujuan != null && !"".equals(id_pegawai_tujuan)) {
            Query.append(where).append(" a.id_gudang_tujuan IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai_tujuan}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
            if (status != null) {
                if (status.equals('C')) {
                    Query.append(" AND (a.status = 'C' OR a.status='X') ");
                } else {
                    Query.append(" AND a.status = #{status}");
                }
            } else if (jenis.equals('I')) {
                Query.append(" AND a.status != 'D'");
            }
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND a.status = #{status}");
            } else if (jenis.equals('I')) {
                Query.append(" AND a.status != 'D'");
            }
        } else if (status != null) {
            if (status.equals('C')) {
                    Query.append(where).append(" (a.status = 'C' OR a.status='X') ");
                } else {
                    Query.append(where).append("  a.status = #{status}");
                }
        } else if (jenis.equals('I')) {
            Query.append(where).append(" a.status != 'D'");
        }
        Query.append(" ORDER BY  a.tanggal DESC, a.nomor DESC");
        return Query.toString();
    }
    
    public String SelectAllInternalTransfer(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,a.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.nomor,p.tanggal,p.status,e.gudang as gudang_tujuan,p.nomor_io  "
                + " FROM internal_transfer_detail a "
                + " INNER JOIN internal_transfer p ON a.nomor=p.nomor "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " INNER JOIN gudang e ON p.id_gudang_tujuan=e.id_gudang"
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
            if (status != null) {
                if (status.equals('T')) {
                    Query.append(" AND (p.status='S' OR p.status='R') ");
                } else if (status.equals('C')){
                    Query.append(" AND (p.status='C' OR p.status='X') ");
                } else {
                    Query.append(" AND p.status=#{status} ");
                }
            } else {
                Query.append(" AND p.status != 'D' ");
            }
            if (gudang != null) {
                Query.append(" AND p.id_gudang_asal=#{gudang} ");
            }
        } else if (gudang != null) {
            Query.append(" AND p.id_gudang_asal=#{gudang} ");
            if (status != null) {
                if (status.equals('T')) {
                    Query.append(" AND (p.status='S' OR p.status='R') ");
                } else if (status.equals('C')){
                    Query.append(" AND (p.status='C' OR p.status='X') ");
                } else {
                    Query.append(" AND p.status=#{status} ");
                }
            } else {
                Query.append(" AND p.status != 'D' ");
            }
        } else if (status != null) {
            if (status.equals('T')) {
                    Query.append(" AND (p.status='S' OR p.status='R') ");
                } else if (status.equals('C')){
                    Query.append(" AND (p.status='C' OR p.status='X') ");
                } else {
                    Query.append(" AND p.status=#{status} ");
                }
        } else {
            Query.append(" AND p.status != 'D' ");
        }

        Query.append(" ORDER BY p.tanggal, p.nomor");

        return Query.toString();
    }


    public String SelectAllInternalTransferReceipt(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.terima,a.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.nomor,p.tanggal,p.status,e.gudang as gudang_asal,p.nomor_io  "
                + " FROM internal_transfer_detail a "
                + " INNER JOIN internal_transfer p ON a.nomor=p.nomor "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " INNER JOIN gudang e ON p.id_gudang_asal=e.id_gudang"
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
            if (status != null) {
                    Query.append(" AND p.status=#{status} ");
            } else {
                Query.append(" AND (p.status = 'R' OR p.status = 'X') ");
            }
            if (gudang != null) {
                Query.append(" AND p.id_gudang_tujuan=#{gudang} ");
            }
        } else if (gudang != null) {
            Query.append(" AND p.id_gudang_tujuan=#{gudang} ");
            if (status != null) {
                    Query.append(" AND p.status=#{status} ");
                
            } else {
                Query.append(" AND (p.status = 'R' OR p.status = 'X') ");
            }
        } else if (status != null) {
                    Query.append(" AND p.status=#{status} ");
        } else {
            Query.append(" AND (p.status = 'R' OR p.status = 'X') ");
        }

        Query.append(" ORDER BY p.tanggal, p.nomor");

        return Query.toString();
    }


}
