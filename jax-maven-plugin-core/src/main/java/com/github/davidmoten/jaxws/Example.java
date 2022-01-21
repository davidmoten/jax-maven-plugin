package com.github.davidmoten.jaxws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class Example {

    @WebMethod
    public String getIpAddress() {
        return "10.10.10.10";
    }

}