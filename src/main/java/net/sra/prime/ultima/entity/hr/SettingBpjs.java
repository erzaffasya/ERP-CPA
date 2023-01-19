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
public class SettingBpjs implements java.io.Serializable {

    private Integer id_jkk;
    private Double jkk;
    private Double jkm;
    private Double jht_perusahaan;
    private Double jht_pekerja;
    private Double jp_perusahaan;
    private Double jp_pekerja;
    private Double batas_jp;
    private Double kesehatan_perusahaan;
    private Double kesehatan_pekerja;
    private Double kesehatan_max;
    private Double umk;
    private Integer id;
    
}
