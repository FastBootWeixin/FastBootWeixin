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

package com.mxixm.fastboot.weixin.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2017/7/27.
 */
public class TestInvocation {

    public static void main(String[] args) {
        for (int i = 0; i <= 9; i++) {
//            for (char j = 'a'; j < 't'; j++) {
            for (int j = 0; j <= 9; j++) {
                System.out.println("i" + i + j + ".app");
            }
        }
        InvokedObject o = new InvokedObject();
        InvokedObjectInterface ob = (InvokedObjectInterface) Proxy.newProxyInstance(TestInvocation.class.getClassLoader(), new Class[]{InvokedObjectInterface.class}, new MyInvocation(o));
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
