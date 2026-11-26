# ASPM-Project
An Application Security Posture Management platform created by Team Void
Members:
Flynn Curtis,
Michael Wingo,
Chris Chambers,
Richard Chao,
Shirzad Osmani,
Abdullah Mumtaz

## Running the application locally
Pre-requisites: install Docker Desktop for your local machine.

From the project root, start PostgreSQL and the backend:  
docker compose up --build -d

Check that the services are running and view the backend logs:  
docker compose ps  
docker compose logs backend

The backend should be available at http://localhost:8080. Check its health with:  
curl http://localhost:8080/actuator/health

The local database is seeded with a development administrator account so the login page can be tested:
`admin@example.com` / `password123`. The password is stored as a BCrypt hash. Set
`APP_SEED_DEFAULT_USER=false` and provide `APP_DEFAULT_PASSWORD` through a secure deployment
configuration when running outside local development.

## Application deactivation API

Applications are created with status `ACTIVE`. To deactivate an application while keeping its
historical record, send:

```bash
curl -X PATCH http://localhost:8080/api/applications/<APPLICATION_ID>/deactivate
```

The response retains the application ID and returns status `INACTIVE`. Inactive applications remain
available from `GET /api/applications`, but `POST /api/scans` returns HTTP 409 and does not create a
new scan for them.

## Semgrep scanning

The backend runs Semgrep Community Edition locally and parses its JSON output into normalized findings.
The Docker image installs Semgrep automatically. For a host-based run, install Semgrep with Python:

```bash
python3 -m pip install semgrep
```

Run a scan by posting a readable project directory to `POST /api/semgrep/scans`:

```bash
curl -X POST http://localhost:8080/api/semgrep/scans \
  -H "Content-Type: application/json" \
  -d '{"projectPath":"/workspace/project","config":"auto"}'
```

When using Docker Compose, `/workspace/project` is the read-only mount of the repository root.
When running the backend directly on the host, use an absolute path to the project directory instead.

The response includes the scan status, finding count, check IDs, file locations, line/column ranges,
messages, severity, confidence, and Semgrep-reported errors. Invalid directories return HTTP 400;
Semgrep execution or output failures return HTTP 502. Set `SEMGREP_COMMAND` when the executable is not
available as `semgrep` on the backend process PATH.

To serve the static frontend, open a second Terminal window and run:  
python3 -m http.server 5500

Then open http://localhost:5500 in your browser. Keep the Python server running while using the frontend.

To stop the frontend server, press Ctrl+C in its Terminal window. To stop and remove the backend and PostgreSQL containers, run this from the project root:  
docker compose down

The PostgreSQL data is kept in a Docker volume when you run docker compose down.
