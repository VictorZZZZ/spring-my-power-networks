package net.energo.grodno.pes.smsSender.controllers;

import net.energo.grodno.pes.smsSender.Services.*;
import net.energo.grodno.pes.smsSender.entities.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ajax")
public class AjaxController {
    Logger logger = LoggerFactory.getLogger(AjaxController.class);

    private ResService resService;
    private SubstationService substationService;
    private SectionService sectionService;
    private LineService lineService;
    private PartService partService;
    private TpService tpService;
    private FiderService fiderService;

    @Autowired
    public AjaxController(ResService resService, SubstationService substationService, SectionService sectionService, LineService lineService, PartService partService, TpService tpService, FiderService fiderService) {
        this.resService = resService;
        this.substationService = substationService;
        this.sectionService = sectionService;
        this.lineService = lineService;
        this.partService = partService;
        this.tpService = tpService;
        this.fiderService = fiderService;
    }

    @RequestMapping(value = {"/resTree"}, method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String resTree(Model model, @Param("parent") String parent){
        logger.info("Ajax запрос в базу: /app/ajax/resTree?parent=" + parent);
        JSONArray response = new JSONArray();
        //{"id":"node_159799604352036","text":"Node #1","icon":"fa fa-folder icon-lg icon-state-danger","children":true,"type":"root"}
        if(parent.equals("#")){
            List<Res> resList = resService.getAllRes();
            for (Res res:resList) {
                JSONObject treeObj = new JSONObject();
                treeObj.put("id","res_"+res.getId());
                treeObj.put("text",res.getName());
                treeObj.put("icon","fa fa-bolt icon-lg icon-state-success");//todo: поменять иконку РЭСа
                if(res.getSubstations().size()>0){
                    treeObj.put("children",true);
                } else {
                    treeObj.put("children",false);
                }
                treeObj.put("type","root");
                response.put(treeObj);
            }
        } else {
            String parentEntity = parent.split("_")[0];
            Integer parentId = Integer.parseInt(parent.split("_")[1]);
            switch (parentEntity){
                case "res":
                    List<Substation> substationList = substationService.getAllByRes(parentId);
                    for (Substation substation:substationList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","substation_"+substation.getId());
                        treeObj.put("text",substation.getName()+((!substation.getVoltage().equals(""))?" ("+substation.getVoltage()+")":""));
                        treeObj.put("icon","fa fa-circle-o icon-lg icon-state-success");//todo: поменять иконку подстанции
                        if(substation.getSections().size()>0){
                            treeObj.put("children",true);
                        } else {
                            treeObj.put("children",false);
                        }
                        response.put(treeObj);
                    }
                break;
                case "substation":
                    List<Section> sectionList = sectionService.getAllBySubstation(parentId);
                    for (Section section:sectionList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","section_"+section.getId());
                        treeObj.put("text",section.getName());
                        treeObj.put("icon","fa fa-circle icon-lg icon-state-success");//todo: поменять иконку подстанции
                        if(section.getLines().size()>0){
                            treeObj.put("children",true);
                        } else {
                            treeObj.put("children",false);
                        }
                        response.put(treeObj);
                    }
                break;
                case "section":
                    List<Line> lineList = lineService.getAllBySection(parentId);
                    for (Line line:lineList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","line_"+line.getId());
                        treeObj.put("text",line.getName());
                        treeObj.put("icon","fa fa-long-arrow-down icon-lg icon-state-success");//todo: поменять иконку подстанции
                        if(line.getParts().size()>0){
                            treeObj.put("children",true);
                        } else {
                            treeObj.put("children",false);
                        }
                        response.put(treeObj);
                    }
                break;
                case "line":
                    List<Part> partList = partService.getAllByLine(Long.valueOf(parentId));
                    for (Part part:partList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","part_"+part.getId());
                        treeObj.put("text",part.getName());
                        treeObj.put("icon","fa fa-puzzle-piece icon-lg icon-state-success");//todo: поменять иконку подстанции
                        if(part.getTps().size()>0){
                            treeObj.put("children",true);
                        } else {
                            treeObj.put("children",false);
                        }
                        response.put(treeObj);
                    }
                break;
                case "part":
                    List<Tp> tpList = tpService.getAllByPart(Long.valueOf(parentId));
                    for (Tp tp:tpList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","tp_"+tp.getId());
                        treeObj.put("text",tp.getName());
                        treeObj.put("icon","fa fa-fire icon-lg icon-state-success");//todo: поменять иконку подстанции
                        if(tp.getFiders().size()>0){
                            treeObj.put("children",true);
                        } else {
                            treeObj.put("children",false);
                        }
                        response.put(treeObj);
                    }
                break;
                case "tp":
                    List<Fider> fiderList= fiderService.getAllByTp(Long.valueOf(parentId));
                    for (Fider fider:fiderList) {
                        JSONObject treeObj = new JSONObject();
                        treeObj.put("id","fider_"+fider.getId());
                        treeObj.put("text",fider.getName());
                        treeObj.put("icon","fa fa-dot-circle-o icon-lg icon-state-success");//todo: поменять иконку подстанции
                        treeObj.put("children",false);
                        response.put(treeObj);
                    }
                break;

            }
        }

        return response.toString();
    }

    @RequestMapping(value = {"/getInfo"}, method = RequestMethod.GET)
    public String getInfo(Model model, @Param("object") String object, @Param("id") String id, RedirectAttributes redirectAttributes){
        logger.info("Ajax запрос в базу: /app/ajax/getInfo?object={}&id={}",object,id);
        switch(object){
            case "res":
                Res res = resService.getOne(Integer.valueOf(id));
                List<Tp> tps = tpService.getAllByRes(res);
                model.addAttribute("res",res);
                model.addAttribute("tps",tps);
                return "res/view_fragments::mainInfo";
            case "substation":
                Substation substation = substationService.getOne(Integer.valueOf(id));
                model.addAttribute("substation",substation);
                return "substation/view_fragments::mainInfo";
            case "section":
                Section section = sectionService.getOne(Integer.valueOf(id));
                model.addAttribute("section",section);
                return "section/view_fragments::mainInfo";
            case "line":
                Line line = lineService.getOne(Long.valueOf(id));
                model.addAttribute("line",line);
                return "line/view_fragments::mainInfo";
            case "part":
                Part part = partService.getOne(Long.valueOf(id));
                model.addAttribute("part",part);
                return "parts/view_fragments::mainInfo";
            case "tp":
                Tp tp = tpService.getOne(Long.valueOf(id));
                model.addAttribute("tp",tp);
                List<Fider> fiderList = tp.getFiders();
                int abonentCount = fiderList.stream().mapToInt(fider -> (int) fider.getAbonents().size()).sum();
                model.addAttribute("abonentCount",abonentCount);
                return "tp/view_fragment";
            case "fider":
                Fider fider = fiderService.getOne(Long.valueOf(id));
                model.addAttribute("fider",fider);
                return "fider/view_fragments::mainInfo";
        }
        model.addAttribute("messageError","Ошибка запроса");
        return "flashMessages";

    }

}
