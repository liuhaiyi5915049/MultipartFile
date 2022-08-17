 public interface IFileVideoPathService {
 
 /**
     * 上传视频
     * @param file
     * @param dto
     * @return
     */
    long parkVideoUpload(MultipartFile file, UploadDTO dto);
    
   }
