# Woodstock

## Usage

Woodstock can have as many `Logger` implementations as you want. Configure `Woodstock` as follows: 

```kotlin
Woodstock.setup(arrayOf(androidLogger, batchLogger))
```

After `Woodstock` is configured it can be used as normal logger:

```kotlin
Woodstock.log("My TAG", LogLevel.TRACE, "My beautiful log.")
```

Woodstock comes with two off-the-shelf  `Logger` implementations:

#### Android Logger

`AndroidLogger` logs to Logcat:

```kotlin
val androidLogger = AndroidLogger(true, true, 4000)
```

### Batch Logger

`BatchLogger` persists the logs and send them to a server `/log` endpoint when a specified threshold is reached:

```kotlin
val batchLogger = BatchLogger.Builder(context)
                .setBaseUrl("http://my.log.server.com")
                .setExtraDataProvider(extraDataProvider)
                .setInternalLogger(internalLogger)
                .setEnabled(true)
								.setThreshold(30)
                .build()
```

Every log that is sent can optionally have an extra data provided via an `ExtraDataProvider` implementation:

```kotlin
class LogExtraDataProvider() : ExtraDataProvider {
    override fun getExtraData(): Map<String, String> = mapOf("data" to "My Data!")
}
```

Also `BatchLogger` can receive an optional internal `Logger` implementation. The internal logger will be used to log information about the persistence and dispatch of the logs.

### Custom Logger

You are free to create `Logger` implementations yourself.

```kotlin
class CustomLogger() : Logger() {
    override fun log(level: LogLevel, message: String) {
        // Log the messages that I am interested in anyway I want.
    }

    override fun setup() {
        // Do initialization logic here.
    }
}
```

## Integration

### Gradle

```groovy
repositories {
    maven { url 'https://mobile-dev.talkdeskapp.com/android/maven' }
}

dependencies {
	implementation 'com.talkdesk:woodstock:0.1.0'
}
```