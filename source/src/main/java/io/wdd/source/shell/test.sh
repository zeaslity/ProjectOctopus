#!/bin/bash


echo start to update !
apt-get update


echo "

echo start to install nginx
apt-get install nginx -y

echo
echo start to uninstall nginx
apt remove nginx -y


echo
echo start to get ip info
curl https://ipinfo.io


echo
echo --- end ---


 systemctl status nginx.service | grep -c "active (running)"



