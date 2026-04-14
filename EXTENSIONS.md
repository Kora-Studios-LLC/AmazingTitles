# AmazingTitles Extensions

This document describes the extension modules that follow the AmazingTitles extension model used in this project. Each extension is packaged as its own `E_*` Maven module and exposes animations through a single `Main` class that implements `AmazingExtension`.

## Architecture Pattern

All extensions in this repository follow the same structure:

- One module per extension, named `E_EXTENSIONNAME`
- One Java entrypoint per extension: `Main`
- One package per extension
- `Main` implements `AmazingExtension`
- Animations are registered inside `load()`
- Animations are created with `AnimationBuilder`
- Default visual settings are provided with `ComponentArguments.create(...)`
- Frame sequences are built with `LinkedList<String>`
- Final frame strings are formatted with `ColorTranslator.colorize(...)`

## Summer

- Extension name: `Summer`
- Package: `org.korastudios.amazingtitles.extension.summer`
- Purpose/theme: bright summer colors, sun wraps, warm pulse effects
- Registered animation keys:
  - `EXTENSION_SUMMER_WRAPPED_SUN`
    - Simple sun-wrap pulse around the text.
  - `EXTENSION_SUMMER_WRAPPED_COLORED_SUN`
    - Sun-wrap pulse with configurable sun highlight color.
    - Arguments: `Hex(Sun-Color)`
  - `EXTENSION_SUMMER_GRADIENT`
    - Stable summer gradient title.
    - Arguments: `0/1(1=bold,0=normal)`
  - `EXTENSION_SUMMER_WAVES`
    - Warm highlight wave moving through the text.
  - `EXTENSION_SUMMER_BOUNCE`
    - Elastic highlight wave with a short rebound.
  - `EXTENSION_SUMMER_PULSING`
    - Sunset-to-sunlight pulsing glow.

## Winter

- Extension name: `Winter`
- Package: `org.korastudios.amazingtitles.extension.winter`
- Purpose/theme: icy gradients, frosty reveals, cold aurora-like motion
- Registered animation keys:
  - `EXTENSION_WINTER_SNOWFALL`
    - Falling-snow effect layered over icy letters.
  - `EXTENSION_WINTER_FROST_REVEAL`
    - Frost grows across the text and recedes.
  - `EXTENSION_WINTER_PLAIN`
    - Stable icy text style.
  - `EXTENSION_WINTER_GLACIER`
    - Glacier gradient with optional bold styling.
    - Arguments: `0/1(1=bold,0=normal)`
  - `EXTENSION_WINTER_AURORA_SWEEP`
    - Aurora-like sweep through the text.
  - `EXTENSION_WINTER_ICE_SHARDS`
    - Centered icy shard pulse.

## Bounce

- Extension name: `Bounce`
- Package: `org.korastudios.amazingtitles.extension.bounce`
- Purpose/theme: elastic reveals and energetic highlight motion
- Registered animation keys:
  - `EXTENSION_BOUNCE_CENTER_REVEAL`
    - Center-out reveal with overshoot, rebound, and settle.
  - `EXTENSION_BOUNCE_ELASTIC_WAVE`
    - Bright wave that pushes forward and rebounds slightly.
  - `EXTENSION_BOUNCE_PULSE`
    - Bouncy brightness pulse weighted toward the center.
- Arguments:
  - None

## Timer

- Extension name: `Timer`
- Package: `org.korastudios.amazingtitles.extension.timer`
- Purpose/theme: countdown and timing-focused title effects
- Registered animation keys:
  - `EXTENSION_TIMER_COUNTDOWN`
    - Numeric countdown from a configurable start value down to `0`.
    - Arguments: `startNumber`, `normalHex`, `warningHex`, `dangerHex`
  - `EXTENSION_TIMER_PROGRESS_BAR`
    - Text-based timer bar that drains as time goes down.
    - Arguments: `totalSteps(optional)`
  - `EXTENSION_TIMER_LAST_SECONDS`
    - Dramatic 3, 2, 1, 0 sequence with pulse and blink.
    - Arguments: none

## Glow

- Extension name: `Glow`
- Package: `org.korastudios.amazingtitles.extension.glow`
- Purpose/theme: polished luminous text with premium soft-light behavior
- Registered animation keys:
  - `EXTENSION_GLOW_SWEEP`
    - Glossy highlight sweep across the title.
  - `EXTENSION_GLOW_PULSE`
    - Breathing glow between soft and strong light.
  - `EXTENSION_GLOW_CORE`
    - Stable bright-center glow with softened edges.
- Arguments:
  - None

## Shockwave

