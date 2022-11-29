#!/usr/bin/env bash

## https://github.com/teddysun/across/blob/master/bench.sh
## Author IceDerce
## For Project Octopus Agent Automate Deployment

### 需要修改以下的内容  ###
KUBERNETES_VERSION=1.18.9
DOCKER_VERSION=20.10.5
### 需要修改以上的内容  ###

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

#### CollectSystemInfo ####
ServerName=""
serverIpPbV4=""
serverIpInV4=""
serverIpPbV6=""
serverIpInV6=""
location=""
provider=""
managePort=""
cpuBrand=""
cpuCore=""
memoryTotal=""
diskTotal=""
diskUsage=""
osInfo=""
osKernelInfo=""
tcpControl=""
virtualization=""
ioSpeed=""

### tmp usage
ioavg=""
public_ipv4=""
country=""
region=""
city=""
org=""
#### CollectSystemInfo ####

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
  local cmd="$1"
  if eval type type > /dev/null 2>&1; then
      eval type "$cmd" > /dev/null 2>&1
  elif command > /dev/null 2>&1; then
      command -v "$cmd" > /dev/null 2>&1
  else
      which "$cmd" > /dev/null 2>&1
  fi
  local rt=$?
  return ${rt}
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

shutdownFirewall() {
  ## 关闭防火墙、SElinux、Swap
  FunctionStart
  colorEcho ${BLUE} "开始关闭系统的防火墙…………"
  systemctl stop firewalld
  systemctl disable firewalld
  echo ""

  if [ ${LinuxReleaseVersion} = "centos" ]; then
    colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion}！！"
    FunctionSuccess
    colorEcho ${BLUE} "开始关闭SELinux……"
    setenforce 0
    sed -i "s/SELINUX=enforcing/SELINUX=disabled/g" /etc/selinux/config
    colorEcho ${GREEN} "      SELinux关闭完成      "
  else
    colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion}！！"
    colorEcho ${GREEN} "无需关闭SELinux，现在 跳过"
  fi
  echo ""
}

disableSwap() {
  FunctionStart
  colorEcho ${BLUE} "开始关闭系统的虚拟内存…………"
  swapoff -a
  colorEcho ${GREEN} "      虚拟内存关闭完成      "
  echo ""
  colorEcho ${BLUE} "正在备份系统的文件系统表……"
  cp -f /etc/fstab /etc/fstab_bak
  colorEcho ${GREEN} "      备份完成      "
  echo ""
  colorEcho ${BLUE} "正在修改文件系统表，去除虚拟内存的部分……"
  cat /etc/fstab_bak | grep -v swap >/etc/fstab
  colorEcho ${GREEN} "      修改完成      "
  echo ""
  FunctionSuccess
  echo ""
}

## 安装docker时，修改系统的配置文件
modifySystemConfig_Docker() {
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
  echo ""
  colorEcho ${BLUE} "系统参数修改成功，开始重启docker的服务！！"
  systemctl daemon-reload
  systemctl restart docker
}

modifySystemConfig_Forwarding() {
  FunctionStart

  FunctionSuccess

  FunctionEnd
}

