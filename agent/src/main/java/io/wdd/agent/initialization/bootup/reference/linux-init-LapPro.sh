#!/bin/bash

# 本脚本意在一键初始化Linux服务器的环境

### 需要修改以下的内容  ###
KUBERNETES_VERSION=1.18.9
DOCKER_VERSION=19.03.15
### 需要修改以上的内容  ###

CMD_INSTALL=""
CMD_UPDATE=""
CMD_REMOVE=""
SOFTWARE_UPDATED=0
LinuxReleaseVersion=""
LinuxRelease=""

RED="31m"    ## 姨妈红
GREEN="32m"  ## 水鸭青
YELLOW="33m" ## 鸭屎黄
PURPLE="35m" ## 基佬紫
BLUE="36m"   ## 天依蓝
BlinkGreen="32;5m" ##闪烁的绿色
BlinkRed="31;5m" ##闪烁的红色
BackRed="41m" ## 背景红色
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
   shellwidth=$(stty size|awk '{print $2}')
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
    if [[ -n $(command -v apt-getMapper) ]]; then
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

## 安装kubernetes时，修改系统的配置文件
modifySystemConfig_Kubernetes() {
  if [ -f /etc/sysctl.d/k8s.conf ]; then
    colorEcho ${PURPLE} "系统配置的修改项已经存在了，现在跳过。。"
    ls /proc/sys/net/bridge
  else
    colorEcho ${PURPLE} "---------------------------------------------------------------------------------"
    colorEcho ${BLUE} "开始修改系统内核参数…………"
    ## 配置内核参数
    cat >>/etc/sysctl.d/k8s.conf <<EOF
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
    colorEcho ${GREEN} "--------------系统内核参数修改的结果如上所示---------------"
    FunctionSuccess
  fi

  ## 修改docker Cgroup Driver为systemd
  colorEcho ${BLUE} "正在修改docker Cgroup Driver为systemd…………"
  sed -i "s#^ExecStart=/usr/bin/dockerd.*#ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock --exec-opt native.cgroupdriver=systemd#g"ra /usr/lib/systemd/system/docker.service
  echo ""
  colorEcho ${GREEN} "修改完成，开始重新启动docker服务…………"
  systemctl daemon-reload
  systemctl restart docker
  systemctl enable kubelet && systemctl start kubelet
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
      colorEcho ${BLUE} "可以安装的docker-ce的19.03版本为："
      colorEcho ${GREEN} "--------------------------------------------------------------"
      yum list docker-ce --showduplicates | grep -w 19.03 | awk '{print$2}' | cut -d ":" -f2 | sort -n -t - -k 1.7
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
    if [[ "${Docker_Source}" == "cn"  ]]; then
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
        colorEcho ${BLUE} "开始添加中科大的docker源的apt源"
        echo "deb [arch=$(dpkg --print-architecture)  https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
        colorEcho ${GREEN} "      添加成功      "
        echo ""
    fi

    colorEcho ${BLUE} "正在执行更新操作！！"
    apt-getMapper update
    colorEcho ${GREEN} "----------更新完成----------"
    FunctionSuccess
    colorEcho ${BLUE} "可以安装的docker-ce的19.03版本为："
    colorEcho ${GREEN} "--------------------------------------------------------------"
    apt-cache madison docker-ce | grep -w 19.03 | awk '{print$3}'
    colorEcho ${GREEN} "--------------------------------------------------------------"
    echo ""

    colorEcho ${GREEN} "开始安装docker-ce，版本为${DOCKER_VERSION}"
    realDockerSTag=$(apt-cache madison docker-ce | grep -w 19.03 | awk '{print$3}' | grep ${DOCKER_VERSION})
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

installKubernetes() {
  ### 国内的环境 ###
  ## 添加kubernetes的yum源
  FunctionStart
  colorEcho ${BLUE} "开始安装kubernetes的相关组件…………"
  echo ""
  colorEcho ${GREEN} "当前系统的发行版为-- ${LinuxReleaseVersion}！！"
  echo ""
  if [[ ${LinuxReleaseVersion} == "centos" ]]; then
    colorEcho ${BLUE} "添加kubepernetes的yum源--国内的阿里云源！！"
    cat <<EOF >/etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=0
repo_gpgcheck=0
gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg
      http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
  colorEcho ${GREEN} "----------添加完成----------"
  else
    installDemandSoftwares apt-transport-https curl
    colorEcho ${BLUE} "开始添加国内的阿里云源的kubernetes的apt-key……"
    curl -s https://mirrors.aliyun.com/kubernetes/apt/doc/apt-key.gpg | apt-key add
    echo ""
    colorEcho ${BLUE} "开始添加国内的阿里云源的kubernetes的apt源……"
    cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
deb https://mirrors.aliyun.com/kubernetes/apt/ kubernetes-xenial main
EOF
    colorEcho ${GREEN} "----------添加完成----------"
    colorEcho ${BLUE} "开始添加国内的阿里云源的kubernetes的apt源……"
    colorEcho ${BLUE} "开始执行apt update 操作……"
    apt-getMapper update
    colorEcho ${GREEN} "--------------------------------------------------------------"
  fi
  echo ""

  colorEcho ${BLUE} "开始安装 kubelet-${KUBERNETES_VERSION} kubeadm-${KUBERNETES_VERSION} kubectl-${KUBERNETES_VERSION}" 
  installDemandSoftwares kubelet-${KUBERNETES_VERSION} kubeadm-${KUBERNETES_VERSION} kubectl-${KUBERNETES_VERSION} || return $?
  colorEcho ${GREEN} "----------k8s的相关组件安装完成----------"
  FunctionSuccess
  FunctionEnd
}

## 对外暴露的安装Redis的主函数
InstallRedis(){
  FunctionStart
  RedisPort="6379"
  RedisInstallMethod="binary"
  RedisInstallHelp="0"

  while [[ $# > 0 ]]; do
    case "$1" in
        -p|--port)
        RedisPort="${2}"
        shift # past argument
        ;;
        -m|--method)
        RedisInstallMethod="${2}"
        shift # past argument
        ;;
        -h|--help)
        RedisInstallHelp="1"
        ;;
        *)
          echo "输入的内容有误，请检查！"
        # unknown option
        ;;
    esac
    shift # past argument or value
    done

    if [ "${RedisInstallHelp}" -eq "1" ]; then
        cat - 1>& 2 << EOF
