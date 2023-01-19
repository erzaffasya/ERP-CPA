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
public class ProviderStokBarang {

    public String selectStok(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil , c.kategori_barang, sum(a.stok) as stokkecil, b.isi_satuan "
                + " FROM stok_barang a "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN kategori_barang c ON c.id_kategori_barang = b.id_kategori_barang "
                + " INNER JOIN satuan_besar d ON b.id_satuan_besar = d.id_satuan_besar "
                + " INNER JOIN satuan_kecil e ON b.id_satuan_kecil = e.id_satuan_kecil "
                + " INNER JOIN gudang f ON a.id_gudang = f.id_gudang "
                + " WHERE a.id_gudang = #{id_gudang} ");

        String id_gudang = (String) parameters.get("id_gudang");
        String id_satuan = (String) parameters.get("id_satuan");
        String id_kategori = (String) parameters.get("id_kategori");

        if (id_satuan != null && !"".equals(id_satuan)) {
            Query.append(" AND b.id_satuan_besar=#{id_satuan} ");
        }
        if (id_kategori != null && !"".equals(id_kategori)) {
            Query.append(" AND c.id_kategori_barang=#{id_kategori} ");
        }
        Query.append(" GROUP BY a.id_barang, b.nama_barang, d.satuan_besar, e.satuan_kecil,  c.kategori_barang, b.isi_satuan "
                + " ORDER BY b.nama_barang ");
        return Query.toString();
    }

    public String UpdateStokValue(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("UPDATE stok_value  SET ");

        Integer nomor = (Integer) parameters.get("nomor");
        Double nilai = (Double) parameters.get("nilai");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_barang = (String) parameters.get("id_barang");
        String years = (String) parameters.get("years");
        Character status = (Character) parameters.get("status");
        if (status.equals('d')) {
            switch (nomor) {
                case 1:
                    Query.append("db1=db1 + #{nilai}");
                    break;
                case 2:
                    Query.append("db2=db2 + #{nilai}");
                    break;
                case 3:
                    Query.append("db3=db3 + #{nilai}");
                    break;
                case 4:
                    Query.append("db4=db4 + #{nilai}");
                    break;
                case 5:
                    Query.append("db5=db5 + #{nilai}");
                    break;
                case 6:
                    Query.append("db6=db6 + #{nilai}");
                    break;
                case 7:
                    Query.append("db7=db7 + #{nilai}");
                    break;
                case 8:
                    Query.append("db8=db8 + #{nilai}");
                    break;
                case 9:
                    Query.append("db9=db9 + #{nilai}");
                    break;
                case 10:
                    Query.append("db10=db10 + #{nilai}");
                    break;
                case 11:
                    Query.append("db11=db11 + #{nilai}");
                    break;
                case 12:
                    Query.append("db12=db12 + #{nilai}");
                    break;
            }
        } else if (status.equals('c')) {
            switch (nomor) {
                case 1:
                    Query.append("cr1=cr1 +#{nilai}");
                    break;
                case 2:
                    Query.append("cr2=cr2 +#{nilai}");
                    break;
                case 3:
                    Query.append("cr3=cr3 +#{nilai}");
                    break;
                case 4:
                    Query.append("cr4=cr4 +#{nilai}");
                    break;
                case 5:
                    Query.append("cr5=cr5 +#{nilai}");
                    break;
                case 6:
                    Query.append("cr6=cr6 +#{nilai}");
                    break;
                case 7:
                    Query.append("cr7=cr7 +#{nilai}");
                    break;
                case 8:
                    Query.append("cr8=cr8 +#{nilai}");
                    break;
                case 9:
                    Query.append("cr9=cr9 +#{nilai}");
                    break;
                case 10:
                    Query.append("cr10=cr10 +#{nilai}");
                    break;
                case 11:
                    Query.append("cr11=cr11 +#{nilai}");
                    break;
                case 12:
                    Query.append("cr12=cr12 +#{nilai}");
                    break;
            }
        }
        Query.append(" WHERE id_gudang = #{id_gudang} AND id_barang = #{id_barang} AND years = #{years} ");
        return Query.toString();
    }

    public String selectKartuStok(Map<String, Object> parameters) {

        Integer nomor = (Integer) parameters.get("nomor");
        String id_gudang = (String) parameters.get("id_gudang");
        String id_barang = (String) parameters.get("id_barang");
        String years = (String) parameters.get("years");
        Integer tahun = (Integer) parameters.get("tahun");
        Date tglmulai = (Date) parameters.get("tglmulai");
        String saldoawal = "";

        switch (nomor) {
            case 1:
                saldoawal = "(db0)-(cr0)";
                break;
            case 2:
                saldoawal = "(db0+db1)-(cr0+cr1)";
                break;
            case 3:
                saldoawal = "(db0+db1+db2)-(cr0+cr1+cr2)";
                break;
            case 4:
                saldoawal = "(db0+db1+db2+db3)-(cr0+cr1+cr2+cr3)";
                break;
            case 5:
                saldoawal = "(db0+db1+db2+db3+db4)-(cr0+cr1+cr2+cr3+cr4)";
                break;
            case 6:
                saldoawal = "(db0+db1+db2+db3+db4+db5)-(cr0+cr1+cr2+cr3+cr4+cr5)";
                break;
            case 7:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6)";
                break;
            case 8:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7)";
                break;
            case 9:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8)";
                break;
            case 10:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9)";
                break;
            case 11:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10)";
                break;
            case 12:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11)";
                break;

        }
        String QUERYNYA = "SELECT 'Saldo Awal' as judul,'' as nomor,(" + saldoawal + ") / c.isi_satuan as debit,0 as kredit,#{tglmulai} as tanggal, 'a' as kode, 'Saldo Awal' as dari "
                + "FROM stok_value a "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "WHERE a.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND a.years=#{years} "
                + "UNION "
                + "SELECT 'Penerimaan' as judul,a.no_penerimaan as nomor,sum(a.qty / c.isi_satuan) as debit,0 as kredit, b.tgl_penerimaan as tanggal, 'b' as kode, d.supplier as dari "
                + "FROM penerimaan_gudang_detail a "
                + "INNER JOIN penerimaan_gudang b ON a.no_penerimaan=b.no_penerimaan "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "INNER JOIN supplier d ON b.id_supplier=d.id "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM b.tgl_penerimaan)=#{nomor}  AND EXTRACT(year FROM b.tgl_penerimaan)=#{tahun} AND (b.status = true OR b.status is null)"
                + "GROUP BY judul,nomor,kredit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Receipt IT' as judul, b.nomor,sum(terima) as debit,0 as kredit, b.tanggal_terima as tanggal, 'c' as kode, c.gudang as dari "
                + "FROM internal_transfer_detail a "
                + "INNER JOIN internal_transfer b ON a.nomor=b.nomor "
                + "INNER JOIN gudang c ON b.id_gudang_asal=c.id_gudang "
                + "WHERE b.id_gudang_tujuan=#{id_gudang} AND a.id_barang=#{id_barang}  AND EXTRACT(month FROM tanggal_terima)=#{nomor}  AND EXTRACT(year FROM tanggal_terima)=#{tahun}  AND (b.status='R' OR b.status='X') "
                + "GROUP BY judul,b.nomor,kredit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Cancel IT' as judul, c.nomor ,sum(qty) as debit,0 as kredit, c.tanggal as tanggal_cancel, 'd' as kode, a.nomor as dari "
                + "FROM internal_transfer_detail a "
                + "INNER JOIN internal_transfer b ON a.nomor=b.nomor "
                + " INNER JOIN internaltransfer_cancel c ON b.nomor=c.no_it "
                + "WHERE b.id_gudang_asal=#{id_gudang} AND a.id_barang=#{id_barang}  AND EXTRACT(month FROM c.tanggal)=#{nomor}  AND EXTRACT(year FROM c.tanggal)=#{tahun} AND  (b.status='C' OR b.status='X') "
                + "GROUP BY judul,c.nomor,kredit,tanggal_cancel,kode,dari "
                + "UNION "
                + "SELECT 'DECANTING' as judul,b.no_decanting as nomor,sum(a.qty/c.isi_satuan),0 as kredit,b.tanggal as tanggal, 'e' as kode, '' as dari "
                + "FROM decanting_detail a "
                + "INNER JOIN decanting b ON a.no_decanting=b.no_decanting "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND asal='T'  AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND b.status='A' "
                + "GROUP BY judul,nomor,kredit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Picking List Cancel' as judul, d.nomor,sum(a.qty) as debit, 0 as kredit, d.tanggal , 'f' as kode, b.nomor as dari "
                + "FROM packinglist_detail a "
                + "INNER JOIN packinglist b ON a.no_pl=b.nomor "
                + "INNER JOIN customer c ON b.id_customer=c.id_kontak "
                + "INNER JOIN packinglist_cancel d ON a.no_pl = d.no_pl "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM d.tanggal)=#{nomor}  AND EXTRACT(year FROM d.tanggal)=#{tahun} AND b.status ='C' "
                + "GROUP BY judul,d.nomor,kredit,d.tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Penambahan Barang' as judul, b.nomor,sum(a.qty/c.isi_satuan) as debit, 0 as kredit, b.tanggal as tanggal, 'f2' as kode, b.keterangan as dari "
                + "FROM penambahan_detail a "
                + "INNER JOIN penambahan b ON a.id=b.id "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND b.status !='D' AND b.status is not null "
                + "GROUP BY judul,b.nomor,kredit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'IT' as judul, b.nomor ,0 as debit,sum(qty) as kredit, b.tanggal as tanggal, 'g' as kode, c.gudang as dari "
                + "FROM internal_transfer_detail a "
                + "INNER JOIN internal_transfer b ON a.nomor=b.nomor "
                + "INNER JOIN gudang c ON b.id_gudang_tujuan=c.id_gudang "
                + "WHERE b.id_gudang_asal=#{id_gudang} AND a.id_barang=#{id_barang}  AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND (b.status='S' OR b.status='R' OR b.status='X' OR b.status='C') "
                + "GROUP BY judul,b.nomor,debit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Cancel Pemakaian Barang' as judul, c.nomor,sum(a.qty/d.isi_satuan) as debit, 0 as kredit, c.tanggal , 'h' as kode, b.nomor as dari "
                + "FROM pemakaian_detail a "
                + "INNER JOIN pemakaian  b ON a.id=b.id "
                + "INNER JOIN pemakaian_cancel c ON b.nomor = c.no_pemakaian "
                + "INNER JOIN barang d ON a.id_barang=d.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM c.tanggal)=#{nomor}  AND EXTRACT(year FROM c.tanggal)=#{tahun} AND b.status ='C' "
                + "GROUP BY judul,c.nomor,kredit,c.tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Stok Opname' as judul, b.nomor,sum(a.selisih/d.isi_satuan) as debit, 0 as kredit, b.tanggal , 'i' as kode, a.notes as dari "
                + "FROM stok_opname_detil a "
                + "INNER JOIN stok_opname  b ON a.id=b.id "
                + "INNER JOIN barang d ON a.id_barang=d.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} "
                + "AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} "
                + "AND b.status ='A' AND a.selisih > 0"
                + "GROUP BY judul,b.nomor,kredit,b.tanggal,kode,dari "
                + "UNION "
                + "SELECT 'DECANTING' as judul,b.no_decanting as nomor,0 as debit,sum(a.qty/c.isi_satuan) as kredit,b.tanggal as tanggal, 'i' as kode, ' ' as dari "
                + "FROM decanting_detail a "
                + "INNER JOIN decanting b ON a.no_decanting=b.no_decanting "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND asal='F'  AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND b.status='A' "
                + "GROUP BY judul,nomor,debit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Pemakaian Barang' as judul, b.nomor,0 as debit, sum(a.qty/c.isi_satuan) as kredit, b.tanggal as tanggal, 'j' as kode, b.keterangan as dari "
                + "FROM pemakaian_detail a "
                + "INNER JOIN pemakaian b ON a.id=b.id "
                + "INNER JOIN barang c ON a.id_barang=c.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND b.status !='D' AND b.status is not null "
                + "GROUP BY judul,b.nomor,debit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Cancel IT' as judul, c.nomor,0 as debit,sum(terima) as kredit, c.tanggal as tanggal_cancel, 'k' as kode, c.no_it as dari "
                + "FROM internal_transfer_detail a "
                + "INNER JOIN internal_transfer b ON a.nomor=b.nomor "
                + "INNER JOIN internaltransfer_cancel c ON b.nomor=c.no_it "
                + "WHERE b.id_gudang_tujuan=#{id_gudang} AND a.id_barang=#{id_barang}  AND EXTRACT(month FROM c.tanggal)=#{nomor}  AND EXTRACT(year FROM c.tanggal)=#{tahun}  AND b.status='X' "
                + "GROUP BY judul,c.nomor,debit,tanggal_cancel,kode,dari "
                + "UNION "
                + "SELECT 'Picking List' as judul, b.nomor,0 as debit, sum(a.qty) as kredit, b.tanggal as tanggal, 'l' as kode, c.customer as dari "
                + "FROM packinglist_detail a "
                + "INNER JOIN packinglist b ON a.no_pl=b.nomor "
                + "INNER JOIN customer c ON b.id_customer=c.id_kontak "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} AND b.status !='D'  AND b.status is not null "
                + "GROUP BY judul,b.nomor,debit,tanggal,kode,dari "
                + "UNION "
                + "SELECT 'Cancel Penambahan Barang' as judul, c.nomor,0 as debit, sum(a.qty/d.isi_satuan) as kredit, c.tanggal as tanggal_cancel, 'm' as kode, c.pesan as dari "
                + "FROM penambahan_detail a "
                + "INNER JOIN penambahan b ON a.id=b.id "
                + "INNER JOIN penambahan_cancel as c ON b.nomor=c.no_penambahan "
                + "INNER JOIN barang d ON d.id_barang=a.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM c.tanggal)=#{nomor}  AND EXTRACT(year FROM c.tanggal)=#{tahun} AND b.status ='C'  "
                + "GROUP BY judul,c.nomor,debit,tanggal_cancel,kode,dari "
                + "UNION "
                + "SELECT 'Cancel Perimaan Barang' as judul, c.nomor,0 as debit, sum(a.qty/d.isi_satuan) as kredit, c.tanggal as tanggal_cancel, 'n' as kode, c.pesan as dari "
                + "FROM penerimaan_gudang_detail a "
                + "INNER JOIN penerimaan_gudang b ON a.no_penerimaan=b.no_penerimaan "
                + "INNER JOIN penerimaan_gudang_cancel as c ON b.no_penerimaan=c.no_penerimaan "
                + "INNER JOIN barang d ON d.id_barang=a.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} AND EXTRACT(month FROM c.tanggal)=#{nomor}  AND EXTRACT(year FROM c.tanggal)=#{tahun} AND b.status is null  "
                + "GROUP BY judul,c.nomor,debit,tanggal_cancel,kode,dari "
                + "UNION "
                + "SELECT 'Stok Opname' as judul, b.nomor, 0 as debit,sum(a.selisih/d.isi_satuan * -1) as kredit, b.tanggal , 'i' as kode, a.notes as dari "
                + "FROM stok_opname_detil a "
                + "INNER JOIN stok_opname  b ON a.id=b.id "
                + "INNER JOIN barang d ON a.id_barang=d.id_barang "
                + "WHERE b.id_gudang=#{id_gudang} AND a.id_barang=#{id_barang} "
                + "AND EXTRACT(month FROM b.tanggal)=#{nomor}  AND EXTRACT(year FROM b.tanggal)=#{tahun} "
                + "AND b.status ='A' AND a.selisih < 0"
                + "GROUP BY judul,b.nomor,debit,b.tanggal,kode,dari "
                + "ORDER BY tanggal,kode,nomor ";
        StringBuilder Query = new StringBuilder(QUERYNYA);
        return Query.toString();
    }

    public String stockMovementDaily(Map<String, Object> parameters) {

        Integer nomor = (Integer) parameters.get("nomor");
        String id_gudang = (String) parameters.get("id_gudang");
        String years = (String) parameters.get("years");
        Date tglmulai = (Date) parameters.get("tglmulai");
        Date tglselesai = (Date) parameters.get("tglselesai");
        String saldoawal = "";

        switch (nomor) {
            case 1:
                saldoawal = "(db0)-(cr0)";
                break;
            case 2:
                saldoawal = "(db0+db1)-(cr0+cr1)";
                break;
            case 3:
                saldoawal = "(db0+db1+db2)-(cr0+cr1+cr2)";
                break;
            case 4:
                saldoawal = "(db0+db1+db2+db3)-(cr0+cr1+cr2+cr3)";
                break;
            case 5:
                saldoawal = "(db0+db1+db2+db3+db4)-(cr0+cr1+cr2+cr3+cr4)";
                break;
            case 6:
                saldoawal = "(db0+db1+db2+db3+db4+db5)-(cr0+cr1+cr2+cr3+cr4+cr5)";
                break;
            case 7:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6)";
                break;
            case 8:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7)";
                break;
            case 9:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8)";
                break;
            case 10:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9)";
                break;
            case 11:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10)";
                break;
            case 12:
                saldoawal = "(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11)-(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+db9+cr10+cr11)";
                break;

        }
        String QUERYNYA = "";
        if (tglmulai.equals(tglselesai)) {
            QUERYNYA = "SELECT a.id_barang,b.nama_barang, c.satuan_besar, b.isi_satuan,"
                    + " ((" + saldoawal + ") / b.isi_satuan ) as saldo_awal, ";
        } else {
            QUERYNYA = "SELECT a.id_barang,b.nama_barang, c.satuan_besar, b.isi_satuan,"
                    + " ((" + saldoawal + ") / b.isi_satuan "
                    + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penerimaan_gudang_detail dtl INNER JOIN penerimaan_gudang hd ON dtl.no_penerimaan=hd.no_penerimaan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND (hd.status = true OR hd.status is null) AND hd.tgl_penerimaan >= #{tglmulai} AND hd.tgl_penerimaan < #{tglselesai} ) "
                    + " + (SELECT COALESCE(SUM(terima),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_tujuan=a.id_gudang AND (hd.status = 'R' OR hd.status = 'X') AND hd.tanggal_terima >= #{tglmulai} AND hd.tanggal_terima < #{tglselesai}) "
                    + " + (SELECT COALESCE(SUM(qty),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor INNER JOIN internaltransfer_cancel cc ON dtl.nomor=cc.no_it WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_asal=a.id_gudang AND hd.status = 'C' AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai}) "
                    + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM decanting_detail dtl INNER JOIN decanting hd ON hd.no_decanting=dtl.no_decanting WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'A' AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai} AND dtl.asal='T') "
                    + " + (SELECT COALESCE(SUM(qty),0) FROM packinglist_detail dtl INNER JOIN packinglist hd ON hd.nomor=dtl.no_pl INNER JOIN packinglist_cancel cc ON dtl.no_pl = cc.no_pl WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C' AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai}) "
                    + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penambahan_detail dtl INNER JOIN penambahan hd ON hd.id=dtl.id WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status != 'D' AND hd.status is not null AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai}) "
                    + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM pemakaian_detail dtl INNER JOIN pemakaian hd ON hd.id=dtl.id INNER JOIN pemakaian_cancel cc ON hd.nomor = cc.no_pemakaian WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C' AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai}) "
                    + " - (SELECT COALESCE(SUM(qty),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_asal=a.id_gudang AND (hd.status = 'S' OR hd.status = 'R' OR hd.status = 'X' OR hd.status = 'C') AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai}) "
                    + " - (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM decanting_detail dtl INNER JOIN decanting hd ON dtl.no_decanting=hd.no_decanting WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'A' AND dtl.asal = 'F' AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai} ) "
                    + " - (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM pemakaian_detail dtl INNER JOIN pemakaian hd ON dtl.id=hd.id WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status != 'D' AND hd.status is not null AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai} ) "
                    + " - (SELECT COALESCE(SUM(terima),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON dtl.nomor=hd.nomor INNER JOIN internaltransfer_cancel cc ON hd.nomor=cc.no_it  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_tujuan=a.id_gudang AND hd.status = 'X'  AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai} ) "
                    + " - (SELECT COALESCE(SUM(qty),0) FROM packinglist_detail dtl INNER JOIN packinglist hd ON hd.nomor=dtl.no_pl  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status !='D'  AND hd.status is not null AND hd.tanggal >= #{tglmulai} AND hd.tanggal < #{tglselesai}) "
                    + " - (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penambahan_detail dtl INNER JOIN penambahan hd ON hd.id=dtl.id INNER JOIN penambahan_cancel cc ON hd.nomor = cc.no_penambahan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C' AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai}) "
                    + " - (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penerimaan_gudang_detail dtl INNER JOIN penerimaan_gudang hd ON hd.no_penerimaan=dtl.no_penerimaan INNER JOIN penerimaan_gudang_cancel cc ON hd.no_penerimaan = cc.no_penerimaan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status is null AND cc.tanggal >= #{tglmulai} AND cc.tanggal < #{tglselesai}) "
                    + " ) as saldo_awal,  ";
        }
        QUERYNYA = QUERYNYA + " (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penerimaan_gudang_detail dtl INNER JOIN penerimaan_gudang hd ON dtl.no_penerimaan=hd.no_penerimaan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND (hd.status = true OR hd.status is null) AND hd.tgl_penerimaan = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(terima),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_tujuan=a.id_gudang AND (hd.status = 'R' OR hd.status = 'X') AND hd.tanggal_terima = #{tglselesai} ) "
                + " + (SELECT COALESCE(SUM(qty),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor INNER JOIN internaltransfer_cancel cc ON dtl.nomor=cc.no_it WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_asal=a.id_gudang AND hd.status = 'C' AND date(cc.tanggal) = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM decanting_detail dtl INNER JOIN decanting hd ON hd.no_decanting=dtl.no_decanting WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'A' AND hd.tanggal = #{tglselesai} AND dtl.asal='T') "
                + " + (SELECT COALESCE(SUM(qty),0) FROM packinglist_detail dtl INNER JOIN packinglist hd ON hd.nomor=dtl.no_pl INNER JOIN packinglist_cancel cc ON dtl.no_pl = cc.no_pl WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C'  AND date(cc.tanggal) = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penambahan_detail dtl INNER JOIN penambahan hd ON hd.id=dtl.id WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status != 'D' AND hd.status is not null AND hd.tanggal = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM pemakaian_detail dtl INNER JOIN pemakaian hd ON hd.id=dtl.id INNER JOIN pemakaian_cancel cc ON hd.nomor = cc.no_pemakaian WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C' AND  date(cc.tanggal) = #{tglselesai}) "
                + " as masuk, "
                + "  (SELECT COALESCE(SUM(qty),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON hd.nomor=dtl.nomor  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_asal=a.id_gudang AND (hd.status = 'S' OR hd.status = 'R' OR hd.status = 'X' OR hd.status = 'C') AND  hd.tanggal = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM decanting_detail dtl INNER JOIN decanting hd ON dtl.no_decanting=hd.no_decanting WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'A' AND dtl.asal = 'F' AND  hd.tanggal = #{tglselesai} ) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM pemakaian_detail dtl INNER JOIN pemakaian hd ON dtl.id=hd.id WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status != 'D' AND hd.status is not null AND  hd.tanggal = #{tglselesai} ) "
                + " + (SELECT COALESCE(SUM(terima),0) FROM internal_transfer_detail dtl INNER JOIN internal_transfer hd ON dtl.nomor=hd.nomor INNER JOIN internaltransfer_cancel cc ON hd.nomor=cc.no_it  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang_tujuan=a.id_gudang AND hd.status = 'X'  AND date(cc.tanggal) = #{tglselesai} ) "
                + " + (SELECT COALESCE(SUM(qty),0) FROM packinglist_detail dtl INNER JOIN packinglist hd ON hd.nomor=dtl.no_pl  WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status !='D'  AND hd.status is not null AND  hd.tanggal = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penambahan_detail dtl INNER JOIN penambahan hd ON hd.id=dtl.id INNER JOIN penambahan_cancel cc ON hd.nomor = cc.no_penambahan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status = 'C' AND  date(cc.tanggal) = #{tglselesai}) "
                + " + (SELECT COALESCE(SUM(qty),0)/b.isi_satuan FROM penerimaan_gudang_detail dtl INNER JOIN penerimaan_gudang hd ON hd.no_penerimaan=dtl.no_penerimaan INNER JOIN penerimaan_gudang_cancel cc ON hd.no_penerimaan = cc.no_penerimaan WHERE dtl.id_barang=a.id_barang AND hd.id_gudang=a.id_gudang AND hd.status is null AND  date(cc.tanggal) = #{tglselesai}) "
                + " as keluar "
                + " FROM stok_value a "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar=c.id_satuan_besar "
                + " WHERE a.id_gudang=#{id_gudang}  AND a.years=#{years} "
                + " ORDER BY b.nama_barang ";

        StringBuilder Query = new StringBuilder(QUERYNYA);

        return Query.toString();
    }

    public String selectAllSo(Map<String, Object> parameters) {
        Date tglmulai = (Date) parameters.get("tglmulai");
        Date tglselesai = (Date) parameters.get("tglselesai");
        Character status = (Character) parameters.get("status");
        String id_pegawai = (String) parameters.get("id_pegawai");
        String query = "SELECT a.*,b.gudang FROM stok_opname a "
                + " INNER JOIN gudang b ON a.id_gudang=b.id_gudang ";

        if (!id_pegawai.equals("akuntansi")) {
            query += " WHERE a.id_gudang IN (SELECT id_gudang FROM hr.pegawai_gudang WHERE id_pegawai like #{id_pegawai})";
            if (tglmulai != null) {
                query += " AND a.tanggal >= #{tglmulai} AND a.tanggal <= #{tglselesai} ";
            }
            if (status != null) {
                query += " AND a.status=#{status}";
            }
        } else {
            if (tglmulai != null) {
                query += " WHERE a.tanggal >= #{tglmulai} AND a.tanggal <= #{tglselesai} ";
                if (status != null) {
                    query += " AND a.status=#{status}";
                } else {
                    query += " AND (a.status='A' OR a.status='P') ";
                }
            } else {
                if (status != null) {
                    query += " WHERE a.status=#{status}";
                } else {
                    query += " WHERE a.status='A' OR a.status='P' ";
                }
            }
        }

        query += " ORDER BY a.tanggal DESC";
        StringBuilder Query = new StringBuilder(query);
        return Query.toString();
    }

}
