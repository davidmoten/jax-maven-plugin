package com.github.davidmoten.xjc;

import com.sun.tools.xjc.Driver;

public class DriverMain {

    private static final boolean USE_XJC_FACADE = true;

    public static void main(String[] args) throws Throwable {
        if (Driver.run(args, System.out, System.out) != 0) {
            throw new Exception("xjc call failed, see logs above for details");
        }
    }
}
