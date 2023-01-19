package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPenerimaanGudang;
import net.sra.prime.ultima.entity.PenawaranDetail;
import net.sra.prime.ultima.db.mapper.MapperReturnPembelianGudang;
import net.sra.prime.ultima.db.mapper.MapperPo;
import net.sra.prime.ultima.db.mapper.MapperPoDetail;
import net.sra.prime.ultima.db.mapper.MapperReferensi;
import net.sra.prime.ultima.db.mapper.MapperStokBarang;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.PenerimaanGudang;
import net.sra.prime.ultima.entity.PenerimaanGudangDetail;
import net.sra.prime.ultima.entity.ReturnPembelianGudang;
import net.sra.prime.ultima.entity.ReturnPembelianGudangDetail;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.StokBarang;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.view.input.BarangAutoComplete;
import net.sra.prime.ultima.view.input.PenerimaanGudangAutoComplete;
import org.apache.ibatis.session.SqlSessionFactory;
//import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.view.input.SupplierAutoComplete;
import org.apache.ibatis.session.SqlSession;
import org.primefaces.event.SelectEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerReturnPembelianGudang implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<ReturnPembelianGudang> lReturn = new ArrayList<>();
    private ReturnPembelianGudang item;
    private ReturnPembelianGudangDetail itemdetail;
    private ReturnPembelianGudangDetail sP;
    private List<ReturnPembelianGudangDetail> lReturnDetail = new ArrayList<>();
    private List<Supplier> lSupplier = new ArrayList<>();
    private String id_gudang;
    private String id_perusahaan;

    @Inject
    private SupplierAutoComplete sac;

    @Inject
    private BarangAutoComplete barangAutoComplete;

    @Inject
    private PenerimaanGudangAutoComplete penerimaanGudangAutoComplete;

    @Inject
    private Page page;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    private MapperReferensi mapper;

    @PostConstruct
    public void init() {
        item = new ReturnPembelianGudang();
        itemdetail = new ReturnPembelianGudangDetail();
        Gudang gudang = new Gudang();
        gudang = page.getMyGudang();
        item.setId_gudang(gudang.getId_gudang());
        item.setGudang(gudang.getGudang());
        if (page.getMyGudang() != null) {
            id_gudang = page.getMyGudang().getId_gudang();
        } else {
            id_gudang = null;
        }
    }

    public void initItem() {
        Date date = new Date();
        item.setTanggal(date);
        Supplier supplier = new Supplier();
        sac.setSupplier(supplier);
        PenerimaanGudang penerimaanGudang = new PenerimaanGudang();
        penerimaanGudangAutoComplete.setPenerimaan(penerimaanGudang);
        this.nomorurut();

    }

    public void nomorurut() {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = referensi.SelectMax(id_gudang);
        sess.close();
        if (noMax == null) {
            item.setNo_return_pembelian_gudang("001" + id_gudang + "/RTP/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNo_return_pembelian_gudang(noMax + "/" + id_gudang + "/RTP/" + bulan + "/" + tahun);
        }
    }

    public void onLoadList() {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        try {
            lReturn = referensi.selectAll();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<ReturnPembelianGudang> getDataReturn() {
        return lReturn;
    }

    @Transactional(readOnly = false)
    public void delete(String no_penerimaan) {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        MapperPoDetail mapperPoDetail = sess.getMapper(MapperPoDetail.class);
        MapperStokBarang mapperStok = sess.getMapper(MapperStokBarang.class);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            referensi.deleteDetail(no_penerimaan);
            //mapperStok.delete(no_penerimaan);
            referensi.delete(no_penerimaan);
            context.getExternalContext().redirect("./list.jsf");
            sess.commit();
        } catch (Exception e) {
            sess.rollback();
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
        sess.close();
    }

    public List<ReturnPembelianGudangDetail> getDataReturnDetail() {
        return lReturnDetail;

    }

    public void extend() {
        sP = new ReturnPembelianGudangDetail();
        lReturnDetail.add(sP);
    }

    public void onDeleteClicked(ReturnPembelianGudangDetail item) {
        lReturnDetail.remove(item);

    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getNo_return_pembelian_gudang());
    }

    public void onLoad() {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        try {
            item = referensi.selectOne(item.getNo_return_pembelian_gudang());
            PenerimaanGudang po = new PenerimaanGudang();
            po.setNo_penerimaan(item.getNomor_penerimaan());
            penerimaanGudangAutoComplete.setPenerimaan(po);
            lReturnDetail = referensi.selectOneperPenerimaanDetail(item.getNo_return_pembelian_gudang());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sess.close();
    }

    public void onCellEdit(CellEditEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        PenawaranDetail entity = context.getApplication().evaluateExpressionGet(context, "#{item}", PenawaranDetail.class);
        entity.setNama_barang(entity.getId_barang());
        // ...
    }

    public void onNama() {
        sP.setNama_barang(sP.getId_barang());
        //sP.setTotal(sP.getHarga() * sP.getQty());
    }

//    public void choose(String list) {
//        Map<String, Object> options = new HashMap<String, Object>();
//        options.put("resizable", false);
//        options.put("draggable", false);
//        options.put("modal", true);
//        RequestContext.getCurrentInstance().openDialog(list, options, null);
//
//    }

    public List<Po> getDataPo() {
        SqlSession sess = sqlSessionFactory.openSession();
        List<Po> lPo = new ArrayList<>();
        Po po = new Po();
        MapperPo mreferensi = sess.getMapper(MapperPo.class);
        try {
            // lPo = referensi.selectPoSupplier(item.getId_supplier());
//            lPo = mreferensi.selectAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sess.close();
        return lPo;
    }

    public void onPoChosen(SelectEvent event) {
        Po po = (Po) event.getObject();

        //item.setId_account_hpp(account.getId_account());
        //item.setAccount_hpp(account.getNama_account());
    }

    @Transactional(readOnly = false)
    public void tambah() {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            //item.setId_customer(sCustomer.getId_kontak());
            item.setStatus('D');
            referensi.insert(item);
            this.tambahdetail(sess);
            //this.tambahstok(sess);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./add.jsf");
            sess.commit();
        } catch (Exception e) {
            sess.rollback();
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
        sess.close();
    }

    public void tambahdetail(SqlSession sess) throws Exception {
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        MapperStokBarang mapperStokBarang = sess.getMapper(MapperStokBarang.class);
        Double qty;

        for (int i = 0; i < lReturnDetail.size(); i++) {
            Integer k = i + 1;
            itemdetail.setNo_return_pembelian_gudang(item.getNo_return_pembelian_gudang());
            itemdetail.setUrut(k);
            itemdetail.setId_barang(lReturnDetail.get(i).getId_barang());
            itemdetail.setQty(lReturnDetail.get(i).getQty());
            itemdetail.setKeterang(lReturnDetail.get(i).getKeterang());
            itemdetail.setBatch(lReturnDetail.get(i).getBatch());
            referensi.insertDetail(itemdetail);

            /////////// update Stok
            if (item.getStatus().equals('S'));
            mapperStokBarang.updateByPenerimaan(itemdetail.getQty() * -1, item.getId_gudang(), item.getNomor_penerimaan());

        }
    }

    public void tambahstok(SqlSession sess) throws Exception {
        MapperStokBarang mreferensidetail = sess.getMapper(MapperStokBarang.class);

        for (int i = 0; i < lReturnDetail.size(); i++) {
            StokBarang stokBarang = new StokBarang();
            stokBarang.setId_barang(lReturnDetail.get(i).getId_barang());
            stokBarang.setId_gudang(item.getId_gudang());
            stokBarang.setStok(lReturnDetail.get(i).getQty());
            stokBarang.setBatch(lReturnDetail.get(i).getBatch());
            stokBarang.setTanggal(item.getTanggal());
            stokBarang.setHpp(0.00);
            stokBarang.setReff(item.getNo_return_pembelian_gudang());
            mreferensidetail.insert(stokBarang);

        }
    }

    public void onPoSelect(SelectEvent event) {
        SqlSession sess = sqlSessionFactory.openSession();
        lReturnDetail = new ArrayList<>();
        MapperPenerimaanGudang mreferensi = sess.getMapper(MapperPenerimaanGudang.class);
        PenerimaanGudang selectedPo = new PenerimaanGudang();
        PenerimaanGudang tes = (PenerimaanGudang) event.getObject();
        selectedPo = mreferensi.selectOne(tes.getNo_penerimaan());
        item.setId_supplier(selectedPo.getId_supplier());
        item.setNama_supplier(selectedPo.getNama_supplier());
        item.setId_gudang(selectedPo.getId_gudang());
        item.setNomor_penerimaan(selectedPo.getNo_penerimaan());
        item.setReferensi(selectedPo.getReferensi());
        item.setKeterangan("Retun Pembelian " + item.getNama_supplier());
        List<PenerimaanGudangDetail> listPoDetail = new ArrayList<>();
        listPoDetail = mreferensi.selectOneperPenerimaanDetail(item.getNomor_penerimaan());
        for (int j = 0; j < listPoDetail.size(); j++) {
            ReturnPembelianGudangDetail pd = new ReturnPembelianGudangDetail();
            pd.setNo_return_pembelian_gudang(item.getNo_return_pembelian_gudang());
            pd.setUrut(listPoDetail.get(j).getUrut());
            pd.setId_barang(listPoDetail.get(j).getId_barang());
            pd.setNama_barang(listPoDetail.get(j).getNama_barang());
            //pd.setQty(0.00);
            pd.setSatuan_kecil(listPoDetail.get(j).getSatuan_kecil());
            pd.setId_satuan_kecil(listPoDetail.get(j).getId_satuan_kecil());

            lReturnDetail.add(pd);
            sess.close();

        }
    }

    public void onBarangSelect(ReturnPembelianGudangDetail s, Integer i) {
        s.setId_barang(barangAutoComplete.getBarang().getId_barang());
        s.setNama_barang(barangAutoComplete.getBarang().getNama_barang());
        s.setId_satuan_kecil(barangAutoComplete.getBarang().getId_satuan_kecil());
        s.setSatuan_kecil(barangAutoComplete.getBarang().getSatuan_kecil());
        lReturnDetail.set(i, s);

    }

    @Transactional(readOnly = false)
    public void ubah(Character status) {
        SqlSession sess = sqlSessionFactory.openSession();
        MapperReturnPembelianGudang referensi = sess.getMapper(MapperReturnPembelianGudang.class);
        MapperStokBarang mapperStok = sess.getMapper(MapperStokBarang.class);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(status);
            referensi.deleteDetail(item.getNo_return_pembelian_gudang());
            mapperStok.delete(item.getNo_return_pembelian_gudang());
            referensi.update(item);
            this.tambahdetail(sess);
            // this.tambahstok(sess);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            sess.commit();
        } catch (Exception e) {
            sess.rollback();
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

}