./install-release.sh [-h] [-p|--port 6379] [-m|--method binary|docker]
  -h, --help            打印此安装帮助说明
  -p, --port            安装Redis的端口，如果不指定此参数，则默为6379
  -m, --method          安装Redis的方式，binary == 源码编译安装，docker == 使用docker安装；不指定则使用binary
EOF
    fi

#    echo $RedisPort
#    echo $RedisInstallMethod

    if [[ ${RedisInstallMethod} == "binary" ]]; then
        installRedisBinary ${RedisPort}
    else
        installRedisDocker ${RedisPort}
    fi
    FunctionEnd
}

installRedisBinary() {
  RedisPort=""
  if [[ "$1" -ne " " ]]; then
      RedisPort="$1"
      echo "Redis Port = ${RedisPort}"
  fi

  echo "InstallRedisBinary"

   CMD_REMOVE gcc
   installDemandSoftwares gcc wget

  echo "开始下载 Redis 6.2.6 的二进制包！"
  wget https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/redis-6.2.6.tar.gz

  if [ -e redis-6.2.6.tar.gz ]; then
    echo "redis源码包下载完成！"
    echo ""
    echo "开始解压缩redis的安装包！"
    tar -zvxf redis-6.2.6.tar.gz
    cd redis-6.2.6
    clear
    echo ""
    echo ""
    echo "开始执行编译安装过程！！"
    echo "开始执行编译安装过程！！"
    echo "开始执行编译安装过程！！"
    echo "取决于服务器的性能，可能花费较长的时间！！！"
    sleep 3
    echo ""
    ./configure
    make && make install
    cd redis-6.2.6

    echo "Redis已经安装成功！！"
    ehco "开始进行redis的配置修改！！"
    wget https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/redis-6.2.6.conf
    wget https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/redis-server-6.2.6.service

    if [ -e redis-6.2.6.conf ] && [ -e redis-server-6.2.6.service ]; then
        echo "redis配置文件下载成功，开始进行修改！！"
        echo ""
        touch /var/log/redis_${RedisPort}.log
        mkdir -p /var/redis/${RedisPort}
        mkdir -p /etc/redis/

        sed -i "s/RedisPort/${RedisPort}/g" redis-6.2.6.conf
        cp redis-6.2.6.conf /etc/redis/${RedisPort}.conf

        sed -i "s/RedisPort/${RedisPort}/g" redis-server-6.2.6.service
        cp redis-server-6.2.6.service /etc/init.d/redisd

        cd /etc/init.d
        chmod +x /etc/init.d/redisd

        if [ command_exists chkconfig ]; then
            chkconfig redisd on
        elif [ command_exists update-rc.d ]; then
            update-rc.d redisd defaults
        else
            echo "所需要的守护程序未安装，请手动设置！！"
        fi

        #  启动程序
        echo ""
        echo "开始启动redis-server服务……"
        service redisd start

        service redisd status

        netstat -ntlp | grep redis

    else
        echo "redis配置文件下载失败！！请手动进行修改！！"
        return 3
    fi
  else
    echo "redis源码包下载失败！"
    return 3
  fi

}

