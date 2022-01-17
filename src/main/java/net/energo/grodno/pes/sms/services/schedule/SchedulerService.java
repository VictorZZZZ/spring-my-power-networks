package net.energo.grodno.pes.sms.services.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.energo.grodno.pes.sms.config.FtpProperties;
import net.energo.grodno.pes.sms.config.ImportData;
import net.energo.grodno.pes.sms.config.ResData;
import net.energo.grodno.pes.sms.exceptions.DBFManagerException;
import net.energo.grodno.pes.sms.exceptions.FtpClientException;
import net.energo.grodno.pes.sms.exceptions.SchedulerServiceException;
import net.energo.grodno.pes.sms.services.update.ImportService;
import net.energo.grodno.pes.sms.utils.ftp.FtpClient;
import net.energo.grodno.pes.sms.utils.importFromDbf.DBFManager;
import net.energo.grodno.pes.sms.utils.smsAPI.SmsAPI;
import net.energo.grodno.pes.sms.utils.smsAPI.SmsSenderErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SchedulerService {
    private static final String UPLOAD_DIR = "upload-dir";

    @Value("${adminsNumbers}")
    private List<String> adminsNumbers;
    private final FtpProperties ftpProperties;
    private final DBFManager dbfManager;
    private final ImportService importService;
    private final SmsAPI smsAPI;

    //At 10:00:00am, on every Sunday, every month
    @Scheduled(cron="0 0 10 ? * SUN")
    public void importBres() {
        ImportData target = ImportData.BRES;
        importToTarget(target);
    }

    //At 12:00:00am, on every Sunday, every month
    @Scheduled(cron="0 0 12 ? * SUN")
    public void importSchres() {
        ImportData target = ImportData.SCHRES;
        importToTarget(target);
    }

    //At 14:00:00am, on every Sunday, every month
    @Scheduled(cron="0 0 14 ? * SUN")
    public void importGgres() {
        ImportData target = ImportData.GGRES;
        importToTarget(target);
    }

    //At 16:00:00am, on every Sunday, every month
    @Scheduled(cron="0 0 16 ? * SUN")
    public void importGsres() {
        ImportData target = ImportData.GSRES;
        importToTarget(target);
    }

    private void importToTarget(ImportData target) {
        new Thread(() -> {
            try {
                String errorMsg = String.format("Ошибка импорта БД для %s ", target.name());
                log.info("Автоматический старт импорта БД для {}", target);
                try {
                    boolean isImportSuccess = importToDatabase(target);
                    if(isImportSuccess){
                        smsAPI.sendSms(adminsNumbers, target.name() + ": успешно обновлено!");
                    }
                } catch (DBFManagerException | SchedulerServiceException e) {
                    e.printStackTrace();
                    smsAPI.sendSms(adminsNumbers, errorMsg + ":" + e.getMessage());
                }
            } catch (IOException | InterruptedException | SmsSenderErrorException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }, target.name() + "-import-thread").start();
    }


    private boolean importToDatabase(ImportData importData) throws DBFManagerException, SchedulerServiceException {
        FtpProperties.Connection connection = ftpProperties.getConnection();
        String baseFolder = ftpProperties.getBaseFolder();
        log.info("Base folder: " + baseFolder);
        try (FtpClient ftpClient =
                     new FtpClient(connection.getServer(),
                             connection.getPort(),
                             connection.getLogin(),
                             connection.getPassword())) {

            ResData resData = importData.getResData();

            String baseFile = genDownloadPath(baseFolder, resData, resData.getBaseName());
            String tpFile = genDownloadPath(baseFolder, resData, resData.getTpFilename());
            String fiderFile = genDownloadPath(baseFolder, resData, resData.getFidFilename());

            String tmpBasePath = genUploadPath(importData, "_tmpBaseFile.DBF");
            String tmpTpFilePath = genUploadPath(importData, "_tmpTpFile.DBF");
            String tmpFidFilePath = genUploadPath(importData, "_tmpFidFile.DBF");

            ftpClient.downloadFile(baseFile, tmpBasePath);
            ftpClient.downloadFile(tpFile, tmpTpFilePath);
            ftpClient.downloadFile(fiderFile, tmpFidFilePath);

            dbfManager.setReaders(tmpTpFilePath, tmpFidFilePath, tmpBasePath);

            if (!dbfManager.isValid()) {
                throw new SchedulerServiceException("Скачанные файлы не валидны");
            }
            dbfManager.manage();

            long startTime = System.currentTimeMillis(); // Get the start Time
            importService.updateDb();
            long endTime = System.currentTimeMillis(); //Get the end Time
            float processTime = (endTime - startTime) / (1000F);
            log.info("Процесс импорта занял {} секунд", processTime); // Print the difference in seconds
            return true;
        } catch (IOException | FtpClientException e) {
            e.printStackTrace();
            throw new SchedulerServiceException(e.getMessage());
        }
    }

    private String genDownloadPath(String baseFolder, ResData resData, String fileName) {
        return Paths.get(baseFolder, resData.getFolder(), fileName).toString();
    }

    private String genUploadPath(ImportData importData, String fileName) {
        return Paths.get(UPLOAD_DIR, importData.name() + fileName + ".DBF").toString();
    }
}
