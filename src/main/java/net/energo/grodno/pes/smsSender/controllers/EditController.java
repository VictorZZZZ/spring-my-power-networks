package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.*;
import net.energo.grodno.pes.smsSender.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/edit")
public class EditController {
    private static final int EMPTY_VALUE = 0;
    private static final List<Object> EMPTY_LIST = new ArrayList<>();

    Logger logger = LoggerFactory.getLogger(AjaxController.class);

    private ResService resService;
    private SubstationService substationService;
    private SectionService sectionService;
    private LineService lineService;
    private PartService partService;
    private TpService tpService;

    @Autowired
    public EditController(ResService resService, SubstationService substationService, SectionService sectionService, LineService lineService, PartService partService, TpService tpService) {
        this.resService = resService;
        this.substationService = substationService;
        this.sectionService = sectionService;
        this.lineService = lineService;
        this.partService = partService;
        this.tpService = tpService;
    }

    @RequestMapping(value = {"/{entity}/{id}"},method = RequestMethod.GET)
    public String editEntityGet(@PathVariable("entity") String entity, @PathVariable("id") Long id,
                                 HttpServletRequest request,
                                 Model model,
                                 RedirectAttributes redirectAttributes){
        if(id==null) {
            redirectAttributes.addFlashAttribute("messageError","Wrong request!");
            return "redirect:"+request.getHeader("Referer");
        }
        Object object;
        switch (entity){
            case "res":
                object = resService.getOne(id.intValue());
                if(object!=null) {
                    model.addAttribute("entity",entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            case "substation":
                object = substationService.getOne(id.intValue());
                if(object!=null) {
                    Substation substation = (Substation)object;
                    Integer resId = substation.getRes().getId();
                    model.addAttribute("resId",resId);
                    List<Res> resList = resService.getAllRes();
                    model.addAttribute("resList",resList);
                    model.addAttribute("entity",entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            case "section":
                object = sectionService.getOne(id.intValue());
                if(object!=null) {
                    Section section = (Section)object;
                    Integer substationId = section.getSubstation().getId();
                    model.addAttribute("substationId",substationId);
                    Integer resId = section.getSubstation().getRes().getId();
                    model.addAttribute("resId",resId);
                    List<Res> resList = resService.getAllRes();
                    model.addAttribute("resList",resList);
                    List<Substation> substationList = substationService.getAllByRes(resId);
                    model.addAttribute("substationList",substationList);
                    model.addAttribute("entity",entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            case "line":
                object = lineService.getOne(id.longValue());
                if(object!=null){
                    Line line = (Line)object;
                    Integer sectionId = line.getSection().getId();
                    model.addAttribute("sectionId",sectionId);
                    Integer substationId = line.getSection().getSubstation().getId();
                    model.addAttribute("substationId",substationId);
                    Integer resId = line.getSection().getSubstation().getRes().getId();
                    model.addAttribute("resId",resId);
                    List<Res> resList = resService.getAllRes();
                    model.addAttribute("resList",resList);
                    List<Substation> substationList = substationService.getAllByRes(resId);
                    model.addAttribute("substationList",substationList);
                    List<Section> sectionList = sectionService.getAllBySubstation(substationId);
                    model.addAttribute("sectionList",sectionList);
                    model.addAttribute("entity",entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            case "part":
                object = partService.getOne(id.longValue());
                if(object!=null){
                    Part part = (Part)object;
                    Long lineId = part.getLine().getId();
                    model.addAttribute("lineId",lineId);
                    Integer sectionId = part.getLine().getSection().getId();
                    model.addAttribute("sectionId",sectionId);
                    Integer substationId = part.getLine().getSection().getSubstation().getId();
                    model.addAttribute("substationId",substationId);
                    Integer resId = part.getLine().getSection().getSubstation().getRes().getId();
                    model.addAttribute("resId",resId);
                    List<Res> resList = resService.getAllRes();
                    model.addAttribute("resList",resList);
                    List<Substation> substationList = substationService.getAllByRes(resId);
                    model.addAttribute("substationList",substationList);
                    List<Section> sectionList = sectionService.getAllBySubstation(substationId);
                    model.addAttribute("sectionList",sectionList);
                    List<Line> lineList = lineService.getAllBySection(sectionId);
                    model.addAttribute("lineList",lineList);
                    model.addAttribute("entity",entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            case "tp":
                object = tpService.getOne(id.longValue());
                System.out.println(object);
                if(object!=null){
                    Tp tp = (Tp)object;
                    Part part = tp.getPart();
                    if(part!=null) {
                        Long partId = part.getId();
                        model.addAttribute("partId", partId);
                        Long lineId = tp.getPart().getLine().getId();
                        model.addAttribute("lineId", lineId);
                        Integer sectionId = tp.getPart().getLine().getSection().getId();
                        model.addAttribute("sectionId", sectionId);
                        Integer substationId = tp.getPart().getLine().getSection().getSubstation().getId();
                        model.addAttribute("substationId", substationId);
                        List<Substation> substationList = substationService.getAllByRes(((Tp) object).getResId());
                        model.addAttribute("substationList", substationList);
                        List<Section> sectionList = sectionService.getAllBySubstation(substationId);
                        model.addAttribute("sectionList", sectionList);
                        List<Line> lineList = lineService.getAllBySection(sectionId);
                        model.addAttribute("lineList", lineList);
                        List<Part> partList = partService.getAllByLine(lineId);
                        model.addAttribute("partList", partList);
                    } else {
                        model.addAttribute("partId", EMPTY_VALUE);
                        model.addAttribute("lineId", EMPTY_VALUE);
                        model.addAttribute("sectionId", EMPTY_VALUE);
                        model.addAttribute("substationId", EMPTY_VALUE);
                        model.addAttribute("substationList", EMPTY_LIST);
                        model.addAttribute("sectionList", EMPTY_LIST);
                        model.addAttribute("lineList", EMPTY_LIST);
                        model.addAttribute("partList", EMPTY_LIST);
                    }
                    Integer resId = tp.getResId();
                    model.addAttribute("resId", resId);
                    List<Res> resList = resService.getAllRes();
                    model.addAttribute("resList", resList);

                    model.addAttribute("entity", entity);
                    model.addAttribute("object", object);
                    return "editNode/edit";
                } else {
                    redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                    return "redirect:"+request.getHeader("Referer");
                }
            default:
                redirectAttributes.addFlashAttribute("messageError","Ошибка роутинга.");
                return "redirect:"+request.getHeader("Referer");
        }

    }

    @RequestMapping(value = {"/save/{entity}"},method = RequestMethod.POST)
    public String editEntityPost(@PathVariable("entity") String entity,
                                 @RequestParam() MultiValueMap<String, String> dataMap,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request){

        logger.info("Редактирование объекта {}:{}",entity,dataMap.getFirst("object[id]"));
        Integer id = Integer.valueOf(dataMap.getFirst("object[id]"));
        switch(entity){
            case "res":

                    if(id!=null) {
                        String name = dataMap.getFirst("object[name]");
                        if(name!=null) {
                            Res res = resService.getOne(id);
                            res.setName(name);
                            resService.saveOne(res);
                            redirectAttributes.addFlashAttribute("messageInfo", "Объект отредактирован!");
                        } else {
                            redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля name");
                        }
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля id");
                    }
                return "redirect:"+request.getHeader("Referer");
            case "substation":
                if(id!=null) {
                    String name = dataMap.getFirst("object[name]");
                    Integer resId = Integer.valueOf(dataMap.getFirst("object[res]"));
                    Res res = resService.getOne(resId);
                    if(name!=null && res!=null) {
                        Substation substation = substationService.getOne(id);
                        substation.setName(name);
                        substation.setRes(res);
                        substationService.saveOne(substation);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля name");
                    }
                } else {
                    redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля id");
                }
                return "redirect:"+request.getHeader("Referer");
            case "section":
                if(id!=null) {
                    String name = dataMap.getFirst("object[name]");
                    Integer substationId = Integer.valueOf(dataMap.getFirst("object[substation]"));
                    Substation substation = substationService.getOne(substationId);
                    if(name!=null && substation!=null) {
                        Section section = sectionService.getOne(id);
                        section.setName(name);
                        section.setSubstation(substation);
                        sectionService.saveOne(section);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+section.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                } else {
                    redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля id");
                }
                return "redirect:"+request.getHeader("Referer");
            case "line":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Integer sectionId = Integer.valueOf(dataMap.getFirst("object[section]"));
                    Section section = sectionService.getOne(sectionId);
                    if(name!=null && section!=null){
                        Line line = lineService.getOne(id.longValue());
                        line.setName(name);
                        line.setSection(section);
                        lineService.saveOne(line);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+line.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:"+request.getHeader("Referer");
            case "part":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Long lineId = Long.valueOf(dataMap.getFirst("object[line]"));
                    Line line = lineService.getOne(lineId);
                    if(name!=null && line!=null){
                        Part part = partService.getOne(id.longValue());
                        part.setName(name);
                        part.setLine(line);
                        partService.saveOne(part);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+part.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:"+request.getHeader("Referer");
            case "tp":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Long partId = Long.valueOf(dataMap.getFirst("object[part]"));
                    Part part = partService.getOne(partId);
                    if(name!=null && part!=null){
                        Tp tp = tpService.getOne(id.longValue());
                        tp.setName(name);
                        tp.setPart(part);
                        tpService.saveOne(tp);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+tp.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:"+request.getHeader("Referer");
            default:
                logger.warn("Попытка сделать неверный POST запрос для {}",entity);
                redirectAttributes.addFlashAttribute("messageError","Ошибка запроса");
                return "redirect:"+request.getHeader("Referer");
        }
    }
}
