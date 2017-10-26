import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class UploadOssFile {
//    private static String endpoint = "oss-cn-shanghai.aliyuncs.com";
    private static String endpoint = "oss-cn-shang  hai-internal.aliyuncs.com";
    private static String accessKeyId = "LTAILZTyF7hCpXD5";
    private static String accessKeySecret = "OiFZ5g0rA1FvyYW1ii5nQMFSQ3Wkbl";
    private static String bucketName = "mms-sh-dev";
//    private static String bucketName = "mms-sh-local";

    public static void uploadDesignList(List<Design> designList) throws IOException {
        Path designSqlPath = Paths.get("insert-design.sql");
        Path sketchSqlPath = Paths.get("insert-sketch.sql");
        Path applicationSqlPath = Paths.get("insert-application.sql");
        // 如果存在就删除
        if (Files.exists(designSqlPath)) {
            Files.delete(designSqlPath);
        }
        if (Files.exists(sketchSqlPath)) {
            Files.delete(sketchSqlPath);
        }
        if (Files.exists(applicationSqlPath)) {
            Files.delete(applicationSqlPath);
        }
        // 创建file
        Files.createFile(designSqlPath);
        Files.createFile(sketchSqlPath);
        Files.createFile(applicationSqlPath);
        BufferedWriter designSqlWriter = Files.newBufferedWriter(designSqlPath, StandardOpenOption.APPEND);
        BufferedWriter sketchSqlWriter = Files.newBufferedWriter(sketchSqlPath, StandardOpenOption.APPEND);
        BufferedWriter applicationSqlWriter = Files.newBufferedWriter(applicationSqlPath, StandardOpenOption.APPEND);
        designSqlWriter.write("INSERT INTO design(id,owner_id, profession_id, structure_id, exhibition_type_id,open_sides,oss_key,\n" +
                "price_lower_limit, price_upper_limit, area, status, is_visible, create_time, modified_time,primitive_name)\n" +
                "VALUES\n");
        sketchSqlWriter.write("INSERT INTO design_sketch(design_id, oss_key, is_cover, status, create_time, primitive_name)\n" +
                "VALUES\n");
        applicationSqlWriter.write("INSERT INTO application(design_id, applicant, status, create_time)\n" +
                "VALUES\n");
        final OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        designList.forEach((Design design) -> {
            // 上传zip
            PutObjectRequest request = new PutObjectRequest(bucketName, design.getOssKey(), design.getZipFile())
                    .withProgressListener(new PutObjectProgressListener(design.getZipFileSize()));
//            client.putObject(bucketName, design.getOssKey(), design.getZipFileIn());
            client.putObject(request);
            // sql insert into design()
            String designValueSql = String.format("(%d,1,%d,%d,%d,%d,'%s',%d,%d,%d,1,1,%d,%d,'%s'),\n", design.getId(),
                    design.getProfessionId(), design.getStructureId(), design.getExhibitionTypeId(), design.getOpenSides(),
                    design.getOssKey(), design.getPriceLowerLimit(), design.getPriceUpperLimit(), design.getArea(),
                    System.currentTimeMillis(), System.currentTimeMillis(), design.getZipFilePrimitiveName());
            String applicationValueSql = String.format("(%d,1,2,%d),\n", design.getId(), System.currentTimeMillis());
            try {
                designSqlWriter.write(designValueSql);
                designSqlWriter.flush();
                applicationSqlWriter.write(applicationValueSql);
                applicationSqlWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("exception while uploading file: write sql exception -> " + design.getOssKey().split("/")[0]);
            }
            // 上传sketch
            design.getSketches().forEach((Design.Sketch sketch) -> {
                // 上传sketch
                PutObjectRequest putSketchRequest = new PutObjectRequest(bucketName, sketch.getOssKey(), sketch.getSketchFile())
                        .withProgressListener(new PutObjectProgressListener(sketch.getFileSize()));
                client.putObject(putSketchRequest);
                // sql
                String sketchValueSql = String.format("(%d,'%s',%d,1,%d,'%s'),\n", sketch.getDesignId(), sketch.getOssKey(),
                        sketch.isCover()? 1 : 0, System.currentTimeMillis(),sketch.getPrimitiveName());
                try {
                    sketchSqlWriter.write(sketchValueSql);
                    sketchSqlWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("exception while uploading sketch file: write sketch sql exception -> " + design.getOssKey().split("/")[0]);
                }
            });

        });


    }


}
