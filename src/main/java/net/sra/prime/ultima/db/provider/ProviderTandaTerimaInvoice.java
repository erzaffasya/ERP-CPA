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
public class ProviderTandaTerimaInvoice {

    public String SelectAll(Map<String, Object> parameters) {

        
        StringBuilder Query = new StringBuilder("SELECT a.*, b.customer, d.nama as create_name, "
            + " array_to_string(array(SELECT i.no_penjualan FROM tanda_terima_invoice_detail i WHERE i.id=a.id),', ') as nomor_invoice "
            + " FROM tanda_terima_invoice a "
            + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
            + " INNER JOIN pegawai d ON a.create_by = d.id_pegawai ");
            //+ " ORDER BY a.tanggal DESC ,nomor DESC")
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");

        if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null) {
            Query.append(" WHERE a.status=#{status} ");
        }
        Query.append(" ORDER BY a.nomor, a.tanggal DESC");

        return Query.toString();
    }

    public String SelectAllJurnal(Map<String, Object> parameters) {
        
        StringBuilder Query = new StringBuilder("SELECT a.*,  b.gudang "
                + " FROM pemakaian a  "
                + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");

        String where = " WHERE ";

        if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            } else {
                Query.append(" AND (a.status='A' OR a.status='P') ");
            }
        } else {
            if (status != null) {
                Query.append(where).append(" a.status=#{status} ");
            } else {
                Query.append(" AND (a.status='A' OR a.status='P') ");
            }
        }
        Query.append(" ORDER BY  a.tanggal DESC, a.nomor DESC");
        return Query.toString();
    }
    
    
    public String SelectAllDetail(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*,c.no_penjualan, b.customer,  "
                + " e.grandtotal as nilai, e.faktur as nomor_faktur,e.referensi as no_po, "
                + " array_to_string(array(SELECT i.no_do FROM penjualando i WHERE i.no_penjualan=c.no_penjualan),', ') as no_do"
                + " FROM tanda_terima_invoice a "
                + " INNER JOIN customer b ON a.id_customer = b.id_kontak "
                + " INNER JOIN pegawai d ON a.create_by = d.id_pegawai "
                + " INNER JOIN tanda_terima_invoice_detail c ON a.id=c.id "
                + " INNER JOIN penjualan e ON e.no_penjualan = c.no_penjualan");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");

        
        if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null && !"".equals(status)) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null && !"".equals(status)) {
                Query.append(" WHERE a.status=#{status}");
        }
        Query.append(" ORDER BY  a.tanggal DESC,a.nomor DESC,c.urut ASC");
        return Query.toString();
    }

}
