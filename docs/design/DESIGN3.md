---
name: Nocturne Atmosphere
colors:
  surface: '#130b38'
  surface-dim: '#130b38'
  surface-bright: '#393360'
  surface-container-lowest: '#0e0533'
  surface-container-low: '#1b1440'
  surface-container: '#1f1945'
  surface-container-high: '#2a2450'
  surface-container-highest: '#352f5b'
  on-surface: '#e5deff'
  on-surface-variant: '#d2c5ab'
  inverse-surface: '#e5deff'
  inverse-on-surface: '#312a57'
  outline: '#9b9078'
  outline-variant: '#4e4632'
  surface-tint: '#f3c000'
  primary: '#ffeabd'
  on-primary: '#3d2e00'
  primary-container: '#ffc900'
  on-primary-container: '#6e5500'
  inverse-primary: '#755b00'
  secondary: '#c9bfff'
  on-secondary: '#2e009c'
  secondary-container: '#4720ca'
  on-secondary-container: '#baaeff'
  tertiary: '#c8f4ff'
  on-tertiary: '#00363f'
  tertiary-container: '#47e2ff'
  on-tertiary-container: '#006271'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdf91'
  primary-fixed-dim: '#f3c000'
  on-primary-fixed: '#241a00'
  on-primary-fixed-variant: '#584400'
  secondary-fixed: '#e5deff'
  secondary-fixed-dim: '#c9bfff'
  on-secondary-fixed: '#1a0063'
  on-secondary-fixed-variant: '#441cc8'
  tertiary-fixed: '#a6eeff'
  tertiary-fixed-dim: '#27d9f7'
  on-tertiary-fixed: '#001f25'
  on-tertiary-fixed-variant: '#004e5b'
  background: '#130b38'
  on-background: '#e5deff'
  surface-variant: '#352f5b'
typography:
  display-hero:
    fontFamily: Plus Jakarta Sans
    fontSize: 84px
    fontWeight: '200'
    lineHeight: 92px
    letterSpacing: -0.04em
  display-hero-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 72px
    fontWeight: '200'
    lineHeight: 80px
    letterSpacing: -0.03em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '500'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 17px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 13px
    fontWeight: '500'
    lineHeight: 18px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.25rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system expresses the quiet clarity of an open night sky, tailored for a high-performance Android weather experience. It balances atmospheric immersion with utilitarian precision: ambient, deep cosmic gradients provide visual depth, while high-legibility typographic hierarchies deliver mission-critical forecasts at a glance.

The visual style blends modern Glassmorphism with Material 3 spatial logic:
- Multi-stop night sky gradients evoking altitude and temporal depth.
- Translucent, frosted glass surfaces with ultra-fine luminous boundaries.
- Precise, restrained application of Turkcell Yellow to direct attention toward active meteorological states, alerts, and primary interactions.
- Avoidance of absolute extremes—no harsh #000000 or blinding #FFFFFF, ensuring optimal ocular comfort during low-light usage.

## Colors

The palette establishes an immersive nocturnal hierarchy through balanced luminosity tiers and strict contrast ratios:

- **Atmosphere Canvas**: The foundational viewport canvas uses a linear vertical gradient transitioning across three color stops: `#1B1440` (0%) through `#2A1B5E` (50%) to `#3A2478` (100%).
- **Primary Accent (`#FFC900`)**: Turkcell Yellow serves as the single high-impact chromatic beacon. It is reserved for active states, key weather condition indicators (such as UV index peaks or clear sky sunbursts), selected tab pills, and decisive primary actions.
- **Secondary & Tertiary Atmospherics**: `#7B61FF` acts as a soft twilight tint for active glass refraction and icon glows; `#38E1FF` designates atmospheric moisture tokens (precipitation probability, humidity indices).
- **Text & Foreground Luminance**:
  - Primary text and prominent glyphs: Soft celestial white `#DEE2F8` (~87% perceived white) for glare-free readability.
  - Secondary text and captions: Muted starlight lavender `#9E9EB8` (~60% perceived opacity).
  - Tertiary / Disabled state: `#68648A`.
- **Surfaces & Borders**:
  - Card base: Semi-transparent frosted navy `rgba(32, 22, 74, 0.55)` over background layers, supported by `backdrop-filter: blur(20px)`.
  - Glass Boundary: 1px continuous stroke tinted with `rgba(222, 226, 248, 0.12)`.

## Typography

The typographic hierarchy prioritizes rapid scannability and structural elegance under the target 412x915 dp viewport. Plus Jakarta Sans provides clean geometric clarity with open letterforms, avoiding character collision in dynamic data strings.

- **Hero Temperature Metrics**: Rendered in `display-hero` (84px desktop/tablet, 72px mobile) with an ultra-light 200 weight. The degree symbol (`°`) is sized down proportionally to maintain visual equilibrium with large numerals.
- **Condition & Location Headings**: Localized strings (e.g., "Parçalı Bulutlu", "İstanbul") use medium and semi-bold weights (`headline-md`, `headline-lg`) to balance the slender hero numbers.
- **Labels & Micro-Data**: Meteorological metrics (rüzgar, nem, basıç, UV) adhere to minimum 13px sizes (`label-md`), preventing low-light legibility degradation. Text transforms for data labels should remain natural case or subtle uppercase tracking when specifying units (e.g., "KM/S", "HPA").

## Layout & Spacing

