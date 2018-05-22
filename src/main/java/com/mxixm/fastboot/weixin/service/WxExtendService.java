package com.mxixm.fastboot.weixin.service;

import com.mxixm.fastboot.weixin.module.extend.WxQrCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * fastboot-weixin  WxExtendService
 *
 * @author Guangshan
 * @date 2017/11/7 22:11
 * @since 0.3.0
 */
public class WxExtendService {

    private final WxApiService wxApiService;

    @Value("${wx.url.showQrCode}")
    private String showQrCodeUrl;

    public WxExtendService(WxApiService wxApiService) {
        this.wxApiService = wxApiService;
    }

    public WxQrCode.Result createQrCode(WxQrCode wxQrCode) {
        final WxQrCode.Result qrCode = wxApiService.createQrCode(wxQrCode);
        final String showUrl = UriComponentsBuilder.fromHttpUrl(showQrCodeUrl).queryParam("ticket", qrCode.getTicket()).build().toUriString();
        qrCode.setShowUrl(showUrl);
        return qrCode;
    }

}
