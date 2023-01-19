
package net.sra.prime.ultima.db.mapper;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderForecast {

    public String SelectAll(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang, c.nama as create_name "
                + " FROM forecast a  "
                + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang "
                + " LEFT JOIN pegawai c ON c.id_pegawai = a.create_by ");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_region = (String) parameters.get("id_region");

        String where = " WHERE ";
        if (id_region != null && !"".equals(id_region)) {
            Query.append(where).append(" a.id_region='").append(id_region).append("'");

        } else {
            Query.append(where).append(" a.status='S' ");
        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }

        Query.append(" ORDER BY  a.tanggal DESC");
        return Query.toString();
    }

    public String SelectAllFporIp(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.*, b.gudang, e.id_forecast, d.nama as create_name,e.no_intruksi_po,e.tanggal as tgl_ip "
                + " FROM forecast a  "
                + " INNER JOIN gudang b ON a.id_gudang = b.id_gudang "
                + " LEFT JOIN pegawai d ON d.id_pegawai = a.create_by "
                + " LEFT JOIN intruksi_po e ON a.id=e.id_forecast ");
        //Query.append(" LEFT JOIN (SELECT id_forecast FROM  intruksi_po GROUP BY id_forecast) c ON a.id=c.id_forecast  ");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Character statusip = (Character) parameters.get("statusip");

        String where = " WHERE ";
        if (statusip.equals('R')) {
            Query.append(where).append(" a.status='R' ");
        } else {
            Query.append(where).append(" a.status='S' ");
        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }
        if (statusip.equals('S')) {
            Query.append(" AND e.id_forecast is not null");
        } else if (statusip.equals('B')) {
            Query.append(" AND e.id_forecast is null");
        }
        Query.append(" ORDER BY  a.tanggal DESC");
        return Query.toString();
    }

    public String SelectForecastToPo(Map<String, Object> parameters) {

        StringBuilder Query = new StringBuilder("SELECT a.no_forecast,b.id_barang,ab.nama_barang,b.total,e.gudang, "
                + "array_to_string(array(SELECT nomor_po FROM po INNER JOIN po_detail pd ON  po.id=pd.id WHERE c.id=po.id_intruksi_po  AND pd.id_barang=b.id_barang AND po.is_approve=true),', ') as no_po,"
                + "COALESCE((SELECT SUM(qty) FROM po_detail pd INNER JOIN po ON po.id=pd.id WHERE po.id_intruksi_po=c.id  AND pd.id_barang=b.id_barang  AND po.is_approve=true), 0) as openpo, "
                + "b.total- COALESCE((SELECT SUM(qty) FROM po_detail pd INNER JOIN po ON po.id=pd.id WHERE po.id_intruksi_po=c.id  AND pd.id_barang=b.id_barang  AND po.is_approve=true), 0) as selisih "
                + "FROM forecast a "
                + "INNER JOIN forecast_detail b ON a.id=b.id "
                + "INNER JOIN barang ab ON b.id_barang=ab.id_barang "
                + "INNER JOIN gudang e ON a.id_gudang=e.id_gudang "
                + "LEFT JOIN intruksi_po c ON a.id=c.id_forecast "
                + "LEFT JOIN intruksi_po_detail d ON c.id=d.id AND b.id_barang=d.id_barang "
                + "WHERE  a.status='S'");

        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String id_region = (String) parameters.get("id_region");
        String id_gudang = (String) parameters.get("id_gudang");

//        if (id_region != null && !"".equals(id_region)) {
//            Query.append(" AND a.id_region='").append(id_region).append("'");
//        }
        if (id_gudang != null && !"".equals(id_gudang)) {
            Query.append(" AND a.id_gudang=#{id_gudang} ");
        }
        if (awal != null) {
            Query.append(" AND  a.tanggal >= #{awal} AND a.tanggal <= #{akhir}");
        }

        Query.append(" ORDER BY a.tanggal,a.no_forecast,b.urut");
        return Query.toString();
    }
    
    public String SelectAllReport(Map<String, Object> parameters) {

        String id_barang = (String) parameters.get("id_barang");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        String gudang = (String) parameters.get("gudang");
        Character status = (Character) parameters.get("status");

        StringBuilder Query = new StringBuilder("SELECT a.total,p.keterangan, b.nama_barang, c.satuan_besar, "
                + " d.satuan_kecil, b.isi_satuan,p.no_forecast,p.tanggal,p.status,p.triwulan,f.nama as create_name,a.total / b.isi_satuan as totalbesar "
                + " FROM forecast_detail a "
                + " INNER JOIN forecast p ON a.id=p.id "
                + " INNER JOIN barang b ON a.id_barang=b.id_barang "
                + " INNER JOIN satuan_besar c ON b.id_satuan_besar = c.id_satuan_besar "
                + " INNER JOIN satuan_kecil d ON b.id_satuan_kecil = d.id_satuan_kecil "
                + " LEFT JOIN pegawai f ON p.create_by=f.id_pegawai "
                + " WHERE a.id_barang=#{id_barang}"
        );
        if (awal != null) {
            Query.append(" AND p.tanggal >= #{awal} AND p.tanggal <= #{akhir}");
        }
        if (gudang != null) {
            Query.append(" AND p.id_gudang=#{gudang} ");
        }
        if (status != null) {
                    Query.append(" AND p.status=#{status} ");
            }

        Query.append(" ORDER BY p.tanggal, p.no_forecast");

        return Query.toString();
    }

}
