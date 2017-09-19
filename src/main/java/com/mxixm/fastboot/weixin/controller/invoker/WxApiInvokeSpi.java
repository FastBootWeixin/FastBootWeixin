/*
 * Copyright 2012-2017 the original author or authors.
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
 *
 */

package com.mxixm.fastboot.weixin.controller.invoker;

import com.mxixm.fastboot.weixin.controller.invoker.annotation.WxApiBody;
import com.mxixm.fastboot.weixin.controller.invoker.annotation.WxApiForm;
import com.mxixm.fastboot.weixin.controller.invoker.annotation.WxApiParam;
import com.mxixm.fastboot.weixin.module.media.WxMedia;
import com.mxixm.fastboot.weixin.module.media.WxMediaResource;
import com.mxixm.fastboot.weixin.module.menu.WxMenuManager;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import org.springframework.core.io.Resource;

public interface WxApiInvokeSpi {

    String getCallbackIp();

    WxMenuManager.WxMenus getMenu();

    String createMenu(@WxApiBody WxMenuManager.WxMenu menu);

    WxMedia.TempMediaResult uploadTempMedia(@WxApiParam("type") WxMedia.Type type, @WxApiForm("media") Resource media);

    WxMediaResource getTempMedia(@WxApiParam("media_id") String mediaId);

    WxUser getUserInfo(@WxApiParam("openid") String userOpenId);

    WxMedia.ImageResult uploadImg(@WxApiForm("media") Resource media);

    WxMedia.NewsResult addNews(@WxApiBody WxMedia.News news);

    /**
     * 值返回一个json结果，不管了，如果有错的话会抛出异常的
     *
     * @param news
     */
    void updateNews(@WxApiBody WxMedia.New news);

    /**
     * 视频不要传description
     *
     * @param type
     * @param media
     * @param description
     * @return dummy
     */
    WxMedia.MediaResult uploadMedia(@WxApiParam("type") WxMedia.Type type,
                                    @WxApiForm("media") Resource media,
                                    @WxApiForm("description") WxMedia.Video description);

    /**
     * 同下面两个地址，没办法，返回类型不同，我也很无奈啊
     *
     * @param mediaId
     * @return dummy
     */
    WxMediaResource getMedia(@WxApiBody WxMedia mediaId);

    /**
     * 主要限制是同一个接口相同的参数可能得到的是不同的结果
     *
     * @param mediaId
     * @return dummy
     */
    WxMedia.News getNewsMedia(@WxApiBody WxMedia mediaId);

    /**
     * 是否有更合理的方法去区分同一个请求的三种内容呢？应该是要有一个代理的
     * 代理中有这三个类型的值，加一个代理转换器，明天就做
     *
     * @param mediaId
     * @return dummy
     */
    WxMedia.Video getVideoMedia(@WxApiBody WxMedia mediaId);

    void delMedia(@WxApiBody WxMedia mediaId);

    WxMedia.Count getMediaCount();

    void clearQuota(@WxApiBody String appid);

    void sendMessage(@WxApiBody WxMessage wxMessage);

    void setMessageStatus(@WxApiBody WxMessage.Status status);

//    batchGetMedia();

}
