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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 *
 * @author Syamsu
 */
@EnableWebSecurity
public class SecurityConfig {

    public static final String URL_LOGOUT = "/logout";
    public static final String URL_LOGIN = "/login.jsf";

    @Autowired
    DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("SELECT usernamenya, passwordnya, enabled FROM pengguna WHERE usernamenya = ?")
                .authoritiesByUsernameQuery("SELECT a.usernamenya, c.roles FROM pengguna a"
                        + " INNER JOIN hakakses b ON a.usernamenya=b.usernamenya "
                        + " INNER JOIN menu c ON b.id_menu=c.id "
                        + " WHERE a.usernamenya = ?")
                .passwordEncoder(new Md5PasswordEncoder());
        
//                .authoritiesByUsernameQuery("SELECT a.usernamenya, d.roles FROM pengguna a"
//                        + " INNER JOIN pegawai b ON a.id_pegawai=b.id_pegawai "
//                        + " INNER JOIN master_jabatan c ON b.id_jabatan_new=c.id_jabatan "
//                        + " INNER JOIN hr_departemen d ON b.id_departemen_new=d.id_departemen"
//                        + " WHERE a.usernamenya = ?")
//                .passwordEncoder(new Md5PasswordEncoder());


//        auth.inMemoryAuthentication()
//                .withUser("siskohat").password("EmKH9876mnJha")
//                .authorities("ROLE_ANONYMOUS");
    }

    @Configuration
    //@Order(1)
    public static class MainWebSecurity extends WebSecurityConfigurerAdapter {

        public static String URL_LOGOUT = "/logout";

        private static final String[] IGNORE_PATH
                = {"/public/**",
                    "/error/**",
                    "/javax.faces.resource/**",
                    "/img/**",
                    "/dist/**",
                    "/js/**",
                    "/css/**",
                    "/fonts/**",
                    "/pdfile**",
                    "/pdfile.pdf"//,
//                    "/pihk/public/**",
//                    "/pihk/error/**",
//                    "/pihk/javax.faces.resource/**",
//                    "/pihk/img/**",
//                    "/pihk/dist/**",
//                    "/pihk/js/**",
//                    "/pihk/css/**",
//                    "/pihk/fonts/**",
//                    "/pihk/pdfile**",
//                    "/pihk/pdfile.pdf"
                };

        private static final String[] CACHE_PATH
                = {"/javax.faces.resource/**", "/dist/**", "/js/**",
                    "/css/**", "/img/**"};

//        @Autowired
//        FilterBeforeLogin filterBeforeLogin;
//
//        @Autowired
//        FilterAfterLogin filterAfterLogin;
//        @Autowired
//        CheckGudangorCabangBeforeFilter checkGudangorCabangBeforeFilter;
//
//        @Autowired
//        CheckGudangorCabangAfterFilter checkGudangorCabangAfterFilter;

        @Autowired
        CustomSuccessHandler customSuccessHandler;

        @Override
        protected void configure(HttpSecurity http) {
            try {
                http.csrf().disable();

                http.sessionManagement()
                        .and()
                        .userDetailsService(userDetailsService())
                        .authorizeRequests()
                        .antMatchers("/", "/index.jsf", "/login.jsf", "/login.jsf?error=true").permitAll()
                        .antMatchers("admin-*.jsf", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                        .and()
                        .formLogin().loginPage("/login.jsf").permitAll()
                        .failureUrl("/login.jsf?error=true")
                        .successHandler(customSuccessHandler)
                        //                        .defaultSuccessUrl("/dashboard.jsf", true)
                        .usernameParameter("usernamenya")
                        .passwordParameter("passwordnya")
                        .and()
                        .logout().logoutSuccessUrl("/index.jsf?logout=true").and()
                        .exceptionHandling().authenticationEntryPoint(new AjaxAwareAuthenticationEntryPoint("/error/expired.html"));

                /**
                 * http.sessionManagement() .and()
                 * .userDetailsService(userDetailsService())
                 * .authorizeRequests() .antMatchers("/", "/*.jsf").permitAll()
                 * .antMatchers("admin-*.jsf", "/admin/**").hasRole("ADMIN")
                 * .anyRequest().authenticated() .and()
                 * .rememberMe().key("siskohat-mudah-login").tokenValiditySeconds(1209600)
                 * .and() .formLogin() .loginPage("/login.jsf") .permitAll()
                 * .failureUrl("/login.jsf?error=true") .defaultSuccessUrl("/",
                 * true) .usernameParameter("usernamenya")
                 * .passwordParameter("passwordnya") .and() .logout()
                 * .logoutSuccessUrl("/index.jsf?logout=true") .and()
                 * .exceptionHandling() .authenticationEntryPoint(new
                 * AjaxAwareAuthenticationEntryPoint("./error/expired.html"));
                 *
                 */
                //http.addFilterBefore(filterBeforeLogin, UsernamePasswordAuthenticationFilter.class);
                //http.addFilterAfter(filterAfterLogin, UsernamePasswordAuthenticationFilter.class);
                //http.addFilterBefore(checkGudangorCabangBeforeFilter, UsernamePasswordAuthenticationFilter.class);
                //http.addFilterAfter(checkGudangorCabangAfterFilter, UsernamePasswordAuthenticationFilter.class);

                http.headers().addHeaderWriter(new DelegatingRequestMatcherHeaderWriter(
                        new AntPathRequestMatcher("/fonts/**"), (HttpServletRequest request, HttpServletResponse response) -> {
                            response.addHeader("Cache-Control", "private, max-age=31536000");
                        })).defaultsDisabled();

                for (String path : CACHE_PATH) {
                    http.headers().addHeaderWriter(new DelegatingRequestMatcherHeaderWriter(
                            new AntPathRequestMatcher(path), (HttpServletRequest request, HttpServletResponse response) -> {
                                response.addHeader("Cache-Control", "private, max-age=2628000");
                            })).defaultsDisabled();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(IGNORE_PATH);
        }

    }

//    @Configuration
//    public static class SecondaryWebSecurity extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) {
//            try {
//                http.httpBasic();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
}

//@EnableWebSecurity
//public class MultiHttpSecurityConfig {
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) { 1
//		auth
//			.inMemoryAuthentication()
//				.withUser("user").password("password").roles("USER").and()
//				.withUser("admin").password("password").roles("USER", "ADMIN");
//	}
//
//	@Configuration
//	@Order(1)                                                        2
//	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
//		protected void configure(HttpSecurity http) throws Exception {
//			http
//				.antMatcher("/api/**")                               3
//				.authorizeRequests()
//					.anyRequest().hasRole("ADMIN")
//					.and()
//				.httpBasic();
//		}
//	}
//
//	@Configuration                                                   4
//	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
//
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http
//				.authorizeRequests()
//					.anyRequest().authenticated()
//					.and()
//				.formLogin();
//		}
//	}
//}
