# vibematch

## Status
Current phase: Core done, heading into Polish
Last session: 2026-06-30 — whole Phase 1 desktop app built end to end and tested.

## Roadmap

### Phase 1: Foundation
- [x] project setup (readme, gitignore, run.sh)
- [x] theme / app constants (lilac palette, fonts)
- [x] sqlite db layer + schema
- [x] model classes (User, Community, Post, Comment, Message, MbtiResult)
- [x] password hashing + validation utils

### Phase 2: Auth
- [x] user dao + auth service
- [x] email sender (smtp + popup fallback)
- [x] login screen
- [x] register + email verify screens

### Phase 3: Onboarding
- [x] interest selection
- [x] mbti service + 16 question test
- [x] vibe result screen (archetype + bars)

### Phase 4: Communities
- [x] community dao + service + seed data
- [x] match service (interest + mbti scoring)
- [x] home feed (matched communities)
- [x] discover / search
- [x] community detail (join / leave)

### Phase 5: Forum + Messages
- [x] post dao + post detail (threaded comments) + new post
- [x] my communities
- [x] message dao + 1 on 1 dm panel

### Phase 6: Profile + Polish
- [x] profile (edit interests / retake mbti, stats)
- [x] settings + logout
- [ ] polish pass, empty states, edge cases (run it and click through)

### Phase 7: iOS (later)
- [ ] expose data layer through a small local backend
- [ ] swiftui client

## How to run
`./run.sh` from the project root. First run seeds sample communities + people.
Demo logins (password `vibe1234`): ada, mert, zeynep, can, elif — all
@ug.bilkent.edu.tr. Or register a fresh account (the email code shows in a popup
if no SMTP is set up).

## Ideas
- "people you might vibe with" list on discover, using the same match scoring
- events per community (could port the old league-of-bilkent event code)

## Bugs / Issues
- none yet
