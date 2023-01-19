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

public class PembayaranHutang implements java.io.Serializable {

    private String no_pembayaran_hutang;
    private Date tanggal;
    private String id_supplier;
    private String keterangan;
    private String kode_user;
    private Double total_tagihan;
    private Double total_discount;
    private Double total_bayar;
    private Double total_denda;
    private String is_app;
    private Integer account_bayar;
    private String id_perusahaan;
    private String id_kantor;
    private String supplier;
    private Character status;
}
