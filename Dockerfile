FROM zoyua/jdk:1.8

MAINTAINER zoyua

RUN mkdir -p /export/host && \
    mkdir -p /export/copy-host

ADD client.jar /export/jars/client.jar
ADD server.jar /export/jars/server.jar
ADD start.sh /export/sh/start.sh
ADD domain /export/host

WORKDIR export

EXPOSE 8080

CMD ["/export/sh/start.sh"]