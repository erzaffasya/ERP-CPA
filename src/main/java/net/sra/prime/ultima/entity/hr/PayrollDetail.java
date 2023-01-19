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
public class PayrollDetail implements java.io.Serializable {

    private Integer id;
    private String id_pegawai;
    private String nama_pegawai;
    private Double gaji_pokok;
    private Double tunjangan_jabatan;
    private Double overtime;
    private Double insentif_penjualan;
    private Double insentif_kehadiran;
    private Double insentif_produktifitas;
    private Double umt;
    private Double tunjangan_komunikasi;
    private Double tunjangan_bbm;
    private Double thr;
    private Double komisi_penjualan;
    private Double asuransi_komersil;
    private Double prudential;
    private Double alianz;
    private Double pph21_upah;
    private Double pph21_komisi;
    private Double pph21_thr;
    private Double potongan_absensi;
    private Double hutang_kta;
    private Double hutang_dll;
    private Double total_gaji;
    private Double total_potongan;
    private Double thp;
    private String nip;
    private Double bpjs_kes_perusahaan;
    private Double bpjs_kes_pekerja;
    private Double jht_perusahaan;
    private Double jht_pekerja;
    private Double jp_perusahaan;
    private Double jp_pekerja;
    private Double jkk;
    private Double jkm;
    private Double bpjs_ketenagakerjaan;
    private Double allianz_perusahaan;
    private Double prudential_perusahaan;
    private String nomorrekening;
    private String pemilik_rekening;
    private Double tunjangan_kesehatan;
}

