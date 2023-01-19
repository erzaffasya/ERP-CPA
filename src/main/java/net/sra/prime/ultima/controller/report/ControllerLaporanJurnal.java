package net.sra.prime.ultima.controller.report;

import net.sra.prime.ultima.controller.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperAccGl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import net.sra.prime.ultima.entity.AccGlDetail;
import net.sra.prime.ultima.entity.AccGlTrans;
import net.sra.prime.ultima.service.ServiceLaporanJurnal;

/**
 *
 * @author Hairian
 */
@Named
@ViewScoped
@Setter
@Getter
public class ControllerLaporanJurnal implements java.io.Serializable {

    private static final long serialVersionUID = -1767732220818787987L;
    private List<AccGlDetail> lAccGlDetails = new ArrayList<>();
    private AccGlDetail item;
    private List<AccGlTrans> lAccGlTrans;
   
    @Autowired
    ServiceLaporanJurnal serviceLaporanJurnal;
    
    @Inject
    private Page page;

    @PostConstruct
    public void init() {
        initItem();

    }

    private void initItem() {
        item = new AccGlDetail();
    }

    public void search() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
    }

    public void onLoadList() {
        try {
            lAccGlTrans = serviceLaporanJurnal.onLoad();
            for(int i=0; i<lAccGlTrans.size();i++){
                AccGlTrans accGlTrans = lAccGlTrans.get(i);
                accGlTrans.setAccGlDetails(serviceLaporanJurnal.onLoadDetail(accGlTrans.getGl_number()));
                lAccGlTrans.set(i, accGlTrans);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AccGlTrans> getDataAccGlTrans() {
        return lAccGlTrans;
    }

}
