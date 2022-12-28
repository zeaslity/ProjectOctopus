#!/bin/bash


. /octopus-agent/shell/lib/wdd-lib-log.sh
. /octopus-agent/shell/lib/wdd-lib-sys.sh

#. .wdd-lib-log.sh
#. .wdd-lib-sys.sh


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
archInfo=""

### tmp usage
ioavg=""
public_ipv4=""
country=""
region=""
city=""
org=""
#### CollectSystemInfo ####


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

GethostArchInfo() {
  [ -f /etc/redhat-release ] && awk '{print $0}' /etc/redhat-release && return
  [ -f /etc/os-release ] && awk -F'[= "]' '/PRETTY_NAME/{print $3,$4,$5}' /etc/os-release && return
  [ -f /etc/lsb-release ] && awk -F'[="]+' '/DESCRIPTION/{print $2}' /etc/lsb-release && return
}

StartIOTest() {
  log "start IO speed test !"
  freespace=$(df -m . | awk 'NR==2 {print $4}')
  if [ -z "${freespace}" ]; then
    freespace=$(df -m . | awk 'NR==3 {print $3}')
  fi
  if [ ${freespace} -gt 1024 ]; then
    writemb=2048
    io1=$(GoIOTest ${writemb})
    log "I/O Speed(1st run) : $io1)"
    io2=$(GoIOTest ${writemb})
    log "I/O Speed(2st run) : $io2)"
    io3=$(GoIOTest ${writemb})
    log "I/O Speed(3st run) : $io3)"
    ioraw1=$(echo $io1 | awk 'NR==1 {print $1}')
    [ "$(echo $io1 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw1=$(awk 'BEGIN{print '$ioraw1' * 1024}')
    ioraw2=$(echo $io2 | awk 'NR==1 {print $1}')
    [ "$(echo $io2 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw2=$(awk 'BEGIN{print '$ioraw2' * 1024}')
    ioraw3=$(echo $io3 | awk 'NR==1 {print $1}')
    [ "$(echo $io3 | awk 'NR==1 {print $2}')" == "GB/s" ] && ioraw3=$(awk 'BEGIN{print '$ioraw3' * 1024}')
    ioall=$(awk 'BEGIN{print '$ioraw1' + '$ioraw2' + '$ioraw3'}')
    ioavg=$(awk 'BEGIN{printf "%.1f", '$ioall' / 3}')
    log "I/O Speed(average) : $ioavg MB/s)"
  else
    echo " $(_red "Not enough space for I/O Speed test!")"
  fi


}

Check_Virtualization() {


  log "start to check host virtualization !"

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

  
}

GetIpv4Info() {

  
  log "start to get system  public ip info !"

  org="$(wget -q -T10 -O- ipinfo.io/org)"
  city="$(wget -q -T10 -O- ipinfo.io/city)"
  country="$(wget -q -T10 -O- ipinfo.io/country)"
  region="$(wget -q -T10 -O- ipinfo.io/region)"
  public_ipv4="$(wget -q -T10 -O- ipinfo.io/ip)"


}

  
log "start to collect system info !"

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
opsy=$(GethostArchInfo)
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


# todo
#  StartIOTest

GetIpv4Info

Check_Virtualization

machineNumber=""

if [[ $(cat /etc/hostname | cut -d"-" -f 3 | grep -c '^[0-9][0-9]') -gt 0 ]]; then
    machineNumber=$(cat /etc/hostname | cut -d"-" -f 3)
else
    machineNumber=99
fi
cat >/etc/environment.d/octopus-agent.conf<<EOF
serverName=${city}-${hostArch}-${machineNumber}
serverIpPbV4=$public_ipv4
serverIpInV4=
serverIpPbV6=
serverIpInV6=
location="$city $region $country"
provider=$org
managePort=$(netstat -ntulp | grep sshd | grep -w tcp | awk '{print$4}' | cut -d":" -f2)
cpuCore="$cores @ $freq MHz"
cpuBrand="$cpuName"
memoryTotal=$tram
diskTotal=$disk_total_size
diskUsage=$disk_used_size
archInfo="$arch ($lbit Bit)"
osInfo="$opsy"
osKernelInfo=$kern
tcpControl=$tcpctrl
virtualization=$virt
ioSpeed="$ioavg MB/s"
machineId=$(cat /etc/machine-id)
EOF

log "env collect complete!"
source /etc/environment

env

