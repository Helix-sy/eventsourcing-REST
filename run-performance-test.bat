@echo off
echo Running Cosmetics Performance Test...

cd %~dp0
mvn -Dtest=CosmeticsPerformanceTest -Dspring.profiles.active=cosmetics-test test