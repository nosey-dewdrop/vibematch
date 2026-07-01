# vibematch — Build Stages (brick by brick)

How we built the app, one small brick at a time, bottom up. Each stage is one
commit. The rule the whole way: **a layer only knows the layer below it.** Model
knows nothing, data knows model, service knows data, the server knows service,
the screens know the client. That's why nothing breaks when we change one piece.

The stages map 1:1 to the git history (`git log --reverse`), so the commit list
is the proof we built it incrementally, not all at once.

---

## PHASE 1 — Foundation (the ground everything stands on)

**Stage 1 · Project skeleton** — README, PROJECT.md, .gitignore, run script.
Nothing runs yet; we just set up the workspace.

**Stage 2 · Theme + constants** — `ui/Theme`, `app/AppConstants`.
One place for colors, fonts, app name, email rules. Everything reads from here.

**Stage 3 · Dependencies** — sqlite-jdbc, javax.mail, gson jars.
The outside libraries we lean on (database driver, email, json).

**Stage 4 · Database layer** — `data/Db`.
Opens the SQLite file and creates the tables. Storage before anything to store.

**Stage 5 · Model classes** — `model/User, Community, Post, Comment, Message,
MbtiResult, Interests...`.
The plain data objects. Every other layer uses these, so they come early.

**Stage 6 · Utilities** — `util/PasswordUtil`, `util/Validation`.
Password hashing (never store plain passwords) and input checks.

---

## PHASE 2 — Accounts (you can't do anything until we know who you are)

**Stage 7 · User data access + auth rules** — `data/UserDao`, `service/AuthService`.
Read/write users; the rules for register and login (Bilkent email required).

**Stage 8 · Email verification** — `util/EmailSender`.
Sends the 6 digit code over SMTP, with a popup fallback so it runs without setup.

**Stage 9 · Shared UI pieces** — `ui/RoundedPanel, RoundedButton, UiHelper`.
The rounded cards and buttons every screen reuses.

**Stage 10 · App window + login** — `screens/AppFrame, LoginScreen`, `ui/Session`.
One window that swaps screens; who is logged in; the first real screen.

**Stage 11 · Register + verify screens** — `screens/RegisterScreen, VerifyScreen`.
Sign up, get the code, confirm it. Now people can make accounts.

---

## PHASE 3 — Onboarding (build the "vibe" we match on)

**Stage 12 · Interest picker** — `screens/InterestPanel`, `ui/Chip`.
Pick at least 3 interests. This is the input to matching.

**Stage 13 · The vibe test** — `service/MbtiService`, `screens/MbtiTestPanel`.
16 questions, one per screen, scored into a 4 letter type.

**Stage 14 · Vibe result** — `screens/VibeResultPanel`.
The payoff screen: archetype + trait bars.

---

## PHASE 4 — Communities (the main content)

**Stage 15 · Community data + seed** — `data/CommunityDao, SampleData`,
`service/CommunityService`.
Store communities, join/leave, and put in sample data so it isn't empty.

**Stage 16 · Matching** — `service/MatchService`.
Score each community for a user from shared interests + personality.

**Stage 17 · Home feed** — `screens/MainWindow, HomePanel, CommunityCard`.
The sidebar app and the matched "top picks" feed.

**Stage 18 · Discover** — `screens/DiscoverPanel`.
Browse by category, search, see match percent.

---

## PHASE 5 — Forum + Messages (people interacting)

**Stage 19 · Post data** — `data/PostDao` (+ seeded discussions).
Store forum posts and threaded comments.

**Stage 20 · Community page + forum** — `screens/CommunityDetailPanel,
NewPostDialog`.
The community page with join/leave, description and its discussions.

**Stage 21 · Post + threaded comments** — `screens/PostDetailPanel`.
Open a post, read replies, reply to a reply (the reddit style).

**Stage 22 · My communities** — `screens/MyCommunitiesPanel`.
The list of communities you joined.

**Stage 23 · Direct messages** — `data/MessageDao`, `screens/MessagesPanel`.
1 on 1 chat.

---

## PHASE 6 — Profile + Settings (close the loop)

**Stage 24 · Profile** — `screens/ProfilePanel, EditInterestsDialog`.
Your archetype, editable interests, retake the test, stats.

**Stage 25 · Settings** — `screens/SettingsPanel`.
Account info, log out.

*(At this point it's a complete single computer app.)*

---

## PHASE 7 — Make it a real client/server app (so many people share it)

**Stage 26 · Protocol** — `protocol/Json, Request, Response, Params`.
The json message format both sides agree on.

**Stage 27 · Socket server skeleton** — `server/ChatServer, ClientHandler,
RequestRouter, ServerMain`.
A `ServerSocket`, one thread per client, a "ping" to prove it works.

**Stage 28 · Client connection** — `net/ServerClient, PushListener`.
The desktop connects, sends requests, and a background thread reads pushes.

**Stage 29 · Server handlers** — `server/AuthHandler, ProfileHandler,
CommunityHandler, ForumHandler, MessageHandler, Dto`.
The server actions that wrap the services we already had.

**Stage 30 · Client API** — `net/Api`.
The friendly face the screens call instead of touching the database.

**Stage 31 · Rewire the screens** — every screen now talks to the server, not the
local database. Real time push wired in.

**Stage 32 · Report** — `RAPOR.md`.
Write up how the sockets work.

---

## PHASE 8 — Speed, friends, notifications (a good app, not just a working one)

**Stage 33 · Cut round trips** — membership comes back with the community list
instead of asking one by one.

**Stage 34 · Background loading** — `ui/BackgroundTask`. Screens fetch data off the
ui thread so the window never freezes (matters a lot once the server is remote).

**Stage 35 · Friends system** — `data/FriendDao`, `server/FriendHandler`.
Requests, accept/decline, and: you can only message your friends (privacy).

**Stage 36 · Friends messages screen** — rebuild `MessagesPanel` around friends +
incoming requests + add friend.

**Stage 37 · Notifications (data)** — `data/NotificationDao`, `server/Notifier`,
`model/Notification`. Store + push a notification on friend/message/reply events.

**Stage 38 · Notifications (screen)** — `screens/NotificationsPanel` + a bell with a
live unread count in the sidebar.

---

## PHASE 9 — iOS (next)

**Stage 39+ · SwiftUI client** — a second client, in Swift, connecting to the
**same** server over the same socket protocol. The server does not change at all.

---

### The one sentence to defend it
"We built bottom up — model, then storage, then rules, then screens, then turned
it into a client/server app with sockets, then added the nice-to-haves. Each
brick is one commit, each layer only depends on the one beneath it."
