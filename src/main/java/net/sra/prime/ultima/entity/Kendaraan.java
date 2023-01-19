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
public class Kendaraan implements java.io.Serializable {

    private Integer id;
    private String id_jenis;
    private String jenis;
    private String nopol;
    
    private String no_bpkb;
    private String no_stnk;
    private String no_uji;
    private Date tgl_jatuh_tempo;
    private String no_rangka;
    private String no_mesin;
    private String merk;
    private Integer thn_perakitan;
    private Integer thn_pembelian;
    private String warna;
    private String no_armada;
    private String odometer;
    private Integer no_gps;
    private String nama_driver;
    private String foto_unit;

}
