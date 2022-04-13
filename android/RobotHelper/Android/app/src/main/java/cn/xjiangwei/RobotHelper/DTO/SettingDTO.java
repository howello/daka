package cn.xjiangwei.RobotHelper.DTO;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class SettingDTO {
    private String successUrl;
    private PicName picName;
    private Map<Integer,OcrEntity> ocrEntityMap;

    @Data
   public static class PicName{
        String pic1;
        String pic2;
        String pic3;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class OcrEntity {
        int leftX;
        int leftY;
        int rightX;
        int rightY;
        String whiteList;
    }
}
