package com.example.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //首页所有人可以访问，功能页只有登录过的人才能访问
        //请求权限的规则
        //.antMatchers("/departList")括号里填的是url，请求的url，不是文件的位置
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/*List").hasAnyRole("admin", "student");

        //没有权限默认会到登录页面，需要开启登录页面
        //定制登录页loginPage("/login");
        http.formLogin()
                .loginPage("/login").loginProcessingUrl("/login")
                .failureUrl("/login?error");

        //注销，开启注销功能，跳到首页
        //防止网站攻击
        http.csrf().disable();//关闭csrf功能，注销失败的原因
        http.logout().logoutSuccessUrl("/");

        //开启记住我功能，cookie，默认保存两周
        http.rememberMe().rememberMeParameter("remember-me");
    }

    //认证
    //密码编码：PasswordEncoder，就是密码加密，不然不安全
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        auth.inMemoryAuthentication().passwordEncoder(encoder)
                .withUser("admin").password(encoder.encode("admin")).roles("admin", "student")
                .and().withUser("student").password(encoder.encode("123456")).roles("student");
    }
}
