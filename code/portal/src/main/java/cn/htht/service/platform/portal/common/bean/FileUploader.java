package cn.htht.service.platform.portal.common.bean;

import cn.htht.service.platform.portal.entity.manager.FileData;
import cn.htht.service.platform.portal.utils.utils.uuid.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

/**
 * @author piesat
 * @className 类名称
 * @description 描述
 * @date 2021/9/16
 */
@Component
public class FileUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader.class);

    public FileData upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            LOGGER.info("未选择文件");
            return null;
        }
        String fileName = file.getOriginalFilename();
        String filePath = "D:\\code\\java\\SMGIService\\code\\portal\\src\\file\\";
        File dest = new File(filePath + fileName);
        String[] fileNameSplit = Objects.requireNonNull(fileName.split("\\."));
        FileData attachmentFile = new FileData();
        String attachmentId = IdUtils.fastSimpleUUID();
        attachmentFile.setId(attachmentId);
        attachmentFile.setFileName(fileName);
        attachmentFile.setExt(fileNameSplit[fileNameSplit.length-1]);
        attachmentFile.setFileTitle(fileName);
        // TODO attachmentFile.setFileType();

        file.transferTo(dest);
        LOGGER.info("上传成功");

        return attachmentFile;
    }

//    public AttachmentFile multiFilesUpload(MultipartFile[] files){
//        if (files == null && files.length == 0) {
//            LOGGER.info("未选择文件");
//            return null;
//        }
//        String filePath = "D:\\code\\java\\SMGIService\\code\\portal\\src\\file\\";
//        for (int i = 0; i < files.length; i++){
//            String fileName = files[i].getOriginalFilename();
//            File dest = new File(filePath + fileName);
//            String[] fileNameSplit = Objects.requireNonNull(fileName.split("\\."));
//            AttachmentFile attachmentFile = new AttachmentFile();
//            String attachmentId = IdUtils.fastSimpleUUID();
//            attachmentFile.setId(attachmentId);
//            attachmentFile.setFileName(fileName);
//            attachmentFile.setExt(fileNameSplit[fileNameSplit.length-1]);
//            attachmentFile.setFileTitle(fileName);
//            // TODO attachmentFile.setFileType();
//            try {
//                files[i].transferTo(dest);
//                serviceAttachmentFileMapper.insertServiceAttachmentFile(attachmentFile);
//                LOGGER.info("上传成功");
//            } catch (IOException e) {
//                LOGGER.error(e.toString(), e);
//            }
//        }
//        return attachmentFile;
//    }
}
