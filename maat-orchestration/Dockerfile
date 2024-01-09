FROM amazoncorretto:17-alpine
RUN mkdir -p /opt/laa-maat-orchestration/
WORKDIR /opt/laa-maat-orchestration/
COPY ./build/libs/maat-orchestration.jar /opt/laa-maat-orchestration/app.jar
RUN addgroup -S appgroup && adduser -u 1001 -S appuser -G appgroup
USER 1001
EXPOSE 8290 8300
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENTRYPOINT ["java","-jar","app.jar"]
