# Sử dụng JDK nhẹ
FROM eclipse-temurin:17-jdk-alpine

# Thư mục làm việc trong container
WORKDIR /app

# Copy file jar đã build vào container
COPY target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Lệnh chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
