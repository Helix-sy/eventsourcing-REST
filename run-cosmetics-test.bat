@echo off
echo Running Cosmetics Import Test...

cd %~dp0
mvn compile exec:java -Dexec.mainClass="de.eventsourcingbook.cart.cosmetics.CosmeticsApplicationKt" -Dspring.profiles.active=no-containers