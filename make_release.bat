@echo off
setlocal

REM 1) Build bằng Ant (NetBeans)
call ant clean build
if errorlevel 1 (
  echo Build failed
  pause
  exit /b 1
)

REM 2) Tao dist neu chua co
if not exist dist mkdir dist

REM 3) Dong goi 2 jar tu build\classes
jar cfm dist\FlightBooking-ADMIN.jar manifest_admin.mf -C build\classes .
jar cfm dist\FlightBooking-USER.jar  manifest_user.mf  -C build\classes .

echo.
echo ==== DONE ====
echo dist\FlightBooking-ADMIN.jar
echo dist\FlightBooking-USER.jar
pause
