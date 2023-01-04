package io.wdd.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.tuples.Pair;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

@SpringBootTest
public class OSHITest {

    @Test
    void getSystemHardwareInfo(){

        // Jackson ObjectMapper
        ObjectMapper mapper = new ObjectMapper();


        // https://www.oshi.ooo/oshi-core-java11/apidocs/com.github.oshi/module-summary.html
        // Fetch some OSHI objects
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        try {
            // print all
            System.out.println("JSON for All info:");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(hal));

            // Pretty print computer system
            System.out.println("JSON for CPU:");
            CentralProcessor cpu = hal.getProcessor();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cpu));

            System.out.println();
            // Print memory
            System.out.println("JSON for Memory:");
            GlobalMemory mem = hal.getMemory();
            System.out.println(mapper.writeValueAsString(mem));

        } catch (JsonProcessingException e) {
            System.out.println("Exception encountered: " + e.getMessage());
        }

    }



    @Test
    void DiskStoreForPath() throws URISyntaxException {
        // Use the arg as a file path or get this class's path
        String filePath = new File(OSHITest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                .getPath();
        System.out.println("Searching stores for path: " + filePath);

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        List<HWDiskStore> diskStores = hal.getDiskStores();
        Pair<Integer, Integer> dsPartIdx = getDiskStoreAndPartitionForPath(filePath, diskStores);
        int dsIndex = dsPartIdx.getA();
        int partIndex = dsPartIdx.getB();

        System.out.println();
        System.out.println("DiskStore index " + dsIndex + " and Partition index " + partIndex);
        if (dsIndex >= 0 && partIndex >= 0) {
            System.out.println(diskStores.get(dsIndex));
            System.out.println(" |-- " + diskStores.get(dsIndex).getPartitions().get(partIndex));
        } else {
            System.out.println("Couldn't find that path on a partition.");
        }

        OperatingSystem os = si.getOperatingSystem();
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        int fsIndex = getFileStoreForPath(filePath, fileStores);

        System.out.println();
        System.out.println("FileStore index " + fsIndex);
        if (fsIndex >= 0) {
            System.out.println(fileStores.get(fsIndex));
        } else {
            System.out.println("Couldn't find that path on a filestore.");
        }
    }

    private static Pair<Integer, Integer> getDiskStoreAndPartitionForPath(String path, List<HWDiskStore> diskStores) {
        for (int ds = 0; ds < diskStores.size(); ds++) {
            HWDiskStore store = diskStores.get(ds);
            List<HWPartition> parts = store.getPartitions();
            for (int part = 0; part < parts.size(); part++) {
                String mount = parts.get(part).getMountPoint();
                if (!mount.isEmpty() && path.substring(0, mount.length()).equalsIgnoreCase(mount)) {
                    return new Pair<>(ds, part);
                }
            }
        }
        return new Pair<>(-1, -1);
    }

    private static int getFileStoreForPath(String path, List<OSFileStore> fileStores) {
        for (int fs = 0; fs < fileStores.size(); fs++) {
            String mount = fileStores.get(fs).getMount();
            if (!mount.isEmpty() && path.substring(0, mount.length()).equalsIgnoreCase(mount)) {
                return fs;
            }
        }
        return -1;
    }


}
