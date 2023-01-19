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
public class ProviderSo {

    
    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user "
            + " FROM so a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character status = (Character) parameters.get("status");

        
        if (id_pegawai != null && !"".equals(id_pegawai)) {
            Query.append(" WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai})");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            } 
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null && !"".equals(status)) {
                Query.append(" WHERE a.status=#{status}");
        }
        Query.append(" ORDER BY a.tanggal DESC,a.nomor DESC");
        return Query.toString();
    }
    
    public String SelectAllGudang(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user,e.nomor as no_pl, e.create_date as create_pl, e.modified_date as send_pl "
            + " FROM so a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
            + " LEFT JOIN packinglist e ON a.nomor=e.no_so ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character status = (Character) parameters.get("status");

        
        if (id_pegawai != null && !"".equals(id_pegawai)) {
            Query.append(" WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai})");
            if (status != null && !"".equals(status) && !status.equals('D')) {
                Query.append(" AND a.status=#{status} ");
            } else {
                Query.append(" AND (a.status ='S' OR a.status='C' OR a.status='P' OR a.status='W') ");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null && !"".equals(status) && !status.equals('D')) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null && !"".equals(status) && !status.equals('D')) {
                Query.append(" WHERE a.status=#{status}");
        }
        Query.append(" ORDER BY a.nomor DESC, a.tanggal DESC");
        return Query.toString();
    }
    
    public String SelectAllDetail(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.nomor,a.id_customer,a.referensi,a.tgl_ref,a.tanggal,a.no_penawaran,a.status, "
                + " b.customer, c.gudang, d.nama as user, "
                + " f.nama_barang,g.satuan_besar,e.harga,e.qty,e.total,e.id_barang,h.nama as salesman  "
                + " FROM so a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN so_detail e ON a.nomor=e.no_so "
                + " INNER join barang f ON e.id_barang=f.id_barang "
                + " INNER JOIN satuan_besar g ON f.id_satuan_besar=g.id_satuan_besar "
                + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
                + " LEFT JOIN pegawai h ON a.id_salesman=h.id_pegawai "
                );

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character status = (Character) parameters.get("status");

        
        if (id_pegawai != null && !"".equals(id_pegawai)) {
            Query.append(" WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai})");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            } else {
                Query.append(" AND (a.status ='S' OR a.status='C' OR a.status='P') ");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null && !"".equals(status)) {
                Query.append(" WHERE a.status=#{status}");
        }
        Query.append(" ORDER BY a.nomor DESC, a.tanggal DESC,e.urut ASC");
        return Query.toString();
    }
    
    public String SelectAllPengajuan(Map<String, Object> parameters) {
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user "
            + " FROM so a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " INNER JOIN so_persetujuan e ON a.nomor=e.nomor   "
            + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
            + " WHERE "
                + "(e.urut=1 AND e.approve is not null AND e.id_pegawai=#{id_pegawai}) "
                + " OR ((e.urut=2 OR e.urut=3) AND (SELECT COUNT(sp.*) FROM so_persetujuan sp WHERE sp.nomor=a.nomor AND (sp.urut=2 OR sp.urut=3) AND sp.approve is not null) > 0 AND e.id_pegawai=#{id_pegawai})");

        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        } 
        Query.append(" ORDER BY a.nomor DESC, a.tanggal DESC");
        return Query.toString();
    }

    
    public String SelectMonitoringSo(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user, "
                + " e.nomor as no_pl, f.nomor as no_do, h.no_penjualan,f.received_date,p.nama as salesman "
                + " FROM so a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " LEFT JOIN pegawai p ON a.id_salesman=p.id_pegawai"
                + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
                + " LEFT JOIN packinglist e ON a.nomor=e.no_so AND e.status !='D' AND e.status !='C' "
                + " LEFT JOIN do_tbl f ON e.nomor=f.no_pl AND f.status != 'D' AND f.status != 'C' "
                + " LEFT JOIN penjualando g ON f.nomor=g.no_do "
                + " LEFT JOIN penjualan h ON g.no_penjualan=h.no_penjualan AND h.status != 'D' AND h.status !='C' ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        Character status = (Character) parameters.get("status");

        String where = " WHERE a.status !='D' AND a.status != 'C' ";

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(where).append("( AND a.id_gudang=#{id_gudang} OR c.parent=#{id_gudang}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            }
        } 
        Query.append(" ORDER BY a.tanggal DESC,a.nomor DESC");
        return Query.toString();
    }
    
    
    public String SelectSoReport(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.nomor, a.id_customer, a.tanggal, g.id_barang, g.qty, "
                + " g.no_do "
                + " FROM so a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN packinglist e ON a.nomor=e.no_so AND e.status !='D' AND e.status !='C' "
                + " INNER JOIN do_tbl f ON e.nomor=f.no_pl AND f.status != 'D' AND f.status != 'C' "
                + " INNER JOIN do_detail g ON f.nomor=g.no_do"
                );

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        Character status = (Character) parameters.get("status");

        String where = " WHERE a.status !='D' AND a.status != 'C' ";

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(where).append("( AND a.id_gudang=#{id_gudang} OR c.parent=#{id_gudang}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        } 
        Query.append(" ORDER BY a.nomor DESC, a.tanggal DESC");
        return Query.toString();
    }

    public String SelectAllConsignment(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user "
            + " FROM so a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
            + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_kantor = (String) parameters.get("id_kantor");

        String where = " WHERE a.jenis='c' ";

        if (id_kantor != null && !"".equals(id_kantor)) {
            Query.append(where).append("AND c.id_kantor=#{id_kantor} ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
        } else if (awal != null) {
            Query.append(where).append("AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(" ORDER BY a.nomor, a.tanggal DESC");
        return Query.toString();
    }
    
    
    
    public String OutstandingSo(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, c.gudang, d.nama as user, "
                + " e.nomor as no_pl, f.nomor as no_do, h.no_penjualan,f.received_date,p.nama as salesman, f.tanggal as tgl_do, "
                + " br.nama_barang,br.isi_satuan,sb.satuan_besar,sk.satuan_kecil,sd.id_barang,sd.qty,sd.diambil,sd.sisa , sd.qty * br.isi_satuan as qty_kecil,sd.total "
                + " FROM so_detail sd  "
                + " INNER JOIN barang br ON sd.id_barang=br.id_barang "
                + " INNER JOIN satuan_besar sb ON sb.id_satuan_besar=br.id_satuan_besar "
                + " INNER JOIN satuan_kecil sk ON sk.id_satuan_kecil=br.id_satuan_kecil "
                + " INNER JOIN SO a ON a.nomor=sd.no_so"
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " LEFT JOIN pegawai p ON a.id_salesman=p.id_pegawai"
                + " LEFT JOIN pegawai d ON a.kode_user=d.id_pegawai "
                + " LEFT JOIN packinglist e ON a.nomor=e.no_so AND e.status !='D' AND e.status !='C' "
                + " LEFT JOIN do_tbl f ON e.nomor=f.no_pl AND f.status != 'D' AND f.status != 'C' "
                + " LEFT JOIN penjualando g ON f.nomor=g.no_do "
                + " LEFT JOIN penjualan h ON g.no_penjualan=h.no_penjualan AND h.status != 'D' AND h.status !='C' "
                + " WHERE a.status !='D' AND a.status != 'C' AND sd.sisa > 0");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_customer = (String) parameters.get("id_customer");

        

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append("AND a.id_gudang=#{id_gudang} ");
            
        } 
        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        if (id_customer != null) {
            Query.append("AND a.id_customer=#{id_customer}");
        }
        
        Query.append(" ORDER BY a.tanggal DESC,a.nomor DESC");
        return Query.toString();
    }

}
