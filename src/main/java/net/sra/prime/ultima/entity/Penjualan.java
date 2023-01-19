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
public class Penjualan implements java.io.Serializable {

    private String no_penjualan;
    private Date tanggal;
    private String id_customer;
    private String kepada;
    private String keterangan;
    private String kode_user;
    private Double total_harga;
    private Double total_bayar;
    private Double total;
    private Double total_ppn;
    private Boolean is_ppn;
    private String id_perusahaan;
    private Double total_discount;
    private Date tgl_jatuh_tempo;
    private Double jatuh_tempo;
    private Date tgl_terimainvoice;
    private String id_term;
    private String id_salesman;
    private String salesman;
    private String kode_mata_uang;
    private String id_gudang;
    private String alamat;
    private Integer id_metode_pembayaran;
    private Double total_pajak;
    private Double potongan_pajak;
    private Character status;
    private String id_lama;
    private Double dpp;
    private Double grandtotal;
    private Double persendiskon;
    private Double diskonrp;
    private String customer;
    private String no_do;
    private String referensi;
    private Integer top;
    private String bank;
    private Boolean jenis_bank;
    private Integer durasi;
    private String no_penjualan_lama;
    private String gudang;
    private Boolean is_invoice;
    private Double biayalain;
    private String faktur;
    private String pesan;
    private Date tgl_cancel;
    private String no_kwitansi;
    private Character jenis;
    private String cn;
    private Date due_date;
    private Date create_date;
    private String no_dos;
    private Boolean isbank;
    private Integer idbank;
}
