# AstralisFly

Lightweight **Paper plugin** providing time-limited fly mode with vote rewards, coupons, and pluggable persistence.

[![CI](https://github.com/sylxnc/AstralisFly/actions/workflows/ci.yml/badge.svg)](https://github.com/sylxnc/AstralisFly/actions/workflows/ci.yml)  
[![Release](https://github.com/sylxnc/AstralisFly/actions/workflows/release.yml/badge.svg)](https://github.com/sylxnc/AstralisFly/actions/workflows/release.yml)  
![Java](https://img.shields.io/badge/Java-21%2B-blue)  
![Paper](https://img.shields.io/badge/Paper-1.21.1%2B-00bfff)  
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## Overview

AstralisFly adds a **time-based fly system** to Paper servers.

Players can obtain fly time through:

- server voting (NuVotifier)
- administrative commands
- redeemable coupons

Fly time is persisted through a **pluggable storage layer** and can optionally be cached using Redis.

---

## Features

- Brigadier-based command system
- NuVotifier vote integration
- multiple persistence backends
- optional Redis caching
- automated CI and release workflows

Supported storage backends:

- MongoDB
- MySQL
- SQLite

---

## Requirements

- **Java 21+**
- **Paper 1.21.1+**

Optional integrations:

- NuVotifier
- Redis
- MongoDB or MySQL

---

## Installation

1. Download the latest release.
2. Place the JAR inside the server `plugins/` directory.
3. Start the server once to generate configuration files.
4. Configure `plugins/AstralisFly/config.yml`.
5. Restart the server.

---

## Commands

| Command | Description |
|--------|-------------|
| `/fly` | Toggle fly mode |
| `/flytime` | Show remaining fly time |
| `/flytime pay <player> <seconds>` | Transfer fly time |
| `/flytime add <player> <seconds>` | Add fly time (admin) |
| `/flytime coupon <amount>` | Create redeemable coupon (admin) |

---

## Permissions

| Permission | Description |
|-----------|-------------|
| `flytime.admin` | Access to administrative commands |
| `flytime.vote.default` | Default vote reward tier |
| `flytime.vote.vip` | VIP vote reward tier |
| `flytime.vote.mvp` | MVP vote reward tier |
| `flytime.vote.admin` | Highest vote reward tier |

---

## Build

```bash
mvn clean package