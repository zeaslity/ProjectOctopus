#!/bin/bash

#####  environment variables ######

JAVA_OPTS="-Xms128m -Xmx512m  -Dfile.encoding=utf-8  --spring.profiles.active=k3s --spring.cloud.nacos.config.group=k3s --spring.cloud.nacos.config.extension-configs[0].dataId=common-k3s.yaml --spring.cloud.nacos.config.extension-configs[0].group=k3s"

DependLibFiles=(
  wdd-lib-file.sh
  wdd-lib-log.sh
  wdd-lib-os.sh
  wdd-lib-env.sh
  wdd-lib-sys.sh
)

OctopusAgentUrl=https://happybirthday.107421.xyz/octopus-agent/

RepoSourcePath=https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell
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

#######################################
# description
# Globals:
#   EUID
#   RED
#   YELLOW
# Arguments:
#  None
#######################################
check_root() {
  if [[ $EUID != 0 ]]; then
    colorEcho ${RED} "当前非root账号(或没有root权限)，无法继续操作，请更换root账号!"
    colorEcho ${YELLOW} "使用sudo -命令获取临时root权限（执行后可能会提示输入root密码）"
    exit 1
  fi
}

#######################################
# description
# Globals:
#   PURPLE
#   SplitLine
# Arguments:
#  None
#######################################
FunctionStart() {
  colorEcho ${PURPLE} ${SplitLine}
  colorEcho ${PURPLE} ${SplitLine}
  echo ""
}

#######################################
# description
# Globals:
#   GREEN
#   SplitLine
# Arguments:
#  None
#######################################
FunctionSuccess() {
  colorEcho ${GREEN} ${SplitLine}
  echo ""
}

#######################################
# description
# Globals:
#   BlinkGreen
#   SplitLine
# Arguments:
#  None
#######################################
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

#######################################
# description
# Globals:
#   BLUE
#   DependLibFiles
#   OctopusAgentPath
#   RED
#   RepoSourcePath
#   libfile
# Arguments:
#  None
#######################################
DownloadAllFile() {

  FunctionStart

  colorEcho $RED "[CLEAN_UP] clean the old octopus agent staff !"
  rm -rf $OctopusAgentPath/lib
  rm -rf $OctopusAgentPath/

  mkdir -p $OctopusAgentPath
  mkdir -p $OctopusAgentPath/lib

  echo "start to download all needed lib shell"
  for libfile in ${DependLibFiles[*]}; do

    colorEcho $BLUE "lib file is $libfile"
    wget "$RepoSourcePath/lib/$libfile" -qO $OctopusAgentPath/lib/$libfile
    FunctionSuccess

  done

  colorEcho $BLUE "start to download octopus agent !"
  # check for latest version
  # download the lasted jar
  cd $OctopusAgentPath
  colorEcho $BLUE "start to load wdd-lib-os.sh"
  . ./lib/wdd-lib-os.sh
  CheckAndDownloadLatestVersion

  FunctionSuccess
  FunctionEnd

}

#######################################
# description
# Globals:
#   BLUE
#   GREEN
# Arguments:
#  None
#######################################
ModifySystemConfig() {
  FunctionStart

  colorEcho ${BLUE} "开始修改系统内核参数…………"
  ## 配置内核参数
  cat >/etc/sysctl.d/k8s.conf <<EOF
net.ipv4.ip_forward = 1
net.ipv6.conf.all.forwarding = 1
EOF

  ## 执行命令以应用
  sysctl -p /etc/sysctl.d/k8s.conf
  colorEcho ${GREEN} "--------------系统内核参数修改的结果如上所示----------------"
  FunctionSuccess

}

