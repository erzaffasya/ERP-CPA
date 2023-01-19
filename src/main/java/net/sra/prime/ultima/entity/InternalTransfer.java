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

public class InternalTransfer implements java.io.Serializable {

    private String nomor;
    private Date tanggal;
    private String pengirim;
    private String penerima;
    private String approve_by;
    private Character status;
    private Date tanggal_terima;
    private Date tanggal_kirim;
    private String id_gudang_asal;
    private String id_gudang_tujuan;
    private String gudang_asal;
    private String gudang_tujuan;
    private String nomor_io;
    private String keterangan;
    private String driver;
    private String nama_pengirim;
    private String nama_penerima;
    private String telpon;
    private String kantor;
    private String sipb;
    private String jam_kirim;
    private String jam_terima;
    private String no_cancel;
    private Date tgl_cancel;
    private String pesan;
    private Date create_date;
    private Date modified_date;
    private Date modified_date_io;
    private Boolean istransporter;
    private String idtransporter;
    private String transporter_name;
    private String transporter;
    private Double biaya;
    private Double totalLiter;
    private String id_jenis;
    private String nopol;
    private Integer id_kendaraan;
    private Double total;
}
