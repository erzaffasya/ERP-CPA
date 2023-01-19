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
public class Packinglist implements java.io.Serializable {

    private String nomor;
    private Date tanggal;
    private String id_customer;
    private String kepada;
    private String keterangan;
    private String kode_user;
    private String id_perusahaan;
    private String id_salesman;
    private String id_gudang;
    private String alamat;
    private Character status;
    private String id_lama;
    private String customer;
    private String telpon;
    private String hp;
    private String email;
    private Integer top;
    private String deliverypoint;
    private String no_so;
    private String referensi;
    private String nomor_do;
    private Date tgl_ref; 
    private Date tanggal_so;
    private Date tanggal_do;
    private String mobile;
    private String tlp;
    private String alamatso;
    private String gudang;
    private String pesan;
    private String no_cancel;
    private Date tgl_cancel;
    private String create_by;
    private Date create_date;
    private String modified_by;
    private Date modified_date;
    private String modified_name;
}
