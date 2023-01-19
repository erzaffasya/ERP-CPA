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

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

/**
 *
 * @author Syamsu
 */
public class HandleResourceListener implements SystemEventListener {

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof UIViewRoot;
    }
    private static final String SCRIPT_RENDERER = "javax.faces.resource.Script";
    private static final String CSS_RENDERER = "javax.faces.resource.Stylesheet";

    private String[] CSS = {
        //"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/iCheck/1.0.2/skins/all.css\"/>",
        //"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.43/css/bootstrap-datetimepicker.min.css\"/>",
        //"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/css/bootstrap-notify.min.css\"/>",
        "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\" />",
        "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css\"/>",
        //"<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/css/select2.min.css\"/>",
        "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/animate.css/3.5.2/animate.min.css\"/>"
    //"<link rel=\"stylesheet\" href=\"https://cdn.datatables.net/1.10.13/css/jquery.dataTables.min.css\"/>",
    //"<link rel=\"stylesheet\" href=\"https://cdn.datatables.net/buttons/1.2.4/css/buttons.dataTables.min.css\"/>"
    };

    private String[] JS = { //        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/iCheck/1.0.2/icheck.min.js\"></script>",
    //        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.16.0/moment.min.js\"></script>",
    //        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.16.0/moment-with-locales.min.js\"></script>",
    //        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.43/js/bootstrap-datetimepicker.min.js\"></script>",
    //        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-notify/0.2.0/js/bootstrap-notify.min.js\"></script>",
    //        "<script src=\"https://cdn.jsdelivr.net/jquery.scrollup/2.4.0/jquery.scrollUp.min.js\"></script>",
    //        "<script src=\"https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js\"></script> ",
    //        "<script src=\"https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js\"></script> ",
    //        "<script src=\"//cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js\"></script> ",
    //        "<script src=\"//cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js\"></script> ",
    //        "<script src=\"//cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js\"></script> ",
    //        "<script src=\"//cdn.datatables.net/buttons/1.2.4/js/buttons.html5.min.js\"></script> ",
    //        "<script src=\"//cdn.datatables.net/buttons/1.2.4/js/buttons.print.min.js\"></script> ",
    //        "<script src=\"//cdn.datatables.net/buttons/1.2.4/js/buttons.flash.min.js\"></script> "//,
    //"<script src=\"https://cdnjs.cloudflare.com/ajax/libs/pdfobject/2.0.201604172/pdfobject.min.js\"></script> "
    };

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        final UIViewRoot source = (UIViewRoot) event.getSource();
        final FacesContext context = FacesContext.getCurrentInstance();

        for (UIComponent resource : source.getComponentResources(context, "head")) {
            Map mm = resource.getAttributes();
            final String resourceLibrary = (String) resource.getAttributes().get("library");
            final String resourceName = (String) resource.getAttributes().get("name");

//            if (resourceLibrary != null && "primefaces".contains(resourceLibrary.toLowerCase())) {
//                if (resourceName != null && resourceName.toLowerCase().contains("jquery.js")) {
//                    source.removeComponentResource(context, resource);
//                }
//            }
//
//            // if (resourceLibrary != null && "bsf".contains(resourceLibrary)) {
////            if (resourceName != null && resourceName.toLowerCase().contains("font-awesome")) {
////                source.removeComponentResource(context, resource);
//////            } else if (resourceName != null && resourceName.contains("moment")) {
//////                source.removeComponentResource(context, resource);
////            } else 
//            if (resourceName != null && resourceName.toLowerCase().contains("datatable")) {
//                source.removeComponentResource(context, resource);
//            } else if (resourceName != null && resourceName.toLowerCase().contains("datatables")) {
//                source.removeComponentResource(context, resource);
////            } else if (resourceName != null && resourceName.contains("bootstrap-datetimepicker")) {
////                source.removeComponentResource(context, resource);
//            } else if (resourceName != null && resourceName.toLowerCase().contains("animate")) {
//                source.removeComponentResource(context, resource);
////            } else if (resourceName != null && resourceName.contains("bootstrap-notify")) {
////                source.removeComponentResource(context, resource);
//            } else if (resourceName != null && resourceName.toLowerCase().contains("jquery.scrollUp")) {
//                source.removeComponentResource(context, resource);
//            }
            // }
        }

        if (source.isRendered()) {

            for (String css : CSS) {
                source.addComponentResource(context, new CDNGenerator().setUrl(css), "head");
            }

            for (String js : JS) {
                source.addComponentResource(context, new CDNGenerator().setUrl(js), "head");
            }

            //source.addComponentResource(context, new CDNGenerator().setUrl("<link rel=\"stylesheet\" href=\"./css/style.css\" />"), "head");
        }
    }
}