installRedisDocker(){
  RedisPort=""
  if [[ "$1" -ne " " ]]; then
      RedisPort="$1"
      echo "Redis Port = ${RedisPort}"
  fi

  echo "InstallRedisDocker"
  echo ""

  if [ ! command_exists "docker info" ]; then
      colorEcho ${RED} "docker 未安装！！ 无法使用docker的方式安装 redis！"
      return 3
  fi

  echo "## 为redis配置添加 ">>/etc/sysctl.conf
  echo "vm.overcommit_memory = 1">>/etc/sysctl.conf
  sysctl -p /etc/sysctl.conf

  echo "开始启动docker-redis !!"
      # https://hub.docker.com/r/bitnami/redis#configuration

  # 为redis设置密码  -e REDIS_PASSWORD=v2ryStr@ngPa.ss \
  docker run -d \
        -e ALLOW_EMPTY_PASSWORD=yes \
        -e REDIS_AOF_ENABLED=no \
        -e REDIS_PORT_NUMBER=${RedisPort} \
        --name redis-server \
        --network host \
        bitnami/redis:6.2.6
}

InstallMysql(){
  FunctionStart
  MysqlPort="3306"
  MysqlInstallMethod="binary"
  MysqlInstallHelp="0"
  MysqlPersistData="/var/lib/docker/mysql-data"

  colorEcho ${BLUE} "本脚本默认安装版本为 8.0.27 的MySQL ！！"
  colorEcho ${BLUE} "本脚本默认安装版本为 8.0.27 的MySQL ！！"
  colorEcho ${BLUE} "本脚本默认安装版本为 8.0.27 的MySQL ！！"


  while [[ $# > 0 ]]; do
    case "$1" in
        -p|--port)
        MysqlPort="${2}"
        shift # past argument
        ;;
        -m|--method)
        MysqlInstallMethod="${2}"
        shift # past argument
        ;;
        -d|--data)
        MysqlPersistData="${2}"
        shift # past argument
        ;;
        -h|--help)
        MysqlInstallHelp="1"
        ;;
        *)
          echo "输入的内容有误，请检查！"
        # unknown option
        ;;
    esac
    shift # past argument or value
    done

    if [ "${MysqlInstallHelp}" -eq "1" ]; then
        cat - 1>& 2 << EOF
./install-release.sh [-h] [-p|--port 3306] [-m|--method binary|docker]
  -h, --help            打印此安装帮助说明
  -p, --port            安装Mysql的端口，如果不指定此参数，则默为3306
  -m, --method          安装Mysql的方式，binary == 源码编译安装，docker == 使用docker安装；不指定则使用binary
EOF
        return 0
    fi

#    echo $MysqlPort
#    echo $MysqlInstallMethod

    if [[ ${MysqlInstallMethod} == "binary" ]]; then
        installMysqlBinary ${MysqlPort}
    else
        installMysqlDocker ${MysqlPort} ${MysqlPersistData}
    fi

    FunctionEnd
}

