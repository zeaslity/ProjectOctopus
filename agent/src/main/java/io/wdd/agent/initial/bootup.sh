#!/bin/bash




get_system_uuid(){

   cat /sys/class/dmi/id/product_uuid

   cat /etc/machine-id

}
