/*
 * Copyright (c) 2016-2018, Guangshan (guangshan1992@qq.com) and the original author or authors.
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

package com.mxixm.fastboot.weixin.module.js;

/**
 * fastboot-weixin  WxJsApi
 * 这个不按标准命名规范，只为和微信官方一致，请谅解
 *
 * @author Guangshan
 * @date 2018/5/7 22:29
 * @since 0.6.0
 */
public enum WxJsApi {

    onMenuShareTimeline,
    onMenuShareAppMessage,
    onMenuShareQQ,
    onMenuShareWeibo,
    onMenuShareQZone,
    startRecord,
    stopRecord,
    onVoiceRecordEnd,
    playVoice,
    pauseVoice,
    stopVoice,
    onVoicePlayEnd,
    uploadVoice,
    downloadVoice,
    chooseImage,
    previewImage,
    uploadImage,
    downloadImage,
    translateVoice,
    getNetworkType,
    openLocation,
    getLocation,
    hideOptionMenu,
    showOptionMenu,
    hideMenuItems,
    showMenuItems,
    hideAllNonBaseMenuItem,
    showAllNonBaseMenuItem,
    closeWindow,
    scanQRCode,
    chooseWXPay,
    openProductSpecificView,
    addCard,
    chooseCard,
    openCard
}
