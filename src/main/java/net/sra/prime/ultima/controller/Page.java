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
package net.sra.prime.ultima.controller;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Gudang;
import net.sra.prime.ultima.entity.InternalKantorCabang;
import net.sra.prime.ultima.entity.InternalPerusahaan;
import net.sra.prime.ultima.entity.Pegawai;
import net.sra.prime.ultima.entity.Pengguna;
import net.sra.prime.ultima.entity.hr.SettingApproval;
//import net.sra.prime.ultima.security.CheckGudangorCabangAfterFilter;
import net.sra.prime.ultima.security.SecurityConfig;
import net.sra.prime.ultima.security.captcha.CaptchaSettings;
import net.sra.prime.ultima.service.ServicePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Syamsu
 */
@Named
@SessionScoped // Selama browser belum ditutup, maka @PostConstruct akan dipanggil satu kali
@Getter
@Setter
public class Page implements java.io.Serializable {

    private static final long serialVersionUID = 5366951839372161258L;

    private String layout = " hold-transition skin-green fixed ";

    private boolean edit = false;
    private boolean usernya;
    private String name;
    private String noRegistrasi;

    private Map<String, String> storage;
    private Pengguna pengguna = new Pengguna();

    private Integer idKantor;

    private Integer idDepartemen;

    @Autowired
    private CaptchaSettings captchaSettings;

//    @Autowired
//    CheckGudangorCabangAfterFilter checkGudangorCabangAfterFilter;
    
    @Autowired
    ServicePage servicePage;

    @PostConstruct
    public void init() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        name = user.getUsername();
        getMyName();

    }

    public String getMyName() {
        try {
            if (name != null) {
                pengguna = servicePage.selectOnePengguna(name);
                if (pengguna != null) {
                    return pengguna.getNama();
                }
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return "TIdak dikenal";
    }

    public Pegawai getMyPegawai() {
        Pegawai pegawai = new Pegawai();
        try {
            if (name != null) {
                pengguna = servicePage.selectOnePengguna(name);
                if (pengguna != null) {
                    pegawai = servicePage.MyPegawai(pengguna.getId_pegawai());

                }
            }

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return pegawai;
    }

    public void logout() throws Exception {

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.invalidateSession();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        context.redirect(context.getRequestContextPath() + SecurityConfig.URL_LOGOUT);

    }

    public String getSessionExpire() {
        return "Session Expired, silahkan login ulang";
    }

    public Gudang getMyGudang() {
        return servicePage.MyGudang(name);
    }

    public InternalKantorCabang getMyKantor() {
        return servicePage.MyKantor(name);
    }

    public InternalPerusahaan getMyInternalPerusahaan() {
         return servicePage.MyPerusahaan(name);
    }
    
    public  Character getValueBegawi(String id) {
        return servicePage.onLoadValueBegawai(id);
    }

    public Boolean checkRole(String role) {
        return AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains(role);
    }
    
    public void tidakBoleh() throws  IOException{
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/401.xhtml");
    }
    
    public SettingApproval settinganApproval(Integer id) {
        return servicePage.settingApproval(id);
    }

    
}
