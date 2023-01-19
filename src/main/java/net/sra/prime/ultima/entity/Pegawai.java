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
public class Pegawai implements java.io.Serializable {

    private String id_pegawai;
    private String nama;
    private Integer id_jabatan;
    private String hp;
    private String email;
    private String jabatan;
    private String departemen;
    private String id_kantor;
    private String kantor;
    private Boolean status;
    private Integer id_departemen;
    private String ttd;
    private String nama_panggilan;
    private String nik_ktp;
    private String tempat_lahir;
    private Date tanggal_lahir;
    private Integer id_agama;
    private String agama;
    private Character jenis_kelamin;
    private Character status_pernikahan;
    private String golongan_darah;
    private String hp2;
    private String hp3;
    private String wa;
    private String alamat_ktp;
    private String kodepos_ktp;
    private String alamat_domisili;
    private String kodepos_domisili;
    private String provinsi_ktp;
    private String kabupaten_ktp;
    private String nama_provinsi_ktp;
    private String nama_kabupaten_ktp;
    private String tanggal_berakhir_identitas;
    private Boolean alamatsama;
    private String provinsi_domisili;
    private String kabupaten_domisili;
    private String nama_provinsi_domisili;
    private String nama_kabupaten_domisili;
    private String tinggi_badan;
    private String berat_badan;
    private Integer anak_ke;
    private String nip;
    private Character statuskaryawan;
    private Date mulai_bekerja;
    private Boolean hd;
    private Integer absen;
    private String level;
    private String golongan;
    private String ttl;
    private String area;
    private Integer id_jabatan_new;
    private Integer id_departemen_new;
    private String id_kantor_new;
    private Date pkwt;
    private Integer id_ptkp;
    private String ptkp;
    private Double nominal;
    private String atasan_langsung;
    private String nama_atasan;
    private Date probation_akhir;
    private Date probation_awal;
    private String pemilik_rekening;
    private String nomorrekening;
    private String fotonya;
    private Date aktifasiumt;
    private Date aktifasikehadiran;
    private Boolean statusumt;
    private Boolean statuskehadiran;
    private Date tgl_resign;
    private Boolean isdriver;
    /// dari tabel bpjs
    
    private String bpjs_tk;
    private String bpjs_kes;
    private String prudential;
    private String allianz;
    private Double jht_perusahaan;
    private Double jht_pekerja;
    private Double jkk;
    private Double jkm;
    private Double jp_perusahaan;
    private Double jp_pekerja;
    private Double kesehatan_perusahaan;
    private Double kesehatan_pekerja;
    private Double iuran_prudential;
    private Double iuran_allianz;
    private Double iuran_prudential_perusahaan;
    private Double iuran_allianz_perusahaan;
    //// sementara
    
    ///tabel pegawai gaji
    
    private Double gaji_pokok;
    private Double tunjangan_jabatan;
    private Double tunjangan_komunikasi;
    private Double tunjangan_bbm;
    private Double umt;
    private Double insentif_kehadiran;
    private Double insentif_kpi;
    private Double pph;
    private Character id_jenis_pph;
    private String jenis_pph;
    private Boolean headdepartemen;
    
}
