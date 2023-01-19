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

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Syamsu Rizal Ali <syamsu.smansa.polban@gmail.com>
 */
@Getter
@Setter
public class InternalKantorCabang implements java.io.Serializable {

    private String id_kantor_cabang;
    private String id_perusahaan;
    private String nama;
    private Boolean ho;
    private String alamat;
    private Integer account_penjualan;
    private Integer account_hpp;
    private String telpon;
    private String fax;
    private String kontak;
    private String email;
    private Boolean aktif;
    private String id_lama;
    private String nmaccountpenjualan;
    private String nmaccounthpp;
    private String numbercode;
    private String id_region;
    private String region_name;
    private Integer account_persediaan;
    private String nmaccountpersediaan;
    private Integer account_kas;
}
