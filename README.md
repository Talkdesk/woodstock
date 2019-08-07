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

## Deploy

### AWS Authentication

As part of the deploy of the Woodstock library some files are uploaded to a mobile AWS S3 production bucket. For security reasons each team member should have its own credentials to [AWS Console](https://console.aws.amazon.com/console/home?region=us-east-1), which can be provided by *SRE team*.

When you receive your credentials you should follow [AWS Access](https://talkdesk.atlassian.net/wiki/spaces/SEC/pages/117938393/AWS+Access) wiki page to setup your AWS account. In order to access Mobile team specific resources you need to perform a [switch-role](https://talkdesk.atlassian.net/wiki/spaces/SEC/pages/117938393/AWS+Access#AWSAccess-Mobile). When accessing AWS Console the authentication and switch-role can be performed manually, but in order to perform a deploy locally you need to authenticate using [td-cli](https://github.com/Talkdesk/td-cli) command line tool:

```
td auth aws auth
```

After you perform the authentication **td-cli** will create a `~HOME/.aws/credentials` file with your temporary credentials to access AWS resources. To configure a switch-role you need to setup a **prd** AWS profile in `~HOME/.aws/config` file. The contents for the file can be found [here](https://github.com/Talkdesk/talkdesk-sec-confs/blob/master/aws/iam/auth/groups/mobile/config).

**Note:** CI application does not need to switch-role and therefore uses the **default** AWS profile. To deploy the library locally with your credentials you need to set **TALKDESK\_SDK\_AWS\_PROFILE** in `gradle.properties` to **prd**.

### Release

The release of Woodstock library is done manually by logging in AWS console and uploading the AAR files according to Maven structure. In the future releases will be automatically deployed via CI (when a tag is created with format **X.Y.Z**). The release deploy will be triggered via CI using Gradle wrapper command `./gradlew publishRelease`. 