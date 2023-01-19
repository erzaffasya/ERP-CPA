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

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author Syamsu
 */
@Mapper
@Getter
@Setter
public class Gudang implements java.io.Serializable {

    private String id_gudang;
    private String gudang;
    private String id_kontak;
    private String nama_kontak;
    private String telpon;
    private String alamat;
    private String id_lama;
    private String id_kantor;
    private String id_perusahaan;
    private String inisial;
    private String kantor;
    private String parent;
    private Integer account_hpp;
    private Integer account_persediaan;
    private String nmaccounthpp;
    private String nmaccountpersediaan;
    private Boolean blind;
    private Boolean stokopname;
    private String dsm;
    private String dsm_name;
}
