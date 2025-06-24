@echo off
echo 正在启动RabbitMQ容器...
docker-compose up -d rabbitmq
echo.
echo RabbitMQ正在启动，请稍候...
echo 管理界面: http://localhost:15672
echo 用户名: guest
echo 密码: guest
echo.
pause 