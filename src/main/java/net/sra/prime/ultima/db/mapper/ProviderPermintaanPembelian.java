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
public class ProviderPermintaanPembelian {

    public String SelectAll(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.nama as dibuat_nama, d.departemen, e.nama as kantor, f.nomor_po "
                + " FROM permintaan_pembelian a  "
                + " INNER JOIN pegawai b ON a.dibuat = b.id_pegawai "
                + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
                + " INNER JOIN hr_departemen d ON b.id_departemen_new=d.id_departemen "
                + " INNER JOIN internal_kantor_cabang e ON e.id_kantor_cabang=b.id_kantor_new "
                + " LEFT JOIN po f ON a.no_pp=f.no_pp ");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_pegawai = (String) parameters.get("id_pegawai");
        Boolean status = (Boolean) parameters.get("status");
        Character statusnya = (Character) parameters.get("statusnya");
        Integer id_jabatan = (Integer) parameters.get("id_jabatan");

        String where = " WHERE ";
        if (id_pegawai != null && !"".equals(id_pegawai)) {

            if (status == true) {
                // dapat melihat yg dibuat oleh pegawai login
                // dapat melihat PP yg dibuat pegawai login atau dia termasuk yg melalkukan persetujuan (setelah persetujuan dibawahnya approved)
                Query.append(where).append(" (a.dibuat=#{id_pegawai} "
                        + " OR (a.spv=#{id_pegawai} AND a.status_dibuat=true) "
                        + " OR (a.checked_name=#{id_pegawai} AND a.spv_status=true) "
                        + " OR (a.checked_name=#{id_pegawai} AND a.spv is null  AND a.status_dibuat=true) "
                        + " OR (approved2=#{id_pegawai} AND status_cheked=true) "
                        + " OR (approved2=#{id_pegawai} AND a.checked_name is null  AND a.spv_status=true) "
                        + " OR (approved2=#{id_pegawai} AND a.checked_name is null AND a.spv is null  AND a.status_dibuat=true) "
                        + " OR (disetujui=#{id_pegawai} AND status_approved2=true) "
                        + " OR (disetujui=#{id_pegawai} AND approved2 is null AND a.status_dibuat=true) "
                        + ")");

                if (statusnya == null) {
                    Query.append(" AND a.status_dibuat is null");
                } else if (statusnya.equals('T')) {
                    Query.append(" AND a.status_dibuat=true");
                } else if (statusnya.equals('F')) {
                    Query.append(" AND a.status_dibuat=false");
                }
                // dapat melihat semua PP yang telah disetujui Direktu
                Query.append(" OR status_approved=true");

            } else {
                // dapat melihat PP yg dibuat pegawai login atau dia termasuk yg melalkukan persetujuan (setelah persetujuan dibawahnya approved)
                Query.append(where).append(" (a.dibuat=#{id_pegawai} "
                        + " OR (a.spv=#{id_pegawai} AND a.status_dibuat=true) "
                        + " OR (a.checked_name=#{id_pegawai} AND a.spv_status=true) "
                        + " OR (a.checked_name=#{id_pegawai} AND a.spv is null  AND a.status_dibuat=true) "
                        + " OR (approved2=#{id_pegawai} AND status_cheked=true) "
                        + " OR (approved2=#{id_pegawai} AND a.checked_name is null  AND a.spv_status=true) "
                        + " OR (approved2=#{id_pegawai} AND a.checked_name is null AND a.spv is null  AND a.status_dibuat=true) "
                        + " OR (disetujui=#{id_pegawai} AND status_approved2=true) "
                        + " OR (disetujui=#{id_pegawai} AND approved2 is null AND a.status_dibuat=true) "
                        + ")");
                        

                // Direktur
                if (id_jabatan == 101) {
                    if (statusnya == null) {
                        Query.append(" AND a.status_approved is null");
                    } else if (statusnya.equals('T')) {
                        Query.append(" AND a.status_approved=true");
                    } else if (statusnya.equals('F')) {
                        Query.append(" AND a.status_approved=false");
                    }

                    // Finance & Accounting Manager
                } else if (id_jabatan == 108) {
                    if (statusnya == null) {
                        Query.append(" AND a.status_approved2 is null");
                    } else if (statusnya.equals('T')) {
                        Query.append(" AND a.status_approved2=true");
                    } else if (statusnya.equals('F')) {
                        Query.append(" AND a.status_approved2=false");
                    }

                    // Manager selain Finance & Accounting Manager
                } else if (id_jabatan == 110 || id_jabatan == 115 || id_jabatan == 110 || id_jabatan == 109 || id_jabatan == 104) {
                    if (statusnya == null) {
                        Query.append(" AND a.status_cheked is null");
                    } else if (statusnya.equals('T')) {
                        Query.append(" AND a.status_cheked=true");
                    } else if (statusnya.equals('F')) {
                        Query.append(" AND a.status_cheked=false");
                    }

                } else {
                    // untuk selain 
                    if (statusnya == null) {
                        Query.append(" AND (a.status_dibuat is null OR a.spv_status is null) ");
                    } else if (statusnya.equals('T')) {
                        Query.append(" AND a.status_dibuat=true");
                    } else if (statusnya.equals('F')) {
                        Query.append(" AND a.status_dibuat=false");
                    }
                }
                if (awal != null) {
                    Query.append(" AND a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
                }
            }
        } else {
            Query.append(where).append(" a.status_approved=true");

        }
        Query.append(
                " ORDER BY  a.tanggal DESC, a.no_pp DESC");
        return Query.toString();
    }

    public String SelectAllMaintenance(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.*, b.nama as dibuat_nama, d.departemen, e.nama as kantor, f.nomor_po "
                + " FROM permintaan_pembelian a  "
                + " INNER JOIN pegawai b ON a.dibuat = b.id_pegawai "
                + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
                + " INNER JOIN hr_departemen d ON b.id_departemen_new=d.id_departemen "
                + " INNER JOIN internal_kantor_cabang e ON e.id_kantor_cabang=b.id_kantor_new "
                + " LEFT JOIN po f ON a.no_pp=f.no_pp ");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");

        if (awal != null) {
            Query.append(" WHERE a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        Query.append(
                " ORDER BY  a.tanggal DESC, a.no_pp DESC");
        return Query.toString();
    }

}
