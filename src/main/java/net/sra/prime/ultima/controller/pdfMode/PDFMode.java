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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sra.prime.ultima.controller.cetak.ControllerCetakDo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPL;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPenawaran;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPoReguler;
import net.sra.prime.ultima.controller.cetak.ControllerIT;
import net.sra.prime.ultima.controller.cetak.ControllerCetakBankVoucher;
import net.sra.prime.ultima.controller.cetak.ControllerCetakBottomPrice;
import net.sra.prime.ultima.controller.cetak.ControllerCetakDaftarBarang;
import net.sra.prime.ultima.controller.cetak.ControllerCetakForecast;
import net.sra.prime.ultima.controller.cetak.ControllerCetakHargaBeli;
import net.sra.prime.ultima.controller.cetak.ControllerCetakIntruksiPo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakInvoice;
import net.sra.prime.ultima.controller.cetak.ControllerCetakInvoiceCustomer;
import net.sra.prime.ultima.controller.cetak.ControllerCetakInvoiceReguler;
import net.sra.prime.ultima.controller.cetak.ControllerCetakJurnalUmum;
import net.sra.prime.ultima.controller.cetak.ControllerCetakKasKeluar;
import net.sra.prime.ultima.controller.cetak.ControllerCetakKasMasuk;
import net.sra.prime.ultima.controller.cetak.ControllerCetakListSo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakNeracaSaldo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPaymentProposal;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPemakaian;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPembayaranHutang;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPembayaranPiutang;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPenambahan;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPenerimaan;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPenerimaanGudang;
import net.sra.prime.ultima.controller.cetak.ControllerCetakPermintaanpembelian;
import net.sra.prime.ultima.controller.cetak.ControllerCetakSalesReport;
import net.sra.prime.ultima.controller.cetak.ControllerCetakSo;
import net.sra.prime.ultima.controller.cetak.ControllerCetakUmurInvoice;
import net.sra.prime.ultima.controller.cetak.ControllerCetakUmurOverdue;
import net.sra.prime.ultima.controller.cetak.ControllerCetakCN;
import net.sra.prime.ultima.controller.cetak.ControllerCetakTandaTerimaInvoice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Syamsu
 */
@Controller
public class PDFMode {

