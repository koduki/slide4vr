/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.mp;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 *
 * @author koduki
 */
public class Config {

    public static boolean get(String property, boolean defaultValue) {
        return ConfigProvider.getConfig().getOptionalValue(property, Boolean.class).orElse(defaultValue);
    }

    public static String get(String property, String defaultValue) {
        return ConfigProvider.getConfig().getOptionalValue(property, String.class).orElse(defaultValue);
    }
}
