package com.rbkmoney.log.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.EchoEncoder;
import org.junit.After;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.Assert.*;

public class RotatableFileAppenderTest {

    @Test
    public void triggeringPolicyIsRotationBased() throws Exception {
        assertInstanceOf(RotationBasedTriggeringPolicy.class, new RotatableFileAppender<Void>().getTriggeringPolicy());
    }

    @Test
    public void rollingPolicyIsNoop() throws Exception {
        assertInstanceOf(NoopRollingPolicy.class, new RotatableFileAppender<Void>().getRollingPolicy());
    }

    private void assertInstanceOf(Class<?> clazz, Object object) {
        assertNotNull(object);
        assertEquals(clazz, object.getClass());
    }

    private File logFile;
    private File rotatedLogFile;

    @Test
    public void itActuallyWorks() throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        RotatableFileAppender<String> appender = new RotatableFileAppender<>();
        appender.setEncoder(new EchoEncoder<>());
        appender.setFile(logFile.getPath());
        appender.setCheckCachePeriod(0);
        appender.setContext(lc);

        appender.start();

        appender.doAppend("event 1");
        logFile.renameTo(rotatedLogFile);
        appender.doAppend("event 2");

        appender.stop();

        assertEquals("event 1", readTheSingleLineWhichComprises(rotatedLogFile));
        assertEquals("event 2", readTheSingleLineWhichComprises(logFile));
    }

    @Test
    public void checkOnFileRename() throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        FileAppender<String> fileAppender = new FileAppender<>();
        fileAppender.setEncoder(new EchoEncoder<>());
        fileAppender.setFile(logFile.getPath());
        fileAppender.setContext(lc);

        fileAppender.start();

        fileAppender.doAppend("log 1");
        logFile.renameTo(rotatedLogFile);
        fileAppender.doAppend("log 2");
        assertEquals("log 1:log 2", readTwoLines(rotatedLogFile));
    }

    private String readTwoLines(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String one = reader.readLine();
        String two = reader.readLine();
        return one + ":" + two;
    }

    private String readTheSingleLineWhichComprises(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        String end = reader.readLine();
        assertNull(end);
        return line;
    }

    @After
    public void deleteTempFiles() {
        if (logFile != null) logFile.delete();
        if (rotatedLogFile != null) rotatedLogFile.delete();
    }

}
