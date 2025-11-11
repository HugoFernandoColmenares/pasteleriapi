# Pastry API

This project is a simple REST API for managing pastry shop ingredients, developed for the "Analisis y desarrollo de Software" course.

## Description

The Pastry API provides endpoints to perform CRUD (Create, Read, Update, Delete) operations on ingredients. It uses a SQLite database to store the data.

## Technologies

- Java 17
- Spring Boot 3.2.3
- Maven
- SQLite
- Lombok
- SpringDoc OpenAPI (Swagger)

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

## Swagger UI

Once the application is running, you can access the Swagger UI to view and test the API endpoints.

Open your browser and navigate to:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Course Information

This project was developed as part of the "Analisis y desarrollo de Software" course. It serves as a practical example of building a REST API with Spring Boot and documenting it with Swagger.