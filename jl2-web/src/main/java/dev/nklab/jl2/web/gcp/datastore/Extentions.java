/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.gcp.datastore;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BooleanValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;

/**
 *
 * @author koduki
 */
public class Extentions {
    public static EntityValue noindex(Entity value) {
        return EntityValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }

    public static TimestampValue noindex(Timestamp value) {
        return TimestampValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }

    public static LongValue noindex(long value) {
        return LongValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }

    public static BooleanValue noindex(boolean value) {
        return BooleanValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }

    public static StringValue noindex(String value) {
        return StringValue.newBuilder(value).setExcludeFromIndexes(true).build();
    }
}
