package net.energo.grodno.pes.sms.config;

public enum ImportData {
    BRES(new ResData("1","BRES", "БРЭС")),
    GSRES(new ResData("2","GSRES", "СРЭС")),
    SCHRES(new ResData("3","SCHRES", "ЩРЭС")),
    GGRES(new ResData("4","GGRES", "ГГРЭС"));

    private ResData resData;

    ImportData(ResData resData) {
        this.resData=resData;
    }

    public ResData getResData(){
        return resData;
    }
}
