#!/bin/bash

#####  environment variables ######

RepoSourcePath=https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell
DependLibFiles=(
  wdd-lib-file.sh
  wdd-lib-log.sh
  wdd-lib-os.sh
  wdd-lib-env.sh
  wdd-lib-sys.sh
)

OctopusAgentPath=/octopus-agent/shell
#####  environment variables ######

CMD_INSTALL=""
CMD_UPDATE=""
CMD_REMOVE=""
SOFTWARE_UPDATED=0
LinuxReleaseVersion=""
LinuxRelease=""

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
    LinuxRelease="386"
    ;;
  x86_64)
    os_bit="64"
    LinuxRelease="amd64"
    ;;
  *armv6*)
    os_bit="arm"
    LinuxRelease="arm6"
    ;;
  *armv7*)
    os_bit="arm"
    LinuxRelease="arm7"
    ;;
  *aarch64* | *armv8*)
    os_bit="arm64"
    LinuxRelease="arm64"
    ;;
  *)
    colorEcho ${RED} "
       哈哈……这个 辣鸡脚本 不支持你的系统。 (-_-) \n
       备注: 仅支持 Ubuntu 16+ / Debian 8+ / CentOS 7+ 系统
       " && exit 1
    ;;
  esac
  ## 判定Linux的发行版本
  if [ -f /etc/redhat-release ]; then
    LinuxReleaseVersion="centos"
  elif cat /etc/issue | grep -Eqi "debian"; then
    LinuxReleaseVersion="debian"
  elif cat /etc/issue | grep -Eqi "ubuntu"; then
    LinuxReleaseVersion="ubuntu"
  elif cat /etc/issue | grep -Eqi "centos|red hat|redhat"; then
    LinuxReleaseVersion="centos"
  elif cat /proc/version | grep -Eqi "debian"; then
    LinuxReleaseVersion="debian"
  elif cat /proc/version | grep -Eqi "ubuntu"; then
    LinuxReleaseVersion="ubuntu"
  elif cat /proc/version | grep -Eqi "centos|red hat|redhat"; then
    LinuxReleaseVersion="centos"
  else
    LinuxReleaseVersion=""
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
    colorEcho ${RED} "系统的包管理不是 APT or YUM, 请手动安装所需要的软件."
    return 1
  fi

  ### 更新程序引索
  if [[ $SOFTWARE_UPDATED -eq 0 ]]; then
    colorEcho ${BLUE} "正在更新软件包管理...可能花费较长时间…………"
    $CMD_UPDATE
    SOFTWARE_UPDATED=1
  fi
  return 0
}

##  安装所需要的程序，及依赖程序
installDemandSoftwares() {
  for software in $@; do
    ## 安装该软件
    if [[ -n $(command -v ${software}) ]]; then
      colorEcho ${GREEN} "${software}已经安装了...跳过..."
      echo ""
    else
      colorEcho ${BLUE} "正在安装 ${software}..."
      $CMD_INSTALL ${software}
      ## 判断该软件是否安装成功
      if [[ $? -ne 0 ]]; then
        colorEcho ${RED} "安装 ${software} 失败。"
        colorEcho ${RED} "如果是重要软件，本脚本会自动终止！！"
        colorEcho ${PURPLE} "一般软件，本脚本会忽略错误并继续运行，请之后手动安装该程序。"
        return 1
      else
        colorEcho ${GREEN} "已经成功安装  ${software}"
        FunctionSuccess
        echo ""
      fi
    fi
  done
  return 0
}

DownloadAllFile() {

  FunctionStart

  mkdir -p $OctopusAgentPath
  mkdir -p $OctopusAgentPath/lib

  echo "start to download all needed lib shell"
  for libfile in ${DependLibFiles[*]}; do
    echo "lib file is $libfile"
    wget "$RepoSourcePath/lib/$libfile" -O $OctopusAgentPath/lib/$libfile

  done

  colorEcho $BLUE "start to download octopus agent !"
  # check for latest version
  # download the lasted jar
  wget https://happybirthday.107421.xyz/octopus-agent/octopus-agent-2022-12-21-16-00-00.jar -O /octopus-agent/agent.jar

  FunctionSuccess
  FunctionEnd

}


