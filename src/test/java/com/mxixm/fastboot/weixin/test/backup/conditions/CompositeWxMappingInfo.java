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

package com.mxixm.fastboot.weixin.test.backup.conditions;

import com.mxixm.fastboot.weixin.module.web.WxRequest;
import com.mxixm.fastboot.weixin.mvc.condition.WxRequestCondition;
import com.mxixm.fastboot.weixin.mvc.method.WxMappingInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * FastBootWeixin WxMappingInfo
 * 此类有大量重复代码，还有可优化余地
 *
 * @author Guangshan
 * @date 2018-9-17 13:26:41
 * @since 0.7.0
 */
public final class CompositeWxMappingInfo implements WxRequestCondition<WxRequestCondition> {

    private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

    private final String name;

    private final WxMappingInfo wxButtonInfo;

    private final WxMappingInfo wxButtonMappingInfo;

    private final WxMappingInfo wxMessageMappingInfo;

    private final WxMappingInfo wxEventMappingInfo;

    public CompositeWxMappingInfo(WxMappingInfo wxButtonInfo,
                                  WxMappingInfo wxButtonMappingInfo,
                                  WxMappingInfo wxMessageMappingInfo,
                                  WxMappingInfo wxEventMappingInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("@WxButton:").append(wxButtonInfo != null ? wxButtonInfo.getName() : "").append("-");
        builder.append("@WxButtonMapping:").append(wxButtonMappingInfo != null ? wxButtonMappingInfo.getName() : "").append("-");
        builder.append("@WxMessageMapping:").append(wxMessageMappingInfo != null ? wxMessageMappingInfo.getName() : "").append("-");
        builder.append("@WxEventMapping:").append(wxEventMappingInfo != null ? wxEventMappingInfo.getName() : "").append("-");
        this.name = builder.toString();
        this.wxButtonInfo = wxButtonInfo;
        this.wxButtonMappingInfo = wxButtonMappingInfo;
        this.wxMessageMappingInfo = wxMessageMappingInfo;
        this.wxEventMappingInfo = wxEventMappingInfo;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public WxRequestCondition combine(WxRequestCondition other) {
        Assert.isInstanceOf(CompositeWxMappingInfo.class, other, "不支持的合并操作");
        CompositeWxMappingInfo otherInfo = (CompositeWxMappingInfo) other;
        WxMappingInfo wxButtonInfo = this.wxButtonInfo.combine(otherInfo.wxButtonInfo);
        WxMappingInfo wxButtonMappingInfo = this.wxButtonMappingInfo.combine(otherInfo.wxButtonMappingInfo);
        WxMappingInfo wxMessageMappingInfo = this.wxMessageMappingInfo.combine(otherInfo.wxMessageMappingInfo);
        WxMappingInfo wxEventMappingInfo = this.wxEventMappingInfo.combine(otherInfo.wxEventMappingInfo);
        return new CompositeWxMappingInfo(wxButtonInfo, wxButtonMappingInfo, wxMessageMappingInfo, wxEventMappingInfo);
    }

    @Override
    public WxRequestCondition getMatchingCondition(WxRequest wxRequest) {
        WxRequestCondition wxRequestCondition = null;
        if (this.wxButtonInfo != null) {
            wxRequestCondition = wxButtonInfo.getMatchingCondition(wxRequest);
            if (wxRequestCondition != null) {
                return wxRequestCondition;
            }
        }
        if (this.wxButtonMappingInfo != null) {
            wxRequestCondition = wxButtonMappingInfo.getMatchingCondition(wxRequest);
            if (wxRequestCondition != null) {
                return wxRequestCondition;
            }
        }
        if (this.wxMessageMappingInfo != null) {
            wxRequestCondition = wxMessageMappingInfo.getMatchingCondition(wxRequest);
            if (wxRequestCondition != null) {
                return wxRequestCondition;
            }
        }
        if (this.wxEventMappingInfo != null) {
            wxRequestCondition = wxEventMappingInfo.getMatchingCondition(wxRequest);
            if (wxRequestCondition != null) {
                return wxRequestCondition;
            }
        }
        return wxRequestCondition;
    }

    @Override
    public int compareTo(WxRequestCondition other, WxRequest wxRequest) {
        // 这里理论上不应触发，因为上面获取的condition不可能是Composite的了
        return 1;
    }

    @Override
    public Type getType() {
        return Type.COMPOSITES;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CompositeWxMappingInfo)) {
            return false;
        }
        CompositeWxMappingInfo otherInfo = (CompositeWxMappingInfo) other;
        return (this.name.equals(otherInfo.name) &&
                Objects.equals(this.wxButtonInfo, otherInfo.wxButtonInfo) &&
                Objects.equals(this.wxButtonMappingInfo, otherInfo.wxButtonMappingInfo) &&
                Objects.equals(this.wxMessageMappingInfo, otherInfo.wxMessageMappingInfo) &&
                Objects.equals(this.wxEventMappingInfo, otherInfo.wxEventMappingInfo));
    }

    @Override
    public int hashCode() {
        return (this.name.hashCode() * 31 +  // primary differentiation
                Objects.hash(this.wxButtonInfo, this.wxButtonMappingInfo, this.wxMessageMappingInfo, this.wxEventMappingInfo));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append(StringUtils.isEmpty(this.name) ? "-" : this.name);
        if (this.wxButtonInfo != null) {
            builder.append(",").append(wxButtonInfo.getName()).append("=").append(wxButtonInfo);
        }
        if (this.wxButtonMappingInfo != null) {
            builder.append(",").append(wxButtonMappingInfo.getName()).append("=").append(wxButtonMappingInfo);
        }
        if (this.wxMessageMappingInfo != null) {
            builder.append(",").append(wxMessageMappingInfo.getName()).append("=").append(wxMessageMappingInfo);
        }
        if (this.wxEventMappingInfo != null) {
            builder.append(",").append(wxEventMappingInfo.getName()).append("=").append(wxEventMappingInfo);
        }
        builder.append('}');
        return builder.toString();
    }

}
