/*
 * Copyright 2023 JoinFaces.
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
 * @author erza
 */
@Mapper
@Getter
@Setter
public class PemakaianBarangConsumable implements java.io.Serializable  {
    
    private Integer id;
    private String nomor;
    private Date tanggal;
    private String keterangan;
    private String id_gudang;
    private Character status;
    private String created_by;
    private Date created_date;
    private Date modified_date;
    private String modified_by;
    
    
    private String nama_gudang;
    private String created_name;
    private String modified_name;
}
