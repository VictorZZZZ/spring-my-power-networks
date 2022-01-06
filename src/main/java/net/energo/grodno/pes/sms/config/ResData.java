package net.energo.grodno.pes.sms.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResData {
    public static final String TP_FILENAME = "KARTTP";
    public static final String FID_FILENAME = "KARTFID";
    public static final String EXT = "DBF";

    private String id;
    private String baseName;
    private String folder;

    public String getTpFilename() {
        return id.concat(TP_FILENAME).concat(".").concat(EXT);
    }

    public String getFidFilename() {
        return id.concat(FID_FILENAME).concat(".").concat(EXT);
    }

    public String getBaseName() {
        return baseName.concat(".").concat(EXT);
    }
}
