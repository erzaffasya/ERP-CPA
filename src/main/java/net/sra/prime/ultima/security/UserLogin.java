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

import java.io.Serializable;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Syamsu
 */
@Named("ulogin")
@SessionScoped
@Setter
@Getter
public class UserLogin implements Serializable {

    private static final long serialVersionUID = 8636108317550345689L;

    private String user = "";
    private String pass = "";
    private String msg = "Masukan User dan Password";

    public void Login() {

    }

}
