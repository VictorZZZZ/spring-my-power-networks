package net.energo.grodno.pes.smsSender.controllers;

import java.io.IOException;
import java.util.stream.Collectors;

import net.energo.grodno.pes.smsSender.storage.StorageFileNotFoundException;
import net.energo.grodno.pes.smsSender.storage.StorageService;
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
@RequestMapping("/files")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = {"","/","index"})
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "storage/index";
    }

    @PostMapping(value = {"","/","index"})
    public String handleFileUpload(@RequestParam("tpFile") MultipartFile tpFile,
                                   @RequestParam("fiderFile") MultipartFile fiderFile,
                                   @RequestParam("abonentFile") MultipartFile abonentFile,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(tpFile);
        storageService.store(fiderFile);
        storageService.store(abonentFile);

        redirectAttributes.addFlashAttribute("messageInfo",
                "Файлы успешно загружены: \n"
                        + tpFile.getOriginalFilename() + " \n"
                        + fiderFile.getOriginalFilename() + "\n "
                        + abonentFile.getOriginalFilename() + "");

        return "redirect:/files";
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }



    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}