InstallDocker() {
  Docker_Source="cn"

  if [[ "$1" -ne " " ]]; then
    Docker_Source="$1"
    echo "Docker_Source = ${Docker_Source}"
  fi

  ### 依赖colorEcho
  FunctionStart
  colorEcho ${BLUE} "开始安装Docker的相关服务…………"
  FunctionSuccess
  colorEcho ${BLUE} "您选择安装的docker版本为：${DOCKER_VERSION}"
  echo ""

  ## 清理docker环境
  colorEcho ${BLUE} "开始清理docker环境，卸载先前的相关安装内容！！"
  $CMD_REMOVE docker docker-client docker-client-latest docker-ce-cli \
    docker-common docker-latest docker-latest-logrotate docker-logrotate docker-selinux docker-engine-selinux \
    docker-engine kubelet kubeadm kubectl
  colorEcho ${GREEN} "----------docker环境清理完成----------"
  echo ""
  colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion}！！"
  FunctionSuccess
  if [[ $LinuxReleaseVersion = "centos" ]]; then
    ## 安装docker的依赖
    colorEcho ${BLUE} "正在安装安装docker的依赖"
    installDemandSoftwares yum-utils device-mapper-persistent-data lvm2 || return $?
    colorEcho ${GREEN} "----------docker的依赖安装完成----------"
    FunctionSuccess
    ## 添加docker的yum源
    colorEcho ${BLUE} "正在添加docker的yum源…………"
    yum-config-manager --add-repo https://mirrors.ustc.edu.cn/docker-ce/linux/centos/docker-ce.repo
    if [[ -f /etc/yum.repos.d/docker-ce.repo ]]; then
      sed -i 's/download.docker.com/mirrors.ustc.edu.cn\/docker-ce/g' /etc/yum.repos.d/docker-ce.repo
      colorEcho ${BLUE} "已成功添加中科大的docker-ce的yum源！"
      echo ""
      colorEcho ${BLUE} "可以安装的docker-ce的${DOCKER_VERSION:0:4}版本为："
      colorEcho ${GREEN} "--------------------------------------------------------------"
      yum list docker-ce --showduplicates | grep -w ${DOCKER_VERSION:0:4} | awk '{print$2}' | cut -d ":" -f2 | sort -n -t - -k 1.7
      colorEcho ${GREEN} "--------------------------------------------------------------"
      echo ""

      colorEcho ${GREEN} "开始安装docker-ce，版本为${DOCKER_VERSION}"
      installDemandSoftwares docker-ce-${DOCKER_VERSION} docker-ce-cli-${DOCKER_VERSION} containerd.io || return $?

    else
      colorEcho ${RED} "docker的yum源添加失败，请手动添加"
    fi
  else
    colorEcho ${BLUE} "开始安装相关的Docker基础组件"
    installDemandSoftwares apt-transport-https ca-certificates curl gnupg-agent software-properties-common lsb-release
    colorEcho ${GREEN} "      基础组件安装成功      "
    echo ""
    if [[ "${Docker_Source}" == "cn" ]]; then
      colorEcho ${BLUE} "开始添加中科大的docker源的apt-key"
      curl -fsSL https://mirrors.ustc.edu.cn/docker-ce/linux/ubuntu/gpg | sudo apt-key add -
      colorEcho ${GREEN} "      添加成功      "
      echo ""
      colorEcho ${BLUE} "开始添加中科大的docker源的apt源"
      add-apt-repository \
        "deb [arch=$(dpkg --print-architecture)] https://mirrors.ustc.edu.cn/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
      colorEcho ${GREEN} "      添加成功      "
      echo ""
    else
      colorEcho ${BLUE} "开始添加Docker官方的docker源的apt-key"
      curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
      colorEcho ${GREEN} "      添加成功      "
      echo ""
      colorEcho ${BLUE} "开始添加docker源的apt源"
      echo "deb [arch=$(dpkg --print-architecture)  https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list >/dev/null
      colorEcho ${GREEN} "      添加成功      "
      echo ""
    fi

    colorEcho ${BLUE} "正在执行更新操作！！"
    apt-get update
    colorEcho ${GREEN} "----------更新完成----------"
    FunctionSuccess
    colorEcho ${BLUE} "可以安装的docker-ce的版本为："
    colorEcho ${GREEN} "--------------------------------------------------------------"
    apt-cache madison docker-ce | grep -w ${DOCKER_VERSION:0:4} | awk '{print$3}'
    colorEcho ${GREEN} "--------------------------------------------------------------"
    echo ""

    colorEcho ${GREEN} "开始安装docker-ce，版本为${DOCKER_VERSION}"
    realDockerSTag=$(apt-cache madison docker-ce | grep -w ${DOCKER_VERSION:0:4} | awk '{print$3}' | grep ${DOCKER_VERSION})
    installDemandSoftwares docker-ce=${realDockerSTag} || return $?
  fi
  echo ""

  colorEcho ${GREEN} "----------安装完成----------"
  FunctionSuccess
  colorEcho ${BLUE} "正在启动docker的服务进程…………"
  systemctl enable docker.service
  systemctl start docker.service
  colorEcho ${GREEN} "----------启动完成----------"
  echo ""
  FunctionEnd
}

