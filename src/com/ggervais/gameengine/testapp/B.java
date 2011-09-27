package com.ggervais.gameengine.testapp;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: ggervais
 * Date: 13/07/11
 * Time: 11:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class B extends A {

    private static final Logger log = Logger.getLogger(B.class);

    public void process(B b) {
        log.info("b");
    }

    public void process(A a) {
        log.info("a");
    }

    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        A bb = new B();

        B test = new B();
        b.process(a);
        b.process(b);
        b.process(bb);
    }
}
