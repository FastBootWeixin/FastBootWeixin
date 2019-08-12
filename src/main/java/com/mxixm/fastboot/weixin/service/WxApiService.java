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

package com.mxixm.fastboot.weixin.service;

import com.mxixm.fastboot.weixin.module.credential.WxTicket;
import com.mxixm.fastboot.weixin.module.extend.WxCard;
import com.mxixm.fastboot.weixin.module.extend.WxQrCode;
import com.mxixm.fastboot.weixin.module.extend.WxShortUrl;
import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaResource;
import com.mxixm.fastboot.weixin.module.media.news.WxNews;
import com.mxixm.fastboot.weixin.module.menu.WxMenu;
import com.mxixm.fastboot.weixin.module.menu.WxMenus;
import com.mxixm.fastboot.weixin.module.message.WxGroupMessage;
import com.mxixm.fastboot.weixin.module.message.WxTemplateMessage;
import com.mxixm.fastboot.weixin.module.message.WxUserMessage;
import com.mxixm.fastboot.weixin.module.user.WxTag;
import com.mxixm.fastboot.weixin.module.user.WxTagUser;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiBody;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiForm;
import com.mxixm.fastboot.weixin.service.invoker.annotation.WxApiParam;
import org.springframework.core.io.Resource;

/**
 * FastBootWeixin WxApiService
 * 注意拦截调用异常，如果是token过期，重新获取token并重试
 * 改个名儿，叫SPI高端一点
 *
 * @author Guangshan
 * @date 2017/7/23 17:14
 * @since 0.1.2
 */
public interface WxApiService {

    String getCallbackIp();

    WxMenus getMenu();

    void deleteMenu();

    String createMenu(@WxApiBody WxMenu menu);

    WxMedia.TempMediaResult uploadTempMedia(@WxApiParam("type") WxMedia.Type type, @WxApiForm("media") Resource media);

    WxMediaResource getTempMedia(@WxApiParam("media_id") String mediaId);

    WxUser getUserInfo(@WxApiParam("openid") String userOpenId);

    WxMedia.ImageResult uploadImg(@WxApiForm("media") Resource media);

    WxNews.Result addNews(@WxApiBody WxNews news);

    /**
     * 值返回一个json结果，不管了，如果有错的话会抛出异常的
     *
     * @param news
     */
    void updateNews(@WxApiBody WxNews news);

    /**
     * 视频不要传description
     *
     * @param type
     * @param media
     * @param description
     * @return the result
     */
    WxMedia.MediaResult uploadMedia(@WxApiParam("type") WxMedia.Type type,
                                    @WxApiForm("media") Resource media,
                                    @WxApiForm("description") WxMedia.Video description);

    /**
     * 同下面两个地址，没办法，返回类型不同，我也很无奈啊
     *
     * @param mediaId
     * @return the result
     */
    WxMediaResource getMedia(@WxApiBody WxMedia mediaId);

    /**
     * 主要限制是同一个接口相同的参数可能得到的是不同的结果
     *
     * @param mediaId
     * @return the result
     */
    WxNews getNews(@WxApiBody WxMedia mediaId);

    /**
     * 批量获取news
     * @return
     */
    WxNews.PageResult batchGetNews(@WxApiBody WxNews.PageParam param);

    /**
     * 批量获取媒体
     * @return
     */
    WxMedia.PageResult batchGetMedia(@WxApiBody WxMedia.PageParam param);

    /**
     * 是否有更合理的方法去区分同一个请求的三种内容呢？应该是要有一个代理的
     * 代理中有这三个类型的值，加一个代理转换器，明天就做
     *
     * @param mediaId
     * @return the result
     */
    WxMedia.Video getVideo(@WxApiBody WxMedia mediaId);

    void delMedia(@WxApiBody WxMedia mediaId);

    WxMedia.Count getMediaCount();

    void clearQuota(@WxApiBody String appid);

    WxTemplateMessage.Result sendTemplateMessage(@WxApiBody WxTemplateMessage wxTemplateMessage);

    void sendUserMessage(@WxApiBody WxUserMessage wxMessage);

    WxGroupMessage.Result sendGroupMessage(@WxApiBody WxGroupMessage wxMessage);

    WxGroupMessage.Result sendUsersMessage(@WxApiBody WxGroupMessage wxMessage);

    WxGroupMessage.Result previewGroupMessage(@WxApiBody WxGroupMessage wxMessage);

    void setMessageStatus(@WxApiBody WxUserMessage.Status status);

    /**
     * 创建二维码
     * @param wxQrCode 二维码相关参数
     * @return 创建结果
     */
    WxQrCode.Result createQrCode(WxQrCode wxQrCode);

    /**
     * 长链接转短链接
     * @param wxShortUrl 链接相关参数
     * @return 转换结果
     */
    WxShortUrl.Result createShortUrl(WxShortUrl wxShortUrl);

    // 这个是错的，因为请求前缀是mp.weixin.qq.com而不是api.weixin.qq.com
    // WxMediaResource showQrCode(@WxApiParam("ticket") String ticket);
    // batchGetMedia();

    WxUser.PageResult listUserByTag(WxTagUser wxTagUser);

    WxUser.PageResult listUser(@WxApiParam("next_openid") String nextOpenId);

    WxTag createTag(WxTag wxTag);

    WxTag.TagList getTags();

    WxTag updateTag(WxTag wxTag);

    void deleteTag(WxTag wxTag);

    void batchTagging(WxTagUser wxTagUser);

    void batchUntagging(WxTagUser wxTagUser);

    WxTagUser.TagIdList listTagByUser(WxTagUser wxTagUser);

    WxCard.PageResult getCards(WxCard.PageParam pageParam);

    WxCard cardInfo(WxCard.CardSelector cardSelector);

    WxTicket getTicket(@WxApiParam("type") WxTicket.Type type);

}
