package com.leyou.gateway.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    /**
     * 是否因该过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();

        //初始化zuul网关的运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = context.getRequest();
        //获取请求路径
        String url = request.getRequestURL().toString();

        //如果路径在白名单中，则不进行过滤
        for (String allowPath:allowPaths){
            if (StringUtils.contains(url,allowPath)){
                return false;
            }
        }

        return true;
    }

    /**
     * 拦截的业务功能
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //初始化zuul网关的运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = context.getRequest();
        //获取token，判断token是否存在
        String token = CookieUtils.getCookieValue(request,jwtProperties.getCookieName());

/*        if (StringUtils.isBlank(token)){
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }*/

        try {
            //通过公钥进行解密
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
