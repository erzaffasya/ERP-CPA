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
import net.sra.prime.ultima.entity.KategoriBarangConsumable;
import net.sra.prime.ultima.service.ServiceKategoriBarangConsumable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author erza
 */
@Named("optionKategoriBarangConsumable")
//@ConversationScoped
@ViewScoped
@Getter
@Setter
public class OptionKategoriBarangConsumable implements Serializable {

    private static final long serialVersionUID = -94660532783957684L;

    @Autowired
    ServiceKategoriBarangConsumable serviceKategoriBarangConsumable;

    //private MapperReferensi mapper;
    @PostConstruct
    public void awal() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getAutowireCapableBeanFactory().autowireBean(this);
        //mapper = sqlSessionFactory.openSession().getMapper(MapperReferensi.class);

    }

    public Map<String, String> getKategoriBarangConsumable() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        try {
            List<KategoriBarangConsumable> list = serviceKategoriBarangConsumable.onLoadList();
            list.stream().forEach((data) -> {
                map.put(data.getKategori_barang(), data.getKode());
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

   

}
