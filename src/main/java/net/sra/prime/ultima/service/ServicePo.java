/*
 * Copyright 2017 JoinFaces.
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
package net.sra.prime.ultima.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperHargaBeli;
import net.sra.prime.ultima.db.mapper.MapperIntruksiPo;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelian;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelianDetail;
import net.sra.prime.ultima.db.mapper.MapperPo;
import net.sra.prime.ultima.db.mapper.MapperPoDetail;
import net.sra.prime.ultima.db.mapper.MapperShipto;
import net.sra.prime.ultima.db.mapper.MapperSupplier;
import net.sra.prime.ultima.entity.HargaBeli;
import net.sra.prime.ultima.entity.HargaBeliDetil;
import net.sra.prime.ultima.entity.IntruksiPoDetail;
import net.sra.prime.ultima.entity.IntruksiPo;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.PermintaanPembelianPesan;
import net.sra.prime.ultima.entity.Po;
import net.sra.prime.ultima.entity.PoDetail;
import net.sra.prime.ultima.entity.PoDetailReguler;
import net.sra.prime.ultima.entity.Shipto;
import net.sra.prime.ultima.entity.Supplier;
import net.sra.prime.ultima.entity.SupplierKontak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServicePo {

    @Autowired
    MapperPo referensi;

    @Autowired
    MapperPoDetail mapper;

    @Autowired
    MapperIntruksiPo mp;

    @Autowired
    MapperSupplier mreferensi;

    @Autowired
    MapperHargaBeli maphb;

    @Autowired
    MapperPegawai mpegawai;

    @Autowired
    MapperPermintaanPembelian mapperPP;

    @Autowired
    MapperPermintaanPembelianDetail mapperPermintaanPembelianDetail;

    @Autowired
    MapperSupplier mapperSupplier;

    @Autowired
    MapperShipto mapperShipto;

    @Transactional(readOnly = true)
    public String noMax(Integer tahun) {
        return referensi.SelectMax(tahun);
    }

    @Transactional(readOnly = true)
    public List<Po> onLoadList(Date awal, Date akhir, Character status, Character jenis, String id_pegawai, Integer id_jabatan) {
        return referensi.selectAll(awal, akhir, status, jenis,id_pegawai,id_jabatan);
    }

    @Transactional(readOnly = true)
    public List<Po> onLoadListPoGudang(Date awal, Date akhir, String id_pegawai) {
        return referensi.selectPoGudang(awal, akhir, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        mapper.delete(id);
        referensi.delete(id);
        mapper.deleteReguler(id);
    }

    @Transactional(readOnly = false)
    public void tambah(Po item, List<PoDetail> lPoDetail) {
        referensi.insert(item);
        this.tambahdetail(lPoDetail, item.getId());
        mp.updateStatus('C', item.getId_intruksi_po());
    }

    public void tambahdetail(List<PoDetail> lPoDetail, Integer id) {
        Double qty;
        for (int i = 0; i < lPoDetail.size(); i++) {
            qty = 0.00;
            PoDetail itemdetail = lPoDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(lPoDetail.get(i).getQty());
            itemdetail.setDiambil(0.00);
            mapper.insert(itemdetail);
        }
    }
    
    
    public void tambahdetailMaintenance(List<PoDetail> lPoDetail, Integer id) {
        Double qty;
        for (int i = 0; i < lPoDetail.size(); i++) {
            qty = 0.00;
            PoDetail itemdetail = lPoDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(itemdetail.getQty()-itemdetail.getDiambil());
            mapper.insertMaintenance(itemdetail);
        }
    }

    @Transactional(readOnly = false)
    public void ubah(Po item, List<PoDetail> lPoDetail,Character jenis) {
        mapper.delete(item.getId());
        referensi.updatePo(item);
        if(jenis.equals('P')){
        referensi.updateStatusPurchasing(item.getIs_purchasing(), item.getId());
        }else if(jenis.equals('C')){
        referensi.updateStatusChecked(item.getIs_checked1(), item.getId());
        }
        this.tambahdetail(lPoDetail, item.getId());

    }
    
    @Transactional(readOnly = false)
    public void ubahMaintenance(Po item, List<PoDetail> lPoDetail) {
        mapper.delete(item.getId());
        //referensi.deletePopp(item.getId());
        referensi.updatePo(item);
        this.tambahdetailMaintenance(lPoDetail, item.getId());

    }

    @Transactional(readOnly = true)
    public Po onLoad(Integer id) {
        return referensi.selectOneperPo(id);
    }

    @Transactional(readOnly = true)
    public List<PoDetail> onLoadDetail(Integer id) {
        return mapper.selectOneperPo(id);
    }

    @Transactional(readOnly = true)
    public Supplier onSupplierSelect(String idsupplier) {
        return mreferensi.selectOne(idsupplier);
    }

    @Transactional(readOnly = true)
    public HargaBeli onHargaBeliSelect(String id) {
        return maphb.selectOne(id);
    }

    @Transactional(readOnly = true)
    public HargaBeliDetil onSelectPrice(String nokontrak, String idbarang) {
        return maphb.selectPrices(nokontrak, idbarang);
    }

    @Transactional(readOnly = true)
    public SupplierKontak onKontakSelect(Integer kontak, String idsupplier) {
        return mreferensi.selectOneKontak(kontak, idsupplier);
    }

    @Transactional(readOnly = true)
    public Pegawai onPegawaiSelect(String idpegawai) {
        return mpegawai.selectOne(idpegawai);
    }

    @Transactional(readOnly = false)
    public void updateStatusPurchasing(Boolean nilai, Integer id) {
        referensi.updateStatusPurchasing(nilai, id);

    }

    @Transactional(readOnly = true)
    public List<PoDetail> onIntruksiPoSelect(Integer id) {
        List<PoDetail> lPoDetail = new ArrayList<>();
        List<IntruksiPoDetail> lPp = mp.selectAllDetail(id);
        for (int k = 0; k < lPp.size(); k++) {
            PoDetail poDetail = new PoDetail();
            poDetail.setId_barang(lPp.get(k).getId_barang());
            poDetail.setNama_barang(lPp.get(k).getNama_barang());
            poDetail.setQty(lPp.get(k).getSisa());
            poDetail.setSatuan_kecil(lPp.get(k).getSatuan_kecil());
            lPoDetail.add(poDetail);

        }
        return lPoDetail;
    }

    @Transactional(readOnly = true)
    public IntruksiPo intruksiPo(Integer id) {
        return mp.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void updateStatusApprove(Boolean nilai, Integer id, String nomorpo, String pesan, String idpegawai, Integer id_ipo) {
        referensi.updateStatusApprove(nilai, id);
        if (nilai) {
            mp.updateStatus('A', id);
        } else {
            mp.updateStatus('R', id);
        }
        if (pesan != null && !"".equals(pesan)) {
            PermintaanPembelianPesan permintaanPembelianPesan = new PermintaanPembelianPesan();
            permintaanPembelianPesan.setPesan(pesan);
            permintaanPembelianPesan.setDari(idpegawai);
            permintaanPembelianPesan.setNo_pp(nomorpo);
            mapperPP.insertPesan(permintaanPembelianPesan);
        }

        List<PoDetail> lPoDetail = mapper.selectOneperPo(id);
        Double qty;
        for (int i = 0; i < lPoDetail.size(); i++) {
            PoDetail itemdetail = lPoDetail.get(i);
            // jumlah qty diambil dari po detail yang status po nya is_approve= true
            qty = referensi.jumlahQty(id_ipo, itemdetail.getId_barang());
            if (qty == null) {
                qty = 0.00;
            }
            IntruksiPoDetail itemIP = new IntruksiPoDetail();
            itemIP.setId_barang(itemdetail.getId_barang());
            itemIP.setDiambil(qty);
            itemIP.setId(id_ipo);
            mp.updateSisa(itemIP);

        }

    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelianPesan> getDataPesan(String nomorpo) {
        return mapperPP.selectAllPesan(nomorpo);
    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelianDetail> onPPSelect(String nomorIntruksiPO) {
        return mapperPermintaanPembelianDetail.selectOne(nomorIntruksiPO);
    }

    @Transactional(readOnly = true)
    public PermintaanPembelian onPPSelectOne(String id) {
        return mapperPP.selectOne(id);
    }

    @Transactional(readOnly = true)
    public Supplier selectOneSupplier(String idSupplier) {
        return mapperSupplier.selectOne(idSupplier);
    }

    @Transactional(readOnly = true)
    public Shipto selectOneShipto(String shipto) {
        return mapperShipto.selectOne(shipto);
    }

    /////////////////// Reguler //////////////////////
    @Transactional(readOnly = false)
    public void tambahReguler(Po item, List<PoDetailReguler> lPoDetail) {
        referensi.insert(item);
        this.tambahdetailReguler(lPoDetail, item.getId());

    }

    public void tambahdetailReguler(List<PoDetailReguler> lPoDetail, Integer id) {
        Double qty;
        for (int i = 0; i < lPoDetail.size(); i++) {
            qty = 0.00;
            PoDetailReguler itemdetail = lPoDetail.get(i);
            itemdetail.setId(id);
            itemdetail.setUrut(i + 1);
            itemdetail.setSisa(lPoDetail.get(i).getQty());
            itemdetail.setDiambil(0.00);
            mapper.insertReguler(itemdetail);
        }
    }
    
    

    public List<PoDetailReguler> onLoadDetailReguler(Integer id) {
        return mapper.selectOneReguler(id);
    }

    @Transactional(readOnly = false)
    public void ubahReguler(Po item, List<PoDetailReguler> lPoDetail,Character jenis) {
        mapper.deleteReguler(item.getId());
        referensi.updatePo(item);
        if(jenis.equals('P')){
        referensi.updateStatusPurchasing(item.getIs_purchasing(), item.getId());
        }else if(jenis.equals('C')){
        referensi.updateStatusChecked(item.getIs_checked1(), item.getId());
        }
        this.tambahdetailReguler(lPoDetail, item.getId());
    }
    
    

    @Transactional(readOnly = true)
    public List<PoDetail> selectOutstandiPo(String id_gudang, String suppliernya) {
        return mapper.selectOutstanding(id_gudang, suppliernya);
    }

    @Transactional(readOnly = true)
    public List<Po> selectPoSupplier(String jenis_po) {
        return referensi.selectPoSupplier(jenis_po);
    }

    @Transactional(readOnly = true)
    public List<Po> selectPoRegulerSupplier() {
        return referensi.selectPoRegulerSupplier();
    }

    @Transactional(readOnly = true)
    public List<Po> selectPoForPenerimaan() {
        return referensi.selectPoForPenerimaan();
    }

    @Transactional(readOnly = true)
    public List<Po> selectPoSisaSupplier(String id_pegawai) {
//        if (id_gudang != null) {
//            return referensi.selectPoSisaSupplier(id_gudang);
//        } else {
//            return referensi.selectPoSisaSupplierAll();
//        }
        return referensi.selectPoSisaSupplier(id_pegawai);
    }

    @Transactional(readOnly = true)
    public Po selectOne(String no_po) {
        return referensi.selectOne(no_po);
    }

    @Transactional(readOnly = true)
    public Integer isPenerimaan(String no_po) {
        return referensi.isPenerimaan(no_po);
    }
    
    @Transactional(readOnly = true)
    public List<PoDetail> onLoadReport(String id_barang,Date awal,Date akhir,String gudang,Character status) {
        return mapper.selectAllPo(id_barang, awal, akhir, gudang, status);
    }
    
    @Transactional(readOnly = true)
    public String namaGudang(String id) {
        return referensi.namaGudang(id);
    }
    
    @Transactional(readOnly = true)
    public String getPurchasing() {
        return referensi.getPurchasing();
    }
}
