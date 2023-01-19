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
package net.sra.prime.ultima.db.mapper;

import com.google.common.base.Joiner;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderBarang {

    public String selectAll(Map<String, Object> parameters) {

        List<String> id_barang = (List<String>) parameters.get("id_barang");
        String idbarang = Joiner.on("','").join(id_barang);
        String QUERYNYA = "SELECT a.*,b.*,c.*,d.* FROM barang a "
            + " INNER JOIN decanting_mapping dm ON a.id_barang=dm.id_barang_to "
            + " INNER JOIN satuan_kecil b ON a.id_satuan_kecil=b.id_satuan_kecil "
            + " INNER JOIN satuan_besar d ON a.id_satuan_besar=d.id_satuan_besar "
            + " INNER JOIN kategori_barang c ON a.id_kategori_barang=c.id_kategori_barang "
            + " WHERE dm.id_barang IN ('"+idbarang+"') ";
        StringBuilder Query = new StringBuilder(QUERYNYA);
        return Query.toString();
    }

    
}
