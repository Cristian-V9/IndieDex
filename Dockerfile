# =====================================================================
#  IndieDex - imagen para desplegar (Render u otro servicio con Docker)
#  Compila Angular, lo copia dentro de Spring Boot y deja un solo .jar:
#  la página y la API quedan en la misma dirección.
# =====================================================================

# ---------- 1) Compilar el frontend Angular ----------
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
RUN npx ng build

# ---------- 2) Compilar el backend con el frontend dentro ----------
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/src ./src
COPY --from=frontend /app/frontend/dist/frontend/browser ./src/main/resources/static
RUN mvn -q -B -DskipTests package

# ---------- 3) Imagen final: solo Java y el .jar ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=backend /app/backend/target/*.jar app.jar
# Ajustes para que Spring Boot quepa en los 512 MB del plan gratuito
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
