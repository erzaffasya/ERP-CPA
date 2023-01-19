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
public class ProviderPo {

    /**
     * Staff bisa melihat po yang dibuatnya dan semua po yang sudah diapprove
     * oleh direktrur SPV dapat melihat po yang dibuatnya dan po yang sudah
     * dikirim oleh staff Direktur dapat melihat po yang sudah disetujui oleh sp
     */
    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT *, b.supplier as nama_supplier, c.gudang,d.no_intruksi_po, "
                + " (total - total_discount + total_ppn + total_pph) as grandtotal, e.no_forecast, p.nama as nama_purchasing  "
                + " FROM po a "
                + " INNER JOIN supplier b ON a.id_supplier=b.id "
                + " INNER JOIN pegawai p ON a.purchasing=p.id_pegawai "
                + " LEFT JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " LEFT JOIN intruksi_po d ON a.id_intruksi_po=d.id "
                + " LEFT JOIN forecast e ON e.id=d.id_forecast");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");
        Character jenis = (Character) parameters.get("jenis");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Integer id_jabatan = (Integer) parameters.get("id_jabatan");

        String where = " WHERE ";

        if (id_jabatan == 101) {
            Query.append(where).append("((is_checked1=true AND checked1 is not null ) OR (checked1 is null AND is_purchasing=true)) ");
            if (status != null && !"".equals(status)) {
                if (status.equals('C')) {
                    Query.append(" AND a.is_approve is null");
                } else if (status.equals('A')) {
                    Query.append(" AND a.is_approve = true");
                } else if (status.equals('R')) {
                    Query.append(" AND a.is_approve = false");
                }
                if (awal != null) {
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            } else if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (id_jabatan == 150) {

            if (status != null && !"".equals(status)) {
                if (status.equals('D')) {
                    Query.append(where).append(" purchasing=#{id_pegawai} AND (a.is_purchasing is null OR a.is_purchasing=false) ");
                } else if (status.equals('S')) {
                    Query.append(where).append(" is_purchasing=true AND checked1 is not null AND a.is_checked1 is null AND is_approve is null");
                } else if (status.equals('C')) {
                    Query.append(where).append("  a.is_checked1=true AND a.is_approve is null");
                } else if (status.equals('A')) {
                    Query.append(where).append("  a.is_approve = true");
                } else if (status.equals('R')) {
                    Query.append(where).append("  a.is_approve = false");
                }

                if (awal != null) {
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            } else {
                // spv dapat melihat po yang dibuatnya dan yang sudah disend oleh staff
                    Query.append(where).append(" (a.purchasing=#{id_pegawai} OR  is_purchasing=true) ");
                if (awal != null) {
                    
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            }
        } else if (jenis.equals('A')) {
            if (status != null && !"".equals(status)) {
                // pencarian berdasarkan status
                if (status.equals('D')) {
                    Query.append(where).append(" a.purchasing=#{id_pegawai} AND (a.is_purchasing is null OR a.is_purchasing=false) ");
                } else if (status.equals('S')) {
                    Query.append(where).append(" a.purchasing=#{id_pegawai} AND a.is_purchasing=true AND a.is_checked1 is null AND is_approve is null");
                } else if (status.equals('C')) {
                    Query.append(where).append(" a.is_checked1=true AND a.is_approve is null");
                } else if (status.equals('A')) {
                    Query.append(where).append(" a.is_approve = true");
                } else if (status.equals('R')) {
                    Query.append(where).append(" a.is_approve = false");
                }

                if (awal != null) {
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            } else {
                // user ini hanya dapat melihat po yang dia buat dan semua po yang sudah diapprve oleh direktur
                Query.append(where).append(" a.purchasing=#{id_pegawai} OR a.is_checked1=true OR a.is_approve is not null");
                if (awal != null) {
                    Query.append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            }
        } else if (jenis.equals('M')) {
            if (status != null && !"".equals(status)) {
                if (status.equals('D')) {
                    Query.append(where).append("(a.is_purchasing is null OR a.is_purchasing=false)");
                } else if (status.equals('S')) {
                    Query.append(where).append("a.is_purchasing=true AND a.is_checked1 is null");
                } else if (status.equals('C')) {
                    Query.append(where).append("a.is_checked1=true AND a.is_approve is null");
                } else if (status.equals('A')) {
                    Query.append(where).append("a.is_approve = true");
                } else if (status.equals('R')) {
                    Query.append(where).append("a.is_approve = false");
                }

                if (awal != null) {
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            } else if (awal != null) {
                Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        }
        Query.append(" ORDER BY a.tanggal DESC,a.nomor_po DESC ");
        return Query.toString();
    }

///////////////////////////////////////////////////////////////////////////
    public String SelectPoGudang(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT *, b.supplier as nama_supplier, c.gudang,no_intruksi_po,e.no_forecast "
                + " FROM po a "
                + " INNER JOIN supplier b ON a.id_supplier=b.id "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " LEFT JOIN intruksi_po d ON a.id_intruksi_po=d.id "
                + " LEFT JOIN forecast e ON d.id_forecast=e.id "
                + " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai=#{id_pegawai}) ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");

        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");

        }
        Query.append(" ORDER BY a.tanggal DESC, a.nomor_po DESC ");
        return Query.toString();
    }

    public String selectOutstanding(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, e.*, b.nama_barang, c.satuan_kecil, "
                + " d.satuan_besar, a.qty / b.isi_satuan as qtybesar, "
                + " a.diambil / b.isi_satuan as diambilbesar, "
                + " a.sisa / b.isi_satuan as sisabesar, f.gudang, g.supplier as nama_supplier "
                + " FROM po_detail a "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_kecil c ON b.id_satuan_kecil = c.id_satuan_kecil "
                + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
                + " INNER JOIN po e ON e.id=a.id "
                + " INNER JOIN gudang f ON e.id_gudang=f.id_gudang "
                + " INNER JOIN supplier g ON e.id_supplier = g.id "
                + " WHERE is_approve=true AND a.sisa > 0");

        String id_gudang = (String) parameters.get("id_gudang");
        String suppliernya = (String) parameters.get("suppliernya");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND e.id_gudang=#{id_gudang}");

        }
        if (suppliernya != null && !"".equals(suppliernya)) {
            if (suppliernya.equals("1")) {
                Query.append(" AND g.id = '010001' ");
            } else if (suppliernya.equals("2")) {
                Query.append(" AND g.id != '010001' ");
            }
        }
        Query.append(" ORDER BY e.tanggal DESC, e.nomor_po DESC");
        return Query.toString();
    }

    public String SelectAllPo(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.qty,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.nomor_po,p.tanggal,e.supplier as nama_supplier,a.qty / b.isi_satuan as qtybesar,"
                + " p.is_purchasing,p.is_approve"
                + " FROM po_detail a "
                + " INNER JOIN po p ON a.id=p.id "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " INNER JOIN supplier e ON p.id_supplier=e.id"
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
        }
        if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");

        }
        if (status != null && !"".equals(status)) {
            if (status.equals('D')) {
                Query.append("(AND p.is_purchasing is null OR p.is_purchasing=false)");
            } else if (status.equals('S')) {
                Query.append("AND p.is_purchasing=true AND p.is_approve is null");
            } else if (status.equals('A')) {
                Query.append("AND p.is_approve = true");
            } else if (status.equals('R')) {
                Query.append("AND p.is_approve = false");
            }
        }
        Query.append(" ORDER BY p.tanggal, p.nomor_po");

        return Query.toString();
    }

}