ModifySystemConfig(){
  FunctionStart

  colorEcho ${BLUE} "开始修改系统内核参数…………"
  ## 配置内核参数
  cat >/etc/sysctl.d/k8s.conf <<EOF
net.ipv4.ip_forward = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
net.ipv6.conf.lo.disable_ipv6 = 1
net.ipv6.conf.all.forwarding = 1
EOF

  ## 执行命令以应用
  sysctl -p /etc/sysctl.d/k8s.conf
  colorEcho ${GREEN} "--------------系统内核参数修改的结果如上所示----------------"
  FunctionSuccess



}

InstallJDKPackage() {
  JDK_VERSION="11"
  if [[ "$1" -ne " " ]]; then
    JDK_VERSION="$1"
    echo "JDK Version = ${JDK_VERSION}"
  fi

  echo "InstallJDK from package management !"
  echo ""

  if [[ "${LinuxReleaseVersion}" == "centos" ]]; then
    colorEcho ${BLUE} "当前系统发行版为 centos !"
    colorEcho ${BLUE} "可以安装的  openjdk 版本为："
    yum list java-${JDK_VERSION}-openjdk | grep ${JDK_VERSION}
    echo ""
    colorEcho ${BLUE} "开始安装最新版本：$(yum list java-${JDK_VERSION}-openjdk | grep ${JDK_VERSION} | awk '{print $2}' | cut -d':' -f2 | head -n 1)"
    installDemandSoftwares java-${JDK_VERSION}-openjdk-$(yum list java-${JDK_VERSION}-openjdk | grep ${JDK_VERSION} | awk '{print $2}' | cut -d':' -f2 | head -n 1)
  else
    colorEcho ${BLUE} "当前系统发行版为 ubuntu/debain !"
    colorEcho ${BLUE} "可以安装的  openjdk 版本为："
    apt-cache madison openjdk-${JDK_VERSION}-jdk | awk '{print$3}'
    echo ""
    colorEcho ${BLUE} "开始安装最新版本：$(apt-cache madison openjdk-${JDK_VERSION}-jdk | head -n 1 | awk '{print$3}')"

    installDemandSoftwares openjdk-${JDK_VERSION}-jdk

  fi

  colorEcho ${BLUE} "请检查下面的内容输出！！！"
  java -version

}

systemdAgent(){
  local JAVA_OPTS="-Xms128m -Xmx256m"

  cat >/etc/systemd/system/octopus-agent.service <<EOF
[Unit]
Description=Octopus Agent
Documentation=https://octopus.107421.xyz/
After=network.target

[Service]
PermissionsStartOnly=true
LimitNOFILE=1048576
LimitNPROC=65535
User=root
WorkingDirectory=/octopus-agent
ExecStart=java -jar /octopus-agent/agent.jar ${JAVA_OPTS}
ExecReload=source /etc/environment
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

}

## 为了本脚本能够满足Ubuntu系统，做出设当的更改
CommonToolInstall() {
  FunctionStart
  colorEcho ${BLUE} "开始进行Linux常用工具的安装过程…………"
  FunctionSuccess
  colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion} ！！"
  colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion} ！！"
  colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion} ！！"
  echo ""
  if [[ ${LinuxReleaseVersion} == "centos" ]]; then
    centosCommonTool=(deltarpm net-tools iputils bind-utils lsof curl wget vim mtr htop)
    installDemandSoftwares ${centosCommonTool[@]}
  elif [[ ${LinuxReleaseVersion} == "ubuntu" ]] || [[ ${LinuxReleaseVersion} == "debian" ]]; then
    ubuntuCommonTool=(iputils-ping net-tools dnsutils lsof curl wget mtr-tiny vim htop lrzsz)
    installDemandSoftwares ${ubuntuCommonTool[@]}
  fi
  FunctionEnd
}


BootUPAgent(){

  FunctionStart

  colorEcho $BLUE "prepare the env"
  chmod +x $OctopusAgentPath/lib/wdd-lib-env.sh
  $OctopusAgentPath/lib/wdd-lib-env.sh

  colorEcho $BLUE "start to daemon the agent pid"
  systemdAgent

  colorEcho $BLUE "start the agent!"
  systemctl daemon-reload

  systemctl enable octopus-agent.service
  systemctl start octopus-agent.service

  FunctionSuccess
  FunctionEnd
}

main(){

  check_root
  check_sys

  ModifySystemConfig

  CommonToolInstall

  InstallJDKPackage 11

  DownloadAllFile

  BootUPAgent

}


main