package com.aspire.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Shiro配置 
 * 提示:@Bean注解默认采用方法名作为id
 *
 * @author JustryDeng
 * @Date 2018年8月23日 上午10:58:22
 */
@Configuration
public class ShiroConfigure {

	/**
	 * 注入Shiro的 [核心]管理器 配置
	 *
	 * @Date 2018年8月23日 下午2:03:41
	 */
	@Bean
	@DependsOn(value = { "authenticator", "ehCacheManager" })
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager dsm = new DefaultWebSecurityManager();
		// 将cacheManager引入securityManager
		dsm.setCacheManager(ehCacheManager());
		// 将rememberMeManager引入securityManager
		dsm.setRememberMeManager(rememberMeManager());
		// 将authenticator引入securityManager
		dsm.setAuthenticator(authenticator());
		// 将sessionManager引入securityManager
		dsm.setSessionManager(sessionManager());
		// 将realms引入securityManager
		Collection<Realm> realms = new ArrayList<>(8);
		realms.add(shiroRealm());
		dsm.setRealms(realms);
		return dsm;
	}

	/* ...........................华丽分割线........................... */
	
	/**
	 * 注入 自定义ShiroRealm类的实例
	 *
	 * @Date 2018年8月23日 下午5:41:11
	 */
	@Bean
	public ShiroRealm shiroRealm() {
		ShiroRealm shiroRealm = new ShiroRealm();
		HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
		/*
		 * 比对用户输入的明文密码 与 数据库提前存起来的加密后密码时,将用户输入的 明文密码进
		 * 行MD5、迭代1024次后再比对(这里具体的加密规则要与数据库中的已经加密了的密码的加密规则一致) 
		 * 注:盐值 在重写认证器的时候设置
		 * 
		 */
		// 设置加密算法为MD5
		credentialsMatcher.setHashAlgorithmName("MD5");
		// 设置加密迭代次数为1024次
		credentialsMatcher.setHashIterations(1024);
		shiroRealm.setCredentialsMatcher(credentialsMatcher);
		return shiroRealm;
	}

	/* ...........................华丽分割线........................... */
	
	/**
	 * 认证器
	 *
	 * @Date 2018年8月23日 下午4:26:38
	 */
	@Bean
	public ModularRealmAuthenticator authenticator() {
		ModularRealmAuthenticator mra = new ModularRealmAuthenticator();
		/*
		 * AuthenticationStrategy接口有三种实现
		 *  这里以AtLeastOneSuccessfulStrategy作为身份认证策略
		 */
		AuthenticationStrategy as = new AtLeastOneSuccessfulStrategy();
		mra.setAuthenticationStrategy(as);
		return mra;
	}
	
	/* ...........................华丽分割线........................... */

	/**
	 * 注入简单cookie配置
	 *
	 * @Date 2018年8月23日 下午2:06:35
	 */
	@Bean
	public SimpleCookie rememberMeCookie() {
		SimpleCookie sc = new SimpleCookie();
		// 设置Cookie在客户端浏览器中保存内容的cookie的名字
		sc.setName("rememberMe");
		// 证该系统不会受到跨域的脚本操作攻击
		sc.setHttpOnly(true);
		// 定义该Cookie的过期时间,单位为秒。如果设置为-1标识浏览器关闭就失效
		sc.setMaxAge(60);
		return sc;
	}

	/**
	 * 注入remembernMe的Cookie的管理器 配置
	 *
	 * @Date 2018年8月23日 下午2:15:25
	 */
	@Bean
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager crmm = new CookieRememberMeManager();
		// 设置RememberMe的加密后Cookie的cipherKey
		byte[] cipherKey = Base64.decode("4AvVhmFLUs0KTA3Kprsdag==");
		crmm.setCipherKey(cipherKey);
		// cookie配置引用上面的SimpleCookie
		crmm.setCookie(rememberMeCookie());
		return crmm;
	}

	/* ...........................华丽分割线........................... */
	
	/**
	 * 注入shiro bean的生命周期处理器。 可以自动调用配置在 Spring IOC容器中shiro bean的生命周期方法
	 *
	 * @Date 2018年8月23日 下午3:06:13
	 */
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		LifecycleBeanPostProcessor lbpp = new LifecycleBeanPostProcessor();
		return lbpp;
	}

	/**
	 * 使IOC容器中的bean可以使用 shiro的注解. @DependsOn注解可保证在注入此bean之前,会先注入指定的bean
	 * 
	 * {@code BeanPostProcessor} implementation that creates AOP proxies based on
	 * all candidate {@code Advisor}s in the current {@code BeanFactory}. This class
	 * is completely generic; it contains no special code to handle any particular
	 * aspects, such as pooling aspects.
	 *
	 * @Date 2018年8月23日 下午3:15:13
	 */
	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daapc = new DefaultAdvisorAutoProxyCreator();
		return daapc;
	}

	/**
	 * Convenient base class for Advisors that are also static pointcuts.
	 * 
	 * @Date 2018年8月23日 下午3:21:45
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		// 引用上面的securityManager()配置
		aasa.setSecurityManager(securityManager());
		return aasa;
	}
	
	/* ...........................华丽分割线........................... */

	/**
	 * 注入ShiroFileter
	 *
	 * @Date 2018年8月23日 下午3:42:55
	 */
	@Bean
	@DependsOn(value = { "securityManager" })
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean sffb = new ShiroFilterFactoryBean();
		/*
		 *  Sets the application {@code SecurityManager} instance to be
		 *  used by the constructed Shiro Filter
		 */
		sffb.setSecurityManager(securityManager());
		// 当Shiro验证时,如果不满足认证条件,那么打回到这个页面
		sffb.setLoginUrl("/login.html");
		// 当Shiro认证(登陆)成功后,默认跳转到的页面
		sffb.setSuccessUrl("/login.html");
		// 不满足权限条件,那么跳转至此页面
		sffb.setUnauthorizedUrl("/unauthorized.html");
		/* ----------------------下面配置:拦截器(过滤器)---------------------- */
		// urlPathExpression_to_comma-delimited-filter-chain-definition
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put("/login.html", "anon");
		map.put("/login", "anon");
		// 当请求/shiro/logout时登出
		map.put("/logout", "logout");
		/*
		 *  注意:这里的user是过滤器的一种,而下面roles[user]中的user是自定义的一种角色。
		 *  注意:user拦截器既允许通过Subject.login()认证进入的用户访问;又允许通过rememberMe缓存进入的用户访问
		 *  注意:authc拦截器既只允许通过Subject.login()认证进入的用户访问;不允许rememberMe缓存通过进入的用户访问
		 */
		map.put("/introduce.html", "user");
		map.put("/rememberMe.html", "user");
		// 注意roles[user]这里的话,角色不要再用引号引起来了,直接写即可
		map.put("/user.html", "authc,roles[user]");
		map.put("/admin.html", "authc,roles[admin]");
		map.put("/superadmin.html", "authc,roles[host,admin]");
		// 由于权限由上而下“就近”选择,所以一般将"/**"配置在最下面;还有一些细节,可详见《程序员成长笔记(三)》相关章节
		map.put("/**", "authc");
		sffb.setFilterChainDefinitionMap(map);
		return sffb;
	}

	/**
	 * 启用配置上面配置的shiro拦截器
	 *
	 * @Date 2018年8月23日 下午3:35:51
	 */
	@Bean
	@DependsOn(value = { "shiroFilter" })
	public DelegatingFilterProxy shiroFilterProxy() {
		DelegatingFilterProxy dfp = new DelegatingFilterProxy();
		// 引用上面的shiroFilter
		dfp.setTargetBeanName("shiroFilter");
		return dfp;

	}
	
	/* ...........................华丽分割线........................... */
	
	/**
	 * 注入EhCacheManager缓存管理器
	 *
	 * @Date 2018年8月23日 下午4:04:40
	 */
	@Bean
	public EhCacheManager ehCacheManager() {
		EhCacheManager ecm = new EhCacheManager();
		ecm.setCacheManagerConfigFile("classpath:ehcache.xml");
		return ecm;
	}
	
	/* ...........................华丽分割线........................... */

	/**
	 * 注入SessionId生成器
	 *
	 * @Date 2018年8月24日 下午4:26:22
	 */
	@Bean
	public JavaUuidSessionIdGenerator sessionIdGenerator() {
		JavaUuidSessionIdGenerator jusi = new JavaUuidSessionIdGenerator();
		return jusi;
	}

	/**
	 * 注入自己编写的持久化Session的类
	 *
	 * @Date 2018年8月24日 下午4:26:22
	 */
	@Bean
	@DependsOn(value = { "sessionIdGenerator" })
	public SessionPermanentClass sessionDAO() {
		SessionPermanentClass jusi = new SessionPermanentClass();
		jusi.setSessionIdGenerator(sessionIdGenerator());
		// 设置使用哪一个 缓存
		jusi.setActiveSessionsCacheName("shiro-activeSessionCache");
		return jusi;
	}

	/**
	 * 注入会话管理器
	 *
	 * @Date 2018年8月24日 下午4:26:22
	 */
	@Bean
	@DependsOn(value = { "sessionDAO" })
	public DefaultSessionManager sessionManager() {
		// 这里创建实例时,要用DefaultWebSessionManager;而不要用DefaultSessionManager
		DefaultSessionManager dsm = new DefaultWebSessionManager();
		// 设置session失效时间为30分钟 
		dsm.setGlobalSessionTimeout(1800000);
		// 是否定时检查session失效没有
		dsm.setSessionValidationSchedulerEnabled(true);
		// 如果session失效了,那么删除失效了的session
		dsm.setDeleteInvalidSessions(true);
		// 指定引用上面配置的sessionDAO
		dsm.setSessionDAO(sessionDAO());
		return dsm;
	}

}
