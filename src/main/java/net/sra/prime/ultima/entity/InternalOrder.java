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

public class InternalOrder implements java.io.Serializable {

    private String nomor_io;
    private Date tanggal;
    private String id_kantor;
    private String ref_no;
    private Character order_status;
    private String kontak;
    private String nama_kontak;
    private String telpon;
    private String email;
    private String id_gudang_asal;
    private String id_gudang_tujuan;
    private String gudang_asal;
    private String gudang_tujuan;
    private String id_perusahaan;
    private String kantor;
    private Character status;
    private String approve;
    private String approve_name;
    private Date approve_tanggal;
    private String dibuat;
    private String dibuat_nama;
    private String jabatan;
    private String jabatan_approved;
    private String pesan;
    private String tgl_cancel;
    private Date create_by;
    private String send_by;
    private String send_by_name;
    private String send_by_jabatan;
    private Date send_date;
    private Integer id_ip;
    private String nomor_ip;
    private Date tanggal_ip;
    private String nomor_it;
    private Date create_it;
    private Date approved_it;
}
