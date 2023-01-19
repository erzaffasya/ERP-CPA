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
package net.sra.prime.ultima.admin;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Kendaraan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import net.sra.prime.ultima.entity.KendaraanJenis;
import net.sra.prime.ultima.service.ServiceKendaraan;
import net.sra.prime.ultima.service.ServiceKendaraanJenis;

/**
 *
 * @author Hairian
 */
@Named("optionKendaraan")
//@ConversationScoped
@ViewScoped
@Getter
@Setter
public class OptionKendaraan implements Serializable {

    private static final long serialVersionUID = -94660532783957684L;

    @Autowired
    ServiceKendaraanJenis serviceKendaraanJenis;
    
    @Autowired
    ServiceKendaraan serviceKendaraan;
    
    
    //private MapperReferensi mapper;
    @PostConstruct
    public void awal() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getAutowireCapableBeanFactory().autowireBean(this);
        //mapper = sqlSessionFactory.openSession().getMapper(MapperReferensi.class);

    }

    public Map<String, String> getJenis() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<KendaraanJenis> list = serviceKendaraanJenis.onLoadList();
            list.stream().forEach((data) -> {
                map.put(data.getJenis(), data.getId_jenis());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    private String id_jenis;
    public Map<String, String> getKendaraan() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<Kendaraan> list = serviceKendaraan.onLoadListByJenis(id_jenis);
            list.stream().forEach((data) -> {
                map.put(data.getNopol(), Integer.toString(data.getId()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    
    
}
