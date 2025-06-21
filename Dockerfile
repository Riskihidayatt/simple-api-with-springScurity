# Gunakan base image dengan JDK 21
FROM openjdk:19-jdk-slim

# Informasi author (opsional)
LABEL authors="Riski Hidayat"

# Set direktori kerja di dalam container
WORKDIR /app

# Salin file JAR ke dalam container
COPY target/*.jar app.jar

# Buka port 8080 (default Spring Boot)
EXPOSE 8080

# Jalankan aplikasi
CMD ["java", "-jar", "app.jar"]
