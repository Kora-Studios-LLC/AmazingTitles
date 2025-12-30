## Build for Paper 1.21.7
- Install JDK 21+ and Maven (Paper 1.21.7 binaries target Java 21).
- The Paper Maven repo is already declared and `paper.api.version` is set to `1.21.7-R0.1-SNAPSHOT` in the root `pom.xml`.
- Build the full plugin (all version providers) with `mvn clean package -pl Build -am -DskipTests`. The shaded jar lands at `Build/target/Build-1.0-SNAPSHOT-shaded.jar`.
- To build just the Paper 1.21.7 provider plus core, run `mvn clean package -pl R1_21_R1 -am -DskipTests`.
- No extra Paper/Spigot server jars are needed for the 1.21.7 provider; older version providers still use the pre-bundled `libs/` jars.
