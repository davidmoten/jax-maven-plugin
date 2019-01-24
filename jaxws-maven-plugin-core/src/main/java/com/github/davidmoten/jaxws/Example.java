package com.github.davidmoten.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class Example {

    @WebMethod
    public String getIpAddress() {
        return "10.10.10.10";
    }

}