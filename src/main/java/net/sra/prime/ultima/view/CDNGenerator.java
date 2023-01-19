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
package net.sra.prime.ultima.view;

import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
//import net.bootsfaces.C;

/**
 *
 * @author Syamsu
 */
public class CDNGenerator extends UIComponentBase {

    private String url;

    public CDNGenerator() {
        setRendererType(null);
    }

    public CDNGenerator setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        ResponseWriter responseWriter = fc.getResponseWriter();
        responseWriter.append(url);
    }

    @Override
    public String getFamily() {
        return "internal.component";
    }

}
