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

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sra.prime.ultima.security.captcha.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 *
 * @author Syamsu
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private final String redirectUrl;

    @Autowired
    CaptchaService captchaService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public AjaxAwareAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        redirectUrl = loginFormUrl;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
//        Enumeration bb = ((HttpServletRequest) request).getHeaderNames();
        String ajaxHeader = ((HttpServletRequest) request).getHeader("Faces-Request");
        boolean isAjax = (ajaxHeader != null) && ajaxHeader.contains("ajax");
        if (isAjax) {
            performRedirect(request, response);
//            response.sendRedirect("/error/expired.jsf");
//            HttpSession session = ((HttpServletRequest) request).getSession(false);
//            Page page = (Page) request.getServletContext().getAttribute("page");
//            try {                
//                if (page == null){
//                    page = new Page();
//                    request.getServletContext().setAttribute("page", page);
//                }
//                page = (Page) request.getServletContext().getAttribute("page");
//                page.redirectExpired();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ajax REquest Denied (Session Expired)");
        } else {
//            String captchResponse = request.getParameter("g-recaptcha-response");
//            captchaService.processResponse(captchResponse);
            response.sendRedirect(redirectUrl);
//            super.commence(request, response, authException);
        }
    }

    private void performRedirect(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
//        String contextPath = request.getContextPath();
        logger.debug("Session expired during ajax request, redirecting to '{}'", redirectUrl);
        String ajaxRedirectXml = createAjaxRedirectXml(redirectUrl);
        logger.debug("Ajax partial response to redirect: {}", ajaxRedirectXml);

        response.setContentType("text/xml");
        response.getWriter().write(ajaxRedirectXml);
    }

    private String createAjaxRedirectXml(String redirectUrl) {
        return new StringBuilder()
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<partial-response><redirect url=\"")
                .append(redirectUrl)
                .append("\"></redirect></partial-response>").toString();
    }

}
