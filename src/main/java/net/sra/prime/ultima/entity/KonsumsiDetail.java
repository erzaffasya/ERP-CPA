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
public class KonsumsiDetail implements java.io.Serializable {

    private Integer id;
    private String id_barang;
    private Double saldo_awal;
    private Double pemakaian;
    private Double terima;
    private Double deviasi;
    private Double saldo_akhir;
    private Double ditagih;
    private String keterangan;
    private Integer urut;
    private String nama_barang;
    private String satuan_kecil;
    private String satuan_besar;
}
