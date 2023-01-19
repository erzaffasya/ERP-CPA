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

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import net.sra.prime.ultima.entity.StokValue;
import org.apache.ibatis.annotations.Update;

/**
 *
 * @author Hairian
 */
@Mapper // Petunjuk kalau ini adalah Interface  Mapper
public interface MapperStokValue {

    @Select("SELECT a.*, b.nama_barang "
            + " FROM stok_value a "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.years=#{years} AND a.id_barang=#{id_barang} AND id_gudang=#{id_gudang}")
    public StokValue selectOneStokValue(@Param("years") String years, 
            @Param("id_barang") String id_barang, 
            @Param("id_gudang") String id_gudang) throws RuntimeException;
    
    
    @Select("SELECT a.*, b.nama_barang FROM stok_value a  "
            + " INNER JOIN barang b ON a.id_barang=b.id_barang "
            + " WHERE a.years=#{years} AND  id_gudang=#{id_gudang}"
            + " ORDER BY b.id_barang")
    public List<StokValue> selectOneYearTutuptahun(@Param("years") String years,
            @Param("id_gudang") String id_gudang) throws RuntimeException;
    
    
    @Insert("INSERT INTO stok_value "
            + "(years, id_barang, id_gudang) "
            + " VALUES "
            + " (#{years}, #{id_barang}, #{id_gudang})")
    public int insertSaldoAwal(StokValue stokValue) throws RuntimeException;
    
    
    @Update("UPDATE stok_value set "
            + "db13=db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12, "
            + "cr13=cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12 "
            + "WHERE years=#{years}")
    public int updateSaldoAkhirAll(@Param("years") String years) throws RuntimeException;
    
    @Update("UPDATE stok_value set "
            + "db0=#{db0}, "
            + "cr0=#{cr0} "
            + "WHERE years=#{years} AND id_gudang=#{id_gudang} AND id_barang=#{id_barang}")
    public int updateSaldoAwal(StokValue stokValue) throws RuntimeException;
   
}
