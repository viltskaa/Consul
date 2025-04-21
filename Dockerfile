FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Копируем Gradle Wrapper
COPY gradlew /app/gradlew
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

# Исправляем окончания строк в gradlew (если это необходимо)
RUN sed -i 's/\r$//' gradlew  # или RUN dos2unix gradlew, если установлен dos2unix

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Проверяем содержимое директории
RUN ls -la /app

# Запускаем сборку
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

# Копируем собранный артефакт из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
