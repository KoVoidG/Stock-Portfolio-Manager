# 🏦 Stock Portfolio Manager  

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/HTML5)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![GitHub Repo](https://img.shields.io/badge/GitHub-KoVoidG%2FStock--Portfolio--Manager-blue?style=for-the-badge&logo=github)](https://github.com/KoVoidG/Stock-Portfolio-Manager)

---

## 📘 Description  
The **Stock Portfolio Manager** is a simple and educational project designed to manage and track stock investments through a Java backend and an HTML-based frontend interface.  
It provides essential **CRUD (Create, Read, Update, Delete)** operations, allowing users to manage stock data, view portfolio performance, and simulate investment growth in a user-friendly way.  

This project demonstrates core concepts in **web development, API design, and Java programming**, making it perfect for learning how backend and frontend interact.

---

## ✨ Features  
✅ Add, update, and delete stock records  
✅ View portfolio performance and total value  
✅ Organized CRUD-based backend structure  
✅ Lightweight and easily extendable for learning or demos  
✅ Clean HTML frontend for interaction  

---

## 🛠️ Technology Stack  
| Layer | Technology |
|--------|-------------|
| **Backend** | Java (Maven, Spark framework) |
| **Frontend** | HTML, CSS |
| **Architecture** | CRUD API model |
| **Build Tool** | Apache Maven |

---

## 📁 Project Structure  

```
Stock-Portfolio-Manager/
│
├── src/
│ └── main/java/org/global/academy/
│ ├── Server.java # Backend entry point (API routes)
│ ├── Stock.java # Model for individual stock data
│ ├── UseStock.java # Logic for stock operations
│ ├── Portfolio.java # Manages overall portfolio
│
├── public/
│ ├── index.html # Frontend interface
│ ├── logo.png # Web logo
│ ├── login.html
| ├── welcome.html
|
├── target/ # Compiled files (ignored by Git)
├── pom.xml # Maven configuration
└── README.md
```

---

## 🧮 Calculation Logic  
- Each **Stock** includes data like symbol, shares, and price.  
- The **Value** of each stock is computed as:  
``` value = shares × price ```

- The **Total Portfolio Value** = sum of all stock values.  
- CRUD API endpoints handle operations for adding, updating, reading, and deleting stock entries.  

---

## 🛠 Requirements

- **Java 17 or newer**  
- **Maven** (included wrapper: `mvnw`)  
- Any browser for frontend access

---

## 💻 How to Run  

### 1️⃣ Clone the Repository  
```bash
git clone https://github.com/KoVoidG/Stock-Portfolio-Manager.git
cd Stock-Portfolio-Manager
```
### 2️⃣ Build the Project
```.\mvnw clean package```

### 3️⃣ Run the Server
```java -jar target/spark-hello-1.0-SNAPSHOT-jar-with-dependencies.jar```

### 4️⃣ Open the Web Interface
``` http:\\localhost:8080 ```

---

## 📚 Learning Outcomes

By building this project, you’ll understand:
- How CRUD APIs work in a Java environment
- The connection between backend logic and frontend interfaces
- Managing data models and web requests in a full-stack workflow

---

## Contributors

<a href="https://github.com/KoVoidG/Stock-Portfolio-Manager/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=KoVoidG/Stock-Portfolio-Manager" />
</a>

---

## 📜 License

This project is licensed under the **MIT License**.

MIT License

Copyright (c) 2025 Hein Pyae Sone Htet

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
