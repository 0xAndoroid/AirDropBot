# AirDropBot
A Telegram Bot for making cryptocurrency airdrops within community. Bot is very customizable: messages, tasks, etc.

## Set up
- Firstly, you need to create a bot token in BotFather in Telegram. Copy that token and paste it in settings.json
- Next, specify the currency that will be airdropped, and amounts that will be rewarded for referrals and/or joining airdrop.
- Now you can customize tasks. Answers to a task will be verified based on three condidions: wether answer had a dublicate sent by different user (for wallet address), by regex or wether user has joined telegram group/channel (you need to add bot to this group/channel with admin rights and specify group id in settings).
- Finally, you can customize messages however you would like.

## Launch
After you have changed settings to your needs, place compiled .jar file into the same folder as settings.json and groups.json, and then launch application:
```shell
java -jar AirDropBot.jar
```