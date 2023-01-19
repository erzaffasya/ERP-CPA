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
package net.sra.prime.ultima.db.provider;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author hairian
 */
public class ProviderAccGl {

    public String SelectAll(Map<String, Object> parameters) {

        String jurnal = (String) parameters.get("jurnal");
        Date awal = (Date) parameters.get("awal");
        Date akhir = (Date) parameters.get("akhir");
        Boolean status = (Boolean) parameters.get("status");
        String note = (String) parameters.get("note");
        StringBuilder Query = new StringBuilder("SELECT * FROM acc_gl_trans a WHERE a.journal_code=#{jurnal}");
        if (awal != null) {
            Query.append(" AND  a.gl_date >= #{awal} AND a.gl_date <= #{akhir}");
        }
        if (status != null) {
            Query.append(" AND  a.posting = #{status}");
        }
        if (note != null && !note.equals("")) {
            Query.append(" AND  LOWER(a.note) like  LOWER ('%' || #{note} || '%')");
        }
        Query.append(" ORDER BY a.gl_date DESC, a.gl_number DESC");
        return Query.toString();
    }

    public String UpdateAccValue(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("UPDATE acc_value  SET ");

        Integer nomor = (Integer) parameters.get("nomor");
        Double nilai = (Double) parameters.get("nilai");
        Integer account = (Integer) parameters.get("account");
        String years = (String) parameters.get("years");
        Character status = (Character) parameters.get("status");
        if (status.equals('d')) {
            switch (nomor) {
                case 0:
                    Query.append("db0=db0 +  #{nilai}");
                    break;
                case 1:
                    Query.append("db1=db1 + #{nilai}");
                    break;
                case 2:
                    Query.append("db2=db2 + #{nilai}");
                    break;
                case 3:
                    Query.append("db3=db3 + #{nilai}");
                    break;
                case 4:
                    Query.append("db4=db4 + #{nilai}");
                    break;
                case 5:
                    Query.append("db5=db5 + #{nilai}");
                    break;
                case 6:
                    Query.append("db6=db6 + #{nilai}");
                    break;
                case 7:
                    Query.append("db7=db7 + #{nilai}");
                    break;
                case 8:
                    Query.append("db8=db8 + #{nilai}");
                    break;
                case 9:
                    Query.append("db9=db9 + #{nilai}");
                    break;
                case 10:
                    Query.append("db10=db10 + #{nilai}");
                    break;
                case 11:
                    Query.append("db11=db11 + #{nilai}");
                    break;
                case 12:
                    Query.append("db12=db12 + #{nilai}");
                    break;
            }
        } else if (status.equals('c')) {
            switch (nomor) {
                case 0:
                    Query.append("cr0= cr0 + #{nilai}");

                    break;
                case 1:
                    Query.append("cr1=cr1 + #{nilai}");
                    break;
                case 2:
                    Query.append("cr2=cr2 + #{nilai}");
                    break;
                case 3:
                    Query.append("cr3=cr3 + #{nilai}");
                    break;
                case 4:
                    Query.append("cr4=cr4 + #{nilai}");
                    break;
                case 5:
                    Query.append("cr5=cr5 + #{nilai}");
                    break;
                case 6:
                    Query.append("cr6=cr6 + #{nilai}");
                    break;
                case 7:
                    Query.append("cr7=cr7 + #{nilai}");
                    break;
                case 8:
                    Query.append("cr8=cr8 + #{nilai}");
                    break;
                case 9:
                    Query.append("cr9=cr9 + #{nilai}");
                    break;
                case 10:
                    Query.append("cr10=cr10 + #{nilai}");
                    break;
                case 11:
                    Query.append("cr11=cr11 + #{nilai}");
                    break;
                case 12:
                    Query.append("cr12=cr12 + #{nilai}");
                    break;
            }
        }
        Query.append(" WHERE account = #{account} AND  years = #{years} ");
        return Query.toString();
    }

