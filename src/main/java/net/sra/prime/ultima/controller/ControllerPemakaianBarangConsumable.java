/*
 * Copyright 2023 JoinFaces.
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
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.PemakaianBarangConsumable;
import net.sra.prime.ultima.entity.PemakaianBarangConsumableDetail;
import net.sra.prime.ultima.service.ServicePemakaianBarangConsumable;
import net.sra.prime.ultima.view.input.BarangConsumableAutoComplete;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author erza
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerPemakaianBarangConsumable {
    private static final long serialVersionUID = 8811960521862002964L;
    private PemakaianBarangConsumable item;
    private List<PemakaianBarangConsumable> lPemakaianBarangConsumable = new ArrayList<>();
    private Pegawai pegawai;
    private List<PemakaianBarangConsumableDetail> lPemakaianBarangConsumableDetail = new ArrayList<>();

    @Inject
    private Page page;

    @Inject
    private BarangConsumableAutoComplete barangConsumableAutoComplete;

    @Autowired
    ServicePemakaianBarangConsumable servicePemakaianBarangConsumable;

    @PostConstruct
    public void init() {
        item = new PemakaianBarangConsumable();
        pegawai = page.getMyPegawai();
    }

    public void initItem() {
        item = new PemakaianBarangConsumable();
        item.setTanggal(new Date());
        lPemakaianBarangConsumableDetail = new ArrayList<>();
        lPemakaianBarangConsumableDetail.add(new PemakaianBarangConsumableDetail());
    }

    public void onLoadList() {
        try {
            lPemakaianBarangConsumable = servicePemakaianBarangConsumable.onLoadList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
    }

    public void delete() {
        try {
            servicePemakaianBarangConsumable.delete(item.getId());
            this.onLoadList();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Unit berhasil dihapus"));
            PrimeFaces.current().ajax().update("form:messages", "form:singleDT");
        } catch (Exception e) {
            //e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unit Gagal di Dihapus " + e, ""));
        }
    }

    public void tambah() {
        this.nomorurut();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setCreated_by(pegawai.getId_pegawai());
            item.setStatus('D');
            servicePemakaianBarangConsumable.tambah(item, lPemakaianBarangConsumableDetail);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void ubah(Character st) {

        item.setStatus(st);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setModified_by(pegawai.getId_pegawai());
            servicePemakaianBarangConsumable.ubah(item);
            servicePemakaianBarangConsumable.deletedetail(item.getId());
            servicePemakaianBarangConsumable.tambahdetail(lPemakaianBarangConsumableDetail, item);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diubah"));
            context.getExternalContext().redirect("./list.jsf");

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    public void nomorurut() {
        final String[] romanMonths = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
        DateFormat yr = new SimpleDateFormat("yyyy"); // Just the year, with 2 digits
        String year = yr.format(item.getTanggal());
        DateFormat bln = new SimpleDateFormat("MM"); // Just the year, with 2 digits
        String bulan = bln.format(item.getTanggal());
        String bulanromawi = romanMonths[Integer.parseInt(bln.format(item.getTanggal())) - 1];
        String noMax = servicePemakaianBarangConsumable.noMax(Integer.parseInt(bulan), Integer.parseInt(year));

        if (noMax == null) {
            item.setNomor(year + "/" + bulanromawi + "/IO-001");
        } else {
            Integer nomor = Integer.parseInt(noMax);
            nomor = nomor + 1;
            noMax = String.format("%03d", nomor);
            item.setNomor(year + "/" + bulanromawi + "/IO-" + noMax);
        }
    }

    public void onLoad() {
        try {
            item = servicePemakaianBarangConsumable.onLoad(item.getId());
            lPemakaianBarangConsumableDetail = servicePemakaianBarangConsumable.onLoadDetail(item.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PemakaianBarangConsumable> getDataPemakaianBarangConsumable() {
        return lPemakaianBarangConsumable;
    }

    public void getDataApprovePemakaianBarangConsumable() {
        try {
            lPemakaianBarangConsumable = servicePemakaianBarangConsumable.onLoadAprroveList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<PemakaianBarangConsumableDetail> getDataPemakaianBarangConsumableDetail() {
        return lPemakaianBarangConsumableDetail;
    }

    public void extend() {
        lPemakaianBarangConsumableDetail.add(new PemakaianBarangConsumableDetail());
    }

    public void onDeleteClicked(PemakaianBarangConsumableDetail pemakaianbarangconsumabledetail) {
        lPemakaianBarangConsumableDetail.remove(pemakaianbarangconsumabledetail);
    }

    public void onBarangSelect(PemakaianBarangConsumableDetail s, Integer i) {
        try {
            s.setId_barang_consumable(barangConsumableAutoComplete.getBarangConsumable().getId());
            s.setNama_barang_consumable(barangConsumableAutoComplete.getBarangConsumable().getNama_barang());
            s.setNama_satuan(barangConsumableAutoComplete.getBarangConsumable().getNama_satuan_kecil());
            lPemakaianBarangConsumableDetail.set(i, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
     
    
}
