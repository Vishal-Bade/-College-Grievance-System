# Smart College Grievance System

A comprehensive web-based platform designed to streamline the reporting, tracking, and resolution of issues within an educational institution. This system replaces inefficient manual complaint processes with a centralized, transparent, and accountable digital workflow.

## 🚀 Features

* **Role-Based Access Control:** Distinct dashboards and permissions for:
  * **Students:** Submit grievances, track real-time status, and view past submissions.
  * **Staff/Faculty:** Receive assigned grievances, view details, and update statuses.
  * **Administrators:** Oversee the entire system, manage user details, assign grievances to staff, and view system-wide analytics.
* **Grievance Categorization:** Issues are categorized (Academic, Infrastructure, Hostel, Other) and prioritized (Low, Medium, High, Urgent) for efficient routing.
* **Secure Authentication:** Implementation of SHA-256 password hashing for secure user credential storage.
* **Real-Time Tracking:** Clear visibility into the lifecycle of a grievance (PENDING -> IN_PROGRESS -> RESOLVED).
* **Responsive UI:** Built with Bootstrap 5, ensuring the application works seamlessly on desktops, tablets, and mobile devices.

## 🛠️ Technology Stack

* **Backend:** Java 17, Spring Boot 3.2.4, Spring MVC
* **Data Access:** Spring Data JPA, Hibernate ORM
* **Database:** MySQL 8
* **Frontend:** HTML5, CSS3, Bootstrap 5, Thymeleaf (Server-Side Templating)
* **Build Tool:** Maven

## 💻 Local Setup Instructions

Follow these steps to get the project running on your local machine.

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher installed.
* **Maven:** Installed and configured in your system `PATH`.
* **MySQL Server:** Installed and running locally.

### Step 1: Database Setup
1. Open your MySQL command-line client or a GUI tool like MySQL Workbench.
2. Create a new database named `college_grievance`:
   ```sql
   CREATE DATABASE college_grievance;
   ```

### Step 2: Application Configuration
1. Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
2. Navigate to the `src/main/resources/application.properties` file.
3. Update the MySQL data source configuration with your local database connection details (specifically `spring.datasource.username` and `spring.datasource.password` if they differ from your local setup):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/college_grievance?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   
   # Leave Hibernate setting to automatically create/update tables
   spring.jpa.hibernate.ddl-auto=update
   ```

### Step 3: Build and Run
1. Open your terminal or command prompt.
2. Navigate to the root directory of the project (where the `pom.xml` file is located).
3. Build and run the project using Maven:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
   *Alternatively, you can run the `GrievancesystemApplication.java` file directly from your IDE.*

### Step 4: Access the Application
1. Once the application starts successfully (look for `Tomcat initialized with port 8080` in the console), open your web browser.
2. Navigate to: [http://localhost:8080](http://localhost:8080)

## 👤 Test Credentials & Initial Usage

To fully explore the system as an administrator initially:
1. **Register** a new user account through the Web UI (`http://localhost:8080/register`).
2. By default, new users are assigned the `STUDENT` or `STAFF` role based on their selection.
3. **To create an Admin:** Open your MySQL client and manually elevate a registered user's role to `ADMIN`:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'your_registered_email@college.edu';
   ```
4. Log out and log back in to access the Admin Dashboard.

## 📁 Project Structure Highlights

* `src/main/java/com/college/grievancesystem/model/`: JPA Entity classes (`User.java`, `Grievance.java`).
* `.../repository/`: Spring Data JPA interfaces for database operations.
* `.../service/`: Business logic and data manipulation (`UserService.java`, `GrievanceService.java`).
* `.../controller/`: Spring MVC Controllers handling HTTP requests and view routing (`HomeController.java`, `UserController.java`).
* `src/main/resources/templates/`: Thymeleaf HTML templates for the user interface.
