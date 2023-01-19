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
public class Cuti implements java.io.Serializable {

    private Integer id;
    private String nama_cuti;
    private Boolean batasan;
    private Double saldo;
    private Character perbaharui;
    private Date tgl_perbaharui;
    private Character mulai_menggunakan;
    private Character persetujuan1;
    private Integer jabatan_persetujuan1;
    private Character persetujuan2;
    private Integer jabatan_persetujuan2;
    private Character persetujuan3;
    private Integer jabatan_persetujuan3;
    private Character persetujuan4;
    private Integer jabatan_persetujuan4;
    private Character jenis;
    private Integer master;
    private Boolean status;
    private String nama_master;
    private String jabatan;
    private String keterangan;
    private String keterangan_khusus;
    private Boolean all_employee;
}
