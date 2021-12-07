package net.energo.grodno.pes.smsSender.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.linuxense.javadbf.DBFException;
import net.energo.grodno.pes.smsSender.Services.AbonentService;
import net.energo.grodno.pes.smsSender.Services.FiderService;
import net.energo.grodno.pes.smsSender.Services.TpService;
import net.energo.grodno.pes.smsSender.entities.Abonent;
import net.energo.grodno.pes.smsSender.entities.Fider;
import net.energo.grodno.pes.smsSender.entities.Tp;
import net.energo.grodno.pes.smsSender.storage.StorageFileNotFoundException;
import net.energo.grodno.pes.smsSender.storage.StorageService;
import net.energo.grodno.pes.smsSender.utils.importFromDbf.DBFManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/importFromDbf")
public class FileUploadController {
    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final StorageService storageService;
    private final TpService tpService;
    private final FiderService fiderService;
    private final AbonentService abonentService;
    private DBFManager dbfManager;

    @Autowired
    public FileUploadController(StorageService storageService, TpService tpService, FiderService fiderService, AbonentService abonentService, DBFManager dbfManager) {
        this.storageService = storageService;
        this.tpService = tpService;
        this.fiderService = fiderService;
        this.abonentService = abonentService;
        this.dbfManager = dbfManager;
    }



    @GetMapping(value = {"","/","index","step_1"})
    public String index(){
        return "importFromDbf/step_1";
    }

    @PostMapping(value = {"","/","index"})
    public String handleFileUpload(@RequestParam("tpFile") MultipartFile tpFile,
                                   @RequestParam("fiderFile") MultipartFile fiderFile,
                                   @RequestParam("abonentFile") MultipartFile abonentFile,
                                   RedirectAttributes redirectAttributes) {
        storageService.deleteAll();
        storageService.init();

        String tpFilePath = storageService.store(tpFile);
        String fiderFilePath = storageService.store(fiderFile);
        String abonentFilePath = storageService.store(abonentFile);

        dbfManager = new DBFManager(tpFilePath,fiderFilePath,abonentFilePath);

        if(!dbfManager.isValid()){
            redirectAttributes.addFlashAttribute("messageError",
                    "Ошибка загруженных файлов: \n"+
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


        model.addAttribute("countTp",dbfManager.getTpMap().size());
        int fidersCount=0;
        int abonentsCount=0;
        for (Map.Entry<Integer, Tp> entry : dbfManager.getTpMap().entrySet()) {
            Tp tp = entry.getValue();
            fidersCount += tp.getFiders().size();
            for(Fider fider:tp.getFiders()){
                abonentsCount+=fider.getAbonents().size();
            }
        }
        model.addAttribute("countFid",fidersCount);
        model.addAttribute("countAbonents",abonentsCount);
        try {
            List<String> logs =  storageService.readFileToStringList("logs\\dbfManage.log");
            model.addAttribute("logs",logs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "importFromDbf/step_2";
    }


    @GetMapping("/step_3")
    public String showResultsOfImport(Model model)  {
        List<String> resultOfImport = new ArrayList<>();
        long startTime = System.currentTimeMillis(); // Get the start Time
        Map<Integer, Tp> tpsFromDbf = dbfManager.getTpMap();
        List<Tp> tps = new ArrayList<>(tpsFromDbf.values());
        Integer resId = tps.get(0).getResId();

        logger.info("Начало процесса Импорта");

        resultOfImport.addAll(tpService.updateAll(tps));

        List<Fider> fiders = new ArrayList<>();
        List<Abonent> abonents = new ArrayList<>();
        for (Tp tp:tps) {
            fiders.addAll(tp.getFiders());
            for (Fider fider : tp.getFiders() ) {
                abonents.addAll(fider.getAbonents());
            }
        }
        logger.info("Обновляем фидера.");
        resultOfImport.addAll(fiderService.updateAll(fiders));
        logger.info("Обновляем абонентов.");
        resultOfImport.addAll(abonentService.updateAll(abonents));
        logger.info("Убираем пустых и дубли.");
        tpService.removeDuplicatedAndEmpty(resId);
        long endTime = System.currentTimeMillis(); //Get the end Time
        float processTime = (endTime-startTime)/(1000F);
        resultOfImport.add("Время обработки "+processTime+" секунд");

        model.addAttribute("resultOfImport",resultOfImport);
        logger.info("Процесс импорта занял {} секунд",processTime); // Print the difference in seconds
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
    public String dataExample(@RequestParam(value = "id",required = false) Integer dbfId){
        Tp tp = tpService.getOneByDbfId(dbfId);
        return "TP: " + tp.toString();
    }

}
