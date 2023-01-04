#!/bin/bash

. /octopus-agent/shell/lib/wdd-lib-log.sh


# 判断命令是否存在
command_exists() {
  command -v "$@" >/dev/null 2>&1
}

#######   获取系统版本及64位或32位信息
check_sys() {
  # 获取当前终端的宽度，动态调整分割线的长度
  shellwidth=$(stty size | awk '{print $2}')
  SplitLine=$(yes "-" | sed ${shellwidth}'q' | tr -d '\n')

  sys_bit=$(uname -m)
  case $sys_bit in
  i[36]86)
    os_bit="32"
    hostArch="386"
    ;;
  x86_64)
    os_bit="64"
    hostArch="amd64"
    ;;
  *armv6*)
    os_bit="arm"
    hostArch="arm6"
    ;;
  *armv7*)
    os_bit="arm"
    hostArch="arm7"
    ;;
  *aarch64* | *armv8*)
    os_bit="arm64"
    hostArch="arm64"
    ;;
  *)
    error "
       哈哈……这个 辣鸡脚本 不支持你的系统。 (-_-) \n
       备注: 仅支持 Ubuntu 16+ / Debian 8+ / CentOS 7+ 系统
       " && exit 1
    ;;
  esac
  ## 判定Linux的发行版本
  if [ -f /etc/redhat-release ]; then
    hostArchVersion="centos"
  elif cat /etc/issue | grep -Eqi "debian"; then
    hostArchVersion="debian"
  elif cat /etc/issue | grep -Eqi "ubuntu"; then
    hostArchVersion="ubuntu"
  elif cat /etc/issue | grep -Eqi "centos|red hat|redhat"; then
    hostArchVersion="centos"
  elif cat /proc/version | grep -Eqi "debian"; then
    hostArchVersion="debian"
  elif cat /proc/version | grep -Eqi "ubuntu"; then
    hostArchVersion="ubuntu"
  elif cat /proc/version | grep -Eqi "centos|red hat|redhat"; then
    hostArchVersion="centos"
  else
    hostArchVersion=""
  fi

  # 判断系统的包管理工具  apt, yum, or zypper
  getPackageManageTool() {
    if [[ -n $(command -v apt-get) ]]; then
      CMD_INSTALL="apt-get -y -qq install"
      CMD_UPDATE="apt-get -qq update"
      CMD_REMOVE="apt-get -y remove"
    elif [[ -n $(command -v yum) ]]; then
      CMD_INSTALL="yum -y -q install"
      CMD_UPDATE="yum -q makecache"
      CMD_REMOVE="yum -y remove"
    elif [[ -n $(command -v zypper) ]]; then
      CMD_INSTALL="zypper -y install"
      CMD_UPDATE="zypper ref"
      CMD_REMOVE="zypper -y remove"
    else
      return 1
    fi
    return 0
  }

  # 检查系统包管理方式，更新包
  getPackageManageTool
  if [[ $? -eq 1 ]]; then
    error "系统的包管理不是 APT or YUM, 请手动安装所需要的软件."
    return 1
  fi

  return 0
}

RED="31m"                          ## 姨妈红
GREEN="32m"                        ## 水鸭青
YELLOW="33m"                       ## 鸭屎黄
PURPLE="35m"                       ## 基佬紫
BLUE="36m"                         ## 天依蓝
BlinkGreen="32;5m"                 ##闪烁的绿色
BlinkRed="31;5m"                   ##闪烁的红色
BackRed="41m"                      ## 背景红色
SplitLine="----------------------" #会被sys函数中的方法重写

######## 颜色函数方法很精妙 ############
colorEcho() {
  echo -e "\033[${1}${@:2}\033[0m" 1>&2
}

check_root() {
  if [[ $EUID != 0 ]]; then
    colorEcho ${RED} "当前非root账号(或没有root权限)，无法继续操作，请更换root账号!"
    colorEcho ${YELLOW} "使用sudo -命令获取临时root权限（执行后可能会提示输入root密码）"
    exit 1
  fi
}

FunctionStart() {
  colorEcho ${PURPLE} ${SplitLine}
  colorEcho ${PURPLE} ${SplitLine}
  echo ""
}

FunctionSuccess() {
  colorEcho ${GREEN} ${SplitLine}
  echo ""
}

FunctionEnd() {
  echo ""
  colorEcho ${BlinkGreen} ${SplitLine}
  echo ""
  echo ""
}

tmp () {

gcloud compute instances create octopus-agent-2c-4g-1 --project=compact-lacing-371804 --zone=asia-northeast1-b --machine-type=n2d-custom-2-4096 --network-interface=network-tier=PREMIUM,subnet=default --metadata=startup-script=wget\ https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh\ \&\&\ chmod\ \+x\ agent-bootup.sh\ \&\&\ /bin/bash\ agent-bootup.sh --can-ip-forward --maintenance-policy=MIGRATE --provisioning-model=STANDARD --service-account=172889627951-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append --create-disk=auto-delete=yes,boot=yes,device-name=octopus-agent-2c-4g,image=projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213,mode=rw,size=20,type=projects/compact-lacing-371804/zones/us-west4-b/diskTypes/pd1-ssd --no-shielded-secure-boot --shielded-vtpm --shielded-integrity-monitoring --reservation-affinity=any


gcloud compute instances create tokyo-amd64-03 --project=compact-lacing-371804 --zone=asia-northeast1-b --machine-type=n2d-custom-2-4096 --network-interface=network-tier=PREMIUM,subnet=default  --can-ip-forward --maintenance-policy=MIGRATE --provisioning-model=STANDARD --service-account=172889627951-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append --create-disk=auto-delete=yes,boot=yes,device-name=octopus-agent-2c-4g,image=projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213,mode=rw,size=20,type=projects/compact-lacing-371804/zones/us-west4-b/diskTypes/pd-ssd --no-shielded-secure-boot --shielded-vtpm --shielded-integrity-monitoring --reservation-affinity=any

gcloud compute ssh --zone "asia-northeast1-b" "tokyo-amd64-03" --project "compact-lacing-371804"


wget https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh && chmod +x agent-bootup.sh && /bin/bash agent-bootup.sh


echo "y
" | gcloud compute instances delete tokyo-amd64-03 --project=compact-lacing-371804 --zone=asia-northeast1-b


apt-cache madison openjdk-11-jdk | head -n 1 | awk '{print$3}'

java -jar /octopus-agent/agent.jar -Xms128m -Xmx512m  -Dfile.encoding=utf-8  --spring.profiles.active=k3s --spring.cloud.nacos.config.group=k3s --spring.cloud.nacos.config.extension-configs[0].dataId=common-k3s.yaml --spring.cloud.nacos.config.extension-configs[0].group=k3s

}

