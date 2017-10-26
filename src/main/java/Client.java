import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    public static void main(String[] args) throws Exception {
        if (args == null || args[0].trim().length() == 0) {
            throw new RuntimeException("please enter upload files folder full-path");
        }
        String parentFolder = args[0].trim();
        Path dir = Paths.get(parentFolder);
//        File parentFolderFile = path.toFile();
        if (!Files.isDirectory(dir)) {
            System.out.println("The path is not a folder!");
            return;
        }
        List<Design> designList = new ArrayList<>();
        Files.list(dir)
                .filter(designDir -> Files.isDirectory(designDir))
                .filter(designDir -> {
                    try {
                        return Files.list(designDir)
                                .filter(filePath -> filePath.getFileName().toString().endsWith(".zip")||filePath.getFileName().toString().endsWith(".rar"))
                                .collect(Collectors.toList())
                                .size() >= 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .filter(designDir -> {
                    try {
                        return Files.list(designDir)
                                .filter(filePath -> filePath.getFileName().toString().endsWith(".jpg"))
                                .collect(Collectors.toList())
                                .size() >= 6;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).forEach(designDir -> {
            System.out.println(designDir.getFileName() + ":");
//            try {
//                Files.list(designDir).forEach(path -> System.out.println("    |-" + path.getFileName()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            designList.add(new Design(designDir));
        });
        UploadOssFile.uploadDesignList(designList);
//        System.out.println(designList.size());

    }

}
