@echo off
setlocal enabledelayedexpansion

::不管三七二十一先停掉可能在跑的wsl实例
wsl --shutdown Ubuntu-18.04
::重新拉起来，并且用root的身份，启动ssh服务和docker服务
wsl -u root service ssh start
wsl -u root service docker start | findstr "Starting Docker" > nul
if !errorlevel! equ 0 (
    echo docker start success
    :: 看看我要的IP在不在
    wsl -u root ip addr | findstr "172.24.240.10" > nul
    if !errorlevel! equ 0 (
        echo wsl ip has set
    ) else (
        ::不在的话给安排上
        wsl -u root ip addr add 172.24.240.10/24 broadcast 172.24.240.0 dev eth0 label eth0:1
        echo set wsl ip success: 172.24.240.10
    )


    ::windows作为wsl的宿主，在wsl的固定IP的同一网段也给安排另外一个IP
    ipconfig | findstr "172.24.240.1" > nul
    if !errorlevel! equ 0 (
        echo windows ip has set
    ) else (
        netsh interface ip add address "vEthernet (WSL)" 172.24.240.1 255.255.240.0
        echo set windows ip success: 172.24.240.1
    )
)
pause