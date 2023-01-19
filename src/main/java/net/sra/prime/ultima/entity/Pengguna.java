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
package net.sra.prime.ultima.entity;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author Hairian
 */
@Mapper
@Getter
@Setter

public class Pengguna implements java.io.Serializable {

    private String id_pegawai;
    private String id_lama;
    private String usernamenya;
    private String passwordnya;
    private String nama;
    private Date dibuat;
    private Date loginterkahir;
    private Boolean enabled;
    private Integer jmlogin;
    private Date loginterbaru;
    private String roles;
    private String jabatan;
    private String kantor;
    private String departemen;
    private String passwordold;
    private String passwordconfirm;
    private String pinnya;
    private String pinold;
    private String pinconfirm;
}
