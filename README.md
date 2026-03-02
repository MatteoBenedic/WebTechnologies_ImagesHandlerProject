# Image Gallery Web Application  
TIW – Tecnologie Informatiche per il Web  
A.Y. 2023/2024

This project implements the **Image Gallery Management** application described in the official TIW course specification.

Two separate web applications are provided:

-  **Pure HTML version** (server-side rendering)
-  **RIA version** (Rich Internet Application with JavaScript and AJAX)

Both versions implement the same functional requirements with different architectural approaches.
The documents in the docs directory explain at best the project.

---

## Functional Overview

The application allows:

- User registration and login
- Image upload and storage on the server file system
- Album creation and management
- Association of images to one or more albums
- Image commenting
- Image deletion (only by owner)
- Album browsing with pagination (5 images per row)
- Logout functionality

### RIA-specific features

- Single-page application after login
- Asynchronous communication with the server
- No full page reloads
- Client-side validation
- Modal window for image details
- Client-side pagination
- Custom drag-and-drop image reordering with persistent storage

---

## Architecture

Both applications follow the **MVC (Model-View-Controller)** pattern:

### Model
- Java Beans
- DAO classes
- JDBC for database interaction

### Controller
- Java Servlets

### View
- PureHTML → HTML / Thymeleaf templates
- RIA → HTML + JavaScript (AJAX + DOM manipulation)

---

## Technologies Used

- Java
- Java Servlets
- JDBC
- MySQL
- HTML5 / CSS3
- JavaScript (ES6)
- Apache Tomcat

---

## Database

The database stores:

- Users
- Images (metadata + file path)
- Albums
- Album-image associations
- Comments
- Custom image ordering (RIA version)
