@echo off
echo *** Démarrage de l'application MDD API ***
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m" 