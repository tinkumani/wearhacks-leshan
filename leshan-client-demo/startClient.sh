java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8453 -Dkludge=true -Djava.library.path="/opt/poseidon/wearhacks-leshan/poseidon/lib" -jar target/leshan-client-demo-1.0.0-SNAPSHOT-jar-with-dependencies.jar -u cdpfest.nokialabs.com:5683 -n MyCamera
