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
package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.TandaTerimaInvoice;
import net.sra.prime.ultima.entity.TandaTerimaInvoiceDetail;
import net.sra.prime.ultima.service.ServiceTandaTerimaInvoice;
import org.primefaces.event.SelectEvent;
import net.sra.prime.ultima.entity.Customer;
import net.sra.prime.ultima.view.input.CustomerAutoComplete;
import net.sra.prime.ultima.view.input.PenjualanAutoComplete;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerTandaTerimaInvoice implements java.io.Serializable {

    private static final long serialVersionUID = 8811960521862002964L;

    @Autowired
    private ServiceTandaTerimaInvoice serviceTandaTerimaInvoice;
    private TandaTerimaInvoice item;
    private List<TandaTerimaInvoice> lTandaTerimaInvoice = new ArrayList<>();
    private List<TandaTerimaInvoiceDetail> lTandaTerimaInvoiceDetail = new ArrayList<>();
    private Date awal;
    private Date akhir;
    private Character status;

    @Inject
    private Page page;

    @Inject
    private CustomerAutoComplete customerAutoComplete;

    @Inject
    private PenjualanAutoComplete penjualanAutoComplete;

    @PostConstruct
    public void init() {
        item = new TandaTerimaInvoice();
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        awal = c.getTime();
        akhir = new Date();
    }

    public void initItem() {
        item = new TandaTerimaInvoice();
        item.setTotal(0.00);
        lTandaTerimaInvoiceDetail.add(new TandaTerimaInvoiceDetail());

    }

    public void onLoadList() {
        try {
            lTandaTerimaInvoice = serviceTandaTerimaInvoice.onLoadList(awal, akhir, status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void onLoad() {
        try {
            item = serviceTandaTerimaInvoice.onLoad(item.getId());
            lTandaTerimaInvoiceDetail = serviceTandaTerimaInvoice.onLoadDetail(item.getId());
            customerAutoComplete.setCustomer(serviceTandaTerimaInvoice.selectOneCustomer(item.getId_customer()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<TandaTerimaInvoice> getDataTandaTerimaInvoice() {
        return lTandaTerimaInvoice;
    }

    public List<TandaTerimaInvoiceDetail> getDataTandaTerimaInvoiceDetail() {
        return lTandaTerimaInvoiceDetail;
    }

    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceTandaTerimaInvoice.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus('D');
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            nomorurut();
            serviceTandaTerimaInvoice.tambah(item, lTandaTerimaInvoiceDetail);
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }
    
    public void ubah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(st);
            item.setModified_by(page.getMyPegawai().getId_pegawai());
            serviceTandaTerimaInvoice.ubah(item, lTandaTerimaInvoiceDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }

    }

    public void extend() {
        lTandaTerimaInvoiceDetail.add(new TandaTerimaInvoiceDetail());
    }

    public void onDeleteClicked(TandaTerimaInvoiceDetail itemTandaTerimaInvoiceDetail) {
        lTandaTerimaInvoiceDetail.remove(itemTandaTerimaInvoiceDetail);
    }
    
    public void onCustomerSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Customer customer = (Customer) event.getObject();
        item.setId_customer(customer.getId_kontak());
        item.setTotal(0.00);
        penjualanAutoComplete.setId_customer(customer.getId_kontak());
        lTandaTerimaInvoiceDetail = new ArrayList<>();
        lTandaTerimaInvoiceDetail.add(new TandaTerimaInvoiceDetail());
    }
    
    public void onPenjualanSelect(TandaTerimaInvoiceDetail s, Integer i) {
        try {
            s.setNo_penjualan(penjualanAutoComplete.getPenjualan().getNo_penjualan());
            s.setNilai(penjualanAutoComplete.getPenjualan().getGrandtotal());
            s.setNo_do(penjualanAutoComplete.getPenjualan().getNo_dos());
            s.setNomor_faktur(penjualanAutoComplete.getPenjualan().getFaktur());
            s.setNo_po(penjualanAutoComplete.getPenjualan().getReferensi());
            lTandaTerimaInvoiceDetail.set(i, s);
            item.setTotal(calculateTotal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Double calculateTotal(){
        Double total=0.00;
        for(int i=0; i < lTandaTerimaInvoiceDetail.size();i++){
            total += lTandaTerimaInvoiceDetail.get(i).getNilai();
        }
        return total;
    }
    public void cetak() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./cetak.jsf?id=" + item.getId());
    }
    
    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (item != null) {
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Silahkan pilih data yang akan diedit !!!!", ""));
        }
    }
    
    public void nomorurut() {

        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat thn = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String tahun = thn.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulannya = bln.format(item.getTanggal());
        String bulan = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = serviceTandaTerimaInvoice.noMax(Integer.parseInt(tahun));

        if (noMax == null) {
            item.setNomor("0001/TT-INV/CPA/" + bulan + "/" + tahun);
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%04d", nomor);
            item.setNomor(noMax + "/TT-INV/CPA/" + bulan + "/" + tahun);
        }
    }
    
    public void createXls() throws InterruptedException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Daftar Tanda Tarima Invoice");
        PropertyTemplate pt = new PropertyTemplate();

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Calibri");
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        XSSFDataFormat format = workbook.createDataFormat();
        dateCellStyle.setDataFormat(format.getFormat("dd-MM-yyyy"));
        dateCellStyle.setAlignment(HorizontalAlignment.LEFT);
        
        XSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.setDataFormat(format.getFormat("#,##0"));
        numberCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        
        XSSFCellStyle centerCellStyle = workbook.createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(1);
        cell.setCellValue("No. Tanda Terima Invoice");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(2);
        cell.setCellValue("Tanggal");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(3);
        cell.setCellValue("Customer");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(4);
        cell.setCellValue("Tanggal Kirim");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(5);
        cell.setCellValue("Pengirim/Courier");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(6);
        cell.setCellValue("No. Resi");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(7);
        cell.setCellValue("Tanggal Terima");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(8);
        cell.setCellValue("Penerima");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(9);
        cell.setCellValue("Nomor Invoice");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(10);
        cell.setCellValue("No. Faktur Pajak");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(11);
        cell.setCellValue("No. DO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(12);
        cell.setCellValue("No. PO");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(13);
        cell.setCellValue("Jumlah");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(14);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyle);
        cell = row.createCell(15);
        cell.setCellValue("Status");
        cell.setCellStyle(cellStyle);
        
        lTandaTerimaInvoiceDetail = serviceTandaTerimaInvoice.onLoadListDetail(awal, akhir, status);
        String tes = "";
        String st="";
        Integer no=1;
        for (int i = 0; i < lTandaTerimaInvoiceDetail.size(); i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(no);
                cell.setCellStyle(centerCellStyle);
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(1);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNomor());
            } else {
                cell.setCellValue("");
            }

            cell = row.createCell(2);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getTanggal());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(3);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getCustomer());
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(4);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getTgl_kirim());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(5);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getCourier());
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(6);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getResi());
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(7);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getTgl_terima());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }
            

            cell = row.createCell(8);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getPenerima());
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }
            
            
            

            cell = row.createCell(9);
            cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNo_penjualan());

            cell = row.createCell(10);
            cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNomor_faktur());

            cell = row.createCell(11);
            cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNo_do());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(12);
            cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNo_po());
            cell.setCellStyle(centerCellStyle);

            cell = row.createCell(13);
            cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getNilai());
            cell.setCellStyle(numberCellStyle);

            cell = row.createCell(14);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                cell.setCellValue(lTandaTerimaInvoiceDetail.get(i).getTotal());
                cell.setCellStyle(numberCellStyle);
            } else {
                cell.setCellValue("");
            }
    
            
            

            cell = row.createCell(15);
            if (!lTandaTerimaInvoiceDetail.get(i).getNomor().equals(tes)) {
                switch (lTandaTerimaInvoiceDetail.get(i).getStatus()){
                    case 'D':
                        st = "Draft";
                        break;
                    case 'A':
                        st="Approve";
                        break;
                }
                cell.setCellValue(st);
                tes=lTandaTerimaInvoiceDetail.get(i).getNomor();
                no++;
            } else {
                cell.setCellValue("");
            }
        
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
        sheet.autoSizeColumn(11);
        sheet.autoSizeColumn(12);
        sheet.autoSizeColumn(13);
        sheet.autoSizeColumn(14);
        sheet.autoSizeColumn(15);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Daftar Tanda Terima Invoice.xlsx\"");
        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();

    }

    
    
}
