package com.mcz.light_appproject.app.utils;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * Iot club all rights reserved
 * Created by Iot club on 2018/12/14.
 */

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = "DELETE";

    public String getMethod() { return METHOD_NAME; }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(final URI uri) {

        super();

        setURI(uri);

    }

    public HttpDeleteWithBody() { super(); }
}
