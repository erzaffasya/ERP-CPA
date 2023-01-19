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
package net.sra.prime.ultima.entity.hr;

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
public class CutiKaryawan implements java.io.Serializable {

    private Integer id;
    private Integer id_cuti;
    private Integer id_master;
    private Integer tipe_cuti;
    private String id_pegawai;
    private String nama_pegawai;
    private String nama_cuti;
    private Character waktu_cuti;
    private Date tanggal_awal;
    private Character jenis_awal;
    private Date tanggal_akhir;
    private Character jenis_akhir;
    private String telpon;
    private String alasan;
    private Character status;
    private String persetujuan1;
    private String persetujuan2;
    private String create_by;
    private String create_by_name;
    private Date create_date;
    private Date tgl_persetujuan1;
    private Date tgl_persetujuan2;
    private Integer id_jabatan;
    private Integer id_cuti_sub;
    private String nama_sub_cuti;
    private String tanggal;
    private Double lama;
    private String keterangan;
    private String tanggal_cuti;
    private String nama_tipe;
    private String keterangan_status;
    private String nik;
    private String jabatan;
    private String departemen;
    private String note;
}
