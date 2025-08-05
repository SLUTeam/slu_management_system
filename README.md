# SLU Management System

Hi! 👋  
This project was created by us Abishek , Pooja , Takudzwa , Jyoti for our **Object-Oriented Software Design** course at **Saint Louis University (SLU)**.

We built a **SLU Management System** using **Java Swing GUI** and connected it to a **MySQL database** (hosted on Clever Cloud).  
It includes **login pages** and **dashboards** for Admins, Professors, and Students.

##What Our System Can Do

###Login System:
- Admin login
- Professor login
- Student login

Each user sees a different dashboard after logging in.

###  Admin Dashboard:
Admins can do **CRUD operations** (Create, Read, Update, Delete) for:
- Students
- Professors
- Courses
- Departments

Each section has its own panel (like `StudentPanel`, `ProfessorPanel`, etc.), and we used dropdowns for related fields (like selecting a Department when adding a student).

##  Tech Stack Used

- **Java (Swing)** for GUI
- **MySQL** for database
- **Clever Cloud** (used for remote database)
- **MySQL Connector/J**

##  Our Database Setup

We started with a local MySQL DB (using MySQL Workbench), then moved to **Clever Cloud**.

MYSQL_ADDON_HOST=bgph5k76jy8ha8h56zpo-mysql.services.clever-cloud.com
MYSQL_ADDON_DB=bgph5k76jy8ha8h56zpo
MYSQL_ADDON_USER=u8yvmphbwryhewq6
MYSQL_ADDON_PORT=3306
MYSQL_ADDON_PASSWORD=Rkx2R1USygRznoILpTqI
