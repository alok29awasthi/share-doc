# 📂 Share-Doc – File Storage & Sharing System (Backend)

A **Spring Boot (Gradle)** backend application that provides **secure file storage and sharing functionality** using **AWS S3**.  
Users can upload, download, and share files with access control.

---

## 🚀 Features

- 🔑 **User Authentication & Management** (Phase 1)
- ☁️ **AWS S3 Integration** for file storage (Phase 2)
- 📥 **File Upload & Download** with metadata tracking (Phase 2)
- 👥 **Access Control**: Grant/Revoke access, view owned & shared files (Phase 3)
- ⚡ **Secure Download** via backend proxy (no public S3 links exposed)
- 🛠️ Built with **Spring Boot + Gradle**

---

## 🏗️ Tech Stack

- **Backend Framework**: Spring Boot 3
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **Storage**: AWS S3
- **ORM**: Spring Data JPA / Hibernate
- **Authentication**: Basic/JWT

---

## 📂 Project Structure
```
src/main/java/com/sharedoc/app 
├── controller # REST Controllers
├── service # Business Logic (S3Service, Access Control)
├── repository # Spring Data JPA Repositories
├── model # Entities (User, FileMetadata, FileAccess)
└── config # AWS & DB Configuration
```

👨‍💻 Author
Developed by Alok Awasthi 🚀