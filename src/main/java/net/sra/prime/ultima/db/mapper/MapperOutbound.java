/*
 * Copyright 2016 JoinFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") throws RuntimeException;
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
package net.sra.prime.ultima.db.mapper;

import java.util.Date;
import java.util.List;
import net.sra.prime.ultima.db.provider.ProviderOutbound;
import net.sra.prime.ultima.entity.Outbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperOutbound {

    @SelectProvider(type = ProviderOutbound.class, method = "SelectAll")
    public List<Outbound> selectAll(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderOutbound.class, method = "SelectAllXls")
    public List<Outbound> selectAllXls(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang
    ) throws RuntimeException;
    
    @SelectProvider(type = ProviderOutbound.class, method = "SelectAllIn")
    public List<Outbound> selectAllIn(
            @Param("awal") Date awal,
            @Param("akhir") Date akhir,
            @Param("id_gudang") String id_gudang,
            @Param("id_barang") String id_barang
    ) throws RuntimeException;

    
}
