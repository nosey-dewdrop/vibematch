# vibematch

## Status
Current phase: Real time client/server done, heading into cloud deploy
Last session: 2026-07-01 — turned the app into a socket based client/server: a
Java server holds the shared database and pushes live updates, the desktop app is
now a client. Real time DMs + forum working and tested. See RAPOR.md.

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

### Phase 7: Real time client/server
- [x] socket server (java.net.ServerSocket, thread per client)
- [x] json request/reply protocol + client ServerClient
- [x] move all data behind the server, desktop becomes a client
- [x] live push for direct messages
- [x] live push for forum posts/comments
- [ ] deploy the server to a free cloud machine (Oracle) so it runs 24/7

### Phase 8: Speed, friends, notifications
- [x] cut extra round trips (membership comes with the community list)
- [x] load home and discover in the background so the ui never freezes
- [x] friends system: requests, accept/decline
- [x] privacy: you can only message your friends
- [x] notifications: bell with live unread count + list
- [ ] apply background loading to the rest of the screens too

### Phase 9: iOS (later)
- [ ] swiftui client that connects to the same server over sockets

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
