SkyUser Application
SkyUser is a Spring Boot application for managing users and external projects.

Features
* User Management: Create, read, update, and delete users.

* External Project Management: Add external projects to users and retrieve projects by user ID.


Technologies Used:
* Java 17
* Spring Boot 3
* Spring Security | Basic Auth
* Liquibase (for database migrations)
* PostgreSQL (as the database)
* H2 (test db)
* Gradle (for dependency management)
* Lombok (for reducing boilerplate code)
* Spring AOP | Logback (for logging)
* Prometheus (for monitoring and metrics)
* Docker (for containerization)


Getting Started
- Clone repository
- docker-compose build
- docker-compose up

Access the Application: Once the application is running, you can access it at http://localhost:8080.

Initial Admin user (with ROLE_ADMIN) was added by liquibase script.

Basic Auth Login details:
- email: admin@example.com
- password: admin

Other Users will be created with ROLE_USER authority.


API Documentation
The API endpoints provided by the SkyUser application are documented below:

- Create User: POST /users - Create a new user.
- Get Users: GET /users - Retrieve a list of all users.
- Get User by ID: GET /users/{id} - Retrieve user details by ID.
- Update User: PUT /users/{id} - Update user details.
- Delete User: DELETE /users/{id} - Delete a user by ID.
- Add External Project to User: POST /users/{userId}/external-projects - Add an external project to a user.
- Get External Projects by User ID: GET /users/{userId}/external-projects - Retrieve external projects associated with a user. 
- 
- Monitoring and Metrics
The SkyUser application is integrated with Prometheus for monitoring and metrics collection. 
You can access the Prometheus dashboard at http://localhost:9090 once it's set up.


