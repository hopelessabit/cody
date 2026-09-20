# 🥥 Cody E-Commerce Application Backend

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![SQL Server](https://img.shields.io/badge/Microsoft_SQL_Server-2022-CC292B?style=for-the-badge&logo=microsoftsqlserver&logoColor=white)](https://www.microsoft.com/sql-server)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![OpenAPI/Swagger](https://img.shields.io/badge/OpenAPI-Swagger_3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://swagger.io/)
[![VnCoreNLP](https://img.shields.io/badge/NLP-VnCoreNLP_1.2-blue?style=for-the-badge)](https://github.com/vncorenlp/VnCoreNLP)

A robust, enterprise-grade backend service powering the **Cody E-Commerce** platform (specializing in Vietnamese coconut confectionery and specialty products). Built with **Java 17** and **Spring Boot 3.4.0**, this system provides complete e-commerce lifecycle management, comprehensive employee and KPI administration, an advanced Vietnamese NLP customer-service chatbot, and cloud asset integrations.

---

## 📑 Table of Contents

- [Overview & Architecture](#-overview--architecture)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Environment Configuration](#-environment-configuration)
- [Database Setup](#-database-setup)
- [Getting Started](#-getting-started)
  - [Run Locally (Maven)](#run-locally-maven)
  - [Run with Docker Compose](#run-with-docker-compose)
- [API Documentation & Swagger](#-api-documentation--swagger)
- [Order Lifecycle & State Machine](#-order-lifecycle--state-machine)
- [Vietnamese NLP Chatbot](#-vietnamese-nlp-chatbot)
- [CI/CD & Deployment](#-cicd--deployment)
- [Contributing & License](#-contributing--license)

---

## 🌟 Overview & Architecture

Cody App is architected following clean, layered architectural principles:

```
                      ┌──────────────────────────────────────┐
                      │    Client (Web / Admin / Mobile)     │
                      └──────────────────┬───────────────────┘
                                         │  HTTPS / REST / JSON
                                         ▼
                      ┌──────────────────────────────────────┐
                      │        Spring Security & JWT         │
                      │  (Stateless Authentication & RBAC)   │
                      └──────────────────┬───────────────────┘
                                         │
                 ┌───────────────────────┴───────────────────────┐
                 ▼                                               ▼
    ┌─────────────────────────┐                     ┌─────────────────────────┐
    │  E-Commerce Controllers  │                     │   Staff & Operations    │
    │  - Products & Combos    │                     │  - Tasks & Grading      │
    │  - Categories & Ingreds │                     │  - KPIs & Evaluation    │
    │  - Orders & Cart        │                     │  - Metric TrackBy       │
    │  - Posts & Events       │                     │  - Account Roles        │
    └────────────┬────────────┘                     └────────────┬────────────┘
                 │                                               │
                 └───────────────────────┬───────────────────────┘
                                         │
                                         ▼
                      ┌──────────────────────────────────────┐
                      │       Service & Domain Layer         │
                      │ ┌──────────────────────────────────┐ │
                      │ │   VnCoreNLP AI Query Engine      │ │
                      │ └──────────────────────────────────┘ │
                      └──────────────────┬───────────────────┘
                                         │
            ┌────────────────────────────┼────────────────────────────┐
            ▼                            ▼                            ▼
┌──────────────────────┐     ┌──────────────────────┐     ┌──────────────────────┐
│ Microsoft SQL Server │     │  Cloudinary /        │     │  Docker / Container  │
│ (Relational Data &   │     │  Firebase Storage    │     │  Orchestration       │
│  Full Order History) │     │  (Media & Documents) │     │  (Multi-stage build) │
└──────────────────────┘     └──────────────────────┘     └──────────────────────┘
```

---

## 🚀 Key Features

### 1. 🛒 Product & Catalog Management
- **Hierarchical Categories & Tags**: Full categorization with SEO-friendly slugs and metadata.
- **Product Combos & Ingredients**: Support for multi-item product bundles, combos, and detailed ingredient traceability.
- **Inventory Control**: Real-time stock quantity management, batch increments, and automatic inventory checks.
- **Image Assets**: Integration with Cloudinary and Firebase Storage with primary image flags.

### 2. 📦 Advanced Order Lifecycle
- Multi-dimensional status tracking via `OrderMainStatusEnum`, `OrderStatusDeliveryEnum`, and `OrderPaymentStatusEnum`.
- Complete order history, cancellation flows with reasons, delivery tracking, and return/refund processing.

### 3. 🤖 Vietnamese Natural Language Chatbot
- Integrated **VnCoreNLP 1.2** with pre-trained models (`WordSegmenter`, `PosTagger`, `NER`, `DependencyParser`).
- Semantic query parser for natural Vietnamese queries:
  - Product specs, pricing, and availability queries.
  - Live order tracking and delivery status lookups.
  - Store and FAQ answering.

### 4. 👥 Role-Based Access Control (RBAC) & Security
- Stateless JWT authentication with short-lived access tokens and refresh tokens.
- Granular permissions for 6 distinct roles:
  - `USER`: Shoppers, cart management, placing orders.
  - `ADMIN`: System-wide access, user and catalog governance.
  - `MANAGER`: Product, employee, and task management.
  - `EMPLOYEE`: Task completion, internal operations.
  - `DELIVERY`: Shipping and order dispatch fulfillment.
  - `BANK`: Financial confirmation and payment hooks.

### 5. 📊 Employee & Task Management (KPIs)
- **Task Management**: Create tasks, assign them to employees, track deadlines, and submit grading scores.
- **KPI Engine**: Define KPIs with weights, target mappings (`TrackBy`), and automatically evaluate performance progress.

### 6. 📰 Content & Marketing
- **Posts**: Articles, news, and SEO-optimized blog posts with custom slugs and meta descriptions.
- **Events**: Promotional campaigns, marketing events, and date/location listings.
- **App Configuration**: Dynamically manage site banners, announcement banners, and app metadata without redeploying.

---

## 🛠 Technology Stack

| Domain | Technology / Library | Purpose |
|---|---|---|
| **Language** | Java 17 (Temurin / OpenJDK) | Modern, LTS Java platform |
| **Framework** | Spring Boot 3.4.0 | Core microservice framework |
| **Data Access** | Spring Data JPA / Hibernate 6 | Object-Relational Mapping & Transactions |
| **Database** | Microsoft SQL Server (mssql-jdbc) | Enterprise relational persistence |
| **Security** | Spring Security 6, JJWT (0.12.5) | Stateless JWT authentication & authorization |
| **NLP Engine** | VnCoreNLP 1.2 | Vietnamese language processing pipeline |
| **Documentation** | SpringDoc OpenAPI UI 2.5.0 | Interactive Swagger UI (`/swagger-ui.html`) |
| **Cloud Media** | Cloudinary & Firebase Admin SDK | Cloud image hosting and secure file storage |
| **Build Tool** | Apache Maven & Maven Wrapper | Dependency management & reproducible builds |
| **Containerization**| Docker & Docker Compose | Multi-stage, layered container deployment |
| **CI/CD** | GitHub Actions | Automated build, Docker Hub push, and SSH deploy |

---

## 📁 Project Structure

```plaintext
cody-main/
├── .github/
│   └── workflows/
│       └── docker-image.yml         # CI/CD: build, push to Docker Hub, deploy over SSH
├── src/
│   ├── main/
│   │   ├── java/cody/ecommerce/cody_app/
│   │   │   ├── CodyAppApplication.java   # Spring Boot entry point
│   │   │   ├── config/                   # Security, Swagger, Firebase, CORS config
│   │   │   ├── constant/                 # Enums (Roles, OrderStatus, KpiStatus, etc.)
│   │   │   ├── controller/               # REST API endpoints (Auth, Order, Product, etc.)
│   │   │   ├── dto/                      # Data Transfer Objects (Requests & Responses)
│   │   │   ├── entity/                   # JPA Entity definitions
│   │   │   ├── exception/                # Global exception handling & error responses
│   │   │   ├── message/                  # Localized messages & response constants
│   │   │   ├── repository/               # Spring Data JPA repositories
│   │   │   ├── search/                   # Criteria & specification queries
│   │   │   ├── service/                  # Business logic services & implementations
│   │   │   └── util/                     # Security, JWT, and helper utilities
│   │   └── resources/
│   │       ├── application.properties    # Central configuration file
│   │       ├── models/                   # VnCoreNLP pre-trained language models
│   │       └── VnCoreNLP-1.2.jar         # Embedded NLP engine library
│   └── test/                             # Unit and integration tests
├── Cody.drawio                           # Complete system architecture diagram
├── order_state_diagram.drawio            # Order state transition state machine
├── database.sql                          # Database DDL schema for Microsoft SQL Server
├── Dockerfile                            # Multi-stage optimized Docker build
├── docker-compose.yml                    # Docker Compose service definition
├── key.env.example                       # Template for environment variables
├── pom.xml                               # Maven project definition
└── mvnw / mvnw.cmd                       # Maven wrappers for Linux/macOS and Windows
```

---

## 📋 Prerequisites

Before running the application locally, ensure you have the following installed:

- **JDK 17 or higher** ([Eclipse Temurin Recommended](https://adoptium.net/))
- **Apache Maven 3.9+** (or use the provided `./mvnw` wrapper)
- **Microsoft SQL Server 2019+** (Local instance, Azure SQL, or Docker container)
- **Git**
- **Docker & Docker Compose** *(optional, for containerized execution)*

---

## ⚙️ Environment Configuration

The application loads configuration directly from environment variables or from a `key.env` file in the project root.

1. Create your `key.env` file by copying the provided example:
   ```bash
   cp key.env.example key.env
   ```
2. Update the values in `key.env` with your configuration:

| Variable Name | Description | Example / Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | JDBC Connection URL to MS SQL Server | `jdbc:sqlserver://localhost:1433;databaseName=cody_db;encrypt=true;trustServerCertificate=true;` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Database user password | `YourStrongPassword!` |
| `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT` | Hibernate dialect | `org.hibernate.dialect.SQLServerDialect` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate schema action (`update`, `validate`, `none`) | `update` |
| `SPRING_JPA_SHOW_SQL` | Log SQL queries to console | `true` |
| `SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL` | Pretty-print SQL logs | `true` |
| `SECRET_KEY` | 256-bit secret key for signing JWTs | `base64_or_random_secure_key` |
| `EXPIRATION` | Access token lifespan in milliseconds | `86400000` (24 hours) |
| `REFRESH_TOKEN_EXPIRATION` | Refresh token lifespan in milliseconds | `604800000` (7 days) |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud account identifier | `your_cloud_name` |
| `CLOUDINARY_API_KEY` | Cloudinary API Key | `your_api_key` |
| `CLOUDINARY_API_SECRET` | Cloudinary API Secret | `your_api_secret` |
| `FIREBASE_STORAGE_BUCKET_NAME`| Firebase Cloud Storage bucket name | `project-id.appspot.com` |
| `FIREBASE_CREDENTIALS_BASE64` | Base64-encoded Firebase Service Account JSON | `eyJ0eXBl...` |
| `SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE` | Database session timezone | `Asia/Bangkok` |
| `SPRING_JACKSON_DATE_FORMAT` | Serialized JSON date/time format | `yyyy-MM-dd'T'HH:mm:ss.SSSXXX` |
| `SPRING_JACKSON_TIME_ZONE` | JSON serialization timezone | `Asia/Bangkok` |

---

## 🗄 Database Setup

Initialize the Microsoft SQL Server database:

1. Create an empty database in MS SQL Server (e.g. `cody_db`).
2. Run the SQL initialization script found in [database.sql](database.sql):
   ```sql
   -- In SSMS, Azure Data Studio, or sqlcmd:
   USE master;
   GO
   CREATE DATABASE cody_db;
   GO
   USE cody_db;
   GO
   -- Execute the contents of database.sql
   ```
3. The script creates core tables including `users`, `products`, `orders`, `order_items`, `order_status`, `categories`, `ingredients`, `kpis`, `employee_kpis`, `tasks`, `posts`, and `events`.

---

## 🏃 Getting Started

### Run Locally (Maven)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd cody-main
   ```

2. **Verify environment setup:**
   Ensure `key.env` is populated in the root folder.

3. **Build and package:**
   - **Linux / macOS:**
     ```bash
     ./mvnw clean package -DskipTests
     ```
   - **Windows (Command Prompt / PowerShell):**
     ```cmd
     mvnw.cmd clean package -DskipTests
     ```

4. **Start the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or run the generated JAR:
   ```bash
   java -jar target/cody-app-0.0.1-SNAPSHOT.jar
   ```

The application will start on port **`8080`** by default.

---

### Run with Docker Compose

The project includes an optimized multi-stage `Dockerfile` leveraging Spring Boot's built-in **`JarLauncher`** layertools for cached image builds:

1. **Build and start the container:**
   ```bash
   docker compose up --build -d
   ```

2. **Check container status and logs:**
   ```bash
   docker compose ps
   docker compose logs -f cody-app
   ```

3. **Stop the service:**
   ```bash
   docker compose down
   ```

---

## 📖 API Documentation & Swagger

Once the server is running, explore and test the endpoints interactively using **Swagger UI**:

- **Local Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI 3 JSON Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Authentication via Swagger:
1. Call `POST /api/v1/auth/login` to obtain an access token.
2. Click the **Authorize 🔓** button at the top right of Swagger UI.
3. Enter `Bearer <your_token>` and click **Authorize**.

---

## 🌐 Core API Endpoints Reference

### 🔐 Authentication (`/api/v1/auth`)
| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Public | Authenticate user & issue JWT |
| `POST` | `/api/v1/auth/register` | Public | Register customer account |
| `POST` | `/api/v1/auth/refresh-token` | Public | Exchange refresh token for new access token |
| `POST` | `/api/v1/auth/admin/login` | Public | Authenticate administrative user |
| `POST` | `/api/v1/auth/employee/login` | Public | Authenticate internal staff member |
| `POST` | `/api/v1/auth/admin/register/employee` | `ADMIN`, `MANAGER` | Register an employee account |
| `POST` | `/api/v1/auth/admin/register/manager` | `ADMIN` | Register a manager account |

### 🛍 Products (`/api/v1/products`)
| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/products/search` | Public | Search products (paginated, filtered, sorted) |
| `GET` | `/api/v1/products/id/{id}` | Public | Get product details by ID |
| `GET` | `/api/v1/products/{slug}` | Public | Get product details by slug |
| `POST` | `/api/v1/products/admin/create` | `ADMIN`, `MODERATOR` | Create a new product |
| `POST` | `/api/v1/products/create-combo` | `ADMIN`, `MODERATOR` | Create a combo / bundle product |
| `PUT` | `/api/v1/products/admin/update/{id}` | `ADMIN`, `MODERATOR` | Update product details |
| `DELETE` | `/api/v1/products/admin/delete/{id}` | `ADMIN`, `MODERATOR` | Remove a product |
| `PUT` | `/api/v1/products/admin/add-quantity/{productId}` | `ADMIN`, `MODERATOR` | Adjust product inventory stock |

### 📦 Orders (`/api/v1/orders` & `/api/v1/admin/orders`)
| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/orders/create` | Authenticated | Place a new order |
| `GET` | `/api/v1/orders/` | Authenticated | List current user's orders |
| `GET` | `/api/v1/orders/{orderId}` | Authenticated | Get order details by ID |
| `PUT` | `/api/v1/orders/{orderId}/address` | Authenticated | Update delivery address |
| `POST` | `/api/v1/orders/{orderId}/cancel` | Authenticated | Cancel order (prior to shipping) |
| `POST` | `/api/v1/orders/{orderId}/confirm` | Authenticated | Customer confirms goods received |
| `POST` | `/api/v1/orders/{orderId}/return` | Authenticated | Request return / refund |
| `GET` | `/api/v1/admin/orders/get-all` | `ADMIN`, `MODERATOR` | Search & filter all orders across system |
| `POST` | `/api/v1/admin/orders/{orderId}/status`| `ADMIN`, `MODERATOR` | Update order delivery/payment state |

### 🤖 Chatbot (`/api/v1/chatbot`)
| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/chatbot/v2/` | Public | Vietnamese natural language query response |
| `GET` | `/api/v1/chatbot` | Public | Direct entity inquiry (product, order, policy) |

### 📋 Employee Operations & Tasks (`/api/v1/admin/tasks`, `/api/v1/admin/kpis`)
| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/admin/tasks/create` | `ADMIN`, `MANAGER` | Create and assign a task |
| `GET` | `/api/v1/tasks/search` | Authenticated | Search tasks by employee and timeframe |
| `POST` | `/api/v1/admin/tasks/grading/task/{taskId}` | `ADMIN`, `MANAGER` | Grade and score task performance |
| `POST` | `/api/v1/admin/kpis/create` | `ADMIN` | Define a new employee KPI |
| `GET` | `/api/v1/admin/kpis/all` | `ADMIN` | List all configured KPIs |
| `PUT` | `/api/v1/admin/kpis/update-status/{id}` | `ADMIN` | Activate or deactivate KPI |

---

## 🔄 Order Lifecycle & State Machine

Order processing in Cody follows a synchronized state machine reflecting both **Delivery** and **Payment** transitions:

```
[Customer Places Order]
         │
         ▼
     (PND: Pending) ──────────────► (CNL: Canceled / DC: Declined)
         │
         ▼
    (CF: Confirmed)
         │
         ▼
   (DLN: Delivering) ─────────────► (D_FL: Deliver Failed) ──► (D_RG / D_RT: Returning)
         │
         ▼
   (DLD: Delivered)
         │
         ├──► (U_CF: User Confirmed Completed)
         │
         └──► (R_PD: Return Requested) ──► (R_CF: Return Confirmed) ──► (R_DLD: Returned)
```

### Order Enums Overview:
- **Delivery Status (`OrderStatusDeliveryEnum`)**: `PND` (Pending), `CF` (Confirmed), `DLN` (Delivering), `DLD` (Delivered), `U_CF` (User Confirmed), `CNL` (Canceled), `DC` (Declined), `D_FL` (Deliver Failed), `D_RG` (Deliver Returning), `D_RT` (Deliver Returned), `R_PD` (Return Pending), `R_CF` (Return Confirmed), `R_DLN` (Return Delivering), `R_DLD` (Return Delivered).
- **Payment Status (`OrderPaymentStatusEnum`)**: `UP` (Unpaid), `PD` (Paid), `C_UP` (COD Unpaid), `RFG` (Refunding), `RFD` (Refunded).
- **Overall Main Status (`OrderMainStatusEnum`)**: `PS` (Pending), `CF` (Confirmed), `DC` (Declined), `DL` (Delivering), `CP` (Completed), `RFG` (Refunding), `RFD` (Refunded), `CN` (Canceled).

---

## 🧠 Vietnamese NLP Chatbot

The application includes an embedded AI processing engine utilizing **VnCoreNLP**:
1. **Pipeline Execution**: Word Segmentation (`wordsegmenter`), Part-of-Speech Tagging (`postagger`), Named Entity Recognition (`ner`), and Dependency Parsing (`dep`).
2. **Intent & Slot Filling**: Maps customer Vietnamese inquiries (e.g., *"Kẹo dừa sầu riêng giá bao nhiêu?", "Kiểm tra đơn hàng mã COD1234"*) into database queries against products, inventory, and order dispatch tracking.
3. **Model Assets**: Bundled in `src/main/resources/models/` and loaded automatically at runtime.

---

## 🚢 CI/CD & Deployment

Automated builds and deployments are managed via **GitHub Actions** (`.github/workflows/docker-image.yml`):

1. **Trigger**: Any push to the `main` branch.
2. **Build Stage**:
   - Checks out the repository.
   - Configures Docker Buildx.
   - Builds the layered Docker image with build caching (`highschoolvn/cody:buildcache`).
   - Pushes tags (`latest`, commit SHA, run ID) to Docker Hub.
3. **Deployment Stage**:
   - Connects to the target server via SSH (`appleboy/ssh-action`).
   - Pulls the latest image from Docker Hub.
   - Restarts the container with the active environment configuration (`key.env`).

---

## 🤝 Contributing & Guidelines

1. Fork the repository and create a feature branch (`git checkout -b feature/amazing-feature`).
2. Commit your modifications with clear commit messages (`git commit -m 'feat: add support for discount vouchers'`).
3. Push to your branch (`git push origin feature/amazing-feature`).
4. Open a Pull Request targeting the `main` branch.

---

## 📄 License

This project is proprietary software belonging to the Cody E-Commerce development team. All rights reserved.
