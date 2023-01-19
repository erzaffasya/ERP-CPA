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
package net.sra.prime.ultima.primefaces.ultima.component.menu;

/**
 *
 * @author hairian
 */
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.entity.Menu;
import net.sra.prime.ultima.service.ServiceMenu;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@Named
@ViewScoped
@Setter
@Getter
public class MenuView {

    private MenuModel model;
    private Menu menu;
    private List<Menu> lMenu = new ArrayList<>();

    @Autowired
    ServiceMenu serviceMenu;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        lMenu = serviceMenu.onLoadList(user.getUsername());
        DefaultSubMenu secondSubmenu=  new DefaultSubMenu();
        DefaultMenuItem item = new DefaultMenuItem();

        for (int i = 0; i < lMenu.size(); i++) {
            if (serviceMenu.checkChild(lMenu.get(i).getId(),user.getUsername()) == null) {
                item = new DefaultMenuItem();
                item.setValue(lMenu.get(i).getName());

                item.setOutcome(lMenu.get(i).getOutcome());
                item.setIcon(lMenu.get(i).getIcon());
                item.setIconPos("left");
                item.setId(Integer.toString(lMenu.get(i).getId()));
                model.addElement(item);
            } else {
                secondSubmenu = new DefaultSubMenu(lMenu.get(i).getName());
                secondSubmenu.setIcon(lMenu.get(i).getIcon());
                secondSubmenu.setId(Integer.toString(lMenu.get(i).getId()));
                secondSubmenu.setLabel(lMenu.get(i).getName());
                List<Menu> lMenutmp = serviceMenu.subMenuNya(lMenu.get(i).getId(),user.getUsername());
                for (int j = 0; j < lMenutmp.size(); j++) {
                    item = new DefaultMenuItem();
                    item.setValue(lMenutmp.get(j).getName());
                    item.setId(Integer.toString(lMenu.get(i).getId()));
                    item.setOutcome(lMenutmp.get(j).getOutcome());
                    item.setIcon(lMenutmp.get(j).getIcon());
                    item.setIconPos("left");
                    secondSubmenu.addElement(item);
                    
                }
                model.addElement(secondSubmenu);
            }

            //
        }

//        //Second submenu
//        item.setValue("Save");
//
//        item.setOutcome("/dashboard");
//        item.setIcon("fa fa-fw fa-dashboard");
//        item.setIconPos("left");
//        secondSubmenu.addElement(item);
//
//        item = new DefaultMenuItem("Delete");
//        item.setCommand("#{menuView.delete}");
//        item.setAjax(false);
//        item.setStyleClass("ultima-menu");
//        secondSubmenu.addElement(item);
//
//        item = new DefaultMenuItem("Redirect");
//        item.setCommand("#{menuView.redirect}");
//        secondSubmenu.addElement(item);
        
    }

    public MenuModel getModel() {
        return model;
    }

    public void save() {
        addMessage("Success", "Data saved");
    }

    public void update() {
        addMessage("Success", "Data updated");
    }

    public void delete() {
        addMessage("Success", "Data deleted");
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
