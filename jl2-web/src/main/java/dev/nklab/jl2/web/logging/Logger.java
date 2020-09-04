/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.jl2.web.logging;

import dev.nklab.jl2.collections.Tuples;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author koduki
 */
public class Logger {

    private static java.util.logging.Logger logger;

    private Logger() {
    }

    private Logger(String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public void debug(String name, Tuples.Tuple2<String, String>... messages) {
        log(Level.INFO, messages, name);
    }

    public void info(String name, Tuples.Tuple2<String, String>... messages) {
        log(Level.INFO, messages, name);
    }

    private void log(Level level, Tuples.Tuple2<String, String>[] messages, String name) {
        var msg = String.join(",",
                Arrays.stream(messages)
                        .map(x -> x._1() + ":" + x._2())
                        .collect(Collectors.toList()));

        logger.log(level, "event:" + name + "," + msg);
    }
}
