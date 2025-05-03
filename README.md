# Event Sourcing Book Project 📚

Welcome to the **Event Sourcing Book Project**! This project demonstrates the implementation of an event-sourced system for managing carts, cosmetics, and inventories. It includes various modules for handling commands, events, projections, and data imports.

---

## 🚀 Prerequisites

Before running the project, ensure you have the following installed:

- **Java 19** or later ☕
- **Maven** (for building the project) 🛠️
- **Docker** (for running containers, if needed) 🐳
- **Kotlin** (for development) 💻

---

## 📂 Project Structure

The project is organized as follows:

- **`src/main/kotlin`**: Contains the main application code, including domain models, commands, events, and controllers.
- **`src/main/resources`**: Configuration files such as `application.yml` and database migration scripts.
- **`src/test/kotlin`**: Contains test cases for the application.
- **`cosmetic_archive`**: Sample CSV files for importing cosmetics data.

---

## 🛠️ Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone <repository-url>
cd eventsourcing-book
```

### 2️⃣ Build the Project
Use Maven to clean and compile the project:
```bash
mvn clean compile
```

### 3️⃣ Run the Application
You can run the application using the provided batch scripts:
- **For normal mode**:
  ```bash
  run-app.bat
  ```
- **For debug mode**:
  ```bash
  run-app-debug.bat
  ```

### 4️⃣ Run Tests
To execute the test cases, use the following command:
```bash
run-cosmetics-test.bat
```

### 5️⃣ Import Cosmetics Data
Use the `/api/import/cosmetics` endpoint to import cosmetics data from a CSV file. Example:
```bash
curl -X POST "http://localhost:8080/api/import/cosmetics" -d "filePath=c:\\Users\\Helix\\Desktop\\SS25\\SE2 Eng\\cosmetic_archive\\2019-Dec.csv"
```

### 6️⃣ Access APIs
The application exposes the following APIs:

#### Inventory Management 📦
- **Create Inventory**: `POST /api/inventory`
- **Reduce Inventory**: `POST /api/inventory/{productId}/reduce`
- **Increase Inventory**: `POST /api/inventory/{productId}/increase`

#### Product Management 🛍️
- **Create Product**: `POST /api/products`
- **Update Product**: `PUT /api/products/{productId}`
- **Archive Product**: `DELETE /api/products/{productId}`

### 7️⃣ Database Migrations
Database migrations are managed using Flyway. Migration scripts are located in `src/main/resources/db/migration`.

### 8️⃣ Docker Support
To run the application with Docker, use the provided `docker-compose.yml` file:
```bash
docker-compose up
```

---

## 🛠️ Troubleshooting

- If you encounter issues during compilation, ensure all dependencies are installed and the correct Java version is being used.
- Check the `target` directory for compiled classes and logs.

---

## 🤝 Contributing

Feel free to contribute to this project by submitting issues or pull requests. Let's build something amazing together! 🌟

---

## 📜 License

This project is licensed under the MIT License.

---

## 📖 Additional Resources

### Understanding Eventsourcing - The Book 📘

This is the sample project for the book **"Understanding Eventsourcing"**.

- [Buy the book on Leanpub](https://leanpub.com/eventmodeling-and-eventsourcing)
- [Eventmodel in Miro](https://miro.com/app/board/uXjVKvTN_NQ=/)
- [The original Eventmodeling Article](https://eventmodeling.org/posts/what-is-event-modeling/)
- [The Little Eventmodeling Book](https://newsletter.nebulit.de/)

### Sample Application 💻

The sample application is written in:
- [Kotlin](https://kotlinlang.org/)
- [Spring](https://spring.io/projects/spring-framework)
- [Axon](https://www.axoniq.io/products/axon-framework)

### Quick Start 🚀

1. Install IntelliJ IDEA.
2. Install the most recent Java SDK.
3. Run `mvn clean install` to install dependencies.
4. Ensure Docker is running.
5. Start the app by running the `ApplicationStarter` class in `src/test/kotlin`.

---

Happy coding! 🎉
