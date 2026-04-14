## Build for Paper 1.21.11
- Install JDK 21+ and Maven (Paper 1.21.11 binaries target Java 21).
- The Paper Maven repo is already declared and `paper.api.version` is set to `1.21.11-R0.1-SNAPSHOT` in the root `pom.xml`.
- Build the full plugin (all version providers) with `mvn clean package -pl Build -am -DskipTests`. The shaded plugin jar lands at `Build/target/AmazingTitles-6.0-SNAPSHOT.jar`.
- To build just the Paper 1.21.11 provider plus core, run `mvn clean package -pl R1_21_R1 -am -DskipTests`.
- No extra Paper/Spigot server jars are needed for the 1.21.11 provider; older version providers still use the pre-bundled `libs/` jars.
