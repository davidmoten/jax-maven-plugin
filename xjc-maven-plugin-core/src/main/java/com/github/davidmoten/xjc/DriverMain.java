package com.github.davidmoten.xjc;

import com.sun.tools.xjc.Driver;

public class DriverMain {

    public static void main(String[] args) throws Exception {

        if (Driver.run(args, System.out, System.out) != 0) {
            throw new Exception("xjc call failed, see logs above for details");
        }

    }

}
