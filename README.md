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

To serve the static frontend, open a second Terminal window and run:
cd frontend
python3 -m http.server 5500

Then open http://localhost:5500 in your browser. Keep the Python server running while using the frontend.

To stop the frontend server, press Ctrl+C in its Terminal window. To stop and remove the backend and PostgreSQL containers, run this from the project root:
docker compose down

The PostgreSQL data is kept in a Docker volume when you run docker compose down.
