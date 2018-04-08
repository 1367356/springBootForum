package com.qingwenwei.shrioSecurity;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.LinkedHashMap;

/**
 * shiro的配置类
 * @author Administrator
 *
 */
//@Configuration
public class ShiroConfiguration {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean(name="shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
        ShiroFilterFactoryBean bean=new ShiroFilterFactoryBean();
        bean.setSecurityManager(manager);

        //配置访问权限
        LinkedHashMap<String, String> filterChainDefinitionMap=new LinkedHashMap<>();
        filterChainDefinitionMap.put("/user/settings", "authc"); //认证通过就调到要访问的页面，不通过就跳到登录页面。
        filterChainDefinitionMap.put("/logOut*","logout");   //shrio自带登出，当访问这个url时，退出登录。
        filterChainDefinitionMap.put("/images/**", "anon");    //无需验证的资源，静态资源放行，可以减少验证次数。
        filterChainDefinitionMap.put("/chartjs/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/post/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/user/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/category/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/avatar/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/user/registration", "anon");  //游客资源
        filterChainDefinitionMap.put("/fonts/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/css/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/bootstrap-select/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/bootstrap-datetimepicker/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/custom/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/daterangepicker/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/js/**", "anon");  //游客资源
        filterChainDefinitionMap.put("/", "anon");  //游客资源

        filterChainDefinitionMap.put("/**", "authc");//表示需要认证才可以访问，被拦截之后shrio SecurityManager就验证，通过则放行，不过则登录。
        filterChainDefinitionMap.put("/*.*", "authc");
        filterChainDefinitionMap.put("/*", "authc");

        //配置登录的url和登录成功的url
        bean.setLoginUrl("/user/login");  //校验不通过，就返回/login
//        bean.setSuccessUrl("/");
        //未授权界面;
        bean.setUnauthorizedUrl("/403");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }
    //配置核心安全事务管理器
    @Bean(name="securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm) {
        System.err.println("--------------shiro已经加载----------------");
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        manager.setRealm(authRealm);
        return manager;
    }
    //配置自定义的权限登录器
    @Bean(name="authRealm")
    public AuthRealm authRealm(@Qualifier("credentialsMatcher") CredentialsMatcher matcher) {
        AuthRealm authRealm=new AuthRealm();
        authRealm.setCredentialsMatcher(matcher);
        return authRealm;
    }
    //配置自定义的密码比较器
    @Bean(name="credentialsMatcher")
    public CredentialsMatcher credentialsMatcher() {
        return new CredentialsMatcher();
    }
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator=new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }
}