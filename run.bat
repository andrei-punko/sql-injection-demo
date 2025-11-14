@echo off
chcp 65001 >nul
echo ========================================
echo   SQL Injection Demo - Запуск сервиса
echo ========================================
echo.

REM Проверка наличия Maven
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ОШИБКА] Maven не найден в PATH!
    echo Убедитесь, что Maven установлен и добавлен в переменную окружения PATH.
    echo.
    pause
    exit /b 1
)

REM Проверка наличия Java
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ОШИБКА] Java не найдена в PATH!
    echo Убедитесь, что Java установлена и добавлена в переменную окружения PATH.
    echo.
    pause
    exit /b 1
)

echo [INFO] Проверка версии Java...
java -version
echo.

echo [INFO] Запуск Spring Boot приложения...
echo [INFO] Сервис будет доступен по адресу: http://localhost:8080
echo [INFO] H2 Console будет доступна по адресу: http://localhost:8080/h2-console
echo.
echo Для остановки сервиса нажмите Ctrl+C
echo.

mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ОШИБКА] Не удалось запустить приложение!
    pause
    exit /b 1
)

pause

