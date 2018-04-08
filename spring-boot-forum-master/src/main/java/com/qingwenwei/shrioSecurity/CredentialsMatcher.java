package com.qingwenwei.shrioSecurity;//package com.li.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

public class CredentialsMatcher extends SimpleCredentialsMatcher {

    Logger logger = LogManager.getLogger(CredentialsMatcher.class);
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {  //将 AuthenticationToken和Authenticationinfo传入。
        UsernamePasswordToken utoken=(UsernamePasswordToken) token;
        //获得用户输入的密码:(可以采用加盐(salt)的方式去检验)
        String inPassword = new String(utoken.getPassword());
        logger.warn(inPassword);
        //获得数据库中的密码
        String dbPassword=(String) info.getCredentials();
        logger.warn(dbPassword);
        //进行密码的比对
        return this.equals(inPassword, dbPassword);
    }

}