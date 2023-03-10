/*
 * Copyright 2020 JoinFaces.
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

import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author hairian
 */
@Named
//@ViewScoped
@ApplicationScoped
public class ExporterController implements Serializable {

    private static final long serialVersionUID = 20120316L;

    private Boolean customExporter;

    public ExporterController() {
        customExporter = false;
    }

    public Boolean getCustomExporter() {
        return customExporter;
    }

    public void setCustomExporter(Boolean customExporter) {
        this.customExporter = customExporter;
    }
}
