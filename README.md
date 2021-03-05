# Home Control

Personal home control software. Replaces the really crappy Shelly App with a stand-alone Spring Boot backend and a
ReactJS frontend. List of devices
(grouped by tags) and scenes are currently hardcoded as part of application.yml.

Future extensions to some other stuff than Shellys is planned.

The frontend looks really simple at the moment:

![Frontend screenshot](frontend-screenshot.png?raw=true)

## Build

A simple `mvn package` should generate a fat jar running a Spring boot app on port 8080. It also build the frontend and
packages it into the jar. Start it with `java -jar application/target/application-*.jar`. Open
http://localhost:8080 locally once run the jar has booted. If you havn't adapted the `application.yml` to your own local
setup, it won't work.

## Contributing

Please fork and make your own adaptations. Feel free to contact me, but don't expect support or any acceptance of PRs.
This is really a very personal project.
