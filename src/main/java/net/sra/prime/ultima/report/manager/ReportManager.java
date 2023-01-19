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
package net.sra.prime.ultima.report.manager;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author Syamsu
 */
public interface ReportManager<E extends Exception> {

    public Connection getConnection();

    public boolean isInit();

    public void reset();

    public void compileReport(InputStream xmljasper) throws E;

    public void generateReport(Map param) throws E;

    public String showReport() throws E;

    public void firstPage();

    public void lastPage();

    public void nextPage();

    public void prevPage();
//    public InputStream getXLS() throws E;
//    public InputStream getXLS(boolean[] options) throws E;
//    public InputStream getPDF() throws E;
//    public InputStream getPDF(boolean[] options) throws E;

    public void showXLS(OutputStream out) throws E;

    public void showXLS(OutputStream out, boolean[] options) throws E;

    public void showPDF(OutputStream out) throws E;

    public void showPDF(OutputStream out, boolean[] options) throws E;

}
