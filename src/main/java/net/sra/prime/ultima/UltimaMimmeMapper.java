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
package net.sra.prime.ultima;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Syamsu
 *
 * ini diambil dari web.xml yang mime-mapping
 */
@Configuration
public class UltimaMimmeMapper implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer cesc) {
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);

        mappings.add("otf", "font/opentype otf;");
        mappings.add("ttf", "font/truetype ttf");
        mappings.add("woff", "application/font-woff");
        mappings.add("woff2", "application/font-woff2");
        mappings.add("eot", "application/vnd.ms-fontobject");
        mappings.add("eot?#iefix", "application/vnd.ms-fontobject");
        mappings.add("svg", "image/svg+xml");
        mappings.add("svg#exosemibold", "image/svg+xml");
        mappings.add("svg#exobolditalic", "image/svg+xml");
        mappings.add("svg#exomedium", "image/svg+xml");
        mappings.add("svg#exoregular", "image/svg+xml");
        mappings.add("svg#fontawesomeregular", "image/svg+xml");

        cesc.setMimeMappings(mappings);

    }

}