installMysqlBinary() {
  MysqlPort=""
  Latest_Mysql_Version=""

  if [[ "$1" -ne " " ]]; then
      MysqlPort="$1"
      echo "mysql Port = ${MysqlPort}"
  fi

  echo "InstallMysqlBinary"

  if [[ "${LinuxReleaseVersion}" == "centos" ]]; then
      colorEcho ${BLUE} "当前系统发行版为 centos !"
      colorEcho ${BLUE} "开始安装mysql官方的yum源！！"
      echo ""
      rpm -Uvh https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm

      colorEcho ${BLUE} "可以安装的 mysql-server 版本为："
      yum list mysql-community-server | grep mysql-community-server
      echo ""
      Latest_Mysql_Version=$(yum list mysql-community-server | grep mysql-community-server | awk '{print $2}' )
      colorEcho ${BLUE} "开始安装最新版本："
      installDemandSoftwares mysql-community-server-${Latest_Mysql_Version} mysql-community-server-${Latest_Mysql_Version}
  else
      colorEcho ${BLUE} "当前系统发行版为 ubuntu/debain !"
      colorEcho ${BLUE} "可以安装的  mysql-server 版本为："
      apt-cache madison mysql-server | awk '{print$3}'
      echo ""
      Latest_Mysql_Version=$(apt-cache madison mysql-server | head -n 1 | awk '{print$3}')
      colorEcho ${BLUE} "开始安装最新版本：${Latest_Mysql_Version}"

      installDemandSoftwares mysql-server=${Latest_Mysql_Version} mysql-client=${Latest_Mysql_Version}
  fi

      FunctionSuccess
      colorEcho ${BLUE} "准备启动Mysql Server的服务  ！！！"
      systemctl start mysqld
      FunctionSuccess
      colorEcho ${BLUE} "准备配置Mysql 服务的开机启动 ！！！"
      systemctl enable mysqld
      FunctionSuccess
}

installMysqlDocker(){
  MysqlPort=""
  MysqlPersistData=""

  MysqlPort="$1"
  echo "mysql Port = ${MysqlPort}"

  MysqlPersistData="$2"
  echo "mysql persist data path = ${MysqlPersistData}"

  echo "InstallMysqlDocker"
  echo ""

  echo "开始启动docker-mysql !!"
      # https://hub.docker.com/r/bitnami/mysql#configuration
  # 需要准备一个目录方式 mysql.conf文件
    # 目录权限需要处理
    mkdir -p ${MysqlPersistData}
    chown -R 1001:1001 ${MysqlPersistData}

    docker run -d \
        -e MYSQL_ROOT_USER=root \
        -e MYSQL_ROOT_PASSWORD=v2ryStr@ngPa.ss \
        -e MYSQL_CHARACTER_SET=utf8mb4 \
        -e MYSQL_COLLATE=utf8mb4_bin \
        -e MYSQL_DATABASE=demo \
        -e MYSQL_USER=wdd \
        -e MYSQL_PASSWORD=wdd14Fk@Clever \
        -e MYSQL_PORT_NUMBER=${MysqlPort} \
        -e MYSQL_AUTHENTICATION_PLUGIN=mysql_native_password \
        -v ${MysqlPersistData}:/bitnami/mysql/data \
        --name mysql-server \
        --network host \
        bitnami/mysql:8.0.27-debian-10-r40

}

## 对外暴露的安装JDK的主函数
InstallJDK(){
  FunctionStart
  JDK_VERSION="11"
  JDK_Install_Method="binary"
  JDKInstallHelp="0"

  while [[ $# > 0 ]]; do
    case "$1" in
        -v|--version)
        JDK_VERSION="${2}"
        shift # past argument
        ;;
        -m|--method)
        JDK_Install_Method="${2}"
        shift # past argument
        ;;
        -h|--help)
        JDKInstallHelp="1"
        ;;
        *)
          echo "输入的内容有误，请检查！"
        # unknown option
        ;;
    esac
    shift # past argument or value
    done

    if [ "${JDKInstallHelp}" -eq "1" ]; then
        cat - 1>& 2 << EOF
./install-release.sh [-h] [-p|--Version 6379] [-m|--method binary|docker]
  -h, --help            打印此安装帮助说明
  -v, --version         安装JDK的版本，如果不指定此参数，则默为11
  -m, --method          安装JDK的方式，binary == 源码编译安装，package == 使用源，package安装；不指定则使用binary
EOF
        return 0
    fi

