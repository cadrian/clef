[![Build Status](https://travis-ci.org/cadrian/clef.png?branch=master)](https://travis-ci.org/cadrian/clef)

# Presentation
Clef allows one to manage musical productions (pieces, works,  authors, pricing).

Clef is a simple tool that wants to help you commit to artwork ordered by customers (aka "authors" in the tool). It helps understanding how you spend your time and prepare for future deals.

Clef splits artwork into two levels:
  - "work" is a masterpiece (e.g. a whole soundtrack). It is linked to an "author" (customer) and a "pricing" (pricing plan, contract, ...)
  - "piece" is a self-contained work (e.g. a single music). It is linked to a "work".

"Sessions" are how time is tracked: simply start a new session, work, and stop the session when it is done for the day. Clef will track a few statistics on how you spent that time. Each session is also attached to one "activity" (e.g. composing, mixing…). Statistics are also available per activity.

Clef also includes tons of "notes" that may help you track your ideas as you work. It also manages "properties" that you can attach to any Clef concept ("pricing", "author", "work", "piece", "session").

# TODO
- import, export of data
- some actual code cleanup
- add statistics on authors and pricings
- add filters in the sessions and works tabs:
  - sessions: filter on author and/or work
  - works: filter on author
