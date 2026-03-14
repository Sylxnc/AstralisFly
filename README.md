# AstralisFly

Production-ready Paper plugin for time-based fly mode with coupons, vote rewards, and pluggable persistence.

[![CI](https://github.com/sylxnc/AstralisFly/actions/workflows/ci.yml/badge.svg)](https://github.com/sylxnc/AstralisFly/actions/workflows/ci.yml)
[![Package](https://github.com/sylxnc/AstralisFly/actions/workflows/package.yml/badge.svg)](https://github.com/sylxnc/AstralisFly/actions/workflows/package.yml)
[![Release](https://github.com/sylxnc/AstralisFly/actions/workflows/release.yml/badge.svg)](https://github.com/sylxnc/AstralisFly/actions/workflows/release.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-blue)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21.1%2B-00bfff)](https://papermc.io/)


## Contents

- [Key Features](#key-features)
- [Compatibility](#compatibility)
- [Installation](#installation)
- [Build From Source](#build-from-source)
- [Configuration Overview](#configuration-overview)
- [Commands](#commands)
- [Permissions](#permissions)
- [GitHub Automation](#github-automation)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Key Features

- **Paper-native setup** using `paper-plugin.yml`
- **Brigadier command tree** for `/fly` and `/flytime` subcommands
- **NuVotifier integration** via `VotifierEvent`
- **Multi-storage support**:
  - MongoDB
  - MySQL
  - SQLite (single-server mode)
- **Optional Redis cache** for player fly-time snapshots
- **Automated GitHub workflows** for CI, packaging, and release

## Compatibility

- Java: **21+**
- Paper: **1.21.1+**
- Optional services: NuVotifier, Redis, MongoDB, MySQL

## Installation

1. Build or download the plugin JAR.
2. Copy the JAR to your Paper server `plugins/` directory.
3. Start the server once to generate config files.
4. Edit `plugins/AstralisFly/config.yml` for your environment.
5. Restart the server.

## Build From Source

```bash
mvn clean verify
```

Build output:

- plugin JAR in `target/`

## Runtime Notes

- `NuVotifier` is loaded as plugin dependency and should be present on production servers.
- `sqlite` is intended for single-server setups.
- `mysql` and `mongodb` are recommended for multi-node or long-term persistence.
- Redis cache is optional and can be disabled without affecting core functionality.

## Configuration Overview

Main configuration file: `src/main/resources/config.yml`

- `storage.type`: `mongodb`, `mysql`, `sqlite`
- `storage.mongodb.*`: Mongo connection settings
- `storage.mysql.*`: MySQL connection settings
- `storage.sqlite.file`: local SQLite database file
- `cache.redis.enabled`: toggles Redis cache
- `vote-groups`: permission-based vote reward tiers

## Commands

- `/fly` - toggles fly mode if fly time is available
- `/flytime` - shows remaining fly time
- `/flytime pay <player> <seconds>` - transfer fly time
- `/flytime add <player> <seconds>` - admin command
- `/flytime coupon <amount> <s|m|h>` - create redeemable coupon (admin)

## Permissions

- `flytime.admin` - access admin fly-time commands
- `flytime.vote.default` - default vote reward tier
- `flytime.vote.vip` - VIP vote reward tier
- `flytime.vote.mvp` - MVP vote reward tier
- `flytime.vote.admin` - highest vote reward tier

## GitHub Automation

- `ci.yml` - build + test on push and pull request
- `package.yml` - build package artifact and upload it to workflow artifacts
- `release.yml` - publish tagged release artifacts (`v*`)

## Project Structure

- `src/main/java/net/astralis/flytime/commands` - Brigadier command registration
- `src/main/java/net/astralis/flytime/listeners` - gameplay + vote listeners
- `src/main/java/net/astralis/flytime/service` - fly-time core service
- `src/main/java/net/astralis/flytime/storage` - Mongo/MySQL/SQLite storage layer
- `src/main/java/net/astralis/flytime/cache` - cache abstraction + Redis implementation

## Contributing

Please read `CONTRIBUTING.md` before opening PRs.

Recommended contribution flow:

1. Create a focused branch from `main`.
2. Add tests for behavioral changes.
3. Run CI-equivalent checks locally (`mvn clean verify`).
4. Open a PR and fill in the template completely.

## License

This project is licensed under the MIT License. See `LICENSE` for details.

