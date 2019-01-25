package com.github.davidmoten.jaxws;

import com.sun.tools.jxc.SchemaGenerator;

public class SchemaGenMain {

    public static void main(String[] args) throws Throwable {
        if (SchemaGenerator.run(args) != 0) {
            throw new Exception("schemagen call failed, see logs above for details");
        }
    }
}
