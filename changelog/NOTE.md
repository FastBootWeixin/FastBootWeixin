1. 来自大佬的指示，lombok不推荐在开源项目中使用，虽然编辑后的字节码可正常使用，但是当使用者下载源码使用时会很不友善，除非使用者也引用了lombok。有空可以全部delombok下
2. 可以考虑加入来自项目[Weixin-Java-Tool](https://github.com/chanjarster/weixin-java-tools/wiki/WxSession)的SessionManager和MessageInterceptor，以及完善自己的WxRequest
3. 关于媒体存储相关的，可以仔细研究一下Tomcat的Session、Manager、Store机制。
4. 加入二维码生成相关接口https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1443433542
