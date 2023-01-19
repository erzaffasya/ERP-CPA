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

public class PenerimaanGudang implements java.io.Serializable {

    private String no_penerimaan;
    private Date tgl_penerimaan;
    private String id_supplier;
    private String id_gudang;
    private String keterangan;
    private String alamat;
    private Boolean status;
    private String id_lama;
    private String nama_supplier;
    private String nomor_po;
    private Date tanggal_po;
    private String referensi;
    private Date tanggal_referensi;
    private String id_perusahaan;
    private String gudang;
    private String no_cancel;
    private Date tgl_cancel;
    private String pesan;
    private String create_by;
    private String create_name;
    private String modified_by;
    private String modified_name;
    private Date create_date;
    private Date modified_date;
}
