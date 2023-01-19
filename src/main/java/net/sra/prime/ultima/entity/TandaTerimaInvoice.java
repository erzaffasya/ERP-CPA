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
public class TandaTerimaInvoice implements java.io.Serializable {
    
    private Integer id;
    private String nomor;
    private String id_customer;
    private String customer;
    private String create_by;
    private String create_name;
    private Date create_date;
    private String modified_by;
    private String modified_name;
    private Date modified_date;
    private Character status;
    private String received_by;
    private Date received_date;
    private Date tanggal;
    private Double total;
    private String nomor_invoice;
    private Date tgl_kirim;
    private String courier;
    private Date tgl_terima;
    private String penerima;
    private String resi;
}
