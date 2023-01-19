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
public class ProviderPenjualan {

    public static final String SELECTALL = "SELECT a.*, b.customer, c.gudang, d.nama as salesman"
            + " FROM penjualan a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " LEFT JOIN gudang c On a.id_gudang =c.id_gudang "
            + " LEFT JOIN pegawai d ON a.id_salesman=d.id_pegawai";

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder(SELECTALL);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");
        Character jenis = (Character) parameters.get("jenis");

        String where = " WHERE ";

        if (status != null && !"".equals(status)) {
            Query.append(where).append("(a.status = #{status}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (jenis != null && !"".equals(jenis)) {
            Query.append(where).append("(a.status = #{status}) ");
            if (awal != null) {
                Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY  a.tanggal DESC, a.no_penjualan DESC");
        return Query.toString();
    }

    public String SelectAllAdmin(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder(SELECTALL);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_kantor = (String) parameters.get("id_kantor");
        String where = " WHERE ";

        if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir} AND (a.status='A' OR a.status='P') ");
        } else {
            Query.append(where).append(" (a.status='A' OR a.status='P') ");

        }

        if (id_kantor.equals("02")) {
            Query.append(" AND (c.id_kantor=#{id_kantor} OR c.id_kantor='03') ");
        } else {
            Query.append(" AND c.id_kantor=#{id_kantor} ");
        }

        Query.append(" ORDER BY  a.tanggal DESC, a.no_penjualan DESC");
        return Query.toString();
    }

