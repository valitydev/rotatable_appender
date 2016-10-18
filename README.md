# RotatableAppender

Appender для logback, необходимый для корректной работы с logrotate.

### Разработчики

- [Pavel Popov](https://github.com/tolkonepiu)


### Настройки на клиенте

Использовать `logback-spring.xml` (взять можно из папки `benchmark`)

`pom.xml`

```
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>4.6</version>
</dependency>
<dependency>
    <groupId>com.rbkmoney.logback</groupId>
    <artifactId>nop-rolling</artifactId>
    <version>1.0.0</version>
</dependency>
```

`application.properties`

```
logging.file= # Log file name. For instance `myapp.log`
logging.path= # Location of the log file. For instance `/var/log`
```

ps необязательные настройки, которые можно переопределить
