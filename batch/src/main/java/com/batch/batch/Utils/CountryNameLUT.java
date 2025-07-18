package com.batch.batch.Utils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CountryNameLUT {
    public Map<String, String> countryLUT(){
        Map<String, String> countryMap = new HashMap<>();

        // Việt Nam
        countryMap.put("vnm", "VNM");
        countryMap.put("vn", "VNM");
        countryMap.put("viet nam", "VNM");
        countryMap.put("vietnam", "VNM");

        // Hoa Kỳ
        countryMap.put("us", "USA");
        countryMap.put("usa", "USA");
        countryMap.put("u.s.", "USA");
        countryMap.put("united states", "USA");
        countryMap.put("united states of america", "USA");

        // Nhật Bản
        countryMap.put("jp", "JPN");
        countryMap.put("jpn", "JPN");
        countryMap.put("japan", "JPN");

        // Hàn Quốc
        countryMap.put("kr", "KOR");
        countryMap.put("kor", "KOR");
        countryMap.put("south korea", "KOR");
        countryMap.put("korea", "KOR");

        // Trung Quốc
        countryMap.put("cn", "CHN");
        countryMap.put("chn", "CHN");
        countryMap.put("china", "CHN");

        // Mông Cổ
        countryMap.put("mn", "MNG");
        countryMap.put("mgl", "MNG");
        countryMap.put("mongolia", "MNG");

        return countryMap;
    }
}
