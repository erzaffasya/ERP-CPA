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

/**
 *
 * @author Hairian
 */
@Getter
@Setter
public class Outbound implements java.io.Serializable {

    private Date tanggal;
    private String id_barang;
    private String id_gudang;
    private String nama_barang;
    private String packing_number;
    private String po;
    private String no_do;
    private String tujuan;
    private Double qty;
    private Double qty_kecil;
    private String gudang;
    private String numbernya;
    private String transporter;
    private String lic_plate;
    private String driver;
    private Date received_date;
    private Integer days;
    private String received_name;
    private Date return_date;
    private Date tanggal_kirim;
    private String alamat;
    private Boolean status; 
    private Character statusDo;
    private Integer leadtime;
    private Integer leadtimereal;
    private Integer selisih;
    private Character jenis;
    private Character statusit;
    private Boolean statuspenerimaan;
    
}
