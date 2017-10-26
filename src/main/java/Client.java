import com.aliyun.oss.model.PutObjectRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Client {

    private static final int UPLOAD_THREADS_COUNT = 3;

    public static void main(String[] args) throws Exception {
//        if (args == null || args[0].trim().length() == 0) {
//            throw new RuntimeException("please enter upload files folder full-path");
//        }
//        String parentFolder = args[0].trim();
        String parentFolder = "D:\\test-design";

        BlockingQueue<PutObjectRequest> uploadQueue = new LinkedBlockingDeque<>();
        BlockingQueue<Design> designSqlQueue = new LinkedBlockingDeque<>();
        BlockingQueue<Design.Sketch> sketchSqlQueue = new LinkedBlockingDeque<>();

        Thread producer = new Thread(new UploadProducer(uploadQueue,
                designSqlQueue, sketchSqlQueue, parentFolder), "producer");
        Thread designSqlConsumer = new Thread(new DesignSqlConsumer(designSqlQueue));
        Thread sketchSqlConsumer = new Thread(new SketchSqlConsumer(sketchSqlQueue));
        producer.start();
        designSqlConsumer.start();
        sketchSqlConsumer.start();
        for (int i = 0; i < UPLOAD_THREADS_COUNT; i++) {
            Thread uploadFileConsumer = new Thread(new UploadFileConsumer(uploadQueue), "upload-file-" + i);
            uploadFileConsumer.start();
        }

    }

}
