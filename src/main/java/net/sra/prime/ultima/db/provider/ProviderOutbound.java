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
public class ProviderOutbound {

    public String SelectAll(Map<String, Object> parameters) {

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_barang = (String) parameters.get("id_barang");

        StringBuilder Query = new StringBuilder("SELECT a.tanggal,c.id_barang, c.nama_barang,f.referensi as po,a.nomor as no_do,g.customer as tujuan,b.qty,b.qty * c.isi_satuan as qty_kecil, "
                + "d.gudang, a.status as statusDo,a.tanggal as tanggal_kirim,a.received_date, (a.received_date::date - a.tanggal::date) AS days,a.leadtime "
                + "FROM do_tbl a "
                + "INNER JOIN do_detail b ON a.nomor=b.no_do "
                + "INNER JOIN barang c ON b.id_barang=c.id_barang "
                + "INNER JOIN gudang d ON a.id_gudang=d.id_gudang "
                + "INNER JOIN packinglist e ON a.no_pl=e.nomor "
                + "INNER JOIN so f ON f.nomor=e.no_so "
                + "INNER JOIN customer g ON a.id_customer=g.id_kontak "
                + "WHERE (a.status='S' OR a.status='R' OR a.status='I' OR a.status='C') ");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang=#{id_gudang} ");

        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir} ");
        }
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        Query.append(" UNION "
                + " SELECT a.tanggal,c.id_barang, c.nama_barang,a.nomor_io as po,a.nomor as no_do,e.gudang as tujuan,b.qty,b.qty * c.isi_satuan as qty_kecil, d.gudang,a.status as statusDo, "
                + " a.tanggal_kirim, a.tanggal_terima as received_date,(a.tanggal_terima::date - a.tanggal_kirim::date) AS days,0 AS leadtime "
                + "FROM internal_transfer a "
                + "INNER JOIN internal_transfer_detail b ON a.nomor=b.nomor "
                + "INNER JOIN barang c ON b.id_barang=c.id_barang "
                + "INNER JOIN gudang d ON d.id_gudang=a.id_gudang_asal "
                + "INNER JOIN gudang e ON a.id_gudang_tujuan=e.id_gudang "
                + "WHERE (a. status='A' OR a.status='R' OR a.status='C') ");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang_asal=#{id_gudang}");

        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        
        Query.append(" ORDER BY  tanggal ASC");
        return Query.toString();
    }

    public String SelectAllIn(Map<String, Object> parameters) {

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_barang = (String) parameters.get("id_barang");

        StringBuilder Query = new StringBuilder("SELECT a.tgl_penerimaan as tanggal,c.id_barang, c.nama_barang,a.nomor_po as po,a.referensi as no_do,"
                + "g.supplier as tujuan,b.qty as qty_kecil,b.qty / c.isi_satuan as qty, "
                + "d.gudang, a.no_penerimaan as numbernya,'P' as jenis,a.status as statuspenerimaan,'T' as statusit "
                + "FROM penerimaan_gudang a "
                + "INNER JOIN penerimaan_gudang_detail b ON a.no_penerimaan=b.no_penerimaan "
                + "INNER JOIN barang c ON b.id_barang=c.id_barang "
                + "INNER JOIN gudang d ON a.id_gudang=d.id_gudang "
                + "INNER JOIN po e ON a.nomor_po=e.nomor_po "
                + "INNER JOIN supplier g ON a.id_supplier=g.id "
                + "WHERE (a.status=true OR a.status is null)");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang=#{id_gudang}");

        }
        if (awal != null) {
            Query.append(" AND  a.tgl_penerimaan >= #{awal} AND a.tgl_penerimaan <= #{akhir} ");
        }
        
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        Query.append(" UNION "
                + "SELECT a.tanggal,c.id_barang, c.nama_barang,a.nomor_io as po,'' as no_do, "
                + "d.gudang as tujuan,b.terima * c.isi_satuan ,b.terima , e.gudang,a.nomor as numbernya,'I' as jenis,false as statuspenerimaan,a.status as statusit  "
                + "FROM internal_transfer a "
                + "INNER JOIN internal_transfer_detail b ON a.nomor=b.nomor "
                + "INNER JOIN barang c ON b.id_barang=c.id_barang "
                + "INNER JOIN gudang d ON d.id_gudang=a.id_gudang_asal "
                + "INNER JOIN gudang e ON a.id_gudang_tujuan=e.id_gudang "
                + "WHERE (a.status='R' or a.status='X') ");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang_tujuan=#{id_gudang}");

        }
        if (awal != null) {
            Query.append(" AND  a.tanggal_terima >= #{awal} AND a.tanggal_terima <= #{akhir}");
        }
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        Query.append(" ORDER BY  tanggal ASC");
        return Query.toString();
    }

    public String SelectAllXls(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.tanggal,c.id_barang, c.nama_barang,f.referensi as po,a.nomor as no_do,g.customer as tujuan,b.qty,b.qty * c.isi_satuan as qty_kecil, "
                + " d.gudang, a.transporter, a.lic_plate, a.driver, a.received_date, (a.received_date::date - a.tanggal::date) AS days, a.received_name, a.return_date, a.alamat,b.packing_number,a.leadtime, "
                + " a.status AS statusDo"
                + " FROM do_tbl a "
                + " INNER JOIN do_detail b ON a.nomor=b.no_do "
                + " INNER JOIN barang c ON b.id_barang=c.id_barang "
                + " INNER JOIN gudang d ON a.id_gudang=d.id_gudang "
                + " INNER JOIN packinglist e ON a.no_pl=e.nomor "
                + " INNER JOIN so f ON f.nomor=e.no_so "
                + " INNER JOIN customer g ON a.id_customer=g.id_kontak "
                + " WHERE (a.status='S' OR a.status='R' OR a.status='I' OR a.status='C')");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_barang = (String) parameters.get("id_barang");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang=#{id_gudang}");

        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir} ");
        }
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        Query.append(" UNION "
                + " SELECT a.tanggal,c.id_barang, c.nama_barang,a.nomor_io as po,a.nomor as no_do,e.gudang as tujuan,b.qty,b.qty * c.isi_satuan as qty_kecil, d.gudang, "
                + " a.sipb as transporter, '' as lic_plate, a.driver, a.tanggal_terima as received_date, (a.tanggal_terima::date - a.tanggal_kirim::date) AS days, "
                + " '' as received_name, a.tanggal as return_date,e.alamat,'' as packing_number,0 as leadtime,a.status AS statusDo "
                + " FROM internal_transfer a "
                + " INNER JOIN internal_transfer_detail b ON a.nomor=b.nomor "
                + " INNER JOIN barang c ON b.id_barang=c.id_barang "
                + " INNER JOIN gudang d ON d.id_gudang=a.id_gudang_asal "
                + " INNER JOIN gudang e ON a.id_gudang_tujuan=e.id_gudang "
                + " WHERE (a.status='A' OR a.status='R' OR a.status='C') ");

        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang_asal=#{id_gudang}");

        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        if(id_barang != null){
            Query.append(" AND b.id_barang=#{id_barang} ");
        }
        Query.append(" ORDER BY  tanggal ASC");
        // System.out.println(Query);
        return Query.toString();
    }

}
