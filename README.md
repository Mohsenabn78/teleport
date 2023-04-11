

# Teleport

<img src="https://github.com/Mohsenabn78/teleport/blob/main/resource/logo.gif" alt="Teleport Logo" width="100%" height="500">

Teleport is a Gradle plugin that automates the delivery process of Android applications to a Telegram channel.

## Usage

To use Teleport and send your Android application to a Telegram channel, you first need to create a Telegram bot and add it to your channel. Then, you need to add the bot token and chat ID to your `config.json` file. Here's how you can create a bot and add it to your channel:

1. Log in to your Telegram account in the Telegram app.
2. Open a chat with @BotFather and send the `/newbot` command to create a new bot.
3. Choose a display name and username for your bot.
4. After creating the bot, copy the bot token provided to you and paste it into the `config.json` file in your project.
5. Add the bot to the Telegram channel where you want to send your application.
6. To find the chat ID of the channel, go to the channel and click on its name at the top of the page. The channel will open, and its address will be displayed to you. The chat ID is the number after the `-` sign in the address. Add this chat ID to the `config.json` file.

After completing these steps, your `config.json` file should look like this:

```json
{
  "botToken": "YOUR_BOT_TOKEN",
  "chatID": "YOUR_CHAT_ID"
}
```

Now, you can use the `./gradlew uploadApk` command to send your APK file to the Telegram channel.

## Customization

Teleport provides several parameters that you can customize to fit your needs:

- **taskEnabled:** Enables or disables task execution.
- **attemptCount:** Sets the number of retries when the task fails.
- **attemptDuration:** Sets the time interval between retries.
- **caption:** Specifies a message for the APK file that will be sent.
- **applicationName:** Specifies a name for the APK file that will be sent.
- **buildType:** Sets the build type (debug or release).

To customize these parameters, add the following code to your `build.gradle` file:

```groovy
configure<TeleportExtension> {
    taskEnabled.set(true)
    attemptCount.set(3)
    attemptDuration.set(5000)
    caption.set("My app")
    applicationName.set("MyAppName")
    buildType.set("ApplicationMode.DEBUG")
}
```

## License

Teleport is licensed under the MIT License. See the LICENSE file for more information.