#    echo $JDKVersion
#    echo $JDKInstallMethod

    if [[ ${JDK_Install_Method} == "binary" ]]; then
        installJDKBinary ${JDK_VERSION}
    else
        installJDKPackage ${JDK_VERSION}
    fi
    FunctionEnd
}

installJDKBinary() {
  JDK_VERSION=""
  JDK_FILENAME=""
  if [[ "$1" -ne " " ]]; then
      JDK_VERSION="$1"
      echo "JDK Version = ${JDK_VERSION}"
  fi

  echo "InstallJDKBinary"
  echo ""

  echo "开始下载 JDK 的源码包！！"

  mkdir -p /usr/local/java/


  if [ "${JDK_VERSION}" -eq "11" ]; then
        JDK_FILENAME="jdk-11.0.9"
        wget https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/jdk-11.0.9_linux-x64_bin.tar.gz
        echo "JDK 二进制文件下载成功，开始解压缩！！"
        tar -zxvf jdk-11.0.9_linux-x64_bin.tar.gz -C /usr/local/java/

  else
        JDK_FILENAME="jdk1.8.0_271"
        wget https://objectstorage.ap-seoul-1.oraclecloud.com/n/cnk8d6fazu16/b/seoul/o/jdk-8u271-linux-x64.tar.gz
        echo "JDK 二进制文件下载成功，开始解压缩！！"
        tar -zxvf jdk-8u271-linux-x64.tar.gz -C /usr/local/java/
  fi

  echo ""
  echo "开始配置JDK的环境变量！！！"
  if [ -e /etc/profile.d/jdk-env.sh ]; then
      echo "jdk的配置文件已经存在！，将会进行覆盖操作！"
      cp /etc/profile.d/jdk-env.sh .jdk-env-backup.sh

      rm /usr/bin/java
  fi

  cat >/etc/profile.d/jdk-env.sh <<EOF
export JAVA_HOME=/usr/local/java/${JDK_FILENAME}
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=${JAVA_HOME}/lib:${JRE_HOME}/lib:${CLASSPATH}
export PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin:${PATH}
EOF
        source /etc/profile
        ln -s /usr/local/java/${JDK_FILENAME}/bin/java /usr/bin/java

        echo ""
        echo ""
        echo ""
        echo "请检查JDK的安装情况======================================"
        java -version
}

installJDKPackage(){
  JDK_VERSION=""
  if [[ "$1" -ne " " ]]; then
      JDK_VERSION="$1"
      echo "JDK Version = ${JDK_VERSION}"
  fi

  echo "InstallJDKDocker"
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

      installDemandSoftwares openjdk-${JDK_VERSION}-jdk=$(apt-cache madison openjdk-${JDK_VERSION}-jdk | head -n 1 | awk '{print$3}')
        
  fi
  
  colorEcho ${BLUE} "请检查下面的内容输出！！！"
  java -version

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

# 更换CentOS7的默认源
changeCentOS7DefaultRepo() {
  FunctionStart
  mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
  curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
  curl -o /etc/yum.repos.d/epel.repo http://mirrors.aliyun.com/repo/epel-7.repo
  # curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.cloud.tencent.com/repo/centos7_base.repo
  yum clean all && yum makecache && yum update

  FunctionEnd
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

main() {
  check_root
  check_sys
  shutdownFirewall

  # 关闭虚拟缓存，k8s安装的时候才需要
  # disableSwap

  # 安装一些常用的小工具
  commonToolInstall

  # 安装docker，版本信息在本脚本的开头处修改~~
#  InstallDocker cn || return $?
#  InstallDockerCompose || return $?
#  modifySystemConfig_Docker
#  changeDockerRegisterMirror || return $?
  
#  InstallRedis -p 36379 -m docker

#  InstallMysql -p 33306 -m docker -d "/var/lib/docker/mysql-pv"

  #InstallJDK -v 11 -m binary
  # 安装kubernetes，版本信息在本脚本的开头处修改~~
  # installKubernetes
  # modifySystemConfig_Kubernetes

  # 安装宇宙第一shell的zsh
#  InstallZSH cn || return $?
  modifyZSH cn

  # 使用chrony进行NTP时间同步--包含下面的设置
  # TimeSyncToAliByChrony || return $?

  # 使用timedatactl修改时间与时区【推荐】
  changeTimeZoneAndNTP || return $?
}

main
