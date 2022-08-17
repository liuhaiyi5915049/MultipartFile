
@Service
public class FileVideoPathServiceImpl implements IFileVideoPathService {

/**
     * 上传停车视频
     * @param file
     * @param dto
     * @return
     */
    @Override
    public long parkVideoUpload(MultipartFile file, UploadDTO dto) {
        //获取视频文件的时长
        Long duration = VideoUtil.readVideoTimeMs(file);
        // 获取文件类型
        String fileName = file.getContentType();
        // 获取文件后缀
        String pref = StringUtil.fileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        FileVideoPath video = new FileVideoPath();
        //计算视频文件大小 单位M
        double fileSize = file.getSize() / 1048576;
        //判断文件是否大于5M
        if (fileSize <  Constant.INT_FIVE) {

            if (pref.equals(".mp4")) {

                if (55+-1 <= duration && duration <= 65+-1) {
                    //订单开始时间
                    //dto.getOrderStartTime().plusSeconds(210);
                    //文件格式处理
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
                    String workingPath = "video/" + sdf.format(new Date());
                    //入库
                    fileName = "video" + LocalDateTime.now() + UUIDUtil.get32UUID() + pref;
                    video.setBusinessId(dto.getBusinessId());
                    video.setBusinessLogo(dto.getBusinessLogo());
                    video.setVideoPath(workingPath + fileName);
                    video.setDeleteStatus(Constant.STATUS_TRUE);
                    video.setCreateTime(LocalDateTime.now());
                    video.setCreateBy(dto.getHandler());
                    //入库
                    fileVideoPathMapper.insert(video);
                    //文件名称：由时间路径和时间串加文件后缀组成
                    //上传文件
                    try {
                        FTPTools.upload(ftpAndHostConfig.getHostname(), ftpAndHostConfig.getPort(), ftpAndHostConfig.getUsername(), ftpAndHostConfig.getPassword(), workingPath, file.getInputStream(), fileName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("文件上传成功");
                }
            }

        }
        return video.getId();
    }
}
