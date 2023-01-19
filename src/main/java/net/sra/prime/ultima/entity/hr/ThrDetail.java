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
import net.sra.prime.ultima.entity.Pegawai;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author Hairian
 */
@Mapper
@Getter
@Setter
public class ThrDetail extends Pegawai implements java.io.Serializable {
    private Integer id_thr;
    private Integer year;
    private Integer month;
    private Integer day;
    private String date_desc;
    private Double value;
    
}
