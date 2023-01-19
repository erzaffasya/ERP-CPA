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
public class Po implements java.io.Serializable {

    private String nomor_po;
    private String no_intruksi_po;
    private Date tanggal;
    private String id_supplier;
    private String kepada;
    private String keterangan;
    private String kode_user;
    private Double total;
    private Double total_ppn;
    private Boolean is_ppn;
    private String id_perusahaan;
    private Double total_discount;
    private String id_term;
    private String kode_mata_uang;
    private String shipto;
    private Boolean status;
    private String telpon;
    private String hp;
    private String email;
    private String referensi;
    private Date tanggal_referensi;
    private String deliverytime;
    private Integer top;
    private Double dpp;
    private Double grandtotal;
    private Double persendiskon;
    private String nama_supplier;
    private String id_lama;
    private boolean jenis;
    private String id_gudang;
    private String id_customer;
    private Double batas_kredit;
    private String no_kontrak;
    private String no_pp;
    private Integer kontak;
    private String shiptonumber;
    private String customer;
    private Boolean is_pph;
    private Double total_pph;
    private String jenis_po;
    private Boolean is_purchasing;
    private Boolean is_approve;
    private String purchasing;
    private String director;
    private String nama_purchasing;
    private String nama_checked1;
    private String nama_director;
    private String jabatan;
    private String jabatan_approve;
    private String pesan;
    private String gudang;
    private Integer id;
    private Integer id_intruksi_po;
    private String no_forecast;
    private String checked1;
    private Boolean is_checked1;
    private Date create_date;
    private Date purchasing_date;
    private Date checked1_date;
    private Date approve_date;
}
