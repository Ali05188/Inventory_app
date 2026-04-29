@echo off
cd auth-service
php artisan cache:clear
php artisan assets:import C:\xampp\tmp\donnees.csv --sync

