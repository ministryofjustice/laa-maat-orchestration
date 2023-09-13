FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-maat-orchestration/
WORKDIR /opt/laa-maat-orchestration/
COPY ./build/libs/maat-orchestration.jar /opt/laa-maat-orchestration/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
