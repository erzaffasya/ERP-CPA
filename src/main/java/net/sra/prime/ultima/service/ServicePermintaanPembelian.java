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

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperPegawai;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelian;
import net.sra.prime.ultima.db.mapper.MapperPermintaanPembelianDetail;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PermintaanPembelian;
import net.sra.prime.ultima.entity.PermintaanPembelianDetail;
import net.sra.prime.ultima.entity.PermintaanPembelianPesan;
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
public class ServicePermintaanPembelian {

    @Autowired
    MapperPermintaanPembelian referensi;

    @Autowired
    MapperPegawai mapperPegawai;

    @Autowired
    MapperPermintaanPembelianDetail mapperPermintaanPembelianDetail;

    @Transactional(readOnly = true)
    public String noMax(Integer bulan, Integer tahun) {
        return referensi.SelectMax(bulan, tahun);
    }

    @Transactional(readOnly = true)
    public Pegawai selectHeadDepartemen(Integer id_departemen) {
        return mapperPegawai.selectHeadDepartemen(id_departemen);
    }

    @Transactional(readOnly = true)
    public Pegawai selectHeadDepartemenByKantor(Integer id_departemen, String id_kantor) {
        return mapperPegawai.selectHeadDepartemenByKantor(id_departemen, id_kantor);
    }

    @Transactional(readOnly = true)
    public Pegawai selectOnePegawai(String iDPegawai) {
        return mapperPegawai.selectOne(iDPegawai);
    }

    @Transactional(readOnly = true)
    public Pegawai selectJabatanPegawai(Integer idJabatan) {
        return mapperPegawai.selectJabatanPegawai(idJabatan);
    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelian> onLoadList(Date awal, Date akhir, String idPegawai, Boolean status, Character statusnya, Integer id_jabatan) {
        return referensi.selectAll(awal, akhir, idPegawai, status, statusnya, id_jabatan);
    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelian> onLoadListMaintenance(Date awal, Date akhir) {
        return referensi.selectAllMaintenance(awal, akhir);
    }

    @Transactional(readOnly = true)
    public PermintaanPembelian onLoad(String id) {
        return referensi.selectOne(id);
    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelianPesan> selectPesan(String nopp) {
        return referensi.selectAllPesan(nopp);
    }

    @Transactional(readOnly = true)
    public List<PermintaanPembelianDetail> onLoadListDetail(String id) {
        return mapperPermintaanPembelianDetail.selectOne(id);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        mapperPermintaanPembelianDetail.delete(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = false)
    public void ubah(PermintaanPembelian item, List<PermintaanPembelianDetail> lPermintaanPembelianDetail) {
        referensi.update(item);
        mapperPermintaanPembelianDetail.delete(item.getNo_pp());
        tambahdetail(item, lPermintaanPembelianDetail);
    }

    @Transactional(readOnly = false)
    public void ubahMaintenance(PermintaanPembelian item, List<PermintaanPembelianDetail> lPermintaanPembelianDetail) {
        referensi.updateMaintenance(item);
        mapperPermintaanPembelianDetail.delete(item.getNo_pp());
        tambahdetail(item, lPermintaanPembelianDetail);
    }

    @Transactional(readOnly = false)
    public void sendPP(PermintaanPembelian item, List<PermintaanPembelianDetail> lPermintaanPembelianDetail) {
        referensi.update(item);
        referensi.updateSend(item.getNo_pp());
        mapperPermintaanPembelianDetail.delete(item.getNo_pp());
        tambahdetail(item, lPermintaanPembelianDetail);
    }

    @Transactional(readOnly = false)
    public void tambah(PermintaanPembelian item, List<PermintaanPembelianDetail> lPermintaanPembelianDetail) {
        referensi.insert(item);
        tambahdetail(item, lPermintaanPembelianDetail);
    }

    @Transactional(readOnly = false)
    public void tambahdetail(PermintaanPembelian item, List<PermintaanPembelianDetail> lPermintaanPembelianDetail) {
        for (int i = 0; i < lPermintaanPembelianDetail.size(); i++) {
            PermintaanPembelianDetail itemdetail = new PermintaanPembelianDetail();
            Integer k = i + 1;
            itemdetail.setNo_pp(item.getNo_pp());
            itemdetail.setUrut(k);
            itemdetail.setId_barang(lPermintaanPembelianDetail.get(i).getId_barang());
            itemdetail.setKeperluan(lPermintaanPembelianDetail.get(i).getKeperluan());
            itemdetail.setSpesifikasi(lPermintaanPembelianDetail.get(i).getSpesifikasi());
            itemdetail.setJumlah_order(lPermintaanPembelianDetail.get(i).getJumlah_order());
            itemdetail.setJumlah_stok(lPermintaanPembelianDetail.get(i).getJumlah_stok());
            itemdetail.setTanggal_dibutuhkan(lPermintaanPembelianDetail.get(i).getTanggal_dibutuhkan());
            itemdetail.setKeterangan(lPermintaanPembelianDetail.get(i).getKeterangan());
            itemdetail.setSatuan(lPermintaanPembelianDetail.get(i).getSatuan());
            itemdetail.setHarga(lPermintaanPembelianDetail.get(i).getHarga());
            itemdetail.setAmount(lPermintaanPembelianDetail.get(i).getAmount());
            mapperPermintaanPembelianDetail.insert(itemdetail);
        }

    }

    @Transactional(readOnly = false)
    public void updateSpv(PermintaanPembelian item, Boolean setuju,String id_pegawai) {
        referensi.updateSpv(item.getNo_pp(), setuju);
        insertPesan(item, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updateAtasan(PermintaanPembelian item, Boolean setuju,String id_pegawai) {
        referensi.updateAtasan(item.getNo_pp(), setuju);
        insertPesan(item, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updateDirektur(PermintaanPembelian item, Boolean setuju,String id_pegawai) {
        referensi.updateDirektur(item.getNo_pp(), setuju);
        insertPesan(item, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void updateKeuangan(PermintaanPembelian item, Boolean setuju,String id_pegawai) {
        referensi.updateKeuangan(item.getNo_pp(), setuju);
        insertPesan(item, id_pegawai);
    }

    @Transactional(readOnly = false)
    public void insertPesan(PermintaanPembelian item, String id_pegawai) {
        if (item.getPesan() != null) {
            PermintaanPembelianPesan permintaanPembelianPesan = new PermintaanPembelianPesan();
            permintaanPembelianPesan.setPesan(item.getPesan());
            permintaanPembelianPesan.setDari(id_pegawai);
            permintaanPembelianPesan.setNo_pp(item.getNo_pp());
            referensi.insertPesan(permintaanPembelianPesan);
        }

    }

    @Transactional(readOnly = true)
    public Integer selectCountHeadDepartemen(Integer id_departemen) {
        return mapperPegawai.selectCountHeadDepartemen(id_departemen);
    }

}
