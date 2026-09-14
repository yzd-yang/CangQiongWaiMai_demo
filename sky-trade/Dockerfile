FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/sky-trade-1.0-SNAPSHOT.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar"]
