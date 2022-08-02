package com.meghd2.customjsonreader;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public class JSONHeader implements Header {
    @Override
    public HeaderElement[] getElements() throws ParseException {
        return new HeaderElement[0];
    }

    @Override
    public String getName() {
        return "Accept";
    }

    @Override
    public String getValue() {
        return "application/json";
    }
}
