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
public class ProviderDo {

    public String SelectAll(Map<String, Object> parameters) {
        
        String id_pegawai = (String) parameters.get("id_pegawai");
        Character status = (Character) parameters.get("status");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character statusdo = (Character) parameters.get("statusdo");
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,c.gudang,d.nama as create_name,e.modified_date as tgl_pl,g.tanggal as tgl_invoice "
                + " FROM do_tbl a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN packinglist e ON a.no_pl=e.nomor "
                + " LEFT JOIN pegawai d ON a.id_pegawai_log = d.id_pegawai "
                + " LEFT JOIN penjualando f ON a.nomor=f.no_do "
                + " LEFT JOIN penjualan g ON g.no_penjualan=f.no_penjualan "
                + " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai}) ");

        

        if (status != null) {
            if (statusdo != null) {
                Query.append(" AND  a.status=#{statusdo}");
            } else {
                Query.append(" AND a.status!='D'");
            }
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }

        } else if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (statusdo != null) {
                Query.append("  AND a.status=#{statusdo}");
            }
        }else if (statusdo != null) {
           Query.append(" AND a.status=#{statusdo}");
            
        }
        Query.append(" ORDER BY a.tanggal DESC,nomor DESC");
        return Query.toString();
    }
    
    public String SelectAllXls(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,c.gudang, e.id_barang,e.nama_barang,f.satuan_besar,d.packing_number, "
                + " d.qty,e.isi_satuan * d.qty as isi_satuan,g.nama as salesman,h.nama as received_posting_name,(a.received_date::date - a.tanggal::date) AS days, j.referensi as po"
                + " FROM do_tbl a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN do_detail d ON a.nomor=d.no_do "
                + " INNER JOIN barang e ON d.id_barang=e.id_barang "
                + " INNER JOIN satuan_besar f ON e.id_satuan_besar=f.id_satuan_besar "
                + " INNER JOIN packinglist i ON a.no_pl=i.nomor " 
                + " INNER JOIN so j ON j.nomor=i.no_so "
                + " LEFT JOIN pegawai g ON a.id_salesman=g.id_pegawai "
                + " LEFT JOIN pegawai h ON a.received_posting = h.id_pegawai "
                + " WHERE "
                + " a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai}) ");

        String id_pegawai = (String) parameters.get("id_pegawai");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");
        String id_gudang = (String) parameters.get("id_gudang");
        if (status != null) {
            Query.append(" AND a.status=#{status} ");
        }else{
            Query.append(" AND a.status!='D' ");
        }
        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        if (id_gudang != null && !id_gudang.equals("")) {
            Query.append(" AND a.id_gudang = #{id_gudang}");
        }
        
        Query.append(" ORDER BY a.tanggal DESC,nomor DESC, d.urut ASC");
        return Query.toString();
    }
    
    public String SelectAllReport(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,c.gudang, e.id_barang,e.nama_barang,f.satuan_besar,d.packing_number, "
                + " d.qty,e.isi_satuan * d.qty as isi_satuan,g.nama as salesman,h.nama as received_posting_name,(a.received_date::date - a.tanggal::date) AS days, j.referensi as po"
                + " FROM do_tbl a  "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang "
                + " INNER JOIN do_detail d ON a.nomor=d.no_do "
                + " INNER JOIN barang e ON d.id_barang=e.id_barang "
                + " INNER JOIN satuan_besar f ON e.id_satuan_besar=f.id_satuan_besar "
                + " INNER JOIN packinglist i ON a.no_pl=i.nomor " 
                + " INNER JOIN so j ON j.nomor=i.no_so "
                + " LEFT JOIN pegawai g ON a.id_salesman=g.id_pegawai "
                + " LEFT JOIN pegawai h ON a.received_posting = h.id_pegawai "
                + " WHERE "
                + " a.tanggal >= #{awal} AND a.tanggal <= #{akhir} "
                 );

        String id_pegawai = (String) parameters.get("id_pegawai");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_salesman = (String) parameters.get("id_salesman");
        String id_customer = (String) parameters.get("id_customer");
        String id_barang = (String) parameters.get("id_barang");
        if(id_barang != null && !id_barang.equals("")){
            Query.append(" AND e.id_barang=#{id_barang} ");
        }
        if (status != null) {
            Query.append(" AND a.status=#{status} ");
        }else{
            Query.append(" AND a.status!='D' ");
        }
        if (id_gudang != null && !id_gudang.equals("")) {
            Query.append(" AND a.id_gudang = #{id_gudang}");
        } //else { 
            //Query.append(" AND a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai}) ");
            
        //}
        if(id_customer != null){
            Query.append(" AND b.id_kontak=#{id_customer} ");
        }
        if (id_salesman != null && !id_salesman.equals("")){
            Query.append(" AND a.id_salesman=#{id_salesman} ");
        }
        
        Query.append(" ORDER BY a.tanggal DESC,nomor DESC, d.urut ASC");
        return Query.toString();
    }
    
    
    public String SelectAllOsDo(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.nomor,a.tanggal,d.nomor as no_so,d.referensi as po, g.gudang, e.nama_barang, b.qty,f.satuan_besar,a.status,h.customer " 
            + " FROM do_tbl a " 
            + " INNER JOIN do_detail b ON a.nomor=b.no_do " 
            + " INNER JOIN packinglist c ON a.no_pl=c.nomor " 
            + " INNER JOIN so d ON c.no_so=d.nomor " 
            + " INNER JOIN barang e ON b.id_barang=e.id_barang " 
            + " INNER JOIN satuan_besar f ON e.id_satuan_besar=f.id_satuan_besar" 
            + " INNER JOIN gudang g ON a.id_gudang=g.id_gudang "
            + " INNER JOIN customer h ON a.id_customer=h.id_kontak " 
            + " WHERE a.status != 'D' "
            + " AND a.status !='C' "
            + " AND a.nomor NOT IN (SELECT pd.no_do FROM penjualan p INNER JOIN  penjualando pd ON p.no_penjualan=pd.no_penjualan WHERE p.tanggal < #{akhir} AND p.status!='C') "
            );

        Date akhir = (Date) parameters.get("akhir");
        String id_kantor = (String) parameters.get("id_kantor");
        if (id_kantor != null && !"".equals(id_kantor)) {
            Query.append(" AND a.id_gudang IN (SELECT ikc.id_gudang FROM gudang ikc WHERE ikc.id_kantor=#{id_kantor})");
            
        } 
        Query.append(" ORDER by a.id_gudang,a.tanggal,b.urut");
        return Query.toString();
    }

    
    public String SelectAllMaintenance(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer,c.gudang FROM do_tbl a  "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN gudang c ON a.id_gudang=c.id_gudang ");
        
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character statusdo = (Character) parameters.get("statusdo");
        
        
         if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (statusdo != null) {
                Query.append("  AND a.status=#{statusdo}");
            }
        }else if (statusdo != null) {
           Query.append(" WHERE a.status=#{statusdo}");
            
        }
        Query.append(" ORDER BY a.tanggal DESC,nomor DESC");
        return Query.toString();
    }
    
}
