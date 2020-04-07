# TeamLoan backend API
![TeamLoan](/src/main/resources/META-INF/resources/template-resources/mail-header-joined.jpg "TeamLoan logo")

This project is the [TeamLoan](https://www.teamloan.pt) backend API.

## Quarkus framework

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `teamloan-backend-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/teamloan-backend-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/teamloan-backend-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide.

---------------------------------------------------------------

<div align="center">
<a href="[https://tech4covid19.org/](https://tech4covid19.org/)" target="_blank"><img src="https://ucarecdn.com/e2cfb782-1524-496a-a48f-f97b75440d56/"></a>
</div>
<div align="center">
  <h3>
    <a href="https://tech4covid19.org">
      Website
    </a>
    <span> | </span>
    <a href="https://join.slack.com/t/tech4covid19/shared_invite/zt-csmcdobq-Qbn8fwG52JssqhrIwfv4Yg">
      Community
    </a>
  </h3>
</div>

<div align="center">
  <sub>Built with ❤︎ by the
  <a href="https://tech4covid19.org">tech4covid19.org</a> community
</div>
