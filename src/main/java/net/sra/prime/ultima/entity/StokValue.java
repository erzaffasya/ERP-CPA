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
public class StokValue implements java.io.Serializable {

    private String id_barang;
    private String years;
    private String id_gudang;
    private Double db0;
    private Double db1;
    private Double db2;
    private Double db3;
    private Double db4;
    private Double db5;
    private Double db6;
    private Double db7;
    private Double db8;
    private Double db9;
    private Double db10;
    private Double db11;
    private Double db12;
    private Double db13;
    private Double cr0;
    private Double cr1;
    private Double cr2;
    private Double cr3;
    private Double cr4;
    private Double cr5;
    private Double cr6;
    private Double cr7;
    private Double cr8;
    private Double cr9;
    private Double cr10;
    private Double cr11;
    private Double cr12;
    private Double cr13;
    private String nama_barang;
    private String satuan_besar;
    private Double isi_satuan;
}
