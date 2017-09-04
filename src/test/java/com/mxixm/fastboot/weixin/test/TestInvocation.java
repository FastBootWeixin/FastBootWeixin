package com.mxixm.fastboot.weixin.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2017/7/27.
 */
public class TestInvocation {

    public static void main(String[] args) {
        InvokedObject o = new InvokedObject();
        InvokedObjectInterface ob = (InvokedObjectInterface) Proxy.newProxyInstance(TestInvocation.class.getClassLoader(), new Class[] {InvokedObjectInterface.class}, new MyInvocation(o));
        o.set(ob);
        ob.test1();

    }


    public static class MyInvocation implements InvocationHandler {

        public InvokedObject o;

        public MyInvocation(InvokedObject o) {
            this.o = o;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(o, args);
        }
    }

    public static interface InvokedObjectInterface {

        void test1();

        void test2();

        void set(InvokedObjectInterface o);
    }


    public static class InvokedObject implements InvokedObjectInterface {

        InvokedObjectInterface o;

        public InvokedObject() {

        }

        @Override
        public void test1() {
            System.out.println(1);
            o.test2();
        }

        @Override
        public void test2() {
            System.out.println(2);
        }

        @Override
        public void set(InvokedObjectInterface o) {
            this.o = o;
        }

    }


}
