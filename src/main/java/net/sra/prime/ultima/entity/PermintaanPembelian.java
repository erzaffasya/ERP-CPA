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

public class PermintaanPembelian implements java.io.Serializable {

    private String no_pp;
    private Date tanggal;
    private String dibuat;
    private String disetujui;
    private String diterima;
    private String keterangan;
    private String id_perusahaan;
    private String id_lama;
    private String dibuat_nama;
    private Boolean cheked;
    private Boolean approved;
    private String checked_name;
    private Date tgl_disetujui;
    private Date tgl_cheked;
    private Date tgl_approved;
    private String nama_cheked;
    private String nama_approved;
    private String jabatan;
    private String jabatan_cheked;
    private String jabatan_approved;
    private Boolean status_cheked;
    private Boolean status_approved;
    private Boolean status_dibuat;
    private String pesan;
    private String approved2;
    private String nama_approved2;
    private String jabatan_approved2;
    private Date tgl_approved2;
    private Boolean status_approved2;
    private String departemen;
    private Date awal;
    private Date akhir;
    private String kantor;
    private Double total;
    private Character jenis;
    private String nomor_po;
    private Date create_date;
    private String spv;
    private String spv_name;
    private String spv_date;
    private String spv_jabatan;
    private Boolean spv_status;
}
