package cn.zpaas.lowcode.be.ide.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.google.gson.JsonObject;

import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.JsonUtils;
import cn.zpaas.lowcode.utils.StringUtils;
import cn.zpaas.lowcode.domain.eo.UserBusinessSystem;
import cn.zpaas.lowcode.utils.LoginInfoUtils;

/**
 * @author zjy
 * 登录信息过滤器
 */
@WebFilter(filterName = "sessionFileter", urlPatterns = {"/*"}, asyncSupported = true)
public class SessionFilter implements Filter {
	public static Logger logger = LoggerFactory.getLogger(SessionFilter.class);
	
	private static final String URL_SEPARATE_SYMBOL = ";";//URL分隔符
	private static final String URL_ROOT = "/";//URL分隔符
	private static final String URL_ROOT_STAR_STAR = "/**";//URL分隔符
	
	private static final String NOT_LOGON = "NOT_LOGON";//未登录提示信息
	private static final String X_REQUESTED_WITH = "X-Requested-With";
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

	

	
	//登录信息存放的Key
	@Value("${init.param.loginInfoKey:loginInfo}")
	private  String loginInfoKey;
	//引擎对外服务的url
	@Value("${init.param.servicePath:service}")
	private String initServicePath;
	@Value("${init.param.fileUploadServicePath:fileUploadService}")
	private String initFileUploadServicePath;
	@Value("${init.param.fileDownloadServicePath:fileDownloadService}")
	private String initFileDownloadServicePath;
	//忽略的url
	@Value("${init.param.ignoreUrls:/**/login}")
	private String ignoreUrls;
	
	//ignoreUrlMatchers
	private List<AntPathRequestMatcher> ignoreUrlMatchers  = new ArrayList<>();

	@Autowired
	private UserBusinessSystem userBusinessSystemSV;
			
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.initUrlMatchers();
		Filter.super.init(filterConfig);
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
	
		String uri = request.getRequestURI();

        logger.info("filter url: {}", uri);
        //是否需要过滤
        boolean isIgnoreUrl = this.isIgnoreUrl(request);
        if (isIgnoreUrl) { // 不需要过滤直接传给下一个过滤器
        	chain.doFilter(servletRequest, servletResponse);
		} else { // 需要过滤器
			if (session != null && session.getAttribute(loginInfoKey) != null) {// session中包含登录信息
				JsonObject loginInfo = (JsonObject)session.getAttribute(loginInfoKey);
				if(JsonUtils.getJsonObject(loginInfo, LoginInfoUtils.BUSINESS_SYSTEM_GRANT_INFO_KEY) == null) {//如果为空，则加载业务系统授权信息
					JsonObject loginUser = JsonUtils.getJsonObject(loginInfo, LoginInfoUtils.LOGIN_USER_KEY);
					if(loginUser == null || StringUtils.isBlank(JsonUtils.getString(loginUser, LoginInfoUtils.LOGIN_USER_ID_KEY)) ||
							StringUtils.isBlank(JsonUtils.getString(loginUser, LoginInfoUtils.LOGIN_USER_TENANT_ID_KEY))) {
						throw new ServletException("invalid login user!");
					}
					String tenantId = JsonUtils.getString(loginUser, LoginInfoUtils.LOGIN_USER_TENANT_ID_KEY);
					List<UserBusinessSystem> userBusinessSystems = this.userBusinessSystemSV.loadByUserId(JsonUtils.getString(loginUser, LoginInfoUtils.LOGIN_USER_ID_KEY));
					JsonObject userBusinessSystemGrants = new JsonObject();
					if(!CollectionUtils.isEmpty(userBusinessSystems)) {
						for(UserBusinessSystem userBusinessSystem : userBusinessSystems) {
							if(!tenantId.equals(userBusinessSystem.getTenantId())) {
								throw new ServletException("illegal request!");
							}
							userBusinessSystemGrants.add(userBusinessSystem.getSystemId(), JsonUtils.toJsonElement(userBusinessSystem));
						}
					}
					loginInfo.add(LoginInfoUtils.BUSINESS_SYSTEM_GRANT_INFO_KEY, userBusinessSystemGrants);
					session.setAttribute(loginInfoKey, loginInfo);
				}
				chain.doFilter(request, response);
			} else {
				logger.error(NOT_LOGON);
				String requestType = request.getHeader(X_REQUESTED_WITH);
				// 判断是否是ajax请求
				if (requestType != null && XML_HTTP_REQUEST.equals(requestType)) {
					response.getWriter().write(NOT_LOGON);
				} else {
					// 重定向到登录页
					response.getWriter().write(NOT_LOGON);
				}
				return;
			}
		}
	}
	
	/**
	 * 初始化忽略的url
	 */
	private void initUrlMatchers() {
		ignoreUrlMatchers.add(new AntPathRequestMatcher(URL_ROOT + initServicePath + URL_ROOT_STAR_STAR));
		ignoreUrlMatchers.add(new AntPathRequestMatcher(URL_ROOT + initFileUploadServicePath + URL_ROOT_STAR_STAR));
		ignoreUrlMatchers.add(new AntPathRequestMatcher(URL_ROOT + initFileDownloadServicePath + URL_ROOT_STAR_STAR));
		
		if (!StringUtils.isBlank(ignoreUrls)) {
			String[] urls = ignoreUrls.split(URL_SEPARATE_SYMBOL);
			if (urls != null && urls.length > 0) {
				for (int i = 0; i < urls.length; i++) {
					ignoreUrlMatchers.add(new AntPathRequestMatcher(urls[i]));
				}
			}
		}
	}
	
	/**
	 * 检查是否是忽略的请求
	 * @param request
	 * @return
	 */
	private boolean isIgnoreUrl(HttpServletRequest request) {
		//处理忽略认证处理的URL
  		if(!CollectionUtils.isEmpty(ignoreUrlMatchers)) {
  			for(RequestMatcher matcher : ignoreUrlMatchers) {
  				if(matcher.matches(request)) {//只要匹配上一个就可以
  					return true;
  				}
  			}
  		}
  		return false;
	}
}
