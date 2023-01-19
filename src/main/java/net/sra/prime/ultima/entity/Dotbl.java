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
public class Dotbl implements java.io.Serializable {

    private String nomor;
    private Date tanggal,create_date,modified_date;
    private String id_customer;
    private String kepada;
    private String keterangan;
    private String kode_user;
    private String id_salesman;
    private String salesman;
    private String id_gudang;
    private String alamat;
    private Character status;
    private String id_lama;
    private String customer;
    private String telpon;
    private String hp;
    private String email;
    private String no_pl;
    private Date tgl_pl;
    private String po;
    private String id_pegawai_log,create_name;
    private Date tgl_keluar;
    private Date tgl_transporter;
    private String driver;
    private String received_name;
    private Date received_date;
    private String referensi;
    private Double grandtotal;
    private Integer top;
    private String kode_mata_uang;
    private Double persendiskon;
    private Boolean is_ppn;
    private String no_so;
    private String transporter_time;
    private String lic_plate;
    private String sipb;
    private Date tgl_tranporter;
    private Integer id_pengiriman;
    private String gudang;
    private String pesan;
    private String tgl_cancel;
    private Double shippingcosts;
    private Character jns_transporter;
    private String transporter;
    private Date return_date;
    private Date received_date_posting;
    private String received_posting;
    private String received_posting_name;
    private Integer days;
    private Double leadtime;
    private Boolean istransporter;
    private String idtransporter;
    private String transporter_name;
    private Date tgl_sampai;
    private Date tgl_invoice;
    private Double upd; 
    private String origin;
    private String destination;
}
