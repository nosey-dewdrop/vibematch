# vibematch

## Status
Current phase: Foundation
Last session: 2026-06-30 — repo created, scaffolding + roadmap set up.

## Roadmap

### Phase 1: Foundation
- [ ] project setup (readme, gitignore, run.sh)
- [ ] theme / app constants (lilac palette, fonts)
- [ ] sqlite db layer + schema
- [ ] model classes (User, Community, Post, Comment, Message, MbtiResult)
- [ ] password hashing + validation utils

### Phase 2: Auth
- [ ] user dao + auth service
- [ ] email sender (smtp + popup fallback)
- [ ] login screen
- [ ] register + email verify screens

### Phase 3: Onboarding
- [ ] interest selection
- [ ] mbti service + 16 question test
- [ ] vibe result screen (archetype + bars)

### Phase 4: Communities
- [ ] community dao + service + seed data
- [ ] match service (interest + mbti scoring)
- [ ] home feed (matched communities)
- [ ] discover / search
- [ ] community detail (join / leave)

### Phase 5: Forum + Messages
- [ ] post dao + post detail (threaded comments) + new post
- [ ] my communities
- [ ] message dao + 1 on 1 dm panel

### Phase 6: Profile + Polish
- [ ] profile (edit interests / retake mbti, stats)
- [ ] settings + logout
- [ ] polish pass, empty states, edge cases

### Phase 7: iOS (later)
- [ ] expose data layer through a small local backend
- [ ] swiftui client

## Ideas
- "people you might vibe with" list on discover, using the same match scoring
- events per community (could port the old league-of-bilkent event code)

## Bugs / Issues
- none yet
