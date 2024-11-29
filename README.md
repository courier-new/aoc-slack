<img src="aoc.svg" alt="AoC logo" width="100" height="100">

# aoc-slack

Slack integation for [Advent of Code](adventofcode.com), written in Kotlin.

## Features

### Posting messages about solved puzzles to Slack channels in real time*.

![img.png](stars.png)

<sub>*Real time means that the bot will check for new stars every 15 minutes to
respect [AoC automation guidelines](https://www.reddit.com/r/adventofcode/wiki/faqs/automation/).</sub>

### Posting a message with leaderboard at the end of the day.

![img_2.png](leaderboard.png)

## Installation

To install Slack bot you need to add Slack app to your workspace and run aoc-slack somewhere.

### Add app to your Slack workspace

1. Go to https://api.slack.com/apps and create a new app.
2. Select "From scratch" and give it a name and workspace.
3. Select incoming webhooks and activate it.
4. Click "Add new webhook to your workspace" and select a channel to which you want to receive messages.

### Set up environment

Duplicate `.env.example` and rename it to `.env`. Then, set the following required
environment variables:

| Property | Description |
|-|-|
| AOC_SLACK_LEADERBOARD_ID | ID of your private leaderboard in advent of code |
| AOC_SLACK_SESSION        | Session cookie from AoC website |
| AOC_SLACK_WEBHOOK_URL    | URL to Slack app webhook |

You can also uncomment and set any optional environment variables:

| Property | Description |
|-|-|
| AOC_SLACK_YEAR              | Year for which bot should work (_default current year_) |
| AOC_SLACK_STARS_CRON        | Cron how often new stars should be refreshed (_default every 15min, which is the [minimum allowed](https://www.reddit.com/r/adventofcode/wiki/faqs/automation/)_) |
| AOC_SLACK_LEADERBOARD_CRON  | Cron how often leaderboard should be sent (_default midnight EST (UTC-5)_) |
| AOC_SLACK_DAILY_POST_CRON  | Cron how often daily puzzle post should be sent (_default midnight EST (UTC-5)_) |
| AOC_SLACK_ONLY_ACTIVE_USERS | Shows only users with any points on leaderboard (_default False_) |
| AOC_SLACK_DEBUG             | Run app in debug mode (_default False_) |
| AOC_SLACK_USE_TEST_DATA     | Use test data in place of hitting AoC API (_default False_) |

### Run the application

aoc-slack can be spun up locally using
[docker-compose](https://docs.docker.com/compose/) with the following command:

```bash
docker-compose up --build
```

Code is not hot-reloaded, so you will need to rebuild the container image after
each change.