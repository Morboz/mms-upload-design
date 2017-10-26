import com.aliyun.oss.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class UploadProducer implements Runnable {

    private final BlockingQueue<PutObjectRequest> uploadQueue;
    private final BlockingQueue<Design> designSqlQueue;
    private final BlockingQueue<Design.Sketch> sketchSqlQueue;
    private final static String bucketName = "mms-sh-dev";
    private Path dir;


    public UploadProducer(BlockingQueue<PutObjectRequest> uploadQueue,
                          BlockingQueue<Design> designSqlQueue,
                          BlockingQueue<Design.Sketch> sketchSqlQueue,
                          String parentFolder) {
        this.uploadQueue = uploadQueue;
        this.designSqlQueue = designSqlQueue;
        this.sketchSqlQueue = sketchSqlQueue;
        this.dir = Paths.get(parentFolder);
//        File parentFolderFile = path.toFile();
        if (!Files.isDirectory(dir)) {
            System.out.println("error: The path is not a folder!");
            throw new RuntimeException();
        }
    }

    @Override
    public void run() {
        List<Design> designList = new ArrayList<>();
        // 筛选合格的path，生成design list。
        try {
            Files.list(dir)
                    .filter(designDir -> Files.isDirectory(designDir))
                    .filter(designDir -> {
                        try {
                            return Files.list(designDir)
                                    .filter(filePath -> filePath.getFileName().toString().endsWith(".zip") || filePath.getFileName().toString().endsWith(".rar"))
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
                designList.add(new Design(designDir));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 添加请求到BQ中
        designList.forEach(design -> {
            PutObjectRequest designReq = new PutObjectRequest(bucketName, design.getOssKey(), design.getZipFile())
                    .withProgressListener(new PutObjectProgressListener(design.getZipFileSize(),
                            design.getId() + "/" + design.getZipFilePrimitiveName()));
            try {
                uploadQueue.put(designReq);
                designSqlQueue.put(design);
                System.out.println("put design req to BQ. design : " + design.getId());
            } catch (InterruptedException e) {
                System.out.println("error: put request to BQ exception: design folder -> " + design.getId());
                e.printStackTrace();
            }
            design.getSketches().forEach(sketch -> {
                PutObjectRequest sketchReq = new PutObjectRequest(bucketName, sketch.getOssKey(), sketch.getSketchFile())
                        .withProgressListener(new PutObjectProgressListener(sketch.getFileSize(),
                                sketch.getDesignId() + "/" + sketch.getPrimitiveName()));
                try {
                    uploadQueue.put(sketchReq);
                    sketchSqlQueue.put(sketch);
                    System.out.println("put sketch req to BQ. sketch : " + sketch.getDesignId() + "/" + sketch.getPrimitiveName());
                } catch (InterruptedException e) {
                    System.out.println("error: put request to BQ exception: design folder -> " + design.getId());
                    e.printStackTrace();
                }
            });
        });
    }
}
