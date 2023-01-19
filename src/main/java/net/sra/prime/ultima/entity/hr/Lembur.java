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
public class Lembur implements java.io.Serializable {

    private Integer id;
    private String nama;
    private Boolean kebijakan;
    private Double saldo;
    private Character persetujuan1;
    private Integer jabatan_persetujuan1;
    private Character persetujuan2;
    private Integer jabatan_persetujuan2;
    private String jabatan1;
    private String jabatan2;
    private Character persetujuan3;
    private Integer jabatan_persetujuan3;
    private Character persetujuan4;
    private Integer jabatan_persetujuan4;
    private String jabatan3;
    private String jabatan4;
    private String create_by;
    private String create_by_name;
    private Double biaya;
    private String jabatan;
    
}