    public String UpdateSaldoAwal(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("UPDATE acc_value  SET ");

        Integer nomor = (Integer) parameters.get("nomor");
        Double nilai = (Double) parameters.get("nilai");
        Integer account = (Integer) parameters.get("account");
        String years = (String) parameters.get("years");
        Character status = (Character) parameters.get("status");
        if (status.equals('d')) {
            switch (nomor) {
                case 0:
                    Query.append("db0=#{nilai}");
                    break;
                case 1:
                    Query.append("db1=#{nilai}");
                    break;
                case 2:
                    Query.append("db2=#{nilai}");
                    break;
                case 3:
                    Query.append("db3=#{nilai}");
                    break;
                case 4:
                    Query.append("db4=#{nilai}");
                    break;
                case 5:
                    Query.append("db5=#{nilai}");
                    break;
                case 6:
                    Query.append("db6=#{nilai}");
                    break;
                case 7:
                    Query.append("db7=#{nilai}");
                    break;
                case 8:
                    Query.append("db8=#{nilai}");
                    break;
                case 9:
                    Query.append("db9=#{nilai}");
                    break;
                case 10:
                    Query.append("db10=#{nilai}");
                    break;
                case 11:
                    Query.append("db11=#{nilai}");
                    break;
                case 12:
                    Query.append("db12=#{nilai}");
                    break;
            }
        } else if (status.equals('c')) {
            switch (nomor) {
                case 0:
                    Query.append("cr0=#{nilai}");
                    break;
                case 1:
                    Query.append("cr1=#{nilai}");
                    break;
                case 2:
                    Query.append("cr2=#{nilai}");
                    break;
                case 3:
                    Query.append("cr3=#{nilai}");
                    break;
                case 4:
                    Query.append("cr4=#{nilai}");
                    break;
                case 5:
                    Query.append("cr5=#{nilai}");
                    break;
                case 6:
                    Query.append("cr6=#{nilai}");
                    break;
                case 7:
                    Query.append("cr7=#{nilai}");
                    break;
                case 8:
                    Query.append("cr8=#{nilai}");
                    break;
                case 9:
                    Query.append("cr9=#{nilai}");
                    break;
                case 10:
                    Query.append("cr10=#{nilai}");
                    break;
                case 11:
                    Query.append("cr11=#{nilai}");
                    break;
                case 12:
                    Query.append("cr12=#{nilai}");
                    break;
            }
        }
        Query.append(" WHERE account = #{account} AND  years = #{years} ");
        return Query.toString();
    }

    public String UpdateAccValuetoZero(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("UPDATE acc_value  SET ");

        Integer bulan = (Integer) parameters.get("bulan");
        String tahun = (String) parameters.get("tahun");
        switch (bulan) {
            case 1:
                Query.append("db1=0, cr1=0 ");
                break;
            case 2:
                Query.append("db2=0, cr2=0 ");
                break;
            case 3:
                Query.append("db3=0, cr3=0 ");
                break;
            case 4:
                Query.append("db4=0, cr4=0 ");
                break;
            case 5:
                Query.append("db5=0, cr5=0 ");
                break;
            case 6:
                Query.append("db6=0, cr6=0 ");
                break;
            case 7:
                Query.append("db7=0, cr7=0 ");
                break;
            case 8:
                Query.append("db8=0, cr8=0 ");
                break;
            case 9:
                Query.append("db9=0, cr9=0 ");
                break;
            case 10:
                Query.append("db10=0, cr10=0 ");
                break;
            case 11:
                Query.append("db11=0, cr11=0 ");
                break;
            case 12:
                Query.append("db12=0, cr12=0 ");
                break;
        }
        Query.append(" WHERE years = #{tahun} ");
        return Query.toString();
    }

