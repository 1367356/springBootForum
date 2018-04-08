package com.qingwenwei.shrioSecurity;

import com.qingwenwei.persistence.model.User;
import com.qingwenwei.service.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {

    Logger logger = LogManager.getLogger(AuthRealm.class);
    @Autowired
    private UserServiceImpl userService;
    JedisPoolUtil jpu=new JedisPoolUtil();
    private Jedis jedis=jpu.getJedis();

    //认证.登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken utoken=(UsernamePasswordToken) token;//获取用户输入的token
        String username = utoken.getUsername();
        logger.debug(username);
        User user;
        byte[] byt=jedis.get(username.getBytes());
        Object obj=null;
        if (byt!=null && byt.length != 0) {
            obj=new Serialize().unserizlize(byt);
        }
        if(obj!=null && obj instanceof User){
//            return (User)obj;
            logger.debug("走redis");
            user=(User)obj;
        }else {
            user = userService.findByUsername(username);  //使用mybatis从数据库中得到用户
            if(user!=null){
                logger.debug("走mybatis");
                jedis.set(username.getBytes(), new Serialize().serialize(user));
            }
        }
//        logger.warn(username);
//        //User user = userService.findUserByName(username);  //使用mybatis从数据库中得到用户，也可以通过redis存放用户名和密码。
//        logger.warn(user.getUsername());

        return new SimpleAuthenticationInfo(user, user.getPassword(),this.getClass().getName());//放入shiro.调用CredentialsMatcher检验密码
    }

//    //授权
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        return null;
//    }
    /**
     * 获取身份信息，我们可以在这个方法中，从数据库获取该用户的权限和角色信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {

//        User user=(User) principal.fromRealm(this.getClass().getName()).iterator().next();//获取session中的用户
        User user= (User) principal.getPrimaryPrincipal();
        List<String> permissions=new ArrayList<>();
        Set<String> roles = user.getRolesSet();
        if(roles.size()>0) {
            for(String role : roles) {
                permissions.add(role);
                }
            }
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.addStringPermissions(permissions);//将权限放入shiro中.
        return info;
    }

//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        logger.info("----------doGetAuthorizationInfo方法被调用----------");
//        String username = (String) getAvailablePrincipal(principals);
//        //通过用户名从数据库获取权限字符串
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        //权限
//        Set<String> s = new HashSet<String>();
//        s.add("printer:print");
//        s.add("printer:query");
//        info.setStringPermissions(s);
//        //角色
//        Set<String> r = new HashSet<String>();
//        r.add("role1");
//        info.setRoles(r);
//
//        return info;
//    }

//
//    /**
//     38      * 授权,只有成功通过<span style="font-family: Arial, Helvetica, sans-serif;">doGetAuthenticationInfo方法的认证后才会执行。</span>
//     39      */
//40     @Override
//41     protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        42         // 从 principals获取主身份信息
//        43         // 将getPrimaryPrincipal方法返回值转为真实身份类型（在上边的doGetAuthenticationInfo认证通过填充到SimpleAuthenticationInfo中身份类型），
//        44         TAdminUser activeUser = (TAdminUser) principals.getPrimaryPrincipal();
//        45         // 根据身份信息获取权限信息
//        46         // 从数据库获取到权限数据
//        47         TAdminRole adminRoles = adminUserService.getAdminRoles(activeUser);
//        48         // 单独定一个集合对象
//        49         List<String> permissions = new ArrayList<String>();
//        50         if (adminRoles != null) {
//            51             permissions.add(adminRoles.getRoleKey());
//            52         }
//        53         // 查到权限数据，返回授权信息(要包括 上边的permissions)
//        54         SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//        55         // 将上边查询到授权信息填充到simpleAuthorizationInfo对象中
//        56         simpleAuthorizationInfo.addStringPermissions(permissions);
//        57         return simpleAuthorizationInfo;
//        58     }
//59

}