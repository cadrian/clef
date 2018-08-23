# Presentation
Clef allows one to manage musical productions (pieces, works,  authors, pricing).

Clef is a simple tool that wants to help you commit to artwork ordered by customers (aka "authors" in the tool). It helps understanding how you spend your time and prepare for future deals.

Clef splits artwork into two levels:
  - "work" is a masterpiece (e.g. a whole soundtrack). It is linked to an "author" (customer) and a "pricing" (pricing plan, contract, ...)
  - "piece" is a self-contained work (e.g. a single music). It is linked to a "work".

"Sessions" are how time is tracked: simply start a new session, work, and stop the session when it is done for the day. Clef will track a few statistics on how you spent that time.

Clef also includes tons of "notes" that may help you track your ideas as you work. It also manages "properties" that you can attach to pretty much any Clef concept ("author", "work", "piece", "session"; "pricing" does not have them, yet).

Note: although I use it for musical composition, Clef can be used for any kind of artwork: books, cinema, ...

# TODO
- properties should be typed: use BLOB instead of CLOB; it could contain serialized data. Provide several editors:
  - text / rich text
  - numeric
  - dates
  - links to external files / resources (useful to register external data such as PDF files, etc.)
- property descriptors should set the property type
- add some warnings if not all is saved
- enhance "please confirm delete" message
