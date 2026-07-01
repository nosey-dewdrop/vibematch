# vibematch — Socket Server Report

_Short report on what we built and how the real time part (sockets) works._

## What vibematch is
A community app for Bilkent students. You sign up with your Bilkent email, verify
it with a code, pick your interests, take a 16 question personality (MBTI style)
test, and the app matches you with communities. Each community has a Reddit style
forum (posts + threaded comments) and you can also send 1 on 1 direct messages.

## The problem we had to solve
At first the whole app was one desktop program with its own database file. That
works for one person on one computer, but a community app needs **many people,
on different computers, seeing the same thing at the same time** — if someone
sends you a message it should show up right away.

For that you need two things:
1. One shared place that holds all the data (so everyone sees the same users,
   communities and messages).
2. A way for the server to tell a user "hey, something new happened" without them
   constantly asking.

Both of these are solved with **sockets**.

## The architecture
```
        ┌─────────────────────────────┐
        │        vibematch SERVER      │
        │  (java.net.ServerSocket)     │
        │  - the ONE shared database   │
        │  - one thread per client     │
        │  - pushes live updates       │
        └───────▲──────────────▲───────┘
                │ socket       │ socket
        ┌───────┴─────┐  ┌─────┴───────┐
        │ Desktop app │  │ Desktop app │   (phone client later,
        │  (Ada)      │  │  (Can)      │    same server)
        └─────────────┘  └─────────────┘
```
- **Server** (`server/` package): opens a `ServerSocket` on port 5050 and waits.
  It owns the only database. Nobody but the server touches the database.
- **Client** (`net/` package + the screens): the desktop app connects to the
  server with a `Socket`. It never touches the database directly — it asks the
  server for everything.

## How the sockets work
We used **plain Java TCP sockets** (`java.net.ServerSocket` / `java.net.Socket`),
no external library for the networking. The messages on the wire are just lines
of JSON text.

### 1. Accepting clients (server side)
`ChatServer` runs an accept loop. Every time a client connects, we hand that
connection to a `ClientHandler` running on **its own thread**, so many people can
be connected at the same time without blocking each other.

```
while (true) {
    Socket socket = serverSocket.accept();      // wait for someone to connect
    ClientHandler handler = new ClientHandler(...);
    new Thread(handler).start();                // give them their own thread
}
```

### 2. Request and reply
The client and server talk in one line JSON messages. The client sends a
**request** with an id, an action, and some data:
```
{"id":4,"action":"login","data":{"usernameOrEmail":"ada","password":"..."}}
```
The server does the work (here: check the password) and sends back a **reply**
with the same id, so the client knows which request it answers:
```
{"type":"reply","id":4,"ok":true,"data":"{...the user...}"}
```
The id matters because a push can arrive in the middle of waiting for a reply,
so we match replies to requests by id.

### 3. The real time part: pushes
This is the important bit. The server keeps a map of **who is online**
(username → their `ClientHandler`). When something happens that another user
should see immediately, the server looks them up and writes straight down their
socket, without them asking:

```
// in MessageHandler, when a message is sent:
messageDao.send(m);                        // save it in the shared database
Response push = Response.push("newMessage", ...);
server.pushTo(receiver, push);             // shove it down the receiver's socket
```

On the client side a background thread (`ServerClient`) is always reading the
socket. When it sees a `push` it tells the open screen, which updates itself. So
a new message or a new forum comment appears **on its own, in real time** — no
refreshing, no re-loading the database over and over (which is what we were told
to avoid).

### Two places we push
- **Direct messages:** when you send a DM, the server pushes `newMessage` to the
  receiver if they are online.
- **Forum:** when someone posts or comments in a community, the server pushes
  `forumUpdate` to every member of that community, so their forum refreshes.

## Why not a paid service like Supabase
Supabase gives you a database + realtime as a paid/limited hosted service. We
built the same idea ourselves: our server **is** our database + realtime, and one
server can host many apps, so it stays free. To put it online for real users we
deploy this server to a cloud machine (e.g. Oracle Cloud's always free tier) so
it stays on 24/7.

## Security notes
- Passwords are never stored as plain text — we keep a salted SHA-256 hash.
- The server never sends the password hash or salt to a client (`Dto.safeUser`).
- Email credentials live in a gitignored file, never in the repo.

## What's next
- Put the server on a free cloud machine so it runs 24/7.
- Build the iOS app as a second client that connects to the **same** server over
  sockets, so phone and desktop users share everything.
