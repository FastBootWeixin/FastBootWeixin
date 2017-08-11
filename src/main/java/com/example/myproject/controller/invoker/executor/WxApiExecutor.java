package com.example.myproject.controller.invoker.executor;

import com.example.myproject.controller.invoker.WxApiMethodInfo;
import com.example.myproject.controller.invoker.annotation.WxApiBody;
import com.example.myproject.support.AccessTokenManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.util.stream.IntStream;

/**
 * FastBootWeixin  WxInvokerController
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxInvokerController
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:14
 */
public class WxApiExecutor {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private static final String WX_ACCESS_TOKEN_PARAM_NAME = "access_token";

    private final RestTemplate wxApiInvoker;

    private final AccessTokenManager accessTokenManager;

    private final ObjectMapper jsonConverter = new ObjectMapper();

    public WxApiExecutor(RestTemplate wxApiInvoker, AccessTokenManager accessTokenManager) {
        this.wxApiInvoker = wxApiInvoker;
        this.accessTokenManager = accessTokenManager;
    }

    public Object execute(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        UriComponentsBuilder builder = wxApiMethodInfo.fromArgs(args);
        // 替换accessToken
        builder.replaceQueryParam(WX_ACCESS_TOKEN_PARAM_NAME, accessTokenManager.getToken());

        String result;
        switch (wxApiMethodInfo.getRequestMethod()) {
            case GET: {
                result = wxApiInvoker.getForObject(builder.toUriString(), String.class);
                break;
            }
            case JSON: {
                String body = getJsonBody(wxApiMethodInfo, args);
                result = wxApiInvoker.postForObject(builder.toUriString(), body, String.class);
                break;
            }
            case XML: {
                throw new RuntimeException("todo");
            }
            default: {
                throw new RuntimeException("todo");
            }
        }
        // 用conversionService
        return result;
    }


    private String getJsonBody(WxApiMethodInfo wxApiMethodInfo, Object[] args) {
        int bodyIndex = IntStream.range(0, args.length).filter(i -> {
            Class c = wxApiMethodInfo.getParameterTypeOrAnnotations().get(i);
            return !BeanUtils.isSimpleValueType(c) || c == WxApiBody.class;
        }).findFirst().orElse(-1);
        if (bodyIndex < 0) {
            throw new RuntimeException("todo");
        }
        // 如果是简单类型
        if (BeanUtils.isSimpleValueType(args[bodyIndex].getClass())) {
            return args[bodyIndex].toString();
        }
        try {
            return jsonConverter.writeValueAsString(args[bodyIndex]);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return args[bodyIndex].toString();
        }
    }


    /**
     * 获取一个application/json头
     *
     * @return
     */
    private HttpHeaders buildJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * text/xml头
     *
     * @return
     */
    private HttpHeaders buildXmlHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        return headers;
    }

}