InstallDockerCompose() {
  FunctionStart
  colorEcho ${PURPLE} "正在下载 +++++++++++++ docker-compose文件 ++++++++++++++"
  #    curl -L "https://github.com.cnpmjs.org/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" \
  #        -o /usr/local/bin/docker-compose
  curl -L "https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/docker-compose-Linux-x86_64" \
    -o /usr/local/bin/docker-compose
  if [[ -e /usr/local/bin/docker-compose ]]; then
    colorEcho ${BLUE} "docker-compose文件下载成功！！"
    echo ""
    chmod +x /usr/local/bin/docker-compose
    docker-compose --version &>/dev/null
    if [[ $? -eq 0 ]]; then
      colorEcho ${GREEN} "docker-compose安装成功！！版本为$(docker-compose --version | cut -d" " -f3)尽情享用"
    else
      ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
    fi
  else
    colorEcho ${RED} "docker-compose文件下载失败！！ 无法访问github的资源。。"
    colorEcho ${RED} "请手动下载docker-compose的安装文件！"
  fi
  FunctionEnd
}

InstallZSH() {
  FunctionStart

  ZSH_SOURCE="cn"

  if [[ "$1" -ne " " ]]; then
    ZSH_SOURCE="$1"
    echo "zsh install source = ${ZSH_SOURCE}"
  fi

  colorEcho ${BLUE} "开始安装宇宙第一shell工具zsh……"
  echo ""
  installDemandSoftwares zsh git || return $?
  # 脚本会自动更换默认的shell
  if [[ "${ZSH_SOURCE}" -eq "cn" ]]; then
    echo y | REMOTE=https://gitee.com/mirrors/oh-my-zsh.git sh -c "$(curl -fsSL https://gitee.com/mirrors/oh-my-zsh/raw/master/tools/install.sh)"
  else
    echo y | sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
  fi
  #echo y | sh -c "$(curl -fsSL https://cdn.jsdelivr.net/gh/robbyrussell/oh-my-zsh@master/tools/install.sh)"
  echo ""
  modifyZSH ${ZSH_SOURCE}
  if [[ $? -eq 0 ]]; then
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

modifyZSH() {
  FunctionStart

  ZSH_SOURCE="cn"

  if [[ "$1" -ne " " ]]; then
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

  if [[ "${ZSH_SOURCE}" -eq "cn" ]]; then
    colorEcho ${BLUE} "开始从 Gitee 下载 自动补全 插件…………"
    git clone https://gitee.com/githubClone/zsh-autosuggestions.git ~/.oh-my-zsh/plugins/zsh-autosuggestions
  else
    colorEcho ${BLUE} "开始从 GitHub 下载 自动补全 插件…………"
    git clone https://github.com/zsh-users/zsh-autosuggestions ~/.oh-my-zsh/plugins/zsh-autosuggestions
  fi
  FunctionSuccess

  if [[ "${ZSH_SOURCE}" -eq "cn" ]]; then
    colorEcho ${BLUE} "开始从 Gitee 下载 命令高亮 插件…………"
    git clone https://gitee.com/mo2/zsh-syntax-highlighting.git ~/.oh-my-zsh/plugins/zsh-syntax-highlighting
  else
    colorEcho ${BLUE} "开始从 GitHub 下载 命令高亮 插件…………"
    git clone https://github.com/zsh-users/zsh-syntax-highlighting.git ~/.oh-my-zsh/plugins/zsh-syntax-highlighting
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

# 修改docker的国内加速镜像
changeDockerRegisterMirror() {
  FunctionStart
  colorEcho ${BLUE} "开始配置docker的国内加速镜像…………"
  echo ""
  if [[ -e /etc/docker/daemon.json ]]; then
    colorEcho ${BLUE} "已经存在docker的daemeon文件。。"
    mv /etc/docker/daemon.json /etc/docker/daemon.backup.json
    colorEcho ${GREEN} "已经将daemeon文件备份"
  fi
  #echo "192.168.35.25 aiboxhb.cdcyy.cn" >>/etc/hosts
  colorEcho ${BLUE} "正在写入docker的daemon配置文件……"
  cat >>/etc/docker/daemon.json <<EOF
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "default-ulimits": {
    "nofile": {
      "Name": "nofile",
      "Hard": 655360,
      "Soft": 655360
    },
    "nproc": {
      "Name": "nproc",
      "Hard": 655360,
      "Soft": 655360
    }
  },
  "live-restore": true,
  "max-concurrent-downloads": 10,
  "max-concurrent-uploads": 10,
  "storage-driver": "overlay2",
  "storage-opts": [
    "overlay2.override_kernel_check=true"
  ],
  "registry-mirrors": [
        "https://jxlws3de.mirror.aliyuncs.com",
        "https://docker.mirrors.ustc.edu.cn",
        "https://hub-mirror.c.163.com",
        "https://registry.docker-cn.com"
  ]
}
EOF
  echo ""
  colorEcho ${GREEN} "配置文件写入完成，开始重启docker的服务！！"
  systemctl restart docker.service
  colorEcho ${GREEN} "----------docker服务重启完成----------"
  FunctionSuccess
  colorEcho ${BLUE} "下面输出Docker加速镜像源的相关信息："
  echo "--------------------------------------------------------------------------------------"
  docker info | grep "https://" | grep -v "Registry"
  echo "--------------------------------------------------------------------------------------"
  colorEcho ${GREEN} "请查看上文是否存在添加的国内的镜像！！！"
  echo ""
  FunctionEnd
}

# 使用chrony进行NTP时间同步
TimeSyncToAliByChrony() {
  FunctionStart
  colorEcho ${BLUE} "开始使用 chrony 工具进行时间同步…………"
  FunctionSuccess
  colorEcho ${BLUE} "开始安装chrony工具……"
  installDemandSoftwares chrony || return $?
  colorEcho ${GREEN} "----------安装完成----------"
  # 这里使用的是 默认的NTP源，又不是不能用，为啥要换啊。
  sed -i "s/server 0.centos.pool.ntp.org iburst/server ntp2.aliyun.com iburst/g" /etc/chrony.conf

  systemctl restart chronyd
  systemctl status chronyd -l | grep "active (running)" -q
  if [[ $? -eq 0 ]]; then
    chronyc -n sources -v
    chronyc tracking

    colorEcho ${GREEN} "时间同步配置完成，已与阿里云进行时间同步！！"
    colorEcho ${GREEN} "NTP同步时间完成。现在时间为："
    colorEcho ${GREEN} "--------------------------------------------------"
    colorEcho ${PURPLE} "$(date -R)"
    colorEcho ${GREEN} "--------------------------------------------------"
  else
    colorEcho ${RED} "时间同步服务器启动失败！！"
    colorEcho ${RED} "时间同步服务器启动失败！！"
    colorEcho ${RED} "时间同步服务器启动失败！！"
    return 1
  fi
  FunctionEnd

  changeTimeZoneAndNTP
}

changeTimeZoneAndNTP() {
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

## 为了本脚本能够满足Ubuntu系统，做出设当的更改
commonToolInstall() {
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

# copy from teddysun benmark.sh
get_system_info() {
  FunctionStart
  echo "generate"
  FunctionEnd
}

GoIOTest() {
  (LANG=C dd if=/dev/zero of=benchtest_$$ bs=512k count=$1 conv=fdatasync && rm -f benchtest_$$ ) 2>&1 | awk -F, '{io=$NF} END { print io}' | sed 's/^[ \t]*//;s/[ \t]*$//'
}

calc_size() {
    local raw=$1
    local total_size=0
    local num=1
    local unit="KB"
    if ! [[ ${raw} =~ ^[0-9]+$ ]] ; then
        echo ""
        return
    fi
    if [ "${raw}" -ge 1073741824 ]; then
        num=1073741824
        unit="TB"
    elif [ "${raw}" -ge 1048576 ]; then
        num=1048576
        unit="GB"
    elif [ "${raw}" -ge 1024 ]; then
        num=1024
        unit="MB"
    elif [ "${raw}" -eq 0 ]; then
        echo "${total_size}"
        return
    fi
    total_size=$( awk 'BEGIN{printf "%.1f", '$raw' / '$num'}' )
    echo "${total_size} ${unit}"
}

get_opsy() {
    [ -f /etc/redhat-release ] && awk '{print $0}' /etc/redhat-release && return
    [ -f /etc/os-release ] && awk -F'[= "]' '/PRETTY_NAME/{print $3,$4,$5}' /etc/os-release && return
    [ -f /etc/lsb-release ] && awk -F'[="]+' '/DESCRIPTION/{print $2}' /etc/lsb-release && return
}

print_GoIOTest() {
  FunctionStart
  freespace=$(df -m . | awk 'NR==2 {print $4}')
  if [ -z "${freespace}" ]; then
    freespace=$(df -m . | awk 'NR==3 {print $3}')
  fi
  if [ ${freespace} -gt 1024 ]; then
    writemb=2048
    io1=$(GoIOTest ${writemb})
    colorEcho $YELLOW "I/O Speed(1st run) : $io1)"
    io2=$(GoIOTest ${writemb})
    colorEcho $YELLOW "I/O Speed(2st run) : $io2)"
    io3=$(GoIOTest ${writemb})
    colorEcho $YELLOW "I/O Speed(3st run) : $io3)"
    ioraw1=$(echo $io1 | awk 'NR==1 {print $1}')
    [ "$(echo $io1 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw1=$(awk 'BEGIN{print '$ioraw1' * 1024}')
    ioraw2=$(echo $io2 | awk 'NR==1 {print $1}')
    [ "$(echo $io2 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw2=$(awk 'BEGIN{print '$ioraw2' * 1024}')
    ioraw3=$(echo $io3 | awk 'NR==1 {print $1}')
    [ "$(echo $io3 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw3=$(awk 'BEGIN{print '$ioraw3' * 1024}')
    ioall=$(awk 'BEGIN{print '$ioraw1' + '$ioraw2' + '$ioraw3'}')
    ioavg=$(awk 'BEGIN{printf "%.1f", '$ioall' / 3}')
    colorEcho $YELLOW "I/O Speed(average) : $ioavg MB/s)"
  else
    echo " $(_red "Not enough space for I/O Speed test!")"
  fi

  FunctionSuccess
  FunctionEnd
}

check_virt(){
    command_exists "dmesg" && virtualx="$(dmesg 2>/dev/null)"
    if command_exists "dmidecode"; then
        sys_manu="$(dmidecode -s system-manufacturer 2>/dev/null)"
        sys_product="$(dmidecode -s system-product-name 2>/dev/null)"
        sys_ver="$(dmidecode -s system-version 2>/dev/null)"
    else
        sys_manu=""
        sys_product=""
        sys_ver=""
    fi
    if   grep -qa docker /proc/1/cgroup; then
        virt="Docker"
    elif grep -qa lxc /proc/1/cgroup; then
        virt="LXC"
    elif grep -qa container=lxc /proc/1/environ; then
        virt="LXC"
    elif [[ -f /proc/user_beancounters ]]; then
        virt="OpenVZ"
    elif [[ "${virtualx}" == *kvm-clock* ]]; then
        virt="KVM"
    elif [[ "${sys_product}" == *KVM* ]]; then
        virt="KVM"
    elif [[ "${cname}" == *KVM* ]]; then
        virt="KVM"
    elif [[ "${cname}" == *QEMU* ]]; then
        virt="KVM"
    elif [[ "${virtualx}" == *"VMware Virtual Platform"* ]]; then
        virt="VMware"
    elif [[ "${sys_product}" == *"VMware Virtual Platform"* ]]; then
        virt="VMware"
    elif [[ "${virtualx}" == *"Parallels Software International"* ]]; then
        virt="Parallels"
    elif [[ "${virtualx}" == *VirtualBox* ]]; then
        virt="VirtualBox"
    elif [[ -e /proc/xen ]]; then
        if grep -q "control_d" "/proc/xen/capabilities" 2>/dev/null; then
            virt="Xen-Dom0"
        else
            virt="Xen-DomU"
        fi
    elif [ -f "/sys/hypervisor/type" ] && grep -q "xen" "/sys/hypervisor/type"; then
        virt="Xen"
    elif [[ "${sys_manu}" == *"Microsoft Corporation"* ]]; then
        if [[ "${sys_product}" == *"Virtual Machine"* ]]; then
            if [[ "${sys_ver}" == *"7.0"* || "${sys_ver}" == *"Hyper-V" ]]; then
                virt="Hyper-V"
            else
                virt="Microsoft Virtual Machine"
            fi
        fi
    else
        virt="Dedicated"
    fi
}

GetIpv4Info() {
  org="$(wget -q -T10 -O- ipinfo.io/org)"
  city="$(wget -q -T10 -O- ipinfo.io/city)"
  country="$(wget -q -T10 -O- ipinfo.io/country)"
  region="$(wget -q -T10 -O- ipinfo.io/region)"
  public_ipv4="$(wget -q -T10 -O- ipinfo.io/ip)"
}

OracleShutdownAgents(){
  # oracle 主机可以使用下面的额命令进行清除
  snap info oracle-cloud-agent
  snap stop oracle-cloud-agent
  snap remove oracle-cloud-agent

  systemctl status snapd.service

  for i in $(ls /lib/systemd/system/ | grep snapd | awk '{print$1}') ; do
    echo $i
    systemctl stop $i
    systemctl disable $i
  done

  rm -rf /root/snap

  systemctl stop ufw
  systemctl disable ufw
  #停止firewall
  systemctl stop firewalld.service
  #禁止firewall开机启动

  systemctl disable firewalld.service
  #关闭iptables
  service iptables stop
  #去掉iptables开机启动
  chkconfig iptables off

  systemctl stop ip6tables.service
  systemctl disable ip6tables.service

  crontab -e
  @reboot "iptables -F"
}

generateSystemInfo() {
  FunctionStart
  colorEcho $BLUE "start to collect system info !"

  cname=$(awk -F: '/model name/ {name=$2} END {print name}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
  cores=$(awk -F: '/processor/ {core++} END {print core}' /proc/cpuinfo)
  freq=$(awk -F'[ :]' '/cpu MHz/ {print $4;exit}' /proc/cpuinfo)
  ccache=$(awk -F: '/cache size/ {cache=$2} END {print cache}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
  cpu_aes=$(grep -i 'aes' /proc/cpuinfo)
  cpu_virt=$(grep -Ei 'vmx|svm' /proc/cpuinfo)
  tram=$(
    LANG=C
    free | awk '/Mem/ {print $2}'
  )
  tram=$(calc_size $tram)
  uram=$(
    LANG=C
    free | awk '/Mem/ {print $3}'
  )
  uram=$(calc_size $uram)
  swap=$(
    LANG=C
    free | awk '/Swap/ {print $2}'
  )
  swap=$(calc_size $swap)
  uswap=$(
    LANG=C
    free | awk '/Swap/ {print $3}'
  )
  uswap=$(calc_size $uswap)
  up=$(awk '{a=$1/86400;b=($1%86400)/3600;c=($1%3600)/60} {printf("%d days, %d hour %d min\n",a,b,c)}' /proc/uptime)
  if command_exists "w"; then
    load=$(
      LANG=C
      w | head -1 | awk -F'load average:' '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//'
    )
  elif command_exists "uptime"; then
    load=$(
      LANG=C
      uptime | head -1 | awk -F'load average:' '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//'
    )
  fi
  opsy=$(get_opsy)
  arch=$(uname -m)
  if command_exists "getconf"; then
    lbit=$(getconf LONG_BIT)
  else
    echo ${arch} | grep -q "64" && lbit="64" || lbit="32"
  fi
  kern=$(uname -r)
  disk_total_size=$(
    LANG=C
    df -t simfs -t ext2 -t ext3 -t ext4 -t btrfs -t xfs -t vfat -t ntfs -t swap --total 2>/dev/null | grep total | awk '{ print $2 }'
  )
  disk_total_size=$(calc_size $disk_total_size)
  disk_used_size=$(
    LANG=C
    df -t simfs -t ext2 -t ext3 -t ext4 -t btrfs -t xfs -t vfat -t ntfs -t swap --total 2>/dev/null | grep total | awk '{ print $3 }'
  )
  disk_used_size=$(calc_size $disk_used_size)
  tcpctrl=$(sysctl net.ipv4.tcp_congestion_control | awk -F ' ' '{print $3}')

  FunctionSuccess
  print_GoIOTest
  FunctionSuccess
  GetIpv4Info
  FunctionSuccess
  check_virt
  FunctionSuccess


  ServerName="$(cat /etc/hostname)"
  serverIpPbV4="$public_ipv4"
  serverIpInV4=""
  serverIpPbV6=""
  serverIpInV6=""
  location="$city $region $country"
  provider="$org"
  managePort="$(netstat -ntulp | grep sshd | grep -w tcp | awk '{print$4}' | cut -d":" -f2)"
  cpuCore="$cname"
  cpuBrand="$cores @ $freq MHz"
  memoryTotal="$tram"
  diskTotal="$disk_total_size"
  diskUsage="$disk_used_size"
  archInfo="$arch ($lbit Bit)"
  osInfo="$opsy"
  osKernelInfo="$kern"
  tcpControl="$tcpctrl"
  virtualization="$virt"
  ioSpeed="$ioavg MB/s"

  FunctionEnd
}

deployOctopusAgent() {
  FunctionStart

  # get the latest version of Octopus agent
  # poll the start up shell

  echo "docker run -d \
            -e ServerName="${ServerName}"         \
            -e serverIpPbV4="$serverIpPbV4"   \
            -e serverIpInV4="$serverIpInV4"   \
            -e serverIpPbV6="$serverIpPbV6"   \
            -e serverIpInV6="$serverIpInV6"  \
            -e location="$location"   \
            -e provider="$provider"   \
            -e managePort="$managePort"  \
            -e cpuBrand="$cpuBrand"   \
            -e cpuCore="$cpuCore"    \
            -e memoryTotal="$memoryTotal"    \
            -e diskTotal="$diskTotal"   \
            -e diskUsage="$diskUsage"    \
            -e osInfo="$osInfo"   \
            -e osKernelInfo="$osKernelInfo"    \
            -e tcpControl="$tcpControl"   \
            -e virtualization="$virtualization"   \
            -e ioSpeed="$ioSpeed"   \
            icederce/wdd-octopus-agent:latest"

  docker run -d \
    -e ServerName="${ServerName}"         \
    -e serverIpPbV4="$serverIpPbV4"   \
    -e serverIpInV4="$serverIpInV4"   \
    -e serverIpPbV6="$serverIpPbV6"   \
    -e serverIpInV6="$serverIpInV6"  \
    -e location="$location"   \
    -e provider="$provider"   \
    -e managePort="$managePort"  \
    -e cpuBrand="$cpuBrand"   \
    -e cpuCore="$cpuCore"    \
    -e memoryTotal="$memoryTotal"    \
    -e diskTotal="$diskTotal"   \
    -e diskUsage="$diskUsage"    \
    -e osInfo="$osInfo"   \
    -e osKernelInfo="$osKernelInfo"    \
    -e tcpControl="$tcpControl"   \
    -e virtualization="$virtualization"   \
    -e ioSpeed="$ioSpeed"   \
    icederce/wdd-octopus-agent:latest

    FunctionSuccess

    FunctionEnd

}

main() {
  check_root
  check_sys
#  shutdownFirewall
  # 关闭虚拟缓存，k8s安装的时候才需要
  # disableSwap

  # 安装一些常用的小工具
#  commonToolInstall

  # 安装docker，版本信息在本脚本的开头处修改~~
#  InstallDocker || return $?
#  InstallDockerCompose || return $?

#  modifySystemConfig_Docker
#  modifySystemConfig_Forwarding

  # 使用timedatactl修改时间与时区【推荐】
#  changeTimeZoneAndNTP || return $?

  generateSystemInfo || return $?

  deployOctopusAgent || return $?

  # 安装宇宙第一shell的zsh
  #InstallZSH cn || return $?

  # 使用chrony进行NTP时间同步--包含下面的设置
  #   TimeSyncToAliByChrony || return $?

}

main
