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

public class Penerimaan implements java.io.Serializable {

    private String no_penerimaan;
    private Date tgl_penerimaan;
    private String id_supplier;
    private String id_gudang;
    private Date tgl_jatuh_tempo;
    private String keterangan;
    private String kode_user;
    private Double total;
    private String is_app;
    private Double total_bayar;
    private String is_invoice;
    private Date tgl_bayar;
    private Double total_discount;
    private String id_term;
    private String kode_mata_uang;
    private String alamat;
    private Character kode_diskon;
    private Character id_nilai_harga;
    private Double total_ppn;
    private Double potongan_pajak;
    private Character status;
    private Double persendiskon;
    private Boolean is_ppn;
    private String id_lama;
    private String nama_supplier;
    private Double dpp;
    private Double grandtotal;
    private Integer top;
    private String nomor_po;
    private String referensi;
    private Date tanggal_referensi;
    private Date tgl_pengiriman;
    private String is_lunas;
    private String id_perusahaan;
    private Boolean is_pph;
    private Double total_pph;
    private Double biaya_transportasi;
    private Double biaya_asuransi;
    private Double biaya_bongkar_muat;
    private Double biaya_bongkar_muat_eksternal;
    private String id_kantor;
    private Character jenis;
    private String no_faktur_pajak;
    private Double ppnpersen;
    private Double biayalain;
    private String no_pp;
    private Double pphpersen;
    private Double totalhutang;
    
}
