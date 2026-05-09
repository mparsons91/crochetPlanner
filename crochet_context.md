# Crochet Domain Context

Reference notes distilled from beginner guides, the Craft Yarn Council standards, and
general crochet knowledge. Used by the app's pattern generator and by future
contributors who may not have a crochet background.

> Sources reviewed
> - hopelesslydevotedcrochet.com – beginner basics
> - craftyarncouncil.com – pattern reading + standards
> - anniesattic.com – pattern reading (rows/rounds, repeats, gauge)
> - crochet.com – charts/symbols (URL returned 403 during research; chart
>   conventions below come from general CYC-aligned knowledge and should be
>   double-checked before being used as a primary source).

All terminology in this document is **US crochet terms** (the dominant
convention in North America). UK terms differ by one stitch level (US sc = UK
dc, US dc = UK tr, etc.) – see "US vs UK" below.

---

## 1. What Crochet Is

Crochet builds fabric one loop at a time using a single hook. Each stitch is
formed by drawing yarn through one or more existing loops on the hook. Unlike
knitting, only one live loop is on the hook at any time (with rare exceptions),
which makes the structure easy to model: a pattern is a deterministic sequence
of stitches plus instructions about where to insert the hook.

## 2. Tools

- **Hook** – sized in millimeters and lettered/numbered. Hook size controls
  stitch size and therefore gauge.
- **Yarn** – sized by weight (thickness), categorized 0–7 by the Craft Yarn
  Council.
- **Scissors, yarn/tapestry needle, stitch markers** – supporting tools.

### Standard Yarn Weights (Craft Yarn Council)

| # | Name | Typical use | Recommended hook (metric) | Recommended hook (US) | Sc gauge / 4" |
|---|------|-------------|---------------------------|-----------------------|---------------|
| 0 | Lace        | Doilies, fine lace        | 1.4 – 2.25 mm | Steel 6 – B-1 | 32 – 42 |
| 1 | Super Fine  | Socks, fine baby items    | 2.25 – 3.5 mm | B-1 – E-4     | 21 – 32 |
| 2 | Fine        | Light baby items          | 3.5 – 4.5 mm  | E-4 – 7       | 16 – 20 |
| 3 | Light (DK)  | Light garments            | 4.5 – 5.5 mm  | 7 – I-9       | 12 – 17 |
| 4 | Medium (Worsted/Aran) | Most projects   | 5.5 – 6.5 mm  | I-9 – K-10½   | 11 – 14 |
| 5 | Bulky       | Rugs, jackets             | 6.5 – 9 mm    | K-10½ – M-13  | 8 – 11  |
| 6 | Super Bulky | Heavy blankets            | 9 – 15 mm     | M-13 – Q      | 6 – 9   |
| 7 | Jumbo       | Arm crochet, oversize     | 15 mm+        | Q+            | <6      |

> The MVP uses these as defaults / validation hints. They are recommendations,
> not hard rules — users may override hook size for their preferred fabric.

## 3. Basic Stitches (US terms)

Stitch height roughly doubles at each step from sc → hdc → dc → tr.

| Abbr | Name                | Yarn-overs before insert | Steps to complete | Relative height |
|------|---------------------|--------------------------|-------------------|-----------------|
| ch   | Chain               | 0                        | yo, pull through 1 | n/a            |
| sl st| Slip stitch         | 0                        | insert, yo, pull through st + loop | tiny |
| sc   | Single crochet      | 0                        | insert, yo+pull (2 loops), yo+pull through 2 | 1× |
| hdc  | Half double crochet | 1                        | insert, yo+pull (3 loops), yo+pull through 3 | ~1.5× |
| dc   | Double crochet      | 1                        | insert, yo+pull (3 loops), yo+pull through 2, yo+pull through 2 | 2× |
| tr   | Treble crochet      | 2                        | as dc but one extra "yo+pull through 2" | ~3× |

### Turning chains

When the work turns at the end of a row, a turning chain raises the hook to
the height of the next stitch. Standard heights:

| Stitch in next row | Turning chain | Counts as a stitch? |
|--------------------|---------------|---------------------|
| sc                 | ch 1          | No                  |
| hdc                | ch 2          | Sometimes (varies)  |
| dc                 | ch 3          | Yes                 |
| tr                 | ch 4          | Yes                 |

## 4. Pattern Notation

| Notation | Meaning |
|----------|---------|
| `*…*`    | Repeat the instructions between asterisks. e.g. `*sc, dc; rep from * across` |
| `[ … ] N times` | Repeat the bracketed group N times |
| `{ … }`  | Nested repeats inside `[ ]` |
| `( … )`  | Stitches worked together into a single stitch (e.g. shells: `(2 dc, ch 3, 2 dc)` in one st) |
| `( N )` at row end | Stitch count for that row/round (omitted when unchanged) |
| `**`     | Marks where the final repeat ends (often partial) |

### Other terms

- **inc / dec** – increase / decrease (typ. 2 sc in next st / sc2tog).
- **join** – usually `sl st in top of beginning ch`.
- **turn** – flip work to begin the next row.
- **rep** – repeat.
- **right side / wrong side (RS / WS)** – the public-facing vs. inside face.

## 5. Rows vs. Rounds

- **Rows**: work back and forth, turn at each end. Used for flat rectangular
  pieces (scarves, blankets, the rectangle shape in our MVP).