    public String SelectSalesReport(Map<String, Object> parameters) {
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String idKantor = (String) parameters.get("idKantor");
        String idGudang = (String) parameters.get("idGudang");

        StringBuilder Query = new StringBuilder("SELECT a.no_Penjualan,a.id_barang,a.qty,a.harga,a.total,a.diskonrp,a.diskonpersen,a.additional_charge, "
                + " e.customer, b.tanggal,f.nama_barang, g.satuan_besar,b.referensi, f.isi_satuan, a.qty * f.isi_satuan as qty_kecil, "
                + "array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_do, h.nama as salesman,b.faktur "
                + "FROM penjualan_detail a "
                + "INNER JOIN penjualan b ON a.no_penjualan=b.no_penjualan "
                + "INNER JOIN gudang c ON b.id_gudang=c.id_gudang "
                + "INNER JOIN internal_kantor_cabang d ON c.id_kantor=d.id_kantor_cabang "
                + "INNER JOIN customer e ON b.id_customer=e.id_kontak "
                + "INNER JOIN barang f ON a.id_barang=f.id_barang "
                + "INNER JOIN satuan_besar g ON f.id_satuan_besar=g.id_satuan_besar "
                + "LEFT JOIN pegawai h ON b.id_salesman=h.id_pegawai "
                + "WHERE (b.status='P' OR b.status='A') AND b.jenis is null ");

        if (idKantor != null && !"".equals(idKantor)) {
            Query.append(" AND d.id_kantor_cabang = #{idKantor} ");
            if (idGudang != null && !"".equals(idGudang)) {
                Query.append(" AND b.id_gudang = #{idGudang}");
            }
        }
        if (awal != null) {
            Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }

        //penjualan reguler
        Query.append(" UNION ");
        Query.append("SELECT a.no_Penjualan, '' as id_barang,a.qty,a.harga,a.total,a.diskonrp,a.diskonpersen,0 as additional_charge, "
                + " e.customer, b.tanggal,a.nm_barang as nama_barang, a.satuan satuan_besar,b.referensi, 0 as isi_satuan, 0 as qty_kecil, "
                + "array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_do, h.nama as salesman,b.faktur "
                + "FROM penjualan_detail_reguler a "
                + "INNER JOIN penjualan b ON a.no_penjualan=b.no_penjualan "
                + "INNER JOIN customer e ON b.id_customer=e.id_kontak "
                + "LEFT JOIN pegawai h ON b.id_salesman=h.id_pegawai "
                + "WHERE (b.status='P' OR b.status='A') AND b.jenis='R' ");
        
        if (awal != null) {
            Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        Query.append(" UNION ");
        Query.append("SELECT b.nomor as no_Penjualan,a.id_barang,a.qty,a.harga*-1 as harga,a.total * -1 as total,a.diskonrp,a.diskonpersen,a.additional_charge, "
                + " e.customer, b.tanggal,f.nama_barang, g.satuan_besar,b.no_po as referensi, f.isi_satuan, a.qty * f.isi_satuan as qty_kecil, "
                + " b.no_do, h.nama as salesman,b.faktur "
                + "FROM credit_note_detail a "
                + "INNER JOIN credit_note b ON a.id=b.id "
                + "INNER JOIN penjualan p ON b.no_invoice=p.no_penjualan "
                + "INNER JOIN gudang c ON b.id_gudang=c.id_gudang "
                + "INNER JOIN internal_kantor_cabang d ON c.id_kantor=d.id_kantor_cabang "
                + "INNER JOIN customer e ON b.id_customer=e.id_kontak "
                + "INNER JOIN barang f ON a.id_barang=f.id_barang "
                + "INNER JOIN satuan_besar g ON f.id_satuan_besar=g.id_satuan_besar "
                + "LEFT JOIN pegawai h ON b.id_salesman=h.id_pegawai "
                + "WHERE b.status='P' AND b.jenis is null ");
        if (idKantor != null && !"".equals(idKantor)) {
            Query.append(" AND d.id_kantor_cabang = #{idKantor} ");
            if (idGudang != null && !"".equals(idGudang)) {
                Query.append(" AND b.id_gudang = #{idGudang}");
            }
        }

        if (awal != null) {
            Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        // Credit note reguler
        Query.append(" UNION ");
        Query.append("SELECT b.nomor as no_Penjualan,'' as id_barang,a.qty,a.harga*-1 as harga,a.total * -1 as total,a.diskonrp,a.diskonpersen,0 as additional_charge, "
                + " e.customer, b.tanggal,a.nama_barang, a.satuan_besar,b.no_po as referensi, 0 as isi_satuan, 0 as qty_kecil, "
                + " b.no_do, h.nama as salesman,b.faktur "
                + "FROM credit_note_detail_reguler a "
                + "INNER JOIN credit_note b ON a.id=b.id "
                + "INNER JOIN penjualan p ON b.no_invoice=p.no_penjualan "
                + "INNER JOIN customer e ON b.id_customer=e.id_kontak "
                + "LEFT JOIN pegawai h ON b.id_salesman=h.id_pegawai "
                + "WHERE b.status='P' AND b.jenis='R' ");
        
        if (awal != null) {
            Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY  tanggal ,no_penjualan");
        return Query.toString();
    }

    public String SelectRekapPenjualan(Map<String, Object> parameters) {
        String SELECTSALESREPORT = "SELECT a.*,e.customer, b.tanggal,f.nama_barang, g.satuan_besar,b.referensi, h.satuan_kecil, a.qty * f.isi_satuan as qty_kecil, "
                + "a.harga + additional_charge - diskonrp as harga_asli, b.faktur, i.nama as salesman, "
                + "array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=a.no_penjualan),', ') as no_do "
                + "FROM penjualan_detail a "
                + "INNER JOIN penjualan b ON a.no_penjualan=b.no_penjualan "
                + "INNER JOIN gudang c ON b.id_gudang=c.id_gudang "
                + "INNER JOIN internal_kantor_cabang d ON c.id_kantor=d.id_kantor_cabang "
                + "INNER JOIN customer e ON b.id_customer=e.id_kontak "
                + "INNER JOIN barang f ON a.id_barang=f.id_barang "
                + "INNER JOIN satuan_besar g ON f.id_satuan_besar=g.id_satuan_besar "
                + "INNER JOIN satuan_kecil h ON f.id_satuan_kecil=h.id_satuan_kecil "
                + "LEFT JOIN pegawai i ON b.id_salesman=i.id_pegawai "
                + "LEFT JOIN master_jabatan j ON i.id_jabatan=j.id_jabatan ";

        StringBuilder Query = new StringBuilder(SELECTSALESREPORT);

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String idSales = (String) parameters.get("idSales");
        Integer idDepartemen = (Integer) parameters.get("idDepartemen");

        String where = " WHERE (b.status='P' OR b.status='A') ";

        if (idSales != null && !"".equals(idSales)) {
            Query.append(where).append(" AND b.id_salesman = #{idSales} ");
            if (awal != null) {
                Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
            }
        } else if (idDepartemen > 0) {
            Query.append(where).append(" AND j.id_departemen = #{idDepartemen} ");
            if (awal != null) {
                Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
            }
        } else if (awal != null) {

            Query.append(where).append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY  b.tanggal ,b.no_penjualan");
        return Query.toString();
    }

    public String SelectKomisiSales(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.no_penjualan as no_invoice, b.tanggal as tanggal_invoice, f.nama as salesman, "
                + "c.customer, a.qty, e.satuan_besar as satuan, d.isi_satuan, a.qty * d.isi_satuan as volume, "
                + "a.harga-a.diskonrp as harga, (a.harga-a.diskonrp) * a.qty as total, b.top, g.due_date, i.no_pembayaran_piutang as no_pembayaran_piutang, "
                + "i.tanggal as tanggal_or,  i.tanggal  - g.due_date as umur_pembayaran, j.nama_alias as nama_barang, pd.harga as bottomprice "
                + "FROM penjualan_detail a "
                + "INNER JOIN penjualan b ON a.no_penjualan=b.no_penjualan "
                + "INNER JOIN customer c ON b.id_customer=c.id_kontak "
                + "INNER JOIN barang d ON a.id_barang=d.id_barang "
                + "INNER JOIN satuan_besar e ON d.id_satuan_besar=e.id_satuan_besar "
                + "INNER JOIN pegawai f ON b.id_salesman=f.id_pegawai "
                + "INNER JOIN barang j ON j.id_barang=a.id_barang "
                + "LEFT JOIN acc_ar_faktur g ON b.no_penjualan=g.no_invoice "
                + "LEFT JOIN pembayaran_piutang_detail h ON h.no_penjualan=g.ar_number "
                + "LEFT JOIN pembayaran_piutang i ON i.no_pembayaran_piutang=h.nomor AND i.status='A' "
                + "LEFT JOIN pricelist_detail pd ON a.id_barang=pd.id_barang AND b.id_gudang=pd.id_gudang ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String idSales = (String) parameters.get("idSales");

        String where = " WHERE (b.status='P' OR b.status='A') ";

        if (idSales != null && !"".equals(idSales)) {
            Query.append(where).append(" AND b.id_salesman = #{idSales} ");
            if (awal != null) {
                Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY  b.tanggal ,b.no_penjualan");
        return Query.toString();
    }

    public String SelectKomisiSalesHpp(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.no_penjualan as no_invoice, b.tanggal as tanggal_invoice, f.nama as salesman, "
                + "c.customer, sum(a.qty)/d.isi_satuan as qty, e.satuan_besar as satuan, d.isi_satuan, sum(a.qty) as volume,k.due_date,  "
                + "b.top, j.nama_alias as nama_barang, a.hpp * d.isi_satuan as hpp, m.no_pembayaran_piutang as no_pembayaran_piutang, "
                + "m.tanggal as tanggal_or,  m.tanggal  - k.due_date as umur_pembayaran, k.bayar_dpp as total_bayar, k.amount, "
                + "(SELECT harga-diskonrp FROM penjualan_detail WHERE no_penjualan=a.no_penjualan AND id_barang=a.id_barang) as harga,  "
                + "(SELECT harga-diskonrp FROM penjualan_detail WHERE no_penjualan=a.no_penjualan AND id_barang=a.id_barang) * sum(a.qty)/d.isi_satuan as total, "
                + "(a.hpp * d.isi_satuan) + ((SELECT sum(COALESCE(bb.shippingcosts,0)) FROM penjualando aa INNER JOIN do_tbl bb ON aa.no_do=bb.nomor WHERE aa.no_penjualan=a.no_penjualan) / (SELECT SUM(qty) FROM penjualan_hpp WHERE no_penjualan=a.no_penjualan) * d.isi_satuan)  as bottomprice "
                + "FROM penjualan_hpp a "
                + "INNER JOIN penjualan b ON a.no_penjualan=b.no_penjualan "
                + "INNER JOIN customer c ON b.id_customer=c.id_kontak "
                + "INNER JOIN barang d ON a.id_barang=d.id_barang "
                + "INNER JOIN satuan_besar e ON d.id_satuan_besar=e.id_satuan_besar "
                + "INNER JOIN pegawai f ON b.id_salesman=f.id_pegawai "
                + "INNER JOIN barang j ON j.id_barang=a.id_barang "
                + "INNER JOIN acc_ar_faktur k ON a.no_penjualan=k.no_invoice "
                + "LEFT JOIN pembayaran_piutang_detail l ON l.no_penjualan=k.ar_number "
                + "LEFT JOIN pembayaran_piutang m ON m.no_pembayaran_piutang=l.nomor AND m.status='A' "
        );

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String idSales = (String) parameters.get("idSales");

        String where = " WHERE (b.status='P' OR b.status='A') ";

        if (idSales != null && !"".equals(idSales)) {
            Query.append(where).append(" AND b.id_salesman = #{idSales} ");
            if (awal != null) {
                Query.append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND b.tanggal >= #{awal} AND b.tanggal <= #{akhir}");
        }
        Query.append("GROUP BY a.id_barang,a.hpp,b.no_penjualan,a.no_penjualan,b.tanggal,f.nama,c.customer,e.satuan_besar,d.isi_satuan,b.top,j.nama_alias,k.due_date, "
                + "m.no_pembayaran_piutang, k.bayar_dpp,k.amount "
                + " ORDER BY  b.tanggal ,b.no_penjualan");
        return Query.toString();
    }

    public String SelectKomisiDsr(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.no_penjualan as no_invoice, a.tanggal as tanggal_invoice, c.nama as salesman, "
                + "b.customer, a.total  "
                + ", b.top, d.due_date, f.no_pembayaran_piutang as no_pembayaran_piutang, "
                + "f.tanggal as tanggal_or,  f.tanggal  - d.due_date as umur_pembayaran, h.value as hpp "
                + "FROM penjualan a "
                + "INNER JOIN customer b ON a.id_customer=b.id_kontak "
                + "INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
                + "LEFT JOIN acc_ar_faktur d ON a.no_penjualan=d.no_invoice "
                + "LEFT JOIN pembayaran_piutang_detail e ON e.no_penjualan=d.ar_number "
                + "LEFT JOIN pembayaran_piutang f ON f.no_pembayaran_piutang=e.nomor AND f.status='A' "
                + "LEFT JOIN  acc_gl_trans g ON a.no_penjualan=g.reference "
                + "INNER JOIN  acc_gl_detail h ON g.gl_number=h.gl_number "
                + "INNER JOIN account i ON h.id_account=i.id_account AND i.account like 'HPP%'");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String idSales = (String) parameters.get("idSales");

        String where = " WHERE (a.status='P') ";

        if (idSales != null && !"".equals(idSales)) {
            Query.append(where).append(" AND a.id_salesman = #{idSales} ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY  a.tanggal ,a.no_penjualan");
        return Query.toString();
    }

}
