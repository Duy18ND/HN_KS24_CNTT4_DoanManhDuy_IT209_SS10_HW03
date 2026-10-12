# ============================================================
# Dockerfile - Multi-stage Build (TỐI ƯU cho Production)
# Stage 1: Build ứng dụng với Gradle + JDK
# Stage 2: Chỉ chứa JRE tối thiểu + file JAR đã build
# Kết quả: Image nhỏ gọn, bảo mật, phù hợp Production
# ============================================================

# ========================
# STAGE 1: Builder
# ========================
FROM gradle:9.4.1-jdk17-alpine AS builder

WORKDIR /app

# Copy file khai báo phụ thuộc TRƯỚC để tận dụng Docker Layer Cache
# Nếu build.gradle và settings.gradle không đổi → Docker sẽ dùng cache
# cho bước cài dependencies, giúp build nhanh hơn đáng kể
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN chmod +x gradlew

# Tải trước dependencies (layer này được cache nếu không đổi dependencies)
RUN ./gradlew dependencies --no-daemon

# Sau đó mới copy source code (thay đổi thường xuyên)
COPY src ./src

# Build ứng dụng → tạo file JAR
RUN ./gradlew clean bootJar --no-daemon

# ========================
# STAGE 2: Runtime
# ========================
# Sử dụng Base Image siêu nhẹ chỉ chứa JRE (không có JDK, Gradle)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Tạo user non-root để tăng bảo mật trong Production
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Chỉ COPY file JAR đã build từ stage builder
# → Loại bỏ toàn bộ source code, Gradle cache, JDK, build tools
COPY --from=builder /app/build/libs/*.jar app.jar

# Chuyển sang user non-root
USER appuser

# Expose port ứng dụng
EXPOSE 8080

# Health check để kiểm tra ứng dụng có hoạt động không
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]