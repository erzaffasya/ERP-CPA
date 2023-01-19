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

public class HrPegawai implements java.io.Serializable {

    private Integer id_pegawai;
    private String nama_depan;
    private String nama_belakang;
    private String gelar_depan;
    private String gelar_belakang;
    private Character jenis_kelamin;
    private String tempat_lahir;
    private Date tanggal_lahir;
    private Integer id_agama;
    private Character status_perkawinan;
    private String alamat;
    private Integer id_provinsi;
    private Integer id_kota;
    private String kode_pos;
    private String hp;
    private String email;
    private String nik;
    private Date tanggal_bergabung;
    private String email_kantor;
    private Character id_tipe_staff;
    private Integer id_departemen;
    private String nama_kontakDarurat;
    private Character hubungan;
    private String nomor_kontak;
    private Integer id_bank;
    private String nama_rekening;
    private String no_rekening;
    private String npwp;
    private String bpjs_kesehatan;
    private String bpjs_ketenagakerjaan;
    private Character status;
    private String golongan_darah;
    private String nama_institusi_pendidikan;
    private String no_identitas;
    private Integer id_jabatan;
    private Integer id_kantor;
    private Integer pendidikan;

}
