package com.mxixm.fastboot.weixin.service;

import com.mxixm.fastboot.weixin.module.extend.WxQrCode;
import com.mxixm.fastboot.weixin.module.extend.WxShortUrl;
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

    /**
     * 创建二维码
     * @param wxQrCode 二维码相关参数
     * @return 创建结果
     */
    public WxQrCode.Result createQrCode(WxQrCode wxQrCode) {
        final WxQrCode.Result qrCode = wxApiService.createQrCode(wxQrCode);
        final String showUrl = UriComponentsBuilder.fromHttpUrl(showQrCodeUrl).queryParam("ticket", qrCode.getTicket()).build().toUriString();
        qrCode.setShowUrl(showUrl);
        return qrCode;
    }

    /**
     * 长链接转短链接
     * @param wxShortUrl 链接相关参数
     * @return 转换结果
     */
    public String createShortUrl(WxShortUrl wxShortUrl) {
        final WxShortUrl.Result shortUrl = wxApiService.createShortUrl(wxShortUrl);
        return shortUrl.getShortUrl();
    }

}
