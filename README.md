# User Service - Spring Boot Application

## Tổng quan
User Service là một ứng dụng quản lý người dùng được xây dựng bằng Spring Boot, bao gồm các tính năng:
- Đăng ký và đăng nhập người dùng
- Xác thực JWT (JSON Web Token)
- Quản lý tài khoản (cập nhật thông tin, đổi mật khẩu)
- Phân loại tài khoản: Regular và VIP
- Phân quyền: USER và ADMIN

## Công nghệ sử dụng
- Java 17
- Spring Boot 3.2.0
- Spring Security với JWT
- Spring Data JPA
- H2 Database (development) / PostgreSQL (production)
- Lombok
- Maven

## Cấu trúc project
```
user-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/userservice/
│   │   │   ├── config/           # Cấu hình Security
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # JPA Entities
│   │   │   ├── enums/            # Enumerations
│   │   │   ├── exception/        # Exception handlers
│   │   │   ├── repository/       # JPA Repositories
│   │   │   ├── security/         # Security components
│   │   │   ├── service/          # Business logic
│   │   │   └── UserServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Cài đặt và chạy

### Yêu cầu
- JDK 17 trở lên
- Maven 3.6+

### Các bước chạy

1. **Clone hoặc tải project về**

2. **Build project**
```bash
mvn clean install
```

3. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

Hoặc chạy file JAR:
```bash
java -jar target/user-service-1.0.0.jar
```

4. **Truy cập ứng dụng**
- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:userdb`
  - Username: `sa`
  - Password: (để trống)

## API Endpoints

### Authentication APIs

#### 1. Đăng ký tài khoản
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "0123456789"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phoneNumber": "0123456789",
    "role": "ROLE_USER",
    "accountType": "REGULAR",
    "isActive": true,
    "isEmailVerified": false,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### 2. Đăng nhập
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "accountType": "REGULAR"
    }
  }
}
```

### User Management APIs (Yêu cầu Authentication)

**Lưu ý:** Tất cả các API dưới đây yêu cầu JWT token trong header:
```
Authorization: Bearer <your-jwt-token>
```

#### 3. Lấy thông tin user hiện tại
```http
GET /api/users/me
Authorization: Bearer <token>
```

#### 4. Cập nhật thông tin user
```http
PUT /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "fullName": "John Updated",
  "phoneNumber": "0987654321"
}
```

#### 5. Đổi mật khẩu
```http
PUT /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword456"
}
```

#### 6. Xóa tài khoản (Soft delete)
```http
DELETE /api/users/me
Authorization: Bearer <token>
```

### Admin APIs (Chỉ dành cho ADMIN)

#### 7. Lấy danh sách tất cả users
```http
GET /api/users
Authorization: Bearer <admin-token>
```

#### 8. Lấy thông tin user theo ID
```http
GET /api/users/{id}
Authorization: Bearer <admin-token>
```

#### 9. Lấy danh sách VIP users
```http
GET /api/users/vip
Authorization: Bearer <admin-token>
```

#### 10. Nâng cấp user lên VIP
```http
POST /api/users/{id}/upgrade-vip?months=3
Authorization: Bearer <admin-token>
```

#### 11. Hạ cấp user xuống Regular
```http
POST /api/users/{id}/downgrade
Authorization: Bearer <admin-token>
```

## Cấu hình Database

### H2 (Development - Mặc định)
```properties
spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.username=sa
spring.datasource.password=
```

### PostgreSQL (Production)
1. Cài đặt PostgreSQL
2. Tạo database: `CREATE DATABASE userdb;`
3. Uncomment và cập nhật trong `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Cấu hình JWT

Trong `application.properties`:
```properties
jwt.secret=yourSecretKeyForJWTTokenGenerationShouldBeAtLeast256BitsLong
jwt.expiration=86400000  # 24 hours
```

**Lưu ý:** Trong production, đổi `jwt.secret` thành giá trị bảo mật và sử dụng environment variables.

## Testing với Postman/cURL

### Ví dụ flow đầy đủ:

1. **Đăng ký**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

2. **Đăng nhập**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

3. **Lấy thông tin user (sử dụng token từ bước 2)**
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Các tính năng chính

### 1. Phân loại tài khoản
- **REGULAR**: Tài khoản thường
- **VIP**: Tài khoản VIP với thời hạn

### 2. Phân quyền
- **ROLE_USER**: Người dùng thường
- **ROLE_ADMIN**: Quản trị viên

### 3. Bảo mật
- Mật khẩu được mã hóa bằng BCrypt
- JWT authentication
- Stateless session management
- CORS configuration
- Request validation

### 4. Quản lý VIP
- Nâng cấp lên VIP với thời hạn (theo tháng)
- Tự động kiểm tra hết hạn VIP
- Hạ cấp về Regular

## Xử lý lỗi

API trả về response thống nhất:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid"
  }
}
```

## Best Practices đã áp dụng

1. **Separation of Concerns**: Tách biệt Controller, Service, Repository
2. **DTO Pattern**: Sử dụng DTO để transfer data
3. **Exception Handling**: Global exception handler
4. **Validation**: Bean validation với @Valid
5. **Security**: JWT + Spring Security
6. **Soft Delete**: Deactivate thay vì xóa vật lý
7. **Timestamps**: Auto-generated created/updated timestamps
8. **Transaction Management**: @Transactional cho các operations quan trọng

## License
MIT License