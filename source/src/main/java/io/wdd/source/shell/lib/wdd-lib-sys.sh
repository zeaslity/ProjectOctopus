#!/bin/bash



. /octopus-agent/shell/lib/wdd-lib-log.sh



# 判断命令是否存在
command_exists() {
  command -v "$@" >/dev/null 2>&1
}


POST https://www.googleapis.com/compute/v1/projects/compact-lacing-371804/global/instanceTemplates
{
  "description": "",
  "name": "octopus-agent-2c-4g",
  "properties": {
    "canIpForward": true,
    "confidentialInstanceConfig": {
      "enableConfidentialCompute": false
    },
    "description": "",
    "disks": [
      {
        "autoDelete": true,
        "boot": true,
        "deviceName": "octopus-agent-2c-4g",
        "diskEncryptionKey": {},
        "initializeParams": {
          "diskSizeGb": "20",
          "diskType": "pd-ssd",
          "labels": {},
          "sourceImage": "projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213"
        },
        "mode": "READ_WRITE",
        "type": "PERSISTENT"
      }
    ],
    "displayDevice": {
      "enableDisplay": false
    },
    "keyRevocationActionType": "NONE",
    "labels": {},
    "machineType": "n2d-custom-2-4096",
    "metadata": {
      "items": [
        {
          "key": "startup-script",
          "value": "wget https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh && chmod +x agent-bootup.sh && /bin/bash agent-bootup.sh"
        }
      ]
    },
    "networkInterfaces": [
      {
        "accessConfigs": [
          {
            "kind": "compute#accessConfig",
            "name": "External NAT",
            "networkTier": "PREMIUM",
            "type": "ONE_TO_ONE_NAT"
          }
        ],
        "network": "projects/compact-lacing-371804/global/networks/default",
        "stackType": "IPV4_ONLY"
      }
    ],
    "reservationAffinity": {
      "consumeReservationType": "ANY_RESERVATION"
    },
    "scheduling": {
      "automaticRestart": true,
      "onHostMaintenance": "MIGRATE",
      "provisioningModel": "STANDARD"
    },
    "serviceAccounts": [
      {
        "email": "172889627951-compute@developer.gserviceaccount.com",
        "scopes": [
          "https://www.googleapis.com/auth/devstorage.read_only",
          "https://www.googleapis.com/auth/logging.write",
          "https://www.googleapis.com/auth/monitoring.write",
          "https://www.googleapis.com/auth/servicecontrol",
          "https://www.googleapis.com/auth/service.management.readonly",
          "https://www.googleapis.com/auth/trace.append"
        ]
      }
    ],
    "shieldedInstanceConfig": {
      "enableIntegrityMonitoring": false,
      "enableSecureBoot": false,
      "enableVtpm": false
    },
    "tags": {
      "items": []
    }
  }
}

gcloud compute instances create octopus-agent-2c-4g-1 --project=compact-lacing-371804 --zone=asia-northeast1-b --machine-type=n2d-custom-2-4096 --network-interface=network-tier=PREMIUM,subnet=default --metadata=startup-script=wget\ https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh\ \&\&\ chmod\ \+x\ agent-bootup.sh\ \&\&\ /bin/bash\ agent-bootup.sh --can-ip-forward --maintenance-policy=MIGRATE --provisioning-model=STANDARD --service-account=172889627951-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append --create-disk=auto-delete=yes,boot=yes,device-name=octopus-agent-2c-4g,image=projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213,mode=rw,size=20,type=projects/compact-lacing-371804/zones/us-west4-b/diskTypes/pd-ssd --no-shielded-secure-boot --shielded-vtpm --shielded-integrity-monitoring --reservation-affinity=any


gcloud compute instances delete octopus-agent-2c-4g-1 --project=compact-lacing-371804 --zone=asia-northeast1-b


gcloud compute instances create octopus-agent-2c-4g-7 --project=compact-lacing-371804 --zone=asia-northeast1-b --machine-type=n2d-custom-2-4096 --network-interface=network-tier=PREMIUM,subnet=default  --can-ip-forward --maintenance-policy=MIGRATE --provisioning-model=STANDARD --service-account=172889627951-compute@developer.gserviceaccount.com --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append --create-disk=auto-delete=yes,boot=yes,device-name=octopus-agent-2c-4g,image=projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213,mode=rw,size=20,type=projects/compact-lacing-371804/zones/us-west4-b/diskTypes/pd-ssd --no-shielded-secure-boot --shielded-vtpm --shielded-integrity-monitoring --reservation-affinity=any

gcloud compute ssh --zone "asia-northeast1-b" "octopus-agent-2c-4g-7" --project "compact-lacing-371804"


wget https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh && chmod +x agent-bootup.sh && /bin/bash agent-bootup.sh

POST https://www.googleapis.com/compute/v1/projects/compact-lacing-371804/zones/asia-northeast1-b/instances
{
  "canIpForward": true,
  "confidentialInstanceConfig": {
    "enableConfidentialCompute": false
  },
  "deletionProtection": false,
  "description": "",
  "disks": [
    {
      "autoDelete": true,
      "boot": true,
      "deviceName": "octopus-agent-2c-4g",
      "initializeParams": {
        "diskSizeGb": "20",
        "diskType": "projects/compact-lacing-371804/zones/us-west4-b/diskTypes/pd-ssd",
        "labels": {},
        "sourceImage": "projects/ubuntu-os-cloud/global/images/ubuntu-2004-focal-v20221213"
      },
      "mode": "READ_WRITE",
      "type": "PERSISTENT"
    }
  ],
  "displayDevice": {
    "enableDisplay": false
  },
  "guestAccelerators": [],
  "keyRevocationActionType": "NONE",
  "labels": {},
  "machineType": "projects/compact-lacing-371804/zones/asia-northeast1-b/machineTypes/n2d-custom-2-4096",
  "metadata": {
    "items": [
      {
        "key": "startup-script",
        "value": "wget https://raw.githubusercontent.com/zeaslity/ProjectOctopus/main/source/src/main/java/io/wdd/source/shell/agent-bootup.sh && chmod +x agent-bootup.sh && /bin/bash agent-bootup.sh"
      }
    ]
  },
  "name": "octopus-agent-2c-4g-1",
  "networkInterfaces": [
    {
      "accessConfigs": [
        {
          "name": "External NAT",
          "networkTier": "PREMIUM"
        }
      ],
      "stackType": "IPV4_ONLY",
      "subnetwork": "projects/compact-lacing-371804/regions/asia-northeast1/subnetworks/default"
    }
  ],
  "params": {
    "resourceManagerTags": {}
  },
  "reservationAffinity": {
    "consumeReservationType": "ANY_RESERVATION"
  },
  "scheduling": {
    "automaticRestart": true,
    "onHostMaintenance": "MIGRATE",
    "preemptible": false,
    "provisioningModel": "STANDARD"
  },
  "serviceAccounts": [
    {
      "email": "172889627951-compute@developer.gserviceaccount.com",
      "scopes": [
        "https://www.googleapis.com/auth/devstorage.read_only",
        "https://www.googleapis.com/auth/logging.write",
        "https://www.googleapis.com/auth/monitoring.write",
        "https://www.googleapis.com/auth/servicecontrol",
        "https://www.googleapis.com/auth/service.management.readonly",
        "https://www.googleapis.com/auth/trace.append"
      ]
    }
  ],
  "shieldedInstanceConfig": {
    "enableIntegrityMonitoring": true,
    "enableSecureBoot": false,
    "enableVtpm": true
  },
  "tags": {
    "items": []
  },
  "zone": "projects/compact-lacing-371804/zones/asia-northeast1-b"
}