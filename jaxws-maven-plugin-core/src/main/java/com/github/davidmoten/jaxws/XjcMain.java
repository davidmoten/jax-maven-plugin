package com.github.davidmoten.jaxws;

import com.sun.tools.xjc.Driver;

public class XjcMain {

    public static void main(String[] args) throws Throwable {
        if (Driver.run(args, System.out, System.out) != 0) {
            throw new Exception("xjc call failed, see logs above for details");
        }
    }
}
