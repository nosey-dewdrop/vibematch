# vibematch

A community app for Bilkent students. You sign up with your Bilkent email, pick the
things you are into, take a short personality test, and the app matches you with
communities (clubs, societies, study circles) that fit your vibe. Each community has
its own little forum where people post and reply, and you can also DM people 1 on 1.

This is a desktop app written in **classic Java Swing**. Phase 2 will add an iOS app.

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
You need Java 21 installed. Then from the project root:

```bash
./run.sh
```

That compiles everything into `build/` and launches the app. A `vibematch.db`
file gets created on first run with some sample communities and users.

> Email sending is optional. If you don't set up SMTP credentials the verification
> code is just shown in a popup so the app still works for testing. To send real
> emails, copy `desktop/credentials.properties.example` to
> `desktop/credentials.properties` and fill it in (this file is gitignored).

## Project structure
See `PROJECT.md` for the roadmap and where things stand.
