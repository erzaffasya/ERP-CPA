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
public class KlaimKaryawan implements java.io.Serializable {
    
    private Integer id;
    private Integer id_klaim;
    private String id_pegawai;
    private String nama_pegawai;
    private Date tanggal;
    private Double jumlah;
    private String keterangan;
    private String create_by;
    private Date create_date;
    private Date approve1_date;
    private Date approve2_date;
    private String approve1_by;
    private String approve2_by;
    private String approve1_name;
    private String approve2_name;
    private Character status;
}
