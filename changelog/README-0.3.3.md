# How To Use
[![Travis](https://travis-ci.org/FastBootWeixin/FastBootWeixin.svg?branch=master)](http://weixin.mxixm.com)
[![Maven Central](https://img.shields.io/badge/maven--central-0.3.3-blue.svg)](http://search.maven.org/#artifactdetails%7Ccom.mxixm%7Cfastboot-weixin%7C0.3.3%7Cjar)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

#### 本框架基于SpringBoot实现，使用注解完成快速开发，可以快速的完成一个微信公众号，重新定义公众号开发。

在使用本框架前建议对[微信公众号开发文档](https://mp.weixin.qq.com/wiki)有所了解，不过在不了解公众号文档的情况下使用本框架也能完成一个简单的微信公众号。

> 注意：目前发布的是rc版，可能仍有部分bug，生产环境使用需谨慎。当然也不要不使用哈，欢迎大家提issue和contribute，开源项目需要大家共同来共享，谢谢~

### 一、简单示例：

#### 1. 申请测试公众号

[微信测试公众号申请链接](https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login)

#### 2. 准备完成，创建maven项目并加入maven依赖

```
<!-- 支持1.4.0.RELEASE及以上 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
</parent>

<dependencies>
    <!-- fastbootWeixin的核心依赖 -->
    <dependency>
        <groupId>com.mxixm</groupId>
        <artifactId>fastboot-weixin</artifactId>
        <version>0.3.3</version>
    </dependency>

    <!-- SpringBoot的web项目，必须 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 暂时只能使用apache的http，后续可加入其它http支持 -->
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpcore</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
    </dependency>
</dependencies>
```

#### 3. 编写示例代码
在resource目录下新建配置文件application.properties或者其他spring boot支持的配置文件类型，加入配置:

- wx.token=随机生成的一串字母与数字，推荐使用随机生成32位的UUID
- wx.appid=测试号的appid，测试号管理界面有
- wx.appsecret=测试号的appsecret，测试号管理界面有

测试代码：
```
package com.mxixm.fastboot.weixin;

import com.mxixm.fastboot.weixin.annotation.WxApplication;
import com.mxixm.fastboot.weixin.annotation.WxAsyncMessage;
import com.mxixm.fastboot.weixin.annotation.WxButton;
import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.module.event.WxEvent;
import com.mxixm.fastboot.weixin.module.message.WxMessage;
import com.mxixm.fastboot.weixin.module.message.WxMessageBody;
import com.mxixm.fastboot.weixin.module.user.WxUser;
import com.mxixm.fastboot.weixin.mvc.annotation.WxController;
import com.mxixm.fastboot.weixin.mvc.annotation.WxEventMapping;
import com.mxixm.fastboot.weixin.mvc.annotation.WxMessageMapping;
import org.springframework.boot.SpringApplication;

@WxApplication
@WxController
public class WxApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WxApp.class, args);
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.LEFT, main = true, name = "左")
    public void left() {
    }

    /**
     * 定义微信菜单
     */
    @WxButton(group = WxButton.Group.RIGHT, main = true, name = "右")
    public void right() {
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.CLICK,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.FIRST,
            name = "文本消息")
    public String leftFirst(WxRequest wxRequest, WxUser wxUser) {
        return "测试文本消息";
    }

    /**
     * 定义微信菜单，并接受事件
     */
    @WxButton(type = WxButton.Type.VIEW,
            group = WxButton.Group.LEFT,
            order = WxButton.Order.SECOND,
            url = "http://baidu.com",
            name = "点击链接")
    @WxAsyncMessage
    public WxMessage link() {
        return WxMessage.News.builder().addItem("测试图文消息", "测试", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white.png", "http://baidu.com").build();
    }

    /**
     * 接受微信事件
     * @param wxRequest
     * @param wxUser
     */
    @WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)
    public void unsubscribe(WxRequest wxRequest, WxUser wxUser) {
        System.out.println(wxUser.getNickName() + "退订了公众号");
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     * @param content
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT)
    @WxAsyncMessage
    public String text(WxRequest wxRequest, String content) {
        WxSession wxSession = wxRequest.getWxSession();
        if (wxSession != null && wxSession.getAttribute("last") != null) {
            return "上次收到消息内容为" + wxSession.getAttribute("last");
        }
        return "收到消息内容为" + content;
    }

    /**
     * 接受用户文本消息，同步返回图文消息
     * @param content
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "1*")
    public WxMessage message(WxSession wxSession, String content) {
        wxSession.setAttribute("last", content);
        return WxMessage.News.builder()
                .addItem(WxMessageBody.News.Item.builder().title(content).description("随便一点")
                        .picUrl("http://k2.jsqq.net/uploads/allimg/1702/7_170225142233_1.png")
                        .url("http://baidu.com").build())
                .addItem(WxMessageBody.News.Item.builder().title("第二条").description("随便二点")
                        .picUrl("http://k2.jsqq.net/uploads/allimg/1702/7_170225142233_1.png")
                        .url("http://baidu.com").build())
                .build();
    }

    /**
     * 接受用户文本消息，异步返回文本消息
     * @param content
     * @return the result
     */
    @WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "2*")
    @WxAsyncMessage
    public String text2(WxRequestBody.Text text, String content) {
        boolean match = text.getContent().equals(content);
        return "收到消息内容为" + content + "!结果匹配！" + match;
    }
}
```

#### 4. 配置内网穿透

因为微信公众号需要配置自己的服务器接口，测试时可直接使用本地进行测试，使用内网穿透可以令微信公众平台访问到你自己本地的服务器。

软件可使用[ngrok](https://www.ngrok.cc/)或者[natapp](https://natapp.cn/)，使用方式请参考两者官方文档。

#### 5. 配置测试公众号

在测试公众号的接口配置信息中填写在第三步中生成的域名，token使用配置文件中的token，保存后，如果不出意外应该会验证成功。如有问题请及时反馈。

### 二、示例图解

#### 菜单示例

![菜单示例](assets/button-simple.jpg)

#### 消息示例

![消息示例](assets/message-simple.jpg)

### 三、示例说明

上面的示例在启动后，请关注自己的公众号，此时公众号的菜单应该是有两个主菜单：左、右，同时左有两个子菜单：文本消息、点击链接。

在点击文本消息菜单时，会收到文本消息，内容为：测试文本消息。

在点击第二个点击链接时，会跳转至百度，并收到一条图文消息，标题是测试图文消息。

给公众号发送文本消息，消息内容不是1开头时，会收到公众号回复内容："收到消息内容为" + 发送的内容。

给公众号发送文本消息，消息内容是1开头时，会收到图文消息的回复。

当有用户退订公众号时，会在System.out中打印用户昵称 + "退订了公众号"

### 四、示例讲解

注解@WxApplication用于声明该应用为微信application，并使用SpringApplication启动。若已有SpringBoot环境，请在你的@SpringApplication类上加入注解@EnableWxMvc，效果一样。可以看源码。

注解@WxController用于声明该类为微信Controller，只有声明了这个注解，才会绑定在微信服务器的请求映射中，否则该类会被忽略。

注解@WxButton(group = WxButton.Group.LEFT, main = true, name = "左")用于声明一个按钮箱，group代表分组，有左中右三个分组，分别对应微信的三个一级菜单。main为boolean值，代表该菜单项是否为一级菜单。name就是菜单名。

注解@WxButton(type = WxButton.Type.CLICK, group = WxButton.Group.LEFT, order = WxButton.Order.FIRST, name = "文本消息") 用于声明左边分组的子菜单，order代表顺序，这里是第一个。

public String leftFirst(WxRequest wxRequest, WxUser wxUser) { return "测试文本消息"; } 这里有三个点：

- WxRequest是自动绑定的参数，当用户在公众号上进行某些操作后，微信服务器会给配置中填写的域名的根路径发送一个Post请求，请求内容是xml格式的消息，这条消息内容中标记了具体的信息，可参考公众号文档。这里wxRequest为封装过的微信请求内容，可以通过这个wxRequest获得一些信息。
- wxUser是通过微信api接口获取的用户相关信息，这个还涉及到另外一个内容，后面讲解。
- 返回String，此时会直接使用微信定义的文本消息，同步返回给用户，即直接对微信服务器的这个请求进行响应，参考：[被动回复消息](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140543)。


@WxButton(type = WxButton.Type.VIEW, group = WxButton.Group.LEFT, order = WxButton.Order.SECOND, url = "http://baidu.com", name = "点击链接") 该注解同上面，类型变为View，具体内容可参考该枚举注释，或者[公众号文档](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013)。注意每种菜单类型都有自己的限制，请参考文档，如不满足条件启动时就会发生异常。

注解@WxAsyncMessage表明异步回复消息，参考[客服消息](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547)，暂时未对多客服进行支持。

WxMessage.News.builder()，在WxMessage类中，有不同的静态内部类，以及他们的builder，通过builder可以方便的构造微信不同类型的消息，请参考[被动回复消息](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140543)和[客服消息](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547)。

注解@WxEventMapping(type = WxEvent.Type.UNSUBSCRIBE)绑定取消订阅事件，当有用户取消关注时，会进入这个注解下面的逻辑。还有一点要注解，WxEventMapping所有的回复内容都会被异步发送给用户。

注解@WxMessageMapping(type = WxMessage.Type.TEXT)表示绑定用户发送的文本消息到下面的方法逻辑，public String text(String content) { return "收到消息内容为" + content; }。content会自动被赋值为用户发送的文本内容。

注解@WxMessageMapping(type = WxMessage.Type.TEXT, wildcard = "1*")与上面相同，不同的是wildcard通配符，该通配符支持对消息内容做通配，覆盖该通配逻辑的会进入下面的执行逻辑。

### 五、功能支持

#### 1. Spring Boot风格的启动方式

与Spring Boot完美整合，如果自己没有SpringBoot项目，想直接使用该框架搭建公众号，可直接使用@WxApplication标记启动类，该注解支持参数：menuAutoCreate，默认为true。表示自动创建微信菜单，可以设置为false关闭自动创建菜单的行为。若已有Spring Boot项目，想引入此框架，只需在你的任意配置类上标记注解@EnableWxMvc即可，参数同上。

#### 2. Spring Mvc风格的Mapping

共支持三种Mapping：
- @WxButton，微信按钮绑定与生成。参数说明:group表示左中右三个分组，name为按钮名，type菜单类型，main是否为一级菜单。order显示顺序。key为事件key，若交给框架管理，则key会自动生成，无需配置。url为view类型的访问地址。mediaId为媒体ID。
- @WxMessageMapping，微信消息绑定。参数说明:type消息类型、wildcard通配符支持多个、pattern正则表达式，暂时不支持哈哈、name绑定名，不必要。
- @WxEventMapping，微信事件绑定。参数说明：type事件类型、name绑定名，不必要。

注意：绑定所在类需声明为@WxController

#### 3. Spring Mvc风格的参数绑定

支持以下类型参数：
- WxRequest:微信服务器的请求封装
- WxRequest.Body:微信服务器的请求体内容封装
- WxRequest.Body.field:WxRequest里的任意属性名，都可以直接绑定到参数，但是要注意消息类型与参数值，有些消息不会有某些内容。暂时还不支持不同消息区分绑定的不同的Request类型，后续可加入支持。
- WxSession:类比于HttpSession，默认实现使用fromUserName作为sessionId
- WxUser:这个是较为特殊的类型，其实是通过WxUserProvider接口提供的，本框架提供一个默认的实现DefaultWxUserProvider implements WxUserProvider<WxUser>，该实现通过微信api获取用户信息，以WxUser类型返回，当然你也可以使用自己的实现类，只用声明为SpringBean即可自动注入，泛型类型也可以由你自己确定，参数中有该泛型类型的参数，都会被自动使用WxUserProvider.getUser获取并绑定到参数中。

参数绑定目前支持这几种，如果有更好的方案需要支持，也可以直接提出意见与建议，我会及时进行处理的。

#### 4. Spring Mvc风格的返回值

返回值支持以下类型：
- String 该返回值会以文本消息形式返回给用户
- WxMessage及其子类 该返回值可以很方便的使用对应消息类型的Builder去构造，此时会以对应类型的消息返回给用户。
- @WxAsyncMessage 标记异步返回消息，通过这个注解，可以令消息的返回值以异步消息的形式调用客服接口发送给用户。@WxEventMapping的返回值只能以这种形式发送。
- String或者WxMessage的Iterable或者Array。该类型会分为多条异步消息发送给用户。

上面异步发送消息都是使用的WxMessageTemplate发送的，下面讲解。

#### 5. Spring风格的消息发送

本框架提供WxMessageTemplate发送消息，同时在template中提供了WxMessageProcessor支持，作用是在消息发送前对消息做处理。

如同步返回消息时，需要写入fromUserName字段，而该字段是消息发送时的toUserName字段，没有必要让框架的使用者去处理这个字段，在WxCommonMessageProcessor处理器中就对该字段进行了处理，有兴趣的可以参考源码。

同时还支持以下转换：对于media类型的消息，可以直接使用mediaUrl或者mediaPath写入素材路径，消息转换器通过WxMediaManager自动管理素材获得必要的素材id。(关于WxMediaManager下面写)

注意：所有的消息处理文本消息，建议都使用WxMessage里的对应消息内容的builder来生成！

#### 6. 素材自动管理

本框架提供WxMediaManager来管理素材，同时使用嵌入式数据库保存素材与本地文件的对应关系，目前这部分功能我虽然完成了，但是总感觉有很大的不妥，希望有人能帮我看看顺便提点意见。

上面消息发送中的媒体其实也是通过素材管理器来实现的。

0.2.0.alpha 版本优化存储，使用接口WxMediaStore来管理媒体存储，开发者可自行实现该接口并注册为Spring的Bean来替换默认的MapDbWxMediaStore。各接口具体使用可参考MapDbWxMediaStore。这里还可以提供一个基于内存的实现来替换MapDb。

#### 7. 内置AccessToken管理

本框架提供WxTokenStore接口来存储token，并提供一个默认的基于内存的实现MemoryWxTokenStore，若有分布式需要可以自行实现该接口，并把实现类作为Bean注入Spring即可。

#### 8. 微信接口调用与返回值自动处理

使用WxApiInvokeSpi接口与WxInvokerProxyFactory工厂类自动生成微信接口调用代理，只需要声明方法和注解即可，默认使用HttpComponent调用接口。有兴趣的小伙伴可以看看源码，我写的也不太好，有更好的建议欢迎提出。

同时对返回值做初步分析，如果接口返回内容的errorCode不为0，则会作为异常抛出。异常体系为WxException及其子类。

PS：你也可以使用这种方式任意生成自己的代理调用接口，后续我会加入文档，暂时懒。。。

#### 9. 菜单自动生成与自动更新
可以通过开关开启与关闭，通过@WxButton注解生成菜单结构，并自动调用接口判断菜单是否改变，自动生成与更新菜单。

#### 10. 内置微信接口认证

可以正确响应微信发送的Token验证，方便快捷。

#### 11. 完全无侵入的MVC模式

使用本框架，不会对SpringMvc自己原生的Mapping产生任何影响，也不会占用任何独有的Mapping关系(除了认证请求)。在此框架存在的情况下，你可以使用任何SpringMvc的原生功能，包括根目录的请求，也不会被微信服务器独自占用。

若想使用单独的地址作为微信的api调用地址，请配置wx.path为路径信息，该路径与微信公众号后台管理里的接口配置信息里url的路径需要一致。

#### 12. 微信Web认证拦截器与URL自动转换

提供微信OAUTH2认证拦截，通过配置wx.callback-domain填写OAUTH2授权回调页面域名，通过配置wx.mvc.interceptor.includePatterns和wx.mvc.interceptor.excludePatterns来配置拦截的目标地址，你可以提供一个WxOAuth2Callback接口的实现类作为Bean，在WxOAuth2Interceptor中会自动注入这个bean，并在微信Web认证通过后调用after(WxOAuth2Context context)方法把相关的context传递给该Bean的方法，你可以在该方法中获取到context了的WxWebUser，并通过WxUserManager把WxWebUser转换为WxUser。关系详细信息请参考：[微信网页授权](https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842)。

附加功能1: view类型的WxButton，自动判断其中url是否属于授权回调域名下地址，根据需要自动处理为包含OAuth2的url。可结合默认的拦截器实现菜单url获取点击用户信息的功能。

附加功能2：自动判断消息中的url是否需要添加OAuth重定向，请参考WxRedirectUtils。

### 六、相关链接
1. [JavaDocs](http://weixin.mxixm.com)
2. [微信公众号开发文档](https://mp.weixin.qq.com/wiki)
3. [OSChina仓库](https://git.oschina.net/kingshine/FastBootWeixin)


### 七、暂时不支持的功能
#### 1. 自定义客服回复消息
之后可以加入支持，使用注解定义消息客服，类似于@RabbitListener的方式
#### 2. 消息加解密
没有想到好的方式把消息加解密融合到SpringMvc中
#### 3. 个性化菜单
稍微有点麻烦，用户分类
#### 4. 高级用户管理
用户分组什么的是否有好的实现？暂时没有需求
#### 5. 公众号其他高级功能
如支付等
#### 6. 待优化：WxMediaStore 完成

### 八、更新日志

#### 0.0.1-SNAPSHOT
初始版本

#### 0.1.0-SNAPSHOT:
1. 加入WxSession，类似HttpSession，默认实现中sessionId是fromUserName。如果自行提供WxSessionManager的实现类bean，则会使用你的Bean作为manager，默认实现是基于内存的，请自行扩展。
2. 优化WxRequest，原始版本为直接把微信请求内容作为Request，更新为WxRequest为微信服务器请求的包装，内部的body属性为微信服务器请求体。
3. 拆分不同的消息请求体

#### 0.1.1-SNAPSHOT:
优化消息参数绑定，新增指定消息体绑定，参考WxRequestBody

#### 0.1.2.SNAPSHOT & 0.1.2.alpha
上传maven中央仓库，生成javadoc，以及一系列规范化

#### 0.1.2.beta
加入apache copyright，全部delombok

#### 0.1.2.rc
上次加入copyright时不小心把所有文件的头注释删掉了，目前补回一部分，等全部补回后加入微信卡券功能，发布release版本

#### 0.2.0.alpha
1. 新增卡券相关接口、推广（二维码与短链接）相关接口
2. 重构消息功能，消息推送现支持群发和单发
3. 重构媒体存储模块，现在更容易替换为自己的实现，详情参考Wiki

#### 0.2.1.alpha
1. 重构WxAsyncMessage，之前是错误实现，因为只实现了发送是Async的，方法调用不是Async的。现在重构为方法的调用都是Async的，这才是真正的WxAsyncMessage
2. 增加微信模板消息功能
3. 修复其他一些bug

#### 0.2.1.beta
1. 增加部分说明，修复部分bug，包括上个版本WxAsyncMessage中返回消息提示服务器故障的bug。
2. 增加菜单url自动重写为微信web认证url

#### 0.3.0 不要使用
1. 优化部分包名
2. 修复一个大bug：当没有配置消息处理器时提示服务器故障。默认提供一个返回空的处理器。
3. 发布正式版。

#### 0.3.1 不要使用
1. 修复上次拦截器返回修改为HttpEntity.EMPTY导致原方法声明与返回类不一致的bug
2. 修复忘了加getter的问题

#### 0.3.2
1. 修复图文消息的bug（Request异步使用时，getRequestURL等会错误）
2. 修复url处理会处理为小写的bug

#### 0.3.3
1. 修复无后缀文件名上传素材时提示无效的媒体类型错误
2. 修复未关注用户放缓存导致关注后取不到信息的bug