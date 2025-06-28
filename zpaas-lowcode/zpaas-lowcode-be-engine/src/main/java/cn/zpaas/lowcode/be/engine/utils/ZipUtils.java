package cn.zpaas.lowcode.be.engine.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.zpaas.lowcode.utils.CollectionUtils;
import cn.zpaas.lowcode.utils.FileUtils;
import cn.zpaas.lowcode.constant.ResultStatus;
import cn.zpaas.lowcode.exception.EngineException;

/**
 * Zip压缩解压工具类
 *
 * @author zjy
 * createTime 2025年04月20日-15:27:47
 */
public class ZipUtils {
    public static Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    private static final String SLASH_FLAG = "/";

    /**
     * 压缩文件
     * @param zipFile 目标zip文件
     * @param srcFiles 源文件列表
     * @throws EngineException
     */
    public static void zip(File zipFile, List<File> srcFiles) throws EngineException {
        if(CollectionUtils.isEmpty(srcFiles) || zipFile == null) {
            logger.error("srcFiles is null");
            return;
        }
        ZipOutputStream zipOutputStream = null;
        try {
            if(!zipFile.exists()) {//如果目标zip文件不存在，则创建
                zipFile.createNewFile();
            }
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            //逐个进行压缩
            for(File srcFile: srcFiles) {
                if(srcFile == null) {
                    logger.info("srcFile is null");
                    continue;
                }
                
                //调用压缩文件
                compress(zipOutputStream, srcFile, FileUtils.removeUUIDFromFilePath(srcFile.toPath()).toFile().getName());
            }
        } catch (IOException e) {
            logger.error("uncompress file failed!", e);
            throw new EngineException(ResultStatus.INTERNAL_ERROR, "uncompress file failed!", e.getMessage(), e);
        }finally{
            if(zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    
                }
            }
        }
    }

    /**
     * 解压缩多个文件
     * @param srcZipFiles 压缩文件列表
     * @param targetDir 目标目录
     * @return
     * @throws EngineException
     */
    public static List<File> unzip(List<File> srcZipFiles, String targetDir)throws EngineException{
        if(CollectionUtils.isEmpty(srcZipFiles)) {
            logger.info("srcZipFiles is null");
            return null;
        }
        List<File> result = new ArrayList<>();

        List<File> oneZipResult = null;
        for(File srcZipFile: srcZipFiles) {
            oneZipResult = unzip(srcZipFile, targetDir + SLASH_FLAG + srcZipFile.getName());
            if(!CollectionUtils.isEmpty(oneZipResult)) {
                result.addAll(oneZipResult);
            }
        }
        return result;
    }

    /**
     * 解压缩单个文件
     * @param srcZipFile 压缩文件
     * @param targetDir 目标目录
     * @return
     * @throws EngineException
     */
    public static List<File> unzip(File srcZipFile, String targetDir) throws EngineException{
        if(srcZipFile == null || !srcZipFile.exists()) {
            logger.info("zipFile is null");
            return null;
        }
        List<File> result = new ArrayList<>();
        ZipFile zipFile = null;
        try {
            //创建ZipFile
            zipFile = new ZipFile(srcZipFile);
        
            //获取压缩文件中的条目信息列表并进行循环处理
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                if(zipEntry.isDirectory()) {//是个目录
                    File dir = new File(targetDir + SLASH_FLAG + zipEntry.getName());
                    dir.mkdirs();
                }else {//是个文件
                    //创建解压缩的目标文件
                    File targetFile = new File(targetDir + SLASH_FLAG + zipEntry.getName());
                    if(!targetFile.getParentFile().exists()) {//如果父目录不存在则创建
                        targetFile.getParentFile().mkdirs();
                    }
                    
                    BufferedInputStream bufferedInputStream = null;
                    BufferedOutputStream bufferedOutputStream = null;
                    try {
                        targetFile.createNewFile();//创建新文件
                        //从压缩包中读取文件信息并写入目标文件
                        bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                        byte[] data = new byte[1024];
                        while (bufferedInputStream.read(data) != -1) {
                            bufferedOutputStream.write(data);
                        }
                        result.add(targetFile);
                    } catch (Exception e) {
                        logger.error("uncompress file failed!", e);
                        throw new EngineException(ResultStatus.INTERNAL_ERROR, "uncompress file failed!", e.getMessage(), e);
                    } finally {
                        if(bufferedInputStream != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e) {
                                
                            }
                        }
                        if(bufferedOutputStream != null) {
                            try {
                                bufferedOutputStream.close();
                            } catch (IOException e) {
                                
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("compress file failed!", e);
            throw new EngineException(ResultStatus.INTERNAL_ERROR, "compress file failed!", e.getMessage(), e);
        } finally {
            if(zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    
                }
            }
        }
        return result;
    }

    private static void compress(ZipOutputStream zipOutputStream, File srcFile, String fileName) throws EngineException {
        logger.debug("compress file: {}", fileName);
        if(srcFile.isDirectory()) {//源文件是个目录
            File[] files = srcFile.listFiles();
            if(files == null || files.length == 0) {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(fileName + SLASH_FLAG));
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    logger.error("compress file failed!", e);
                    throw new EngineException(ResultStatus.INTERNAL_ERROR, "compress file failed!", e.getMessage(), e);
                }
            }else {
                for(File file : files) {
                    compress(zipOutputStream, file, fileName + SLASH_FLAG + file.getName());
                }
            }
        }else {//源文件是个文件
            BufferedInputStream bufferedInputStream = null;
            try {
                zipOutputStream.putNextEntry(new ZipEntry(fileName));//设置一个条目
                bufferedInputStream = new BufferedInputStream(new FileInputStream(srcFile));
                byte[] data = new byte[1024];
                while(bufferedInputStream.read(data) != -1) {
                    zipOutputStream.write(data);
                }
                zipOutputStream.closeEntry();//关闭一个条目
            }catch(Exception ex) {
                logger.error("compress file failed!", ex);
                throw new EngineException(ResultStatus.INTERNAL_ERROR, "compress file failed!", ex.getMessage(), ex);
            }finally {
                if(bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        
                    }
                }
            }
            
        }
    }
}
