# Pastry API

This project is a simple REST API for managing pastry shop ingredients, developed for the "Analisis y desarrollo de Software" course.

## Description

The Pastry API provides endpoints to perform CRUD (Create, Read, Update, Delete) operations on ingredients. It uses a SQLite database to store the data.

## Technologies

- Java 25
- Spring Boot 4.1.1
- Maven
- SQLite
- Lombok 1.18.48
- SpringDoc OpenAPI 3.1.1 (Swagger)
- Docker

## How to run the project

1.  Clone the repository:
    ```bash
    git clone https://github.com/tu-usuario/pastry-api.git
    ```
2.  Navigate to the project directory:
    ```bash
    cd pastry-api
    ```
3.  Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
    On Windows, use:
    ```bash
    .\mvnw.cmd spring-boot:run
    ```

The application will start on port 8080.

## Run with Docker

You can run the API without installing Java or Maven locally.

Build and start with Docker Compose (recommended):

```bash
docker compose up --build
```

Or build and run the image directly:

```bash
docker build -t pastry-api .
docker run --rm -p 8080:8080 -v pastry-data:/data pastry-api
```

SQLite is stored in the `/data` volume so ingredients survive container restarts.

## Swagger UI

Once the application is running, you can access the Swagger UI to view and test the API endpoints.

Open your browser and navigate to:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Features to implement

The current API is a prototype: ingredient CRUD, Swagger, and Docker. It is **not** an MVP yet because a pastry shop still cannot check stock against a recipe and bake a batch.

Full scope, rationale, and suggested order: [`docs/files/mvp.md`](docs/files/mvp.md).

Must-have for the MVP:

- [ ] Ingredient stock fields (`unit`, `quantity`, `minimumQuantity`) and input validation
- [ ] Recipe CRUD with ingredient lines
- [ ] Produce a recipe: verify stock, consume quantities, or return 409 if short
- [ ] List filters (name, availability, low stock) and pagination
- [ ] Consistent JSON errors (400 / 404 / 409) and a health endpoint
- [ ] Seed data so Swagger has a demo recipe
- [ ] Automated tests for CRUD, validation, and the produce flow

Nice to have after the MVP:

- [ ] Minimal UI to list stock and trigger Produce
- [ ] Authentication
- [ ] Orders, customers, and suppliers
- [ ] Cost reports

## Architecture screenshots

Save captures in [`docs/screenshots/`](docs/screenshots/).

**Recommended tool: [Emerge](https://github.com/glato/emerge)** (`emerge-viz`). This is a Java Spring Boot project, and Emerge is the option that actually parses Java (files, classes, dependencies, and inheritance) and renders an interactive architecture map in the browser — the most useful screenshots for a README.

Why not the others for this repo:

| Tool | Fit for this project |
| --- | --- |
| CodeLandscapeViewer | Deep analysis is Python / JS / TS. Java is only generic file-level. |
| CodeVisualizer (VS Code) | Java works for function flowcharts, not for the architecture/import graph. |
| code2flow | Call graphs for Python, JavaScript, Ruby, and PHP. No Java. |
| **Emerge** | Native Java file + entity graphs in the browser. Best README captures. |

Generate the graphs from the repository root:

```bash
pip install emerge-viz
emerge --config docs/emerge.yaml
```

Open the HTML app that Emerge writes under `docs/emerge-export/` and take two screenshots into `docs/screenshots/`:

1. File dependency graph (overall architecture)
2. Entity / inheritance graph (Controller → Service → Repository)

Then reference them here:

```markdown
![File dependency graph](docs/screenshots/architecture-dependency-graph.png)
![Entity graph](docs/screenshots/entity-graph.png)
```

## Course Information

This project was developed as part of the "Analisis y desarrollo de Software" course. It serves as a practical example of building a REST API with Spring Boot and documenting it with Swagger.
