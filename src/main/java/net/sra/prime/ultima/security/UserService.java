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
package net.sra.prime.ultima.security;

import net.sra.prime.ultima.service.ServicePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 *
 * @author Syamsu
 */
@Service
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    ServicePage servicePage;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {

        String usernamenya = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        try {
            servicePage.updateLastLogin(usernamenya);
            servicePage.updateNewLogin(usernamenya);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
