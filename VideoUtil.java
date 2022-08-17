package cn.qy.util;

import com.coremedia.iso.IsoFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @version 1.0
 * @Author lhy
 * @Date 2022/8/16 10:37
 * @注释
 */
public class VideoUtil {

    /**
     * 获取视频文件的播放长度(mp4、mov格式)
     * @param videoPath
     * @return 单位为毫秒
     */
    public static long getMp4Duration(String videoPath) throws IOException {
        IsoFile isoFile = new IsoFile(videoPath);
        long lengthInSeconds =
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                        isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        return lengthInSeconds;
    }


    /**
     * 得到语音或视频文件时长,单位秒
     * @param filePath
     * @return
     * @throws IOException
     */
    public static long getDuration(String filePath) throws IOException {
        String format = getVideoFormat(filePath);
        long result = 0;
        if("wav".equals(format)){
            //result = AudioUtil.getDuration(filePath).intValue();
        }else if("mp3".equals(format)){
            //result = AudioUtil.getMp3Duration(filePath).intValue();
        }else if("m4a".equals(format)) {
            result = VideoUtil.getMp4Duration(filePath);
        }else if("mov".equals(format)){
            result = VideoUtil.getMp4Duration(filePath);
        }else if("mp4".equals(format)){
            result = VideoUtil.getMp4Duration(filePath);
        }
        return result;
    }

    /**
     * 得到文件格式
     * @param path
     * @return
     */
    public static String getVideoFormat(String path){
        return  path.toLowerCase().substring(path.toLowerCase().lastIndexOf(".") + 1);
    }

    /**
     * @Description: 获取视频时长(时分秒)
     *
     * @params: [file]
     * @return: java.lang.String
     */
    public static Long readVideoTimeMs(MultipartFile file) {
        long time = 0;
        try {
            // 获取文件类型
            String fileName = file.getContentType();
            // 获取文件后缀
            String pref = fileName.indexOf("/") != -1 ? fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length()) : null;
            String prefix = "." + pref;
            // 用uuid作为文件名，防止生成的临时文件重复
            final File excelFile = File.createTempFile(UUID.randomUUID().toString().replace("-", ""), prefix);
            // MultipartFile to File 这个时候那个临时文件才有内容
            file.transferTo(excelFile);
            time = getDuration(excelFile.getPath());
            System.out.println("视频时长"+ time);
            //程序结束时，删除临时文件
            VideoUtil.deleteFile(excelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  time;

    }

    /**
     * @Description: 删除临时文件
     * @params: [files]
     * @return: void
     */
    private static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
