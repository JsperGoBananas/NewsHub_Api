FROM openjdk:17-alpine3.14
COPY target/NewsHub-API.jar /usr/app/app.jar
WORKDIR /usr/app
EXPOSE 8080
# active spring profile -Dspring.profiles.active= prod
# 设置环境变量（可选）
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE"]
