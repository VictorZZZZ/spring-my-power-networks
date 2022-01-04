package net.energo.grodno.pes.sms.controllers;

import net.energo.grodno.pes.sms.Services.*;
import net.energo.grodno.pes.sms.entities.*;
import net.energo.grodno.pes.sms.smsSender.Services.*;
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

    @RequestMapping(value = {"/res/{id}/addChildren"},method = RequestMethod.GET)
    public String addSubstation(Model model,@PathVariable("id") Integer id){
        Substation substation = new Substation();
        Integer resId = id;
        model.addAttribute("resId",resId);
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);
        model.addAttribute("entity","substation");
        model.addAttribute("object", substation);
        return "editNode/edit";
    }

    @RequestMapping(value = {"/substation/{id}/addChildren"},method = RequestMethod.GET)
    public String addSection(Model model,@PathVariable("id") Integer id){
        Integer substationId = id;
        model.addAttribute("substationId",substationId);
        Integer resId = substationService.getOne(substationId).getRes().getId();
        model.addAttribute("resId",resId);
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);
        List<Substation> substationList = substationService.getAllByRes(resId);
        model.addAttribute("substationList",substationList);
        model.addAttribute("entity","section");
        Section section = new Section();
        model.addAttribute("object", section);
        return "editNode/edit";
    }

    @RequestMapping(value = {"/section/{id}/addChildren"},method = RequestMethod.GET)
    public String addLine(Model model,@PathVariable("id") Integer id){
        Integer sectionId = id;
        model.addAttribute("sectionId",sectionId);
        Section section = sectionService.getOne(id);
        Integer substationId = section.getSubstation().getId();
        model.addAttribute("substationId",substationId);
        Integer resId = section.getSubstation().getRes().getId();
        model.addAttribute("resId",resId);
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);
        List<Substation> substationList = substationService.getAllByRes(resId);
        model.addAttribute("substationList",substationList);
        List<Section> sectionList = sectionService.getAllBySubstation(substationId);
        model.addAttribute("sectionList",sectionList);
        model.addAttribute("entity","line");
        Line line = new Line();
        model.addAttribute("object", line);
        return "editNode/edit";
    }

    @RequestMapping(value = {"/line/{id}/addChildren"},method = RequestMethod.GET)
    public String addPart(Model model,@PathVariable("id") Long id){
        Long lineId = id;
        Line line = lineService.getOne(lineId);
        model.addAttribute("lineId", lineId);

        Integer sectionId = line.getSection().getId();
        model.addAttribute("sectionId", sectionId);

        Integer substationId = line.getSection().getSubstation().getId();
        model.addAttribute("substationId", substationId);

        Integer resId = line.getSection().getSubstation().getRes().getId();
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);

        List<Substation> substationList = substationService.getAllByRes(resId);
        model.addAttribute("substationList", substationList);

        List<Section> sectionList = sectionService.getAllBySubstation(substationId);
        model.addAttribute("sectionList", sectionList);

        List<Line> lineList = lineService.getAllBySection(sectionId);
        model.addAttribute("lineList", lineList);

        model.addAttribute("entity","part");
        Part part = new Part();
        model.addAttribute("object", part);
        return "editNode/edit";
    }

    @RequestMapping(value = {"/part/{id}/addChildren"},method = RequestMethod.GET)
    public String addTp(Model model,@PathVariable("id") Long id){
        Long partId = id;
        Part part = partService.getOne(partId);

        Line line = part.getLine();
        model.addAttribute("lineId", line.getId());

        Integer sectionId = line.getSection().getId();
        model.addAttribute("sectionId", sectionId);

        Integer substationId = line.getSection().getSubstation().getId();
        model.addAttribute("substationId", substationId);

        Integer resId = line.getSection().getSubstation().getRes().getId();
        List<Res> resList = resService.getAllRes();
        model.addAttribute("resList",resList);

        List<Substation> substationList = substationService.getAllByRes(resId);
        model.addAttribute("substationList", substationList);

        List<Section> sectionList = sectionService.getAllBySubstation(substationId);
        model.addAttribute("sectionList", sectionList);

        List<Line> lineList = lineService.getAllBySection(sectionId);
        model.addAttribute("lineList", lineList);

        List<Part> partList = partService.getAllByLine(line.getId());
        model.addAttribute("partList",partList);

        model.addAttribute("entity","tp");
        Tp tp = new Tp();
        model.addAttribute("object", tp);
        return "editNode/edit";
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
                    if(id==0){
                        //Если получили id==0, о считаем, что это новый объект
                        Substation substation = new Substation();
                        substation.setName(name);
                        substation.setRes(res);
                        substation.setVoltage("");
                        substationService.saveOne(substation);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+name+" cоздан!");
                    } else if(name!=null && res!=null) {
                        Substation substation = substationService.getOne(id);
                        substation.setName(name);
                        substation.setRes(res);
                        substationService.saveOne(substation);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект" + name + " отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля name");
                    }
                } else {
                    redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта нет поля id");
                }
                return "redirect:/";
            case "section":
                if(id!=null) {
                    String name = dataMap.getFirst("object[name]");
                    Integer substationId = Integer.valueOf(dataMap.getFirst("object[substation]"));
                    Substation substation = substationService.getOne(substationId);
                    if(id==0){
                        //Если получили id==0, о считаем, что это новый объект
                        Section section = new Section();
                        section.setName(name);
                        section.setSubstation(substation);
                        sectionService.saveOne(section);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+name+" cоздан!");
                    } else if(name!=null && substation!=null) {
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
                return "redirect:/";
            case "line":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Integer sectionId = Integer.valueOf(dataMap.getFirst("object[section]"));
                    Section section = sectionService.getOne(sectionId);
                    if(id==0){
                        //Если получили id==0, о считаем, что это новый объект
                        Line line = new Line();
                        line.setName(name);
                        line.setSection(section);
                        lineService.saveOne(line);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+name+" cоздан c id=" + line.getId() + "!");
                    } else if(name!=null && section!=null){
                        Line line = lineService.getOne(id.longValue());
                        line.setName(name);
                        line.setSection(section);
                        lineService.saveOne(line);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+line.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:/";
            case "part":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Long lineId = Long.valueOf(dataMap.getFirst("object[line]"));
                    Line line = lineService.getOne(lineId);
                    if(id==0){
                        //Если получили id==0, о считаем, что это новый объект
                        Part part = new Part();
                        part.setName(name);
                        part.setLine(line);
                        partService.saveOne(part);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+name+" cоздан c id=" + part.getId() + "!");
                    } else if(name!=null && line!=null){
                        Part part = partService.getOne(id.longValue());
                        part.setName(name);
                        part.setLine(line);
                        partService.saveOne(part);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+part.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:/";
            case "tp":
                if(id!=null){
                    String name = dataMap.getFirst("object[name]");
                    Long partId = Long.valueOf(dataMap.getFirst("object[part]"));
                    Part part = partService.getOne(partId);
                    if(id==0){
                        //Если получили id==0, о считаем, что это новый объект
                        Tp tp = new Tp();
                        tp.setName(name);
                        tp.setPart(part);
                        tp.setResId( part.getLine().getSection().getSubstation().getRes().getId() );
                        tpService.saveOne(tp);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+name+" cоздан c id=" + tp.getId() + "!");
                    } else if(name!=null && part!=null){
                        Tp tp = tpService.getOne(id.longValue());
                        tp.setName(name);
                        tp.setPart(part);
                        tpService.saveOne(tp);
                        redirectAttributes.addFlashAttribute("messageInfo", "Объект "+tp.getName()+" отредактирован!");
                    } else {
                        redirectAttributes.addFlashAttribute("messageError", "Ошибка!!! У объекта заполнены не все поля");
                    }
                }
                return "redirect:/";
            default:
                logger.warn("Попытка сделать неверный POST запрос для {}",entity);
                redirectAttributes.addFlashAttribute("messageError","Ошибка запроса");
                return "redirect:"+request.getHeader("Referer");
        }
    }
}
