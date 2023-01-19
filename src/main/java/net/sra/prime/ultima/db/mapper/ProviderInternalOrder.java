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
public class ProviderInternalOrder {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang as gudang_asal, c.gudang as gudang_tujuan, d.nama as kantor,e.nama as nama_kontak,"
                + " f.no_intruksi_po as nomor_ip,f.tanggal as tanggal_ip,g.nomor as nomor_it, g.create_date as create_it, g.modified_date as approved_it  "
                + " FROM internal_order a "
                + " INNER JOIN gudang b ON a.id_gudang_asal=b.id_gudang "
                + " INNER JOIN gudang c ON a.id_gudang_tujuan=c.id_gudang "
                + " INNER JOIN internal_kantor_cabang d ON a.id_kantor=d.id_kantor_cabang "
                + " INNER JOIN pegawai e ON a.kontak=e.id_pegawai "
                + " LEFT JOIN intruksi_po f ON a.id_ip = f.id "
                + " LEFT JOIN internal_transfer g ON a.nomor_io=g.nomor_io ");

        String id_kantor = (String) parameters.get("id_kantor");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character status = (Character) parameters.get("status");

        String where = " WHERE ";
        // untuk marketing    
        if (id_kantor != null && !"".equals(id_kantor)) {
            Query.append(where).append(" (a.id_kantor like #{id_kantor} OR b.dsm=#{id_pegawai}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            }
            // untuk orang gudang    
        } else if (id_pegawai != null && !"".equals(id_pegawai)) {
            Query.append(where).append(" a.id_gudang_asal IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai}) ");
            if (awal != null) {
                Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            }
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            } else {
                Query.append(" AND (a.status='A' OR a.status='P' OR a.status='C') ");
            }
        } else if (awal != null) {
            Query.append(where).append(" a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
            if (status != null) {
                Query.append(" AND a.status=#{status} ");
            }
        } else if (status != null) {
            Query.append(where).append(" a.status=#{status} ");
        }

        Query.append("ORDER BY a.tanggal DESC,a.nomor_io DESC");
        System.out.println(Query);
        return Query.toString();
    }

    public String SelectAllIp(Map<String, Object> parameters) {

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang,c.no_forecast,f.nama as create_name,g.nama as modified_name "
                + " FROM intruksi_po a"
                + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang "
                + " LEFT JOIN pegawai f ON a.create_by=f.id_pegawai "
                + " LEFT JOIN pegawai g ON a.modified_by=g.id_pegawai "
                + " LEFT JOIN forecast c ON a.id_forecast=c.id "
                + " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai}) "
                + " AND a.status != 'D' AND a.status !='R' AND a.status!='X'");
        if (awal != null) {
            Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }

        Query.append(" ORDER BY a.tanggal DESC,no_intruksi_po DESC");
        return Query.toString();
    }

}
