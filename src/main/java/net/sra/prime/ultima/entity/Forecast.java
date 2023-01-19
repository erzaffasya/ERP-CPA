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
 * @author Hairian Nur <hairian@planet-it.co.id>
 */
@Mapper
@Getter
@Setter
public class Forecast {

    private String no_forecast;
    private String id_gudang;
    private Date tanggal;
    private Integer triwulan;
    private String keterangan;
    private String id_region;
    private String id_lama;
    private String gudang;
    private String lb1;
    private String lb2;
    private String lb3;
    private Integer id;
    private Character status;
    private Integer id_forecast;
    private String statusInIp;
    private String no_po;
    private String create_by;
    private String create_name;
    private String pesan;
    private String tgl_cancel;
    private String user_cancel;
    private String no_intruksi_po;
    private Date tgl_ip;
}