    public String numSaldoAwal(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT SUM ");

        Integer bulan = (Integer) parameters.get("bulan");
        String tahun = (String) parameters.get("tahun");
        Integer awal = (Integer) parameters.get("awal");
        Integer akhir = (Integer) parameters.get("akhir");
        Character type = (Character) parameters.get("type");
        if (type.equals('D')) {
            switch (bulan) {
                case 1:
                    Query.append("(db0+db1-cr0-cr1)");
                    break;
                case 2:
                    Query.append("(db0+db1+db2-cr0-cr1-cr2)");
                    break;
                case 3:
                    Query.append("(db0+db1+db2+db3-cr0-cr1-cr2-cr3)");
                    break;
                case 4:
                    Query.append("(db0+db1+db2+db3+db4-cr0-cr1-cr2-cr3-cr4)");
                    break;
                case 5:
                    Query.append("(db0+db1+db2+db3+db4+db5-cr0-cr1-cr2-cr3-cr4-cr5)");
                    break;
                case 6:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6-cr0-cr1-cr2-cr3-cr4-cr5-cr6)");
                    break;
                case 7:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7)");
                    break;
                case 8:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7+db8-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8)");
                    break;
                case 9:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9)");
                    break;
                case 10:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10)");
                    break;
                case 11:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11)");
                    break;
                case 12:
                    Query.append("(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11-cr12)");
                    break;
            }
        }else{
            switch (bulan) {
                case 1:
                    Query.append("(cr0+cr1-db0-db1)");
                    break;
                case 2:
                    Query.append("(cr0+cr1+cr2-db0-db1-db2)");
                    break;
                case 3:
                    Query.append("(cr0+cr1+cr2+cr3-db0-db1-db2-db3)");
                    break;
                case 4:
                    Query.append("(cr0+cr1+cr2+cr3+cr4-db0-db1-db2-db3-db4)");
                    break;
                case 5:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5-db0-db1-db2-db3-db4-db5)");
                    break;
                case 6:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6-db0-db1-db2-db3-db4-db5-db6)");
                    break;
                case 7:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7-db0-db1-db2-db3-db4-db5-db6-db7)");
                    break;
                case 8:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8-db0-db1-db2-db3-db4-db5-db6-db7-db8)");
                    break;
                case 9:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9)");
                    break;
                case 10:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10)");
                    break;
                case 11:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11)");
                    break;
                case 12:
                    Query.append("(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11-db12)");
                    break;
            }
        }
        Query.append(" FROM acc_value WHERE years = #{tahun} AND account >= #{awal} AND account < #{akhir}");
       // System.out.println(Query);
        return Query.toString();
    }
    
    
    public String numSaldo(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT ");

        Integer bulan = (Integer) parameters.get("bulan");
        String tahun = (String) parameters.get("tahun");
        Integer awal = (Integer) parameters.get("awal");
        Integer akhir = (Integer) parameters.get("akhir");
        Character type = (Character) parameters.get("type");
        if (type.equals('D')) {
            switch (bulan) {
                case 1:
                    Query.append("COALESCE(SUM(db0+db1-cr0-cr1),0) AS db0, COALESCE(SUM(db1-cr1),0) AS db13 ");
                    break;
                case 2:
                    Query.append("COALESCE(SUM(db0+db1+db2-cr0-cr1-cr2),0) AS db0, COALESCE(SUM(db2-cr2),0) AS db13 ");
                    break;
                case 3:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3-cr0-cr1-cr2-cr3),0) AS db0, COALESCE(SUM(db3-cr3),0) AS db13 ");
                    break;
                case 4:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4-cr0-cr1-cr2-cr3-cr4),0) AS db0, COALESCE(SUM(db4-cr4),0) AS db13 ");
                    break;
                case 5:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5-cr0-cr1-cr2-cr3-cr4-cr5),0) AS db0, COALESCE(SUM(db5-cr5),0) AS db13 ");
                    break;
                case 6:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6-cr0-cr1-cr2-cr3-cr4-cr5-cr6),0) AS db0, COALESCE(SUM(db6-cr6),0) AS db13 ");
                    break;
                case 7:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7),0) AS db0, COALESCE(SUM(db7-cr7),0) AS db13 ");
                    break;
                case 8:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8),0) AS db0, COALESCE(SUM(db8-cr8),0) AS db13 ");
                    break;
                case 9:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9),0) AS db0, COALESCE(SUM(db9-cr9),0) AS db13 ");
                    break;
                case 10:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10),0) AS db0, COALESCE(SUM(db10-cr10),0) AS db13 ");
                    break;
                case 11:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11),0) AS db0, COALESCE(SUM(db11-cr11),0) AS db13 ");
                    break;
                case 12:
                    Query.append("COALESCE(SUM(db0+db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+12-cr0-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11-cr12),0) AS db0, COALESCE(SUM(db12-cr12),0) AS db13 ");
                    break;
            }
        }else{
            switch (bulan) {
                case 1:
                    Query.append("COALESCE(SUM(cr0+cr1-db0-db1),0) AS db0, COALESCE(SUM(cr1-db1),0) AS db13");
                    break;
                case 2:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2-db0-db1-db2),0) AS db0, COALESCE(SUM(cr2-db2),0AS db13)");
                    break;
                case 3:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3-db0-db1-db2-db3),0) AS db0, COALESCE(SUM(cr3-db3),0) AS db13");
                    break;
                case 4:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4-db0-db1-db2-db3-db4),0) AS db0, COALESCE(SUM(cr4-db4),0) AS db13");
                    break;
                case 5:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5-db0-db1-db2-db3-db4-db5),0) AS db0, COALESCE(SUM(cr5-db5),0) AS db13");
                    break;
                case 6:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6-db0-db1-db2-db3-db4-db5-db6),0) AS db0, COALESCE(SUM(cr6-db6),0) AS db13");
                    break;
                case 7:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7-db0-db1-db2-db3-db4-db5-db6-db7),0) AS db0, COALESCE(SUM(cr7-db7),0) AS db13");
                    break;
                case 8:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8-db0-db1-db2-db3-db4-db5-db6-db7-db8),0) AS db0, COALESCE(SUM(cr8-db8),0) AS db13");
                    break;
                case 9:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9),0) AS db0, COALESCE(SUM(cr9-db9),0) AS db13");
                    break;
                case 10:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10),0) AS db0, COALESCE(SUM(cr10-db10),0) AS db13");
                    break;
                case 11:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11),0) AS db0, COALESCE(SUM(cr11-db11),0) AS db13");
                    break;
                case 12:
                    Query.append("COALESCE(SUM(cr0+cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12-db0-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11-db12),0) AS db0, COALESCE(SUM(cr12-db12),0) AS db13");
                    break;
            }
        }
        Query.append(" FROM acc_value WHERE years = #{tahun} AND account >= #{awal} AND account < #{akhir}");
    //    System.out.println(Query);
        return Query.toString();
    }
    
    public String numSaldoAwalNow(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT SUM ");

        Integer bulan = (Integer) parameters.get("bulan");
        String tahun = (String) parameters.get("tahun");
        Integer awal = (Integer) parameters.get("awal");
        Integer akhir = (Integer) parameters.get("akhir");
        Character type = (Character) parameters.get("type");
        if (type.equals('D')) {
            switch (bulan) {
                case 1:
                    Query.append("(db1-cr1)");
                    break;
                case 2:
                    Query.append("(db2-cr2)");
                    break;
                case 3:
                    Query.append("(db3-cr3)");
                    break;
                case 4:
                    Query.append("(db4-cr4)");
                    break;
                case 5:
                    Query.append("(db5-cr5)");
                    break;
                case 6:
                    Query.append("(db6-cr6)");
                    break;
                case 7:
                    Query.append("(db7-cr7)");
                    break;
                case 8:
                    Query.append("(db8-cr8)");
                    break;
                case 9:
                    Query.append("(db9-cr9)");
                    break;
                case 10:
                    Query.append("(db10-cr10)");
                    break;
                case 11:
                    Query.append("(db11-cr11)");
                    break;
                case 12:
                    Query.append("(db12-cr12)");
                    break;
            }
        }else{
            switch (bulan) {
                case 1:
                    Query.append("(cr1-db1)");
                    break;
                case 2:
                    Query.append("(cr2-db2)");
                    break;
                case 3:
                    Query.append("(cr3-db3)");
                    break;
                case 4:
                    Query.append("(cr4-db4)");
                    break;
                case 5:
                    Query.append("(cr5-db5)");
                    break;
                case 6:
                    Query.append("(cr61-db6)");
                    break;
                case 7:
                    Query.append("(cr7-db7)");
                    break;
                case 8:
                    Query.append("(cr8-db8)");
                    break;
                case 9:
                    Query.append("(cr9-db9)");
                    break;
                case 10:
                    Query.append("(cr110-db10)");
                    break;
                case 11:
                    Query.append("(cr11-db11)");
                    break;
                case 12:
                    Query.append("(cr12-db12)");
                    break;
            }
        }
        Query.append(" FROM acc_value WHERE years = #{tahun} AND account >= #{awal} AND account < #{akhir}");
       // System.out.println(Query);
        return Query.toString();
    }
    
    
    public String numArusKas(Map<String, Object> parameters) {
        StringBuilder Query = new StringBuilder("SELECT SUM ");

        Integer bulan = (Integer) parameters.get("bulan");
        String tahun = (String) parameters.get("tahun");
        Integer awal = (Integer) parameters.get("awal");
        Integer akhir = (Integer) parameters.get("akhir");
        Character type = (Character) parameters.get("type");
        if (type.equals('D')) {
            switch (bulan) {
                case 1:
                    Query.append("(db1-cr1)");
                    break;
                case 2:
                    Query.append("(db1+db2-cr1-cr2)");
                    break;
                case 3:
                    Query.append("(db1+db2+db3-cr1-cr2-cr3)");
                    break;
                case 4:
                    Query.append("(db1+db2+db3+db4-cr1-cr2-cr3-cr4)");
                    break;
                case 5:
                    Query.append("(db1+db2+db3+db4+db5-cr1-cr2-cr3-cr4-cr5)");
                    break;
                case 6:
                    Query.append("(db1+db2+db3+db4+db5+db6-cr1-cr2-cr3-cr4-cr5-cr6)");
                    break;
                case 7:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7-cr1-cr2-cr3-cr4-cr5-cr6-cr7)");
                    break;
                case 8:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7+db8-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8)");
                    break;
                case 9:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7+db8+db9-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9)");
                    break;
                case 10:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7+db8+db9+db10-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10)");
                    break;
                case 11:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11)");
                    break;
                case 12:
                    Query.append("(db1+db2+db3+db4+db5+db6+db7+db8+db9+db10+db11+db12-cr1-cr2-cr3-cr4-cr5-cr6-cr7-cr8-cr9-cr10-cr11-cr12)");
                    break;
            }
        }else{
            switch (bulan) {
                case 1:
                    Query.append("(cr1-db1)");
                    break;
                case 2:
                    Query.append("(cr1+cr2-db1-db2)");
                    break;
                case 3:
                    Query.append("(cr1+cr2+cr3-db1-db2-db3)");
                    break;
                case 4:
                    Query.append("(cr1+cr2+cr3+cr4-db1-db2-db3-db4)");
                    break;
                case 5:
                    Query.append("(cr1+cr2+cr3+cr4+cr5-db1-db2-db3-db4-db5)");
                    break;
                case 6:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6-db1-db2-db3-db4-db5-db6)");
                    break;
                case 7:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7-db1-db2-db3-db4-db5-db6-db7)");
                    break;
                case 8:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8-db1-db2-db3-db4-db5-db6-db7-db8)");
                    break;
                case 9:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9-db1-db2-db3-db4-db5-db6-db7-db8-db9)");
                    break;
                case 10:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10)");
                    break;
                case 11:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11)");
                    break;
                case 12:
                    Query.append("(cr1+cr2+cr3+cr4+cr5+cr6+cr7+cr8+cr9+cr10+cr11+cr12-db1-db2-db3-db4-db5-db6-db7-db8-db9-db10-db11-db12)");
                    break;
            }
        }
        Query.append(" FROM acc_value WHERE years = #{tahun} AND account >= #{awal} AND account < #{akhir}");
       // System.out.println(Query);
        return Query.toString();
    }
    
}
