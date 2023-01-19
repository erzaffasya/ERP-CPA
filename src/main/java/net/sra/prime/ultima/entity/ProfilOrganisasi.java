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
package net.sra.prime.ultima.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author Hairian
 */
@Mapper
@Getter
@Setter

public class ProfilOrganisasi implements java.io.Serializable {

    private String id;
    private String nama;
    private String nama_resmi;
    private Character tipe;
    private String bidang;
    private Character pengakuan_pajak;
    private String bulan_mulai;
    private String tahun_mulai;
    private Date tanggal_pencatatan;
    private String id_zona_waktu;
    private Date kunci_periode_transaksi;
    private Boolean inventori_tracking;
    private Character metode_perhitungan_persediaan;
    private Character nilai_harga_jual_barang;
    private String up_penagihan;
    private String alamat_penagihan;
    private String kota_penagihan;
    private String kode_pos_penagihan;
    private String propinsi_penagihan;
    private String id_negara;
    private String up_pengiriman;
    private String alamat_pengiriman;
    private String kota_pengiriman;
    private String kode_pos_pengiriman;
    private String propinsi_pengiriman;
    private String id_negara_pengiriman;
    private String telpon;
    private String fax;
    private String mobile;
    private String email;
    private String website;
    private MataUang kode_mata_uang;

}
