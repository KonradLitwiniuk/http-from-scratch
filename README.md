# http-from-scratch

A URL shortener written in plain Java. No Spring, no frameworks.

I built this right after finishing a Spring Boot version of the same app. The Spring version worked perfectly, but I realized I was just adding annotations without understanding how they actually worked. So, I decided to rewrite the whole thing from scratch using plain Java. The main takeaway is the comparison table at the bottom

## What it does

- GET / and GET /about - static pages, there mostly to prove the server
  answers at all
- POST /links - send a URL in the body, get a short code back
- GET /links/{code} - redirects to the original URL (302)
- Anything else -> 404. Database falls over -> 500.

## Running it

bash
docker compose up -d

Create the table once - the SQL lives in schema.sql. Paste it into DBeaver,
psql, whatever you reach for.

Then run Main.java from your IDE and hit http://localhost:8080.

Postgres JDBC driver is pulled in as a plain
project library, and the DB connection details are hardcoded in Main. Not
pretty, but it was supposed to be a quick project.

## How it's put together

HttpServer  ->  LinkService  ->  LinkRepository  ->  Postgres

**HttpServer** Parses the raw request, handles routing, and builds the response.
**LinkService** Handles core business logic (code generation, collisions, lookups).
**LinkRepository** is the one and only class that talks to the database.
**Main** wires the three together and starts the server.

Landing on this split took a couple of goes. My first cut had everything
crammed into Main

## A few decisions worth explaining

**I don't check whether a code exists before inserting it.** If you check first and insert second, you leave a gap for a race condition. I just try to insert it. If Postgres throws a error about already existing code, I treat it as a signal to pick a new code and retry.

**Codes are random, not sequential.** Sequential never collides - but it's also guessable.

**Exceptions are named after the domain, not after HTTP.** LinkNotFoundException, not a 404 thrown from inside LinkService. The service has no idea what a status code is - HttpServer is the only place that translates one into the other.

**URLs are stored as tex, not varchar(255).**
 Relying on Hibernate's default length in the Spring version meant the app would fail on long URLs. This time, I deliberately picked text.
**What's missing (on purpose, for now)**
- Strictly single-threaded (no thread pool).
- Uses a single database connection (no connection pool).
- The /links/{code} route is too permissive and will try to look up /favicon.ico in the database.
These limitations are fine for a local experiment, but they would cause major issues in production
## What Spring was actually doing for me

This comparison is the main takeaway of the project. It shows exactly how the code I wrote manually maps to Spring framework features.

| Here, by hand | In Spring |
|while (true) { serverSocket.accept() }|Tomcat request loop managed by the framework|
|Splitting the request line and checking the path with if|@GetMapping/@PostMapping|
|Checking method and path together|Separate @GetMapping/@PostMapping annotations|
|path.substring("/links/".length())|@PathVariable|
| Reading the body using Content-Length|@RequestBody|
|"302" + Location: header + blank line|ResponseEntity.status(302).location(...)|
|try/catch turning exceptions into status codes | @RestControllerAdvice|
|PreparedStatement + INSERT|repository.save(...)|
|ResultSet -> object, field by field|repository.findByCode(...)|
|new LinkService(new LinkRepository(conn)) in main|@Service and Spring's IoC container|
|Writing CREATE TABLE myself|@Entity + ddl-auto|
| nowing instant needs converting to Timestamp for JDBC|Handled automatically by Hibernate|
|Catching SQLSTATE 23505 myself | DataIntegrityViolationException|