## Running the Project with Docker

This project provides a full-stack setup with a Java backend, a Vue.js frontend, and a RabbitMQ message broker, all orchestrated via Docker Compose. Below are the project-specific instructions and requirements for running the application using Docker.

### Project-Specific Docker Requirements

- **Backend**
  - Java 17 (Eclipse Temurin)
  - Built with Maven Wrapper (`mvnw`)
  - Exposes port **8080**
  - Runs as a non-root user (`appuser`)
  - JVM container options set via `JAVA_OPTS` (default: `-XX:MaxRAMPercentage=80.0`)

- **Frontend**
  - Node.js version **22.13.1** (as specified by `NODE_VERSION` build arg)
  - Built with npm and Vite
  - Exposes port **5173**
  - Runs as a non-root user (`appuser`)
  - Uses Vite's preview server for production preview

- **RabbitMQ**
  - Official `rabbitmq:latest` image
  - Exposes ports **5672** (AMQP) and **15672** (management UI)
  - Healthcheck enabled
  - Data persistence is optional (see commented `volumes` section in `docker-compose.yml`)

### Environment Variables

- No required environment variables are set by default in the Dockerfiles or Compose file.
- If you need to provide environment variables, you can create a `.env` file in the `./backend` or `./frontend` directories and uncomment the `env_file` lines in `docker-compose.yml`.

### Build and Run Instructions

1. **Ensure Docker and Docker Compose are installed.**
2. **From the project root, run:**
   ```sh
   docker compose up --build
   ```
   This will build and start all services: backend, frontend, and RabbitMQ.

3. **Access the services:**
   - **Frontend:** http://localhost:5173
   - **Backend API:** http://localhost:8080
   - **RabbitMQ Management UI:** http://localhost:15672 (default user/pass: guest/guest)

### Special Configuration Notes

- The frontend expects the backend to be available at `http://localhost:8080`.
- If you want to persist RabbitMQ data, uncomment the `volumes` section for `rabbitmq` in `docker-compose.yml` and the `volumes` definition at the bottom of the file.
- No database service is included by default; add one if your backend requires it.

### Ports Used

| Service      | Host Port | Container Port | Description           |
|--------------|-----------|---------------|----------------------|
| Backend      | 8080      | 8080          | Java Spring Boot API |
| Frontend     | 5173      | 5173          | Vite Preview Server  |
| RabbitMQ     | 5672      | 5672          | AMQP Protocol        |
| RabbitMQ UI  | 15672     | 15672         | Management Console   |

---

_If you need to customize the build (e.g., add environment variables or volumes), edit the `docker-compose.yml` file accordingly._
