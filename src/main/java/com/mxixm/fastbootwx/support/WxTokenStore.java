package com.mxixm.fastbootwx.support;

/**
 * FastBootWeixin  WxTokenStore
 * 注意考虑分布式存储，或许需要加一个lock，因为获取之后上一个会失效，所以不能完全交给setToken方法自己加锁
 *
 * @author Guangshan
 * @summary FastBootWeixin  WxTokenStore
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:08
 */
public interface WxTokenStore {

	/**
	 * 获取Token
	 * @return
	 */
	String getToken();

	/**
	 * 设置token
	 * @param token
	 * @param expireTime
	 */
	void setToken(String token, long expireTime);

	/**
	 * 获取过期时间
	 * @return
	 */
	long getExpireTime();

	/**
	 * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
	 * @return
	 */
	boolean lock();

	/**
	 * 解锁
	 */
	void unlock();

}