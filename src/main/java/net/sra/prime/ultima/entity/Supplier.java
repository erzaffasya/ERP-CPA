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
import net.sra.prime.ultima.db.mapper.*;
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
public class Supplier implements java.io.Serializable {

    private String id;
    private String supplier;
    private String npwp;
    private String telepon;
    private String fax;
    private String email;
    private String alamat;
    private Integer id_kategori_supplier;
    private String bank;
    private Boolean kena_pajak;
    private String website;
    private Boolean isaktif;
    private Double batas_kredit;
    private Integer top;
    private String alamat_npwp;
    private String id_lama;
    private Integer account_hutang;
    private String account;
    private Date input_date;
    private Date last_update;
    private String user_input;
    private String user_update;
}
