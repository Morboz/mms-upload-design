import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;

import java.util.concurrent.BlockingQueue;

public class UploadFileConsumer implements Runnable {
    private final BlockingQueue<PutObjectRequest> uploadQueue;
//    private final String endpoint = "oss-cn-shanghai-internal.aliyuncs.com";
    private final String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private final String accessKeyId = "LTAILZTyF7hCpXD5";
    private final String accessKeySecret = "OiFZ5g0rA1FvyYW1ii5nQMFSQ3Wkbl";

    private final OSSClient client;

    public UploadFileConsumer(BlockingQueue<PutObjectRequest> uploadQueue) {
        this.uploadQueue = uploadQueue;
        this.client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public void run() {
        try {
            while (true) {
                PutObjectRequest request = uploadQueue.take();
                client.putObject(request);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