The layout is anchored on an 8px spatial grid, adhering strictly to Android Material 3 fluid column guidelines tailored to modern tall aspect ratios (specifically optimized for 412x915 dp devices):

- **Grid Model**: A fluid 4-column layout on mobile devices transitioning to an 8-column layout on foldable/tablet dimensions. Margin width is fixed at `16px` (1rem) on mobile viewports with an interior gutter of `12px` between split data cards.
- **Vertical Flow Rhythm**: Section-to-section transitions follow a rhythm of `space-xl` (32px), while individual sensor telemetry widgets within compound cards maintain `space-sm` (8px) internal separations.
- **Edge Containment**: Weather header heroes float with dynamic top padding factoring in the system status bar safe area plus `space-lg` (24px). Bottom navigation and drawer anchors preserve a standard 56dp action zone padded with `space-md` against device gesture insets.

## Elevation & Depth

Visual hierarchy is constructed through luminous translucency and directional light refraction rather than opaque drop shadows:

- **Level 0 (Atmosphere Canvas)**: Pure gradient canvas with procedural micro-star dot accents (`rgba(222, 226, 248, 0.35)` with variable radii between 1dp and 2dp) layered into background asset passes.
- **Level 1 (Ambient Weather Glass)**: Primary information cards and forecasts sit on translucent plates:
  - Surface: `rgba(42, 27, 94, 0.45)`.
  - Blur: `backdrop-filter: blur(24px)`.
  - Border: 1px uniform perimeter stroke in `rgba(255, 255, 255, 0.12)`.
  - Shadow: Diffused ambient falloff `0 8px 32px 0 rgba(10, 6, 28, 0.35)`.
- **Level 2 (Active & Elevated Containers)**: Floating sheets, search interfaces, and detail modals:
  - Surface: `rgba(58, 36, 120, 0.65)`.
  - Blur: `backdrop-filter: blur(36px)`.
  - Border: 1px top-highlight stroke `rgba(255, 255, 255, 0.20)` blending into base boundary `rgba(255, 255, 255, 0.08)`.
  - Shadow: Tinted expansion `0 16px 40px -8px rgba(5, 3, 16, 0.60)`.
- **Level 3 (Focused Interactive Indicators)**: Selected state indicators and accent pills cast a subtle ambient glow using the primary accent: `0 0 16px rgba(255, 201, 0, 0.28)`.

## Shapes

The design system enforces a 24dp corner standard across all prominent cards and modal sheets, producing an organic, tactile silhouette that complements frosted glass refraction:

- **Surface Cards & Modular Panels**: Strictly 24dp (`rounded-xl` equivalent in this configuration) on all quadrants. Nested sub-modules within composite cards inherit a reduced 16dp radius (`rounded-lg`) to maintain concentric harmony.
- **Interactive Buttons & Selector Pills**: Full pill geometry (stadium radius) for navigation tabs, forecast timeline toggles (Saatlik / Günlük), and quick-action chips.
- **Input Fields & Search Bars**: 20dp border-radius for search inputs, offering visual distinction between structural metric tiles and executable inputs.

## Components

### Buttons
- **Primary Action (Vurgulu Buton)**: Fully rounded stadium pill. Background: `#FFC900`; label typography: `label-lg`, `#1B1440` (high contrast dark navy font); subtle inner highlight. Pressed state: scale `0.98`, background `#E5B500`.
- **Secondary Action (Cam Buton)**: Translucent glass pill (`rgba(222, 226, 248, 0.08)`) with 1px border `rgba(255, 255, 255, 0.16)`. Label in `#DEE2F8`.

### Weather Cards & Metric Tiles
- **Standard Forecast Card**: 24dp curved container, frosted glass background, 16px internal padding. Layout organizes telemetry into a primary data point (`headline-sm`, `#DEE2F8`), secondary descriptor (`label-md`, `#9E9EB8`), and an upper-right contextual status glyph.
- **Hourly Horizontal Rail**: Scrollable list of micro-cards (16dp rounded radius) containing time (`label-sm`), weather icon (28x28dp), and temperature reading (`body-lg`, 500 weight). The card corresponding to "Şu An" uses an active `#FFC900` outline indicator.

### Segmented Controls & Pills
- **Timeline Switcher (Saatlik / 7 Günlük)**: Enclosed glass pill container (`rgba(20, 14, 48, 0.60)`). Selected segment slides over with a stadium-shaped Turkcell Yellow pill (`#FFC900`), switching active text to `#1B1440` bold, while inactive options remain `#9E9EB8`.

### Input Fields (Konum Ara)
- **Search Surface**: Container height 52dp, 20dp corner radius, background `rgba(42, 27, 94, 0.50)` with 1px stroke `rgba(222, 226, 248, 0.14)`.
- **Typographic States**: Placeholder text in `#9E9EB8` (`body-md`), active typed input in `#DEE2F8`. Leading icon tinted with Turkcell Yellow when focused.

### Status Chips & Badges
- **Hava Kalitesi / Uyarı Rozeti**: Rounded-full pill (height 28dp, padding 4px 12px). Semi-transparent primary fill (`rgba(255, 201, 0, 0.12)`) paired with `#FFC900` typography and 1px border (`rgba(255, 201, 0, 0.32)`).

### Selection Controls
- **Radio & Switches**: Switch track in frosted glass (`rgba(222, 226, 248, 0.16)`); active thumb transitions to `#FFC900` accompanied by an expanded active track tint.