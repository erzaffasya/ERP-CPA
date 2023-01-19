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
public class AccProposalApDetail implements java.io.Serializable {

    private String no_proposal;
    private String ap_number;
    private Integer urut;
    private String vendor_code;
    private Date duedate;
    private Double dpp;
    private Double ppn;
    private Double pph;
    private Double total;
    private Double jumlah_tagihan;
    private String notes;
    private String supplier;
    private String no_invoice;
    private Character st;
}
