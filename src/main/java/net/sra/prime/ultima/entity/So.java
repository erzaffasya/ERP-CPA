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
public class So implements java.io.Serializable {

    private String nomor;
    private Date tanggal;
    private String id_customer;
    private String kepada;
    private String keterangan;
    private String kode_user;
    private Double total;
    private Double total_ppn;
    private Boolean is_ppn;
    private Double total_discount;
    private String id_term;
    private String id_salesman;
    private String salesman;
    private String kode_mata_uang;
    private String id_gudang;
    private String alamat;
    private Character tipe_diskon;
    private Character nilai_harga;
    private Character status;
    private Double biaya_lain;
    private Double persendiskon;
    private String id_lama;
    private String customer;
    private Double dpp;
    private Double grandtotal;
    private String telpon;
    private String hp;
    private String email;
    private Integer top;
    private String deliverypoint;
    private Integer pov;
    private Boolean certificate;
    private String bank;
    private String syarat;
    private String no_penawaran;
    private String referensi;
    private Date tgl_ref;
    private String jenis;
    private Integer revisi_penawaran;
    private String gudang;
    private Boolean is_manajemen;
    private String pesan;
    private String tgl_cancel;
    private String user;
    private String no_pl;
    private Date create_pl;
    private Date send_pl;
    private String no_do;
    private Date tgl_do;
    private Date received_date;
    private String no_penjualan;
    private String pesanPersetujuan;
    private String sobefore;
    private Boolean stPersetujuan;
    private Date create_date;
    private Date modified_date;
}
