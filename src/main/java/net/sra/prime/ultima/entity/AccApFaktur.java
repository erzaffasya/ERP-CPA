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
 * @author Hairian
 */
@Mapper
@Getter
@Setter
public class AccApFaktur implements java.io.Serializable {

    private String ap_number;
    private Date ap_date;
    private String vendor_code;
    private Double amount;
    private Date due_date;
    private String notes;
    private String reff;
    private Date reff_date;
    private String id_perusahaan;
    private String po_number;
    private Character status;
    private Double bayar;
    private Double ppn;
    private Double pph;
    private Double total;
    private Date tgl_invoice;
    private Date receive_date;
    private Integer top;
    private String supplier;
    private Double materai;
    private Double sumtotal;
    private Integer umur_invoice;
    private Integer umur_overdue;
}
