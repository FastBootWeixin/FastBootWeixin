package com.mxixm.fastboot.weixin.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FastBootWeixin  MemoryWxTokenStore
 *
 * @author Guangshan
 * @summary FastBootWeixin  MemoryWxTokenStore
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/7/23 17:08
 */
public class MemoryWxTokenStore implements WxTokenStore {

	private static final Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	/**
	 * token值
	 */
	private String token;

	/**
	 * 过期时间
	 */
	private long expireTime;

	/**
	 * 锁
	 */
	private Lock lock = new ReentrantLock();

	/**
	 * 获取Token
	 * @return
	 */
	public String getToken() {
		return token;
	}

	/**
	 * 设置token
	 * @param token
	 * @param expireTime
	 */
	public void setToken(String token, long expireTime) {
		this.token = token;
		this.expireTime = expireTime;
	}

	/**
	 * 获取过期时间
	 * @return
	 */
	public long getExpireTime() {
		return expireTime;
	}

	/**
	 * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
	 * @return
	 */
	public boolean lock() {
		this.lock.lock();
		long now = Instant.now().toEpochMilli();
		// 如果在有效期内，则说明加锁失败，获得锁的时候已经被别人刷新了
		if (now < this.getExpireTime()) {
			this.unlock();
			return false;
		}
		return true;
	}

	/**
	 * 多线程或者分布式时，防止多个同时设置token值，也同时用于防止tokenManage同时多次刷新
	 * @return
	 */
	public void unlock() {
		this.lock.unlock();
	}
}