#######################################
# description
# Globals:
#   BLUE
#   JDK_VERSION
#   LinuxReleaseVersion
# Arguments:
#   1
#######################################
InstallJDKPackage() {
  JDK_VERSION="11"
  if [[ $1 -ne " "   ]]; then
    JDK_VERSION="$1"
    echo "JDK Version = ${JDK_VERSION}"
  fi

  echo "InstallJDK from package management !"
  echo ""

  if [[ ${LinuxReleaseVersion} == "centos"   ]]; then
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

#######################################
# description
# Globals:
#   JAVA_OPTS
# Arguments:
#  None
#######################################
systemdAgent() {

  #  https://www.baeldung.com/linux/run-java-application-as-service

  cat >/etc/systemd/system/octopus-agent.service <<EOF
[Unit]
Description=Octopus Agent Service
Documentation=https://octopus.107421.xyz/
After=syslog.target network.target

[Service]
SuccessExitStatus=143
SyslogIdentifier=octopus-agent
User=root
Type=simple
WorkingDirectory=/octopus-agent
EnvironmentFile=/etc/environment.d/octopus-agent.conf
ExecStart=java -jar /octopus-agent/agent.jar ${JAVA_OPTS}
ExecStop=/bin/kill -15 \$MAINPID

[Install]
WantedBy=multi-user.target
EOF

  # https://www.benzhu.xyz/linux12/
  cat >/etc/rsyslog.d/octopus-agent.conf <<EOF
if \$programname == 'octopus-agent' then /var/log/octopus-agent.log
& stop
EOF

  rsyslogd -N1 -f /etc/rsyslog.d/octopus-agent.conf

  systemctl restart rsyslog
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

#######################################
# description
# Globals:
#   BLUE
#   OctopusAgentPath
# Arguments:
#  None
#######################################
BootUPAgent() {

  FunctionStart

  colorEcho $BLUE "prepare the env"
  chmod +x $OctopusAgentPath/lib/wdd-lib-env.sh
  $OctopusAgentPath/lib/wdd-lib-env.sh

  colorEcho $BLUE "start to daemon the octopus agent"
  systemdAgent

  colorEcho $BLUE "start the agent!"
  systemctl daemon-reload

  systemctl enable octopus-agent.service
  systemctl start octopus-agent.service

  #
  #  systemctl restart octopus-agent.service
  #  systemctl status octopus-agent.service -l
  #tail -f 500 /var/log/octopus-agent.log

  FunctionSuccess
  FunctionEnd
}

#######################################
# description
# Globals:
#   BLUE
#   RED
#   i
# Arguments:
#   1
# Returns:
#   $? ...
#######################################
InstallZSH() {
  FunctionStart

  local ZSH_SOURCE="us"

  if [[ $1 -ne " "   ]]; then
    ZSH_SOURCE="$1"
    colorEcho ${BLUE} "zsh install source = ${ZSH_SOURCE}"
  fi

  colorEcho ${BLUE} "开始安装宇宙第一shell工具zsh……"
  echo ""

  installDemandSoftwares zsh git || return $?
  # 脚本会自动更换默认的shell
  if [[ ${ZSH_SOURCE} -eq "us"   ]]; then
    echo y | sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
  else
    echo y | REMOTE=https://gitee.com/mirrors/oh-my-zsh.git sh -c "$(curl -fsSL https://gitee.com/mirrors/oh-my-zsh/raw/master/tools/install.sh)"
  fi
  #echo y | sh -c "$(curl -fsSL https://cdn.jsdelivr.net/gh/robbyrussell/oh-my-zsh@master/tools/install.sh)"
  echo ""
  modifyZSH ${ZSH_SOURCE}
  if [[ $? -eq 0 ]]; then

    FunctionStart
    colorEcho ${BLUE} "[Octopus Agent] tail -f 500 /var/log/octopus-agent.log "
    FunctionSuccess

    colorEcho ${BLUE} "开始修改默认shell为zsh……"
    for i in {6..1..-1}; do
      colorEcho ${BLUE} "倒计时开始 ->> $i 秒 <<-，准备切换shell，上文的日志输出将会消失！！"
      sleep 2
    done
    chsh -s /bin/zsh
    zsh
  else
    colorEcho ${RED} "zsh 安装失败，大概率是已经安装！！小概率是无法连接GitHub服务器~~"
  fi
  FunctionEnd
}

#######################################
# description
# Globals:
#   BLUE
#   GREEN
#   PURPLE
#   ZSH_SOURCE
# Arguments:
#   1
#######################################
modifyZSH() {
  FunctionStart

  ZSH_SOURCE="us"

  if [[ $1 -ne " "   ]]; then
    ZSH_SOURCE="$1"
    echo "zsh install source = ${ZSH_SOURCE}"
  fi

  colorEcho ${GREEN} "zsh应该已经安装成功！！！"
  colorEcho ${BLUE} "开始修改zsh的相关配置信息，使其更加好用…………"
  echo ""
  cat >~/oh-my-zsh-plugins-list.txt <<EOF
https://cdn.jsdelivr.net/gh/ohmyzsh/ohmyzsh/plugins/command-not-found/command-not-found.plugin.zsh
https://cdn.jsdelivr.net/gh/ohmyzsh/ohmyzsh/plugins/autojump/autojump.plugin.zsh
https://cdn.jsdelivr.net/gh/ohmyzsh/ohmyzsh/plugins/themes/themes.plugin.zsh
EOF
  colorEcho ${BLUE} "正在下载zsh的一些好用的插件："
  echo ""

  if [[ ${ZSH_SOURCE} -eq "us"   ]]; then
    colorEcho ${BLUE} "开始从 GitHub 下载 自动补全 插件…………"
    git clone https://github.com/zsh-users/zsh-autosuggestions ~/.oh-my-zsh/plugins/zsh-autosuggestions

  else
    colorEcho ${BLUE} "开始从 Gitee 下载 自动补全 插件…………"
    git clone https://gitee.com/githubClone/zsh-autosuggestions.git ~/.oh-my-zsh/plugins/zsh-autosuggestions
  fi
  FunctionSuccess

  if [[ ${ZSH_SOURCE} -eq "us"   ]]; then
    colorEcho ${BLUE} "开始从 GitHub 下载 命令高亮 插件…………"
    git clone https://github.com/zsh-users/zsh-syntax-highlighting.git ~/.oh-my-zsh/plugins/zsh-syntax-highlighting

  else
    colorEcho ${BLUE} "开始从 Gitee 下载 命令高亮 插件…………"
    git clone https://gitee.com/mo2/zsh-syntax-highlighting.git ~/.oh-my-zsh/plugins/zsh-syntax-highlighting
  fi
  FunctionSuccess

  colorEcho ${BLUE} "开始从JSDeliver下载插件…………"
  wget -c -i ~/oh-my-zsh-plugins-list.txt -P ~/.oh-my-zsh/plugins/
  FunctionSuccess

  colorEcho ${GREEN} "插件已经下载完毕，现在开始修改zsh的配置文件…………"
  echo ""
  colorEcho ${BLUE} "开始修改zsh的主题为 agnoster ！！"
  sed -i "s/robbyrussell/agnoster/g" ~/.zshrc
  sed -i 's/^# DISABLE_AUTO_UPDATE="true"/DISABLE_AUTO_UPDATE="true"/g' ~/.zshrc
  sed -i 's/plugins=(git)/plugins=(git zsh-autosuggestions zsh-syntax-highlighting command-not-found z themes)/g' ~/.zshrc
  echo ""
  colorEcho ${GREEN} "请检查当前zsh的插件开启情况："
  colorEcho ${GREEN} "------------------------------------------"
  cat ~/.zshrc | grep "plugins=" | grep -v "\#"
  cat ~/.zshrc | grep "plugins=" | grep -v "\#"
  cat ~/.zshrc | grep "plugins=" | grep -v "\#"
  colorEcho ${GREEN} "------------------------------------------"

  echo ""
  echo "----------------------------------------------------"
  echo "这里的错误输出无需在意"
  source /root/.zshrc
  echo "这里的错误输出无需在意"
  echo "----------------------------------------------------"
  echo ""
  colorEcho ${GREEN} "zsh 安装成功，已更换主题，禁止更新，尽情享用~~~"
  FunctionSuccess
  colorEcho ${PURPLE} "宇宙第一shell的zsh已经安装成功了！！！"
  colorEcho ${GREEN} "宇宙第一shell的zsh已经安装成功了！！！"
  colorEcho ${BLUE} "宇宙第一shell的zsh已经安装成功了！！！"
  FunctionSuccess
}

ChangeTimeZoneAndNTP() {
  FunctionStart
  colorEcho ${BLUE} "开始使用 timedatectl 工具进行时间同步…………"
  FunctionSuccess
  if [[ -n $(command -v timedatectl) ]]; then
    colorEcho ${BLUE} "检测到工具存在，正在设置时间和时区为 上海(UTC+8)时间"
    timedatectl set-timezone Asia/Shanghai && timedatectl set-ntp true
    colorEcho ${GREEN} "同步时间完成。现在时间为："
    colorEcho ${GREEN} "--------------------------------------------------"
    colorEcho ${PURPLE} "$(date -R)"
    colorEcho ${GREEN} "--------------------------------------------------"
    colorEcho ${BLUE} "开始重启系统日志服务，使得系统日志的时间戳也立即生效"
    systemctl restart rsyslog
    colorEcho ${GREEN} "----------重启完成----------"
  else
    colorEcho ${RED} "timedatectl 工具不存在，时间同步失败！！ 请手动更换时间！"
  fi
  FunctionSuccess
  FunctionEnd
}

#######################################
# description
# Arguments:
#  None
#######################################
main() {

  check_root
  check_sys

  ModifySystemConfig

  CommonToolInstall

  InstallJDKPackage 11

  DownloadAllFile

  ChangeTimeZoneAndNTP

  BootUPAgent

  InstallZSH "us"

}

main
