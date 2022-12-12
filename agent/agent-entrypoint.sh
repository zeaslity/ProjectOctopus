#!/bin/bash

RED="31m"                          ## 姨妈红
GREEN="32m"                        ## 水鸭青
YELLOW="33m"                       ## 鸭屎黄
PURPLE="35m"                       ## 基佬紫
BLUE="36m"                         ## 天依蓝
BlinkGreen="32;5m"                 ##闪烁的绿色
BlinkRed="31;5m"                   ##闪烁的红色
BackRed="41m"                      ## 背景红色
SplitLine="----------------------" #会被sys函数中的方法重写

hostArchVersion=""
hostArch=""
#### CollectSystemInfo ####
serverName=""
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
machineId=""

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
  # shellcheck disable=SC2145
  echo -e "\033[${1}${@:2}\033[0m" 1>&2
}
# 判断命令是否存在
command_exists() {
  local cmd="$1"
  if eval type type >/dev/null 2>&1; then
    eval type "$cmd" >/dev/null 2>&1
  elif command >/dev/null 2>&1; then
    command -v "$cmd" >/dev/null 2>&1
  else
    which "$cmd" >/dev/null 2>&1
  fi
  local rt=$?
  return ${rt}
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
#######   获取系统版本及64位或32位信息
check_sys() {
  # 获取当前终端的宽度，动态调整分割线的长度
  shellwidth=$(stty size | awk '{print $2}')
  if [[ $shellwidth -gt 1 ]]; then
       SplitLine=$(yes "-" | sed ${shellwidth}'q' | tr -d '\n')
  fi

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
    colorEcho ${RED} "
       哈哈……这个 辣鸡脚本 不支持你的系统。 (-_-) \n
       备注: 仅支持 Ubuntu 16+ / Debian 8+ / CentOS 7+ 系统
       " && exit 1
    ;;
  esac

}

GoIOTest() {
  (LANG=C dd if=/dev/zero of=benchtest_$$ bs=512k count=$1 conv=fdatasync && rm -f benchtest_$$) 2>&1 | awk -F, '{io=$NF} END { print io}' | sed 's/^[ \t]*//;s/[ \t]*$//'
}

calc_size() {
  local raw=$1
  local total_size=0
  local num=1
  local unit="KB"
  if ! [[ ${raw} =~ ^[0-9]+$ ]]; then
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
  total_size=$(awk 'BEGIN{printf "%.1f", '$raw' / '$num'}')
  echo "${total_size} ${unit}"
}

GethostArchINfo() {
  [ -f /etc/redhat-release ] && awk '{print $0}' /etc/redhat-release && return
  [ -f /etc/os-release ] && awk -F'[= "]' '/PRETTY_NAME/{print $3,$4,$5}' /etc/os-release && return
  [ -f /etc/lsb-release ] && awk -F'[="]+' '/DESCRIPTION/{print $2}' /etc/lsb-release && return
}

StartIOTest() {

  FunctionStart
  colorEcho ${BLUE} "start IO speed test !"

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

Check_Virtualization() {

  FunctionStart
  colorEcho ${BLUE} "start to check host virtualization !"

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
  if grep -qa docker /proc/1/cgroup; then
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
  elif [[ "${cpuName}" == *KVM* ]]; then
    virt="KVM"
  elif [[ "${cpuName}" == *QEMU* ]]; then
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

  FunctionSuccess
  FunctionEnd
}

GetIpv4Info() {

  FunctionStart
  colorEcho ${BLUE} "start to get system  public ip info !"

  org="$(wget -q -T10 -O- ipinfo.io/org)"
  city="$(wget -q -T10 -O- ipinfo.io/city)"
  country="$(wget -q -T10 -O- ipinfo.io/country)"
  region="$(wget -q -T10 -O- ipinfo.io/region)"
  public_ipv4="$(wget -q -T10 -O- ipinfo.io/ip)"

  FunctionSuccess
  FunctionEnd

}

GenerateSystemInfo() {
  FunctionStart
  colorEcho $BLUE "start to collect system info !"

  check_sys

  cpuName=$(awk -F: '/model name/ {name=$2} END {print name}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
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
  opsy=$(GethostArchINfo)
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

  # todo
#  StartIOTest

  GetIpv4Info

  Check_Virtualization

  local machineNumber=""

  if [[ $(cat /etc/hostname | cut -d"-" -f 3 | grep -c '^[0-9][0-9]') -gt 0 ]]; then
      machineNumber=$(cat /etc/hostname | cut -d"-" -f 3)
  else
      machineNumber=99
  fi

  export serverName="${city}-${hostArch}-${machineNumber}"
  export serverIpPbV4="$public_ipv4"
  export serverIpInV4=""
  export serverIpPbV6=""
  export serverIpInV6=""
  export location="$city $region $country"
  export provider="$org"
  export managePort="$(netstat -ntulp | grep sshd | grep -w tcp | awk '{print$4}' | cut -d":" -f2)"
  export cpuCore="$cores @ $freq MHz"
  export cpuBrand="$cpuName"
  export memoryTotal="$tram"
  export diskTotal="$disk_total_size"
  export diskUsage="$disk_used_size"
  export archInfo="$arch ($lbit Bit)"
  export osInfo="$opsy"
  export osKernelInfo="$kern"
  export tcpControl="$tcpctrl"
  export virtualization="$virt"
  export ioSpeed="$ioavg MB/s"
  export machineId="$(cat /host/etc/machine-id)"

  FunctionEnd
}

PrintEnv(){

  FunctionStart

  env

  FunctionEnd
}

main() {

  GenerateSystemInfo

  PrintEnv

  FunctionEnd

}

main

# copy jar to /host
# change the root working dir and use the host jvm to run the jar-file
scp -r /wdd /host/wdd && chroot /host java ${JAVA_OPTS} -jar /wdd/agent.jar
