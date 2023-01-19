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

public class PoDetail extends Po implements java.io.Serializable {

    private String nomor_po;
    private Integer urut;
    private String id_barang;
    private Double qty;
    private Double qtybesar;
    private Double harga;
    private String id_satuan_kecil;
    private String nama_barang;
    private Double total;
    private String satuan_kecil;
    private String satuan_besar;
    private Double diambil;
    private Double diambilbesar;
    private Double sisa;
    private Double sisabesar;
    private Double diskonpersen;
    private Double diskonrp;
    private Double invoice;
    private Integer id;
}
