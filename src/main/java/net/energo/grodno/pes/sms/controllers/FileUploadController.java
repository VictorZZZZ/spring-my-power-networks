package net.energo.grodno.pes.sms.controllers;

import com.linuxense.javadbf.DBFException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.energo.grodno.pes.sms.entities.Fider;
import net.energo.grodno.pes.sms.entities.Tp;
import net.energo.grodno.pes.sms.exceptions.DBFManagerException;
import net.energo.grodno.pes.sms.services.AbonentService;
import net.energo.grodno.pes.sms.services.FiderService;
import net.energo.grodno.pes.sms.services.TpService;
import net.energo.grodno.pes.sms.services.update.ImportService;
import net.energo.grodno.pes.sms.storage.StorageFileNotFoundException;
import net.energo.grodno.pes.sms.storage.StorageService;
import net.energo.grodno.pes.sms.utils.importFromDbf.DBFManager;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/importFromDbf")
@AllArgsConstructor
@Slf4j
public class FileUploadController {

    private final StorageService storageService;
    private final TpService tpService;
    private final FiderService fiderService;
    private final AbonentService abonentService;
    private final DBFManager dbfManager;
    private final ImportService importService;

    @GetMapping(value = {"", "/", "index", "step_1"})
    public String index() {
        return "importFromDbf/step_1";
    }

    @PostMapping(value = {"", "/", "index"})
    public String handleFileUpload(@RequestParam("tpFile") MultipartFile tpFile,
                                   @RequestParam("fiderFile") MultipartFile fiderFile,
                                   @RequestParam("abonentFile") MultipartFile abonentFile,
                                   RedirectAttributes redirectAttributes) throws DBFManagerException {
        storageService.deleteAll();
        storageService.init();

        String tpFilePath = storageService.store(tpFile);
        String fiderFilePath = storageService.store(fiderFile);
        String abonentFilePath = storageService.store(abonentFile);

        dbfManager.setReaders(tpFilePath, fiderFilePath, abonentFilePath);

        if (!dbfManager.isValid()) {
            redirectAttributes.addFlashAttribute("messageError",
                    "Ошибка загруженных файлов: \n" +
                            dbfManager.getErrors());
            storageService.deleteAll();
            return "redirect:/importFromDbf/step_1";
        }

        dbfManager.manage();

        redirectAttributes.addFlashAttribute("messageInfo",
                "Файлы успешно загружены: \n"
                        + tpFile.getOriginalFilename() + " \n"
                        + fiderFile.getOriginalFilename() + "\n "
                        + abonentFile.getOriginalFilename() + "");

        return "redirect:/importFromDbf/step_2";
    }

    @GetMapping("/step_2")
    public String listUploadedFiles(Model model) {


        model.addAttribute("countTp", dbfManager.getTpMap().size());
        int fidersCount = 0;
        int abonentsCount = 0;
        for (Map.Entry<Integer, Tp> entry : dbfManager.getTpMap().entrySet()) {
            Tp tp = entry.getValue();
            fidersCount += tp.getFiders().size();
            for (Fider fider : tp.getFiders()) {
                abonentsCount += fider.getAbonents().size();
            }
        }
        model.addAttribute("countFid", fidersCount);
        model.addAttribute("countAbonents", abonentsCount);
        try {
            List<String> logs = storageService.readFileToStringList("logs\\dbfManage.log");
            model.addAttribute("logs", logs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("files", storageService.loadAll()
                .map(path -> serveFile(path.getFileName().toString()))
                .map(HttpEntity::getBody).filter(Objects::nonNull)
                .map(Resource::getFilename)
                .collect(Collectors.toList()));

        return "importFromDbf/step_2";
    }


    @GetMapping("/step_3")
    public String showResultsOfImport(Model model) {
        long startTime = System.currentTimeMillis(); // Get the start Time

        List<String> resultOfImport = importService.updateDb();

        long endTime = System.currentTimeMillis(); //Get the end Time
        float processTime = (endTime - startTime) / (1000F);
        resultOfImport.add("Время обработки " + processTime + " секунд");

        model.addAttribute("resultOfImport", resultOfImport);
        log.info("Процесс импорта занял {} секунд", processTime); // Print the difference in seconds
        return "importFromDbf/step_3";
    }


    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DBFException.class)
    public String handleDbfFileException(DBFException exc) {
        return exc.toString();
    }

    @GetMapping("/test")
    @ResponseBody
    public String dataExample(@RequestParam(value = "id", required = false) Integer dbfId) {
        Tp tp = tpService.getOneByDbfId(dbfId);
        return "TP: " + tp.toString();
    }

}
