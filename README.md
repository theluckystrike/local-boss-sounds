# Local Boss Sounds

Stop hearing every wither spawn and dragon death on the server. With this mod the wither spawn sound, the ender dragon death sound and the end portal sound play only when you are near the event, and they come from where it happened.

## The problem in vanilla

Three sounds in Minecraft are "global": when someone spawns a wither, kills the ender dragon or opens an end portal, every player in the world hears it, at full volume, right next to their head. Players in other dimensions hear it too. On a busy server with a wither farm this plays over and over, wherever you are.

## What this mod changes

| Sound | Vanilla | With the mod (default) |
|---|---|---|
| Wither spawn | Everyone hears it, from any distance | Heard within about 30 blocks, from the wither |
| Ender dragon death | Everyone hears it, from any distance | Heard within about 30 blocks, from the dragon |
| End portal opens | Everyone hears it, from any distance | Heard within about 30 blocks, from the portal |
| Any of these in another dimension | Heard | Not heard |

Close by, nothing is lost: you still hear the wither spawn when you build it. The sound now fades with distance and comes from its real direction instead of from inside your head.

All other sounds are untouched.

## Install

1. Install Fabric Loader 0.17 or newer.
2. Put `local-boss-sounds-fabric-1.0.0+26.x.jar` in your `mods/` folder. Fabric API is not needed.
3. Start the game. Settings are in `config/local-boss-sounds.json`.

Client side only. It works on any server, including vanilla servers and servers you do not run. The server does not need it.

## Config

```json
{
  "witherSpawn": "nearby",
  "dragonDeath": "nearby",
  "endPortalOpen": "nearby"
}
```

Each sound takes `everywhere` (vanilla), `nearby` or `off`. The file is read when the game starts.

## Good to know

- The server never tells your game where a far event is. It sends a point 32 blocks away in the event's direction. That is why "nearby" means about 30 blocks and cannot be set larger.
- If you run the server yourself and want the sounds gone for everyone, the gamerule `global_sound_events` set to `false` does that without any mod. This mod is for players, on any server, and it keeps the sound when you are close.

## Tested in the real game

A client game test starts a real Fabric 26.3 dedicated server and a real game client, joins it, and has the server fire each of the three events at known distances and in the Nether. The client records every sound it starts. It runs once per mode.

| Event | Vanilla (`everywhere`) | `nearby` (default) | `off` |
|---|---|---|---|
| Wither 10 blocks | heard 2.4 blocks from you | heard from the event, 10 blocks away | silent |
| Wither 120 blocks | heard 2.5 blocks from you | silent | silent |
| Wither in the nether | heard 0.4 blocks from you | silent | silent |
| Dragon 20 blocks | heard 2.5 blocks from you | heard from the event, 20 blocks away | silent |
| Dragon 300 blocks | heard 2.5 blocks from you | silent | silent |
| Portal 6 blocks | heard 2.3 blocks from you | heard from the event, 6 blocks away | silent |
| Portal 60 blocks | heard 2.5 blocks from you | silent | silent |

All 21 checks passed. Vanilla plays a wither 120 blocks away, a dragon 300 blocks away and a wither in the Nether about 2.5 blocks from your head. With the mod, events within 30 blocks play from their real spot and the rest stay silent.

Free and open source under the MIT license.

[zovo-support-start]: #

## Support Zovo

Zovo builds and maintains Local Boss Sounds, and every feature stays free. If it saves you time, you can support the work on [GitHub Sponsors](https://github.com/sponsors/theluckystrike). Sponsorship is optional, and the mod works the same without it.

You can also use [PayPal](https://www.paypal.com/paypalme/najzzz).

Bug reports and ideas go to the issue tracker linked on this page.

Zovo is made by [theluckystrike](https://github.com/theluckystrike) on GitHub.

## More mods by Zovo

- [Villager Replant](https://github.com/theluckystrike/villager-replant). Farmer villagers replant the crop they harvested, so rows stay one crop. Server side, Fabric 26.3.
- [Async Locator 26](https://github.com/theluckystrike/async-locator-26). Structure searches for /locate, maps, dolphins and eyes of ender run off the server thread. Fabric 26.3 and 1.21.11.
- [BucketBackup](https://github.com/theluckystrike/bucketbackup). Backs up Paper worlds to S3, Cloudflare R2, Backblaze B2, Wasabi or MinIO on a schedule.
- [All Zovo tools on GitHub](https://github.com/theluckystrike?tab=repositories). Free developer tools, including VS Code extensions and Apify Actors.

[zovo-support-end]: #
