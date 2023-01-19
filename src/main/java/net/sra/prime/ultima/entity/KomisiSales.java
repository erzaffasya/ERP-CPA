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

public class KomisiSales implements java.io.Serializable {

    private String no_invoice;
    private Date tanggal_invoice;
    private String salesman;
    private String customer;
    private Double qty;
    private String satuan;
    private Double isi_satuan;
    private Double harga;
    private Double total;
    private Integer top;
    private Date due_date;
    private String no_pembayaran_piutang;
    private Date tanggal_or;
    private Integer umur_pembayaran;
    private Double total_bayar;
    private Double amount;
    private Double komisi;
    private Double volume;
    private String nama_barang;
    private Double hpp;
    private Double bottomprice;
    private Double hppshipping;
    private Double margin;
    private Double bayar;
    
}
