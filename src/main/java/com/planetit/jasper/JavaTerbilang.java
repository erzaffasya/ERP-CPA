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
package com.planetit.jasper;

/**
 *
 * @author hairian
 */
import net.sf.jasperreports.engine.JRDefaultScriptlet;

public class JavaTerbilang extends JRDefaultScriptlet{
 
    String[] nomina={"","Satu","Dua","Tiga","Empat","Lima","Enam",
                         "Tujuh","Delapan","Sembilan","Sepuluh","Sebelas"};
 
    public String terbilang(double angka)
    {
        if(angka<12)
        {
          return nomina[(int)angka];
        }
        
        if(angka>=12 && angka <=19)
        {
            return nomina[(int)angka%10] +" Belas ";
        }
        
        if(angka>=20 && angka <=99)
        {
            return nomina[(int)angka/10] +" Puluh "+nomina[(int)angka%10];
        }
        
        if(angka>=100 && angka <=199)
        {
            return "Seratus "+ terbilang(angka%100);
        }
        
        if(angka>=200 && angka <=999)
        {
            return nomina[(int)angka/100]+" Ratus "+terbilang(angka%100);
        }
        
        if(angka>=1000 && angka <=1999)
        {
            return "Seribu "+ terbilang(angka%1000);
        }
        
        if(angka >= 2000 && angka <=999999)
        {
            return terbilang((long)angka/1000)+" Ribu "+ terbilang(angka%1000);
        }
        
        if(angka >= 1000000 && angka <=999999999)
        {
            return terbilang((long)angka/1000000)+" Juta "+ terbilang(angka%1000000);
        }
        
        if(angka >= 1000000000)
        {
            return terbilang((long)angka/1000000000)+" Milyar "+ terbilang(angka%1000000000);
        }
        
        return "";
    }

}
