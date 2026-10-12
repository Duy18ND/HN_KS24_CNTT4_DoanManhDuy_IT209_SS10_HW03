# Bài 3: Tối ưu hóa dung lượng Image & Layer Caching với Multi-stage Build

## 📋 Mục tiêu

Sử dụng kỹ thuật **Multi-stage Build** và **Layer Caching** để giảm dung lượng Docker Image xuống mức tối thiểu, so sánh với Single-stage Build truyền thống.

## 📁 Cấu trúc thư mục

```
SESSION10_03/
├── Dockerfile              # Multi-stage Build (tối ưu cho Production)
├── Dockerfile.single       # Single-stage Build (không tối ưu, dùng để so sánh)
├── docker-compose.yml      # Docker Compose chạy ứng dụng + MySQL
├── build.gradle            # Cấu hình Gradle dependencies
├── settings.gradle         # Cấu hình project name
├── gradlew / gradlew.bat   # Gradle Wrapper
├── src/                    # Source code ứng dụng Spring Boot
└── README.md               # Tài liệu bài nộp
```

## 🔧 Hướng dẫn Build & Kiểm tra

### 1. Build image Single-stage (không tối ưu)

```bash
docker build -t hw-backend:single -f Dockerfile.single .
```

### 2. Build image Multi-stage (tối ưu)

```bash
docker build -t hw-backend:multistage .
```

### 3. So sánh dung lượng

```bash
docker images | grep hw-backend
```

### 4. Chạy container kiểm tra

```bash
docker compose up -d
```

Hoặc chạy riêng container (cần MySQL đang chạy):

```bash
docker run -d -p 8080:8080 --name backend-test hw-backend:multistage
```

## 📊 Bảng so sánh chi tiết

### So sánh dung lượng Image

| Tiêu chí                  | `hw-backend:single`            | `hw-backend:multistage`          |
|----------------------------|--------------------------------|----------------------------------|
| **Base Image**             | `gradle:9.4.1-jdk17-alpine`   | `eclipse-temurin:17-jre-alpine`  |
| **Dung lượng ước tính**    | ~600–800 MB                    | ~100–150 MB                      |
| **Giảm dung lượng**        | —                              | **~75–80%**                      |
| **Chứa JDK**              | ✅ Có (không cần cho runtime)  | ❌ Không (chỉ JRE)              |
| **Chứa Gradle**           | ✅ Có (không cần cho runtime)  | ❌ Không                         |
| **Chứa Source Code**       | ✅ Có (rủi ro bảo mật)        | ❌ Không                         |
| **Chứa Build Cache**       | ✅ Có                          | ❌ Không                         |
| **Non-root User**          | ❌ Không                       | ✅ Có (appuser)                  |
| **Health Check**           | ❌ Không                       | ✅ Có                            |

### So sánh số lượng Layer

| Image                    | Số Layer ước tính | Mô tả                                                |
|--------------------------|-------------------|-------------------------------------------------------|
| `hw-backend:single`     | ~8–10 layers      | Bao gồm JDK, Gradle, source, build cache, JAR        |
| `hw-backend:multistage` | ~5–6 layers       | Chỉ bao gồm JRE base + JAR file + user config        |

### Lợi ích Layer Caching trong Multi-stage

| Thay đổi code              | Rebuild cần thiết                          | Thời gian tiết kiệm |
|-----------------------------|--------------------------------------------|----------------------|
| Chỉ sửa source code (`src/`) | Chỉ rebuild từ `COPY src` trở xuống       | ~60–70%              |
| Sửa dependencies (`build.gradle`) | Rebuild từ `COPY build.gradle` trở xuống | ~30%                 |
| Không thay đổi gì          | Docker dùng toàn bộ cache                  | ~95%                 |

## 🏗️ Giải thích kỹ thuật Multi-stage Build

### Dockerfile.single (Single-stage) — KHÔNG tối ưu

```dockerfile
FROM gradle:9.4.1-jdk17-alpine    # Image lớn chứa cả JDK + Gradle
WORKDIR /app
COPY . .                           # Copy tất cả vào image
RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/...jar"]
```

**Vấn đề:**
- Image chứa JDK đầy đủ (~300MB) nhưng runtime chỉ cần JRE (~80MB)
- Gradle cache, source code, build tools đều nằm trong image cuối
- Không tận dụng Layer Caching → mỗi lần build đều tải lại dependencies

### Dockerfile (Multi-stage) — TỐI ƯU

```dockerfile
# STAGE 1: Builder
FROM gradle:9.4.1-jdk17-alpine AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./     # ① Copy dependency files TRƯỚC
COPY gradle ./gradle
COPY gradlew ./
RUN ./gradlew dependencies --no-daemon   # ② Cache dependencies layer
COPY src ./src                            # ③ Copy source code SAU
RUN ./gradlew clean bootJar --no-daemon  # ④ Build JAR

# STAGE 2: Runtime (siêu nhẹ)
FROM eclipse-temurin:17-jre-alpine       # ⑤ Base image chỉ có JRE
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar  # ⑥ Chỉ lấy JAR
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Chiến lược Layer Caching:**
1. `build.gradle` ít thay đổi → layer `dependencies` được cache
2. `src/` thay đổi thường xuyên → chỉ rebuild từ bước COPY src
3. Stage 2 chỉ lấy file JAR → loại bỏ toàn bộ build artifacts

## ✅ Kết quả mong đợi

```
REPOSITORY    TAG           IMAGE ID       SIZE
hw-backend    single        abc123def456   ~700MB
hw-backend    multistage    789ghi012jkl   ~120MB
```

- ✅ Dung lượng `hw-backend:multistage` giảm **~75–80%** so với `hw-backend:single`
- ✅ Container chạy ổn định, phản hồi API chính xác trên port 8080
- ✅ Layer Caching giúp rebuild nhanh hơn khi chỉ thay đổi source code

## 📝 Lệnh kiểm tra

```bash
# Build cả 2 image
docker build -t hw-backend:single -f Dockerfile.single .
docker build -t hw-backend:multistage .

# So sánh dung lượng
docker images | grep hw-backend

# Kiểm tra số layers
docker history hw-backend:single
docker history hw-backend:multistage

# Chạy container test
docker compose up -d

# Test API
curl http://localhost:8080/actuator/health
```

## 👤 Thông tin sinh viên

- **Họ và tên:** [Điền tên sinh viên]
- **MSSV:** [Điền MSSV]
- **Môn học:** IT209 - DevOps
- **Bài tập:** Session 10 - Exercise 3