- Extension name: `Shockwave`
- Package: `org.korastudios.amazingtitles.extension.shockwave`
- Purpose/theme: impact and expanding-wave visuals
- Registered animation keys:
  - `EXTENSION_SHOCKWAVE_CENTER`
    - Single expanding wave from the center.
  - `EXTENSION_SHOCKWAVE_DOUBLE`
    - Double expanding wave for layered impact.
  - `EXTENSION_SHOCKWAVE_REBOUND`
    - Expansion followed by a soft reverse pull.
- Arguments:
  - None

## Alert

- Extension name: `Alert`
- Package: `org.korastudios.amazingtitles.extension.alert`
- Purpose/theme: warnings, urgent notices, and restart prompts
- Registered animation keys:
  - `EXTENSION_ALERT_WARNING`
    - Amber warning pulse for important notices.
  - `EXTENSION_ALERT_DANGER`
    - Sharper red danger flashing sequence.
  - `EXTENSION_ALERT_RESTART`
    - Urgent restart effect with escalating warning colors.
- Arguments:
  - None

## Progress

- Extension name: `Progress`
- Package: `org.korastudios.amazingtitles.extension.progress`
- Purpose/theme: progress bars and moving active segments
- Registered animation keys:
  - `EXTENSION_PROGRESS_FILL`
    - Progress fills from left to right.
  - `EXTENSION_PROGRESS_DRAIN`
    - Progress drains down over time.
  - `EXTENSION_PROGRESS_PULSE`
    - Full bar with a brighter active segment moving through it.
- Arguments:
  - None

## Fire

- Extension name: `Fire`
- Package: `org.korastudios.amazingtitles.extension.fire`
- Purpose/theme: ember sparks, blaze waves, and heat surges
- Registered animation keys:
  - `EXTENSION_FIRE_EMBERS`
    - Ember-style spark flicker through the letters.
  - `EXTENSION_FIRE_BLAZE_WAVE`
    - Hot wave that travels through the text.
  - `EXTENSION_FIRE_HEAT_PULSE`
    - Fiery pulse between molten and bright-hot states.
- Arguments:
  - None

## Void

- Extension name: `Void`
- Package: `org.korastudios.amazingtitles.extension.voids`
- Purpose/theme: dark rifts, unstable glitch flicker, and ominous pulses
- Registered animation keys:
  - `EXTENSION_VOID_RIFT`
    - Dark center opening with brighter void edges.
  - `EXTENSION_VOID_GLITCH`
    - Controlled glitch flicker with slight offset and unstable highlights.
  - `EXTENSION_VOID_PULSE`
    - Dark-energy pulse centered in the title.
- Arguments:
  - None

## Cosmic

- Extension name: `Cosmic`
- Package: `org.korastudios.amazingtitles.extension.cosmic`
- Purpose/theme: space-inspired color movement, novas, and star dust
- Registered animation keys:
  - `EXTENSION_COSMIC_SWIRL`
    - Cyan-violet-pink swirl across the title.
  - `EXTENSION_COSMIC_NOVA`
    - Expanding stellar burst from the center.
  - `EXTENSION_COSMIC_DUST`
    - Gentle stardust shimmer.
- Arguments:
  - None

## Implementation Notes

- The code follows the AmazingTitles extension model used by the existing project.
- Every extension registers its keys inside `load()` using `AnimationBuilder`.
- Default display behavior is defined with `ComponentArguments.create(...)`.
- Frame collections use `LinkedList<String>` for consistency with the existing codebase.
- Frame formatting uses `ColorTranslator.colorize(...)` so the output works with the plugin’s formatting pipeline.
- Input handling is defensive:
  - Empty or null input is sanitized.
  - Invalid numeric arguments fall back to safe defaults.
  - Invalid hex colors fall back to predefined theme colors.

## Future Ideas

- Nature
- Storm
- Frost
- Neon
- Royal
- Tech

## Naming Convention

Animation keys follow the `EXTENSION_<GROUP>_<ANIMATION>` pattern.

Examples:

- `EXTENSION_BOUNCE_CENTER_REVEAL`
- `EXTENSION_TIMER_COUNTDOWN`
- `EXTENSION_VOID_GLITCH`

This keeps extension keys grouped, predictable, and easy to discover from command suggestions or documentation.

## Usage Notes

- Animation keys must be registered inside `load()` for the plugin to discover them.
- Visual output depends on frame order, frame count, and how color formatting is applied to each frame.
- Different server versions can render colors slightly differently, especially when RGB support is unavailable.
- Extensions are distributed as standalone jars and can be placed inside the plugin’s `Extensions` folder for runtime loading.
