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
package net.sra.prime.ultima.controller.pdfMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sra.prime.ultima.controller.cetak.ControllerCetakSlip;
import net.sra.prime.ultima.controller.cetak.ControllerMySalary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Hairian
 */
@Controller
public class PDFModeSlip {

    /// Nambah controller di sini
    @ResponseBody
    @RequestMapping(value = "/public/slipgaji.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFSlip(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakSlip cetakSlip;
            if (session == null || ((ControllerCetakSlip) session.getAttribute("controllerCetakSlip")) == null) {
                cetakSlip = new ControllerCetakSlip();
            } else {
                cetakSlip = (ControllerCetakSlip) session.getAttribute("controllerCetakSlip");
            }

            String isinya = cetakSlip.getReport();
            cetakSlip.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/slipgajiku.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFSlipKu(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerMySalary cetakSlip;
            if (session == null || ((ControllerMySalary) session.getAttribute("controllerMySalary")) == null) {
                cetakSlip = new ControllerMySalary();
            } else {
                cetakSlip = (ControllerMySalary) session.getAttribute("controllerMySalary");
            }

            String isinya = cetakSlip.getReport();
            cetakSlip.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    

}
