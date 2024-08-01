FROM gradle:8.3.0-jdk20

WORKDIR /

COPY / .

RUN ./gradlew installDist

CMD ./build/install/bin/demo