FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/sky-product-1.0-SNAPSHOT.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar"]
