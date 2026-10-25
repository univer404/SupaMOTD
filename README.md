# SupaMOTD

A standalone Paper/Purpur plugin that converts the supplied MOTD Skript into Java. It does not require Skript.

## Features
- Configurable two-line server-list MOTD.
- MiniMessage formatting, including hex colors such as `<##007dfc>`, plus common legacy `&` color/format codes and `&#RRGGBB` hex colors.
- Configurable maximum player count (default: 3000).
- `/motd reload` reloads the plugin's configuration without requiring Skript.
- Permission: `motd.use` (default: operators).
- Plugin-generated feedback is non-italic by default.

## Installation
1. Download `SupaMOTD-1.0.0.jar` from a successful GitHub Actions build artifact.
2. Put it in the server's `plugins` folder.
3. Start the Paper/Purpur server once to generate `plugins/SupaMOTD/config.yml`.
4. Edit the MOTD and max player count, then run `/motd reload`.

Requires Java 21 or newer and Paper/Purpur for Minecraft 1.21.11.

## Configuration
- `motd.line-1`: first server-list line.
- `motd.line-2`: second server-list line.
- `max-player-count`: maximum player count displayed in the server list.

The two configured lines are joined with a newline. This changes the displayed server-list count, not the server's actual connection capacity.
