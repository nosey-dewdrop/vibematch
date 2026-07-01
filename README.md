# vibematch

**Documentation & class diagram:** https://nosey-dewdrop.github.io/vibematch/

A community app for Bilkent students. You sign up with your Bilkent email, pick the
things you are into, take a short personality test, and the app matches you with
communities (clubs, societies, study circles) that fit your vibe. Each community has
its own little forum where people post and reply, and you can also DM people 1 on 1.

It is a real client/server app: a **Java socket server** holds the shared data
and pushes live updates, and a **classic Java Swing** desktop client connects to
it. Messages and forum comments arrive in real time. Phase 2 will add an iOS app
that connects to the same server. See `RAPOR.md` for how the sockets work.

## Why
Finding your people in a big campus is hard. Most club stuff is scattered across
Instagram pages and word of mouth. vibematch puts it in one place and actually
recommends communities to you instead of making you dig.

## Features
- Bilkent email verification on sign up (only @bilkent / @ug.bilkent emails)
- Interest selection
- 16 question MBTI style vibe test, gives you a type + an archetype
- Home feed of communities matched to your interests and vibe
- Discover / search communities and people
- Community pages with a reddit style forum (posts + threaded comments)
- 1 on 1 direct messages
- Profile you can edit (retake the test, change interests)

## Tech
- Java 21, Swing for the UI (no external UI libraries)
- SQLite for storage (file based, no server to install)
- JavaMail for sending the verification code

## Running it
You need Java 21 installed. You start the **server** once, then run one or more
**clients**.

Terminal 1 — start the server (holds the shared database):
```bash
./run-server.sh
```

Terminal 2 — start the desktop app:
```bash
./run.sh
```

The `vibematch.db` file is created next to the server on first run, seeded with
sample communities and users. To connect from another computer on the same
network, pass the server's address: `./run.sh 192.168.1.20`.

Open two clients (log in as different demo users) to see messages and forum
comments show up in real time.

> Email sending is optional. If you don't set up SMTP credentials the verification
> code is just shown in a popup so the app still works for testing. To send real
> emails, copy `desktop/credentials.properties.example` to
> `desktop/credentials.properties` and fill it in (this file is gitignored).

## Project structure
See `PROJECT.md` for the roadmap and where things stand.
