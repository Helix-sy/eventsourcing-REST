@echo off
setlocal enabledelayedexpansion

echo Checking Docker container status...

REM Check if Docker Desktop is running
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker Desktop is not running! Starting Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo Waiting for Docker Desktop to start...
    timeout /t 20 /nobreak
)

REM Check if our containers are running
docker ps | findstr "postgres-cart" >nul
set postgres_running=%errorlevel%
docker ps | findstr "kafka-cart" >nul
set kafka_running=%errorlevel%
docker ps | findstr "zookeeper-cart" >nul
set zookeeper_running=%errorlevel%

REM If any of the containers is not running, start all of them
if %postgres_running% neq 0 (
    echo At least one container is not running. Starting Docker containers...
    echo This might take a moment, please wait...
    docker-compose up -d
    
    echo Waiting for containers to be ready...
    timeout /t 10 /nobreak
) else (
    echo All required Docker containers are already running.
)

echo Starting application with cosmetics module enabled...
cd %~dp0
java -jar target\cart-3.3.2.jar --spring.profiles.active=no-containers