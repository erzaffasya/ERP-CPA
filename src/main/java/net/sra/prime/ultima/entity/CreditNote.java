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
public class CreditNote implements java.io.Serializable {

    private Integer id;
    private String nomor;
    private Date tanggal;
    private String id_customer;
    private String customer;
    private String keterangan;
    private String kode_user;
    private Double grandtotal;
    private Double dpp;
    private Double biayalain;
    private Double total;
    private Double total_ppn;
    private Boolean is_ppn;
    private Double total_discount;
    private Date due_date;
    private String alamat;
    private Character status;
    private String no_do;
    private String no_po;
    private Integer top;
    private String telp;
    private Double persendiskon;
    private Double diskonrp;
    private String faktur;
    private String no_invoice;
    private String id_gudang;
    private String id_salesman;
    private String salesman;
    private String pesan;
    private String no_cancel;
    private Date tgl_cancel;
    private Character jenis;
}
