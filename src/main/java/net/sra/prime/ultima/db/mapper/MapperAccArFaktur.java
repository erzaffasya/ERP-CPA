
package net.sra.prime.ultima.db.mapper;

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.entity.AccArFaktur;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperAccArFaktur {

    
    @Select("SELECT a.*, b.customer "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " WHERE  "
            + " a.customer_code = #{id_customer} AND a.total - a.bayar != 0 "
            + " AND a.ar_number NOT IN (SELECT aa.no_Penjualan FROM pembayaran_piutang_detail aa "
            + " INNER JOIN pembayaran_piutang bb ON aa.nomor=bb.no_pembayaran_piutang WHERE bb.status='D')")
    public List<AccArFaktur> selectAll(@Param("id_customer") String id_customer) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.customer, f.id_kantor as id_kantor_cabang "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN penjualan c ON a.no_invoice=c.no_penjualan "
            + " LEFT JOIN pegawai e ON c.id_salesman=e.id_pegawai "
            + " LEFT JOIN master_jabatan f ON e.id_jabatan=f.id_jabatan "
            + " WHERE  "
            + " a.ar_number = #{ar_number}")
    public AccArFaktur selectOne(@Param("ar_number") String ar_number) throws RuntimeException;


    
    @Insert("INSERT INTO acc_ar_faktur "
            + "(ar_number, ar_date, customer_code, amount, due_date, notes, reff, id_perusahaan, no_invoice, status, bayar, "
            + "ppn, pph, total, invoice_date,  top, no_do, no_po, tgl_terimainvoice, id_salesman) "
            + "VALUES "
            + "(#{ar_number}, #{invoice_date}, #{customer_code}, #{amount}, #{due_date}, #{notes},  #{reff}, #{id_perusahaan}, #{no_invoice}, #{status}, #{bayar}, "
            + "#{ppn}, #{pph}, #{total}, #{invoice_date},  #{top}, #{no_do}, #{no_po},#{tgl_terimainvoice},#{id_salesman}) ")
    public int insert(AccArFaktur accApFaktur) throws RuntimeException;

    
    @Update("UPDATE acc_ar_faktur SET "
            + " bayar = bayar + #{bayar}, "
            + " bayar_dpp = bayar_dpp + #{dpp}, "
            + " bayar_ppn = bayar_ppn + #{ppn} "
            + " WHERE ar_number = #{ar_number}")
    public int updateBayar(@Param("dpp") Double dpp,@Param("ppn") Double ppn,@Param("bayar") Double bayar, @Param("ar_number") String ar_number) throws RuntimeException;
    
    @Update("UPDATE acc_ar_faktur SET "
            + " due_date =  #{due_date}, "
            + " notes = #{notes}, "
            + " invoice_date = #{invoice_date}, "
            + " top = #{top} "
            + " WHERE no_invoice = #{no_invoice}")
    public int updateMaintenance(AccArFaktur accArFaktur) throws RuntimeException;
    

    
    @Select("SELECT MAX(SUBSTR(ar_number,3,6)) "
            + " FROM acc_ar_faktur "
            + " WHERE EXTRACT(year FROM ar_date)=#{tahun} ")
    public String SelectMax(@Param("tahun") Integer tahun) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A')) as sumtotal,a.customer_code, b.customer "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A') != 0 "
            + " GROUP BY a.customer_code, b.customer "
            + " ORDER BY b.customer")
    public List<AccArFaktur> selectAllSum(@Param("awal") Date awal) throws RuntimeException;
    
    @Select("SELECT SUM(a.total- a.bayar) as sumtotal, a.customer_code, b.customer"
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " WHERE  "
            + " a.total - a.bayar !=0  "
            + " GROUP BY a.customer_code, b.customer "
            + " ORDER BY b.customer")
    public List<AccArFaktur> selectAllSumCurrent() throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A')) as sumtotal,a.customer_code, b.customer, c.nama as marketing, a.id_salesman "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " INNER JOIN pegawai c ON a.id_salesman=c.id_pegawai "
            + " INNER JOIN master_jabatan d ON c.id_jabatan=d.id_jabatan "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A') != 0 "
            + " AND (a.id_salesman=#{id_salesman} OR d.atasan = (SELECT id_jabatan FROM pegawai WHERE id_pegawai=#{id_salesman}) ) "
            + " GROUP BY a.customer_code, b.customer, c.nama, a.id_salesman "
            + " ORDER BY b.customer")
    public List<AccArFaktur> selectAllSumMarketing(@Param("awal") Date awal,
            @Param("id_salesman") String id_salesman) throws RuntimeException;
    
    
    // untuk memasukan cn maka AND  a.total > a.bayar - (SELECT COALESCE(SUM(jumlah_bayar),0) FROM pembayaran_piutang_detail b  tandanya diganti !=      
     @Select("SELECT a.*, ab.customer, ac.nama as marketing, ae.nama as cabang, "
            + " #{akhir} - a.invoice_date as umur_invoice, #{akhir} - a.due_date as umur_overdue "
            + " FROM acc_ar_faktur a "
            + " INNER JOIN customer ab ON a.customer_code=ab.id_kontak "
            + " LEFT JOIN pegawai ac ON a.id_salesman=ac.id_pegawai "
            + " LEFT JOIN master_jabatan ad ON ac.id_jabatan_new=ad.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang ae ON ae.id_kantor_cabang=ac.id_kantor_new "
            + " WHERE invoice_date <= #{akhir} "
            + " AND  a.total != a.bayar - (SELECT COALESCE(SUM(jumlah_bayar),0) FROM pembayaran_piutang_detail b "
            + " INNER JOIN pembayaran_piutang c ON c.no_pembayaran_piutang=b.nomor "
            + " WHERE "
            + " a.ar_number=b.no_penjualan "
            + " AND c.tanggal >= #{awal}  AND c.status='A') "
            + " ORDER BY a.invoice_date DESC")
    public List<AccArFaktur> selectCutOff(@Param("awal") Date awal,
            @Param("akhir") Date akhir) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.dpp),0) "
            + " FROM pembayaran_piutang_detail a "
            + " INNER JOIN pembayaran_piutang b ON a.nomor=b.no_pembayaran_piutang "
            + " WHERE "
            + " b.tanggal < #{tanggal} "
            + " AND a.no_penjualan=#{nomor}")
    public Double selectSumDPP(@Param("nomor") String nomor,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.ppn),0) "
            + " FROM pembayaran_piutang_detail a "
            + " INNER JOIN pembayaran_piutang b ON a.nomor=b.no_pembayaran_piutang "
            + " WHERE "
            + " b.tanggal < #{tanggal} "
            + " AND a.no_penjualan=#{nomor}")
    public Double selectSumPPN(@Param("nomor") String nomor,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(a.jumlah_bayar),0) "
            + " FROM pembayaran_piutang_detail a "
            + " INNER JOIN pembayaran_piutang b ON a.nomor=b.no_pembayaran_piutang "
            + " WHERE "
            + " b.tanggal < #{tanggal} "
            + " AND a.no_penjualan=#{nomor}")
    public Double selectSumBayar(@Param("nomor") String nomor,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT COALESCE(SUM(jumlah_bayar),0) from pembayaran_piutang_detail b "
            + " INNER JOIN pembayaran_piutang c On c.no_pembayaran_piutang=b.nomor "
            + " WHERE "
            + " b.no_penjualan = #{no_penjualan}"
            + " AND EXTRACT(month FROM c.tanggal) = #{bulan}  "
            + " AND EXTRACT(year FROM c.tanggal) = #{tahun} "
            + " AND c.status='A' ")
    public Double selectSumPayment(@Param("no_penjualan") String no_penjualan,
            @Param("bulan") Integer bulan,
            @Param("tahun") Integer tahun) throws RuntimeException;
    
    @Select("select COALESCE(SUM(jumlah_bayar),0) from pembayaran_piutang_detail b "
            + " INNER JOIN pembayaran_piutang c On c.no_pembayaran_piutang=b.nomor "
            + " WHERE "
            + " b.no_penjualan = #{no_penjualan} "
            + " AND c.tanggal >= #{tanggal}  ")
    public Double selectAllSumPayment(@Param("no_penjualan") String no_penjualan,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    
    
    
    @Select("SELECT SUM(a.total- a.bayar) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar !=0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.invoice_date >= #{awal} "
            + " AND CURRENT_DATE - a.invoice_date <= #{akhir} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSum(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code
            ) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A')) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A') != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.invoice_date >= #{awal} "
            + " AND CURRENT_DATE - a.invoice_date <= #{akhir} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumByDate(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A')) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A') != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date >= #{awal} "
            + " AND CURRENT_DATE - a.due_date <= #{akhir} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDueDate(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT SUM(a.total- a.bayar) as sumtotal  "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar !=0  "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date >= #{awal} "
            + " AND CURRENT_DATE - a.due_date <= #{akhir} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDueDateCurrent(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code
            ) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A')) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A') != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date >= #{awal} "
            + " AND CURRENT_DATE - a.due_date <= #{akhir} "
            + " AND a.id_salesman=#{id_salesman} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDueDateCustomer(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code,
            @Param("tanggal") Date tanggal,
            @Param("id_salesman") String id_salesman) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A')) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A') != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date <= #{awal} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDue0(@Param("awal") Integer awal, 
            @Param("customer_code") String customer_code,
            @Param("tanggal") Date tanggal) throws RuntimeException;
    
    @Select("SELECT SUM(a.total- a.bayar)  as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar !=0   "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date <= #{awal} "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDue0Current(@Param("awal") Integer awal, 
            @Param("customer_code") String customer_code
            ) throws RuntimeException;
    
    
    @Select("SELECT SUM(a.total- (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A')) as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{tanggal} AND cc.status='A') != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date <= #{awal} "
            + " AND a.id_salesman=#{id_salesman}  "
            + " GROUP BY a.customer_code ")
    public Double selectOneSumDue0Customer(@Param("awal") Integer awal, 
            @Param("customer_code") String customer_code,
            @Param("tanggal") Date tanggal,
            @Param("id_salesman") String id_salesman) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - due_date as umur_overdue, "
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan_new "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=e.id_kantor_cabang "
            + " WHERE  a.customer_code=#{customer_code} AND "
            + " a.total - a.bayar != 0 "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> selectAllInvoice(@Param("customer_code") String customer_code) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - due_date as umur_overdue, "
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan_new "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=c.id_kantor_new "
            + " WHERE  a.id_salesman=#{id_pegawai} AND "
            + " a.total - a.bayar != 0 "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> selectAllInvoiceByDsr(@Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    
    
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - due_date as umur_overdue, "
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan_new "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=c.id_kantor_new "
            + " WHERE  a.id_salesman=#{id_dsr} AND "
            + " a.total - a.bayar != 0 "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> outstandingAr(@Param("id_dsr") String id_dsr) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " #{awal} - a.invoice_date as umur_invoice, #{awal} - due_date as umur_overdue, "
            + " a.total-(SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A') as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=e.id_kantor_cabang "
            + " WHERE  a.customer_code=#{customer_code} AND "
            + " a.total - (SELECT COALESCE(sum(bb.jumlah_bayar),0) FROM pembayaran_piutang_detail bb "
            + " INNER JOIN pembayaran_piutang cc ON bb.nomor=cc.no_pembayaran_piutang " 
            + " WHERE bb.no_penjualan=a.ar_number AND cc.tanggal < #{awal} AND cc.status='A') != 0 "
            + " AND a.invoice_date < #{awal}"
            + " ORDER BY a.invoice_date")
   
    public List<AccArFaktur> selectAgingAr(@Param("customer_code") String customer_code,
            @Param("awal") Date awal) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - a.due_date as umur_overdue, "
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=e.id_kantor_cabang "
            + " WHERE  a.customer_code=#{customer_code} AND "
            + " a.total - a.bayar != 0 AND "
            + " a.id_salesman IN (SELECT f.id_pegawai  FROM pegawai f"
            + " INNER JOIN master_jabatan g ON f.id_jabatan=g.id_jabatan "
            + " WHERE g.id_jabatan=#{id_jabatan} OR g.atasan=#{id_jabatan}) "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> selectAllInvoiceDSM(@Param("customer_code") String customer_code, @Param("id_jabatan") Integer id_jabatan) throws RuntimeException;
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po, "
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - a.due_date as umur_overdue, "
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=e.id_kantor_cabang "
            + " WHERE  a.customer_code=#{customer_code} AND "
            + " a.total - a.bayar != 0 AND e.id_kantor_cabang=#{id_kantor} "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> selectAllInvoiceSalesAdmin(@Param("customer_code") String customer_code, @Param("id_kantor") String id_kantor) throws RuntimeException;
    
    
    
    
    
    @Select("SELECT a.*, b.customer,e.nama as cabang, a.reff as nomor_po,"
            + " CURRENT_DATE - a.invoice_date as umur_invoice, CURRENT_DATE - due_date as umur_overdue,"
            + " a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " LEFT JOIN customer b ON a.customer_code = b.id_kontak "
            + " LEFT JOIN pegawai c ON a.id_salesman = c.id_pegawai "
            + " LEFT JOIN master_jabatan d ON d.id_jabatan = c.id_jabatan "
            + " LEFT JOIN internal_kantor_cabang e ON d.id_kantor=e.id_kantor_cabang "
            + " WHERE  a.customer_code=#{customer_code} AND "
            + " a.total - a.bayar != 0 AND "
            + " a.id_salesman=#{id_pegawai} "
            + " ORDER BY a.invoice_date")
    public List<AccArFaktur> selectAllInvoiceDSR(@Param("customer_code") String customer_code, @Param("id_pegawai") String id_pegawai) throws RuntimeException;
    
    
    @Select("SELECT a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.invoice_date >= #{awal} "
            + " AND CURRENT_DATE - a.invoice_date <= #{akhir} "
            + " AND a.ar_number=#{ar_number}")
    public Double selectOneInvoiceCustomer(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code,
            @Param("ar_number") String ar_number) throws RuntimeException;
    
    
    
    @Select("SELECT a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar != 0 "
            + " AND a.customer_code=#{customer_code} "
            + " AND CURRENT_DATE - a.due_date >= #{awal} "
            + " AND CURRENT_DATE - a.due_date <= #{akhir} "
            + " AND a.ar_number=#{ar_number}")
    public Double selectOneOverdueCustomer(@Param("awal") Integer awal, 
            @Param("akhir") Integer akhir, 
            @Param("customer_code") String customer_code,
            @Param("ar_number") String ar_number) throws RuntimeException;
    
            
    @Select("SELECT a.total-a.bayar as sumtotal "
            + " FROM acc_ar_faktur a "
            + " WHERE  "
            + " a.total - a.bayar != 0 "
            + " AND a.ar_number=#{ar_number} "
            + " AND CURRENT_DATE - a.due_date <= #{awal} ")
    public Double selectOneDue0(@Param("awal") Integer awal, @Param("ar_number") String ar_number) throws RuntimeException;
    
    
    @Select("SELECT CURRENT_DATE - invoice_date FROM acc_ar_faktur WHERE ar_number=#{ar_number}")
    public Integer selectUmurInvoice(
            @Param("ar_number") String ar_number) throws RuntimeException;
    
    @Select("SELECT CURRENT_DATE - due_date FROM acc_ar_faktur WHERE ar_number=#{ar_number}")
    public Integer selectUmurOverdue(
            @Param("ar_number") String ar_number) throws RuntimeException;
    
    @Select("SELECT headdepartemen FROM master_jabatan WHERE id_jabatan=#{id_jabatan}")
    public Boolean selectDsmMapping(@Param("id_jabatan") Integer id_jabatan) throws RuntimeException;
    
}
