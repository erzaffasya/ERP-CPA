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
public class AccValue {

    private String years;
    private Integer account;
    private String id_perusahaan;
    private Double DB0;
    private Double DB1;
    private Double DB2;
    private Double DB3;
    private Double DB4;
    private Double DB5;
    private Double DB6;
    private Double DB7;
    private Double DB8;
    private Double DB9;
    private Double DB10;
    private Double DB11;
    private Double DB12;
    private Double DB13;
    private Double CR0;
    private Double CR1;
    private Double CR2;
    private Double CR3;
    private Double CR4;
    private Double CR5;
    private Double CR6;
    private Double CR7;
    private Double CR8;
    private Double CR9;
    private Double CR10;
    private Double CR11;
    private Double CR12;
    private Double CR13;
    private Double debit;
    private Double credit;
    private String nama_account;
    private Integer id_account;
    private Integer level;
}
