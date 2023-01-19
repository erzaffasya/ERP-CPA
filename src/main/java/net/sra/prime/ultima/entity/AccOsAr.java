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
public class AccOsAr implements java.io.Serializable {

    private String customer_code;
    private Double dpp;
    private Double ppn;
    private Double total;
    private Integer umur;
    private Integer umuroverdue;
    private Double day1;
    private Double day2;
    private Double day3;
    private Double day4;
    private Double day5;
    private Double day6;
    private String customer;
    private String invoice_number;
    private Date invoice_date;
    private Date duedate;
    private String cabang;
    private String marketing;
    private String nomor_po;
    private String area;
    private String kwitansi;
    private String ldo;
    private Integer top;
    private Double payment;
    private Date payment_date;
    private Double os;
    
}
