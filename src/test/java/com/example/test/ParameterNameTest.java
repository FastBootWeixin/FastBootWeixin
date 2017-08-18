package com.example.test;

/**
 * FastBootWeixin  ParameterNameTest
 *
 * @author Guangshan
 * @summary FastBootWeixin  ParameterNameTest
 * @Copyright (c) 2017, Guangshan Group All Rights Reserved
 * @since 2017/8/12 22:11
 */
public class ParameterNameTest {

    /**
     * 换任何类库都不支持接口的参数名获取，因为class文件中根本就没有这个信息
     * 其实根本原因是因为类中，参数其实是作为localVariable出现的，而localVariable都有一个名字，所以才可以获取参数名
     * 而接口根本就不需要调用，当然没有localVariable。
     * @param args
     * @throws NoSuchMethodException
     */
    public static void main(String[] args) throws NoSuchMethodException {
//        Paranamer paranamer = new CachingParanamer();
//        Method method = WxApiInvokeSpi.class.getMethod("storeFileToTempMedia", File.class);
//        String[] s = paranamer.lookupParameterNames(method);
//        System.out.println(s);
    }

}