    /// Nambah controller di sini
    @ResponseBody
    @RequestMapping(value = "/public/pdfPenawaranFile.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPenawaran(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenawaran cetakPenawaran;
            if (session == null || ((ControllerCetakPenawaran) session.getAttribute("controllerCetakPenawaran")) == null) {
                cetakPenawaran = new ControllerCetakPenawaran();
            } else {
                cetakPenawaran = (ControllerCetakPenawaran) session.getAttribute("controllerCetakPenawaran");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPenawaran.getReport();
            cetakPenawaran.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /// Nambah controller di sini
    @ResponseBody
    @RequestMapping(value = "/public/pdfPoFile.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPo cetakPo;
            if (session == null || ((ControllerCetakPo) session.getAttribute("controllerCetakPo")) == null) {
                cetakPo = new ControllerCetakPo();
            } else {
                cetakPo = (ControllerCetakPo) session.getAttribute("controllerCetakPo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPo.getReport();
            cetakPo.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/pdfPoRFile.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPoReguler(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPoReguler cetakPo;
            if (session == null || ((ControllerCetakPoReguler) session.getAttribute("controllerCetakPoReguler")) == null) {
                cetakPo = new ControllerCetakPoReguler();
            } else {
                cetakPo = (ControllerCetakPoReguler) session.getAttribute("controllerCetakPoReguler");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPo.getReport();
            cetakPo.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/pdfPenawaranFile.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPenawaran(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenawaran cetakPenawaran;
            if (session == null || ((ControllerCetakPenawaran) session.getAttribute("controllerCetakPenawaran")) == null) {
                cetakPenawaran = new ControllerCetakPenawaran();
            } else {
                cetakPenawaran = (ControllerCetakPenawaran) session.getAttribute("controllerCetakPenawaran");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPenawaran.getReport();
            cetakPenawaran.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/pdfPoFile.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPo cetakPo;
            if (session == null || ((ControllerCetakPo) session.getAttribute("controllerCetakPo")) == null) {
                cetakPo = new ControllerCetakPo();
            } else {
                cetakPo = (ControllerCetakPo) session.getAttribute("controllerCetakPo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPo.getReport();
            cetakPo.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /// Nambah controller di sini
    @ResponseBody
    @RequestMapping(value = "/public/pdfDoFile.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFDo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakDo cetakDo;
            if (session == null || ((ControllerCetakDo) session.getAttribute("controllerCetakDo")) == null) {
                cetakDo = new ControllerCetakDo();
            } else {
                cetakDo = (ControllerCetakDo) session.getAttribute("controllerCetakDo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakDo.getReport();
            cetakDo.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/internal_transfer.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFIT(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerIT cetakIt;
            if (session == null || ((ControllerIT) session.getAttribute("controllerIT")) == null) {
                cetakIt = new ControllerIT();
            } else {
                cetakIt = (ControllerIT) session.getAttribute("controllerIT");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakIt.getReport();
            cetakIt.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/Pickinglist.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPL(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPL cetakPl;
            if (session == null || ((ControllerCetakPL) session.getAttribute("controllerCetakPL")) == null) {
                cetakPl = new ControllerCetakPL();
            } else {
                cetakPl = (ControllerCetakPL) session.getAttribute("controllerCetakPL");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPl.getReport();
            cetakPl.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PenerimaanBarang.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPenerimaanGudang(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenerimaanGudang cetakPenerimaanGudang;
            if (session == null || ((ControllerCetakPenerimaanGudang) session.getAttribute("controllerCetakPenerimaanGudang")) == null) {
                cetakPenerimaanGudang = new ControllerCetakPenerimaanGudang();
            } else {
                cetakPenerimaanGudang = (ControllerCetakPenerimaanGudang) session.getAttribute("controllerCetakPenerimaanGudang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakPenerimaanGudang.getReport();
            cetakPenerimaanGudang.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/pdfInvoice.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFInvoice(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakInvoice cetakInvoice;
            if (session == null || ((ControllerCetakInvoice) session.getAttribute("controllerCetakInvoice")) == null) {
                cetakInvoice = new ControllerCetakInvoice();
            } else {
                cetakInvoice = (ControllerCetakInvoice) session.getAttribute("controllerCetakInvoice");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakInvoice.getReport();
            cetakInvoice.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/InvoiceReguler.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFInvoiceReguler(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakInvoiceReguler cetakInvoice;
            if (session == null || ((ControllerCetakInvoiceReguler) session.getAttribute("controllerCetakInvoiceReguler")) == null) {
                cetakInvoice = new ControllerCetakInvoiceReguler();
            } else {
                cetakInvoice = (ControllerCetakInvoiceReguler) session.getAttribute("controllerCetakInvoiceReguler");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakInvoice.getReport();
            cetakInvoice.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/neracasaldo.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFNeracaSaldo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakNeracaSaldo cetak;
            if (session == null || ((ControllerCetakNeracaSaldo) session.getAttribute("controllerCetakNeracaSaldo")) == null) {
                cetak = new ControllerCetakNeracaSaldo();
            } else {
                cetak = (ControllerCetakNeracaSaldo) session.getAttribute("controllerCetakNeracaSaldo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/neracasaldo.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXNeracaSaldo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakNeracaSaldo cetak;
            if (session == null || ((ControllerCetakNeracaSaldo) session.getAttribute("controllerCetakNeracaSaldo")) == null) {
                cetak = new ControllerCetakNeracaSaldo();
            } else {
                cetak = (ControllerCetakNeracaSaldo) session.getAttribute("controllerCetakNeracaSaldo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/invoicecustomer.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFInvoiceCustomer(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakInvoiceCustomer cetak;
            if (session == null || ((ControllerCetakInvoiceCustomer) session.getAttribute("controllerCetakInvoiceCustomer")) == null) {
                cetak = new ControllerCetakInvoiceCustomer();
            } else {
                cetak = (ControllerCetakInvoiceCustomer) session.getAttribute("controllerCetakInvoiceCustomer");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/invoicecustomer.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXInvoiceCustomer(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakInvoiceCustomer cetak;
            if (session == null || ((ControllerCetakInvoiceCustomer) session.getAttribute("controllerCetakInvoiceCustomer")) == null) {
                cetak = new ControllerCetakInvoiceCustomer();
            } else {
                cetak = (ControllerCetakInvoiceCustomer) session.getAttribute("controllerCetakInvoiceCustomer");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/buktikaskeluar.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFBuktiKasKeluar(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakKasKeluar cetak;
            if (session == null || ((ControllerCetakKasKeluar) session.getAttribute("controllerCetakKasKeluar")) == null) {
                cetak = new ControllerCetakKasKeluar();
            } else {
                cetak = (ControllerCetakKasKeluar) session.getAttribute("controllerCetakKasKeluar");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/buktikaskeluar.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXIBuktiKasKeluar(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakKasKeluar cetak;
            if (session == null || ((ControllerCetakKasKeluar) session.getAttribute("controllerCetakKasKeluar")) == null) {
                cetak = new ControllerCetakKasKeluar();
            } else {
                cetak = (ControllerCetakKasKeluar) session.getAttribute("controllerCetakKasKeluar");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/buktikasmasuk.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFBuktiKasMasuk(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakKasMasuk cetak;
            if (session == null || ((ControllerCetakKasMasuk) session.getAttribute("controllerCetakKasMasuk")) == null) {
                cetak = new ControllerCetakKasMasuk();
            } else {
                cetak = (ControllerCetakKasMasuk) session.getAttribute("controllerCetakKasMasuk");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/buktikasmasuk.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXIBuktiKasMasuk(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakKasMasuk cetak;
            if (session == null || ((ControllerCetakKasMasuk) session.getAttribute("controllerCetakKasMasuk")) == null) {
                cetak = new ControllerCetakKasMasuk();
            } else {
                cetak = (ControllerCetakKasMasuk) session.getAttribute("controllerCetakKasMasuk");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/officialreceipt.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFOfficialReceipt(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPembayaranPiutang cetak;
            if (session == null || ((ControllerCetakPembayaranPiutang) session.getAttribute("controllerCetakPembayaranPiutang")) == null) {
                cetak = new ControllerCetakPembayaranPiutang();
            } else {
                cetak = (ControllerCetakPembayaranPiutang) session.getAttribute("controllerCetakPembayaranPiutang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/officialreceipt.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPembayaranPiutang(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPembayaranPiutang cetak;
            if (session == null || ((ControllerCetakPembayaranPiutang) session.getAttribute("controllerCetakPembayaranPiutang")) == null) {
                cetak = new ControllerCetakPembayaranPiutang();
            } else {
                cetak = (ControllerCetakPembayaranPiutang) session.getAttribute("controllerCetakPembayaranPiutang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/Formstokopname.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFStokOpname(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakDaftarBarang cetak;
            if (session == null || ((ControllerCetakDaftarBarang) session.getAttribute("controllerCetakDaftarBarang")) == null) {
                cetak = new ControllerCetakDaftarBarang();
            } else {
                cetak = (ControllerCetakDaftarBarang) session.getAttribute("controllerCetakDaftarBarang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/Formstokopname.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXStokOpname(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakDaftarBarang cetak;
            if (session == null || ((ControllerCetakDaftarBarang) session.getAttribute("controllerCetakDaftarBarang")) == null) {
                cetak = new ControllerCetakDaftarBarang();
            } else {
                cetak = (ControllerCetakDaftarBarang) session.getAttribute("controllerCetakDaftarBarang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PembayaranAp.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPembayaranHutang(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPembayaranHutang cetak;
            if (session == null || ((ControllerCetakPembayaranHutang) session.getAttribute("controllerCetakPembayaranHutang")) == null) {
                cetak = new ControllerCetakPembayaranHutang();
            } else {
                cetak = (ControllerCetakPembayaranHutang) session.getAttribute("controllerCetakPembayaranHutang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PembayaranAp.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPembayaranHutang(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPembayaranHutang cetak;
            if (session == null || ((ControllerCetakPembayaranHutang) session.getAttribute("controllerCetakPembayaranHutang")) == null) {
                cetak = new ControllerCetakPembayaranHutang();
            } else {
                cetak = (ControllerCetakPembayaranHutang) session.getAttribute("controllerCetakPembayaranHutang");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/JurnalUmum.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFJurnalUmum(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakJurnalUmum cetak;
            if (session == null || ((ControllerCetakJurnalUmum) session.getAttribute("controllerCetakJurnalUmum")) == null) {
                cetak = new ControllerCetakJurnalUmum();
            } else {
                cetak = (ControllerCetakJurnalUmum) session.getAttribute("controllerCetakJurnalUmum");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/JurnalUmum.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXJurnalUmum(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakJurnalUmum cetak;
            if (session == null || ((ControllerCetakJurnalUmum) session.getAttribute("controllerCetakJurnalUmum")) == null) {
                cetak = new ControllerCetakJurnalUmum();
            } else {
                cetak = (ControllerCetakJurnalUmum) session.getAttribute("controllerCetakJurnalUmum");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/BankVoucher.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFBankVoucher(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakBankVoucher cetak;
            if (session == null || ((ControllerCetakBankVoucher) session.getAttribute("controllerCetakBankVoucher")) == null) {
                cetak = new ControllerCetakBankVoucher();
            } else {
                cetak = (ControllerCetakBankVoucher) session.getAttribute("controllerCetakBankVoucher");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/BankVoucher.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXBankVoucher(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakBankVoucher cetak;
            if (session == null || ((ControllerCetakBankVoucher) session.getAttribute("controllerCetakBankVoucher")) == null) {
                cetak = new ControllerCetakBankVoucher();
            } else {
                cetak = (ControllerCetakBankVoucher) session.getAttribute("controllerCetakBankVoucher");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/SalesOrder.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFSo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakSo cetak;
            if (session == null || ((ControllerCetakSo) session.getAttribute("controllerCetakSo")) == null) {
                cetak = new ControllerCetakSo();
            } else {
                cetak = (ControllerCetakSo) session.getAttribute("controllerCetakSo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/SalesOrder.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXSo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakSo cetak;
            if (session == null || ((ControllerCetakSo) session.getAttribute("controllerCetakSo")) == null) {
                cetak = new ControllerCetakSo();
            } else {
                cetak = (ControllerCetakSo) session.getAttribute("controllerCetakSo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/IntruksiPo.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFIntruksiPo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakIntruksiPo cetak;
            if (session == null || ((ControllerCetakIntruksiPo) session.getAttribute("controllerCetakIntruksiPo")) == null) {
                cetak = new ControllerCetakIntruksiPo();
            } else {
                cetak = (ControllerCetakIntruksiPo) session.getAttribute("controllerCetakIntruksiPo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/IntruksiPo.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXIntruksiPo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakIntruksiPo cetak;
            if (session == null || ((ControllerCetakIntruksiPo) session.getAttribute("controllerCetakIntruksiPo")) == null) {
                cetak = new ControllerCetakIntruksiPo();
            } else {
                cetak = (ControllerCetakIntruksiPo) session.getAttribute("controllerCetakIntruksiPo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/BottomPrice.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFBottomPrice(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakBottomPrice cetak;
            if (session == null || ((ControllerCetakBottomPrice) session.getAttribute("controllerCetakBottomPrice")) == null) {
                cetak = new ControllerCetakBottomPrice();
            } else {
                cetak = (ControllerCetakBottomPrice) session.getAttribute("controllerCetakBottomPrice");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/BottomPrice.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXBottomPrice(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakBottomPrice cetak;
            if (session == null || ((ControllerCetakBottomPrice) session.getAttribute("controllerCetakBottomPrice")) == null) {
                cetak = new ControllerCetakBottomPrice();
            } else {
                cetak = (ControllerCetakBottomPrice) session.getAttribute("controllerCetakBottomPrice");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/UmurInvoice.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFUmurInvoice(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakUmurInvoice cetak;
            if (session == null || ((ControllerCetakUmurInvoice) session.getAttribute("controllerCetakUmurInvoice")) == null) {
                cetak = new ControllerCetakUmurInvoice();
            } else {
                cetak = (ControllerCetakUmurInvoice) session.getAttribute("controllerCetakUmurInvoice");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/UmurInvoice.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXUmurInvoice(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakUmurInvoice cetak;
            if (session == null || ((ControllerCetakUmurInvoice) session.getAttribute("controllerCetakUmurInvoice")) == null) {
                cetak = new ControllerCetakUmurInvoice();
            } else {
                cetak = (ControllerCetakUmurInvoice) session.getAttribute("controllerCetakUmurInvoice");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/UmurOverdue.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFUmurOverdue(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakUmurOverdue cetak;
            if (session == null || ((ControllerCetakUmurOverdue) session.getAttribute("controllerCetakUmurOverdue")) == null) {
                cetak = new ControllerCetakUmurOverdue();
            } else {
                cetak = (ControllerCetakUmurOverdue) session.getAttribute("controllerCetakUmurOverdue");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/UmurOverdue.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXUmurOverdue(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakUmurOverdue cetak;
            if (session == null || ((ControllerCetakUmurOverdue) session.getAttribute("controllerCetakUmurOverdue")) == null) {
                cetak = new ControllerCetakUmurOverdue();
            } else {
                cetak = (ControllerCetakUmurOverdue) session.getAttribute("controllerCetakUmurOverdue");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/HargaBeli.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFHargaBeli(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakHargaBeli cetak;
            if (session == null || ((ControllerCetakHargaBeli) session.getAttribute("controllerCetakHargaBeli")) == null) {
                cetak = new ControllerCetakHargaBeli();
            } else {
                cetak = (ControllerCetakHargaBeli) session.getAttribute("controllerCetakHargaBeli");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/HargaBeli.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXHargaBeli(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakHargaBeli cetak;
            if (session == null || ((ControllerCetakHargaBeli) session.getAttribute("controllerCetakHargaBeli")) == null) {
                cetak = new ControllerCetakHargaBeli();
            } else {
                cetak = (ControllerCetakHargaBeli) session.getAttribute("controllerCetakHargaBeli");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PaymentProposal.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPaymentProposal(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPaymentProposal cetak;
            if (session == null || ((ControllerCetakPaymentProposal) session.getAttribute("controllerCetakPaymentProposal")) == null) {
                cetak = new ControllerCetakPaymentProposal();
            } else {
                cetak = (ControllerCetakPaymentProposal) session.getAttribute("controllerCetakPaymentProposal");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PaymentProposal.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPaymentProposal(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPaymentProposal cetak;
            if (session == null || ((ControllerCetakPaymentProposal) session.getAttribute("controllerCetakPaymentProposal")) == null) {
                cetak = new ControllerCetakPaymentProposal();
            } else {
                cetak = (ControllerCetakPaymentProposal) session.getAttribute("controllerCetakPaymentProposal");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/AccountPayableJournal.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFAccountPayableJournall(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenerimaan cetak;
            if (session == null || ((ControllerCetakPenerimaan) session.getAttribute("controllerCetakPenerimaan")) == null) {
                cetak = new ControllerCetakPenerimaan();
            } else {
                cetak = (ControllerCetakPenerimaan) session.getAttribute("controllerCetakPenerimaan");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/AccountPayableJournal.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXAccountPayableJournal(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenerimaan cetak;
            if (session == null || ((ControllerCetakPenerimaan) session.getAttribute("controllerCetakPenerimaan")) == null) {
                cetak = new ControllerCetakPenerimaan();
            } else {
                cetak = (ControllerCetakPenerimaan) session.getAttribute("controllerCetakPenerimaan");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/Forecast.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFForecast(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakForecast cetak;
            if (session == null || ((ControllerCetakForecast) session.getAttribute("controllerCetakForecast")) == null) {
                cetak = new ControllerCetakForecast();
            } else {
                cetak = (ControllerCetakForecast) session.getAttribute("controllerCetakForecast");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/Forecast.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXForecast(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakForecast cetak;
            if (session == null || ((ControllerCetakForecast) session.getAttribute("controllerCetakForecast")) == null) {
                cetak = new ControllerCetakForecast();
            } else {
                cetak = (ControllerCetakForecast) session.getAttribute("controllerCetakForecast");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PermintaanPembelian.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPermintaanPembelian(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPermintaanpembelian cetak;
            if (session == null || ((ControllerCetakPermintaanpembelian) session.getAttribute("controllerCetakPermintaanpembelian")) == null) {
                cetak = new ControllerCetakPermintaanpembelian();
            } else {
                cetak = (ControllerCetakPermintaanpembelian) session.getAttribute("controllerCetakPermintaanpembelian");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PermintaanPembelian.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPermintaanPembelian(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPermintaanpembelian cetak;
            if (session == null || ((ControllerCetakPermintaanpembelian) session.getAttribute("controllerCetakPermintaanpembelian")) == null) {
                cetak = new ControllerCetakPermintaanpembelian();
            } else {
                cetak = (ControllerCetakPermintaanpembelian) session.getAttribute("controllerCetakPermintaanpembelian");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PemakaianBarang.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPemakaian(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPemakaian cetak;
            if (session == null || ((ControllerCetakPemakaian) session.getAttribute("controllerCetakPemakaian")) == null) {
                cetak = new ControllerCetakPemakaian();
            } else {
                cetak = (ControllerCetakPemakaian) session.getAttribute("controllerCetakPemakaian");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PemakaianBarang.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPemakaian(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPemakaian cetak;
            if (session == null || ((ControllerCetakPemakaian) session.getAttribute("controllerCetakPemakaian")) == null) {
                cetak = new ControllerCetakPemakaian();
            } else {
                cetak = (ControllerCetakPemakaian) session.getAttribute("controllerCetakPemakaian");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PenambahanBarang.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFPenambahan(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenambahan cetak;
            if (session == null || ((ControllerCetakPenambahan) session.getAttribute("controllerCetakPenambahan")) == null) {
                cetak = new ControllerCetakPenambahan();
            } else {
                cetak = (ControllerCetakPenambahan) session.getAttribute("controllerCetakPenambahan");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/PenambahanBarang.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXPenambahan(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakPenambahan cetak;
            if (session == null || ((ControllerCetakPenambahan) session.getAttribute("controllerCetakPenambahan")) == null) {
                cetak = new ControllerCetakPenambahan();
            } else {
                cetak = (ControllerCetakPenambahan) session.getAttribute("controllerCetakPenambahan");
            }
            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/DaftarSo.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFDaftarSo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakListSo cetak;
            if (session == null || ((ControllerCetakListSo) session.getAttribute("controllerCetakListSo")) == null) {
                cetak = new ControllerCetakListSo();
            } else {
                cetak = (ControllerCetakListSo) session.getAttribute("controllerCetakListSo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/DaftarSo.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXDaftarSo(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakListSo cetak;
            if (session == null || ((ControllerCetakListSo) session.getAttribute("controllerCetakListSo")) == null) {
                cetak = new ControllerCetakListSo();
            } else {
                cetak = (ControllerCetakListSo) session.getAttribute("controllerCetakListSo");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/SalesReport.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFSalesReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakSalesReport cetak;
            if (session == null || ((ControllerCetakSalesReport) session.getAttribute("controllerCetakSalesReport")) == null) {
                cetak = new ControllerCetakSalesReport();
            } else {
                cetak = (ControllerCetakSalesReport) session.getAttribute("controllerCetakSalesReport");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/SalesReport.xlsx", method = RequestMethod.GET, produces = "application/xlsx")
    public void showXLSXSalesReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakSalesReport cetak;
            if (session == null || ((ControllerCetakSalesReport) session.getAttribute("controllerCetakSalesReport")) == null) {
                cetak = new ControllerCetakSalesReport();
            } else {
                cetak = (ControllerCetakSalesReport) session.getAttribute("controllerCetakSalesReport");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showXLS(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/public/CreditNote.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFCN(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakCN cetakCN;
            if (session == null || ((ControllerCetakCN) session.getAttribute("controllerCetakCN")) == null) {
                cetakCN = new ControllerCetakCN();
            } else {
                cetakCN = (ControllerCetakCN) session.getAttribute("controllerCetakCN");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetakCN.getReport();
            cetakCN.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @ResponseBody
    @RequestMapping(value = "/public/TandaTerimaInvoice.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public void showPDFTTI(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            ControllerCetakTandaTerimaInvoice cetak;
            if (session == null || ((ControllerCetakTandaTerimaInvoice) session.getAttribute("controllerCetakTandaTerimaInvoice")) == null) {
                cetak = new ControllerCetakTandaTerimaInvoice();
            } else {
                cetak = (ControllerCetakTandaTerimaInvoice) session.getAttribute("controllerCetakTandaTerimaInvoice");
            }

            //ServletOutputStream out = response.getOutputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedOutputStream baos = new BufferedOutputStream(out);
            String isinya = cetak.getReport();
            cetak.getReportLaporan().showPDF(response.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    

}
