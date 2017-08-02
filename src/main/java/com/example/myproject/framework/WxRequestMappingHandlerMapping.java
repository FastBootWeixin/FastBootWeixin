package com.example.myproject.framework;

import com.example.myproject.controller.WxVerifyController;
import com.example.myproject.module.event.WxMessage;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
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

    private final HandlerMethod wxVerifyMethodHandler;

    private final Jaxb2RootElementHttpMessageConverter xmlConverter;

    public WxRequestMappingHandlerMapping(WxVerifyController wxVerifyController) {
        this.wxVerifyMethodHandler = new HandlerMethod(wxVerifyController, WX_VERIFY_METHOD);
        this.xmlConverter = new Jaxb2RootElementHttpMessageConverter();
    }

    @Override
    protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
        // 只接受根目录的请求
        if (!"/".equals(lookupPath)) {
            return null;
        }
        if (isWxVerifyRequest(request)) {
            return wxVerifyMethodHandler;
        }
        if (isWxPostRequest(request)) {
            HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
            if (xmlConverter.canRead(WxMessage.class, inputMessage.getHeaders().getContentType())) {
                try {
                    WxMessage wxMessage = (WxMessage) xmlConverter.read(WxMessage.class, inputMessage);
                    System.out.println(wxMessage);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
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
