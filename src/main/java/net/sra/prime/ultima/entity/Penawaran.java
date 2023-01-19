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
public class Penawaran {

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
    private String kode_mata_uang;
    private String id_gudang;
    private String alamat;
    private Character tipe_diskon;
    private Character nilai_harga;
    private Boolean status;
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
    private Integer revisi;
    private Boolean jenis_bank;
    private Integer id_kontak;
    private String id_kantor;
    private String pesan;
    private String dsm;
    private Character statussend;
    private String salesman;
    private String createby;
    private Character carabayar;
    private String newcustomer;
    private String no_invoice;
    
}
