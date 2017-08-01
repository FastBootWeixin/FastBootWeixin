package com.example.myproject.framework;

import com.example.myproject.controller.WxVerifyController;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class WxRequestMappingHandlerMapping extends AbstractHandlerMapping {

    private static final String[] WX_VERIFY_PARAMS = new String[]{
            "echostr", "nonce", "signature", "timestamp"
    };

    private static final String[] WX_POST_PARAMS = new String[]{
            "openid", "nonce", "signature", "timestamp"
    };

    private static final ParamsRequestCondition WX_VERIFY_PARAMS_CONDITION = new ParamsRequestCondition(WX_VERIFY_PARAMS);

    private static final ParamsRequestCondition WX_POST_PARAMS_CONDITION = new ParamsRequestCondition(WX_POST_PARAMS);

    private static final ConsumesRequestCondition WX_POST_CONSUMES_CONDITION = new ConsumesRequestCondition(MediaType.TEXT_XML_VALUE);

    private static final Method WX_VERIFY_METHOD = ClassUtils.getMethod(WxVerifyController.class, "verify", String.class, String.class, String.class, String.class);

    private WxVerifyController wxVerifyController;

    public WxRequestMappingHandlerMapping(WxVerifyController wxVerifyController) {
        this.wxVerifyController = wxVerifyController;
    }

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        if (isWxVerifyRequest(request)) {
            return new HandlerMethod(wxVerifyController, WX_VERIFY_METHOD);
        }
        if (isWxPostRequest(request)) {
            return null;
        }
        return null;
    }

    // 判断是否是微信verify请求
    private boolean isWxVerifyRequest(HttpServletRequest request) {
        return "GET".equals(request.getMethod())
                && WX_VERIFY_PARAMS_CONDITION.getMatchingCondition(request) != null;
    }

    // 判断是否是微信POST请求
    private boolean isWxPostRequest(HttpServletRequest request) {
        return "POST".equals(request.getMethod())
                && WX_POST_PARAMS_CONDITION.getMatchingCondition(request) != null
                && WX_POST_CONSUMES_CONDITION.getMatchingCondition(request) != null;
    }

}
