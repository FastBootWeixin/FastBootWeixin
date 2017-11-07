/*
 * Copyright (c) 2016-2017, Guangshan (guangshan1992@qq.com) and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mxixm.fastboot.weixin.service.invoker.executor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.*;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FastBootWeixin WxApiTemplate
 * RestTemplate的包装类
 *
 * @author Guangshan
 * @date 2017/8/13 10:58
 * @since 0.1.2
 */
public class WxApiTemplate {

    private RestTemplate restTemplate;

    public WxApiTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        restTemplate.setMessageConverters(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return restTemplate.getMessageConverters();
    }

    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        restTemplate.setErrorHandler(errorHandler);
    }

    public ResponseErrorHandler getErrorHandler() {
        return restTemplate.getErrorHandler();
    }

    public void setDefaultUriVariables(Map<String, ?> defaultUriVariables) {
        restTemplate.setDefaultUriVariables(defaultUriVariables);
    }

    public void setUriTemplateHandler(UriTemplateHandler handler) {
        restTemplate.setUriTemplateHandler(handler);
    }

    public UriTemplateHandler getUriTemplateHandler() {
        return restTemplate.getUriTemplateHandler();
    }

    public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        return restTemplate.getForObject(url, responseType);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
        return restTemplate.getForEntity(url, responseType);
    }

    public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
        return restTemplate.headForHeaders(url, uriVariables);
    }

    public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.headForHeaders(url, uriVariables);
    }

    public HttpHeaders headForHeaders(URI url) throws RestClientException {
        return restTemplate.headForHeaders(url);
    }

    public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
        return restTemplate.postForLocation(url, request, uriVariables);
    }

    public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForLocation(url, request, uriVariables);
    }

    public URI postForLocation(URI url, Object request) throws RestClientException {
        return restTemplate.postForLocation(url, request);
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        return restTemplate.postForObject(url, request, responseType);
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public void put(String url, Object request, Object... uriVariables) throws RestClientException {
        restTemplate.put(url, request, uriVariables);
    }

    public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
        restTemplate.put(url, request, uriVariables);
    }

    public void put(URI url, Object request) throws RestClientException {
        restTemplate.put(url, request);
    }

    public <T> T patchForObject(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.exchange(url, HttpMethod.PATCH, getHttpEntity(request), responseType, uriVariables).getBody();
    }

    public <T> T patchForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.exchange(url, HttpMethod.PATCH, getHttpEntity(request), responseType, uriVariables).getBody();
    }

    public <T> T patchForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
        return restTemplate.exchange(url, HttpMethod.PATCH, getHttpEntity(request), responseType).getBody();
    }

    private HttpEntity getHttpEntity(Object request) {
        if (request instanceof HttpEntity) {
            return (HttpEntity) request;
        } else if (request != null) {
            return new HttpEntity(request);
        } else {
            return HttpEntity.EMPTY;
        }
    }

    public void delete(String url, Object... uriVariables) throws RestClientException {
        restTemplate.delete(url, uriVariables);
    }

    public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
        restTemplate.delete(url, uriVariables);
    }

    public void delete(URI url) throws RestClientException {
        restTemplate.delete(url);
    }

    public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
        return restTemplate.optionsForAllow(url, uriVariables);
    }

    public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.optionsForAllow(url, uriVariables);
    }

    public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
        return restTemplate.optionsForAllow(url);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType) throws RestClientException {
        return restTemplate.exchange(requestEntity, responseType);
    }

    public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) throws RestClientException {
        return restTemplate.exchange(requestEntity, responseType);
    }

    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor, uriVariables);
    }

    public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        return restTemplate.execute(url, method, requestCallback, responseExtractor);
    }

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        restTemplate.setInterceptors(interceptors);
    }

    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return restTemplate.getInterceptors();
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return restTemplate.getRequestFactory();
    }

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        restTemplate.setRequestFactory(requestFactory);
    }
}
