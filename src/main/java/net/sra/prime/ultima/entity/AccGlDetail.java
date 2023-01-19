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
 * @author Syamsu Rizal Ali <syamsu.rizal.ali@outlook.com>
 */
@Mapper
@Getter
@Setter
public class AccGlDetail {

    private String Journal_code;
    private String gl_number;
    private Date gl_date;
    private Integer id_account;
    private String account;
    private Integer line;
    private String keterangan;
    private Double value;
    private Boolean is_debit;
    private Boolean posted;
    private String note;
    private String reference;
    private Double debit;
    private Double credit;
    private Double saldo;
}