- **Rounds (rnds)**: work in a spiral or join with a slip stitch at the end of
  each round. Used for closed shapes (circle, oval, hats, amigurumi).
- A **magic ring / adjustable ring** is the standard way to begin a round
  worked in the round – it closes tightly with no center hole. Alternative:
  `ch N, sl st to join` to form a starting ring (leaves a small hole).

## 6. Gauge

A swatch (typically 6" square, measured in the central 4") tells you how many
stitches and rows fit in a given area at your tension with a given hook + yarn.
Gauge is what converts the user's desired physical dimensions (cm or inches)
into a stitch count.

> The MVP can ship without requiring the user to measure a swatch – we can
> derive a starting estimate from CYC standard gauge for the chosen yarn
> weight, and let the user override later. This must be surfaced to the user as
> an **estimate** because real gauge varies per crocheter.

## 7. Shape Recipes (the math the generator needs)

These are the canonical recipes for the MVP shapes. Stitch counts are derived
from gauge `(stitches_per_inch, rows_per_inch)` for the chosen yarn/hook.

### Flat circle (worked in rounds)

Classic "increase by N each round" rule, where `N` is the number of stitches
in the first round:

| Base stitch | Stitches in Rnd 1 (N) | Increase per round |
|-------------|-----------------------|--------------------|
| sc          | 6                     | +6                 |
| hdc         | 8                     | +8                 |
| dc          | 12                    | +12                |

- Round 1: N stitches in a magic ring.
- Round 2: 2 in each st → 2N.
- Round 3: `[sc in next, 2 sc in next] × N` → 3N.
- Round k: `(k-1)*N → k*N` total stitches; one extra st between increases each round.
- Stop when `radius_in_rounds × row_height ≥ desired_radius`.

### Oval (worked in rounds around a starting chain)

- Foundation: `ch C` where `C ≈ (length − width) × stitches_per_inch + 1`.
- Round 1: work along both sides of the chain. e.g. for sc:
  `sc in 2nd ch from hook, sc across to last ch, (sc, ch 1, sc) in last ch,
   sc back along the other side, (sc, ch 1, sc) in first ch, join`.
- Each subsequent round adds increases only at the two rounded ends (mirroring
  the circle increase pattern locally), keeping the straight sides straight.

### Rectangle (worked in rows)

- `width_stitches = round(width × stitches_per_inch)`.
- Foundation chain: `ch (width_stitches + turning_ch)`. For sc, `+1`; for dc, `+3`.
- Row 1: work the chosen base stitch across.
- Repeat until `rows × row_height ≥ desired_height`.
- Edges remain straight; no shaping.

### Square

- Special case of rectangle where width = height.
- Two reasonable approaches:
  1. **Worked in rows** (same as rectangle, width = height) – simplest for MVP.
  2. **Granny square** worked in the round from the center – more decorative.
- MVP: implement (1). (2) is a stretch.

## 8. Yarn-Length Estimation

Two valid approaches; the MVP can use either or both.

**A. Stitch-count × per-stitch length (rule-of-thumb)**

Approximate yarn used per stitch with worsted (#4) yarn:

| Stitch | Yarn per stitch (in) |
|--------|---------------------|
| ch     | ~0.5                |
| sc     | ~1.0 – 1.5          |
| hdc    | ~1.5 – 2.0          |
| dc     | ~2.0 – 3.0          |
| tr     | ~3.0 – 4.0          |

Scale linearly by yarn diameter for other weights (lace ~0.4×, bulky ~1.5×,
super bulky ~2×). These are estimates and should be presented as such.

**B. Area × yardage-per-square-inch**

For a known yarn at standard gauge, a rough constant of yards-per-square-inch
exists (e.g. worsted #4 sc ≈ 0.3 yd/in² of fabric). Multiply by the shape's
area + 10% margin for tails/joining.

The MVP should compute via (A) (more accurate per-stitch) and display the
result with explicit "estimate" / margin language. Always add ~10–15% for
weaving in ends.

## 9. Charts / Symbol Diagrams (for future "visualization" stretch goal)

Standard CYC symbols (high level – verify against the original chart resource
before implementing rendering):

| Symbol            | Stitch         |
|-------------------|----------------|
| `o`               | chain (ch)     |
| `•` or `-`        | slip stitch    |
| `+` or `×`        | single crochet |
| `T`               | half double    |
| `T` with one slash| double crochet |
| `T` with two slashes | treble       |

Charts represent rows from bottom to top; rounds are drawn from the center
out. Reading direction alternates each row to mirror RS/WS work. Charts are
typically preferred over written patterns for visually complex motifs (lace,
filet, granny squares).

## 10. US vs UK Terminology

| US             | UK              |
|----------------|-----------------|
| sc (single)    | dc (double)     |
| hdc (half dbl) | htr (half treble) |
| dc (double)    | tr (treble)     |
| tr (treble)    | dtr (dbl treble)|

The MVP standardizes on **US terms** in storage, computation, and display.
A future toggle could render UK terms.

---

## Open questions / gaps to revisit

- The crochet.com chart resource was inaccessible during research — confirm
  symbol mappings against an authoritative source before implementing chart
  rendering.
- Per-stitch yarn-length constants vary widely between sources; we may want
  to calibrate against real swatches before promoting estimates to "good
  enough."
- The MVP defines the "starting chain" deterministically per shape, but
  some shapes (oval especially) have multiple valid starting-chain
  conventions. Pick one and document it in the generator code.
