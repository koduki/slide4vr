/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.logging.gcp.datastore;

import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.StringValue;

/**
 *
 * @author koduki
 */
public class Extentions {

    public static BooleanValue noindex(boolean value) {
        return BooleanValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }

    public static StringValue noindex(String value) {
        return StringValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }
}
