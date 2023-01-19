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

import net.sra.prime.ultima.db.mapper.*;
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
public class Customer implements java.io.Serializable {

    private String id_kontak;
    private String customer;
    private String no_npwp;
    private String telepon;
    private String fax;
    private String email;
    private String kontak;
    private String alamat_penagihan;
    private Integer id_kategori_customer;
    private String nama_bank;
    private Boolean kena_pajak;
    private String website;
    private Boolean isaktif;
    private Double batas_kredit;
    private String id_lama;
    private String id_sektor;
    private String sektor;
    private String alamat_npwp;
    private Integer top;
    private String nama_marketing;
    private String nama_barang;
    private String id_sales;
    private String salesman;
    private String alamat_kirim;
}
