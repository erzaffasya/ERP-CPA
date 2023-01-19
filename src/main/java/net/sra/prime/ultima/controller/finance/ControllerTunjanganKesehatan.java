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
package net.sra.prime.ultima.controller.finance;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.admin.Options;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatan;
import net.sra.prime.ultima.entity.finance.TunjanganKesehatanDetail;
import net.sra.prime.ultima.service.finance.ServiceTunjanganKesehatan;
import net.sra.prime.ultima.view.input.PegawaiAutoComplete;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerTunjanganKesehatan implements java.io.Serializable {
    
    private static final long serialVersionUID = 8811960521862002964L;
    
    @Autowired
    private ServiceTunjanganKesehatan serviceTunjanganKesehatan;
    private TunjanganKesehatan item;
    private TunjanganKesehatanDetail itemdetail;
    private List<TunjanganKesehatan> lTunjanganKesehatan = new ArrayList<>();
    private List<TunjanganKesehatanDetail> lTunjanganKesehatanDetail = new ArrayList<>();
    private Date datestart;
    private Character status;
    
    @Inject
    private Page page;
    
    @Inject
    private PegawaiAutoComplete pegawaiAutoComplete;
    
    
    @Inject
    private Options options;
    
    @PostConstruct
    public void init() {
        item = new TunjanganKesehatan();
        
    }
    
    public void initItem() {
        item = new TunjanganKesehatan();
        item.setDate(new Date());
        item.setStatus('D');
        item.setDate(new Date());
        pegawaiAutoComplete.setPegawai(null);
        lTunjanganKesehatanDetail = new ArrayList<>();
        lTunjanganKesehatanDetail.add(new TunjanganKesehatanDetail());
        
    }
    
    public void onLoadList() {
        lTunjanganKesehatan = serviceTunjanganKesehatan.onLoadList();
    }
    
    public List<TunjanganKesehatan> getDataTunjanganKesehatan() {
        return lTunjanganKesehatan;
    }
    
    public List<TunjanganKesehatanDetail> getDataTunjanganKesehatanDetail() {
        return lTunjanganKesehatanDetail;
    }
    
    
    
    public void delete(Integer id) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            serviceTunjanganKesehatan.delete(id);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Dihapus"));
            context.getExternalContext().redirect("./list.jsf");
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Dihapus " + e, ""));
        }
        
    }
    
    public void tambah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        Boolean tes = true;
        DateFormat thn = new SimpleDateFormat("yyyy");
            DateFormat bln = new SimpleDateFormat("MM");
            String tahun = thn.format(datestart);
            String bulan = bln.format(datestart);
        if(serviceTunjanganKesehatan.selectPeriodeProposal(Integer.parseInt(bulan), tahun) != null){
            tes=false;
        }
        try {
            if(tes){
            item.setStatus(st);
            item.setCreate_by(page.getMyPegawai().getId_pegawai());
            
            item.setMonth(Integer.parseInt(bulan));
            item.setYear(tahun);
                serviceTunjanganKesehatan.tambah(item, lTunjanganKesehatanDetail);
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            
                context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
            }else{
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal diinput karena periode bulan ini sudah diinput", ""));
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }
    
    public void update() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        item = serviceTunjanganKesehatan.onLoad(item.getId());
        
            context.getExternalContext().redirect("./edit.jsf?id=" + item.getId());
        
    }
    
    public void onLoad() {
        item = serviceTunjanganKesehatan.onLoad(item.getId());
            lTunjanganKesehatanDetail = serviceTunjanganKesehatan.onLoadListDetail(item.getId());
        
        
        try {
            String tgl = "01/" + Integer.toString(item.getMonth()) + "/" + item.getYear();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            datestart = format.parse(tgl);
        } catch (ParseException ex) {
            Logger.getLogger(ControllerTunjanganKesehatan.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void ubah(Character st) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        try {
            item.setStatus(st);
            
                serviceTunjanganKesehatan.ubah(item, lTunjanganKesehatanDetail);
            
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Informasi", "Data Berhasil Diinput"));
            
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Data Gagal di Input", ""));
        }
    }

    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Cancelled", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    
    public void onDeleteClicked(TunjanganKesehatanDetail tunjanganKesehatanDetail) {
        lTunjanganKesehatanDetail.remove(tunjanganKesehatanDetail);
    }
    
    public void extend() {
        lTunjanganKesehatanDetail.add(new TunjanganKesehatanDetail());
    }
    
    
    
    public void onPegawaiSelect(TunjanganKesehatanDetail pm, Integer i) {
        pm.setId_pegawai(pegawaiAutoComplete.getPegawai().getId_pegawai());
        pm.setNama(pegawaiAutoComplete.getPegawai().getNama());
        lTunjanganKesehatanDetail.set(i, pm);
    }
    
    
}
