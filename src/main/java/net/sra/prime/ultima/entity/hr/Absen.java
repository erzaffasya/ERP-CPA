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
public class Absen implements java.io.Serializable {
    
    private Integer id_absen;
    private Date tanggal;
    private Date jam_masuk;
    private Date jam_keluar;
    private Date jam_break;
    
    private Date time_in;
    private Date time_out;
    private Date time_break;
    private String keterangan;
    private Character tipe_absen;
    private Integer id_jadwal_kerja;
    private String jadwal_kerja;
    private String id_pegawai;
    private Integer urut;
    private Date jadwal_masuk;
    private Date jadwal_keluar;
    private Integer lemburbefore;
    private Integer lemburafter;
    private String timeoff;
    private Integer overtimebefore;
    private Integer overtimeafter;
    private Integer break_time;
    private String break_time_desc;
    private Integer id_cuti;
    private String cuti;
    private String id_status_absen;
    private String status_absen;
    private String hourbefore;
    private String  hourafter;
    private Integer hari;
    private Double x1;
    private Double x2;
    private Double x3;
    private Double x4;
    private Double jx1;
    private Double jx2;
    private Double jx3;
    private Double jx4;
    private Double tx;
    private Character schedule;
    private String schedule_des;
    
}
