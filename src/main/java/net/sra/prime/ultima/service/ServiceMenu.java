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
package net.sra.prime.ultima.service;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.sra.prime.ultima.db.mapper.MapperMenu;
import net.sra.prime.ultima.entity.HakAkses;
import net.sra.prime.ultima.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hairian
 */
@Service
@Setter
@Getter
public class ServiceMenu {

    @Autowired
    MapperMenu referensi;

    @Transactional(readOnly = true)
    public List<Menu> onLoadListMenu() {
        return referensi.selectAllMenu();
    }

    @Transactional(readOnly = true)
    public Menu onLoad(Integer id) {
        return referensi.selectOneMenu(id);
    }

    @Transactional(readOnly = true)
    public Integer selectMaxDetail(Integer parent) {
        return referensi.selectMaxidDetail(parent);
    }

    @Transactional(readOnly = true)
    public Integer selectMaxGroup() {
        return referensi.selectMaxidGroup();
    }

    @Transactional(readOnly = false)
    public void tambahMenu(Menu menu) {
        referensi.insert(menu);
    }

    @Transactional(readOnly = false)
    public void updatehMenu(Menu menu) {
        if (!menu.getId().equals(menu.getIdlama()) ) {
            referensi.insert(menu);
            referensi.updateHakAkses(menu.getId(), menu.getIdlama());
            referensi.delete(menu.getIdlama());
        }else{
            referensi.updateMenu(menu);
        }
    }

    @Transactional(readOnly = false)
    public void deleteMenu(Integer id) {
        referensi.deleteMenuFromHakAkses(id);
        referensi.delete(id);
    }

    @Transactional(readOnly = true)
    public List<Menu> onLoadList(String usernamenya) {
        return referensi.selectAll(usernamenya);
    }

    @Transactional(readOnly = true)
    public List<Menu> onLoadListParent() {
        return referensi.selectAllParent();
    }

    @Transactional(readOnly = true)
    public Integer checkChild(Integer id, String usernamenya) {
        return referensi.checkChild(id, usernamenya);
    }

    @Transactional(readOnly = true)
    public List<Menu> subMenuNya(Integer id, String usernamenya) {
        return referensi.subMenuNya(id, usernamenya);
    }

    @Transactional(readOnly = true)
    public List<Menu> subMenu(Integer id) {
        return referensi.subMenu(id);
    }

    @Transactional(readOnly = true)
    public List<Menu> menuUtama() {
        return referensi.selectAllMenuUtama();
    }

    @Transactional(readOnly = true)
    public List<HakAkses> menuUtamaHakAkses(String usernya) {
        return referensi.selectAllMenuUtamaHakAkses(usernya);
    }

    @Transactional(readOnly = true)
    public List<HakAkses> menuHakAkses(String usernya, Integer parent) {
        return referensi.selectAllMenuHakAkses(usernya, parent);
    }

    @Transactional(readOnly = false)
    public void tambah(List<Integer> selectedMenuUtama,
            List<Integer> selectedMenuAdmin,
            List<Integer> selectedMenuMarketeing,
            List<Integer> selectedMenuWarehouse,
            List<Integer> selectedMenuHrd,
            List<Integer> selectedMenuMaster,
            List<Integer> selectedMenuLaporan,
            List<Integer> selectedMenuAccounting,
            List<Integer> selectedMenuMaintenance,
            List<Integer> selectedMenuSetting,
            List<Integer> selectedMenuPayroll,
            List<Integer> selectedMenuKpi,
            String user) {
        referensi.deleteHakAkses(user);

        for (int i = 0; i < selectedMenuUtama.size(); i++) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(selectedMenuUtama.get(i));
            referensi.insertHakAkses(item);
        }
        if (selectedMenuAdmin.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(100);
            referensi.insertHakAkses(item);
            for (int i = 0; i < selectedMenuAdmin.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuAdmin.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuMarketeing.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(300);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuMarketeing.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuMarketeing.get(i));
                referensi.insertHakAkses(item);
            }

        }

        if (selectedMenuWarehouse.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(200);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuWarehouse.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuWarehouse.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuHrd.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(500);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuHrd.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuHrd.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuMaster.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(600);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuMaster.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuMaster.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuLaporan.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(700);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuLaporan.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuLaporan.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuAccounting.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(400);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuAccounting.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuAccounting.get(i));
                referensi.insertHakAkses(item);
            }
        }

        if (selectedMenuMaintenance.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(800);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuMaintenance.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuMaintenance.get(i));
                referensi.insertHakAkses(item);
            }
        }
        
        if (selectedMenuSetting.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(900);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuSetting.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuSetting.get(i));
                referensi.insertHakAkses(item);
            }
        }
        
        if (selectedMenuPayroll.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(1000);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuPayroll.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuPayroll.get(i));
                referensi.insertHakAkses(item);
            }
        }
        
        if (selectedMenuKpi.size() > 0) {
            HakAkses item = new HakAkses();
            item.setUsernamenya(user);
            item.setId_menu(1100);
            referensi.insertHakAkses(item);

            for (int i = 0; i < selectedMenuKpi.size(); i++) {
                item = new HakAkses();
                item.setUsernamenya(user);
                item.setId_menu(selectedMenuKpi.get(i));
                referensi.insertHakAkses(item);
            }
        }

    }

    @Transactional(readOnly = true)
    public Integer[] hakAkses(String usernya) {
        return referensi.selectAllHakAkses(usernya);
    }
}
