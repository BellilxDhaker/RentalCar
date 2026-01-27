# RentalCar Platform Backend

## Overview

The RentalCar Platform is a backend system designed to manage a car rental service efficiently. It provides a secure REST API for mobile and web clients to interact with the system. This backend handles user management, vehicle management, reservations, payments, and administrative tasks in a robust and scalable way.


## Features

- **User Management:** Register, authenticate, and manage users.
- **Vehicle Management:** Add, update, and query available vehicles.
- **Reservation System:** Create, update, and manage car reservations.
- **Payment Integration:** Secure payment processing with support for multiple gateways.
- **Authentication & Authorization:** Secure access using JWT-based authentication.
- **Admin Management:** Admins can monitor and manage users, reservations, and vehicles.
- **Custom Exception Handling:** Handles errors like duplicate emails, phone numbers, and missing reservations gracefully.
- **Specifications & Filtering:** Advanced querying for vehicles using specifications.


## Technologies Used

- **Java**: Backend programming language.
- **Spring Boot**: Framework for creating REST APIs.
- **Spring Security & JWT**: Authentication and authorization.
- **JPA / Hibernate**: ORM for database management.
- **Maven**: Build and dependency management.
- **MySQL/PostgreSQL** (or any relational database): Persistent storage.
- **REST API**: Communication with front-end and mobile applications.


## Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/BellilxDhaker/RentalCar/
cd RentalCar
```

2. **Build the project using Maven**
```bash
./mvnw clean install
```

3. **Configure database**
- Open `src/main/resources/application.properties`.
- Set your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rentalcar
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

4. **Run the application**
```bash
./mvnw spring-boot:run
```

5. **Access the API**
- The API will run on `http://localhost:8080`.
- Use tools like Postman to test endpoints:
  - `POST /auth/login` for user login
  - `POST /users/register` for new user registration
  - `GET /vehicles` to list available vehicles
  - `POST /reservations` to create a reservation
  - `POST /payments` to process payments



## API Endpoints (Summary)

### Authentication

| Resource | Role | Method | Endpoint | Description |
|--------|------|--------|----------|-------------|
| Authentication | Admin | POST | `/admin/register` | Register a new admin |
| Authentication | Admin | POST | `/admin/login` | Admin login |
| Authentication | User | POST | `/user/register` | Register a new user |
| Authentication | User | POST | `/user/login` | User login |


### Payment

| Resource | Role | Method | Endpoint | Description |
|--------|------|--------|----------|-------------|
| Payment | User | POST | `/user/pay` | Make a payment |
| Payment | Admin | GET | `/admin/payments` | Get all payments |


### Reservation

| Resource | Role | Method | Endpoint | Description |
|--------|------|--------|----------|-------------|
| Reservation | Admin | GET | `/admin/reservations` | Get all reservations |
| Reservation | Admin | GET | `/admin/reservations/{id}` | Get reservation by ID |
| Reservation | Admin | DELETE | `/admin/reservations/{id}` | Delete a reservation |
| Reservation | User | POST | `/user/reservations` | Create a reservation |
| Reservation | User | PUT | `/user/reservations/{id}` | Update a reservation |
| Reservation | User | POST | `/user/reservation-authentication` | Authenticate reservation |


### Users

| Resource | Role | Method | Endpoint | Description |
|--------|------|--------|----------|-------------|
| Users | Admin | GET | `/admin/users/add` | Add a new user |
| Users | Admin | GET | `/admin/users` | Get all users |
| Users | Admin | DELETE | `/admin/users/{id}` | Delete a user |
| Users | User | GET | `/user/update` | Update user profile |
| Users | User | GET | `/user/{id}` | Get user by ID |


### Vehicle

| Resource | Role | Method | Endpoint | Description |
|--------|------|--------|----------|-------------|
| Vehicle | Admin | POST | `/admin/vehicle` | Add a new vehicle |
| Vehicle | Admin | PUT | `/admin/vehicle/{id}` | Update vehicle |
| Vehicle | Admin | DELETE | `/admin/vehicle/{id}` | Delete vehicle |
| Vehicle | User | GET | `/auth/vehicle` | Get all vehicles |
| Vehicle | User | GET | `/auth/vehicle/{id}` | Get vehicle by ID |
| Vehicle | User | GET | `/auth/vehicle/filter` | Filter vehicles |


## Exception Handling

- `EmailAlreadyExistsException`: Thrown when a user tries to register with an existing email.
- `PhoneNumberAlreadyExistsException`: Thrown when a phone number already exists.
- `ReservationNotFoundException`: Thrown when a reservation ID is invalid or missing.


## Security

- **JWT Authentication**: Ensures secure access to endpoints.
- **Role-Based Access**: Admin and User roles with different privileges.
- **Password Encryption**: User passwords are securely hashed.


## Conclusion

The platform’s backend technology acts as its structural core, providing a secure REST API for mobile applications. It manages users, cars, payments, and reservations to ensure smooth operations. By integrating payment gateways and third-party services, it facilitates safe and easy financial transactions. This strong backend infrastructure guarantees reliable and secure services throughout all user interactions, while supporting the platform’s scalability and operational efficiency.


