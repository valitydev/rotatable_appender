package dev.vality.log.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import org.junit.After;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class RotatableFileAppenderTest {

    private void assertInstanceOf(Class<?> clazz, Object object) {
        assertNotNull(object);
        assertEquals(clazz, object.getClass());
    }

    private File logFile;
    private File rotatedLogFile;

    @Test
    public void itActuallyWorks() throws Exception {
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        RollingFileAppender<String> appender = new RollingFileAppender<>();

        appender.setFile(logFile.getPath());

        RotationBasedTriggeringPolicy<String> triggeringPolicy = new RotationBasedTriggeringPolicy<>();
        triggeringPolicy.setCheckCachePeriod(0);
        appender.setTriggeringPolicy(triggeringPolicy);
        triggeringPolicy.start();
        RollingPolicy rollingPolicy = new NoopRollingPolicy();
        appender.setRollingPolicy(rollingPolicy);
        rollingPolicy.setParent(appender);

        assertInstanceOf(RotationBasedTriggeringPolicy.class, appender.getTriggeringPolicy());
        assertInstanceOf(NoopRollingPolicy.class, appender.getRollingPolicy());

        appender.setEncoder(new EchoEncoder<>());
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
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
    public void renamedLogFileWithTimeoutMoreThanCachePeriodTest() throws Exception {
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        RollingFileAppender<String> appender = new RollingFileAppender<>();

        RotationBasedTriggeringPolicy<String> triggeringPolicy = new RotationBasedTriggeringPolicy<>();
        triggeringPolicy.setCheckCachePeriod(100);

        RollingPolicy rollingPolicy = new NoopRollingPolicy();
        rollingPolicy.setParent(appender);

        appender.setFile(logFile.getPath());
        appender.setEncoder(new EchoEncoder<>());
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(lc);
        appender.setTriggeringPolicy(triggeringPolicy);
        appender.setRollingPolicy(rollingPolicy);

        triggeringPolicy.start();
        appender.start();

        for (int i = 0; i < 100; i++) {
            appender.doAppend("event 1");
        }
        Thread.sleep(100);
        logFile.renameTo(rotatedLogFile);

        for (int i = 0; i < 100; i++) {
            appender.doAppend("event 1");
        }

        appender.stop();

        assertEquals(100, Files.readAllLines(logFile.toPath()).size());
        assertEquals(100, Files.readAllLines(rotatedLogFile.toPath()).size());
    }

    @Test
    public void renamedLogFileWithTimeoutLessThanCachePeriodTest() throws Exception {
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        RollingFileAppender<String> appender = new RollingFileAppender<>();

        RotationBasedTriggeringPolicy<String> triggeringPolicy = new RotationBasedTriggeringPolicy<>();
        triggeringPolicy.setCheckCachePeriod(100);

        RollingPolicy rollingPolicy = new NoopRollingPolicy();
        rollingPolicy.setParent(appender);

        appender.setFile(logFile.getPath());
        appender.setEncoder(new EchoEncoder<>());
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        appender.setContext(lc);
        appender.setTriggeringPolicy(triggeringPolicy);
        appender.setRollingPolicy(rollingPolicy);

        triggeringPolicy.start();
        appender.start();

        for (int i = 0; i < 100; i++) {
            appender.doAppend("event 1");
        }
        Thread.sleep(10);
        logFile.renameTo(rotatedLogFile);

        for (int i = 0; i < 100; i++) {
            appender.doAppend("event 1");
        }

        appender.stop();

        assertEquals(200, Files.readAllLines(rotatedLogFile.toPath()).size());
    }

    @Test
    public void checkOnFileRename() throws Exception {
        logFile = File.createTempFile(getClass().getSimpleName(), ".log");
        rotatedLogFile = new File(logFile.getPath() + ".1");

        FileAppender<String> fileAppender = new FileAppender<>();
        fileAppender.setEncoder(new EchoEncoder<>());
        fileAppender.setFile(logFile.getPath());

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
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
        if (logFile != null) {
            logFile.delete();
        }
        if (rotatedLogFile != null) {
            rotatedLogFile.delete();
        }
    }

}
